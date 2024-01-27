package com.recipia.aos.ui.api.recipe

import com.recipia.aos.ui.dto.ResponseDto
import com.recipia.aos.ui.dto.recipe.detail.RecipeDetailViewResponseDto
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 레시피 상세정보 관련 api
 */
interface RecipeDetailAndDeleteService {

    // 레시피 상세정보 조회
    @GET("/recipe/getRecipeDetail")
    suspend fun getRecipeDetailView(
        @Query("recipeId") recipeId: Long
    ): Response<ResponseDto<RecipeDetailViewResponseDto>>

    // 레시피 삭제
    @DELETE("/recipe/deleteRecipe")
    suspend fun deleteRecipe(
        @Query("recipeId") recipeId: Long
    ): Response<ResponseDto<Void>>

}