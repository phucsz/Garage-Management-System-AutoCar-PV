package business;

import java.sql.SQLException;
import java.util.ArrayList;

import dao.ChuongTrinhKhuyenMaiDAO;
import entity.ChuongTrinhKhuyenMai;

/** Nghiệp vụ cho màn hình KhuyenMaiGUI, thao tác trên Entity ChuongTrinhKhuyenMai. */
public class KhuyenMaiBUS {
	private final ChuongTrinhKhuyenMaiDAO dao = new ChuongTrinhKhuyenMaiDAO();
	
	public ArrayList<ChuongTrinhKhuyenMai> getAll() throws SQLException { 
		return dao.getAll(); 
	}
	
	public boolean them(ChuongTrinhKhuyenMai value) throws SQLException { 
		return dao.insert(value); 
	}
	
	public boolean capNhat(ChuongTrinhKhuyenMai value) throws SQLException { 
		return dao.update(value); 
	}
	
	public boolean xoa(String ma) throws SQLException { 
		return dao.delete(ma); 
	}
	
	public String phatSinhMaKM() throws SQLException {
		return String.format("KM%03d", dao.getSoLuongKM() +1);
	}
}
