package com.tormozzg.app

import com.tormozzg.app.model.Role
import com.tormozzg.app.model.User
import com.tormozzg.app.model.RolesRepository
import com.tormozzg.app.model.UsersRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@EnableJpaRepositories("com.tormozzg.app.model")
@SpringBootApplication
open class AppApplication

fun main(args: Array<String>) {
    var ctx = runApplication<AppApplication>(*args)
    ctx.getBean("bootStrap", Bootstrap::class.java).init()
}


@Component("bootStrap")
open class Bootstrap {

    @Autowired lateinit var usersRepository: UsersRepository
    @Autowired lateinit var rolesRepository: RolesRepository

    @Transactional
    open fun init() {

        var adminRole = rolesRepository.findById(1).let { if (it.isPresent) it.get() else null }
        if (adminRole == null) {
            adminRole = Role(id = 1).apply {
                name = "admin"
            }
            rolesRepository.save(adminRole)
        }

        var adminUser = usersRepository.findById(1).let { if (it.isPresent) it.get() else null }
        if (adminUser == null) {
            adminUser = User(id = 1).apply {
                email = "admin@host.com"
                password = "admin"
                enabled = true
                roles = mutableSetOf(adminRole)
            }
            usersRepository.save(adminUser)
        }
    }
}

@Service open class ContextHolder : ApplicationContextAware {
    companion object {
        private lateinit var context: ApplicationContext
        fun getApplicationContext(): ApplicationContext {
            return context
        }
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }
}