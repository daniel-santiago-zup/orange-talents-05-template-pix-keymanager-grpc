package br.com.zup.edu.external.bacenPix

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import java.time.LocalDateTime

@JacksonXmlRootElement(localName = "CreatePixKeyResponse")
data class BacenPixCreateResponse(
    @field:JacksonXmlProperty(localName = "keyType") val keyType: TipoChaveBacen,
    @field:JacksonXmlProperty(localName = "key") val key: String,
    @field:JacksonXmlProperty(localName = "bankAccount") val bankAccount: BankAccount,
    @field:JacksonXmlProperty(localName = "owner") val owner: Owner,
    @field:JacksonXmlProperty(localName = "createdAt") val createdAt: LocalDateTime
)
