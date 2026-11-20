package dao;

import connectDB.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lấy danh sách phụ tùng / vật tư đã dùng của một phiếu sửa chữa.
 *
 * Bảng dùng: ChiTietPhuTung(maPSC, maPhuTung, soLuongDuKien, donGia, thueSuatVAT,
 *                          soLuongDaDung, soLuongCapPhat)
 *            PhuTung(maPhuTung, tenPhuTung, maDanhMuc, donViTinh, donGiaHienTai, thueSuatVAT)
 * donGia lấy từ ChiTietPhuTung (đơn giá tại thời điểm sửa chữa), không lấy donGiaHienTai.
 */
public class ChiTietPhuTungDAO {

    /** Một dòng phụ tùng để hiển thị lên bảng thanh toán. */
    public static class DongPhuTung {
        public final String maPhuTung;
        public final String tenPhuTung;
        public final String donViTinh;
        public final int soLuong;
        public final double donGia;
        public final String ghiChu;
        public final int soLuongDuKien;
        public final int soLuongDaDung;
        public final int soLuongCapPhat;

        public DongPhuTung(String maPhuTung, String tenPhuTung, String donViTinh,
                           int soLuong, double donGia, String ghiChu,
                           int soLuongDuKien, int soLuongDaDung, int soLuongCapPhat) {
            this.maPhuTung = maPhuTung;
            this.tenPhuTung = tenPhuTung;
            this.donViTinh = donViTinh;
            this.soLuong = soLuong;
            this.donGia = donGia;
            this.ghiChu = ghiChu;
            this.soLuongDuKien = soLuongDuKien;
            this.soLuongDaDung = soLuongDaDung;
            this.soLuongCapPhat = soLuongCapPhat;
        }

        public double thanhTien() {
            return soLuong * donGia;
        }
    }

    private static final String SQL =
            "SELECT ct.maPhuTung, pt.tenPhuTung, pt.donViTinh, ct.soLuongDuKien, ct.soLuongDaDung, "
            + "ct.soLuongCapPhat, ct.donGia, ct.thueSuatVAT " +
            "FROM ChiTietPhuTung ct " +
            "JOIN PhuTung pt ON ct.maPhuTung = pt.maPhuTung " +
            "WHERE ct.maPSC = ? " +
            "ORDER BY ct.maPhuTung";

    public List<DongPhuTung> getByMaPSC(String maPSC) {
        List<DongPhuTung> ds = new ArrayList<>();
        Connection con = ConnectDB.getConnection();

        try (PreparedStatement stmt = con.prepareStatement(SQL)) {
            stmt.setString(1, maPSC);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ds.add(new DongPhuTung(
                            rs.getString("maPhuTung"),
                            rs.getString("tenPhuTung"),
                            rs.getString("donViTinh"),
                            rs.getInt("soLuongDaDung"),
                            rs.getDouble("donGia"),
                            null,
                            rs.getInt("soLuongDuKien"),
                            rs.getInt("soLuongDaDung"),
                            rs.getInt("soLuongCapPhat")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
}