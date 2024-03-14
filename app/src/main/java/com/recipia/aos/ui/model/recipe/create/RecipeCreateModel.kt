package com.recipia.aos.ui.model.recipe.create

import TokenManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.recipia.aos.BuildConfig
import com.recipia.aos.ui.api.recipe.RecipeCreateAndUpdateService
import com.recipia.aos.ui.api.dto.ResponseDto
import com.recipia.aos.ui.api.dto.recipe.NutritionalInfoDto
import com.recipia.aos.ui.api.dto.recipe.RecipeCreateUpdateRequestDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * 레시피 생성할 때 사용하는 모델 객체
 */
class RecipeCreateModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    // 레시피 생성 화면에서 사용되는 데이터
    var recipeName = mutableStateOf("")
    var recipeDesc = mutableStateOf("")
    var timeTaken = mutableStateOf("")
    var ingredient = mutableStateOf("")
    var hashtag = mutableStateOf("")
    var nutritionalInfoList = mutableStateListOf<com.recipia.aos.ui.api.dto.recipe.NutritionalInfoDto>()
    var selectedImageUris = mutableStateListOf<Uri?>()


    private val recipeService: RecipeCreateAndUpdateService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = tokenManager.addAccessTokenToHeader(chain)
                chain.proceed(request)
            }
            .build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.RECIPE_SERVER_URL) // 서버 주소 확인 필요
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(RecipeCreateAndUpdateService::class.java)
    }

    // 레시피 생성하는 요청을 서버로 보낸다.
    fun createRecipeRequest(
        requestDto: com.recipia.aos.ui.api.dto.recipe.RecipeCreateUpdateRequestDto,
        imageUris: List<Uri?>,
        context: Context,
        onSuccess: (Long) -> Unit, // Long 타입의 recipeId를 인자로 받는 콜백
        onError: (String) -> Unit
    ) {
        val imageParts = imageUris.mapNotNull { uri ->
            uri?.let { nonNullableUri ->
                uriToMultipartBodyPart(nonNullableUri, context)
            }
        }

        val recipeName = requestDto.recipeName.toRequestBody("text/plain".toMediaTypeOrNull())
        val recipeDesc = requestDto.recipeDesc.toRequestBody("text/plain".toMediaTypeOrNull())
        val timeTaken =
            requestDto.timeTaken.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val ingredient = requestDto.ingredient.toRequestBody("text/plain".toMediaTypeOrNull())
        val hashtag = requestDto.hashtag.toRequestBody("text/plain".toMediaTypeOrNull())
        val carbohydrates = requestDto.nutritionalInfo?.carbohydrates.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val fat = requestDto.nutritionalInfo?.fat.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val minerals = requestDto.nutritionalInfo?.minerals.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val protein = requestDto.nutritionalInfo?.protein.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val vitamins = requestDto.nutritionalInfo?.vitamins.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val subCategoryId = requestDto.subCategoryDtoList.firstOrNull()?.id.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())

        recipeService.createRecipe(
            recipeName,
            recipeDesc,
            timeTaken,
            ingredient,
            hashtag,
            carbohydrates,
            fat,
            minerals,
            protein,
            vitamins,
            subCategoryId,
            imageParts
        ).enqueue(object : Callback<com.recipia.aos.ui.api.dto.ResponseDto<Long>> {
            override fun onResponse(
                call: Call<com.recipia.aos.ui.api.dto.ResponseDto<Long>>,
                response: Response<com.recipia.aos.ui.api.dto.ResponseDto<Long>>
            ) {
                if (response.isSuccessful) {
                    val recipeId = response.body()?.result ?: 0L // recipeId 추출
                    onSuccess(recipeId) // recipeId를 전달
                } else {
                    onError("서버 오류: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<com.recipia.aos.ui.api.dto.ResponseDto<Long>>, t: Throwable) {
                onError("네트워크 오류: ${t.message}")
            }
        })
    }

    // 레시피 수정하는 요청을 서버로 보낸다.
    fun updateRecipeRequest(
        requestDto: com.recipia.aos.ui.api.dto.recipe.RecipeCreateUpdateRequestDto,
        imageUris: List<Uri?>,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val imageParts = imageUris.mapNotNull { uri ->
            uri?.let { nonNullableUri ->
                uriToMultipartBodyPart(nonNullableUri, context)
            }
        }

        val idString = requestDto.id.toString()
        val recipeId = idString.toRequestBody("text/plain".toMediaTypeOrNull())
        val recipeName = requestDto.recipeName.toRequestBody("text/plain".toMediaTypeOrNull())
        val recipeDesc = requestDto.recipeDesc.toRequestBody("text/plain".toMediaTypeOrNull())
        val timeTaken =
            requestDto.timeTaken.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val ingredient = requestDto.ingredient.toRequestBody("text/plain".toMediaTypeOrNull())
        val hashtag = requestDto.hashtag.toRequestBody("text/plain".toMediaTypeOrNull())
        val nutritionalInfoId = requestDto.nutritionalInfo?.id.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val carbohydrates = requestDto.nutritionalInfo?.carbohydrates.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val fat = requestDto.nutritionalInfo?.fat.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val minerals = requestDto.nutritionalInfo?.minerals.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val protein = requestDto.nutritionalInfo?.protein.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val vitamins = requestDto.nutritionalInfo?.vitamins.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val subCategoryParts = mutableMapOf<String, RequestBody>()
        requestDto.subCategoryDtoList.forEachIndexed { index, subCategoryDto ->
            subCategoryParts["subCategoryDtoList[$index].id"] =
                subCategoryDto.id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        }

        recipeService.updateRecipe(
            recipeId,
            recipeName,
            recipeDesc,
            timeTaken,
            ingredient,
            hashtag,
            nutritionalInfoId,
            carbohydrates,
            fat,
            minerals,
            protein,
            vitamins,
            subCategoryParts,
            imageParts
        ).enqueue(object : Callback<com.recipia.aos.ui.api.dto.ResponseDto<Void>> {
            override fun onResponse(
                call: Call<com.recipia.aos.ui.api.dto.ResponseDto<Void>>,
                response: Response<com.recipia.aos.ui.api.dto.ResponseDto<Void>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.result
                    onSuccess()
                } else {
                    onError("서버 오류: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<com.recipia.aos.ui.api.dto.ResponseDto<Void>>, t: Throwable) {
                onError("네트워크 오류: ${t.message}")
            }
        })
    }

    private fun getMimeType(context: Context, uri: Uri): String? {
        return context.contentResolver.getType(uri)
    }

    // 이미지 URI를 MultipartBody.Part로 변환하는 함수
    private fun uriToMultipartBodyPart(
        uri: Uri,
        context: Context
    ): MultipartBody.Part? {

        // 이미지 압축 및 예외 처리를 통한 오류 처리 추가
        val compressedFile: File
        try {
            compressedFile = compressImageFile(context, uri, 1 * 1024 * 1024) // 10MB로 압축
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        // 이미지 MIME 유형 결정 추가
        val mimeType = getMimeType(context, uri) ?: "image/*"
        val requestFile = compressedFile.asRequestBody(mimeType.toMediaTypeOrNull())

        return MultipartBody.Part.createFormData("fileList", compressedFile.name, requestFile)
    }

    // 이미지를 압축하여 파일로 저장하는 함수
    @Throws(IOException::class)
    private fun compressImageFile(
        context: Context,
        uri: Uri,
        targetSizeBytes: Long
    ): File {

        val inputStream = context.contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)

        var quality = 100
        val byteArrayOutputStream = ByteArrayOutputStream()

        do {
            byteArrayOutputStream.reset()
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
            quality -= 5
        } while (byteArrayOutputStream.toByteArray().size > targetSizeBytes && quality > 0)

        val compressedFileName = "compressed_${System.currentTimeMillis()}.jpg"
        val compressedFile = File(context.cacheDir, compressedFileName)
        val fileOutputStream = FileOutputStream(compressedFile)
        fileOutputStream.write(byteArrayOutputStream.toByteArray())
        fileOutputStream.flush()
        fileOutputStream.close()

        return compressedFile
    }
}