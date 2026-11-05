package entity;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class PhieuTiepNhan {
	private String maPTN;
	private LocalDateTime ngayTiepNhan;
	private String yeuCauKhachHang;
	private double soKmDaDi;
	private NhanVien nhanVienLapPhieu;
	private Xe xe;
	private KhachHang khachHang;
	private ArrayList<ChiTietHangMuc> chiTietHangMuc;

	public PhieuTiepNhan() {
	}

	public PhieuTiepNhan(String maPTN) {
		this.maPTN = maPTN;
	}

	public PhieuTiepNhan(String maPTN, LocalDateTime ngayTiepNhan, String yeuCauKhachHang, double soKmDaDi,
			NhanVien nhanVienLapPhieu, Xe xe, KhachHang khachHang) {
		this.maPTN = maPTN;
		this.ngayTiepNhan = ngayTiepNhan;
		this.yeuCauKhachHang = yeuCauKhachHang;
		this.soKmDaDi = soKmDaDi;
		this.nhanVienLapPhieu = nhanVienLapPhieu;
		this.xe = xe;
		this.khachHang = khachHang;
	}

	public String getMaPTN() { return maPTN; }
	public void setMaPTN(String maPTN) { this.maPTN = maPTN; }
	public LocalDateTime getNgayTiepNhan() { return ngayTiepNhan; }
	public void setNgayTiepNhan(LocalDateTime ngayTiepNhan) { this.ngayTiepNhan = ngayTiepNhan; }
	public String getYeuCauKhachHang() { return yeuCauKhachHang; }
	public void setYeuCauKhachHang(String yeuCauKhachHang) { this.yeuCauKhachHang = yeuCauKhachHang; }
	public double getSoKmDaDi() { return soKmDaDi; }
	public void setSoKmDaDi(double soKmDaDi) { this.soKmDaDi = soKmDaDi; }
	public NhanVien getNhanVienLapPhieu() { return nhanVienLapPhieu; }
	public void setNhanVienLapPhieu(NhanVien nhanVienLapPhieu) { this.nhanVienLapPhieu = nhanVienLapPhieu; }
	public Xe getXe() { return xe; }
	public void setXe(Xe xe) { this.xe = xe; }
	public KhachHang getKhachHang() { return khachHang; }
	public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }
	
	public ArrayList<ChiTietHangMuc> getChiTietHangMuc() {
		return chiTietHangMuc;
	}

	public void setChiTietHangMuc(ArrayList<ChiTietHangMuc> chiTietHangMuc) {
		this.chiTietHangMuc = chiTietHangMuc;
	}
	
	@Override
	public String toString() {
		return String.format("%s %s %s %s %s %s %s", maPTN, ngayTiepNhan, yeuCauKhachHang, soKmDaDi,
				nhanVienLapPhieu == null ? null : nhanVienLapPhieu.getMaNV(),
				xe == null ? null : xe.getBienSoXe(), khachHang == null ? null : khachHang.getMaKH());
	}
}
