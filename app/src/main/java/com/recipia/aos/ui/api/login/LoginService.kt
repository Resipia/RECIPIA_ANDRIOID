package com.recipia.aos.ui.api.login

import com.recipia.aos.ui.api.dto.login.LoginResponseDto
import com.recipia.aos.ui.api.dto.ResponseDto
import com.recipia.aos.ui.api.dto.login.TokenMemberInfoDto
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 로그인
 */
interface LoginService {

    // 코루틴을 사용하여 로그인 요청 보내기
    @POST("/auth/login")
    suspend fun login(
        @Body tokenMemberInfoDto: com.recipia.aos.ui.api.dto.login.TokenMemberInfoDto
    ): Response<com.recipia.aos.ui.api.dto.ResponseDto<com.recipia.aos.ui.api.dto.login.LoginResponseDto>>

}