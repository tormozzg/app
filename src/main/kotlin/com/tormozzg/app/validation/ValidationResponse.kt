package com.tormozzg.app.validation

import javax.validation.ConstraintViolation

class ValidationResponse {
    val errorCount: Int
    val errors: Collection<ConstraintError>

    private constructor (constraints: Set<ConstraintViolation<*>>) {
        errorCount = constraints.size
        errors = constraints.map { ConstraintError(it) }
    }

    companion object {
        fun createValidationResponse(constraints: Set<ConstraintViolation<*>>): ValidationResponse {
            return ValidationResponse(constraints)
        }
    }
}

class ConstraintError(constraint: ConstraintViolation<*>) {
    val property: String = constraint.propertyPath.toString()
    val value: Any? = constraint.invalidValue
    val message: String = constraint.message
}