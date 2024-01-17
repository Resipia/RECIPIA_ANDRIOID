package com.recipia.aos.ui.jwt

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Request
import java.util.Date

class JwtTokenManager(private val context: Context) {

    // 토큰 저장
    fun saveToken(token: String) {
        val sharedPreferences = context.getSharedPreferences("my_app_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("jwt_token", token)
        editor.apply()
    }

    // 토큰 로드
    private fun loadToken(): String? {
        val sharedPreferences = context.getSharedPreferences("my_app_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("jwt_token", null)
    }

    // 토큰을 요청 헤더에 추가
    fun addTokenToHeader(chain: Interceptor.Chain): Request {
        val request = chain.request()
        val token = loadToken()

        if (!token.isNullOrBlank()) {
            return request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        }

        return request
    }

    fun hasValidToken(): Boolean {
        val token = loadToken()
        return !token.isNullOrBlank()
    }

}
