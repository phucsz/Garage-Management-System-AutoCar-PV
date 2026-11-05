package business;

import java.sql.SQLException;
import java.util.ArrayList;

import dao.DichVuDAO;
import entity.DichVu;

public class DichVuBUS {
	private final DichVuDAO dao = new DichVuDAO();
	public ArrayList<DichVu> getAll() throws SQLException { return dao.getAll(); }
	public boolean them(DichVu value) throws SQLException { return dao.insert(value); }
	public boolean capNhat(DichVu value) throws SQLException { return dao.update(value); }
	public boolean xoa(String ma) throws SQLException { return dao.delete(ma); }
}
