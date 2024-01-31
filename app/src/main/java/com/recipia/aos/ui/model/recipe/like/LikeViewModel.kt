package com.recipia.aos.ui.model.recipe.like

import TokenManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipia.aos.BuildConfig
import com.recipia.aos.ui.api.recipe.BookmarkService
import com.recipia.aos.ui.api.recipe.like.RecipeLikeService
import com.recipia.aos.ui.dto.like.RecipeLikeRequestDto
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 좋아요 전용 Model 객체
 */
class LikeViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    // 좋아요 요청 refrofit 설정 (로깅 인터셉터 추가)
    private val recipeLikeService: RecipeLikeService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 로깅 및 jwt 추가
        val client = OkHttpClient.Builder().addInterceptor(logging).addInterceptor { chain ->
            val request = tokenManager.addAccessTokenToHeader(chain)
            chain.proceed(request)
        }.build()

        Retrofit.Builder().baseUrl(BuildConfig.RECIPE_SERVER_URL) // 레시피 서버
            .addConverterFactory(GsonConverterFactory.create()).client(client).build()
            .create(RecipeLikeService::class.java)
    }

    // 좋아요 api 함수
    fun toggleRecipeLike(
        recipeLikeId: Long?,
        recipeId: Long?,
        memberId: Long?,
        onSuccess: (Long?) -> Unit,
        onError: (String) -> Unit
    ) {
        if (recipeId == null || memberId == null) {
            onError("recipeId 또는 memberId가 null입니다.")
            return
        }

        viewModelScope.launch {
            try {
                // 좋아요 요청 DTO 생성
                val likeRequestDto = RecipeLikeRequestDto(recipeLikeId, recipeId, memberId)
                // 서버에 요청
                val response = recipeLikeService.recipeLike(likeRequestDto)

                if (response.isSuccessful && response.body() != null) {
                    // 성공 시 콜백 호출, 좋아요 ID 업데이트
                    onSuccess(response.body()!!.result)
                } else {
                    // 실패 시 콜백 호출
                    onError("좋아요 처리 중 오류가 발생했습니다.")
                }
            } catch (e: Exception) {
                onError(e.message ?: "알 수 없는 오류가 발생했습니다.")
            }
        }
    }
}