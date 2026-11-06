create database QuanLyGaraOto
go
use QuanLyGaraOto
go
CREATE TABLE VaiTro (
    maVaiTro VARCHAR(50) PRIMARY KEY,
    tenVaiTro NVARCHAR(100),
    moTa NVARCHAR(500)
);

CREATE TABLE NhanVien (
    maNV VARCHAR(50) PRIMARY KEY,
    hoTenNV NVARCHAR(100),
    noiSinh NVARCHAR(100),
    ngaySinh DATETIME,
	gioiTinh NVARCHAR(5) CHECK (gioiTinh IN (N'Nam', N'Nữ', 'Khác')),
    CCCD VARCHAR(20),
    sdt VARCHAR(15),
    diaChi NVARCHAR(255),
    email VARCHAR(100),
    trangThai NVARCHAR(50)
);

CREATE TABLE TaiKhoan (
    tenDangNhap VARCHAR(50) PRIMARY KEY,
    matKhau VARBINARY(MAX),
    maVaiTro VARCHAR(50),
    FOREIGN KEY (maVaiTro) REFERENCES VaiTro(maVaiTro),
    FOREIGN KEY (tenDangNhap) REFERENCES NhanVien(maNV)
);

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
    loaiHangMuc NVARCHAR(100) NULL,
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

/* =========================================================
   2. XE VÀ PHIẾU TIẾP NHẬN
   ========================================================= */

CREATE TABLE Xe (
    bienSoXe VARCHAR(20) PRIMARY KEY,
    loaiXe NVARCHAR(100) NULL,
    congSuat INT NULL CHECK (congSuat IS NULL OR congSuat > 0),
    hangXe NVARCHAR(100) NULL,
    mauXe NVARCHAR(50) NULL,
    soThangDaDi INT NULL CHECK (soThangDaDi IS NULL OR soThangDaDi >= 0),
    soKmHienTai FLOAT NOT NULL DEFAULT 0 CHECK (soKmHienTai >= 0),
    maKH VARCHAR(50) NOT NULL,
    CONSTRAINT FK_Xe_KhachHang FOREIGN KEY (maKH) REFERENCES KhachHang(maKH)
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
        REFERENCES Xe(bienSoXe),CONSTRAINT FK_PTN_KhachHang FOREIGN KEY (maKH)
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
/* =========================================================
   3. PHIẾU SỬA CHỮA VÀ CÁC CHI TIẾT PHỤ TÙNG / DỊCH VỤ
   ========================================================= */

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

CREATE TABLE PhuTung (
    maPhuTung VARCHAR(50) PRIMARY KEY,
    tenPhuTung NVARCHAR(150) NOT NULL,
    maDanhMuc VARCHAR(10),
    donViTinh NVARCHAR(30) NULL,
    donGiaHienTai FLOAT NOT NULL CHECK (donGiaHienTai >= 0),
    soLuongTon INT NOT NULL DEFAULT 0 CHECK (soLuongTon >= 0),
    ngaySanXuat DATE NULL,
    maNCC VARCHAR(50) NOT NULL,
    CONSTRAINT FK_PhuTung_DanhMucPhuTung FOREIGN KEY (maDanhMuc) REFERENCES DanhMucPhuTung(maDanhMuc),
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

/* =========================================================
   4. HÓA ĐƠN VÀ KHUYẾN MÃI
   ========================================================= */

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

/* =========================================================
   5. NHẬP KHO
   ========================================================= */

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

---Dữ liệu

/*--
select * from VaiTro
select * from NhanVien
select * from TaiKhoan

SELECT * FROM KhachHang;
SELECT * FROM Xe;

SELECT * FROM HangMucKiemTra
SELECT * FROM ChiTietHangMuc;
SELECT * FROM PhieuTiepNhan;
SELECT * FROM PhieuSuaChua;

SELECT * FROM ChiTietPhuTung;
SELECT * FROM ChiTietDichVu;
SELECT * FROM HoaDon;

SELECT * FROM ChuongTrinhKhuyenMai;

SELECT * FROM PhuTung;
SELECT * FROM DichVu;
SELECT * FROM NhaCungCap;

SELECT * FROM PhieuNhapKho;
SELECT * FROM ChiTietPhieuNhapKho;


select tenDangNhap, convert(VARCHAR(100),DECRYPTBYPASSPHRASE('123',matKhau)) as matKhau,maVaiTro from TaiKhoan
where tenDangNhap = 'NV2024001' and convert(VARCHAR(100),DECRYPTBYPASSPHRASE('123',matKhau)) = '15092006'


--*/