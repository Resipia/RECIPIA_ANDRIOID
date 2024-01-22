package com.recipia.aos.ui.dto.mypage

data class MyPageViewResponse(
    val memberId: Long,
    val profileImageUrl: String,
    val nickname: String,
    val introduction: String,
    val followingCount: Long,
    val followerCount: Long
)
