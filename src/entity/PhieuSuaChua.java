package entity;

import java.time.LocalDateTime;

public class PhieuSuaChua {
	private String maPSC;
	private LocalDateTime ngayLapPhieu;
	private String trangThai;
	private PhieuTiepNhan phieuTiepNhan;
	private NhanVien nhanVienLapPhieu;
	private NhanVien nhanVienPhuTrach;
	private double tongTienDichVu;
	private double tongTienPhuTung;
	private double tongChiPhiDuKien;

	public PhieuSuaChua() {
	}

	public PhieuSuaChua(String maPSC) {
		this.maPSC = maPSC;
	}

	public PhieuSuaChua(String maPSC, LocalDateTime ngayLapPhieu, String trangThai, PhieuTiepNhan phieuTiepNhan,
			NhanVien nhanVienLapPhieu, NhanVien nhanVienPhuTrach, double tongTienDichVu, double tongTienPhuTung,
			double tongChiPhiDuKien) {
		this.maPSC = maPSC;
		this.ngayLapPhieu = ngayLapPhieu;
		this.trangThai = trangThai;
		this.phieuTiepNhan = phieuTiepNhan;
		this.nhanVienLapPhieu = nhanVienLapPhieu;
		this.nhanVienPhuTrach = nhanVienPhuTrach;
		this.tongTienDichVu = tongTienDichVu;
		this.tongTienPhuTung = tongTienPhuTung;
		this.tongChiPhiDuKien = tongChiPhiDuKien;
	}

	public String getMaPSC() { return maPSC; }
	public void setMaPSC(String maPSC) { this.maPSC = maPSC; }
	public LocalDateTime getNgayLapPhieu() { return ngayLapPhieu; }
	public void setNgayLapPhieu(LocalDateTime ngayLapPhieu) { this.ngayLapPhieu = ngayLapPhieu; }
	public String getTrangThai() { return trangThai; }
	public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
	public PhieuTiepNhan getPhieuTiepNhan() { return phieuTiepNhan; }
	public void setPhieuTiepNhan(PhieuTiepNhan phieuTiepNhan) { this.phieuTiepNhan = phieuTiepNhan; }
	public NhanVien getNhanVienLapPhieu() { return nhanVienLapPhieu; }
	public void setNhanVienLapPhieu(NhanVien nhanVienLapPhieu) { this.nhanVienLapPhieu = nhanVienLapPhieu; }
	public NhanVien getNhanVienPhuTrach() { return nhanVienPhuTrach; }
	public void setNhanVienPhuTrach(NhanVien nhanVienPhuTrach) { this.nhanVienPhuTrach = nhanVienPhuTrach; }
	public double getTongTienDichVu() { return tongTienDichVu; }
	public void setTongTienDichVu(double tongTienDichVu) { this.tongTienDichVu = tongTienDichVu; }
	public double getTongTienPhuTung() { return tongTienPhuTung; }
	public void setTongTienPhuTung(double tongTienPhuTung) { this.tongTienPhuTung = tongTienPhuTung; }
	public double getTongChiPhiDuKien() { return tongChiPhiDuKien; }
	public void setTongChiPhiDuKien(double tongChiPhiDuKien) { this.tongChiPhiDuKien = tongChiPhiDuKien; }

	@Override
	public String toString() {
		return String.format("%s %s %s %s %s %s %s %s %s",
				maPSC, ngayLapPhieu, trangThai,
				phieuTiepNhan == null ? null : phieuTiepNhan.getMaPTN(),
				nhanVienLapPhieu == null ? null : nhanVienLapPhieu.getMaNV(),
				nhanVienPhuTrach == null ? null : nhanVienPhuTrach.getMaNV(),
				tongTienDichVu, tongTienPhuTung, tongChiPhiDuKien);
	}
}
