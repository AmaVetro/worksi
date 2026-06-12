package com.worksi.app.data.api

import com.worksi.app.data.model.CandidateConversationDetailJson
import com.worksi.app.data.model.DismissLoginNoticeResponseJson
import com.worksi.app.data.model.LoginNoticeResponseJson
import com.worksi.app.data.model.MessageItemJson
import com.worksi.app.data.model.MessagingConversationsPageJson
import com.worksi.app.data.model.MessagingMessagesPageJson
import com.worksi.app.data.model.SendMessageRequestJson
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MessagingApi {
  @GET("api/v1/messaging/conversations")
  suspend fun listConversations(
      @Query("page") page: Int,
      @Query("size") size: Int,
      @Query("sort") sort: String = "updated_at,desc"
  ): Response<MessagingConversationsPageJson>

  @GET("api/v1/messaging/conversations/login-notice")
  suspend fun getLoginNotice(): Response<LoginNoticeResponseJson>

  @PATCH("api/v1/messaging/conversations/{conversation_id}/dismiss-login-notice")
  suspend fun dismissLoginNotice(
      @Path("conversation_id") conversationId: Long
  ): Response<DismissLoginNoticeResponseJson>

  @GET("api/v1/messaging/conversations/{conversation_id}")
  suspend fun getConversation(
      @Path("conversation_id") conversationId: Long
  ): Response<CandidateConversationDetailJson>

  @GET("api/v1/messaging/conversations/{conversation_id}/messages")
  suspend fun listMessages(
      @Path("conversation_id") conversationId: Long,
      @Query("page") page: Int? = null,
      @Query("size") size: Int = 50,
      @Query("sort") sort: String = "sent_at,asc",
      @Query("after_message_id") afterMessageId: Long? = null
  ): Response<MessagingMessagesPageJson>

  @POST("api/v1/messaging/conversations/{conversation_id}/messages")
  suspend fun sendMessage(
      @Path("conversation_id") conversationId: Long,
      @Body body: SendMessageRequestJson
  ): Response<MessageItemJson>
}
