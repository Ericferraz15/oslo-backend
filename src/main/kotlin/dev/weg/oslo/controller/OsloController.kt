package dev.weg.oslo.controller

import dev.weg.oslo.services.GalileoService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/oslo")
class OsloController(
    private val service: GalileoService,
) {

    @RequestMapping("/analyze/{id}")
    fun analyze(@PathVariable id: Int) = service.analyze(id)
}
