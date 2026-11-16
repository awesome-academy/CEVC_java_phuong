-- V7__add_version_to_suggestions.sql
-- Optimistic locking: concurrent admin/user saves on the same suggestion are rejected instead of overwriting each other
ALTER TABLE suggestions
ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
