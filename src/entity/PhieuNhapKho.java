package entity;

import java.time.LocalDate;
import java.util.ArrayList;


public class PhieuNhapKho {
	private String maPNK;
	private LocalDate ngayNhap;
	private NhanVien nhanVienLapPhieu;
	private NhaCungCap nhaCungCap;
	private String ghiChu;
	private ArrayList<LoPhuTung> chiTietNhapKho;

	public PhieuNhapKho() {
	}

	public PhieuNhapKho(String maPNK) {
		this.maPNK = maPNK;
	}

	public PhieuNhapKho(String maPNK, LocalDate ngayNhap, NhanVien nhanVienLapPhieu, NhaCungCap nhaCungCap,
			String ghiChu, ArrayList<LoPhuTung> chiTietNhapKho) {
		this.maPNK = maPNK;
		this.ngayNhap = ngayNhap;
		this.nhanVienLapPhieu = nhanVienLapPhieu;
		this.nhaCungCap = nhaCungCap;
		this.ghiChu = ghiChu;
		this.chiTietNhapKho = chiTietNhapKho;
	}

	public String getMaPNK() { return maPNK; }
	public void setMaPNK(String maPNK) { this.maPNK = maPNK; }
	public LocalDate getNgayNhap() { return ngayNhap; }
	public void setNgayNhap(LocalDate ngayNhap) { this.ngayNhap = ngayNhap; }
	public NhaCungCap getNhaCungCap() { return nhaCungCap; }
	public void setNhaCungCap(NhaCungCap nhaCungCap) { this.nhaCungCap = nhaCungCap; }
	public String getGhiChu() { return ghiChu; }
	public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

	public NhanVien getNhanVienLapPhieu() {
		return nhanVienLapPhieu;
	}

	public void setNhanVienLapPhieu(NhanVien nhanVienLapPhieu) {
		this.nhanVienLapPhieu = nhanVienLapPhieu;
	}

	public ArrayList<LoPhuTung> getChiTietNhapKho() {
		return chiTietNhapKho;
	}

	public void setChiTietNhapKho(ArrayList<LoPhuTung> chiTietNhapKho) {
		this.chiTietNhapKho = chiTietNhapKho;
	}
		
}
