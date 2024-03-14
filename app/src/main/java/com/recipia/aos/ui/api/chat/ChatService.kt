package com.recipia.aos.ui.api.chat

import com.recipia.aos.ui.api.dto.chat.ChatRoomDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 채팅 api 요청
 */
interface ChatService {

    @GET("/userChatRooms")
    suspend fun getUserChatRooms(
        @Query("memberId") memberId: String
    ): Response<List<ChatRoomDto>>

    @POST("/chatRoom")
    suspend fun createChatRoom(
        @Query("creatorId") creatorId: String,
        @Query("participantId") participantId: String
    ): Response<ChatRoomDto>

}
