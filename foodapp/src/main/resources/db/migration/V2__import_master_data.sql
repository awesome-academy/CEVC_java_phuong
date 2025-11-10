-- V2_import_master_data.sql
-- Insert master data into roles table
INSERT INTO
  roles (name, description)
VALUES
  ('USER', 'User with limited access'),
  ('ADMIN', 'Administrator with full access');

-- Insert master data into auth_providers table
INSERT INTO
  auth_providers (name, description)
VALUES
  ('LOCAL', 'Login type username, password'),
  ('GOOGLE', 'Login type Google account'),
  ('FACEBOOK', 'Login type Facebook account'),
  ('TWITTER', 'Login type Twitter account');

-- Insert master data into product_types table
INSERT INTO
  product_types (name, description)
VALUES
  ('FOOD', 'Food and dishes'),
  ('DRINK', 'Beverages and drinks');

-- Insert master data into order_statuses table
INSERT INTO
  order_statuses (name, description)
VALUES
  ('PENDING', 'Order is pending'),
  ('CONFIRMED', 'Order is confirmed'),
  ('SHIPPED', 'Order is shipped'),
  ('COMPLETED', 'Order is completed'),
  ('CANCELLED', 'Order is cancelled');
