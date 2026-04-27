package dev.weg.oslo.client

import dev.weg.oslo.model.Alarme
import dev.weg.oslo.model.TelemetriaResponse
import dev.weg.oslo.model.Unidade
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class GalileoClient(private val webClient: WebClient) {

    fun getUnidades(): List<Unidade> {
        return webClient.get()
            .uri("/api_hackathon?route=unidades")
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<List<Unidade>>() {})
            .block() ?: emptyList()
    }

    fun getAlarmes(): List<Alarme> {
        return webClient.get()
            .uri("/api_hackathon?route=alarmes")
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<List<Alarme>>() {})
            .block() ?: emptyList()
    }

    fun getTelemetria(dispositivoId: Int): TelemetriaResponse {
        return webClient.get()
            .uri("/api_hackathon?route=telemetria&dispositivoId=$dispositivoId")
            .retrieve()
            .bodyToMono(TelemetriaResponse::class.java)
            .block()!!
    }
}
