package dev.weg.oslo.services.detectors

import dev.weg.oslo.model.Event
import dev.weg.oslo.model.EventType
import dev.weg.oslo.model.TelemetryContext
import org.springframework.stereotype.Component

@Component
class HighTemperatureDetector : EventDetector {

    override fun detect(context: TelemetryContext): List<Event> {
        val temps = context.series.temperaturaAmbiente
        val stats = context.stats.temperatura

        val threshold = stats.max - (stats.amplitude * 0.2)
        val events = mutableListOf<Event>()

        var start: Int? = null

        for (i in temps.indices) {
            val high = temps[i] > threshold

            if (high && start == null) {
                start = i
            }

            if (!high && start != null) {
                val segment = temps.subList(start, i)
                val avgExcess = segment.map { it - threshold }.average()

                val confianca = (avgExcess / stats.amplitude).coerceIn(0.5, 1.0)

                events.add(
                    Event(
                        tipo = EventType.TEMPERATURA_ALTA,
                        inicio = start,
                        fim = i,
                        confianca = confianca,
                        evidencias = listOf(
                            "Temperatura acima do threshold",
                            "Excesso médio: $avgExcess"
                        )
                    )
                )

                start = null
            }
        }

        return events
    }
}
