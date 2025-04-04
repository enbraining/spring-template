package net.bricn.example.domain.user.controller

import net.bricn.example.domain.user.dto.request.WithdrawRequest
import net.bricn.example.domain.user.dto.response.FetchUserInfoResponse
import net.bricn.example.domain.user.usecase.FetchUserUsecase
import net.bricn.example.domain.user.usecase.WithdrawUsecase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("user")
class UserController(
	private val fetchUserUsecase: FetchUserUsecase,
	private val withdrawUsecase: WithdrawUsecase,
) {
	@GetMapping
	fun getUserInfo(): ResponseEntity<FetchUserInfoResponse> =
		fetchUserUsecase.execute().let {
			ResponseEntity.ok(it)
		}

	@DeleteMapping("withdraw")
	fun withdraw(
		@RequestBody withdrawRequest: WithdrawRequest,
	): ResponseEntity<Unit> =
		withdrawUsecase.execute(withdrawRequest.password).let {
			ResponseEntity.ok().build()
		}
}
