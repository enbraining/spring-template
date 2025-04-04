package net.bricn.example.global.security.configuration

import net.bricn.example.domain.user.enums.Role
import net.bricn.example.global.security.filter.JwtFilter
import net.bricn.example.global.security.jwt.JwtProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig(
	private val jwtProvider: JwtProvider,
) {
	@Bean
	fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
		val jwtFilter = JwtFilter(jwtProvider)

		return http
			.authorizeHttpRequests {
				it
					// 인증
					.requestMatchers("/auth/**")
					.permitAll()
					// 사용자
					.requestMatchers(HttpMethod.GET, "/user/**")
					.hasAuthority(Role.ROLE_USER.name)
					.requestMatchers(HttpMethod.DELETE, "/user/withdraw")
					.hasAuthority(Role.ROLE_USER.name)
					// 상태 확인
					.requestMatchers(HttpMethod.GET, "/actuator/**")
					.permitAll()
			}.csrf { it.disable() }
			.formLogin { it.disable() }
			.httpBasic { it.disable() }
			.sessionManagement {
				it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			}.cors {
				it.configurationSource(corsConfig())
			}.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
			.build()
	}

	fun corsConfig(): CorsConfigurationSource {
		val corsConfigurationSource = CorsConfiguration()
		corsConfigurationSource.addAllowedHeader("*")
		corsConfigurationSource.addAllowedMethod("*")
		corsConfigurationSource.addAllowedOriginPattern("*")
		corsConfigurationSource.allowCredentials = true
		corsConfigurationSource.addExposedHeader("Authorization")

		val urlBasedCorsConfigurationSource = UrlBasedCorsConfigurationSource()

		urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfigurationSource)
		return urlBasedCorsConfigurationSource
	}
}
