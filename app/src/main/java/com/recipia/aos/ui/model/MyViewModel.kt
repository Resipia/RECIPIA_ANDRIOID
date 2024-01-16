package com.recipia.aos.ui.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.recipia.aos.ui.dto.PagingResponseDto
import com.recipia.aos.ui.dto.RecipeMainListResponseDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyViewModel : ViewModel() {
    private val _items = MutableLiveData<List<RecipeMainListResponseDto>>()
    val items: LiveData<List<RecipeMainListResponseDto>> = _items

    private var currentPage = 0
    private val pageSize = 5
    private val sortType = "new"
    var isLastPage = false

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loadFailed = MutableLiveData<Boolean>(false)
    val loadFailed: LiveData<Boolean> = _loadFailed

    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8082/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    init {
        loadMoreItems() // 이 위치에서 apiService가 처음 사용될 때 초기화됨
    }

    fun resetLoadFailed() {
        _loadFailed.value = false
    }

    fun loadMoreItems() {
        if (_isLoading.value == true || isLastPage) return

        _isLoading.value = true
        loadItemsFromServer(currentPage, pageSize, sortType) // 서버로부터 데이터 가져오기
    }

    // 서버로부터 데이터를 가져오는 함수 예시
    private fun loadItemsFromServer(page: Int, size: Int, sortType: String) {
        apiService.getAllRecipeList(page, size, sortType)
            .enqueue(object : Callback<PagingResponseDto<RecipeMainListResponseDto>> {

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
                }
                _isLoading.postValue(false) // LiveData 업데이트
            }

            override fun onFailure(call: Call<PagingResponseDto<RecipeMainListResponseDto>>, t: Throwable) {
                _isLoading.postValue(false) // LiveData 업데이트
                _loadFailed.postValue(true) // 실패 상태 업데이트
            }
        })
    }


}