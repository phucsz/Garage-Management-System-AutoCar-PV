package entity;

import java.time.LocalDate;

public class LoPhuTung {
	private String maLo;
	private LocalDate ngaySanXuat;
	private LocalDate hanSuDung;
	private double donGiaNhap;
	private int soLuongNhap;
	private int soLuongTon;
	private PhuTung phuTung;
	private PhieuNhapKho phieuNhapKho;
	
	public LoPhuTung() {
	}
	
	public LoPhuTung(String maLo) {
		this.maLo = maLo;
	}

	public LoPhuTung(String maLo, LocalDate ngaySanXuat, LocalDate hanSuDung, double donGiaNhap, int soLuongNhap,
			int soLuongTon, PhuTung phuTung, PhieuNhapKho phieuNhapKho) {
		this.maLo = maLo;
		this.ngaySanXuat = ngaySanXuat;
		this.hanSuDung = hanSuDung;
		this.donGiaNhap = donGiaNhap;
		this.soLuongNhap = soLuongNhap;
		this.soLuongTon = soLuongTon;
		this.phuTung = phuTung;
		this.phieuNhapKho = phieuNhapKho;
	}

	public String getMaLo() {
		return maLo;
	}

	public void setMaLo(String maLo) {
		this.maLo = maLo;
	}

	public LocalDate getNgaySanXuat() {
		return ngaySanXuat;
	}

	public void setNgaySanXuat(LocalDate ngaySanXuat) {
		this.ngaySanXuat = ngaySanXuat;
	}

	public LocalDate getHanSuDung() {
		return hanSuDung;
	}

	public void setHanSuDung(LocalDate hanSuDung) {
		this.hanSuDung = hanSuDung;
	}

	public double getDonGiaNhap() {
		return donGiaNhap;
	}

	public void setDonGiaNhap(double donGiaNhap) {
		this.donGiaNhap = donGiaNhap;
	}

	public int getSoLuongNhap() {
		return soLuongNhap;
	}

	public void setSoLuongNhap(int soLuongNhap) {
		this.soLuongNhap = soLuongNhap;
	}

	public int getSoLuongTon() {
		return soLuongTon;
	}

	public void setSoLuongTon(int soLuongTon) {
		this.soLuongTon = soLuongTon;
	}

	public PhuTung getPhuTung() {
		return phuTung;
	}

	public void setPhuTung(PhuTung phuTung) {
		this.phuTung = phuTung;
	}

	public PhieuNhapKho getPhieuNhapKho() {
		return phieuNhapKho;
	}

	public void setPhieuNhapKho(PhieuNhapKho phieuNhapKho) {
		this.phieuNhapKho = phieuNhapKho;
	}
	
	public double tinhThanhTien() {
		return Math.max(0, soLuongNhap) * Math.max(0, donGiaNhap);
	}
	
}
