package gui;

import static gui.UITheme.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 * Màn hình TRA CỨU PHIẾU TIẾP NHẬN (JPanel) - được nhúng vào CardLayout của TrangChuTiepNhanVienGUI.
 *
 * Mỗi lần màn hình được mở, gọi {@link #datLai()} để về trạng thái ban đầu: 0 kết quả.
 * Kết quả chỉ hiện sau khi người dùng bấm "Tra cứu" (hoặc Enter) theo bộ lọc ở thẻ "Thông tin tra cứu".
 */
public class TraCuuPhieuTiepNhanGUI extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final DateTimeFormatter FMT_NGAY = DateTimeFormatter.ofPattern("dd/MM/uuuu")
			.withResolverStyle(ResolverStyle.STRICT);

	private static final int SO_DONG_TRANG = 8;
	private static final int CAO_DONG = 56;
	private static final int CAO_HEADER_BANG = 40;
	private static final int COL_KHACH = 2;
	private static final int COL_THAO_TAC = 5;
	private static final int PAD_TRAI = 16;
	private static final int NUT = 32;
	private static final int KHOANG = 8;

	private static final Border O_BANG = new CompoundBorder(new MatteBorder(0, 0, 1, 0, new Color(236, 240, 245)),
			new EmptyBorder(0, PAD_TRAI, 0, 8));

	private final Runnable quayVeLapPhieu;
	private final List<DuLieuMauTiepNhan.Phieu> dsKetQua = new ArrayList<>();
	private int trang = 1;
	private boolean daTraCuu = false;

	// ---- Bộ lọc ----
	private Input txtMaPhieu, txtBienSo, txtKhachHang, txtTuNgay, txtDenNgay;
	private JComboBox<String> cboTrangThai;

	// ---- Kết quả ----
	private Pill lblKetQua;
	private JLabel lblDieuKien, lblHienThi, lblSoTrang;
	private JTable tblPhieu;
	private DefaultTableModel model;
	private JScrollPane spBang;
	private JPanel theDanhSach;
	private JButton btnTruoc, btnSau;

	public TraCuuPhieuTiepNhanGUI(Runnable quayVeLapPhieu) {
		this.quayVeLapPhieu = quayVeLapPhieu;
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
		txtBienSo.setText("");
		txtKhachHang.setText("");
		txtTuNgay.setText(homNay.minusDays(2).format(FMT_NGAY));
		txtDenNgay.setText(homNay.format(FMT_NGAY));
		cboTrangThai.setSelectedIndex(0);

		dsKetQua.clear();
		daTraCuu = false;
		trang = 1;
		lblDieuKien.setText("Chưa tra cứu - nhập điều kiện lọc rồi bấm \"Tra cứu\"");
		hienThiTrang();
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
		JLabel lblTitle = new JLabel("Tra cứu phiếu tiếp nhận");
		lblTitle.setFont(FONT_TITLE);
		lblTitle.setForeground(TITLE);
		JLabel lblSub = new JLabel("Tìm kiếm, xem chi tiết và in các phiếu tiếp nhận đã lập.");
		lblSub.setFont(FONT_LABEL);
		lblSub.setForeground(TEXT_MUTED);
		tieuDe.add(lblTitle);
		tieuDe.add(lblSub);
		header.add(tieuDe, BorderLayout.WEST);

		ActionButton btnQuayLai = new ActionButton("Lập phiếu tiếp nhận", ActionButton.KIEU_VIEN_XANH, IMG_QUAY_LAI);
		btnQuayLai.addActionListener(e -> {
			if (quayVeLapPhieu != null) {
				quayVeLapPhieu.run();
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

		txtMaPhieu = new Input("Nhập mã phiếu", false);
		txtBienSo = new Input("Nhập biển số xe", false);
		txtKhachHang = new Input("Nhập tên hoặc số điện thoại", false);
		txtTuNgay = new Input("dd/MM/yyyy", false);
		txtTuNgay.themIconBenPhai(IMG_LICH, null);
		txtDenNgay = new Input("dd/MM/yyyy", false);
		txtDenNgay.themIconBenPhai(IMG_LICH, null);
		cboTrangThai = createComboBox(themDau("Tất cả trạng thái", DuLieuMauTiepNhan.TRANG_THAI));

		ActionButton btnTraCuu = new ActionButton("Tra cứu", ActionButton.KIEU_CHINH, IMG_TIM_KIEM);
		ActionButton btnDatLai = new ActionButton("Đặt lại", ActionButton.KIEU_VIEN_XANH, IMG_DAT_LAI);
		btnTraCuu.addActionListener(e -> traCuu());
		btnDatLai.addActionListener(e -> datLai());
		txtMaPhieu.addActionListener(e -> traCuu());
		txtBienSo.addActionListener(e -> traCuu());
		txtKhachHang.addActionListener(e -> traCuu());
		txtTuNgay.addActionListener(e -> traCuu());
		txtDenNgay.addActionListener(e -> traCuu());

		JPanel ngan = new JPanel(new GridBagLayout());
		ngan.setOpaque(false);
		// Hàng 1
		themCot(ngan, labeled("Mã phiếu tiếp nhận", txtMaPhieu), 0, 0, 1.0, 16);
		themCot(ngan, labeled("Biển số xe", txtBienSo), 1, 0, 1.0, 16);
		themCot(ngan, labeled("Khách hàng / Số điện thoại", txtKhachHang), 2, 0, 1.0, 0);
		// Hàng 2
		JPanel hang2 = new JPanel(new GridBagLayout());
		hang2.setOpaque(false);
		themCot(hang2, labeled("Ngày tiếp nhận từ", txtTuNgay), 0, 0, 1.1, 16);
		themCot(hang2, labeled("Đến ngày", txtDenNgay), 1, 0, 1.1, 16);
		themCot(hang2, labeled("Trạng thái phiếu", cboTrangThai), 2, 0, 1.0, 16);
		themCot(hang2, labeledFixed(" ", btnTraCuu), 3, 0, 0, 12);
		themCot(hang2, labeledFixed(" ", btnDatLai), 4, 0, 0, 0);

		GridBagConstraints g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 1;
		g.gridwidth = 3;
		g.weightx = 1;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.insets = new Insets(14, 0, 0, 0);
		ngan.add(hang2, g);

		the.getBody().add(ngan, BorderLayout.CENTER);
		return the;
	}

	private static void themCot(JPanel p, Component c, int x, int y, double wx, int phai) {
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = x;
		g.gridy = y;
		g.weightx = wx;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.anchor = GridBagConstraints.NORTH;
		g.insets = new Insets(0, 0, 0, phai);
		p.add(c, g);
	}

	private static String[] themDau(String dau, String[] ds) {
		String[] kq = new String[ds.length + 1];
		kq[0] = dau;
		System.arraycopy(ds, 0, kq, 1, ds.length);
		return kq;
	}

	// =====================================================================
	// THẺ 2: DANH SÁCH PHIẾU TIẾP NHẬN
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
		JLabel lblTieuDe = new JLabel("Danh sách phiếu tiếp nhận");
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

		// --- Bảng (KHÔNG có cột Trạng thái) ---
		model = new DefaultTableModel(new String[] { "Mã phiếu", "Ngày tiếp nhận", "Khách hàng", "Biển số xe",
				"Yêu cầu tiếp nhận", "Xem / In" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tblPhieu = new JTable(model) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (getRowCount() == 0) {
					String s = daTraCuu ? "Không tìm thấy phiếu tiếp nhận nào phù hợp với điều kiện tra cứu."
							: "Chưa có kết quả. Hãy nhập điều kiện lọc rồi bấm \"Tra cứu\".";
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
		};
		tuyChinhBang();

		spBang = new JScrollPane(tblPhieu);
		spBang.setBorder(null);
		spBang.getViewport().setBackground(Color.WHITE);
		the.add(spBang, BorderLayout.CENTER);

		// --- Chân thẻ ---
		the.add(taoChanThe(), BorderLayout.SOUTH);
		return the;
	}

	private void tuyChinhBang() {
		tblPhieu.setRowHeight(CAO_DONG);
		tblPhieu.setShowGrid(false);
		tblPhieu.setIntercellSpacing(new Dimension(0, 0));
		tblPhieu.setFont(FONT_BODY);
		tblPhieu.setForeground(TEXT_DARK);
		tblPhieu.setSelectionBackground(ROW_SELECTED);
		tblPhieu.setSelectionForeground(TEXT_DARK);
		tblPhieu.setFillsViewportHeight(true);
		tblPhieu.setFocusable(false);

		JTableHeader th = tblPhieu.getTableHeader();
		th.setReorderingAllowed(false);
		th.setResizingAllowed(false);
		th.setPreferredSize(new Dimension(100, CAO_HEADER_BANG));
		th.setDefaultRenderer(new HeaderRenderer());

		int[] rong = { 150, 120, 190, 120, 280, 110 };
		for (int i = 0; i < rong.length; i++) {
			tblPhieu.getColumnModel().getColumn(i).setPreferredWidth(rong[i]);
		}
		tblPhieu.getColumnModel().getColumn(0).setCellRenderer(new CellRenderer(true));
		tblPhieu.getColumnModel().getColumn(1).setCellRenderer(new CellRenderer(false));
		tblPhieu.getColumnModel().getColumn(COL_KHACH).setCellRenderer(new KhachHangRenderer());
		tblPhieu.getColumnModel().getColumn(3).setCellRenderer(new CellRenderer(false));
		tblPhieu.getColumnModel().getColumn(4).setCellRenderer(new CellRenderer(false));
		tblPhieu.getColumnModel().getColumn(COL_THAO_TAC).setCellRenderer(new ThaoTacRenderer());

		tblPhieu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = tblPhieu.rowAtPoint(e.getPoint());
				if (row < 0) {
					return;
				}
				DuLieuMauTiepNhan.Phieu p = (DuLieuMauTiepNhan.Phieu) model.getValueAt(row, COL_KHACH);
				int v = vungThaoTac(e);
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
				tblPhieu.setCursor(Cursor
						.getPredefinedCursor(vungThaoTac(e) != 0 ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
			}
		});
	}

	/** Vị trí click trong ô "Xem / In": 0 = ngoài, 1 = nút Xem, 2 = nút In. */
	private int vungThaoTac(MouseEvent e) {
		int row = tblPhieu.rowAtPoint(e.getPoint());
		int col = tblPhieu.columnAtPoint(e.getPoint());
		if (row < 0 || col != COL_THAO_TAC) {
			return 0;
		}
		Rectangle rc = tblPhieu.getCellRect(row, col, false);
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

		String ma = txtMaPhieu.getText().trim().toUpperCase();
		String bienSo = DuLieuMauTiepNhan.chuanHoaBienSo(txtBienSo.getText());
		String khachChu = DuLieuMauTiepNhan.boDau(txtKhachHang.getText().trim());
		String khachSo = DuLieuMauTiepNhan.chiSo(txtKhachHang.getText());
		boolean locTrangThai = cboTrangThai.getSelectedIndex() > 0;
		String trangThai = (String) cboTrangThai.getSelectedItem();

		// TODO: thay vòng lặp này bằng phieuTiepNhanDAO.timKiem(...) với cùng các điều kiện lọc
		dsKetQua.clear();
		for (DuLieuMauTiepNhan.Phieu p : DuLieuMauTiepNhan.PHIEU) {
			if (!ma.isEmpty() && !p.ma.toUpperCase().contains(ma)) {
				continue;
			}
			if (!bienSo.isEmpty() && !DuLieuMauTiepNhan.chuanHoaBienSo(p.bienSo).contains(bienSo)) {
				continue;
			}
			if (!khachChu.isEmpty()) {
				boolean khop = DuLieuMauTiepNhan.boDau(p.tenKhach).contains(khachChu)
						|| (!khachSo.isEmpty() && p.sdt.contains(khachSo));
				if (!khop) {
					continue;
				}
			}
			if (tu != null && p.ngay.isBefore(tu)) {
				continue;
			}
			if (den != null && p.ngay.isAfter(den)) {
				continue;
			}
			if (locTrangThai && !p.trangThai.equals(trangThai)) {
				continue;
			}
			dsKetQua.add(p);
		}
		Comparator<DuLieuMauTiepNhan.Phieu> moiNhatTruoc = Comparator
				.comparing((DuLieuMauTiepNhan.Phieu p) -> p.ngay).thenComparing((DuLieuMauTiepNhan.Phieu p) -> p.gio);
		dsKetQua.sort(moiNhatTruoc.reversed());

		daTraCuu = true;
		trang = 1;
		lblDieuKien.setText("Ngày tiếp nhận: " + (tu == null ? "Không giới hạn" : tu.format(FMT_NGAY)) + " – "
				+ (den == null ? "Không giới hạn" : den.format(FMT_NGAY)) + " • "
				+ (locTrangThai ? trangThai : "Tất cả trạng thái"));
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
			DuLieuMauTiepNhan.Phieu p = dsKetQua.get(i);
			model.addRow(new Object[] { p.ma, p.ngay.format(FMT_NGAY), p, p.bienSo, p.yeuCau, p });
		}

		lblKetQua.setText(tong + " kết quả");
		lblHienThi.setText("Hiển thị " + (den - tu) + " trên tổng số " + DuLieuMauTiepNhan.PHIEU.size() + " phiếu");
		lblSoTrang.setText(String.valueOf(trang));
		btnTruoc.setEnabled(trang > 1);
		btnSau.setEnabled(trang < tongSoTrang());

		int soDong = Math.max(2, den - tu); // chừa chỗ cho dòng thông báo khi chưa có kết quả
		spBang.setPreferredSize(new Dimension(100, CAO_HEADER_BANG + soDong * CAO_DONG));
		theDanhSach.revalidate();
		theDanhSach.repaint();
	}

	private void xem(DuLieuMauTiepNhan.Phieu p) {
		StringBuilder sb = new StringBuilder();
		sb.append("Mã phiếu: ").append(p.ma);
		sb.append("\nNgày giờ tiếp nhận: ").append(p.ngay.format(FMT_NGAY)).append(" ").append(p.gio);
		sb.append("\nKhách hàng: ").append(p.tenKhach).append(" - ").append(p.sdt);
		sb.append("\nĐịa chỉ: ").append(p.diaChi);
		sb.append("\nXe: ").append(p.bienSo).append(" - ").append(p.hangXe).append(" (").append(p.soKm)
				.append(" km)");
		sb.append("\nYêu cầu: ").append(p.yeuCau == null || p.yeuCau.isEmpty() ? "(không có)" : p.yeuCau);
		sb.append("\nNgười tiếp nhận: ").append(p.nguoiTiepNhan);
		sb.append("\nTrạng thái: ").append(p.trangThai);
		sb.append("\nHạng mục có lỗi:");
		if (p.tinhTrang.isEmpty()) {
			sb.append(" (không ghi nhận lỗi)");
		} else {
			p.tinhTrang.forEach((k, v) -> sb.append("\n  - ").append(k).append(": ").append(v));
		}
		JOptionPane.showMessageDialog(this, sb.toString(), "Chi tiết phiếu tiếp nhận", JOptionPane.INFORMATION_MESSAGE);
	}

	private void in(DuLieuMauTiepNhan.Phieu p) {
		// TODO: xuất / in phiếu tiếp nhận (PDF hoặc PrinterJob)
		JOptionPane.showMessageDialog(this, "Chức năng in phiếu " + p.ma + " đang được xây dựng.", "Thông báo",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private static LocalDate parseNgay(String s) {
		try {
			return LocalDate.parse(s, FMT_NGAY);
		} catch (DateTimeParseException ex) {
			return null;
		}
	}

	private void baoLoi(String msg, Component focus) {
		JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.WARNING_MESSAGE);
		focus.requestFocusInWindow();
	}

	// =====================================================================
	// RENDERER & THÀNH PHẦN VẼ RIÊNG CHO BẢNG
	// =====================================================================
	private static Color mauNen(boolean chon, int row) {
		return chon ? ROW_SELECTED : (row % 2 == 1 ? ROW_ALT : Color.WHITE);
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

	/** Ô "Khách hàng": tên ở trên, số điện thoại (chữ nhỏ, xám) ở dưới. */
	private static class KhachHangRenderer implements TableCellRenderer {
		private final JPanel panel = new JPanel();
		private final JLabel lblTen = new JLabel();
		private final JLabel lblSdt = new JLabel();

		KhachHangRenderer() {
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, new Color(236, 240, 245)),
					new EmptyBorder(8, PAD_TRAI, 8, 8)));
			lblTen.setFont(FONT_BODY);
			lblTen.setForeground(TEXT_DARK);
			lblSdt.setFont(FONT_SMALL);
			lblSdt.setForeground(TEXT_MUTED);
			panel.add(javax.swing.Box.createVerticalGlue());
			panel.add(lblTen);
			panel.add(lblSdt);
			panel.add(javax.swing.Box.createVerticalGlue());
		}

		@Override
		public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
			DuLieuMauTiepNhan.Phieu p = (DuLieuMauTiepNhan.Phieu) v;
			panel.setBackground(mauNen(sel, r));
			lblTen.setText(p.tenKhach);
			lblSdt.setText(dinhDangSdt(p.sdt));
			return panel;
		}

		/** "0912567890" -> "0912 567 890". */
		private static String dinhDangSdt(String s) {
			return s != null && s.length() == 10 ? s.substring(0, 4) + " " + s.substring(4, 7) + " " + s.substring(7)
					: s;
		}
	}

	/** Ô "Xem / In": hai nút vuông bo góc có icon (VÙNG CHÈN ẢNH: IMG_XEM, IMG_IN trong UITheme). */
	private static class ThaoTacRenderer extends JPanel implements TableCellRenderer {
		private static final long serialVersionUID = 1L;
		private static final Color DASH = new Color(150, 155, 165);
		private final SlotIcon iconXem = new SlotIcon(IMG_XEM, 16, 16, DASH);
		private final SlotIcon iconIn = new SlotIcon(IMG_IN, 16, 16, DASH);

		ThaoTacRenderer() {
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

			g2.setColor(PRIMARY_LIGHT);
			g2.fillRoundRect(x1, y, NUT, NUT, 10, 10);
			g2.setColor(Color.WHITE);
			g2.fillRoundRect(x2, y, NUT, NUT, 10, 10);
			g2.setColor(TEXT_DARK);
			g2.drawRoundRect(x2, y, NUT - 1, NUT - 1, 10, 10);

			iconXem.paintIcon(this, g2, x1 + (NUT - 16) / 2, y + (NUT - 16) / 2);
			iconIn.paintIcon(this, g2, x2 + (NUT - 16) / 2, y + (NUT - 16) / 2);

			g2.setColor(new Color(236, 240, 245));
			g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
			g2.dispose();
		}
	}
}
