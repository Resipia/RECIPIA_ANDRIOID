package com.recipia.aos.ui.api

import com.recipia.aos.ui.dto.ResponseDto
import com.recipia.aos.ui.dto.recipe.detail.RecipeDetailViewResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeApiService {

    @GET("/recipe/getRecipeDetail")
    suspend fun getRecipeDetailView(
        @Query("recipeId") recipeId: Long
    ): Response<ResponseDto<RecipeDetailViewResponseDto>>

}