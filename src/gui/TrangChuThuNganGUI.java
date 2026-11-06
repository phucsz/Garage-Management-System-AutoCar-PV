package gui;

import static gui.UITheme.*;

import business.HoaDonBUS;
import entity.HoaDon;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/**
 * ============================================================
 * TRANG CHỦ DÀNH CHO THU NGÂN (TÍCH HỢP DỮ LIỆU THẬT)
 * ============================================================
 */
public class TrangChuThuNganGUI extends TrangChuBaseGUI {

    private static final long serialVersionUID = 1L;

    // =========================================================
    // 1. MÀU SẮC
    // =========================================================

    private static final Color MAU_XANH_DAM  = new Color(21, 101, 192);
    private static final Color MAU_XANH      = new Color(30, 136, 229);
    private static final Color MAU_XANH_NHAT = new Color(227, 242, 253);
    private static final Color MAU_CAM       = new Color(255, 145, 0);
    private static final Color MAU_DO        = new Color(211, 47, 47);
    private static final Color MAU_CHU       = new Color(26, 35, 126);
    private static final Color MAU_CHU_TOI   = new Color(55, 71, 110);
    private static final Color MAU_CHU_PHU   = new Color(96, 115, 140);
    private static final Color MAU_NEN       = new Color(239, 243, 247);
    private static final Color MAU_VIEN      = new Color(215, 228, 242);
    private static final Color MAU_HANG_LE   = new Color(247, 249, 252);
    private static final Color MAU_TRANG     = Color.WHITE;

    private static final String FONT = "Segoe UI";
    private static final String FONT_ICON = "Segoe UI Symbol";

    // =========================================================
    // 2. ICON MENU
    // =========================================================

    private static final String ICON_TRANG_CHU = IMG_TRANG_CHU;
    private static final String ICON_THANH_TOAN = "/images/icon_thanhToan.png";
    private static final String ICON_BAO_CAO = IMG_BAO_CAO;

    // =========================================================
    // 3. MENU CHÍNH
    // =========================================================

    private static final MucMenu[] MENU = {
        new MucMenu(KEY_TRANG_CHU, "Trang Chủ", ICON_TRANG_CHU),
        new MucMenu(KEY_THANH_TOAN, "Thanh Toán Hóa Đơn", ICON_THANH_TOAN),
        new MucMenu(KEY_BAO_CAO, "Báo Cáo Thống Kê", ICON_BAO_CAO)
    };

    // =========================================================
    // 4. THÔNG TIN THU NGÂN & BUS
    // =========================================================

    private String tenThuNgan = "Phạm Ngọc Anh";
    private String vaiTro = "Thu ngân";
    private String ngaySinh = "15/09/2006";
    private String gioiTinh = "Nam";

    private HoaDonBUS hoaDonBUS;

    // =========================================================
    // 5. THÀNH PHẦN GIAO DIỆN
    // =========================================================

    private JLabel lblDongHo;
    private JLabel lblNgayHienTai;
    private JTable tblHoaDon;
    private DefaultTableModel modelHoaDonCho;
    private Timer timerDongHo;
    private DanhSachHoaDonGUI pnlDanhSachHD;
    private ThanhToanHoaDonGUI pnlThanhToanHD;

    // Key riêng cho màn hình thanh toán chi tiết
    private static final String KEY_CHI_TIET_THANH_TOAN = "CHI_TIET_THANH_TOAN";

    // Các JLabel hiển thị số liệu thống kê động
    private JLabel lblSoTienDaThu;
    private JLabel lblSoLuongChoThu;
    private JLabel lblSoTienTienMat;
    private JLabel lblSoTienChuyenKhoan;
    private JLabel lblTieuDeNgayThongKe;
    private JLabel lblThongTinSoLuongCho;

    // =========================================================
    // 6. CONSTRUCTOR
    // =========================================================

    public TrangChuThuNganGUI(String hoTen) {
        super(hoTen, "Thu ngân", MENU);

        if (hoTen != null && !hoTen.trim().isEmpty()) {
            tenThuNgan = hoTen;
        }

        this.hoaDonBUS = new HoaDonBUS();

        // =========================================
        // 1. KHỞI TẠO CÁC MÀN HÌNH
        // =========================================

        pnlDanhSachHD = new DanhSachHoaDonGUI();
        pnlThanhToanHD = new ThanhToanHoaDonGUI();

        // =========================================
        // 2. LẮNG NGHE SỰ KIỆN TỪ DANH SÁCH HÓA ĐƠN
        // =========================================

        pnlDanhSachHD.setChuyenTrangListener(
            new DanhSachHoaDonGUI.ChuyenTrangListener() {

                @Override
                public void chuyenDenThanhToan(String maHoaDon) {

                    // Nạp hóa đơn cần thanh toán
                    boolean thanhCong = pnlThanhToanHD.napHoaDon(maHoaDon);

                    if (thanhCong) {
                        hienThi(KEY_CHI_TIET_THANH_TOAN);
                    } else {
                        JOptionPane.showMessageDialog(
                            TrangChuThuNganGUI.this,
                            "Không tìm thấy hóa đơn: " + maHoaDon,
                            "Thông báo",
                            JOptionPane.WARNING_MESSAGE
                        );
                    }
                }

                @Override
                public void quayVeTrangChu() {
                    hienThi(KEY_TRANG_CHU);
                }
            }
        );

        // =========================================
        // 3. LẮNG NGHE SỰ KIỆN TỪ MÀN HÌNH THANH TOÁN
        // =========================================

        pnlThanhToanHD.setThanhToanListener(
            new ThanhToanHoaDonGUI.ThanhToanListener() {

                @Override
                public void quayVeDanhSach() {
                    hienThi(KEY_THANH_TOAN);
                }

                @Override
                public void xacNhanThanhToanThanhCong(String maHoaDon) {

                    // Cập nhật lại dữ liệu trang chủ
                    taiDuLieuTrangChu();

                    // Quay lại danh sách hóa đơn
                    hienThi(KEY_THANH_TOAN);
                }
            }
        );

        // =========================================
        // 4. ĐĂNG KÝ CÁC CARD
        // =========================================

        themCard(KEY_TRANG_CHU, taoTrangChu());

        // Card danh sách hóa đơn
        themCard(KEY_THANH_TOAN, pnlDanhSachHD);

        // Card thanh toán chi tiết
        themCard(KEY_CHI_TIET_THANH_TOAN, pnlThanhToanHD);

        hoanTat();

        khoiDongDongHo();

        taiDuLieuTrangChu();
    }

    // =========================================================
    // 7. TRANG CHỦ
    // =========================================================

    private JPanel taoTrangChu() {
        JPanel pnlTrangChu = new JPanel(new BorderLayout(0, 14));
        pnlTrangChu.setBackground(MAU_NEN);
        pnlTrangChu.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel pnlNoiDung = new JPanel();
        pnlNoiDung.setBackground(MAU_NEN);
        pnlNoiDung.setLayout(new BoxLayout(pnlNoiDung, BoxLayout.Y_AXIS));

        JPanel pnlChucNang = taoKhuVucChucNang();
        JPanel pnlTieuDeTK = taoTieuDeThongKe();
        JPanel pnlThongKe = taoKhuVucThongKe();
        JPanel pnlHoaDon = taoKhuVucHoaDon();
        JPanel pnlGhiChu = taoKhuVucGhiChu();

        pnlNoiDung.add(pnlChucNang);
        pnlNoiDung.add(Box.createVerticalStrut(16));
        pnlNoiDung.add(pnlTieuDeTK);
        pnlNoiDung.add(Box.createVerticalStrut(10));
        pnlNoiDung.add(pnlThongKe);
        pnlNoiDung.add(Box.createVerticalStrut(16));
        pnlNoiDung.add(pnlHoaDon);
        pnlNoiDung.add(Box.createVerticalStrut(12));
        pnlNoiDung.add(pnlGhiChu);

        gioiHanChieuCao(pnlChucNang);
        gioiHanChieuCao(pnlTieuDeTK);
        gioiHanChieuCao(pnlThongKe);
        gioiHanChieuCao(pnlGhiChu);

        pnlTrangChu.add(taoKhuVucThongTin(), BorderLayout.NORTH);
        pnlTrangChu.add(pnlNoiDung, BorderLayout.CENTER);

        return pnlTrangChu;
    }

    // =========================================================
    // 8. TIÊU ĐỀ + THẺ THÔNG TIN CÁ NHÂN
    // =========================================================

    private JPanel taoKhuVucThongTin() {
        JPanel pnlChinh = new JPanel(new BorderLayout(0, 14));
        pnlChinh.setBackground(MAU_NEN);

        JPanel pnlTieuDe = new JPanel();
        pnlTieuDe.setLayout(new BoxLayout(pnlTieuDe, BoxLayout.Y_AXIS));
        pnlTieuDe.setBackground(MAU_NEN);

        pnlTieuDe.add(taoLabel("Trang chủ – Thu ngân", 24, Font.BOLD, MAU_CHU));
        pnlTieuDe.add(Box.createVerticalStrut(4));
        pnlTieuDe.add(taoLabel("Theo dõi hóa đơn, số tiền đã thu và các giao dịch cần thanh toán trong ngày.", 12, Font.PLAIN, MAU_CHU_PHU));

        PanelBoTron pnlCaNhan = new PanelBoTron(14, MAU_TRANG, MAU_VIEN);
        pnlCaNhan.setLayout(new BorderLayout(16, 0));
        pnlCaNhan.setBorder(new EmptyBorder(16, 14, 16, 22));

        AvatarTron avatar = new AvatarTron(62, MAU_XANH_DAM);
        JPanel pnlAvatar = new JPanel(new GridBagLayout());
        pnlAvatar.setOpaque(false);
        pnlAvatar.add(avatar);

        JPanel pnlChiTiet = new JPanel();
        pnlChiTiet.setLayout(new BoxLayout(pnlChiTiet, BoxLayout.Y_AXIS));
        pnlChiTiet.setOpaque(false);

        pnlChiTiet.add(taoDongThongTin("♙", tenThuNgan, 16, Font.BOLD, MAU_CHU));
        pnlChiTiet.add(Box.createVerticalStrut(4));
        pnlChiTiet.add(taoDongThongTin("◉", "Vai trò: " + vaiTro, 12, Font.PLAIN, MAU_CHU_TOI));
        pnlChiTiet.add(Box.createVerticalStrut(3));
        pnlChiTiet.add(taoDongThongTin("▣", "Ngày sinh: " + ngaySinh, 12, Font.PLAIN, MAU_CHU_TOI));
        pnlChiTiet.add(Box.createVerticalStrut(3));
        pnlChiTiet.add(taoDongThongTin("⊗", "Giới tính: " + gioiTinh, 12, Font.PLAIN, MAU_CHU_TOI));

        JPanel pnlDongHo = new JPanel();
        pnlDongHo.setLayout(new BoxLayout(pnlDongHo, BoxLayout.Y_AXIS));
        pnlDongHo.setOpaque(false);

        lblDongHo = taoLabel("00:00:00", 28, Font.BOLD, MAU_DO);
        lblDongHo.setAlignmentX(Component.RIGHT_ALIGNMENT);

        lblNgayHienTai = taoLabel("", 11, Font.PLAIN, MAU_XANH_DAM);
        lblNgayHienTai.setAlignmentX(Component.RIGHT_ALIGNMENT);

        pnlDongHo.add(Box.createVerticalGlue());
        pnlDongHo.add(lblDongHo);
        pnlDongHo.add(lblNgayHienTai);
        pnlDongHo.add(Box.createVerticalGlue());

        pnlCaNhan.add(pnlAvatar, BorderLayout.WEST);
        pnlCaNhan.add(pnlChiTiet, BorderLayout.CENTER);
        pnlCaNhan.add(pnlDongHo, BorderLayout.EAST);

        pnlChinh.add(pnlTieuDe, BorderLayout.NORTH);
        pnlChinh.add(pnlCaNhan, BorderLayout.CENTER);

        return pnlChinh;
    }

    private JPanel taoDongThongTin(String icon, String noiDung, int coChu, int kieu, Color mau) {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnl.setOpaque(false);
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font(FONT_ICON, Font.PLAIN, coChu));
        lblIcon.setForeground(MAU_XANH_DAM);
        lblIcon.setBorder(new EmptyBorder(0, 0, 0, 8));

        pnl.add(lblIcon);
        pnl.add(taoLabel(noiDung, coChu, kieu, mau));
        return pnl;
    }

    // =========================================================
    // 9. BA THẺ CHỨC NĂNG NHANH
    // =========================================================

    private JPanel taoKhuVucChucNang() {
        JPanel pnl = new JPanel(new GridLayout(1, 3, 14, 0));
        pnl.setBackground(MAU_NEN);
        pnl.setPreferredSize(new Dimension(0, 110));

        pnl.add(taoTheChucNang("▤", "Danh sách hóa đơn",
                "Tra cứu hóa đơn và lọc theo trạng thái thanh toán.",
                MAU_XANH_DAM, this::moDanhSachHoaDon));

        pnl.add(taoTheChucNang("💳", "Thanh toán hóa đơn",
                "Chuyển đến màn hình quản lý và thu tiền hóa đơn.",
                MAU_XANH, this::moThanhToanHoaDon));

        pnl.add(taoTheChucNang("✓", "Hóa đơn đã thanh toán",
                "Xem lại các hóa đơn đã hoàn thành trong hệ thống.",
                MAU_CAM, this::moHoaDonDaThanhToan));

        return pnl;
    }

    private JPanel taoTheChucNang(String icon, String tieuDe, String moTa,
                                Color mauNen, Runnable hanhDong) {
        PanelBoTron pnlThe = new PanelBoTron(14, mauNen, null);
        pnlThe.setLayout(new BorderLayout(0, 14));
        pnlThe.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel pnlDau = new JPanel(new BorderLayout(12, 0));
        pnlDau.setOpaque(false);

        pnlDau.add(new IconTron(36, new Color(255, 255, 255, 55), MAU_TRANG, icon),
                BorderLayout.WEST);

        JLabel lblTieuDe = taoLabel(tieuDe, 14, Font.BOLD, MAU_TRANG);
        pnlDau.add(lblTieuDe, BorderLayout.CENTER);

        JLabel lblMoTa = taoLabel(moTa, 11, Font.PLAIN, MAU_TRANG);
        lblMoTa.setVerticalAlignment(SwingConstants.TOP);

        pnlThe.add(pnlDau, BorderLayout.NORTH);
        pnlThe.add(lblMoTa, BorderLayout.CENTER);

        pnlThe.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnlThe.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hanhDong.run();
            }
        });

        return pnlThe;
    }

    // =========================================================
    // 10. TIÊU ĐỀ THỐNG KÊ
    // =========================================================

    private JPanel taoTieuDeThongKe() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(MAU_NEN);
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        lblTieuDeNgayThongKe = taoLabel("Tổng hợp thu ngân • " + LocalDate.now().format(dtf), 16, Font.BOLD, MAU_CHU);
        pnl.add(lblTieuDeNgayThongKe, BorderLayout.WEST);
        
        return pnl;
    }

    // =========================================================
    // 11. BỐN THẺ THỐNG KÊ
    // =========================================================

    private JPanel taoKhuVucThongKe() {
        JPanel pnl = new JPanel(new GridLayout(1, 4, 10, 0));
        pnl.setBackground(MAU_NEN);
        pnl.setPreferredSize(new Dimension(0, 116));

        lblSoTienDaThu = taoLabel("0 đ", 24, Font.BOLD, MAU_CHU);
        lblSoLuongChoThu = taoLabel("0", 26, Font.BOLD, MAU_CHU);
        lblSoTienTienMat = taoLabel("0 đ", 24, Font.BOLD, MAU_CHU);
        lblSoTienChuyenKhoan = taoLabel("0 đ", 24, Font.BOLD, MAU_CHU);

        pnl.add(taoTheThongKe("💰", "Đã thu (Tổng tiền)", lblSoTienDaThu, "Tổng doanh thu hóa đơn"));
        pnl.add(taoTheThongKe("⏳", "Chờ thanh toán", lblSoLuongChoThu, "Hóa đơn chưa thu tiền"));
        pnl.add(taoTheThongKe("💵", "Thanh toán Tiền mặt", lblSoTienTienMat, "Giao dịch tiền mặt"));
        pnl.add(taoTheThongKe("🏦", "Thanh toán Chuyển khoản", lblSoTienChuyenKhoan, "Giao dịch chuyển khoản"));

        return pnl;
    }

    private JPanel taoTheThongKe(String icon, String tieuDe, JLabel lblGiaTri, String moTa) {
        PanelBoTron pnlThe = new PanelBoTron(12, MAU_TRANG, MAU_VIEN);
        pnlThe.setLayout(new BorderLayout(0, 4));
        pnlThe.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel pnlDau = new JPanel(new BorderLayout(10, 0));
        pnlDau.setOpaque(false);
        pnlDau.add(new IconTron(34, MAU_XANH_NHAT, MAU_XANH_DAM, icon), BorderLayout.WEST);
        pnlDau.add(taoLabel(tieuDe, 12, Font.BOLD, MAU_CHU_TOI), BorderLayout.CENTER);

        pnlThe.add(pnlDau, BorderLayout.NORTH);
        pnlThe.add(lblGiaTri, BorderLayout.CENTER);
        pnlThe.add(taoLabel(moTa, 10, Font.PLAIN, MAU_CHU_PHU), BorderLayout.SOUTH);

        return pnlThe;
    }

    // =========================================================
    // 12. BẢNG HÓA ĐƠN CHỜ THANH TOÁN
    // =========================================================

    private JPanel taoKhuVucHoaDon() {
        PanelBoTron pnlHoaDon = new PanelBoTron(12, MAU_TRANG, MAU_VIEN);
        pnlHoaDon.setLayout(new BorderLayout());

        JPanel pnlTieuDe = new JPanel(new BorderLayout());
        pnlTieuDe.setBackground(MAU_XANH_NHAT);
        pnlTieuDe.setBorder(new EmptyBorder(14, 16, 8, 16));

        pnlTieuDe.add(taoLabel("Hóa đơn chờ thanh toán", 13, Font.BOLD, MAU_XANH_DAM),
                BorderLayout.WEST);
        
        lblThongTinSoLuongCho = taoLabel("0 hóa đơn đang chờ", 10, Font.PLAIN, MAU_CHU_PHU);
        pnlTieuDe.add(lblThongTinSoLuongCho, BorderLayout.EAST);

        String[] tenCot = {
            "Mã hóa đơn", "Ngày lập", "Khách hàng / Xe",
            "Phải thanh toán", "Trạng thái", "Thao tác"
        };

        modelHoaDonCho = new DefaultTableModel(null, tenCot) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblHoaDon = new JTable(modelHoaDonCho);
        tblHoaDon.setRowHeight(50);
        tblHoaDon.setFont(new Font(FONT, Font.PLAIN, 11));
        tblHoaDon.setShowGrid(false);
        tblHoaDon.setShowHorizontalLines(true);
        tblHoaDon.setShowVerticalLines(false);
        tblHoaDon.setGridColor(MAU_VIEN);
        tblHoaDon.setIntercellSpacing(new Dimension(0, 1));
        tblHoaDon.setSelectionBackground(MAU_XANH_NHAT);
        tblHoaDon.setSelectionForeground(MAU_CHU);
        tblHoaDon.setFocusable(false);
        tblHoaDon.setFillsViewportHeight(true);

        int[] doRong = {140, 100, 280, 150, 130, 110};
        for (int i = 0; i < doRong.length; i++) {
            tblHoaDon.getColumnModel().getColumn(i).setPreferredWidth(doRong[i]);
        }

        JTableHeader header = tblHoaDon.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setPreferredSize(new Dimension(0, 34));
        header.setDefaultRenderer(new RendererTieuDeCot());

        TableColumnModel cm = tblHoaDon.getColumnModel();
        cm.getColumn(0).setCellRenderer(new RendererChu(true, MAU_XANH_DAM, "", false));
        cm.getColumn(1).setCellRenderer(new RendererChu(false, MAU_CHU_TOI, "", false));
        cm.getColumn(2).setCellRenderer(new RendererChu(false, MAU_CHU_TOI, "", true));
        cm.getColumn(3).setCellRenderer(new RendererChu(true, MAU_CHU, "", false));
        cm.getColumn(4).setCellRenderer(new RendererPill());
        cm.getColumn(5).setCellRenderer(new RendererChu(true, MAU_XANH_DAM, "→ ", false));

        tblHoaDon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int hang = tblHoaDon.rowAtPoint(e.getPoint());
                int cot = tblHoaDon.columnAtPoint(e.getPoint());

                if (hang >= 0 && cot == 5) {
                    thanhToanHoaDon(hang);
                }
            }
        });

        tblHoaDon.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int hang = tblHoaDon.rowAtPoint(e.getPoint());
                int cot = tblHoaDon.columnAtPoint(e.getPoint());
                tblHoaDon.setCursor((hang >= 0 && cot == 5) ? new Cursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
            }
        });

        JScrollPane scrBang = new JScrollPane(tblHoaDon);
        scrBang.setBorder(null);
        scrBang.getViewport().setBackground(MAU_TRANG);
        scrBang.setColumnHeaderView(header);
        scrBang.getColumnHeader().setBackground(MAU_XANH_NHAT);

        pnlHoaDon.add(pnlTieuDe, BorderLayout.NORTH);
        pnlHoaDon.add(scrBang, BorderLayout.CENTER);

        return pnlHoaDon;
    }

    // =========================================================
    // 13. KHU VỰC GHI CHÚ
    // =========================================================

    private JPanel taoKhuVucGhiChu() {
        JPanel pnlGhiChu = new JPanel(new BorderLayout(12, 0));
        pnlGhiChu.setBackground(MAU_XANH_NHAT);
        pnlGhiChu.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel lblIcon = taoLabel("ⓘ", 14, Font.BOLD, MAU_XANH_DAM);
        JLabel lblNoiDung = taoLabel(
                "Kiểm tra thông tin khách hàng và số tiền thực thu trước khi xác nhận. Chỉ bàn giao xe sau khi hóa đơn đã thanh toán.",
                11, Font.PLAIN, MAU_XANH_DAM);

        pnlGhiChu.add(lblIcon, BorderLayout.WEST);
        pnlGhiChu.add(lblNoiDung, BorderLayout.CENTER);

        return pnlGhiChu;
    }

    private Color mauHang(boolean isSelected, int row) {
        if (isSelected) return MAU_XANH_NHAT;
        return (row % 2 == 0) ? MAU_TRANG : MAU_HANG_LE;
    }

    // =========================================================
    // 14. RENDERER TIÊU ĐỀ CỘT
    // =========================================================

    private class RendererTieuDeCot extends JLabel implements TableCellRenderer {
        private static final long serialVersionUID = 1L;

        public RendererTieuDeCot() {
            setOpaque(true);
            setBackground(MAU_XANH_NHAT);
            setForeground(MAU_CHU_TOI);
            setFont(new Font(FONT, Font.BOLD, 11));
            setBorder(new EmptyBorder(0, 16, 0, 8));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            return this;
        }
    }

    // =========================================================
    // 15. RENDERER CHỮ
    // =========================================================

    private class RendererChu extends JLabel implements TableCellRenderer {
        private static final long serialVersionUID = 1L;
        private final String tienTo;
        private final boolean nhieuDong;

        public RendererChu(boolean dam, Color mau, String tienTo, boolean nhieuDong) {
            this.tienTo = tienTo;
            this.nhieuDong = nhieuDong;
            setOpaque(true);
            setForeground(mau);
            setFont(new Font(FONT, dam ? Font.BOLD : Font.PLAIN, 11));
            setBorder(new EmptyBorder(0, 16, 0, 8));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            String text = value == null ? "" : value.toString();
            if (nhieuDong) {
                setText("<html>" + text.replace("\n", "<br>") + "</html>");
            } else {
                setText(tienTo + text);
            }
            setBackground(mauHang(isSelected, row));
            return this;
        }
    }

    // =========================================================
    // 16. RENDERER BADGE (PILL)
    // =========================================================

    private class RendererPill extends JPanel implements TableCellRenderer {
        private static final long serialVersionUID = 1L;
        private final PillLabel pill = new PillLabel();

        public RendererPill() {
            super(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.WEST;
            c.insets = new Insets(0, 16, 0, 0);
            add(pill, c);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            String noiDung = value == null ? "" : value.toString();
            setBackground(mauHang(isSelected, row));
            pill.setText(noiDung);

            if (noiDung.equals("Chờ thanh toán")) {
                pill.setMau(new Color(230, 119, 0), new Color(255, 236, 209));
            } else if (noiDung.equals("Đã thanh toán")) {
                pill.setMau(new Color(46, 125, 50), new Color(232, 245, 233));
            } else {
                pill.setMau(MAU_XANH_DAM, MAU_XANH_NHAT);
            }

            return this;
        }
    }

    // =========================================================
    // 17. ĐỒNG HỒ
    // =========================================================

    private void khoiDongDongHo() {
        timerDongHo = new Timer(1000, e -> capNhatDongHo());
        timerDongHo.start();
        capNhatDongHo();
    }

    private void capNhatDongHo() {
        DateTimeFormatter dinhDangGio = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter dinhDangNgay = DateTimeFormatter.ofPattern(
                "EEEE, dd/MM/yyyy", new Locale("vi", "VN"));

        lblDongHo.setText(LocalTime.now().format(dinhDangGio));
        lblNgayHienTai.setText(LocalDate.now().format(dinhDangNgay));
    }

    // =========================================================
    // 18. HÀM HỖ TRỢ
    // =========================================================

    private JLabel taoLabel(String noiDung, int coChu, int kieuChu, Color mauChu) {
        JLabel lbl = new JLabel(noiDung);
        lbl.setFont(new Font(FONT, kieuChu, coChu));
        lbl.setForeground(mauChu);
        return lbl;
    }

    private void gioiHanChieuCao(JComponent c) {
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
    }

    // =========================================================
    // 19. CÁC THÀNH PHẦN VẼ TÙY CHỈNH
    // =========================================================

    private static class PanelBoTron extends JPanel {
        private static final long serialVersionUID = 1L;
        private final int banKinh;
        private final Color mauNen;
        private final Color mauVien;

        PanelBoTron(int banKinh, Color mauNen, Color mauVien) {
            this.banKinh = banKinh;
            this.mauNen = mauNen;
            this.mauVien = mauVien;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(mauNen);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, banKinh * 2, banKinh * 2);
            g2.dispose();
        }

        @Override
        protected void paintChildren(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.clip(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1,
                    banKinh * 2, banKinh * 2));
            super.paintChildren(g2);
            g2.dispose();
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            if (mauVien != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(mauVien);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, banKinh * 2, banKinh * 2);
                g2.dispose();
            }
        }
    }

    private static class IconTron extends JComponent {
        private static final long serialVersionUID = 1L;
        private final int kichThuoc;
        private final Color mauNen;
        private final Color mauChu;
        private final String ky_tu;

        IconTron(int kichThuoc, Color mauNen, Color mauChu, String kyTu) {
            this.kichThuoc = kichThuoc;
            this.mauNen = mauNen;
            this.mauChu = mauChu;
            this.ky_tu = kyTu;
            setPreferredSize(new Dimension(kichThuoc, kichThuoc));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int y = (getHeight() - kichThuoc) / 2;

            g2.setColor(mauNen);
            g2.fillOval(0, y, kichThuoc - 1, kichThuoc - 1);

            g2.setColor(mauChu);
            g2.setFont(new Font(FONT_ICON, Font.PLAIN, kichThuoc / 2));
            FontMetrics fm = g2.getFontMetrics();
            int x = (kichThuoc - fm.stringWidth(ky_tu)) / 2;
            int yChu = y + (kichThuoc - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(ky_tu, x, yChu);

            g2.dispose();
        }
    }

    private static class AvatarTron extends JComponent {
        private static final long serialVersionUID = 1L;
        private final int kichThuoc;
        private final Color mauNen;

        AvatarTron(int kichThuoc, Color mauNen) {
            this.kichThuoc = kichThuoc;
            this.mauNen = mauNen;
            setPreferredSize(new Dimension(kichThuoc, kichThuoc));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(mauNen);
            g2.fillOval(0, 0, kichThuoc - 1, kichThuoc - 1);

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int dauR = kichThuoc / 5;
            int cx = kichThuoc / 2;

            g2.drawOval(cx - dauR, kichThuoc / 4, dauR * 2, dauR * 2);

            int vaiR = kichThuoc / 3;
            g2.drawArc(cx - vaiR, kichThuoc / 2 + 2, vaiR * 2, vaiR * 2, 0, 180);

            g2.dispose();
        }
    }

    private static class PillLabel extends JLabel {
        private static final long serialVersionUID = 1L;
        private Color mauNen = Color.WHITE;

        PillLabel() {
            setOpaque(false);
            setFont(new Font(FONT, Font.BOLD, 10));
            setBorder(new EmptyBorder(4, 10, 4, 10));
        }

        void setMau(Color chu, Color nen) {
            setForeground(chu);
            this.mauNen = nen;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(mauNen);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2.dispose();

            super.paintComponent(g);
        }
    }

    // =========================================================
    // 20. HÀM TẢI DỮ LIỆU THẬT & ĐIỀU HƯỚNG
    // =========================================================

    private void taiDuLieuTrangChu() {
        List<HoaDon> dsHD = hoaDonBUS.layDanhSachHoaDon();

        double tongDaThu = 0;
        int demChoThanhToan = 0;
        double tongTienMat = 0;
        double tongChuyenKhoan = 0;

        modelHoaDonCho.setRowCount(0);
        DateTimeFormatter dtfTime = DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy");

        for (HoaDon hd : dsHD) {
            String trangThai = hd.getTrangThai() != null ? hd.getTrangThai() : "";
            boolean daThanhToan = trangThai.equalsIgnoreCase("Đã thanh toán");

            if (daThanhToan) {
                tongDaThu += hd.getTongTienThanhToan();
                String hinhThuc = hd.getHinhThucThanhToan() != null ? hd.getHinhThucThanhToan() : "";
                if (hinhThuc.equalsIgnoreCase("Tiền mặt")) {
                    tongTienMat += hd.getTongTienThanhToan();
                } else if (hinhThuc.equalsIgnoreCase("Chuyển khoản")) {
                    tongChuyenKhoan += hd.getTongTienThanhToan();
                }
            } else {
                demChoThanhToan++;
                
                String maHD = hd.getMaHoaDon() != null ? hd.getMaHoaDon() : "";
                String gioLap = hd.getNgayLap() != null ? hd.getNgayLap().format(dtfTime) : "N/A";
                
                String tenKH = (hd.getKhachHang() != null && hd.getKhachHang().getHoTenKH() != null) ? hd.getKhachHang().getHoTenKH() : "Khách lẻ";
                String maPSC = (hd.getPhieuSuaChua() != null && hd.getPhieuSuaChua().getMaPSC() != null) ? hd.getPhieuSuaChua().getMaPSC() : "";
                String bienSo = hoaDonBUS.getBienSoXeByPSC(maPSC);
                
                String thongTinKHXe = tenKH + "\n" + bienSo;
                String tongTienStr = String.format("%,.0f đ", hd.getTongTienThanhToan());

                modelHoaDonCho.addRow(new Object[]{
                    maHD,
                    gioLap,
                    thongTinKHXe,
                    tongTienStr,
                    "Chờ thanh toán",
                    "Thanh toán"
                });
            }
        }

        // Cập nhật lên giao diện thống kê
        lblSoTienDaThu.setText(String.format("%,.0f đ", tongDaThu));
        lblSoLuongChoThu.setText(String.valueOf(demChoThanhToan));
        lblSoTienTienMat.setText(String.format("%,.0f đ", tongTienMat));
        lblSoTienChuyenKhoan.setText(String.format("%,.0f đ", tongChuyenKhoan));
        lblThongTinSoLuongCho.setText(demChoThanhToan + " hóa đơn đang chờ");
    }

    private void moDanhSachHoaDon() {
        hienThi(KEY_THANH_TOAN);
    }

    private void moThanhToanHoaDon() {
        hienThi(KEY_THANH_TOAN);
    }

    private void moHoaDonDaThanhToan() {
        hienThi(KEY_THANH_TOAN);
    }

    private void thanhToanHoaDon(int hang) {

        // Lấy mã hóa đơn được chọn
        String maHoaDon = modelHoaDonCho.getValueAt(hang, 0).toString();

        // Nạp hóa đơn vào màn hình thanh toán
        boolean thanhCong = pnlThanhToanHD.napHoaDon(maHoaDon);

        if (thanhCong) {

            // Chuyển sang màn hình thanh toán chi tiết
            hienThi(KEY_CHI_TIET_THANH_TOAN);

        } else {

            JOptionPane.showMessageDialog(
                this,
                "Không tìm thấy hóa đơn: " + maHoaDon,
                "Thông báo",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }

    // =========================================================
    // 21. MAIN
    // =========================================================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TrangChuThuNganGUI giaoDien = new TrangChuThuNganGUI("Phạm Ngọc Anh");
            giaoDien.setVisible(true);
        });
    }
}