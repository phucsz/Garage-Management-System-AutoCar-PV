/*==============================================================
  DATABASE: QuanLyGaraOto
  MUC DICH : Tao CSDL va nap du lieu mau cho tat ca cac bang
  GHI CHU  :
  - Chay script tren SQL Server.
  - Neu database da ton tai, script se KHONG xoa du lieu cu.
  - Neu muon tao lai tu dau, hay backup va xoa database cu truoc.
==============================================================*/

IF DB_ID(N'QuanLyGaraOto') IS NULL
    CREATE DATABASE QuanLyGaraOto;
GO
USE QuanLyGaraOto;
GO

/*==============================================================
  PHAN 1. TAO CAC BANG
==============================================================*/

CREATE TABLE VaiTro (
    maVaiTro VARCHAR(50) PRIMARY KEY,
    tenVaiTro NVARCHAR(100),
    moTa NVARCHAR(500)
);
GO

CREATE TABLE NhanVien (
    maNV VARCHAR(50) PRIMARY KEY,
    hoTenNV NVARCHAR(100),
    noiSinh NVARCHAR(100),
    ngaySinh DATETIME,
    gioiTinh NVARCHAR(5) CHECK (gioiTinh IN (N'Nam', N'Nữ', N'Khác')),
    CCCD VARCHAR(20),
    sdt VARCHAR(15),
    diaChi NVARCHAR(255),
    email VARCHAR(100),
    trangThai NVARCHAR(50)
);
GO

CREATE TABLE TaiKhoan (
    tenDangNhap VARCHAR(50) PRIMARY KEY,
    matKhau VARBINARY(MAX),
    maVaiTro VARCHAR(50),
    CONSTRAINT FK_TaiKhoan_VaiTro FOREIGN KEY (maVaiTro)
        REFERENCES VaiTro(maVaiTro),
    CONSTRAINT FK_TaiKhoan_NhanVien FOREIGN KEY (tenDangNhap)
        REFERENCES NhanVien(maNV)
);
GO

CREATE TABLE KhachHang (
    maKH VARCHAR(50) PRIMARY KEY,
    hoTenKH NVARCHAR(100) NOT NULL,
    noiSinh NVARCHAR(100) NULL,
    ngaySinh DATE NULL,
    CCCD VARCHAR(20) NULL,
    sdt VARCHAR(15) NOT NULL,
    diaChi NVARCHAR(255) NULL,
    email VARCHAR(100) NULL
);
GO

CREATE TABLE NhaCungCap (
    maNCC VARCHAR(50) PRIMARY KEY,
    tenNCC NVARCHAR(150) NOT NULL,
    diaChi NVARCHAR(255) NULL,
    email VARCHAR(100) NULL,
    sdt VARCHAR(15) NULL
);
GO

CREATE TABLE HangMucKiemTra (
    maHangMuc VARCHAR(50) PRIMARY KEY,
    tenHangMuc NVARCHAR(150) NOT NULL,
    loaiHangMuc NVARCHAR(100) NULL
);
GO

CREATE TABLE DichVu (
    maDichVu VARCHAR(50) PRIMARY KEY,
    tenDichVu NVARCHAR(150) NOT NULL,
    loaiDichVu NVARCHAR(100) NULL,
    donGiaHienTai FLOAT NOT NULL CHECK (donGiaHienTai >= 0)
);
GO

CREATE TABLE ChuongTrinhKhuyenMai (
    maChuongTrinh VARCHAR(50) PRIMARY KEY,
    tenChuongTrinh NVARCHAR(150) NOT NULL,
    ngayBatDau DATE NOT NULL,
    ngayKetThuc DATE NOT NULL,
    loaiKhuyenMai NVARCHAR(50) NOT NULL,
    giaTriKhuyenMai FLOAT NOT NULL CHECK (giaTriKhuyenMai >= 0),
    CONSTRAINT CK_KhuyenMai_Ngay CHECK (ngayKetThuc >= ngayBatDau)
);
GO

CREATE TABLE Xe (
    bienSoXe VARCHAR(20) PRIMARY KEY,
    loaiXe NVARCHAR(100) NULL,
    congSuat INT NULL CHECK (congSuat IS NULL OR congSuat > 0),
    hangXe NVARCHAR(100) NULL,
    mauXe NVARCHAR(50) NULL,
    soThangDaDi INT NULL CHECK (soThangDaDi IS NULL OR soThangDaDi >= 0),
    soKmHienTai FLOAT NOT NULL DEFAULT 0 CHECK (soKmHienTai >= 0),
    maKH VARCHAR(50) NOT NULL,
    CONSTRAINT FK_Xe_KhachHang FOREIGN KEY (maKH)
        REFERENCES KhachHang(maKH)
);
GO

CREATE TABLE PhieuTiepNhan (
    maPTN VARCHAR(50) PRIMARY KEY,
    ngayTiepNhan DATETIME NOT NULL DEFAULT GETDATE(),
    yeuCauKhachHang NVARCHAR(500) NULL,
    soKmDaDi FLOAT NOT NULL CHECK (soKmDaDi >= 0),
    maNVLapPhieu VARCHAR(50) NOT NULL,
    bienSoXe VARCHAR(20) NOT NULL,
    maKH VARCHAR(50) NOT NULL,
    CONSTRAINT FK_PTN_NhanVien FOREIGN KEY (maNVLapPhieu)
        REFERENCES NhanVien(maNV),
    CONSTRAINT FK_PTN_Xe FOREIGN KEY (bienSoXe)
        REFERENCES Xe(bienSoXe),
    CONSTRAINT FK_PTN_KhachHang FOREIGN KEY (maKH)
        REFERENCES KhachHang(maKH)
);
GO

CREATE TABLE ChiTietHangMuc (
    maPTN VARCHAR(50) NOT NULL,
    maHangMuc VARCHAR(50) NOT NULL,
    tinhTrang NVARCHAR(100) NULL,
    CONSTRAINT PK_ChiTietHangMuc PRIMARY KEY (maPTN, maHangMuc),
    CONSTRAINT FK_CTHM_PhieuTiepNhan FOREIGN KEY (maPTN)
        REFERENCES PhieuTiepNhan(maPTN),
    CONSTRAINT FK_CTHM_HangMuc FOREIGN KEY (maHangMuc)
        REFERENCES HangMucKiemTra(maHangMuc)
);
GO

CREATE TABLE PhieuSuaChua (
    maPSC VARCHAR(50) PRIMARY KEY,
    ngayLapPhieu DATETIME NOT NULL DEFAULT GETDATE(),
    trangThai NVARCHAR(50) NOT NULL DEFAULT N'Đang sửa chữa',
    maPTN VARCHAR(50) NOT NULL UNIQUE,
    maNVLapPhieu VARCHAR(50) NOT NULL,
    maNVPhuTrach VARCHAR(50) NOT NULL,
    tongTienDichVu FLOAT NOT NULL DEFAULT 0 CHECK (tongTienDichVu >= 0),
    tongTienPhuTung FLOAT NOT NULL DEFAULT 0 CHECK (tongTienPhuTung >= 0),
    tongChiPhiDuKien FLOAT NOT NULL DEFAULT 0 CHECK (tongChiPhiDuKien >= 0),
    CONSTRAINT FK_PSC_PhieuTiepNhan FOREIGN KEY (maPTN)
        REFERENCES PhieuTiepNhan(maPTN),
    CONSTRAINT FK_PSC_NhanVienLap FOREIGN KEY (maNVLapPhieu)
        REFERENCES NhanVien(maNV),
    CONSTRAINT FK_PSC_NhanVienPhuTrach FOREIGN KEY (maNVPhuTrach)
        REFERENCES NhanVien(maNV)
);
GO

CREATE TABLE DanhMucPhuTung (
    maDanhMuc VARCHAR(10) PRIMARY KEY,
    tenDanhMuc NVARCHAR(100) NOT NULL
);
GO

CREATE TABLE PhuTung (
    maPhuTung VARCHAR(50) PRIMARY KEY,
    tenPhuTung NVARCHAR(150) NOT NULL,
    maDanhMuc VARCHAR(10),
    donViTinh NVARCHAR(30) NULL,
    donGiaHienTai FLOAT NOT NULL CHECK (donGiaHienTai >= 0),
    soLuongTon INT NOT NULL DEFAULT 0 CHECK (soLuongTon >= 0),
    ngaySanXuat DATE NULL,
    maNCC VARCHAR(50) NOT NULL,
    CONSTRAINT FK_PhuTung_DanhMucPhuTung FOREIGN KEY (maDanhMuc)
        REFERENCES DanhMucPhuTung(maDanhMuc),
    CONSTRAINT FK_PhuTung_NhaCungCap FOREIGN KEY (maNCC)
        REFERENCES NhaCungCap(maNCC)
);
GO

CREATE TABLE ChiTietPhuTung (
    maPSC VARCHAR(50) NOT NULL,
    maPhuTung VARCHAR(50) NOT NULL,
    soLuong INT NOT NULL CHECK (soLuong > 0),
    donGia FLOAT NOT NULL CHECK (donGia >= 0),
    ghiChu NVARCHAR(255) NULL,
    CONSTRAINT PK_ChiTietPhuTung PRIMARY KEY (maPSC, maPhuTung),
    CONSTRAINT FK_CTPThu_PhieuSuaChua FOREIGN KEY (maPSC)
        REFERENCES PhieuSuaChua(maPSC),
    CONSTRAINT FK_CTPThu_PhuTung FOREIGN KEY (maPhuTung)
        REFERENCES PhuTung(maPhuTung)
);
GO

CREATE TABLE ChiTietDichVu (
    maPSC VARCHAR(50) NOT NULL,
    maDichVu VARCHAR(50) NOT NULL,
    soLuong INT NOT NULL CHECK (soLuong > 0),
    donGia FLOAT NOT NULL CHECK (donGia >= 0),
    ghiChu NVARCHAR(255) NULL,
    CONSTRAINT PK_ChiTietDichVu PRIMARY KEY (maPSC, maDichVu),
    CONSTRAINT FK_CTDV_PhieuSuaChua FOREIGN KEY (maPSC)
        REFERENCES PhieuSuaChua(maPSC),
    CONSTRAINT FK_CTDV_DichVu FOREIGN KEY (maDichVu)
        REFERENCES DichVu(maDichVu)
);
GO

CREATE TABLE HoaDon (
    maHoaDon VARCHAR(50) PRIMARY KEY,
    ngayLap DATETIME NOT NULL DEFAULT GETDATE(),
    hinhThucThanhToan NVARCHAR(50) NULL,
    trangThai NVARCHAR(50) NOT NULL DEFAULT N'Chưa thanh toán',
    maPSC VARCHAR(50) NOT NULL UNIQUE,
    maNVThuNgan VARCHAR(50) NOT NULL,
    maKH VARCHAR(50) NOT NULL,
    maChuongTrinh VARCHAR(50) NULL,
    tienGiamGia FLOAT NOT NULL DEFAULT 0 CHECK (tienGiamGia >= 0),
    soTienGiamGia FLOAT NOT NULL DEFAULT 0 CHECK (soTienGiamGia >= 0),
    tongTienThanhToan FLOAT NOT NULL CHECK (tongTienThanhToan >= 0),
    CONSTRAINT FK_HoaDon_PhieuSuaChua FOREIGN KEY (maPSC)
        REFERENCES PhieuSuaChua(maPSC),
    CONSTRAINT FK_HoaDon_NhanVien FOREIGN KEY (maNVThuNgan)
        REFERENCES NhanVien(maNV),
    CONSTRAINT FK_HoaDon_KhachHang FOREIGN KEY (maKH)
        REFERENCES KhachHang(maKH),
    CONSTRAINT FK_HoaDon_KhuyenMai FOREIGN KEY (maChuongTrinh)
        REFERENCES ChuongTrinhKhuyenMai(maChuongTrinh)
);
GO

CREATE TABLE PhieuNhapKho (
    maPNK VARCHAR(50) PRIMARY KEY,
    ngayNhap DATETIME NOT NULL DEFAULT GETDATE(),
    maNVLap VARCHAR(50) NOT NULL,
    maNCC VARCHAR(50) NOT NULL,
    ghiChu NVARCHAR(500) NULL,
    tongTienNhapKho FLOAT NOT NULL DEFAULT 0 CHECK (tongTienNhapKho >= 0),
    CONSTRAINT FK_PNK_NhanVien FOREIGN KEY (maNVLap)
        REFERENCES NhanVien(maNV),
    CONSTRAINT FK_PNK_NhaCungCap FOREIGN KEY (maNCC)
        REFERENCES NhaCungCap(maNCC)
);
GO

CREATE TABLE ChiTietPhieuNhapKho (
    maPNK VARCHAR(50) NOT NULL,
    maPhuTung VARCHAR(50) NOT NULL,
    soLuong INT NOT NULL CHECK (soLuong > 0),
    donGiaNhap FLOAT NOT NULL CHECK (donGiaNhap >= 0),
    CONSTRAINT PK_ChiTietPhieuNhapKho PRIMARY KEY (maPNK, maPhuTung),
    CONSTRAINT FK_CTPNK_PhieuNhapKho FOREIGN KEY (maPNK)
        REFERENCES PhieuNhapKho(maPNK),
    CONSTRAINT FK_CTPNK_PhuTung FOREIGN KEY (maPhuTung)
        REFERENCES PhuTung(maPhuTung)
);
GO

/*==============================================================
  PHAN 2. CHEN DU LIEU MAU
  Thu tu: bang doc lap -> bang phu thuoc -> bang nghiep vu
  -> bang chi tiet.
==============================================================*/

-- 1. VAI TRO
INSERT INTO VaiTro (maVaiTro, tenVaiTro, moTa)
VALUES
('QUANLY', N'Quản lý', N'Quản lý và giám sát hoạt động của gara.'),
('TIEPNHANVIEN', N'Tiếp nhận viên', N'Tiếp nhận xe và lập phiếu tiếp nhận.'),
('KYTHUATVIEN', N'Kỹ thuật viên', N'Kiểm tra, bảo dưỡng và sửa chữa xe.'),
('THUNGAN', N'Thu ngân', N'Lập hóa đơn và thực hiện thanh toán.');
GO

-- 2. NHAN VIEN
INSERT INTO NhanVien
    (maNV, hoTenNV, noiSinh, ngaySinh, gioiTinh, CCCD, sdt, diaChi, email, trangThai)
VALUES
('NV2024001', N'Phạm Phương Hoàng Phúc', N'TP. Hồ Chí Minh', '2006-09-15', N'Nam', '079206000001', '0949101885', N'Quận 1, TP. Hồ Chí Minh', 'phuc@autocarpv.vn', N'Đang làm việc'),
('NV2024002', N'Nguyễn Đăng Vinh', N'TP. Hồ Chí Minh', '2006-11-03', N'Nam', '079206000002', '0981293545', N'Quận 3, TP. Hồ Chí Minh', 'vinh@autocarpv.vn', N'Đang làm việc'),
('NV2024003', N'Nguyễn Văn A', N'TP. Hồ Chí Minh', '2006-09-15', N'Nam', '079206000003', '0123456789', N'Gò Vấp, TP. Hồ Chí Minh', 'vana@autocarpv.vn', N'Đang làm việc'),
('NV2024004', N'Nguyễn Văn B', N'TP. Hồ Chí Minh', '2006-09-15', N'Nam', '079206000004', '0987654321', N'Bình Thạnh, TP. Hồ Chí Minh', 'vanb@autocarpv.vn', N'Đang làm việc'),
('NV2024005', N'Lê Minh Khang', N'Đồng Nai', '2002-04-12', N'Nam', '079202000005', '0902000005', N'Thủ Đức, TP. Hồ Chí Minh', 'khang@autocarpv.vn', N'Đang làm việc'),
('NV2024006', N'Trần Thị Mai', N'Long An', '2001-08-22', N'Nữ', '079201000006', '0902000006', N'Tân Bình, TP. Hồ Chí Minh', 'mai@autocarpv.vn', N'Đang làm việc');
GO

-- 3. TAI KHOAN
-- Mat khau duoc ma hoa bang ENCRYPTBYPASSPHRASE.
-- Mat khau giai ma mau lan luot: 15092006, 03112006, 15092006...
INSERT INTO TaiKhoan (tenDangNhap, matKhau, maVaiTro)
VALUES
('NV2024001', ENCRYPTBYPASSPHRASE('123', '15092006'), 'QUANLY'),
('NV2024002', ENCRYPTBYPASSPHRASE('123', '03112006'), 'TIEPNHANVIEN'),
('NV2024003', ENCRYPTBYPASSPHRASE('123', '15092006'), 'KYTHUATVIEN'),
('NV2024004', ENCRYPTBYPASSPHRASE('123', '15092006'), 'THUNGAN'),
('NV2024005', ENCRYPTBYPASSPHRASE('123', '12042002'), 'KYTHUATVIEN'),
('NV2024006', ENCRYPTBYPASSPHRASE('123', '22082001'), 'TIEPNHANVIEN');
GO

-- 4. KHACH HANG
INSERT INTO KhachHang
    (maKH, hoTenKH, noiSinh, ngaySinh, CCCD, sdt, diaChi, email)
VALUES
('KH001', N'Nguyễn Văn An', N'TP. Hồ Chí Minh', '1990-05-12', '079090000001', '0901000001', N'Quận 1, TP. Hồ Chí Minh', 'an@example.com'),
('KH002', N'Trần Thị Bình', N'Đồng Nai', '1988-10-20', '079090000002', '0901000002', N'Thủ Đức, TP. Hồ Chí Minh', 'binh@example.com'),
('KH003', N'Lê Minh Cường', NULL, NULL, NULL, '0901000003', N'Bình Thạnh, TP. Hồ Chí Minh', NULL),
('KH004', N'Phạm Thị Dung', N'Long An', '1992-03-18', '080092000004', '0901000004', N'Tân Bình, TP. Hồ Chí Minh', 'dung@example.com'),
('KH005', N'Hoàng Quốc Đạt', N'TP. Hồ Chí Minh', '1985-07-09', '079085000005', '0901000005', N'Gò Vấp, TP. Hồ Chí Minh', 'dat@example.com'),
('KH006', N'Võ Ngọc Hà', N'Tiền Giang', '1995-12-01', '082095000006', '0901000006', N'Quận 7, TP. Hồ Chí Minh', 'ha@example.com'),
('KH007', N'Đặng Minh Khôi', N'TP. Hồ Chí Minh', '1991-02-14', '079091000007', '0901000007', N'Phú Nhuận, TP. Hồ Chí Minh', 'khoi@example.com'),
('KH008', N'Bùi Thanh Lan', N'Bình Dương', '1989-06-25', '074089000008', '0901000008', N'Quận 10, TP. Hồ Chí Minh', 'lan@example.com');
GO

-- 5. NHA CUNG CAP
INSERT INTO NhaCungCap (maNCC, tenNCC, diaChi, email, sdt)
VALUES
('NCC001', N'Công ty Phụ tùng ô tô Đắc Yến', N'TP. Hồ Chí Minh', 'congty.dacyen@gmail.com', '0283000001'),
('NCC002', N'Thành Dũng Auto', N'TP. Hồ Chí Minh', NULL, '0283000002'),
('NCC003', N'Phụ tùng ô tô Minh Phát', N'Bình Dương', 'minhphat@example.com', '0274000003'),
('NCC004', N'Auto Parts Sài Gòn', N'Đồng Nai', 'autoparts@example.com', '0251000004');
GO

-- 6. HANG MUC KIEM TRA
INSERT INTO HangMucKiemTra (maHangMuc, tenHangMuc, loaiHangMuc)
VALUES
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

-- 7. DICH VU
INSERT INTO DichVu (maDichVu, tenDichVu, loaiDichVu, donGiaHienTai)
VALUES
('DV001', N'Thay dầu động cơ', N'Bảo dưỡng', 250000),
('DV002', N'Kiểm tra hệ thống phanh', N'Kiểm tra', 150000),
('DV003', N'Vệ sinh xe', N'Vệ sinh', 200000),
('DV004', N'Kiểm tra hệ thống điện', N'Kiểm tra', 180000),
('DV005', N'Cân chỉnh góc đặt bánh xe', N'Bảo dưỡng', 350000),
('DV006', N'Bảo dưỡng hệ thống làm mát', N'Bảo dưỡng', 300000),
('DV007', N'Kiểm tra điều hòa', N'Kiểm tra', 120000),
('DV008', N'Đánh bóng thân xe', N'Làm đẹp', 500000);
GO

-- 8. CHUONG TRINH KHUYEN MAI
INSERT INTO ChuongTrinhKhuyenMai
    (maChuongTrinh, tenChuongTrinh, ngayBatDau, ngayKetThuc, loaiKhuyenMai, giaTriKhuyenMai)
VALUES
('KM001', N'Khuyến mãi khai trương', '2026-01-01', '2027-12-31', N'Trừ tiền', 100000),
('KM002', N'Ưu đãi lễ Quốc Khánh 2/9', '2026-09-02', '2026-09-05', N'Phần trăm', 5),
('KM003', N'Ưu đãi khách hàng thân thiết', '2026-01-01', '2027-12-31', N'Phần trăm', 3);
GO

-- 9. XE
INSERT INTO Xe
    (bienSoXe, loaiXe, congSuat, hangXe, mauXe, soThangDaDi, soKmHienTai, maKH)
VALUES
('51G-52451', N'Ô tô 4 chỗ', 1500, N'Toyota', N'Trắng', 36, 35000, 'KH001'),
('36F-23490', N'Ô tô 7 chỗ', 2000, N'Ford', N'Đen', 48, 52000, 'KH002'),
('72A-22982', N'Ô tô 4 chỗ', 1600, N'Honda', N'Đỏ', 24, 18000, 'KH003'),
('51H-12345', N'Ô tô 5 chỗ', 1600, N'Mazda', N'Trắng', 30, 42000, 'KH004'),
('51K-67890', N'Ô tô 5 chỗ', 1800, N'Hyundai', N'Bạc', 20, 27000, 'KH005'),
('60A-45678', N'Ô tô 7 chỗ', 2500, N'Toyota', N'Đen', 60, 78000, 'KH006'),
('51A-99887', N'Ô tô 4 chỗ', 1400, N'Kia', N'Xanh', 18, 15000, 'KH007'),
('61C-11223', N'Ô tô 5 chỗ', 1600, N'Honda', N'Xám', 42, 61000, 'KH008');
GO

-- 10. DANH MUC PHU TUNG
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

-- 11. PHU TUNG
INSERT INTO PhuTung
    (maPhuTung, tenPhuTung, maDanhMuc, donViTinh, donGiaHienTai, soLuongTon, ngaySanXuat, maNCC)
VALUES
('PT001', N'Bugi đánh lửa', 'PT', N'Cái', 120000, 50, '2026-01-15', 'NCC001'),
('PH001', N'Má phanh trước', 'PH', N'Bộ', 850000, 15, '2026-02-10', 'NCC002'),
('GT001', N'Giảm xóc trước', 'GT', N'Cái', 1500000, 8, '2026-01-20', 'NCC001'),
('DI001', N'Ắc quy ô tô', 'DI', N'Bình', 1800000, 12, '2026-03-05', 'NCC001'),
('LM001', N'Két nước làm mát', 'LM', N'Cái', 2500000, 5, '2026-02-18', 'NCC002'),
('LK001', N'Lọc dầu động cơ', 'LK', N'Cái', 180000, 40, '2026-03-12', 'NCC002'),
('NT001', N'Gương chiếu hậu', 'NT', N'Cái', 750000, 10, '2026-01-25', 'NCC001'),
('LP001', N'Lốp xe ô tô 16 inch', 'LP', N'Cái', 1800000, 20, '2026-02-28', 'NCC002'),
('PT002', N'Dây curoa động cơ', 'PT', N'Sợi', 450000, 18, '2026-03-01', 'NCC003'),
('PH002', N'Dầu phanh', 'PH', N'Chai', 160000, 30, '2026-03-08', 'NCC003'),
('DI002', N'Máy phát điện ô tô', 'DI', N'Cái', 3200000, 4, '2026-02-15', 'NCC004'),
('LK002', N'Lọc gió động cơ', 'LK', N'Cái', 220000, 25, '2026-03-10', 'NCC004');
GO

-- 12. PHIEU TIEP NHAN
INSERT INTO PhieuTiepNhan
    (maPTN, ngayTiepNhan, yeuCauKhachHang, soKmDaDi, maNVLapPhieu, bienSoXe, maKH)
VALUES
('PTN001', '2026-10-01T08:30:00', N'Xe cần thay dầu và kiểm tra phanh.', 35000, 'NV2024002', '51G-52451', 'KH001'),
('PTN002', '2026-10-02T09:00:00', N'Xe có tiếng kêu ở hệ thống treo.', 52000, 'NV2024002', '36F-23490', 'KH002'),
('PTN003', '2026-10-03T10:15:00', N'Kiểm tra tổng quát và hệ thống điện.', 18000, 'NV2024006', '72A-22982', 'KH003'),
('PTN004', '2026-10-04T13:30:00', N'Xe cần thay gương chiếu hậu.', 42000, 'NV2024006', '51H-12345', 'KH004'),
('PTN005', '2026-10-05T08:45:00', N'Bảo dưỡng định kỳ và kiểm tra điều hòa.', 27000, 'NV2024002', '51K-67890', 'KH005'),
('PTN006', '2026-10-06T14:00:00', N'Kiểm tra lốp và hệ thống phanh.', 78000, 'NV2024006', '60A-45678', 'KH006'),
('PTN007', '2026-10-07T09:20:00', N'Xe cần vệ sinh và kiểm tra ngoại thất.', 15000, 'NV2024002', '51A-99887', 'KH007'),
('PTN008', '2026-10-08T11:00:00', N'Kiểm tra hệ thống làm mát.', 61000, 'NV2024006', '61C-11223', 'KH008');
GO

-- 13. CHI TIET HANG MUC
INSERT INTO ChiTietHangMuc (maPTN, maHangMuc, tinhTrang)
VALUES
('PTN001', 'NG003', N'Lốp xe còn sử dụng tốt'),
('PTN001', 'NT011', N'Bàn đạp hoạt động bình thường'),
('PTN001', 'NG002', N'Đèn hoạt động bình thường'),
('PTN002', 'NG003', N'Lốp trước mòn nhẹ'),
('PTN002', 'NG019', N'Vè bánh xe bình thường'),
('PTN003', 'NT006', N'Bảng điều khiển hoạt động bình thường'),
('PTN003', 'NG002', N'Đèn trước hoạt động không ổn định'),
('PTN004', 'NG001', N'Gương chiếu hậu bên phải bị nứt'),
('PTN004', 'NG013', N'Cửa xe hoạt động bình thường'),
('PTN005', 'NT008', N'Điều hòa làm mát yếu'),
('PTN005', 'NT009', N'Cửa gió có bụi'),
('PTN006', 'NG003', N'Lốp sau mòn'),
('PTN006', 'NT011', N'Hệ thống phanh cần kiểm tra'),
('PTN007', 'NG004', N'Lớp sơn có vết bẩn'),
('PTN007', 'NG005', N'Kính lái bình thường'),
('PTN008', 'NG002', N'Đèn hoạt động bình thường'),
('PTN008', 'NT008', N'Cần kiểm tra hệ thống làm mát và điều hòa');
GO

-- 14. PHIEU SUA CHUA
-- Cac tong tien duoi day khop voi tong cac dong chi tiet ben duoi.
INSERT INTO PhieuSuaChua
    (maPSC, ngayLapPhieu, trangThai, maPTN, maNVLapPhieu, maNVPhuTrach,
     tongTienDichVu, tongTienPhuTung, tongChiPhiDuKien)
VALUES
('PSC001', '2026-10-01T09:00:00', N'Hoàn thành', 'PTN001', 'NV2024002', 'NV2024003', 400000, 300000, 700000),
('PSC002', '2026-10-02T10:00:00', N'Hoàn thành', 'PTN002', 'NV2024002', 'NV2024005', 350000, 1500000, 1850000),
('PSC003', '2026-10-03T11:00:00', N'Hoàn thành', 'PTN003', 'NV2024006', 'NV2024003', 180000, 1800000, 1980000),
('PSC004', '2026-10-04T14:00:00', N'Hoàn thành', 'PTN004', 'NV2024006', 'NV2024005', 200000, 750000, 950000),
('PSC005', '2026-10-05T09:30:00', N'Hoàn thành', 'PTN005', 'NV2024002', 'NV2024003', 370000, 400000, 770000),
('PSC006', '2026-10-06T15:00:00', N'Hoàn thành', 'PTN006', 'NV2024006', 'NV2024005', 500000, 1010000, 1510000),
('PSC007', '2026-10-07T10:00:00', N'Hoàn thành', 'PTN007', 'NV2024002', 'NV2024003', 700000, 0, 700000),
('PSC008', '2026-10-08T12:00:00', N'Hoàn thành', 'PTN008', 'NV2024006', 'NV2024005', 300000, 2500000, 2800000);
GO

-- 15. CHI TIET DICH VU
INSERT INTO ChiTietDichVu (maPSC, maDichVu, soLuong, donGia, ghiChu)
VALUES
('PSC001', 'DV001', 1, 250000, N'Thay dầu động cơ'),
('PSC001', 'DV002', 1, 150000, N'Kiểm tra hệ thống phanh'),
('PSC002', 'DV005', 1, 350000, N'Cân chỉnh sau khi kiểm tra gầm'),
('PSC003', 'DV004', 1, 180000, N'Kiểm tra hệ thống điện'),
('PSC004', 'DV003', 1, 200000, N'Vệ sinh xe'),
('PSC005', 'DV001', 1, 250000, N'Thay dầu động cơ'),
('PSC005', 'DV007', 1, 120000, N'Kiểm tra điều hòa'),
('PSC006', 'DV002', 1, 150000, N'Kiểm tra hệ thống phanh'),
('PSC006', 'DV005', 1, 350000, N'Cân chỉnh góc đặt bánh xe'),
('PSC007', 'DV003', 1, 200000, N'Vệ sinh xe'),
('PSC007', 'DV008', 1, 500000, N'Đánh bóng thân xe'),
('PSC008', 'DV006', 1, 300000, N'Bảo dưỡng hệ thống làm mát');
GO

-- 16. CHI TIET PHU TUNG
INSERT INTO ChiTietPhuTung (maPSC, maPhuTung, soLuong, donGia, ghiChu)
VALUES
('PSC001', 'LK001', 1, 180000, N'Lọc dầu động cơ'),
('PSC001', 'PT001', 1, 120000, N'Bugi đánh lửa'),
('PSC002', 'GT001', 1, 1500000, N'Thay giảm xóc trước'),
('PSC003', 'DI001', 1, 1800000, N'Thay ắc quy'),
('PSC004', 'NT001', 1, 750000, N'Thay gương chiếu hậu'),
('PSC005', 'LK001', 1, 240000, N'Lọc dầu động cơ - đơn giá tại thời điểm sửa chữa'),
('PSC005', 'PH002', 1, 160000, N'Bổ sung dầu phanh'),
('PSC006', 'PH001', 1, 850000, N'Thay má phanh trước'),
('PSC006', 'PH002', 1, 160000, N'Dầu phanh'),
('PSC008', 'LM001', 1, 2500000, N'Thay két nước làm mát');
GO

-- 17. PHIEU NHAP KHO
INSERT INTO PhieuNhapKho
    (maPNK, ngayNhap, maNVLap, maNCC, ghiChu, tongTienNhapKho)
VALUES
('PNK001', '2026-09-20T08:00:00', 'NV2024001', 'NCC001', N'Nhập phụ tùng đợt 1.', 22200000),
('PNK002', '2026-09-22T09:30:00', 'NV2024001', 'NCC002', N'Nhập phụ tùng phục vụ bảo dưỡng.', 15300000),
('PNK003', '2026-09-25T10:00:00', 'NV2024001', 'NCC003', N'Nhập vật tư thay thế.', 10000000),
('PNK004', '2026-09-28T14:00:00', 'NV2024001', 'NCC004', N'Nhập phụ tùng hệ thống điện và lọc.', 6620000);
GO

-- 18. CHI TIET PHIEU NHAP KHO
INSERT INTO ChiTietPhieuNhapKho (maPNK, maPhuTung, soLuong, donGiaNhap)
VALUES
('PNK001', 'PT001', 30, 100000),
('PNK001', 'GT001', 8, 1500000),
('PNK001', 'DI001', 4, 1800000),
('PNK002', 'PH001', 10, 850000),
('PNK002', 'LM001', 2, 2500000),
('PNK002', 'LK001', 10, 180000),
('PNK003', 'PT002', 10, 450000),
('PNK003', 'PH002', 25, 160000),
('PNK003', 'NT001', 2, 750000),
('PNK004', 'DI002', 1, 3200000),
('PNK004', 'LK002', 10, 220000),
('PNK004', 'LP001', 1, 1220000);
GO

-- 19. HOA DON
-- tienGiamGia: gia tri uu dai theo loai khuyen mai (VND hoac %).
-- soTienGiamGia: so tien duoc giam thuc te.
-- tongTienThanhToan = tongChiPhiDuKien - soTienGiamGia.
INSERT INTO HoaDon
    (maHoaDon, ngayLap, hinhThucThanhToan, trangThai, maPSC,
     maNVThuNgan, maKH, maChuongTrinh, tienGiamGia, soTienGiamGia, tongTienThanhToan)
VALUES
('HD001', '2026-10-01T10:30:00', N'Tiền mặt', N'Đã thanh toán', 'PSC001', 'NV2024004', 'KH001', 'KM001', 100000, 100000, 600000),
('HD002', '2026-10-02T12:00:00', N'Chuyển khoản', N'Đã thanh toán', 'PSC002', 'NV2024004', 'KH002', 'KM003', 3, 55500, 1794500),
('HD003', '2026-10-03T13:00:00', N'Tiền mặt', N'Đã thanh toán', 'PSC003', 'NV2024004', 'KH003', NULL, 0, 0, 1980000),
('HD004', '2026-10-04T15:30:00', N'Chuyển khoản', N'Đã thanh toán', 'PSC004', 'NV2024004', 'KH004', 'KM001', 100000, 100000, 850000),
('HD005', '2026-10-05T11:00:00', N'Tiền mặt', N'Đã thanh toán', 'PSC005', 'NV2024004', 'KH005', 'KM003', 3, 23100, 746900),
('HD006', '2026-10-06T16:00:00', N'Chuyển khoản', N'Đã thanh toán', 'PSC006', 'NV2024004', 'KH006', NULL, 0, 0, 1510000),
('HD007', '2026-10-07T11:30:00', N'Tiền mặt', N'Đã thanh toán', 'PSC007', 'NV2024004', 'KH007', 'KM001', 100000, 100000, 600000),
('HD008', '2026-10-08T13:00:00', N'Chuyển khoản', N'Đã thanh toán', 'PSC008', 'NV2024004', 'KH008', NULL, 0, 0, 2800000);
GO

/*==============================================================
  PHAN 3. CAC CAU LENH KIEM TRA DU LIEU
==============================================================*/

SELECT * FROM VaiTro;
SELECT * FROM NhanVien;
SELECT * FROM TaiKhoan;
SELECT * FROM KhachHang;
SELECT * FROM NhaCungCap;
SELECT * FROM HangMucKiemTra;
SELECT * FROM DichVu;
SELECT * FROM ChuongTrinhKhuyenMai;
SELECT * FROM Xe;
SELECT * FROM DanhMucPhuTung;
SELECT * FROM PhuTung;
SELECT * FROM PhieuTiepNhan;
SELECT * FROM ChiTietHangMuc;
SELECT * FROM PhieuSuaChua;
SELECT * FROM ChiTietDichVu;
SELECT * FROM ChiTietPhuTung;
SELECT * FROM HoaDon;
SELECT * FROM PhieuNhapKho;
SELECT * FROM ChiTietPhieuNhapKho;
GO

-- Kiem tra giai ma tai khoan mau:
SELECT
    tenDangNhap,
    CONVERT(VARCHAR(100), DECRYPTBYPASSPHRASE('123', matKhau)) AS matKhauGiaiMa,
    maVaiTro
FROM TaiKhoan
WHERE tenDangNhap = 'NV2024001';
GO
