package gui;

import business.HoaDonBUS;
import entity.HoaDon;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class DanhSachHoaDonGUI extends JPanel {

    private static final long serialVersionUID = 1L;

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
    private static final String FONT_ICON = "Segoe UI Symbol";

    public interface ChuyenTrangListener {
        void chuyenDenThanhToan(String maHoaDon);
        void quayVeTrangChu();
    }

    private ChuyenTrangListener listener;
    private HoaDonBUS hoaDonBUS;

    private JTextField txtMaPhieu;
    private JTextField txtKhachHang;
    private JTextField txtBienSo;
    private JComboBox<String> cboTuNgay;
    private JComboBox<String> cboDenNgay;
    private JComboBox<String> cboTrangThai;

    private JTable tblHoaDon;
    private DefaultTableModel modelHoaDon;

    private JLabel lblGiaTriTongHoaDes;
    private JLabel lblGiaTriDaThanhToan;
    private JLabel lblGiaTriChoThanhToan;
    private JLabel lblSoBanGhi;

    public DanhSachHoaDonGUI() {
        this.hoaDonBUS = new HoaDonBUS();
        khoiTaoGiaoDien();
    }

    public void setChuyenTrangListener(ChuyenTrangListener listener) {
        this.listener = listener;
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(MAU_NEN);
        setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel pnlNoiDung = new JPanel();
        pnlNoiDung.setLayout(new BoxLayout(pnlNoiDung, BoxLayout.Y_AXIS));
        pnlNoiDung.setBackground(MAU_NEN);

        pnlNoiDung.add(taoKhuVucTieuDe());
        pnlNoiDung.add(Box.createVerticalStrut(14));
        pnlNoiDung.add(taoKhuVucBoLoc());
        pnlNoiDung.add(Box.createVerticalStrut(12));
        pnlNoiDung.add(taoKhuVucThongKe());
        pnlNoiDung.add(Box.createVerticalStrut(12));
        pnlNoiDung.add(taoKhuVucBangHoaDon());
        pnlNoiDung.add(Box.createVerticalStrut(12));
        pnlNoiDung.add(taoKhuVucGhiChu());
        pnlNoiDung.add(Box.createVerticalStrut(12));
        pnlNoiDung.add(taoKhuVucChanTrang());

        JScrollPane scrNoiDung = new JScrollPane(pnlNoiDung);
        scrNoiDung.setBorder(null);
        scrNoiDung.getViewport().setBackground(MAU_NEN);
        scrNoiDung.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrNoiDung, BorderLayout.CENTER);
    }

    private JPanel taoKhuVucTieuDe() {
        JPanel pnlTieuDe = new JPanel(new BorderLayout(10, 0));
        pnlTieuDe.setBackground(MAU_NEN);

        JPanel pnlThongTin = new JPanel();
        pnlThongTin.setLayout(new BoxLayout(pnlThongTin, BoxLayout.Y_AXIS));
        pnlThongTin.setBackground(MAU_NEN);

        pnlThongTin.add(taoLabel("Danh sách hóa đơn", 24, Font.BOLD, MAU_CHU));
        pnlThongTin.add(Box.createVerticalStrut(4));
        pnlThongTin.add(taoLabel(
                "Tra cứu hóa đơn sửa chữa, kiểm tra công nợ và thực hiện thanh toán cho khách hàng.",
                12, Font.PLAIN, MAU_CHU_PHU));

        JButton btnTrangChu = taoNut("← Trang chủ thu ngân", MAU_TRANG, MAU_XANH_DAM);
        btnTrangChu.setBorder(new CompoundBorder(
                new LineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));
        btnTrangChu.addActionListener(e -> {
            if (listener != null) {
                listener.quayVeTrangChu();
            }
        });

        pnlTieuDe.add(pnlThongTin, BorderLayout.CENTER);
        pnlTieuDe.add(btnTrangChu, BorderLayout.EAST);

        return pnlTieuDe;
    }

    private JPanel taoKhuVucBoLoc() {
        JPanel pnlBoLoc = new JPanel(new BorderLayout(0, 0));
        pnlBoLoc.setBackground(MAU_TRANG);
        pnlBoLoc.setBorder(new LineBorder(MAU_VIEN, 1, true));

        JPanel pnlTieuDe = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        pnlTieuDe.setBackground(MAU_XANH_NHAT);
        pnlTieuDe.add(taoLabel("Thông tin tra cứu hóa đơn", 13, Font.BOLD, MAU_XANH_DAM));

        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(MAU_TRANG);
        pnlForm.setBorder(new EmptyBorder(14, 16, 14, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        pnlForm.add(taoTruongForm("Mã hóa đơn / Phiếu sửa chữa", txtMaPhieu = taoTextField("Nhập mã hóa đơn hoặc phiếu")), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        pnlForm.add(taoTruongForm("Khách hàng / Số điện thoại", txtKhachHang = taoTextField("Nhập tên hoặc số điện thoại")), gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.insets = new Insets(0, 0, 10, 0);
        pnlForm.add(taoTruongForm("Biển số xe", txtBienSo = taoTextField("Nhập biển số xe")), gbc);

        gbc.insets = new Insets(0, 0, 0, 12);
        gbc.gridx = 0; gbc.gridy = 1;
        pnlForm.add(taoTruongForm("Từ ngày", cboTuNgay = taoComboBox("", "01/10/2026", "02/10/2026", "03/10/2026", "04/10/2026", "05/10/2026", "06/10/2026", "07/10/2026", "08/10/2026", "09/10/2026", "10/10/2026", "17/10/2026")), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        pnlForm.add(taoTruongForm("Đến ngày", cboDenNgay = taoComboBox("", "17/10/2026", "18/10/2026")), gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        pnlForm.add(taoTruongForm("Trạng thái thanh toán", cboTrangThai = taoComboBox("Tất cả trạng thái", "Chờ thanh toán", "Đã thanh toán")), gbc);

        JPanel pnlNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlNut.setBackground(MAU_TRANG);

        JButton btnTraCuu = taoNut("🔍  Tra cứu", MAU_XANH_DAM, MAU_TRANG);
        btnTraCuu.setPreferredSize(new Dimension(110, 34));
        btnTraCuu.addActionListener(e -> traCuuHoaDon());

        JButton btnDatLai = taoNut("Đặt lại", MAU_TRANG, MAU_CHU_PHU);
        btnDatLai.setPreferredSize(new Dimension(80, 34));
        btnDatLai.setBorder(new CompoundBorder(new LineBorder(MAU_VIEN, 1, true), new EmptyBorder(5, 10, 5, 10)));
        btnDatLai.addActionListener(e -> datLaiBoLoc());

        pnlNut.add(btnTraCuu);
        pnlNut.add(btnDatLai);

        JPanel pnlWrapperNut = new JPanel(new BorderLayout());
        pnlWrapperNut.setBackground(MAU_TRANG);
        pnlWrapperNut.add(taoLabel(" ", 11, Font.PLAIN, MAU_CHU_PHU), BorderLayout.NORTH);
        pnlWrapperNut.add(pnlNut, BorderLayout.CENTER);

        gbc.gridx = 3; gbc.gridy = 1; gbc.insets = new Insets(0, 0, 0, 0); gbc.weightx = 0;
        pnlForm.add(pnlWrapperNut, gbc);

        pnlBoLoc.add(pnlTieuDe, BorderLayout.NORTH);
        pnlBoLoc.add(pnlForm, BorderLayout.CENTER);

        return pnlBoLoc;
    }

    private JPanel taoTruongForm(String nhan, JComponent comp) {
        JPanel pnl = new JPanel(new BorderLayout(0, 5));
        pnl.setBackground(MAU_TRANG);
        pnl.add(taoLabel(nhan, 11, Font.PLAIN, MAU_CHU_PHU), BorderLayout.NORTH);
        pnl.add(comp, BorderLayout.CENTER);
        return pnl;
    }

    private JTextField taoTextField(String placeholder) {
        JTextField txt = new JTextField();
        txt.setFont(new Font(FONT, Font.PLAIN, 12));
        txt.setPreferredSize(new Dimension(0, 34));
        txt.setBorder(new CompoundBorder(
                new LineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return txt;
    }

    private JComboBox<String> taoComboBox(String... items) {
        JComboBox<String> cbo = new JComboBox<>(items);
        cbo.setFont(new Font(FONT, Font.PLAIN, 12));
        cbo.setBackground(MAU_TRANG);
        cbo.setPreferredSize(new Dimension(0, 34));
        return cbo;
    }

    private JPanel taoKhuVucThongKe() {
        JPanel pnl = new JPanel(new GridLayout(1, 3, 14, 0));
        pnl.setBackground(MAU_NEN);
        pnl.setPreferredSize(new Dimension(0, 116));

        lblGiaTriTongHoaDes = taoLabel("0", 26, Font.BOLD, MAU_CHU);
        lblGiaTriDaThanhToan = taoLabel("0", 26, Font.BOLD, MAU_CHU);
        lblGiaTriChoThanhToan = taoLabel("0", 26, Font.BOLD, MAU_CHU);

        pnl.add(taoTheThongKe("💵", "Tổng hóa đơn", lblGiaTriTongHoaDes, "Tổng giá trị: 0 đ"));
        pnl.add(taoTheThongKe("✓", "Đã thanh toán", lblGiaTriDaThanhToan, "Đã thu: 0 đ"));
        pnl.add(taoTheThongKe("⏳", "Chờ thanh toán", lblGiaTriChoThanhToan, "Còn phải thu: 0 đ"));

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

    private JPanel taoKhuVucBangHoaDon() {
        JPanel pnlBang = new JPanel(new BorderLayout(0, 0));
        pnlBang.setBackground(MAU_TRANG);
        pnlBang.setBorder(new LineBorder(MAU_VIEN, 1, true));

        JPanel pnlTieuDe = new JPanel(new BorderLayout());
        pnlTieuDe.setBackground(MAU_XANH_NHAT);
        pnlTieuDe.setBorder(new EmptyBorder(14, 16, 8, 16));

        pnlTieuDe.add(taoLabel("Kết quả tra cứu hóa đơn", 13, Font.BOLD, MAU_XANH_DAM), BorderLayout.WEST);
        pnlTieuDe.add(taoLabel("0 kết quả • Đơn vị tiền: VNĐ", 10, Font.PLAIN, MAU_CHU_PHU), BorderLayout.EAST);

        String[] tenCot = {
            "Mã hóa đơn", "Ngày lập", "Khách hàng / Xe",
            "Tổng tiền", "Đã thu", "Trạng thái", "Thao tác"
        };

        modelHoaDon = new DefaultTableModel(null, tenCot) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblHoaDon = new JTable(modelHoaDon);
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

        int[] doRong = {140, 110, 240, 130, 130, 130, 110};
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
        cm.getColumn(3).setCellRenderer(new RendererChu(false, MAU_CHU_TOI, "", false));
        cm.getColumn(4).setCellRenderer(new RendererChu(false, MAU_CHU_TOI, "", false));
        cm.getColumn(5).setCellRenderer(new RendererPill());
        cm.getColumn(6).setCellRenderer(new RendererChu(true, MAU_XANH_DAM, "→  ", false));

        tblHoaDon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int hang = tblHoaDon.rowAtPoint(e.getPoint());
                int cot = tblHoaDon.columnAtPoint(e.getPoint());

                if (hang >= 0 && cot == 6) {
                    String thaoTac = modelHoaDon.getValueAt(hang, 6).toString();
                    String maHoaDon = modelHoaDon.getValueAt(hang, 0).toString();

                    if (thaoTac.contains("Thanh toán")) {
                        if (listener != null) {
                            listener.chuyenDenThanhToan(maHoaDon);
                        } else {
                            JOptionPane.showMessageDialog(DanhSachHoaDonGUI.this,
                                    "Chuyển sang màn hình Thanh toán hóa đơn: " + maHoaDon,
                                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else if (thaoTac.contains("Xem")) {
                        JOptionPane.showMessageDialog(DanhSachHoaDonGUI.this,
                                "Xem chi tiết/In chứng từ cho hóa đơn: " + maHoaDon,
                                "Chi tiết hóa đơn", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });

        tblHoaDon.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int hang = tblHoaDon.rowAtPoint(e.getPoint());
                int cot = tblHoaDon.columnAtPoint(e.getPoint());
                tblHoaDon.setCursor((hang >= 0 && cot == 6) ? new Cursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
            }
        });

        JScrollPane scrBang = new JScrollPane(tblHoaDon);
        scrBang.setBorder(null);
        scrBang.getViewport().setBackground(MAU_TRANG);
        scrBang.setColumnHeaderView(header);
        scrBang.getColumnHeader().setBackground(MAU_XANH_NHAT);

        pnlBang.add(pnlTieuDe, BorderLayout.NORTH);
        pnlBang.add(scrBang, BorderLayout.CENTER);

        return pnlBang;
    }

    private Color mauHang(boolean isSelected, int row) {
        if (isSelected) return MAU_XANH_NHAT;
        return (row % 2 == 0) ? MAU_TRANG : MAU_HANG_LE;
    }

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



    private JPanel taoKhuVucGhiChu() {
        JPanel pnlGhiChu = new JPanel(new BorderLayout(12, 0));
        pnlGhiChu.setBackground(MAU_XANH_NHAT);
        pnlGhiChu.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel lblIcon = taoLabel("ⓘ", 14, Font.BOLD, MAU_XANH_DAM);
        JLabel lblNoiDung = taoLabel(
                "Hóa đơn chờ thanh toán có thể mở để thu tiền. Hóa đơn đã thanh toán chỉ xem chi tiết hoặc in chứng từ.",
                11, Font.PLAIN, MAU_XANH_DAM);

        pnlGhiChu.add(lblIcon, BorderLayout.WEST);
        pnlGhiChu.add(lblNoiDung, BorderLayout.CENTER);

        return pnlGhiChu;
    }

    private JPanel taoKhuVucChanTrang() {
        JPanel pnlChanTrang = new JPanel(new BorderLayout());
        pnlChanTrang.setBackground(MAU_NEN);

        pnlChanTrang.add(taoLabel("Dữ liệu minh họa • AutoCar PV", 10, Font.PLAIN, MAU_CHU_PHU), BorderLayout.WEST);
        pnlChanTrang.add(taoLabel("Thứ Bảy, 17/10/2026", 10, Font.PLAIN, MAU_CHU_PHU), BorderLayout.EAST);

        return pnlChanTrang;
    }

    private void traCuuHoaDon() {
        modelHoaDon.setRowCount(0);

        List<HoaDon> dsHD = hoaDonBUS.layDanhSachHoaDon();

        String tuKhoaMa = txtMaPhieu.getText().trim().toLowerCase();
        String tuKhoaKH = txtKhachHang.getText().trim().toLowerCase();
        String tuKhoaXe = txtBienSo.getText().trim().toLowerCase();
        String trangThaiChon = cboTrangThai.getSelectedItem().toString();

        String tuNgayStr = cboTuNgay.getSelectedItem() != null ? cboTuNgay.getSelectedItem().toString().trim() : "";
        String denNgayStr = cboDenNgay.getSelectedItem() != null ? cboDenNgay.getSelectedItem().toString().trim() : "";

        DateTimeFormatter dtfInput = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate tuNgay = null;
        LocalDate denNgay = null;
        try {
            if (!tuNgayStr.isEmpty()) tuNgay = LocalDate.parse(tuNgayStr, dtfInput);
            if (!denNgayStr.isEmpty()) denNgay = LocalDate.parse(denNgayStr, dtfInput);
        } catch (Exception e) {
            // Bỏ qua lỗi định dạng ngày nếu có
        }

        int demTong = 0;
        int demDaThanhToan = 0;
        int demChoThanhToan = 0;
        double tongGiaTri = 0;
        double tongDaThu = 0;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (HoaDon hd : dsHD) {
            String maHD = hd.getMaHoaDon() != null ? hd.getMaHoaDon() : "";
            String maPSC = (hd.getPhieuSuaChua() != null && hd.getPhieuSuaChua().getMaPSC() != null) ? hd.getPhieuSuaChua().getMaPSC() : "";
            String trangThaiHD = hd.getTrangThai() != null ? hd.getTrangThai() : "Chưa thanh toán";
            
            // 1. Lọc theo mã hóa đơn hoặc phiếu
            if (!tuKhoaMa.isEmpty() && !maHD.toLowerCase().contains(tuKhoaMa) && !maPSC.toLowerCase().contains(tuKhoaMa)) {
                continue;
            }

            // 2. Lọc theo khách hàng / số điện thoại
            String tenKH = (hd.getKhachHang() != null && hd.getKhachHang().getHoTenKH() != null) ? hd.getKhachHang().getHoTenKH() : "";
            String sdtKH = (hd.getKhachHang() != null && hd.getKhachHang().getSdt() != null) ? hd.getKhachHang().getSdt() : "";
            if (!tuKhoaKH.isEmpty() && !tenKH.toLowerCase().contains(tuKhoaKH) && !sdtKH.toLowerCase().contains(tuKhoaKH)) {
                continue;
            }

            // 3. Lọc theo biển số xe
            String bienSo = hoaDonBUS.getBienSoXeByPSC(maPSC);
            if (!tuKhoaXe.isEmpty() && !bienSo.toLowerCase().contains(tuKhoaXe)) {
                continue;
            }

            // 4. Lọc theo từ ngày đến ngày
            if (hd.getNgayLap() != null) {
                LocalDate ngayLapDate = hd.getNgayLap().toLocalDate();
                if (tuNgay != null && ngayLapDate.isBefore(tuNgay)) {
                    continue;
                }
                if (denNgay != null && ngayLapDate.isAfter(denNgay)) {
                    continue;
                }
            } else if (tuNgay != null || denNgay != null) {
                continue; // Bỏ qua nếu hóa đơn không có ngày lập mà người dùng có chọn lọc ngày
            }

            // 5. Lọc theo trạng thái thanh toán
            boolean khopTrangThai = false;
            if ("Tất cả trạng thái".equals(trangThaiChon)) {
                khopTrangThai = true;
            } else if ("Đã thanh toán".equalsIgnoreCase(trangThaiChon)) {
                khopTrangThai = trangThaiHD.equalsIgnoreCase("Đã thanh toán");
            } else if ("Chờ thanh toán".equalsIgnoreCase(trangThaiChon)) {
                khopTrangThai = trangThaiHD.equalsIgnoreCase("Chờ thanh toán") || trangThaiHD.equalsIgnoreCase("Chưa thanh toán");
            }
            if (!khopTrangThai) {
                continue;
            }

            demTong++;
            tongGiaTri += hd.getTongTienThanhToan();

            String ngayLapStr = hd.getNgayLap() != null ? hd.getNgayLap().format(formatter) : "N/A";
            String hienThiKH = !tenKH.isEmpty() ? tenKH : "Khách lẻ";
            
            boolean daThanhToan = trangThaiHD.equalsIgnoreCase("Đã thanh toán");
            double daThu = daThanhToan ? hd.getTongTienThanhToan() : 0;
            
            if (daThanhToan) {
                demDaThanhToan++;
                tongDaThu += daThu;
            } else {
                demChoThanhToan++;
            }

            String trangThaiHienThi = daThanhToan ? "Đã thanh toán" : "Chờ thanh toán";
            String thaoTac = daThanhToan ? "Xem - In" : "Thanh toán";

            modelHoaDon.addRow(new Object[]{
                maHD,
                ngayLapStr,
                hienThiKH + "\n" + bienSo,
                String.format("%,.0f", hd.getTongTienThanhToan()),
                String.format("%,.0f", daThu),
                trangThaiHienThi,
                thaoTac
            });
        }

        lblGiaTriTongHoaDes.setText(String.valueOf(demTong));
        lblGiaTriDaThanhToan.setText(String.valueOf(demDaThanhToan));
        lblGiaTriChoThanhToan.setText(String.valueOf(demChoThanhToan));
        if (lblSoBanGhi != null) {
            lblSoBanGhi.setText("Hiển thị " + demTong + " / " + demTong + " bản ghi");
        }

        if (demTong == 0) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn nào phù hợp với điều kiện tra cứu!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void datLaiBoLoc() {
        txtMaPhieu.setText("");
        txtKhachHang.setText("");
        txtBienSo.setText("");
        cboTuNgay.setSelectedIndex(0);
        cboDenNgay.setSelectedIndex(0);
        cboTrangThai.setSelectedIndex(0);
        
        modelHoaDon.setRowCount(0);
        lblGiaTriTongHoaDes.setText("0");
        lblGiaTriDaThanhToan.setText("0");
        lblGiaTriChoThanhToan.setText("0");
        if (lblSoBanGhi != null) {
            lblSoBanGhi.setText("Hiển thị 0 / 0 bản ghi");
        }
    }

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
            g2.setFont(new Font(FONT_ICON, Font.PLAIN, kichThuoc / 2));
            FontMetrics fm = g2.getFontMetrics();
            int x = (kichThuoc - fm.stringWidth(kyTu)) / 2;
            int yChu = y + (kichThuoc - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(kyTu, x, yChu);
            g2.dispose();
        }
    }
}