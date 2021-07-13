package br.com.zup.edu.model.chavePix

import br.com.zup.edu.external.bacenPix.BankAccount
import br.com.zup.edu.proto.TipoConta

enum class TipoConta {
    CONTA_CORRENTE {
        override fun paraBacenPixAccount(): BankAccount.AccountType {
            return BankAccount.AccountType.CACC
        }
    },
    CONTA_POUPANCA {
        override fun paraBacenPixAccount(): BankAccount.AccountType {
            return BankAccount.AccountType.SVGS
        }
    };

    abstract fun paraBacenPixAccount(): BankAccount.AccountType
    open fun toTipoContaProtobuff(): TipoConta = TipoConta.valueOf(this.toString())
}