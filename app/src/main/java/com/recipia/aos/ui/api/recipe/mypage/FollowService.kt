package com.recipia.aos.ui.api.recipe.mypage

import com.recipia.aos.ui.api.dto.PagingResponseDto
import com.recipia.aos.ui.api.dto.ResponseDto
import com.recipia.aos.ui.api.dto.mypage.follow.FollowListResponseDto
import com.recipia.aos.ui.api.dto.mypage.follow.FollowRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 팔로잉, 팔로워 서비스
 */
interface FollowService {

    // 팔로잉/팔로워 리스트 가져오기
    @GET("/member/myPage/followList")
    suspend fun getFollowList(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("targetMemberId") targetMemberId: Long,
        @Query("type") type: String
    ): Response<com.recipia.aos.ui.api.dto.PagingResponseDto<com.recipia.aos.ui.api.dto.mypage.follow.FollowListResponseDto>>

    // 팔로우/언팔로우 요청
    @POST("/member/follow/totalFollow")
    suspend fun followOrUnFollow(
        @Body dto: com.recipia.aos.ui.api.dto.mypage.follow.FollowRequestDto
    ): Response<com.recipia.aos.ui.api.dto.ResponseDto<Long>>

}