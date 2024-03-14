package com.recipia.aos.ui.api.dto.mypage

/**
 * 마이페이지에서 레시피 count를 조회할때 사용한다.
 */
data class MyPageRequestDto(
    val targetMemberId: Long
)