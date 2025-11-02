-- V2: Vendor Packages module tables

-- vendor_packages
CREATE TABLE IF NOT EXISTS vendor_packages (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(255) NOT NULL,
    description TEXT,
    price NUMERIC(10,2) NOT NULL,
    discounted_price NUMERIC(10,2),
    duration_days INTEGER NOT NULL,
    duration_type VARCHAR(20),
    plan_type VARCHAR(50) NOT NULL,
    badge VARCHAR(100),
    color VARCHAR(50),
    icon VARCHAR(255),
    max_products INTEGER,
    max_leads INTEGER,
    max_orders INTEGER,
    max_quotations INTEGER,
    max_product_images INTEGER,
    featured_listing BOOLEAN DEFAULT FALSE,
    priority_support BOOLEAN DEFAULT FALSE,
    analytics_access BOOLEAN DEFAULT FALSE,
    chatbot_priority BOOLEAN DEFAULT FALSE,
    custom_branding BOOLEAN DEFAULT FALSE,
    bulk_import_export BOOLEAN DEFAULT FALSE,
    api_access BOOLEAN DEFAULT FALSE,
    multi_location_support BOOLEAN DEFAULT FALSE,
    inventory_management BOOLEAN DEFAULT FALSE,
    customer_insights BOOLEAN DEFAULT FALSE,
    marketplace_integration BOOLEAN DEFAULT FALSE,
    social_media_integration BOOLEAN DEFAULT FALSE,
    gst_compliance BOOLEAN DEFAULT FALSE,
    invoice_generation BOOLEAN DEFAULT FALSE,
    payment_gateway BOOLEAN DEFAULT FALSE,
    shipping_integration BOOLEAN DEFAULT FALSE,
    return_management BOOLEAN DEFAULT FALSE,
    loyalty_program BOOLEAN DEFAULT FALSE,
    search_ranking INTEGER,
    storage_limit INTEGER,
    bandwidth_limit INTEGER,
    api_call_limit INTEGER,
    setup_fee NUMERIC(10,2),
    monthly_price NUMERIC(10,2),
    yearly_price NUMERIC(10,2),
    trial_days INTEGER,
    offer_text VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    is_popular BOOLEAN DEFAULT FALSE,
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_vendor_packages_active ON vendor_packages(is_active);
CREATE INDEX IF NOT EXISTS idx_vendor_packages_plan ON vendor_packages(plan_type);
CREATE INDEX IF NOT EXISTS idx_vendor_packages_sort ON vendor_packages(sort_order);

-- vendor_package_features
CREATE TABLE IF NOT EXISTS vendor_package_features (
    id BIGSERIAL PRIMARY KEY,
    vendor_package_id BIGINT NOT NULL REFERENCES vendor_packages(id) ON DELETE CASCADE,
    feature_name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    feature_type VARCHAR(50),
    value VARCHAR(255),
    is_included BOOLEAN DEFAULT TRUE,
    is_highlighted BOOLEAN DEFAULT FALSE,
    display_order INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_vpf_package ON vendor_package_features(vendor_package_id);
CREATE INDEX IF NOT EXISTS idx_vpf_order ON vendor_package_features(display_order);
