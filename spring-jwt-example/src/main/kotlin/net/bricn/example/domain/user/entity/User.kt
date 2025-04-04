package net.bricn.example.domain.user.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import net.bricn.example.domain.user.enums.Role
import net.bricn.example.global.database.converter.StringListConverter
import org.hibernate.annotations.UuidGenerator
import java.util.UUID

@Entity(name = "users")
data class User(
	@Id
	@UuidGenerator
	@GeneratedValue
	val id: UUID? = null,

	@Column(unique = true, nullable = false)
	val email: String,

	@Column(nullable = false)
	var encodedPassword: String,

	@Convert(converter = StringListConverter::class)
	val roles: List<Role>,
)
