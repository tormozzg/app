package com.tormozzg.app

import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.github.fge.jsonschema.main.JsonValidator
import java.io.InputStreamReader

object SchemaValidator {

    private val validator: JsonValidator = JsonSchemaFactory.byDefault().validator

    private fun loadJsonSchema(path: String): JsonNode {
        return this::class.java.classLoader.getResourceAsStream(path).use {
            JsonLoader.fromReader(InputStreamReader(it))
        }
    }

    fun validateJsonSchema(body: String, schemaPath: String) {
        val node = loadJsonSchema(schemaPath)
        val report = validator.validate(node, JsonLoader.fromString(body))
        if (!report.isSuccess) {
            throw AssertionError(report.toString())
        }
    }
}