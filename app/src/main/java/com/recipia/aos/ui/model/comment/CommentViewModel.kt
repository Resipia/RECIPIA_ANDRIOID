package com.recipia.aos.ui.model.comment

import TokenManager
import androidx.lifecycle.ViewModel
import com.recipia.aos.ui.api.recipe.CommentService
import com.recipia.aos.ui.dto.PagingResponseDto
import com.recipia.aos.ui.dto.comment.CommentListResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 댓글 view 모델
 */
class CommentViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    // 댓글 데이터를 저장할 StateFlow
    private val _comments = MutableStateFlow<PagingResponseDto<CommentListResponseDto>?>(null)
    val comments = _comments.asStateFlow()

    private var currentPage = 0
    private var isCommentsLoading = false
    private var initialLoadDone = false
    var isLastPage = false

    // retrofit 설정
    private val commentService: CommentService by lazy {
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
            .baseUrl("http://10.0.2.2:8082/") // 레시피 서버
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(CommentService::class.java)
    }

    // 첫 페이지 댓글 불러오기
    suspend fun loadInitialComments(recipeId: Long) {
        if (!initialLoadDone) {
            getAllComments(recipeId, currentPage)
            currentPage++ // 첫 페이지 로드 후 페이지 번호 증가
            initialLoadDone = true
        }
    }

    // 댓글 콘텐츠를 불러온다.
    suspend fun getAllComments(
        recipeId: Long,
        currentPage: Int,
        size: Int = 10,
        sortType: String = "new"
    ): PagingResponseDto<CommentListResponseDto>? {
        val response = commentService.getAllCommentList(recipeId, currentPage, size, sortType)
        return if (response.isSuccessful) {
            if (currentPage == 0) {
                _comments.value = response.body()
            }
            response.body()
        } else {
            _comments.value = null
            null
        }
    }

    // 추가 페이지 댓글 불러오기
    suspend fun loadMoreComments(recipeId: Long) {

        if (!isCommentsLoading && !isLastPage) {

            isCommentsLoading = true
            val newComments = getAllComments(recipeId, currentPage)

            newComments?.let { newCommentList ->
                if (newCommentList.content.isNotEmpty()) {

                    val existingComments = _comments.value?.content ?: listOf()
                    val updatedComments = existingComments + newCommentList.content

                    _comments.value = PagingResponseDto(
                        content = updatedComments,
                        totalCount = newCommentList.totalCount
                    )
                    currentPage++ // 새 페이지 로드 후 페이지 번호 증가
                }
                isCommentsLoading = false
            } ?: run {
                isCommentsLoading = false
            }
        }
    }

    // 댓글 목록 초기화 함수
    fun clearComments() {
        _comments.value = null
        currentPage = 0
        initialLoadDone = false
    }

}