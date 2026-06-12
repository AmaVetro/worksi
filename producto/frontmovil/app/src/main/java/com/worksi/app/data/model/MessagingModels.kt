package com.worksi.app.data.model

import com.squareup.moshi.Json

data class MessagingConversationsPageJson(
    val items: List<CandidateConversationListItemJson>,
    val page: Int,
    val size: Int,
    @Json(name = "total_items") val totalItems: Long,
    @Json(name = "total_pages") val totalPages: Int
)

data class CandidateConversationListItemJson(
    @Json(name = "conversation_id") val conversationId: Long,
    @Json(name = "company_id") val companyId: Long,
    @Json(name = "company_commercial_name") val companyCommercialName: String,
    @Json(name = "application_id") val applicationId: Long,
    @Json(name = "job_id") val jobId: Long,
    @Json(name = "job_title") val jobTitle: String,
    @Json(name = "last_message_preview") val lastMessagePreview: String,
    @Json(name = "last_message_at") val lastMessageAt: String?,
    @Json(name = "updated_at") val updatedAt: String?,
    @Json(name = "unread_count") val unreadCount: Int = 0,
    @Json(name = "match_score") val matchScore: Double? = null
)

data class LoginNoticeResponseJson(
    val item: LoginNoticeItemJson?
)

data class LoginNoticeItemJson(
    @Json(name = "conversation_id") val conversationId: Long,
    @Json(name = "company_commercial_name") val companyCommercialName: String,
    @Json(name = "job_title") val jobTitle: String,
    @Json(name = "first_message_preview") val firstMessagePreview: String,
    @Json(name = "salary_offered") val salaryOffered: Int = 0
)

data class DismissLoginNoticeResponseJson(
    @Json(name = "conversation_id") val conversationId: Long,
    @Json(name = "login_notice_dismissed") val loginNoticeDismissed: Boolean
)

data class CandidateConversationDetailJson(
    @Json(name = "conversation_id") val conversationId: Long,
    @Json(name = "company_id") val companyId: Long,
    @Json(name = "company_commercial_name") val companyCommercialName: String,
    @Json(name = "application_id") val applicationId: Long,
    @Json(name = "job_id") val jobId: Long,
    @Json(name = "job_title") val jobTitle: String,
    @Json(name = "created_at") val createdAt: String?
)

data class MessagingMessagesPageJson(
    val items: List<MessageItemJson>,
    val page: Int,
    val size: Int,
    @Json(name = "total_items") val totalItems: Long,
    @Json(name = "total_pages") val totalPages: Int
)

data class MessageItemJson(
    @Json(name = "message_id") val messageId: Long,
    @Json(name = "conversation_id") val conversationId: Long,
    @Json(name = "sender_user_id") val senderUserId: Long,
    @Json(name = "sender_role") val senderRole: String,
    val body: String,
    @Json(name = "sent_at") val sentAt: String?
)

data class SendMessageRequestJson(
    val body: String
)
