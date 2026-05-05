package dev.weg.oslo.controller

import dev.weg.oslo.client.GalileoClient
import dev.weg.oslo.model.TelemetriaResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test")
class TestController(private val client: GalileoClient) {

    @GetMapping("/alarmes")
    fun getAlarmes() = client.getAlarmes()

    @GetMapping("/telemetria/{id}")
    fun getTelemetria(
        @PathVariable id: Int,
    ): TelemetriaResponse{
        return client.getTelemetria(id)
    }

    @GetMapping("/unidade")
    fun getUnidade() = client.getUnidades()
}
