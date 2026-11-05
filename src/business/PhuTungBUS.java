package business;

import java.sql.SQLException;
import java.util.ArrayList;

import dao.PhuTungDAO;
import entity.PhuTung;

public class PhuTungBUS {
	private final PhuTungDAO dao = new PhuTungDAO();
	public ArrayList<PhuTung> getAll() throws SQLException { return dao.getAll(); }
	public boolean them(PhuTung value) throws SQLException { return dao.insert(value); }
	public boolean capNhat(PhuTung value) throws SQLException { return dao.update(value); }
	public boolean xoa(String ma) throws SQLException { return dao.delete(ma); }
}
