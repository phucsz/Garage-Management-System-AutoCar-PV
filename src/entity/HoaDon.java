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
	private double tienGiamGia;
	private double soTienGiamGia;
	private double tongTienThanhToan;

	public HoaDon() {
	}

	public HoaDon(String maHoaDon) {
		this.maHoaDon = maHoaDon;
	}

	public HoaDon(String maHoaDon, LocalDateTime ngayLap, String hinhThucThanhToan, String trangThai,
			PhieuSuaChua phieuSuaChua, NhanVien nhanVienThuNgan, KhachHang khachHang,
			ChuongTrinhKhuyenMai chuongTrinhKhuyenMai, double tienGiamGia, double soTienGiamGia,
			double tongTienThanhToan) {
		this.maHoaDon = maHoaDon;
		this.ngayLap = ngayLap;
		this.hinhThucThanhToan = hinhThucThanhToan;
		this.trangThai = trangThai;
		this.phieuSuaChua = phieuSuaChua;
		this.nhanVienThuNgan = nhanVienThuNgan;
		this.khachHang = khachHang;
		this.chuongTrinhKhuyenMai = chuongTrinhKhuyenMai;
		this.tienGiamGia = tienGiamGia;
		this.soTienGiamGia = soTienGiamGia;
		this.tongTienThanhToan = tongTienThanhToan;
	}

	public String getMaHoaDon() { return maHoaDon; }
	public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }
	public LocalDateTime getNgayLap() { return ngayLap; }
	public void setNgayLap(LocalDateTime ngayLap) { this.ngayLap = ngayLap; }
	public String getHinhThucThanhToan() { return hinhThucThanhToan; }
	public void setHinhThucThanhToan(String hinhThucThanhToan) { this.hinhThucThanhToan = hinhThucThanhToan; }
	public String getTrangThai() { return trangThai; }
	public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
	public PhieuSuaChua getPhieuSuaChua() { return phieuSuaChua; }
	public void setPhieuSuaChua(PhieuSuaChua phieuSuaChua) { this.phieuSuaChua = phieuSuaChua; }
	public NhanVien getNhanVienThuNgan() { return nhanVienThuNgan; }
	public void setNhanVienThuNgan(NhanVien nhanVienThuNgan) { this.nhanVienThuNgan = nhanVienThuNgan; }
	public KhachHang getKhachHang() { return khachHang; }
	public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }
	public ChuongTrinhKhuyenMai getChuongTrinhKhuyenMai() { return chuongTrinhKhuyenMai; }
	public void setChuongTrinhKhuyenMai(ChuongTrinhKhuyenMai chuongTrinhKhuyenMai) {
		this.chuongTrinhKhuyenMai = chuongTrinhKhuyenMai;
	}
	public double getTienGiamGia() { return tienGiamGia; }
	public void setTienGiamGia(double tienGiamGia) { this.tienGiamGia = tienGiamGia; }
	public double getSoTienGiamGia() { return soTienGiamGia; }
	public void setSoTienGiamGia(double soTienGiamGia) { this.soTienGiamGia = soTienGiamGia; }
	public double getTongTienThanhToan() { return tongTienThanhToan; }
	public void setTongTienThanhToan(double tongTienThanhToan) { this.tongTienThanhToan = tongTienThanhToan; }

	@Override
	public String toString() {
		return String.format("%s %s %s %s %s %s %s %s %s %s %s",
				maHoaDon, ngayLap, hinhThucThanhToan, trangThai,
				phieuSuaChua == null ? null : phieuSuaChua.getMaPSC(),
				nhanVienThuNgan == null ? null : nhanVienThuNgan.getMaNV(),
				khachHang == null ? null : khachHang.getMaKH(),
				chuongTrinhKhuyenMai == null ? null : chuongTrinhKhuyenMai.getMaChuongTrinh(),
				tienGiamGia, soTienGiamGia, tongTienThanhToan);
	}
}
