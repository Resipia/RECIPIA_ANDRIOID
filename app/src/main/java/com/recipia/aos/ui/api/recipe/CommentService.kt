package com.recipia.aos.ui.api.recipe

import com.recipia.aos.ui.dto.PagingResponseDto
import com.recipia.aos.ui.dto.ResponseDto
import com.recipia.aos.ui.dto.comment.CommentDeleteRequestDto
import com.recipia.aos.ui.dto.comment.CommentListResponseDto
import com.recipia.aos.ui.dto.comment.CommentRegistRequestDto
import com.recipia.aos.ui.dto.comment.CommentUpdateRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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

    // 댓글 등록
    @POST("/recipe/regist/comment")
    suspend fun registComment(
        @Body requestDto: CommentRegistRequestDto
    ): Response<ResponseDto<Long>>

    // 댓글 수정
    @POST("/recipe/update/comment")
    suspend fun updateComment(
        @Body requestDto: CommentUpdateRequestDto
    ): Response<ResponseDto<Void>>

    // 댓글 삭제
    @POST("/recipe/delete/comment")
    suspend fun deleteComment(
        @Body requestDto: CommentDeleteRequestDto
    ): Response<ResponseDto<Void>>

}