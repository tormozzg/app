package com.tormozzg.app.controllers

import com.fasterxml.jackson.databind.JsonNode
import com.tormozzg.app.model.RolesRepository
import com.tormozzg.app.model.User
import com.tormozzg.app.model.UsersRepository
import com.tormozzg.app.validation.ConstraintError
import com.tormozzg.app.validation.Unique
import com.tormozzg.app.validation.ValidationResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.validation.Validator
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@RestController
@RequestMapping(value = ["/users"])
@PreAuthorize("isAuthenticated() and hasAuthority('admin')")
class UserController {

    @Autowired lateinit var usersRepository: UsersRepository

    @Autowired lateinit var rolesRepository: RolesRepository
    @Autowired lateinit var validator: Validator

    @GetMapping
    fun list(@RequestParam(value = "size", defaultValue = "20") size: Int = 20,
             @RequestParam(value = "page", defaultValue = "0") page: Int = 0): PageResponse {
        val result = usersRepository.findAll(PageRequest.of(page, size, Sort(Sort.Direction.ASC, "created")))
        return PageResponse(page, size, result)
    }

    @GetMapping("/{id}")
    @PreAuthorize(" isAuthenticated() and (hasAuthority('admin') or #id == authentication.principal.id)")
    fun load(@PathVariable(value = "id") id: Long): ResponseEntity<User?> {
        val user = usersRepository.findById(id)
        return if (user.isPresent)
            ResponseEntity.ok(user.get())
        else
            ResponseEntity.notFound().build()
    }

    @PostMapping
    @Transactional
    fun createUser(@RequestBody createObject: UserCreateObject): ResponseEntity<Any> {
        val validationResult = validator.validate(createObject)
        if (validationResult.isNotEmpty()) {
            return ResponseEntity.badRequest().body(ValidationResponse.createValidationResponse(validationResult))
        }
        val user = usersRepository.save(User().apply {
            email = createObject.email!!
            password = createObject.password!!
            roles = rolesRepository.findAllById(createObject.roles!!).toMutableSet()
            enabled = createObject.enabled
        })
        return ResponseEntity.ok(user)
    }

    @PostMapping("/{id}/enabled")
    @Transactional
    fun changeUserActive(@PathVariable(value = "id") id: Long, @RequestBody map: Map<String, Any>): ResponseEntity<Any> {
        if (!usersRepository.existsById(id))
            return ResponseEntity.notFound().build()

        if (!map.containsKey("enabled") || map["enabled"] as? Boolean == null)
            return ResponseEntity.badRequest().body(ValidationResponse.Builder().addConstraintError(ConstraintError("enabled", "Missing required property \"enabled\"")).build())

        val user = usersRepository.findById(id).get()
        user.enabled = map["enabled"] as Boolean
        usersRepository.save(user)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable(value = "id") id: Long): ResponseEntity<Any> {
        usersRepository.deleteById(id)
        return ResponseEntity.noContent().build()
    }


}

data class UserCreateObject(
    @field:Size(min = 3, max = 100) @field:Email @field:NotNull
    @field:Unique(property = "email", entity = User::class)
    val email: String?,
    @field:Size(min = 5) @field:NotNull val password: String?,
    @field:Size(min = 1) @field:NotNull val roles: List<Long>?,
    val enabled: Boolean = true
)

