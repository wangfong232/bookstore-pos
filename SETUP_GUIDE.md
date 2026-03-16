# Hướng dẫn Setup Dự án cho Thành viên Mới

## 🎯 Vấn đề
Khi pull code về, bạn cần tài khoản Manager để activate các tài khoản đăng ký mới. Dự án đã có sẵn tài khoản Manager mặc định.

## 📋 Các Bước Setup

### 1. **Pull Code về**

```bash
git clone <repo-url>
cd se1972_g2
```

### 2. **Tạo/Restore Database**

Chạy SQL script để tạo database:

```sql
-- Chạy file bookstore-sample-data.sql trong SQL Server Management Studio
-- Hoặc dùng command line:
sqlcmd -S <server-name> -U <username> -P <password> -i bookstore-sample-data.sql
```

### 3. **Generate Password Hash cho Admin Manager**

Tài khoản Manager mặc định được tạo với:

- **Email**: `admin.manager@bookstore.vn`
- **Password**: `admin@123` (cần hash SHA-256)
- **Status**: `ACTIVE` ✅

#### **Cách 1: Chạy trực tiếp script SQL (Nhanh nhất)**

Cách nhanh nhất cho thành viên mới là copy lệnh này chạy thẳng vào DB SQL Server. Lệnh này đã chứa sẵn password hash cho `admin@123`:

```sql
INSERT INTO Employees (FullName, Email, Phone, RoleID, HireDate, Status, PasswordHash)
VALUES (N'Admin Manager', 'admin.manager@bookstore.vn', '0900000000', 2, CAST(GETDATE() AS DATE), 'ACTIVE', '7676aaafb027c825bd9abab78b234070e702752f625b7588828989f3c7062408');
```

#### **Cách 2: Dùng Java Program**

1. Mở terminal/command prompt tại thư mục project
2. Chạy:

```bash
javac -d build\classes src\java\util\PasswordHashGenerator.java
java -cp build\classes util.PasswordHashGenerator
```

3. Output sẽ hiển thị:

```
=== PASSWORD HASH GENERATOR ===
Password: admin@123
SHA-256 Hash:
[LONG_HASH_STRING_HERE]

Dùng hash này trong SQL:
UPDATE Employees SET PasswordHash = '[LONG_HASH_STRING_HERE]' WHERE Email = 'admin.manager@bookstore.vn';
```

4. Copy lệnh UPDATE từ output và chạy trong SQL Server:

```sql
UPDATE Employees SET PasswordHash = '[PASTE_HASH_HERE]' WHERE Email = 'admin.manager@bookstore.vn';
```

#### **Cách 3: Dùng Online Tool**

1. Truy cập [SHA256 Online Generator](https://www.sha256online.com/)
2. Nhập: `admin@123`
3. Copy hash, rồi chạy SQL:

```sql
UPDATE Employees SET PasswordHash = '[HASH_TỪ_ONLINE]' WHERE Email = 'admin.manager@bookstore.vn';
```

### 4. **Đăng Nhập**

Mở ứng dụng, đăng nhập với:

- **Email**: `admin.manager@bookstore.vn`
- **Password**: `admin@123`

### 5. **Activate Tài Khoản Mới**

Sau khi đăng nhập với admin account:

1. Vào mục **Nhân viên** → **Danh sách nhân viên**
2. Tìm tài khoản với Status = `PENDING`
3. Thay đổi Status thành `ACTIVE`
4. Người đó có thể đăng nhập ngay

---

## ⚙️ Troubleshooting

### ❌ "Hash không đúng" / "Không thể đăng nhập"

**Giải pháp**: Kiểm tra lại hash SHA-256. Password phải chính xác là `admin@123` (có kí tự `@`)

### ❌ "Status = PENDING, không thể đăng nhập"

**Giải pháp**:

- Chạy lệnh SQL để set Status = 'ACTIVE':

```sql
UPDATE Employees SET Status = 'ACTIVE' WHERE Email = 'admin.manager@bookstore.vn';
```

### ❌ "Email 'admin.manager@bookstore.vn' không tồn tại"

**Giải pháp**: Database có thể chưa được refresh. Chạy lại `bookstore-sample-data.sql`

---

## 🔐 Lưu Ý An Toàn

- ✅ Sau lần đăng nhập đầu tiên, **hãy đổi password** trong mục **Hồ sơ cá nhân**
- ✅ Không chia sẻ password `admin@123` với bất kì ai (chỉ để test)
- ✅ Trong production, quản lý password bằng hệ thống khác (environment variables, etc.)

---

## 📞 Cần Giúp?

Nếu gặp vấn đề, liên hệ với người quản lí dự án hoặc kiểm tra lại các bước trên.
