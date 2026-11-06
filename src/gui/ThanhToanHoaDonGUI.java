package gui;

import business.HoaDonBUS;
import dao.ChiTietDichVuDAO;
import dao.ChiTietPhuTungDAO;
import entity.HoaDon;
import entity.KhachHang;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;

/**
 * ============================================================
 * THANH TOÁN CHI TIẾT HÓA ĐƠN – THU NGÂN
 * ============================================================
 * Cách dùng: tạo 1 lần, mỗi lần mở một hóa đơn thì gọi {@link #napHoaDon(String)}.
 * Dữ liệu hiển thị lấy từ HoaDonBUS (hóa đơn, khách hàng, biển số, chi tiết dịch vụ, chi tiết phụ tùng).
 */
public class ThanhToanHoaDonGUI extends JPanel {

    private static final long serialVersionUID = 1L;

    // =========================================================
    // 1. MÀU SẮC / HẰNG SỐ
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
    private static final Color MAU_DO        = new Color(211, 47, 47);
    private static final Color MAU_TRANG     = Color.WHITE;

    private static final Color MAU_CAM_CHU   = new Color(230, 119, 0);
    private static final Color MAU_CAM_NEN   = new Color(255, 236, 209);
    private static final Color MAU_LUC_CHU    = new Color(46, 125, 50);
    private static final Color MAU_LUC_NEN    = new Color(232, 245, 233);

    private static final String FONT = "Segoe UI";
    private static final String TT_DA_THANH_TOAN = "Đã thanh toán";
    private static final String TT_CHO_THANH_TOAN = "Chờ thanh toán";

    private static final DateTimeFormatter FMT_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_GIO = DateTimeFormatter.ofPattern("HH:mm");

    /** Callback điều hướng về Trang chủ thu ngân. */
    public interface ThanhToanListener {
        void quayVeDanhSach();
        void xacNhanThanhToanThanhCong(String maHoaDon);
    }

    private ThanhToanListener listener;
    private final HoaDonBUS hoaDonBUS = new HoaDonBUS();

    // Dữ liệu đang hiển thị
    private String maHoaDon = "";
    private HoaDon hoaDonHienTai;
    private String tenThuNgan = "";
    private boolean daThanhToanXong = false;
    private long giamGia = 0;
    private long daThu = 0;
    private long tongPhaiTra = 0;
    private long conPhaiThanhToan = 0;

    // Thành phần giao diện
    private JScrollPane scrNoiDung;
    private JLabel lblPhuDe;
    private JLabel lblThoiGianLap;
    private JLabel lblDiaChi;
    private JLabel lblSoHangMucCong;
    private JLabel lblSoHangMucPT;
    private JLabel lblNgayChanTrang;
    private JTextField txtKhachHang;
    private JTextField txtXe;
    private JTextField txtPhieuSC;
    private JTextField txtGiaoDich;
    private PillLabel pillTrangThai;

    private DefaultTableModel modelTienCong;
    private DefaultTableModel modelPhuTung;
    private JComboBox<String> cboPhuongThuc;
    private JTextField txtKhachDua;
    private JTextField txtTienThua;
    private JCheckBox chkXacNhan;
    private NutBo btnXacNhan;
    private JButton btnIn;

    private JLabel lblTongCong;
    private JLabel lblTongPhuTung;
    private JLabel lblTruocThue;
    private JLabel lblGiamGia;
    private JLabel lblDaThanhToan;
    private JLabel lblConPhai;
    private JPanel dongGiamGia;

    // =========================================================
    // 2. CONSTRUCTOR & API CÔNG KHAI
    // =========================================================

    public ThanhToanHoaDonGUI() {
        khoiTaoGiaoDien();
        xoaDuLieu();
    }

    public ThanhToanHoaDonGUI(String maHD) {
        this();
        napHoaDon(maHD);
    }

    public void setThanhToanListener(ThanhToanListener listener) {
        this.listener = listener;
    }

    /** Tên thu ngân đang đăng nhập (hiện ở mục "Thu ngân / Thời gian giao dịch"). */
    public void setThuNgan(String hoTen) {
        this.tenThuNgan = hoTen == null ? "" : hoTen;
    }

    /**
     * Nạp dữ liệu của một hóa đơn lên màn hình.
     * @return true nếu tìm thấy hóa đơn; false nếu không (màn hình giữ nguyên, có báo lỗi)
     */
    public boolean napHoaDon(String maHD) {
        HoaDon hd = hoaDonBUS.timHoaDonTheoMa(maHD);
        if (hd == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn: " + maHD,
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        this.hoaDonHienTai = hd;
        this.maHoaDon = hd.getMaHoaDon();
        this.daThanhToanXong = TT_DA_THANH_TOAN.equalsIgnoreCase(hd.getTrangThai());

        String maPSC = (hd.getPhieuSuaChua() != null && hd.getPhieuSuaChua().getMaPSC() != null)
                ? hd.getPhieuSuaChua().getMaPSC() : "";

        // ----- Thông tin hóa đơn -----
        lblPhuDe.setText(maHoaDon + " • Kiểm tra dịch vụ và phụ tùng trước khi xác nhận thanh toán.");
        lblThoiGianLap.setText(hd.getNgayLap() == null ? ""
                : "Lập lúc " + hd.getNgayLap().format(FMT_GIO) + " • " + hd.getNgayLap().format(FMT_NGAY));

        KhachHang kh = hd.getKhachHang();
        String ten = (kh != null && kh.getHoTenKH() != null && !kh.getHoTenKH().isEmpty()) ? kh.getHoTenKH() : "Khách lẻ";
        String sdt = (kh != null && kh.getSdt() != null) ? kh.getSdt() : "";
        txtKhachHang.setText(sdt.isEmpty() ? ten : ten + " • " + sdt);
        lblDiaChi.setText(kh != null && kh.getDiaChi() != null ? kh.getDiaChi() : "");

        txtXe.setText(maPSC.isEmpty() ? "" : hoaDonBUS.getThongTinXeByPSC(maPSC));
        txtPhieuSC.setText(maPSC);

        // ----- Chi tiết tiền công & phụ tùng -----
        modelTienCong.setRowCount(0);
        List<ChiTietDichVuDAO.DongDichVu> dsDV = hoaDonBUS.layChiTietDichVu(maPSC);
        for (ChiTietDichVuDAO.DongDichVu d : dsDV) {
            modelTienCong.addRow(new Object[]{
                    d.maDichVu, d.tenDichVu, String.valueOf(d.soLuong),
                    dinhDangTien(Math.round(d.donGia)), dinhDangTien(Math.round(d.thanhTien()))});
        }
        lblSoHangMucCong.setText(dsDV.size() + " hạng mục • VNĐ");

        modelPhuTung.setRowCount(0);
        List<ChiTietPhuTungDAO.DongPhuTung> dsPT = hoaDonBUS.layChiTietPhuTung(maPSC);
        for (ChiTietPhuTungDAO.DongPhuTung d : dsPT) {
            modelPhuTung.addRow(new Object[]{
                    d.maPhuTung, d.tenPhuTung, d.donViTinh == null ? "" : d.donViTinh, String.valueOf(d.soLuong),
                    dinhDangTien(Math.round(d.donGia)), dinhDangTien(Math.round(d.thanhTien()))});
        }
        lblSoHangMucPT.setText(dsPT.size() + " hạng mục • VNĐ");

        // ----- Số tiền -----
        giamGia = Math.round(hd.getSoTienGiamGia());
        daThu = daThanhToanXong ? Math.round(hd.getTongTienThanhToan()) : 0;

        // ----- Thanh toán -----
        txtGiaoDich.setText(tenThuNgan + " • " + LocalDate.now().format(FMT_NGAY) + " • "
                + LocalDateTime.now().format(FMT_GIO));
        chkXacNhan.setSelected(daThanhToanXong);

        cboPhuongThuc.setSelectedIndex(0);
        if (daThanhToanXong && hd.getHinhThucThanhToan() != null) {
            for (int i = 0; i < cboPhuongThuc.getItemCount(); i++) {
                if (cboPhuongThuc.getItemAt(i).equalsIgnoreCase(hd.getHinhThucThanhToan().trim())) {
                    cboPhuongThuc.setSelectedIndex(i);
                }
            }
        }

        tinhTongHop();
        txtKhachDua.setText(dinhDangTien(daThanhToanXong ? daThu : conPhaiThanhToan));
        capNhatTrangThaiForm();

        SwingUtilities.invokeLater(() -> scrNoiDung.getVerticalScrollBar().setValue(0));
        return true;
    }

    // =========================================================
    // 3. KHỞI TẠO GIAO DIỆN CHÍNH
    // =========================================================

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(MAU_NEN);

        JPanel pnlNoiDung = new PanelCuon(new GridBagLayout());
        pnlNoiDung.setBackground(MAU_NEN);
        pnlNoiDung.setBorder(new EmptyBorder(18, 20, 18, 20));

        themDong(pnlNoiDung, taoKhuVucTieuDe(), 0);
        themDong(pnlNoiDung, taoKhuVucThongTinHoaDon(), 14);
        themDong(pnlNoiDung, taoKhuVucTienCong(), 12);
        themDong(pnlNoiDung, taoKhuVucPhuTung(), 12);
        themDong(pnlNoiDung, taoKhuVucThanhToanVaTongHop(), 12);
        themDong(pnlNoiDung, taoKhuVucChucNang(), 12);
        themDong(pnlNoiDung, taoKhuVucChanTrang(), 12);
        themDem(pnlNoiDung);

        scrNoiDung = new JScrollPane(pnlNoiDung);
        scrNoiDung.setBorder(null);
        scrNoiDung.getViewport().setBackground(MAU_NEN);
        scrNoiDung.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrNoiDung.getVerticalScrollBar().setUnitIncrement(18);

        add(scrNoiDung, BorderLayout.CENTER);
    }

    // =========================================================
    // 4. TIÊU ĐỀ TRANG
    // =========================================================

    private JPanel taoKhuVucTieuDe() {
        JPanel pnlTieuDe = new JPanel(new BorderLayout(10, 0));
        pnlTieuDe.setOpaque(false);

        lblPhuDe = taoLabel(" ", 12, Font.PLAIN, MAU_CHU_PHU);

        JPanel pnlThongTin = new JPanel(new GridBagLayout());
        pnlThongTin.setOpaque(false);
        themDong(pnlThongTin, taoLabel("Thanh toán chi tiết hóa đơn", 24, Font.BOLD, MAU_CHU), 0);
        themDong(pnlThongTin, lblPhuDe, 4);

        NutBo btnDanhSach = new NutBo("←  Danh sách hóa đơn", MAU_TRANG, MAU_XANH_DAM, true);
        btnDanhSach.addActionListener(e -> {
            if (listener != null) listener.quayVeDanhSach();
        });

        JPanel pnlNut = new JPanel(new GridBagLayout());
        pnlNut.setOpaque(false);
        pnlNut.add(btnDanhSach);

        pnlTieuDe.add(pnlThongTin, BorderLayout.CENTER);
        pnlTieuDe.add(pnlNut, BorderLayout.EAST);
        return pnlTieuDe;
    }

    // =========================================================
    // 5. THÔNG TIN HÓA ĐƠN
    // =========================================================

    private JPanel taoKhuVucThongTinHoaDon() {
        Card pnl = new Card(new BorderLayout());

        txtKhachHang = taoTextFieldKhoa("");
        txtXe = taoTextFieldKhoa("");
        txtPhieuSC = taoTextFieldKhoa("");

        JPanel pnlForm = new JPanel(new GridLayout(1, 3, 12, 0));
        pnlForm.setBackground(MAU_TRANG);
        pnlForm.setBorder(new EmptyBorder(16, 16, 10, 16));
        pnlForm.add(taoTruongForm("Khách hàng / Số điện thoại", txtKhachHang));
        pnlForm.add(taoTruongForm("Biển số / Dòng xe", txtXe));
        pnlForm.add(taoTruongForm("Phiếu sửa chữa liên kết", txtPhieuSC));

        lblDiaChi = taoLabel(" ", 11, Font.PLAIN, MAU_CHU_PHU);
        pillTrangThai = new PillLabel();

        JPanel pnlDuoi = new JPanel(new BorderLayout());
        pnlDuoi.setBackground(MAU_TRANG);
        pnlDuoi.setBorder(new EmptyBorder(0, 16, 14, 16));
        pnlDuoi.add(lblDiaChi, BorderLayout.WEST);

        JPanel pnlBadge = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlBadge.setOpaque(false);
        pnlBadge.add(pillTrangThai);
        pnlDuoi.add(pnlBadge, BorderLayout.EAST);

        lblThoiGianLap = taoLabel(" ", 11, Font.PLAIN, MAU_CHU_PHU);

        pnl.add(taoTieuDeCard("Thông tin hóa đơn", lblThoiGianLap), BorderLayout.NORTH);
        pnl.add(pnlForm, BorderLayout.CENTER);
        pnl.add(pnlDuoi, BorderLayout.SOUTH);
        return pnl;
    }

    private JPanel taoTruongForm(String nhan, JComponent comp) {
        JPanel pnl = new JPanel(new BorderLayout(0, 5));
        pnl.setBackground(MAU_TRANG);
        pnl.add(taoLabel(nhan, 11, Font.PLAIN, MAU_CHU_PHU), BorderLayout.NORTH);
        pnl.add(comp, BorderLayout.CENTER);
        return pnl;
    }

    private JTextField taoTextFieldKhoa(String text) {
        JTextField txt = new JTextField(text);
        txt.setFont(new Font(FONT, Font.PLAIN, 12));
        txt.setEditable(false);
        txt.setBackground(MAU_HANG_LE);
        txt.setForeground(MAU_CHU_TOI);
        txt.setPreferredSize(new Dimension(0, 34));
        txt.setBorder(new CompoundBorder(
                new LineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(5, 10, 5, 10)));
        return txt;
    }

    // =========================================================
    // 6. CHI TIẾT TIỀN CÔNG
    // =========================================================

    private JPanel taoKhuVucTienCong() {
        String[] tenCot = {"Mã dịch vụ", "Dịch vụ / Tiền công", "Số lượng", "Đơn giá", "Thành tiền"};
        modelTienCong = new DefaultTableModel(null, tenCot) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = taoBang(modelTienCong, new int[]{100, 300, 80, 120, 120});

        lblSoHangMucCong = taoLabel("0 hạng mục • VNĐ", 11, Font.PLAIN, MAU_CHU_PHU);
        return taoCardBang("Chi tiết tiền công", lblSoHangMucCong, tbl);
    }

    // =========================================================
    // 7. CHI TIẾT PHỤ TÙNG
    // =========================================================

    private JPanel taoKhuVucPhuTung() {
        String[] tenCot = {"Mã phụ tùng", "Phụ tùng / Vật tư", "ĐVT", "Số lượng", "Đơn giá", "Thành tiền"};
        modelPhuTung = new DefaultTableModel(null, tenCot) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = taoBang(modelPhuTung, new int[]{100, 260, 60, 80, 110, 110});

        lblSoHangMucPT = taoLabel("0 hạng mục • VNĐ", 11, Font.PLAIN, MAU_CHU_PHU);
        return taoCardBang("Chi tiết phụ tùng", lblSoHangMucPT, tbl);
    }

    /** Card gồm tiêu đề + header bảng + bảng (không lồng JScrollPane để chuột cuộn trang mượt). */
    private JPanel taoCardBang(String tieuDe, JLabel phai, JTable bang) {
        Card pnl = new Card(new BorderLayout());
        JPanel pnlBang = new JPanel(new BorderLayout());
        pnlBang.setBackground(MAU_TRANG);
        pnlBang.add(bang.getTableHeader(), BorderLayout.NORTH);
        pnlBang.add(bang, BorderLayout.CENTER);

        pnl.add(taoTieuDeCard(tieuDe, phai), BorderLayout.NORTH);
        pnl.add(pnlBang, BorderLayout.CENTER);
        return pnl;
    }

    private JTable taoBang(DefaultTableModel model, int[] doRong) {
        JTable tbl = new JTable(model);
        tbl.setRowHeight(46);
        tbl.setFont(new Font(FONT, Font.PLAIN, 11));
        tbl.setGridColor(MAU_VIEN);
        tbl.setShowVerticalLines(false);
        tbl.setShowHorizontalLines(true);
        tbl.setIntercellSpacing(new Dimension(0, 1));
        tbl.setSelectionBackground(MAU_XANH_NHAT);
        tbl.setSelectionForeground(MAU_CHU_TOI);
        tbl.setFillsViewportHeight(true);

        JTableHeader header = tbl.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setDefaultRenderer(new RendererTieuDe());
        header.setPreferredSize(new Dimension(0, 34));

        int cuoi = doRong.length - 1;
        for (int i = 0; i <= cuoi; i++) {
            TableColumn col = tbl.getColumnModel().getColumn(i);
            col.setPreferredWidth(doRong[i]);
            if (i == 0)         col.setCellRenderer(new RendererChu(true, MAU_XANH_DAM));
            else if (i == cuoi) col.setCellRenderer(new RendererChu(true, MAU_CHU));
            else                col.setCellRenderer(new RendererChu(false, MAU_CHU_TOI));
        }
        return tbl;
    }

    // =========================================================
    // 8. THÔNG TIN THANH TOÁN & TỔNG HỢP (2 CỘT)
    // =========================================================

    private JPanel taoKhuVucThanhToanVaTongHop() {
        JPanel pnl = new JPanel(new GridLayout(1, 2, 14, 0));
        pnl.setOpaque(false);
        pnl.add(taoCardThongTinThanhToan());
        pnl.add(taoCardTongHop());
        return pnl;
    }

    private JPanel taoCardThongTinThanhToan() {
        Card pnl = new Card(new BorderLayout());

        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(MAU_TRANG);
        pnlForm.setBorder(new EmptyBorder(14, 16, 16, 16));

        themDong(pnlForm, taoLabel("Phương thức thanh toán *", 11, Font.PLAIN, MAU_CHU_PHU), 0);
        cboPhuongThuc = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "Thẻ tín dụng"});
        cboPhuongThuc.setFont(new Font(FONT, Font.PLAIN, 12));
        cboPhuongThuc.setBackground(MAU_TRANG);
        cboPhuongThuc.setPreferredSize(new Dimension(0, 34));
        cboPhuongThuc.addActionListener(e -> capNhatTheoPhuongThuc());
        themDong(pnlForm, cboPhuongThuc, 5);

        txtKhachDua = new JTextField("0");
        txtKhachDua.setFont(new Font(FONT, Font.PLAIN, 12));
        txtKhachDua.setPreferredSize(new Dimension(0, 34));
        txtKhachDua.setBorder(new CompoundBorder(new LineBorder(MAU_VIEN, 1, true), new EmptyBorder(5, 10, 5, 10)));

        txtTienThua = new JTextField("0");
        txtTienThua.setFont(new Font(FONT, Font.PLAIN, 12));
        txtTienThua.setEditable(false);
        txtTienThua.setBackground(MAU_HANG_LE);
        txtTienThua.setForeground(MAU_CHU_TOI);
        txtTienThua.setPreferredSize(new Dimension(0, 34));
        txtTienThua.setBorder(new CompoundBorder(new LineBorder(MAU_VIEN, 1, true), new EmptyBorder(5, 10, 5, 10)));

        JPanel pnlTien = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlTien.setBackground(MAU_TRANG);
        pnlTien.add(taoTruongForm("Khách đưa (VNĐ) *", txtKhachDua));
        pnlTien.add(taoTruongForm("Tiền thừa trả khách (VNĐ)", txtTienThua));
        themDong(pnlForm, pnlTien, 12);

        txtKhachDua.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { capNhatTienThua(); }
            @Override public void removeUpdate(DocumentEvent e)  { capNhatTienThua(); }
            @Override public void changedUpdate(DocumentEvent e) { capNhatTienThua(); }
        });
        txtKhachDua.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                if (txtKhachDua.isEditable()) {
                    txtKhachDua.setText(dinhDangTien(parseTien(txtKhachDua.getText())));
                }
            }
        });

        txtGiaoDich = taoTextFieldKhoa("");
        themDong(pnlForm, taoLabel("Thu ngân / Thời gian giao dịch", 11, Font.PLAIN, MAU_CHU_PHU), 12);
        themDong(pnlForm, txtGiaoDich, 5);

        chkXacNhan = new JCheckBox(" Đã kiểm tra hóa đơn và số tiền khách thanh toán");
        chkXacNhan.setFont(new Font(FONT, Font.PLAIN, 11));
        chkXacNhan.setForeground(MAU_CHU_TOI);
        chkXacNhan.setBackground(MAU_TRANG);
        chkXacNhan.setFocusPainted(false);
        themDong(pnlForm, chkXacNhan, 12);
        themDem(pnlForm);

        pnl.add(taoTieuDeCard("Thông tin thanh toán", null), BorderLayout.NORTH);
        pnl.add(pnlForm, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel taoCardTongHop() {
        Card pnl = new Card(new BorderLayout());

        lblTongCong    = taoLabel("0 đ", 12, Font.BOLD, MAU_CHU);
        lblTongPhuTung = taoLabel("0 đ", 12, Font.BOLD, MAU_CHU);
        lblTruocThue   = taoLabel("0 đ", 12, Font.BOLD, MAU_CHU);
        lblGiamGia     = taoLabel("0 đ", 12, Font.BOLD, MAU_CHU);
        lblDaThanhToan = taoLabel("0 đ", 12, Font.BOLD, MAU_CHU);
        lblConPhai     = taoLabel("0 đ", 18, Font.BOLD, MAU_DO);

        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(MAU_TRANG);
        pnlForm.setBorder(new EmptyBorder(16, 16, 16, 16));

        dongGiamGia = taoDongTongHop("Giảm giá:", lblGiamGia, false);

        themDong(pnlForm, taoDongTongHop("Tổng tiền công:", lblTongCong, false), 0);
        themDong(pnlForm, taoDongTongHop("Tổng phụ tùng:", lblTongPhuTung, false), 12);
        themDong(pnlForm, taoDongTongHop("Tổng cộng:", lblTruocThue, false), 12);
        themDong(pnlForm, dongGiamGia, 12);                 // chỉ hiện khi có giảm giá
        themDong(pnlForm, taoDongTongHop("Đã thanh toán:", lblDaThanhToan, false), 12);
        themDong(pnlForm, taoDongTongHop("Còn phải thanh toán", lblConPhai, true), 16);
        themDem(pnlForm);

        pnl.add(taoTieuDeCard("Tổng hợp thanh toán", null), BorderLayout.NORTH);
        pnl.add(pnlForm, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel taoDongTongHop(String nhan, JLabel lblGiaTri, boolean noiBat) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(MAU_TRANG);
        pnl.add(taoLabel(nhan, noiBat ? 12 : 11, noiBat ? Font.BOLD : Font.PLAIN,
                noiBat ? MAU_CHU : MAU_CHU_PHU), BorderLayout.WEST);
        pnl.add(lblGiaTri, BorderLayout.EAST);
        return pnl;
    }

    // =========================================================
    // 9. NÚT CHỨC NĂNG & CHÂN TRANG
    // =========================================================

    private JPanel taoKhuVucChucNang() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnl.setOpaque(false);

        btnXacNhan = new NutBo("✓  Xác nhận thanh toán", MAU_XANH_DAM, MAU_TRANG, false);
        btnXacNhan.addActionListener(e -> xacNhanThanhToan());

        NutBo btnQuayLai = new NutBo("Quay lại", MAU_TRANG, MAU_CHU_PHU, true);
        btnQuayLai.addActionListener(e -> {
            if (listener != null) listener.quayVeDanhSach();
        });

        btnIn = new JButton("<html><u>In hóa đơn sau khi thanh toán</u></html>");
        btnIn.setFont(new Font(FONT, Font.BOLD, 11));
        btnIn.setForeground(MAU_XANH_DAM);
        btnIn.setContentAreaFilled(false);
        btnIn.setBorderPainted(false);
        btnIn.setFocusPainted(false);
        btnIn.setOpaque(false);
        btnIn.setBorder(new EmptyBorder(9, 6, 9, 6));
        btnIn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIn.addActionListener(e -> inHoaDon());

        pnl.add(btnXacNhan);
        pnl.add(btnQuayLai);
        pnl.add(btnIn);
        return pnl;
    }

    private JPanel taoKhuVucChanTrang() {
        lblNgayChanTrang = taoLabel(thuVN(LocalDate.now()) + ", " + LocalDate.now().format(FMT_NGAY),
                10, Font.PLAIN, MAU_CHU_PHU);
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setOpaque(false);
        pnl.add(taoLabel("AutoCar PV", 10, Font.PLAIN, MAU_CHU_PHU), BorderLayout.WEST);
        pnl.add(lblNgayChanTrang, BorderLayout.EAST);
        return pnl;
    }

    // =========================================================
    // 10. TÍNH TOÁN & TRẠNG THÁI FORM
    // =========================================================

    /** Xóa sạch dữ liệu (trạng thái ban đầu khi chưa mở hóa đơn nào). */
    private void xoaDuLieu() {
        hoaDonHienTai = null;
        maHoaDon = "";
        daThanhToanXong = false;
        giamGia = 0;
        daThu = 0;
        modelTienCong.setRowCount(0);
        modelPhuTung.setRowCount(0);
        txtKhachHang.setText("");
        txtXe.setText("");
        txtPhieuSC.setText("");
        txtGiaoDich.setText("");
        lblDiaChi.setText(" ");
        lblThoiGianLap.setText(" ");
        lblPhuDe.setText("Chưa chọn hóa đơn.");
        tinhTongHop();
        capNhatTrangThaiForm();
    }

    /** Tính tổng hợp từ dữ liệu 2 bảng. */
    private void tinhTongHop() {
        long tongCong = tongCot(modelTienCong, 4);
        long tongPhuTung = tongCot(modelPhuTung, 5);
        long truocThue = tongCong + tongPhuTung;   // CSDL không tính thuế GTGT: tổng = dịch vụ + phụ tùng - giảm giá

        tongPhaiTra = Math.max(0, truocThue - giamGia);
        conPhaiThanhToan = Math.max(0, tongPhaiTra - daThu);

        lblTongCong.setText(dinhDangTien(tongCong) + " đ");
        lblTongPhuTung.setText(dinhDangTien(tongPhuTung) + " đ");
        lblTruocThue.setText(dinhDangTien(truocThue) + " đ");
        lblGiamGia.setText("- " + dinhDangTien(giamGia) + " đ");
        dongGiamGia.setVisible(giamGia > 0);
        lblDaThanhToan.setText(dinhDangTien(daThu) + " đ");
        lblConPhai.setText(dinhDangTien(conPhaiThanhToan) + " đ");

        capNhatTienThua();
    }

    /** Bật/tắt các ô nhập, nút bấm và nhãn trạng thái theo hóa đơn đã/chưa thanh toán. */
    private void capNhatTrangThaiForm() {
        if (daThanhToanXong) {
            pillTrangThai.setText(TT_DA_THANH_TOAN);
            pillTrangThai.setMau(MAU_LUC_CHU, MAU_LUC_NEN);
        } else {
            pillTrangThai.setText(TT_CHO_THANH_TOAN);
            pillTrangThai.setMau(MAU_CAM_CHU, MAU_CAM_NEN);
        }
        pillTrangThai.setVisible(hoaDonHienTai != null);
        pillTrangThai.revalidate();
        pillTrangThai.repaint();

        boolean choPhepNhap = hoaDonHienTai != null && !daThanhToanXong;
        cboPhuongThuc.setEnabled(choPhepNhap);
        chkXacNhan.setEnabled(choPhepNhap);
        btnXacNhan.setEnabled(choPhepNhap);
        btnIn.setEnabled(daThanhToanXong);
        capNhatTheoPhuongThuc();
    }

    /** Tiền mặt: nhập tay. Chuyển khoản / thẻ: tự điền đúng số tiền phải trả. */
    private void capNhatTheoPhuongThuc() {
        if (cboPhuongThuc == null || txtKhachDua == null) return;
        boolean tienMat = cboPhuongThuc.getSelectedIndex() == 0;
        boolean nhapTay = tienMat && !daThanhToanXong && hoaDonHienTai != null;

        txtKhachDua.setEditable(nhapTay);
        txtKhachDua.setBackground(nhapTay ? MAU_TRANG : MAU_HANG_LE);
        if (!tienMat && !daThanhToanXong) {
            txtKhachDua.setText(dinhDangTien(conPhaiThanhToan));
        }
        capNhatTienThua();
    }

    private void capNhatTienThua() {
        if (txtTienThua == null || txtKhachDua == null) return;
        if (daThanhToanXong) {
            txtTienThua.setText("0");
            txtTienThua.setForeground(MAU_CHU_TOI);
            return;
        }
        long khachDua = parseTien(txtKhachDua.getText());
        if (khachDua >= conPhaiThanhToan) {
            txtTienThua.setText(dinhDangTien(khachDua - conPhaiThanhToan));
            txtTienThua.setForeground(MAU_CHU_TOI);
        } else {
            txtTienThua.setText("Thiếu " + dinhDangTien(conPhaiThanhToan - khachDua));
            txtTienThua.setForeground(MAU_DO);
        }
    }

    // =========================================================
    // 11. XỬ LÝ SỰ KIỆN
    // =========================================================

    private void xacNhanThanhToan() {
        if (hoaDonHienTai == null || daThanhToanXong) return;

        if (conPhaiThanhToan <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Hóa đơn chưa có chi tiết dịch vụ / phụ tùng nên không có số tiền cần thanh toán!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String hinhThuc = String.valueOf(cboPhuongThuc.getSelectedItem());
        long khachDua = parseTien(txtKhachDua.getText());

        if (cboPhuongThuc.getSelectedIndex() == 0 && khachDua < conPhaiThanhToan) {
            JOptionPane.showMessageDialog(this, "Số tiền khách đưa chưa đủ để thanh toán hóa đơn!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtKhachDua.requestFocus();
            return;
        }
        if (!chkXacNhan.isSelected()) {
            JOptionPane.showMessageDialog(this, "Vui lòng xác nhận đã kiểm tra hóa đơn và tiền thanh toán!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int chon = JOptionPane.showConfirmDialog(this,
                "Xác nhận thu " + dinhDangTien(conPhaiThanhToan) + " đ (" + hinhThuc + ") cho hóa đơn " + maHoaDon + "?",
                "Xác nhận thanh toán", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (chon != JOptionPane.YES_OPTION) return;

        boolean ok = hoaDonBUS.xuLyThanhToan(maHoaDon, hinhThuc, khachDua, conPhaiThanhToan, giamGia);
        if (!ok) {
            JOptionPane.showMessageDialog(this,
                    "Thanh toán thất bại! Hóa đơn có thể đã được thanh toán hoặc xảy ra lỗi cơ sở dữ liệu.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        long tienThua = Math.max(0, khachDua - conPhaiThanhToan);
        String thongBao = "Thanh toán thành công cho hóa đơn: " + maHoaDon;
        if (cboPhuongThuc.getSelectedIndex() == 0 && tienThua > 0) {
            thongBao += "\nTiền thừa trả khách: " + dinhDangTien(tienThua) + " đ";
        }
        JOptionPane.showMessageDialog(this, thongBao, "Thông báo", JOptionPane.INFORMATION_MESSAGE);

        napHoaDon(maHoaDon);   // hiển thị lại ở trạng thái "Đã thanh toán"
        if (listener != null) {
            listener.xacNhanThanhToanThanhCong(maHoaDon);
        }
    }

    private void inHoaDon() {
        if (!daThanhToanXong) {
            JOptionPane.showMessageDialog(this, "Chỉ in được hóa đơn sau khi đã thanh toán.",
                    "In hóa đơn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Đang chuẩn bị in chứng từ/hóa đơn cho " + maHoaDon + "...",
                "In hóa đơn", JOptionPane.INFORMATION_MESSAGE);
    }

    // =========================================================
    // 12. HÀM HỖ TRỢ
    // =========================================================

    private long tongCot(DefaultTableModel model, int cot) {
        long tong = 0;
        for (int r = 0; r < model.getRowCount(); r++) {
            tong += parseTien(String.valueOf(model.getValueAt(r, cot)));
        }
        return tong;
    }

    private static long parseTien(String s) {
        if (s == null) return 0;
        String so = s.replaceAll("[^0-9]", "");
        if (so.isEmpty()) return 0;
        try {
            return Long.parseLong(so);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private static String dinhDangTien(long so) {
        return String.format(Locale.GERMANY, "%,d", so); // 1.694.000
    }

    private static String thuVN(LocalDate d) {
        switch (d.getDayOfWeek()) {
            case MONDAY:    return "Thứ Hai";
            case TUESDAY:   return "Thứ Ba";
            case WEDNESDAY: return "Thứ Tư";
            case THURSDAY:  return "Thứ Năm";
            case FRIDAY:    return "Thứ Sáu";
            case SATURDAY:  return "Thứ Bảy";
            default:        return "Chủ Nhật";
        }
    }

    private JLabel taoLabel(String noiDung, int coChu, int kieuChu, Color mauChu) {
        JLabel lbl = new JLabel(noiDung);
        lbl.setFont(new Font(FONT, kieuChu, coChu));
        lbl.setForeground(mauChu);
        return lbl;
    }

    /** Thanh tiêu đề xanh nhạt của mỗi card (phai có thể null). */
    private JPanel taoTieuDeCard(String trai, JLabel phai) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(MAU_XANH_NHAT);
        pnl.setBorder(new EmptyBorder(12, 16, 12, 16));
        pnl.add(taoLabel(trai, 13, Font.BOLD, MAU_XANH_DAM), BorderLayout.WEST);
        if (phai != null) {
            pnl.add(phai, BorderLayout.EAST);
        }
        return pnl;
    }

    /** Thêm component vào panel GridBagLayout, xếp dọc, giãn ngang, không giãn dọc. */
    private static void themDong(JPanel p, Component c, int khoangTren) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = p.getComponentCount();
        g.weightx = 1.0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTHWEST;
        g.insets = new Insets(khoangTren, 0, 0, 0);
        p.add(c, g);
    }

    /** Phần đệm cuối để đẩy nội dung lên trên. */
    private static void themDem(JPanel p) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = p.getComponentCount();
        g.weighty = 1.0;
        g.fill = GridBagConstraints.BOTH;
        JPanel dem = new JPanel();
        dem.setOpaque(false);
        p.add(dem, g);
    }

    // =========================================================
    // 13. CÁC LỚP GIAO DIỆN TÙY BIẾN
    // =========================================================

    /** Panel cuộn được, luôn bám đúng chiều rộng của viewport. */
    private static class PanelCuon extends JPanel implements Scrollable {
        private static final long serialVersionUID = 1L;

        PanelCuon(LayoutManager lm) { super(lm); }

        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 18; }
        @Override public int getScrollableBlockIncrement(Rectangle r, int o, int d) {
            return o == SwingConstants.VERTICAL ? r.height : r.width;
        }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() {
            return getParent() instanceof JViewport
                    && getParent().getHeight() > getPreferredSize().height;
        }
    }

    /** Card nền trắng, bo góc, viền mảnh; nội dung con được cắt theo góc bo. */
    private static class Card extends JPanel {
        private static final long serialVersionUID = 1L;
        private static final int R = 12;

        Card(LayoutManager lm) {
            super(lm);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(MAU_TRANG);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, R, R);
            g2.dispose();
        }

        @Override
        protected void paintChildren(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.clip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), R, R));
            super.paintChildren(g2);
            g2.dispose();

            Graphics2D gv = (Graphics2D) g.create();
            gv.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gv.setColor(MAU_VIEN);
            gv.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, R, R);
            gv.dispose();
        }
    }

    /** Nút bo góc, tự vẽ nên hiển thị đúng màu ở mọi Look & Feel. */
    private static class NutBo extends JButton {
        private static final long serialVersionUID = 1L;
        private final Color mauNen;
        private final boolean coVien;

        NutBo(String text, Color nen, Color chu, boolean coVien) {
            super(text);
            this.mauNen = nen;
            this.coVien = coVien;
            setFont(new Font(FONT, Font.BOLD, 11));
            setForeground(chu);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(9, 16, 9, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color c = mauNen;
            if (!isEnabled()) {
                c = new Color(176, 190, 205);
            } else if (getModel().isPressed()) {
                c = mauNen.darker();
            } else if (getModel().isRollover()) {
                c = mauNen.equals(MAU_XANH_DAM) ? MAU_XANH : MAU_XANH_NHAT;
            }
            g2.setColor(c);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            if (coVien) {
                g2.setColor(MAU_VIEN);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Renderer ô dữ liệu: căn lề giống header, dòng chẵn/lẻ xen kẽ. */
    private static class RendererChu extends JLabel implements TableCellRenderer {
        private static final long serialVersionUID = 1L;

        RendererChu(boolean dam, Color mau) {
            setOpaque(true);
            setForeground(mau);
            setFont(new Font(FONT, dam ? Font.BOLD : Font.PLAIN, 11));
            setBorder(new EmptyBorder(0, 16, 0, 8));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            setBackground(isSelected ? MAU_XANH_NHAT : (row % 2 == 0 ? MAU_TRANG : MAU_HANG_LE));
            return this;
        }
    }

    /** Renderer header: tự vẽ màu nền (tránh bị Look & Feel ghi đè). */
    private static class RendererTieuDe extends JLabel implements TableCellRenderer {
        private static final long serialVersionUID = 1L;

        RendererTieuDe() {
            setOpaque(true);
            setBackground(MAU_XANH_NHAT);
            setForeground(MAU_XANH_DAM);
            setFont(new Font(FONT, Font.BOLD, 10));
            setBorder(new EmptyBorder(0, 16, 0, 8));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            return this;
        }
    }

    /** Nhãn trạng thái dạng viên thuốc. */
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
}