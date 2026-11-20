package entity;

import java.time.LocalDateTime;

public class ChiTietDichVu {
	private PhieuSuaChua phieuSuaChua;
	private DichVu dichVu;
	private int soLuong;
	private double donGia;
	private double thueSuatVAT;
	private String ghiChu;
	private LocalDateTime thoiGianCapNhat;
	private String trangThai;

	public ChiTietDichVu() {
	}

	public ChiTietDichVu(PhieuSuaChua phieuSuaChua, DichVu dichVu) {
		this.phieuSuaChua = phieuSuaChua;
		this.dichVu = dichVu;
	}

	public ChiTietDichVu(PhieuSuaChua phieuSuaChua, DichVu dichVu, int soLuong, double donGia, double thueSuatVAT,
			String ghiChu, LocalDateTime thoiGianCapNhat, String trangThai) {
		this.phieuSuaChua = phieuSuaChua;
		this.dichVu = dichVu;
		this.soLuong = soLuong;
		this.donGia = donGia;
		this.thueSuatVAT = thueSuatVAT;
		this.ghiChu = ghiChu;
		this.thoiGianCapNhat = thoiGianCapNhat;
		this.trangThai = trangThai;
	}

	public PhieuSuaChua getPhieuSuaChua() { return phieuSuaChua; }
	public void setPhieuSuaChua(PhieuSuaChua phieuSuaChua) { this.phieuSuaChua = phieuSuaChua; }
	public DichVu getDichVu() { return dichVu; }
	public void setDichVu(DichVu dichVu) { this.dichVu = dichVu; }
	public int getSoLuong() { return soLuong; }
	public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
	public double getDonGia() { return donGia; }
	public void setDonGia(double donGia) { this.donGia = donGia; }
	public String getGhiChu() { return ghiChu; }
	public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

	public double getThueSuatVAT() {
		return thueSuatVAT;
	}

	public void setThueSuatVAT(double thueSuatVAT) {
		this.thueSuatVAT = thueSuatVAT;
	}

	public LocalDateTime getThoiGianCapNhat() {
		return thoiGianCapNhat;
	}

	public void setThoiGianCapNhat(LocalDateTime thoiGianCapNhat) {
		this.thoiGianCapNhat = thoiGianCapNhat;
	}

	public String getTrangThai() {
		return trangThai;
	}

	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}

	@Override
	public String toString() {
		return String.format("%s %s %s %s %s",
				phieuSuaChua == null ? null : phieuSuaChua.getMaPSC(),
				dichVu == null ? null : dichVu.getMaDichVu(), soLuong, donGia, ghiChu);
	}
}
