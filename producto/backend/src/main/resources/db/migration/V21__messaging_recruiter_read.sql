ALTER TABLE conversations
  ADD COLUMN recruiter_last_read_message_id BIGINT UNSIGNED NOT NULL DEFAULT 0
    AFTER login_notice_dismissed;

UPDATE conversations c
SET recruiter_last_read_message_id = COALESCE(
  (
    SELECT MAX(m.id)
    FROM conversation_messages m
    WHERE m.conversation_id = c.id
  ),
  0
);
