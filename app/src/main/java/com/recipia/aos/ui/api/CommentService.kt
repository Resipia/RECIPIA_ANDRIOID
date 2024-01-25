package com.recipia.aos.ui.api

import com.recipia.aos.ui.dto.PagingResponseDto
import com.recipia.aos.ui.dto.comment.CommentListResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 댓글 서비스
 */
interface CommentService {

    // 모든 댓글 리스트를 불러온다. (페이징으로)
    @GET("/recipe/getAllCommentList")
    suspend fun getAllCommentList(
        @Query("recipeId") recipeId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortType") sortType: String
    ): Response<PagingResponseDto<CommentListResponseDto>>

}