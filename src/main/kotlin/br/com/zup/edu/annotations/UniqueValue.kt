package br.com.zup.edu.annotations

import br.com.zup.edu.exceptions.NotUniqueValueException
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Inject
import javax.inject.Singleton
import javax.persistence.EntityManager
import javax.transaction.Transactional
import javax.validation.Constraint
import kotlin.reflect.KClass

@Constraint(validatedBy = [UniqueValueValidator::class])
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class UniqueValue(
    val message: String = "Valor infomado deve ser único",
    val entidade: KClass<*>,
    val nomeCampo: String
)

@Singleton
open class UniqueValueValidator(@Inject val entityManager: EntityManager): ConstraintValidator<UniqueValue, Any?> {

    @Transactional
    override fun isValid(
        value: Any?,
        annotationMetadata: AnnotationValue<UniqueValue>?,
        context: ConstraintValidatorContext?
    ): Boolean {
        val nomeDaEntidadeJPA = annotationMetadata?.values?.get("entidade")
        val nomeCampo = annotationMetadata?.getRequiredValue("nomeCampo", String::class.java)
        val resultados: List<*> = entityManager.createQuery("select c from $nomeDaEntidadeJPA c where c.$nomeCampo= :pCampo")
            .setParameter("pCampo",value)
            .resultList

        if (resultados.isNotEmpty()) throw NotUniqueValueException()

        return true
    }

}