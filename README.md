Đề bài  Xây dựng IAM Service 2
1. Tích hợp Keycloak như một Identity Server
•	Hệ thống cho phép lựa chọn bật/tắt sử dụng Keycloak thông qua cấu hình:
o	Khi bật Keycloak:
	Xác thực request: Khi người dùng gọi tới /login thì trả về thông báo yêu cầu người dùng truy cập trang đăng nhập USER của keycloak (Trang này có thể dùng trang mặc định của keycloak hoặc viết 1 trang web riêng tích hợp keycloak.js). Mở trình duyệt vào trang đăng nhập, Sau khi đăng nhập lấy được access_token. Dùng Bearer access token đó gắn vào các request, mỗi request  kiểm tra thông tin user trong database của iam, nếu database của iam có thì mới cho thực hiện request.
	Register: Tạo tài khoản trên Keycloak và đồng bộ với database nội bộ.
	Logout: Gọi API logout của Keycloak.
	Refresh Token: Làm mới token từ Keycloak.
o	Khi tắt Keycloak (selft-idp):
	Sử dụng thông tin user trong database nội bộ.
2. API quản lý người dùng
•	Yêu cầu các API quản lý người dùng:
o	Tạo user.
o	Xoá mềm user.
o	Khoá/Mở khoá user.
o	Reset mật khẩu.
o	Xem danh sách user (hỗ trợ tìm kiếm và phân trang).
o	Xem thông tin chi tiết của user.
o	Gán vai trò cho user.
•	Tất cả API phải hỗ trợ cả self-IDP và tích hợp Keycloak.
3. Phân quyền RBAC
•	Thêm tính năng RBAC với các API quản lý:
o	CRUD quyền.
o	CRUD vai trò.
o	Gán quyền cho vai trò.
o	Gán vai trò cho user.
•	Sử dụng @PreAuthorize hasPermission() để xác định quyền cho từng API.
4. Chức năng xóa mềm
•	Tất cả hành động xóa trong hệ thống phải là xóa mềm, sử dụng trường deleted.
5. AuditorAware
•	Ghi nhận người thực hiện các thay đổi trong hệ thống (Auditor).
6. Pagination
•	Hỗ trợ phân trang cho các API lấy danh sách (user, vai trò, quyền).
7. Tích hợp Swagger
•	Tích hợp Swagger để tự động sinh tài liệu API.
•	Viết thông tin về dự án trên Readme.MD của Github để khi vào thấy được thông tin dự án luôn (Liệt kê các công nghệ spring boot sử dụng, các chức năng nổi bật mà các bạn tâm đắc).
8. Logging
•	Yêu cầu logging với các thông tin:
o	Log theo ngày (log rolling).
o	Log request, response và exception.
o	Không log thông tin nhạy cảm như mật khẩu.
9. Vai trò hệ thống
•	Tạo các vai trò mặc định:
o	Quản lý người dùng: Có quyền truy cập các API quản lý user.
o	Quản trị hệ thống: Có quyền truy cập các API quản lý quyền và vai trò.
10. Tính năng nâng cao (Không bắt buộc)
•	Thêm chức năng cho phép user đổi mật khẩu khi sử dụng Keycloak làm Identity Server.
•	Tích hợp Google SSO.


Đề bài  Xây dựng Storage Service, Import & Export Excel
1. Xây dựng 1 service chuyên lưu trữ file (Storage Service)
Cung cấp các api cho phép upload, xem, cập nhật, xoá, tải, danh sách file 
Các API Auth bằng Access Token.
Phân quyền xem, sửa, xoá, cập nhật
Cung cấp 2 loại /public, /private
-	public: Ko cần Role vẫn có thể thực hiện. VD: Ảnh sự kiện của công ty thì cả công ty có thể xem được. Ảnh cá nhân thì ai cũng có quyền up lên và ai cũng có quyền xem.
-	private: Cần Role để có thể thực hiện. VD: Hợp đồng chỉ có HR được cấp quyền đọc ghi mới có thể tải lên và xem.
Upload:
-	Upload 1 file
-	Upload nhiều file
Xem thông tin file
Hiển thị (view) với file ảnh (cho phép truyền lên ratio hoặc width-height của ảnh)
Danh sách file:
-	Danh sách phân trang
-	Filter (Tìm theo tên, theo loại file, theo ngày tạo, ngày sửa đổi, người sở hữu (owner), ...)
2. Tích hợp IAM Service và Storage Service bằng Keycloak
- IAM Service gọi sang Storage Service để upload và quản lý ảnh profile cá nhân, và các loại file khác
- IAM Server xác thực với Storage Service bằng client credentials (server-to-server)
3. Import & Export dữ liệu ra file Excel (CSV)
- Xử lý ở IAM Service
- API Import dữ liệu users:
•	Import dữ liệu người dùng  từ file excel.
•	File excel do dev tự định nghĩa nhưng cần có 1 số trường bắt buộc: STT, username, Họ Tên, Ngày sinh, Tên đường, Xã (Phường), Huyện, Tỉnh, Số năm kinh nghiệm
•	File excel định dạng xlsx, font chữ Times New Roman
•	Validate các trường dữ liệu. Cảnh báo dòng nào bị lỗi, lỗi gì để cập nhật lại. VD: Ngày sinh không hợp lệ, Số năm kinh nghiệm lại là text chứ ko phải number, Họ Tên trống, username trống, username đã tồn tại, ....
- API Export dữ liệu users: 
•	Cho phép export danh sách user thành file excel
•	Export theo filter
•	Yêu cầu file excel export ra phải có tiêu đề bôi đậm và tô màu xanh dương, dữ liệu không bị lỗi font tiếng Việt
- Thư viện khuyến nghị sử dụng: JXLS, Apache POI
4. Design Pattern & Resful API
- Áp dụng các design Pattern vào dự án
- Các API tuân thủ quy tắc Restful API

Sử dụng Feign Client để giao tiếp giữa 2 service. Cấu hình Gateway để nhận request từ client rồi điều hướng đến đúng service.
Dùng Eureka để đăng ký các service, không phải sử dụng port để call nhau.

Tích hợp các chức năng giám sát  Monitoring/Logging (Theo dõi CPU, memory, request latency, error rate)
Prometheus: Đi thu thập số liệu sức khỏe (CPU, RAM, số lượng Request) từ các service về.
Grafana: Vẽ biểu đồ từ số liệu của Prometheus và quản lý việc gửi cảnh báo.
ELK Stack (Logstash - Elasticsearch - Kibana): Gom toàn bộ Log (nhật ký hoạt động/lỗi) từ các service về một chỗ để dễ tìm kiếm và tra cứu lỗi.
Zipkin: Vẽ sơ đồ đường đi của Request qua các service để biết nó bị chậm ở khâu nào (đo độ trễ).
Telegram Bot: Nhận tin nhắn cảnh báo từ Grafana để báo cho bạn biết ngay khi hệ thống vượt ngưỡng (CPU quá tải).


