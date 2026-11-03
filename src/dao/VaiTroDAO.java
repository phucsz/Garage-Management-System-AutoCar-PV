package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connectDB.ConnectDB;
import entity.VaiTro;

public class VaiTroDAO {
	public VaiTroDAO() {
	}
	
	public VaiTro getVaiTroTheoMa(String maVaiTro) throws SQLException {
		ConnectDB.getInstance();
		Connection con = ConnectDB.getConnection();
		String sql = "Select * from VaiTro where maVaiTro = ?";
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, maVaiTro);
		
		VaiTro vt = null;
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			String tenVaiTro = rs.getString(2);
			String moTa = rs.getString(3);
			
			vt = new VaiTro(maVaiTro, tenVaiTro, moTa);
		}
		return vt;
	}
}
