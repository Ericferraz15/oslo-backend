package dev.weg.oslo.services

import dev.weg.oslo.model.Event
import dev.weg.oslo.model.TelemetryContext
import dev.weg.oslo.services.detectors.EventDetector
import org.springframework.stereotype.Service

@Service
class EventDetectionEngine(
    private val detectors: List<EventDetector>
) {

    fun analyze(context: TelemetryContext): List<Event> {
        return detectors.flatMap { it.detect(context) }
    }
}
