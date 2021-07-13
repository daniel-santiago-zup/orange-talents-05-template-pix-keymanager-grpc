package br.com.zup.edu.model.chavePix

import br.com.zup.edu.external.bacenPix.*
import br.com.zup.edu.proto.ChavePixRequest
import br.com.zup.edu.proto.KeyManagerServiceGrpc
import io.grpc.Channel
import io.grpc.Status
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RegistraChavePixEndpointTest(@Inject val keyManagerClient: KeyManagerServiceGrpc.KeyManagerServiceBlockingStub) {

    @Inject
    lateinit var chavePixRepository: ChavePixRepository

    @Inject
    lateinit var bacenPixClientMock: BacenPixClient

    @BeforeEach
    internal fun setUp() {
        chavePixRepository.deleteAll()
    }

    @Test
    internal fun `deve registrar uma chave de CPF com sucesso`() {

        val (bacenPixCreateRequest, bacenPixCreateResponse) = geraBacenPixCreateRequestEResponse(
            tipoChavePix = TipoChavePix.CPF,
            valorChave = "06628726061"
        )

        Mockito.`when`(bacenPixClientMock.registraChavePix(bacenPixCreateRequest)).thenReturn(bacenPixCreateResponse)

        val request = ChavePixRequest.newBuilder()
            .setIdCliente("0d1bb194-3c52-4e67-8c35-a93c0af9284f")
            .setTipoConta(br.com.zup.edu.proto.TipoConta.CONTA_CORRENTE)
            .setTipoChave(br.com.zup.edu.proto.TipoChavePix.CPF)
            .setValorChave("06628726061")
            .build()

        assertDoesNotThrow {
            val response = keyManagerClient.registraChave(request)
            assertTrue(response.pixId.isNotBlank())
        }
    }

    @Test
    internal fun `deve registrar uma chave de telefone com sucesso`() {
        val (bacenPixCreateRequest, bacenPixCreateResponse) = geraBacenPixCreateRequestEResponse(
            tipoChavePix = TipoChavePix.PHONE,
            valorChave = "+5534998981323"
        )

        Mockito.`when`(bacenPixClientMock.registraChavePix(bacenPixCreateRequest)).thenReturn(bacenPixCreateResponse)

        val request = ChavePixRequest.newBuilder()
            .setIdCliente("0d1bb194-3c52-4e67-8c35-a93c0af9284f")
            .setTipoConta(br.com.zup.edu.proto.TipoConta.CONTA_CORRENTE)
            .setTipoChave(br.com.zup.edu.proto.TipoChavePix.PHONE)
            .setValorChave("+5534998981323")
            .build()

        assertDoesNotThrow {
            val response = keyManagerClient.registraChave(request)
            assertTrue(response.pixId.isNotBlank())
        }
    }

    @Test
    internal fun `deve registrar uma chave de email com sucesso`() {

        val (bacenPixCreateRequest, bacenPixCreateResponse) = geraBacenPixCreateRequestEResponse(
            tipoChavePix = TipoChavePix.EMAIL,
            valorChave = "email@email.com"
        )

        Mockito.`when`(bacenPixClientMock.registraChavePix(bacenPixCreateRequest)).thenReturn(bacenPixCreateResponse)

        val request = ChavePixRequest.newBuilder()
            .setIdCliente("0d1bb194-3c52-4e67-8c35-a93c0af9284f")
            .setTipoConta(br.com.zup.edu.proto.TipoConta.CONTA_CORRENTE)
            .setTipoChave(br.com.zup.edu.proto.TipoChavePix.EMAIL)
            .setValorChave("email@email.com")
            .build()

        assertDoesNotThrow {
            val response = keyManagerClient.registraChave(request)
            assertTrue(response.pixId.isNotBlank())
        }
    }

    @Test
    internal fun `deve registrar uma chave aleatoria com sucesso`() {
        val (bacenPixCreateRequest, bacenPixCreateResponse) = geraBacenPixCreateRequestEResponse(
            tipoChavePix = TipoChavePix.RANDOM_KEY,
            valorChave = "",
            valorChaveReponse = "2eb0cb29-25c3-4f1c-92db-babe4c788e28"
        )

        Mockito.`when`(bacenPixClientMock.registraChavePix(bacenPixCreateRequest)).thenReturn(bacenPixCreateResponse)

        val request = ChavePixRequest.newBuilder()
            .setIdCliente("0d1bb194-3c52-4e67-8c35-a93c0af9284f")
            .setTipoConta(br.com.zup.edu.proto.TipoConta.CONTA_CORRENTE)
            .setTipoChave(br.com.zup.edu.proto.TipoChavePix.RANDOM_KEY)
            .build()

        assertDoesNotThrow {
            val response = keyManagerClient.registraChave(request)
            assertTrue(response.pixId.isNotBlank())
        }
    }

    @Test
    internal fun `deve falhar ao tentar registrar chave aleatoria passando valor na requisicao`() {
        val (bacenPixCreateRequest, bacenPixCreateResponse) = geraBacenPixCreateRequestEResponse(
            tipoChavePix = TipoChavePix.RANDOM_KEY,
            valorChave = "2eb0cb29-25c3-4f1c-92db-babe4c788e28"
        )

        Mockito.`when`(bacenPixClientMock.registraChavePix(bacenPixCreateRequest)).thenReturn(bacenPixCreateResponse)

        val request = ChavePixRequest.newBuilder()
            .setIdCliente("0d1bb194-3c52-4e67-8c35-a93c0af9284f")
            .setTipoConta(br.com.zup.edu.proto.TipoConta.CONTA_CORRENTE)
            .setTipoChave(br.com.zup.edu.proto.TipoChavePix.RANDOM_KEY)
            .setValorChave("valor qualquer")
            .build()

        assertThrows(Status.INVALID_ARGUMENT.asRuntimeException()::class.java) {
            keyManagerClient.registraChave(request)
        }
    }

    @Test
    internal fun `deve falhar ao tentar registrar uma chave repetida`() {
        val (bacenPixCreateRequest, bacenPixCreateResponse) = geraBacenPixCreateRequestEResponse(
            tipoChavePix = TipoChavePix.CPF,
            valorChave = "06628726061"
        )

        Mockito.`when`(bacenPixClientMock.registraChavePix(bacenPixCreateRequest)).thenReturn(bacenPixCreateResponse)

        val chavePix = ChavePix(
            idCliente = "0d1bb194-3c52-4e67-8c35-a93c0af9284f",
            tipoConta = TipoConta.CONTA_CORRENTE,
            tipoChave = TipoChavePix.CPF,
            valorChave = "06628726061",
            conta = ChavePix.ContaAssociada(
                instituicao = "ITAÚ UNIBANCO S.A.",
                ispb = "60701190",
                nome = "Alberto Tavares",
                cpf = "06628726061",
                agencia = "0001",
                numeroDaConta = "212233"
            )
        )

        chavePixRepository.save(chavePix)

        val request = ChavePixRequest.newBuilder()
            .setIdCliente("0d1bb194-3c52-4e67-8c35-a93c0af9284f")
            .setTipoConta(br.com.zup.edu.proto.TipoConta.CONTA_CORRENTE)
            .setTipoChave(br.com.zup.edu.proto.TipoChavePix.CPF)
            .setValorChave("06628726061")
            .build()

        assertThrows(Status.ALREADY_EXISTS.asRuntimeException()::class.java) {
            keyManagerClient.registraChave(request)
        }
    }

    @Test
    internal fun `deve retornar status not found ao procurar por id inexistente`() {
        val (bacenPixCreateRequest, bacenPixCreateResponse) = geraBacenPixCreateRequestEResponse(
            tipoChavePix = TipoChavePix.RANDOM_KEY,
            valorChave = "2eb0cb29-25c3-4f1c-92db-babe4c788e28"
        )

        Mockito.`when`(bacenPixClientMock.registraChavePix(bacenPixCreateRequest)).thenReturn(bacenPixCreateResponse)

        val request = ChavePixRequest.newBuilder()
            .setIdCliente("0d1bb194-3c52-4e67-8c35-a93c0af92312")
            .setTipoConta(br.com.zup.edu.proto.TipoConta.CONTA_CORRENTE)
            .setTipoChave(br.com.zup.edu.proto.TipoChavePix.RANDOM_KEY)
            .build()

        assertThrows(Status.NOT_FOUND.asRuntimeException()::class.java) {
            keyManagerClient.registraChave(request)
        }
    }

    @Factory
    class GrpcClientFactory {

        @Singleton
        fun geraClienteGrpc(@GrpcChannel(GrpcServerChannel.NAME) grpcChannel: Channel): KeyManagerServiceGrpc.KeyManagerServiceBlockingStub {
            return KeyManagerServiceGrpc.newBlockingStub(grpcChannel)
        }

    }

    @MockBean(BacenPixClient::class)
    fun geraBacenPixClientMock(): BacenPixClient {
        return Mockito.mock(BacenPixClient::class.java)
    }

    fun geraBacenPixCreateRequestEResponse(
        valorChave: String,
        valorChaveReponse: String? = null,
        tipoChavePix: TipoChavePix
    ): BacenPixRegistraRequestEResponse {
        val bacenPixCreateRequest = BacenPixCreateRequest(
            keyType = tipoChavePix.toTipoChaveBacen(),
            key = valorChave,
            bankAccount = BankAccount(
                participant = "60701190",
                branch = "0001",
                accountNumber = "212233",
                accounType = TipoConta.CONTA_CORRENTE.paraBacenPixAccount()
            ),
            owner = Owner(
                type = Owner.OwnerType.NATURAL_PERSON,
                name = "Alberto Tavares",
                taxIdNumber = "06628726061"
            )
        )

        val bacenPixCreateResponse = HttpResponse.ok(
            BacenPixCreateResponse(
                keyType = tipoChavePix.toTipoChaveBacen(),
                key = valorChaveReponse ?: valorChave,
                bankAccount = BankAccount(
                    participant = "60701190",
                    branch = "0001",
                    accountNumber = "212233",
                    accounType = TipoConta.CONTA_CORRENTE.paraBacenPixAccount()
                ),
                owner = Owner(
                    type = Owner.OwnerType.NATURAL_PERSON,
                    name = "Alberto Tavares",
                    taxIdNumber = "06628726061"
                ),
                createdAt = LocalDateTime.now()
            )
        )

        return BacenPixRegistraRequestEResponse(bacenPixCreateRequest, bacenPixCreateResponse)
    }

    data class BacenPixRegistraRequestEResponse(
        val bacenPixCreateRequest: BacenPixCreateRequest,
        val bacenPixCreateResponse: HttpResponse<BacenPixCreateResponse>
    )

}