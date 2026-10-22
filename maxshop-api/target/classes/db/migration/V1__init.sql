CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(150)        NOT NULL,
    email           VARCHAR(180)        NOT NULL UNIQUE,
    password_hash   VARCHAR(255)        NOT NULL,
    role            VARCHAR(20)         NOT NULL DEFAULT 'CLIENTE',
    phone           VARCHAR(30),
    enabled         BOOLEAN             NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP           NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP           NOT NULL DEFAULT now()
);

CREATE TABLE seller_profiles (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID                NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    store_name      VARCHAR(150)        NOT NULL,
    document        VARCHAR(20)         NOT NULL UNIQUE,
    approved        BOOLEAN             NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP           NOT NULL DEFAULT now()
);

CREATE TABLE categories (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(100)        NOT NULL,
    slug            VARCHAR(120)        NOT NULL UNIQUE,
    parent_id       UUID                REFERENCES categories(id)
);

CREATE TABLE products (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    seller_id       UUID                NOT NULL REFERENCES users(id),
    category_id     UUID                NOT NULL REFERENCES categories(id),
    name            VARCHAR(200)        NOT NULL,
    slug            VARCHAR(220)        NOT NULL UNIQUE,
    description     TEXT,
    price           NUMERIC(12,2)       NOT NULL,
    compare_at_price NUMERIC(12,2),
    stock_quantity  INTEGER             NOT NULL DEFAULT 0,
    active          BOOLEAN             NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP           NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP           NOT NULL DEFAULT now()
);

CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_seller ON products(seller_id);
CREATE INDEX idx_products_name_trgm ON products USING gin (to_tsvector('portuguese', name));

CREATE TABLE product_images (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id      UUID                NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    url             VARCHAR(500)        NOT NULL,
    position        INTEGER             NOT NULL DEFAULT 0
);

CREATE TABLE cart_items (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID                NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    product_id      UUID                NOT NULL REFERENCES products(id),
    quantity        INTEGER             NOT NULL DEFAULT 1,
    created_at      TIMESTAMP           NOT NULL DEFAULT now(),
    UNIQUE (user_id, product_id)
);

CREATE TABLE orders (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id     UUID                NOT NULL REFERENCES users(id),
    status          VARCHAR(20)         NOT NULL DEFAULT 'PENDENTE',
    subtotal        NUMERIC(12,2)       NOT NULL,
    shipping_cost   NUMERIC(12,2)       NOT NULL DEFAULT 0,
    discount        NUMERIC(12,2)       NOT NULL DEFAULT 0,
    total           NUMERIC(12,2)       NOT NULL,
    shipping_address TEXT               NOT NULL,
    payment_method  VARCHAR(20)         NOT NULL,
    created_at      TIMESTAMP           NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP           NOT NULL DEFAULT now()
);

CREATE INDEX idx_orders_customer ON orders(customer_id);

CREATE TABLE order_items (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id        UUID                NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id      UUID                NOT NULL REFERENCES products(id),
    seller_id       UUID                NOT NULL REFERENCES users(id),
    quantity        INTEGER             NOT NULL,
    unit_price      NUMERIC(12,2)       NOT NULL
);

CREATE TABLE reviews (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id      UUID                NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    customer_id     UUID                NOT NULL REFERENCES users(id),
    rating          SMALLINT            NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment         TEXT,
    created_at      TIMESTAMP           NOT NULL DEFAULT now(),
    UNIQUE (product_id, customer_id)
);

CREATE TABLE coupons (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code            VARCHAR(40)         NOT NULL UNIQUE,
    discount_percent NUMERIC(5,2),
    discount_amount NUMERIC(12,2),
    valid_from      TIMESTAMP           NOT NULL,
    valid_until     TIMESTAMP           NOT NULL,
    max_uses        INTEGER,
    used_count      INTEGER             NOT NULL DEFAULT 0,
    active          BOOLEAN             NOT NULL DEFAULT TRUE
);
