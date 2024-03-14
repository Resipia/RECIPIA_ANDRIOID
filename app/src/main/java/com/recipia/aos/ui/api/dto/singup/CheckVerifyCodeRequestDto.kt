package com.recipia.aos.ui.api.dto.singup

data class CheckVerifyCodeRequestDto(
    val phoneNumber: String,
    val verifyCode: String
)