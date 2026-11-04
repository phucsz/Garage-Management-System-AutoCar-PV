package business;

import java.sql.SQLException;

import dao.NhanVienDAO;
import dao.TaiKhoanDAO;
import entity.NhanVien;
import entity.TaiKhoan;

public class DangNhapBUS {
	private NhanVienDAO nv_dao = new NhanVienDAO();
	private TaiKhoanDAO tk_dao = new TaiKhoanDAO();
	
	public DangNhapBUS() {
		
	}
	
	public TaiKhoan DangNhap(String tenDangNhap, String matKhau) throws SQLException {
		TaiKhoan tk = tk_dao.dangNhap(tenDangNhap, matKhau);
		return tk;
	}
	
	public NhanVien getNVtheoMa(String tenDN) throws SQLException {
		NhanVien nv = nv_dao.getNVTheoMa(tenDN);
		
		return nv;
	}
}
