package entity;

import java.time.LocalDate;

public class PhuTung {
	private String maPhuTung;
	private String tenPhuTung;
	private DanhMucPhuTung danhMucPhuTung;
	private String donViTinh;
	private double donGiaHienTai;
	private int soLuongTon;
	private LocalDate ngaySanXuat;
	private NhaCungCap nhaCungCap;

	public PhuTung() {
	}

	public PhuTung(String maPhuTung) {
		this.maPhuTung = maPhuTung;
	}

	public PhuTung(String maPhuTung, String tenPhuTung, DanhMucPhuTung danhMucPhuTung, String donViTinh,
			double donGiaHienTai, int soLuongTon, LocalDate ngaySanXuat, NhaCungCap nhaCungCap) {
		this.maPhuTung = maPhuTung;
		this.tenPhuTung = tenPhuTung;
		this.danhMucPhuTung = danhMucPhuTung;
		this.donViTinh = donViTinh;
		this.donGiaHienTai = donGiaHienTai;
		this.soLuongTon = soLuongTon;
		this.ngaySanXuat = ngaySanXuat;
		this.nhaCungCap = nhaCungCap;
	}

	public String getMaPhuTung() { return maPhuTung; }
	public void setMaPhuTung(String maPhuTung) { this.maPhuTung = maPhuTung; }
	public String getTenPhuTung() { return tenPhuTung; }
	public void setTenPhuTung(String tenPhuTung) { this.tenPhuTung = tenPhuTung; }
	public DanhMucPhuTung getDanhMucPhuTung() { return danhMucPhuTung; }
	public void setDanhMucPhuTung(DanhMucPhuTung danhMucPhuTung) { this.danhMucPhuTung = danhMucPhuTung; }
	public String getDonViTinh() { return donViTinh; }
	public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }
	public double getDonGiaHienTai() { return donGiaHienTai; }
	public void setDonGiaHienTai(double donGiaHienTai) { this.donGiaHienTai = donGiaHienTai; }
	public int getSoLuongTon() { return soLuongTon; }
	public void setSoLuongTon(int soLuongTon) { this.soLuongTon = soLuongTon; }
	public LocalDate getNgaySanXuat() { return ngaySanXuat; }
	public void setNgaySanXuat(LocalDate ngaySanXuat) { this.ngaySanXuat = ngaySanXuat; }
	public NhaCungCap getNhaCungCap() { return nhaCungCap; }
	public void setNhaCungCap(NhaCungCap nhaCungCap) { this.nhaCungCap = nhaCungCap; }

	@Override
	public String toString() {
		return String.format("%s %s %s %s %s %s %s %s", maPhuTung, tenPhuTung,
				danhMucPhuTung == null ? null : danhMucPhuTung.getMaDanhMuc(), donViTinh, donGiaHienTai,
				soLuongTon, ngaySanXuat, nhaCungCap == null ? null : nhaCungCap.getMaNCC());
	}
}
