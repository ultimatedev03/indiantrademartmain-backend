-- V5: Buyer taxonomy tables to match JPA entities

-- buyer_category table
CREATE TABLE IF NOT EXISTS buyer_category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    display_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    meta_title VARCHAR(255),
    meta_description VARCHAR(500),
    slug VARCHAR(255),
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);
CREATE INDEX IF NOT EXISTS idx_buyer_category_active ON buyer_category(is_active);
CREATE INDEX IF NOT EXISTS idx_buyer_category_order ON buyer_category(display_order);
CREATE UNIQUE INDEX IF NOT EXISTS uq_buyer_category_slug ON buyer_category(slug);

-- sub_categories table (already named explicitly in entity)
CREATE TABLE IF NOT EXISTS sub_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    display_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    meta_title VARCHAR(255),
    meta_description VARCHAR(500),
    slug VARCHAR(255),
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    category_id BIGINT NOT NULL REFERENCES buyer_category(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_sub_categories_category ON sub_categories(category_id);
CREATE INDEX IF NOT EXISTS idx_sub_categories_active ON sub_categories(is_active);

-- buyer_micro_category table
CREATE TABLE IF NOT EXISTS buyer_micro_category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    display_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    meta_title VARCHAR(255),
    meta_description VARCHAR(500),
    slug VARCHAR(255),
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    sub_category_id BIGINT NOT NULL REFERENCES sub_categories(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_buyer_micro_category_sub ON buyer_micro_category(sub_category_id);
CREATE INDEX IF NOT EXISTS idx_buyer_micro_category_active ON buyer_micro_category(is_active);
