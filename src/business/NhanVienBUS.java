package business;

import java.sql.SQLException;
import java.util.ArrayList;

import dao.NhanVienDAO;
import dao.TaiKhoanDAO;
import connectDB.ConnectDB;
import entity.NhanVien;

public class NhanVienBUS {
	private NhanVienDAO nv_dao = new NhanVienDAO();
	private TaiKhoanDAO tk_dao = new TaiKhoanDAO();
	
	public NhanVienBUS() {
	}
	
	public ArrayList<NhanVien> getDSNV() throws SQLException {
		return nv_dao.getdsNV();
	}
	
	public boolean themNhanVien(NhanVien nv) throws SQLException {
		if (nv_dao.themNV(nv)) return true;
		return false;
	}

	public String taoMaNVMoi() throws SQLException {
		return nv_dao.taoMaNVMoi();
	}

	public boolean themNhanVien(NhanVien nv, String maVaiTro) throws SQLException {
		return thucHienGiaoDich(() -> nv_dao.themNV(nv) && tk_dao.themTK(nv.getMaNV(), maVaiTro));
	}

	public boolean capNhatNhanVien(NhanVien nv, String maVaiTro) throws SQLException {
		return thucHienGiaoDich(() -> nv_dao.capNhatNV(nv) && tk_dao.capNhatVaiTro(nv.getMaNV(), maVaiTro));
	}

	public boolean capNhatMatKhau(String maNV, String matKhauMoi) throws SQLException {
		return tk_dao.capNhatMatKhau(maNV, matKhauMoi);
	}

	private boolean thucHienGiaoDich(GiaoDich giaoDich) throws SQLException {
		java.sql.Connection con = ConnectDB.getConnection();
		boolean autoCommit = con.getAutoCommit();
		try {
			con.setAutoCommit(false);
			boolean ketQua = giaoDich.thucHien();
			if (ketQua) {
				con.commit();
			} else {
				con.rollback();
			}
			return ketQua;
		} catch (SQLException e) {
			con.rollback();
			throw e;
		} finally {
			con.setAutoCommit(autoCommit);
		}
	}

	@FunctionalInterface
	private interface GiaoDich {
		boolean thucHien() throws SQLException;
	}
}
