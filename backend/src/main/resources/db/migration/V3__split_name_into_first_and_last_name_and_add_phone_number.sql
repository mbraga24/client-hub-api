ALTER TABLE customer DROP COLUMN IF EXISTS name;

ALTER TABLE customer ADD COLUMN app_user_id BIGINT NOT NULL UNIQUE;
ALTER TABLE customer ADD COLUMN first_name VARCHAR(255) NOT NULL;
ALTER TABLE customer ADD COLUMN last_name VARCHAR(255) NOT NULL;

ALTER TABLE customer ADD COLUMN username VARCHAR(255) NOT NULL;
ALTER TABLE customer ADD COLUMN phone_number VARCHAR(20) NOT NULL;

ALTER TABLE customer
ADD CONSTRAINT customer_username_unique
UNIQUE (username);

ALTER TABLE customer
ADD CONSTRAINT customer_phone_number_unique
UNIQUE (phone_number);