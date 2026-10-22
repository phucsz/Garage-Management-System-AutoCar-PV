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

insert into VaiTro values 
('QUANLY',N'Quản lý cửa hàng',N'Là người có quyền hạn cao nhất trong hệ thống'),
('TIEPNHANVIEN',N'Tiếp nhận viên',N'Làm việc trực tiếp với khách hàng'),
('KYTHUATVIEN',N'Kỹ thuật viên',N'Thực hiện công việc kỹ thuật'),
('THUNGAN',N'Thu ngân',N'Phụ trách thanh toán hóa đơn')

insert into NhanVien(maNV,hoTenNV,noiSinh,ngaySinh,gioiTinh,sdt,trangThai) values 
('NV15092006001',N'Phạm Phương Hoàng Phúc',N'TP.Hồ Chí Minh','2006-09-15',N'Nam','0949101885',N'Đang làm việc')

insert into TaiKhoan values
('NV15092006001',ENCRYPTBYPASSPHRASE('123','15092006'),'QUANLY')

select * from VaiTro
select * from NhanVien
select * from TaiKhoan


select tenDangNhap, convert(VARCHAR(100),DECRYPTBYPASSPHRASE('123',matKhau)) as matKhau,maVaiTro from TaiKhoan
where tenDangNhap = 'NV15092006001' and convert(VARCHAR(100),DECRYPTBYPASSPHRASE('123',matKhau)) = '15092006'

Select * from TaiKhoan where tenDangNhap = 'NV15092006001'
