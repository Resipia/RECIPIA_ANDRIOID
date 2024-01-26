package com.recipia.aos.ui.model.recipe.read

import TokenManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipia.aos.ui.api.RecipeApiService
import com.recipia.aos.ui.dto.ResponseDto
import com.recipia.aos.ui.dto.recipe.detail.RecipeDetailViewResponseDto
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RecipeDetailViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    // 서버로부터 받은 레시피 상세 정보를 저장하는 LiveData
    private val _recipeDetail = MutableLiveData<RecipeDetailViewResponseDto?>()
    val recipeDetail: MutableLiveData<RecipeDetailViewResponseDto?> = _recipeDetail

    // 레시피 상세 정보 로딩 상태
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // RecipeApiService를 초기화하는 코드
    val recipeApiService: RecipeApiService by lazy {
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
            .baseUrl("http://10.0.2.2:8082/") // 여기서는 서버의 Base URL을 설정합니다.
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecipeApiService::class.java)
    }

    fun loadRecipeDetail(recipeId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _recipeDetail.value = null // 이전 상태 초기화
                val response = getRecipeDetailView(recipeId) // Retrofit을 사용한 서버 요청
                if (response.isSuccessful && response.body() != null) {
                    _recipeDetail.value = response.body()!!.result
                } else {
                    // 처리 로직 (에러 처리)
                }
            } catch (e: Exception) {
                // 예외 처리
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 서버로부터 레시피 상세 정보를 가져오는 함수 (Retrofit 사용)
    private suspend fun getRecipeDetailView(
        recipeId: Long
    ): Response<ResponseDto<RecipeDetailViewResponseDto>> {

        // Retrofit을 사용하여 서버 요청을 구현합니다.
         return recipeApiService.getRecipeDetailView(recipeId)
    }

    // 레시피 삭제 API 호출 함수
    fun deleteRecipe(
        recipeId: Long,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = recipeApiService.deleteRecipe(recipeId)
                if (response.isSuccessful) {
                    onSuccess() // 삭제 성공 시 수행할 액션
                } else {
                    onError("삭제 중 오류가 발생했습니다.") // 오류 처리
                }
            } catch (e: Exception) {
                onError(e.message ?: "알 수 없는 오류 발생") // 예외 처리
            }
        }
    }

}
