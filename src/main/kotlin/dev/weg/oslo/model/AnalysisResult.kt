package dev.weg.oslo.model

data class AnalysisResult(
    val stats: TemperatureStats,
    val events: List<EventView>,
    val alarmes: List<Alarme>,
    val insights: SystemInsights
)
