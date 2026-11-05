
package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/**
 * ============================================================
 * DANH SÁCH CÔNG VIỆC DÀNH CHO KỸ THUẬT VIÊN
 * ============================================================
 *
 * Chức năng:
 * - Lọc danh sách công việc.
 * - Thống kê công việc được phân công.
 * - Hiển thị danh sách phiếu sửa chữa.
 * - Theo dõi, nhận việc và xem chi tiết công việc.
 * - Phân trang danh sách công việc.
 */
public class DanhSachCongViecGUI extends JPanel {

    private static final long serialVersionUID = 1L;


    // =========================================================
    // 1. KHAI BÁO MÀU SẮC GIAO DIỆN
    // =========================================================

    private static final Color MAU_XANH_DAM =
            new Color(21, 101, 192);

    private static final Color MAU_XANH =
            new Color(30, 136, 229);

    private static final Color MAU_XANH_NHAT =
            new Color(227, 242, 253);

    private static final Color MAU_CHU =
            new Color(38, 50, 100);

    private static final Color MAU_CHU_PHU =
            new Color(84, 110, 122);

    private static final Color MAU_NEN =
            new Color(239, 243, 247);

    private static final Color MAU_VIEN =
            new Color(215, 228, 242);

    private static final Color MAU_TRANG =
            Color.WHITE;

    private static final Color MAU_CAM =
            new Color(255, 243, 224);

    private static final Color MAU_DO =
            new Color(255, 235, 238);

    private static final Color MAU_XANH_LA =
            new Color(232, 245, 233);


    // =========================================================
    // 2. KHAI BÁO THÔNG TIN KỸ THUẬT VIÊN
    // =========================================================

    private String tenKyThuatVien = "Trần Minh";

    private String maNhanVien = "NV-003";


    // =========================================================
    // 3. KHAI BÁO CÁC THÀNH PHẦN GIAO DIỆN
    // =========================================================

    private JTextField txtTimKiem;

    private JComboBox<String> cboNgayPhanCong;

    private JComboBox<String> cboTrangThai;

    private JComboBox<String> cboMucUuTien;

    private JLabel lblTongCongViec;

    private JLabel lblDangThucHien;

    private JLabel lblChoBatDau;

    private JLabel lblHoanThanh;

    private JLabel lblSoKetQua;

    private JLabel lblTrangHienTai;

    private JTable tblCongViec;

    private DefaultTableModel modelCongViec;

    private JButton btnTrangTruoc;

    private JButton btnTrangSau;

    private int trangHienTai = 1;

    private int soBanGhiMoiTrang = 5;


    // =========================================================
    // 4. CONSTRUCTOR
    // =========================================================

    public DanhSachCongViecGUI() {

        khoiTaoGiaoDien();

        napDuLieuMau();
    }


    // =========================================================
    // 5. KHỞI TẠO GIAO DIỆN CHÍNH
    // =========================================================

    private void khoiTaoGiaoDien() {

        setLayout(new BorderLayout());

        setBackground(MAU_NEN);

        setBorder(
                new EmptyBorder(18, 18, 18, 18)
        );

        // Khu vực nội dung
        JPanel pnlNoiDung = new JPanel();

        pnlNoiDung.setLayout(
                new BoxLayout(pnlNoiDung, BoxLayout.Y_AXIS)
        );

        pnlNoiDung.setBackground(MAU_NEN);

        // Tiêu đề trang
        pnlNoiDung.add(taoKhuVucTieuDe());

        pnlNoiDung.add(Box.createVerticalStrut(14));

        // Bộ lọc công việc
        pnlNoiDung.add(taoKhuVucBoLoc());

        pnlNoiDung.add(Box.createVerticalStrut(12));

        // Thống kê công việc
        pnlNoiDung.add(taoKhuVucThongKe());

        pnlNoiDung.add(Box.createVerticalStrut(12));

        // Bảng công việc
        pnlNoiDung.add(taoKhuVucBangCongViec());

        pnlNoiDung.add(Box.createVerticalStrut(12));

        // Ghi chú
        pnlNoiDung.add(taoKhuVucGhiChu());

        pnlNoiDung.add(Box.createVerticalStrut(12));

        // Chân trang
        pnlNoiDung.add(taoKhuVucChanTrang());

        add(pnlNoiDung, BorderLayout.CENTER);
    }


    // =========================================================
    // 6. KHU VỰC TIÊU ĐỀ TRANG
    // =========================================================

    private JPanel taoKhuVucTieuDe() {

        JPanel pnlTieuDe = new JPanel(
                new BorderLayout(10, 0)
        );

        pnlTieuDe.setBackground(MAU_NEN);

        // Thông tin tiêu đề
        JPanel pnlThongTin = new JPanel();

        pnlThongTin.setLayout(
                new BoxLayout(pnlThongTin, BoxLayout.Y_AXIS)
        );

        pnlThongTin.setBackground(MAU_NEN);

        JLabel lblTieuDe = taoLabel(
                "Danh sách công việc",
                22,
                Font.BOLD,
                MAU_CHU
        );

        JLabel lblMoTa = taoLabel(
                "Các công việc được phân công cho "
                + tenKyThuatVien
                + " • "
                + maNhanVien
                + " • Ca sáng 17/10/2026.",
                12,
                Font.PLAIN,
                MAU_CHU_PHU
        );

        pnlThongTin.add(lblTieuDe);

        pnlThongTin.add(Box.createVerticalStrut(4));

        pnlThongTin.add(lblMoTa);

        // Nút quay về
        JButton btnTrangChu = taoNut(
                "←  Trang chủ kỹ thuật viên",
                MAU_TRANG,
                MAU_XANH_DAM
        );

        btnTrangChu.setBorder(
                new CompoundBorder(
                        new LineBorder(MAU_VIEN, 1, true),
                        new EmptyBorder(8, 12, 8, 12)
                )
        );

        btnTrangChu.addActionListener(e -> quayVeTrangChu());

        pnlTieuDe.add(pnlThongTin, BorderLayout.CENTER);

        pnlTieuDe.add(btnTrangChu, BorderLayout.EAST);

        return pnlTieuDe;
    }


    // =========================================================
    // 7. KHU VỰC BỘ LỌC CÔNG VIỆC
    // =========================================================

    private JPanel taoKhuVucBoLoc() {

        JPanel pnlBoLoc = new JPanel(
                new BorderLayout(0, 0)
        );

        pnlBoLoc.setBackground(MAU_TRANG);

        pnlBoLoc.setBorder(
                taoVien(0, MAU_VIEN)
        );

        // Tiêu đề bộ lọc
        JLabel lblTieuDe = taoLabel(
                "Lọc công việc",
                13,
                Font.BOLD,
                MAU_XANH_DAM
        );

        JPanel pnlTieuDe = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 15, 10)
        );

        pnlTieuDe.setBackground(MAU_XANH_NHAT);

        pnlTieuDe.add(lblTieuDe);

        // Nội dung bộ lọc
        JPanel pnlNoiDung = new JPanel(
                new GridBagLayout()
        );

        pnlNoiDung.setBackground(MAU_TRANG);

        pnlNoiDung.setBorder(
                new EmptyBorder(12, 15, 12, 15)
        );

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(0, 0, 0, 10);

        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.weightx = 1;

        // -----------------------------------------------------
        // Bộ lọc mã phiếu / biển số
        // -----------------------------------------------------

        JPanel pnlMaPhieu = taoTruongBoLoc(
                "Mã phiếu / Biển số"
        );

        txtTimKiem = new JTextField();

        txtTimKiem.setFont(
                new Font("Segoe UI", Font.PLAIN, 12)
        );

        txtTimKiem.setPreferredSize(
                new Dimension(180, 34)
        );

        txtTimKiem.setBorder(
                new CompoundBorder(
                        new LineBorder(MAU_VIEN, 1, true),
                        new EmptyBorder(5, 10, 5, 10)
                )
        );

        pnlMaPhieu.add(txtTimKiem, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;

        pnlNoiDung.add(pnlMaPhieu, gbc);


        // -----------------------------------------------------
        // Bộ lọc ngày phân công
        // -----------------------------------------------------

        JPanel pnlNgay = taoTruongBoLoc(
                "Ngày phân công"
        );

        cboNgayPhanCong = taoComboBox(
                "17/10/2026",
                "16/10/2026",
                "15/10/2026",
                "Tất cả ngày"
        );

        pnlNgay.add(cboNgayPhanCong, BorderLayout.CENTER);

        gbc.gridx = 1;

        pnlNoiDung.add(pnlNgay, gbc);


        // -----------------------------------------------------
        // Bộ lọc trạng thái
        // -----------------------------------------------------

        JPanel pnlTrangThai = taoTruongBoLoc(
                "Trạng thái"
        );

        cboTrangThai = taoComboBox(
                "Tất cả trạng thái",
                "Đang thực hiện",
                "Chờ bắt đầu",
                "Đã hoàn thành"
        );

        pnlTrangThai.add(cboTrangThai, BorderLayout.CENTER);

        gbc.gridx = 2;

        pnlNoiDung.add(pnlTrangThai, gbc);


        // -----------------------------------------------------
        // Bộ lọc mức ưu tiên
        // -----------------------------------------------------

        JPanel pnlUuTien = taoTruongBoLoc(
                "Mức ưu tiên"
        );

        cboMucUuTien = taoComboBox(
                "Tất cả mức ưu tiên",
                "Cao",
                "Bình thường"
        );

        pnlUuTien.add(cboMucUuTien, BorderLayout.CENTER);

        gbc.gridx = 3;

        pnlNoiDung.add(pnlUuTien, gbc);


        // -----------------------------------------------------
        // Nút áp dụng bộ lọc
        // -----------------------------------------------------

        JButton btnApDung = taoNut(
                "⚲  Áp dụng",
                MAU_XANH_DAM,
                MAU_TRANG
        );

        btnApDung.setPreferredSize(
                new Dimension(100, 34)
        );

        btnApDung.addActionListener(
                e -> apDungBoLoc()
        );

        gbc.gridx = 4;

        gbc.weightx = 0;

        gbc.insets = new Insets(18, 0, 0, 0);

        pnlNoiDung.add(btnApDung, gbc);


        // Thêm các khu vực
        pnlBoLoc.add(pnlTieuDe, BorderLayout.NORTH);

        pnlBoLoc.add(pnlNoiDung, BorderLayout.CENTER);

        return pnlBoLoc;
    }


    // =========================================================
    // 8. TẠO TRƯỜNG BỘ LỌC
    // =========================================================

    private JPanel taoTruongBoLoc(String tieuDe) {

        JPanel pnlTruong = new JPanel(
                new BorderLayout(0, 5)
        );

        pnlTruong.setBackground(MAU_TRANG);

        JLabel lblTieuDe = taoLabel(
                tieuDe,
                11,
                Font.PLAIN,
                MAU_CHU_PHU
        );

        pnlTruong.add(lblTieuDe, BorderLayout.NORTH);

        return pnlTruong;
    }


    // =========================================================
    // 9. TẠO COMBOBOX
    // =========================================================

    private JComboBox<String> taoComboBox(
            String... danhSach
    ) {

        JComboBox<String> cbo = new JComboBox<>(danhSach);

        cbo.setFont(
                new Font("Segoe UI", Font.PLAIN, 12)
        );

        cbo.setBackground(MAU_TRANG);

        cbo.setForeground(MAU_CHU_PHU);

        cbo.setPreferredSize(
                new Dimension(160, 34)
        );

        return cbo;
    }


    // =========================================================
    // 10. KHU VỰC THỐNG KÊ
    // =========================================================

    private JPanel taoKhuVucThongKe() {

        JPanel pnlThongKe = new JPanel(
                new GridLayout(1, 4, 10, 0)
        );

        pnlThongKe.setBackground(MAU_NEN);

        // Thẻ 1
        pnlThongKe.add(
                taoTheThongKe(
                        "▥",
                        "Tất cả công việc",
                        "5",
                        "Phụ trách: Trần Minh"
                )
        );

        // Thẻ 2
        pnlThongKe.add(
                taoTheThongKe(
                        "▥",
                        "Đang thực hiện",
                        "2",
                        "Cần cập nhật trước khi bàn giao"
                )
        );

        // Thẻ 3
        pnlThongKe.add(
                taoTheThongKe(
                        "▥",
                        "Chờ bắt đầu",
                        "1",
                        "Đã sẵn sàng tiếp nhận"
                )
        );

        // Thẻ 4
        pnlThongKe.add(
                taoTheThongKe(
                        "▥",
                        "Đã hoàn thành",
                        "2",
                        "Đã kiểm tra chất lượng"
                )
        );

        return pnlThongKe;
    }


    // =========================================================
    // 11. TẠO MỘT THẺ THỐNG KÊ
    // =========================================================

    private JPanel taoTheThongKe(
            String icon,
            String tieuDe,
            String giaTri,
            String moTa
    ) {

        JPanel pnlThe = new JPanel(
                new BorderLayout(0, 8)
        );

        pnlThe.setBackground(MAU_TRANG);

        pnlThe.setBorder(
                taoVien(10, MAU_VIEN)
        );

        // Tiêu đề
        JLabel lblTieuDe = taoLabel(
                icon + "   " + tieuDe,
                11,
                Font.BOLD,
                MAU_CHU_PHU
        );

        // Số liệu
        JLabel lblGiaTri = taoLabel(
                giaTri,
                25,
                Font.BOLD,
                MAU_CHU
        );

        // Mô tả
        JLabel lblMoTa = taoLabel(
                moTa,
                10,
                Font.PLAIN,
                MAU_CHU_PHU
        );

        pnlThe.add(lblTieuDe, BorderLayout.NORTH);

        pnlThe.add(lblGiaTri, BorderLayout.CENTER);

        pnlThe.add(lblMoTa, BorderLayout.SOUTH);

        return pnlThe;
    }


    // =========================================================
    // 12. KHU VỰC BẢNG CÔNG VIỆC
    // =========================================================

    private JPanel taoKhuVucBangCongViec() {

        JPanel pnlBang = new JPanel(
                new BorderLayout(0, 0)
        );

        pnlBang.setBackground(MAU_TRANG);

        pnlBang.setBorder(
                taoVien(0, MAU_VIEN)
        );

        // -----------------------------------------------------
        // Tiêu đề bảng
        // -----------------------------------------------------

        JPanel pnlTieuDe = new JPanel(
                new BorderLayout()
        );

        pnlTieuDe.setBackground(MAU_XANH_NHAT);

        pnlTieuDe.setBorder(
                new EmptyBorder(12, 15, 12, 15)
        );

        JLabel lblTieuDe = taoLabel(
                "Công việc được phân công",
                13,
                Font.BOLD,
                MAU_XANH_DAM
        );

        lblSoKetQua = taoLabel(
                "5 kết quả • Người phụ trách: Trần Minh",
                10,
                Font.PLAIN,
                MAU_CHU_PHU
        );

        pnlTieuDe.add(lblTieuDe, BorderLayout.WEST);

        pnlTieuDe.add(lblSoKetQua, BorderLayout.EAST);


        // -----------------------------------------------------
        // Khởi tạo bảng
        // -----------------------------------------------------

        String[] tenCot = {
            "Phiếu / Phân công",
            "Xe",
            "Công việc",
            "Ưu tiên",
            "Hạn xử lý",
            "Trạng thái",
            "Thao tác"
        };

        modelCongViec = new DefaultTableModel(
                null,
                tenCot
        ) {

            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {

                return false;
            }
        };

        tblCongViec = new JTable(modelCongViec);

        // Cấu hình bảng
        tblCongViec.setRowHeight(46);

        tblCongViec.setFont(
                new Font("Segoe UI", Font.PLAIN, 11)
        );

        tblCongViec.setForeground(MAU_CHU_PHU);

        tblCongViec.setGridColor(MAU_VIEN);

        tblCongViec.setShowVerticalLines(false);

        tblCongViec.setSelectionBackground(MAU_XANH_NHAT);

        tblCongViec.setSelectionForeground(MAU_CHU);

        tblCongViec.setAutoResizeMode(
                JTable.AUTO_RESIZE_ALL_COLUMNS
        );


        // -----------------------------------------------------
        // Cấu hình tiêu đề cột
        // -----------------------------------------------------

        JTableHeader header = tblCongViec.getTableHeader();

        header.setFont(
                new Font("Segoe UI", Font.BOLD, 10)
        );

        header.setBackground(MAU_XANH_NHAT);

        header.setForeground(MAU_XANH_DAM);

        header.setPreferredSize(
                new Dimension(0, 34)
        );


        // -----------------------------------------------------
        // Cấu hình độ rộng các cột
        // -----------------------------------------------------

        tblCongViec.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(130);

        tblCongViec.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(100);

        tblCongViec.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(220);

        tblCongViec.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(90);

        tblCongViec.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(90);

        tblCongViec.getColumnModel()
                .getColumn(5)
                .setPreferredWidth(120);

        tblCongViec.getColumnModel()
                .getColumn(6)
                .setPreferredWidth(90);


        // -----------------------------------------------------
        // Renderer trạng thái và ưu tiên
        // -----------------------------------------------------

        tblCongViec.getColumnModel()
                .getColumn(3)
                .setCellRenderer(new RendererTrangThai());

        tblCongViec.getColumnModel()
                .getColumn(5)
                .setCellRenderer(new RendererTrangThai());


        // -----------------------------------------------------
        // Xử lý nút thao tác trong bảng
        // -----------------------------------------------------

        tblCongViec.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {

                        int hang = tblCongViec.rowAtPoint(
                                e.getPoint()
                        );

                        int cot = tblCongViec.columnAtPoint(
                                e.getPoint()
                        );

                        if (hang >= 0 && cot == 6) {

                            String thaoTac = modelCongViec
                                    .getValueAt(hang, 6)
                                    .toString();

                            if (thaoTac.equals("Theo dõi")) {

                                theoDoiCongViec(hang);

                            } else if (thaoTac.equals("Nhận việc")) {

                                nhanViec(hang);

                            } else if (thaoTac.equals("Xem")) {

                                xemCongViec(hang);
                            }
                        }
                    }
                }
        );


        // -----------------------------------------------------
        // Tạo vùng cuộn
        // -----------------------------------------------------

        JScrollPane scrBang = new JScrollPane(tblCongViec);

        scrBang.setBorder(null);

        scrBang.getViewport().setBackground(MAU_TRANG);


        // -----------------------------------------------------
        // Phân trang
        // -----------------------------------------------------

        JPanel pnlPhanTrang = taoKhuVucPhanTrang();


        // -----------------------------------------------------
        // Thêm thành phần vào bảng
        // -----------------------------------------------------

        pnlBang.add(pnlTieuDe, BorderLayout.NORTH);

        pnlBang.add(scrBang, BorderLayout.CENTER);

        pnlBang.add(pnlPhanTrang, BorderLayout.SOUTH);

        return pnlBang;
    }


    // =========================================================
    // 13. RENDERER TRẠNG THÁI VÀ ƯU TIÊN
    // =========================================================

    private class RendererTrangThai
            extends JLabel
            implements TableCellRenderer {

        private static final long serialVersionUID = 1L;

        public RendererTrangThai() {

            setOpaque(true);

            setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            setFont(
                    new Font("Segoe UI", Font.BOLD, 10)
            );
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {

            String noiDung = value == null
                    ? ""
                    : value.toString();

            setText(noiDung);

            // Màu sắc theo trạng thái
            if (noiDung.equals("Cao")) {

                setForeground(new Color(183, 28, 28));

                setBackground(MAU_DO);

            } else if (noiDung.equals("Đang thực hiện")) {

                setForeground(MAU_XANH_DAM);

                setBackground(MAU_XANH_NHAT);

            } else if (noiDung.equals("Chờ bắt đầu")) {

                setForeground(new Color(230, 119, 0));

                setBackground(MAU_CAM);

            } else if (noiDung.equals("Đã hoàn thành")) {

                setForeground(new Color(46, 125, 50));

                setBackground(MAU_XANH_LA);

            } else {

                setForeground(MAU_XANH_DAM);

                setBackground(MAU_XANH_NHAT);
            }

            return this;
        }
    }


    // =========================================================
    // 14. KHU VỰC PHÂN TRANG
    // =========================================================

    private JPanel taoKhuVucPhanTrang() {

        JPanel pnlPhanTrang = new JPanel(
                new BorderLayout()
        );

        pnlPhanTrang.setBackground(MAU_TRANG);

        pnlPhanTrang.setBorder(
                new EmptyBorder(10, 15, 10, 15)
        );

        // Thông tin số bản ghi
        JLabel lblSoBanGhi = taoLabel(
                "Hiển thị 5 / 5 bản ghi",
                10,
                Font.PLAIN,
                MAU_CHU_PHU
        );

        // Các nút phân trang
        JPanel pnlNut = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 5, 0)
        );

        pnlNut.setBackground(MAU_TRANG);

        btnTrangTruoc = taoNut(
                "‹",
                MAU_TRANG,
                MAU_CHU_PHU
        );

        btnTrangTruoc.setPreferredSize(
                new Dimension(25, 28)
        );

        JButton btnTrang = taoNut(
                "1",
                MAU_XANH_DAM,
                MAU_TRANG
        );

        btnTrang.setPreferredSize(
                new Dimension(28, 28)
        );

        btnTrangSau = taoNut(
                "›",
                MAU_TRANG,
                MAU_CHU_PHU
        );

        btnTrangSau.setPreferredSize(
                new Dimension(25, 28)
        );

        lblTrangHienTai = taoLabel(
                "1",
                11,
                Font.BOLD,
                MAU_TRANG
        );

        // Sự kiện chuyển trang
        btnTrangTruoc.addActionListener(
                e -> chuyenTrangTruoc()
        );

        btnTrangSau.addActionListener(
                e -> chuyenTrangSau()
        );

        pnlNut.add(btnTrangTruoc);

        pnlNut.add(btnTrang);

        pnlNut.add(btnTrangSau);

        pnlPhanTrang.add(
                lblSoBanGhi,
                BorderLayout.WEST
        );

        pnlPhanTrang.add(
                pnlNut,
                BorderLayout.EAST
        );

        return pnlPhanTrang;
    }


    // =========================================================
    // 15. KHU VỰC GHI CHÚ
    // =========================================================

    private JPanel taoKhuVucGhiChu() {

        JPanel pnlGhiChu = new JPanel(
                new BorderLayout(10, 0)
        );

        pnlGhiChu.setBackground(MAU_XANH_NHAT);

        pnlGhiChu.setBorder(
                new EmptyBorder(12, 12, 12, 12)
        );

        JLabel lblIcon = taoLabel(
                "ⓘ",
                14,
                Font.BOLD,
                MAU_XANH_DAM
        );

        JLabel lblNoiDung = taoLabel(
                "Nhận việc để bắt đầu sửa chữa. "
                + "Chọn Theo dõi để cập nhật checklist, "
                + "vật tư sử dụng và tiến độ từng hạng mục.",
                11,
                Font.PLAIN,
                MAU_XANH_DAM
        );

        pnlGhiChu.add(lblIcon, BorderLayout.WEST);

        pnlGhiChu.add(lblNoiDung, BorderLayout.CENTER);

        return pnlGhiChu;
    }


    // =========================================================
    // 16. KHU VỰC CHÂN TRANG
    // =========================================================

    private JPanel taoKhuVucChanTrang() {

        JPanel pnlChanTrang = new JPanel(
                new BorderLayout()
        );

        pnlChanTrang.setBackground(MAU_NEN);

        JLabel lblThongTin = taoLabel(
                "Dữ liệu minh họa • AutoCar PV",
                10,
                Font.PLAIN,
                MAU_CHU_PHU
        );

        JLabel lblNgay = taoLabel(
                "Thứ Bảy, 17/10/2026",
                10,
                Font.PLAIN,
                MAU_CHU_PHU
        );

        pnlChanTrang.add(
                lblThongTin,
                BorderLayout.WEST
        );

        pnlChanTrang.add(
                lblNgay,
                BorderLayout.EAST
        );

        return pnlChanTrang;
    }


    // =========================================================
    // 17. NẠP DỮ LIỆU MẪU
    // =========================================================

    private void napDuLieuMau() {

        // Xóa dữ liệu cũ
        modelCongViec.setRowCount(0);

        // -----------------------------------------------------
        // Công việc 1
        // -----------------------------------------------------

        modelCongViec.addRow(
                new Object[]{

                    "SC-2026-00141\n17/10 • 08:15",

                    "51H-112.23\nHonda City",

                    "Kiểm tra hệ thống phanh\nKhu vực: Cầu nâng 02",

                    "Cao",

                    "17/10\n10:30",

                    "Đang thực hiện",

                    "Theo dõi"
                }
        );


        // -----------------------------------------------------
        // Công việc 2
        // -----------------------------------------------------

        modelCongViec.addRow(
                new Object[]{

                    "SC-2026-00142\n17/10 • 08:45",

                    "51G-123.45\nToyota Altis",

                    "Thay dầu, lọc cabin, kiểm tra\nKhu vực: Cầu nâng 01",

                    "Bình thường",

                    "17/10\n11:30",

                    "Chờ bắt đầu",

                    "Nhận việc"
                }
        );


        // -----------------------------------------------------
        // Công việc 3
        // -----------------------------------------------------

        modelCongViec.addRow(
                new Object[]{

                    "SC-2026-00137\n17/10 • 07:45",

                    "51F-345.67\nMazda 3",

                    "Bảo dưỡng điều hòa\nKhu vực: Khoang dịch vụ 03",

                    "Bình thường",

                    "17/10\n14:00",

                    "Đang thực hiện",

                    "Theo dõi"
                }
        );


        // -----------------------------------------------------
        // Công việc 4
        // -----------------------------------------------------

        modelCongViec.addRow(
                new Object[]{

                    "SC-2026-00136\n17/10 • 07:30",

                    "51H-678.90\nKia Seltos",

                    "Thay ắc quy, kiểm tra điện\nĐã bàn giao: 08:50",

                    "Bình thường",

                    "17/10\n09:00",

                    "Đã hoàn thành",

                    "Xem"
                }
        );


        // -----------------------------------------------------
        // Công việc 5
        // -----------------------------------------------------

        modelCongViec.addRow(
                new Object[]{

                    "SC-2026-00135\n17/10 • 07:30",

                    "59A-246.80\nHyundai Accent",

                    "Đảo lốp, cân bằng động\nĐã bàn giao: 09:20",

                    "Bình thường",

                    "17/10\n09:30",

                    "Đã hoàn thành",

                    "Xem"
                }
        );
    }


    // =========================================================
    // 18. XỬ LÝ ÁP DỤNG BỘ LỌC
    // =========================================================

    private void apDungBoLoc() {

        // TODO: Lấy mã phiếu hoặc biển số từ txtTimKiem.
        // TODO: Lấy ngày phân công từ cboNgayPhanCong.
        // TODO: Lấy trạng thái từ cboTrangThai.
        // TODO: Lấy mức ưu tiên từ cboMucUuTien.
        // TODO: Lọc dữ liệu công việc.
        // TODO: Cập nhật lại bảng và số lượng thống kê.

    }


    // =========================================================
    // 19. XỬ LÝ CHUYỂN TRANG
    // =========================================================

    private void chuyenTrangTruoc() {

        // TODO: Kiểm tra trang hiện tại.
        // TODO: Chuyển về trang trước.
        // TODO: Cập nhật dữ liệu bảng.
    }


    private void chuyenTrangSau() {

        // TODO: Kiểm tra số trang hiện có.
        // TODO: Chuyển sang trang tiếp theo.
        // TODO: Cập nhật dữ liệu bảng.
    }


    // =========================================================
    // 20. XỬ LÝ CÁC THAO TÁC CÔNG VIỆC
    // =========================================================

    private void theoDoiCongViec(int hang) {

        // TODO: Lấy mã phiếu sửa chữa.
        // TODO: Mở màn hình theo dõi công việc.
        // TODO: Hiển thị checklist và tiến độ.
    }


    private void nhanViec(int hang) {

        // TODO: Lấy mã phiếu sửa chữa.
        // TODO: Hiển thị xác nhận nhận việc.
        // TODO: Cập nhật trạng thái công việc.
    }


    private void xemCongViec(int hang) {

        // TODO: Lấy mã phiếu sửa chữa.
        // TODO: Mở thông tin chi tiết công việc.
        // TODO: Hiển thị lịch sử sửa chữa.
    }


    // =========================================================
    // 21. ĐIỀU HƯỚNG VỀ TRANG CHỦ
    // =========================================================

    private void quayVeTrangChu() {

        // TODO: Điều hướng về trang chủ kỹ thuật viên.
        // TODO: Sử dụng CardLayout của TrangChuBaseGUI.
    }


    // =========================================================
    // 22. HÀM HỖ TRỢ TẠO LABEL
    // =========================================================

    private JLabel taoLabel(
            String noiDung,
            int coChu,
            int kieuChu,
            Color mauChu
    ) {

        JLabel lbl = new JLabel(noiDung);

        lbl.setFont(
                new Font("Segoe UI", kieuChu, coChu)
        );

        lbl.setForeground(mauChu);

        return lbl;
    }


    // =========================================================
    // 23. HÀM HỖ TRỢ TẠO NÚT
    // =========================================================

    private JButton taoNut(
            String noiDung,
            Color mauNen,
            Color mauChu
    ) {

        JButton btn = new JButton(noiDung);

        btn.setFont(
                new Font("Segoe UI", Font.BOLD, 11)
        );

        btn.setForeground(mauChu);

        btn.setBackground(mauNen);

        btn.setFocusPainted(false);

        btn.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        btn.setBorder(
                new EmptyBorder(7, 10, 7, 10)
        );

        return btn;
    }


    // =========================================================
    // 24. HÀM HỖ TRỢ TẠO VIỀN
    // =========================================================

    private Border taoVien(
            int khoangCach,
            Color mauVien
    ) {

        return new CompoundBorder(

                new LineBorder(mauVien, 1, true),

                new EmptyBorder(
                        khoangCach,
                        khoangCach,
                        khoangCach,
                        khoangCach
                )
        );
    }
}
