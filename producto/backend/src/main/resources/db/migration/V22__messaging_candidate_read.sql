ALTER TABLE conversations
  ADD COLUMN candidate_last_read_message_id BIGINT UNSIGNED NOT NULL DEFAULT 0
    AFTER recruiter_last_read_message_id;

UPDATE conversations c
SET candidate_last_read_message_id = COALESCE(
  (
    SELECT MAX(m.id)
    FROM conversation_messages m
    WHERE m.conversation_id = c.id
  ),
  0
);
