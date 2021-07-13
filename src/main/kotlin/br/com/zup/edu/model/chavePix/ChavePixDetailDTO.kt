package br.com.zup.edu.model.chavePix

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class ChavePixDetailDTO(
    @field:NotBlank val idCliente: String,
    @field:NotBlank val pixId: String
)