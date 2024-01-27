package com.recipia.aos.ui.dto.search

data class SearchResponseDto(
    val type: SearchType?, // 검색조건 (전체, 재료, 해시태그)
    val searchResultList: List<String>
)