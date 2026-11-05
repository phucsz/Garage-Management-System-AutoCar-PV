package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/**
 * ============================================================
 * YÊU CẦU XUẤT KHO - KỸ THUẬT VIÊN
 * ============================================================
 * Đồng bộ chuẩn giao diện: Thẻ bo góc, badge dạng pill,
 * font chữ chuẩn Segoe UI, chiều cao bảng 46px.
 */
public class YeuCauXuatKhoGUI extends JPanel {

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
    // 2. THÀNH PHẦN GIAO DIỆN
    // =========================================================

    private JComboBox<String> cboPhieuSuaChua;
    private JTextField txtXeKhachHang;
    private JTextField txtNguoiYeuCau;
    private JTextField txtNgayYeuCau;
    private JComboBox<String> cboThoiGianCan;
    private JComboBox<String> cboMucUuTien;
    private JTable tblPhuTung;
    private DefaultTableModel modelPhuTung;
    private JTextArea txtLyDo;
    private JTextField txtNoiNhan;

    // =========================================================
    // 3. CONSTRUCTOR
    // =========================================================

    public YeuCauXuatKhoGUI() {
        khoiTaoGiaoDien();
        napDuLieuMau();
    }

    // =========================================================
    // 4. KHỞI TẠO GIAO DIỆN CHÍNH
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
        pnlNoiDung.add(taoKhuVucThongTinYeuCau());
        pnlNoiDung.add(Box.createVerticalStrut(12));
        pnlNoiDung.add(taoKhuVucCanhBao());
        pnlNoiDung.add(Box.createVerticalStrut(12));
        pnlNoiDung.add(taoKhuVucPhuTung());
        pnlNoiDung.add(Box.createVerticalStrut(12));
        pnlNoiDung.add(taoKhuVucLyDoVaTomTat());
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
    // 5. TIÊU ĐỀ TRANG
    // =========================================================

    private JPanel taoKhuVucTieuDe() {
        JPanel pnlTieuDe = new JPanel(new BorderLayout(10, 0));
        pnlTieuDe.setBackground(MAU_NEN);

        JPanel pnlThongTin = new JPanel();
        pnlThongTin.setLayout(new BoxLayout(pnlThongTin, BoxLayout.Y_AXIS));
        pnlThongTin.setBackground(MAU_NEN);

        pnlThongTin.add(taoLabel("Yêu cầu xuất kho", 24, Font.BOLD, MAU_CHU));
        pnlThongTin.add(Box.createVerticalStrut(4));
        pnlThongTin.add(taoLabel("Lập đề nghị cấp vật tư cho công việc sửa chữa và kiểm tra số lượng khả dụng.", 12, Font.PLAIN, MAU_CHU_PHU));

        JButton btnQuayLai = taoNut("← Công việc phụ trách", MAU_TRANG, MAU_XANH_DAM);
        btnQuayLai.setBorder(new CompoundBorder(
                new LineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));
        btnQuayLai.addActionListener(e -> quayVeCongViecPhuTrach());

        pnlTieuDe.add(pnlThongTin, BorderLayout.CENTER);
        pnlTieuDe.add(btnQuayLai, BorderLayout.EAST);

        return pnlTieuDe;
    }

    // =========================================================
    // 6. THÔNG TIN YÊU CẦU
    // =========================================================

    private JPanel taoKhuVucThongTinYeuCau() {
        JPanel pnlThongTin = new JPanel(new BorderLayout(0, 0));
        pnlThongTin.setBackground(MAU_TRANG);
        pnlThongTin.setBorder(new LineBorder(MAU_VIEN, 1, true));

        JPanel pnlTieuDe = new JPanel(new BorderLayout());
        pnlTieuDe.setBackground(MAU_XANH_NHAT);
        pnlTieuDe.setBorder(new EmptyBorder(12, 16, 12, 16));
        pnlTieuDe.add(taoLabel("Thông tin yêu cầu", 13, Font.BOLD, MAU_XANH_DAM), BorderLayout.WEST);
        pnlTieuDe.add(taoLabel("Yêu cầu mới • XK-2026-00059", 11, Font.PLAIN, MAU_CHU_PHU), BorderLayout.EAST);

        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(MAU_TRANG);
        pnlForm.setBorder(new EmptyBorder(16, 16, 16, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Dòng 1
        gbc.gridx = 0; gbc.gridy = 0;
        pnlForm.add(taoTruongForm("Phiếu sửa chữa *", cboPhieuSuaChua = taoComboBox("SC-2026-00142", "SC-2026-00141")), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        pnlForm.add(taoTruongForm("Xe / Khách hàng", txtXeKhachHang = taoTextField("51G-123.45 • Nguyễn Văn A", false)), gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        pnlForm.add(taoTruongForm("Người yêu cầu", txtNguoiYeuCau = taoTextField("Trần Minh • NV-003", false)), gbc);

        // Dòng 2
        gbc.gridx = 0; gbc.gridy = 1; gbc.insets = new Insets(0, 0, 0, 12);
        pnlForm.add(taoTruongForm("Ngày yêu cầu", txtNgayYeuCau = taoTextField("17/10/2026 • 10:20", false)), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        pnlForm.add(taoTruongForm("Thời gian cần vật tư *", cboThoiGianCan = taoComboBox("17/10/2026 • Trước 11:00", "17/10/2026 • Trước 14:00")), gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        pnlForm.add(taoTruongForm("Mức ưu tiên", cboMucUuTien = taoComboBox("Cao • Xe hẹn trả 11:30", "Bình thường")), gbc);

        pnlThongTin.add(pnlTieuDe, BorderLayout.NORTH);
        pnlThongTin.add(pnlForm, BorderLayout.CENTER);

        return pnlThongTin;
    }

    private JPanel taoTruongForm(String nhan, JComponent comp) {
        JPanel pnl = new JPanel(new BorderLayout(0, 5));
        pnl.setBackground(MAU_TRANG);
        pnl.add(taoLabel(nhan, 11, Font.PLAIN, MAU_CHU_PHU), BorderLayout.NORTH);
        pnl.add(comp, BorderLayout.CENTER);
        return pnl;
    }

    private JTextField taoTextField(String text, boolean editable) {
        JTextField txt = new JTextField(text);
        txt.setFont(new Font(FONT, Font.PLAIN, 12));
        txt.setEditable(editable);
        txt.setPreferredSize(new Dimension(0, 34));
        txt.setBorder(new CompoundBorder(
                new LineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        if (!editable) {
            txt.setBackground(new Color(247, 249, 252));
            txt.setForeground(MAU_CHU_TOI);
        }
        return txt;
    }

    private JComboBox<String> taoComboBox(String... items) {
        JComboBox<String> cbo = new JComboBox<>(items);
        cbo.setFont(new Font(FONT, Font.PLAIN, 12));
        cbo.setBackground(MAU_TRANG);
        cbo.setPreferredSize(new Dimension(0, 34));
        return cbo;
    }

    // =========================================================
    // 7. KHU VỰC CẢNH BÁO
    // =========================================================

    private JPanel taoKhuVucCanhBao() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        pnl.setBackground(new Color(255, 243, 224));
        pnl.setBorder(new LineBorder(new Color(255, 183, 77), 1, true));

        JLabel lblIcon = taoLabel("⚠", 14, Font.BOLD, new Color(230, 119, 0));
        JLabel lblText = taoLabel("PT-003 đang hết hàng. Yêu cầu vẫn có thể gửi để bộ phận kho bổ sung; chỉ xác nhận xuất khi đủ tồn khả dụng.", 11, Font.PLAIN, new Color(133, 77, 14));

        pnl.add(lblIcon);
        pnl.add(lblText);
        return pnl;
    }

    // =========================================================
    // 8. PHỤ TÙNG ĐỀ NGHỊ CẤP
    // =========================================================

    private JPanel taoKhuVucPhuTung() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(MAU_TRANG);
        pnl.setBorder(new LineBorder(MAU_VIEN, 1, true));

        JPanel pnlTieuDe = new JPanel(new BorderLayout());
        pnlTieuDe.setBackground(MAU_XANH_NHAT);
        pnlTieuDe.setBorder(new EmptyBorder(12, 16, 12, 16));
        pnlTieuDe.add(taoLabel("Phụ tùng đề nghị cấp", 13, Font.BOLD, MAU_XANH_DAM), BorderLayout.WEST);
        pnlTieuDe.add(taoLabel("1 loại phụ tùng • Số lượng theo ĐVT", 11, Font.PLAIN, MAU_CHU_PHU), BorderLayout.EAST);

        String[] tenCot = {"Mã phụ tùng", "Tên phụ tùng", "ĐVT", "Yêu cầu", "Tồn kho", "Khả dụng", "Đánh giá", "Thao tác"};
        modelPhuTung = new DefaultTableModel(null, tenCot) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblPhuTung = new JTable(modelPhuTung);
        tblPhuTung.setRowHeight(46);
        tblPhuTung.setFont(new Font(FONT, Font.PLAIN, 11));
        tblPhuTung.setGridColor(MAU_VIEN);
        tblPhuTung.setShowVerticalLines(false);
        tblPhuTung.setSelectionBackground(MAU_XANH_NHAT);

        JTableHeader header = tblPhuTung.getTableHeader();
        header.setFont(new Font(FONT, Font.BOLD, 10));
        header.setBackground(MAU_XANH_NHAT);
        header.setForeground(MAU_XANH_DAM);
        header.setPreferredSize(new Dimension(0, 34));

        tblPhuTung.getColumnModel().getColumn(0).setPreferredWidth(90);
        tblPhuTung.getColumnModel().getColumn(1).setPreferredWidth(220);
        tblPhuTung.getColumnModel().getColumn(2).setPreferredWidth(60);
        tblPhuTung.getColumnModel().getColumn(3).setPreferredWidth(60);
        tblPhuTung.getColumnModel().getColumn(4).setPreferredWidth(60);
        tblPhuTung.getColumnModel().getColumn(5).setPreferredWidth(70);
        tblPhuTung.getColumnModel().getColumn(6).setPreferredWidth(100);
        tblPhuTung.getColumnModel().getColumn(7).setPreferredWidth(90);

        tblPhuTung.getColumnModel().getColumn(6).setCellRenderer(new RendererPill());
        tblPhuTung.getColumnModel().getColumn(7).setCellRenderer(new RendererChu(true, MAU_XANH_DAM, "", false));

        JScrollPane scr = new JScrollPane(tblPhuTung);
        scr.setBorder(null);
        scr.getViewport().setBackground(MAU_TRANG);

        JPanel pnlThaoTac = new JPanel(new BorderLayout());
        pnlThaoTac.setBackground(MAU_TRANG);
        pnlThaoTac.setBorder(new EmptyBorder(12, 16, 12, 16));

        JButton btnThem = taoNut("+ Thêm phụ tùng", MAU_TRANG, MAU_XANH_DAM);
        btnThem.setBorder(new CompoundBorder(new LineBorder(MAU_VIEN, 1, true), new EmptyBorder(6, 12, 6, 12)));
        btnThem.addActionListener(e -> themPhuTung());

        JLabel lblNote = taoLabel("Khả dụng = Tồn kho – Số lượng đã giữ chỗ", 11, Font.PLAIN, MAU_CHU_PHU);

        pnlThaoTac.add(btnThem, BorderLayout.WEST);
        pnlThaoTac.add(lblNote, BorderLayout.EAST);

        pnl.add(pnlTieuDe, BorderLayout.NORTH);
        pnl.add(scr, BorderLayout.CENTER);
        pnl.add(pnlThaoTac, BorderLayout.SOUTH);

        return pnl;
    }

    // =========================================================
    // 9. LÝ DO & TÓM TẮT YÊU CẦU
    // =========================================================

    private JPanel taoKhuVucLyDoVaTomTat() {
        JPanel pnl = new JPanel(new GridLayout(1, 2, 14, 0));
        pnl.setBackground(MAU_NEN);

        // Trái: Lý do & ghi chú
        JPanel pnlTrai = new JPanel(new BorderLayout());
        pnlTrai.setBackground(MAU_TRANG);
        pnlTrai.setBorder(new LineBorder(MAU_VIEN, 1, true));

        JPanel pnlTieuDeTrai = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        pnlTieuDeTrai.setBackground(MAU_XANH_NHAT);
        pnlTieuDeTrai.add(taoLabel("Lý do & Ghi chú", 13, Font.BOLD, MAU_XANH_DAM));

        JPanel pnlFormTrai = new JPanel();
        pnlFormTrai.setLayout(new BoxLayout(pnlFormTrai, BoxLayout.Y_AXIS));
        pnlFormTrai.setBackground(MAU_TRANG);
        pnlFormTrai.setBorder(new EmptyBorder(14, 16, 16, 16));

        pnlFormTrai.add(taoLabel("Lý do xuất kho *", 11, Font.PLAIN, MAU_CHU_PHU));
        pnlFormTrai.add(Box.createVerticalStrut(5));

        txtLyDo = new JTextArea(3, 20);
        txtLyDo.setFont(new Font(FONT, Font.PLAIN, 12));
        txtLyDo.setLineWrap(true);
        txtLyDo.setWrapStyleWord(true);
        txtLyDo.setText("Thay lọc gió cabin theo phiếu SC-2026-00142. Vật tư cũ bẩn, không thể tái sử dụng.");
        txtLyDo.setBorder(new CompoundBorder(new LineBorder(MAU_VIEN, 1, true), new EmptyBorder(8, 10, 8, 10)));
        JScrollPane scrLyDo = new JScrollPane(txtLyDo);
        scrLyDo.setBorder(null);
        pnlFormTrai.add(scrLyDo);

        pnlFormTrai.add(Box.createVerticalStrut(12));
        pnlFormTrai.add(taoLabel("Nơi nhận vật tư", 11, Font.PLAIN, MAU_CHU_PHU));
        pnlFormTrai.add(Box.createVerticalStrut(5));
        txtNoiNhan = taoTextField("Cầu nâng 01 • Trần Minh", true);
        pnlFormTrai.add(txtNoiNhan);

        pnlTrai.add(pnlTieuDeTrai, BorderLayout.NORTH);
        pnlTrai.add(pnlFormTrai, BorderLayout.CENTER);

        // Phải: Tóm tắt yêu cầu
        JPanel pnlPhai = new JPanel(new BorderLayout());
        pnlPhai.setBackground(MAU_TRANG);
        pnlPhai.setBorder(new LineBorder(MAU_VIEN, 1, true));

        JPanel pnlTieuDePhai = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        pnlTieuDePhai.setBackground(MAU_XANH_NHAT);
        pnlTieuDePhai.add(taoLabel("Tóm tắt yêu cầu", 13, Font.BOLD, MAU_XANH_DAM));

        JPanel pnlFormPhai = new JPanel();
        pnlFormPhai.setLayout(new BoxLayout(pnlFormPhai, BoxLayout.Y_AXIS));
        pnlFormPhai.setBackground(MAU_TRANG);
        pnlFormPhai.setBorder(new EmptyBorder(16, 16, 16, 16));

        pnlFormPhai.add(taoDongTomTat("Số loại phụ tùng:", "1 loại", false));
        pnlFormPhai.add(Box.createVerticalStrut(10));
        pnlFormPhai.add(taoDongTomTat("Tổng số lượng đề nghị:", "1 cái", false));
        pnlFormPhai.add(Box.createVerticalStrut(10));
        pnlFormPhai.add(taoDongTomTat("Số lượng còn thiếu:", "1 cái", true));
        pnlFormPhai.add(Box.createVerticalStrut(20));

        JLabel lblWorkflow = taoLabel("<html>Sau khi gửi: <b>Chờ xử lý</b> → Kho bổ sung vật tư → Xác nhận cấp phát. Theo dõi kết quả tại công việc phụ trách.</html>", 11, Font.PLAIN, MAU_CHU_PHU);
        pnlFormPhai.add(lblWorkflow);

        pnlPhai.add(pnlTieuDePhai, BorderLayout.NORTH);
        pnlPhai.add(pnlFormPhai, BorderLayout.CENTER);

        pnl.add(pnlTrai);
        pnl.add(pnlPhai);

        return pnl;
    }

    private JPanel taoDongTomTat(String nhan, String giaTri, boolean noiBat) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(MAU_TRANG);
        pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        pnl.add(taoLabel(nhan, 12, Font.PLAIN, noiBat ? MAU_CHU : MAU_CHU_PHU), BorderLayout.WEST);
        pnl.add(taoLabel(giaTri, noiBat ? 16 : 12, noiBat ? Font.BOLD : Font.PLAIN, noiBat ? new Color(211, 47, 47) : MAU_CHU), BorderLayout.EAST);
        return pnl;
    }

    // =========================================================
    // 10. CÁC NÚT CHỨC NĂNG & CHÂN TRANG
    // =========================================================

    private JPanel taoKhuVucChucNang() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnl.setBackground(MAU_NEN);

        JButton btnGui = taoNut("▤  Gửi yêu cầu xuất kho", MAU_XANH_DAM, MAU_TRANG);
        btnGui.addActionListener(e -> guiYeuCau());

        JButton btnHuy = taoNut("Hủy", MAU_TRANG, MAU_CHU_PHU);
        btnHuy.setBorder(new CompoundBorder(new LineBorder(MAU_VIEN, 1, true), new EmptyBorder(8, 14, 8, 14)));
        btnHuy.addActionListener(e -> huyYeuCau());

        JButton btnXemCu = taoNut("Xem yêu cầu đã cấp XK-2026-00058", MAU_NEN, MAU_XANH_DAM);
        btnXemCu.setBorder(new EmptyBorder(8, 10, 8, 10));
        btnXemCu.addActionListener(e -> xemYeuCauCu());

        pnl.add(btnGui);
        pnl.add(btnHuy);
        pnl.add(Box.createHorizontalStrut(10));
        pnl.add(btnXemCu);

        return pnl;
    }

    private JPanel taoKhuVucChanTrang() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(MAU_NEN);
        pnl.add(taoLabel("Dữ liệu minh họa • AutoCar PV", 10, Font.PLAIN, MAU_CHU_PHU), BorderLayout.WEST);
        pnl.add(taoLabel("Thứ Bảy, 17/10/2026", 10, Font.PLAIN, MAU_CHU_PHU), BorderLayout.EAST);
        return pnl;
    }

    // =========================================================
    // 11. NẠP DỮ LIỆU MẪU
    // =========================================================

    private void napDuLieuMau() {
        modelPhuTung.setRowCount(0);
        modelPhuTung.addRow(new Object[]{
            "PT-003",
            "Lọc gió cabin Altis",
            "Cái",
            "1",
            "0",
            "0",
            "Hết hàng",
            "→ Sửa - Xóa"
        });
    }

    // =========================================================
    // 12. RENDERER PHỤ TRỢ (PILL & CHỮ)
    // =========================================================

    private Color mauHang(boolean isSelected, int row) {
        if (isSelected) return MAU_XANH_NHAT;
        return (row % 2 == 0) ? MAU_TRANG : MAU_HANG_LE;
    }

    private class RendererChu extends JLabel implements TableCellRenderer {
        private static final long serialVersionUID = 1L;
        private final String tienTo;

        public RendererChu(boolean dam, Color mau, String tienTo, boolean nhieuDong) {
            this.tienTo = tienTo;
            setOpaque(true);
            setForeground(mau);
            setFont(new Font(FONT, dam ? Font.BOLD : Font.PLAIN, 11));
            setBorder(new EmptyBorder(0, 16, 0, 8));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText(tienTo + (value == null ? "" : value.toString()));
            setBackground(mauHang(isSelected, row));
            return this;
        }
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

            if (noiDung.equals("Hết hàng")) {
                pill.setMau(new Color(198, 40, 40), new Color(255, 225, 229));
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
    // 13. XỬ LÝ SỰ KIỆN & ĐIỀU HƯỚNG
    // =========================================================

    private void themPhuTung() {}
    private void guiYeuCau() {}
    private void huyYeuCau() {}
    private void xemYeuCauCu() {}
    private void quayVeCongViecPhuTrach() {}

    // =========================================================
    // 14. HÀM HỖ TRỢ GIAO DIỆN
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