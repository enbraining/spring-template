package net.bricn.example.domain.user.usecase

import net.bricn.example.domain.user.dto.response.FetchUserInfoResponse
import net.bricn.example.global.util.UserUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FetchUserUsecase(
	private val userUtil: UserUtil,
) {
	fun execute(): FetchUserInfoResponse {
		val user = userUtil.getUser()

		return FetchUserInfoResponse(
			id = requireNotNull(user.id),
			email = user.email,
		)
	}
}
