package com.jobsalrt.worker.service

import com.jobsalrt.worker.controller.view.AuthenticationRequest
import com.jobsalrt.worker.domain.Admin
import com.jobsalrt.worker.repository.AdminRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AdminService(
    @Autowired private val adminRepository: AdminRepository,
) : UserDetailsService {
    private val bCryptPasswordEncoder = BCryptPasswordEncoder()
    fun login(request: AuthenticationRequest): Mono<Admin> {
        return adminRepository.findByEmail(request.email)
            .flatMap {
                if (bCryptPasswordEncoder.matches(request.password, it.password))
                    Mono.just(it)
                else Mono.empty()
            }
    }

    override fun loadUserByUsername(email: String): UserDetails {
        val admin = adminRepository.findByEmail(email).block()
        return User(admin?.email ?: "email", admin?.password ?: "password", emptyList())
    }

    fun getAdmin(email: String): Mono<Admin> {
        return adminRepository.findByEmail(email)
    }

    fun save(admin: Admin): Mono<Admin> {
        return adminRepository.save(admin)
    }

    fun getAdminByToken(token: String): Mono<Admin> {
        return adminRepository.findByToken(token)

    }
}
