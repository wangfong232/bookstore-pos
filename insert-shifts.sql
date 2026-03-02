-- Chèn ca làm việc (Shifts) để POS có thể lưu hóa đơn
-- ShiftID = 1 là giá trị mặc định trong code

USE BookstorePOSSystem;
GO

SET IDENTITY_INSERT Shifts ON;

INSERT INTO Shifts (ShiftID, ShiftName, StartTime, EndTime) VALUES
(1, N'Ca sáng', '07:00', '14:00'),
(2, N'Ca chiều', '14:00', '21:00'),
(3, N'Ca hành chính', '08:00', '17:00');

SET IDENTITY_INSERT Shifts OFF;
GO
