package com.recipia.aos.ui.model.recipe.create

import TokenManager
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.recipia.aos.ui.api.CreateRecipeService
import com.recipia.aos.ui.dto.login.ResponseDto
import com.recipia.aos.ui.dto.recipe.NutritionalInfoDto
import com.recipia.aos.ui.dto.recipe.RecipeCreateUpdateRequestDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

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
    var nutritionalInfoList = mutableStateListOf<NutritionalInfoDto>()
    var selectedImageUris = mutableStateListOf<Uri?>()


    private val recipeService: CreateRecipeService by lazy {
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
            .baseUrl("http://10.0.2.2:8082/") // 서버 주소 확인 필요
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(CreateRecipeService::class.java)
    }

    // 서버로 데이터를 전송하고 응답을 처리한다.
    fun sendRecipeToServer(
        requestDto: RecipeCreateUpdateRequestDto,
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

        val recipeName = requestDto.recipeName.toRequestBody("text/plain".toMediaTypeOrNull())
        val recipeDesc = requestDto.recipeDesc.toRequestBody("text/plain".toMediaTypeOrNull())
        val timeTaken = requestDto.timeTaken.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val ingredient = requestDto.ingredient.toRequestBody("text/plain".toMediaTypeOrNull())
        val hashtag = requestDto.hashtag.toRequestBody("text/plain".toMediaTypeOrNull())
        val carbohydrates = requestDto.nutritionalInfo?.carbohydrates.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val fat = requestDto.nutritionalInfo?.fat.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val minerals = requestDto.nutritionalInfo?.minerals.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val protein = requestDto.nutritionalInfo?.protein.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val vitamins = requestDto.nutritionalInfo?.vitamins.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val subCategoryId = requestDto.subCategoryDtoList.firstOrNull()?.id.toString().toRequestBody("text/plain".toMediaTypeOrNull())

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
        ).enqueue(object : Callback<ResponseDto<Long>> {
            override fun onResponse(
                call: Call<ResponseDto<Long>>,
                response: Response<ResponseDto<Long>>
            ) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("서버 오류: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseDto<Long>>, t: Throwable) {
                onError("네트워크 오류: ${t.message}")
            }
        })
    }

    // 이미지 URI를 실제 파일 경로로 변환하는 함수
    private fun uriToFilePath(context: Context, uri: Uri): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(
                context,
                uri
            )
        ) {
            if ("com.android.externalstorage.documents" == uri.authority) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                if (split.size == 2) {
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return context.getExternalFilesDir(null)?.absolutePath + "/" + split[1]
                    }
                }
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), id.toLong()
                )
                return getDataColumn(context, contentUri, null, null)
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {
                return getDataColumn(context, uri, null, null)
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }
        } else {
            return getDataColumn(context, uri, null, null)
        }
        return null
    }

    private fun getMimeType(context: Context, uri: Uri): String? {
        return context.contentResolver.getType(uri)
    }

    private fun uriToMultipartBodyPart(uri: Uri, context: Context): MultipartBody.Part? {
        val filePath = uriToFilePath(context, uri) ?: return null

        val file = File(filePath)
        val mimeType = getMimeType(context, uri) ?: "image/*"
        val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())

        return MultipartBody.Part.createFormData("fileList", file.name, requestFile)
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        val column = "_data"
        val projection = arrayOf(column)
        var cursor: Cursor? = null
        try {
            cursor = uri?.let {
                context.contentResolver.query(
                    it,
                    projection,
                    selection,
                    selectionArgs,
                    null
                )
            }
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    fun clearData() {
        recipeName.value = ""
        recipeDesc.value = ""
        timeTaken.value = ""
        ingredient.value = ""
        hashtag.value = ""
        nutritionalInfoList.clear()
        selectedImageUris.clear()
    }

}