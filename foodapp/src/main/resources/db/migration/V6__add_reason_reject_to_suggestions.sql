-- V6__add_reason_reject_to_suggestions.sql
-- Reason entered by admin when a suggestion is REJECTED (returned to the user)
ALTER TABLE suggestions
ADD COLUMN reason_reject VARCHAR(1000) NULL;
