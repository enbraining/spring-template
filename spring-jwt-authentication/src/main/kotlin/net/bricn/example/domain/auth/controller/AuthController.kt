package net.bricn.example.domain.auth.controller

import jakarta.validation.Valid
import net.bricn.example.domain.auth.dto.request.SignInRequest
import net.bricn.example.domain.auth.dto.request.SignUpRequest
import net.bricn.example.domain.auth.dto.response.RefreshTokenResponse
import net.bricn.example.domain.auth.dto.response.SignInResponse
import net.bricn.example.domain.auth.usecase.JoinUsecase
import net.bricn.example.domain.auth.usecase.LoginUsecase
import net.bricn.example.domain.auth.usecase.LogoutUsecase
import net.bricn.example.domain.auth.usecase.RefreshTokenUsecase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("auth")
class AuthController(
	private val joinUsecase: JoinUsecase,
	private val loginUsecase: LoginUsecase,
	private val refreshTokenUsecase: RefreshTokenUsecase,
	private val logoutUsecase: LogoutUsecase,
) {
	@PostMapping("join")
	fun joinUser(
		@Valid @RequestBody signUpRequest: SignUpRequest,
	): ResponseEntity<Unit> =
		joinUsecase.execute(signUpRequest).let {
			ResponseEntity.ok().build()
		}

	@PostMapping("login")
	fun login(
		@Valid @RequestBody signInRequest: SignInRequest,
	): ResponseEntity<SignInResponse> =
		loginUsecase.execute(signInRequest).let {
			ResponseEntity.ok(it)
		}

	@PostMapping("logout")
	fun logout(
		@RequestHeader("Refresh-Token") refreshToken: String,
	): ResponseEntity<Unit> {
		val resolveRefreshToken = refreshToken.substring(7)
		return logoutUsecase.execute(resolveRefreshToken).let {
			ResponseEntity.ok().build()
		}
	}

	@PatchMapping
	fun refreshToken(
		@RequestHeader("Refresh-Token") refreshToken: String,
	): ResponseEntity<RefreshTokenResponse> {
		val resolveRefreshToken = refreshToken.substring(7)
		return refreshTokenUsecase.execute(resolveRefreshToken).let {
			ResponseEntity.ok(it)
		}
	}
}
