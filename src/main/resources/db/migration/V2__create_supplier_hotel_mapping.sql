-- Create supplier_hotel_mapping table
-- Compatible with both H2 (PostgreSQL mode) and PostgreSQL
CREATE TABLE supplier_hotel_mapping (
    hotel_id VARCHAR(255) NOT NULL,
    supplier_code VARCHAR(255) NOT NULL,
    supplier_hotel_id VARCHAR(255),
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (hotel_id, supplier_code)
);

