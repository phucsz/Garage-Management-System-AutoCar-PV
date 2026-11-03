package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import connectDB.ConnectDB;
import entity.NhanVien;

public class NhanVienDAO {
	public NhanVienDAO() {
	}
	
	public NhanVien getNVTheoMa(String maNV) throws SQLException {
		ConnectDB.getInstance();
		Connection con = ConnectDB.getConnection();
		String sql = "Select * from NhanVien where maNV = ?";
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, maNV);
		
		ResultSet rs = stmt.executeQuery();
		
		NhanVien nv = null;
		if (rs.next()) {
			String hoTenNV = rs.getString(2); 
			String noiSinh = rs.getString(3); 
			LocalDate ngaySinh = rs.getDate(4).toLocalDate();
			String gioiTinh = rs.getString(5);
			String cccd = rs.getString(6);
			String sdt = rs.getString(7);
			String diaChi = rs.getString(8);
			String email = rs.getString(9);
			String trangThai = rs.getString(10);
			
			nv = new NhanVien(maNV, hoTenNV, noiSinh, ngaySinh, gioiTinh, cccd, sdt, diaChi, email, trangThai);
		}
		return nv;
	}

}
