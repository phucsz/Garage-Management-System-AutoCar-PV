package entity;

import java.time.LocalDateTime;

public class HoaDon {
	private String maHoaDon;
	private LocalDateTime ngayLap;
	private String hinhThucThanhToan;
	private String trangThai;
	private PhieuSuaChua phieuSuaChua;
	private NhanVien nhanVienThuNgan;
	private KhachHang khachHang;
	private ChuongTrinhKhuyenMai chuongTrinhKhuyenMai;

	public HoaDon() {
	}

	public HoaDon(String maHoaDon) {
		this.maHoaDon = maHoaDon;
	}

	public HoaDon(String maHoaDon, LocalDateTime ngayLap, String hinhThucThanhToan, String trangThai,
			PhieuSuaChua phieuSuaChua, NhanVien nhanVienThuNgan, KhachHang khachHang,
			ChuongTrinhKhuyenMai chuongTrinhKhuyenMai) {
		this.maHoaDon = maHoaDon;
		this.ngayLap = ngayLap;
		this.hinhThucThanhToan = hinhThucThanhToan;
		this.trangThai = trangThai;
		this.phieuSuaChua = phieuSuaChua;
		this.nhanVienThuNgan = nhanVienThuNgan;
		this.khachHang = khachHang;
		this.chuongTrinhKhuyenMai = chuongTrinhKhuyenMai;
	}

	public String getMaHoaDon() { 
		return maHoaDon; 
	}
	public void setMaHoaDon(String maHoaDon) { 
		this.maHoaDon = maHoaDon; 
	}
	public LocalDateTime getNgayLap() { 
		return ngayLap; 
	}
	public void setNgayLap(LocalDateTime ngayLap) { 
		this.ngayLap = ngayLap; 
	}
	public String getHinhThucThanhToan() { 
		return hinhThucThanhToan; 
	}
	public void setHinhThucThanhToan(String hinhThucThanhToan) { 
		this.hinhThucThanhToan = hinhThucThanhToan; 
	}
	public String getTrangThai() { 
		return trangThai; 
	}
	public void setTrangThai(String trangThai) { 
		this.trangThai = trangThai; 
	}
	public PhieuSuaChua getPhieuSuaChua() { 
		return phieuSuaChua; 
	}
	public void setPhieuSuaChua(PhieuSuaChua phieuSuaChua) { 
		this.phieuSuaChua = phieuSuaChua; 
	}
	public NhanVien getNhanVienThuNgan() { 
		return nhanVienThuNgan; 
	}
	public void setNhanVienThuNgan(NhanVien nhanVienThuNgan) { 
		this.nhanVienThuNgan = nhanVienThuNgan; 
	}
	public KhachHang getKhachHang() { 
		return khachHang; 
	}
	public void setKhachHang(KhachHang khachHang) { 
		this.khachHang = khachHang; 
	}
	public ChuongTrinhKhuyenMai getChuongTrinhKhuyenMai() { 
		return chuongTrinhKhuyenMai; 
	}
	public void setChuongTrinhKhuyenMai(ChuongTrinhKhuyenMai chuongTrinhKhuyenMai) {
		this.chuongTrinhKhuyenMai = chuongTrinhKhuyenMai;
	}
	

	public double getTienGiamGia() {
		return chuongTrinhKhuyenMai == null ? 0 : chuongTrinhKhuyenMai.getGiaTriKhuyenMai();
	}

	public double getSoTienGiamGia() {
		if (chuongTrinhKhuyenMai == null) {
			return 0;
		}
		double tongTienTruocGiam = getTongTienTruocGiam();
		if ("Phần trăm".equalsIgnoreCase(chuongTrinhKhuyenMai.getLoaiKhuyenMai())) {
			return tongTienTruocGiam * Math.max(0, getTienGiamGia()) / 100;
		}
		return Math.min(tongTienTruocGiam, Math.max(0, getTienGiamGia()));
	}

	public double getTongTienThanhToan() {
		return Math.max(0, getTongTienTruocGiam() - getSoTienGiamGia());
	}

	private double getTongTienTruocGiam() {
		if (phieuSuaChua == null) {
			return 0;
		}
		return Math.max(0, phieuSuaChua.getTongTienDichVu())
				+ Math.max(0, phieuSuaChua.getTongTienPhuTung());
	}

}
