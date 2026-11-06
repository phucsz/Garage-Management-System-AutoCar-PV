package dao;

import connectDB.ConnectDB;
import entity.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    public List<HoaDon> getAllHoaDon() {
        List<HoaDon> dsHoaDon = new ArrayList<>();
        Connection con = ConnectDB.getConnection();
        String sql = "SELECT h.*, " +
                     "k.hoTenKH, k.noiSinh AS noiSinhKH, k.ngaySinh AS ngaySinhKH, k.cccd AS cccdKH, k.sdt AS sdtKH, k.diaChi AS diaChiKH, k.email AS emailKH " +
                     "FROM HoaDon h " +
                     "LEFT JOIN KhachHang k ON h.maKH = k.maKH";
        
        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String maHoaDon = rs.getString("maHoaDon");
                LocalDateTime ngayLap = rs.getTimestamp("ngayLap") != null ? rs.getTimestamp("ngayLap").toLocalDateTime() : null;
                String hinhThucThanhToan = rs.getString("hinhThucThanhToan");
                String trangThai = rs.getString("trangThai");
                String maPSC = rs.getString("maPSC");
                String maNVThuNgan = rs.getString("maNVThuNgan");
                String maKH = rs.getString("maKH");
                String maChuongTrinh = rs.getString("maChuongTrinh");
                double tienGiamGia = rs.getDouble("tienGiamGia");
                double soTienGiamGia = rs.getDouble("soTienGiamGia");
                double tongTienThanhToan = rs.getDouble("tongTienThanhToan");

                // Khởi tạo Khách hàng đầy đủ thông tin
                KhachHang kh = new KhachHang(maKH, 
                    rs.getString("hoTenKH"), 
                    rs.getString("noiSinhKH"), 
                    rs.getDate("ngaySinhKH") != null ? rs.getDate("ngaySinhKH").toLocalDate() : null, 
                    rs.getString("cccdKH"), 
                    rs.getString("sdtKH"), 
                    rs.getString("diaChiKH"), 
                    rs.getString("emailKH")
                );

                PhieuSuaChua psc = new PhieuSuaChua(maPSC);
                NhanVien nv = new NhanVien(maNVThuNgan);
                ChuongTrinhKhuyenMai km = maChuongTrinh != null ? new ChuongTrinhKhuyenMai(maChuongTrinh) : null;

                HoaDon hd = new HoaDon(maHoaDon, ngayLap, hinhThucThanhToan, trangThai, psc, nv, kh, km, tienGiamGia, soTienGiamGia, tongTienThanhToan);
                dsHoaDon.add(hd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsHoaDon;
    }

    public String getBienSoXeByPSC(String maPSC) {
        String bienSo = "51G-123.45"; // Giá trị mặc định nếu không tìm thấy
        Connection con = ConnectDB.getConnection();
        String sql = "SELECT ptn.bienSoXe FROM PhieuSuaChua psc JOIN PhieuTiepNhan ptn ON psc.maPTN = ptn.maPTN WHERE psc.maPSC = ?";
        
        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maPSC);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String bs = rs.getString("bienSoXe");
                if (bs != null && !bs.trim().isEmpty()) {
                    bienSo = bs;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bienSo;
    }

    public HoaDon getHoaDonByMa(String maHD) {
        Connection con = ConnectDB.getConnection();
        String sql = "SELECT h.*, " +
                     "k.hoTenKH, k.noiSinh AS noiSinhKH, k.ngaySinh AS ngaySinhKH, k.cccd AS cccdKH, k.sdt AS sdtKH, k.diaChi AS diaChiKH, k.email AS emailKH " +
                     "FROM HoaDon h " +
                     "LEFT JOIN KhachHang k ON h.maKH = k.maKH " +
                     "WHERE h.maHoaDon = ?";
        
        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maHD);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String maHoaDon = rs.getString("maHoaDon");
                LocalDateTime ngayLap = rs.getTimestamp("ngayLap") != null ? rs.getTimestamp("ngayLap").toLocalDateTime() : null;
                String hinhThucThanhToan = rs.getString("hinhThucThanhToan");
                String trangThai = rs.getString("trangThai");
                String maPSC = rs.getString("maPSC");
                String maNVThuNgan = rs.getString("maNVThuNgan");
                String maKH = rs.getString("maKH");
                String maChuongTrinh = rs.getString("maChuongTrinh");
                double tienGiamGia = rs.getDouble("tienGiamGia");
                double soTienGiamGia = rs.getDouble("soTienGiamGia");
                double tongTienThanhToan = rs.getDouble("tongTienThanhToan");

                KhachHang kh = new KhachHang(maKH, 
                    rs.getString("hoTenKH"), 
                    rs.getString("noiSinhKH"), 
                    rs.getDate("ngaySinhKH") != null ? rs.getDate("ngaySinhKH").toLocalDate() : null, 
                    rs.getString("cccdKH"), 
                    rs.getString("sdtKH"), 
                    rs.getString("diaChiKH"), 
                    rs.getString("emailKH")
                );

                PhieuSuaChua psc = new PhieuSuaChua(maPSC);
                NhanVien nv = new NhanVien(maNVThuNgan);
                ChuongTrinhKhuyenMai km = maChuongTrinh != null ? new ChuongTrinhKhuyenMai(maChuongTrinh) : null;

                return new HoaDon(maHoaDon, ngayLap, hinhThucThanhToan, trangThai, psc, nv, kh, km, tienGiamGia, soTienGiamGia, tongTienThanhToan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean capNhatThanhToan(String maHoaDon, String hinhThucThanhToan, double soTienGiamGia, double tongTienThanhToan, String trangThai) {
        Connection con = ConnectDB.getConnection();
        String sql = "UPDATE HoaDon SET hinhThucThanhToan = ?, soTienGiamGia = ?, tongTienThanhToan = ?, trangThai = ?, ngayLap = GETDATE() WHERE maHoaDon = ?";
        
        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, hinhThucThanhToan);
            stmt.setDouble(2, soTienGiamGia);
            stmt.setDouble(3, tongTienThanhToan);
            stmt.setString(4, trangThai);
            stmt.setString(5, maHoaDon);
            
            int n = stmt.executeUpdate();
            return n > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Thông tin xe của phiếu sửa chữa, dạng "51G-52451 • Toyota • Ô tô 4 chỗ • Trắng". Rỗng nếu không có. */
    public String getThongTinXeByPSC(String maPSC) {
        Connection con = ConnectDB.getConnection();
        String sql = "SELECT x.bienSoXe, x.hangXe, x.loaiXe, x.mauXe " +
                     "FROM PhieuSuaChua psc " +
                     "JOIN PhieuTiepNhan ptn ON psc.maPTN = ptn.maPTN " +
                     "JOIN Xe x ON ptn.bienSoXe = x.bienSoXe " +
                     "WHERE psc.maPSC = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maPSC);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    StringBuilder sb = new StringBuilder(rs.getString("bienSoXe"));
                    for (String cot : new String[]{"hangXe", "loaiXe", "mauXe"}) {
                        String v = rs.getString(cot);
                        if (v != null && !v.trim().isEmpty()) {
                            sb.append(" • ").append(v.trim());
                        }
                    }
                    return sb.toString();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }
}