package com.recipia.aos.ui.api.dto

data class PagingResponseDto<T>(
    val content: List<T>,
    val totalCount: Long
)
