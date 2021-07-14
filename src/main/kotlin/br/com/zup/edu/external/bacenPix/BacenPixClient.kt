package br.com.zup.edu.external.bacenPix

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client("bacenPix")
interface BacenPixClient {

    @Get("/api/v1/pix/keys", produces = [MediaType.APPLICATION_XML])
    fun listaChavesPix(): HttpResponse<BacenPixListResponse>

    @Get("/api/v1/pix/keys/{key}", produces = [MediaType.APPLICATION_XML])
    fun detalhaChavePix(@PathVariable("key") key: String): HttpResponse<BacenPixDetailResponse>

    @Post("/api/v1/pix/keys", consumes = [MediaType.APPLICATION_XML], produces = [MediaType.APPLICATION_XML])
    fun registraChavePix(@Body createRequest: BacenPixCreateRequest): HttpResponse<BacenPixCreateResponse>

    @Delete("/api/v1/pix/keys/{key}", consumes = [MediaType.APPLICATION_XML], produces = [MediaType.APPLICATION_XML])
    fun deletaChavePix(@PathVariable("key") key: String, @Body deleteRequest: BacenPixDeleteRequest): HttpResponse<BacenPixDeleteResponse>

}