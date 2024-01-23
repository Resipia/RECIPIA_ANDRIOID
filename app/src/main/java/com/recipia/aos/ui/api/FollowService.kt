package com.recipia.aos.ui.api

import com.recipia.aos.ui.dto.PagingResponseDto
import com.recipia.aos.ui.dto.mypage.follow.FollowListResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 팔로잉, 팔로워 서비스
 */
interface FollowService {

    @GET("/member/myPage/followList")
    suspend fun getFollowList(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("targetMemberId") targetMemberId: Long,
        @Query("type") type: String
    ): Response<PagingResponseDto<FollowListResponseDto>>

}