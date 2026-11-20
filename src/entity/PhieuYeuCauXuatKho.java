package entity;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class PhieuYeuCauXuatKho {
	private String maYCXK;
	private LocalDateTime ngayYeuCau;
	private LocalDateTime ngayDuyet;
	private String trangThai;
	private String ghiChu;
	private NhanVien nhanVienYeuCau;
	private NhanVien nhanVienDuyet;
	private PhieuSuaChua phieuSuaChua;
	private ArrayList<ChiTietYeuCau> chiTietYeuCau;
	
	public PhieuYeuCauXuatKho() {
	}
	
	public PhieuYeuCauXuatKho(String maYCXK) {
		this.maYCXK = maYCXK;
	}
	
	public PhieuYeuCauXuatKho(String maYCXK, LocalDateTime ngayYeuCau, LocalDateTime ngayDuyet, String trangThai,
			String ghiChu, NhanVien nhanVienYeuCau, NhanVien nhanVienDuyet, PhieuSuaChua phieuSuaChua,
			ArrayList<ChiTietYeuCau> chiTietYeuCau) {
		this.maYCXK = maYCXK;
		this.ngayYeuCau = ngayYeuCau;
		this.ngayDuyet = ngayDuyet;
		this.trangThai = trangThai;
		this.ghiChu = ghiChu;
		this.nhanVienYeuCau = nhanVienYeuCau;
		this.nhanVienDuyet = nhanVienDuyet;
		this.phieuSuaChua = phieuSuaChua;
		this.chiTietYeuCau = chiTietYeuCau;
	}

	public String getMaYCXK() {
		return maYCXK;
	}

	public void setMaYCXK(String maYCXK) {
		this.maYCXK = maYCXK;
	}

	public LocalDateTime getNgayYeuCau() {
		return ngayYeuCau;
	}

	public void setNgayYeuCau(LocalDateTime ngayYeuCau) {
		this.ngayYeuCau = ngayYeuCau;
	}

	public LocalDateTime getNgayDuyet() {
		return ngayDuyet;
	}

	public void setNgayDuyet(LocalDateTime ngayDuyet) {
		this.ngayDuyet = ngayDuyet;
	}

	public String getTrangThai() {
		return trangThai;
	}

	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}

	public String getGhiChu() {
		return ghiChu;
	}

	public void setGhiChu(String ghiChu) {
		this.ghiChu = ghiChu;
	}

	public NhanVien getNhanVienYeuCau() {
		return nhanVienYeuCau;
	}

	public void setNhanVienYeuCau(NhanVien nhanVienYeuCau) {
		this.nhanVienYeuCau = nhanVienYeuCau;
	}

	public NhanVien getNhanVienDuyet() {
		return nhanVienDuyet;
	}

	public void setNhanVienDuyet(NhanVien nhanVienDuyet) {
		this.nhanVienDuyet = nhanVienDuyet;
	}

	public PhieuSuaChua getPhieuSuaChua() {
		return phieuSuaChua;
	}

	public void setPhieuSuaChua(PhieuSuaChua phieuSuaChua) {
		this.phieuSuaChua = phieuSuaChua;
	}

	public ArrayList<ChiTietYeuCau> getChiTietYeuCau() {
		return chiTietYeuCau;
	}

	public void setChiTietYeuCau(ArrayList<ChiTietYeuCau> chiTietYeuCau) {
		this.chiTietYeuCau = chiTietYeuCau;
	}	
}
