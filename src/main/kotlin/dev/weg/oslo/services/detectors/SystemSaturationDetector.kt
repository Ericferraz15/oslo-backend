package dev.weg.oslo.services.detectors

import dev.weg.oslo.model.Event
import dev.weg.oslo.model.EventType
import dev.weg.oslo.model.TelemetryContext
import org.springframework.stereotype.Component

@Component
class SystemSaturationDetector : EventDetector {

    override fun detect(context: TelemetryContext): List<Event> {
        val valvula = context.series.aberturaValvula ?: return emptyList()
        val temps = context.series.temperaturaAmbiente
        val stats = context.stats.temperatura

        val threshold = stats.avg + (stats.amplitude * 0.5)

        val events = mutableListOf<Event>()

        var start: Int? = null

        for (i in temps.indices) {
            val highValve = i < valvula.size && valvula[i] > 80
            val highTemp = temps[i] > threshold

            val saturated = highValve && highTemp

            if (saturated && start == null) {
                start = i
            }

            if (!saturated && start != null) {
                val end = i

                val duration = end - start
                val confianca = (duration / temps.size.toDouble()).coerceIn(0.5, 1.0)

                events.add(
                    Event(
                        tipo = EventType.SISTEMA_SATURADO,
                        inicio = start,
                        fim = end,
                        confianca = confianca,
                        evidencias = listOf(
                            "Alta válvula + alta temperatura",
                            "Duração: $duration pontos"
                        )
                    )
                )

                start = null
            }
        }

        return events
    }
}
