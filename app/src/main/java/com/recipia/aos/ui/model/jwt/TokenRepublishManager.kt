package com.recipia.aos.ui.model.jwt

import TokenManager
import android.util.Log
import com.recipia.aos.BuildConfig
import com.recipia.aos.ui.api.recipe.jwt.JwtRepublishService
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

    // 토큰 재발급 api 설계
    private val jwtRepublishService: JwtRepublishService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.MEMBER_SERVER_URL) // 멤버 서버 요청
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(JwtRepublishService::class.java)
    }

    // 토큰 재발급 요청 실시
    suspend fun renewTokenIfNeeded(): Boolean {
        val memberId = tokenManager.loadMemberId()
        val refreshToken = tokenManager.loadRefreshToken()

        if (memberId != null && refreshToken != null) {
            try {
                val response = jwtRepublishService.republishAccessToken(JwtRepublishRequestDto(memberId, refreshToken))

                if (response.isSuccessful) {
                    val newAccessToken = response.body()?.result?.accessToken
                    newAccessToken?.let {
                        tokenManager.saveAccessToken(it)
                        return true
                    }
                }
                return false
            } catch (e: Exception) {
                Log.e("TokenManager", "Error on renewing token: ${e.message}")
                return false
            }
        }
        return false
    }
}
