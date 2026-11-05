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
		this.maChuongTrinh = maChuongTrinh;
		this.tenChuongTrinh = tenChuongTrinh;
		this.ngayBatDau = ngayBatDau;
		this.ngayKetThuc = ngayKetThuc;
		this.loaiKhuyenMai = loaiKhuyenMai;
		this.giaTriKhuyenMai = giaTriKhuyenMai;
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
	public void setGiaTriKhuyenMai(double giaTriKhuyenMai) { this.giaTriKhuyenMai = giaTriKhuyenMai; }

	@Override
	public String toString() {
		return String.format("%s %s %s %s %s %s", maChuongTrinh, tenChuongTrinh, ngayBatDau, ngayKetThuc,
				loaiKhuyenMai, giaTriKhuyenMai);
	}
}
