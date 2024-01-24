package com.recipia.aos.ui.api

import com.recipia.aos.ui.dto.ResponseDto
import com.recipia.aos.ui.dto.forgot.FindEmailRequestDto
import com.recipia.aos.ui.dto.forgot.TempPasswordRequestDto
import com.recipia.aos.ui.dto.singup.EmailAvailableRequestDto
import com.recipia.aos.ui.dto.singup.NicknameAvailableRequestDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

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

    // 이메일 찾기
    @POST("/member/management/find/email")
    suspend fun findEmail(
        @Body dto: FindEmailRequestDto
    ): Response<ResponseDto<String>>

    // 임시 비밀번호 재발급
    @POST("/member/management/tempPassword")
    suspend fun sendTempPassword(
        @Body dto: TempPasswordRequestDto
    ): Response<ResponseDto<Void>>

    // 회원가입
    @Multipart
    @POST("/member/signUp")
    suspend fun signUp(
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("fullName") fullName: RequestBody?,
        @Part("nickname") nickname: RequestBody,
        @Part("introduction") introduction: RequestBody?,
        @Part("telNo") telNo: RequestBody?,
        @Part("address1") address1: RequestBody?,
        @Part("address2") address2: RequestBody?,
        @Part profileImage: MultipartBody.Part?,
        @Part("isPersonalInfoConsent") isPersonalInfoConsent: RequestBody,
        @Part("isDataRetentionConsent") isDataRetentionConsent: RequestBody,
        @Part("birth") birth: RequestBody?,
        @Part("gender") gender: RequestBody?
    ): Response<ResponseDto<Long>>

}
