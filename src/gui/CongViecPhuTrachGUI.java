package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/**
 * ============================================================
 * THEO DÕI CÔNG VIỆC PHỤ TRÁCH - KỸ THUẬT VIÊN
 * ============================================================
 * Đồng bộ chuẩn giao diện: Thẻ bo góc, badge dạng pill,
 * font chữ chuẩn Segoe UI (11-12px), chiều cao bảng 46-50px.
 */
public class CongViecPhuTrachGUI extends JPanel {

    private static final long serialVersionUID = 1L;

    // =========================================================
    // 1. MÀU SẮC GIAO DIỆN
    // =========================================================

    private static final Color MAU_XANH_DAM  = new Color(21, 101, 192);
    private static final Color MAU_XANH      = new Color(30, 136, 229);
    private static final Color MAU_XANH_NHAT = new Color(227, 242, 253);
    private static final Color MAU_CHU       = new Color(26, 35, 126);
    private static final Color MAU_CHU_TOI   = new Color(55, 71, 110);
    private static final Color MAU_CHU_PHU   = new Color(96, 115, 140);
    private static final Color MAU_NEN       = new Color(239, 243, 247);
    private static final Color MAU_VIEN      = new Color(215, 228, 242);
    private static final Color MAU_HANG_LE   = new Color(247, 249, 252);
    private static final Color MAU_TRANG     = Color.WHITE;

    private static final String FONT = "Segoe UI";

    // =========================================================
    // 2. THÔNG TIN CÔNG VIỆC
    // =========================================================

    private String maPhieu = "SC-2026-00142";
    private String maNhanVien = "NV-003";
    private String tenKyThuatVien = "Trần Minh";
    private String tenKhachHang = "Nguyễn Văn A";
    private String soDienThoai = "0903 123 456";
    private String bienSoXe = "51G-123.45";
    private String tenXe = "Toyota Corolla Altis";
    private String maYeuCauKho = "XK-2026-00058";

    // =========================================================
    // 3. THÀNH PHẦN GIAO DIỆN
    // =========================================================

    private JLabel lblTienDo;
    private JLabel lblPhanTram;
    private JLabel lblTrangThai;
    private JLabel lblThoiGian;
    private JProgressBar thanhTienDo;
    private JTable tblChecklist;
    private DefaultTableModel modelChecklist;
    private JTable tblVatTu;
    private DefaultTableModel modelVatTu;
    private JTextArea txtNhatKy;
    private JComboBox<String> cboTrangThaiCapNhat;

    // =========================================================
    // 4. CONSTRUCTOR
    // =========================================================

    public CongViecPhuTrachGUI() {
        khoiTaoGiaoDien();
        napDuLieuMau();
        capNhatTienDo();
    }

    // =========================================================
    // 5. KHỞI TẠO GIAO DIỆN CHÍNH
    // =========================================================

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(MAU_NEN);
        setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel pnlNoiDung = new JPanel();
        pnlNoiDung.setLayout(new BoxLayout(pnlNoiDung, BoxLayout.Y_AXIS));
        pnlNoiDung.setBackground(MAU_NEN);

        pnlNoiDung.add(taoKhuVucTieuDe());
        pnlNoiDung.add(Box.createVerticalStrut(14));
        pnlNoiDung.add(taoKhuVucThongTin());
        pnlNoiDung.add(Box.createVerticalStrut(12));
        pnlNoiDung.add(taoKhuVucChecklist());
        pnlNoiDung.add(Box.createVerticalStrut(12));
        pnlNoiDung.add(taoKhuVucVatTu());
        pnlNoiDung.add(Box.createVerticalStrut(12));
        pnlNoiDung.add(taoKhuVucNhatKy());
        pnlNoiDung.add(Box.createVerticalStrut(12));
        pnlNoiDung.add(taoKhuVucChucNang());
        pnlNoiDung.add(Box.createVerticalStrut(12));
        pnlNoiDung.add(taoKhuVucChanTrang());

        JScrollPane scrNoiDung = new JScrollPane(pnlNoiDung);
        scrNoiDung.setBorder(null);
        scrNoiDung.getViewport().setBackground(MAU_NEN);
        scrNoiDung.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrNoiDung, BorderLayout.CENTER);
    }

    // =========================================================
    // 6. TIÊU ĐỀ TRANG
    // =========================================================

    private JPanel taoKhuVucTieuDe() {
        JPanel pnlTieuDe = new JPanel(new BorderLayout(10, 0));
        pnlTieuDe.setBackground(MAU_NEN);

        JPanel pnlThongTin = new JPanel();
        pnlThongTin.setLayout(new BoxLayout(pnlThongTin, BoxLayout.Y_AXIS));
        pnlThongTin.setBackground(MAU_NEN);

        pnlThongTin.add(taoLabel("Theo dõi công việc phụ trách", 24, Font.BOLD, MAU_CHU));
        pnlThongTin.add(Box.createVerticalStrut(4));
        pnlThongTin.add(taoLabel(
                maPhieu + " • Cập nhật tiến độ và ghi nhận vật tư thực tế cho công việc đang sửa chữa.",
                12, Font.PLAIN, MAU_CHU_PHU));

        JButton btnDanhSach = taoNut("←  Danh sách công việc", MAU_TRANG, MAU_XANH_DAM);
        btnDanhSach.setBorder(new CompoundBorder(
                new LineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));
        btnDanhSach.addActionListener(e -> quayVeDanhSachCongViec());

        pnlTieuDe.add(pnlThongTin, BorderLayout.CENTER);
        pnlTieuDe.add(btnDanhSach, BorderLayout.EAST);

        return pnlTieuDe;
    }

    // =========================================================
    // 7. THÔNG TIN CÔNG VIỆC & TIẾN ĐỘ
    // =========================================================

    private JPanel taoKhuVucThongTin() {
        JPanel pnlThongTin = new JPanel(new BorderLayout(0, 0));
        pnlThongTin.setBackground(MAU_TRANG);
        pnlThongTin.setBorder(new LineBorder(MAU_VIEN, 1, true));

        // Tiêu đề khu vực
        JPanel pnlTieuDe = new JPanel(new BorderLayout());
        pnlTieuDe.setBackground(MAU_XANH_NHAT);
        pnlTieuDe.setBorder(new EmptyBorder(12, 16, 12, 16));

        pnlTieuDe.add(taoLabel("Thông tin công việc", 13, Font.BOLD, MAU_XANH_DAM), BorderLayout.WEST);
        pnlTieuDe.add(taoLabel("Phụ trách: " + tenKyThuatVien + " • " + maNhanVien, 11, Font.PLAIN, MAU_CHU_PHU), BorderLayout.EAST);

        // Chi tiết 3 cột
        JPanel pnlThongTinChiTiet = new JPanel(new GridLayout(1, 3, 12, 0));
        pnlThongTinChiTiet.setBackground(MAU_TRANG);
        pnlThongTinChiTiet.setBorder(new EmptyBorder(14, 16, 10, 16));

        pnlThongTinChiTiet.add(taoTruongThongTin("Phiếu sửa chữa / Tiếp nhận", maPhieu + " / TNX-2026-00142"));
        pnlThongTinChiTiet.add(taoTruongThongTin("Khách hàng / Điện thoại", tenKhachHang + " • " + soDienThoai));
        pnlThongTinChiTiet.add(taoTruongThongTin("Xe phụ trách", bienSoXe + " • " + tenXe));

        // Tiến độ
        JPanel pnlTienDo = new JPanel(new BorderLayout(0, 8));
        pnlTienDo.setBackground(MAU_TRANG);
        pnlTienDo.setBorder(new EmptyBorder(4, 16, 16, 16));

        JPanel pnlTieuDeTienDo = new JPanel(new BorderLayout());
        pnlTieuDeTienDo.setBackground(MAU_TRANG);

        lblTienDo = taoLabel("Tiến độ: 2 / 4 hạng mục", 12, Font.BOLD, MAU_XANH_DAM);
        lblPhanTram = taoLabel("50%", 12, Font.BOLD, MAU_XANH_DAM);

        pnlTieuDeTienDo.add(lblTienDo, BorderLayout.WEST);
        pnlTieuDeTienDo.add(lblPhanTram, BorderLayout.EAST);

        thanhTienDo = new JProgressBar(0, 100);
        thanhTienDo.setValue(50);
        thanhTienDo.setStringPainted(false);
        thanhTienDo.setForeground(MAU_XANH_DAM);
        thanhTienDo.setBackground(MAU_XANH_NHAT);
        thanhTienDo.setPreferredSize(new Dimension(100, 8));

        JPanel pnlThongTinTienDo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlThongTinTienDo.setBackground(MAU_TRANG);

        lblTrangThai = taoLabel("Đang thực hiện", 11, Font.BOLD, MAU_XANH_DAM);
        lblThoiGian = taoLabel("Bắt đầu 09:45 • Hẹn trả 11:30", 11, Font.PLAIN, MAU_CHU_PHU);

        pnlThongTinTienDo.add(lblTrangThai);
        pnlThongTinTienDo.add(lblThoiGian);

        pnlTienDo.add(pnlTieuDeTienDo, BorderLayout.NORTH);
        pnlTienDo.add(thanhTienDo, BorderLayout.CENTER);
        pnlTienDo.add(pnlThongTinTienDo, BorderLayout.SOUTH);

        pnlThongTin.add(pnlTieuDe, BorderLayout.NORTH);
        pnlThongTin.add(pnlThongTinChiTiet, BorderLayout.CENTER);
        pnlThongTin.add(pnlTienDo, BorderLayout.SOUTH);

        return pnlThongTin;
    }

    private JPanel taoTruongThongTin(String tieuDe, String noiDung) {
        JPanel pnlTruong = new JPanel(new BorderLayout(0, 5));
        pnlTruong.setBackground(MAU_TRANG);

        JLabel lblTieuDe = taoLabel(tieuDe, 11, Font.PLAIN, MAU_CHU_PHU);
        JLabel lblNoiDung = taoLabel(noiDung, 12, Font.PLAIN, MAU_CHU_TOI);
        lblNoiDung.setBorder(new CompoundBorder(
                new LineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        lblNoiDung.setOpaque(true);
        lblNoiDung.setBackground(new Color(247, 249, 252));

        pnlTruong.add(lblTieuDe, BorderLayout.NORTH);
        pnlTruong.add(lblNoiDung, BorderLayout.CENTER);

        return pnlTruong;
    }

    // =========================================================
    // 8. CHECKLIST THỰC HIỆN
    // =========================================================

    private JPanel taoKhuVucChecklist() {
        JPanel pnlChecklist = new JPanel(new BorderLayout());
        pnlChecklist.setBackground(MAU_TRANG);
        pnlChecklist.setBorder(new LineBorder(MAU_VIEN, 1, true));

        JPanel pnlTieuDe = new JPanel(new BorderLayout());
        pnlTieuDe.setBackground(MAU_XANH_NHAT);
        pnlTieuDe.setBorder(new EmptyBorder(12, 16, 12, 16));

        pnlTieuDe.add(taoLabel("Checklist thực hiện", 13, Font.BOLD, MAU_XANH_DAM), BorderLayout.WEST);
        pnlTieuDe.add(taoLabel("Đánh dấu sau khi kiểm tra thực tế", 11, Font.PLAIN, MAU_CHU_PHU), BorderLayout.EAST);

        String[] tenCot = {"Đánh dấu", "Hạng mục", "Kết quả / Ghi chú", "Cập nhật", "Trạng thái"};
        modelChecklist = new DefaultTableModel(null, tenCot) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return Boolean.class;
                return String.class;
            }
        };

        tblChecklist = new JTable(modelChecklist);
        tblChecklist.setRowHeight(46);
        tblChecklist.setFont(new Font(FONT, Font.PLAIN, 11));
        tblChecklist.setGridColor(MAU_VIEN);
        tblChecklist.setShowVerticalLines(false);
        tblChecklist.setSelectionBackground(MAU_XANH_NHAT);
        tblChecklist.setSelectionForeground(MAU_CHU);

        JTableHeader header = tblChecklist.getTableHeader();
        header.setFont(new Font(FONT, Font.BOLD, 10));
        header.setBackground(MAU_XANH_NHAT);
        header.setForeground(MAU_XANH_DAM);
        header.setPreferredSize(new Dimension(0, 34));

        tblChecklist.getColumnModel().getColumn(0).setPreferredWidth(70);
        tblChecklist.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblChecklist.getColumnModel().getColumn(2).setPreferredWidth(280);
        tblChecklist.getColumnModel().getColumn(3).setPreferredWidth(90);
        tblChecklist.getColumnModel().getColumn(4).setPreferredWidth(120);

        tblChecklist.getColumnModel().getColumn(4).setCellRenderer(new RendererPill());
        modelChecklist.addTableModelListener(e -> capNhatTienDo());

        JScrollPane scrChecklist = new JScrollPane(tblChecklist);
        scrChecklist.setBorder(null);
        scrChecklist.getViewport().setBackground(MAU_TRANG);

        pnlChecklist.add(pnlTieuDe, BorderLayout.NORTH);
        pnlChecklist.add(scrChecklist, BorderLayout.CENTER);

        return pnlChecklist;
    }

    // =========================================================
    // 9. VẬT TƯ SỬ DỤNG
    // =========================================================

    private JPanel taoKhuVucVatTu() {
        JPanel pnlVatTu = new JPanel(new BorderLayout());
        pnlVatTu.setBackground(MAU_TRANG);
        pnlVatTu.setBorder(new LineBorder(MAU_VIEN, 1, true));

        JPanel pnlTieuDe = new JPanel(new BorderLayout());
        pnlTieuDe.setBackground(MAU_XANH_NHAT);
        pnlTieuDe.setBorder(new EmptyBorder(12, 16, 12, 16));

        pnlTieuDe.add(taoLabel("Vật tư sử dụng", 13, Font.BOLD, MAU_XANH_DAM), BorderLayout.WEST);
        pnlTieuDe.add(taoLabel("Liên kết phiếu yêu cầu: " + maYeuCauKho, 11, Font.PLAIN, MAU_CHU_PHU), BorderLayout.EAST);

        String[] tenCot = {"Mã vật tư", "Tên vật tư", "ĐVT", "Dự kiến", "Đã dùng", "Cấp phát", "Trạng thái"};
        modelVatTu = new DefaultTableModel(null, tenCot) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblVatTu = new JTable(modelVatTu);
        tblVatTu.setRowHeight(46);
        tblVatTu.setFont(new Font(FONT, Font.PLAIN, 11));
        tblVatTu.setGridColor(MAU_VIEN);
        tblVatTu.setShowVerticalLines(false);
        tblVatTu.setSelectionBackground(MAU_XANH_NHAT);

        JTableHeader header = tblVatTu.getTableHeader();
        header.setFont(new Font(FONT, Font.BOLD, 10));
        header.setBackground(MAU_XANH_NHAT);
        header.setForeground(MAU_XANH_DAM);
        header.setPreferredSize(new Dimension(0, 34));

        tblVatTu.getColumnModel().getColumn(0).setPreferredWidth(90);
        tblVatTu.getColumnModel().getColumn(1).setPreferredWidth(220);
        tblVatTu.getColumnModel().getColumn(2).setPreferredWidth(70);
        tblVatTu.getColumnModel().getColumn(3).setPreferredWidth(80);
        tblVatTu.getColumnModel().getColumn(4).setPreferredWidth(80);
        tblVatTu.getColumnModel().getColumn(5).setPreferredWidth(80);
        tblVatTu.getColumnModel().getColumn(6).setPreferredWidth(120);

        tblVatTu.getColumnModel().getColumn(6).setCellRenderer(new RendererPill());

        JScrollPane scrVatTu = new JScrollPane(tblVatTu);
        scrVatTu.setBorder(null);
        scrVatTu.getViewport().setBackground(MAU_TRANG);

        JPanel pnlThaoTac = new JPanel(new BorderLayout());
        pnlThaoTac.setBackground(MAU_TRANG);
        pnlThaoTac.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel lblCanhBao = taoLabel("Thiếu 1 lọc gió cabin • Không có sẵn tại kho", 11, Font.PLAIN, new Color(230, 119, 0));
        JButton btnYeuCauXuatKho = taoNut("⬡  Lập yêu cầu xuất kho", MAU_XANH_DAM, MAU_TRANG);
        btnYeuCauXuatKho.addActionListener(e -> lapYeuCauXuatKho());

        pnlThaoTac.add(lblCanhBao, BorderLayout.WEST);
        pnlThaoTac.add(btnYeuCauXuatKho, BorderLayout.EAST);

        pnlVatTu.add(pnlTieuDe, BorderLayout.NORTH);
        pnlVatTu.add(scrVatTu, BorderLayout.CENTER);
        pnlVatTu.add(pnlThaoTac, BorderLayout.SOUTH);

        return pnlVatTu;
    }

    // =========================================================
    // 10. NHẬT KÝ KỸ THUẬT
    // =========================================================

    private JPanel taoKhuVucNhatKy() {
        JPanel pnlNhatKy = new JPanel(new BorderLayout());
        pnlNhatKy.setBackground(MAU_TRANG);
        pnlNhatKy.setBorder(new LineBorder(MAU_VIEN, 1, true));

        JPanel pnlTieuDe = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        pnlTieuDe.setBackground(MAU_XANH_NHAT);
        pnlTieuDe.add(taoLabel("Nhật ký kỹ thuật", 13, Font.BOLD, MAU_XANH_DAM));

        JPanel pnlNoiDung = new JPanel(new GridLayout(1, 2, 14, 0));
        pnlNoiDung.setBackground(MAU_TRANG);
        pnlNoiDung.setBorder(new EmptyBorder(14, 16, 16, 16));

        JPanel pnlGhiChu = new JPanel(new BorderLayout(0, 6));
        pnlGhiChu.setBackground(MAU_TRANG);
        pnlGhiChu.add(taoLabel("Ghi chú tiến độ", 11, Font.PLAIN, MAU_CHU_PHU), BorderLayout.NORTH);

        txtNhatKy = new JTextArea(3, 20);
        txtNhatKy.setFont(new Font(FONT, Font.PLAIN, 12));
        txtNhatKy.setLineWrap(true);
        txtNhatKy.setWrapStyleWord(true);
        txtNhatKy.setText("Đã hoàn tất thay dầu và kiểm tra phanh. Chờ lọc cabin để hoàn thiện và chạy thử.");
        txtNhatKy.setBorder(new CompoundBorder(
                new LineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JScrollPane scrNhatKy = new JScrollPane(txtNhatKy);
        scrNhatKy.setBorder(null);
        pnlGhiChu.add(scrNhatKy, BorderLayout.CENTER);

        JPanel pnlTrangThai = new JPanel(new BorderLayout(0, 6));
        pnlTrangThai.setBackground(MAU_TRANG);
        pnlTrangThai.add(taoLabel("Trạng thái cập nhật", 11, Font.PLAIN, MAU_CHU_PHU), BorderLayout.NORTH);

        cboTrangThaiCapNhat = new JComboBox<>(new String[]{"Đang thực hiện", "Chờ vật tư", "Đã hoàn thành", "Chờ kiểm tra"});
        cboTrangThaiCapNhat.setFont(new Font(FONT, Font.PLAIN, 12));
        cboTrangThaiCapNhat.setBackground(MAU_TRANG);
        cboTrangThaiCapNhat.setPreferredSize(new Dimension(150, 34));
        pnlTrangThai.add(cboTrangThaiCapNhat, BorderLayout.CENTER);

        pnlNoiDung.add(pnlGhiChu);
        pnlNoiDung.add(pnlTrangThai);

        pnlNhatKy.add(pnlTieuDe, BorderLayout.NORTH);
        pnlNhatKy.add(pnlNoiDung, BorderLayout.CENTER);

        return pnlNhatKy;
    }

    // =========================================================
    // 11. CÁC NÚT CHỨC NĂNG
    // =========================================================

    private JPanel taoKhuVucChucNang() {
        JPanel pnlChucNang = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlChucNang.setBackground(MAU_NEN);

        JButton btnLuuTienDo = taoNut("▣  Lưu tiến độ", MAU_XANH_DAM, MAU_TRANG);
        btnLuuTienDo.addActionListener(e -> luuTienDo());

        JButton btnQuayLai = taoNut("Quay lại danh sách", MAU_TRANG, MAU_XANH_DAM);
        btnQuayLai.setBorder(new CompoundBorder(
                new LineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));
        btnQuayLai.addActionListener(e -> quayVeDanhSachCongViec());

        JButton btnHoanThanh = taoNut("Hoàn thành & bàn giao sau khi đủ 4/4 hạng mục", MAU_TRANG, MAU_XANH_DAM);
        btnHoanThanh.setBorder(new CompoundBorder(
                new LineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));
        btnHoanThanh.addActionListener(e -> hoanThanhBanGiao());

        pnlChucNang.add(btnLuuTienDo);
        pnlChucNang.add(btnQuayLai);
        pnlChucNang.add(btnHoanThanh);

        return pnlChucNang;
    }

    private JPanel taoKhuVucChanTrang() {
        JPanel pnlChanTrang = new JPanel(new BorderLayout());
        pnlChanTrang.setBackground(MAU_NEN);
        pnlChanTrang.add(taoLabel("Dữ liệu minh họa • AutoCar PV", 10, Font.PLAIN, MAU_CHU_PHU), BorderLayout.WEST);
        pnlChanTrang.add(taoLabel("Thứ Bảy, 17/10/2026", 10, Font.PLAIN, MAU_CHU_PHU), BorderLayout.EAST);
        return pnlChanTrang;
    }

    // =========================================================
    // 12. NẠP DỮ LIỆU MẪU
    // =========================================================

    private void napDuLieuMau() {
        modelChecklist.setRowCount(0);
        modelChecklist.addRow(new Object[]{true, "Thay dầu động cơ & lọc dầu", "Đã thay 4 lít 5W-30, kiểm tra rò rỉ", "10:00", "Hoàn thành"});
        modelChecklist.addRow(new Object[]{true, "Kiểm tra phanh & đọc mã lỗi", "Má phanh còn tốt, không ghi nhận lỗi", "10:15", "Hoàn thành"});
        modelChecklist.addRow(new Object[]{false, "Thay lọc gió cabin", "PT-003 hết hàng; chờ kho bổ sung", "—", "Chờ vật tư"});
        modelChecklist.addRow(new Object[]{false, "Tra cứu lỗi & chạy thử", "Thực hiện sau khi hoàn tất thay lọc", "—", "Chưa thực hiện"});

        modelVatTu.setRowCount(0);
        modelVatTu.addRow(new Object[]{"PT-001", "Dầu động cơ 5W-30", "Lít", "4", "4", "4", "Đã cấp"});
        modelVatTu.addRow(new Object[]{"PT-002", "Lọc dầu Toyota", "Cái", "1", "1", "1", "Đã cấp"});
        modelVatTu.addRow(new Object[]{"PT-003", "Lọc gió cabin Altis", "Cái", "1", "0", "0", "Chờ vật tư"});
    }

    // =========================================================
    // 13. CẬP NHẬT TIẾN ĐỘ
    // =========================================================

    private void capNhatTienDo() {
        if (modelChecklist == null) return;
        int tongHangMuc = modelChecklist.getRowCount();
        int soHangMucHoanThanh = 0;

        for (int i = 0; i < tongHangMuc; i++) {
            if (Boolean.TRUE.equals(modelChecklist.getValueAt(i, 0))) {
                soHangMucHoanThanh++;
            }
        }

        int phanTram = tongHangMuc == 0 ? 0 : soHangMucHoanThanh * 100 / tongHangMuc;

        if (lblTienDo != null) lblTienDo.setText("Tiến độ: " + soHangMucHoanThanh + " / " + tongHangMuc + " hạng mục");
        if (lblPhanTram != null) lblPhanTram.setText(phanTram + "%");
        if (thanhTienDo != null) thanhTienDo.setValue(phanTram);

        if (lblTrangThai != null) {
            if (soHangMucHoanThanh == tongHangMuc) {
                lblTrangThai.setText("Đã hoàn thành");
                lblTrangThai.setForeground(new Color(46, 125, 50));
            } else {
                lblTrangThai.setText("Đang thực hiện");
                lblTrangThai.setForeground(MAU_XANH_DAM);
            }
        }
    }

    // =========================================================
    // 14. RENDERER PILL (HUY HIỆU BO TRÒN)
    // =========================================================

    private Color mauHang(boolean isSelected, int row) {
        if (isSelected) return MAU_XANH_NHAT;
        return (row % 2 == 0) ? MAU_TRANG : MAU_HANG_LE;
    }

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

            if (noiDung.equals("Hoàn thành") || noiDung.equals("Đã cấp")) {
                pill.setMau(new Color(46, 125, 50), new Color(232, 245, 233));
            } else if (noiDung.equals("Chờ vật tư")) {
                pill.setMau(new Color(230, 119, 0), new Color(255, 236, 209));
            } else if (noiDung.equals("Chưa thực hiện")) {
                pill.setMau(MAU_XANH_DAM, MAU_XANH_NHAT);
            } else {
                pill.setMau(MAU_XANH_DAM, MAU_XANH_NHAT);
            }
            return this;
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
    // 15. CÁC HÀM XỬ LÝ SỰ KIỆN
    // =========================================================

    private void luuTienDo() {}
    private void lapYeuCauXuatKho() {}
    private void hoanThanhBanGiao() {}
    private void quayVeDanhSachCongViec() {}

    // =========================================================
    // 16. HÀM HỖ TRỢ GIAO DIỆN
    // =========================================================

    private JLabel taoLabel(String noiDung, int coChu, int kieuChu, Color mauChu) {
        JLabel lbl = new JLabel(noiDung);
        lbl.setFont(new Font(FONT, kieuChu, coChu));
        lbl.setForeground(mauChu);
        return lbl;
    }

    private JButton taoNut(String noiDung, Color mauNen, Color mauChu) {
        JButton btn = new JButton(noiDung);
        btn.setFont(new Font(FONT, Font.BOLD, 11));
        btn.setForeground(mauChu);
        btn.setBackground(mauNen);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        return btn;
    }
}