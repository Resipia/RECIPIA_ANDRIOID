package com.recipia.aos.ui.dto

data class PagingResponseDto<T>(
    val content: List<T>,
    val totalCount: Long
)
