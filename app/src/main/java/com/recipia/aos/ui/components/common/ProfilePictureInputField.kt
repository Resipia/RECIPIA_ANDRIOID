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
    // Box를 부모 컴포넌트의 중앙에 배치하기 위해 'fillMaxSize'와 'align' 사용
    Box(
        modifier = Modifier
            .fillMaxSize() // 부모 컴포넌트를 꽉 채움
            .padding(16.dp), // 내부 컨텐츠와의 간격 설정
        contentAlignment = Alignment.Center // Box 내부의 컨텐츠를 중앙에 배치
    ) {
        // 프로필 사진을 보여주는 내부 Box
        Box(
            modifier = Modifier
                .size(150.dp) // 내부 Box의 크기를 150dp x 150dp로 설정
                .clip(CircleShape) // 내부 Box의 모서리를 원형으로 클리핑
                .clickable { onImageSelected() } // 내부 Box를 클릭 가능하게 하여 이미지 선택 함수를 호출
                .border(BorderStroke(2.dp, Color.Gray), shape = CircleShape), // 원형의 테두리 추가
            contentAlignment = Alignment.Center
        ) {
            // 이미지가 있으면 이미지를 보여주고, 그렇지 않으면 + 아이콘을 보여줌
            if (profilePictureUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(model = profilePictureUri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize() // 이미지를 내부 Box의 최대 크기로 채우기
                        .clip(CircleShape), // 이미지를 원형으로 클리핑
                    contentScale = ContentScale.Crop // 이미지가 내부 Box를 꽉 채우도록 조정
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
