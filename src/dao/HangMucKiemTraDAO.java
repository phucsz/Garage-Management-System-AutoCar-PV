package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.HangMucKiemTra;

public class HangMucKiemTraDAO {

	public ArrayList<HangMucKiemTra> timTheoLoai(String loaiHangMuc, String tuKhoa) throws SQLException {
		ArrayList<HangMucKiemTra> ds = new ArrayList<>();
		String sql = "SELECT maHangMuc, tenHangMuc, loaiHangMuc FROM HangMucKiemTra "
				+ "WHERE loaiHangMuc = ? AND LOWER(tenHangMuc) LIKE LOWER(?) ORDER BY tenHangMuc";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			stmt.setString(1, loaiHangMuc);
			stmt.setString(2, "%" + (tuKhoa == null ? "" : tuKhoa.trim()) + "%");
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					ds.add(new HangMucKiemTra(rs.getString("maHangMuc"), rs.getString("tenHangMuc"),
							rs.getString("loaiHangMuc")));
				}
			}
		}
		return ds;
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
