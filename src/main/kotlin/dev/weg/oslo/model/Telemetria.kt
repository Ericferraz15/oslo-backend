package dev.weg.oslo.model

import java.time.LocalDateTime

data class TelemetriaResponse(
    val datasets: List<Dataset>,
    val labels: List<String>
)

data class Dataset(
    val label: String,
    val values: List<Double>
)

data class Alarme(
    val contaId: Int,
    val contaNm: String,
    val lojaId: Int,
    val lojaNm: String,
    val nrPedido: String,
    val dispositivoId: Int,
    val dispositivoNm: String,
    val alarmeDesc: String,
    val criticidade: String,
    val tempo: String
)

data class Unidade(
    val lojaId: Int,
    val ativo: Boolean,
    val lojaNm: String,
    val lojaApelido: String,
    val tpContratoId: Int,
    val tpContratoNm: String,
    val dtValContrato: LocalDateTime,
    val contaId: Int,
    val contaNm: String,
    val cnpj: String,
    val nrPedido: String,
    val telefone: String,
    val dhSinalVida: LocalDateTime,
    val apiTipo: String,
    val endereco: String,
)