package br.com.zup.edu.external.itauERP

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("itauERP")
interface ItauERPClient {

    @Get(value = "/api/v1/clientes/{clienteId}/contas?tipo={tipoConta}", produces = [MediaType.APPLICATION_JSON])
    fun obtemContaCliente(@PathVariable("clienteId") clienteId: String, @QueryValue("tipoConta") tipoConta: String): HttpResponse<ItauERPContaResponse>

}