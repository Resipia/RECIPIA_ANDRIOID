package com.recipia.aos.ui.api.dto.search

data class SearchResponseDto(
    val type: com.recipia.aos.ui.api.dto.search.SearchType?, // 검색조건 (전체, 재료, 해시태그)
    val searchResultList: List<String>
)