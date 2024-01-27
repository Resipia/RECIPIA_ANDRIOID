package com.recipia.aos.ui.api.recipe

import com.recipia.aos.ui.dto.ResponseDto
import com.recipia.aos.ui.dto.recipe.BookmarkRequestDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface BookmarkService {

    // 북마크 추가
    @POST("/recipe/addBookmark")
    fun addBookmark(@Body request: BookmarkRequestDto): Call<ResponseDto<Long>>

    // 북마크 삭제
    @POST("/recipe/removeBookmark")
    fun removeBookmark(@Query("bookmarkId") bookmarkId: Long): Call<ResponseDto<Void>>
}
