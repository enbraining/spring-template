package net.bricn.example.domain.auth.dto.response

import java.time.LocalDateTime

data class SignInResponse(
	val accessToken: String,
	val accessTokenExpiredAt: LocalDateTime,
	val refreshToken: String,
	val refreshTokenExpiredAt: LocalDateTime,
)
