package br.com.zup.edu.external.bacenPix

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import java.time.LocalDateTime

@JacksonXmlRootElement(localName = "DeletePixKeyResponse")
data class BacenPixDeleteResponse(
    @field:JacksonXmlProperty(localName = "key") val key: String,
    @field:JacksonXmlProperty(localName = "participant") val participant: String,
    @field:JacksonXmlProperty(localName = "deletedAt") val deletedAt: LocalDateTime
)