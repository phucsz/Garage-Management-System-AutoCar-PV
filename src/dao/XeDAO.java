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
		String sql = "SELECT bienSoXe, loaiXe, congSuat, hangXe, mauXe, soThangDaDi, soKmHienTai, maKH "
				+ "FROM Xe WHERE maKH=? ORDER BY bienSoXe";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			stmt.setString(1, maKH);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					ds.add(new Xe(rs.getString("bienSoXe"), rs.getString("loaiXe"), nullableInt(rs, "congSuat"),
							rs.getString("hangXe"), rs.getString("mauXe"), nullableInt(rs, "soThangDaDi"),
							rs.getDouble("soKmHienTai"), new KhachHang(rs.getString("maKH"))));
				}
			}
		}
		return ds;
	}

	public boolean capNhatXe(Xe xe) throws SQLException {
		String sql = "UPDATE Xe SET loaiXe=?, hangXe=?, mauXe=?, soThangDaDi=?, soKmHienTai=? "
				+ "WHERE bienSoXe=? AND maKH=?";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			stmt.setString(1, xe.getLoaiXe());
			stmt.setString(2, xe.getHangXe());
			stmt.setString(3, xe.getMauXe());
			stmt.setInt(4, xe.getSoThangDaDi());
			stmt.setDouble(5, xe.getSoKmHienTai());
			stmt.setString(6, xe.getBienSoXe());
			stmt.setString(7, xe.getKhachHang().getMaKH());
			return stmt.executeUpdate() > 0;
		}
	}

	public boolean themXe(Xe xe) throws SQLException {
		String sql = "INSERT INTO Xe (bienSoXe, loaiXe, congSuat, hangXe, mauXe, soThangDaDi, soKmHienTai, maKH) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
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
			stmt.setDouble(7, xe.getSoKmHienTai());
			stmt.setString(8, xe.getKhachHang().getMaKH());
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
			double soKmHienTai = rs.getDouble(7);
			KhachHang kh = new KhachHang(rs.getString(8));
			
			xe = new Xe(bienSo, loaiXe, congSuat, hangXe, mauXe, soThangDaDi, soKmHienTai, kh);
		}
		return xe;
	}

	private static int nullableInt(ResultSet rs, String column) throws SQLException {
		int value = rs.getInt(column);
		return rs.wasNull() ? 0 : value;
	}

	
}
