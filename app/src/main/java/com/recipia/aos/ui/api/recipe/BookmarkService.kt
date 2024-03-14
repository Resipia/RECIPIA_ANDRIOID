package com.recipia.aos.ui.api.recipe

import com.recipia.aos.ui.api.dto.ResponseDto
import com.recipia.aos.ui.api.dto.recipe.BookmarkRequestDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface BookmarkService {

    // 북마크 추가
    @POST("/recipe/addBookmark")
    fun addBookmark(
        @Body request: com.recipia.aos.ui.api.dto.recipe.BookmarkRequestDto
    ): Call<com.recipia.aos.ui.api.dto.ResponseDto<Long>>

    // 북마크 삭제
    @POST("/recipe/removeBookmark")
    fun removeBookmark(
        @Query("bookmarkId") bookmarkId: Long
    ): Call<com.recipia.aos.ui.api.dto.ResponseDto<Void>>
}
