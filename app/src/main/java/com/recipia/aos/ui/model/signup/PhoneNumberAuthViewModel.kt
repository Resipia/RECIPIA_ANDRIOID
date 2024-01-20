package com.recipia.aos.ui.model.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipia.aos.ui.api.AuthApiService
import com.recipia.aos.ui.dto.singup.CheckVerifyCodeRequestDto
import com.recipia.aos.ui.dto.singup.PhoneNumberRequestDto
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PhoneNumberAuthViewModel() : ViewModel() {

    var phone: String = "" // 전화번호 변수 추가
    var responseCode by mutableStateOf(0)

    // 인증 성공 여부와 메시지
    var isVerificationSuccess by mutableStateOf(false)
    var verificationMessage by mutableStateOf("")

    var verificationSentMessage by mutableStateOf("") // 인증번호 전송 메시지
    var verificationSuccessMessage by mutableStateOf("") // 인증 성공 메시지

    // Retrofit 인스턴스 생성
    private val authApiService: AuthApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8081/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(AuthApiService::class.java)
    }


    // 휴대폰 번호로 인증코드 전송
    fun sendVerificationCode(phone: String) {
        viewModelScope.launch {
            try {
                val response = authApiService.sendPhoneNumber(PhoneNumberRequestDto(phone))

                // 성공적인 응답 처리
                if (response.isSuccessful) {
                    verificationSentMessage = "인증번호가 발송되었습니다."
                    responseCode = 200
                } else {
                    // 실패한 응답 처리
                    responseCode = response.code()
                    val errorResponseBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorResponseBody)

                    if (errorJson != null) {
                        val errorCode = errorJson.optInt("code")

                        if (errorCode == 2002) {
                            verificationMessage = "이미 존재하는 휴대폰 번호입니다."
                        } else {
                            // 다른 오류 코드 처리
                        }
                    } else {
                        // 기타 오류 메시지 처리
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // 예외 처리
            }
        }
    }


    // 인증코드 검증
    fun checkVerificationCode(code: String) {
        viewModelScope.launch {
            val response = authApiService.checkVerifyCode(CheckVerifyCodeRequestDto(phone, code))

            if (response.isSuccessful && response.body()?.result == true) {
                verificationSuccessMessage = "인증에 성공했습니다."
                isVerificationSuccess = true
            } else {
                verificationSuccessMessage = "인증에 실패했습니다."
                isVerificationSuccess = false
            }
        }
    }

}