package com.recipia.aos.ui.dto.login

// Retrofit 인터페이스에서 서버 응답에 대한 DTO를 정의
data class LoginResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val memberId: Long
)