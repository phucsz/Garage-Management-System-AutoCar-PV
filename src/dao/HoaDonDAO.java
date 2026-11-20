package dao;

import connectDB.ConnectDB;
import entity.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    private void napChiTietPhieuSuaChua(PhieuSuaChua phieu) {
        if (phieu == null || phieu.getMaPSC() == null) {
            return;
        }
        Connection con = ConnectDB.getConnection();
        ArrayList<ChiTietDichVu> dichVu = new ArrayList<>();
        ArrayList<ChiTietPhuTung> phuTung = new ArrayList<>();

        String sqlDichVu = "SELECT ctdv.maDichVu, ctdv.soLuong, ctdv.donGia, ctdv.thueSuatVAT, "
                + "ctdv.ghiChu, ctdv.thoiGianCapNhat, ctdv.trangThai, dv.tenDichVu, dv.loaiDichVu, "
                + "dv.donGiaHienTai, dv.thueSuatVAT AS vatDichVu "
                + "FROM ChiTietDichVu ctdv "
                + "JOIN DichVu dv ON dv.maDichVu = ctdv.maDichVu "
                + "WHERE ctdv.maPSC = ?";
        try (PreparedStatement stmt = con.prepareStatement(sqlDichVu)) {
            stmt.setString(1, phieu.getMaPSC());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DichVu dv = new DichVu(rs.getString("maDichVu"), rs.getString("tenDichVu"),
                            rs.getString("loaiDichVu"), rs.getDouble("donGiaHienTai"),
                            rs.getDouble("vatDichVu"));
                    Timestamp capNhat = rs.getTimestamp("thoiGianCapNhat");
                    dichVu.add(new ChiTietDichVu(phieu, dv, rs.getInt("soLuong"), rs.getDouble("donGia"),
                            rs.getDouble("thueSuatVAT"), rs.getString("ghiChu"),
                            capNhat == null ? null : capNhat.toLocalDateTime(), rs.getString("trangThai")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sqlPhuTung = "SELECT ctpt.maPhuTung, ctpt.soLuongDuKien, ctpt.donGia, ctpt.thueSuatVAT, "
                + "ctpt.soLuongDaDung, ctpt.soLuongCapPhat, pt.tenPhuTung, pt.donViTinh, "
                + "pt.donGiaHienTai, pt.thueSuatVAT AS vatPhuTung "
                + "FROM ChiTietPhuTung ctpt "
                + "JOIN PhuTung pt ON pt.maPhuTung = ctpt.maPhuTung "
                + "WHERE ctpt.maPSC = ?";
        try (PreparedStatement stmt = con.prepareStatement(sqlPhuTung)) {
            stmt.setString(1, phieu.getMaPSC());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PhuTung pt = new PhuTung(rs.getString("maPhuTung"), rs.getString("tenPhuTung"),
                            rs.getString("donViTinh"), rs.getDouble("donGiaHienTai"),
                            rs.getDouble("vatPhuTung"), null, null);
                    phuTung.add(new ChiTietPhuTung(phieu, pt, rs.getInt("soLuongDaDung"),
                            rs.getDouble("donGia"), rs.getDouble("thueSuatVAT"),
                            rs.getInt("soLuongDaDung"), rs.getInt("soLuongCapPhat")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        phieu.setChiTietDichVu(dichVu);
        phieu.setChiTietPhuTung(phuTung);
    }

    public List<HoaDon> getAllHoaDon() {
        List<HoaDon> dsHoaDon = new ArrayList<>();
        Connection con = ConnectDB.getConnection();
        String sql = "SELECT h.*, " +
                 "k.hoTenKH, k.noiSinh AS noiSinhKH, k.ngaySinh AS ngaySinhKH, k.cccd AS cccdKH, k.sdt AS sdtKH, k.diaChi AS diaChiKH, k.email AS emailKH, " +
                 "COALESCE((SELECT SUM(ctdv.soLuong * ctdv.donGia) FROM ChiTietDichVu ctdv WHERE ctdv.maPSC = h.maPSC), 0) AS tongTienDichVu, " +
                 "COALESCE((SELECT SUM(ctpt.soLuongDaDung * ctpt.donGia) FROM ChiTietPhuTung ctpt WHERE ctpt.maPSC = h.maPSC), 0) AS tongTienPhuTung, " +
                 "km.tenChuongTrinh, km.ngayBatDau, km.ngayKetThuc, km.loaiKhuyenMai, km.giaTriKhuyenMai " +
                     "FROM HoaDon h " +
                     "LEFT JOIN KhachHang k ON h.maKH = k.maKH " +
                     "LEFT JOIN ChuongTrinhKhuyenMai km ON h.maChuongTrinh = km.maChuongTrinh";
        
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
                napChiTietPhieuSuaChua(psc);
                NhanVien nv = new NhanVien(maNVThuNgan);
                ChuongTrinhKhuyenMai km = maChuongTrinh != null ? new ChuongTrinhKhuyenMai(
                    maChuongTrinh, rs.getString("tenChuongTrinh"),
                    rs.getDate("ngayBatDau") != null ? rs.getDate("ngayBatDau").toLocalDate() : null,
                    rs.getDate("ngayKetThuc") != null ? rs.getDate("ngayKetThuc").toLocalDate() : null,
                    rs.getString("loaiKhuyenMai"), rs.getDouble("giaTriKhuyenMai")) : null;

                HoaDon hd = new HoaDon(maHoaDon, ngayLap, hinhThucThanhToan, trangThai, psc, nv, kh, km);
                dsHoaDon.add(hd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsHoaDon;
    }

    public String getBienSoXeByPSC(String maPSC) {
        String bienSo = "";
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
                 "k.hoTenKH, k.noiSinh AS noiSinhKH, k.ngaySinh AS ngaySinhKH, k.cccd AS cccdKH, k.sdt AS sdtKH, k.diaChi AS diaChiKH, k.email AS emailKH, " +
                 "COALESCE((SELECT SUM(ctdv.soLuong * ctdv.donGia) FROM ChiTietDichVu ctdv WHERE ctdv.maPSC = h.maPSC), 0) AS tongTienDichVu, " +
                 "COALESCE((SELECT SUM(ctpt.soLuongDaDung * ctpt.donGia) FROM ChiTietPhuTung ctpt WHERE ctpt.maPSC = h.maPSC), 0) AS tongTienPhuTung, " +
                 "km.tenChuongTrinh, km.ngayBatDau, km.ngayKetThuc, km.loaiKhuyenMai, km.giaTriKhuyenMai " +
                     "FROM HoaDon h " +
                     "LEFT JOIN KhachHang k ON h.maKH = k.maKH " +
                     "LEFT JOIN ChuongTrinhKhuyenMai km ON h.maChuongTrinh = km.maChuongTrinh " +
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
                napChiTietPhieuSuaChua(psc);
                NhanVien nv = new NhanVien(maNVThuNgan);
                ChuongTrinhKhuyenMai km = maChuongTrinh != null ? new ChuongTrinhKhuyenMai(
                    maChuongTrinh, rs.getString("tenChuongTrinh"),
                    rs.getDate("ngayBatDau") != null ? rs.getDate("ngayBatDau").toLocalDate() : null,
                    rs.getDate("ngayKetThuc") != null ? rs.getDate("ngayKetThuc").toLocalDate() : null,
                    rs.getString("loaiKhuyenMai"), rs.getDouble("giaTriKhuyenMai")) : null;

                return new HoaDon(maHoaDon, ngayLap, hinhThucThanhToan, trangThai, psc, nv, kh, km);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean capNhatThanhToan(String maHoaDon, String hinhThucThanhToan, String trangThai) {
        Connection con = ConnectDB.getConnection();
        String sql = "UPDATE HoaDon SET hinhThucThanhToan = ?, trangThai = ?, ngayLap = GETDATE() WHERE maHoaDon = ?";
        
        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, hinhThucThanhToan);
            stmt.setString(2, trangThai);
            stmt.setString(3, maHoaDon);
            
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