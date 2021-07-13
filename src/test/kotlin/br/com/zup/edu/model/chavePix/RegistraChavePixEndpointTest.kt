package br.com.zup.edu.model.chavePix

import br.com.zup.edu.proto.ChavePixRequest
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
internal class RegistraChavePixEndpointTest(@Inject val keyManagerClient: KeyManagerServiceGrpc.KeyManagerServiceBlockingStub) {

    @Inject
    lateinit var chavePixRepository: ChavePixRepository

    @BeforeEach
    internal fun setUp() {
        chavePixRepository.deleteAll()
    }

    @Test
    internal fun `deve registrar uma chave de CPF com sucesso`() {

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

        val chavePix = ChavePix(
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

}