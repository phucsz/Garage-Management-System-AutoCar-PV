package entity;

public class DichVu {
	private String maDichVu;
	private String tenDichVu;
	private String loaiDichVu;
	private double donGiaHienTai;

	public DichVu() {
	}

	public DichVu(String maDichVu) {
		this.maDichVu = maDichVu;
	}

	public DichVu(String maDichVu, String tenDichVu, String loaiDichVu, double donGiaHienTai) {
		this.maDichVu = maDichVu;
		this.tenDichVu = tenDichVu;
		this.loaiDichVu = loaiDichVu;
		this.donGiaHienTai = donGiaHienTai;
	}

	public String getMaDichVu() { return maDichVu; }
	public void setMaDichVu(String maDichVu) { this.maDichVu = maDichVu; }
	public String getTenDichVu() { return tenDichVu; }
	public void setTenDichVu(String tenDichVu) { this.tenDichVu = tenDichVu; }
	public String getLoaiDichVu() { return loaiDichVu; }
	public void setLoaiDichVu(String loaiDichVu) { this.loaiDichVu = loaiDichVu; }
	public double getDonGiaHienTai() { return donGiaHienTai; }
	public void setDonGiaHienTai(double donGiaHienTai) { this.donGiaHienTai = donGiaHienTai; }

	@Override
	public String toString() {
		return String.format("%s %s %s %s", maDichVu, tenDichVu, loaiDichVu, donGiaHienTai);
	}
}
