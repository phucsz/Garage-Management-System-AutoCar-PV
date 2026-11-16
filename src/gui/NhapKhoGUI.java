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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 * Màn hình NHẬP KHO (JPanel) - lập phiếu nhập phụ tùng từ nhà cung cấp.
 * Được nhúng vào CardLayout của TrangChuQuanLyGUI, mở từ nút "Nhập kho" của QuanLyPhuTungGUI.
 *
 * Gồm các thẻ: Thông tin phiếu nhập, Chi tiết lô phụ tùng nhập, Ghi chú nhập kho, Tổng hợp phiếu nhập.
 *
 * Ánh xạ với CSDL:
 *  - PhieuNhapKho(maPNK, ngayNhap, maNVLap, maNCC, ghiChu, tongTienNhapKho)
 *  - ChiTietPhieuNhapKho(maPNK, maPhuTung, soLuong, donGiaNhap)  <- mỗi "lô phụ tùng" trên giao diện = 1 dòng chi tiết
 *  Mỗi phụ tùng chỉ xuất hiện 1 lần trong một phiếu (khóa chính maPNK + maPhuTung).
 *  Phụ tùng chỉ được chọn trong số phụ tùng của nhà cung cấp đã chọn (PhuTung.maNCC).
 *
 * KHÔNG có dữ liệu mẫu. Mọi chỗ cần lấy / ghi dữ liệu từ CSDL là các hàm có "TODO":
 *  layDanhSachNhaCungCap, layDanhSachPhuTungTheoNhaCungCap, demSoPhieuNhapTrongNgay, luuPhieuNhapKho.
 */
public class NhapKhoGUI extends JPanel {
	private static final long serialVersionUID = 1L;

	// =====================================================================
	// ICON CỦA MÀN HÌNH NÀY - TỰ NHẬP ĐƯỜNG DẪN VÀO GIỮA CẶP DẤU "".
	// Ví dụ: "/images/icon_sua.png" hoặc chỉ "icon_sua.png" (tự hiểu là nằm trong /images/).
	// Để rỗng thì hiện khung viền nét đứt. (Các icon dùng chung như IMG_THEM, IMG_LUU... nằm trong UITheme.)
	// =====================================================================
	private static final String ICON_SUA = ""; // TODO: icon "Sửa" (cây bút)
	private static final String ICON_XOA = ""; // TODO: icon "Xóa" (thùng rác)
	private static final String ICON_XAC_NHAN = ""; // TODO: icon dấu tích ở nút "Xác nhận nhập kho"

	private static final int CAO_DONG = 52;
	private static final int CAO_HEADER = 40;
	private static final int SO_DONG_TOI_THIEU = 3;
	private static final int PAD_TRAI = 16;
	private static final int NUT = 32;
	private static final int KHOANG = 8;
	private static final int COL_THAO_TAC = 7;
	private static final Color DASH = new Color(150, 155, 165);

	private static final DateTimeFormatter FMT_NGAY = DateTimeFormatter.ofPattern("dd/MM/uuuu")
			.withResolverStyle(ResolverStyle.STRICT);
	private static final DateTimeFormatter FMT_MA = DateTimeFormatter.ofPattern("ddMMyyyy");
	private static final NumberFormat FMT_TIEN = NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN"));
	private static final Border O_BANG = new CompoundBorder(new MatteBorder(0, 0, 1, 0, new Color(236, 240, 245)),
			new EmptyBorder(0, PAD_TRAI, 0, 8));

	/** Một mục chọn (mã - tên) cho ô danh sách. */
	private static class MucChon {
		final String ma, ten;

		MucChon(String ma, String ten) {
			this.ma = ma;
			this.ten = ten;
		}

		@Override
		public String toString() {
			return ten;
		}
	}

	/** Một phụ tùng có thể chọn khi thêm lô. TODO: có thể thay bằng entity.PhuTung. */
	private static class PhuTungChon {
		final String ma, ten, donViTinh;
		final LocalDate ngaySanXuat; // có thể null

		PhuTungChon(String ma, String ten, String donViTinh, LocalDate ngaySanXuat) {
			this.ma = ma;
			this.ten = ten;
			this.donViTinh = donViTinh;
			this.ngaySanXuat = ngaySanXuat;
		}

		@Override
		public String toString() {
			return ma + " - " + ten;
		}
	}

	/** Một lô phụ tùng nhập = một dòng ChiTietPhieuNhapKho. */
	private static class LoNhapRow {
		final PhuTungChon phuTung;
		int soLuong;
		double donGiaNhap;

		LoNhapRow(PhuTungChon phuTung, int soLuong, double donGiaNhap) {
			this.phuTung = phuTung;
			this.soLuong = soLuong;
			this.donGiaNhap = donGiaNhap;
		}

		double thanhTien() {
			return soLuong * donGiaNhap;
		}
	}

	private final Runnable quayVeQuanLyKho;
	private final List<LoNhapRow> dsLo = new ArrayList<>();
	private String maNVLap = "";
	private MucChon nccDangChon;
	private boolean dangNapNcc = false;

	// ---- Thẻ thông tin phiếu ----
	private Input txtMaPhieu, txtNgayNhap, txtNguoiLap;
	private JComboBox<MucChon> cboNhaCungCap;

	// ---- Thẻ chi tiết lô ----
	private CardPanel cardChiTiet;
	private BangCoThongBao tblLo;
	private DefaultTableModel model;
	private JScrollPane spLo;

	// ---- Ghi chú + tổng hợp ----
	private JTextArea txtGhiChu;
	private JLabel lblTongTien;

	/**
	 * @param quayVeQuanLyKho hành động của nút "Quản lý kho" (quay về màn hình QuanLyPhuTungGUI)
	 */
	public NhapKhoGUI(Runnable quayVeQuanLyKho) {
		this.quayVeQuanLyKho = quayVeQuanLyKho;
		setLayout(new BorderLayout());
		setBackground(BG);

		ScrollablePanel trangND = new ScrollablePanel(new BorderLayout(0, 16));
		trangND.setBackground(BG);
		trangND.setBorder(new EmptyBorder(22, 28, 28, 28));
		trangND.add(taoTieuDeTrang(), BorderLayout.NORTH);

		JPanel cacThe = new JPanel(new GridBagLayout());
		cacThe.setOpaque(false);
		themDong(cacThe, taoTheThongTin(), 0, 16);
		themDong(cacThe, taoTheChiTiet(), 1, 16);
		themDong(cacThe, taoHangGhiChuVaTongHop(), 2, 16);
		themDong(cacThe, taoHangNut(), 3, 0);
		trangND.add(cacThe, BorderLayout.CENTER);

		JScrollPane sp = new JScrollPane(trangND, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setBorder(null);
		sp.getVerticalScrollBar().setUnitIncrement(16);
		add(sp, BorderLayout.CENTER);

		lamMoi();
		refresh();
	}

	/**
	 * Gán người lập phiếu (nhân viên đang đăng nhập). Gọi sau khi tạo màn hình.
	 * @param maNV  mã nhân viên (lưu vào PhieuNhapKho.maNVLap)
	 * @param hoTen họ tên hiển thị
	 */
	public void setNguoiLap(String maNV, String hoTen) {
		this.maNVLap = maNV == null ? "" : maNV;
		txtNguoiLap.setText(hoTen == null ? "" : hoTen);
	}

	/** Gọi mỗi khi chuyển sang màn hình này: tải lại nhà cung cấp, cập nhật mã phiếu (giữ nguyên các lô đã nhập). */
	public void refresh() {
		napNhaCungCap();
		capNhatMaPhieu();
	}

	// =====================================================================
	// TODO: KẾT NỐI CƠ SỞ DỮ LIỆU (DAO) - BẠN CHỈ CẦN SỬA CÁC HÀM TRONG KHỐI NÀY
	// =====================================================================

	/**
	 * Lấy danh sách nhà cung cấp.
	 * TODO: SELECT maNCC, tenNCC FROM NhaCungCap ORDER BY tenNCC, rồi ds.add(new MucChon(maNCC, tenNCC)).
	 */
	private List<MucChon> layDanhSachNhaCungCap() {
		List<MucChon> ds = new ArrayList<>();
		// TODO: ds.add(new MucChon(ncc.getMaNCC(), ncc.getTenNCC()));
		return ds;
	}

	/**
	 * Lấy các phụ tùng thuộc nhà cung cấp (PhuTung.maNCC = maNCC) để chọn khi thêm lô.
	 * TODO: gọi DAO rồi ds.add(new PhuTungChon(maPhuTung, tenPhuTung, donViTinh, ngaySanXuat)).
	 */
	private List<PhuTungChon> layDanhSachPhuTungTheoNhaCungCap(String maNCC) {
		List<PhuTungChon> ds = new ArrayList<>();
		// TODO: ds.add(new PhuTungChon(pt.getMaPhuTung(), pt.getTenPhuTung(), pt.getDonViTinh(), pt.getNgaySanXuat()));
		return ds;
	}

	/**
	 * Đếm số phiếu nhập đã có trong ngày để sinh mã dạng PNK + ddMMyyyy + số thứ tự (VD: PNK17102026001).
	 * TODO: SELECT COUNT(*) FROM PhieuNhapKho WHERE CAST(ngayNhap AS DATE) = ?
	 *       (hoặc lấy số thứ tự lớn nhất trong mã để tránh trùng khi có phiếu bị xóa).
	 */
	private int demSoPhieuNhapTrongNgay(LocalDate ngay) {
		// TODO: return phieuNhapKhoDAO.demTheoNgay(ngay);
		return 0;
	}

	/**
	 * Lưu phiếu nhập kho. PHẢI chạy trong MỘT transaction gồm:
	 *  1) INSERT PhieuNhapKho (tongTienNhapKho = tổng thành tiền)
	 *  2) INSERT từng dòng ChiTietPhieuNhapKho
	 *  3) UPDATE PhuTung SET soLuongTon = soLuongTon + soLuong cho từng phụ tùng
	 * @return true nếu lưu thành công
	 */
	private boolean luuPhieuNhapKho(String maPNK, LocalDate ngayNhap, String maNVLap, String maNCC, String ghiChu,
			double tongTien, List<LoNhapRow> chiTiet) {
		// TODO: return phieuNhapKhoDAO.lapPhieu(...);
		return false;
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
		JLabel lblTitle = new JLabel("Nhập kho");
		lblTitle.setFont(FONT_TITLE);
		lblTitle.setForeground(TITLE);
		JLabel lblSub = new JLabel("Lập phiếu nhập phụ tùng từ nhà cung cấp và xác nhận số lượng thực nhận tại kho.");
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
	// THẺ 1: THÔNG TIN PHIẾU NHẬP
	// =====================================================================
	private CardPanel taoTheThongTin() {
		CardPanel the = new CardPanel("Thông tin phiếu nhập", "");
		the.getBody().setBorder(new EmptyBorder(16, 20, 8, 20));

		txtMaPhieu = new Input(null, true);
		txtNgayNhap = new Input("dd/MM/yyyy", false);
		txtNgayNhap.themIconBenPhai(IMG_LICH, null);
		txtNguoiLap = new Input(null, true);
		cboNhaCungCap = new JComboBox<>();
		cboNhaCungCap.setFont(FONT_BODY);
		cboNhaCungCap.setBackground(Color.WHITE);

		// Đổi ngày thì mã phiếu đổi theo
		txtNgayNhap.addActionListener(e -> capNhatMaPhieu());
		txtNgayNhap.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				capNhatMaPhieu();
			}
		});
		cboNhaCungCap.addActionListener(e -> khiDoiNhaCungCap());

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
		themO(luoi, labeled("Mã phiếu nhập", txtMaPhieu), 1, 0, 2, 16, 12);
		themO(luoi, labeled("Ngày nhập *", txtNgayNhap), 1, 2, 2, 16, 12);
		themO(luoi, labeled("Người lập", txtNguoiLap), 1, 4, 2, 0, 12);
		themO(luoi, labeled("Nhà cung cấp *", cboNhaCungCap), 2, 0, 6, 0, 0);

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

	/** Đổ danh sách nhà cung cấp từ CSDL vào ô chọn, cố gắng giữ lại lựa chọn hiện tại. */
	private void napNhaCungCap() {
		String maCu = (nccDangChon == null) ? null : nccDangChon.ma;
		List<MucChon> ds = layDanhSachNhaCungCap();

		DefaultComboBoxModel<MucChon> m = new DefaultComboBoxModel<>();
		m.addElement(new MucChon(null, "-- Chọn nhà cung cấp --"));
		if (ds != null) {
			for (MucChon c : ds) {
				m.addElement(c);
			}
		}
		dangNapNcc = true;
		cboNhaCungCap.setModel(m);
		cboNhaCungCap.setSelectedIndex(0);
		for (int i = 0; i < m.getSize() && maCu != null; i++) {
			if (maCu.equals(m.getElementAt(i).ma)) {
				cboNhaCungCap.setSelectedIndex(i);
			}
		}
		dangNapNcc = false;

		nccDangChon = (MucChon) cboNhaCungCap.getSelectedItem();
		if (!Objects.equals(maCu, nccDangChon.ma) && !dsLo.isEmpty()) {
			dsLo.clear(); // nhà cung cấp cũ không còn -> các lô đã thêm không còn hợp lệ
			hienThiLo();
		}
	}

	/** Đổi nhà cung cấp: các lô đã thêm thuộc nhà cung cấp cũ nên phải xóa (sau khi người dùng xác nhận). */
	private void khiDoiNhaCungCap() {
		if (dangNapNcc) {
			return;
		}
		MucChon moi = (MucChon) cboNhaCungCap.getSelectedItem();
		if (moi == null) {
			return;
		}
		if (!dsLo.isEmpty() && !Objects.equals(moi.ma, nccDangChon == null ? null : nccDangChon.ma)) {
			int c = JOptionPane.showConfirmDialog(this,
					"Đổi nhà cung cấp sẽ xóa toàn bộ các lô phụ tùng đã thêm. Tiếp tục?", "Xác nhận",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (c != JOptionPane.YES_OPTION) {
				dangNapNcc = true;
				cboNhaCungCap.setSelectedItem(nccDangChon);
				dangNapNcc = false;
				return;
			}
			dsLo.clear();
			hienThiLo();
		}
		nccDangChon = moi;
	}

	/** Sinh lại mã phiếu theo ngày đang nhập (nếu ngày chưa hợp lệ thì dùng ngày hôm nay). */
	private void capNhatMaPhieu() {
		LocalDate ngay = parseNgay(txtNgayNhap.getText());
		if (ngay == null) {
			ngay = LocalDate.now();
		}
		txtMaPhieu.setText("PNK" + ngay.format(FMT_MA) + String.format("%03d", demSoPhieuNhapTrongNgay(ngay) + 1));
	}

	// =====================================================================
	// THẺ 2: CHI TIẾT LÔ PHỤ TÙNG NHẬP
	// =====================================================================
	private CardPanel taoTheChiTiet() {
		cardChiTiet = new CardPanel("Chi tiết lô phụ tùng nhập", "");
		cardChiTiet.getBody().setBorder(new EmptyBorder(0, 0, 0, 0));

		model = new DefaultTableModel(new String[] { "Mã phụ tùng", "Tên phụ tùng", "Ngày sản xuất", "ĐVT",
				"Số lượng nhập", "Đơn giá nhập (đ)", "Thành tiền (đ)", "Thao tác" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tblLo = new BangCoThongBao(model);
		tblLo.setThongBaoTrong("Chưa có lô phụ tùng nào. Bấm \"Thêm lô phụ tùng\" để bắt đầu.");
		tuyChinhBang(tblLo, new int[] { 110, 260, 120, 80, 120, 140, 140, 110 },
				new TableCellRenderer[] { new CellRenderer(true), new CellRenderer(false), new CellRenderer(false),
						new CellRenderer(false), new CellRenderer(false), new CellRenderer(false),
						new CellRenderer(false),
						new ThaoTacRenderer(new SlotIcon(ICON_SUA, 16, 16, DASH), ThaoTacRenderer.NEN_VIEN,
								new SlotIcon(ICON_XOA, 16, 16, DASH), ThaoTacRenderer.NEN_DO_NHAT) });

		tblLo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = tblLo.rowAtPoint(e.getPoint());
				if (row < 0 || row >= dsLo.size()) {
					return;
				}
				int v = vungIcon(tblLo, e, COL_THAO_TAC);
				if (v == 1) {
					suaLo(dsLo.get(row));
				} else if (v == 2) {
					xoaLo(dsLo.get(row));
				}
			}
		});
		tblLo.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				tblLo.setCursor(Cursor.getPredefinedCursor(
						vungIcon(tblLo, e, COL_THAO_TAC) != 0 ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
			}
		});

		spLo = new JScrollPane(tblLo);
		spLo.setBorder(null);
		spLo.getViewport().setBackground(Color.WHITE);
		cardChiTiet.getBody().add(spLo, BorderLayout.CENTER);

		ActionButton btnThemLo = new ActionButton("Thêm lô phụ tùng", ActionButton.KIEU_VIEN_XANH, IMG_THEM);
		btnThemLo.addActionListener(e -> themLo());
		JPanel chan = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		chan.setOpaque(false);
		chan.setBorder(new EmptyBorder(16, 16, 16, 16));
		chan.add(btnThemLo);
		cardChiTiet.getBody().add(chan, BorderLayout.SOUTH);
		return cardChiTiet;
	}

	/** Đổ danh sách lô vào bảng và cập nhật tổng tiền. */
	private void hienThiLo() {
		model.setRowCount(0);
		double tong = 0;
		for (LoNhapRow lo : dsLo) {
			PhuTungChon p = lo.phuTung;
			model.addRow(new Object[] { p.ma, p.ten, p.ngaySanXuat == null ? "" : p.ngaySanXuat.format(FMT_NGAY),
					p.donViTinh == null ? "" : p.donViTinh, lo.soLuong, FMT_TIEN.format(lo.donGiaNhap),
					FMT_TIEN.format(lo.thanhTien()), "" });
			tong += lo.thanhTien();
		}
		lblTongTien.setText(FMT_TIEN.format(tong) + " đ");
		cardChiTiet.setNote(dsLo.size() + " lô");

		int n = Math.max(SO_DONG_TOI_THIEU, dsLo.size());
		spLo.setPreferredSize(new Dimension(100, CAO_HEADER + n * CAO_DONG));
		cardChiTiet.revalidate();
		cardChiTiet.repaint();
	}

	private double tongTien() {
		double t = 0;
		for (LoNhapRow lo : dsLo) {
			t += lo.thanhTien();
		}
		return t;
	}

	// =====================================================================
	// THẺ 3 + 4: GHI CHÚ & TỔNG HỢP
	// =====================================================================
	private JPanel taoHangGhiChuVaTongHop() {
		JPanel hang = new JPanel(new GridBagLayout());
		hang.setOpaque(false);

		// --- Ghi chú ---
		CardPanel cardGhiChu = new CardPanel("Ghi chú nhập kho", "");
		cardGhiChu.getBody().setBorder(new EmptyBorder(16, 20, 20, 20));
		txtGhiChu = new JTextArea(3, 20);
		txtGhiChu.setFont(FONT_BODY);
		txtGhiChu.setLineWrap(true);
		txtGhiChu.setWrapStyleWord(true);
		txtGhiChu.setBackground(new Color(250, 251, 253));
		txtGhiChu.setBorder(new EmptyBorder(8, 12, 8, 12));
		JScrollPane spGhiChu = new JScrollPane(txtGhiChu);
		spGhiChu.setBorder(new LineBorder(BORDER, 1, true));
		spGhiChu.setPreferredSize(new Dimension(100, 90));
		cardGhiChu.getBody().add(spGhiChu, BorderLayout.CENTER);

		// --- Tổng hợp ---
		CardPanel cardTong = new CardPanel("Tổng hợp phiếu nhập", "");
		cardTong.getBody().setBorder(new EmptyBorder(20, 20, 20, 20));
		JPanel dong = new JPanel(new BorderLayout());
		dong.setOpaque(false);
		JLabel lblNhan = new JLabel("Tổng tiền nhập kho");
		lblNhan.setFont(FONT_SECTION);
		lblNhan.setForeground(TEXT_DARK);
		lblTongTien = new JLabel("0 đ");
		lblTongTien.setFont(FONT_TITLE.deriveFont(26f));
		lblTongTien.setForeground(TITLE);
		dong.add(lblNhan, BorderLayout.WEST);
		dong.add(lblTongTien, BorderLayout.EAST);
		JLabel lblGhiChuTong = new JLabel("Tổng tiền = tổng (số lượng nhập × đơn giá nhập) của các lô.");
		lblGhiChuTong.setFont(FONT_SMALL);
		lblGhiChuTong.setForeground(TEXT_MUTED);
		lblGhiChuTong.setBorder(new EmptyBorder(14, 0, 0, 0));
		cardTong.getBody().add(dong, BorderLayout.NORTH);
		cardTong.getBody().add(lblGhiChuTong, BorderLayout.CENTER);

		GridBagConstraints g1 = new GridBagConstraints();
		g1.gridx = 0;
		g1.weightx = 1;
		g1.fill = GridBagConstraints.BOTH;
		g1.insets = new Insets(0, 0, 0, 16);
		hang.add(cardGhiChu, g1);
		GridBagConstraints g2 = new GridBagConstraints();
		g2.gridx = 1;
		g2.weightx = 1;
		g2.fill = GridBagConstraints.BOTH;
		hang.add(cardTong, g2);
		return hang;
	}

	private JPanel taoHangNut() {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
		p.setOpaque(false);

		ActionButton btnXacNhan = new ActionButton("Xác nhận nhập kho", ActionButton.KIEU_CHINH, ICON_XAC_NHAN);
		ActionButton btnLamMoi = new ActionButton("Làm mới", ActionButton.KIEU_VIEN_XANH, IMG_DAT_LAI);
		btnXacNhan.addActionListener(e -> xacNhanNhapKho());
		btnLamMoi.addActionListener(e -> {
			if (dsLo.isEmpty() || JOptionPane.showConfirmDialog(this, "Xóa toàn bộ dữ liệu đang nhập?", "Xác nhận",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
				lamMoi();
			}
		});
		p.add(btnXacNhan);
		p.add(btnLamMoi);
		return p;
	}

	// =====================================================================
	// THÊM / SỬA / XÓA LÔ
	// =====================================================================
	private void themLo() {
		if (nccDangChon == null || nccDangChon.ma == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp trước khi thêm lô phụ tùng!", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			cboNhaCungCap.requestFocusInWindow();
			return;
		}
		List<PhuTungChon> ds = layDanhSachPhuTungTheoNhaCungCap(nccDangChon.ma);
		List<PhuTungChon> conLai = new ArrayList<>();
		if (ds != null) {
			for (PhuTungChon p : ds) {
				if (!daCoTrongPhieu(p.ma)) {
					conLai.add(p);
				}
			}
		}
		if (conLai.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Không còn phụ tùng nào của nhà cung cấp này để thêm vào phiếu.",
					"Thông báo", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		String[] hienThi = conLai.stream().map(PhuTungChon::toString).toArray(String[]::new);
		String[] kq = hienFormDialog("Thêm lô phụ tùng",
				new String[] { "Phụ tùng *", "Số lượng nhập *", "Đơn giá nhập (đ) *" },
				new String[] { hienThi[0], "", "" }, new boolean[] { false, false, false },
				new String[][] { hienThi, null, null }, v -> kiemTraSoLuongDonGia(v[1], v[2]));
		if (kq == null) {
			return;
		}
		for (PhuTungChon p : conLai) {
			if (p.toString().equals(kq[0])) {
				dsLo.add(new LoNhapRow(p, Integer.parseInt(kq[1]), docTien(kq[2])));
				break;
			}
		}
		hienThiLo();
	}

	private void suaLo(LoNhapRow lo) {
		String[] kq = hienFormDialog("Sửa lô phụ tùng",
				new String[] { "Mã phụ tùng", "Tên phụ tùng", "Số lượng nhập *", "Đơn giá nhập (đ) *" },
				new String[] { lo.phuTung.ma, lo.phuTung.ten, String.valueOf(lo.soLuong),
						String.valueOf((long) lo.donGiaNhap) },
				new boolean[] { true, true, false, false }, null, v -> kiemTraSoLuongDonGia(v[2], v[3]));
		if (kq == null) {
			return;
		}
		lo.soLuong = Integer.parseInt(kq[2]);
		lo.donGiaNhap = docTien(kq[3]);
		hienThiLo();
	}

	private void xoaLo(LoNhapRow lo) {
		int c = JOptionPane.showConfirmDialog(this,
				"Xóa lô " + lo.phuTung.ma + " - " + lo.phuTung.ten + " khỏi phiếu nhập?", "Xác nhận xóa",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (c == JOptionPane.YES_OPTION) {
			dsLo.remove(lo);
			hienThiLo();
		}
	}

	private boolean daCoTrongPhieu(String maPhuTung) {
		for (LoNhapRow lo : dsLo) {
			if (lo.phuTung.ma.equals(maPhuTung)) {
				return true;
			}
		}
		return false;
	}

	private static String kiemTraSoLuongDonGia(String soLuong, String donGia) {
		if (!soLuong.matches("\\d{1,9}") || Integer.parseInt(soLuong) <= 0) {
			return "Số lượng nhập phải là số nguyên lớn hơn 0!";
		}
		if (!donGia.replaceAll("[.,\\s]", "").matches("\\d{1,12}")) {
			return "Đơn giá nhập phải là số không âm (VD: 135000 hoặc 135.000)!";
		}
		return null;
	}

	private static double docTien(String s) {
		return Double.parseDouble(s.replaceAll("[.,\\s]", ""));
	}

	// =====================================================================
	// XÁC NHẬN NHẬP KHO / LÀM MỚI
	// =====================================================================
	private void xacNhanNhapKho() {
		LocalDate ngay = parseNgay(txtNgayNhap.getText());
		if (ngay == null) {
			baoLoi("Ngày nhập không hợp lệ (định dạng dd/MM/yyyy)!", txtNgayNhap);
			return;
		}
		if (ngay.isAfter(LocalDate.now())) {
			baoLoi("Ngày nhập không được sau ngày hiện tại!", txtNgayNhap);
			return;
		}
		if (nccDangChon == null || nccDangChon.ma == null) {
			baoLoi("Vui lòng chọn nhà cung cấp!", cboNhaCungCap);
			return;
		}
		if (maNVLap.isEmpty()) {
			baoLoi("Chưa xác định người lập phiếu (gọi setNguoiLap sau khi tạo màn hình)!", txtNguoiLap);
			return;
		}
		if (dsLo.isEmpty()) {
			baoLoi("Vui lòng thêm ít nhất một lô phụ tùng!", cboNhaCungCap);
			return;
		}
		String ghiChu = txtGhiChu.getText().trim();
		if (ghiChu.length() > 500) {
			baoLoi("Ghi chú tối đa 500 ký tự!", txtGhiChu);
			return;
		}

		capNhatMaPhieu(); // đảm bảo mã khớp với ngày nhập
		String ma = txtMaPhieu.getText();
		double tong = tongTien();
		int c = JOptionPane.showConfirmDialog(this,
				"Xác nhận nhập kho phiếu " + ma + "?\nSố lô: " + dsLo.size() + "\nTổng tiền: " + FMT_TIEN.format(tong)
						+ " đ",
				"Xác nhận nhập kho", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (c != JOptionPane.YES_OPTION) {
			return;
		}

		if (luuPhieuNhapKho(ma, ngay, maNVLap, nccDangChon.ma, ghiChu, tong, new ArrayList<>(dsLo))) {
			JOptionPane.showMessageDialog(this, "Đã lập phiếu nhập kho " + ma + ".", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
			lamMoi();
		} else {
			JOptionPane.showMessageDialog(this, "Lập phiếu nhập kho thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	/** Đưa form về trạng thái ban đầu: xóa lô, ghi chú, bỏ chọn nhà cung cấp, ngày nhập = hôm nay. */
	private void lamMoi() {
		dsLo.clear();
		txtGhiChu.setText("");
		txtNgayNhap.setText(LocalDate.now().format(FMT_NGAY));
		dangNapNcc = true;
		if (cboNhaCungCap.getItemCount() > 0) {
			cboNhaCungCap.setSelectedIndex(0);
		}
		dangNapNcc = false;
		nccDangChon = (MucChon) cboNhaCungCap.getSelectedItem();
		capNhatMaPhieu();
		hienThiLo();
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
	 *                (có thể truyền null nếu không ô nào là danh sách chọn)
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

	/** Vị trí click trong ô "Thao tác": 0 = ngoài, 1 = nút thứ nhất (Sửa), 2 = nút thứ hai (Xóa). */
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

	/** Ô "Thao tác": hai nút vuông bo góc có icon (VÙNG CHÈN ẢNH). */
	private static class ThaoTacRenderer extends JPanel implements TableCellRenderer {
		private static final long serialVersionUID = 1L;
		static final int NEN_XANH_NHAT = 0;
		static final int NEN_VIEN = 1; // nền trắng, viền xám
		static final int NEN_DO_NHAT = 2; // nút Xóa

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
				g2.setColor(kieu == NEN_DO_NHAT ? ERR_BG : PRIMARY_LIGHT);
				g2.fillRoundRect(x, y, NUT, NUT, 10, 10);
			}
		}
	}
}
