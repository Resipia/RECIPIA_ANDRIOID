package com.recipia.aos.ui.api.recipe.mypage

import com.recipia.aos.ui.dto.ResponseDto
import com.recipia.aos.ui.dto.mypage.MyPageViewResponseDto
import com.recipia.aos.ui.dto.mypage.ViewMyPageRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 마이페이지 retrofit 인터페이스
 */
interface MyPageService {

    // 마이페이지 보기
    @POST("/member/myPage/view")
    suspend fun viewMyPage(
        @Body dto: ViewMyPageRequestDto
    )
    : Response<ResponseDto<MyPageViewResponseDto>>

    // 로그아웃
    @POST("/member/auth/logout")
    suspend fun logout()
    : Response<ResponseDto<Void>>

    // 회원탈퇴
    @POST("/member/auth/deactivate")
    suspend fun deactivate()
    : Response<ResponseDto<Void>>

}