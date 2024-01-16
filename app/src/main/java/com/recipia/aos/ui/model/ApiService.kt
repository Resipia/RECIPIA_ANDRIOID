package com.recipia.aos.ui.model

import com.recipia.aos.ui.dto.PagingResponseDto
import com.recipia.aos.ui.dto.RecipeMainListResponseDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/recipe/getAllRecipeList")
    fun getAllRecipeList(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortType") sortType: String
    ): Call<PagingResponseDto<RecipeMainListResponseDto>>
}