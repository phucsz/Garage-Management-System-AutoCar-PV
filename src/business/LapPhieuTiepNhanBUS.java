package business;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import dao.KhachHangDAO;
import dao.PhieuTiepNhanDAO;
import dao.XeDAO;
import dao.NhanVienDAO;
import connectDB.ConnectDB;
import entity.ChiTietHangMuc;
import entity.PhieuTiepNhan;
import entity.KhachHang;
import entity.NhanVien;
import entity.Xe;


public class LapPhieuTiepNhanBUS {
	private PhieuTiepNhanDAO ptn_dao = new PhieuTiepNhanDAO();
	private KhachHangDAO kh_dao = new KhachHangDAO();
	private XeDAO xe_dao = new XeDAO();
	private NhanVienDAO nv_dao = new NhanVienDAO();
	
	public LapPhieuTiepNhanBUS() {
		
	}
	
	//Phát sinh mã phiếu tự động, VD ngày 5/11/2026 thì có mã là PTN05112026001
	public String taoMaPhieu(LocalDate ngay) throws SQLException {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyy");
		int dem = ptn_dao.getSoLuongPTNTrongNgay(ngay);
		
		return "PTN" + ngay.format(dtf) +String.format("%03d", dem + 1);
	}
	
	public KhachHang timKHTheoSDT(String sdt) throws SQLException {
		KhachHang kh = kh_dao.timTheoSdt(sdt);
		return kh;
	}
	
	public Xe timXeTheoBienSo(String bienSo) throws SQLException {
		Xe xe = xe_dao.timTheoBienSo(bienSo);
		return xe;
	}

	public boolean lapPhieu(PhieuTiepNhan ptn, String soDienThoai, String hoTenKhachHang, String hangXe)
			throws SQLException {
		java.sql.Connection con = ConnectDB.getConnection();
		boolean autoCommit = con.getAutoCommit();
		try {
			con.setAutoCommit(false);

			KhachHang kh = kh_dao.timTheoSdt(soDienThoai);
			if (kh == null) {
				kh = new KhachHang(kh_dao.taoMaKHMoi(), hoTenKhachHang, null, null, null, soDienThoai, null, null);
				if (!kh_dao.themKH(kh)) {
					con.rollback();
					return false;
				}
			}

			Xe xe = xe_dao.timTheoBienSo(ptn.getXe().getBienSoXe());
			if (xe == null) {
				xe = new Xe(ptn.getXe().getBienSoXe(), null, 0, hangXe, null, 0, ptn.getSoKmDaDi(), kh);
				if (!xe_dao.themXe(xe)) {
					con.rollback();
					return false;
				}
			} else if (xe.getKhachHang() == null
					|| !kh.getMaKH().equals(xe.getKhachHang().getMaKH())) {
				throw new SQLException("Biển số xe đã thuộc về một khách hàng khác.");
			}

			NhanVien nhanVien = nv_dao.timTheoHoTen(ptn.getNhanVienLapPhieu().getMaNV());
			if (nhanVien == null) {
				throw new SQLException("Không tìm thấy nhân viên tiếp nhận trong CSDL.");
			}
			ptn.setKhachHang(kh);
			ptn.setXe(xe);
			ptn.setNhanVienLapPhieu(nhanVien);

			if (!ptn_dao.themPTN(ptn)) {
				con.rollback();
				return false;
			}
			for (ChiTietHangMuc chiTiet : ptn.getChiTietHangMuc()) {
				if (!ptn_dao.themChiTietHangMuc(ptn.getMaPTN(), chiTiet)) {
					con.rollback();
					return false;
				}
			}
			con.commit();
			return true;
		} catch (SQLException e) {
			con.rollback();
			throw e;
		} finally {
			con.setAutoCommit(autoCommit);
		}
	}
}
