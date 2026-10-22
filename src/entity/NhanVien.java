package entity;

import java.time.LocalDate;

public class NhanVien {
	private String maNV;
	private String hoTenNV;
	private String noiSinh;
	private LocalDate ngaySinh;
	private String gioiTinh;
	private String cccd;
	private String sdt;
	private String diaChi;
	private String email;
	private String trangThai;
	
	public NhanVien() {
	}
	public NhanVien(String maNV) {
		this.maNV = maNV;
	}
	
	public NhanVien(String maNV, String hoTenNV, String noiSinh, LocalDate ngaySinh, String gioiTinh, String cccd,
			String sdt, String diaChi, String email, String trangThai) {
		this.maNV = maNV;
		this.hoTenNV = hoTenNV;
		this.noiSinh = noiSinh;
		this.ngaySinh = ngaySinh;
		this.gioiTinh = gioiTinh;
		this.cccd = cccd;
		this.sdt = sdt;
		this.diaChi = diaChi;
		this.email = email;
		this.trangThai = trangThai;
	}

	public String getMaNV() {
		return maNV;
	}

	public void setMaNV(String maNV) {
		this.maNV = maNV;
	}

	public String getHoTenNV() {
		return hoTenNV;
	}

	public void setHoTenNV(String hoTenNV) {
		this.hoTenNV = hoTenNV;
	}

	public String getNoiSinh() {
		return noiSinh;
	}

	public void setNoiSinh(String noiSinh) {
		this.noiSinh = noiSinh;
	}

	public LocalDate getNgaySinh() {
		return ngaySinh;
	}

	public void setNgaySinh(LocalDate ngaySinh) {
		this.ngaySinh = ngaySinh;
	}

	public String getGioiTinh() {
		return gioiTinh;
	}

	public void setGioiTinh(String gioiTinh) {
		this.gioiTinh = gioiTinh;
	}

	public String getCccd() {
		return cccd;
	}

	public void setCccd(String cccd) {
		this.cccd = cccd;
	}

	public String getSdt() {
		return sdt;
	}

	public void setSdt(String sdt) {
		this.sdt = sdt;
	}

	public String getDiaChi() {
		return diaChi;
	}

	public void setDiaChi(String diaChi) {
		this.diaChi = diaChi;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTrangThai() {
		return trangThai;
	}

	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}
	
	
}
