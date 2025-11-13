-- V3__add_deleted_at_to_users.sql
-- add deleted_at column to users table
ALTER TABLE users
ADD COLUMN deleted_at TIMESTAMP NULL;
