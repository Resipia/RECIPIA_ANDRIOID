package com.recipia.aos.ui.model.mypage.ask

import TokenManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipia.aos.BuildConfig
import com.recipia.aos.ui.api.recipe.mypage.ask.AskService
import com.recipia.aos.ui.dto.mypage.ask.AskListResponseDto
import com.recipia.aos.ui.dto.mypage.ask.AskRequestDto
import com.recipia.aos.ui.dto.mypage.ask.AskViewResponseDto
import com.recipia.aos.ui.dto.mypage.ask.ViewAskRequestDto
import com.recipia.aos.ui.model.jwt.TokenRepublishManager
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * 문의하기 전용 모델 객체
 */
class AskViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    val askItems = MutableLiveData<List<AskListResponseDto>>(emptyList())
    var isLastPage = false
    private var currentPage = 0
    private val pageSize = 10

    val askDetail = MutableLiveData<AskViewResponseDto?>()

    // 로그인 화면으로 이동해야 함을 알린다.
    val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> = _navigateToLogin

    // 북마크 요청 refrofit 설정 (로깅 인터셉터 추가)
    private val askService: AskService by lazy {
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
            .baseUrl(BuildConfig.MEMBER_SERVER_URL) // 멤버 서버 요청
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(AskService::class.java)
    }

    // 첫 페이지 로드 함수
    fun loadFirstInitItems() {
        currentPage = 0 // 첫 페이지로 초기화
        askItems.value = emptyList() // 기존 목록 초기화
        getAskList() // 첫 페이지 데이터 불러오기
    }


    // 추가 데이터 로드 함수
    fun loadMoreItems() {
        if (!isLastPage) { // 마지막 페이지가 아닌 경우에만 더 불러오기
            getAskList()
        }
    }

    // 문의사항 목록 가져오는 함수
    private fun getAskList() {
        viewModelScope.launch {
            try {
                val response = askService.getList(currentPage, pageSize)
                if (response.isSuccessful) {
                    val newItems = response.body()?.content ?: emptyList()
                    askItems.value = askItems.value!! + newItems // 기존 목록에 추가
                    isLastPage = newItems.size < pageSize // 마지막 페이지 여부 업데이트
                    currentPage++ // 다음 페이지로
                } else if (response.code() == 401) {
                    handleUnauthorizedError {
                        getAskList() // 401 에러 시 재시도할 작업,
                    }
                } else {
                    // 다른 오류 처리
                }
            } catch (e: Exception) {
                // 네트워크 오류 처리
            }
        }
    }

    // 문의사항 등록 함수
    fun createAsk(
        title: String,
        content: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = askService.createAsk(AskRequestDto(title, content))
                if (response.isSuccessful) {
                    // 성공적으로 문의사항이 등록되면 문의사항 목록 초기화 및 첫 페이지 로드
                    askItems.postValue(emptyList()) // LiveData를 사용하여 UI 스레드에서 안전하게 데이터 업데이트
                    onSuccess()
                } else if (response.code() == 401) {
                    handleUnauthorizedError {
                        createAsk(title, content, onSuccess, onError) // 401 에러 시 재시도할 작업
                    }
                } else {
                    onError("문의사항 등록에 실패했습니다.")
                }
            } catch (e: Exception) {
                // 네트워크 오류 등의 예외 처리
                onError("네트워크 오류가 발생했습니다.")
            }
        }
    }

    // 문의/피드백 상세보기
    fun getAskDetail(askId: Long) {
        viewModelScope.launch {
            try {
                val response = askService.getDetailAsk(ViewAskRequestDto(askId))
                if (response.isSuccessful && response.body()?.result != null) {
                    askDetail.postValue(response.body()?.result)
                } else if (response.code() == 401) {
                    handleUnauthorizedError {
                        getAskDetail(askId) // 401 에러 시 재시도할 작업
                    }
                } else {
                    askDetail.postValue(null) // 오류 처리
                }
            } catch (e: Exception) {
                // 네트워크 오류 처리
                askDetail.postValue(null)
            }
        }
    }

    /**
     * 401 Unauthorized 에러 처리 및 토큰 재발급 로직
     */
    private fun handleUnauthorizedError(
        retryAction: () -> Unit
    ) {
        viewModelScope.launch {
            val tokenRepublishManager = TokenRepublishManager(tokenManager)
            val result = tokenRepublishManager.renewTokenIfNeeded()
            if (result) {
                retryAction() // 토큰 재발급 성공 시 전달받은 작업 재시도
            } else {
                // 토큰 재발급에 실패했다면, 로그인 화면으로 이동합니다.
                _navigateToLogin.value = true
            }
        }
    }

}