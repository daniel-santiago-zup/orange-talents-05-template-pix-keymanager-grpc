package br.com.zup.edu.external.bacenPix

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client

@Client("bacenPix")
interface BacenPixClient {

    @Post("/api/v1/pix/keys", consumes = [MediaType.APPLICATION_XML], produces = [MediaType.APPLICATION_XML])
    fun registraChavePix(@Body createRequest: BacenPixCreateRequest): HttpResponse<BacenPixCreateResponse>

    @Delete("/api/v1/pix/keys/{key}", consumes = [MediaType.APPLICATION_XML], produces = [MediaType.APPLICATION_XML])
    fun deletaChavePix(@PathVariable("key") key: String, @Body deleteRequest: BacenPixDeleteRequest): HttpResponse<BacenPixDeleteResponse>

}