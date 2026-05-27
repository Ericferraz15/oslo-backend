package dev.weg.oslo.services

import dev.weg.oslo.model.*
import dev.weg.oslo.model.TelemetryContext
import org.springframework.stereotype.Service
import kotlin.math.abs

@Service
class SystemInsightService {

    fun analyze(context: TelemetryContext, events: List<Event>): SystemInsights {
        val tipo = inferDevice(context)

        val amplitude = analyzeAmplitude(context)
        val estabilidade = analyzeStability(context)
        val tempoFora = analyzeTimeOutOfRange(context, tipo)
        val eficiencia = analyzeEfficiency(events)

        val insights = SystemInsights(
            eficiencia = eficiencia,
            amplitude = amplitude,
            tempoForaFaixa = tempoFora,
            estabilidade = estabilidade,
            tipoInferido = tipo
        )

        val diagnosticos = diagnose(insights)

        return insights.copy(diagnosticos = diagnosticos)
    }

    private fun analyzeEfficiency(events: List<Event>): EfficiencyInsight {
        val recoveries = events.filter { it.tipo == EventType.RECUPERACAO }

        if (recoveries.isEmpty()) {
            return EfficiencyInsight(0.0, "desconhecida")
        }

        val durations = recoveries.mapNotNull {
            if (it.inicio != null && it.fim != null) it.fim - it.inicio else null
        }

        if (durations.isEmpty()) {
            return EfficiencyInsight(0.0, "desconhecida")
        }

        val avgPoints = durations.average()
        val avgMinutes = avgPoints * 5

        val frequency = recoveries.size

        val classificacao = when {
            frequency > 6 && avgMinutes < 20 -> "instável com recuperação rápida"
            avgMinutes < 10 -> "alta"
            avgMinutes < 25 -> "moderada"
            else -> "baixa"
        }

        return EfficiencyInsight(avgMinutes, classificacao)
    }

    private fun analyzeAmplitude(context: TelemetryContext): AmplitudeInsight {
        val stats = context.stats.temperatura
        val amp = stats.amplitude

        val isDegelo = context.snapshot.emDegelo ||
                (context.series.statusDegelo?.any { it == 1 } == true)

        val sistemaLigado = context.snapshot.sistemaLigado ?: true
        val confiavel = context.quality.isReliable

        val classificacao = when {
            !confiavel ->
                "inconclusiva (baixa qualidade dos dados)"

            !sistemaLigado ->
                "sistema desligado (variação não operacional)"

            isDegelo && amp < 30 ->
                "alta (compatível com ciclo de degelo)"

            isDegelo && amp >= 30 ->
                "muito alta (degelo agressivo ou possível anomalia)"

            amp < 5 ->
                "baixa (estável)"

            amp < 15 ->
                "normal (operação típica)"

            amp < 25 ->
                "alta (atenção: verificar padrão)"

            else ->
                "anômala (fora do comportamento esperado)"
        }

        return AmplitudeInsight(
            amplitude = amp,
            classificacao = classificacao,
            emDegelo = isDegelo,
            sistemaLigado = sistemaLigado,
            confiancaDados = context.quality.confianca
        )
    }

    private fun analyzeTimeOutOfRange(
        context: TelemetryContext,
        device: DeviceInference
    ): TimeOutOfRangeInsight {

        val temps = context.series.temperaturaAmbiente
        if (temps.isEmpty()) return TimeOutOfRangeInsight(0, 0.0)

        val (lower, upper) = when (device.tipo) {
            "Freezer / Congelados" -> -25.0 to -10.0
            "Refrigerados (Carnes/Laticínios)" -> 0.0 to 4.0
            "Hortifruti / Resfriados leves" -> 4.0 to 10.0
            else -> 10.0 to 35.0
        }

        val outCount = temps.count { it !in lower..upper }
        val percentual = outCount.toDouble() / temps.size

        return TimeOutOfRangeInsight(
            minutosForaFaixa = outCount * 5,
            percentual = percentual
        )
    }

    private fun analyzeStability(context: TelemetryContext): StabilityInsight {
        val temps = context.series.temperaturaAmbiente

        if (temps.size < 2) {
            return StabilityInsight(0.0, "desconhecida")
        }

        val diffs = temps.zipWithNext { a, b -> abs(a - b) }
        val avgDiff = diffs.average()

        val spikeCount = diffs.count { it > avgDiff * 2 }

        val classificacao = when {
            avgDiff < 0.5 && spikeCount == 0 -> "alta"
            avgDiff < 1.5 && spikeCount < 3 -> "moderada"
            spikeCount >= 3 -> "instável (picos frequentes)"
            else -> "baixa"
        }

        return StabilityInsight(avgDiff, classificacao)
    }

    private fun inferDevice(context: TelemetryContext): DeviceInference {
        val stats = context.stats.temperatura

        return when {
            stats.min < -10 && stats.max > 20 ->
                DeviceInference("Congelados com degelo agressivo ou anomalia", "Ampla variação térmica")

            stats.min < -10 ->
                DeviceInference("Freezer / Congelados", "-18°C a -10°C")

            stats.avg < 2 ->
                DeviceInference("Refrigerados (Carnes/Laticínios)", "0°C a 4°C")

            stats.avg < 10 ->
                DeviceInference("Hortifruti / Resfriados leves", "4°C a 10°C")

            else ->
                DeviceInference("Ambiente ou possível anomalia", ">10°C")
        }
    }

    private fun diagnose(insights: SystemInsights): List<String> {
        val result = mutableListOf<String>()

        if (insights.amplitude.classificacao.contains("anômala") &&
            insights.estabilidade.classificacao.contains("instável")
        ) {
            result.add("Variações bruscas indicam possível falha ou sensor inconsistente")
        }

        if (insights.tempoForaFaixa.percentual > 0.3 &&
            insights.tipoInferido.tipo.contains("Freezer")
        ) {
            result.add("Freezer operando fora da faixa por tempo significativo")
        }

        if (insights.eficiencia.classificacao == "baixa" &&
            insights.amplitude.classificacao.contains("alta")
        ) {
            result.add("Sistema com baixa eficiência e alta oscilação (possível problema mecânico)")
        }

        return result
    }
}
