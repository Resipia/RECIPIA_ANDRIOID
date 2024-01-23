package com.recipia.aos.ui.model.mypage

import TokenManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipia.aos.ui.api.MyPageService
import com.recipia.aos.ui.dto.mypage.MyPageViewResponse
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 마이페이지 전용 Model
 */
class MyPageViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _myPageData = MutableLiveData<MyPageViewResponse?>()
    val myPageData: MutableLiveData<MyPageViewResponse?> = _myPageData


    // 북마크 요청 refrofit 설정 (로깅 인터셉터 추가)
    private val myPageService: MyPageService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = tokenManager.addAccessTokenToHeader(chain)
                chain.proceed(request)
            }
            .build()

        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8081/") // 멤버 서버 요청
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(MyPageService::class.java)
    }

    // 마이페이지 정보 로딩
    fun loadMyPageData() {
        viewModelScope.launch {
            try {
                val response = myPageService.viewMyPage()
                if (response.isSuccessful) {
                    _myPageData.value = response.body()?.result
                } else {
                    // 에러 처리
                }
            } catch (e: Exception) {
                // 네트워크 오류 등의 예외 처리
            }
        }
    }

    // 로그아웃
    fun logout(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = myPageService.logout()
                if (response.isSuccessful) {
                    clearSession()
                    onSuccess()
                } else {
                    onError("로그아웃 실패")
                }
            } catch (e: Exception) {
                onError("네트워크 에러 발생")
            }
        }
    }

    // 회원 탈퇴
    fun deactivateAccount(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = myPageService.deactivate()
                if (response.isSuccessful) {
                    clearSession()
                    onSuccess()
                } else {
                    onError("회원탈퇴 실패")
                }
            } catch (e: Exception) {
                onError("네트워크 에러 발생")
            }
        }
    }

    // jwt 관련정보 초기화
    private fun clearSession() {
        tokenManager.saveAccessToken("")
        tokenManager.saveRefreshToken("")
        tokenManager.saveMemberId(0)
    }

}
