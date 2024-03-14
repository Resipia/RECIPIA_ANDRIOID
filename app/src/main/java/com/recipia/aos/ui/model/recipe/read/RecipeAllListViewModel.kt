package com.recipia.aos.ui.model.recipe.read

import TokenManager
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipia.aos.BuildConfig
import com.recipia.aos.ui.api.recipe.RecipeListService
import com.recipia.aos.ui.api.dto.PagingResponseDto
import com.recipia.aos.ui.api.dto.RecipeListResponseDto
import com.recipia.aos.ui.model.jwt.TokenRepublishManager
import kotlinx.coroutines.launch
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

    // api요청으로 받은 응답 데이터 리스트 (메인)
    var items = mutableStateOf<List<com.recipia.aos.ui.api.dto.RecipeListResponseDto>>(listOf())
        private set // 이렇게 하면 외부에서는 읽기만 가능해짐

    // 현재 페이지, 사이즈, 정렬 유형 저장
    var currentRequestPage: Int = 0
    var currentRequestSize: Int = 10
    var currentRequestSortType: String = "new"
    var isLastPage = false
    var isLastSearchPage = false

    // 검색어 상태
    var searchText = mutableStateOf("")
    // api요청으로 받은 응답 데이터 리스트 (검색)
    var searchResults = mutableStateOf<List<com.recipia.aos.ui.api.dto.RecipeListResponseDto>>(listOf())
        private set // 이렇게 하면 외부에서는 읽기만 가능해짐

    val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val _isSearchLoading = MutableLiveData<Boolean>()
    val isSearchLoading: LiveData<Boolean> = _isSearchLoading

    val _loadFailed = MutableLiveData<Boolean>(false)
    val loadFailed: LiveData<Boolean> = _loadFailed

    // 로그인 화면으로 이동해야 함을 알린다.
    val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> = _navigateToLogin

    // 모든 레시피 리스트를 호출하는 서비스 선언
    val recipeListService: RecipeListService by lazy {
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
            .baseUrl(BuildConfig.RECIPE_SERVER_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecipeListService::class.java)
    }

    // 선택된 서브 카테고리를 저장하는 변수
    var selectedSubCategories = mutableStateOf<List<Long>>(emptyList())

    // 선택된 서브 카테고리에 따라 데이터를 요청하는 메서드
    fun loadItemsWithSelectedSubCategories() {
        currentRequestPage = 0
        isLastPage = false
        items.value = emptyList() // 기존 데이터를 초기화
        loadMoreItems(selectedSubCategories.value)
    }

    // 검색할때 이 함수를 호출해서 서브 카테고리값을 저장한다.
    fun setSubCategories(
        subCategoryList: List<Long>
    ) {
        selectedSubCategories.value = subCategoryList
    }

    // 서브 카테고리 초기화
    fun makeEmptyListSubCategoryData() {
        selectedSubCategories.value = emptyList()
    }

    fun resetLoadFailed() {
        _loadFailed.value = false
    }

    // 데이터를 새로고침하는 메서드
    fun refreshItems(
        subCategoryList: List<Long>
    ) {
        currentRequestPage = 0 // 페이지를 초기화
        isLastPage = false
        items.value = emptyList() // 기존 데이터를 초기화
        loadMoreItems(subCategoryList) // 첫 페이지부터 다시 로딩
    }

    // 더 많은 아이템을 요청하는 메서드
    fun loadMoreItems(
        subCategoryList: List<Long>
    ) {
        Log.d("RecipeAllListViewModel", "Loading more items")
        if (_isLoading.value == true || isLastPage) return

        _isLoading.value = true
        loadItemsFromServer(
            currentRequestPage,
            currentRequestSize,
            currentRequestSortType,
            subCategoryList
        )
        Log.d("RecipeAllListViewModel", "Loading finished")
    }

    // 더 많은 검색결과 아이템을 요청하는 메서드
    fun loadMoreSearchItems(
        searchText: String
    ) {
        Log.d("RecipeAllListViewModel", "Loading more items")
        if (isSearchLoading.value == true || isLastSearchPage) return

        _isSearchLoading.value = true
        loadItemsFromServerForSearch(searchText)
        Log.d("RecipeAllListViewModel", "Loading finished")
    }

    // 코루틴을 사용하여 서버로부터 데이터를 비동기적으로 가져오는 함수
    private fun loadItemsFromServer(
        page: Int,
        size: Int,
        sortType: String,
        subCategoryList: List<Long>?,
        searchWord: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = recipeListService.getAllRecipeList(
                    page, size, sortType, subCategoryList, searchWord
                )

                if (response.isSuccessful) {
                    // 성공적인 응답 처리
                    val responseData = response.body()
                    items.value += responseData?.content ?: emptyList()
                    isLastPage = responseData?.content?.size!! < size
                    currentRequestPage++
                } else if (response.code() == 401) {
                    // 401 Unauthorized 오류 처리
                    handleUnauthorizedError(subCategoryList, searchWord)
                } else {
                    // 다른 오류 응답 처리
                    Log.e("ViewModel", "Error in API call: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                // 예외 처리
                Log.e("ViewModel", "Exception in API call", e)
                _loadFailed.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 401 Unauthorized 에러 처리 및 토큰 재발급 로직
    private fun handleUnauthorizedError(
        subCategoryList: List<Long>?,
        searchWord: String?
    ) {
        viewModelScope.launch {
            val result = TokenRepublishManager(tokenManager).renewTokenIfNeeded()
            if (result) {
                loadItemsFromServer(
                    currentRequestPage,
                    currentRequestSize,
                    currentRequestSortType,
                    subCategoryList,
                    searchWord
                )
            } else {
                _navigateToLogin.value = true
            }
        }
    }

    // 검색 기능에서 401 Unauthorized 오류 처리 및 토큰 재발급 로직
    private fun handleUnauthorizedErrorForSearch(searchText: String) {
        viewModelScope.launch {
            val result = TokenRepublishManager(tokenManager).renewTokenIfNeeded()
            if (result) {
                // 토큰 재발급에 성공했다면, 검색을 다시 시도합니다.
                loadItemsFromServerForSearch(searchText)
            } else {
                // 토큰 재발급에 실패했다면, 로그인 화면으로 이동합니다.
                _navigateToLogin.value = true
            }
        }
    }

    // 검색 결과를 불러오는 함수
    fun searchRecipes(searchText: String) {
        currentRequestPage = 0 // 검색 시 항상 첫 페이지부터 시작
        currentRequestSize = 10 // 한 페이지에 표시할 아이템 수
        currentRequestSortType = "new" // 정렬 유형
        isLastSearchPage = false // 마지막 페이지 여부 초기화
        searchResults.value = emptyList() // 기존 검색 결과 초기화

        loadItemsFromServerForSearch(searchText)
    }

    // 코루틴을 사용하여 서버로부터 데이터를 비동기적으로 가져오는 함수
    fun loadItemsFromServerForSearch(
        searchText: String
    ) {
        viewModelScope.launch {
            _isSearchLoading.value = true
            try {
                val response = recipeListService.getAllRecipeList(
                    page = currentRequestPage,
                    size = currentRequestSize,
                    sortType = currentRequestSortType,
                    subCategoryList = null, // 검색에서는 사용하지 않으므로 null 전달
                    searchWord = searchText
                )

                if (response.isSuccessful) {
                    // 성공적인 응답 처리
                    val responseData = response.body()
                    searchResults.value += responseData?.content ?: emptyList()
                    isLastSearchPage = responseData?.content?.size!! < currentRequestSize
                    currentRequestPage++
                } else if (response.code() == 401) {
                    // 401 Unauthorized 오류 처리
                    handleUnauthorizedErrorForSearch(searchText)
                } else {
                    // 에러 응답 처리
                    Log.e("ViewModel", "Error in API call: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                // 예외 처리
                Log.e("ViewModel", "Exception in API call", e)
                _loadFailed.value = true
            } finally {
                _isSearchLoading.value = false
            }
        }
    }

    // RecipeAllListViewModel 내에 아이템 업데이트 메서드
    fun updateItemBookmarkId(recipeId: Long, bookmarkId: Long?) {
        items.value = items.value.map { item ->
            if (item.id == recipeId) {
                item.copy(bookmarkId = bookmarkId)
            } else {
                item
            }
        }
    }

    // 홈 화면 이동 초기화
    fun resetNavigateToLogin() {
        _navigateToLogin.value = false
    }

}