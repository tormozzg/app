package com.tormozzg.app.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.JdbcUserDetailsManager
import javax.sql.DataSource

@Configuration
open class SecurityConfiguration(disableDefaults: Boolean = false) : WebSecurityConfigurerAdapter(disableDefaults) {
    override fun configure(http: HttpSecurity) {
        super.configure(http)
        http.csrf().disable()
    }

    @Bean open fun jdbcUserDetailsManager(@Autowired dataSource: DataSource): JdbcUserDetailsManager {
        return JdbcUserDetailsManager().apply {
            setDataSource(dataSource)
            usersByUsernameQuery = "select email as username, password as password, enabled as enabled from users where email = ?"
            setAuthoritiesByUsernameQuery("select u.email as username, r.name as authority from users_roles ur inner join users u on ur.user_id = u.id inner join roles r on ur.role_id = r.id where u.email = ?")
        }
    }

    @Bean open fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}