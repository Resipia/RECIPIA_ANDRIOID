package com.recipia.aos.ui.dto.signup

// 서버의 전체 응답을 나타내는 클래스
data class ServerResponseDto(
    val resultCode: String,
    val result: TokenResponseDto
)