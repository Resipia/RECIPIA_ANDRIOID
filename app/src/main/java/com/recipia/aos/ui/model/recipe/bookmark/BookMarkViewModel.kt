package com.recipia.aos.ui.model.recipe.bookmark

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.recipia.aos.ui.api.BookmarkService
import com.recipia.aos.ui.dto.RecipeMainListResponseDto
import com.recipia.aos.ui.dto.login.ResponseDto
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
class BookMarkViewModel() : ViewModel() {

    val toastMessage = MutableLiveData<String>()
    private val bookmarks = MutableLiveData<List<RecipeMainListResponseDto>?>()

    // 북마크 상태 변경을 감지하기 위한 LiveData
    val bookmarkChangeNotifier = MutableLiveData<Long?>()

    // 북마크 요청 refrofit 설정 (로깅 인터셉터 추가)
    private val bookmarkService: BookmarkService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
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
                    // 북마크를 추가하고 받은 결과값에 있는 id를 상태 업데이트에 사용한다.
                    val newBookmarkId = response.body()?.result
                    newBookmarkId?.let { bookmarkId ->
                        updateBookmarkStatus(recipeId,  bookmarkId)
                    }
                    toastMessage.postValue("북마크에 추가되었습니다.")
                } else {
                    toastMessage.postValue("북마크 추가 실패: ${response.message()}")
                }
            }

            // 응답 실패
            override fun onFailure(
                call: Call<ResponseDto<Long>>,
                t: Throwable
            ) {
                toastMessage.postValue("네트워크 오류: ${t.message}")
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
                    // 북마크 상태 업데이트
                    updateBookmarkStatus(null, null)
                    // 토스트 메시지 전송
                    toastMessage.postValue("북마크가 제거되었습니다.")
                } else {
                    toastMessage.postValue("북마크 제거 실패: ${response.message()}")
                }
            }

            // 응답 실패
            override fun onFailure(
                call: Call<ResponseDto<Void>>,
                t: Throwable
            ) {
                toastMessage.postValue("네트워크 오류: ${t.message}")
            }
        })
    }

    // 북마크 상태 토글
    fun toggleBookmark(item: RecipeMainListResponseDto) {
        item.bookmarkId?.let {
            removeBookmark(it)
        } ?: run {
            addBookmark(item.id ?: return)
        }
    }

    // 북마크 상태 업데이트
    private fun updateBookmarkStatus(
        recipeId: Long?,
        newBookmarkId: Long?
    ) {
        val updatedList = bookmarks.value?.map { recipe ->
            if (recipe.id == recipeId) {
                recipe.copy(bookmarkId = newBookmarkId)
            } else {
                recipe
            }
        }
        bookmarks.postValue(updatedList)
        // 북마크 변경을 알림
        bookmarkChangeNotifier.postValue(recipeId)
    }
}
