package com.recipia.aos.ui.dto.signup

// Retrofit 인터페이스에서 서버 응답에 대한 DTO를 정의
data class TokenResponseDto(
    val accessToken: String,
    val refreshToken: String
)