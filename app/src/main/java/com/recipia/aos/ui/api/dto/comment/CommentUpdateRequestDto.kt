package com.recipia.aos.ui.api.dto.comment

// 댓글 수정 요청 DTO
data class CommentUpdateRequestDto(
    val id: Long,
    val recipeId: Long,
    val commentText: String
)
