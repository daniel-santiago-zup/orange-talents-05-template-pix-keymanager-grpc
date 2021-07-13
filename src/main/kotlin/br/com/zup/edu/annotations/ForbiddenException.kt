package br.com.zup.edu.annotations

class ForbiddenException (override val message: String = "ação não permitida"): Exception() {

}
