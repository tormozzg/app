package com.tormozzg.app.services

import com.tormozzg.app.configuration.AppUserPrincipal
import com.tormozzg.app.model.UsersRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AppUserDetailsService : UserDetailsService {
    @Autowired lateinit var usersRepository: UsersRepository

    override fun loadUserByUsername(email: String): UserDetails {
        val userOptional = usersRepository.findByEmail(email)
        if (!userOptional.isPresent)
            throw UsernameNotFoundException(email)
        return AppUserPrincipal(userOptional.get())
    }
}