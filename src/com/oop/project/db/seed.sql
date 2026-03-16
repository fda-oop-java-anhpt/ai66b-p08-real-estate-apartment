INSERT INTO users (username, password_hash, role) VALUES
('admin1', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'admin'), -- "password"
('admin2', '2c1743a391305fbf367df8e4f069f9f9a44fbdc0b6f0f3b4c8f3a3c3d5f4c8f0', 'admin'), -- "admin123"
('admin3', '8d969eef6ecad3c29a3a629280e686cff8fabd3c5b3f5f3c3f5f3c3f5f3c3f5f', 'admin'), -- "123456"

('agent1', '6cf615d5d44b8a0c0f6f0f3c3f5f3c3f5f3c3f5f3c3f5f3c3f5f3c3f5f3c3f5f', 'agent'), -- "qwerty"
('agent2', 'bcb1b1b1b1b1b1b1b1b1b1b1b1b1b1b1b1b1b1b1b1b1b1b1b1b1b1b1b1b1b1b1', 'agent'), -- "letmein"
('agent3', '9c56cc51b374c3b4c8f3a3c3d5f4c8f09c56cc51b374c3b4c8f3a3c3d5f4c8f0', 'agent'), -- "welcome"
('agent4', '5d41402abc4b2a76b9719d911017c592', 'agent'), -- "hello"
('agent5', '098f6bcd4621d373cade4e832627b4f6', 'agent'), -- "test"
('agent6', 'e99a18c428cb38d5f260853678922e03', 'agent'), -- "abc123"
('agent7', '7c6a180b36896a0a8c02787eeafb0e4c', 'agent'); -- "password1"

INSERT INTO apartment (address, city, price, bedrooms, size, category) VALUES
-- Luxury (10)
('123 Le Loi', 'Ho Chi Minh City', 15.2, 4, 120, 'luxury'),
('45 Tran Hung Dao', 'Hanoi', 18.5, 5, 150, 'luxury'),
('88 Vo Nguyen Giap', 'Da Nang', 12.7, 3, 95, 'luxury'),
('12 Nguyen Hue', 'Ho Chi Minh City', 20.1, 5, 160, 'luxury'),
('77 Ly Thuong Kiet', 'Hue', 11.3, 3, 100, 'luxury'),
('56 Bach Dang', 'Da Nang', 14.8, 4, 110, 'luxury'),
('9 Pham Van Dong', 'Nha Trang', 13.6, 3, 105, 'luxury'),
('101 Nguyen Van Cu', 'Can Tho', 10.9, 3, 90, 'luxury'),
('33 Tran Phu', 'Ha Long', 16.4, 4, 130, 'luxury'),
('5 Le Thanh Ton', 'Ho Chi Minh City', 19.0, 5, 155, 'luxury'),

-- Standard (10)
('22 Nguyen Trai', 'Hanoi', 5.2, 2, 70, 'standard'),
('67 Hoang Dieu', 'Da Nang', 4.8, 2, 65, 'standard'),
('34 Le Duan', 'Hue', 6.1, 3, 80, 'standard'),
('89 Tran Phu', 'Nha Trang', 5.7, 2, 75, 'standard'),
('12 Nguyen Thi Minh Khai', 'Ho Chi Minh City', 6.5, 3, 85, 'standard'),
('45 Phan Chu Trinh', 'Da Lat', 4.9, 2, 68, 'standard'),
('78 Cach Mang Thang 8', 'Can Tho', 5.3, 2, 72, 'standard'),
('23 Tran Hung Dao', 'Hoi An', 6.0, 3, 82, 'standard'),
('56 Nguyen Van Linh', 'Da Nang', 5.8, 2, 78, 'standard'),
('90 Le Loi', 'Hanoi', 6.2, 3, 88, 'standard'),

-- Budget (10)
('12 Nguyen Van Troi', 'Ho Chi Minh City', 2.1, 1, 40, 'budget'),
('34 Tran Cao Van', 'Da Nang', 1.9, 1, 35, 'budget'),
('56 Nguyen Thai Hoc', 'Hue', 2.3, 1, 42, 'budget'),
('78 Le Hong Phong', 'Nha Trang', 2.5, 2, 50, 'budget'),
('90 Tran Quang Khai', 'Hanoi', 2.0, 1, 38, 'budget'),
('11 Phan Dinh Phung', 'Da Lat', 2.4, 2, 45, 'budget'),
('22 Nguyen Van Linh', 'Can Tho', 2.2, 1, 36, 'budget'),
('33 Tran Hung Dao', 'Hoi An', 2.6, 2, 48, 'budget'),
('44 Ly Thuong Kiet', 'Ha Long', 2.1, 1, 37, 'budget'),
('55 Nguyen Trai', 'Ho Chi Minh City', 2.7, 2, 52, 'budget');

INSERT INTO amenities (name) VALUES
('Swimming Pool'),
('Tennis Court'),
('Gym/Fitness Center'),
('Parking Garage'),
('Playground'),
('Garden'),
('Elevator'),
('Security System');

-- Luxury apartments get more amenities
INSERT INTO apartmentAmenities (apartment_id, amenity_id) VALUES
(1, 1), -- Swimming Pool
(1, 2), -- Tennis Court
(1, 3), -- Gym
(1, 4), -- Parking Garage
(1, 8), -- Security System

(2, 1), (2, 3), (2, 4), (2, 7), (2, 8),
(3, 1), (3, 3), (3, 6), (3, 8),

-- Standard apartments get a balanced set
(11, 3), (11, 4), (11, 7),
(12, 3), (12, 4), (12, 5),
(13, 3), (13, 4), (13, 6),
(14, 3), (14, 4), (14, 8),

-- Budget apartments get fewer amenities
(21, 4), (21, 7),
(22, 4), (22, 5),
(23, 4),
(24, 4), (24, 5),
(25, 4), (25, 7);

INSERT INTO favourites (username, apartment_id) VALUES
('admin1', 3),   -- admin1 likes apartment #3
('agent4', 15),  -- agent4 likes apartment #15
('agent7', 25);  -- agent7 likes apartment #25

INSERT INTO notes (username, apartment_id, content) VALUES
('admin1', 2, 'Great location in Hanoi, close to city center.'),
('agent3', 14, 'Standard apartment, good size but price a bit high.'),
('agent5', 23, 'Budget option, small but cozy.'),
('admin2', 7, 'Luxury apartment with sea view, impressive amenities.'),
('agent7', 25, 'Budget apartment, convenient for students.');

INSERT INTO login_history (username) VALUES
('admin1'),
('admin2'),
('agent3'),
('agent5'),
('agent7');


