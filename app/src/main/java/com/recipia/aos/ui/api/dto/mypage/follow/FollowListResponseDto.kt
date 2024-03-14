package com.recipia.aos.ui.api.dto.mypage.follow

/**
 * 팔로잉, 팔로워에 대한 응답객체
 */
data class FollowListResponseDto(
    val memberId: Long,        // 멤버id (pk)
    val imageFullPath: String?, //프로필 사진 저장된 경로
    val preUrl: String?,        // 회원 프사를 담을 url
    val nickname: String,      // 회원 닉네임
    val followId: Long?,        // 만약 내가 팔로우 하고 있는 회원이면 follow pk값이 담겨있고, 내가 팔로우 하는 회원이 아니면 null 반환
    val me: Boolean          // 이 회원이 로그인한 본인 계정이면 '나'라는걸 표시하기 위한 flag
)