package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.ChuongTrinhKhuyenMai;

public class ChuongTrinhKhuyenMaiDAO {
	public ArrayList<ChuongTrinhKhuyenMai> getAll() throws SQLException {
		ArrayList<ChuongTrinhKhuyenMai> result = new ArrayList<>();
		String sql = "SELECT maChuongTrinh, tenChuongTrinh, ngayBatDau, ngayKetThuc, loaiKhuyenMai, "
				+ "giaTriKhuyenMai FROM ChuongTrinhKhuyenMai ORDER BY ngayBatDau DESC";
		try (PreparedStatement stmt = connection().prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				result.add(new ChuongTrinhKhuyenMai(rs.getString("maChuongTrinh"), rs.getString("tenChuongTrinh"),
						rs.getDate("ngayBatDau").toLocalDate(), rs.getDate("ngayKetThuc").toLocalDate(),
						rs.getString("loaiKhuyenMai"), rs.getDouble("giaTriKhuyenMai")));
			}
		}
		return result;
	}

	public boolean insert(ChuongTrinhKhuyenMai value) throws SQLException {
		String sql = "INSERT INTO ChuongTrinhKhuyenMai(maChuongTrinh, tenChuongTrinh, ngayBatDau, ngayKetThuc, "
				+ "loaiKhuyenMai, giaTriKhuyenMai) VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = connection().prepareStatement(sql)) {
			set(stmt, value);
			return stmt.executeUpdate() == 1;
		}
	}

	public boolean update(ChuongTrinhKhuyenMai value) throws SQLException {
		String sql = "UPDATE ChuongTrinhKhuyenMai SET tenChuongTrinh=?, ngayBatDau=?, ngayKetThuc=?, "
				+ "loaiKhuyenMai=?, giaTriKhuyenMai=? WHERE maChuongTrinh=?";
		try (PreparedStatement stmt = connection().prepareStatement(sql)) {
			stmt.setString(1, value.getTenChuongTrinh());
			stmt.setDate(2, Date.valueOf(value.getNgayBatDau()));
			stmt.setDate(3, Date.valueOf(value.getNgayKetThuc()));
			stmt.setString(4, value.getLoaiKhuyenMai());
			stmt.setDouble(5, value.getGiaTriKhuyenMai());
			stmt.setString(6, value.getMaChuongTrinh());
			return stmt.executeUpdate() == 1;
		}
	}

	public boolean delete(String id) throws SQLException {
		try (PreparedStatement stmt = connection().prepareStatement(
				"DELETE FROM ChuongTrinhKhuyenMai WHERE maChuongTrinh=?")) {
			stmt.setString(1, id);
			return stmt.executeUpdate() == 1;
		}
	}

	private void set(PreparedStatement stmt, ChuongTrinhKhuyenMai value) throws SQLException {
		stmt.setString(1, value.getMaChuongTrinh());
		stmt.setString(2, value.getTenChuongTrinh());
		stmt.setDate(3, Date.valueOf(value.getNgayBatDau()));
		stmt.setDate(4, Date.valueOf(value.getNgayKetThuc()));
		stmt.setString(5, value.getLoaiKhuyenMai());
		stmt.setDouble(6, value.getGiaTriKhuyenMai());
	}

	private Connection connection() throws SQLException {
		Connection con = ConnectDB.getConnection();
		if (con == null || con.isClosed()) {
			ConnectDB.getInstance().connect();
			con = ConnectDB.getConnection();
		}
		return con;
	}
}
