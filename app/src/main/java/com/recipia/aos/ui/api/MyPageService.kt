package com.recipia.aos.ui.api

import com.recipia.aos.ui.dto.ResponseDto
import com.recipia.aos.ui.dto.mypage.MyPageViewResponse
import retrofit2.Response
import retrofit2.http.POST

interface MyPageService {

    @POST("/member/myPage/view")
    suspend fun viewMyPage()
    : Response<ResponseDto<MyPageViewResponse>>

}