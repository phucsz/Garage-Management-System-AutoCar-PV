package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Arc2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/**
 * ============================================================
 * BÁO CÁO THỐNG KÊ – KỸ THUẬT VIÊN
 * ============================================================
 * Đồng bộ chuẩn giao diện: Thẻ bo góc, badge dạng pill,
 * biểu đồ trực quan, bảng chi tiết theo ngày và font Segoe UI.
 */
public class BaoCaoThongKeGUI extends JPanel {

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

    private JComboBox<String> cboTuNgay;
    private JComboBox<String> cboDenNgay;
    private JTextField txtKyThuatVien;
    private JTable tblChiTiet;
    private DefaultTableModel modelChiTiet;

    // =========================================================
    // 3. CONSTRUCTOR
    // =========================================================

    public BaoCaoThongKeGUI() {
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
        pnlNoiDung.add(taoKhuVucPhamViBaoCao());
        pnlNoiDung.add(Box.createVerticalStrut(12));
        pnlNoiDung.add(taoKhuVucThongKe());
        pnlNoiDung.add(Box.createVerticalStrut(12));
        pnlNoiDung.add(taoKhuVucBieuDo());
        pnlNoiDung.add(Box.createVerticalStrut(12));
        pnlNoiDung.add(taoKhuVucBangChiTiet());
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

        pnlThongTin.add(taoLabel("Báo cáo thống kê – Kỹ thuật viên", 24, Font.BOLD, MAU_CHU));
        pnlThongTin.add(Box.createVerticalStrut(4));
        pnlThongTin.add(taoLabel("Tổng hợp khối lượng công việc và tiến độ sửa chữa theo khoảng thời gian lựa chọn.", 12, Font.PLAIN, MAU_CHU_PHU));

        JButton btnTrangChu = taoNut("← Trang chủ kỹ thuật viên", MAU_TRANG, MAU_XANH_DAM);
        btnTrangChu.setBorder(new CompoundBorder(
                new LineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));
        btnTrangChu.addActionListener(e -> quayVeTrangChu());

        pnlTieuDe.add(pnlThongTin, BorderLayout.CENTER);
        pnlTieuDe.add(btnTrangChu, BorderLayout.EAST);

        return pnlTieuDe;
    }

    // =========================================================
    // 6. PHẠM VI BÁO CÁO (BỘ LỌC)
    // =========================================================

    private JPanel taoKhuVucPhamViBaoCao() {
        JPanel pnl = new JPanel(new BorderLayout(0, 0));
        pnl.setBackground(MAU_TRANG);
        pnl.setBorder(new LineBorder(MAU_VIEN, 1, true));

        JPanel pnlTieuDe = new JPanel(new BorderLayout());
        pnlTieuDe.setBackground(MAU_XANH_NHAT);
        pnlTieuDe.setBorder(new EmptyBorder(12, 16, 12, 16));
        pnlTieuDe.add(taoLabel("Phạm vi báo cáo", 13, Font.BOLD, MAU_XANH_DAM), BorderLayout.WEST);
        pnlTieuDe.add(taoLabel("Cập nhật: 17/10/2026 • 09:41", 11, Font.PLAIN, MAU_CHU_PHU), BorderLayout.EAST);

        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(MAU_TRANG);
        pnlForm.setBorder(new EmptyBorder(14, 16, 14, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        pnlForm.add(taoTruongForm("Từ ngày", cboTuNgay = taoComboBox("12/10/2026", "01/10/2026")), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        pnlForm.add(taoTruongForm("Đến ngày", cboDenNgay = taoComboBox("17/10/2026", "31/10/2026")), gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        pnlForm.add(taoTruongForm("Kỹ thuật viên", txtKyThuatVien = taoTextFieldKhoa("Trần Minh")), gbc);

        // Nút hành động
        JPanel pnlNut = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlNut.setBackground(MAU_TRANG);

        JButton btnXem = taoNut("⚲  Xem báo cáo", MAU_XANH_DAM, MAU_TRANG);
        btnXem.setPreferredSize(new Dimension(130, 34));
        btnXem.addActionListener(e -> xemBaoCao());

        JButton btnXuat = taoNut("📥 Xuất báo cáo", MAU_TRANG, MAU_XANH_DAM);
        btnXuat.setPreferredSize(new Dimension(130, 34));
        btnXuat.setBorder(new CompoundBorder(new LineBorder(MAU_VIEN, 1, true), new EmptyBorder(5, 10, 5, 10)));
        btnXuat.addActionListener(e -> xuatBaoCao());

        pnlNut.add(btnXem);
        pnlNut.add(btnXuat);

        JPanel pnlNutWrapper = new JPanel(new BorderLayout());
        pnlNutWrapper.setBackground(MAU_TRANG);
        pnlNutWrapper.add(taoLabel(" ", 11, Font.PLAIN, MAU_CHU_PHU), BorderLayout.NORTH);
        pnlNutWrapper.add(pnlNut, BorderLayout.CENTER);

        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 1.2;
        pnlForm.add(pnlNutWrapper, gbc);

        pnl.add(pnlTieuDe, BorderLayout.NORTH);
        pnl.add(pnlForm, BorderLayout.CENTER);

        return pnl;
    }

    private JPanel taoTruongForm(String nhan, JComponent comp) {
        JPanel pnl = new JPanel(new BorderLayout(0, 5));
        pnl.setBackground(MAU_TRANG);
        pnl.add(taoLabel(nhan, 11, Font.PLAIN, MAU_CHU_PHU), BorderLayout.NORTH);
        pnl.add(comp, BorderLayout.CENTER);
        return pnl;
    }

    private JComboBox<String> taoComboBox(String... items) {
        JComboBox<String> cbo = new JComboBox<>(items);
        cbo.setFont(new Font(FONT, Font.PLAIN, 12));
        cbo.setBackground(MAU_TRANG);
        cbo.setPreferredSize(new Dimension(0, 34));
        return cbo;
    }

    private JTextField taoTextFieldKhoa(String text) {
        JTextField txt = new JTextField(text);
        txt.setFont(new Font(FONT, Font.PLAIN, 12));
        txt.setEditable(false);
        txt.setBackground(new Color(247, 249, 252));
        txt.setForeground(MAU_CHU_TOI);
        txt.setPreferredSize(new Dimension(0, 34));
        txt.setBorder(new CompoundBorder(
                new LineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return txt;
    }

    // =========================================================
    // 7. 4 THẺ THỐNG KÊ TỔNG QUAN
    // =========================================================

    private JPanel taoKhuVucThongKe() {
        JPanel pnl = new JPanel(new GridLayout(1, 4, 12, 0));
        pnl.setBackground(MAU_NEN);
        pnl.setPreferredSize(new Dimension(0, 116));

        pnl.add(taoTheThongKe("▢", "Được phân công", "28", "5 công việc được giao ngày 17/10"));
        pnl.add(taoTheThongKe("✓", "Hoàn thành", "24", "Đã bàn giao kiểm tra chất lượng"));
        pnl.add(taoTheThongKe("⚒", "Đang thực hiện", "3", "2 công việc trong ca hôm nay"));
        pnl.add(taoTheThongKe("◷", "Chờ bắt đầu", "1", "SC-2026-00142 • 17/10, 11:30"));

        return pnl;
    }

    private JPanel taoTheThongKe(String icon, String tieuDe, String giaTri, String moTa) {
        JPanel pnlThe = new JPanel(new BorderLayout(0, 4));
        pnlThe.setBackground(MAU_TRANG);
        pnlThe.setBorder(new CompoundBorder(
                new LineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

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
    // 8. BIỂU ĐỒ (CÔNG VIỆC HOÀN THÀNH & PHÂN BỐ CÔNG VIỆC)
    // =========================================================

    private JPanel taoKhuVucBieuDo() {
        JPanel pnl = new JPanel(new GridLayout(1, 2, 14, 0));
        pnl.setBackground(MAU_NEN);

        // Biểu đồ cột: Công việc hoàn thành theo ngày
        JPanel pnlCot = new JPanel(new BorderLayout());
        pnlCot.setBackground(MAU_TRANG);
        pnlCot.setBorder(new LineBorder(MAU_VIEN, 1, true));

        JPanel pnlTieuDeCot = new JPanel(new BorderLayout());
        pnlTieuDeCot.setBackground(MAU_TRANG);
        pnlTieuDeCot.setBorder(new EmptyBorder(14, 16, 8, 16));
        pnlTieuDeCot.add(taoLabel("Công việc hoàn thành theo ngày", 13, Font.BOLD, MAU_CHU), BorderLayout.WEST);
        pnlTieuDeCot.add(taoLabel("Đơn vị: công việc", 11, Font.PLAIN, MAU_CHU_PHU), BorderLayout.EAST);

        // Panel vẽ biểu đồ cột minh họa
        JPanel pnlVeCot = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                String[] ngay = {"12/10", "13/10", "14/10", "15/10", "16/10", "17/10"};
                int[] giaTri = {4, 4, 5, 5, 4, 2};
                int width = getWidth();
                int height = getHeight();
                int colWidth = width / 7;
                int maxVal = 6;

                for (int i = 0; i < ngay.length; i++) {
                    int x = colWidth * i + colWidth / 2 + 10;
                    int barH = (int)((double)giaTri[i] / maxVal * (height - 60));
                    int y = height - barH - 35;

                    // Vẽ cột
                    g2.setColor(MAU_XANH);
                    g2.fillRoundRect(x, y, 32, barH, 6, 6);

                    // Giá trị trên cột
                    g2.setColor(MAU_CHU);
                    g2.setFont(new Font(FONT, Font.BOLD, 11));
                    g2.drawString(String.valueOf(giaTri[i]), x + 10, y - 6);

                    // Nhãn ngày dưới cột
                    g2.setColor(MAU_CHU_PHU);
                    g2.setFont(new Font(FONT, Font.PLAIN, 11));
                    g2.drawString(ngay[i], x + 2, height - 15);
                }
                g2.dispose();
            }
        };
        pnlVeCot.setBackground(MAU_TRANG);
        pnlVeCot.setPreferredSize(new Dimension(0, 180));

        JLabel lblFooterCot = taoLabel("Tổng: 24 công việc • Hôm nay: 2 công việc hoàn thành", 11, Font.PLAIN, MAU_CHU_PHU);
        JPanel pnlFooterCotWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        pnlFooterCotWrapper.setBackground(MAU_TRANG);
        pnlFooterCotWrapper.add(lblFooterCot);

        pnlCot.add(pnlTieuDeCot, BorderLayout.NORTH);
        pnlCot.add(pnlVeCot, BorderLayout.CENTER);
        pnlCot.add(pnlFooterCotWrapper, BorderLayout.SOUTH);

        // Biểu đồ tròn: Phân bố công việc
        JPanel pnlTron = new JPanel(new BorderLayout());
        pnlTron.setBackground(MAU_TRANG);
        pnlTron.setBorder(new LineBorder(MAU_VIEN, 1, true));

        JPanel pnlTieuDeTron = new JPanel(new BorderLayout());
        pnlTieuDeTron.setBackground(MAU_TRANG);
        pnlTieuDeTron.setBorder(new EmptyBorder(14, 16, 8, 16));
        pnlTieuDeTron.add(taoLabel("Phân bố công việc", 13, Font.BOLD, MAU_CHU), BorderLayout.WEST);
        pnlTieuDeTron.add(taoLabel("28 công việc trong kỳ", 11, Font.PLAIN, MAU_CHU_PHU), BorderLayout.EAST);

        // Panel vẽ Donut Chart minh họa
        JPanel pnlVeTron = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = 130;
                int x = 35;
                int y = (getHeight() - size) / 2;

                // Vòng ngoài phân khúc
                g2.setColor(MAU_XANH_NHAT);
                g2.setStroke(new BasicStroke(16f));
                g2.drawOval(x, y, size, size);

                g2.setColor(MAU_XANH_DAM);
                g2.draw(new Arc2D.Double(x, y, size, size, 90, 308, Arc2D.OPEN)); // ~85.7%

                // Chữ giữa
                g2.setColor(MAU_CHU);
                g2.setFont(new Font(FONT, Font.BOLD, 18));
                g2.drawString("85,7%", x + 38, y + 62);
                g2.setFont(new Font(FONT, Font.PLAIN, 10));
                g2.setColor(MAU_CHU_PHU);
                g2.drawString("đã hoàn thành", x + 33, y + 78);

                g2.dispose();
            }
        };
        pnlVeTron.setBackground(MAU_TRANG);
        pnlVeTron.setPreferredSize(new Dimension(180, 180));

        // Chú thích bên phải biểu đồ tròn
        JPanel pnlChuThich = new JPanel();
        pnlChuThich.setLayout(new BoxLayout(pnlChuThich, BoxLayout.Y_AXIS));
        pnlChuThich.setBackground(MAU_TRANG);
        pnlChuThich.setBorder(new EmptyBorder(25, 10, 10, 16));

        pnlChuThich.add(taoDongChuThich(MAU_XANH_DAM, "Hoàn thành", "24 công việc", true));
        pnlChuThich.add(Box.createVerticalStrut(10));
        pnlChuThich.add(taoDongChuThich(MAU_XANH, "Đang thực hiện", "3 công việc", false));
        pnlChuThich.add(Box.createVerticalStrut(10));
        pnlChuThich.add(taoDongChuThich(new Color(255, 145, 0), "Chờ bắt đầu", "1 công việc", false));

        JPanel pnlTronCenter = new JPanel(new BorderLayout());
        pnlTronCenter.setBackground(MAU_TRANG);
        pnlTronCenter.add(pnlVeTron, BorderLayout.WEST);
        pnlTronCenter.add(pnlChuThich, BorderLayout.CENTER);

        JLabel lblFooterTron = taoLabel("Khối lượng còn xử lý: 4 công việc. Hoàn thành được ghi nhận sau khi bàn giao kiểm tra chất lượng.", 10, Font.PLAIN, MAU_CHU_PHU);
        JPanel pnlFooterTronWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        pnlFooterTronWrapper.setBackground(MAU_TRANG);
        pnlFooterTronWrapper.add(lblFooterTron);

        pnlTron.add(pnlTieuDeTron, BorderLayout.NORTH);
        pnlTron.add(pnlTronCenter, BorderLayout.CENTER);
        pnlTron.add(pnlFooterTronWrapper, BorderLayout.SOUTH);

        pnl.add(pnlCot);
        pnl.add(pnlTron);

        return pnl;
    }

    private JPanel taoDongChuThich(Color mau, String tieuDe, String giaTri, boolean dam) {
        JPanel pnl = new JPanel(new BorderLayout(8, 0));
        pnl.setBackground(MAU_TRANG);
        pnl.setMaximumSize(new Dimension(220, 30));

        JPanel pnlColor = new JPanel();
        pnlColor.setBackground(mau);
        pnlColor.setPreferredSize(new Dimension(10, 10));

        JPanel pnlText = new JPanel(new GridLayout(2, 1));
        pnlText.setBackground(MAU_TRANG);
        pnlText.add(taoLabel(tieuDe, 11, dam ? Font.BOLD : Font.PLAIN, MAU_CHU_TOI));
        pnlText.add(taoLabel(giaTri, 11, Font.BOLD, MAU_CHU));

        pnl.add(pnlColor, BorderLayout.WEST);
        pnl.add(pnlText, BorderLayout.CENTER);
        return pnl;
    }

    // =========================================================
    // 9. BẢNG CHI TIẾT THEO NGÀY
    // =========================================================

    private JPanel taoKhuVucBangChiTiet() {
        JPanel pnlBang = new JPanel(new BorderLayout());
        pnlBang.setBackground(MAU_TRANG);
        pnlBang.setBorder(new LineBorder(MAU_VIEN, 1, true));

        JPanel pnlTieuDe = new JPanel(new BorderLayout());
        pnlTieuDe.setBackground(MAU_XANH_NHAT);
        pnlTieuDe.setBorder(new EmptyBorder(12, 16, 12, 16));
        pnlTieuDe.add(taoLabel("Chi tiết theo ngày", 13, Font.BOLD, MAU_XANH_DAM), BorderLayout.WEST);
        pnlTieuDe.add(taoLabel("Công việc của Trần Minh • Chi nhánh Gò Vấp", 11, Font.PLAIN, MAU_CHU_PHU), BorderLayout.EAST);

        String[] tenCot = {
            "Ngày", "Được phân công", "Hoàn thành",
            "Giờ công hoàn thành", "Thời gian TB / việc", "Thao tác"
        };

        modelChiTiet = new DefaultTableModel(null, tenCot) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblChiTiet = new JTable(modelChiTiet);
        tblChiTiet.setRowHeight(46);
        tblChiTiet.setFont(new Font(FONT, Font.PLAIN, 11));
        tblChiTiet.setGridColor(MAU_VIEN);
        tblChiTiet.setShowVerticalLines(false);
        tblChiTiet.setSelectionBackground(MAU_XANH_NHAT);
        tblChiTiet.setSelectionForeground(MAU_CHU);

        JTableHeader header = tblChiTiet.getTableHeader();
        header.setFont(new Font(FONT, Font.BOLD, 10));
        header.setBackground(MAU_XANH_NHAT);
        header.setForeground(MAU_XANH_DAM);
        header.setPreferredSize(new Dimension(0, 34));

        tblChiTiet.getColumnModel().getColumn(0).setPreferredWidth(120);
        tblChiTiet.getColumnModel().getColumn(1).setPreferredWidth(110);
        tblChiTiet.getColumnModel().getColumn(2).setPreferredWidth(110);
        tblChiTiet.getColumnModel().getColumn(3).setPreferredWidth(140);
        tblChiTiet.getColumnModel().getColumn(4).setPreferredWidth(140);
        tblChiTiet.getColumnModel().getColumn(5).setPreferredWidth(110);

        tblChiTiet.getColumnModel().getColumn(0).setCellRenderer(new RendererChu(true, MAU_XANH_DAM, "", false));
        tblChiTiet.getColumnModel().getColumn(1).setCellRenderer(new RendererChu(false, MAU_CHU_TOI, "", false));
        tblChiTiet.getColumnModel().getColumn(2).setCellRenderer(new RendererChu(false, MAU_CHU_TOI, "", false));
        tblChiTiet.getColumnModel().getColumn(3).setCellRenderer(new RendererChu(false, MAU_CHU_TOI, "", false));
        tblChiTiet.getColumnModel().getColumn(4).setCellRenderer(new RendererChu(false, MAU_CHU_TOI, "", false));
        tblChiTiet.getColumnModel().getColumn(5).setCellRenderer(new RendererChu(true, MAU_XANH_DAM, "→  ", false));

        // Sự kiện click nút thao tác
        tblChiTiet.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int hang = tblChiTiet.rowAtPoint(e.getPoint());
                int cot = tblChiTiet.columnAtPoint(e.getPoint());
                if (hang >= 0 && cot == 5) {
                    xemChiTietNgay(hang);
                }
            }
        });

        JScrollPane scr = new JScrollPane(tblChiTiet);
        scr.setBorder(null);
        scr.getViewport().setBackground(MAU_TRANG);

        pnlBang.add(pnlTieuDe, BorderLayout.NORTH);
        pnlBang.add(scr, BorderLayout.CENTER);

        return pnlBang;
    }

    // =========================================================
    // 10. RENDERER PHỤ TRỢ (CHỮ)
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

    // =========================================================
    // 11. CHÂN TRANG
    // =========================================================

    private JPanel taoKhuVucChanTrang() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(MAU_NEN);
        pnl.add(taoLabel("Dữ liệu minh họa • AutoCar PV", 10, Font.PLAIN, MAU_CHU_PHU), BorderLayout.WEST);
        pnl.add(taoLabel("Thứ Bảy, 17/10/2026", 10, Font.PLAIN, MAU_CHU_PHU), BorderLayout.EAST);
        return pnl;
    }

    // =========================================================
    // 12. NẠP DỮ LIỆU MẪU
    // =========================================================

    private void napDuLieuMau() {
        modelChiTiet.setRowCount(0);

        modelChiTiet.addRow(new Object[]{"12/10/2026", "5", "4", "7 giờ", "1 giờ 45 phút", "Xem công việc"});
        modelChiTiet.addRow(new Object[]{"13/10/2026", "4", "4", "7 giờ", "1 giờ 45 phút", "Xem công việc"});
        modelChiTiet.addRow(new Object[]{"14/10/2026", "5", "5", "8 giờ 30 phút", "1 giờ 42 phút", "Xem công việc"});
        modelChiTiet.addRow(new Object[]{"15/10/2026", "5", "5", "9 giờ", "1 giờ 48 phút", "Xem công việc"});
        modelChiTiet.addRow(new Object[]{"16/10/2026", "4", "4", "7 giờ", "1 giờ 45 phút", "Xem công việc"});
        modelChiTiet.addRow(new Object[]{"17/10/2026", "5", "2", "3 giờ 30 phút", "1 giờ 45 phút", "Xem công việc"});
        modelChiTiet.addRow(new Object[]{"Tổng cộng", "28", "24", "42 giờ", "1 giờ 45 phút", ""});
    }

    // =========================================================
    // 13. ICON TRÒN PHỤ TRỢ
    // =========================================================

    private static class IconTron extends JComponent {
        private static final long serialVersionUID = 1L;
        private final int kichThuoc;
        private final Color mauNen, mauChu;
        private final String kyTu;

        IconTron(int kichThuoc, Color mauNen, Color mauChu, String kyTu) {
            this.kichThuoc = kichThuoc;
            this.mauNen = mauNen;
            this.mauChu = mauChu;
            this.kyTu = kyTu;
            setPreferredSize(new Dimension(kichThuoc, kichThuoc));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int y = (getHeight() - kichThuoc) / 2;
            g2.setColor(mauNen);
            g2.fillOval(0, y, kichThuoc - 1, kichThuoc - 1);

            g2.setColor(mauChu);
            g2.setFont(new Font(FONT, Font.PLAIN, kichThuoc / 2));
            FontMetrics fm = g2.getFontMetrics();
            int x = (kichThuoc - fm.stringWidth(kyTu)) / 2;
            int yChu = y + (kichThuoc - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(kyTu, x, yChu);
            g2.dispose();
        }
    }

    // =========================================================
    // 14. XỬ LÝ SỰ KIỆN & ĐIỀU HƯỚNG
    // =========================================================

    private void xemBaoCao() {
        // TODO: Lọc dữ liệu theo khoảng thời gian chọn
    }

    private void xuatBaoCao() {
        // TODO: Xuất file báo cáo thống kê (PDF / Excel)
    }

    private void xemChiTietNgay(int hang) {
        // TODO: Xem chi tiết công việc của ngày tương ứng
    }

    private void quayVeTrangChu() {
        // TODO: Điều hướng về TrangChuKyThuatVienGUI
    }

    // =========================================================
    // 15. HÀM HỖ TRỢ GIAO DIỆN
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