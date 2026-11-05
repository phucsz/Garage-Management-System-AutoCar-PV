package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.DanhMucPhuTung;
import entity.NhaCungCap;
import entity.PhuTung;

public class PhuTungDAO {
	public ArrayList<PhuTung> getAll() throws SQLException {
		ArrayList<PhuTung> result = new ArrayList<>();
		String sql = "SELECT maPhuTung, tenPhuTung, maDanhMuc, donViTinh, donGiaHienTai, soLuongTon, "
				+ "ngaySanXuat, maNCC FROM PhuTung ORDER BY tenPhuTung";
		try (PreparedStatement stmt = connection().prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				result.add(new PhuTung(rs.getString("maPhuTung"), rs.getString("tenPhuTung"),
						new DanhMucPhuTung(rs.getString("maDanhMuc")), rs.getString("donViTinh"),
						rs.getDouble("donGiaHienTai"), rs.getInt("soLuongTon"),
						rs.getDate("ngaySanXuat") == null ? null : rs.getDate("ngaySanXuat").toLocalDate(),
						new NhaCungCap(rs.getString("maNCC"))));
			}
		}
		return result;
	}

	public boolean insert(PhuTung value) throws SQLException {
		String sql = "INSERT INTO PhuTung(maPhuTung, tenPhuTung, maDanhMuc, donViTinh, donGiaHienTai, "
				+ "soLuongTon, ngaySanXuat, maNCC) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = connection().prepareStatement(sql)) {
			set(stmt, value);
			return stmt.executeUpdate() == 1;
		}
	}

	public boolean update(PhuTung value) throws SQLException {
		String sql = "UPDATE PhuTung SET tenPhuTung=?, maDanhMuc=?, donViTinh=?, donGiaHienTai=?, "
				+ "soLuongTon=?, ngaySanXuat=?, maNCC=? WHERE maPhuTung=?";
		try (PreparedStatement stmt = connection().prepareStatement(sql)) {
			stmt.setString(1, value.getTenPhuTung());
			if (value.getDanhMucPhuTung() == null) stmt.setNull(2, java.sql.Types.VARCHAR);
			else stmt.setString(2, value.getDanhMucPhuTung().getMaDanhMuc());
			stmt.setString(3, value.getDonViTinh());
			stmt.setDouble(4, value.getDonGiaHienTai());
			stmt.setInt(5, value.getSoLuongTon());
			if (value.getNgaySanXuat() == null) stmt.setNull(6, java.sql.Types.DATE);
			else stmt.setDate(6, Date.valueOf(value.getNgaySanXuat()));
			stmt.setString(7, value.getNhaCungCap().getMaNCC());
			stmt.setString(8, value.getMaPhuTung());
			return stmt.executeUpdate() == 1;
		}
	}

	public boolean delete(String id) throws SQLException {
		try (PreparedStatement stmt = connection().prepareStatement("DELETE FROM PhuTung WHERE maPhuTung=?")) {
			stmt.setString(1, id);
			return stmt.executeUpdate() == 1;
		}
	}

	private void set(PreparedStatement stmt, PhuTung value) throws SQLException {
		stmt.setString(1, value.getMaPhuTung());
		stmt.setString(2, value.getTenPhuTung());
		if (value.getDanhMucPhuTung() == null) stmt.setNull(3, java.sql.Types.VARCHAR);
		else stmt.setString(3, value.getDanhMucPhuTung().getMaDanhMuc());
		stmt.setString(4, value.getDonViTinh());
		stmt.setDouble(5, value.getDonGiaHienTai());
		stmt.setInt(6, value.getSoLuongTon());
		if (value.getNgaySanXuat() == null) stmt.setNull(7, java.sql.Types.DATE);
		else stmt.setDate(7, Date.valueOf(value.getNgaySanXuat()));
		stmt.setString(8, value.getNhaCungCap().getMaNCC());
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
