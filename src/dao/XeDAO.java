package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.KhachHang;
import entity.Xe;

public class XeDAO {
	private Connection getConnection() throws SQLException {
		Connection con = ConnectDB.getConnection();
		if (con == null || con.isClosed()) {
			ConnectDB.getInstance().connect();
			con = ConnectDB.getConnection();
		}
		return con;
	}
	
	public ArrayList<Xe> getXeTheoKhachHang(String maKH) throws SQLException {
		ArrayList<Xe> ds = new ArrayList<>();
		String sql = "SELECT DISTINCT x.* FROM Xe x JOIN PhieuTiepNhan ptn ON x.bienSoXe = ptn.bienSoXe WHERE ptn.maKH = ?";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			stmt.setString(1, maKH);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					ds.add(new Xe(rs.getString("bienSoXe"), rs.getString("loaiXe"), nullableInt(rs, "congSuat"),
							rs.getString("hangXe"), rs.getString("mauXe"), nullableInt(rs, "soThangDaDi")));
				}
			}
		}
		return ds;
	}

	public boolean capNhatXe(Xe xe) throws SQLException {
		String sql = "UPDATE Xe SET loaiXe=?, hangXe=?, mauXe=?, soThangDaDi=? "
				+ "WHERE bienSoXe=?";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			stmt.setString(1, xe.getLoaiXe());
			stmt.setString(2, xe.getHangXe());
			stmt.setString(3, xe.getMauXe());
			stmt.setInt(4, xe.getSoThangDaDi());
			stmt.setString(5, xe.getBienSoXe());
			return stmt.executeUpdate() > 0;
		}
	}

	public boolean themXe(Xe xe) throws SQLException {
		String sql = "INSERT INTO Xe (bienSoXe, loaiXe, congSuat, hangXe, mauXe, soThangDaDi) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			stmt.setString(1, xe.getBienSoXe());
			stmt.setString(2, xe.getLoaiXe());
			if (xe.getCongSuat() > 0) {
				stmt.setInt(3, xe.getCongSuat());
			} else {
				stmt.setNull(3, java.sql.Types.INTEGER);
			}
			stmt.setString(4, xe.getHangXe());
			stmt.setString(5, xe.getMauXe());
			stmt.setInt(6, xe.getSoThangDaDi());
			return stmt.executeUpdate() > 0;
		}
	}
	
	public Xe timTheoBienSo(String bienSo) throws SQLException {
		String sql = "SELECT * FROM Xe where bienSoXe = ?";
		PreparedStatement stmt = getConnection().prepareStatement(sql); 
		stmt.setString(1,bienSo);
		ResultSet rs = stmt.executeQuery();
		Xe xe = null;
		if (rs.next()) {
			String loaiXe = rs.getString(2);
			int congSuat = rs.getInt(3);
			String hangXe = rs.getString(4);
			String mauXe = rs.getString(5);
			int soThangDaDi = rs.getInt(6);
			
			xe = new Xe(bienSo, loaiXe, congSuat, hangXe, mauXe, soThangDaDi);
		}
		return xe;
	}

	public Integer laySoKmGanNhat(String bienSo) throws SQLException {
		String sql = "SELECT TOP 1 soKmDaDi FROM PhieuTiepNhan "
				+ "WHERE bienSoXe = ? ORDER BY ngayTiepNhan DESC";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			stmt.setString(1, bienSo);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					double km = rs.getDouble("soKmDaDi");
					return rs.wasNull() ? null : (int) km;
				}
			}
		}
		return null;
	}

	private static int nullableInt(ResultSet rs, String column) throws SQLException {
		int value = rs.getInt(column);
		return rs.wasNull() ? 0 : value;
	}

	
}
