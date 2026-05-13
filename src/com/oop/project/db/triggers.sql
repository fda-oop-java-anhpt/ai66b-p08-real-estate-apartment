DELIMITER $$

-- =========================================================
-- Timestamp auto‑update triggers
-- =========================================================
CREATE TRIGGER before_apartment_update
BEFORE UPDATE ON apartment
FOR EACH ROW
BEGIN
    SET NEW.updated_at = NOW();
END$$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER before_notes_update
BEFORE UPDATE ON notes
FOR EACH ROW
BEGIN
    SET NEW.updated_at = NOW();
END$$

DELIMITER ;

-- =========================================================
-- Cascading delete triggers
-- =========================================================
DELIMITER //
CREATE TRIGGER delamen_apar
BEFORE DELETE ON amenities
FOR EACH ROW
BEGIN
    DELETE FROM apartmentAmenities
    WHERE amenity_id = OLD.amenity_id;
END //
DELIMITER ;

DELIMITER //
CREATE TRIGGER delapar_amen
BEFORE DELETE ON apartment
FOR EACH ROW
BEGIN
    DELETE FROM apartmentAmenities
    WHERE apartment_id = OLD.apartment_id;
END //
DELIMITER ;

DELIMITER //
CREATE TRIGGER delapar_fav
BEFORE DELETE ON apartment
FOR EACH ROW
BEGIN
    DELETE FROM favourites
    WHERE apartment_id = OLD.apartment_id;
END //
DELIMITER ;

DELIMITER //
CREATE TRIGGER delapar_note
BEFORE DELETE ON apartment
FOR EACH ROW
BEGIN
    DELETE FROM notes
    WHERE apartment_id = OLD.apartment_id;
END //
DELIMITER ;

-- =========================================================
-- Login history trigger
-- =========================================================
DELIMITER //
CREATE TRIGGER after_user_login_update
AFTER UPDATE ON users
FOR EACH ROW
BEGIN
    IF NEW.last_login <> OLD.last_login THEN
        INSERT INTO login_history (username, role) VALUES (NEW.username, NEW.role);
    END IF;
END //
DELIMITER ;

-- =========================================================
-- Audit log triggers – apartment (INSERT, UPDATE, DELETE)
-- =========================================================
DELIMITER //

-- INSERT apartment
CREATE TRIGGER log_apartment_insert
AFTER INSERT ON apartment
FOR EACH ROW
BEGIN
    DECLARE user_role VARCHAR(255);
    SELECT role INTO user_role FROM users WHERE username = @current_username;
    INSERT INTO universal_log (table_name, action_type, record_id, username, role, content)
    VALUES ('apartment', 'INSERT', NEW.apartment_id, @current_username, user_role,
            CONCAT('Created apartment: ', NEW.address, ', ', NEW.city,
                   ' | Price: ', NEW.price, 'B | Bedrooms: ', NEW.bedrooms,
                   ' | Size: ', NEW.size, 'm² | Category: ', NEW.category,
                   ' | Status: ', NEW.status));
END //

-- UPDATE apartment
CREATE TRIGGER log_apartment_update
AFTER UPDATE ON apartment
FOR EACH ROW
BEGIN
    DECLARE user_role VARCHAR(255);
    DECLARE changes TEXT DEFAULT '';
    SELECT role INTO user_role FROM users WHERE username = @current_username;

    IF OLD.address <> NEW.address THEN
        SET changes = CONCAT(changes, 'Address: "', OLD.address, '" → "', NEW.address, '"; ');
    END IF;
    IF OLD.city <> NEW.city THEN
        SET changes = CONCAT(changes, 'City: "', OLD.city, '" → "', NEW.city, '"; ');
    END IF;
    IF OLD.price <> NEW.price THEN
        SET changes = CONCAT(changes, 'Price: ', OLD.price, ' → ', NEW.price, '; ');
    END IF;
    IF OLD.bedrooms <> NEW.bedrooms THEN
        SET changes = CONCAT(changes, 'Bedrooms: ', OLD.bedrooms, ' → ', NEW.bedrooms, '; ');
    END IF;
    IF OLD.size <> NEW.size THEN
        SET changes = CONCAT(changes, 'Size: ', OLD.size, ' → ', NEW.size, '; ');
    END IF;
    IF OLD.category <> NEW.category THEN
        SET changes = CONCAT(changes, 'Category: "', OLD.category, '" → "', NEW.category, '"; ');
    END IF;
    IF OLD.status <> NEW.status THEN
        SET changes = CONCAT(changes, 'Status: "', OLD.status, '" → "', NEW.status, '"; ');
    END IF;

    IF changes = '' THEN
        SET changes = 'No changes detected (updated_at only).';
    END IF;

    INSERT INTO universal_log (table_name, action_type, record_id, username, role, content)
    VALUES ('apartment', 'UPDATE', NEW.apartment_id, @current_username, user_role,
            CONCAT('Updated apartment #', NEW.apartment_id, ' | ', changes));
END //

-- DELETE apartment
CREATE TRIGGER log_apartment_delete
AFTER DELETE ON apartment
FOR EACH ROW
BEGIN
    DECLARE user_role VARCHAR(255);
    SELECT role INTO user_role FROM users WHERE username = @current_username;
    INSERT INTO universal_log (table_name, action_type, record_id, username, role, content)
    VALUES ('apartment', 'DELETE', OLD.apartment_id, @current_username, user_role,
            CONCAT('Deleted apartment: ', OLD.address, ', ', OLD.city,
                   ' | Price: ', OLD.price, 'B | Bedrooms: ', OLD.bedrooms));
END //

-- =========================================================
-- Audit log triggers – users (INSERT, DELETE only)
-- =========================================================

-- INSERT user
CREATE TRIGGER log_users_insert
AFTER INSERT ON users
FOR EACH ROW
BEGIN
    DECLARE user_role VARCHAR(255);
    SELECT role INTO user_role FROM users WHERE username = @current_username;
    INSERT INTO universal_log (table_name, action_type, record_id, username, role, content)
    VALUES ('users', 'INSERT', NULL, @current_username, user_role,
            CONCAT('Created user: "', NEW.username, '" with role "', NEW.role, '"'));
END //

-- DELETE user
CREATE TRIGGER log_users_delete
AFTER DELETE ON users
FOR EACH ROW
BEGIN
    DECLARE user_role VARCHAR(255);
    SELECT role INTO user_role FROM users WHERE username = @current_username;
    INSERT INTO universal_log (table_name, action_type, record_id, username, role, content)
    VALUES ('users', 'DELETE', NULL, @current_username, user_role,
            CONCAT('Deleted user "', OLD.username, '" (role: ', OLD.role, ')'));
END //

DELIMITER ;


-- =========================================================
-- Audit log triggers – apartmentAmenities (INSERT, DELETE)
-- =========================================================
DELIMITER //

-- INSERT into apartmentAmenities
CREATE TRIGGER log_apartmentAmenities_insert
AFTER INSERT ON apartmentAmenities
FOR EACH ROW
BEGIN
    DECLARE user_role VARCHAR(255);
    DECLARE amenity_name VARCHAR(255);
    
    -- Get current user's role
    SELECT role INTO user_role FROM users WHERE username = @current_username;
    
    -- Get amenity name
    SELECT name INTO amenity_name FROM amenities WHERE amenity_id = NEW.amenity_id;
    
    INSERT INTO universal_log (table_name, action_type, record_id, username, role, content)
    VALUES ('apartmentAmenities', 'INSERT', NEW.apartment_id, @current_username, user_role,
            CONCAT('Added amenity "', amenity_name, '" (ID ', NEW.amenity_id, ') to apartment #', NEW.apartment_id));
END //

-- DELETE from apartmentAmenities
CREATE TRIGGER log_apartmentAmenities_delete
AFTER DELETE ON apartmentAmenities
FOR EACH ROW
BEGIN
    DECLARE user_role VARCHAR(255);
    DECLARE amenity_name VARCHAR(255);
    
    SELECT role INTO user_role FROM users WHERE username = @current_username;
    SELECT name INTO amenity_name FROM amenities WHERE amenity_id = OLD.amenity_id;
    
    INSERT INTO universal_log (table_name, action_type, record_id, username, role, content)
    VALUES ('apartmentAmenities', 'DELETE', OLD.apartment_id, @current_username, user_role,
            CONCAT('Removed amenity "', amenity_name, '" (ID ', OLD.amenity_id, ') from apartment #', OLD.apartment_id));
END //

DELIMITER ;
