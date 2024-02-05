package com.recipia.aos.ui.model.recipe.bookmark

import TokenManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipia.aos.BuildConfig
import com.recipia.aos.ui.api.recipe.BookmarkService
import com.recipia.aos.ui.dto.RecipeListResponseDto
import com.recipia.aos.ui.dto.ResponseDto
import com.recipia.aos.ui.dto.recipe.BookmarkRequestDto
import com.recipia.aos.ui.model.jwt.TokenRepublishManager
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 북마크 전용 모델 객체
 */
class BookMarkViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    val snackBarMessage = MutableLiveData<String>()

    // 북마크 상태 업데이트를 위한 LiveData
    private val _bookmarkUpdateState = MutableLiveData<BookmarkUpdateState?>()
    val bookmarkUpdateState: MutableLiveData<BookmarkUpdateState?> get() = _bookmarkUpdateState

    // 로그인 화면으로 이동해야 함을 알린다.
    val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> = _navigateToLogin

    // 북마크 요청 refrofit 설정 (로깅 인터셉터 추가)
    private val bookmarkService: BookmarkService by lazy {
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
            .baseUrl(BuildConfig.RECIPE_SERVER_URL) // 주소 확인 필요
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(BookmarkService::class.java)
    }

    // 북마크 추가
    fun addBookmark(recipeId: Long) {
        val request = BookmarkRequestDto(recipeId)
        bookmarkService.addBookmark(request).enqueue(object : Callback<ResponseDto<Long>> {

            override fun onResponse(
                call: Call<ResponseDto<Long>>,
                response: Response<ResponseDto<Long>>
            ) {
                // 응답 성공시 동작
                if (response.isSuccessful) {
                    val newBookmarkId = response.body()?.result
                    newBookmarkId?.let { bookmarkId ->
                        _bookmarkUpdateState.postValue(
                            BookmarkUpdateState.Added(
                                recipeId,
                                bookmarkId
                            )
                        )
                        snackBarMessage.postValue("북마크에 추가되었습니다.")
                    }
                // 만약 jwt의 access토큰이 만료되었다면
                } else if (response.code() == 401) {
                    handleUnauthorizedError {
                        addBookmark(recipeId) // 토큰 재발급 후 북마크 추가 재시도
                    }
                // 다른 오류가 발생했다면
                } else {
                    snackBarMessage.postValue("북마크 추가 실패: ${response.message()}")
                }
            }

            // 응답 실패
            override fun onFailure(call: Call<ResponseDto<Long>>, t: Throwable) {
                snackBarMessage.postValue("네트워크 오류: ${t.message}")
            }
        })
    }

    // 북마크 제거
    fun removeBookmark(bookmarkId: Long) {
        bookmarkService.removeBookmark(bookmarkId).enqueue(object : Callback<ResponseDto<Void>> {

            // 응답 성공
            override fun onResponse(
                call: Call<ResponseDto<Void>>,
                response: Response<ResponseDto<Void>>
            ) {
                // 응답 성공시 동작
                if (response.isSuccessful) {
                    _bookmarkUpdateState.postValue(BookmarkUpdateState.Removed(bookmarkId))
                    snackBarMessage.postValue("북마크가 제거되었습니다.")
                // 만약 jwt의 access토큰이 만료되었다면
                } else if (response.code() == 401) {
                    handleUnauthorizedError {
                        removeBookmark(bookmarkId) // 토큰 재발급 후 북마크 제거 재시도
                    }
                // 다른 오류가 발생했다면
                } else {
                    snackBarMessage.postValue("북마크 제거 실패: ${response.message()}")
                }
            }

            // 응답 실패
            override fun onFailure(
                call: Call<ResponseDto<Void>>,
                t: Throwable
            ) {
                snackBarMessage.postValue("네트워크 오류: ${t.message}")
            }
        })
    }

    // 북마크 상태 업데이트
    fun resetBookmarkUpdateState() {
        _bookmarkUpdateState.value = null
    }

    /**
     * 401 Unauthorized 에러 처리 및 토큰 재발급 로직
     */
    private fun handleUnauthorizedError(
        retryAction: suspend () -> Unit
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

    // 홈 화면 이동 초기화
    fun resetNavigateToLogin() {
        _navigateToLogin.value = false
    }
}
