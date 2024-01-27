package com.recipia.aos.ui.dto.comment

// 댓글 수정 요청 DTO
data class CommentUpdateRequestDto(
    val id: Long,
    val commentText: String
)
