package com.recipia.aos.ui.api.dto.login.jwt

data class JwtRepublishRequestDto(
    val memberId: Long,
    val refreshToken: String
)