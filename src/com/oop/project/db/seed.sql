-- ============================================================
-- Real Estate Management System – Expanded Seed Data
-- Version 2.0 | Schema with `content` column in universal_log
-- ============================================================

-- Note: Run schema.sql first to create tables and triggers.
-- Passwords are SHA‑256 hashes; see comments beside each user.

USE real_estate;

-- ============================================================
-- 1. Users (5 admins, 10 agents)
-- ============================================================
INSERT INTO users (username, password_hash, role) VALUES
-- Admins
('admin1', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'admin'), -- "password"
('admin2', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'admin'), -- "admin123"
('admin3', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'admin'), -- "123456"
('admin4', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'admin'), -- "password"
('admin5', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'admin'), -- "admin123"

-- Agents
('agent1', '65e84be33532fb784c48129675f9eff3a682b27168c0ea744b2cf58ee02337c5', 'agent'), -- "qwerty"
('agent2', '1c8bfe8f801d79745c4631d09fff36c82aa37fc4cce4fc946683d7b336b63032', 'agent'), -- "letmein"
('agent3', '280d44ab1e9f79b5cce2dd4f58f5fe91f0fbacdac9f7447dffc318ceb79f2d02', 'agent'), -- "welcome"
('agent4', '2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824', 'agent'), -- "hello"
('agent5', '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08', 'agent'), -- "test"
('agent6', '6ca13d52ca70c883e0f0bb101e425a89e8624de51db2d2392593af6a84118090', 'agent'), -- "abc123"
('agent7', '0b14d501a594442a01c6859541bcb3e8164d183d32937b851835442f69d5c94e', 'agent'), -- "password1"
('agent8', '65e84be33532fb784c48129675f9eff3a682b27168c0ea744b2cf58ee02337c5', 'agent'), -- "qwerty"
('agent9', '2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824', 'agent'), -- "hello"
('agent10','9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08', 'agent'); -- "test"

-- ============================================================
-- 2. Amenities (12 items)
-- ============================================================
INSERT INTO amenities (name) VALUES
('Swimming Pool'),
('Tennis Court'),
('Gym/Fitness Center'),
('Parking Garage'),
('Playground'),
('Garden'),
('Elevator'),
('Security System'),
('Balcony'),
('Air Conditioning'),
('Laundry Room'),
('Storage');

-- ============================================================
-- 3. Apartments (50 listings – 15 luxury, 20 standard, 15 budget)
--    Prices in billions VND, sizes in m².
-- ============================================================
INSERT INTO apartment (address, city, price, bedrooms, size, category, status) VALUES
-- === Luxury (15) ===
('123 Le Loi','Ho Chi Minh City',15.2,4,120,'luxury','rented'),
('45 Tran Hung Dao','Hanoi',18.5,5,150,'luxury','empty'),
('88 Vo Nguyen Giap','Da Nang',12.7,3,95,'luxury','rented'),
('12 Nguyen Hue','Ho Chi Minh City',20.1,5,160,'luxury','empty'),
('77 Ly Thuong Kiet','Hue',11.3,3,100,'luxury','rented'),
('56 Bach Dang','Da Nang',14.8,4,110,'luxury','empty'),
('9 Pham Van Dong','Nha Trang',13.6,3,105,'luxury','rented'),
('101 Nguyen Van Cu','Can Tho',10.9,3,90,'luxury','empty'),
('33 Tran Phu','Ha Long',16.4,4,130,'luxury','rented'),
('5 Le Thanh Ton','Ho Chi Minh City',19.0,5,155,'luxury','empty'),
('27 Hai Ba Trung','Hanoi',17.1,4,125,'luxury','rented'),
('66 Ba Trieu','Da Nang',14.2,3,98,'luxury','empty'),
('99 Phu Dong','Vung Tau',13.9,4,115,'luxury','rented'),
('14 Nguyen Thi Minh Khai','Ho Chi Minh City',21.5,5,170,'luxury','empty'),
('78 Cach Mang Thang Tam','Hanoi',18.9,5,145,'luxury','rented'),

-- === Standard (20) ===
('22 Nguyen Trai','Hanoi',5.2,2,70,'standard','rented'),
('67 Hoang Dieu','Da Nang',4.8,2,65,'standard','empty'),
('34 Le Duan','Hue',6.1,3,80,'standard','rented'),
('89 Tran Phu','Nha Trang',5.7,2,75,'standard','empty'),
('12 Nguyen Thi Minh Khai','Ho Chi Minh City',6.5,3,85,'standard','rented'),
('45 Phan Chu Trinh','Da Lat',4.9,2,68,'standard','empty'),
('78 Cach Mang Thang 8','Can Tho',5.3,2,72,'standard','rented'),
('23 Tran Hung Dao','Hoi An',6.0,3,82,'standard','empty'),
('56 Nguyen Van Linh','Da Nang',5.8,2,78,'standard','rented'),
('90 Le Loi','Hanoi',6.2,3,88,'standard','empty'),
('110 Ba Thang Hai','Ho Chi Minh City',7.0,3,95,'standard','rented'),
('35 Ly Thuong Kiet','Hue',5.5,2,70,'standard','empty'),
('47 Ton Duc Thang','Ha Long',6.3,3,90,'standard','rented'),
('82 Quang Trung','Nha Trang',5.9,2,76,'standard','empty'),
('118 Tran Hung Dao','Da Nang',6.8,3,92,'standard','rented'),
('25 Nguyen Cong Tru','Hanoi',5.1,2,68,'standard','empty'),
('66 Pham Ngu Lao','Ho Chi Minh City',6.4,3,87,'standard','rented'),
('40 Le Loi','Hoi An',5.6,2,73,'standard','empty'),
('53 Hai Ba Trung','Da Lat',4.7,2,66,'standard','rented'),
('77 Nguyen Dinh Chieu','Vung Tau',6.1,3,81,'standard','empty'),

-- === Budget (15) ===
('12 Nguyen Van Troi','Ho Chi Minh City',2.1,1,40,'budget','rented'),
('34 Tran Cao Van','Da Nang',1.9,1,35,'budget','empty'),
('56 Nguyen Thai Hoc','Hue',2.3,1,42,'budget','rented'),
('78 Le Hong Phong','Nha Trang',2.5,2,50,'budget','empty'),
('90 Tran Quang Khai','Hanoi',2.0,1,38,'budget','rented'),
('11 Phan Dinh Phung','Da Lat',2.4,2,45,'budget','empty'),
('22 Nguyen Van Linh','Can Tho',2.2,1,36,'budget','rented'),
('33 Tran Hung Dao','Hoi An',2.6,2,48,'budget','empty'),
('44 Ly Thuong Kiet','Ha Long',2.1,1,37,'budget','rented'),
('55 Nguyen Trai','Ho Chi Minh City',2.7,2,52,'budget','empty'),
('99 Pham Van Dong','Vung Tau',2.3,1,39,'budget','rented'),
('67 Ho Tung Mau','Hanoi',1.8,1,33,'budget','empty'),
('80 Le Duan','Hue',2.4,2,44,'budget','rented'),
('15 Tran Phu','Da Nang',2.0,1,34,'budget','empty'),
('28 Nguyen Cong Tru','Nha Trang',2.8,2,55,'budget','rented');

-- ============================================================
-- 4. Apartment–Amenity Assignments (many‑to‑many)
-- ============================================================
INSERT INTO apartmentAmenities (apartment_id, amenity_id) VALUES
-- Luxury apartments (1‑15): each gets 5‑8 amenities
(1,1),(1,2),(1,3),(1,4),(1,8),
(2,1),(2,3),(2,4),(2,7),(2,8),(2,9),(2,10),
(3,1),(3,3),(3,6),(3,8),(3,9),
(4,1),(4,2),(4,3),(4,4),(4,5),(4,7),(4,8),(4,9),(4,10),
(5,1),(5,3),(5,4),(5,6),(5,7),
(6,1),(6,4),(6,6),(6,8),(6,9),(6,10),
(7,1),(7,2),(7,3),(7,4),(7,7),(7,8),(7,11),
(8,1),(8,4),(8,5),(8,8),(8,9),
(9,1),(9,2),(9,3),(9,6),(9,7),(9,8),(9,10),(9,12),
(10,1),(10,2),(10,3),(10,4),(10,6),(10,8),(10,9),(10,11),
(11,1),(11,3),(11,4),(11,5),(11,7),(11,8),
(12,1),(12,2),(12,3),(12,4),(12,6),(12,9),(12,10),
(13,1),(13,3),(13,4),(13,6),(13,7),(13,8),
(14,1),(14,2),(14,3),(14,4),(14,5),(14,6),(14,7),(14,8),(14,9),
(15,1),(15,2),(15,3),(15,4),(15,6),(15,7),(15,8),(15,11),

-- Standard apartments (16‑35): each gets 3‑5 amenities
(16,3),(16,4),(16,7),(16,10),
(17,3),(17,4),(17,5),(17,9),
(18,3),(18,4),(18,6),(18,7),(18,10),
(19,3),(19,4),(19,8),(19,9),
(20,3),(20,4),(20,5),(20,7),(20,10),
(21,3),(21,4),(21,6),(21,9),
(22,3),(22,4),(22,7),
(23,3),(23,4),(23,5),(23,8),(23,10),
(24,3),(24,4),(24,7),(24,9),
(25,3),(25,4),(25,6),(25,10),
(26,3),(26,4),(26,5),(26,8),
(27,3),(27,4),(27,7),(27,9),(27,10),
(28,3),(28,4),(28,6),
(29,3),(29,4),(29,5),(29,8),(29,10),
(30,3),(30,4),(30,7),(30,9),
(31,3),(31,4),(31,6),(31,10),
(32,3),(32,4),(32,5),(32,7),
(33,3),(33,4),(33,8),(33,9),(33,10),
(34,3),(34,4),(34,6),
(35,3),(35,4),(35,7),(35,10),

-- Budget apartments (36‑50): each gets 1‑3 amenities
(36,4),(36,7),
(37,4),(37,5),
(38,4),
(39,4),(39,5),(39,10),
(40,4),(40,7),
(41,4),(41,5),(41,9),
(42,4),
(43,4),(43,5),(43,10),
(44,4),(44,7),
(45,4),(45,5),(45,9),
(46,4),(46,7),
(47,4),
(48,4),(48,5),(48,10),
(49,4),(49,7),
(50,4),(50,5),(50,9);

-- ============================================================
-- 5. Favourites (10 entries)
-- ============================================================
INSERT INTO favourites (username, apartment_id) VALUES
('admin1', 3),
('admin2', 7),
('agent1', 14),
('agent2', 25),
('agent3', 18),
('agent4', 15),
('agent5', 30),
('agent6', 42),
('agent7', 50),
('agent8', 9);

-- ============================================================
-- 6. Notes (8 entries)
-- ============================================================
INSERT INTO notes (username, apartment_id, content) VALUES
('admin1', 2, 'Great location in Hanoi, close to city center.'),
('admin2', 7, 'Luxury apartment with sea view, impressive amenities.'),
('agent3', 14, 'Huge penthouse, need to verify AV equipment.'),
('agent4', 15, 'Modern building, good for high‑end clients.'),
('agent5', 23, 'Standard apartment, good size but price a bit high.'),
('agent6', 39, 'Budget option, small but cozy.'),
('agent7', 50, 'Recently renovated, looks bigger than listed.'),
('agent9', 11, 'Prime location, should increase rent slightly.');

-- ============================================================
-- 7. Login History (simulate recent logins)
-- ============================================================
INSERT INTO login_history (username, role) VALUES
('admin1', 'admin'),
('admin2', 'admin'),
('admin3', 'admin'),
('agent1', 'agent'),
('agent3', 'agent'),
('agent5', 'agent'),
('agent7', 'agent'),
('agent9', 'agent'),
('admin4', 'admin'),
('agent10','agent');

-- ============================================================
-- End of seed.sql
-- After running, execute triggers.sql to activate logging.
-- ============================================================
