package com.jobsalrt.worker.controller

import com.jobsalrt.worker.controller.view.AuthenticationRequest
import com.jobsalrt.worker.controller.view.AuthenticationResponse
import com.jobsalrt.worker.domain.Admin
import com.jobsalrt.worker.security.WebToken
import com.jobsalrt.worker.service.AdminService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/admin")
class AdminController(
    @Autowired private val adminService: AdminService,
    @Autowired private val webToken: WebToken
) {

    @PostMapping("/sign-in")
    fun login(@RequestBody authenticationRequest: AuthenticationRequest): Mono<AuthenticationResponse> {
        return adminService.login(authenticationRequest)
            .flatMap {
                it.token = webToken.generateToken(it.email)
                adminService.save(it)
            }.map {
                AuthenticationResponse(it.token)
            }
            .switchIfEmpty(
                Mono.just(AuthenticationResponse())
            )
    }

    @GetMapping
    fun getAdminData(request: HttpServletRequest): Mono<Admin> {
        val email = (request.getAttribute("adminEmail") ?: "") as String
        return adminService.getAdmin(email)
    }
}
