package dev.weg.oslo.services

import dev.weg.oslo.model.DataQuality
import dev.weg.oslo.model.Dataset
import dev.weg.oslo.model.TelemetriaResponse
import dev.weg.oslo.model.TelemetryContext
import dev.weg.oslo.model.TelemetrySeries
import dev.weg.oslo.model.TelemetrySnapshot
import dev.weg.oslo.model.TelemetryStatsBundle
import dev.weg.oslo.model.TemperatureStats
import org.springframework.stereotype.Component

@Component
class TelemetryMapper {

    fun toContext(response: TelemetriaResponse): TelemetryContext {
        val datasets = response.datasets

        val temperaturaRaw = findDataset(datasets, "Temperatura Ambiente")
        val degeloRaw = findDataset(datasets, "Status Degelo")
        val valvulaRaw = findDataset(datasets, "Abertura de válvula")

        val temperatura = temperaturaRaw.filterNotNull()
        val degelo = degeloRaw.mapNotNull { it?.toInt() }
        val valvula = valvulaRaw.filterNotNull()

        val stats = calculateTemperatureStats(temperaturaRaw)

        val snapshot = TelemetrySnapshot(
            temperaturaAtual = temperatura.lastOrNull() ?: 0.0,
            sistemaLigado = null,
            emDegelo = degelo.lastOrNull() == 1,
            valvulaAtual = valvula.lastOrNull(),
            pressaoAtual = null
        )

        val series = TelemetrySeries(
            temperaturaAmbiente = temperatura,
            temperaturaEvaporacao = null,
            temperaturaSuccao = null,
            temperaturaDegelo = null,
            statusDegelo = degelo,
            sistemaLigado = null,
            ventilador = null,
            aberturaValvula = valvula,
            pressaoSuccaoo = null,
            superaquecimento = null
        )

        val quality = buildDataQuality(temperaturaRaw)

        return TelemetryContext(
            series = series,
            snapshot = snapshot,
            stats = TelemetryStatsBundle(
                temperatura = stats,
                valvula = null,
                pressao = null
            ),
            quality = quality,
            timestamps = response.labels
        )
    }

    private fun findDataset(
        datasets: List<Dataset>,
        keyword: String
    ): List<Double?> {
        return datasets
            .find { it.label.contains(keyword, ignoreCase = true) }
            ?.values ?: emptyList()
    }

    private fun calculateTemperatureStats(values: List<Double?>): TemperatureStats {
        val valid = values.filterNotNull()

        val min = valid.minOrNull() ?: 0.0
        val max = valid.maxOrNull() ?: 0.0
        val avg = if (valid.isNotEmpty()) valid.average() else 0.0
        val current = valid.lastOrNull() ?: 0.0

        return TemperatureStats(
            min = min,
            max = max,
            avg = avg,
            current = current,
            amplitude = max - min
        )
    }

    private fun buildDataQuality(values: List<Double?>): DataQuality {
        val total = values.size
        val valid = values.count { it != null }

        val confianca = if (total > 0) valid.toDouble() / total else 0.0

        return DataQuality(
            validSamples = valid,
            totalSamples = total,
            confianca = confianca,
            missingRate = 1 - confianca,
            isReliable = confianca > 0.7
        )
    }
}
