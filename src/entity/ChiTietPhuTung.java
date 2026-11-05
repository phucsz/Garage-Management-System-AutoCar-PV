package entity;

public class ChiTietPhuTung {
	private PhieuSuaChua phieuSuaChua;
	private PhuTung phuTung;
	private int soLuong;
	private double donGia;
	private String ghiChu;

	public ChiTietPhuTung() {
	}

	public ChiTietPhuTung(PhieuSuaChua phieuSuaChua, PhuTung phuTung) {
		this.phieuSuaChua = phieuSuaChua;
		this.phuTung = phuTung;
	}

	public ChiTietPhuTung(PhieuSuaChua phieuSuaChua, PhuTung phuTung, int soLuong, double donGia, String ghiChu) {
		this.phieuSuaChua = phieuSuaChua;
		this.phuTung = phuTung;
		this.soLuong = soLuong;
		this.donGia = donGia;
		this.ghiChu = ghiChu;
	}

	public PhieuSuaChua getPhieuSuaChua() { return phieuSuaChua; }
	public void setPhieuSuaChua(PhieuSuaChua phieuSuaChua) { this.phieuSuaChua = phieuSuaChua; }
	public PhuTung getPhuTung() { return phuTung; }
	public void setPhuTung(PhuTung phuTung) { this.phuTung = phuTung; }
	public int getSoLuong() { return soLuong; }
	public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
	public double getDonGia() { return donGia; }
	public void setDonGia(double donGia) { this.donGia = donGia; }
	public String getGhiChu() { return ghiChu; }
	public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

	@Override
	public String toString() {
		return String.format("%s %s %s %s %s",
				phieuSuaChua == null ? null : phieuSuaChua.getMaPSC(),
				phuTung == null ? null : phuTung.getMaPhuTung(), soLuong, donGia, ghiChu);
	}
}
