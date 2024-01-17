package com.recipia.aos.ui.model

import JwtTokenManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.recipia.aos.ui.api.GetAllRecipeListService
import com.recipia.aos.ui.dto.PagingResponseDto
import com.recipia.aos.ui.dto.RecipeMainListResponseDto
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.log

class RecipeAllListViewModel(
    private val jwtTokenManager: JwtTokenManager
) : ViewModel() {

    private val _items = MutableLiveData<List<RecipeMainListResponseDto>>()
    val items: LiveData<List<RecipeMainListResponseDto>> = _items

    private var currentPage = 0
    private val pageSize = 10
    private val sortType = "new"
    var isLastPage = false

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loadFailed = MutableLiveData<Boolean>(false)
    val loadFailed: LiveData<Boolean> = _loadFailed


    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            // JwtTokenManager를 사용하여 토큰을 요청 헤더에 추가
            val request = jwtTokenManager.addAccessTokenToHeader(chain)
            chain.proceed(request)
        }
        .build()

    val getAllRecipeListService: GetAllRecipeListService by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8082/") // 서버의 base URL을 설정하세요.
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GetAllRecipeListService::class.java)
    }


    fun resetLoadFailed() {
        _loadFailed.value = false
    }

    fun loadMoreItems() {
        if (_isLoading.value == true || isLastPage) return

        _isLoading.value = true
        loadItemsFromServer(currentPage, pageSize, sortType)
    }

    // 서버로부터 데이터를 가져오는 함수 예시
    private fun loadItemsFromServer(page: Int, size: Int, sortType: String) {
        getAllRecipeListService.getAllRecipeList(page, size, sortType)
            .enqueue(object : Callback<PagingResponseDto<RecipeMainListResponseDto>> {

                // 응답 성공
                override fun onResponse(
                    call: Call<PagingResponseDto<RecipeMainListResponseDto>>,
                    response: Response<PagingResponseDto<RecipeMainListResponseDto>>
                ) {
                    if (response.isSuccessful) {
                        // 성공적인 응답 로그
                        Log.d("MyViewModel", "Response received: ${response.body()}")

                        val newItems = response.body()?.content ?: emptyList()
                        val currentItems = _items.value ?: emptyList()
                        _items.postValue(currentItems + newItems)
                        isLastPage = newItems.size < pageSize
                        currentPage++ // 현재 페이지 업데이트
                        // todo: 여기서 ++해줘서 그런듯
                        Log.d("Count", "content count received: ${response.body()?.content?.size}")
                    }
                    _isLoading.postValue(false) // LiveData 업데이트
                }

                // 응답 실패
                override fun onFailure(
                    call: Call<PagingResponseDto<RecipeMainListResponseDto>>,
                    t: Throwable
                ) {
                    _isLoading.postValue(false) // 로딩 상태 업데이트
                    _loadFailed.postValue(true) // 실패 상태 업데이트

                    // 서버로부터의 오류 로그를 남깁니다.
                    Log.e("MyViewModel", "Failed to load items: ${t.message}", t)
                }
            })
    }


}