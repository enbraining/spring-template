package net.bricn.example.domain.auth.usecase

import net.bricn.example.domain.auth.dto.request.SignInRequest
import net.bricn.example.domain.auth.dto.response.SignInResponse
import net.bricn.example.domain.auth.entity.RefreshToken
import net.bricn.example.domain.auth.repository.RefreshTokenRepository
import net.bricn.example.domain.user.repository.UserRepository
import net.bricn.example.global.exception.ExceptionEnum
import net.bricn.example.global.exception.HttpException
import net.bricn.example.global.exception.toPair
import net.bricn.example.global.security.jwt.JwtProvider
import net.bricn.example.global.security.jwt.JwtType
import net.bricn.example.global.security.jwt.dto.JwtDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional
class LoginUsecase(
	private val userRepository: UserRepository,
	private val passwordEncoder: PasswordEncoder,
	private val jwtProvider: JwtProvider,
	private val refreshTokenRepository: RefreshTokenRepository,
) {
	fun execute(signInRequest: SignInRequest): SignInResponse {
		val email = signInRequest.email
		val user =
			userRepository.findByEmail(email).orElseThrow {
				HttpException(ExceptionEnum.USER.NOT_FOUND_USER.toPair())
			}

		val id = user.id
		requireNotNull(id) { "id cannot be null" }

		val rawPassword = signInRequest.password
		val encodedPassword = user.encodedPassword

		if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
			throw HttpException(ExceptionEnum.AUTH.WRONG_PASSWORD.toPair())
		}

		val accessToken = jwtProvider.generateToken(id.toString(), JwtType.ACCESS_TOKEN)
		val refreshToken = getRefreshTokenOrSave(id)

		return SignInResponse(
			accessToken = accessToken.token,
			accessTokenExpiredAt = accessToken.expiredAt,
			refreshToken = refreshToken.token,
			refreshTokenExpiredAt = refreshToken.expiredAt,
		)
	}

	fun getRefreshTokenOrSave(id: UUID): JwtDetails {
		val refreshToken = refreshTokenRepository.findById(id)

		if (refreshToken.isEmpty) {
			val newRefreshToken = jwtProvider.generateToken(id.toString(), JwtType.REFRESH_TOKEN)
			refreshTokenRepository.save(
				RefreshToken(
					id = id,
					refreshToken = newRefreshToken.token,
					expires = jwtProvider.refreshTokenExpires,
				),
			)
			return newRefreshToken
		} else {
			return JwtDetails(
				token = refreshToken.get().refreshToken,
				expiredAt = LocalDateTime.now().plus(Duration.ofMillis(refreshToken.get().expires)),
			)
		}
	}
}
