package br.com.zup.edu.external.bacenPix

import br.com.zup.edu.model.chavePix.TipoChavePix
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement


@JacksonXmlRootElement(localName = "CreatePixKeyRequest")
data class BacenPixCreateRequest(
    @field:JacksonXmlProperty(localName = "keyType") val keyType: TipoChaveBacen,
    @field:JacksonXmlProperty(localName = "key") val key: String?,
    @field:JacksonXmlElementWrapper(localName = "bankAccount") val bankAccount: BankAccount,
    @field:JacksonXmlElementWrapper(localName = "owner") val owner: Owner
)