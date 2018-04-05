package com.tormozzg.app.controllers

import com.tormozzg.app.model.Role
import com.tormozzg.app.model.RolesRepository
import com.tormozzg.app.validation.Unique
import com.tormozzg.app.validation.ValidationResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import javax.validation.Validator
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@RestController
@RequestMapping(value = ["/roles"])
@PreAuthorize("isAuthenticated() and hasAuthority('admin')")
class RoleController {
    @Autowired lateinit var rolesRepository: RolesRepository
    @Autowired lateinit var validator: Validator

    @GetMapping
    fun list(@RequestParam(value = "size", defaultValue = "20") size: Int = 20,
             @RequestParam(value = "page", defaultValue = "0") page: Int = 0): PageResponse {
        val result = rolesRepository.findAll(PageRequest.of(page, size, Sort(Sort.Direction.ASC, "created")))
        return PageResponse(page, size, result)
    }

    @PostMapping
    @Transactional
    fun create(@RequestBody createObject: CreateRoleObject): ResponseEntity<Any> {
        val constraints = validator.validate(createObject)
        if (constraints.isNotEmpty()) {
            return ResponseEntity.badRequest().body(ValidationResponse.createValidationResponse(constraints))
        }
        val role = rolesRepository.save(Role().apply {
            name = createObject.name!!
        })
        return ResponseEntity.ok(role)
    }

    @DeleteMapping(value = ["/{id}"])
    @Transactional
    fun delete(@PathVariable(value = "id") id: Long): ResponseEntity<Any> {
        if (!rolesRepository.existsById(id))
            return ResponseEntity.notFound().build()
        // todo: check using or reassign
        rolesRepository.deleteById(id)
        return ResponseEntity.noContent().build()
    }
}

data class CreateRoleObject(
    @field:Size(min = 3, max = 100)
    @field:NotNull
    @field:Unique(property = "name", entity = Role::class)
    val name: String?
)