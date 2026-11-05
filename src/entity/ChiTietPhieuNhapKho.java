package entity;

public class ChiTietPhieuNhapKho {
	private PhieuNhapKho phieuNhapKho;
	private PhuTung phuTung;
	private int soLuong;
	private double donGiaNhap;

	public ChiTietPhieuNhapKho() {
	}

	public ChiTietPhieuNhapKho(PhieuNhapKho phieuNhapKho, PhuTung phuTung) {
		this.phieuNhapKho = phieuNhapKho;
		this.phuTung = phuTung;
	}

	public ChiTietPhieuNhapKho(PhieuNhapKho phieuNhapKho, PhuTung phuTung, int soLuong, double donGiaNhap) {
		this.phieuNhapKho = phieuNhapKho;
		this.phuTung = phuTung;
		this.soLuong = soLuong;
		this.donGiaNhap = donGiaNhap;
	}

	public PhieuNhapKho getPhieuNhapKho() { return phieuNhapKho; }
	public void setPhieuNhapKho(PhieuNhapKho phieuNhapKho) { this.phieuNhapKho = phieuNhapKho; }
	public PhuTung getPhuTung() { return phuTung; }
	public void setPhuTung(PhuTung phuTung) { this.phuTung = phuTung; }
	public int getSoLuong() { return soLuong; }
	public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
	public double getDonGiaNhap() { return donGiaNhap; }
	public void setDonGiaNhap(double donGiaNhap) { this.donGiaNhap = donGiaNhap; }

	@Override
	public String toString() {
		return String.format("%s %s %s %s",
				phieuNhapKho == null ? null : phieuNhapKho.getMaPNK(),
				phuTung == null ? null : phuTung.getMaPhuTung(), soLuong, donGiaNhap);
	}
}
