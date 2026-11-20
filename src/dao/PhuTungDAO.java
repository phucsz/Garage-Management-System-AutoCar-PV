package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import connectDB.ConnectDB;
import entity.DanhMucPhuTung;
import entity.LoPhuTung;
import entity.PhuTung;

public class PhuTungDAO {
	public ArrayList<PhuTung> getAll() throws SQLException {
		return timKiem(null, null);
	}

	public ArrayList<PhuTung> timKiem(String tuKhoa, String maDanhMuc) throws SQLException {
		LinkedHashMap<String, PhuTung> theoMa = new LinkedHashMap<>();
		StringBuilder sql = new StringBuilder(
				"SELECT pt.maPhuTung, pt.tenPhuTung, pt.maDanhMuc, dm.tenDanhMuc, "
				+ "pt.donViTinh, pt.donGiaHienTai, pt.thueSuatVAT, "
				+ "lo.maLo, lo.ngaySanXuat, lo.hanSuDung, lo.donGiaNhap, lo.soLuongNhap, lo.soLuongTon "
				+ "FROM PhuTung pt "
				+ "LEFT JOIN DanhMucPhuTung dm ON dm.maDanhMuc = pt.maDanhMuc "
				+ "LEFT JOIN LoPhuTung lo ON lo.maPhuTung = pt.maPhuTung "
				+ "WHERE 1=1");
		ArrayList<Object> thamSo = new ArrayList<>();
		if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
			sql.append(" AND (pt.maPhuTung LIKE ? OR pt.tenPhuTung LIKE ?)");
			String like = "%" + tuKhoa.trim() + "%";
			thamSo.add(like);
			thamSo.add(like);
		}
		if (maDanhMuc != null && !maDanhMuc.trim().isEmpty()) {
			sql.append(" AND pt.maDanhMuc = ?");
			thamSo.add(maDanhMuc.trim());
		}
		sql.append(" ORDER BY pt.tenPhuTung, lo.maLo");

		try (PreparedStatement stmt = connection().prepareStatement(sql.toString())) {
			for (int i = 0; i < thamSo.size(); i++) {
				stmt.setObject(i + 1, thamSo.get(i));
			}
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String ma = rs.getString("maPhuTung");
					PhuTung phuTung = theoMa.get(ma);
					if (phuTung == null) {
						DanhMucPhuTung danhMuc = rs.getString("maDanhMuc") == null
								? null : new DanhMucPhuTung(rs.getString("maDanhMuc"), rs.getString("tenDanhMuc"));
						phuTung = new PhuTung(ma, rs.getString("tenPhuTung"), rs.getString("donViTinh"),
								rs.getDouble("donGiaHienTai"), rs.getDouble("thueSuatVAT"), danhMuc,
								new ArrayList<>());
						theoMa.put(ma, phuTung);
					}
					String maLo = rs.getString("maLo");
					if (maLo != null) {
						LoPhuTung lo = new LoPhuTung(
								maLo,
								rs.getDate("ngaySanXuat") == null ? null : rs.getDate("ngaySanXuat").toLocalDate(),
								rs.getDate("hanSuDung") == null ? null : rs.getDate("hanSuDung").toLocalDate(),
								rs.getDouble("donGiaNhap"),
								rs.getInt("soLuongNhap"),
								rs.getInt("soLuongTon"),
								phuTung,
								null);
						phuTung.getLoPhuTung().add(lo);
					}
				}
			}
		}
		return new ArrayList<>(theoMa.values());
	}

	public ArrayList<DanhMucPhuTung> layDanhSachDanhMuc() throws SQLException {
		ArrayList<DanhMucPhuTung> result = new ArrayList<>();
		String sql = "SELECT maDanhMuc, tenDanhMuc FROM DanhMucPhuTung ORDER BY tenDanhMuc";
		try (PreparedStatement stmt = connection().prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				result.add(new DanhMucPhuTung(rs.getString("maDanhMuc"), rs.getString("tenDanhMuc")));
			}
		}
		return result;
	}

	public boolean insert(PhuTung value) throws SQLException {
		String sql = "INSERT INTO PhuTung(maPhuTung, tenPhuTung, donViTinh, donGiaHienTai, thueSuatVAT, maDanhMuc) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = connection().prepareStatement(sql)) {
			set(stmt, value);
			return stmt.executeUpdate() == 1;
		}
	}

	public boolean update(PhuTung value) throws SQLException {
		String sql = "UPDATE PhuTung SET tenPhuTung=?, maDanhMuc=?, donViTinh=?, donGiaHienTai=? "
				+ "WHERE maPhuTung=?";
		try (PreparedStatement stmt = connection().prepareStatement(sql)) {
			stmt.setString(1, value.getTenPhuTung());
			if (value.getDanhMucPhuTung() == null) stmt.setNull(2, java.sql.Types.VARCHAR);
			else stmt.setString(2, value.getDanhMucPhuTung().getMaDanhMuc());
			stmt.setString(3, value.getDonViTinh());
			stmt.setDouble(4, value.getDonGiaHienTai());
			stmt.setString(5, value.getMaPhuTung());
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
		stmt.setString(3, value.getDonViTinh());
		stmt.setDouble(4, value.getDonGiaHienTai());
		stmt.setDouble(5, value.getThueSuatVAT());
		if (value.getDanhMucPhuTung() == null) stmt.setNull(6, java.sql.Types.VARCHAR);
		else stmt.setString(6, value.getDanhMucPhuTung().getMaDanhMuc());
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
