package dev.weg.oslo.services.detectors

import dev.weg.oslo.model.Event
import dev.weg.oslo.model.EventType
import dev.weg.oslo.model.TelemetryContext
import org.springframework.stereotype.Component

@Component
class DefrostDetector : EventDetector {

    override fun detect(context: TelemetryContext): List<Event> {
        val degelo = context.series.statusDegelo ?: return emptyList()

        val events = mutableListOf<Event>()

        var start: Int? = null

        for (i in degelo.indices) {
            val active = degelo[i] == 1

            if (active && start == null) {
                start = i
            }

            if (!active && start != null) {

                val duration = i - start
                val confianca = (duration / degelo.size.toDouble()).coerceIn(0.5, 1.0)

                events.add(
                    Event(
                        tipo = EventType.DEGELO,
                        inicio = start,
                        fim = i,
                        confianca = confianca,
                        evidencias = listOf(
                            "Status de degelo ativo no período",
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
