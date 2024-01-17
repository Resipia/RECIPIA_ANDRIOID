package com.recipia.aos.ui.api

import com.recipia.aos.ui.dto.signup.ServerResponseDto
import com.recipia.aos.ui.dto.signup.TokenMemberInfoDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    // 로그인 요청을 보내는 POST 요청 정의
    @POST("/auth/login")
    fun login(
        @Body tokenMemberInfoDto: TokenMemberInfoDto
    ): Call<ServerResponseDto>
}