package dao;

import connectDB.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lấy danh sách dịch vụ (tiền công) của một phiếu sửa chữa.
 *
 * Bảng dùng: ChiTietDichVu(maPSC, maDichVu, soLuong, donGia, ghiChu)
 *            DichVu(maDichVu, tenDichVu, loaiDichVu, donGiaHienTai)
 * donGia lấy từ ChiTietDichVu (đơn giá tại thời điểm sửa chữa), không lấy donGiaHienTai.
 */
public class ChiTietDichVuDAO {

    /** Một dòng dịch vụ để hiển thị lên bảng thanh toán. */
    public static class DongDichVu {
        public final String maDichVu;
        public final String tenDichVu;
        public final int soLuong;
        public final double donGia;
        public final String ghiChu;

        public DongDichVu(String maDichVu, String tenDichVu, int soLuong, double donGia, String ghiChu) {
            this.maDichVu = maDichVu;
            this.tenDichVu = tenDichVu;
            this.soLuong = soLuong;
            this.donGia = donGia;
            this.ghiChu = ghiChu;
        }

        public double thanhTien() {
            return soLuong * donGia;
        }
    }

    private static final String SQL =
            "SELECT ct.maDichVu, dv.tenDichVu, ct.soLuong, ct.donGia, ct.ghiChu " +
            "FROM ChiTietDichVu ct " +
            "JOIN DichVu dv ON ct.maDichVu = dv.maDichVu " +
            "WHERE ct.maPSC = ? " +
            "ORDER BY ct.maDichVu";

    public List<DongDichVu> getByMaPSC(String maPSC) {
        List<DongDichVu> ds = new ArrayList<>();
        Connection con = ConnectDB.getConnection();

        try (PreparedStatement stmt = con.prepareStatement(SQL)) {
            stmt.setString(1, maPSC);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ds.add(new DongDichVu(
                            rs.getString("maDichVu"),
                            rs.getString("tenDichVu"),
                            rs.getInt("soLuong"),
                            rs.getDouble("donGia"),
                            rs.getString("ghiChu")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
}