package entity;

public class HangMucKiemTra {
	private String maHangMuc;
	private String tenHangMuc;
	private String loaiHangMuc;

	public HangMucKiemTra() {
	}

	public HangMucKiemTra(String maHangMuc) {
		this.maHangMuc = maHangMuc;
	}

	public HangMucKiemTra(String maHangMuc, String tenHangMuc, String loaiHangMuc) {
		this.maHangMuc = maHangMuc;
		this.tenHangMuc = tenHangMuc;
		this.loaiHangMuc = loaiHangMuc;
	}

	public String getMaHangMuc() { return maHangMuc; }
	public void setMaHangMuc(String maHangMuc) { this.maHangMuc = maHangMuc; }
	public String getTenHangMuc() { return tenHangMuc; }
	public void setTenHangMuc(String tenHangMuc) { this.tenHangMuc = tenHangMuc; }
	public String getLoaiHangMuc() { return loaiHangMuc; }
	public void setLoaiHangMuc(String loaiHangMuc) { this.loaiHangMuc = loaiHangMuc; }

	@Override
	public String toString() {
		return String.format("%s %s %s", maHangMuc, tenHangMuc, loaiHangMuc);
	}
}
