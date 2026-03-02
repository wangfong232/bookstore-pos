-- Chèn 1 nhân viên thu ngân để POS có thể thanh toán
-- Cách 1: Không ghi EmployeeID -> SQL Server tự sinh (tránh lỗi IDENTITY_INSERT)

USE BookstorePOSSystem;
GO

-- Bước 1: Nếu bảng Roles trống, thêm 1 role (không ghi RoleID nếu cột đó là IDENTITY)
IF NOT EXISTS (SELECT 1 FROM Roles)
BEGIN
    INSERT INTO Roles (RoleName, Permissions)
    VALUES (N'Thu ngân', '{"sales": true}');
    PRINT N'Đã thêm 1 role.';
END
GO

-- Bước 2: Chèn nhân viên (RoleID = 1 nếu vừa tạo role ở trên; nếu đã có Roles từ trước thì đổi 1 thành RoleID có sẵn, ví dụ 4)
IF NOT EXISTS (SELECT 1 FROM Employees WHERE Email = 'cashier@bookstore.vn')
BEGIN
    DECLARE @RoleID INT = (SELECT TOP 1 RoleID FROM Roles ORDER BY RoleID);
    INSERT INTO Employees (FullName, Email, Phone, RoleID, HireDate, Status)
    VALUES (N'Thu ngân mặc định', 'cashier@bookstore.vn', '0905555555', @RoleID, '2025-01-01', 'ACTIVE');
    PRINT N'Đã thêm 1 nhân viên thu ngân.';
END
ELSE
    PRINT N'Đã tồn tại nhân viên với email cashier@bookstore.vn.';
GO
