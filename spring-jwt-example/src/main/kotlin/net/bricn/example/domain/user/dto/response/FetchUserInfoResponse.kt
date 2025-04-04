package net.bricn.example.domain.user.dto.response

import java.util.UUID

data class FetchUserInfoResponse(
	val id: UUID,
	val email: String,
)
