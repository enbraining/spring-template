package net.bricn.example.global.util

import net.bricn.example.domain.user.entity.User
import net.bricn.example.domain.user.repository.UserRepository
import net.bricn.example.global.exception.ExceptionEnum
import net.bricn.example.global.exception.HttpException
import net.bricn.example.global.exception.toPair
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class UserUtil(
	private val userRepository: UserRepository,
) {
	fun getUser(): User {
		val email = SecurityContextHolder.getContext().authentication.name
		return userRepository.findByEmail(email).orElseThrow {
			HttpException(ExceptionEnum.USER.NOT_FOUND_USER.toPair())
		}
	}
}
