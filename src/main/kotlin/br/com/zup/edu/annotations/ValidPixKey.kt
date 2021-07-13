package br.com.zup.edu.annotations

import br.com.zup.edu.model.chavePix.ChavePixCreateDTO
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import javax.inject.Singleton
import javax.validation.Constraint

@Constraint(validatedBy = [ValidPixKeyValidator::class])
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ValidPixKey(
    val message: String = "Formato de chave pix inválido"
)

@Singleton
class ValidPixKeyValidator(): ConstraintValidator<ValidPixKey, ChavePixCreateDTO> {

    override fun isValid(
        value: ChavePixCreateDTO,
        annotationMetadata: AnnotationValue<ValidPixKey>,
        context: io.micronaut.validation.validator.constraints.ConstraintValidatorContext
    ): Boolean {
        return value.tipoChave.valida(value.valorChave)
    }
}