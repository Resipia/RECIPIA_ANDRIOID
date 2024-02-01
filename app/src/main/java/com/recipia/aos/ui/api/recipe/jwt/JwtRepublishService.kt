package com.recipia.aos.ui.api.recipe.jwt

import com.recipia.aos.ui.dto.ResponseDto
import com.recipia.aos.ui.dto.login.jwt.JwtRepublishRequestDto
import com.recipia.aos.ui.dto.login.jwt.JwtRepublishResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * jwt 재발급
 */
interface JwtRepublishService {

    @POST("/member/jwt/republish")
    suspend fun republishAccessToken(
        @Body republishRequestDto: JwtRepublishRequestDto
    ): Response<ResponseDto<JwtRepublishResponseDto>>

}