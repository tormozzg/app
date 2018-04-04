package com.tormozzg.app.validation

import org.springframework.beans.factory.annotation.Autowired
import javax.persistence.EntityManager
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [UniqueValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.VALUE_PARAMETER)
@Retention
annotation class Unique(
    val property: String,
    val entity: KClass<*>,
    val message: String = "Must be unique",
    val groups: Array<KClass<Any>> = emptyArray(),
    val payload: Array<KClass<out Payload>> = emptyArray()
)

class UniqueValidator : ConstraintValidator<Unique, String> {
    lateinit var entity: KClass<*>
    lateinit var property: String

    @Autowired lateinit var em: EntityManager


    override fun initialize(annotation: Unique) {
        super.initialize(annotation)
        entity = annotation.entity
        property = annotation.property
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        return em.criteriaBuilder.let { cb ->
            cb.createQuery(Long::class.java).let { qb ->
                val entity = qb.from(entity.java)
                val property = entity.get<String>(property)
                qb.select(cb.count(property))
                qb.where(cb.equal(property, value))
                val query = em.createQuery(qb)
                query.singleResult == 0L
            }
        }
    }
}