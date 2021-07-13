package br.com.zup.edu.external.bacenPix

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "DeletePixKeyRequest")
data class BacenPixDeleteRequest(
    @field:JacksonXmlProperty(localName = "key") val key: String,
    @field:JacksonXmlProperty(localName = "participant") val participant: String
)