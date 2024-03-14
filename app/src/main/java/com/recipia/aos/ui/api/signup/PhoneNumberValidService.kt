package com.recipia.aos.ui.api.signup

import com.recipia.aos.ui.api.dto.ResponseDto
import com.recipia.aos.ui.api.dto.singup.CheckVerifyCodeRequestDto
import com.recipia.aos.ui.api.dto.singup.PhoneNumberRequestDto
import com.recipia.aos.ui.api.dto.singup.ServerResponse
import com.recipia.aos.ui.api.dto.singup.TelNoAvailableRequestDto
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response

/**
 * suspend fun은 Kotlin의 코루틴을 사용하여 비동기 코드를 처리할 때 주로 사용된다.
 * 이 함수를 호출하면 비동기 작업이 완료될 때까지 기다리지 않고 다른 작업을 수행할 수 있다.
 */
interface PhoneNumberValidService {

    // 전화번호 중복 체크 요청
    @POST("/member/management/checkDupTelNo")
    suspend fun checkDupTelNo(
        @Body requestDto: com.recipia.aos.ui.api.dto.singup.TelNoAvailableRequestDto
    ): Response<com.recipia.aos.ui.api.dto.ResponseDto<Boolean>>

    // 핸드폰 번호 인증 요청
    @POST("/member/auth/phone")
    suspend fun sendPhoneNumber(
        @Body requestDto: com.recipia.aos.ui.api.dto.singup.PhoneNumberRequestDto
    ): Response<com.recipia.aos.ui.api.dto.ResponseDto<com.recipia.aos.ui.api.dto.singup.ServerResponse>>

    // 인증번호 입력 후 확인 요청
    @POST("/member/auth/check/verifyCode")
    suspend fun checkVerifyCode(
        @Body requestDto: com.recipia.aos.ui.api.dto.singup.CheckVerifyCodeRequestDto
    ): Response<com.recipia.aos.ui.api.dto.ResponseDto<Boolean>>

}
