package com.recipia.aos.ui.api.dto.mypage

/**
 * 마이페이지 조회 요청을 담당하는 request dto
 */
data class ViewMyPageRequestDto(
    val targetMemberId: Long
)