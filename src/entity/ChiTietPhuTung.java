package entity;

public class ChiTietPhuTung {
	private PhieuSuaChua phieuSuaChua;
	private PhuTung phuTung;
	private int soLuong;
	private double donGia;
	private double thueSuatVAT;
	private int soLuongDaDung;
	private int soLuongCapPhat;

	public ChiTietPhuTung() {
	}

	public ChiTietPhuTung(PhieuSuaChua phieuSuaChua, PhuTung phuTung) {
		this.phieuSuaChua = phieuSuaChua;
		this.phuTung = phuTung;
	}

	

	public ChiTietPhuTung(PhieuSuaChua phieuSuaChua, PhuTung phuTung, int soLuong, double donGia, double thueSuatVAT,
			int soLuongDaDung, int soLuongCapPhat) {
		this.phieuSuaChua = phieuSuaChua;
		this.phuTung = phuTung;
		this.soLuong = soLuong;
		this.donGia = donGia;
		this.thueSuatVAT = thueSuatVAT;
		this.soLuongDaDung = soLuongDaDung;
		this.soLuongCapPhat = soLuongCapPhat;
	}

	public PhieuSuaChua getPhieuSuaChua() { return phieuSuaChua; }
	public void setPhieuSuaChua(PhieuSuaChua phieuSuaChua) { this.phieuSuaChua = phieuSuaChua; }
	public PhuTung getPhuTung() { return phuTung; }
	public void setPhuTung(PhuTung phuTung) { this.phuTung = phuTung; }
	public int getSoLuong() { return soLuong; }
	public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
	public double getDonGia() { return donGia; }
	public void setDonGia(double donGia) { this.donGia = donGia; }
	

	public double getThueSuatVAT() {
		return thueSuatVAT;
	}

	public void setThueSuatVAT(double thueSuatVAT) {
		this.thueSuatVAT = thueSuatVAT;
	}

	public int getSoLuongDaDung() {
		return soLuongDaDung;
	}

	public void setSoLuongDaDung(int soLuongDaDung) {
		this.soLuongDaDung = soLuongDaDung;
	}

	public int getSoLuongCapPhat() {
		return soLuongCapPhat;
	}

	public void setSoLuongCapPhat(int soLuongCapPhat) {
		this.soLuongCapPhat = soLuongCapPhat;
	}
}
