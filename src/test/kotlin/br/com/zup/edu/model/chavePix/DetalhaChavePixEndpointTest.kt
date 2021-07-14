package br.com.zup.edu.model.chavePix

import br.com.zup.edu.external.bacenPix.*
import br.com.zup.edu.proto.DetalhaChaveInternalRequest
import br.com.zup.edu.proto.DetalhaChavePixExternalRequest
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
internal class DetalhaChavePixEndpointTest(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val bacenPixClient: BacenPixClient,
    @Inject val keyManagerClient: KeyManagerServiceGrpc.KeyManagerServiceBlockingStub
) {

    @BeforeEach
    internal fun setUp() {
        chavePixRepository.deleteAll()
    }

    @Test
    internal fun `deve obter os detalhes de uma chave pix pelo seu valor`() {

        Mockito.`when`(bacenPixClient.detalhaChavePix("06628726061")).thenReturn(
            geraBacenPixDetailResponse(TipoChaveBacen.CPF, "06628726061")
        )

        val request = DetalhaChaveInternalRequest.newBuilder()
            .setChavePix("06628726061")
            .build()

        val response = keyManagerClient.detalhaChaveInternal(request)

        assertEquals("06628726061", response.valorChave)
        assertEquals("Alberto Tavares", response.titular.nome)

    }

    @Test
    internal fun `deve falhar ao buscar detalhes de chave inexistente pelo seu valor`() {
        Mockito.`when`(bacenPixClient.detalhaChavePix("06628726061")).thenReturn(
            HttpResponse.notFound()
        )

        val request = DetalhaChaveInternalRequest.newBuilder()
            .setChavePix("06628726061")
            .build()

        val error = assertThrows(Status.NOT_FOUND.asRuntimeException()::class.java) {
            keyManagerClient.detalhaChaveInternal(request)
        }

        assertEquals("Chave pix não encontrada no Banco Central", error.status.description)
    }

    @Test
    internal fun `deve obter os detalhes de uma chave pix pelo id do cliente e pix id`() {

        Mockito.`when`(bacenPixClient.detalhaChavePix("06628726061")).thenReturn(
            geraBacenPixDetailResponse(TipoChaveBacen.CPF, "06628726061")
        )

        val chavePix = chavePixRepository.save(
            ChavePix(
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
        )

        val request = DetalhaChavePixExternalRequest.newBuilder()
            .setIdCliente("0d1bb194-3c52-4e67-8c35-a93c0af9284f")
            .setPixId(chavePix.id.toString())
            .build()

        val response = keyManagerClient.detalhaChaveExternal(request)

        assertEquals("0d1bb194-3c52-4e67-8c35-a93c0af9284f", response.idCliente)
        assertEquals(chavePix.id.toString(), response.pixId)
        assertEquals("06628726061", response.valorChave)
    }

    @Test
    internal fun `deve falhar ao tentar obter uma chave que nao pertence ao cliente`() {

        val chavePix = chavePixRepository.save(
            ChavePix(
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
        )

        val erro = assertThrows(Status.PERMISSION_DENIED.asRuntimeException()::class.java) {
            keyManagerClient.detalhaChaveExternal(
                DetalhaChavePixExternalRequest.newBuilder()
                    .setIdCliente("8d00b5e6-66a1-4126-8ac8-9d10ac177517")
                    .setPixId(chavePix.id.toString())
                    .build()
            )
        }

        assertEquals("O pixId fornecido não pertence ao cliente", erro.status.description)

    }

    @Test
    internal fun `deve falhar ao tentar obter uma chave que nao consta no Banco Centra pelo id do cliente e pix id`() {
        Mockito.`when`(bacenPixClient.detalhaChavePix("06628726061")).thenReturn(
            HttpResponse.notFound()
        )

        val chavePix = chavePixRepository.save(
            ChavePix(
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
        )

        val erro = assertThrows(Status.NOT_FOUND.asRuntimeException()::class.java) {
            keyManagerClient.detalhaChaveExternal(
                DetalhaChavePixExternalRequest.newBuilder()
                    .setIdCliente("0d1bb194-3c52-4e67-8c35-a93c0af9284f")
                    .setPixId(chavePix.id.toString())
                    .build()
            )
        }

        assertEquals("Chave pix fornecida não encontrada no Banco Central", erro.status.description)
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

    fun geraBacenPixDetailResponse(
        tipoChaveBacen: TipoChaveBacen,
        valorChave: String
    ): HttpResponse<BacenPixDetailResponse> {
        return HttpResponse.ok(
            BacenPixDetailResponse(
                keyType = tipoChaveBacen,
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
                ),
                createdAt = LocalDateTime.now()
            )
        )
    }
}