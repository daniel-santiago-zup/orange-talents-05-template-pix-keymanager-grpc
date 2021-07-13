package br.com.zup.edu.model.chavePix

import br.com.zup.edu.annotations.ErrorAroundHandler
import br.com.zup.edu.annotations.ForbiddenException
import br.com.zup.edu.annotations.NotFoundException
import br.com.zup.edu.annotations.NotUniqueValueException
import br.com.zup.edu.external.bacenPix.*
import br.com.zup.edu.external.itauERP.ItauERPClient
import br.com.zup.edu.proto.ChavePixRequest
import br.com.zup.edu.proto.ChavePixResponse
import br.com.zup.edu.proto.DeletaChavePixRequest
import br.com.zup.edu.proto.DeletaChavePixResponse
import br.com.zup.edu.proto.KeyManagerServiceGrpc.KeyManagerServiceImplBase
import io.grpc.stub.StreamObserver
import io.micronaut.http.HttpStatus
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.ConstraintViolationException
import javax.validation.Validator

@ErrorAroundHandler
@Singleton
class ChavePixEndpoint(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val itauClient: ItauERPClient,
    @Inject val bacenPixClient: BacenPixClient,
    @Inject val validator: Validator
) : KeyManagerServiceImplBase() {

    @Transactional
    override fun registraChave(request: ChavePixRequest, responseObserver: StreamObserver<ChavePixResponse>) {

        val chavePixDTO = request.converte(validator)

        val contaResponse = itauClient.obtemContaCliente(chavePixDTO.idCliente, chavePixDTO.tipoConta.toString()).body()
            ?: throw NotFoundException()

        val bacenPixRequest = BacenPixCreateRequest(
            keyType = chavePixDTO.tipoChave.toTipoChaveBacen(),
            key = chavePixDTO.valorChave,
            bankAccount = BankAccount(
                participant = contaResponse.instituicao.ispb,
                branch = contaResponse.agencia,
                accountNumber = contaResponse.numero,
                accounType = contaResponse.tipo.paraBacenPixAccount()
            ),
            owner = Owner(
                type = Owner.OwnerType.NATURAL_PERSON,
                name = contaResponse.titular.nome,
                taxIdNumber = contaResponse.titular.cpf
            )
        )

        val bacenPixResponse = bacenPixClient.registraChavePix(bacenPixRequest)

        if (bacenPixResponse.status == HttpStatus.UNPROCESSABLE_ENTITY) throw NotUniqueValueException("Chave ja esta registrada no Banco Central")

        var chavePix: ChavePix = if (chavePixDTO.tipoChave == TipoChavePix.RANDOM_KEY) {
            chavePixDTO.converte(contaResponse, bacenPixResponse.body().key)
        } else {
            chavePixDTO.converte(contaResponse)
        }

        chavePix = chavePixRepository.save(chavePix)

        responseObserver.onNext(
            ChavePixResponse.newBuilder()
                .setPixId(chavePix.id.toString())
                .build()
        )

        responseObserver.onCompleted()

    }

    override fun deletaChave(
        request: DeletaChavePixRequest,
        responseObserver: StreamObserver<DeletaChavePixResponse>
    ) {

        val pixUuid = UUID.fromString(request.pixId)

        if (!chavePixRepository.existsById(pixUuid)) throw NotFoundException()

        val chavePix = chavePixRepository.findById(pixUuid).get()

        val bacenDeleteRequest = BacenPixDeleteRequest (
            key = chavePix.valorChave,
            participant = chavePix.conta.ispb
        )

        val bacenDeleteReponse = bacenPixClient.deletaChavePix(chavePix.valorChave, bacenDeleteRequest)

        when (bacenDeleteReponse.status) {
            HttpStatus.NOT_FOUND -> throw NotFoundException("Chave pix não encontrada nos registros do Banco Central")
            HttpStatus.FORBIDDEN -> throw ForbiddenException()
        }

        chavePixRepository.deleteById(pixUuid)

        responseObserver.onNext(DeletaChavePixResponse.getDefaultInstance())
        responseObserver.onCompleted()

    }

}

fun ChavePixRequest.converte(validador: Validator): ChavePixDTO {
    val chavePix = ChavePixDTO(
        idCliente = idCliente,
        tipoChave = TipoChavePix.valueOf(tipoChave.name),
        valorChave = valorChave,
        tipoConta = TipoConta.valueOf(tipoConta.name)
    )

    val erros = validador.validate(chavePix)

    if (erros.isNotEmpty()) {
        throw ConstraintViolationException(erros)
    }

    return chavePix
}