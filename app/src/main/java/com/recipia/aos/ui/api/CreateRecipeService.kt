package com.recipia.aos.ui.api

import com.recipia.aos.ui.dto.ResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


/**
 * 레시피 생성
 */
interface CreateRecipeService {

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
    ): Call<ResponseDto<Long>>
}