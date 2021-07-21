package br.com.zup.edu.model.chavePix

import br.com.zup.edu.external.bacenPix.*
import br.com.zup.edu.external.itauERP.ItauERPClient
import br.com.zup.edu.external.itauERP.ItauERPContaResponse
import br.com.zup.edu.proto.DetalhaChaveInternalRequest
import br.com.zup.edu.proto.DetalhaChavePixExternalRequest
import br.com.zup.edu.proto.KeyManagerServiceGrpc
import br.com.zup.edu.proto.ListaChavesRequest
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
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@MicronautTest(transactional = false)
internal class ListaChavesPixEndpointTest(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val keyManagerClient: KeyManagerServiceGrpc.KeyManagerServiceBlockingStub
) {

    @BeforeEach
    internal fun setUp() {
        chavePixRepository.deleteAll()
        chavePixRepository.saveAll(
            listOf(
                geraChavePixComRandomKey(),
                geraChavePixComRandomKey(),
                geraChavePixComRandomKey()
            )
        )
    }

    @Test
    internal fun `deve obter lista de chaves pix com sucesso`() {

        val request = ListaChavesRequest.newBuilder()
            .setIdCliente("0d1bb194-3c52-4e67-8c35-a93c0af9284f")
            .build()

        assertDoesNotThrow {
            val response = keyManagerClient.listaChaves(request)

            assertEquals(3, response.chavesList.size)
            assertEquals(TipoChavePix.RANDOM_KEY.toTipoChavePixProtobuff(), response.getChaves(0).tipoChave)
        }
    }

    @Test
    internal fun `deve falhar ao buscar chaves de um cliente que nao possui nenhuma`() {
        val request = ListaChavesRequest.newBuilder()
            .setIdCliente("14590746-a9d1-4caf-a0e3-99c5bd13fc36")
            .build()

        val error = assertThrows(Status.NOT_FOUND.asRuntimeException()::class.java) {
            keyManagerClient.listaChaves(request)
        }

        assertEquals("Não foi possível encontrar chaves para esse cliente", error.status.description)
    }

    // ---------------------------------- Secção de Setup para testes ----------------------------------------

    /**
     * Essa função gera uma chave pix do tipo aleatória com um valor aleatório a cada chamada
     */
    fun geraChavePixComRandomKey(): ChavePix {
        return ChavePix(
            idCliente = "0d1bb194-3c52-4e67-8c35-a93c0af9284f",
            tipoConta = TipoConta.CONTA_CORRENTE,
            tipoChave = TipoChavePix.RANDOM_KEY,
            valorChave = UUID.randomUUID().toString(),
            conta = ChavePix.ContaAssociada(
                instituicao = "ITAÚ UNIBANCO S.A.",
                ispb = "60701190",
                nome = "Alberto Tavares",
                cpf = "06628726061",
                agencia = "0001",
                numeroDaConta = "212233"
            )
        )
    }
}