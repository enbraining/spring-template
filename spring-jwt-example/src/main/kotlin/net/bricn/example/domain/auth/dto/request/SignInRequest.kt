package net.bricn.example.domain.auth.dto.request

import jakarta.validation.constraints.NotNull

data class SignInRequest(
	@field:NotNull
	val email: String,
	@field:NotNull
	val password: String,
)
