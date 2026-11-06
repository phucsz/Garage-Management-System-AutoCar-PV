package business;

import java.sql.SQLException;
import java.util.ArrayList;

import dao.DichVuDAO;
import entity.DichVu;

public class DichVuBUS {
	private final DichVuDAO dao = new DichVuDAO();
	public ArrayList<DichVu> getAll() throws SQLException { return dao.getAll(); }
	public ArrayList<DichVu> timKiem(String ma, String loai) throws SQLException { return dao.timKiem(ma, loai); }
	public ArrayList<String> layDanhSachLoai() throws SQLException { return dao.layDanhSachLoai(); }
	public boolean them(DichVu value) throws SQLException { return dao.insert(value); }
	public boolean capNhat(DichVu value) throws SQLException { return dao.update(value); }
	public boolean xoa(String ma) throws SQLException { return dao.delete(ma); }
	
	public String phatSinhMaDV() throws SQLException { 
		return String.format("DV%03d", dao.getSoLuongDV()+1); 
	}
}
