package com.tormozzg.app.controllers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.tormozzg.app.model.Role
import com.tormozzg.app.model.RolesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/roles"])
@PreAuthorize("hasAuthority('admin')")
class RoleController {
    @Autowired lateinit var rolesRepository: RolesRepository

    @GetMapping
    fun list(@RequestParam(value = "size", defaultValue = "20") size: Int = 20,
             @RequestParam(value = "page", defaultValue = "0") page: Int = 0): ObjectNode {
        val result = rolesRepository.findAll(PageRequest.of(page, size, Sort(Sort.Direction.ASC, "created")))
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

    @PostMapping
    @Transactional
    fun create(@RequestBody createObject: CreateRoleObject): ResponseEntity<Role> {
        val role = rolesRepository.save(Role().apply {
            name = createObject.name
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
    val name: String
)