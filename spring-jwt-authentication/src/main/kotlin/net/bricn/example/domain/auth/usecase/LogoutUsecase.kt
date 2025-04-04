package net.bricn.example.domain.auth.usecase

import net.bricn.example.domain.auth.repository.RefreshTokenRepository
import net.bricn.example.global.exception.ExceptionEnum
import net.bricn.example.global.exception.HttpException
import net.bricn.example.global.exception.toPair
import net.bricn.example.global.security.jwt.JwtProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LogoutUsecase(
	private val refreshTokenRepository: RefreshTokenRepository,
	private val jwtProvider: JwtProvider,
) {
	fun execute(resolveRefreshToken: String) {
		val savedRefreshToken = jwtProvider.getSavedRefreshTokenByRefreshToken(resolveRefreshToken)

		if (resolveRefreshToken != savedRefreshToken.refreshToken) {
			throw HttpException(ExceptionEnum.AUTH.INVALID_REFRESH_TOKEN.toPair())
		}

		refreshTokenRepository.delete(savedRefreshToken)
	}
}
