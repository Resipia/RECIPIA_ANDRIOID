package com.recipia.aos.ui.api.recipe

import com.recipia.aos.ui.dto.PagingResponseDto
import com.recipia.aos.ui.dto.RecipeListResponseDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 모든 레시피 리스트로 받기
 */
interface RecipeListService {

    // 레시피 정보 리스트로 받기
    @GET("/recipe/getAllRecipeList")
    fun getAllRecipeList(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortType") sortType: String,
        @Query("subCategoryList") subCategoryList: List<Long>
    ): Call<PagingResponseDto<RecipeListResponseDto>>

}