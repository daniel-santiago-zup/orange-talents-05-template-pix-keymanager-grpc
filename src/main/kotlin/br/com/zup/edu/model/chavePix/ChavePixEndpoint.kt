package br.com.zup.edu.model.chavePix

import br.com.zup.edu.annotations.ErrorAroundHandler
import br.com.zup.edu.annotations.NotFoundException
import br.com.zup.edu.external.itauERP.ItauERPClient
import br.com.zup.edu.proto.ChavePixRequest
import br.com.zup.edu.proto.ChavePixResponse
import br.com.zup.edu.proto.DeletaChavePixRequest
import br.com.zup.edu.proto.DeletaChavePixResponse
import br.com.zup.edu.proto.KeyManagerServiceGrpc.KeyManagerServiceImplBase
import io.grpc.Status
import io.grpc.stub.StreamObserver
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
    @Inject val validator: Validator
) : KeyManagerServiceImplBase() {

    @Transactional
    override fun registraChave(request: ChavePixRequest, responseObserver: StreamObserver<ChavePixResponse>) {

        val chavePixDTO = request.converte(validator)

        val contaResponse = itauClient.obtemContaCliente(chavePixDTO.idCliente, chavePixDTO.tipoConta.toString()).body()
            ?: throw NotFoundException()

        val chavePix = chavePixRepository.save(chavePixDTO.converte(contaResponse))

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