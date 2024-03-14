package com.recipia.aos.ui.api.dto.forgot

/**
 * 이메일 찾기 요청 dto
 */
data class FindEmailRequestDto(
    val fullName: String,
    val telNo: String
)