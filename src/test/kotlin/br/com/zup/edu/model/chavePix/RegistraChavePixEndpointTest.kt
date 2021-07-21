package br.com.zup.edu.model.chavePix

import br.com.zup.edu.external.bacenPix.*
import br.com.zup.edu.external.itauERP.ItauERPClient
import br.com.zup.edu.external.itauERP.ItauERPContaResponse
import br.com.zup.edu.proto.CriaChavePixRequest
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
internal class RegistraChavePixEndpointTest(
    @Inject val keyManagerClient: KeyManagerServiceGrpc.KeyManagerServiceBlockingStub,
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val bacenPixClientMock: BacenPixClient,
    @Inject val itauERPClientMock: ItauERPClient
) {

    @BeforeEach
    internal fun setUp() {
        chavePixRepository.deleteAll()
    }

    @Test
    internal fun `deve registrar uma chave de CPF com sucesso`() {
        // Configura mock para retornar resposta esperada do cliente http que se comunica com o sistema itau ERP

        Mockito.`when`(
            itauERPClientMock.obtemContaCliente(
                clienteId = "0d1bb194-3c52-4e67-8c35-a93c0af9284f",
                tipoConta = TipoConta.CONTA_CORRENTE.toString()
            )
        )
            .thenReturn(HttpResponse.ok(itauERPContaResponse))

        // Configura mock para retornar resposta esperada do cliente http que se comunica com o sistema do Banco Central

        val (bacenPixCreateRequest, bacenPixCreateResponse) = geraBacenPixCreateRequestEResponse(
            tipoChavePix = TipoChavePix.CPF,
            valorChave = "06628726061"
        )

        Mockito.`when`(bacenPixClientMock.registraChavePix(bacenPixCreateRequest)).thenReturn(bacenPixCreateResponse)

        val request = CriaChavePixRequest.newBuilder()
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
        // Configura mock para retornar resposta esperada do cliente http que se comunica com o sistema itau ERP

        Mockito.`when`(
            itauERPClientMock.obtemContaCliente(
                clienteId = "0d1bb194-3c52-4e67-8c35-a93c0af9284f",
                tipoConta = TipoConta.CONTA_CORRENTE.toString()
            )
        )
            .thenReturn(HttpResponse.ok(itauERPContaResponse))

        // Configura mock para retornar resposta esperada do cliente http que se comunica com o sistema do Banco Central

        val (bacenPixCreateRequest, bacenPixCreateResponse) = geraBacenPixCreateRequestEResponse(
            tipoChavePix = TipoChavePix.PHONE,
            valorChave = "+5534998981323"
        )

        Mockito.`when`(bacenPixClientMock.registraChavePix(bacenPixCreateRequest)).thenReturn(bacenPixCreateResponse)

        val request = CriaChavePixRequest.newBuilder()
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
        // Configura mock para retornar resposta esperada do cliente http que se comunica com o sistema itau ERP

        Mockito.`when`(
            itauERPClientMock.obtemContaCliente(
                clienteId = "0d1bb194-3c52-4e67-8c35-a93c0af9284f",
                tipoConta = TipoConta.CONTA_CORRENTE.toString()
            )
        )
            .thenReturn(HttpResponse.ok(itauERPContaResponse))

        // Configura mock para retornar resposta esperada do cliente http que se comunica com o sistema do Banco Central

        val (bacenPixCreateRequest, bacenPixCreateResponse) = geraBacenPixCreateRequestEResponse(
            tipoChavePix = TipoChavePix.EMAIL,
            valorChave = "email@email.com"
        )

        Mockito.`when`(bacenPixClientMock.registraChavePix(bacenPixCreateRequest)).thenReturn(bacenPixCreateResponse)

        val request = CriaChavePixRequest.newBuilder()
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
        // Configura mock para retornar resposta esperada do cliente http que se comunica com o sistema itau ERP

        Mockito.`when`(
            itauERPClientMock.obtemContaCliente(
                clienteId = "0d1bb194-3c52-4e67-8c35-a93c0af9284f",
                tipoConta = TipoConta.CONTA_CORRENTE.toString()
            )
        )
            .thenReturn(HttpResponse.ok(itauERPContaResponse))

        // Configura mock para retornar resposta esperada do cliente http que se comunica com o sistema do Banco Central

        val (bacenPixCreateRequest, bacenPixCreateResponse) = geraBacenPixCreateRequestEResponse(
            tipoChavePix = TipoChavePix.RANDOM_KEY,
            valorChave = "",
            valorChaveReponse = "2eb0cb29-25c3-4f1c-92db-babe4c788e28"
        )

        Mockito.`when`(bacenPixClientMock.registraChavePix(bacenPixCreateRequest)).thenReturn(bacenPixCreateResponse)

        val request = CriaChavePixRequest.newBuilder()
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
        // Configura mock para retornar resposta esperada do cliente http que se comunica com o sistema itau ERP

        Mockito.`when`(
            itauERPClientMock.obtemContaCliente(
                clienteId = "0d1bb194-3c52-4e67-8c35-a93c0af9284f",
                tipoConta = TipoConta.CONTA_CORRENTE.toString()
            )
        )
            .thenReturn(HttpResponse.ok(itauERPContaResponse))

        // Configura mock para retornar resposta esperada do cliente http que se comunica com o sistema do Banco Central

        val (bacenPixCreateRequest, bacenPixCreateResponse) = geraBacenPixCreateRequestEResponse(
            tipoChavePix = TipoChavePix.RANDOM_KEY,
            valorChave = "2eb0cb29-25c3-4f1c-92db-babe4c788e28"
        )

        Mockito.`when`(bacenPixClientMock.registraChavePix(bacenPixCreateRequest)).thenReturn(bacenPixCreateResponse)

        val request = CriaChavePixRequest.newBuilder()
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
        // Configura mock para retornar resposta esperada do cliente http que se comunica com o sistema itau ERP

        Mockito.`when`(
            itauERPClientMock.obtemContaCliente(
                clienteId = "0d1bb194-3c52-4e67-8c35-a93c0af9284f",
                tipoConta = TipoConta.CONTA_CORRENTE.toString()
            )
        )
            .thenReturn(HttpResponse.ok(itauERPContaResponse))

        // Configura mock para retornar resposta esperada do cliente http que se comunica com o sistema do Banco Central

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

        val request = CriaChavePixRequest.newBuilder()
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
        // Configura mock para retornar resposta esperada do cliente http que se comunica com o sistema itau ERP

        Mockito.`when`(
            itauERPClientMock.obtemContaCliente(
                clienteId = "0d1bb194-3c52-4e67-8c35-a93c0af92312",
                tipoConta = TipoConta.CONTA_CORRENTE.toString()
            )
        )
            .thenReturn(HttpResponse.notFound())

        // Configura mock para retornar resposta esperada do cliente http que se comunica com o sistema do Banco Central

        val (bacenPixCreateRequest, bacenPixCreateResponse) = geraBacenPixCreateRequestEResponse(
            tipoChavePix = TipoChavePix.RANDOM_KEY,
            valorChave = "2eb0cb29-25c3-4f1c-92db-babe4c788e28"
        )

        Mockito.`when`(bacenPixClientMock.registraChavePix(bacenPixCreateRequest)).thenReturn(bacenPixCreateResponse)

        val request = CriaChavePixRequest.newBuilder()
            .setIdCliente("0d1bb194-3c52-4e67-8c35-a93c0af92312")
            .setTipoConta(br.com.zup.edu.proto.TipoConta.CONTA_CORRENTE)
            .setTipoChave(br.com.zup.edu.proto.TipoChavePix.RANDOM_KEY)
            .build()

        assertThrows(Status.NOT_FOUND.asRuntimeException()::class.java) {
            keyManagerClient.registraChave(request)
        }
    }

    // ---------------------------------- Secção de Setup para testes ----------------------------------------

    /**
     * Cria um cliente grpc para se comunicar com o servidor grpc levantado pela nossa aplicação
     */
    @Factory
    class GrpcClientFactory {
        @Singleton
        fun geraClienteGrpc(@GrpcChannel(GrpcServerChannel.NAME) grpcChannel: Channel): KeyManagerServiceGrpc.KeyManagerServiceBlockingStub {
            return KeyManagerServiceGrpc.newBlockingStub(grpcChannel)
        }

    }

    /**
     * Cria mock do cliente http que se comunica com o sistema do itauERP
     */
    @MockBean(ItauERPClient::class)
    fun geraItauERPClientMock(): ItauERPClient {
        return Mockito.mock(ItauERPClient::class.java)
    }

    /**
     * Cria mock do cliente http que se comunica com o sistema do Banco Central
     */
    @MockBean(BacenPixClient::class)
    fun geraBacenPixClientMock(): BacenPixClient {
        return Mockito.mock(BacenPixClient::class.java)
    }

    /**
     * Esta variavel representa a resposta que o mock do cliente http que se comunica com o sistema do itauERP deve retornar
     */
    private val itauERPContaResponse = ItauERPContaResponse(
        tipo = TipoConta.CONTA_CORRENTE,
        instituicao = ItauERPContaResponse.Instituicao(
            ispb = "60701190",
            nome = "ITAÚ UNIBANCO S.A."
        ),
        agencia = "0001",
        numero = "212233",
        titular = ItauERPContaResponse.Titular(
            id = "0d1bb194-3c52-4e67-8c35-a93c0af9284f",
            nome = "Alberto Tavares",
            cpf = "06628726061"
        )
    )

    /**
     * Essa função gera uma request e uma response do Banco Central a partir dos valores de chave pix e tipo de chave
     * pix recebidos. Os valores de bankAccount e owner são os mesmos que o itauERP deve retornar, por isso são tirados
     * diretamente dessa variavel para não haver inconsistencias.
     *
     * O retorno é um Pair porque assim podemos "desestruturar" esses dois valores no recebimento, algo como:
     *      val (retorno1, retorno2) = funcaoQueRetornaUmPair()
     *
     * obs: Um dos retornos ja é um HttpResponse
     */
    fun geraBacenPixCreateRequestEResponse(
        valorChave: String,
        valorChaveReponse: String? = null,
        tipoChavePix: TipoChavePix
    ): Pair<BacenPixCreateRequest, HttpResponse<BacenPixCreateResponse>> {
        val bacenPixCreateRequest = BacenPixCreateRequest(
            keyType = tipoChavePix.toTipoChaveBacen(),
            key = valorChave,
            bankAccount = BankAccount(
                participant = itauERPContaResponse.instituicao.ispb,
                branch = itauERPContaResponse.agencia,
                accountNumber = itauERPContaResponse.numero,
                accounType = TipoConta.CONTA_CORRENTE.paraBacenPixAccount()
            ),
            owner = Owner(
                type = Owner.OwnerType.NATURAL_PERSON,
                name = itauERPContaResponse.titular.nome,
                taxIdNumber = itauERPContaResponse.titular.cpf
            )
        )

        val bacenPixCreateResponse = HttpResponse.ok(
            BacenPixCreateResponse(
                keyType = tipoChavePix.toTipoChaveBacen(),
                key = valorChaveReponse ?: valorChave,
                bankAccount = BankAccount(
                    participant = itauERPContaResponse.instituicao.ispb,
                    branch = itauERPContaResponse.agencia,
                    accountNumber = itauERPContaResponse.numero,
                    accounType = TipoConta.CONTA_CORRENTE.paraBacenPixAccount()
                ),
                owner = Owner(
                    type = Owner.OwnerType.NATURAL_PERSON,
                    name = itauERPContaResponse.titular.nome,
                    taxIdNumber = itauERPContaResponse.titular.cpf
                ),
                createdAt = LocalDateTime.now()
            )
        )

        return Pair(bacenPixCreateRequest, bacenPixCreateResponse)
    }

}