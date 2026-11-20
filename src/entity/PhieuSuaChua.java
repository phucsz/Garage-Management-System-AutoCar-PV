package entity;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class PhieuSuaChua {
	private String maPSC;
	private LocalDateTime ngayLapPhieu;
	private String trangThai;
	private PhieuTiepNhan phieuTiepNhan;
	private NhanVien nhanVienPhuTrach;
	private ArrayList<ChiTietPhuTung> chiTietPhuTung;
	private ArrayList<ChiTietDichVu> chiTietDichVu;
	

	public PhieuSuaChua() {
	}

	public PhieuSuaChua(String maPSC) {
		this.maPSC = maPSC;
	}


	public PhieuSuaChua(String maPSC, LocalDateTime ngayLapPhieu, String trangThai, PhieuTiepNhan phieuTiepNhan,
			NhanVien nhanVienPhuTrach, ArrayList<ChiTietPhuTung> chiTietPhuTung,
			ArrayList<ChiTietDichVu> chiTietDichVu) {
		super();
		this.maPSC = maPSC;
		this.ngayLapPhieu = ngayLapPhieu;
		this.trangThai = trangThai;
		this.phieuTiepNhan = phieuTiepNhan;
		this.nhanVienPhuTrach = nhanVienPhuTrach;
		setChiTietPhuTung(chiTietPhuTung);
		setChiTietDichVu(chiTietDichVu);
	}

	public String getMaPSC() { return maPSC; }
	public void setMaPSC(String maPSC) { this.maPSC = maPSC; }
	public LocalDateTime getNgayLapPhieu() { return ngayLapPhieu; }
	public void setNgayLapPhieu(LocalDateTime ngayLapPhieu) { this.ngayLapPhieu = ngayLapPhieu; }
	public String getTrangThai() { return trangThai; }
	public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
	public PhieuTiepNhan getPhieuTiepNhan() { return phieuTiepNhan; }
	public void setPhieuTiepNhan(PhieuTiepNhan phieuTiepNhan) { this.phieuTiepNhan = phieuTiepNhan; }
	public NhanVien getNhanVienPhuTrach() { return nhanVienPhuTrach; }
	public void setNhanVienPhuTrach(NhanVien nhanVienPhuTrach) { this.nhanVienPhuTrach = nhanVienPhuTrach; }


	

	public ArrayList<ChiTietPhuTung> getChiTietPhuTung() {
		return chiTietPhuTung;
	}

	public void setChiTietPhuTung(ArrayList<ChiTietPhuTung> chiTietPhuTung) {
		this.chiTietPhuTung = chiTietPhuTung;
	}

	public ArrayList<ChiTietDichVu> getChiTietDichVu() {
		return chiTietDichVu;
	}

	public void setChiTietDichVu(ArrayList<ChiTietDichVu> chiTietDichVu) {
		this.chiTietDichVu = chiTietDichVu;
	}

	public double getTongTienDichVu() {
		if (chiTietDichVu == null) {
			return 0;
		}
		double tong = 0;
		for (ChiTietDichVu chiTiet : chiTietDichVu) {
			if (chiTiet != null) {
				tong += Math.max(0, chiTiet.getSoLuong()) * Math.max(0, chiTiet.getDonGia());
			}
		}
		return tong;
	}

	public double getTongTienPhuTung() {
		if (chiTietPhuTung == null) {
			return 0;
		}
		double tong = 0;
		for (ChiTietPhuTung chiTiet : chiTietPhuTung) {
			if (chiTiet != null) {
				tong += Math.max(0, chiTiet.getSoLuong()) * Math.max(0, chiTiet.getDonGia());
			}
		}
		return tong;
	}
}
