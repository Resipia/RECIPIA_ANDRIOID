package com.recipia.aos.ui.components.common

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

// 프로필 사진 입력 필드 컴포저블 함수
@Composable
fun ProfilePictureInputField(
    profilePictureUri: Uri?,
    onImageSelected: () -> Unit,
    onImageRemoved: () -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(150.dp)
            .clickable { onImageSelected() }, // 클릭 이벤트 설정
        contentAlignment = Alignment.Center // 내용을 중앙에 정렬
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .border(2.dp, Color.Gray, shape = CircleShape)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // 이미지를 보여준다.
            if (profilePictureUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(model = profilePictureUri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize() // Image를 Box 안에서 최대로 채우도록 설정
                        .align(Alignment.Center), // Image를 Box의 중앙에 정렬
                    contentScale = ContentScale.Crop // 이미지가 Box를 꽉 채우도록 조정
                )
            }
            // 이미지 내부에 + 아이콘
            IconButton(onClick = { onImageSelected() }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = if (profilePictureUri == null) "프로필 사진 추가" else "프로필 사진 변경",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
