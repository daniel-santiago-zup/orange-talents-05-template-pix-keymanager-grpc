package br.com.zup.edu.annotations

class NotFoundException: Exception() {

    override val message: String
        get() = "recurso não encontrado"

}