package com.recipia.aos.ui.model.recipe.bookmark

// 북마크 업데이트 상태를 나타내는 sealed class
sealed class BookmarkUpdateState {
    data class Added(val recipeId: Long, val bookmarkId: Long) : BookmarkUpdateState()
    data class Removed(val recipeId: Long) : BookmarkUpdateState() // 변경: bookmarkId -> recipeId
}
