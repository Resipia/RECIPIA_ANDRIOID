package com.recipia.aos.ui.model.search

import TokenManager
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipia.aos.BuildConfig
import com.recipia.aos.ui.api.recipe.search.MongoSearchService
import com.recipia.aos.ui.api.dto.search.SearchType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 재료, 해시태그 mongoDB 검색 모델
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MongoSearchViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    // 검색 유형 상태 (재료 또는 해시태그)
    private val _searchType = MutableStateFlow(com.recipia.aos.ui.api.dto.search.SearchType.HASHTAG)
    val searchType = _searchType.asStateFlow()

    // 검색 텍스트
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    // 몽고DB 검색 결과
    private val _mongoSearchResults = MutableStateFlow<List<String>>(emptyList())
    val mongoSearchResults = _mongoSearchResults.asStateFlow()

    // 검색어 결과 상태
    private val _showSearchResults = MutableStateFlow(true)
    val showSearchResults = _showSearchResults.asStateFlow()

    // 사용자가 선택한 재료를 저장하는 상태
    private val _selectedIngredients = MutableStateFlow<List<String>>(emptyList())
    val selectedIngredients = _selectedIngredients.asStateFlow()

    // 사용자가 선택한 해시태그를 저장하는 상태
    private val _selectedHashtags = MutableStateFlow<List<String>>(emptyList())
    val selectedHashtags = _selectedHashtags.asStateFlow()

    // retrofit 설정
    val mongoSearchService: MongoSearchService by lazy {
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
            .baseUrl(BuildConfig.RECIPE_SERVER_URL) // 레시피 서버에 요청
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(MongoSearchService::class.java)
    }

    // 검색어를 서버에 요청하는 메서드
    @OptIn(FlowPreview::class)
    fun init(type: com.recipia.aos.ui.api.dto.search.SearchType) {
        _searchText
            .debounce(200L) // 검색어 입력 후 500ms 대기
            .filter { it.isNotBlank() }
            .flatMapLatest { text ->
                flow {
                    val response = mongoSearchService.search(
                        type, text, 10 // type 매개변수를 사용하여 검색 종류 설정
                    )
                    if (response.isSuccessful) {
                        val searchResponseDto = response.body()?.result
                        val searchResults = searchResponseDto
                            ?.flatMap { it.searchResultList }
                            ?.mapNotNull { parseMongoSearchResult(it, type) }
                        emit(searchResults ?: emptyList())
                    }
                }
            }
            .onEach { _mongoSearchResults.value = it }
            .launchIn(viewModelScope)
    }

    // 재료, 해시태그에 따라 Json 파싱 실시
    fun parseMongoSearchResult(
        jsonString: String,
        type: com.recipia.aos.ui.api.dto.search.SearchType
    ): String? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val key = when (type) {
                com.recipia.aos.ui.api.dto.search.SearchType.INGREDIENT -> "ingredients"
                com.recipia.aos.ui.api.dto.search.SearchType.HASHTAG -> "hashtags"
                else -> return null // 다른 타입이면 null 반환 또는 예외 처리
            }
            jsonObject.optString(key, null) // 'null'은 기본값으로, 키가 없을 경우 반환될 값임
        } catch (e: JSONException) {
            null // JSON 파싱 실패 시 null 반환
        }
    }

    // 입력 텍스트 초기화
    fun resetTextChange() {
        _searchText.value = ""
        _mongoSearchResults.value = emptyList()
    }

    // 검색어 텍스트가 바뀔때마다 동작
    fun onSearchTextChange(text: String) {
        _searchText.value = text
        _showSearchResults.value = text.isNotEmpty() // 검색어가 있을 때만 결과 표시
    }

    // 사용자가 선택 완료 버튼을 눌렀을 때 선택된 해시태그를 세팅하는 함수
    fun setSelectedHashtags(selectedHashtags: List<String>) {
        _selectedHashtags.value = selectedHashtags
    }

    // 사용자가 선택 완료 버튼을 눌렀을 때 선택된 재료를 세팅하는 함수
    fun setSelectedIngredients(selectedIngredients: List<String>) {
        _selectedIngredients.value = selectedIngredients
    }

    // 재료 결과값 리셋
    fun resetSelectedIngredients() {
        _selectedIngredients.value = emptyList()
    }

    // 해시태그 결과값 리셋
    fun resetSelectedHashtags() {
        _selectedHashtags.value = emptyList()
    }

    // 연관 검색어 목록을 초기화하는 함수
    fun clearMongoSearchResults() {
        _mongoSearchResults.value = emptyList()
    }

    // 초기화 상태 플래그
    var isInitialized = false

    // 업데이트 페이지에서 뒤로가면 초기 상태 초기화하기
    fun changeInitialized() {
        isInitialized = false
    }

    // 재료와 해시태그 초기화 메소드 (레시피 수정에서 사용)
    fun initializeSelectedIngredientsAndHashtags(
        initialIngredients: List<String>,
        initialHashtags: List<String>
    ) {
        if (!isInitialized) {
            // 초기화 로직
            _selectedIngredients.value = initialIngredients.toMutableStateList()
            _selectedHashtags.value = initialHashtags.toMutableStateList()
            isInitialized = true
        }
    }

    // 재료 삭제 메서드
    fun removeSelectedIngredient(ingredient: String) {
        val updatedIngredients = _selectedIngredients.value.toMutableList()
        updatedIngredients.remove(ingredient)
        _selectedIngredients.value = updatedIngredients
    }

    // 해시태그 삭제 메서드
    fun removeSelectedHashtag(hashtag: String) {
        val updatedHashtags = _selectedHashtags.value.toMutableList()
        updatedHashtags.remove(hashtag)
        _selectedHashtags.value = updatedHashtags
    }


}