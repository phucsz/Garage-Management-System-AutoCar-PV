package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.PhieuTiepNhan;
import entity.ChiTietHangMuc;
import entity.KhachHang;
import entity.NhanVien;
import entity.Xe;

public class PhieuTiepNhanDAO {
	private Connection getConnection() throws SQLException {
		Connection con = ConnectDB.getConnection();
		if (con == null || con.isClosed()) {
			ConnectDB.getInstance().connect();
			con = ConnectDB.getConnection();
		}
		return con;
	}
	
	public int getSoLuongPTNTrongNgay(LocalDate ngay) throws SQLException {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyy");
		int dem = 0;
		String sql = "SELECT count(maPTN) FROM PhieuTiepNhan where maPTN like ?";
		PreparedStatement stmt = getConnection().prepareStatement(sql); 
		stmt.setString(1, "PTN" + ngay.format(dtf) + "%");
		
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) dem = rs.getInt(1);
		return dem;
	}

	public boolean themPTN(PhieuTiepNhan ptn) throws SQLException {
		String sql = "INSERT INTO PhieuTiepNhan "
				+ "(maPTN, ngayTiepNhan, yeuCauKhachHang, soKmDaDi, maNVLapPhieu, bienSoXe, maKH) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			stmt.setString(1, ptn.getMaPTN());
			stmt.setTimestamp(2, java.sql.Timestamp.valueOf(ptn.getNgayTiepNhan()));
			stmt.setString(3, ptn.getYeuCauKhachHang());
			stmt.setDouble(4, ptn.getSoKmDaDi());
			stmt.setString(5, ptn.getNhanVienLapPhieu().getMaNV());
			stmt.setString(6, ptn.getXe().getBienSoXe());
			stmt.setString(7, ptn.getKhachHang().getMaKH());
			return stmt.executeUpdate() > 0;
		}
	}

	public boolean themChiTietHangMuc(String maPTN, ChiTietHangMuc chiTiet) throws SQLException {
		String sql = "INSERT INTO ChiTietHangMuc (maPTN, maHangMuc, tinhTrang) VALUES (?, ?, ?)";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			stmt.setString(1, maPTN);
			stmt.setString(2, chiTiet.getHangMuc().getMaHangMuc());
			stmt.setString(3, chiTiet.getTinhTrang());
			return stmt.executeUpdate() > 0;
		}
	}
		public ArrayList<PhieuTiepNhan> timKiem(String maPTN, String bienSoXe, String thongTinKhachHang,
				LocalDate tuNgay, LocalDate denNgay, String maNV) throws SQLException {
			ArrayList<PhieuTiepNhan> ds = new ArrayList<>();
			StringBuilder sql = new StringBuilder(
					"SELECT p.maPTN, p.ngayTiepNhan, p.yeuCauKhachHang, p.soKmDaDi, "
					+ "kh.maKH, kh.hoTenKH, kh.sdt, kh.diaChi, "
					+ "x.bienSoXe, x.hangXe, nv.maNV, nv.hoTenNV "
					+ "FROM PhieuTiepNhan p "
					+ "INNER JOIN KhachHang kh ON kh.maKH = p.maKH "
					+ "INNER JOIN Xe x ON x.bienSoXe = p.bienSoXe "
					+ "INNER JOIN NhanVien nv ON nv.maNV = p.maNVLapPhieu "
					+ "WHERE 1 = 1");
			ArrayList<Object> thamSo = new ArrayList<>();
			if (maPTN != null && !maPTN.trim().isEmpty()) {
				sql.append(" AND p.maPTN LIKE ?");
				thamSo.add("%" + maPTN.trim() + "%");
			}
			if (bienSoXe != null && !bienSoXe.trim().isEmpty()) {
				sql.append(" AND p.bienSoXe LIKE ?");
				thamSo.add("%" + bienSoXe.trim() + "%");
			}
			if (thongTinKhachHang != null && !thongTinKhachHang.trim().isEmpty()) {
				sql.append(" AND (kh.hoTenKH LIKE ? OR kh.sdt LIKE ?)");
				thamSo.add("%" + thongTinKhachHang.trim() + "%");
				thamSo.add("%" + thongTinKhachHang.trim() + "%");
			}
			if (tuNgay != null) {
				sql.append(" AND p.ngayTiepNhan >= ?");
				thamSo.add(java.sql.Date.valueOf(tuNgay));
			}
			if (denNgay != null) {
				sql.append(" AND p.ngayTiepNhan < ?");
				thamSo.add(java.sql.Date.valueOf(denNgay.plusDays(1)));
			}
			if (maNV != null && !maNV.trim().isEmpty()) {
				sql.append(" AND nv.maNV LIKE ?");
				thamSo.add("%" + maNV.trim() + "%");
			}
			sql.append(" ORDER BY p.ngayTiepNhan DESC, p.maPTN DESC");

			try (PreparedStatement stmt = getConnection().prepareStatement(sql.toString())) {
				for (int i = 0; i < thamSo.size(); i++) {
					stmt.setObject(i + 1, thamSo.get(i));
				}
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						KhachHang kh = new KhachHang(rs.getString("maKH"));
						kh.setHoTenKH(rs.getString("hoTenKH"));
						kh.setSdt(rs.getString("sdt"));
						kh.setDiaChi(rs.getString("diaChi"));
						Xe xe = new Xe(rs.getString("bienSoXe"));
						xe.setHangXe(rs.getString("hangXe"));
						NhanVien nv = new NhanVien(rs.getString("maNV"));
						nv.setHoTenNV(rs.getString("hoTenNV"));
						ds.add(new PhieuTiepNhan(rs.getString("maPTN"),
								rs.getTimestamp("ngayTiepNhan").toLocalDateTime(),
								rs.getString("yeuCauKhachHang"), rs.getDouble("soKmDaDi"), nv, xe, kh));
					}
				}
			}
			return ds;
		}
	}
	
	
