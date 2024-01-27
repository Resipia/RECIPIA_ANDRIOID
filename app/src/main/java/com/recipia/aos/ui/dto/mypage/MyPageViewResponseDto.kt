package com.recipia.aos.ui.dto.mypage

data class MyPageViewResponseDto(
    val memberId: Long,       // 멤버 id
    val profileImageUrl: String?, // 프로필 이미지 url
    val nickname: String,     // 닉네임
    val introduction: String?,// 한 줄 소개
    val followingCount: Long, // 팔로잉 숫자
    val followerCount: Long,  // 팔로워 숫자
    val birth: String?,       // 생년 월일
    val gender: String?,      // 성별
    var followId: Long,       // 로그인한 회원이 팔로우 하고있는 회원이면 데이터가 들어있고 팔로우 하고있지 않은 회원이면 null
    val me: Boolean         // 응답한 마이페이지가 나의 마이페이지라면 true, 다른 회원의 마이페이지라면 false
)
