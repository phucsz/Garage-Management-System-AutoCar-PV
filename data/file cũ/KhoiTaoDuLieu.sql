use QuanLyGaraOto

--Vai trò
insert into VaiTro values 
('QUANLY',N'Quản lý',N'Là người có quyền hạn cao nhất trong hệ thống'),
('TIEPNHANVIEN',N'Tiếp nhận viên',N'Làm việc trực tiếp với khách hàng'),
('KYTHUATVIEN',N'Kỹ thuật viên',N'Thực hiện công việc kỹ thuật'),
('THUNGAN',N'Thu ngân',N'Phụ trách thanh toán hóa đơn')

--Nhân viên
insert into NhanVien(maNV,hoTenNV,noiSinh,ngaySinh,gioiTinh,sdt,trangThai) values 
('NV2024001',N'Phạm Phương Hoàng Phúc',N'TP.Hồ Chí Minh','2006-09-15',N'Nam','0949101885',N'Đang làm việc')
insert into NhanVien(maNV,hoTenNV,noiSinh,ngaySinh,gioiTinh,sdt,trangThai) values 
('NV2024002',N'Nguyễn Đăng Vinh',N'TP.Hồ Chí Minh','2006-11-03',N'Nam','0981293545',N'Đang làm việc')
insert into NhanVien(maNV,hoTenNV,noiSinh,ngaySinh,gioiTinh,sdt,trangThai) values 
('NV2024003',N'Nguyễn Văn A',N'TP.Hồ Chí Minh','2006-09-15',N'Nam','0123456789',N'Đang làm việc')
insert into NhanVien(maNV,hoTenNV,noiSinh,ngaySinh,gioiTinh,sdt,trangThai) values 
('NV2024004',N'Nguyễn Văn B',N'TP.Hồ Chí Minh','2006-09-15',N'Nam','0987654321',N'Đang làm việc')

--Tài khoản
insert into TaiKhoan values
('NV2024001',ENCRYPTBYPASSPHRASE('123','15092006'),'QUANLY')
insert into TaiKhoan values
('NV2024002',ENCRYPTBYPASSPHRASE('123','16092006'),'TIEPNHANVIEN')
insert into TaiKhoan values
('NV2024003',ENCRYPTBYPASSPHRASE('123','17092006'),'KYTHUATVIEN')
insert into TaiKhoan values
('NV2024004',ENCRYPTBYPASSPHRASE('123','18092006'),'THUNGAN')

--Khách Hàng
INSERT INTO KhachHang
    (maKH, hoTenKH, noiSinh, ngaySinh, CCCD, sdt, diaChi, email)
VALUES
    ('KH001', N'Nguyễn Văn An', N'TP. Hồ Chí Minh', '1990-05-12','079090000001', '0901000001', N'Quận 1, TP. Hồ Chí Minh', 'an@example.com'),
    ('KH002', N'Trần Thị Bình', N'Đồng Nai', '1988-10-20','079090000002', '0901000002', N'Thủ Đức, TP. Hồ Chí Minh', 'binh@example.com'),
    ('KH003', N'Lê Minh Cường', NULL, NULL, NULL, '0901000003', N'Bình Thạnh, TP. Hồ Chí Minh', NULL);
GO

--Nhà cung cấp
INSERT INTO NhaCungCap (maNCC, tenNCC, diaChi, email, sdt)
VALUES
    ('NCC001', N'Công ty Phụ tùng ô tô Đắc Yến', N'TP. Hồ Chí Minh', 'congty.dacyen@gmail.com', '0283000001'),
    ('NCC002', N'Thành Dũng Auto', N'TP.Hồ Chí Minh', null, '0283000002');
GO

--Xe
INSERT INTO Xe
    (bienSoXe, loaiXe, congSuat, hangXe, mauXe, soThangDaDi, soKmHienTai, maKH)
VALUES
    ('51G-52451', N'Ô tô 4 chỗ', 1500, N'Toyota', N'Trắng', 36, 35000, 'KH001'),
    ('36F-23490', N'Ô tô 7 chỗ', 2000, N'Ford', N'Đen', 48, 52000, 'KH002'),
    ('72A-22982', N'Ô tô 4 chỗ', 1600, N'Honda', N'Đỏ', 24, 18000, 'KH003');
GO

--Hạng mục kiểm tra
INSERT INTO HangMucKiemTra (maHangMuc, tenHangMuc, loaiHangMuc)
VALUES
    -- NỘI THẤT
    ('NT001', N'Tay mở cửa bên trong', N'Nội thất'),
    ('NT002', N'Nút nâng, hạ kính', N'Nội thất'),
    ('NT003', N'Ghế ngồi', N'Nội thất'),
    ('NT004', N'Dây an toàn', N'Nội thất'),
    ('NT005', N'Vô lăng', N'Nội thất'),
    ('NT006', N'Bảng điều khiển trung tâm', N'Nội thất'),
    ('NT007', N'Màn hình giải trí', N'Nội thất'),
    ('NT008', N'Hệ thống điều hòa', N'Nội thất'),
    ('NT009', N'Cửa gió điều hòa', N'Nội thất'),
    ('NT010', N'Cần số', N'Nội thất'),
    ('NT011', N'Bàn đạp ga, phanh, côn', N'Nội thất'),
    ('NT012', N'Thảm lót sàn', N'Nội thất'),
    ('NT013', N'Trần xe', N'Nội thất'),
    ('NT014', N'Tấm che nắng', N'Nội thất'),
    ('NT015', N'Gương chiếu hậu trong xe', N'Nội thất'),
    ('NT016', N'Đèn nội thất', N'Nội thất'),
    ('NT017', N'Hộc đựng đồ', N'Nội thất'),
    ('NT018', N'Bệ tỳ tay', N'Nội thất'),
    ('NT019', N'Loa trong xe', N'Nội thất'),
    ('NT020', N'Camera hành trình', N'Nội thất'),
    ('NT021', N'Cần gạt mưa', N'Nội thất'),
    ('NT022', N'Cụm đồng hồ hiển thị', N'Nội thất'),
    ('NT023', N'Khóa cửa bên trong', N'Nội thất'),
    ('NT024', N'Rèm che nắng', N'Nội thất'),

    -- NGOẠI THẤT
    ('NG001', N'Gương chiếu hậu ngoài', N'Ngoại thất'),
    ('NG002', N'Đèn trước, sau', N'Ngoại thất'),
    ('NG003', N'Bánh xe', N'Ngoại thất'),
    ('NG004', N'Lớp sơn xe', N'Ngoại thất'),
    ('NG005', N'Kính lái trước', N'Ngoại thất'),
    ('NG006', N'Kính chắn gió sau', N'Ngoại thất'),
    ('NG007', N'Kính cửa sổ', N'Ngoại thất'),
    ('NG008', N'Cần gạt nước kính chắn gió', N'Ngoại thất'),
    ('NG009', N'Cản trước', N'Ngoại thất'),
    ('NG010', N'Cản sau', N'Ngoại thất'),
    ('NG011', N'Nắp capo', N'Ngoại thất'),
    ('NG012', N'Nắp cốp sau', N'Ngoại thất'),
    ('NG013', N'Cửa xe', N'Ngoại thất'),
    ('NG014', N'Tay nắm cửa bên ngoài', N'Ngoại thất'),
    ('NG015', N'Lưới tản nhiệt', N'Ngoại thất'),
    ('NG016', N'Ốp bảo vệ thân xe', N'Ngoại thất'),
    ('NG017', N'Hông xe', N'Ngoại thất'),
    ('NG018', N'Mui xe', N'Ngoại thất'),
    ('NG019', N'Vè bánh xe', N'Ngoại thất'),
    ('NG020', N'Đèn xi nhan', N'Ngoại thất'),
    ('NG021', N'Đèn phanh', N'Ngoại thất'),
    ('NG022', N'Đèn sương mù', N'Ngoại thất'),
    ('NG023', N'Ăng-ten xe', N'Ngoại thất'),
    ('NG024', N'Biển số xe', N'Ngoại thất'),
    ('NG025', N'Cửa bình nhiên liệu', N'Ngoại thất');
GO

--Danh mục phụ tùng
INSERT INTO DanhMucPhuTung (maDanhMuc, tenDanhMuc)
VALUES
    ('PT', N'Động cơ'),
    ('PH', N'Hệ thống phanh'),
    ('GT', N'Gầm và hệ thống treo'),
    ('DI', N'Hệ thống điện'),
    ('LM', N'Hệ thống làm mát'),
    ('LK', N'Lọc và dầu nhớt'),
    ('NT', N'Nội thất và ngoại thất'),
    ('LP', N'Lốp và bánh xe');
GO

--Phụ tùng
INSERT INTO PhuTung
    (maPhuTung, tenPhuTung, maDanhMuc, donViTinh,
     donGiaHienTai, soLuongTon, ngaySanXuat, maNCC)
VALUES
('PT001', N'Bugi đánh lửa',       'PT', N'Cái', 120000, 50, '2026-01-15', 'NCC001'),
('PH001', N'Má phanh trước',      'PH', N'Bộ',  850000, 15, '2026-02-10', 'NCC002'),
('GT001', N'Giảm xóc trước',      'GT', N'Cái', 1500000, 8, '2026-01-20', 'NCC001'),
('DI001', N'Ắc quy ô tô',          'DI', N'Bình', 1800000, 12, '2026-03-05', 'NCC001'),
('LM001', N'Két nước làm mát',    'LM', N'Cái', 2500000, 5, '2026-02-18', 'NCC002'),
('LK001', N'Lọc dầu động cơ',     'LK', N'Cái', 180000, 40, '2026-03-12', 'NCC002'),
('NT001', N'Gương chiếu hậu',     'NT', N'Cái', 750000, 10, '2026-01-25', 'NCC001'),
('LP001', N'Lốp xe ô tô 16 inch', 'LP', N'Cái', 1800000, 20, '2026-02-28', 'NCC002');

--Dịch vụ
INSERT INTO DichVu (maDichVu, tenDichVu, loaiDichVu, donGiaHienTai)
VALUES
    ('DV001', N'Thay dầu động cơ', N'Bảo dưỡng', 250000),
    ('DV002', N'Kiểm tra hệ thống phanh', N'Kiểm tra', 150000),
    ('DV003', N'Vệ sinh xe', N'Vệ sinh', 200000);
GO

--Chương trình khuyến mãi
INSERT INTO ChuongTrinhKhuyenMai
    (maChuongTrinh, tenChuongTrinh, ngayBatDau, ngayKetThuc, loaiKhuyenMai, giaTriKhuyenMai)
VALUES
    ('KM001', N'Khuyến mãi khai trương', '2026-01-01', '2027-12-31', N'Trừ tiền', 100000),
    ('KM002', N'Ưu đãi lễ Quốc Khánh 2/9', '2026-09-02', '2026-09-05', N'Phần trăm', 5);
GO