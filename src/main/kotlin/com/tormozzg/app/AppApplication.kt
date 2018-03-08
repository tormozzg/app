package com.tormozzg.app

import com.tormozzg.app.model.Role
import com.tormozzg.app.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import javax.persistence.EntityManager
import javax.transaction.Transactional

@SpringBootApplication
class AppApplication

fun main(args: Array<String>) {
    var ctx = runApplication<AppApplication>(*args)
    ctx.getBean("bootStrap",Bootstrap::class.java).init()
}


@Component("bootStrap")
open class Bootstrap {

    @Autowired lateinit var entityManager: EntityManager

    @Transactional
    open fun init() {

        var adminRole = entityManager.find(Role::class.java, 1L)
        if (adminRole == null) {
            adminRole = Role(id = 1).apply {
                name = "admin"
            }
            entityManager.merge(adminRole)
        }

        var adminUser = entityManager.find(User::class.java, 1L)
        if (adminUser == null) {
            adminUser = User(id = 1).apply {
                email = "admin@host.com"
                password = "admin"
                enabled = true
                roles = mutableSetOf(adminRole)
            }
            entityManager.merge(adminUser)
        }
    }

}