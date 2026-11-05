package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.NhanVien;


public class NhanVienDAO {
	public NhanVienDAO() {
	}
	
	public ArrayList<NhanVien> getdsNV() throws SQLException{
		ArrayList<NhanVien> dsNV = new ArrayList<NhanVien>();
		ConnectDB.getInstance();
		Connection con = ConnectDB.getConnection();
		String sql = "Select * from NhanVien";
		Statement statement = con.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		
		while (rs.next()) {
			String maNV = rs.getString(1);
			String hoTenNV = rs.getString(2);
			String noiSinh = rs.getString(3);
			LocalDate ngaySinh = rs.getDate(4).toLocalDate();
			String gioiTinh = rs.getString(5);
			String cccd = rs.getString(6);
			String sdt = rs.getString(7);
			String diaChi = rs.getString(8);
			String email = rs.getString(9);
			String trangThai =  rs.getString(10);
			
			NhanVien nv = new NhanVien(maNV, hoTenNV, noiSinh, ngaySinh, gioiTinh, cccd, sdt, diaChi, email, trangThai);
			dsNV.add(nv);
		}
		return dsNV;
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

	public NhanVien timTheoHoTen(String hoTen) throws SQLException {
		ConnectDB.getInstance();
		Connection con = ConnectDB.getConnection();
		String sql = "SELECT maNV, hoTenNV FROM NhanVien WHERE hoTenNV = ?";
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, hoTen);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			return new NhanVien(rs.getString("maNV"));
		}
		return null;
	}
	
	public boolean themNV(NhanVien nv) throws SQLException {
		ConnectDB.getInstance();
		Connection con = ConnectDB.getConnection();
		String sql = "insert into NhanVien values (?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement stmt = con.prepareStatement(sql);
		
		stmt.setString(1, nv.getMaNV());
		stmt.setString(2, nv.getHoTenNV());
		stmt.setString(3, nv.getNoiSinh());
		stmt.setDate(4, Date.valueOf(nv.getNgaySinh()));
		stmt.setString(5, nv.getGioiTinh());
		stmt.setString(6, nv.getCccd());
		stmt.setString(7, nv.getSdt());
		stmt.setString(8, nv.getDiaChi());
		stmt.setString(9, nv.getEmail());
		stmt.setString(10, nv.getTrangThai());
		
		int rs = stmt.executeUpdate();
		if (rs>0) {
			return true;
		}
		return false;
	}

	public String taoMaNVMoi() throws SQLException {
		ConnectDB.getInstance();
		Connection con = ConnectDB.getConnection();
		String nam = String.valueOf(Year.now().getValue());
		String sql = "select maNV from NhanVien where maNV like ?";
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, "NV" + nam + "%");
		ResultSet rs = stmt.executeQuery();
		int soThuTu = 0;
		while (rs.next()) {
			String maNV = rs.getString(1);
			if (maNV != null && maNV.matches("NV" + nam + "\\d{3}")) {
				soThuTu = Math.max(soThuTu, Integer.parseInt(maNV.substring(6)));
			}
		}
		return String.format("NV%s%03d", nam, soThuTu + 1);
	}

	public boolean capNhatNV(NhanVien nv) throws SQLException {
		ConnectDB.getInstance();
		Connection con = ConnectDB.getConnection();
		String sql = "update NhanVien set hoTenNV=?, noiSinh=?, ngaySinh=?, gioiTinh=?, CCCD=?, "
				+ "sdt=?, diaChi=?, email=?, trangThai=? where maNV=?";
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, nv.getHoTenNV());
		stmt.setString(2, nv.getNoiSinh());
		stmt.setDate(3, Date.valueOf(nv.getNgaySinh()));
		stmt.setString(4, nv.getGioiTinh());
		stmt.setString(5, nv.getCccd());
		stmt.setString(6, nv.getSdt());
		stmt.setString(7, nv.getDiaChi());
		stmt.setString(8, nv.getEmail());
		stmt.setString(9, nv.getTrangThai());
		stmt.setString(10, nv.getMaNV());
		return stmt.executeUpdate() > 0;
	}

}
