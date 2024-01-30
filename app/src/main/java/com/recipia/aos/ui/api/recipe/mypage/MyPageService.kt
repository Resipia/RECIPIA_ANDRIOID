package com.recipia.aos.ui.api.recipe.mypage

import com.recipia.aos.ui.dto.PagingResponseDto
import com.recipia.aos.ui.dto.RecipeListResponseDto
import com.recipia.aos.ui.dto.ResponseDto
import com.recipia.aos.ui.dto.mypage.MyPageRequestDto
import com.recipia.aos.ui.dto.mypage.MyPageViewResponseDto
import com.recipia.aos.ui.dto.mypage.ViewMyPageRequestDto
import com.recipia.aos.ui.dto.recipe.detail.MemberProfileRequestDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

/**
 * 마이페이지 retrofit 인터페이스
 */
interface MyPageService {

    // 마이페이지 보기
    @POST("/member/myPage/view")
    suspend fun viewMyPage(
        @Body dto: ViewMyPageRequestDto
    ): Response<ResponseDto<MyPageViewResponseDto>>

    // 프로필 수정
    @Multipart
    @POST("/member/myPage/update")
    suspend fun updateProfile(
        @Part("nickname") nickname: RequestBody,
        @Part("introduction") introduction: RequestBody?,
        @Part profileImage: MultipartBody.Part?,
        @Part("deleteFileOrder") deleteFileOrder: RequestBody?,
        @Part("birth") birth: RequestBody?,
        @Part("gender") gender: RequestBody?
    ): Response<ResponseDto<Void>>

    // targetMemberId가 작성한 레시피 갯수 가져오기
    @POST("/recipe/mypage/recipeCnt")
    suspend fun getRecipeTotalCount(
        @Body dto: MyPageRequestDto
    ): Response<ResponseDto<Long>>

    // targetMemberId가 작성한 레시피 중 조회수 높은 레시피 최대 5개 가져오기
    @POST("/recipe/mypage/highRecipe")
    suspend fun getHighRecipe(
        @Body dto: MyPageRequestDto
    ): Response<ResponseDto<List<RecipeListResponseDto>>>

    // 내가 북마크한 레시피 조회
    @GET("/recipe/mypage/myBookmarkRecipeList")
    suspend fun getAllMyBookmarkRecipeList(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<PagingResponseDto<RecipeListResponseDto>>

    // 내가 좋아요한 레시피 조회
    @GET("/recipe/mypage/myLikeRecipeList")
    suspend fun getAllMyLikeRecipeList(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<PagingResponseDto<RecipeListResponseDto>>

    // targetMember가 작성한 레시피 조회
    @GET("/recipe/mypage/targetMemberRecipeList")
    suspend fun getAllTargetMemberRecipeList(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortType") sortType: String,
        @Query("targetMemberId") targetMemberId: Long
    ): Response<PagingResponseDto<RecipeListResponseDto>>

    // 회원 프로필 사진 preUrl 받기 (요청 파라미터 없이도 jwt에 담긴 memberId로 요청)
    @POST("/member/management/getProfile")
    suspend fun getProfileImage(
        @Body dto: MemberProfileRequestDto
    ): Response<ResponseDto<String>>

    // 로그아웃
    @POST("/member/auth/logout")
    suspend fun logout(): Response<ResponseDto<Void>>

    // 회원탈퇴
    @POST("/member/auth/deactivate")
    suspend fun deactivate(): Response<ResponseDto<Void>>

}