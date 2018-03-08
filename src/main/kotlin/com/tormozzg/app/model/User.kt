package com.tormozzg.app.model

import com.tormozzg.app.model.technical.CreateTimestamp
import com.tormozzg.app.model.technical.UpdateTimestamp
import java.sql.Timestamp
import javax.persistence.*
import javax.validation.constraints.Size

@Entity
@Table(name = "users", uniqueConstraints = [(UniqueConstraint(name = "uk_email_constraint", columnNames = ["email"]))])
data class User(@Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long = -1) : UpdateTimestamp {

    @Size(min = 3, max = 100)
    var email: String = ""

    var password: String = ""

    var enabled: Boolean = true

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = [
        JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [(JoinColumn(name = "role_id", referencedColumnName = "id"))])
    var roles: MutableSet<Role> = mutableSetOf()

    override var created: Timestamp = Timestamp(System.currentTimeMillis())
    override var updated: Timestamp = Timestamp(System.currentTimeMillis())
}

@Entity
@Table(name = "roles", uniqueConstraints = [(UniqueConstraint(name = "uk_role_name", columnNames = ["name"]))])
data class Role(@Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long = -1) : CreateTimestamp {

    @Size(min = 3, max = 100)
    var name: String = ""

    override var created: Timestamp = Timestamp(System.currentTimeMillis())
}