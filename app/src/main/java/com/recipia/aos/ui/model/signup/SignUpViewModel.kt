package com.recipia.aos.ui.model.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipia.aos.ui.api.MemberManagementService
import com.recipia.aos.ui.dto.ResponseDto
import com.recipia.aos.ui.dto.singup.EmailAvailableRequestDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignUpViewModel : ViewModel() {

    // 각 입력 필드에 대한 StateFlow
    private val _name = MutableStateFlow("")
    private val _nickname = MutableStateFlow("")
    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _phoneNumber = MutableStateFlow("")

    val name: StateFlow<String> = _name
    val nickname: StateFlow<String> = _nickname
    val email: StateFlow<String> = _email
    val password: StateFlow<String> = _password
    val phoneNumber: StateFlow<String> = _phoneNumber

    // 중복 체크 결과를 나타내는 LiveData
    val _isEmailAvailable = MutableLiveData<Boolean?>()

    // 중복 체크 결과와 관련된 상태 추가
    val _emailDuplicateCheckResult = MutableLiveData<String?>()
    val emailDuplicateCheckResult: LiveData<String?> = _emailDuplicateCheckResult

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

    // 여기에 데이터 업데이트 함수들 추가
    fun updateName(newName: String) {
        _name.value = newName
    }

    fun updateNickname(newNickname: String) {
        _nickname.value = newNickname
    }

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun updatePhoneNumber(newPhoneNumber: String) {
        _phoneNumber.value = newPhoneNumber
    }

    // 중복 체크 함수
    fun checkDuplicateNickname(nickname: String) {
        // TODO: Retrofit을 사용하여 API 호출 및 결과를 _isNicknameAvailable에 설정
    }

    // 중복 체크 함수 수정
    fun checkDuplicateEmail(email: String) {
        viewModelScope.launch {
            try {
                val response = getCheckDuplicateEmailResult(email)
                if (response.isSuccessful) {
                    val responseDto: ResponseDto<Boolean>? = response.body()
                    if (responseDto?.result != null) {
                        _isEmailAvailable.value = responseDto.result
                        // 중복 체크 결과 메시지 업데이트
                        _emailDuplicateCheckResult.value = if (responseDto.result) "사용가능한 이메일입니다." else "이미 존재하는 이메일입니다."
                    }
                } else {
                    // 오류 처리
                }
            } catch (e: Exception) {
                // 예외 처리
            }
        }
    }

    // 이메일 중복 체크
    private suspend fun getCheckDuplicateEmailResult(
        email: String
    ): Response<ResponseDto<Boolean>> {
        // Retrofit을 사용하여 API 호출
        return memberManagementService.checkDuplicateEmail(EmailAvailableRequestDto(email))
    }

}
