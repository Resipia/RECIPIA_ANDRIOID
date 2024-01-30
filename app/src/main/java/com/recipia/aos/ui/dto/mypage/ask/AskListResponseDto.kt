package com.recipia.aos.ui.dto.mypage.ask

/**
 * 문의하기 조회 응답 dto
 */
data class AskListResponseDto(
    val id: Long,
    val title: String,
    val answerYn: Boolean,
    val createDate: String
)