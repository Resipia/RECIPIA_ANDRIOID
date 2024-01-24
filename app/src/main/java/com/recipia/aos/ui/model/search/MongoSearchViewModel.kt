package com.recipia.aos.ui.model.search

import TokenManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipia.aos.ui.api.MongoSearchService
import com.recipia.aos.ui.dto.search.SearchType
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
    private val _searchType = MutableStateFlow(SearchType.HASHTAG)
    val searchType = _searchType.asStateFlow()

    // 선택된 재료 결과를 저장하는 상태
    private val _selectedIngredientResults = MutableStateFlow<List<String>>(emptyList())
    val selectedIngredientResults = _selectedIngredientResults.asStateFlow()

    // 선택된 해시태그 결과를 저장하는 상태
    private val _selectedHashTagResults = MutableStateFlow<List<String>>(emptyList())
    val selectedHashTagResults = _selectedHashTagResults.asStateFlow()

    // 검색 텍스트
    private val _searchText = MutableStateFlow("")
    private val _searchResults = MutableStateFlow<List<String>>(emptyList())

    val searchText = _searchText.asStateFlow()
    val searchResults = _searchResults.asStateFlow()

    private val _mongoSearchResults = MutableStateFlow<List<String>>(emptyList())
    val mongoSearchResults = _mongoSearchResults.asStateFlow()

    // 선택된 검색 결과들을 저장하는 리스트
    private val _selectedSearchResults = MutableStateFlow<List<String>>(emptyList())
    val selectedSearchResults = _selectedSearchResults.asStateFlow()

    private val _showSearchResults = MutableStateFlow(true)
    val showSearchResults = _showSearchResults.asStateFlow()

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
            .baseUrl("http://10.0.2.2:8082/") // 레시피 서버에 요청
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(MongoSearchService::class.java)
    }

    @OptIn(FlowPreview::class)
    fun init(type: SearchType) {
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

    fun parseMongoSearchResult(jsonString: String, type: SearchType): String? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val key = when (type) {
                SearchType.INGREDIENT -> "ingredients"
                SearchType.HASHTAG -> "hashtags"
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

    fun onSearchTextChange(text: String) {
        _searchText.value = text
        _showSearchResults.value = text.isNotEmpty() // 검색어가 있을 때만 결과 표시
    }

    // 사용자가 선택 완료 버튼을 눌렀을 때 선택된 해시태그를 세팅하는 함수
    fun setSelectedHashtags(selectedHashtags: List<String>) {
        // 선택된 해시태그들을 처리하는 로직
        // 예: 데이터 저장소에 저장, 다른 ViewModel에 전달 등
        // 이 예제에서는 로그로 출력
        selectedHashtags.forEach { hashtag ->
            println("선택된 해시태그: $hashtag")
        }
        // 선택된 해시태그들을 _selectedSearchResults 상태에 저장
        _selectedSearchResults.value = selectedHashtags
    }

    // 검색 유형 변경 함수
    fun changeSearchType(newType: SearchType) {
        _searchType.value = newType
    }

    // 검색 결과 추가 함수 (재료 또는 해시태그에 따라 다른 상태 업데이트)
    fun addSearchResult(result: String) {
        when(_searchType.value) {
            SearchType.INGREDIENT -> _selectedIngredientResults.value = _selectedIngredientResults.value + result
            SearchType.HASHTAG -> _selectedHashTagResults.value = _selectedHashTagResults.value + result
            else -> {}
        }
    }


}