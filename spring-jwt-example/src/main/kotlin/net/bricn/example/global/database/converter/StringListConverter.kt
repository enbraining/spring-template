package net.bricn.example.global.database.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.bricn.example.domain.user.enums.Role

@Converter
class StringListConverter : AttributeConverter<List<Role?>?, String?> {
	override fun convertToDatabaseColumn(attribute: List<Role?>?): String? =
		if (attribute.isNullOrEmpty()) {
			null
		} else {
			attribute.joinToString(",")
		}

	override fun convertToEntityAttribute(dbData: String?): List<Role>? {
		val roleList = dbData?.split(",")?.map { Role.valueOf(it) }?.toList()
		return roleList
	}
}
