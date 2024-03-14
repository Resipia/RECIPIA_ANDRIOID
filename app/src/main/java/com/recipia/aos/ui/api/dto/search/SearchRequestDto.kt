package com.recipia.aos.ui.api.dto.search

data class SearchRequestDto(
    val condition: com.recipia.aos.ui.api.dto.search.SearchType?, // 검색조건 (전체, 재료, 해시태그)
    val searchWord: String, // 사용자가 입력한 검색어
    val resultSize: Int? // 반환될 결과의 최대 개수 (5, 10)
)