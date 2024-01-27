package com.recipia.aos.ui.api.recipe.search

import com.recipia.aos.ui.dto.ResponseDto
import com.recipia.aos.ui.dto.search.SearchResponseDto
import com.recipia.aos.ui.dto.search.SearchType
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 몽고db 재료, 해시태그 검색
 */
interface MongoSearchService {

    @GET("/mongo/search")
    suspend fun search(
        @Query("condition") condition: SearchType,
        @Query("searchWord") searchWord: String,
        @Query("resultSize") resultSize: Int
    ): Response<ResponseDto<List<SearchResponseDto>>>


}