package net.bricn.example.domain.auth.usecase

import net.bricn.example.domain.auth.dto.response.RefreshTokenResponse
import net.bricn.example.domain.auth.entity.RefreshToken
import net.bricn.example.domain.auth.repository.RefreshTokenRepository
import net.bricn.example.global.exception.ExceptionEnum
import net.bricn.example.global.exception.HttpException
import net.bricn.example.global.exception.toPair
import net.bricn.example.global.security.jwt.JwtProvider
import net.bricn.example.global.security.jwt.JwtType
import net.bricn.example.global.security.jwt.dto.JwtDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class RefreshTokenUsecase(
	private val jwtProvider: JwtProvider,
	private val refreshTokenRepository: RefreshTokenRepository,
) {
	fun execute(resolveRefreshToken: String): RefreshTokenResponse {
		val currentUserId = jwtProvider.getIdByRefreshToken(resolveRefreshToken)
		val savedRefreshToken = jwtProvider.getSavedRefreshTokenByRefreshToken(resolveRefreshToken)

		if (resolveRefreshToken != savedRefreshToken.refreshToken) {
			throw HttpException(ExceptionEnum.AUTH.INVALID_REFRESH_TOKEN.toPair())
		}

		val newAccessToken = jwtProvider.generateToken(currentUserId, JwtType.ACCESS_TOKEN)
		val newRefreshToken = deleteRefreshTokenOrSave(currentUserId)

		refreshTokenRepository.save(
			RefreshToken(
				id = UUID.fromString(currentUserId),
				refreshToken = newRefreshToken.token,
				expires = jwtProvider.refreshTokenExpires,
			),
		)

		return RefreshTokenResponse(
			accessToken = newAccessToken.token,
			accessTokenExpiredAt = newAccessToken.expiredAt,
			refreshToken = newRefreshToken.token,
			refreshTokenExpiredAt = newRefreshToken.expiredAt,
		)
	}

	fun deleteRefreshTokenOrSave(id: String): JwtDetails {
		val refreshToken =
			refreshTokenRepository.findById(UUID.fromString(id)).orElseThrow {
				HttpException(ExceptionEnum.AUTH.NOT_FOUND_REFRESH_TOKEN.toPair())
			}

		refreshTokenRepository.delete(refreshToken)
		val newRefreshToken = jwtProvider.generateToken(id, JwtType.REFRESH_TOKEN)

		val newRefreshTokenEntity =
			RefreshToken(
				id = UUID.fromString(id),
				refreshToken = newRefreshToken.token,
				expires = jwtProvider.refreshTokenExpires,
			)

		refreshTokenRepository.save(newRefreshTokenEntity)

		return newRefreshToken
	}
}
