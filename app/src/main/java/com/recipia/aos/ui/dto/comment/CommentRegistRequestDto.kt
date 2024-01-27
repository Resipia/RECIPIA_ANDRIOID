package com.recipia.aos.ui.dto.comment

// 댓글 작성 요청 DTO
data class CommentRegistRequestDto(
    val recipeId: Long,
    val commentText: String
)
