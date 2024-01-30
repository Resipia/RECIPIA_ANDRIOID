package com.recipia.aos.ui.api.recipe.mypage.ask

import com.recipia.aos.ui.dto.PagingResponseDto
import com.recipia.aos.ui.dto.ResponseDto
import com.recipia.aos.ui.dto.mypage.ask.AskListResponseDto
import com.recipia.aos.ui.dto.mypage.ask.AskRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 마이페이지 문의사항 api요청 인터페이스
 */
interface AskService {

    // 문의사항 등록하기
    @POST("/member/ask/create")
    suspend fun createAsk(
        @Body dto: AskRequestDto
    ): Response<ResponseDto<Long>>

    @GET("/member/ask/list")
    suspend fun getList(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<PagingResponseDto<AskListResponseDto>>

}