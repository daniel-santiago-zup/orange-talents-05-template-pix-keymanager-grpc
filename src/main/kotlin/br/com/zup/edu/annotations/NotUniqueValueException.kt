package br.com.zup.edu.annotations

class NotUniqueValueException: Exception() {
    override val message: String
        get() = "valor deve ser único"
}