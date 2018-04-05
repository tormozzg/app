package com.tormozzg.app.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.tormozzg.app.ContextHolder
import com.tormozzg.app.model.technical.CreateTimestamp
import com.tormozzg.app.model.technical.UpdateTimestamp
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Size

@Entity
@Table(name = "users", uniqueConstraints = [(UniqueConstraint(name = "uk_email_constraint", columnNames = ["email"]))])
@EntityListeners(UsersListener::class)
data class User(@Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long = -1) : UpdateTimestamp {

    @Size(min = 3, max = 100)
    var email: String = ""

    @JsonIgnore
    var password: String = ""

    var enabled: Boolean = true

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [(JoinColumn(name = "role_id", referencedColumnName = "id"))])
    var roles: MutableSet<Role> = mutableSetOf()

    override var created: Timestamp = Timestamp(System.currentTimeMillis())
    override var updated: Timestamp = Timestamp(System.currentTimeMillis())

    @JsonIgnore
    @Transient
    var prevPassword: String = ""

    @PostLoad
    fun onLoad() {
        prevPassword = password
    }
}

open class UsersListener {

    @PrePersist fun onCreate(user: User) {
        val passwordEncoder: PasswordEncoder = ContextHolder.getApplicationContext().getBean("passwordEncoder", PasswordEncoder::class.java)
        user.password = passwordEncoder.encode(user.password)
    }

    @PreUpdate fun onUpdate(user: User) {
        val passwordEncoder: PasswordEncoder = ContextHolder.getApplicationContext().getBean("passwordEncoder", PasswordEncoder::class.java)
        if (user.password != user.prevPassword) {
            user.password = passwordEncoder.encode(user.password)
        }
    }
}

@Entity
@Table(name = "roles", uniqueConstraints = [(UniqueConstraint(name = "uk_role_name", columnNames = ["name"]))])
data class Role(@Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long = -1) : CreateTimestamp {

    @Size(min = 3, max = 100)
    var name: String = ""

    override var created: Timestamp = Timestamp(System.currentTimeMillis())
}

@Repository interface UsersRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): Optional<User>
}

@Repository interface RolesRepository : JpaRepository<Role, Long>