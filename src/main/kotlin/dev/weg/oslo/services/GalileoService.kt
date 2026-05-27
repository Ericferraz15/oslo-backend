package dev.weg.oslo.services

import dev.weg.oslo.client.GalileoClient
import dev.weg.oslo.model.AnalysisResult
import org.springframework.stereotype.Service

@Service
class GalileoService(
    private val client: GalileoClient,
    private val mapper: TelemetryMapper,
    private val engine: EventDetectionEngine,
    private val timeResolver: EventTimeResolver,
    private val insightService: SystemInsightService
) {

    fun analyze(dispositivoId: Int): AnalysisResult {
        val telemetria = client.getTelemetria(dispositivoId)
        val alarmes = client.getAlarmes()

        val context = mapper.toContext(telemetria)

        val rawEvents = engine.analyze(context)
        val events = timeResolver.resolve(rawEvents, context)

        val alarmesDoDispositivo =
            alarmes.filter { it.dispositivoId == dispositivoId }

        val insights = insightService.analyze(context, rawEvents)

        return AnalysisResult(
            stats = context.stats.temperatura,
            events = events,
            alarmes = alarmesDoDispositivo,
            insights = insights
        )
    }
}
