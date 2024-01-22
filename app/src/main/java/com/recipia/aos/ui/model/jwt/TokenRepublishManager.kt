package com.recipia.aos.ui.model.jwt

import TokenManager
import android.util.Log
import com.recipia.aos.ui.api.JwtRepublishService
import com.recipia.aos.ui.dto.ResponseDto
import com.recipia.aos.ui.dto.login.jwt.JwtRepublishRequestDto
import com.recipia.aos.ui.dto.login.jwt.JwtRepublishResponseDto
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * 토큰 재발급 요청을 보내는 클래스
 */
class TokenRepublishManager(
    private val tokenManager: TokenManager
) {

    private val jwtRepublishService: JwtRepublishService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8081/") // 주소 확인 필요
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(JwtRepublishService::class.java)
    }

    fun renewTokenIfNeeded(
        onTokenRenewed: (String) -> Unit,
        onRenewalFailed: (Boolean) -> Unit
    ) {

        // 1. 멤버id, 리프레시 토큰 추출
        val memberId = tokenManager.loadMemberId()
        val refreshToken = tokenManager.loadRefreshToken()

        if (memberId != null && refreshToken != null) {

            // 2. 멤버 서버에 엑세스 토큰 재발급 요청 보내기
            jwtRepublishService.republishAccessToken(JwtRepublishRequestDto(memberId, refreshToken))
                .enqueue(object : Callback<ResponseDto<JwtRepublishResponseDto>> {

                    // 응답 성공시 동작
                    override fun onResponse(
                        call: Call<ResponseDto<JwtRepublishResponseDto>>,
                        response: Response<ResponseDto<JwtRepublishResponseDto>>
                    ) {
                        if (response.isSuccessful) {
                            val newAccessToken = response.body()?.result?.accessToken
                            newAccessToken?.let {
                                tokenManager.saveAccessToken(it)
                                onTokenRenewed(it)
                            } ?: onRenewalFailed(false)
                        } else {
                            onRenewalFailed(true)
                        }
                    }

                    // 응답 실패시 동작
                    override fun onFailure(
                        call: Call<ResponseDto<JwtRepublishResponseDto>>,
                        t: Throwable
                    ) {
                        // 실패한 경우 로그를 찍어서 문제를 파악하도록 하자.
                        Log.e("TokenManager", "Error on renewing token: ${t.message}")
                        onRenewalFailed(true)
                    }
                })
        } else {
            onRenewalFailed(true)
        }
    }
}
