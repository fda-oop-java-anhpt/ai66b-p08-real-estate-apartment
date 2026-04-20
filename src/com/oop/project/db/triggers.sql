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
