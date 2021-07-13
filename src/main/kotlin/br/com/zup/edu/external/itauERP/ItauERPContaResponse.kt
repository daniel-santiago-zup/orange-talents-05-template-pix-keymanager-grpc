package br.com.zup.edu.external.itauERP

import br.com.zup.edu.model.chavePix.TipoConta

data class ItauERPContaResponse(
    val tipo: TipoConta,
    val instituicao: Instituicao,
    val agencia: String,
    val numero: String,
    val titular: Titular
) {
    data class Instituicao(
        val nome: String,
        val ispb: String
    )

    data class Titular(
        val id: String,
        val nome: String,
        val cpf: String
    )
}

