package com.recipia.aos.ui.api.recipe.like

import com.recipia.aos.ui.dto.ResponseDto
import com.recipia.aos.ui.dto.like.RecipeLikeRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 좋아요 retrofit 서비스
 */
interface RecipeLikeService {

    // 좋아요 추가/삭제
    @POST("/recipe/like")
    suspend fun recipeLike(
        @Body dto: RecipeLikeRequestDto
    ) :Response<ResponseDto<Long>>

}