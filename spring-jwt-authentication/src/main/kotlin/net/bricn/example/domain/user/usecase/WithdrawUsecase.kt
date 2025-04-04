package net.bricn.example.domain.user.usecase

import net.bricn.example.domain.auth.repository.RefreshTokenRepository
import net.bricn.example.domain.user.repository.UserRepository
import net.bricn.example.global.exception.ExceptionEnum
import net.bricn.example.global.exception.HttpException
import net.bricn.example.global.exception.toPair
import net.bricn.example.global.util.UserUtil
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class WithdrawUsecase(
	private val userRepository: UserRepository,
	private val refreshTokenRepository: RefreshTokenRepository,
	private val userUtil: UserUtil,
	private val passwordEncoder: PasswordEncoder,
) {
	fun execute(password: String) {
		val savedUser = userUtil.getUser()
		val isComparePassword = passwordEncoder.matches(password, savedUser.encodedPassword)
		val id = savedUser.id

		requireNotNull(id) { "id cannot be null" }

		if (!isComparePassword) {
			throw HttpException(ExceptionEnum.AUTH.WRONG_PASSWORD.toPair())
		}

		refreshTokenRepository.deleteById(id)
		userRepository.delete(savedUser)
	}
}
