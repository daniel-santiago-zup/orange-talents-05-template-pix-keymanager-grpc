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
) {

    @JacksonXmlRootElement(localName = "bankAccount")
    data class BankAccount(
        @field:JacksonXmlProperty(localName = "participant") val participant: String,
        @field:JacksonXmlProperty(localName = "branch") val branch: String,
        @field:JacksonXmlProperty(localName = "accountNumber") val accountNumber: String,
        @field:JacksonXmlProperty(localName = "accountType") val accounType: AccountType,
    ) {
        enum class AccountType {
            CACC,
            SVGS
        }
    }

    @JacksonXmlRootElement(localName = "owner")
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
}