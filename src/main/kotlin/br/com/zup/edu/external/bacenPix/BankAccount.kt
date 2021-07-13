package br.com.zup.edu.external.bacenPix

import br.com.zup.edu.proto.TipoConta
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

data class BankAccount(
    @field:JacksonXmlProperty(localName = "participant") val participant: String,
    @field:JacksonXmlProperty(localName = "branch") val branch: String,
    @field:JacksonXmlProperty(localName = "accountNumber") val accountNumber: String,
    @field:JacksonXmlProperty(localName = "accountType") val accounType: AccountType,
) {
    enum class AccountType {
        CACC {
            override fun toTipoContaProtobuff(): TipoConta {
                return TipoConta.CONTA_CORRENTE
            }
        },
        SVGS {
            override fun toTipoContaProtobuff(): TipoConta {
                return TipoConta.CONTA_POUPANCA
            }
        };

        abstract fun toTipoContaProtobuff(): TipoConta
    }
}
