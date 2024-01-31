package com.recipia.aos.ui.model.login

import TokenManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipia.aos.BuildConfig
import com.recipia.aos.ui.api.login.LoginService
import com.recipia.aos.ui.dto.login.TokenMemberInfoDto
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {


    // retrofit 초기화
    private val loginService: LoginService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.MEMBER_SERVER_URL) // 멤버 서버 요청
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LoginService::class.java)
    }

    // 로그인 함수
    fun login(
        email: String,
        password: String,
        onLoginSuccess: () -> Unit,
        onLoginFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = loginService.login(TokenMemberInfoDto(email, password))
                if (response.isSuccessful) {
                    response.body()?.result?.let {
                        // 엑세스 토큰, 리프래시 토큰, 멤버id 저장
                        tokenManager.saveAccessToken(it.accessToken)
                        tokenManager.saveRefreshToken(it.refreshToken)
                        tokenManager.saveMemberId(it.memberId)
                        onLoginSuccess() // 엑세스 토큰을 전달하지 않고 콜백만 호출
                    } ?: onLoginFailure("존재하지 않는 계정입니다.")
                } else {
                    // 서버로부터의 실패 응답 처리
                    if (response.code() == 403) {
                        onLoginFailure("존재하지 않는 계정입니다.")
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                        onLoginFailure(errorMsg)
                    }
                }
            } catch (e: Exception) {
                onLoginFailure(e.message ?: "Network error occurred")
            }
        }
    }

}
