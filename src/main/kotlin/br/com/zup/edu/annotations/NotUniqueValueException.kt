package br.com.zup.edu.annotations

class NotUniqueValueException(override val message: String = "valor deve ser único") : Exception() {
}