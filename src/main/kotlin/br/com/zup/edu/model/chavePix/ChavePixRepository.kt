package br.com.zup.edu.model.chavePix

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository : JpaRepository<ChavePix, UUID> {
    fun findAllByIdCliente(idCliente: String): List<ChavePix>
}