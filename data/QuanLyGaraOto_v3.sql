/*==============================================================
  DATABASE: QuanLyGaraOto
  MUC DICH : Tao CSDL theo so do class moi va nap du lieu mau
  GHI CHU  :
  - Chinh xac 21 bang theo so do class moi.
  - Cac thuoc tinh kieu double trong UML duoc luu bang FLOAT.
  - Cac thuoc tinh dan xuat (bat dau bang / trong UML) khong tao cot.
  - Du lieu bang khong thay doi duoc giu nguyen; du lieu bang thay doi
    duoc tao lai theo cau truc moi dua tren cac mau cu.
==============================================================*/

IF DB_ID(N'QuanLyGaraOto') IS NULL
    CREATE DATABASE QuanLyGaraOto;
GO
USE QuanLyGaraOto;
GO
IF OBJECT_ID(N'dbo.VaiTro', N'U') IS NOT NULL
    THROW 50001, N'Database QuanLyGaraOto da co bang. Hay sao luu va chay tren database trong.', 1;
GO

/*==================== PHAN 1. TAO 21 BANG ====================*/
--1
CREATE TABLE VaiTro (
    maVaiTro VARCHAR(50) PRIMARY KEY,
    tenVaiTro NVARCHAR(100),
    moTa NVARCHAR(500)
);
GO
--2
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
--3
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
--4
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
--5
CREATE TABLE NhaCungCap (
    maNCC VARCHAR(50) PRIMARY KEY,
    tenNCC NVARCHAR(150) NOT NULL,
    diaChi NVARCHAR(255) NULL,
    email VARCHAR(100) NULL,
    sdt VARCHAR(15) NULL
);
GO
--6
CREATE TABLE HangMucKiemTra (
    maHangMuc VARCHAR(50) PRIMARY KEY,
    tenHangMuc NVARCHAR(150) NOT NULL,
    loaiHangMuc NVARCHAR(100) NULL
);
GO
--7
CREATE TABLE DichVu (
    maDichVu VARCHAR(50) PRIMARY KEY,
    tenDichVu NVARCHAR(150) NOT NULL,
    loaiDichVu NVARCHAR(100) NULL,
    donGiaHienTai FLOAT NOT NULL CHECK (donGiaHienTai >= 0),
    thueSuatVAT FLOAT NOT NULL CONSTRAINT DF_DichVu_VAT DEFAULT 0
        CHECK (thueSuatVAT >= 0 AND thueSuatVAT <= 100)
);
GO
--8 
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
--9
CREATE TABLE Xe (
    bienSoXe VARCHAR(20) PRIMARY KEY,
    loaiXe NVARCHAR(100) NULL,
    congSuat INT NULL CHECK (congSuat IS NULL OR congSuat > 0),
    hangXe NVARCHAR(100) NULL,
    mauXe NVARCHAR(50) NULL,
    soThangDaDi INT NULL CHECK (soThangDaDi IS NULL OR soThangDaDi >= 0)    
);
GO
--10
CREATE TABLE PhieuTiepNhan (
    maPTN VARCHAR(50) PRIMARY KEY,
    ngayTiepNhan DATETIME NOT NULL DEFAULT GETDATE(),
    yeuCauKhachHang NVARCHAR(500) NULL,
    soKmDaDi FLOAT NOT NULL CHECK (soKmDaDi >= 0),
    trangThai NVARCHAR(50) NOT NULL DEFAULT N'Chờ phân công',
    maNVLapPhieu VARCHAR(50) NOT NULL,
    maNVKyThuatPhuTrach VARCHAR(50) NULL,
    ngayPhanCong DATETIME NULL,
    bienSoXe VARCHAR(20) NOT NULL,
    maKH VARCHAR(50) NOT NULL,
    CONSTRAINT FK_PTN_NhanVienLap FOREIGN KEY (maNVLapPhieu)
        REFERENCES NhanVien(maNV),
    CONSTRAINT FK_PTN_KyThuatVien FOREIGN KEY (maNVKyThuatPhuTrach)
        REFERENCES NhanVien(maNV),
    CONSTRAINT FK_PTN_Xe FOREIGN KEY (bienSoXe)
        REFERENCES Xe(bienSoXe),
    CONSTRAINT FK_PTN_KhachHang FOREIGN KEY (maKH)
        REFERENCES KhachHang(maKH)
);
GO
--11
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
--12
CREATE TABLE PhieuSuaChua (
    maPSC VARCHAR(50) PRIMARY KEY,
    ngayLapPhieu DATETIME NOT NULL DEFAULT GETDATE(),
    trangThai NVARCHAR(50) NOT NULL DEFAULT N'Đang sửa chữa',
    maPTN VARCHAR(50) NOT NULL UNIQUE,
    maNVPhuTrach VARCHAR(50) NOT NULL,
    CONSTRAINT FK_PSC_PhieuTiepNhan FOREIGN KEY (maPTN)
        REFERENCES PhieuTiepNhan(maPTN),
    CONSTRAINT FK_PSC_NhanVienPhuTrach FOREIGN KEY (maNVPhuTrach)
        REFERENCES NhanVien(maNV)
);
GO
--13
CREATE TABLE DanhMucPhuTung (
    maDanhMuc VARCHAR(10) PRIMARY KEY,
    tenDanhMuc NVARCHAR(100) NOT NULL
);
GO
--14
CREATE TABLE PhuTung (
    maPhuTung VARCHAR(50) PRIMARY KEY,
    tenPhuTung NVARCHAR(150) NOT NULL,
    donViTinh NVARCHAR(30) NULL,
    donGiaHienTai FLOAT NOT NULL CHECK (donGiaHienTai >= 0),
    thueSuatVAT FLOAT NOT NULL CONSTRAINT DF_PhuTung_VAT DEFAULT 0
        CHECK (thueSuatVAT >= 0 AND thueSuatVAT <= 100),
    maDanhMuc VARCHAR(10) NULL,
    CONSTRAINT FK_PhuTung_DanhMucPhuTung FOREIGN KEY (maDanhMuc)
        REFERENCES DanhMucPhuTung(maDanhMuc)
);
GO
--15
CREATE TABLE ChiTietPhuTung (
    maPSC VARCHAR(50) NOT NULL,
    maPhuTung VARCHAR(50) NOT NULL,
    soLuongDuKien INT NOT NULL CHECK (soLuongDuKien > 0),
    donGia FLOAT NOT NULL CHECK (donGia >= 0),
    thueSuatVAT FLOAT NOT NULL CHECK (thueSuatVAT >= 0 AND thueSuatVAT <=100),
    soLuongDaDung INT NOT NULL DEFAULT 0 CHECK (soLuongDaDung >= 0),
    soLuongCapPhat INT NOT NULL DEFAULT 0 CHECK (soLuongCapPhat >= 0),
    CONSTRAINT PK_ChiTietPhuTung PRIMARY KEY (maPSC, maPhuTung),
    CONSTRAINT FK_CTPThu_PhieuSuaChua FOREIGN KEY (maPSC)
        REFERENCES PhieuSuaChua(maPSC),
    CONSTRAINT FK_CTPThu_PhuTung FOREIGN KEY (maPhuTung)
        REFERENCES PhuTung(maPhuTung),
    CONSTRAINT CK_CTPThu_SoLuong CHECK (soLuongDaDung <= soLuongCapPhat)
);
GO
--16
CREATE TABLE ChiTietDichVu (
    maPSC VARCHAR(50) NOT NULL,
    maDichVu VARCHAR(50) NOT NULL,
    soLuong INT NOT NULL CHECK (soLuong > 0),
    donGia FLOAT NOT NULL CHECK (donGia >= 0),
    thueSuatVAT FLOAT NOT NULL CHECK (thueSuatVAT >= 0 AND thueSuatVAT<=100),
    ghiChu NVARCHAR(255) NULL,
    thoiGianCapNhat DATETIME NULL,
    trangThai NVARCHAR(30) NOT NULL DEFAULT N'Chưa thực hiện'
        CHECK (trangThai IN (N'Chưa thực hiện', N'Đang thực hiện', N'Hoàn thành')),
    CONSTRAINT PK_ChiTietDichVu PRIMARY KEY (maPSC, maDichVu),
    CONSTRAINT FK_CTDV_PhieuSuaChua FOREIGN KEY (maPSC)
        REFERENCES PhieuSuaChua(maPSC),
    CONSTRAINT FK_CTDV_DichVu FOREIGN KEY (maDichVu)
        REFERENCES DichVu(maDichVu)
);
GO
--17
CREATE TABLE PhieuNhapKho (
    maPNK VARCHAR(50) PRIMARY KEY,
    ngayNhap DATETIME NOT NULL DEFAULT GETDATE(),
    maNVLap VARCHAR(50) NOT NULL,
    maNCC VARCHAR(50) NOT NULL,
    ghiChu NVARCHAR(500) NULL,
    CONSTRAINT FK_PNK_NhanVien FOREIGN KEY (maNVLap)
        REFERENCES NhanVien(maNV),
    CONSTRAINT FK_PNK_NhaCungCap FOREIGN KEY (maNCC)
        REFERENCES NhaCungCap(maNCC)
);
GO
--18
CREATE TABLE LoPhuTung (
    maLo VARCHAR(50) PRIMARY KEY,
    ngaySanXuat DATE NULL,
    hanSuDung DATE NULL,
    donGiaNhap FLOAT NOT NULL CHECK (donGiaNhap >= 0),
    soLuongNhap INT NOT NULL CHECK (soLuongNhap > 0),
    soLuongTon INT NOT NULL,
    maPhuTung VARCHAR(50) NOT NULL,
    maPNK VARCHAR(50) NOT NULL,

    CONSTRAINT CK_LoPhuTung_SoLuongTon
        CHECK (soLuongTon >= 0 AND soLuongTon <= soLuongNhap),

    CONSTRAINT FK_LoPhuTung_PhuTung
        FOREIGN KEY (maPhuTung) REFERENCES PhuTung(maPhuTung),

    CONSTRAINT FK_LoPhuTung_PhieuNhapKho
        FOREIGN KEY (maPNK) REFERENCES PhieuNhapKho(maPNK)
);
GO
--19
CREATE TABLE PhieuYeuCauXuatKho (
    maYCXK VARCHAR(50) PRIMARY KEY,
    ngayYeuCau DATETIME NOT NULL DEFAULT GETDATE(),
    ngayDuyet DATETIME NULL,
    trangThai NVARCHAR(50) NOT NULL DEFAULT N'Chờ duyệt',
    ghiChu NVARCHAR(500) NULL,
    maNVYeuCau VARCHAR(50) NOT NULL,
    maNVDuyet VARCHAR(50) NULL,
    maPSC VARCHAR(50) NOT NULL,
    CONSTRAINT FK_YCXK_NhanVienYeuCau FOREIGN KEY (maNVYeuCau)
        REFERENCES NhanVien(maNV),
    CONSTRAINT FK_YCXK_NhanVienDuyet FOREIGN KEY (maNVDuyet)
        REFERENCES NhanVien(maNV),
    CONSTRAINT FK_YCXK_PhieuSuaChua FOREIGN KEY (maPSC)
        REFERENCES PhieuSuaChua(maPSC)
);
GO
--20
CREATE TABLE ChiTietYeuCauXuatKho (
    maYCXK VARCHAR(50) NOT NULL,
    maLo VARCHAR(50) NOT NULL,
    soLuongYeuCau INT NOT NULL CHECK (soLuongYeuCau > 0),
    CONSTRAINT PK_ChiTietYeuCauXuatKho PRIMARY KEY (maYCXK, maLo),
    CONSTRAINT FK_CTYCXK_PhieuYeuCau FOREIGN KEY (maYCXK)
        REFERENCES PhieuYeuCauXuatKho(maYCXK),
    CONSTRAINT FK_CTYCXK_LoPhuTung FOREIGN KEY (maLo)
        REFERENCES LoPhuTung(maLo)
);
GO
--21
CREATE TABLE HoaDon (
    maHoaDon VARCHAR(50) PRIMARY KEY,
    ngayLap DATETIME NOT NULL DEFAULT GETDATE(),
    hinhThucThanhToan NVARCHAR(50) NULL,
    trangThai NVARCHAR(50) NOT NULL DEFAULT N'Chưa thanh toán',
    maPSC VARCHAR(50) NOT NULL UNIQUE,
    maNVThuNgan VARCHAR(50) NOT NULL,
    maKH VARCHAR(50) NOT NULL,
    maChuongTrinh VARCHAR(50) NULL,
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


/*==================== PHAN 2. DU LIEU MAU ====================*/

-- DU LIEU MAU: VaiTro
INSERT INTO VaiTro (maVaiTro, tenVaiTro, moTa)
VALUES
('QUANLY', N'Quản lý', N'Quản lý và giám sát hoạt động của gara.'),
('TIEPNHANVIEN', N'Tiếp nhận viên', N'Tiếp nhận xe và lập phiếu tiếp nhận.'),
('KYTHUATVIEN', N'Kỹ thuật viên', N'Kiểm tra, bảo dưỡng và sửa chữa xe.'),
('THUNGAN', N'Thu ngân', N'Lập hóa đơn và thực hiện thanh toán.');
GO

-- DU LIEU MAU: NhanVien
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

-- DU LIEU MAU: TaiKhoan
INSERT INTO TaiKhoan (tenDangNhap, matKhau, maVaiTro)
VALUES
('NV2024001', ENCRYPTBYPASSPHRASE('123', '15092006'), 'QUANLY'),
('NV2024002', ENCRYPTBYPASSPHRASE('123', '03112006'), 'TIEPNHANVIEN'),
('NV2024003', ENCRYPTBYPASSPHRASE('123', '15092006'), 'KYTHUATVIEN'),
('NV2024004', ENCRYPTBYPASSPHRASE('123', '15092006'), 'THUNGAN'),
('NV2024005', ENCRYPTBYPASSPHRASE('123', '12042002'), 'KYTHUATVIEN'),
('NV2024006', ENCRYPTBYPASSPHRASE('123', '22082001'), 'TIEPNHANVIEN');
GO

-- DU LIEU MAU: KhachHang
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

-- DU LIEU MAU: NhaCungCap
INSERT INTO NhaCungCap (maNCC, tenNCC, diaChi, email, sdt)
VALUES
('NCC001', N'Công ty Phụ tùng ô tô Đắc Yến', N'TP. Hồ Chí Minh', 'congty.dacyen@gmail.com', '0283000001'),
('NCC002', N'Thành Dũng Auto', N'TP. Hồ Chí Minh', NULL, '0283000002'),
('NCC003', N'Phụ tùng ô tô Minh Phát', N'Bình Dương', 'minhphat@example.com', '0274000003'),
('NCC004', N'Auto Parts Sài Gòn', N'Đồng Nai', 'autoparts@example.com', '0251000004');
GO

-- DU LIEU MAU: HangMucKiemTra
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

-- DU LIEU MAU: ChuongTrinhKhuyenMai
INSERT INTO ChuongTrinhKhuyenMai
    (maChuongTrinh, tenChuongTrinh, ngayBatDau, ngayKetThuc, loaiKhuyenMai, giaTriKhuyenMai)
VALUES
('KM001', N'Khuyến mãi khai trương', '2026-01-01', '2027-12-31', N'Trừ tiền', 100000),
('KM002', N'Ưu đãi lễ Quốc Khánh 2/9', '2026-09-02', '2026-09-05', N'Phần trăm', 5),
('KM003', N'Ưu đãi khách hàng thân thiết', '2026-01-01', '2027-12-31', N'Phần trăm', 3);
GO

-- DU LIEU MAU: Xe
INSERT INTO Xe
    (bienSoXe, loaiXe, congSuat, hangXe, mauXe, soThangDaDi)
VALUES
('51G-52451', N'Ô tô 4 chỗ', 1500, N'Toyota', N'Trắng', 36),
('36F-23490', N'Ô tô 7 chỗ', 2000, N'Ford', N'Đen', 48),
('72A-22982', N'Ô tô 4 chỗ', 1600, N'Honda', N'Đỏ', 24),
('51H-12345', N'Ô tô 5 chỗ', 1600, N'Mazda', N'Trắng',30),
('51K-67890', N'Ô tô 5 chỗ', 1800, N'Hyundai', N'Bạc', 20),
('60A-45678', N'Ô tô 7 chỗ', 2500, N'Toyota', N'Đen', 60),
('51A-99887', N'Ô tô 4 chỗ', 1400, N'Kia', N'Xanh', 18),
('61C-11223', N'Ô tô 5 chỗ', 1600, N'Honda', N'Xám', 42);
GO

-- DU LIEU MAU: DanhMucPhuTung
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

-- DU LIEU MAU MOI: DichVu (bo sung thueSuatVAT)
INSERT INTO DichVu (maDichVu, tenDichVu, loaiDichVu, donGiaHienTai, thueSuatVAT)
VALUES
('DV001', N'Thay dầu động cơ', N'Bảo dưỡng', 250000, 8.00),
('DV002', N'Kiểm tra hệ thống phanh', N'Kiểm tra', 150000, 8.00),
('DV003', N'Vệ sinh xe', N'Vệ sinh', 200000, 0.00),
('DV004', N'Kiểm tra hệ thống điện', N'Kiểm tra', 180000, 8.00),
('DV005', N'Cân chỉnh góc đặt bánh xe', N'Bảo dưỡng', 350000, 8.00),
('DV006', N'Bảo dưỡng hệ thống làm mát', N'Bảo dưỡng', 300000, 8.00),
('DV007', N'Kiểm tra điều hòa', N'Kiểm tra', 120000, 8.00),
('DV008', N'Đánh bóng thân xe', N'Làm đẹp', 500000, 8.00);
GO

-- DU LIEU MAU MOI: PhuTung (ton kho duoc tong hop tu LoPhuTung)
INSERT INTO PhuTung (maPhuTung, tenPhuTung, donViTinh, donGiaHienTai, thueSuatVAT, maDanhMuc)
VALUES
('PT001', N'Bugi đánh lửa', N'Cái', 120000, 8.00, 'PT'),
('PH001', N'Má phanh trước', N'Bộ', 850000, 8.00, 'PH'),
('GT001', N'Giảm xóc trước', N'Cái', 1500000, 8.00, 'GT'),
('DI001', N'Ắc quy ô tô', N'Bình', 1800000, 8.00, 'DI'),
('LM001', N'Két nước làm mát', N'Cái', 2500000, 8.00, 'LM'),
('LK001', N'Lọc dầu động cơ', N'Cái', 180000, 8.00, 'LK'),
('NT001', N'Gương chiếu hậu', N'Cái', 750000, 8.00, 'NT'),
('LP001', N'Lốp xe ô tô 16 inch', N'Cái', 1800000, 8.00, 'LP'),
('PT002', N'Dây curoa động cơ', N'Sợi', 450000, 8.00, 'PT'),
('PH002', N'Dầu phanh', N'Chai', 160000, 8.00, 'PH'),
('DI002', N'Máy phát điện ô tô', N'Cái', 3200000, 8.00, 'DI'),
('LK002', N'Lọc gió động cơ', N'Cái', 220000, 8.00, 'LK');
GO

-- DU LIEU MAU MOI: PhieuTiepNhan (bo sung ky thuat vien va ngay phan cong)
INSERT INTO PhieuTiepNhan (maPTN, ngayTiepNhan, yeuCauKhachHang, soKmDaDi, trangThai, maNVLapPhieu, maNVKyThuatPhuTrach, ngayPhanCong, bienSoXe, maKH)
VALUES
('PTN01102026001', '2026-10-01T08:30:00', N'Xe cần thay dầu và kiểm tra phanh.', 35000, N'Đã lập phiếu sửa chữa', 'NV2024002', 'NV2024003', '2026-10-01T09:00:00', '51G-52451', 'KH001'),
('PTN02102026001', '2026-10-02T09:00:00', N'Xe có tiếng kêu ở hệ thống treo.', 52000, N'Đã lập phiếu sửa chữa', 'NV2024002', 'NV2024005', '2026-10-02T10:00:00', '36F-23490', 'KH002'),
('PTN03102026001', '2026-10-03T10:15:00', N'Kiểm tra tổng quát và hệ thống điện.', 18000, N'Đã lập phiếu sửa chữa', 'NV2024006', 'NV2024003', '2026-10-03T11:00:00', '72A-22982', 'KH003'),
('PTN04102026001', '2026-10-04T13:30:00', N'Xe cần thay gương chiếu hậu.', 42000, N'Đã lập phiếu sửa chữa', 'NV2024006', 'NV2024005', '2026-10-04T14:00:00', '51H-12345', 'KH004'),
('PTN05102026001', '2026-10-05T08:45:00', N'Bảo dưỡng định kỳ và kiểm tra điều hòa.', 27000, N'Đã lập phiếu sửa chữa', 'NV2024002', 'NV2024003', '2026-10-05T09:30:00', '51K-67890', 'KH005'),
('PTN06102026001', '2026-10-06T14:00:00', N'Kiểm tra lốp và hệ thống phanh.', 78000, N'Đã lập phiếu sửa chữa', 'NV2024006', 'NV2024005', '2026-10-06T15:00:00', '60A-45678', 'KH006'),
('PTN07102026001', '2026-10-07T09:20:00', N'Xe cần vệ sinh và kiểm tra ngoại thất.', 15000, N'Đã lập phiếu sửa chữa', 'NV2024002', 'NV2024003', '2026-10-07T10:00:00', '51A-99887', 'KH007'),
('PTN08102026001', '2026-10-08T11:00:00', N'Kiểm tra hệ thống làm mát.', 61000, N'Đã lập phiếu sửa chữa', 'NV2024006', 'NV2024005', '2026-10-08T12:00:00', '61C-11223', 'KH008'),
('PTN09102026001', '2026-10-09T08:00:00', N'Kiểm tra định kỳ và bảo dưỡng phanh.', 36000, N'Đã lập phiếu sửa chữa', 'NV2024002', 'NV2024003', '2026-10-09T09:30:00', '51G-52451', 'KH001'),
('PTN10102026001', '2026-10-10T09:00:00', N'Xe có tiếng kêu lạ ở động cơ.', 53000, N'Đã lập phiếu sửa chữa', 'NV2024002', 'NV2024005', '2026-10-10T10:00:00', '36F-23490', 'KH002');
GO

-- DU LIEU MAU GIU NGUYEN: ChiTietHangMuc
INSERT INTO ChiTietHangMuc (maPTN, maHangMuc, tinhTrang)
VALUES
('PTN01102026001', 'NG003', N'Lốp xe còn sử dụng tốt'),
('PTN01102026001', 'NT011', N'Bàn đạp hoạt động bình thường'),
('PTN01102026001', 'NG002', N'Đèn hoạt động bình thường'),
('PTN02102026001', 'NG003', N'Lốp trước mòn nhẹ'),
('PTN02102026001', 'NG019', N'Vè bánh xe bình thường'),
('PTN03102026001', 'NT006', N'Bảng điều khiển hoạt động bình thường'),
('PTN03102026001', 'NG002', N'Đèn trước hoạt động không ổn định'),
('PTN04102026001', 'NG001', N'Gương chiếu hậu bên phải bị nứt'),
('PTN04102026001', 'NG013', N'Cửa xe hoạt động bình thường'),
('PTN05102026001', 'NT008', N'Điều hòa làm mát yếu'),
('PTN05102026001', 'NT009', N'Cửa gió có bụi'),
('PTN06102026001', 'NG003', N'Lốp sau mòn'),
('PTN06102026001', 'NT011', N'Hệ thống phanh cần kiểm tra'),
('PTN07102026001', 'NG004', N'Lớp sơn có vết bẩn'),
('PTN07102026001', 'NG005', N'Kính lái bình thường'),
('PTN08102026001', 'NG002', N'Đèn hoạt động bình thường'),
('PTN08102026001', 'NT008', N'Cần kiểm tra hệ thống làm mát và điều hòa');
GO

INSERT INTO PhieuTiepNhan (maPTN, ngayTiepNhan, yeuCauKhachHang, soKmDaDi, trangThai, maNVLapPhieu, maNVKyThuatPhuTrach, ngayPhanCong, bienSoXe, maKH)
VALUES ('PTN11102026001', '2026-10-11T08:15:00', N'Kiểm tra và thay lọc gió động cơ.', 62000, N'Đã lập phiếu sửa chữa', 'NV2024002', 'NV2024005', '2026-10-11T09:00:00', '61C-11223', 'KH008');
GO
-- DU LIEU MAU MOI: PhieuSuaChua (tong tien la thuoc tinh dan xuat)
INSERT INTO PhieuSuaChua (maPSC, ngayLapPhieu, trangThai, maPTN, maNVPhuTrach)
VALUES
('PSC01102026001', '2026-10-01T09:00:00', N'Hoàn thành', 'PTN01102026001', 'NV2024003'),
('PSC02102026001', '2026-10-02T10:00:00', N'Hoàn thành', 'PTN02102026001', 'NV2024005'),
('PSC03102026001', '2026-10-03T11:00:00', N'Hoàn thành', 'PTN03102026001', 'NV2024003'),
('PSC04102026001', '2026-10-04T14:00:00', N'Hoàn thành', 'PTN04102026001', 'NV2024005'),
('PSC05102026001', '2026-10-05T09:30:00', N'Hoàn thành', 'PTN05102026001', 'NV2024003'),
('PSC06102026001', '2026-10-06T15:00:00', N'Hoàn thành', 'PTN06102026001', 'NV2024005'),
('PSC07102026001', '2026-10-07T10:00:00', N'Hoàn thành', 'PTN07102026001', 'NV2024003'),
('PSC08102026001', '2026-10-08T12:00:00', N'Hoàn thành', 'PTN08102026001', 'NV2024005'),
('PSC09102026001', '2026-10-09T09:30:00', N'Hoàn thành', 'PTN09102026001', 'NV2024003'),
('PSC10102026001', '2026-10-10T10:00:00', N'Hoàn thành', 'PTN10102026001', 'NV2024005'),
('PSC11102026001', '2026-10-11T09:15:00', N'Đang sửa chữa', 'PTN11102026001', 'NV2024005');
GO

-- DU LIEU MAU MOI: ChiTietDichVu (them trangThai va thoiGianCapNhat)
INSERT INTO ChiTietDichVu (maPSC, maDichVu, soLuong, donGia, thueSuatVAT, ghiChu, thoiGianCapNhat, trangThai)
VALUES
('PSC01102026001', 'DV001', 1, 250000, 8, N'Thay dầu động cơ', '2026-10-01T16:00:00', N'Hoàn thành'),
('PSC01102026001', 'DV002', 1, 150000, 8, N'Kiểm tra hệ thống phanh', '2026-10-01T16:30:00', N'Hoàn thành'),
('PSC02102026001', 'DV005', 1, 350000, 8, N'Cân chỉnh sau khi kiểm tra gầm', '2026-10-02T16:00:00', N'Hoàn thành'),
('PSC03102026001', 'DV004', 1, 180000, 8, N'Kiểm tra hệ thống điện', '2026-10-03T16:00:00', N'Hoàn thành'),
('PSC04102026001', 'DV003', 1, 200000, 8, N'Vệ sinh xe', '2026-10-04T16:00:00', N'Hoàn thành'),
('PSC05102026001', 'DV001', 1, 250000, 8, N'Thay dầu động cơ', '2026-10-05T16:00:00', N'Hoàn thành'),
('PSC05102026001', 'DV007', 1, 120000, 8, N'Kiểm tra điều hòa', '2026-10-05T16:30:00', N'Hoàn thành'),
('PSC06102026001', 'DV002', 1, 150000, 8, N'Kiểm tra hệ thống phanh', '2026-10-06T16:00:00', N'Hoàn thành'),
('PSC06102026001', 'DV005', 1, 350000, 8, N'Cân chỉnh góc đặt bánh xe', '2026-10-06T16:30:00', N'Hoàn thành'),
('PSC07102026001', 'DV003', 1, 200000, 8, N'Vệ sinh xe', '2026-10-07T16:00:00', N'Hoàn thành'),
('PSC07102026001', 'DV008', 1, 500000, 8, N'Đánh bóng thân xe', '2026-10-07T16:30:00', N'Hoàn thành'),
('PSC08102026001', 'DV006', 1, 300000, 8, N'Bảo dưỡng hệ thống làm mát', '2026-10-08T16:00:00', N'Hoàn thành'),
('PSC09102026001', 'DV001', 1, 250000, 8, N'Thay dầu động cơ', '2026-10-09T16:00:00', N'Hoàn thành'),
('PSC10102026001', 'DV006', 1, 300000, 8, N'Bảo dưỡng hệ thống làm mát', '2026-10-10T16:00:00', N'Hoàn thành'),
('PSC11102026001', 'DV007', 1, 120000, 8, N'Kiểm tra điều hòa và đường gió', NULL, N'Chưa thực hiện');
GO

-- DU LIEU MAU MOI: ChiTietPhuTung (them soLuongDaDung va soLuongCapPhat)
INSERT INTO ChiTietPhuTung (maPSC, maPhuTung, soLuongDuKien, donGia, thueSuatVAT, soLuongDaDung, soLuongCapPhat)
VALUES
('PSC01102026001', 'LK001', 1, 180000, 8, 1, 1),
('PSC01102026001', 'PT001', 1, 120000, 8, 1, 1),
('PSC02102026001', 'GT001', 1, 1500000, 8, 1, 1),
('PSC03102026001', 'DI001', 1, 1800000, 8, 1, 1),
('PSC04102026001', 'NT001', 1, 750000, 8, 1, 1),
('PSC05102026001', 'LK001', 1, 240000, 8, 1, 1),
('PSC05102026001', 'PH002', 1, 160000, 8, 1, 1),
('PSC06102026001', 'PH001', 1, 850000, 8, 1, 1),
('PSC06102026001', 'PH002', 1, 160000, 8, 1, 1),
('PSC08102026001', 'LM001', 1, 2500000, 8, 1, 1),
('PSC09102026001', 'LK001', 1, 180000, 8, 1, 1),
('PSC10102026001', 'PH001', 1, 850000, 8, 1, 1),
('PSC11102026001', 'LK002', 1, 220000, 8, 0, 0);
GO

-- DU LIEU MAU MOI: PhieuNhapKho (tong tien nhap kho la thuoc tinh dan xuat)
INSERT INTO PhieuNhapKho (maPNK, ngayNhap, maNVLap, maNCC, ghiChu)
VALUES
('PNK20092026001', '2026-09-20T08:00:00', 'NV2024001', 'NCC001', N'Nhập phụ tùng đợt 1.'),
('PNK22092026001', '2026-09-22T09:30:00', 'NV2024001', 'NCC002', N'Nhập phụ tùng phục vụ bảo dưỡng.'),
('PNK25092026001', '2026-09-25T10:00:00', 'NV2024001', 'NCC003', N'Nhập vật tư thay thế.'),
('PNK28092026001', '2026-09-28T14:00:00', 'NV2024001', 'NCC004', N'Nhập phụ tùng hệ thống điện và lọc.')
GO

-- DU LIEU MAU MOI: LoPhuTung (chi tiet nhap kho duoc quan ly theo lo)
INSERT INTO LoPhuTung (maLo, ngaySanXuat, hanSuDung, donGiaNhap, soLuongNhap, soLuongTon, maPhuTung, maPNK)
VALUES
('LOPNK20092026001_001', '2026-01-15', '2030-01-15', 100000, 30, 30, 'PT001', 'PNK20092026001'),
('LOPNK20092026001_002', '2026-01-20', '2031-01-20', 1500000, 8, 8, 'GT001', 'PNK20092026001'),
('LOPNK20092026001_003', '2026-03-05', '2028-03-05', 1800000, 4, 4, 'DI001', 'PNK20092026001'),
('LOPNK22092026001_001', '2026-02-10', '2029-02-10', 850000, 10, 10, 'PH001', 'PNK22092026001'),
('LOPNK22092026001_002', '2026-02-18', '2027-08-18', 2500000, 2, 2, 'LM001', 'PNK22092026001'),
('LOPNK22092026001_003', '2026-03-12', '2030-09-12', 180000, 10, 10, 'LK001', 'PNK22092026001'),
('LOPNK25092026001_001', '2026-03-01', NULL, 450000, 10, 10, 'PT002', 'PNK25092026001'),
('LOPNK25092026001_002', '2026-03-08', NULL, 160000, 25, 25, 'PH002', 'PNK25092026001'),
('LOPNK25092026001_003', '2026-01-25', NULL, 750000, 2, 2, 'NT001', 'PNK25092026001'),
('LOPNK28092026001_001', '2026-02-15', NULL, 3200000, 1, 1, 'DI002', 'PNK28092026001'),
('LOPNK28092026001_002', '2026-03-10', NULL, 220000, 10, 10, 'LK002', 'PNK28092026001'),
('LOPNK28092026001_003', '2026-02-28', NULL, 1220000, 1, 1, 'LP001', 'PNK28092026001')
GO

-- DU LIEU MAU MOI: PhieuYeuCauXuatKho va ChiTietYeuCauXuatKho
INSERT INTO PhieuYeuCauXuatKho (maYCXK, ngayYeuCau, ngayDuyet, trangThai, ghiChu, maNVYeuCau, maNVDuyet, maPSC)
VALUES ('YCXK11102026001', '2026-10-11T09:30:00', NULL, N'Chờ duyệt', N'Yêu cầu cấp lọc gió động cơ cho phiếu sửa chữa.', 'NV2024005', NULL, 'PSC11102026001');
GO
INSERT INTO ChiTietYeuCauXuatKho (maYCXK, maLo, soLuongYeuCau)
VALUES ('YCXK11102026001', 'LOPNK28092026001_001', 1);
GO

-- DU LIEU MAU MOI: HoaDon (cac tong tien la thuoc tinh dan xuat)
INSERT INTO HoaDon (maHoaDon, ngayLap, hinhThucThanhToan, trangThai, maPSC, maNVThuNgan, maKH, maChuongTrinh)
VALUES
('HD01102026001', '2026-10-01T10:30:00', N'Tiền mặt', N'Đã thanh toán', 'PSC01102026001', 'NV2024004', 'KH001', 'KM001'),
('HD02102026001', '2026-10-02T12:00:00', N'Chuyển khoản', N'Đã thanh toán', 'PSC02102026001', 'NV2024004', 'KH002', 'KM003'),
('HD03102026001', '2026-10-03T13:00:00', N'Tiền mặt', N'Đã thanh toán', 'PSC03102026001', 'NV2024004', 'KH003', NULL),
('HD04102026001', '2026-10-04T15:30:00', N'Chuyển khoản', N'Đã thanh toán', 'PSC04102026001', 'NV2024004', 'KH004', 'KM001'),
('HD05102026001', '2026-10-05T11:00:00', N'Tiền mặt', N'Đã thanh toán', 'PSC05102026001', 'NV2024004', 'KH005', 'KM003'),
('HD06102026001', '2026-10-06T16:00:00', N'Chuyển khoản', N'Đã thanh toán', 'PSC06102026001', 'NV2024004', 'KH006', NULL),
('HD07102026001', '2026-10-07T11:30:00', N'Tiền mặt', N'Đã thanh toán', 'PSC07102026001', 'NV2024004', 'KH007', 'KM001'),
('HD08102026001', '2026-10-08T13:00:00', N'Chuyển khoản', N'Đã thanh toán', 'PSC08102026001', 'NV2024004', 'KH008', NULL),
('HD09102026001', '2026-10-09T11:00:00', NULL, N'Chưa thanh toán', 'PSC09102026001', 'NV2024004', 'KH001', NULL),
('HD10102026001', '2026-10-10T11:30:00', NULL, N'Chưa thanh toán', 'PSC10102026001', 'NV2024004', 'KH002', NULL);
GO

/*==================== PHAN 3. KIEM TRA DU LIEU ====================*/
SELECT * FROM VaiTro;
SELECT * FROM NhanVien;
SELECT * FROM TaiKhoan;
SELECT * FROM KhachHang;
SELECT * FROM NhaCungCap;
SELECT * FROM HangMucKiemTra;
SELECT * FROM DichVu;
SELECT * FROM ChuongTrinhKhuyenMai;
SELECT * FROM Xe;
SELECT * FROM PhieuTiepNhan;
SELECT * FROM ChiTietHangMuc;
SELECT * FROM PhieuSuaChua;
SELECT * FROM DanhMucPhuTung;
SELECT * FROM PhuTung;
SELECT * FROM ChiTietPhuTung;
SELECT * FROM ChiTietDichVu;
SELECT * FROM PhieuNhapKho;
SELECT * FROM LoPhuTung;
SELECT * FROM PhieuYeuCauXuatKho;
SELECT * FROM ChiTietYeuCauXuatKho;
SELECT * FROM HoaDon;
GO

-- Tong ton kho duoc tinh tu cac lo, khong luu truc tiep trong PhuTung.
SELECT pt.maPhuTung, pt.tenPhuTung, ISNULL(SUM(lo.soLuongTon), 0) AS soLuongTon
FROM PhuTung pt LEFT JOIN LoPhuTung lo ON lo.maPhuTung = pt.maPhuTung
GROUP BY pt.maPhuTung, pt.tenPhuTung;
GO

