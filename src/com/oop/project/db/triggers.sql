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

delimiter //
create trigger delamen_apar
before delete on amenities
for each row
begin
	delete from apartmentamenities
    where amenity_id = old.amenity_id;
end // 
	
delimiter ;

delimiter //
create trigger delapar_amen
before delete on apartment
for each row 
begin
	delete from apartmentamenities
    where apartment_id = old.apartment_id;
end //
delimiter ;

delimiter //
create trigger delapar_fav
before delete on apartment
for each row
begin
	delete from favourites
    where apartment_id = old.apartment_id;
end //
delimiter ;

delimiter //
create trigger delapar_note
before delete on apartment
for each row
begin
	delete from notes
    where apartment_id = old.apartment_id;
end //
delimiter ;

-- testing section, delete later
insert into amenities(name) values ('sofa');
select * from notes;
drop trigger delamen_apar;

update notes set content = 'test update trigger' where note_id = 5;

update apartment set price = 20.4 where apartment_id = 1;

select * from apartmentamenities;
select * from notes;
select * from favourites;

delete from amenities where amenity_id = 4;

delete from apartment where apartment_id = 3;
