-- set up
drop database if exists real_estate;
create database real_estate;
use real_estate;

-- create tables
CREATE TABLE `user` (
  `username` varchar(255) primary key,
  `password_hash` varchar(64) not null , -- hash the original pw
  `role` varchar(255) not null, -- admin, agent, client
  constraint chk_role check(role in ('admin', 'agent', 'client')),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `last_login` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `apartment` (
  `apartment_id` int PRIMARY KEY AUTO_INCREMENT,
  `address` varchar(255) not null,
  `city` varchar(255) not null,
  `price` float, 
  constraint chk_pos_price check (price > 0) ,
  `bedrooms` int,
  constraint chk_pos_bed check (bedrooms > 0),
  `size` float,
  constraint chk_pos_size check (size > 0),
  `category` varchar(255), -- luxury, standard, budget
  constraint chk_cate check (category in ('luxury', 'standard', 'budget')),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `amenities` (
  `amenity_id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(255) -- sth like pool, tennis yard,...
);

CREATE TABLE `apartmentAmenities` ( -- this is to connect appartment and amenities table
  `apartment_id` int,
  `amenity_id` int
);

CREATE TABLE `favourites` (
  `username` varchar(255),
  `apartment_id` int,
  primary key (username, apartment_id),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `notes` ( 
  `note_id` int PRIMARY KEY AUTO_INCREMENT,
  `username` varchar(255),
  `apartment_id` int,
  `content` varchar(255),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `logs` ( -- alter db history
  `log_id` int PRIMARY KEY AUTO_INCREMENT,
  `username` varchar(255),
  `action` varchar(255),
  `table_name` varchar(255),
  constraint chk_tbl check (`table_name` in ('user', 'apartment', 'notes', 
		'favourites', 'apartmentAmenities', 'amenities', 'login_history')),
  `timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table `login_history` (
	`login_id` int primary key auto_increment,
	`username` varchar(255),
    `log_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- foreign key
ALTER TABLE `apartmentAmenities`
  ADD FOREIGN KEY (`apartment_id`) REFERENCES `apartment` (`apartment_id`),
  ADD FOREIGN KEY (`amenity_id`) REFERENCES `amenities` (`amenity_id`);

ALTER TABLE `logs`
  ADD FOREIGN KEY (`username`) REFERENCES `user` (`username`);

ALTER TABLE `favourites`
  ADD FOREIGN KEY (`username`) REFERENCES `user` (`username`),
  ADD FOREIGN KEY (`apartment_id`) REFERENCES `apartment` (`apartment_id`);

ALTER TABLE `notes`
  ADD FOREIGN KEY (`username`) REFERENCES `user` (`username`),
  ADD FOREIGN KEY (`apartment_id`) REFERENCES `apartment` (`apartment_id`);

ALTER TABLE `login_history`
  ADD FOREIGN KEY (`username`) REFERENCES `user` (`username`);


