package com.tormozzg.app.configuration

import com.tormozzg.app.model.User
import com.tormozzg.app.services.AppUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) class SecurityConfiguration(disableDefaults: Boolean = false) : WebSecurityConfigurerAdapter(disableDefaults) {

    @Autowired lateinit var appUserDetailsService: AppUserDetailsService

    override fun configure(http: HttpSecurity) {
        super.configure(http)
        http.csrf().disable()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        super.configure(auth)
        auth.authenticationProvider(authenticationProvider())
    }

    @Bean fun authenticationProvider(): DaoAuthenticationProvider {
        return DaoAuthenticationProvider().apply {
            setUserDetailsService(appUserDetailsService)
            setPasswordEncoder(passwordEncoder())
        }
    }

    @Bean fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}

data class AppUserPrincipal(private val user: User) : UserDetails {

    fun getId(): Long {
        return user.id
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return user.roles.map { role -> SimpleGrantedAuthority(role.name) }.toMutableList()
    }

    override fun isEnabled(): Boolean {
        return user.enabled
    }

    override fun getUsername(): String {
        return user.email
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun getPassword(): String {
        return user.password
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

}