package com.recipia.aos.ui.dto.mypage.ask

/**
 * 문의/피드백 상세보기 응답 dto
 */
data class AskViewResponseDto(
    val id: Long,
    val title: String,
    val content: String,
    val answer: String?,
    val createDate: String,
    val answerCreateDate: String?
)