package com.recipia.aos.ui.model.recipe.read

import TokenManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.recipia.aos.BuildConfig
import com.recipia.aos.ui.api.recipe.RecipeDetailAndDeleteService
import com.recipia.aos.ui.dto.recipe.detail.RecipeDetailViewResponseDto
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RecipeSearchViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    // 서버로부터 받은 레시피 상세 정보를 저장하는 LiveData
    private val _recipeDetail = MutableLiveData<RecipeDetailViewResponseDto?>()
    val recipeDetail: MutableLiveData<RecipeDetailViewResponseDto?> = _recipeDetail

    // RecipeApiService를 초기화하는 코드
    val recipeDetailAndDeleteService: RecipeDetailAndDeleteService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 클라이언트에서 보낼 요청을 생성 (여기서 jwt를 헤더에 추가해줌)
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                // JwtTokenManager를 사용하여 토큰을 요청 헤더에 추가
                val request = tokenManager.addAccessTokenToHeader(chain)
                chain.proceed(request)
            }
            .build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.RECIPE_SERVER_URL) // 여기서는 서버의 Base URL을 설정합니다.
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecipeDetailAndDeleteService::class.java)
    }


}