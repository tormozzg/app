package com.tormozzg.app.validation

import javax.validation.ConstraintViolation

class ValidationResponse private constructor() {

    var errorCount: Int = 0
        private set
    var errors: Set<ConstraintError> = emptySet()
        private set


    companion object {
        fun createValidationResponse(constraints: Set<ConstraintViolation<*>>): ValidationResponse {
            return ValidationResponse().apply {
                errorCount = constraints.size
                errors = constraints.map { ConstraintError(it) }.toSet()
            }
        }
    }

    class Builder {
        val errors: MutableSet<ConstraintError> = mutableSetOf()

        fun addConstraintError(error: ConstraintError): Builder {
            errors.add(error)
            return this
        }

        fun build(): ValidationResponse {
            return ValidationResponse().apply {
                errorCount = errors.size
                errors = errors.toSet()
            }
        }
    }
}

class ConstraintError {
    val property: String
    val value: Any?
    val message: String

    constructor(constraints: ConstraintViolation<*>) {
        property = constraints.propertyPath.toString()
        message = constraints.message
        value = constraints.invalidValue
    }

    constructor(property: String, message: String, value: Any? = null) {
        this.property = property
        this.message = message
        this.value = value
    }
}