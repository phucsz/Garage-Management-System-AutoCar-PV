package business;

import java.sql.SQLException;
import java.util.ArrayList;

import dao.NhanVienDAO;
import entity.NhanVien;

public class NhanVienBUS {
	private NhanVienDAO nv_dao = new NhanVienDAO();
	
	public NhanVienBUS() {
	}
	
	public ArrayList<NhanVien> getDSNV() throws SQLException {
		return nv_dao.getdsNV();
	}
	
	public boolean themNhanVien(NhanVien nv) throws SQLException {
		if (nv_dao.themNV(nv)) return true;
		return false;
	}
}
