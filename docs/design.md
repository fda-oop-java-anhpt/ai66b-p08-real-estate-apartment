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
| `LoginScreen` | `com.oop.project.ui` | Cửa sổ đăng nhập / đăng ký. |
| `MainFrame` | `com.oop.project.ui` | Cửa sổ chính sau khi đăng nhập thành công, giao diện sảnh chính. |
| `AdminPanel` | `com.oop.project.ui.components` | Tab chỉ hiện thị với admin, dẫn đến các chức năng xem lịch sử đăng nhập và audit logs. |
| `ApartmentPanel` | `com.oop.project.ui.components` | Bảng hiển thị danh sách căn hộ chính. Bao gồm bộ lọc, các nút thao tác (Add/Edit/Delete/Export CSV – với quyền truy cập theo vai trò), nút hình trái tim cho mục yêu thích và nút ghi chú cho từng căn hộ. |
| `ApartmentDialog` | `com.oop.project.ui.components` | Hộp thoại để tạo hoặc chỉnh sửa căn hộ. Bao gồm các trường thông tin về địa chỉ, thành phố, giá cả, số phòng ngủ, diện tích, tình trạng và lựa chọn tiện nghi. |
| `ApartmentTableModel` | `com.oop.project.ui.components` | TableModel dùng để lưu trữ và sắp xếp/lọc danh sách căn hộ. Được sử dụng bởi ApartmentPanel. |
| `AuditLogPanel` | `com.oop.project.ui.components` | Hiển thị mục universal_log (chỉ dành cho admin). |
| `BarChartPanel` | `com.oop.project.ui.components` | Biểu đồ cột trong dashboard. Kèm theo hiệu chứng chuyển cảnh, nhãn giá trị và tùy chọn sắp xếp. Cập nhật theo thời gian thực |
| `DashboardPanel` | `com.oop.project.ui.components` | Chứa các tab con: Summary, Bar Chart và Pie Chart. |
| `FavoritesPanel` | `com.oop.project.ui.components` | Hiển thị các căn hộ yêu thích của người dùng hiện tại, giao diện tương tự giao diện chính. |
| `FavoritesTableModel` | `com.oop.project.ui.components` | TableModel cho danh sách yêu thích. |
| `NotesDialog` | `com.oop.project.ui.components` | Hiển thị tất cả ghi chú cho một căn hộ cụ thể. Chỉ tác giả mới có thể chỉnh sửa hoặc xóa ghi chú của chính họ. |
| `PieChartPanel` | `com.oop.project.ui.components` | Biểu đồ tròn trong dashboard. Kèm theo hiệu chứng chuyển cảnh, nhãn giá trị và tùy chọn sắp xếp. Cập nhật theo thời gian thực. |

---

## 2. Áp dụng các nguyên lý OOP

Mô tả rõ **từng nguyên lý OOP được áp dụng ở đâu trong hệ thống**.

### 2.1. Encapsulation
- Các thuộc tính nào được khai báo `private`?
- Truy cập thông qua getter/setter nào?
- Lý do áp dụng encapsulation?

**Mô tả:**
> Được áp dụng và thể hiện rõ nhất ở trong tất cả các lớp POJOs và DTOs trong package model.  
> Ví dụ lớp `Apartment`:
> - Tất cả các thuộc tính Id, address, city, price, ... đều được khai báo `private`.
> - Chỉ có thể truy cập thông qua các getters: `getId()`, `getAddress()`, `getCity()`, `getPrice()`, ...
> - Đây đều là các thông tin quan trọng của căn hộ, phải được đóng gói và truy cập thông qua phương thức trung gian để đảm bảo tính toàn vẹn của dữ liệu, tránh tác động từ phía không được uỷ quyền.
---

### 2.2. Inheritance
- Class cha là gì?
- Các class con kế thừa từ đâu?
- Lý do sử dụng kế thừa?

**Mô tả:**
| Superclass | Subclass |
|---------------|-------------|
| `javax.swing.JFrame` | `LoginScreen`, `MainFrame` |
| `javax.swing.JPanel` | `ApartmentPanel`, `FavoritesPanel`, `AuditLogPanel`, `DashboardPanel`, `BarChartPanel`, `PieChartPanel`, `AdminPanel`, `SummaryPanel`, ... |
| `javax.swing.JDialog` | `ApartmentDialog`, `AmenityFilterDialog`, `NotesDialog` |
| `javax.swing.table.AbstractTableModel` | `ApartmentTableModel`, `FavoritesTableModel`, `LoginHistoryTableModel`, `UniversalLogTableModel` |
| `javax.swing.table.TableCellRenderer` | Inner classes `AmenityCellRenderer`, `NotesButtonRenderer`, `FavButtonRenderer`, ... |
> - Tái sử dụng (Reusability) – Có được tất cả các hành vi tích hợp sẵn của cửa sổ, bảng điều khiển, nút bấm, bảng và chỉ ghi đè những gì cần tùy chỉnh (bố cục, vẽ, xử lý sự kiện).
>
> - Tính nhất quán (Consistency) – Lớp Theme (phông chữ, màu sắc) được áp dụng trong các lớp con này, mang lại giao diện hiện đại đồng nhất mà không cần sao chép mã.
>
> - Tính đa hình (Polymorphism) – Các container có thể xử lý nhiều bảng điều khiển khác nhau như JPanel, giúp việc quản lý bố cục trở nên đơn giản.

---

### 2.3. Polymorphism
- Phương thức nào được override?
- Được gọi thông qua reference kiểu cha ở đâu?

**Mô tả:**
> - Các lớp POJOs (`Amenity`, `Apartment`, `User`, ...) đều ghi đè phương thức `getId()` từ interface `POJO`.
> - Lớp cha `AbstractTableModel` có các phương thức `getRowCount()`, `getColumnCount()`, `getValueAt(int row, int column)`, `getColumnName(int column)`, `isCellEditable(int row, int col)` được các lớp con ghi đè (`ApartmentTableModel`, `FavoritesTableModel`, ...)

---

### 2.4. Interface
- Interface nào được sử dụng?
- Vai trò của interface trong thiết kế?

**Mô tả:**
> - Các giao diện tùy chỉnh (`POJO`, `DTO`, `DAO`) xác định kiến ​​trúc cốt lõi của dự án bằng cách phân loại các lớp thành các thực thể, các phần tử chứa dữ liệu và các trình xử lý cơ sở dữ liệu.
> - Các giao diện Swing (`TableCellRenderer`, `DocumentListener`, `ActionListener`, ...) được sử dụng rộng rãi để xây dựng giao diện người dùng hiện đại, đáp ứng nhanh và có tính tương tác cao, đồng thời vẫn giữ cho mã nguồn có tính module và dễ bảo trì.

---

### 2.5. Abstraction
- Abstract class / method nào được sử dụng?
- Phần chi tiết nào được ẩn đi?

**Mô tả:**
> - interface `POJO` định nghĩa 2 phương thức trừu tượng `getId()` và `toString()`.
> - Lớp trừu tượng `AbstractTableModel` định nghĩa các phương thức trừu tượng `getRowCount()`, `getColumnCount()`, `getValueAt(row, col)`, ... .
> - Lớp trừu tượng `StringWorker` định nghĩa phương thức trừu tượng `doInBackground()`.
---

## 3. Design Patterns được sử dụng

Liệt kê các design pattern (nếu có) và giải thích ngắn gọn cách áp dụng.

| Design Pattern | Áp dụng ở đâu | Mục đích |
|---------------|-------------|---------|
| Data Access Object (DAO) | Toàn bộ package repository (`ApartmentRepository`, `UserRepository`, ...) đều áp dụng interface `DAO`. | Đóng gói việc truy cập cơ sở dữ liệu và logic SQL, tách biệt logic lưu trữ dữ liệu khỏi logic nghiệp vụ. Giúp dễ dàng thay đổi nguồn dữ liệu mà không ảnh hưởng đến các dịch vụ hoặc giao diện người dùng. |
| Data Transfer Object (DTO) | Các lớp `CityStats`, `CategoryProportion`, `OverallStats` đều áp dụng interface `DTO`. | Các đối tượng gọn nhẹ mang dữ liệu tổng hợp từ cơ sở dữ liệu đến các dashboards, tránh việc phải hiển thị toàn bộ thực thể hoặc nhiều truy vấn. |
| Singleton | `SesionManager` chỉ quản lý duy nhất một người dùng hiện tại | Lưu trữ người dùng hiện đang đăng nhập trong một trường tĩnh, giúp truy cập được thông tin này trên toàn hệ thống mà không cần truyền qua từng phương thức. |
| Adapter | Các bảng khác nhau có thể linh hoạt hiển thị trên cùng UI (`ApartmentTableModel`, `UniversalLogTableModel`, `FavoritesTableModel`) | Chuyển đổi danh sách đối tượng thành TableModel mà JTable có thể hiển thị, dịch các lệnh gọi phương thức sang định dạng mong muốn. |
| Facade | Các business logics có cấu trúc phức tạp (`ApartmentManagement`, `ApartmentSearch`, `DashboardService`, `LoginAuthentication`, ...) được thể hiện đơn giản với người dùng qua UI | Sự phức tạp của mã nguồn được ẩn trong hệ thống, cung cấp dịch vụ tới người dùng thông qua UI đơn giản và trực quan. Điều này giúp đơn giản hóa lớp giao diện người dùng và tập trung hóa các quy tắc nghiệp vụ. |

---

## 4. Luồng hoạt động chính (Main Application Flows)

Mô tả các luồng xử lý chính của hệ thống theo dạng từng bước.

### 4.1. Login
1. Người dùng nhập username và password.
2. LoginView gửi thông tin đăng nhập đến AuthService.
3. AuthService kiểm tra thông tin người dùng.
4. Nếu hợp lệ, hệ thống chuyển sang MenuView.

---

### 4.2. Create - Update - Delete (CRUD)
1. Người dùng lựa chọn đơn vị data (ô/hàng/cột) muốn chỉnh sửa.
2. Người dùng ấn nút hành động muốn thực hiện (Insert, Update, Delete).
3. Main Frame gửi thông tin đến service theo bảng tương ứng (vd: ApartmentManagement).
4. Service gửi yêu cầu đến MySQL.
5. MySQl kiểm tra constraints, triggers,... thực hiện yêu cầu.
6. MainFrame refresh data.

### 4.3. Apartment filtering
1. Người dùng lựa chọn filter theo ý muốn.
2. Main Frame gửi thông tin đến service theo bảng tương ứng.
3. Service gửi yêu cầu đến MySQL.
4. MySQl thực hiện procedures.
4. MainFrame refresh data.
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

### 6.1. Hình thức lưu trữ
- [ ] In-memory
- [ ] File (txt / csv / json)
- [x] Database (MySQL, SQLite, ...)

**Mô tả lý do lựa chọn:**
> Database (MySQL) cung cấp độ tin cậy, hiệu suất, bảo mật và bộ tính năng cần thiết cho một hệ thống quản lý đa người dùng, dựa trên vai trò – vượt xa những gì mà các tập tin, danh sách hoặc bộ nhớ trong có thể cung cấp.

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

