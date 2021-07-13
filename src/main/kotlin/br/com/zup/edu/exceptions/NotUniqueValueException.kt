package br.com.zup.edu.exceptions

class NotUniqueValueException(override val message: String = "valor deve ser único") : Exception() {
}