package com.recipia.aos.ui.dto

// 서버의 전체 응답을 나타내는 클래스
data class ResponseDto<T>(
    val resultCode: String,
    val result: T
)