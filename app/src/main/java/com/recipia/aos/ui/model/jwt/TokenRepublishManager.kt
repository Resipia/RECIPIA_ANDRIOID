package com.recipia.aos.ui.model.jwt

import TokenManager
import android.util.Log
import com.recipia.aos.BuildConfig
import com.recipia.aos.ui.api.recipe.jwt.JwtRepublishService
import com.recipia.aos.ui.dto.login.jwt.JwtRepublishRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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

    /**
     * 토큰 재발급 요청 실시
     * Dispatchers.IO: 입출력, 네트워크 작업과 같은 블로킹 작업에 최적화된 스레드 풀이다.
     */
    suspend fun renewTokenIfNeeded(): Boolean = withContext(Dispatchers.IO) {
        val memberId = tokenManager.loadMemberId()
        val refreshToken = tokenManager.loadRefreshToken()

        // memberId가 존재하고 refreshToken도 존재하면 재발급 요청 실시
        if (memberId != null && refreshToken != null) {
            try {
                val response = jwtRepublishService.republishAccessToken(
                    JwtRepublishRequestDto(
                        memberId,
                        refreshToken
                    )
                )
                // 재발급 요청에 성공한다면
                if (response.isSuccessful) {
                    val newAccessToken = response.body()?.result?.accessToken
                    newAccessToken?.let {
                        tokenManager.saveAccessToken(it)
                        return@withContext true
                    }
                }
                // 실패했다면
                return@withContext false
            } catch (e: Exception) {
                Log.e("TokenManager", "Error on renewing token: ${e.message}")
                return@withContext false
            }
        }
        return@withContext false
    }
}
