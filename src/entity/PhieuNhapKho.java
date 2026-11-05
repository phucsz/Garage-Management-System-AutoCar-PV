package entity;

import java.time.LocalDateTime;

public class PhieuNhapKho {
	private String maPNK;
	private LocalDateTime ngayNhap;
	private NhanVien nhanVienLap;
	private NhaCungCap nhaCungCap;
	private String ghiChu;
	private double tongTienNhapKho;

	public PhieuNhapKho() {
	}

	public PhieuNhapKho(String maPNK) {
		this.maPNK = maPNK;
	}

	public PhieuNhapKho(String maPNK, LocalDateTime ngayNhap, NhanVien nhanVienLap, NhaCungCap nhaCungCap,
			String ghiChu, double tongTienNhapKho) {
		this.maPNK = maPNK;
		this.ngayNhap = ngayNhap;
		this.nhanVienLap = nhanVienLap;
		this.nhaCungCap = nhaCungCap;
		this.ghiChu = ghiChu;
		this.tongTienNhapKho = tongTienNhapKho;
	}

	public String getMaPNK() { return maPNK; }
	public void setMaPNK(String maPNK) { this.maPNK = maPNK; }
	public LocalDateTime getNgayNhap() { return ngayNhap; }
	public void setNgayNhap(LocalDateTime ngayNhap) { this.ngayNhap = ngayNhap; }
	public NhanVien getNhanVienLap() { return nhanVienLap; }
	public void setNhanVienLap(NhanVien nhanVienLap) { this.nhanVienLap = nhanVienLap; }
	public NhaCungCap getNhaCungCap() { return nhaCungCap; }
	public void setNhaCungCap(NhaCungCap nhaCungCap) { this.nhaCungCap = nhaCungCap; }
	public String getGhiChu() { return ghiChu; }
	public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
	public double getTongTienNhapKho() { return tongTienNhapKho; }
	public void setTongTienNhapKho(double tongTienNhapKho) { this.tongTienNhapKho = tongTienNhapKho; }

	@Override
	public String toString() {
		return String.format("%s %s %s %s %s %s", maPNK, ngayNhap,
				nhanVienLap == null ? null : nhanVienLap.getMaNV(),
				nhaCungCap == null ? null : nhaCungCap.getMaNCC(), ghiChu, tongTienNhapKho);
	}
}
