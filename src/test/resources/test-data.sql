-- Test data for automated testing
-- This file will populate the test database with sample data

-- Users table test data
INSERT INTO users (id, email, password, first_name, last_name, phone, user_type, is_active, is_verified, created_at, updated_at) VALUES
(1, 'admin@test.com', '$2a$10$example_hash', 'Admin', 'User', '+91-9999999999', 'ADMIN', true, true, NOW(), NOW()),
(2, 'vendor@test.com', '$2a$10$example_hash', 'Test', 'Vendor', '+91-8888888888', 'VENDOR', true, true, NOW(), NOW()),
(3, 'buyer@test.com', '$2a$10$example_hash', 'Test', 'Buyer', '+91-7777777777', 'BUYER', true, true, NOW(), NOW()),
(4, 'employee@test.com', '$2a$10$example_hash', 'Test', 'Employee', '+91-6666666666', 'EMPLOYEE', true, true, NOW(), NOW());

-- Categories table test data
INSERT INTO categories (id, name, description, slug, is_active, parent_id, created_at, updated_at) VALUES
(1, 'Electronics', 'Electronic products and components', 'electronics', true, NULL, NOW(), NOW()),
(2, 'Smartphones', 'Mobile phones and accessories', 'smartphones', true, 1, NOW(), NOW()),
(3, 'Laptops', 'Laptop computers and accessories', 'laptops', true, 1, NOW(), NOW()),
(4, 'Industrial Equipment', 'Heavy machinery and industrial tools', 'industrial-equipment', true, NULL, NOW(), NOW()),
(5, 'Textiles', 'Fabrics, clothing, and textile products', 'textiles', true, NULL, NOW(), NOW());

-- Companies table test data
INSERT INTO companies (id, name, description, gstin, pan, address, city, state, country, pincode, phone, email, website, user_id, is_verified, created_at, updated_at) VALUES
(1, 'Test Vendor Company', 'A test vendor company for automated testing', '29ABCDE1234F1Z5', 'ABCDE1234F', '123 Test Street', 'Mumbai', 'Maharashtra', 'India', '400001', '+91-8888888888', 'vendor@test.com', 'https://testvendor.com', 2, true, NOW(), NOW()),
(2, 'Test Buyer Company', 'A test buyer company for automated testing', '29FGHIJ5678K1L2', 'FGHIJ5678K', '456 Test Avenue', 'Delhi', 'Delhi', 'India', '110001', '+91-7777777777', 'buyer@test.com', 'https://testbuyer.com', 3, true, NOW(), NOW());

-- Products table test data
INSERT INTO products (id, name, description, short_description, price, discount_price, sku, stock_quantity, min_order_quantity, category_id, vendor_id, is_active, is_featured, created_at, updated_at) VALUES
(1, 'Test Smartphone', 'A test smartphone for automated testing', 'Test phone with all features', 25000.00, 23000.00, 'TSP001', 100, 1, 2, 2, true, true, NOW(), NOW()),
(2, 'Test Laptop', 'A test laptop for automated testing', 'High-performance test laptop', 55000.00, 50000.00, 'TLP001', 50, 1, 3, 2, true, false, NOW(), NOW()),
(3, 'Industrial Machine', 'Test industrial equipment', 'Heavy duty test machine', 250000.00, 240000.00, 'TIM001', 10, 1, 4, 2, true, true, NOW(), NOW());

-- Orders table test data
INSERT INTO orders (id, order_number, buyer_id, vendor_id, total_amount, status, payment_status, shipping_address, billing_address, created_at, updated_at) VALUES
(1, 'ORD001', 3, 2, 23000.00, 'PENDING', 'PENDING', '456 Test Avenue, Delhi', '456 Test Avenue, Delhi', NOW(), NOW()),
(2, 'ORD002', 3, 2, 50000.00, 'CONFIRMED', 'COMPLETED', '456 Test Avenue, Delhi', '456 Test Avenue, Delhi', NOW(), NOW());

-- Order items table test data
INSERT INTO order_items (id, order_id, product_id, quantity, price, total_amount) VALUES
(1, 1, 1, 1, 23000.00, 23000.00),
(2, 2, 2, 1, 50000.00, 50000.00);

-- Reviews table test data
INSERT INTO reviews (id, product_id, user_id, rating, title, comment, is_verified_purchase, created_at, updated_at) VALUES
(1, 1, 3, 5, 'Great product!', 'This smartphone works perfectly for our business needs.', true, NOW(), NOW()),
(2, 2, 3, 4, 'Good laptop', 'Nice performance, good value for money.', true, NOW(), NOW());

-- Inquiries table test data
INSERT INTO inquiries (id, product_id, buyer_id, vendor_id, subject, message, quantity_required, budget, status, created_at, updated_at) VALUES
(1, 1, 3, 2, 'Bulk order inquiry', 'We need 100 units of this smartphone. Can you provide?', 100, 2200000.00, 'OPEN', NOW(), NOW()),
(2, 3, 3, 2, 'Industrial equipment quote', 'Need quote for 5 units of this machine', 5, 1200000.00, 'QUOTED', NOW(), NOW());

-- Quotes table test data
INSERT INTO quotes (id, inquiry_id, vendor_id, quoted_price, quantity, validity_days, terms_conditions, status, created_at, updated_at) VALUES
(1, 1, 2, 2200000.00, 100, 30, 'Standard terms and conditions apply', 'SENT', NOW(), NOW()),
(2, 2, 2, 1200000.00, 5, 15, 'Installation and training included', 'SENT', NOW(), NOW());

-- Test API keys and settings
INSERT INTO settings (id, setting_key, setting_value, description, created_at, updated_at) VALUES
(1, 'TEST_MODE', 'true', 'Enable test mode for automated testing', NOW(), NOW()),
(2, 'EMAIL_NOTIFICATIONS', 'false', 'Disable email notifications during testing', NOW(), NOW()),
(3, 'PAYMENT_GATEWAY_MODE', 'test', 'Use test payment gateway', NOW(), NOW());
