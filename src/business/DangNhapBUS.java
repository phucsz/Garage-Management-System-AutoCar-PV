package business;

import java.sql.SQLException;

import dao.TaiKhoanDAO;

public class DangNhapBUS {
	private TaiKhoanDAO tk_dao = new TaiKhoanDAO();
	
	public DangNhapBUS() {
		
	}
	
	public boolean DangNhap(String tenDangNhap, String matKhau) throws SQLException {
		if (tk_dao.dangNhap(tenDangNhap, matKhau)!= null) return true;
		return false;
	}
}
