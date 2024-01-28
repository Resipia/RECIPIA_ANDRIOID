package com.recipia.aos.ui.model.recipe.bookmark

import TokenManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.recipia.aos.ui.api.recipe.BookmarkService
import com.recipia.aos.ui.dto.RecipeListResponseDto
import com.recipia.aos.ui.dto.ResponseDto
import com.recipia.aos.ui.dto.recipe.BookmarkRequestDto
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
            .baseUrl("http://10.0.2.2:8082/") // 주소 확인 필요
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(BookmarkService::class.java)
    }

    // 북마크 추가
    fun addBookmark(recipeId: Long) {
        val request = BookmarkRequestDto(recipeId)
        bookmarkService.addBookmark(request).enqueue(object : Callback<ResponseDto<Long>> {
            // 응답 성공
            override fun onResponse(
                call: Call<ResponseDto<Long>>,
                response: Response<ResponseDto<Long>>
            ) {
                if (response.isSuccessful) {
                    val newBookmarkId = response.body()?.result
                    newBookmarkId?.let { bookmarkId ->
                        _bookmarkUpdateState.postValue(BookmarkUpdateState.Added(recipeId, bookmarkId))
                        snackBarMessage.postValue("북마크에 추가되었습니다.")
                    }
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
                if (response.isSuccessful) {
                    _bookmarkUpdateState.postValue(BookmarkUpdateState.Removed(bookmarkId))
                    snackBarMessage.postValue("북마크가 제거되었습니다.")
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

    // 북마크 상태 토글 함수
    fun toggleBookmark(item: RecipeListResponseDto) {
        item.bookmarkId?.let {
            // bookmarkId가 있으면 북마크 제거
            removeBookmark(it)
        } ?: run {
            // bookmarkId가 없으면 북마크 추가
            addBookmark(item.id ?: return)
        }
    }

    fun resetBookmarkUpdateState() {
        _bookmarkUpdateState.value = null
    }
}
