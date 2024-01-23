package com.recipia.aos.ui.model.forgot

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipia.aos.ui.api.MemberManagementService
import com.recipia.aos.ui.dto.ResponseDto
import com.recipia.aos.ui.dto.forgot.FindEmailRequestDto
import com.recipia.aos.ui.dto.forgot.TempPasswordRequestDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 이메일, 비밀번호 찾기 Model
 */
class ForgotViewModel : ViewModel() {

    // 이메일 찾기 state
    private val _name = MutableStateFlow("")
    private val _phoneNumber = MutableStateFlow("")

    // 비밀번호 찾기 state
    private val _email = MutableStateFlow("")

    // 찾은 이메일을 저장하는 변수
    private val _foundEmail = MutableStateFlow<String?>(null)
    val foundEmail: StateFlow<String?> = _foundEmail

    // RecipeApiService를 초기화
    val memberManagementService: MemberManagementService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 클라이언트에서 보낼 요청을 생성 (여기서 jwt를 헤더에 추가해줌)
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8081/") // 멤버 서버 호출
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MemberManagementService::class.java)
    }

    // 이메일 찾기
    fun findEmail(
        name: String,
        phoneNumber: String,
        onResult: (ResponseDto<String>?) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = getFindEmailResponse(name, phoneNumber)
                if (response.isSuccessful) {
                    response.body()?.let {
                        onResult(it)
                    } ?: run {
                        onError("응답 데이터가 없습니다.")
                    }
                } else {
                    Log.d("ListItem", "서버 오류: ${response.code()}")
                    onError("사용자를 찾을 수 없습니다.")
                }
            } catch (e: Exception) {
                onError("네트워크 오류가 발생했습니다: ${e.localizedMessage}")
            }
        }
    }

    // 임시 비밀번호 전송
    fun sendTempPassword(
        email: String,
        onResult: (Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = getFindPasswordResponse(email)
                if (response.isSuccessful) {
                    // 성공적으로 임시 비밀번호 전송
                    onResult(true)
                } else {
                    // 서버에서 오류 응답
                    Log.d("ListItem", "서버 오류: ${response.code()}")
                    onError("회원가입되지 않은 이메일입니다.")
                }
            } catch (e: Exception) {
                // 네트워크 오류 처리
                onError("네트워크 오류가 발생했습니다: ${e.localizedMessage}")
            }
        }
    }

    // 이메일 찾기 호출(코루틴)
    private suspend fun getFindEmailResponse(
        name: String,
        phoneNumber: String
    ): Response<ResponseDto<String>> {
        // Retrofit을 사용하여 API 호출
        return memberManagementService.findEmail(FindEmailRequestDto(name, phoneNumber))
    }

    // 임시 비밀번호 재발급 호출(코루틴)
    private suspend fun getFindPasswordResponse(
        email: String
    ): Response<ResponseDto<Void>> {
        // Retrofit을 사용하여 API 호출
        return memberManagementService.sendTempPassword(TempPasswordRequestDto(email))
    }

    // 찾은 이메일 저장
    fun saveFoundEmail(email: String?) {
        _foundEmail.value = email
    }


}
