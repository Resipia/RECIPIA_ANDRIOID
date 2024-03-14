package com.recipia.aos.ui.api.dto.forgot

/**
 * 비밀번호 재발급 요청 dto
 */
data class TempPasswordRequestDto(
    val name: String,
    val telNo: String,
    val email: String
)