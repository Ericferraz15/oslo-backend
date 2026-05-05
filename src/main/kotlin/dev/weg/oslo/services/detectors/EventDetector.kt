package dev.weg.oslo.services.detectors

import dev.weg.oslo.model.Event
import dev.weg.oslo.model.TelemetryContext

interface EventDetector {
    fun detect(context: TelemetryContext): List<Event>
}
