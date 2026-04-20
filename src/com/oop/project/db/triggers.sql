DELIMITER $$

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

DELIMITER //

-- ========== APARTMENT TRIGGERS ==========
CREATE TRIGGER log_apartment_insert
AFTER INSERT ON apartment
FOR EACH ROW
BEGIN
    DECLARE user_role VARCHAR(255);
    SELECT role INTO user_role FROM users WHERE username = @current_username;
    INSERT INTO universal_log (table_name, action_type, record_id, username, role)
    VALUES ('apartment', 'INSERT', NEW.apartment_id, @current_username, user_role);
END //

CREATE TRIGGER log_apartment_update
AFTER UPDATE ON apartment
FOR EACH ROW
BEGIN
    DECLARE user_role VARCHAR(255);
    SELECT role INTO user_role FROM users WHERE username = @current_username;
    INSERT INTO universal_log (table_name, action_type, record_id, username, role)
    VALUES ('apartment', 'UPDATE', NEW.apartment_id, @current_username, user_role);
END //

CREATE TRIGGER log_apartment_delete
AFTER DELETE ON apartment
FOR EACH ROW
BEGIN
    DECLARE user_role VARCHAR(255);
    SELECT role INTO user_role FROM users WHERE username = @current_username;
    INSERT INTO universal_log (table_name, action_type, record_id, username, role)
    VALUES ('apartment', 'DELETE', OLD.apartment_id, @current_username, user_role);
END //

-- ========== USERS TRIGGERS ==========
CREATE TRIGGER log_users_insert
AFTER INSERT ON users
FOR EACH ROW
BEGIN
    DECLARE user_role VARCHAR(255);
    SELECT role INTO user_role FROM users WHERE username = @current_username;
    INSERT INTO universal_log (table_name, action_type, record_id, username, role)
    VALUES ('users', 'INSERT', NULL, @current_username, user_role);
END //

CREATE TRIGGER log_users_update
AFTER UPDATE ON users
FOR EACH ROW
BEGIN
    DECLARE user_role VARCHAR(255);
    SELECT role INTO user_role FROM users WHERE username = @current_username;
    INSERT INTO universal_log (table_name, action_type, record_id, username, role)
    VALUES ('users', 'UPDATE', NULL, @current_username, user_role);
END //

CREATE TRIGGER log_users_delete
AFTER DELETE ON users
FOR EACH ROW
BEGIN
    DECLARE user_role VARCHAR(255);
    SELECT role INTO user_role FROM users WHERE username = @current_username;
    INSERT INTO universal_log (table_name, action_type, record_id, username, role)
    VALUES ('users', 'DELETE', NULL, @current_username, user_role);
END //

-- ========== NOTES TRIGGERS ==========
CREATE TRIGGER log_notes_insert
AFTER INSERT ON notes
FOR EACH ROW
BEGIN
    DECLARE user_role VARCHAR(255);
    SELECT role INTO user_role FROM users WHERE username = @current_username;
    INSERT INTO universal_log (table_name, action_type, record_id, username, role)
    VALUES ('notes', 'INSERT', NEW.note_id, @current_username, user_role);
END //

CREATE TRIGGER log_notes_update
AFTER UPDATE ON notes
FOR EACH ROW
BEGIN
    DECLARE user_role VARCHAR(255);
    SELECT role INTO user_role FROM users WHERE username = @current_username;
    INSERT INTO universal_log (table_name, action_type, record_id, username, role)
    VALUES ('notes', 'UPDATE', NEW.note_id, @current_username, user_role);
END //

CREATE TRIGGER log_notes_delete
AFTER DELETE ON notes
FOR EACH ROW
BEGIN
    DECLARE user_role VARCHAR(255);
    SELECT role INTO user_role FROM users WHERE username = @current_username;
    INSERT INTO universal_log (table_name, action_type, record_id, username, role)
    VALUES ('notes', 'DELETE', OLD.note_id, @current_username, user_role);
END //

-- ========== FAVOURITES TRIGGERS ==========
CREATE TRIGGER log_favourites_insert
AFTER INSERT ON favourites
FOR EACH ROW
BEGIN
    DECLARE user_role VARCHAR(255);
    SELECT role INTO user_role FROM users WHERE username = @current_username;
    INSERT INTO universal_log (table_name, action_type, record_id, username, role)
    VALUES ('favourites', 'INSERT', NULL, @current_username, user_role);
END //

CREATE TRIGGER log_favourites_delete
AFTER DELETE ON favourites
FOR EACH ROW
BEGIN
    DECLARE user_role VARCHAR(255);
    SELECT role INTO user_role FROM users WHERE username = @current_username;
    INSERT INTO universal_log (table_name, action_type, record_id, username, role)
    VALUES ('favourites', 'DELETE', NULL, @current_username, user_role);
END //

-- ========== AMENITIES TRIGGERS ==========
CREATE TRIGGER log_amenities_insert
AFTER INSERT ON amenities
FOR EACH ROW
BEGIN
    DECLARE user_role VARCHAR(255);
    SELECT role INTO user_role FROM users WHERE username = @current_username;
    INSERT INTO universal_log (table_name, action_type, record_id, username, role)
    VALUES ('amenities', 'INSERT', NEW.amenity_id, @current_username, user_role);
END //

CREATE TRIGGER log_amenities_update
AFTER UPDATE ON amenities
FOR EACH ROW
BEGIN
    DECLARE user_role VARCHAR(255);
    SELECT role INTO user_role FROM users WHERE username = @current_username;
    INSERT INTO universal_log (table_name, action_type, record_id, username, role)
    VALUES ('amenities', 'UPDATE', NEW.amenity_id, @current_username, user_role);
END //

CREATE TRIGGER log_amenities_delete
AFTER DELETE ON amenities
FOR EACH ROW
BEGIN
    DECLARE user_role VARCHAR(255);
    SELECT role INTO user_role FROM users WHERE username = @current_username;
    INSERT INTO universal_log (table_name, action_type, record_id, username, role)
    VALUES ('amenities', 'DELETE', OLD.amenity_id, @current_username, user_role);
END //

-- ========== APARTMENT_AMENITIES TRIGGERS ==========
CREATE TRIGGER log_apartmentamenities_insert
AFTER INSERT ON apartmentAmenities
FOR EACH ROW
BEGIN
    DECLARE user_role VARCHAR(255);
    SELECT role INTO user_role FROM users WHERE username = @current_username;
    INSERT INTO universal_log (table_name, action_type, record_id, username, role)
    VALUES ('apartmentAmenities', 'INSERT', NULL, @current_username, user_role);
END //

CREATE TRIGGER log_apartmentamenities_delete
AFTER DELETE ON apartmentAmenities
FOR EACH ROW
BEGIN
    DECLARE user_role VARCHAR(255);
    SELECT role INTO user_role FROM users WHERE username = @current_username;
    INSERT INTO universal_log (table_name, action_type, record_id, username, role)
    VALUES ('apartmentAmenities', 'DELETE', NULL, @current_username, user_role);
END //

DELIMITER ;
