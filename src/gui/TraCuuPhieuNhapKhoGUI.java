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
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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

/**
 * Màn hình TRA CỨU PHIẾU NHẬP KHO (JPanel) - tương tự TraCuuPhieuTiepNhanGUI.
 * Được nhúng vào CardLayout của TrangChuQuanLyGUI, mở từ nút "Tra cứu phiếu nhập kho" của QuanLyPhuTungGUI.
 *
 * Mỗi lần màn hình được mở, gọi {@link #datLai()} để về trạng thái ban đầu: 0 kết quả.
 * Kết quả chỉ hiện sau khi người dùng bấm "Tra cứu" (hoặc Enter) theo bộ lọc ở thẻ "Thông tin tra cứu".
 *
 * Bảng CSDL: PhieuNhapKho(maPNK, ngayNhap, maNVLap, maNCC, ghiChu, tongTienNhapKho)
 *            ChiTietPhieuNhapKho(maPNK, maPhuTung, soLuong, donGiaNhap).
 *
 * KHÔNG có dữ liệu mẫu. Các hàm có "TODO": timKiemPhieuNhapKho, layChiTietPhieuNhap.
 */
public class TraCuuPhieuNhapKhoGUI extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final DateTimeFormatter FMT_NGAY = DateTimeFormatter.ofPattern("dd/MM/uuuu")
			.withResolverStyle(ResolverStyle.STRICT);
	private static final NumberFormat FMT_TIEN = NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN"));

	private static final int SO_DONG_TRANG = 8;
	private static final int CAO_DONG = 56;
	private static final int CAO_HEADER = 40;
	private static final int COL_NHAN_VIEN = 2;
	private static final int COL_THAO_TAC = 5;
	private static final int PAD_TRAI = 16;
	private static final int NUT = 32;
	private static final int KHOANG = 8;
	private static final Color DASH = new Color(150, 155, 165);

	private static final Border O_BANG = new CompoundBorder(new MatteBorder(0, 0, 1, 0, new Color(236, 240, 245)),
			new EmptyBorder(0, PAD_TRAI, 0, 8));

	/** Một dòng phiếu nhập kho. TODO: có thể thay bằng entity.PhieuNhapKho (JOIN lấy thêm tên NV, tên NCC). */
	private static class PhieuNhapRow {
		final String ma, maNV, tenNV, maNCC, tenNCC, ghiChu;
		final LocalDate ngayNhap;
		final double tongTien;

		PhieuNhapRow(String ma, LocalDate ngayNhap, String maNV, String tenNV, String maNCC, String tenNCC,
				String ghiChu, double tongTien) {
			this.ma = ma;
			this.ngayNhap = ngayNhap;
			this.maNV = maNV;
			this.tenNV = tenNV;
			this.maNCC = maNCC;
			this.tenNCC = tenNCC;
			this.ghiChu = ghiChu;
			this.tongTien = tongTien;
		}
	}

	/** Một dòng chi tiết của phiếu nhập. */
	private static class ChiTietRow {
		final String maPhuTung, tenPhuTung, donViTinh;
		final int soLuong;
		final double donGiaNhap;

		ChiTietRow(String maPhuTung, String tenPhuTung, String donViTinh, int soLuong, double donGiaNhap) {
			this.maPhuTung = maPhuTung;
			this.tenPhuTung = tenPhuTung;
			this.donViTinh = donViTinh;
			this.soLuong = soLuong;
			this.donGiaNhap = donGiaNhap;
		}
	}

	private final Runnable quayVeQuanLyKho;
	private final List<PhieuNhapRow> dsKetQua = new ArrayList<>();
	private int trang = 1;
	private boolean daTraCuu = false;

	// ---- Bộ lọc ----
	private Input txtMaPhieu, txtTuNgay, txtDenNgay, txtMaNV, txtMaNCC;

	// ---- Kết quả ----
	private Pill lblKetQua;
	private JLabel lblDieuKien, lblHienThi, lblSoTrang;
	private BangCoThongBao tblPhieu;
	private DefaultTableModel model;
	private JScrollPane spBang;
	private JPanel theDanhSach;
	private JButton btnTruoc, btnSau;

	/**
	 * @param quayVeQuanLyKho hành động của nút "Quản lý kho" (quay về màn hình QuanLyPhuTungGUI)
	 */
	public TraCuuPhieuNhapKhoGUI(Runnable quayVeQuanLyKho) {
		this.quayVeQuanLyKho = quayVeQuanLyKho;
		setLayout(new BorderLayout());
		setBackground(BG);

		ScrollablePanel trangNoiDung = new ScrollablePanel(new BorderLayout(0, 16));
		trangNoiDung.setBackground(BG);
		trangNoiDung.setBorder(new EmptyBorder(22, 28, 28, 28));
		trangNoiDung.add(taoTieuDeTrang(), BorderLayout.NORTH);

		JPanel cacThe = new JPanel(new GridBagLayout());
		cacThe.setOpaque(false);
		themDong(cacThe, taoTheTraCuu(), 0, 16);
		themDong(cacThe, taoTheDanhSach(), 1, 0);
		trangNoiDung.add(cacThe, BorderLayout.CENTER);

		JScrollPane sp = new JScrollPane(trangNoiDung, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setBorder(null);
		sp.getVerticalScrollBar().setUnitIncrement(16);
		add(sp, BorderLayout.CENTER);

		datLai();
	}

	/** Đưa màn hình về trạng thái ban đầu: xóa bộ lọc, 0 kết quả. Gọi mỗi khi mở màn hình này. */
	public void datLai() {
		LocalDate homNay = LocalDate.now();
		txtMaPhieu.setText("");
		txtMaNV.setText("");
		txtMaNCC.setText("");
		txtTuNgay.setText(homNay.minusDays(30).format(FMT_NGAY));
		txtDenNgay.setText(homNay.format(FMT_NGAY));

		dsKetQua.clear();
		daTraCuu = false;
		trang = 1;
		lblDieuKien.setText("Chưa tra cứu - nhập điều kiện lọc rồi bấm \"Tra cứu\"");
		hienThiTrang();
	}

	// =====================================================================
	// TODO: KẾT NỐI CƠ SỞ DỮ LIỆU (DAO) - BẠN CHỈ CẦN SỬA CÁC HÀM TRONG KHỐI NÀY
	// =====================================================================

	/**
	 * Tìm phiếu nhập kho theo bộ lọc. Tham số rỗng / null nghĩa là KHÔNG lọc theo tiêu chí đó.
	 * @param maPNK     mã phiếu cần tìm ("" = không lọc), nên tìm gần đúng: LIKE %...%
	 * @param tuNgay    chỉ lấy phiếu có ngayNhap >= tuNgay (null = không lọc)
	 * @param denNgay   chỉ lấy phiếu có ngayNhap <= denNgay, tính đến hết ngày (null = không lọc)
	 * @param maNVLap   mã nhân viên lập ("" = không lọc)
	 * @param maNCC     mã nhà cung cấp ("" = không lọc)
	 * TODO: gọi DAO (JOIN NhanVien, NhaCungCap để lấy hoTenNV, tenNCC) rồi đổi mỗi dòng thành
	 * new PhieuNhapRow(maPNK, ngayNhap, maNV, hoTenNV, maNCC, tenNCC, ghiChu, tongTienNhapKho).
	 */
	private List<PhieuNhapRow> timKiemPhieuNhapKho(String maPNK, LocalDate tuNgay, LocalDate denNgay, String maNVLap,
			String maNCC) {
		List<PhieuNhapRow> ds = new ArrayList<>();
		// TODO: ds.add(new PhieuNhapRow(...));
		return ds;
	}

	/**
	 * Lấy chi tiết của một phiếu (JOIN PhuTung để lấy tên, đơn vị tính).
	 * TODO: gọi DAO rồi ds.add(new ChiTietRow(maPhuTung, tenPhuTung, donViTinh, soLuong, donGiaNhap)).
	 */
	private List<ChiTietRow> layChiTietPhieuNhap(String maPNK) {
		List<ChiTietRow> ds = new ArrayList<>();
		// TODO: ds.add(new ChiTietRow(...));
		return ds;
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
		JLabel lblTitle = new JLabel("Tra cứu phiếu nhập kho");
		lblTitle.setFont(FONT_TITLE);
		lblTitle.setForeground(TITLE);
		JLabel lblSub = new JLabel("Tìm kiếm, xem chi tiết và in các phiếu nhập kho đã lập.");
		lblSub.setFont(FONT_LABEL);
		lblSub.setForeground(TEXT_MUTED);
		tieuDe.add(lblTitle);
		tieuDe.add(lblSub);
		header.add(tieuDe, BorderLayout.WEST);

		ActionButton btnQuayLai = new ActionButton("Quản lý kho", ActionButton.KIEU_VIEN_XANH, IMG_QUAY_LAI);
		btnQuayLai.addActionListener(e -> {
			if (quayVeQuanLyKho != null) {
				quayVeQuanLyKho.run();
			}
		});
		JPanel wrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		wrap.setOpaque(false);
		wrap.add(btnQuayLai);
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
	// THẺ 1: THÔNG TIN TRA CỨU
	// =====================================================================
	private CardPanel taoTheTraCuu() {
		CardPanel the = new CardPanel("Thông tin tra cứu", "");
		the.setTitleIcon(IMG_TIM_KIEM);
		the.getBody().setBorder(new EmptyBorder(16, 20, 18, 20));

		txtMaPhieu = new Input("Nhập mã phiếu nhập kho", false);
		txtMaNV = new Input("Nhập mã nhân viên lập", false);
		txtMaNCC = new Input("Nhập mã nhà cung cấp", false);
		txtTuNgay = new Input("dd/MM/yyyy", false);
		txtTuNgay.themIconBenPhai(IMG_LICH, null);
		txtDenNgay = new Input("dd/MM/yyyy", false);
		txtDenNgay.themIconBenPhai(IMG_LICH, null);

		ActionButton btnTraCuu = new ActionButton("Tra cứu", ActionButton.KIEU_CHINH, IMG_TIM_KIEM);
		ActionButton btnDatLai = new ActionButton("Đặt lại", ActionButton.KIEU_VIEN_XANH, IMG_DAT_LAI);
		btnTraCuu.addActionListener(e -> traCuu());
		btnDatLai.addActionListener(e -> datLai());
		txtMaPhieu.addActionListener(e -> traCuu());
		txtMaNV.addActionListener(e -> traCuu());
		txtMaNCC.addActionListener(e -> traCuu());
		txtTuNgay.addActionListener(e -> traCuu());
		txtDenNgay.addActionListener(e -> traCuu());

		// Lưới 6 cột: hàng 1 gồm 3 ô, hàng 2 gồm 2 ô ngày và cụm nút căn phải (mỗi mục chiếm 2 cột)
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
		themO(luoi, labeled("Mã phiếu nhập kho", txtMaPhieu), 1, 0, 16, 14);
		themO(luoi, labeled("Mã nhân viên lập", txtMaNV), 1, 2, 16, 14);
		themO(luoi, labeled("Mã nhà cung cấp", txtMaNCC), 1, 4, 0, 14);
		themO(luoi, labeled("Ngày nhập từ", txtTuNgay), 2, 0, 16, 0);
		themO(luoi, labeled("Đến ngày", txtDenNgay), 2, 2, 16, 0);

		JPanel cumNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
		cumNut.setOpaque(false);
		cumNut.add(labeledFixed(" ", btnTraCuu));
		cumNut.add(labeledFixed(" ", btnDatLai));
		themO(luoi, cumNut, 2, 4, 0, 0);

		the.getBody().add(luoi, BorderLayout.CENTER);
		return the;
	}

	/** Mỗi ô chiếm 2 cột của lưới 6 cột. */
	private static void themO(JPanel p, Component c, int row, int col, int phai, int duoi) {
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = col;
		g.gridy = row;
		g.gridwidth = 2;
		g.weightx = 2;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.anchor = GridBagConstraints.NORTHWEST;
		g.insets = new Insets(0, 0, duoi, phai);
		p.add(c, g);
	}

	// =====================================================================
	// THẺ 2: DANH SÁCH PHIẾU NHẬP KHO
	// =====================================================================
	private JPanel taoTheDanhSach() {
		RoundedPanel the = new RoundedPanel(12, Color.WHITE);
		the.setLayout(new BorderLayout());
		theDanhSach = the;

		// --- Đầu thẻ ---
		JPanel dau = new JPanel(new BorderLayout());
		dau.setOpaque(false);
		dau.setBorder(new EmptyBorder(16, 20, 12, 20));

		JPanel trai = new JPanel();
		trai.setOpaque(false);
		trai.setLayout(new BoxLayout(trai, BoxLayout.Y_AXIS));

		JPanel hangTieuDe = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		hangTieuDe.setOpaque(false);
		hangTieuDe.setAlignmentX(Component.LEFT_ALIGNMENT);
		JLabel lblTieuDe = new JLabel("Danh sách phiếu nhập kho");
		lblTieuDe.setFont(FONT_SECTION);
		lblTieuDe.setForeground(TITLE);
		lblKetQua = new Pill();
		hangTieuDe.add(lblTieuDe);
		hangTieuDe.add(lblKetQua);

		lblDieuKien = new JLabel(" ");
		lblDieuKien.setFont(FONT_SMALL);
		lblDieuKien.setForeground(TEXT_MUTED);
		lblDieuKien.setBorder(new EmptyBorder(4, 0, 0, 0));
		lblDieuKien.setAlignmentX(Component.LEFT_ALIGNMENT);

		trai.add(hangTieuDe);
		trai.add(lblDieuKien);
		dau.add(trai, BorderLayout.WEST);
		the.add(dau, BorderLayout.NORTH);

		// --- Bảng ---
		model = new DefaultTableModel(
				new String[] { "Mã phiếu", "Ngày nhập", "Nhân viên lập", "Nhà cung cấp", "Tổng tiền", "Thao tác" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tblPhieu = new BangCoThongBao(model);
		tuyChinhBang(tblPhieu, new int[] { 170, 110, 210, 240, 150, 110 },
				new TableCellRenderer[] { new CellRenderer(true), new CellRenderer(false),
						new HaiDongRenderer(true), new HaiDongRenderer(false), new CellRenderer(false),
						new ThaoTacRenderer(new SlotIcon(IMG_XEM, 16, 16, DASH), ThaoTacRenderer.NEN_XANH_NHAT,
								new SlotIcon(IMG_IN, 16, 16, DASH), ThaoTacRenderer.NEN_VIEN) });

		tblPhieu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = tblPhieu.rowAtPoint(e.getPoint());
				if (row < 0) {
					return;
				}
				PhieuNhapRow p = (PhieuNhapRow) model.getValueAt(row, COL_NHAN_VIEN);
				int v = vungIcon(tblPhieu, e, COL_THAO_TAC);
				if (v == 1) {
					xem(p);
				} else if (v == 2) {
					in(p);
				}
			}
		});
		tblPhieu.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				tblPhieu.setCursor(Cursor.getPredefinedCursor(
						vungIcon(tblPhieu, e, COL_THAO_TAC) != 0 ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
			}
		});

		spBang = new JScrollPane(tblPhieu);
		spBang.setBorder(null);
		spBang.getViewport().setBackground(Color.WHITE);
		the.add(spBang, BorderLayout.CENTER);

		the.add(taoChanThe(), BorderLayout.SOUTH);
		return the;
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
		btnTruoc.addActionListener(e -> {
			if (trang > 1) {
				trang--;
				hienThiTrang();
			}
		});
		btnSau.addActionListener(e -> {
			if (trang < tongSoTrang()) {
				trang++;
				hienThiTrang();
			}
		});

		RoundedPanel chip = new RoundedPanel(8, PRIMARY);
		chip.setLayout(new GridBagLayout());
		chip.setPreferredSize(new Dimension(NUT, NUT));
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
		b.setPreferredSize(new Dimension(NUT, NUT));
		return b;
	}

	// =====================================================================
	// NGHIỆP VỤ TRA CỨU
	// =====================================================================
	private void traCuu() {
		LocalDate tu = null;
		LocalDate den = null;

		String sTu = txtTuNgay.getText().trim();
		if (!sTu.isEmpty()) {
			tu = parseNgay(sTu);
			if (tu == null) {
				baoLoi("Ngày bắt đầu không hợp lệ (định dạng dd/MM/yyyy)!", txtTuNgay);
				return;
			}
		}
		String sDen = txtDenNgay.getText().trim();
		if (!sDen.isEmpty()) {
			den = parseNgay(sDen);
			if (den == null) {
				baoLoi("Ngày kết thúc không hợp lệ (định dạng dd/MM/yyyy)!", txtDenNgay);
				return;
			}
		}
		if (tu != null && den != null && tu.isAfter(den)) {
			baoLoi("Ngày bắt đầu không được sau ngày kết thúc!", txtTuNgay);
			return;
		}

		List<PhieuNhapRow> ds = timKiemPhieuNhapKho(txtMaPhieu.getText().trim(), tu, den, txtMaNV.getText().trim(),
				txtMaNCC.getText().trim());
		dsKetQua.clear();
		if (ds != null) {
			dsKetQua.addAll(ds);
		}
		dsKetQua.sort(Comparator.comparing((PhieuNhapRow p) -> p.ngayNhap).reversed()); // mới nhất trước

		daTraCuu = true;
		trang = 1;
		lblDieuKien.setText("Ngày nhập: " + (tu == null ? "Không giới hạn" : tu.format(FMT_NGAY)) + " – "
				+ (den == null ? "Không giới hạn" : den.format(FMT_NGAY)));
		hienThiTrang();
	}

	private int tongSoTrang() {
		return Math.max(1, (dsKetQua.size() + SO_DONG_TRANG - 1) / SO_DONG_TRANG);
	}

	/** Đổ dòng của trang hiện tại vào bảng và cập nhật chân thẻ. */
	private void hienThiTrang() {
		int tong = dsKetQua.size();
		trang = Math.min(Math.max(1, trang), tongSoTrang());
		int tu = (trang - 1) * SO_DONG_TRANG;
		int den = Math.min(tong, tu + SO_DONG_TRANG);

		model.setRowCount(0);
		for (int i = tu; i < den; i++) {
			PhieuNhapRow p = dsKetQua.get(i);
			model.addRow(new Object[] { p.ma, p.ngayNhap.format(FMT_NGAY), p, p, FMT_TIEN.format(p.tongTien) + " đ",
					p });
		}

		lblKetQua.setText(tong + " kết quả");
		lblHienThi.setText("Hiển thị " + (den - tu) + " / " + tong + " phiếu");
		lblSoTrang.setText(String.valueOf(trang));
		btnTruoc.setEnabled(trang > 1);
		btnSau.setEnabled(trang < tongSoTrang());

		int soDong = Math.max(2, den - tu); // chừa chỗ cho dòng thông báo khi chưa có kết quả
		spBang.setPreferredSize(new Dimension(100, CAO_HEADER + soDong * CAO_DONG));
		theDanhSach.revalidate();
		theDanhSach.repaint();
	}

	// =====================================================================
	// XEM / IN
	// =====================================================================
	/** Hiện hộp thoại chi tiết phiếu: thông tin chung + bảng các phụ tùng đã nhập. */
	private void xem(PhieuNhapRow p) {
		List<ChiTietRow> ct = layChiTietPhieuNhap(p.ma);

		Window cha = SwingUtilities.getWindowAncestor(this);
		JDialog dlg = new JDialog(cha, "Chi tiết phiếu nhập kho " + p.ma, Dialog.ModalityType.APPLICATION_MODAL);
		JPanel nd = new JPanel(new BorderLayout(0, 14));
		nd.setBackground(Color.WHITE);
		nd.setBorder(new EmptyBorder(20, 24, 20, 24));

		// --- Thông tin chung ---
		JPanel thongTin = new JPanel();
		thongTin.setOpaque(false);
		thongTin.setLayout(new BoxLayout(thongTin, BoxLayout.Y_AXIS));
		thongTin.add(dongThongTin("Mã phiếu", p.ma));
		thongTin.add(dongThongTin("Ngày nhập", p.ngayNhap.format(FMT_NGAY)));
		thongTin.add(dongThongTin("Nhân viên lập", p.tenNV + " (" + p.maNV + ")"));
		thongTin.add(dongThongTin("Nhà cung cấp", p.tenNCC + " (" + p.maNCC + ")"));
		thongTin.add(dongThongTin("Ghi chú", p.ghiChu == null || p.ghiChu.isEmpty() ? "(không có)" : p.ghiChu));
		nd.add(thongTin, BorderLayout.NORTH);

		// --- Bảng chi tiết ---
		DefaultTableModel m = new DefaultTableModel(
				new String[] { "Mã phụ tùng", "Tên phụ tùng", "ĐVT", "Số lượng", "Đơn giá nhập (đ)", "Thành tiền (đ)" },
				0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		if (ct != null) {
			for (ChiTietRow c : ct) {
				m.addRow(new Object[] { c.maPhuTung, c.tenPhuTung, c.donViTinh == null ? "" : c.donViTinh, c.soLuong,
						FMT_TIEN.format(c.donGiaNhap), FMT_TIEN.format(c.soLuong * c.donGiaNhap) });
			}
		}
		BangCoThongBao bang = new BangCoThongBao(m);
		bang.setThongBaoTrong("Phiếu này chưa có chi tiết.");
		tuyChinhBang(bang, new int[] { 110, 220, 70, 90, 130, 130 },
				new TableCellRenderer[] { new CellRenderer(true), new CellRenderer(false), new CellRenderer(false),
						new CellRenderer(false), new CellRenderer(false), new CellRenderer(false) });
		JScrollPane sp = new JScrollPane(bang);
		sp.setBorder(null);
		sp.getViewport().setBackground(Color.WHITE);
		int n = Math.max(2, Math.min(m.getRowCount(), 6));
		sp.setPreferredSize(new Dimension(760, CAO_HEADER + n * CAO_DONG));
		nd.add(sp, BorderLayout.CENTER);

		// --- Tổng tiền + nút đóng ---
		JPanel chan = new JPanel(new BorderLayout());
		chan.setOpaque(false);
		JLabel lblTong = new JLabel("Tổng tiền nhập kho: " + FMT_TIEN.format(p.tongTien) + " đ");
		lblTong.setFont(FONT_SECTION);
		lblTong.setForeground(TITLE);
		ActionButton btnDong = new ActionButton("Đóng", ActionButton.KIEU_VIEN_XAM);
		btnDong.addActionListener(e -> dlg.dispose());
		JPanel wrapNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		wrapNut.setOpaque(false);
		wrapNut.add(btnDong);
		chan.add(lblTong, BorderLayout.WEST);
		chan.add(wrapNut, BorderLayout.EAST);
		nd.add(chan, BorderLayout.SOUTH);

		dlg.setContentPane(nd);
		dlg.pack();
		dlg.setLocationRelativeTo(cha);
		dlg.setVisible(true);
	}

	private static JPanel dongThongTin(String nhan, String giaTri) {
		JPanel p = new JPanel(new BorderLayout(10, 0));
		p.setOpaque(false);
		p.setBorder(new EmptyBorder(2, 0, 2, 0));
		JLabel l1 = new JLabel(nhan + ":");
		l1.setFont(FONT_BOLD);
		l1.setForeground(TEXT_MUTED);
		l1.setPreferredSize(new Dimension(100, 20));
		JLabel l2 = new JLabel(giaTri);
		l2.setFont(FONT_BODY);
		l2.setForeground(TEXT_DARK);
		p.add(l1, BorderLayout.WEST);
		p.add(l2, BorderLayout.CENTER);
		return p;
	}

	private void in(PhieuNhapRow p) {
		// TODO: xuất / in phiếu nhập kho (PDF hoặc PrinterJob)
		JOptionPane.showMessageDialog(this, "Chức năng in phiếu " + p.ma + " đang được xây dựng.", "Thông báo",
				JOptionPane.INFORMATION_MESSAGE);
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

	/** Vị trí click trong ô "Thao tác": 0 = ngoài, 1 = nút Xem, 2 = nút In. */
	private static int vungIcon(JTable t, MouseEvent e, int colThaoTac) {
		int row = t.rowAtPoint(e.getPoint());
		int col = t.columnAtPoint(e.getPoint());
		if (row < 0 || col != colThaoTac) {
			return 0;
		}
		Rectangle rc = t.getCellRect(row, col, false);
		int x = e.getX() - rc.x - PAD_TRAI;
		int y = e.getY() - rc.y - (CAO_DONG - NUT) / 2;
		if (y < 0 || y > NUT) {
			return 0;
		}
		if (x >= 0 && x <= NUT) {
			return 1;
		}
		if (x >= NUT + KHOANG && x <= 2 * NUT + KHOANG) {
			return 2;
		}
		return 0;
	}

	private static Color mauNen(boolean chon, int row) {
		return chon ? ROW_SELECTED : (row % 2 == 1 ? ROW_ALT : Color.WHITE);
	}

	/** JTable tự vẽ dòng thông báo ở giữa khi không có dòng dữ liệu nào. */
	private class BangCoThongBao extends JTable {
		private static final long serialVersionUID = 1L;
		private String thongBaoTrong = null; // null = dùng thông báo mặc định của màn hình tra cứu

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
				String s = thongBaoTrong != null ? thongBaoTrong
						: (daTraCuu ? "Không tìm thấy phiếu nhập kho nào phù hợp với điều kiện tra cứu."
								: "Chưa có kết quả. Hãy nhập điều kiện lọc rồi bấm \"Tra cứu\".");
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2.setFont(FONT_BODY);
				g2.setColor(TEXT_MUTED);
				FontMetrics fm = g2.getFontMetrics();
				Rectangle r = getVisibleRect();
				int x = r.x + (r.width - fm.stringWidth(s)) / 2;
				int y = r.y + (r.height - fm.getHeight()) / 2 + fm.getAscent();
				g2.drawString(s, x, y);
				g2.dispose();
			}
		}
	}

	/** Huy hiệu "N kết quả" bo tròn, nền xanh nhạt. */
	private static class Pill extends JLabel {
		private static final long serialVersionUID = 1L;

		Pill() {
			setFont(FONT_SMALL_BOLD);
			setForeground(PRIMARY);
			setBorder(new EmptyBorder(3, 10, 3, 10));
			setOpaque(false);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(PRIMARY_LIGHT);
			g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
			g2.dispose();
			super.paintComponent(g);
		}
	}

	private static class HeaderRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
			super.getTableCellRendererComponent(t, v, false, false, r, c);
			setOpaque(true);
			setBackground(PRIMARY_LIGHT);
			setForeground(TEXT_MUTED);
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

	/** Ô hai dòng: tên ở trên, mã (chữ nhỏ, xám) ở dưới. nhanVien = true: nhân viên lập; false: nhà cung cấp. */
	private static class HaiDongRenderer implements TableCellRenderer {
		private final boolean nhanVien;
		private final JPanel panel = new JPanel();
		private final JLabel lblTen = new JLabel();
		private final JLabel lblMa = new JLabel();

		HaiDongRenderer(boolean nhanVien) {
			this.nhanVien = nhanVien;
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, new Color(236, 240, 245)),
					new EmptyBorder(8, PAD_TRAI, 8, 8)));
			lblTen.setFont(FONT_BODY);
			lblTen.setForeground(TEXT_DARK);
			lblMa.setFont(FONT_SMALL);
			lblMa.setForeground(TEXT_MUTED);
			panel.add(Box.createVerticalGlue());
			panel.add(lblTen);
			panel.add(lblMa);
			panel.add(Box.createVerticalGlue());
		}

		@Override
		public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
			PhieuNhapRow p = (PhieuNhapRow) v;
			panel.setBackground(mauNen(sel, r));
			lblTen.setText(nhanVien ? p.tenNV : p.tenNCC);
			lblMa.setText(nhanVien ? p.maNV : p.maNCC);
			return panel;
		}
	}

	/** Ô "Thao tác": hai nút vuông bo góc có icon (VÙNG CHÈN ẢNH). */
	private static class ThaoTacRenderer extends JPanel implements TableCellRenderer {
		private static final long serialVersionUID = 1L;
		static final int NEN_XANH_NHAT = 0; // nút Xem
		static final int NEN_VIEN = 1; // nút In: nền trắng, viền xám

		private final SlotIcon icon1, icon2;
		private final int kieu1, kieu2;

		ThaoTacRenderer(SlotIcon icon1, int kieu1, SlotIcon icon2, int kieu2) {
			this.icon1 = icon1;
			this.kieu1 = kieu1;
			this.icon2 = icon2;
			this.kieu2 = kieu2;
			setOpaque(true);
		}

		@Override
		public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
			setBackground(mauNen(sel, r));
			return this;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			int y = (getHeight() - NUT) / 2;
			int x1 = PAD_TRAI;
			int x2 = x1 + NUT + KHOANG;
			veNut(g2, x1, y, kieu1);
			veNut(g2, x2, y, kieu2);
			icon1.paintIcon(this, g2, x1 + (NUT - 16) / 2, y + (NUT - 16) / 2);
			icon2.paintIcon(this, g2, x2 + (NUT - 16) / 2, y + (NUT - 16) / 2);

			g2.setColor(new Color(236, 240, 245));
			g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
			g2.dispose();
		}

		private void veNut(Graphics2D g2, int x, int y, int kieu) {
			if (kieu == NEN_VIEN) {
				g2.setColor(Color.WHITE);
				g2.fillRoundRect(x, y, NUT, NUT, 10, 10);
				g2.setColor(new Color(175, 185, 200));
				g2.drawRoundRect(x, y, NUT - 1, NUT - 1, 10, 10);
			} else {
				g2.setColor(PRIMARY_LIGHT);
				g2.fillRoundRect(x, y, NUT, NUT, 10, 10);
			}
		}
	}
}
