package gui;

import static gui.UITheme.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/**
 * ============================================================
 * TRANG CHỦ DÀNH CHO KỸ THUẬT VIÊN
 * ============================================================
 * Giao diện: thẻ bo góc, icon tròn, badge dạng pill,
 * bảng công việc có tiêu đề liền với thanh xanh nhạt.
 */
public class TrangChuKyThuatVienGUI extends TrangChuBaseGUI {

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
    private static final String ICON_DS_CONG_VIEC = "/images/icon_dsCongViec.png";
    private static final String ICON_CONG_VIEC_PHU_TRACH = "/images/icon_cvPhuTrach.png";
    private static final String ICON_YEU_CAU_XUAT_KHO = "/images/icon_yeuCauXuatKho.png";
    private static final String ICON_BAO_CAO = IMG_BAO_CAO;

    // =========================================================
    // 3. MENU CHÍNH
    // =========================================================

    private static final MucMenu[] MENU = {
        new MucMenu(KEY_TRANG_CHU, "Trang Chủ", ICON_TRANG_CHU),
        new MucMenu(KEY_DS_CONG_VIEC, "Danh Sách Công Việc", ICON_DS_CONG_VIEC),
        new MucMenu(KEY_CONG_VIEC_PHU_TRACH, "Công Việc Phụ Trách", ICON_CONG_VIEC_PHU_TRACH),
        new MucMenu(KEY_YEU_CAU_XUAT_KHO, "Yêu Cầu Xuất Kho", ICON_YEU_CAU_XUAT_KHO),
        new MucMenu(KEY_BAO_CAO, "Báo Cáo Thống Kê", ICON_BAO_CAO)
    };

    // =========================================================
    // 4. THÔNG TIN KỸ THUẬT VIÊN
    // =========================================================

    private String tenKyThuatVien = "Hoàng Phúc";
    private String vaiTro = "Kỹ thuật viên";
    private String ngaySinh = "15/09/2006";
    private String gioiTinh = "Nam";

    // =========================================================
    // 5. THÀNH PHẦN GIAO DIỆN
    // =========================================================

    private JLabel lblDongHo;
    private JLabel lblNgayHienTai;
    private JTable tblCongViec;
    private Timer timerDongHo;

    // =========================================================
    // 6. CONSTRUCTOR
    // =========================================================

    public TrangChuKyThuatVienGUI(String hoTen) {

        super(hoTen, "Kỹ thuật viên", MENU);

        tenKyThuatVien = hoTen;

        themCard(KEY_TRANG_CHU, taoTrangChu());
        
        themCard(
                KEY_DS_CONG_VIEC,
                new DanhSachCongViecGUI()
        );
        
        themCard(
                KEY_CONG_VIEC_PHU_TRACH,
                new CongViecPhuTrachGUI()
        );
        themCard(
                KEY_YEU_CAU_XUAT_KHO,
                new YeuCauXuatKhoGUI()
        );
        
        hoanTat();

        khoiDongDongHo();
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

        pnlNoiDung.add(pnlChucNang);
        pnlNoiDung.add(Box.createVerticalStrut(16));
        pnlNoiDung.add(pnlTieuDeTK);
        pnlNoiDung.add(Box.createVerticalStrut(10));
        pnlNoiDung.add(pnlThongKe);
        pnlNoiDung.add(Box.createVerticalStrut(16));
        pnlNoiDung.add(taoKhuVucCongViec());

        // Các khu vực phía trên giữ chiều cao cố định
        gioiHanChieuCao(pnlChucNang);
        gioiHanChieuCao(pnlTieuDeTK);
        gioiHanChieuCao(pnlThongKe);

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

        // ----- Tiêu đề -----
        JPanel pnlTieuDe = new JPanel();
        pnlTieuDe.setLayout(new BoxLayout(pnlTieuDe, BoxLayout.Y_AXIS));
        pnlTieuDe.setBackground(MAU_NEN);

        pnlTieuDe.add(taoLabel("Trang chủ – Kỹ thuật viên", 24, Font.BOLD, MAU_CHU));
        pnlTieuDe.add(Box.createVerticalStrut(4));
        pnlTieuDe.add(taoLabel(
                "Theo dõi phân công, tiến độ sửa chữa và nhu cầu vật tư trong ca làm việc.",
                12, Font.PLAIN, MAU_CHU_PHU));

        // ----- Thẻ cá nhân -----
        PanelBoTron pnlCaNhan = new PanelBoTron(14, MAU_TRANG, MAU_VIEN);
        pnlCaNhan.setLayout(new BorderLayout(16, 0));
        pnlCaNhan.setBorder(new EmptyBorder(16, 14, 16, 22));

        // Avatar tròn
        AvatarTron avatar = new AvatarTron(62, MAU_XANH_DAM);
        JPanel pnlAvatar = new JPanel(new GridBagLayout());
        pnlAvatar.setOpaque(false);
        pnlAvatar.add(avatar);

        // Thông tin chi tiết
        JPanel pnlChiTiet = new JPanel();
        pnlChiTiet.setLayout(new BoxLayout(pnlChiTiet, BoxLayout.Y_AXIS));
        pnlChiTiet.setOpaque(false);

        pnlChiTiet.add(taoDongThongTin("♙", tenKyThuatVien, 16, Font.BOLD, MAU_CHU));
        pnlChiTiet.add(Box.createVerticalStrut(4));
        pnlChiTiet.add(taoDongThongTin("◉", "Vai trò: " + vaiTro, 12, Font.PLAIN, MAU_CHU_TOI));
        pnlChiTiet.add(Box.createVerticalStrut(3));
        pnlChiTiet.add(taoDongThongTin("▣", "Ngày sinh: " + ngaySinh, 12, Font.PLAIN, MAU_CHU_TOI));
        pnlChiTiet.add(Box.createVerticalStrut(3));
        pnlChiTiet.add(taoDongThongTin("⊗", "Giới tính: " + gioiTinh, 12, Font.PLAIN, MAU_CHU_TOI));

        // Đồng hồ (canh phải, giữa theo chiều dọc)
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

    /** Một dòng thông tin: icon xanh nhỏ + nội dung. */
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

        pnl.add(taoTheChucNang("▤", "Danh sách công việc",
                "Xem các phiếu được phân công và thời hạn xử lý.",
                MAU_XANH_DAM, this::moDanhSachCongViec));

        pnl.add(taoTheChucNang("⚒", "Theo dõi công việc",
                "Cập nhật checklist và tiến độ công việc phụ trách.",
                MAU_XANH, this::moCongViecPhuTrach));

        pnl.add(taoTheChucNang("⬡", "Yêu cầu xuất kho",
                "Đề nghị cấp phụ tùng, theo dõi tình trạng vật tư.",
                MAU_CAM, this::moYeuCauXuatKho));

        return pnl;
    }

    private JPanel taoTheChucNang(String icon, String tieuDe, String moTa,
                                  Color mauNen, Runnable hanhDong) {

        PanelBoTron pnlThe = new PanelBoTron(14, mauNen, null);
        pnlThe.setLayout(new BorderLayout(0, 14));
        pnlThe.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Hàng tiêu đề: icon tròn mờ + tên
        JPanel pnlDau = new JPanel(new BorderLayout(12, 0));
        pnlDau.setOpaque(false);

        pnlDau.add(new IconTron(36, new Color(255, 255, 255, 55), MAU_TRANG, icon),
                BorderLayout.WEST);

        JLabel lblTieuDe = taoLabel(tieuDe, 14, Font.BOLD, MAU_TRANG);
        pnlDau.add(lblTieuDe, BorderLayout.CENTER);

        // Mô tả
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
        pnl.add(taoLabel("Công việc của " + tenKyThuatVien + " hôm nay",
                16, Font.BOLD, MAU_CHU), BorderLayout.WEST);
        return pnl;
    }

    // =========================================================
    // 11. BỐN THẺ THỐNG KÊ
    // =========================================================

    private JPanel taoKhuVucThongKe() {

        JPanel pnl = new JPanel(new GridLayout(1, 4, 10, 0));
        pnl.setBackground(MAU_NEN);
        pnl.setPreferredSize(new Dimension(0, 116));

        pnl.add(taoTheThongKe("▢", "Được phân công", "5", "Tổng số công việc trong ca"));
        pnl.add(taoTheThongKe("⚒", "Đang thực hiện", "2", "1 việc ưu tiên cao"));
        pnl.add(taoTheThongKe("✓", "Hoàn thành", "2", "Đã bàn giao kiểm tra chất lượng"));
        pnl.add(taoTheThongKe("◷", "Chờ bắt đầu", "1", "SC-2026-00142 • 11:30"));

        return pnl;
    }

    private JPanel taoTheThongKe(String icon, String tieuDe, String giaTri, String moTa) {

        PanelBoTron pnlThe = new PanelBoTron(12, MAU_TRANG, MAU_VIEN);
        pnlThe.setLayout(new BorderLayout(0, 4));
        pnlThe.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel pnlDau = new JPanel(new BorderLayout(10, 0));
        pnlDau.setOpaque(false);
        pnlDau.add(new IconTron(34, MAU_XANH_NHAT, MAU_XANH_DAM, icon), BorderLayout.WEST);
        pnlDau.add(taoLabel(tieuDe, 12, Font.BOLD, MAU_CHU_TOI), BorderLayout.CENTER);

        pnlThe.add(pnlDau, BorderLayout.NORTH);
        pnlThe.add(taoLabel(giaTri, 26, Font.BOLD, MAU_CHU), BorderLayout.CENTER);
        pnlThe.add(taoLabel(moTa, 10, Font.PLAIN, MAU_CHU_PHU), BorderLayout.SOUTH);

        return pnlThe;
    }

    // =========================================================
    // 12. BẢNG CÔNG VIỆC CẦN CHÚ Ý
    // =========================================================

    private JPanel taoKhuVucCongViec() {

        PanelBoTron pnlCongViec = new PanelBoTron(12, MAU_TRANG, MAU_VIEN);
        pnlCongViec.setLayout(new BorderLayout());

        // Thanh tiêu đề xanh nhạt
        JPanel pnlTieuDe = new JPanel(new BorderLayout());
        pnlTieuDe.setBackground(MAU_XANH_NHAT);
        pnlTieuDe.setBorder(new EmptyBorder(14, 16, 8, 16));

        pnlTieuDe.add(taoLabel("Công việc cần chú ý", 13, Font.BOLD, MAU_XANH_DAM),
                BorderLayout.WEST);
        pnlTieuDe.add(taoLabel("Sắp xếp theo thời hạn", 10, Font.PLAIN, MAU_CHU_PHU),
                BorderLayout.EAST);

        String[] tenCot = {
            "Phiếu sửa chữa", "Xe / Hạng mục", "Ưu tiên",
            "Hạn hoàn thành", "Trạng thái", "Thao tác"
        };

        Object[][] duLieu = {
            {"SC-2026-00141", "51H-112.23 • Honda City\nKiểm tra hệ thống phanh",
                "Cao", "17/10 • 10:30", "Đang thực hiện", "Theo dõi"},
            {"SC-2026-00142", "51G-123.45 • Toyota Altis\nThay dầu, lọc cabin, kiểm tra",
                "Bình thường", "17/10 • 11:30", "Chờ bắt đầu", "Nhận việc"},
            {"SC-2026-00137", "51F-345.67 • Mazda 3\nBảo dưỡng hệ thống điều hòa",
                "Bình thường", "17/10 • 14:00", "Đang thực hiện", "Theo dõi"}
        };

        tblCongViec = new JTable(new DefaultTableModel(duLieu, tenCot) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        // Cấu hình bảng
        tblCongViec.setRowHeight(50);
        tblCongViec.setFont(new Font(FONT, Font.PLAIN, 11));
        tblCongViec.setShowGrid(false);
        tblCongViec.setShowHorizontalLines(true);
        tblCongViec.setShowVerticalLines(false);
        tblCongViec.setGridColor(MAU_VIEN);
        tblCongViec.setIntercellSpacing(new Dimension(0, 1));
        tblCongViec.setSelectionBackground(MAU_XANH_NHAT);
        tblCongViec.setSelectionForeground(MAU_CHU);
        tblCongViec.setFocusable(false);
        tblCongViec.setFillsViewportHeight(true);

        // Độ rộng cột
        int[] doRong = {140, 300, 110, 130, 130, 110};
        for (int i = 0; i < doRong.length; i++) {
            tblCongViec.getColumnModel().getColumn(i).setPreferredWidth(doRong[i]);
        }

        // Tiêu đề cột nằm liền với thanh xanh nhạt
        JTableHeader header = tblCongViec.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setPreferredSize(new Dimension(0, 34));
        header.setDefaultRenderer(new RendererTieuDeCot());

        // Renderer từng cột
        TableColumnModel cm = tblCongViec.getColumnModel();
        cm.getColumn(0).setCellRenderer(new RendererChu(true, MAU_XANH_DAM, "", false));
        cm.getColumn(1).setCellRenderer(new RendererChu(false, MAU_CHU_TOI, "", true));
        cm.getColumn(2).setCellRenderer(new RendererPill());
        cm.getColumn(3).setCellRenderer(new RendererChu(false, MAU_CHU_TOI, "", false));
        cm.getColumn(4).setCellRenderer(new RendererPill());
        cm.getColumn(5).setCellRenderer(new RendererChu(true, MAU_XANH_DAM, "→  ", false));

        // Nhấn nút thao tác
        tblCongViec.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                int hang = tblCongViec.rowAtPoint(e.getPoint());
                int cot = tblCongViec.columnAtPoint(e.getPoint());

                if (hang >= 0 && cot == 5) {

                    String thaoTac = tblCongViec.getValueAt(hang, 5).toString();

                    if (thaoTac.equals("Theo dõi")) {
                        theoDoiCongViec(hang);
                    } else if (thaoTac.equals("Nhận việc")) {
                        nhanViec(hang);
                    }
                }
            }
        });

        // Con trỏ tay khi rê vào cột thao tác
        tblCongViec.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {

                int hang = tblCongViec.rowAtPoint(e.getPoint());
                int cot = tblCongViec.columnAtPoint(e.getPoint());

                tblCongViec.setCursor(
                        (hang >= 0 && cot == 5)
                                ? new Cursor(Cursor.HAND_CURSOR)
                                : Cursor.getDefaultCursor());
            }
        });

        JScrollPane scrBang = new JScrollPane(tblCongViec);
        scrBang.setBorder(null);
        scrBang.getViewport().setBackground(MAU_TRANG);
        scrBang.setColumnHeaderView(header);
        scrBang.getColumnHeader().setBackground(MAU_XANH_NHAT);

        pnlCongViec.add(pnlTieuDe, BorderLayout.NORTH);
        pnlCongViec.add(scrBang, BorderLayout.CENTER);

        return pnlCongViec;
    }

    /** Màu nền hàng: xen kẽ trắng / xám rất nhạt. */
    private Color mauHang(boolean isSelected, int row) {

        if (isSelected) {
            return MAU_XANH_NHAT;
        }

        return (row % 2 == 0) ? MAU_TRANG : MAU_HANG_LE;
    }

    // =========================================================
    // 13. RENDERER TIÊU ĐỀ CỘT
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
    // 14. RENDERER CHỮ (CÓ THỂ ĐẬM / NHIỀU DÒNG / TIỀN TỐ)
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
    // 15. RENDERER BADGE (ƯU TIÊN / TRẠNG THÁI)
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

            if (noiDung.equals("Cao")) {
                pill.setMau(new Color(198, 40, 40), new Color(255, 225, 229));

            } else if (noiDung.equals("Chờ bắt đầu")) {
                pill.setMau(new Color(230, 119, 0), new Color(255, 236, 209));

            } else {
                // "Bình thường", "Đang thực hiện", ...
                pill.setMau(MAU_XANH_DAM, MAU_XANH_NHAT);
            }

            return this;
        }
    }

    // =========================================================
    // 16. ĐỒNG HỒ
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
    // 17. HÀM HỖ TRỢ
    // =========================================================

    private JLabel taoLabel(String noiDung, int coChu, int kieuChu, Color mauChu) {

        JLabel lbl = new JLabel(noiDung);
        lbl.setFont(new Font(FONT, kieuChu, coChu));
        lbl.setForeground(mauChu);
        return lbl;
    }

    /** Giữ chiều cao cố định cho khu vực khi đặt trong BoxLayout. */
    private void gioiHanChieuCao(JComponent c) {

        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
    }

    // =========================================================
    // 18. CÁC THÀNH PHẦN VẼ TÙY CHỈNH
    // =========================================================

    /** Panel bo góc, có thể có viền; các thành phần con bị cắt theo góc bo. */
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

    /** Icon ký tự đặt trong hình tròn. */
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

    /** Ảnh đại diện: hình tròn xanh + biểu tượng người màu trắng. */
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

            // Đầu
            g2.drawOval(cx - dauR, kichThuoc / 4, dauR * 2, dauR * 2);

            // Vai
            int vaiR = kichThuoc / 3;
            g2.drawArc(cx - vaiR, kichThuoc / 2 + 2, vaiR * 2, vaiR * 2, 0, 180);

            g2.dispose();
        }
    }

    /** Nhãn dạng viên thuốc (pill) bo tròn hoàn toàn. */
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
    // 19. HÀM ĐIỀU HƯỚNG
    // =========================================================

    private void moDanhSachCongViec() {
        // TODO: Bổ sung chức năng mở danh sách công việc.
    }

    private void moCongViecPhuTrach() {
        // TODO: Bổ sung chức năng mở công việc phụ trách.
    }

    private void moYeuCauXuatKho() {
        // TODO: Bổ sung chức năng mở yêu cầu xuất kho.
    }

    // =========================================================
    // 20. HÀM XỬ LÝ CÔNG VIỆC
    // =========================================================

    private void theoDoiCongViec(int hang) {
        // TODO: Lấy mã phiếu sửa chữa.
        // TODO: Mở màn hình chi tiết công việc.
        // TODO: Hiển thị checklist và tiến độ sửa chữa.
    }

    private void nhanViec(int hang) {
        // TODO: Lấy mã phiếu sửa chữa.
        // TODO: Xác nhận nhận việc.
        // TODO: Cập nhật trạng thái công việc.
    }

    // =========================================================
    // 21. MAIN
    // =========================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            TrangChuKyThuatVienGUI giaoDien = new TrangChuKyThuatVienGUI("Trần Minh");
            giaoDien.setVisible(true);
        });
    }
}