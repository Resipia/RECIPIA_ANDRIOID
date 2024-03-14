package com.recipia.aos.ui.api.dto.singup

import okhttp3.MultipartBody

/**
 * 회원가입을 요청할 때 데이터를 담는 request dto
 */
data class SignUpRequestDto(
    val email: String,
    val password: String,
    val fullName: String?,
    val nickname: String,
    val introduction: String?,
    val telNo: String?,
    val address1: String?,
    val address2: String?,
    val profileImage: MultipartBody.Part?,
    val isPersonalInfoConsent: String,
    val isDataRetentionConsent: String,
    val birth: String?,
    val gender: String?
)
