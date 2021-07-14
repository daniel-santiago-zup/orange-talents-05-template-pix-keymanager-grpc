package br.com.zup.edu.model.chavePix

import br.com.zup.edu.annotations.ValidUUID
import com.sun.istack.NotNull
import org.hibernate.validator.constraints.br.CPF
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
data class ChavePix(
    @field:NotBlank @field:ValidUUID val idCliente: String,
    @field:NotNull @field:Enumerated(EnumType.STRING) val tipoConta: TipoConta,
    @field:NotNull @field:Enumerated(EnumType.STRING) val tipoChave: TipoChavePix,
    @field:NotBlank @field:Size(max = 77) @field:Column(unique = true) var valorChave: String,
    @field:NotNull @field:Valid @Embedded val conta: ContaAssociada
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    lateinit var id: UUID

    var criadaEm: LocalDateTime = LocalDateTime.now()

    @Embeddable
    data class ContaAssociada(
        @field:NotBlank val instituicao: String,
        @field:NotBlank val ispb: String,
        @field:NotBlank val nome: String,
        @field:NotBlank @field:CPF val cpf: String,
        @field:NotBlank val agencia: String,
        @field:NotBlank val numeroDaConta: String
    )
}