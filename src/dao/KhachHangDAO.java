package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.KhachHang;

public class KhachHangDAO {

	public ArrayList<KhachHang> timKiem(String hoTen, String sdt) throws SQLException {
		ArrayList<KhachHang> ds = new ArrayList<>();
		String sql = "SELECT maKH, hoTenKH, noiSinh, ngaySinh, CCCD, sdt, diaChi, email "
				+ "FROM KhachHang WHERE hoTenKH LIKE ? AND sdt LIKE ? ORDER BY maKH";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			stmt.setString(1, "%" + (hoTen == null ? "" : hoTen.trim()) + "%");
			stmt.setString(2, "%" + (sdt == null ? "" : sdt.trim()) + "%");
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					ds.add(new KhachHang(rs.getString("maKH"), rs.getString("hoTenKH"), rs.getString("noiSinh"),
							rs.getDate("ngaySinh") == null ? null : rs.getDate("ngaySinh").toLocalDate(),
							rs.getString("CCCD"), rs.getString("sdt"), rs.getString("diaChi"), rs.getString("email")));
				}
			}
		}
		return ds;
	}

	public boolean themKH(KhachHang kh) throws SQLException {
		String sql = "INSERT INTO KhachHang (maKH, hoTenKH, noiSinh, ngaySinh, CCCD, sdt, diaChi, email) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			ganGiaTri(stmt, kh);
			return stmt.executeUpdate() > 0;
		}
	}

	public boolean capNhatKH(KhachHang kh) throws SQLException {
		String sql = "UPDATE KhachHang SET hoTenKH=?, sdt=?, diaChi=? WHERE maKH=?";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			stmt.setString(1, kh.getHoTenKH());
			stmt.setString(2, kh.getSdt());
			stmt.setString(3, kh.getDiaChi());
			stmt.setString(4, kh.getMaKH());
			return stmt.executeUpdate() > 0;
		}
	}

	public boolean xoaKH(String maKH) throws SQLException {
		String sql = "DELETE FROM KhachHang WHERE maKH=?";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			stmt.setString(1, maKH);
			return stmt.executeUpdate() > 0;
		}
	}

	public String taoMaKHMoi() throws SQLException {
		String sql = "SELECT maKH FROM KhachHang WHERE maKH LIKE 'KH%'";
		int soThuTu = 0;
		try (PreparedStatement stmt = getConnection().prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String ma = rs.getString(1);
				if (ma != null && ma.matches("KH\\d+")) {
					soThuTu = Math.max(soThuTu, Integer.parseInt(ma.substring(2)));
				}
			}
		}
		return String.format("KH%03d", soThuTu + 1);
	}

	private void ganGiaTri(PreparedStatement stmt, KhachHang kh) throws SQLException {
		stmt.setString(1, kh.getMaKH());
		stmt.setString(2, kh.getHoTenKH());
		stmt.setString(3, kh.getNoiSinh());
		if (kh.getNgaySinh() == null) {
			stmt.setDate(4, null);
		} else {
			stmt.setDate(4, java.sql.Date.valueOf(kh.getNgaySinh()));
		}
		stmt.setString(5, kh.getCccd());
		stmt.setString(6, kh.getSdt());
		stmt.setString(7, kh.getDiaChi());
		stmt.setString(8, kh.getEmail());
	}

	private Connection getConnection() throws SQLException {
		Connection con = ConnectDB.getConnection();
		if (con == null || con.isClosed()) {
			ConnectDB.getInstance().connect();
			con = ConnectDB.getConnection();
		}
		return con;
	}
}
