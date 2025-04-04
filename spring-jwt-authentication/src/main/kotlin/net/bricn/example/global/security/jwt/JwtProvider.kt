package net.bricn.example.global.security.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import net.bricn.example.domain.auth.entity.RefreshToken
import net.bricn.example.domain.auth.repository.RefreshTokenRepository
import net.bricn.example.global.exception.ExceptionEnum
import net.bricn.example.global.exception.HttpException
import net.bricn.example.global.exception.toPair
import net.bricn.example.global.security.details.AuthDetailsService
import net.bricn.example.global.security.jwt.dto.JwtDetails
import net.bricn.example.global.util.toDate
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime
import java.util.Base64
import java.util.Date
import java.util.UUID

@Component
class JwtProvider(
	private val authDetailsService: AuthDetailsService,
	private val refreshTokenRepository: RefreshTokenRepository,
	@Value("\${jwt.access-token-key}")
	private val accessTokenKey: String,
	@Value("\${jwt.refresh-token-key}")
	private val refreshTokenKey: String,
	@Value("\${jwt.access-token-expires}")
	private val accessTokenExpires: Long,
	@Value("\${jwt.refresh-token-expires}")
	val refreshTokenExpires: Long,
) {
	fun getAuthentication(token: String?): UsernamePasswordAuthenticationToken? {
		val resolvedToken = resolveToken(token)
		val payload = getPayload(resolvedToken, JwtType.ACCESS_TOKEN)

		val userDetails = authDetailsService.loadUserByUsername(payload.subject)

		return UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
	}

	fun resolveToken(token: String?): String? =
		if (token == null || !token.startsWith("Bearer ")) {
			null
		} else {
			token.substring(7)
		}

	fun getSavedRefreshTokenByRefreshToken(refreshToken: String): RefreshToken {
		val userId = UUID.fromString(getPayload(refreshToken, JwtType.REFRESH_TOKEN).subject)
		val savedRefreshToken =
			refreshTokenRepository.findById(userId).orElseThrow {
				HttpException(ExceptionEnum.AUTH.NOT_FOUND_REFRESH_TOKEN.toPair())
			}

		return savedRefreshToken
	}

	fun getIdByRefreshToken(refreshToken: String): String = getPayload(refreshToken, JwtType.REFRESH_TOKEN).subject

	fun getPayload(
		token: String?,
		jwtType: JwtType,
	): Claims {
		if (token == null) {
			throw HttpException(ExceptionEnum.AUTH.EMPTY_TOKEN.toPair())
		}

		val tokenKey = if (jwtType == JwtType.ACCESS_TOKEN) accessTokenKey else refreshTokenKey
		val keyBytes = Base64.getEncoder().encode(tokenKey.encodeToByteArray())
		val signingKey = Keys.hmacShaKeyFor(keyBytes)

		try {
			return Jwts
				.parser()
				.verifyWith(signingKey)
				.build()
				.parseSignedClaims(token)
				.payload
		} catch (e: ExpiredJwtException) {
			throw HttpException(ExceptionEnum.AUTH.EXPIRED_TOKEN.toPair())
		} catch (e: UnsupportedJwtException) {
			throw HttpException(ExceptionEnum.AUTH.UNSUPPORTED_TOKEN.toPair())
		} catch (e: MalformedJwtException) {
			throw HttpException(ExceptionEnum.AUTH.MALFORMED_TOKEN.toPair())
		} catch (e: RuntimeException) {
			throw HttpException(ExceptionEnum.AUTH.OTHER_TOKEN.toPair())
		}
	}

	fun generateToken(
		id: String,
		jwtType: JwtType,
	): JwtDetails {
		val isAccessToken = jwtType == JwtType.ACCESS_TOKEN
		val expiredAt =
			LocalDateTime.now().plus(
				Duration.ofMillis(
					if (isAccessToken) {
						accessTokenExpires
					} else {
						refreshTokenExpires
					},
				),
			)

		val tokenKey = if (jwtType == JwtType.ACCESS_TOKEN) accessTokenKey else refreshTokenKey
		val keyBytes = Base64.getEncoder().encode(tokenKey.encodeToByteArray())
		val signingKey = Keys.hmacShaKeyFor(keyBytes)

		val token =
			Jwts
				.builder()
				.subject(id)
				.signWith(signingKey)
				.issuedAt(Date())
				.expiration(expiredAt.toDate())
				.compact()

		return JwtDetails(
			token = token,
			expiredAt = expiredAt,
		)
	}
}
