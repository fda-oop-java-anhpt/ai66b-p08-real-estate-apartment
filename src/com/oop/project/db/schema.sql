-- set up
drop database if exists real_estate;
create database real_estate;
use real_estate;

-- create tables
CREATE TABLE `users` (
  `username` varchar(255) primary key,
  `password_hash` varchar(64) not null , -- hash the original pw
  `role` varchar(255) not null, -- admin, agent(viewer)
  constraint chk_role check(`role` in ('admin', 'agent')),
 `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `last_login` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `apartment` (
  `apartment_id` int PRIMARY KEY AUTO_INCREMENT,
  `address` varchar(255) not null,
  `city` varchar(255) not null,
  `price` float,
  constraint chk_pos_price check (price > 0),
  `bedrooms` int,
  constraint chk_pos_bed check (bedrooms > 0),
  `size` float,
  constraint chk_pos_size check (size > 0),
  `category` varchar(255),
  constraint chk_cate check (category in ('luxury', 'standard', 'budget')),
  `status` varchar(10) not null default 'empty',
  constraint chk_status check (status in ('empty', 'rented')),
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

CREATE TABLE universal_log (
    log_id          INT PRIMARY KEY AUTO_INCREMENT,
    table_name      VARCHAR(255) NOT NULL,
    action_type     VARCHAR(255) NOT NULL,   -- 'INSERT', 'UPDATE', 'DELETE'
    record_id       INT,                      -- primary key of affected row (if applicable)
    username        VARCHAR(255),             -- who performed the action
    role            VARCHAR(255),             -- role at the time of action
    action_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (username) REFERENCES users(username)
);

CREATE TABLE `login_history` (
    `login_id` int PRIMARY KEY AUTO_INCREMENT,
    `username` varchar(255),
    `role` varchar(255) NOT NULL,
    `log_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`username`) REFERENCES `users` (`username`)
);

-- foreign key
ALTER TABLE `apartmentAmenities`
  ADD FOREIGN KEY (`apartment_id`) REFERENCES `apartment` (`apartment_id`),
  ADD FOREIGN KEY (`amenity_id`) REFERENCES `amenities` (`amenity_id`);

ALTER TABLE `universal_log`
  ADD FOREIGN KEY (`username`) REFERENCES `users` (`username`);

ALTER TABLE `favourites`
  ADD FOREIGN KEY (`username`) REFERENCES `users` (`username`),
  ADD FOREIGN KEY (`apartment_id`) REFERENCES `apartment` (`apartment_id`);

ALTER TABLE `notes`
  ADD FOREIGN KEY (`username`) REFERENCES `users` (`username`),
  ADD FOREIGN KEY (`apartment_id`) REFERENCES `apartment` (`apartment_id`);

ALTER TABLE `login_history`
  ADD FOREIGN KEY (`username`) REFERENCES `users` (`username`);
