package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.NhanVien;
import entity.TaiKhoan;
import entity.VaiTro;

public class TaiKhoanDAO {
	public ArrayList<TaiKhoan> getdsTK() throws SQLException{
		ArrayList<TaiKhoan> dsTK = new ArrayList<TaiKhoan>();
		ConnectDB.getInstance();
		Connection con = ConnectDB.getConnection();
		String sql = "Select * from TaiKhoan";
		Statement statement = con.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		
		while (rs.next()) {
			String tenDangNhap = rs.getString(1);
			String matKhau = rs.getString(2);
			String maVaiTro = rs.getString(3);
			
			NhanVien nv = new NhanVien(tenDangNhap);
			VaiTro vt = new VaiTro(maVaiTro);
			TaiKhoan tk = new TaiKhoan(nv, matKhau, vt);
			dsTK.add(tk);
		}
		return dsTK;
	}
	
	public TaiKhoan dangNhap(String tenDangNhap,String matKhau) throws SQLException {
		TaiKhoan tk = null;
		ConnectDB.getInstance();
		Connection con = ConnectDB.getConnection();
		
		String sql = "select tenDangNhap, convert(VARCHAR(100),DECRYPTBYPASSPHRASE('123',matKhau)) as matKhau,maVaiTro from TaiKhoan\r\n"
				+ "where tenDangNhap = ? and convert(VARCHAR(100),DECRYPTBYPASSPHRASE('123',matKhau)) = ?";
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, tenDangNhap);
		stmt.setString(2, matKhau);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			NhanVien nv = new NhanVien(tenDangNhap);
			String maVaiTro = rs.getString(3);
			VaiTro vt = new VaiTro(maVaiTro);
			
			tk = new TaiKhoan(nv, matKhau, vt);
		}
		return tk;
	}
}
