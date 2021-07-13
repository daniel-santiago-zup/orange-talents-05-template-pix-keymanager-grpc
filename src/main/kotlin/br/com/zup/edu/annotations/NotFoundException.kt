package br.com.zup.edu.annotations

class NotFoundException (override val message: String = "recurso não encontrado"): Exception()