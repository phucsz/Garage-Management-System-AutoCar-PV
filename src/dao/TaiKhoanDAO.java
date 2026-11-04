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
	private VaiTroDAO vt_dao = new VaiTroDAO();
	
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
			VaiTro vt = vt_dao.getVaiTroTheoMa(maVaiTro);
			
			tk = new TaiKhoan(nv, matKhau, vt);
		}
		return tk;
	}
	
	//public boolean taoTaiKhoan(String maNV,)
	public boolean themTK(String maNV, String maVaiTro) throws SQLException {
		ConnectDB.getInstance();
		Connection con = ConnectDB.getConnection();
		String sql = "insert into TaiKhoan(tenDangNhap, matKhau, maVaiTro) "
				+ "values (?, ENCRYPTBYPASSPHRASE('123', ?), ?)";
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, maNV);
		stmt.setString(2, "123");
		stmt.setString(3, maVaiTro);
		return stmt.executeUpdate() > 0;
	}

	public boolean capNhatVaiTro(String maNV, String maVaiTro) throws SQLException {
		ConnectDB.getInstance();
		Connection con = ConnectDB.getConnection();
		String updateSql = "update TaiKhoan set maVaiTro=? where tenDangNhap=?";
		PreparedStatement update = con.prepareStatement(updateSql);
		update.setString(1, maVaiTro);
		update.setString(2, maNV);
		if (update.executeUpdate() > 0) {
			return true;
		}
		return themTK(maNV, maVaiTro);
	}

	public boolean capNhatMatKhau(String maNV, String matKhauMoi) throws SQLException {
		ConnectDB.getInstance();
		Connection con = ConnectDB.getConnection();
		String sql = "update TaiKhoan set matKhau=ENCRYPTBYPASSPHRASE('123', ?) where tenDangNhap=?";
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, matKhauMoi);
		stmt.setString(2, maNV);
		return stmt.executeUpdate() > 0;
	}
}
