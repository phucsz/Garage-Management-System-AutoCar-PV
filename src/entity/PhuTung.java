package entity;

import java.util.ArrayList;

public class PhuTung {
	private String maPhuTung;
	private String tenPhuTung;
	private String donViTinh;
	private double donGiaHienTai;
	private double thueSuatVAT;
	private DanhMucPhuTung danhMucPhuTung;
	private ArrayList<LoPhuTung> loPhuTung;

	public PhuTung() {
	}

	public PhuTung(String maPhuTung) {
		this.maPhuTung = maPhuTung;
	}

	

	public PhuTung(String maPhuTung, String tenPhuTung, String donViTinh, double donGiaHienTai, double thueSuatVAT,
			DanhMucPhuTung danhMucPhuTung, ArrayList<LoPhuTung> loPhuTung) {
		this.maPhuTung = maPhuTung;
		this.tenPhuTung = tenPhuTung;
		this.donViTinh = donViTinh;
		this.donGiaHienTai = donGiaHienTai;
		this.thueSuatVAT = thueSuatVAT;
		this.danhMucPhuTung = danhMucPhuTung;
		this.loPhuTung = loPhuTung;
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

	public double getThueSuatVAT() {
		return thueSuatVAT;
	}

	public void setThueSuatVAT(double thueSuatVAT) {
		this.thueSuatVAT = thueSuatVAT;
	}

	public ArrayList<LoPhuTung> getLoPhuTung() {
		return loPhuTung;
	}

	public void setLoPhuTung(ArrayList<LoPhuTung> loPhuTung) {
		this.loPhuTung = loPhuTung;
	}
	
}
