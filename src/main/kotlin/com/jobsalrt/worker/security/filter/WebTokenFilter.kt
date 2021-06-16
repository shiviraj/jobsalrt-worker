package com.jobsalrt.worker.security.filter

import com.jobsalrt.worker.security.WebToken
import com.jobsalrt.worker.service.AdminService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class WebTokenFilter(
    @Autowired private val adminService: AdminService,
    @Autowired private val webToken: WebToken
) : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val token = request.getHeader("authorization") ?: ""
        val email = webToken.extractEmail(token)
        if (SecurityContextHolder.getContext().authentication == null) {
            val userDetails = adminService.loadUserByUsername(email)
            if (webToken.validateToken(token, userDetails)) {
                val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.authorities
                )
                usernamePasswordAuthenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
            }
        }
        request.setAttribute("adminEmail", email)
        chain.doFilter(request, response)
    }
}
