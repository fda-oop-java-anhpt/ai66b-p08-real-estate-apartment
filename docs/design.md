# Design Document

## 1. Danh sách các lớp và vai trò (Class List & Responsibilities)

Liệt kê các class chính trong hệ thống và mô tả ngắn gọn vai trò của từng class.

| Class | Package | Vai trò |
|------|--------|--------|
| `DBConnection` | `com.oop.project.db` | Quản lý các tham số kết nối MySQL (tệp `Connection.env`) và cung cấp các phương thức để thiết lập kết nối. |
| `Amenity` | `com.oop.project.model` | Object cho dữ liệu từ bảng 'amenity'. |
| `Apartment` | `com.oop.project.model` | Object cho dữ liệu từ bảng 'apartment'. |
| `Favourite` | `com.oop.project.model` | Object cho dữ liệu từ bảng 'favourite'. |
| `Note` | `com.oop.project.model` | Object cho dữ liệu từ bảng 'note'. |
| `User` | `com.oop.project.model` | Object cho dữ liệu từ bảng 'user'. |
| `AmenityRepository` | `com.oop.project.repository` | Cung cấp các phương thức CRUD cho bảng 'amenity'. |
| `ApartmentAmenityRepository` | `com.oop.project.repository` | Quản lý bảng trung gian 'apartmentAmenities'. |
| `ApartmentRepository` | `com.oop.project.repository` | Cung cấp các phương thức CRUD, bộ lọc tìm kiếm, xuất CSV cho bảng 'apartment'. |
| `DashboardRepository` | `com.oop.project.repository` | Cung cấp các truy vấn cần thiết cho dashboard. |
| `FavouriteRepository` | `com.oop.project.repository` | Quản lý danh sách favourite của từng user. |
| `NoteRepository` | `com.oop.project.repository` | Cung cấp các phương thức CRUD cho bảng 'note', kiểm tra quyền sở hữu 'note'. |
| `UserRepository` | `com.oop.project.repository` | Cung cấp các phương thức CREATE và READ cho bảng 'user'. |
| `ApartmentAmenity` | `com.oop.project.service` | Liệt kê danh sách amenities của từng căn hộ. |
| `ApartmentExport` | `com.oop.project.service` | Xuất tệp CSV của danh sách liệt kê cụ thể. |
| `ApartmentManagement` | `com.oop.project.service` | Quản lý và cấp quyền chỉnh sửa danh sách căn hộ theo vai trò. |
| `ApartmentSearch` | `com.oop.project.service` | Bộ lọc tìm kiếm căn hộ. |
| `AuthenticationController` | `com.oop.project.service` | Điều hướng lựa chọn Đăng nhập/Đăng ký và quản lý quyền người dùng. |
| `DashboardService` | `com.oop.project.service` | Tổng hợp dữ liệu cần thiết cho dashboard. |
| `FavouriteService` | `com.oop.project.service` | Quản lý danh sách yêu thích cho từng người dùng. |
| `LoginAuthentication` | `com.oop.project.service` | Chức năng đăng nhập. |
| `LoginHistoryQuery` | `com.oop.project.service` | Bộ lọc tìm tiếm lịch sử đăng nhập (quyền Admin). |
| `NoteManagement` | `com.oop.project.service` | Chức năng thêm, xoá, chỉnh sửa note cho người sở hữu. |
| `RegisterAuthentication` | `com.oop.project.service` | Chức năng đăng ký. |
| `ApartmentCategorizer` | `com.oop.project.util` | Tự động phân loại căn hộ dựa trên mức giá và diện tích (từ `ApartmentCategory.env`). |
| `CityDataProvider` | `com.oop.project.util` | Cung cấp danh sách các thành phố (từ `Cities.env`). |
| `HashingUtil` | `com.oop.project.util` | Mã hoá mật khẩu sang định dạng SHA-256. |
| `ReadEnv` | `com.oop.project.util` | Đọc thông tin từ các tệp .env. |
| `SessionManager` | `com.oop.project.util` | Lưu lại thông tin của người dùng đăng nhập hiện tại, để quản lý quyền và ghi audit logs. |
---

## 2. Áp dụng các nguyên lý OOP

Mô tả rõ **từng nguyên lý OOP được áp dụng ở đâu trong hệ thống**.

### 2.1. Encapsulation
- Các thuộc tính nào được khai báo `private`?
- Truy cập thông qua getter/setter nào?
- Lý do áp dụng encapsulation?

**Mô tả:**
> …

---

### 2.2. Inheritance
- Class cha là gì?
- Các class con kế thừa từ đâu?
- Lý do sử dụng kế thừa?

**Mô tả:**
> …

---

### 2.3. Polymorphism
- Phương thức nào được override?
- Được gọi thông qua reference kiểu cha ở đâu?

**Mô tả:**
> …

---

### 2.4. Interface
- Interface nào được sử dụng?
- Vai trò của interface trong thiết kế?

**Mô tả:**
> …

---

### 2.5. Abstraction
- Abstract class / method nào được sử dụng?
- Phần chi tiết nào được ẩn đi?

**Mô tả:**
> …

---

## 3. Design Patterns được sử dụng

Liệt kê các design pattern (nếu có) và giải thích ngắn gọn cách áp dụng.

| Design Pattern | Áp dụng ở đâu | Mục đích |
|---------------|-------------|---------|
| Data Access Object (DAO) | Toàn bộ package repository (`ApartmentRepository`, `UserRepository`, ...) đều áp dụng interface `DAO`. | Đóng gói việc truy cập cơ sở dữ liệu và logic SQL, tách biệt logic lưu trữ dữ liệu khỏi logic nghiệp vụ. Giúp dễ dàng thay đổi nguồn dữ liệu mà không ảnh hưởng đến các dịch vụ hoặc giao diện người dùng. |
| Data Transfer Object (DTO) | Các lớp `CityStats`, `CategoryProportion`, `OverallStats` đều áp dụng interface `DTO`. | Các đối tượng gọn nhẹ mang dữ liệu tổng hợp từ cơ sở dữ liệu đến các dashboards, tránh việc phải hiển thị toàn bộ thực thể hoặc nhiều truy vấn. |
| Singleton | `SesionManager` chỉ quản lý duy nhất một người dùng hiện tại | Lưu trữ người dùng hiện đang đăng nhập trong một trường tĩnh, giúp truy cập được thông tin này trên toàn hệ thống mà không cần truyền qua từng phương thức. |
| Adapter | Các bảng khác nhau có thể linh hoạt hiển thị trên cùng UI (`ApartmentTableModel`, `UniversalLogTableModel`, `FavoritesTableModel`) | Chuyển đổi danh sách đối tượng thành TableModel mà JTable có thể hiển thị, dịch các lệnh gọi phương thức sang định dạng mong muốn. |
| Facade | Các business logics có cấu trúc phức tạp (`ApartmentManagement`, `ApartmentSearch`, `DashboardService`, `LoginAuthentication`, ...) được thể hiện đơn giản với người dùng qua UI | Sự phức tạp của mã nguồn được ẩn trong hệ thống, cung cấp dịch vụ tới người dùng thông qua UI đơn giản và trực quan . Điều này đơn giản hóa lớp giao diện người dùng và tập trung hóa các quy tắc nghiệp vụ. |

---

## 4. Luồng hoạt động chính (Main Application Flows)

Mô tả các luồng xử lý chính của hệ thống theo dạng từng bước.

### 4.1. Login
1. Người dùng nhập username và password.
2. LoginView gửi thông tin đăng nhập đến AuthService.
3. AuthService kiểm tra thông tin người dùng.
4. Nếu hợp lệ, hệ thống chuyển sang MenuView.

---

### 4.2. [Tên luồng chức năng khác]
1. …
2. …
3. …

---

## 5. Class Diagram

- Vẽ **class diagram** cho hệ thống bằng **draw.io**.
- Sơ đồ phải thể hiện:
  - Quan hệ kế thừa
  - Quan hệ association / composition (nếu có)
  - Interface và class implement

📌 **Yêu cầu:**
- Xuất sơ đồ thành file ảnh (PNG hoặc JPG).
- Lưu tại: `docs/class-diagram.png`

---

## 6. Thiết kế lưu trữ dữ liệu (Database / File Design)

Mô tả cách hệ thống lưu trữ dữ liệu.

### 6.1. Hình thức lưu trữ
- [ ] In-memory
- [ ] File (txt / csv / json)
- [ ] Database (MySQL, SQLite, ...)

**Mô tả lý do lựa chọn:**
> …

---

### 6.2. Cấu trúc dữ liệu lưu trữ

Mô tả các bảng / file chính và dữ liệu được lưu trữ.

| Tên bảng / file | Mô tả | Dữ liệu chính |
|----------------|------|--------------|
| | | |
| | | |

---

## 7. Nhận xét về thiết kế (Optional)

- Ưu điểm của thiết kế hiện tại
- Hạn chế
- Hướng cải tiến trong tương lai (nếu có)

---

## 8. Kết luận

Tóm tắt ngắn gọn cách thiết kế hệ thống và cách áp dụng OOP trong project.

