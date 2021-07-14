package br.com.zup.edu.model.chavePix

import br.com.zup.edu.annotations.ErrorAroundHandler
import br.com.zup.edu.exceptions.ForbiddenException
import br.com.zup.edu.exceptions.NotFoundException
import br.com.zup.edu.exceptions.NotUniqueValueException
import br.com.zup.edu.external.bacenPix.*
import br.com.zup.edu.external.itauERP.ItauERPClient
import br.com.zup.edu.pix.Instituicoes
import br.com.zup.edu.proto.*
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
    override fun registraChave(request: CriaChavePixRequest, responseObserver: StreamObserver<CriaChavePixResponse>) {

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
            CriaChavePixResponse.newBuilder()
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

        val bacenDeleteRequest = BacenPixDeleteRequest(
            key = chavePix.valorChave,
            participant = chavePix.conta.ispb
        )

        val bacenDeleteReponse = bacenPixClient.deletaChavePix(chavePix.valorChave, bacenDeleteRequest)

        if (bacenDeleteReponse.status == HttpStatus.NOT_FOUND) throw NotFoundException("Chave pix não encontrada nos registros do Banco Central")
        else if (bacenDeleteReponse.status == HttpStatus.FORBIDDEN) throw ForbiddenException()

        chavePixRepository.deleteById(pixUuid)

        responseObserver.onNext(DeletaChavePixResponse.getDefaultInstance())
        responseObserver.onCompleted()

    }

    override fun detalhaChaveExternal(
        request: DetalhaChavePixExternalRequest,
        responseObserver: StreamObserver<DetalhaChavePixExternalResponse>
    ) {

        val chavePixDetailDTO = request.converte(validator)

        val chavePix = chavePixRepository.findById(UUID.fromString(chavePixDetailDTO.pixId))
            .orElseThrow() {
                NotFoundException("Chave pix fornecida não encontrada localmente")
            }

        if (chavePixDetailDTO.idCliente != chavePix.idCliente) throw ForbiddenException("O pixId fornecido não pertence ao cliente")
        if (bacenPixClient.detalhaChavePix(chavePix.valorChave).status == HttpStatus.NOT_FOUND) throw NotFoundException(
            "Chave pix fornecida não encontrada no Banco Central"
        )

        responseObserver.onNext(
            DetalhaChavePixExternalResponse.newBuilder()
                .setIdCliente(chavePix.idCliente)
                .setPixId(chavePix.id.toString())
                .setTipoChave(chavePix.tipoChave.toTipoChavePixProtobuff())
                .setValorChave(chavePix.valorChave)
                .setTitular(
                    DetalhaChavePixExternalResponse.Titular.newBuilder()
                        .setNome(chavePix.conta.nome)
                        .setCpf(chavePix.conta.cpf)
                        .build()
                )
                .setConta(
                    DetalhaChavePixExternalResponse.Conta.newBuilder()
                        .setInstituicao(chavePix.conta.instituicao)
                        .setAgencia(chavePix.conta.agencia)
                        .setNumero(chavePix.conta.numeroDaConta)
                        .setTipo(chavePix.tipoConta.toTipoContaProtobuff())
                        .build()
                )
                .setCriadoEm(chavePix.criadaEm.toString())
                .build()
        )

        responseObserver.onCompleted()
    }

    override fun detalhaChaveInternal(
        request: DetalhaChaveInternalRequest,
        responseObserver: StreamObserver<DetalhaChaveInternalResponse>
    ) {
        if (request.chavePix.isNullOrBlank()) throw IllegalArgumentException("Chave pix deve ser preenchida")

        val bacenChavePixResponse = bacenPixClient.detalhaChavePix(request.chavePix)

        if (bacenChavePixResponse.status == HttpStatus.NOT_FOUND) throw NotFoundException("Chave pix não encontrada no Banco Central")

        val chavePix =
            bacenChavePixResponse.body() ?: throw IllegalStateException("Corpo da resposta do Banco Central vazio")

        responseObserver.onNext(
            DetalhaChaveInternalResponse.newBuilder()
                .setTipoChave(chavePix.keyType.toTipoChaveProtobuff())
                .setValorChave(chavePix.key)
                .setTitular(
                    DetalhaChaveInternalResponse.Titular.newBuilder()
                        .setNome(chavePix.owner.name)
                        .setCpf(chavePix.owner.taxIdNumber)
                        .build()
                )
                .setConta(
                    DetalhaChaveInternalResponse.Conta.newBuilder()
                        .setInstituicao(Instituicoes.nome(chavePix.bankAccount.participant))
                        .setAgencia(chavePix.bankAccount.branch)
                        .setNumero(chavePix.bankAccount.accountNumber)
                        .setTipo(chavePix.bankAccount.accounType.toTipoContaProtobuff())
                        .build()
                )
                .setCriadoEm(chavePix.createdAt.toString())
                .build()
        )

        responseObserver.onCompleted()

    }

    override fun listaChaves(request: ListaChavesRequest, responseObserver: StreamObserver<ListaChavesResponse>) {

        val chavesPix = chavePixRepository.findAllByIdCliente(request.idCliente).ifEmpty {
            throw NotFoundException("Não foi possível encontrar chaves para esse cliente")
        }

        responseObserver.onNext(ListaChavesResponse.newBuilder()
            .addAllChaves(chavesPix.map {
                ListaChavesResponse.ListaChavesDetails.newBuilder()
                    .setPixId(it.id.toString())
                    .setIdCliente(it.idCliente)
                    .setTipoChave(it.tipoChave.toTipoChavePixProtobuff())
                    .setValorChave(it.valorChave)
                    .setTipoConta(it.tipoConta.toTipoContaProtobuff())
                    .setCriadoEm(it.criadaEm.toString())
                    .build()
            })
            .build())

        responseObserver.onCompleted()
    }

}

fun CriaChavePixRequest.converte(validador: Validator): ChavePixCreateDTO {
    val chavePix = ChavePixCreateDTO(
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

fun DetalhaChavePixExternalRequest.converte(validador: Validator): ChavePixDetailDTO {
    val chavePixDetailDTO = ChavePixDetailDTO(
        idCliente = idCliente,
        pixId = pixId
    )

    val erros = validador.validate(chavePixDetailDTO)

    if (erros.isNotEmpty()) {
        throw ConstraintViolationException(erros)
    }

    return chavePixDetailDTO

}