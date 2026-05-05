package dev.weg.oslo.services

import dev.weg.oslo.model.Event
import dev.weg.oslo.model.EventView
import dev.weg.oslo.model.TelemetryContext
import org.springframework.stereotype.Component

@Component
class EventTimeResolver {

    fun resolve(events: List<Event>, context: TelemetryContext): List<EventView> {
        val timestamps = context.timestamps

        return events.map { event ->
            EventView(
                tipo = event.tipo,
                confianca = event.confianca,
                evidencias = event.evidencias,
                inicio = event.inicio?.let { idx -> timestamps.getOrNull(idx) },
                fim = event.fim?.let { idx -> timestamps.getOrNull(idx) }
            )
        }
    }
}
