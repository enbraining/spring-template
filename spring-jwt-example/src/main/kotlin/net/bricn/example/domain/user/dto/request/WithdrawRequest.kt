package net.bricn.example.domain.user.dto.request

import jakarta.validation.constraints.NotNull

data class WithdrawRequest(
	@field:NotNull
	val password: String,
)
