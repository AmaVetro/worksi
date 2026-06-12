import api from "./apiClient";

export async function createConversation(applicationId, firstMessage) {
  const { data } = await api.post("/api/v1/messaging/conversations", {
    application_id: applicationId,
    first_message: firstMessage,
  });
  return data;
}

export async function listConversations(page = 1, size = 20) {
  const { data } = await api.get("/api/v1/messaging/conversations", {
    params: { page, size, sort: "updated_at,desc" },
  });
  return data;
}

export async function getConversation(conversationId) {
  const { data } = await api.get(
    `/api/v1/messaging/conversations/${conversationId}`
  );
  return data;
}

export async function listMessages(conversationId, options = {}) {
  const params = { sort: "sent_at,asc", size: 50 };
  if (options.afterMessageId != null) {
    params.after_message_id = options.afterMessageId;
  } else {
    params.page = options.page ?? 1;
  }
  const { data } = await api.get(
    `/api/v1/messaging/conversations/${conversationId}/messages`,
    { params }
  );
  return data;
}

export async function sendMessage(conversationId, body) {
  const { data } = await api.post(
    `/api/v1/messaging/conversations/${conversationId}/messages`,
    { body }
  );
  return data;
}
