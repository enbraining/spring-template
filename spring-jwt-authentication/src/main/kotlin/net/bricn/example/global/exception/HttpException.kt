package net.bricn.example.global.exception

import org.springframework.http.HttpStatus

open class HttpException(
	val exceptionEnum: Pair<HttpStatus, String>,
) : RuntimeException(exceptionEnum.second)
