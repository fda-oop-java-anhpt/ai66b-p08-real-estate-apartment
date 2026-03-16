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

DELIMITER $$

-- Log INSERT on notes
CREATE TRIGGER after_notes_insert
AFTER INSERT ON notes
FOR EACH ROW
BEGIN
    INSERT INTO logs (username, action, table_name)
    VALUES (NEW.username, 'INSERT', 'notes');
END$$

-- Log UPDATE on notes
CREATE TRIGGER after_notes_update
AFTER UPDATE ON notes
FOR EACH ROW
BEGIN
    INSERT INTO logs (username, action, table_name)
    VALUES (NEW.username, 'UPDATE', 'notes');
END$$

-- Log DELETE on notes
CREATE TRIGGER after_notes_delete
AFTER DELETE ON notes
FOR EACH ROW
BEGIN
    INSERT INTO logs (username, action, table_name)
    VALUES (OLD.username, 'DELETE', 'notes');
END$$

-- Log INSERT on amenities
CREATE TRIGGER after_amenities_insert
AFTER INSERT ON amenities
FOR EACH ROW
BEGIN
    INSERT INTO logs (username, action, table_name)
    VALUES (NEW.username, 'INSERT', 'amenities');
END$$

-- Log UPDATE on amenities
CREATE TRIGGER after_amenities_update
AFTER UPDATE ON amenities
FOR EACH ROW
BEGIN
    INSERT INTO logs (username, action, table_name)
    VALUES (NEW.username, 'UPDATE', 'amenities');
END$$

-- Log DELETE on amenities
CREATE TRIGGER after_amenities_delete
AFTER DELETE ON amenities
FOR EACH ROW
BEGIN
    INSERT INTO logs (username, action, table_name)
    VALUES (OLD.username, 'DELETE', 'amenities');
END$$

-- Log INSERT on apartment
CREATE TRIGGER after_apartment_insert
AFTER INSERT ON apartment
FOR EACH ROW
BEGIN
    INSERT INTO logs (username, action, table_name)
    VALUES (NEW.username, 'INSERT', 'apartment');
END$$

-- Log UPDATE on apartment
CREATE TRIGGER after_apartment_update
AFTER UPDATE ON apartment
FOR EACH ROW
BEGIN
    INSERT INTO logs (username, action, table_name)
    VALUES (NEW.username, 'UPDATE', 'apartment');
END$$

-- Log DELETE on apartment
CREATE TRIGGER after_apartment_delete
AFTER DELETE ON apartment
FOR EACH ROW
BEGIN
    INSERT INTO logs (username, action, table_name)
    VALUES (OLD.username, 'DELETE', 'apartment');
END$$

-- Log INSERT on apartmentamenities
CREATE TRIGGER after_apartmentamenities_insert
AFTER INSERT ON apartmentamenities
FOR EACH ROW
BEGIN
    INSERT INTO logs (username, action, table_name)
    VALUES (NEW.username, 'INSERT', 'apartmentAmenities');
END$$

-- Log UPDATE on apartmentamenities
CREATE TRIGGER after_apartmentamenities_update
AFTER UPDATE ON apartmentamenities
FOR EACH ROW
BEGIN
    INSERT INTO logs (username, action, table_name)
    VALUES (NEW.username, 'UPDATE', 'apartmentAmenities');
END$$

-- Log DELETE on apartmentamenities
CREATE TRIGGER after_apartmentamenities_delete
AFTER DELETE ON apartmentamenities
FOR EACH ROW
BEGIN
    INSERT INTO logs (username, action, table_name)
    VALUES (OLD.username, 'DELETE', 'apartmentAmenities');
END$$

DELIMITER ;

