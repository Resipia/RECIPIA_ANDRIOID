package com.recipia.aos.ui.api.dto.chat

import java.time.LocalDateTime

/**
 * 채팅방 dto
 */
data class ChatRoomDto(

    val id: String?,  // 채팅방 고유 ID
    val roomIdentifier: String,  // 채팅방 식별자
    val memberIds: Set<String>,  // 참여자 ID 목록
    val creatorId: String,  // 채팅방을 생성한 사용자 ID
    val participantId: String,  // 채팅방에 참여한 다른 사용자 ID
    val createdAt: LocalDateTime?,  // 채팅방 생성 시간
    val updatedAt: LocalDateTime?  // 채팅방 최근 업데이트 시간

)