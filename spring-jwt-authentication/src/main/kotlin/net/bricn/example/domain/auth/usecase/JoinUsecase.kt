package net.bricn.example.domain.auth.usecase

import net.bricn.example.domain.auth.dto.request.SignUpRequest
import net.bricn.example.domain.user.entity.User
import net.bricn.example.domain.user.enums.Role
import net.bricn.example.domain.user.repository.UserRepository
import net.bricn.example.global.exception.ExceptionEnum
import net.bricn.example.global.exception.HttpException
import net.bricn.example.global.exception.toPair
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class JoinUsecase(
	private val userRepository: UserRepository,
	private val passwordEncoder: PasswordEncoder,
) {
	@Transactional
	fun execute(request: SignUpRequest) {
		val encodedPassword = passwordEncoder.encode(request.password)

		if (userRepository.existsByEmail(request.email)) {
			throw HttpException(ExceptionEnum.AUTH.DUPLICATED_EMAIL.toPair())
		}

		userRepository.save(
			User(
				email = request.email,
				encodedPassword = encodedPassword,
				roles = mutableListOf(Role.ROLE_USER),
			),
		)
	}
}
