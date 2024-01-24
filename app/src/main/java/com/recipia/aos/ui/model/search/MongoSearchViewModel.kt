package com.recipia.aos.ui.model.search

import TokenManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipia.aos.ui.api.MongoSearchService
import com.recipia.aos.ui.dto.search.SearchType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    init {
        _searchText
//            .debounce(00L) // 검색어 입력 후 500ms 대기
            .filter { it.isNotBlank() }
            .flatMapLatest { text ->
                flow {
                    val response = mongoSearchService.search(
                        SearchType.HASHTAG, text, 10
                    )
                    if (response.isSuccessful) {
                        val searchResponseDto = response.body()?.result
                        val hashtags = searchResponseDto
                            ?.flatMap { it.searchResultList }
                            ?.mapNotNull { parseMongoSearchResult(it) } // JSON 문자열에서 'hashtags' 값만 추출
                        emit(hashtags ?: emptyList())
                    }
                }
            }
            .onEach { _mongoSearchResults.value = it }
            .launchIn(viewModelScope)
    }

    fun parseMongoSearchResult(jsonString: String): String? {
        return try {
            val jsonObject = JSONObject(jsonString)
            jsonObject.optString("hashtags", null) // 'null'은 기본값으로, 'hashtags' 키가 없을 경우 반환될 값임
        } catch (e: JSONException) {
            null // JSON 파싱 실패 시 null 반환
        }
    }

    fun onSearchTextChange(text: String) {
//        if (text.isBlank()) {
//            _mongoSearchResults.value = emptyList() // 검색어가 비어있으면 검색 결과를 비움
//        }
        _searchText.value = text
        _showSearchResults.value = text.isNotEmpty() // 검색어가 있을 때만 결과 표시
    }

    fun onSearchResultClick(searchWord: String) {
        _selectedSearchResults.value = _selectedSearchResults.value + searchWord
        _showSearchResults.value = false // 검색 결과 숨기기
        _searchText.value = "" // 검색창 입력값 초기화
    }


}