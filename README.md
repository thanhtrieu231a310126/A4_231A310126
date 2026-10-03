# Lab A4 TextView Button EditText và xử lý sự kiện

**Nguyễn Thị Thanh Triều — MSSV 231A310126**
Học phần INT4211 — Lập trình trên các thiết bị di động.

Ứng dụng Android Java + XML gồm máy tính bốn phép toán và tính BMI. Minimum SDK 24; package `vn.edu.vhu.ltdd.a4events`.

## Mở và chạy

1. Android Studio → Open → chọn thư mục `A4_231A310126` chứa `settings.gradle`.
2. Chọn Gradle JDK 17 hoặc 21 trong Settings → Build Tools → Gradle. Không dùng JDK 25 với Gradle 8.9.
3. Đợi Gradle Sync. Cài SDK Platform 36 và Build Tools 36.0.0 nếu được hỏi.
4. Chọn máy ảo hoặc điện thoại Android API 24 trở lên rồi bấm Run.
5. Có thể cài APK trong thư mục `submission` bằng cách kéo vào máy ảo.

Gradle Wrapper 8.9 và Android Gradle Plugin 8.7.3 được cố định để build có thể tái lập. Tham khảo tương thích: https://developer.android.com/build/releases/agp-8-7-0-release-notes.

## Chức năng

- Cộng, trừ, nhân, chia bằng `double`; kết quả hai chữ số thập phân với `Locale.getDefault()`.
- Ô trống, sai định dạng, chia 0, số không hữu hạn hoặc kết quả tràn: `setError`, con trỏ về ô sai; các lỗi ngoài ô trống có Toast.
- Xóa trắng xóa A/B, lỗi và kết quả máy tính; giữ lịch sử.
- BMI: kg và chiều cao m hoặc cm. Chiều cao > 3 được chia 100 theo đề.
- Phân loại BMI chưa làm tròn: <18.5; <23; <25; từ 25.
- NC2: 5 phép tính thành công gần nhất, mới nhất ở trên; lưu `Bundle` khi xoay màn hình.
- NC3: bình thường xanh, thừa cân vàng đậm dễ đọc, béo phì đỏ; thiếu cân xám.
- Kết quả và phân loại BMI cũng được giữ khi Activity tạo lại.

## Mã nguồn chính

- `MainActivity.java`: ánh xạ view, hai kiểu listener, hiển thị lỗi, lưu trạng thái.
- `CalculatorLogic.java`: kiểm tra và tính toán Java thuần, `try-catch NumberFormatException`.
- `HistoryStore.java`: giới hạn và phục hồi lịch sử.
- `activity_main.xml`: ScrollView, hai phần chức năng, bốn nút chia đều bằng layout_weight.
- `strings.xml`: họ tên, MSSV và các chuỗi giao diện.

## Kiểm thử

```sh
./gradlew testDebugUnitTest logicCoverage lintDebug assembleDebug
./gradlew connectedDebugAndroidTest
```

JUnit kiểm thử phép tính, ranh giới BMI, định dạng, tràn số, ô gây lỗi và lịch sử. `LabUiTest` chạy các test của đề cùng đổi màu và xoay thật trên emulator. Khi kiểm tra chuỗi `abc` và `1.5.5`, test bỏ bộ lọc ký tự của ô nhập để đưa dữ liệu sai vào đúng handler; bàn phím số bình thường có thể chặn các chuỗi này.

Báo cáo coverage chỉ đo `CalculatorLogic` và `HistoryStore`; không phải độ phủ toàn bộ giao diện Android. Ảnh, log chạy và video thực tế nằm trong `evidence` và `submission`.

## Nộp bài

1. Đọc báo cáo trong `submission`, kiểm tra lại trên máy của bạn.
2. Tạo repository GitHub **Private** tên `A4_231A310126`.
3. Upload/push project này và mời đúng tài khoản giảng viên vào Collaborators.
4. Nộp link repo, báo cáo Word/PDF và video không quá 2 phút theo hệ thống của lớp.

Repo local có lịch sử commit thực hiện bài. Chưa tạo repo GitHub và chưa mời giảng viên; cần tài khoản GV và thao tác xuất bản được bạn xác nhận.

## Video demo trong Android Studio

`submission/Demo_A4_231A310126.mp4` quay toàn màn hình Android Studio: mã nguồn ở bên trái và app chạy nhỏ trong Running Devices ở bên phải. App được Run từ IDE trên A1 Pixel API 35. Chuỗi thao tác demo được chạy tự động bằng LabUiTest trên chính máy ảo tích hợp; log xác nhận ở `evidence/studio-demo-test-run.txt`.
