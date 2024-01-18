package com.recipia.aos.ui.dto.login.jwt

data class JwtResponseDto<T>(
    val resultCode: String,
    val result: JwtRepublishResponseDto
)