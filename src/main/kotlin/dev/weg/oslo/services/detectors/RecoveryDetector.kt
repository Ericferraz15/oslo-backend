package dev.weg.oslo.services.detectors

import dev.weg.oslo.model.Event
import dev.weg.oslo.model.EventType
import dev.weg.oslo.model.TelemetryContext
import org.springframework.stereotype.Component

@Component
class RecoveryDetector : EventDetector {

    override fun detect(context: TelemetryContext): List<Event> {
        val temps = context.series.temperaturaAmbiente
        val stats = context.stats.temperatura

        val spikeThreshold = stats.avg + (stats.amplitude * 0.5)
        val recoveryThreshold = stats.avg + (stats.amplitude * 0.2)

        val events = mutableListOf<Event>()

        var spikeStart: Int? = null

        for (i in temps.indices) {
            val temp = temps[i]

            if (spikeStart == null && temp > spikeThreshold) {
                spikeStart = i
            }

            if (spikeStart != null && temp <= recoveryThreshold) {
                val end = i

                val duration = end - spikeStart
                val confianca = (1.0 - (duration / temps.size.toDouble())).coerceIn(0.5, 1.0)

                events.add(
                    Event(
                        tipo = EventType.RECUPERACAO,
                        inicio = spikeStart,
                        fim = end,
                        confianca = confianca,
                        evidencias = listOf(
                            "Pico seguido de recuperação",
                            "Duração: $duration pontos"
                        )
                    )
                )

                spikeStart = null
            }
        }

        return events
    }
}
