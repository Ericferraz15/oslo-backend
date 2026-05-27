package dev.weg.oslo.model

data class TelemetryContext(
    val series: TelemetrySeries,
    val snapshot: TelemetrySnapshot,
    val stats: TelemetryStatsBundle,
    val quality: DataQuality,
    val timestamps: List<String>
)

data class TelemetrySeries(
    val temperaturaAmbiente: List<Double>,

    val temperaturaEvaporacao: List<Double>?,
    val temperaturaSuccao: List<Double>?,
    val temperaturaDegelo: List<Double>?,

    val statusDegelo: List<Int>?,
    val sistemaLigado: List<Int>?,
    val ventilador: List<Int>?,

    val aberturaValvula: List<Double>?,
    val pressaoSuccaoo: List<Double>?,
    val superaquecimento: List<Double>?
)

data class TelemetrySnapshot(
    val temperaturaAtual: Double,
    val sistemaLigado: Boolean?,
    val emDegelo: Boolean,
    val valvulaAtual: Double?,
    val pressaoAtual: Double?
)

data class TelemetryStatsBundle(
    val temperatura: TemperatureStats,
    val valvula: BasicStats?,
    val pressao: BasicStats?
)

data class TemperatureStats(
    val min: Double,
    val max: Double,
    val avg: Double,
    val current: Double,
    val amplitude: Double
)

data class BasicStats(
    val min: Double,
    val max: Double,
    val avg: Double
)

data class DataQuality(
    val validSamples: Int,
    val totalSamples: Int,
    val confianca: Double,
    val missingRate: Double,
    val isReliable: Boolean
)
