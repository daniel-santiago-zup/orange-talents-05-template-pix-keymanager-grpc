package br.com.zup.edu.model.chavePix

import br.com.zup.edu.proto.DeletaChavePixRequest
import br.com.zup.edu.proto.KeyManagerServiceGrpc
import io.grpc.Channel
import io.grpc.Status
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class DeletaChavePixEndpointTest(
    @Inject val keyManagerClient: KeyManagerServiceGrpc.KeyManagerServiceBlockingStub,
    @Inject val chavePixRepository: ChavePixRepository
) {

    @BeforeEach
    internal fun setUp() {
        chavePixRepository.deleteAll()
    }

    @Test
    internal fun `deve deletar chave pix com sucesso`() {

        var chavePix = ChavePix(
            idCliente = "0d1bb194-3c52-4e67-8c35-a93c0af9284f",
            tipoConta = br.com.zup.edu.model.chavePix.TipoConta.CONTA_CORRENTE,
            tipoChave = br.com.zup.edu.model.chavePix.TipoChavePix.CPF,
            valorChave = "06628726061",
            conta = ChavePix.ContaAssociada(
                instituicao = "ITAÚ UNIBANCO S.A.",
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

        assertThrows (Status.NOT_FOUND.asRuntimeException()::class.java) {
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
}