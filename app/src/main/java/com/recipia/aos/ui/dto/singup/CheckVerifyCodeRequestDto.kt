package com.recipia.aos.ui.dto.singup

data class CheckVerifyCodeRequestDto(
    val phoneNumber: String,
    val verifyCode: String
)