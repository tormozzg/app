package com.tormozzg.app.controllers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.tormozzg.app.model.RolesRepository
import com.tormozzg.app.model.User
import com.tormozzg.app.model.UsersRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/users"])
@PreAuthorize("hasAuthority('admin')")
class UserController {

    @Autowired lateinit var usersRepository: UsersRepository

    @Autowired lateinit var rolesRepository: RolesRepository

    @GetMapping
    fun list(@RequestParam(value = "size", defaultValue = "20") size: Int = 20,
             @RequestParam(value = "page", defaultValue = "0") page: Int = 0): ObjectNode {
        val result = usersRepository.findAll(PageRequest.of(page, size, Sort(Sort.Direction.ASC, "created")))
        return ObjectMapper().let { om ->
            om.createObjectNode().apply {
                put("totalPages", result.totalPages)
                put("page", page)
                put("pageSize", size)
                put("count", result.numberOfElements)
                set("items", om.convertValue(result.toList(), JsonNode::class.java))
            }
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @PostAuthorize("hasAuthority('admin') or returnObject.body?.email == authentication.principal.username")
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
        // todo: validate create object
        val user = usersRepository.save(User().apply {
            email = createObject.email
            password = createObject.password
            roles = rolesRepository.findAllById(createObject.roles).toMutableSet()
            enabled = createObject.enabled
        })
        return ResponseEntity.ok(user)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable(value = "id") id: Long): ResponseEntity<Any> {
        usersRepository.deleteById(id)
        return ResponseEntity.noContent().build()
    }


}

data class UserCreateObject(
    val email: String,
    val password: String,
    val roles: List<Long>,
    val enabled: Boolean = true
)

