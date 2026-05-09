DELIMITER //

-- 1.1 Insert User (Có kiểm tra trùng username)
CREATE PROCEDURE sp_insert_user(
    IN p_username VARCHAR(255),
    IN p_password_hash VARCHAR(64),
    IN p_role VARCHAR(255)
)
BEGIN
    IF EXISTS (SELECT 1 FROM users WHERE username = p_username) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Username already exists';
    ELSE
        INSERT INTO users (username, password_hash, role) 
        VALUES (p_username, p_password_hash, p_role);
    END IF;
END //

-- 1.2 Update User Password/Role
CREATE PROCEDURE sp_update_user(
    IN p_username VARCHAR(255),
    IN p_new_password_hash VARCHAR(64),
    IN p_new_role VARCHAR(255)
)
BEGIN
    UPDATE users 
    SET password_hash = p_new_password_hash, 
        role = p_new_role 
    WHERE username = p_username;
END //

-- 1.3 Delete User
CREATE PROCEDURE sp_delete_user(IN p_username VARCHAR(255))
BEGIN
    DELETE FROM users WHERE username = p_username;
END //

DELIMITER ;

DELIMITER //

-- 2.1 Insert Apartment (Tự động tính Category)
CREATE PROCEDURE sp_insert_apartment(
    IN p_address VARCHAR(255),
    IN p_city VARCHAR(255),
    IN p_price FLOAT,
    IN p_bedrooms INT,
    IN p_size FLOAT
)
BEGIN
    DECLARE v_category VARCHAR(255);
    
    -- Logic FR-4.3: Tạm tính Luxury > 10 tỷ, Budget < 2 tỷ (Bạn có thể sửa số)
    IF p_price >= 10 THEN SET v_category = 'luxury';
    ELSEIF p_price <= 2 THEN SET v_category = 'budget';
    ELSE SET v_category = 'standard';
    END IF;

    INSERT INTO apartment (address, city, price, bedrooms, size, category)
    VALUES (p_address, p_city, p_price, p_bedrooms, p_size, v_category);
END //

-- 2.2 Update Apartment
CREATE PROCEDURE sp_update_apartment(
    IN p_id INT,
    IN p_address VARCHAR(255),
    IN p_city VARCHAR(255),
    IN p_price FLOAT,
    IN p_bedrooms INT,
    IN p_size FLOAT
)
BEGIN
    DECLARE v_category VARCHAR(255);
    
    IF p_price >= 10 THEN SET v_category = 'luxury';
    ELSEIF p_price <= 2 THEN SET v_category = 'budget';
    ELSE SET v_category = 'standard';
    END IF;

    UPDATE apartment 
    SET address = p_address, 
        city = p_city, 
        price = p_price, 
        bedrooms = p_bedrooms, 
        size = p_size, 
        category = v_category
    WHERE apartment_id = p_id;
END //

-- 2.3 Delete Apartment
CREATE PROCEDURE sp_delete_apartment(IN p_id INT)
BEGIN
    DELETE FROM apartment WHERE apartment_id = p_id;
END //

DELIMITER ;

DELIMITER //

-- 3.1 Insert Amenity
CREATE PROCEDURE sp_insert_amenity(IN p_name VARCHAR(255))
BEGIN
    IF NOT EXISTS (SELECT 1 FROM amenities WHERE name = p_name) THEN
        INSERT INTO amenities (name) VALUES (p_name);
    END IF;
END //

-- 3.2 Update Amenity
CREATE PROCEDURE sp_update_amenity(IN p_id INT, IN p_name VARCHAR(255))
BEGIN
    UPDATE amenities SET name = p_name WHERE amenity_id = p_id;
END //

-- 3.3 Delete Amenity
CREATE PROCEDURE sp_delete_amenity(IN p_id INT)
BEGIN
    DELETE FROM amenities WHERE amenity_id = p_id;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE sp_link_amenity_to_apartment(
    IN p_apartment_id INT,
    IN p_amenity_id INT
)
BEGIN
    -- Tránh insert trùng lặp
    IF NOT EXISTS (SELECT 1 FROM apartmentAmenities 
                   WHERE apartment_id = p_apartment_id AND amenity_id = p_amenity_id) THEN
        INSERT INTO apartmentAmenities (apartment_id, amenity_id) 
        VALUES (p_apartment_id, p_amenity_id);
    END IF;
END //

DELIMITER ;