package entity;

public class NhaCungCap {
	private String maNCC;
	private String tenNCC;
	private String diaChi;
	private String email;
	private String sdt;

	public NhaCungCap() {
	}

	public NhaCungCap(String maNCC) {
		this.maNCC = maNCC;
	}

	public NhaCungCap(String maNCC, String tenNCC, String diaChi, String email, String sdt) {
		this.maNCC = maNCC;
		this.tenNCC = tenNCC;
		this.diaChi = diaChi;
		this.email = email;
		this.sdt = sdt;
	}

	public String getMaNCC() { return maNCC; }
	public void setMaNCC(String maNCC) { this.maNCC = maNCC; }
	public String getTenNCC() { return tenNCC; }
	public void setTenNCC(String tenNCC) { this.tenNCC = tenNCC; }
	public String getDiaChi() { return diaChi; }
	public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getSdt() { return sdt; }
	public void setSdt(String sdt) { this.sdt = sdt; }

	@Override
	public String toString() {
		return String.format("%s %s %s %s %s", maNCC, tenNCC, diaChi, email, sdt);
	}
}
