package com.recipia.aos.ui.components.mypage.follow

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun FollowButton(
    isFollowing: Boolean,
    onFollowClick: () -> Unit
) {

    Button(onClick = onFollowClick) {
        Text(text = if (isFollowing) "Unfollow" else "Follow")
    }

}
