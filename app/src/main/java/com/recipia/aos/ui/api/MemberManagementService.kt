package com.recipia.aos.ui.api

import com.recipia.aos.ui.dto.ResponseDto
import com.recipia.aos.ui.dto.singup.EmailAvailableRequestDto
import com.recipia.aos.ui.dto.singup.NicknameAvailableRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 코루틴 비동기 요청
 * 이메일 중복 체크와 같은 네트워크 요청은 비동기 작업이므로 suspend fun으로 처리하는 것이 좋다.
 */
interface MemberManagementService {

    // 이메일 중복 체크 요청
    @POST("/member/management/checkDupEmail")
    suspend fun checkDuplicateEmail(
        @Body request: EmailAvailableRequestDto
    ): Response<ResponseDto<Boolean>>

    // 닉네임 중복 체크 요청
    @POST("/member/management/checkDupNickname")
    suspend fun checkDuplicateNickname(
        @Body nicknameRequestDto: NicknameAvailableRequestDto
    ): Response<ResponseDto<Boolean>>
}
