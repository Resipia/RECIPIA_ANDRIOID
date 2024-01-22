package com.recipia.aos.ui.model.signup

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipia.aos.ui.api.MemberManagementService
import com.recipia.aos.ui.dto.ResponseDto
import com.recipia.aos.ui.dto.singup.EmailAvailableRequestDto
import com.recipia.aos.ui.dto.singup.NicknameAvailableRequestDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignUpViewModel : ViewModel() {

    // 1번째 회원가입 입력 form 데이터
    private val _phoneNumber = MutableStateFlow("")

    // 2번째 회원가입 입력 form 데이터
    private val _name = MutableStateFlow("")
    private val _nickname = MutableStateFlow("")
    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")

    // 3번째 회원가입 입력 form 데이터
    private val _profilePictureUri = MutableStateFlow<Uri?>(null)
    private val _oneLineIntroduction = MutableStateFlow("")
    private val _gender = MutableStateFlow("")
    private val _selectedDate = MutableStateFlow("")

    val name: StateFlow<String> = _name
    val nickname: StateFlow<String> = _nickname
    val email: StateFlow<String> = _email
    val password: StateFlow<String> = _password
    val phoneNumber: StateFlow<String> = _phoneNumber
    val profilePictureUri: StateFlow<Uri?> = _profilePictureUri
    val oneLineIntroduction: StateFlow<String> = _oneLineIntroduction
    val gender: StateFlow<String> = _gender
    val selectedDate: StateFlow<String> = _selectedDate


    // 이메일 중복 체크
    val _isEmailAvailable = MutableLiveData<Boolean?>()
    val _emailDuplicateCheckResult = MutableLiveData<String?>()
    val emailDuplicateCheckResult: LiveData<String?> = _emailDuplicateCheckResult

    // 이메일 인증 성공 여부를 나타내는 LiveData 추가
    private val _isEmailVerified = MutableLiveData(false)
    val isEmailVerified: LiveData<Boolean> = _isEmailVerified

    // 닉네임 중복 체크 결과 관련 LiveData 추가
    private val _isNicknameAvailable = MutableLiveData<Boolean?>()
    val isNicknameAvailable: LiveData<Boolean?> = _isNicknameAvailable

    private val _nicknameDuplicateCheckResult = MutableLiveData<String?>()
    val nicknameDuplicateCheckResult: LiveData<String?> = _nicknameDuplicateCheckResult

    // 비밀번호 일치 여부를 나타내는 LiveData 추가
    private val _isPasswordMatching = MutableLiveData(false)
    val isPasswordMatching: LiveData<Boolean> = _isPasswordMatching

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

    // 새로운 데이터 업데이트 함수들
    fun updateProfilePictureUri(newUri: Uri?) {
        _profilePictureUri.value = newUri
    }

    fun updateOneLineIntroduction(newIntroduction: String) {
        _oneLineIntroduction.value = newIntroduction
    }

    fun updateGender(newGender: String) {
        _gender.value = newGender
    }

    fun updateSelectedDate(newDate: String) {
        _selectedDate.value = newDate
    }

    // 닉네임 중복 체크 함수
    fun checkDuplicateNickname(nickname: String) {
        viewModelScope.launch {
            try {
                val response = getCheckDuplicateNicknameResult(nickname)

                if (response.isSuccessful) {
                    val responseDto: ResponseDto<Boolean>? = response.body()
                    if (responseDto?.result != null) {
                        _isNicknameAvailable.value = responseDto.result
                        // 중복 체크 결과 메시지 업데이트
                        _nicknameDuplicateCheckResult.value = if (responseDto.result) "사용가능한 닉네임입니다." else "이미 존재하는 닉네임입니다."
                    }
                } else {
                    // 오류 처리
                }
            } catch (e: Exception) {
                // 예외 처리
            }
        }
    }

    // 이메일 중복 체크 함수
    fun checkDuplicateEmail(email: String) {
        viewModelScope.launch {
            try {
                val response = getCheckDuplicateEmailResult(email)

                if (response.isSuccessful) {
                    val responseDto: ResponseDto<Boolean>? = response.body()
                    if (responseDto?.result != null) {
                        // 이메일 인증 성공 여부 업데이트
                        _isEmailVerified.value = responseDto.result
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

    // 닉네임 중복 체크
    private suspend fun getCheckDuplicateNicknameResult(
        nickname: String
    ): Response<ResponseDto<Boolean>> {
        // Retrofit을 사용하여 API 호출
        return memberManagementService.checkDuplicateNickname(NicknameAvailableRequestDto(nickname))
    }

    // 비밀번호 일치여부 변경 함수
    fun updatePasswordMatching(isMatching: Boolean) {
        _isPasswordMatching.value = isMatching
    }

    // Model 초기화 함수
    fun clearData() {
        _name.value = ""
        _nickname.value = ""
        _email.value = ""
        _password.value = ""
        _phoneNumber.value = ""
        _isEmailAvailable.value = null
        _emailDuplicateCheckResult.value = null
        _isEmailVerified.value = false
        _isPasswordMatching.value = false
    }

    // 이메일 중복 체크 결과를 초기화하는 함수
    fun resetEmailDuplicateCheck() {
        _emailDuplicateCheckResult.value = ""
    }

    // 닉네임 중복 체크 결과를 초기화하는 함수
    fun resetNicknameDuplicateCheck() {
        _nicknameDuplicateCheckResult.value = null
    }

}
