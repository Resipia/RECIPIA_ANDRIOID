package com.recipia.aos.ui.dto.login.jwt

data class JwtRepublishRequestDto(
    val memberId: Long,
    val refreshToken: String
)