package business;

import dao.ChiTietDichVuDAO;
import dao.ChiTietPhuTungDAO;
import dao.HoaDonDAO;
import entity.HoaDon;

import java.util.List;

public class HoaDonBUS {

    private HoaDonDAO hoaDonDAO;
    private ChiTietDichVuDAO chiTietDichVuDAO;
    private ChiTietPhuTungDAO chiTietPhuTungDAO;

    public HoaDonBUS() {
        this.hoaDonDAO = new HoaDonDAO();
        this.chiTietDichVuDAO = new ChiTietDichVuDAO();
        this.chiTietPhuTungDAO = new ChiTietPhuTungDAO();
    }

    public List<HoaDon> layDanhSachHoaDon() {
        return hoaDonDAO.getAllHoaDon();
    }

    public String getBienSoXeByPSC(String maPSC) {
        return hoaDonDAO.getBienSoXeByPSC(maPSC);
    }

    /** "Biển số • Hãng • Loại xe • Màu" của phiếu sửa chữa. */
    public String getThongTinXeByPSC(String maPSC) {
        return hoaDonDAO.getThongTinXeByPSC(maPSC);
    }

    public HoaDon timHoaDonTheoMa(String maHD) {
        if (maHD == null || maHD.trim().isEmpty()) {
            return null;
        }
        return hoaDonDAO.getHoaDonByMa(maHD.trim());
    }

    /** Danh sách dịch vụ (tiền công) của phiếu sửa chữa. */
    public List<ChiTietDichVuDAO.DongDichVu> layChiTietDichVu(String maPSC) {
        if (maPSC == null || maPSC.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }
        return chiTietDichVuDAO.getByMaPSC(maPSC.trim());
    }

    /** Danh sách phụ tùng / vật tư của phiếu sửa chữa. */
    public List<ChiTietPhuTungDAO.DongPhuTung> layChiTietPhuTung(String maPSC) {
        if (maPSC == null || maPSC.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }
        return chiTietPhuTungDAO.getByMaPSC(maPSC.trim());
    }

    public boolean xuLyThanhToan(String maHoaDon, String hinhThucThanhToan, double tienKhachDua, double tongTienThanhToan, double soTienGiamGia) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            return false;
        }

        HoaDon hd = hoaDonDAO.getHoaDonByMa(maHoaDon);
        if (hd == null) {
            return false;
        }

        if (hd.getTrangThai() != null && hd.getTrangThai().contains("Đã thanh toán")) {
            return false; 
        }

        if ("Tiền mặt".equalsIgnoreCase(hinhThucThanhToan)) {
            if (tienKhachDua < tongTienThanhToan) {
                return false; 
            }
        }

        String trangThaiMoi = "Đã thanh toán";
        return hoaDonDAO.capNhatThanhToan(maHoaDon, hinhThucThanhToan, soTienGiamGia, tongTienThanhToan, trangThaiMoi);
    }
}