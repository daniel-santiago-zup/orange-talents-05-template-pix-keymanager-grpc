package br.com.zup.edu.model.chavePix

import br.com.zup.edu.annotations.UniqueValue
import br.com.zup.edu.annotations.ValidPixKey
import br.com.zup.edu.annotations.ValidUUID
import br.com.zup.edu.external.itauERP.ItauERPContaResponse
import com.sun.istack.NotNull
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
@ValidPixKey
data class ChavePixDTO(
    @field:NotBlank @field:ValidUUID val idCliente: String,
    @field:NotNull val tipoConta: TipoConta,
    @field:NotNull val tipoChave: TipoChavePix,
    @field:Size(max = 77) @field:UniqueValue(
        entidade = ChavePix::class,
        nomeCampo = "valorChave"
    ) var valorChave: String?,
) {

    fun converte(itauContaResponse: ItauERPContaResponse): ChavePix {
        if (valorChave == null || tipoChave == TipoChavePix.RANDOM_KEY) throw IllegalArgumentException()

        val conta = ChavePix.ContaAssociada(
            instituicao = itauContaResponse.instituicao.nome,
            ispb = itauContaResponse.instituicao.ispb,
            numeroDaConta = itauContaResponse.numero,
            agencia = itauContaResponse.agencia,
            nome = itauContaResponse.titular.nome,
            cpf = itauContaResponse.titular.cpf
        )

        return ChavePix(
            idCliente = idCliente,
            tipoConta = tipoConta,
            tipoChave = tipoChave,
            valorChave = valorChave!!,
            conta = conta,
        )

    }

    fun converte(itauContaResponse: ItauERPContaResponse, chaveAleatoriaGerada: String): ChavePix {
        if (tipoChave != TipoChavePix.RANDOM_KEY) throw IllegalArgumentException()

        val conta = ChavePix.ContaAssociada(
            instituicao = itauContaResponse.instituicao.nome,
            ispb = itauContaResponse.instituicao.ispb,
            numeroDaConta = itauContaResponse.numero,
            agencia = itauContaResponse.agencia,
            nome = itauContaResponse.titular.nome,
            cpf = itauContaResponse.titular.cpf
        )

        return ChavePix(
            idCliente = idCliente,
            tipoConta = tipoConta,
            tipoChave = tipoChave,
            valorChave = chaveAleatoriaGerada,
            conta = conta,
        )

    }

}
