package com.recipia.aos.ui.dto.comment

// 댓글 삭제 요청 DTO
data class CommentDeleteRequestDto(
    val id: Long,
    val recipeId: Long
)