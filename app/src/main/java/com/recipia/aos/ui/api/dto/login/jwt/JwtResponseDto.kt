package com.recipia.aos.ui.api.dto.login.jwt

data class JwtResponseDto<T>(
    val resultCode: String,
    val result: com.recipia.aos.ui.api.dto.login.jwt.JwtRepublishResponseDto
)