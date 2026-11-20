package entity;

import java.time.LocalDate;

public class ChuongTrinhKhuyenMai {
	private String maChuongTrinh;
	private String tenChuongTrinh;
	private LocalDate ngayBatDau;
	private LocalDate ngayKetThuc;
	private String loaiKhuyenMai;
	private double giaTriKhuyenMai;

	public ChuongTrinhKhuyenMai() {
	}

	public ChuongTrinhKhuyenMai(String maChuongTrinh) {
		this.maChuongTrinh = maChuongTrinh;
	}

	public ChuongTrinhKhuyenMai(String maChuongTrinh, String tenChuongTrinh, LocalDate ngayBatDau,
			LocalDate ngayKetThuc, String loaiKhuyenMai, double giaTriKhuyenMai) {
		setMaChuongTrinh(maChuongTrinh);
		setTenChuongTrinh(tenChuongTrinh);
		setNgayBatDau(ngayBatDau);
		setNgayKetThuc(ngayKetThuc);
		setLoaiKhuyenMai(loaiKhuyenMai);
		setGiaTriKhuyenMai(giaTriKhuyenMai);
	}

	public String getMaChuongTrinh() { return maChuongTrinh; }
	public void setMaChuongTrinh(String maChuongTrinh) { this.maChuongTrinh = maChuongTrinh; }
	public String getTenChuongTrinh() { return tenChuongTrinh; }
	public void setTenChuongTrinh(String tenChuongTrinh) { this.tenChuongTrinh = tenChuongTrinh; }
	public LocalDate getNgayBatDau() { return ngayBatDau; }
	public void setNgayBatDau(LocalDate ngayBatDau) { this.ngayBatDau = ngayBatDau; }
	public LocalDate getNgayKetThuc() { return ngayKetThuc; }
	public void setNgayKetThuc(LocalDate ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }
	public String getLoaiKhuyenMai() { return loaiKhuyenMai; }
	public void setLoaiKhuyenMai(String loaiKhuyenMai) { this.loaiKhuyenMai = loaiKhuyenMai; }
	public double getGiaTriKhuyenMai() { return giaTriKhuyenMai; }
	
	public void setGiaTriKhuyenMai(double giaTriKhuyenMai) {
	    if (giaTriKhuyenMai < 0) {
	        throw new IllegalArgumentException("Lỗi: Giá trị khuyến mãi không được nhỏ hơn 0");
	    }
	    if (this.getLoaiKhuyenMai().equals("Phần trăm")) {
	        if (giaTriKhuyenMai > 100) {
	            throw new IllegalArgumentException("Lỗi: Khuyến mãi theo phần trăm không được vượt quá 100%");
	        }
	    }
	    this.giaTriKhuyenMai = giaTriKhuyenMai;
	}


	public String KiemTraTrangThai() {
		LocalDate ngayHienTai = LocalDate.now();
		if (ngayHienTai.isAfter(ngayBatDau) && ngayHienTai.isBefore(ngayKetThuc)) return "Đang diễn ra";
		else if (ngayHienTai.isBefore(ngayBatDau)) return "Chưa bắt đầu";
		return "Đã kết thúc";
	}
	@Override
	public String toString() {
		return String.format("%s %s %s %s %s %s", maChuongTrinh, tenChuongTrinh, ngayBatDau, ngayKetThuc,
				loaiKhuyenMai, giaTriKhuyenMai);
	}
}
