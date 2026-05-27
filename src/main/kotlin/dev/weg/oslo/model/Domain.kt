package dev.weg.oslo.model

data class Event(
    val tipo: EventType,
    val confianca: Double,
    val evidencias: List<String>,
    val inicio: Int? = null,
    val fim: Int? = null
)

data class EventView(
    val tipo: EventType,
    val confianca: Double,
    val evidencias: List<String>,
    val inicio: String? = null,
    val fim: String? = null
)

enum class EventType {
    DEGELO,
    TEMPERATURA_ALTA,
    RECUPERACAO,
    FALHA_RECUPERACAO,
    OSCILACAO,
    SISTEMA_SATURADO,
    BAIXA_EFICIENCIA,
    SENSOR_FALHA,
    DADOS_INSUFICIENTES
}

data class SystemInsights(
    val eficiencia: EfficiencyInsight,
    val amplitude: AmplitudeInsight,
    val tempoForaFaixa: TimeOutOfRangeInsight,
    val estabilidade: StabilityInsight,
    val tipoInferido: DeviceInference,
    val diagnosticos: List<String> = emptyList(),
)

data class EfficiencyInsight(
    val tempoMedioRecuperacaoMin: Double,
    val classificacao: String
)

data class AmplitudeInsight(
    val amplitude: Double,

    val classificacao: String,

    val emDegelo: Boolean,
    val sistemaLigado: Boolean,

    val confiancaDados: Double,

    val observacao: String? = null
)

data class TimeOutOfRangeInsight(
    val minutosForaFaixa: Int,
    val percentual: Double
)

data class StabilityInsight(
    val variacaoMedia: Double,
    val classificacao: String
)

data class DeviceInference(
    val tipo: String,
    val faixaEsperada: String
)
