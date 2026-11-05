package entity;

public class ChiTietHangMuc {
	private PhieuTiepNhan phieuTiepNhan;
	private HangMucKiemTra hangMuc;
	private String tinhTrang;

	public ChiTietHangMuc() {
	}

	public ChiTietHangMuc(PhieuTiepNhan phieuTiepNhan, HangMucKiemTra hangMuc) {
		this.phieuTiepNhan = phieuTiepNhan;
		this.hangMuc = hangMuc;
	}

	public ChiTietHangMuc(PhieuTiepNhan phieuTiepNhan, HangMucKiemTra hangMuc, String tinhTrang) {
		this.phieuTiepNhan = phieuTiepNhan;
		this.hangMuc = hangMuc;
		this.tinhTrang = tinhTrang;
	}

	public PhieuTiepNhan getPhieuTiepNhan() { return phieuTiepNhan; }
	public void setPhieuTiepNhan(PhieuTiepNhan phieuTiepNhan) { this.phieuTiepNhan = phieuTiepNhan; }
	public HangMucKiemTra getHangMuc() { return hangMuc; }
	public void setHangMuc(HangMucKiemTra hangMuc) { this.hangMuc = hangMuc; }
	public String getTinhTrang() { return tinhTrang; }
	public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }

	@Override
	public String toString() {
		return String.format("%s %s %s", phieuTiepNhan == null ? null : phieuTiepNhan.getMaPTN(),
				hangMuc == null ? null : hangMuc.getMaHangMuc(), tinhTrang);
	}
}
