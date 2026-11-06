package gui;

import static gui.UITheme.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Function;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import business.KhuyenMaiBUS;
import entity.ChuongTrinhKhuyenMai;

/**
 * Màn hình QUẢN LÝ KHUYẾN MÃI (JPanel) - được nhúng vào CardLayout của TrangChuQuanLyGUI.
 *
 * Gồm 2 thẻ:
 *  1. Tra cứu: Mã chương trình, Ngày bắt đầu, Ngày kết thúc, Loại khuyến mãi,
 *     nút Tìm kiếm / Thêm chương trình khuyến mãi
 *  2. Danh sách khuyến mãi: Mã chương trình, Tên chương trình, Ngày bắt đầu, Ngày kết thúc,
 *     Loại khuyến mãi, Giá trị khuyến mãi, Trạng thái (tự tính), Thao tác (Cập nhật)
 *
 * Bảng CSDL: ChuongTrinhKhuyenMai(maChuongTrinh, tenChuongTrinh, ngayBatDau, ngayKetThuc,
 *            loaiKhuyenMai ['Trừ tiền' | 'Phần trăm'], giaTriKhuyenMai).
 *
 * Trạng thái KHÔNG lưu trong CSDL mà được tính khi hiển thị, dựa vào ngày hiện hành:
 *  - hôm nay < ngày bắt đầu  -> "Sắp diễn ra"
 *  - hôm nay > ngày kết thúc -> "Đã kết thúc"
 *  - còn lại                 -> "Đang diễn ra"
 *
 * KHÔNG có dữ liệu mẫu. Mọi chỗ cần lấy / ghi dữ liệu từ CSDL là các hàm có "TODO":
 *  layDanhSachKhuyenMai, luuChuongTrinh.
 */
public class KhuyenMaiGUI extends JPanel {
	private static final long serialVersionUID = 1L;

	// ====== Giá trị cột loaiKhuyenMai trong CSDL ======
	public static final String LOAI_TRU_TIEN = "Trừ tiền";
	public static final String LOAI_PHAN_TRAM = "Phần trăm";

	// ====== Các trạng thái (tự tính) ======
	private static final String TT_SAP_DIEN_RA = "Sắp diễn ra";
	private static final String TT_DANG_DIEN_RA = "Đang diễn ra";
	private static final String TT_DA_KET_THUC = "Đã kết thúc";

	private static final String TAT_CA_LOAI = "Tất cả loại khuyến mãi";

	private static final int SO_DONG_TRANG = 8;
	private static final int CAO_DONG = 52;
	private static final int CAO_HEADER = 40;
	private static final int PAD_TRAI = 16;

	private static final String[] NHAN_THAO_TAC = { "Cập nhật" };
	private static final String SEP = " · ";
	private static final String TXT_THAO_TAC = String.join(SEP, NHAN_THAO_TAC);
	private static final int COL_TRANG_THAI = 6;
	private static final int COL_THAO_TAC = 7;

	private static final DateTimeFormatter FMT_NGAY = DateTimeFormatter.ofPattern("dd/MM/uuuu")
			.withResolverStyle(ResolverStyle.STRICT);
	private static final NumberFormat FMT_TIEN = NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN"));
	private static final NumberFormat FMT_PHAN_TRAM = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
	static {
		FMT_PHAN_TRAM.setMaximumFractionDigits(2);
	}

	private static final Border O_BANG = new CompoundBorder(new MatteBorder(0, 0, 1, 0, new Color(236, 240, 245)),
			new EmptyBorder(0, PAD_TRAI, 0, 8));

	private final Runnable quayVeTrangChu;
	private final ArrayList<ChuongTrinhKhuyenMai> dsKhuyenMai = new ArrayList<>(); // toàn bộ kết quả (chia trang ở UI)
	private int trang = 1;

	// Bộ lọc đã được kiểm tra hợp lệ ở lần "Tìm kiếm" gần nhất
	private String locMa = "";
	private LocalDate locTuNgay = null;
	private LocalDate locDenNgay = null;
	private String locLoai = null;

	// ---- Thẻ tra cứu ----
	private Input txtMa, txtNgayBatDau, txtNgayKetThuc;
	private JComboBox<String> cboLoai;

	// ---- Thẻ danh sách ----
	private CardPanel cardDanhSach;
	private BangCoThongBao tblKhuyenMai;
	private DefaultTableModel model;
	private JScrollPane spBang;
	private JLabel lblHienThi, lblSoTrang;
	private JButton btnTruoc, btnSau;
	
	private KhuyenMaiBUS km_bus;

	/**
	 * @param quayVeTrangChu hành động của nút "Trang chủ quản lý" (thường là hienThi(KEY_TRANG_CHU))
	 */
	public KhuyenMaiGUI(Runnable quayVeTrangChu) {
		
		km_bus = new KhuyenMaiBUS();
		
		this.quayVeTrangChu = quayVeTrangChu;
		setLayout(new BorderLayout());
		setBackground(BG);

		ScrollablePanel trangND = new ScrollablePanel(new BorderLayout(0, 16));
		trangND.setBackground(BG);
		trangND.setBorder(new EmptyBorder(22, 28, 28, 28));
		trangND.add(taoTieuDeTrang(), BorderLayout.NORTH);

		JPanel cacThe = new JPanel(new GridBagLayout());
		cacThe.setOpaque(false);
		themDong(cacThe, taoTheTraCuu(), 0, 16);
		themDong(cacThe, taoTheDanhSach(), 1, 0);
		trangND.add(cacThe, BorderLayout.CENTER);

		JScrollPane sp = new JScrollPane(trangND, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setBorder(null);
		sp.getVerticalScrollBar().setUnitIncrement(16);
		add(sp, BorderLayout.CENTER);

		refresh();
	}

	/** Gọi mỗi khi chuyển sang màn hình này: xóa bộ lọc và tải lại toàn bộ danh sách. */
	public void refresh() {
		txtMa.setText("");
		txtNgayBatDau.setText("");
		txtNgayKetThuc.setText("");
		cboLoai.setSelectedIndex(0);
		locMa = "";
		locTuNgay = null;
		locDenNgay = null;
		locLoai = null;
		taiLaiDanhSach();
	}

	// =====================================================================
	// TODO: KẾT NỐI CƠ SỞ DỮ LIỆU (DAO) - BẠN CHỈ CẦN SỬA CÁC HÀM TRONG KHỐI NÀY
	// =====================================================================

	/**
	 * Lấy danh sách chương trình khuyến mãi theo bộ lọc. Tham số rỗng / null nghĩa là KHÔNG lọc theo tiêu chí đó.
	 * @param maChuongTrinh   chuỗi mã cần tìm ("" = không lọc), nên tìm gần đúng: LIKE %...%
	 * @param ngayBatDauTu    chỉ lấy chương trình có ngayBatDau >= giá trị này (null = không lọc)
	 * @param ngayKetThucDen  chỉ lấy chương trình có ngayKetThuc <= giá trị này (null = không lọc)
	 * @param loaiKhuyenMai   LOAI_TRU_TIEN hoặc LOAI_PHAN_TRAM (null = tất cả loại)
	 * TODO: gọi DAO, ví dụ khuyenMaiDAO.timKiem(...), rồi đổi mỗi entity thành
	 * new KhuyenMaiRow(ma, ten, ngayBatDau, ngayKetThuc, loai, giaTri).
	 * Cột "Trạng thái" KHÔNG cần lấy từ CSDL, giao diện tự tính.
	 */
	private ArrayList<ChuongTrinhKhuyenMai> layDanhSachKhuyenMai(String maChuongTrinh, LocalDate ngayBatDauTu,
			LocalDate ngayKetThucDen, String loaiKhuyenMai) {
		ArrayList<ChuongTrinhKhuyenMai> ds = null;
		try {
			ds = km_bus.getAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * Thêm mới (themMoi = true, mã tự sinh, ví dụ KM003) hoặc cập nhật (themMoi = false) một chương trình.
	 * @return true nếu lưu thành công
	 */
	private boolean luuChuongTrinh(ChuongTrinhKhuyenMai km, boolean themMoi) {
		try {
			return themMoi ? km_bus.them(km) : km_bus.capNhat(km);
		} catch (SQLException e) {
			return false;
		}
	}

	// =====================================================================
	// TIÊU ĐỀ TRANG
	// =====================================================================
	private JPanel taoTieuDeTrang() {
		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);

		JPanel tieuDe = new JPanel();
		tieuDe.setOpaque(false);
		tieuDe.setLayout(new BoxLayout(tieuDe, BoxLayout.Y_AXIS));
		JLabel lblTitle = new JLabel("Chương trình khuyến mãi");
		lblTitle.setFont(FONT_TITLE);
		lblTitle.setForeground(TITLE);
		JLabel lblSub = new JLabel("Quản lý các chương trình khuyến mãi áp dụng cho hóa đơn của gara.");
		lblSub.setFont(FONT_LABEL);
		lblSub.setForeground(TEXT_MUTED);
		tieuDe.add(lblTitle);
		tieuDe.add(lblSub);
		header.add(tieuDe, BorderLayout.WEST);

		ActionButton btnTrangChu = new ActionButton("Trang chủ quản lý", ActionButton.KIEU_VIEN_XANH, IMG_QUAY_LAI);
		btnTrangChu.addActionListener(e -> {
			if (quayVeTrangChu != null) {
				quayVeTrangChu.run();
			}
		});
		JPanel wrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		wrap.setOpaque(false);
		wrap.add(btnTrangChu);
		header.add(wrap, BorderLayout.EAST);
		return header;
	}

	private static void themDong(JPanel p, Component c, int y, int duoi) {
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = y;
		g.weightx = 1;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.anchor = GridBagConstraints.NORTHWEST;
		g.insets = new Insets(0, 0, duoi, 0);
		p.add(c, g);
	}

	// =====================================================================
	// THẺ 1: TRA CỨU
	// =====================================================================
	private CardPanel taoTheTraCuu() {
		CardPanel the = new CardPanel("Tra cứu chương trình khuyến mãi", "");
		the.setTitleIcon(IMG_TIM_KIEM);
		the.getBody().setBorder(new EmptyBorder(16, 20, 20, 20));

		txtMa = new Input("Nhập mã chương trình", false);
		txtNgayBatDau = new Input("dd/MM/yyyy", false);
		txtNgayBatDau.themIconBenPhai(IMG_LICH, null);
		txtNgayKetThuc = new Input("dd/MM/yyyy", false);
		txtNgayKetThuc.themIconBenPhai(IMG_LICH, null);
		cboLoai = createComboBox(TAT_CA_LOAI, LOAI_TRU_TIEN, LOAI_PHAN_TRAM);

		ActionButton btnTim = new ActionButton("Tìm kiếm", ActionButton.KIEU_CHINH, IMG_TIM_KIEM);
		ActionButton btnThem = new ActionButton("Thêm chương trình khuyến mãi", ActionButton.KIEU_CHINH, IMG_THEM);
		btnTim.addActionListener(e -> timKiem());
		txtMa.addActionListener(e -> timKiem());
		txtNgayBatDau.addActionListener(e -> timKiem());
		txtNgayKetThuc.addActionListener(e -> timKiem());
		cboLoai.addActionListener(e -> timKiem());
		btnThem.addActionListener(e -> themChuongTrinh());

		// Lưới 6 cột: hàng 1 gồm 3 ô (mỗi ô 2 cột); hàng 2 gồm ô Loại (2 cột) và cụm nút căn phải (4 cột)
		JPanel luoi = new JPanel(new GridBagLayout());
		luoi.setOpaque(false);
		for (int c = 0; c < 6; c++) {
			GridBagConstraints g = new GridBagConstraints();
			g.gridx = c;
			g.gridy = 0;
			g.weightx = 1;
			g.fill = GridBagConstraints.HORIZONTAL;
			JPanel thuoc = new JPanel();
			thuoc.setOpaque(false);
			thuoc.setPreferredSize(new Dimension(1, 0));
			luoi.add(thuoc, g);
		}
		themO(luoi, labeled("Mã chương trình", txtMa), 1, 0, 2, 16, 14);
		themO(luoi, labeled("Ngày bắt đầu (từ ngày)", txtNgayBatDau), 1, 2, 2, 16, 14);
		themO(luoi, labeled("Ngày kết thúc (đến ngày)", txtNgayKetThuc), 1, 4, 2, 0, 14);
		themO(luoi, labeled("Loại khuyến mãi", cboLoai), 2, 0, 2, 16, 0);

		JPanel cumNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
		cumNut.setOpaque(false);
		cumNut.add(labeledFixed(" ", btnTim));
		cumNut.add(labeledFixed(" ", btnThem));
		themO(luoi, cumNut, 2, 2, 4, 0, 0);

		the.getBody().add(luoi, BorderLayout.CENTER);
		return the;
	}

	private static void themO(JPanel p, Component c, int row, int col, int span, int phai, int duoi) {
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = col;
		g.gridy = row;
		g.gridwidth = span;
		g.weightx = span;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.anchor = GridBagConstraints.NORTHWEST;
		g.insets = new Insets(0, 0, duoi, phai);
		p.add(c, g);
	}

	// =====================================================================
	// THẺ 2: DANH SÁCH KHUYẾN MÃI
	// =====================================================================
	private CardPanel taoTheDanhSach() {
		cardDanhSach = new CardPanel("Danh sách chương trình khuyến mãi", "");
		cardDanhSach.getBody().setBorder(new EmptyBorder(0, 0, 0, 0));

		model = new DefaultTableModel(new String[] { "Mã chương trình", "Tên chương trình", "Ngày bắt đầu",
				"Ngày kết thúc", "Loại khuyến mãi", "Giá trị khuyến mãi", "Trạng thái", "Thao tác" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tblKhuyenMai = new BangCoThongBao(model);
		tblKhuyenMai.setThongBaoTrong("Không có chương trình khuyến mãi nào để hiển thị.");
		tuyChinhBang(tblKhuyenMai, new int[] { 120, 230, 110, 110, 130, 140, 130, 100 },
				new TableCellRenderer[] { new CellRenderer(true), new CellRenderer(false), new CellRenderer(false),
						new CellRenderer(false), new CellRenderer(false), new CellRenderer(false),
						new BadgeRenderer(), new ThaoTacRenderer(TXT_THAO_TAC) });

		tblKhuyenMai.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = tblKhuyenMai.rowAtPoint(e.getPoint());
				ChuongTrinhKhuyenMai km = khuyenMaiTrenHang(row);
				if (km != null && vungThaoTac(tblKhuyenMai, e, COL_THAO_TAC, NHAN_THAO_TAC, SEP) == 1) {
					capNhatChuongTrinh(km);
				}
			}
		});
		tblKhuyenMai.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				tblKhuyenMai.setCursor(Cursor.getPredefinedCursor(
						vungThaoTac(tblKhuyenMai, e, COL_THAO_TAC, NHAN_THAO_TAC, SEP) != 0 ? Cursor.HAND_CURSOR
								: Cursor.DEFAULT_CURSOR));
			}
		});

		spBang = new JScrollPane(tblKhuyenMai);
		spBang.setBorder(null);
		spBang.getViewport().setBackground(Color.WHITE);

		cardDanhSach.getBody().add(spBang, BorderLayout.CENTER);
		cardDanhSach.getBody().add(taoChanThe(), BorderLayout.SOUTH);
		return cardDanhSach;
	}

	private JPanel taoChanThe() {
		JPanel p = new JPanel(new BorderLayout());
		p.setOpaque(false);
		p.setBorder(new EmptyBorder(14, 20, 14, 20));

		lblHienThi = new JLabel();
		lblHienThi.setFont(FONT_SMALL);
		lblHienThi.setForeground(TEXT_MUTED);
		p.add(lblHienThi, BorderLayout.WEST);

		JPanel pt = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		pt.setOpaque(false);
		btnTruoc = taoNutTrang("‹");
		btnSau = taoNutTrang("›");
		btnTruoc.addActionListener(e -> chuyenTrang(trang - 1));
		btnSau.addActionListener(e -> chuyenTrang(trang + 1));

		RoundedPanel chip = new RoundedPanel(8, PRIMARY);
		chip.setLayout(new GridBagLayout());
		chip.setPreferredSize(new Dimension(32, 32));
		lblSoTrang = new JLabel("1");
		lblSoTrang.setFont(FONT_BOLD);
		lblSoTrang.setForeground(Color.WHITE);
		chip.add(lblSoTrang);

		pt.add(btnTruoc);
		pt.add(chip);
		pt.add(btnSau);
		p.add(pt, BorderLayout.EAST);
		return p;
	}

	private ActionButton taoNutTrang(String text) {
		ActionButton b = new ActionButton(text, ActionButton.KIEU_VIEN_XAM);
		b.setBorder(new EmptyBorder(0, 0, 0, 0));
		b.setFont(FONT_SECTION);
		b.setPreferredSize(new Dimension(32, 32));
		return b;
	}

	// =====================================================================
	// TÌM KIẾM & ĐỔ DỮ LIỆU LÊN BẢNG
	// =====================================================================

	/** Kiểm tra bộ lọc, ghi nhớ rồi tải lại danh sách. */
	private void timKiem() {
		LocalDate tu = null;
		LocalDate den = null;

		String sTu = txtNgayBatDau.getText().trim();
		if (!sTu.isEmpty()) {
			tu = parseNgay(sTu);
			if (tu == null) {
				baoLoi("Ngày bắt đầu không hợp lệ (định dạng dd/MM/yyyy)!", txtNgayBatDau);
				return;
			}
		}
		String sDen = txtNgayKetThuc.getText().trim();
		if (!sDen.isEmpty()) {
			den = parseNgay(sDen);
			if (den == null) {
				baoLoi("Ngày kết thúc không hợp lệ (định dạng dd/MM/yyyy)!", txtNgayKetThuc);
				return;
			}
		}
		if (tu != null && den != null && tu.isAfter(den)) {
			baoLoi("Ngày bắt đầu không được sau ngày kết thúc!", txtNgayBatDau);
			return;
		}

		locMa = txtMa.getText().trim();
		locTuNgay = tu;
		locDenNgay = den;
		locLoai = cboLoai.getSelectedIndex() > 0 ? (String) cboLoai.getSelectedItem() : null;
		taiLaiDanhSach();
	}

	/** Lấy dữ liệu theo bộ lọc đã ghi nhớ (qua DAO) rồi hiển thị trang 1. */
	private void taiLaiDanhSach() {
		ArrayList<ChuongTrinhKhuyenMai> ds = layDanhSachKhuyenMai(locMa, locTuNgay, locDenNgay, locLoai);
		dsKhuyenMai.clear();
		if (ds != null) {
			dsKhuyenMai.addAll(ds);
		}
		trang = 1;
		hienThiTrang();
	}

	private int tongSoTrang() {
		return Math.max(1, (dsKhuyenMai.size() + SO_DONG_TRANG - 1) / SO_DONG_TRANG);
	}

	private void chuyenTrang(int trangMoi) {
		if (trangMoi < 1 || trangMoi > tongSoTrang()) {
			return;
		}
		trang = trangMoi;
		hienThiTrang();
	}

	private void hienThiTrang() {
		int tong = dsKhuyenMai.size();
		trang = Math.min(Math.max(1, trang), tongSoTrang());
		int tu = (trang - 1) * SO_DONG_TRANG;
		int den = Math.min(tong, tu + SO_DONG_TRANG);

		model.setRowCount(0);
		for (int i = tu; i < den; i++) {
			ChuongTrinhKhuyenMai km = dsKhuyenMai.get(i);
			model.addRow(new Object[] { km.getMaChuongTrinh(), km.getTenChuongTrinh(), km.getNgayBatDau().format(FMT_NGAY), km.getNgayKetThuc().format(FMT_NGAY),
					km.getLoaiKhuyenMai(), hienGiaTri(km), tinhTrangThai(km.getNgayBatDau(), km.getNgayKetThuc()), TXT_THAO_TAC });
		}

		cardDanhSach.setNote(tong + " chương trình");
		lblHienThi.setText("Hiển thị " + (den - tu) + " / " + tong + " bản ghi");
		lblSoTrang.setText(String.valueOf(trang));
		btnTruoc.setEnabled(trang > 1);
		btnSau.setEnabled(trang < tongSoTrang());

		int n = Math.max(2, den - tu); // tối thiểu 2 dòng để chừa chỗ cho dòng thông báo
		spBang.setPreferredSize(new Dimension(100, CAO_HEADER + n * CAO_DONG));
		cardDanhSach.revalidate();
		cardDanhSach.repaint();
	}

	private ChuongTrinhKhuyenMai khuyenMaiTrenHang(int r) {
		int idx = (trang - 1) * SO_DONG_TRANG + r;
		return (r >= 0 && idx < dsKhuyenMai.size()) ? dsKhuyenMai.get(idx) : null;
	}

	/** Tự tính trạng thái dựa trên ngày bắt đầu / kết thúc so với ngày hiện hành. */
	private static String tinhTrangThai(LocalDate batDau, LocalDate ketThuc) {
		LocalDate homNay = LocalDate.now();
		if (homNay.isBefore(batDau)) {
			return TT_SAP_DIEN_RA;
		}
		if (homNay.isAfter(ketThuc)) {
			return TT_DA_KET_THUC;
		}
		return TT_DANG_DIEN_RA;
	}

	/** Phần trăm -> "5%"; trừ tiền -> "100.000 đ". */
	private static String hienGiaTri(ChuongTrinhKhuyenMai km) {
		return LOAI_PHAN_TRAM.equals(km.getLoaiKhuyenMai()) ? FMT_PHAN_TRAM.format(km.getGiaTriKhuyenMai()) + "%" : FMT_TIEN.format(km.getGiaTriKhuyenMai()) + " đ";
	}

	// =====================================================================
	// THÊM / CẬP NHẬT
	// =====================================================================
	private static final String[] NHAN_FORM = { "Mã chương trình", "Tên chương trình *", "Ngày bắt đầu * (dd/MM/yyyy)",
			"Ngày kết thúc * (dd/MM/yyyy)", "Loại khuyến mãi *", "Giá trị khuyến mãi * (số tiền hoặc %)" };
	private static final boolean[] CHI_DOC_FORM = { true, false, false, false, false, false };
	private static final String[][] LUA_CHON_FORM = { null, null, null, null, { LOAI_TRU_TIEN, LOAI_PHAN_TRAM },
			null };

	private void themChuongTrinh() {
		String[] kq = null;
		String maKM = null;
		try {
			maKM = km_bus.phatSinhMaKM();
			kq = hienFormDialog("Thêm chương trình khuyến mãi", NHAN_FORM,
					new String[] { maKM, "", "", "", LOAI_TRU_TIEN, "" }, CHI_DOC_FORM, LUA_CHON_FORM,
					KhuyenMaiGUI::kiemTraChuongTrinh);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (kq == null) {
			return;
		}
		ChuongTrinhKhuyenMai km = taoDong(maKM, kq);
		if (luuChuongTrinh(km, true)) {
			JOptionPane.showMessageDialog(this, "Đã thêm chương trình " + km.getTenChuongTrinh() + ".", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
			taiLaiDanhSach();
		} else {
			JOptionPane.showMessageDialog(this, "Thêm chương trình khuyến mãi thất bại!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void capNhatChuongTrinh(ChuongTrinhKhuyenMai km) {
		String[] kq = hienFormDialog("Cập nhật chương trình khuyến mãi", NHAN_FORM,
				new String[] { km.getMaChuongTrinh(), km.getTenChuongTrinh(), km.getNgayBatDau().format(FMT_NGAY), km.getNgayKetThuc().format(FMT_NGAY),
						km.getLoaiKhuyenMai(), soThanhChuoi(km.getGiaTriKhuyenMai()) },
				CHI_DOC_FORM, LUA_CHON_FORM, KhuyenMaiGUI::kiemTraChuongTrinh);
		if (kq == null) {
			return;
		}
		ChuongTrinhKhuyenMai moi = taoDong(km.getMaChuongTrinh(), kq);
		if (luuChuongTrinh(moi, false)) {
			JOptionPane.showMessageDialog(this, "Đã cập nhật chương trình " + km.getMaChuongTrinh() + ".", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
			taiLaiDanhSach();
		} else {
			JOptionPane.showMessageDialog(this, "Cập nhật chương trình khuyến mãi thất bại!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/** Đổi mảng giá trị của form (đã kiểm tra hợp lệ) thành một dòng khuyến mãi. */
	private static ChuongTrinhKhuyenMai taoDong(String ma, String[] v) {
		return new ChuongTrinhKhuyenMai(ma, v[1], parseNgay(v[2]), parseNgay(v[3]), v[4],
				docGiaTri(v[5], LOAI_PHAN_TRAM.equals(v[4])));
	}

	/** 5.0 -> "5"; 7.5 -> "7.5"; 100000.0 -> "100000". */
	private static String soThanhChuoi(double d) {
		return d == Math.rint(d) ? String.valueOf((long) d) : String.valueOf(d);
	}

	/** Trả về thông báo lỗi, hoặc null nếu hợp lệ. v = {mã, tên, ngày BD, ngày KT, loại, giá trị}. */
	private static String kiemTraChuongTrinh(String[] v) {
		if (v[1].isEmpty()) {
			return "Vui lòng nhập tên chương trình!";
		}
		if (v[1].length() > 150) {
			return "Tên chương trình tối đa 150 ký tự!";
		}
		LocalDate bd = parseNgay(v[2]);
		if (bd == null) {
			return "Ngày bắt đầu không hợp lệ (định dạng dd/MM/yyyy)!";
		}
		LocalDate kt = parseNgay(v[3]);
		if (kt == null) {
			return "Ngày kết thúc không hợp lệ (định dạng dd/MM/yyyy)!";
		}
		if (kt.isBefore(bd)) {
			return "Ngày kết thúc không được trước ngày bắt đầu!";
		}
		boolean phanTram = LOAI_PHAN_TRAM.equals(v[4]);
		Double gt = docGiaTri(v[5], phanTram);
		if (gt == null) {
			return phanTram ? "Giá trị phần trăm không hợp lệ (VD: 5 hoặc 7,5)!"
					: "Giá trị trừ tiền phải là số không âm (VD: 100000 hoặc 100.000)!";
		}
		if (phanTram && gt > 100) {
			return "Giá trị phần trăm phải từ 0 đến 100!";
		}
		return null;
	}

	/** Đọc giá trị khuyến mãi; trả về null nếu không hợp lệ. Phần trăm: "7,5" hoặc "7.5"; trừ tiền: "100.000". */
	private static Double docGiaTri(String s, boolean phanTram) {
		String t = s == null ? "" : s.trim();
		if (t.isEmpty()) {
			return null;
		}
		try {
			if (phanTram) {
				double d = Double.parseDouble(t.replace(',', '.'));
				return d >= 0 ? d : null;
			}
			String so = t.replaceAll("[.,\\s]", "");
			return so.matches("\\d+") ? Double.valueOf(so) : null;
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	private static LocalDate parseNgay(String s) {
		try {
			return LocalDate.parse(s.trim(), FMT_NGAY);
		} catch (DateTimeParseException ex) {
			return null;
		}
	}

	private void baoLoi(String msg, Component focus) {
		JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.WARNING_MESSAGE);
		focus.requestFocusInWindow();
	}

	// =====================================================================
	// HỘP THOẠI NHẬP LIỆU DÙNG CHUNG
	// =====================================================================
	/**
	 * Hiện hộp thoại nhập liệu (modal) gồm các ô "nhãn - giá trị".
	 * @param luaChon luaChon[i] != null thì ô i là danh sách chọn với các giá trị đó, ngược lại là ô nhập chữ
	 * @param kiemTra hàm kiểm tra dữ liệu: trả về thông báo lỗi hoặc null nếu hợp lệ (có thể null)
	 * @return mảng giá trị đã nhập (đã trim), hoặc null nếu người dùng bấm Hủy
	 */
	private String[] hienFormDialog(String tieuDe, String[] nhan, String[] giaTri, boolean[] chiDoc,
			String[][] luaChon, Function<String[], String> kiemTra) {
		Window cha = SwingUtilities.getWindowAncestor(this);
		JDialog dlg = new JDialog(cha, tieuDe, Dialog.ModalityType.APPLICATION_MODAL);

		JPanel noiDung = new JPanel(new BorderLayout(0, 8));
		noiDung.setBackground(Color.WHITE);
		noiDung.setBorder(new EmptyBorder(20, 24, 20, 24));

		JPanel luoi = new JPanel(new GridBagLayout());
		luoi.setOpaque(false);
		JComponent[] o = new JComponent[nhan.length];
		for (int i = 0; i < nhan.length; i++) {
			if (luaChon != null && luaChon[i] != null) {
				JComboBox<String> cbo = createComboBox(luaChon[i]);
				cbo.setSelectedItem(giaTri[i]);
				cbo.setEnabled(!chiDoc[i]);
				o[i] = cbo;
			} else {
				Input in = new Input(null, chiDoc[i]);
				in.setText(giaTri[i]);
				o[i] = in;
			}
			GridBagConstraints g = new GridBagConstraints();
			g.gridx = 0;
			g.gridy = i;
			g.weightx = 1;
			g.fill = GridBagConstraints.HORIZONTAL;
			g.insets = new Insets(0, 0, 12, 0);
			luoi.add(labeled(nhan[i], o[i]), g);
		}
		noiDung.add(luoi, BorderLayout.CENTER);

		final String[][] ketQua = { null };
		ActionButton btnLuu = new ActionButton("Lưu", ActionButton.KIEU_CHINH, IMG_LUU);
		ActionButton btnHuy = new ActionButton("Hủy", ActionButton.KIEU_VIEN_XAM);
		btnLuu.addActionListener(e -> {
			String[] v = new String[o.length];
			for (int i = 0; i < o.length; i++) {
				v[i] = (o[i] instanceof JTextField) ? ((JTextField) o[i]).getText().trim()
						: String.valueOf(((JComboBox<?>) o[i]).getSelectedItem());
			}
			String loi = (kiemTra == null) ? null : kiemTra.apply(v);
			if (loi != null) {
				JOptionPane.showMessageDialog(dlg, loi, "Thông báo", JOptionPane.WARNING_MESSAGE);
				return;
			}
			ketQua[0] = v;
			dlg.dispose();
		});
		btnHuy.addActionListener(e -> dlg.dispose());

		JPanel hangNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		hangNut.setOpaque(false);
		hangNut.add(btnHuy);
		hangNut.add(btnLuu);
		noiDung.add(hangNut, BorderLayout.SOUTH);

		dlg.setContentPane(noiDung);
		dlg.getRootPane().setDefaultButton(btnLuu);
		dlg.pack();
		dlg.setSize(Math.max(dlg.getWidth(), 480), dlg.getHeight());
		dlg.setLocationRelativeTo(cha);
		dlg.setVisible(true); // chặn cho đến khi đóng
		return ketQua[0];
	}

	// =====================================================================
	// BẢNG: ĐỊNH DẠNG, RENDERER
	// =====================================================================
	private static void tuyChinhBang(JTable t, int[] rong, TableCellRenderer[] renderers) {
		t.setRowHeight(CAO_DONG);
		t.setShowGrid(false);
		t.setIntercellSpacing(new Dimension(0, 0));
		t.setFont(FONT_BODY);
		t.setForeground(TEXT_DARK);
		t.setSelectionBackground(ROW_SELECTED);
		t.setSelectionForeground(TEXT_DARK);
		t.setFillsViewportHeight(true);
		t.setFocusable(false);

		JTableHeader th = t.getTableHeader();
		th.setReorderingAllowed(false);
		th.setResizingAllowed(false);
		th.setPreferredSize(new Dimension(100, CAO_HEADER));
		th.setDefaultRenderer(new HeaderRenderer());

		for (int i = 0; i < rong.length; i++) {
			t.getColumnModel().getColumn(i).setPreferredWidth(rong[i]);
			t.getColumnModel().getColumn(i).setCellRenderer(renderers[i]);
		}
	}

	/** Vị trí click trong ô "Thao tác": 0 = ngoài, 1..n = nhãn thứ n. */
	private static int vungThaoTac(JTable t, MouseEvent e, int colThaoTac, String[] nhan, String sep) {
		int row = t.rowAtPoint(e.getPoint());
		int col = t.columnAtPoint(e.getPoint());
		if (row < 0 || col != colThaoTac) {
			return 0;
		}
		Rectangle rc = t.getCellRect(row, col, false);
		int x = e.getX() - rc.x - PAD_TRAI;
		FontMetrics fm = t.getFontMetrics(FONT_BOLD);
		int viTri = 0;
		for (int i = 0; i < nhan.length; i++) {
			int w = fm.stringWidth(nhan[i]);
			if (x >= viTri && x <= viTri + w) {
				return i + 1;
			}
			viTri += w + fm.stringWidth(sep);
		}
		return 0;
	}

	private static Color mauNen(boolean chon, int row) {
		return chon ? ROW_SELECTED : (row % 2 == 1 ? ROW_ALT : Color.WHITE);
	}

	/** JTable tự vẽ dòng thông báo ở giữa khi không có dòng dữ liệu nào. */
	private static class BangCoThongBao extends JTable {
		private static final long serialVersionUID = 1L;
		private String thongBaoTrong = "";

		BangCoThongBao(DefaultTableModel m) {
			super(m);
		}

		void setThongBaoTrong(String s) {
			this.thongBaoTrong = s;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (getRowCount() == 0) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2.setFont(FONT_BODY);
				g2.setColor(TEXT_MUTED);
				FontMetrics fm = g2.getFontMetrics();
				Rectangle r = getVisibleRect();
				int x = r.x + (r.width - fm.stringWidth(thongBaoTrong)) / 2;
				int y = r.y + (r.height - fm.getHeight()) / 2 + fm.getAscent();
				g2.drawString(thongBaoTrong, x, y);
				g2.dispose();
			}
		}
	}

	private static class HeaderRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
			super.getTableCellRendererComponent(t, v, false, false, r, c);
			setOpaque(true);
			setBackground(PRIMARY_LIGHT);
			setForeground(PRIMARY);
			setFont(FONT_BOLD);
			setBorder(new EmptyBorder(0, PAD_TRAI, 0, 8));
			return this;
		}
	}

	private static class CellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		private final boolean nhanManh;

		CellRenderer(boolean nhanManh) {
			this.nhanManh = nhanManh;
		}

		@Override
		public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
			super.getTableCellRendererComponent(t, v, false, false, r, c);
			setOpaque(true);
			setBackground(mauNen(sel, r));
			setForeground(nhanManh ? PRIMARY : TEXT_DARK);
			setFont(nhanManh ? FONT_BOLD : FONT_BODY);
			setBorder(O_BANG);
			return this;
		}
	}

	/** Ô "Thao tác": chữ xanh đậm, ví dụ "Cập nhật". */
	private static class ThaoTacRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		private final String text;

		ThaoTacRenderer(String text) {
			this.text = text;
		}

		@Override
		public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
			super.getTableCellRendererComponent(t, text, false, false, r, c);
			setOpaque(true);
			setBackground(mauNen(sel, r));
			setForeground(PRIMARY);
			setFont(FONT_BOLD);
			setBorder(O_BANG);
			return this;
		}
	}

	/** Nhãn trạng thái dạng "huy hiệu" bo tròn, màu theo trạng thái. */
	private static class Badge extends JLabel {
		private static final long serialVersionUID = 1L;
		private Color nen = Color.WHITE;

		Badge() {
			setFont(FONT_SMALL_BOLD);
			setBorder(new EmptyBorder(4, 10, 4, 10));
			setOpaque(false);
		}

		void datTrangThai(String t) {
			setText(t);
			if (TT_DANG_DIEN_RA.equals(t)) {
				nen = OK_BG;
				setForeground(OK_FG);
			} else if (TT_SAP_DIEN_RA.equals(t)) {
				nen = new Color(255, 244, 214);
				setForeground(new Color(146, 100, 0));
			} else {
				nen = new Color(233, 235, 238);
				setForeground(new Color(95, 99, 104));
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(nen);
			g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
			g2.dispose();
			super.paintComponent(g);
		}
	}

	private static class BadgeRenderer implements TableCellRenderer {
		private final JPanel panel = new JPanel(new GridBagLayout());
		private final Badge badge = new Badge();

		BadgeRenderer() {
			panel.setBorder(O_BANG);
			GridBagConstraints g = new GridBagConstraints();
			g.anchor = GridBagConstraints.WEST;
			g.weightx = 1;
			panel.add(badge, g);
		}

		@Override
		public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
			panel.setBackground(mauNen(sel, r));
			badge.datTrangThai(String.valueOf(v));
			return panel;
		}
	}
}
