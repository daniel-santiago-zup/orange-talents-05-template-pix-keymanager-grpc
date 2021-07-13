package br.com.zup.edu.model.chavePix

import br.com.zup.edu.external.bacenPix.BankAccount

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
}