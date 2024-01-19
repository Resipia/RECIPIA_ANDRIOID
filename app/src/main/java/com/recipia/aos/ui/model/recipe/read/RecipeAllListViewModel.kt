package com.recipia.aos.ui.model.recipe.read

import TokenManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.recipia.aos.ui.api.GetAllRecipeListService
import com.recipia.aos.ui.dto.PagingResponseDto
import com.recipia.aos.ui.dto.RecipeMainListResponseDto
import com.recipia.aos.ui.model.jwt.TokenRepublishManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RecipeAllListViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _items = MutableLiveData<List<RecipeMainListResponseDto>>()
    val items: LiveData<List<RecipeMainListResponseDto>> = _items

    // 현재 페이지, 사이즈, 정렬 유형 저장
    private var currentRequestPage: Int = 0
    private var currentRequestSize: Int = 10
    private var currentRequestSortType: String = "new"
    var isLastPage = false

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loadFailed = MutableLiveData<Boolean>(false)
    val loadFailed: LiveData<Boolean> = _loadFailed

    // 로그인 화면으로 이동해야 함을 알린다.
    private val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> = _navigateToLogin

    // 모든 레시피 리스트를 호출하는 서비스 선언
    val getAllRecipeListService: GetAllRecipeListService by lazy {
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
            .baseUrl("http://10.0.2.2:8082/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GetAllRecipeListService::class.java)
    }

    fun resetLoadFailed() {
        _loadFailed.value = false
    }

    private fun redirectToLogin() {
        _navigateToLogin.value = true
    }

    // 더 많은 아이템을 요청하는 메서드
    fun loadMoreItems() {
        Log.d("RecipeAllListViewModel", "Loading more items")
        if (_isLoading.value == true || isLastPage) return

        _isLoading.value = true
        loadItemsFromServer(currentRequestPage, currentRequestSize, currentRequestSortType)
        Log.d("RecipeAllListViewModel", "Loading finished")
    }

    // 서버로부터 데이터를 가져오는 함수 예시
    private fun loadItemsFromServer(page: Int, size: Int, sortType: String) {
        Log.d("RecipeAllListViewModel", "Loading items from server - Page: $page")

        // 현재 요청 정보 저장
        currentRequestPage = page
        currentRequestSize = size
        currentRequestSortType = sortType

        // 서버에 레시피 전체 리스트 데이터 요청
        getAllRecipeListService.getAllRecipeList(currentRequestPage, currentRequestSize, currentRequestSortType)
            .enqueue(object : Callback<PagingResponseDto<RecipeMainListResponseDto>> {

                // 응답 성공
                override fun onResponse(
                    call: Call<PagingResponseDto<RecipeMainListResponseDto>>,
                    response: Response<PagingResponseDto<RecipeMainListResponseDto>>
                ) {
                    if (response.isSuccessful) {
                        Log.d("RecipeAllListViewModel", "Response received: ${response.body()}")

                        val newItems = response.body()?.content ?: emptyList()
                        val currentItems = _items.value ?: emptyList()
                        _items.postValue(currentItems + newItems)
                        isLastPage = newItems.size < currentRequestSize
                        currentRequestPage++ // 현재 페이지 업데이트
                        Log.d("Count", "content count received: ${response.body()?.content?.size}")
                    } else {
                        if (response.code() == 401) {
                            // 401 Unauthorized 오류 처리
                            handleUnauthorizedError(call)
                        } else {
                            // 오류 응답 처리
                            val errorBodyStr = response.errorBody()?.string()
                            Log.e(
                                "RecipeAllListViewModel",
                                "Response error: Code: ${response.code()}, Message: ${response.message()}, Error Body: $errorBodyStr"
                            )
                        }
                    }
                    _isLoading.postValue(false) // LiveData 업데이트
                }

                // 응답 실패
                override fun onFailure(
                    call: Call<PagingResponseDto<RecipeMainListResponseDto>>,
                    t: Throwable
                ) {
                    Log.e("RecipeAllListViewModel", "Failed to load items: ${t.message}", t)
                    _isLoading.postValue(false) // 로딩 상태 업데이트
                    _loadFailed.postValue(true) // 실패 상태 업데이트
                }
            })
    }

    // failedCall은 getAllRecipeListService.getAllRecipeList에 의해 생성된 Call 객체입니다.
    private fun handleUnauthorizedError(
        failedCall: Call<PagingResponseDto<RecipeMainListResponseDto>>
    ) {
        val tokenRepublishManager = TokenRepublishManager(tokenManager)
        // 토큰을 새롭게 발급받고 새로운 요청을 보낸다.
        tokenRepublishManager.renewTokenIfNeeded(
            onTokenRenewed = { newToken ->
                retryRequestWithNewToken(failedCall, newToken)
            },
            onRenewalFailed = { navigateToLogin ->
                if (navigateToLogin) {
                    redirectToLogin() // 로그인 화면으로 이동하는 상태 업데이트
                }
            }
        )
    }

    // 새로운 토큰을 담아서 다시 요청을 보냄
    private fun retryRequestWithNewToken(
        failedCall: Call<PagingResponseDto<RecipeMainListResponseDto>>,
        newAccessToken: String
    ) {
        // 새 토큰을 사용하여 요청 재시도
        getAllRecipeListService.getAllRecipeList(
            currentRequestPage,
            currentRequestSize,
            currentRequestSortType
        )
            .enqueue(object : Callback<PagingResponseDto<RecipeMainListResponseDto>> {
                override fun onResponse(
                    call: Call<PagingResponseDto<RecipeMainListResponseDto>>,
                    response: Response<PagingResponseDto<RecipeMainListResponseDto>>
                ) {
                    if (response.isSuccessful) {
                        // 성공적으로 데이터를 받아왔을 때의 처리
                        val newItems = response.body()?.content ?: emptyList()
                        val currentItems = _items.value ?: emptyList()
                        _items.postValue(currentItems + newItems)
                        isLastPage = newItems.size < currentRequestSize
                        currentRequestPage++
                    } else {
                        // 오류 응답 처리
                        val errorBodyStr = response.errorBody()?.string()
                        Log.e(
                            "RecipeAllListViewModel",
                            "Retry Response error: Code: ${response.code()}, Message: ${response.message()}, Error Body: $errorBodyStr"
                        )
                    }
                }

                override fun onFailure(
                    call: Call<PagingResponseDto<RecipeMainListResponseDto>>,
                    t: Throwable
                ) {
                    // 네트워크 오류나 기타 문제로 요청 실패 시 처리
                    Log.e("RecipeAllListViewModel", "Retry Failed to load items: ${t.message}", t)
                }
            })
    }


}