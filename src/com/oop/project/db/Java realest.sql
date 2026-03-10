CREATE TABLE `user` (
  `user_id` int PRIMARY KEY AUTO_INCREMENT,
  `username` varchar(255),
  `password_hash` int, -- hash the original pw
  `role` varchar(255), -- admin, agent, client
  `created_at` datetime,
  `last_login` datetime
);

CREATE TABLE `apartment` (
  `apartment_id` int PRIMARY KEY AUTO_INCREMENT,
  `address` varchar(255),
  `location` varchar(255),
  `price` float,
  `bedrooms` int,
  `size` float,
  `category` varchar(255), -- luxury, standard, budget
  `created_at` datetime,
  `updated_at` datetime
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
  `fav_id` int PRIMARY KEY AUTO_INCREMENT,
  `user_id` int,
  `apartment_id` int,
  `created_at` datetime
);

CREATE TABLE `notes` ( 
  `note_id` int PRIMARY KEY AUTO_INCREMENT,
  `user_id` int,
  `apartment_id` int,
  `content` varchar(255),
  `created_at` datetime,
  `updated_at` datetime
);

CREATE TABLE `logs` ( -- alter db history
  `log_id` int PRIMARY KEY AUTO_INCREMENT,
  `user_id` int,
  `action` varchar(255),
  `apartment_id` int,
  `timestamp` datetime
);

ALTER TABLE `apartment` ADD FOREIGN KEY (`apartment_id`) REFERENCES `apartmentAmenities` (`apartment_id`);

ALTER TABLE `amenities` ADD FOREIGN KEY (`amenity_id`) REFERENCES `apartmentAmenities` (`amenity_id`);

ALTER TABLE `user` ADD FOREIGN KEY (`user_id`) REFERENCES `logs` (`user_id`);

ALTER TABLE `apartment` ADD FOREIGN KEY (`apartment_id`) REFERENCES `logs` (`apartment_id`);

ALTER TABLE `user` ADD FOREIGN KEY (`user_id`) REFERENCES `favourites` (`user_id`);

ALTER TABLE `user` ADD FOREIGN KEY (`user_id`) REFERENCES `notes` (`user_id`);

ALTER TABLE `apartment` ADD FOREIGN KEY (`apartment_id`) REFERENCES `notes` (`apartment_id`);

ALTER TABLE `apartment` ADD FOREIGN KEY (`apartment_id`) REFERENCES `favourites` (`apartment_id`);
