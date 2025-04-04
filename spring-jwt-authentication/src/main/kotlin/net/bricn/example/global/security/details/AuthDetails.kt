package net.bricn.example.global.security.details

import net.bricn.example.domain.user.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class AuthDetails(
	private val user: User,
) : UserDetails {
	override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
		user.roles
			.stream()
			.map {
				SimpleGrantedAuthority(it.name)
			}.toList()
			.toMutableList()

	override fun getPassword(): String = user.encodedPassword

	override fun getUsername(): String = user.email
}
