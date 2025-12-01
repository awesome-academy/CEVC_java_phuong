-- V4__add_user_address_order_address.sql
-- Create user_addresses table
CREATE TABLE
  user_addresses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users (id),
    receiver_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    city VARCHAR(100) NOT NULL,
    district VARCHAR(100) NOT NULL,
    street_address VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
  );

-- Indexes for user_addresses table
CREATE INDEX idx_user_addresses_user_id ON user_addresses (user_id);

-- Create order_addresses table
CREATE TABLE
  order_addresses (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders (id),
    receiver_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    city VARCHAR(100) NOT NULL,
    district VARCHAR(100) NOT NULL,
    street_address VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
  );

CREATE INDEX idx_order_addresses_order_id ON order_addresses (order_id);

-- Add note column to orders table
ALTER TABLE orders
ADD COLUMN note VARCHAR(10000);
