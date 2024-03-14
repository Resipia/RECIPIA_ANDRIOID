package com.recipia.aos.ui.api.recipe

import com.recipia.aos.ui.api.dto.ResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap


/**
 * 레시피 생성, 수정
 */
interface RecipeCreateAndUpdateService {

    // 레시피 생성하기
    @Multipart
    @POST("/recipe/createRecipe")
    fun createRecipe(
        @Part("recipeName") recipeName: RequestBody,
        @Part("recipeDesc") recipeDesc: RequestBody,
        @Part("timeTaken") timeTaken: RequestBody,
        @Part("ingredient") ingredient: RequestBody,
        @Part("hashtag") hashtag: RequestBody,
        @Part("nutritionalInfo.carbohydrates") carbohydrates: RequestBody,
        @Part("nutritionalInfo.fat") fat: RequestBody,
        @Part("nutritionalInfo.minerals") minerals: RequestBody,
        @Part("nutritionalInfo.protein") protein: RequestBody,
        @Part("nutritionalInfo.vitamins") vitamins: RequestBody,
        @Part("subCategoryDtoList[0].id") subCategoryId: RequestBody,
        @Part fileList: List<MultipartBody.Part>
    ): Call<com.recipia.aos.ui.api.dto.ResponseDto<Long>>

    // 레시피 수정하기
    @Multipart
    @PUT("/recipe/updateRecipe")
    fun updateRecipe(
        @Part("id") recipeId: RequestBody,
        @Part("recipeName") recipeName: RequestBody,
        @Part("recipeDesc") recipeDesc: RequestBody,
        @Part("timeTaken") timeTaken: RequestBody,
        @Part("ingredient") ingredient: RequestBody,
        @Part("hashtag") hashtag: RequestBody,
        @Part("nutritionalInfo.id") nutritionalInfoId: RequestBody,
        @Part("nutritionalInfo.carbohydrates") carbohydrates: RequestBody,
        @Part("nutritionalInfo.fat") fat: RequestBody,
        @Part("nutritionalInfo.minerals") minerals: RequestBody,
        @Part("nutritionalInfo.protein") protein: RequestBody,
        @Part("nutritionalInfo.vitamins") vitamins: RequestBody,
        @PartMap subCategoryParts: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part fileList: List<MultipartBody.Part>
    ): Call<com.recipia.aos.ui.api.dto.ResponseDto<Void>>

}