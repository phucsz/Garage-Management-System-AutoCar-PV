package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.DichVu;

public class DichVuDAO {
	public ArrayList<DichVu> getAll() throws SQLException {
		ArrayList<DichVu> result = new ArrayList<>();
		String sql = "SELECT maDichVu, tenDichVu, loaiDichVu, donGiaHienTai FROM DichVu ORDER BY tenDichVu";
		try (PreparedStatement stmt = connection().prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				result.add(new DichVu(rs.getString("maDichVu"), rs.getString("tenDichVu"),
						rs.getString("loaiDichVu"), rs.getDouble("donGiaHienTai")));
			}
		}
		return result;
	}

	public ArrayList<DichVu> timKiem(String maDichVu, String loaiDichVu) throws SQLException {
		ArrayList<DichVu> result = new ArrayList<>();
		StringBuilder sql = new StringBuilder(
				"SELECT maDichVu, tenDichVu, loaiDichVu, donGiaHienTai FROM DichVu WHERE maDichVu LIKE ?");
		if (loaiDichVu != null && !loaiDichVu.trim().isEmpty()) {
			sql.append(" AND loaiDichVu = ?");
		}
		sql.append(" ORDER BY tenDichVu");

		try (PreparedStatement stmt = connection().prepareStatement(sql.toString())) {
			stmt.setString(1, "%" + (maDichVu == null ? "" : maDichVu.trim()) + "%");
			if (loaiDichVu != null && !loaiDichVu.trim().isEmpty()) {
				stmt.setString(2, loaiDichVu.trim());
			}
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					result.add(new DichVu(rs.getString("maDichVu"), rs.getString("tenDichVu"),
							rs.getString("loaiDichVu"), rs.getDouble("donGiaHienTai")));
				}
			}
		}
		return result;
	}

	public ArrayList<String> layDanhSachLoai() throws SQLException {
		ArrayList<String> result = new ArrayList<>();
		String sql = "SELECT DISTINCT loaiDichVu FROM DichVu "
				+ "WHERE loaiDichVu IS NOT NULL AND LTRIM(RTRIM(loaiDichVu)) <> '' ORDER BY loaiDichVu";
		try (PreparedStatement stmt = connection().prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				result.add(rs.getString(1));
			}
		}
		return result;
	}

	public int getSoLuongDV() throws SQLException {
		int dem = 0;
		String sql = "SELECT count(maDichVu) FROM DichVu";
		PreparedStatement stmt = connection().prepareStatement(sql);
		
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) dem = rs.getInt(1);
		return dem;
	}

	public boolean insert(DichVu value) throws SQLException {
		String sql = "INSERT INTO DichVu(maDichVu, tenDichVu, loaiDichVu, donGiaHienTai) VALUES (?, ?, ?, ?)";
		try (PreparedStatement stmt = connection().prepareStatement(sql)) {
			set(stmt, value);
			return stmt.executeUpdate() == 1;
		}
	}

	public boolean update(DichVu value) throws SQLException {
		String sql = "UPDATE DichVu SET tenDichVu=?, loaiDichVu=?, donGiaHienTai=? WHERE maDichVu=?";
		try (PreparedStatement stmt = connection().prepareStatement(sql)) {
			stmt.setString(1, value.getTenDichVu());
			stmt.setString(2, value.getLoaiDichVu());
			stmt.setDouble(3, value.getDonGiaHienTai());
			stmt.setString(4, value.getMaDichVu());
			return stmt.executeUpdate() == 1;
		}
	}

	public boolean delete(String id) throws SQLException {
		try (PreparedStatement stmt = connection().prepareStatement("DELETE FROM DichVu WHERE maDichVu=?")) {
			stmt.setString(1, id);
			return stmt.executeUpdate() == 1;
		}
	}

	private void set(PreparedStatement stmt, DichVu value) throws SQLException {
		stmt.setString(1, value.getMaDichVu());
		stmt.setString(2, value.getTenDichVu());
		stmt.setString(3, value.getLoaiDichVu());
		stmt.setDouble(4, value.getDonGiaHienTai());
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
