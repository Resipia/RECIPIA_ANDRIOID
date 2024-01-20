package com.recipia.aos.ui.api

import com.recipia.aos.ui.dto.ResponseDto
import com.recipia.aos.ui.dto.singup.CheckVerifyCodeRequestDto
import com.recipia.aos.ui.dto.singup.PhoneNumberRequestDto
import com.recipia.aos.ui.dto.singup.ServerResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response

/**
 * suspend fun은 Kotlin의 코루틴을 사용하여 비동기 코드를 처리할 때 주로 사용된다.
 * 이 함수를 호출하면 비동기 작업이 완료될 때까지 기다리지 않고 다른 작업을 수행할 수 있다.
 */
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
