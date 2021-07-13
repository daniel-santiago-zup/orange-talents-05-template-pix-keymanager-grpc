package br.com.zup.edu.annotations

import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint

@Constraint(validatedBy = [UUIDValidator::class])
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ValidUUID(
    val message: String = "UUID deve ser válido"
)

@Singleton
class UUIDValidator(): ConstraintValidator<ValidUUID, String> {

    override fun isValid(
        value: String?,
        annotationMetadata: AnnotationValue<ValidUUID>?,
        context: ConstraintValidatorContext?
    ): Boolean {
        return value?.matches("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}".toRegex()) ?: false
    }

}
