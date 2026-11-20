package entity;

public class ChiTietYeuCau {
	private PhieuYeuCauXuatKho phieuYeuCauXuatKho;
	private LoPhuTung loPhuTung;
	private int soLuongYeuCau;
	
	public ChiTietYeuCau() {
	
	}
	
	public ChiTietYeuCau(PhieuYeuCauXuatKho phieuYeuCauXuatKho, LoPhuTung loPhuTung) {
		this.phieuYeuCauXuatKho = phieuYeuCauXuatKho;
		this.loPhuTung = loPhuTung;
	}

	public ChiTietYeuCau(PhieuYeuCauXuatKho phieuYeuCauXuatKho, LoPhuTung loPhuTung, int soLuongYeuCau) {
		this.phieuYeuCauXuatKho = phieuYeuCauXuatKho;
		this.loPhuTung = loPhuTung;
		this.soLuongYeuCau = soLuongYeuCau;
	}

	public PhieuYeuCauXuatKho getPhieuYeuCauXuatKho() {
		return phieuYeuCauXuatKho;
	}

	public void setPhieuYeuCauXuatKho(PhieuYeuCauXuatKho phieuYeuCauXuatKho) {
		this.phieuYeuCauXuatKho = phieuYeuCauXuatKho;
	}

	public LoPhuTung getLoPhuTung() {
		return loPhuTung;
	}

	public void setLoPhuTung(LoPhuTung loPhuTung) {
		this.loPhuTung = loPhuTung;
	}

	public int getSoLuongYeuCau() {
		return soLuongYeuCau;
	}

	public void setSoLuongYeuCau(int soLuongYeuCau) {
		this.soLuongYeuCau = soLuongYeuCau;
	}
	
	
}
