package br.com.zup.edu.annotations

import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.aop.Around
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Around
annotation class ErrorAroundHandler()

@Singleton
@InterceptorBean(ErrorAroundHandler::class)
class ErrorAroundHandlerInterceptor() : MethodInterceptor<Any, Any> {

    override fun intercept(context: MethodInvocationContext<Any, Any>): Any? {

        try {
            return context.proceed()
        } catch (e: Exception) {
            val responseObserver = context.parameterValues[1] as StreamObserver<*>

            val status: RuntimeException = when (e) {
                is ConstraintViolationException -> Status.INVALID_ARGUMENT
                    .withDescription(e.message)
                    .asRuntimeException()

                is IllegalArgumentException -> Status.INVALID_ARGUMENT
                    .withDescription(e.message)
                    .asRuntimeException()

                is NotFoundException -> Status.NOT_FOUND
                    .withDescription(e.message)
                    .asRuntimeException()

                is NotUniqueValueException -> Status.ALREADY_EXISTS
                    .withDescription(e.message)
                    .asRuntimeException()

                is ForbiddenException -> Status.PERMISSION_DENIED
                    .withDescription(e.message)
                    .asRuntimeException()

                else -> {
                    println(e)
                    Status.UNKNOWN
                        .withDescription("Erro inesperado")
                        .asRuntimeException()
                }
            }

            responseObserver.onError(status)

            return null
        }

    }
}