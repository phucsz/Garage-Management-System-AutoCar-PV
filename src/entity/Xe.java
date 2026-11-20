package entity;

public class Xe {
	private String bienSoXe;
	private String loaiXe;
	private int congSuat;
	private String hangXe;
	private String mauXe;
	private int soThangDaDi;
	

	public Xe() {
	}

	public Xe(String bienSoXe) {
		this.bienSoXe = bienSoXe;
	}

	public Xe(String bienSoXe, String loaiXe, int congSuat, String hangXe, String mauXe, int soThangDaDi) {
		this.bienSoXe = bienSoXe;
		this.loaiXe = loaiXe;
		this.congSuat = congSuat;
		this.hangXe = hangXe;
		this.mauXe = mauXe;
		this.soThangDaDi = soThangDaDi;
	}

	public String getBienSoXe() { return bienSoXe; }
	public void setBienSoXe(String bienSoXe) { this.bienSoXe = bienSoXe; }
	public String getLoaiXe() { return loaiXe; }
	public void setLoaiXe(String loaiXe) { this.loaiXe = loaiXe; }
	public int getCongSuat() { return congSuat; }
	public void setCongSuat(int congSuat) { this.congSuat = congSuat; }
	public String getHangXe() { return hangXe; }
	public void setHangXe(String hangXe) { this.hangXe = hangXe; }
	public String getMauXe() { return mauXe; }
	public void setMauXe(String mauXe) { this.mauXe = mauXe; }
	public int getSoThangDaDi() { return soThangDaDi; }
	public void setSoThangDaDi(int soThangDaDi) { this.soThangDaDi = soThangDaDi; }


	@Override
	public String toString() {
		return String.format("%s %s %s %s %s %s %s %s", bienSoXe, loaiXe, congSuat, hangXe, mauXe, soThangDaDi);
	}
}
