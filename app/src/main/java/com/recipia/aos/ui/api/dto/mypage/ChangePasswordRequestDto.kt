package com.recipia.aos.ui.api.dto.mypage

/**
 * 비밀번호 변경 요청 dto
 */
data class ChangePasswordRequestDto(
    val originPassword: String,
    val newPassword: String
)