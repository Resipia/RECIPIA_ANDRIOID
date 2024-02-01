package com.recipia.aos.ui.api.recipe

import com.recipia.aos.ui.dto.PagingResponseDto
import com.recipia.aos.ui.dto.RecipeListResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 모든 레시피 리스트로 받기
 */
interface RecipeListService {

    @GET("/recipe/getAllRecipeList")
    suspend fun getAllRecipeList(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortType") sortType: String,
        @Query("subCategoryList") subCategoryList: List<Long>?,
        @Query("searchWord") searchWord: String?
    ): Response<PagingResponseDto<RecipeListResponseDto>>

}