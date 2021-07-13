package br.com.zup.edu.model.chavePix

import br.com.zup.edu.external.bacenPix.BacenPixCreateRequest

enum class TipoConta {
    CONTA_CORRENTE {
        override fun paraBacenPixAccount(): BacenPixCreateRequest.BankAccount.AccountType {
            return BacenPixCreateRequest.BankAccount.AccountType.CACC
        }
    },
    CONTA_POUPANCA {
        override fun paraBacenPixAccount(): BacenPixCreateRequest.BankAccount.AccountType {
            return BacenPixCreateRequest.BankAccount.AccountType.SVGS
        }
    };

    abstract fun paraBacenPixAccount(): BacenPixCreateRequest.BankAccount.AccountType
}