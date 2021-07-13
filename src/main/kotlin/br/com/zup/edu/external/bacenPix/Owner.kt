package br.com.zup.edu.external.bacenPix

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

data class Owner(
    @field:JacksonXmlProperty(localName = "type") val type: OwnerType,
    @field:JacksonXmlProperty(localName = "name") val name: String,
    @field:JacksonXmlProperty(localName = "taxIdNumber") val taxIdNumber: String
) {
    enum class OwnerType {
        NATURAL_PERSON,
        LEGAL_PERSON
    }
}