package entity;

public class DanhMucPhuTung {
	private String maDanhMuc;
	private String tenDanhMuc;

	public DanhMucPhuTung() {
	}

	public DanhMucPhuTung(String maDanhMuc) {
		this.maDanhMuc = maDanhMuc;
	}

	public DanhMucPhuTung(String maDanhMuc, String tenDanhMuc) {
		this.maDanhMuc = maDanhMuc;
		this.tenDanhMuc = tenDanhMuc;
	}

	public String getMaDanhMuc() { return maDanhMuc; }
	public void setMaDanhMuc(String maDanhMuc) { this.maDanhMuc = maDanhMuc; }
	public String getTenDanhMuc() { return tenDanhMuc; }
	public void setTenDanhMuc(String tenDanhMuc) { this.tenDanhMuc = tenDanhMuc; }

	@Override
	public String toString() {
		return String.format("%s %s", maDanhMuc, tenDanhMuc);
	}
}
