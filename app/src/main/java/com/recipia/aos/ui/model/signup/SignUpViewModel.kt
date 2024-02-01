package com.recipia.aos.ui.model.signup

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipia.aos.BuildConfig
import com.recipia.aos.ui.api.signup.SignUpAndForgotService
import com.recipia.aos.ui.dto.ResponseDto
import com.recipia.aos.ui.dto.singup.EmailAvailableRequestDto
import com.recipia.aos.ui.dto.singup.NicknameAvailableRequestDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream

class SignUpViewModel : ViewModel() {

    // 1번째 회원가입 입력 form 데이터
    private val _phoneNumber = MutableStateFlow("")
    private val _isPersonalInfoConsent = MutableStateFlow(false)
    private val _isDataRetentionConsent = MutableStateFlow(false)

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
    val signUpAndForgotService: SignUpAndForgotService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 클라이언트에서 보낼 요청을 생성 (여기서 jwt를 헤더에 추가해줌)
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.MEMBER_SERVER_URL) // 멤버 서버 요청
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SignUpAndForgotService::class.java)
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
    fun checkDuplicateNickname(
        nickname: String,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = getCheckDuplicateNicknameResult(nickname)

                if (response.isSuccessful) {
                    val responseDto: ResponseDto<Boolean>? = response.body()
                    if (responseDto?.result != null) {
                        _isNicknameAvailable.value = responseDto.result
                        // 중복 체크 결과 메시지 업데이트
                        _nicknameDuplicateCheckResult.value =
                            if (responseDto.result) "사용 가능한 닉네임입니다." else "이미 존재하는 닉네임입니다."
                    } else {
                        onError("응답 데이터가 없습니다.")
                    }
                } else {
//                    onError("서버 오류: ${response.code()}")
                    onError("사용 불가능한 이메일입니다.")
                }
            } catch (e: Exception) {
                onError("네트워크 오류가 발생했습니다: ${e.localizedMessage}")
            }
        }
    }

    // 이메일 중복 체크 함수
    fun checkDuplicateEmail(
        email: String,
        onError: (String) -> Unit
    ) {
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
                        _emailDuplicateCheckResult.value =
                            if (responseDto.result) "사용 가능한 이메일입니다." else "이미 존재하는 이메일입니다."
                    } else {
                        onError("응답 데이터가 없습니다.")
                    }

                } else {
//                    onError("서버 오류: ${response.code()}")
                    onError("중복된 이메일입니다.")
                }
            } catch (e: Exception) {
                onError("네트워크 오류가 발생했습니다: ${e.localizedMessage}")
            }
        }
    }

    // 이메일 중복 체크
    private suspend fun getCheckDuplicateEmailResult(
        email: String
    ): Response<ResponseDto<Boolean>> {
        // Retrofit을 사용하여 API 호출
        return signUpAndForgotService.checkDuplicateEmail(EmailAvailableRequestDto(email))
    }

    // 닉네임 중복 체크
    private suspend fun getCheckDuplicateNicknameResult(
        nickname: String
    ): Response<ResponseDto<Boolean>> {
        // Retrofit을 사용하여 API 호출
        return signUpAndForgotService.checkDuplicateNickname(NicknameAvailableRequestDto(nickname))
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
        _isNicknameAvailable.value = false
        _nicknameDuplicateCheckResult.value = null
        _profilePictureUri.value = null
        _oneLineIntroduction.value = ""
        _gender.value = ""
        _selectedDate.value = ""
        _isPersonalInfoConsent.value = false
        _isDataRetentionConsent.value = false
    }

    // 이메일 중복 체크 결과를 초기화하는 함수
    fun resetEmailDuplicateCheck() {
        _emailDuplicateCheckResult.value = ""
    }

    // 닉네임 중복 체크 결과를 초기화하는 함수
    fun resetNicknameDuplicateCheck() {
        _nicknameDuplicateCheckResult.value = null
    }

    // Boolean 값을 'Y' 또는 'N'으로 변환하는 함수
    private fun booleanToYN(value: Boolean): String {
        return if (value) "Y" else "N"
    }

    // 개인정보 수집 및 이용 동의 상태 업데이트 함수
    fun updatePersonalInfoConsent(consent: Boolean) {
        _isPersonalInfoConsent.value = consent
    }

    // 개인정보 보관 및 파기 동의 상태 업데이트 함수
    fun updateDataRetentionConsent(consent: Boolean) {
        _isDataRetentionConsent.value = consent
    }

    // 서버로 회원가입 데이터 전송
    fun signUp(
        context: Context,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // 입력 데이터를 RequestBody 객체로 변환
                val emailBody = _email.value.toRequestBody("text/plain".toMediaType())
                val passwordBody = _password.value.toRequestBody("text/plain".toMediaType())
                val nameBody = _name.value.toRequestBody("text/plain".toMediaType())
                val nicknameBody = _nickname.value.toRequestBody("text/plain".toMediaType())
                val telNo = _phoneNumber.value.toRequestBody("text/plain".toMediaType())
                val introductionBody = _oneLineIntroduction.value.toRequestBody("text/plain".toMediaType())
                val genderBody = _gender.value.toRequestBody("text/plain".toMediaType())
                val selectedDateBody = _selectedDate.value.toRequestBody("text/plain".toMediaType())
                val personalInfoConsentBody = booleanToYN(_isPersonalInfoConsent.value).toRequestBody("text/plain".toMediaType())
                val dataRetentionConsentBody = booleanToYN(_isDataRetentionConsent.value).toRequestBody("text/plain".toMediaType())

                // 프로필 이미지 처리
                val profileImagePart = _profilePictureUri.value?.let { uri ->
                    uriToMultipartBodyPart(uri, context)
                }

                // Retrofit 서비스 호출
                val response = signUpAndForgotService.signUp(
                    email = emailBody,
                    password = passwordBody,
                    fullName = nameBody,
                    nickname = nicknameBody,
                    introduction = introductionBody,
                    telNo = telNo,
                    address1 = null,
                    address2 = null,
                    profileImage = profileImagePart,
                    isPersonalInfoConsent = personalInfoConsentBody,
                    isDataRetentionConsent = dataRetentionConsentBody,
                    birth = selectedDateBody,
                    gender = genderBody
                )

                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    Log.d("회원가입 실패", "Response received: ${response.errorBody()?.string()}")
                    onFailure("회원가입에 실패했습니다.")
                }

            } catch (e: Exception) {
                onFailure("네트워크 오류가 발생했습니다: ${e.localizedMessage}")
            }
        }
    }

    // Uri를 MultipartBody.Part로 변환하는 함수
    private fun uriToMultipartBodyPart(uri: Uri, context: Context): MultipartBody.Part? {
        val file = uriToFile(uri, context) ?: return null
        val requestBody = file.asRequestBody("image/jpeg".toMediaType())
        return MultipartBody.Part.createFormData("profileImage", file.name, requestBody)
    }

    // Uri를 파일로 변환하는 함수

    private fun uriToFile(uri: Uri, context: Context): File? {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            // 임시 파일 생성
            val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir).apply {
                deleteOnExit()
            }

            FileOutputStream(tempFile).use { fileOutputStream ->
                // InputStream의 내용을 파일에 복사
                inputStream.copyTo(fileOutputStream)
            }

            return tempFile
        }
        return null
    }

}
