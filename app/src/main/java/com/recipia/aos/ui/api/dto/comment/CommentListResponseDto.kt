package com.recipia.aos.ui.api.dto.comment

data class CommentListResponseDto(
    val id: Long,                 // comment pk
    val memberId: Long,           // 작성자 member id
    val nickname: String,         // 작성자 닉네임
    val commentValue: String,     // 댓글 내용
    val createDate: String,       // 댓글 작성 날짜
    val updated: Boolean        // 댓글 수정 여부
)