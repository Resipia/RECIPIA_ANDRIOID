package com.recipia.aos.ui.dto.mypage

data class MyPageViewResponse(
    val memberId: Long, // 멤버 id
    val profileImageUrl: String, // 프로필 이미지 url
    val nickname: String, // 닉네임
    val introduction: String, // 한 줄 소개
    val followingCount: Long, // 팔로잉 숫자
    val followerCount: Long, // 팔로워 숫자
    val birth: String, // 생년 월일
    val gender: String // 성별
)
