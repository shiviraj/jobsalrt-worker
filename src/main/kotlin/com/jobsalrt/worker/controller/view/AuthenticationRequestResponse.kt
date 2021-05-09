package com.jobsalrt.worker.controller.view

data class AuthenticationRequest(val email: String, val password: String)

data class AuthenticationResponse(val token: String? = "")
