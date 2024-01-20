package com.recipia.aos.ui.api

import com.recipia.aos.ui.dto.ResponseDto
import com.recipia.aos.ui.dto.singup.CheckVerifyCodeRequestDto
import com.recipia.aos.ui.dto.singup.PhoneNumberRequestDto
import com.recipia.aos.ui.dto.singup.ServerResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response

interface AuthApiService {

    @POST("/member/auth/phone")
    suspend fun sendPhoneNumber(
        @Body requestDto: PhoneNumberRequestDto
    ): Response<ResponseDto<ServerResponse>>

    @POST("/member/auth/check/verifyCode")
    suspend fun checkVerifyCode(
        @Body requestDto: CheckVerifyCodeRequestDto
    ): Response<ResponseDto<Boolean>>
}
