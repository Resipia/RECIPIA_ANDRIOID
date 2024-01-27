package com.recipia.aos.ui.api.recipe.jwt

import com.recipia.aos.ui.dto.ResponseDto
import com.recipia.aos.ui.dto.login.jwt.JwtRepublishRequestDto
import com.recipia.aos.ui.dto.login.jwt.JwtRepublishResponseDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface JwtRepublishService {
    @POST("/member/jwt/republish")
    fun republishAccessToken(
        @Body republishRequestDto: JwtRepublishRequestDto
    ): Call<ResponseDto<JwtRepublishResponseDto>>
}
