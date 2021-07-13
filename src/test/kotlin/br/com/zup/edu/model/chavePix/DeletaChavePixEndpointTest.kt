package br.com.zup.edu.model.chavePix

import br.com.zup.edu.external.bacenPix.BacenPixClient
import br.com.zup.edu.external.bacenPix.BacenPixDeleteRequest
import br.com.zup.edu.external.bacenPix.BacenPixDeleteResponse
import br.com.zup.edu.proto.DeletaChavePixRequest
import br.com.zup.edu.proto.KeyManagerServiceGrpc
import io.grpc.Channel
import io.grpc.Status
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class DeletaChavePixEndpointTest(
    @Inject val keyManagerClient: KeyManagerServiceGrpc.KeyManagerServiceBlockingStub,
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val bacenPixClientMock: BacenPixClient
) {

    @BeforeEach
    internal fun setUp() {
        chavePixRepository.deleteAll()
    }

    @Test
    internal fun `deve deletar chave pix com sucesso`() {

        val (bacenPixDeleteRequest, bacenPixDeleteResponse) = geraBacenPixDeleteRequestEResponse(
            valorChave = "06628726061"
        )

        Mockito.`when`(bacenPixClientMock.deletaChavePix("06628726061", bacenPixDeleteRequest))
            .thenReturn(bacenPixDeleteResponse)

        var chavePix = ChavePix(
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

        chavePix = chavePixRepository.save(chavePix)

        val request = DeletaChavePixRequest.newBuilder()
            .setPixId(chavePix.id.toString())
            .build()

        assertDoesNotThrow {
            keyManagerClient.deletaChave(request)
        }


    }

    @Test
    internal fun `deve falhar ao tentar deletar chave pix que não existe`() {
        val request = DeletaChavePixRequest.newBuilder()
            .setPixId("0d1bb194-3c52-4e67-8c35-a93c0af9284f")
            .build()

        assertThrows(Status.NOT_FOUND.asRuntimeException()::class.java) {
            keyManagerClient.deletaChave(request)
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

    fun geraBacenPixDeleteRequestEResponse(
        valorChave: String,
        valorChaveReponse: String? = null,
    ): BacenPixRegistraRequestEResponse {
        val bacenPixDeleteRequestRequest = BacenPixDeleteRequest(
            key = valorChave,
            participant = "60701190",
        )

        val bacenPixDeleteResponse = HttpResponse.ok(
            BacenPixDeleteResponse(
                key = valorChaveReponse ?: valorChave,
                participant = "60701190",
                deletedAt = LocalDateTime.now()
            )
        )

        return BacenPixRegistraRequestEResponse(bacenPixDeleteRequestRequest, bacenPixDeleteResponse)
    }

    data class BacenPixRegistraRequestEResponse(
        val bacenPixDeleteRequestRequest: BacenPixDeleteRequest,
        val bacenPixDeleteResponse: HttpResponse<BacenPixDeleteResponse>
    )
}