package com.recipia.aos.ui.components.recipe.create

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

/**
 * 레시피 생성, 수정 화면에서 선택한 이미지의 썸네일을 보여주는 컴포저
 * 이 코드는 드래그 앤 드롭으로 sortedImageUris 리스트의 아이템 순서를 변경할 수 있게 해주며, 이 변경 사항은 자동으로 selectedImageUris에도 반영되어야 해. onMove 콜백 함수를 ImageThumbnails 컴포저에 추가해서, 순서 변경이 발생할 때마다 selectedImageUris 리스트를 업데이트하는 로직을 구현할 수 있어.
 */
@Composable
fun ImageThumbnails(
    selectedImageUris: List<Uri?>,
    onRemoveImage: (Uri) -> Unit,
    onMove: (Int, Int) -> Unit
) {
    val state = rememberReorderableLazyListState(onMove = { from, to ->
        onMove(from.index, to.index)
    })

    LazyRow(
        state = state.listState,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.reorderable(state).detectReorderAfterLongPress(state)
    ) {
        items(selectedImageUris, { it.hashCode() }) { uri ->
            ReorderableItem(state, key = uri) { isDragging ->
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .padding(8.dp)
                        .then(if (isDragging) Modifier.shadow(4.dp) else Modifier)
                ) {
                    uri?.let {
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize().aspectRatio(1f)
                        )
                        IconButton(
                            onClick = { onRemoveImage(uri) },
                            modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove Image",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
