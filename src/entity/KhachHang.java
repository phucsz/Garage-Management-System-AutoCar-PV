package entity;

import java.time.LocalDate;

public class KhachHang {
	private String maKH;
	private String hoTenKH;
	private String noiSinh;
	private LocalDate ngaySinh;
	private String cccd;
	private String sdt;
	private String diaChi;
	private String email;

	public KhachHang() {
	}

	public KhachHang(String maKH) {
		this.maKH = maKH;
	}

	public KhachHang(String maKH, String hoTenKH, String noiSinh, LocalDate ngaySinh, String cccd, String sdt,
			String diaChi, String email) {
		this.maKH = maKH;
		this.hoTenKH = hoTenKH;
		this.noiSinh = noiSinh;
		this.ngaySinh = ngaySinh;
		this.cccd = cccd;
		this.sdt = sdt;
		this.diaChi = diaChi;
		this.email = email;
	}

	public String getMaKH() { return maKH; }
	public void setMaKH(String maKH) { this.maKH = maKH; }
	public String getHoTenKH() { return hoTenKH; }
	public void setHoTenKH(String hoTenKH) { this.hoTenKH = hoTenKH; }
	public String getNoiSinh() { return noiSinh; }
	public void setNoiSinh(String noiSinh) { this.noiSinh = noiSinh; }
	public LocalDate getNgaySinh() { return ngaySinh; }
	public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
	public String getCccd() { return cccd; }
	public void setCccd(String cccd) { this.cccd = cccd; }
	public String getSdt() { return sdt; }
	public void setSdt(String sdt) { this.sdt = sdt; }
	public String getDiaChi() { return diaChi; }
	public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	@Override
	public String toString() {
		return String.format("%s %s %s %s %s %s %s %s", maKH, hoTenKH, noiSinh, ngaySinh, cccd, sdt, diaChi, email);
	}
}
