package gui;

import static gui.UITheme.*;

import java.awt.BasicStroke;
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
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
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
 * Gồm các thẻ (giống ảnh mẫu): Thông tin phiếu nhập, Chi tiết lô phụ tùng nhập, Ghi chú nhập kho,
 * Tổng hợp phiếu nhập, cùng hai nút "Xác nhận nhập kho" / "Làm mới".
 *
 * Mỗi "lô phụ tùng" gồm: Mã lô (tự đánh số), Phụ tùng, Ngày sản xuất, Hạn sử dụng, Số lượng nhập, Đơn giá nhập.
 *
 * KHÔNG có dữ liệu mẫu và chưa gắn với CSDL cụ thể. Các hàm "TODO":
 *  layDanhSachNhaCungCap, layDanhSachPhuTung, demSoPhieuNhapTrongNgay, demSoLoDaCo, luuPhieuNhapKho.
 */
public class NhapKhoGUI extends JPanel {
	private static final long serialVersionUID = 1L;

	// =====================================================================
	// ICON CỦA MÀN HÌNH NÀY - TỰ NHẬP ĐƯỜNG DẪN VÀO GIỮA CẶP DẤU "".
	// Ví dụ: "/images/icon_sua.png" hoặc chỉ "icon_sua.png" (tự hiểu là nằm trong /images/).
	// Để rỗng thì hiện khung viền nét đứt. (Các icon dùng chung như IMG_THEM, IMG_LUU... nằm trong UITheme.)
	// =====================================================================
	private static final String ICON_SUA = ""; // TODO: icon "Sửa" (cây bút)
	private static final String ICON_XOA = ""; // TODO: icon "Xóa" (thùng rác, màu đỏ)
	private static final String ICON_XAC_NHAN = "/images/icon_check.png"; // TODO: icon dấu tích ở nút "Xác nhận nhập kho"
	private static final String ICON_THEM_XANH = "/images/icon_plus_xanh.png";

	private static final int CAO_DONG = 58;
	private static final int CAO_HEADER = 40;
	private static final int SO_DONG_TOI_THIEU = 3;
	private static final int PAD_TRAI = 16;
	private static final int NUT = 32;
	private static final int KHOANG = 8;
	private static final int COL_THAO_TAC = 8;
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

	/** Một phụ tùng có thể chọn khi thêm lô. TODO: có thể thay bằng entity.PhuTung (sau khi có CSDL mới). */
	private static class PhuTungChon {
		final String ma, ten, donViTinh;

		PhuTungChon(String ma, String ten, String donViTinh) {
			this.ma = ma;
			this.ten = ten;
			this.donViTinh = donViTinh;
		}

		@Override
		public String toString() {
			return ma + " - " + ten;
		}
	}

	/** Một lô phụ tùng nhập. */
	private static class LoNhapRow {
		String maLo;
		final PhuTungChon phuTung;
		LocalDate ngaySanXuat, hanSuDung;
		int soLuong;
		double donGiaNhap;

		LoNhapRow(String maLo, PhuTungChon phuTung, LocalDate ngaySanXuat, LocalDate hanSuDung, int soLuong,
				double donGiaNhap) {
			this.maLo = maLo;
			this.phuTung = phuTung;
			this.ngaySanXuat = ngaySanXuat;
			this.hanSuDung = hanSuDung;
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
	private int soLoDaCo = 0;
	private boolean dangNapNcc = false;

	// ---- Thẻ thông tin phiếu ----
	private Input txtMaPhieu, txtNguoiLap;
	private NgayChonField dtNgayNhap;
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
	 * @param maNV  mã nhân viên (lưu cùng phiếu nhập)
	 * @param hoTen họ tên hiển thị ở ô "Người lập"
	 */
	public void setNguoiLap(String maNV, String hoTen) {
		this.maNVLap = maNV == null ? "" : maNV;
		txtNguoiLap.setText(hoTen == null ? "" : hoTen);
	}

	/** Gọi mỗi khi chuyển sang màn hình này: tải lại nhà cung cấp, cập nhật mã phiếu / mã lô (giữ nguyên các lô đã nhập). */
	public void refresh() {
		napNhaCungCap();
		soLoDaCo = demSoLoDaCo();
		danhSoLaiLo();
		capNhatMaPhieu();
		hienThiLo();
	}

	// =====================================================================
	// TODO: KẾT NỐI CƠ SỞ DỮ LIỆU (DAO) - BẠN CHỈ CẦN SỬA CÁC HÀM TRONG KHỐI NÀY
	// =====================================================================

	/** Lấy danh sách nhà cung cấp. TODO: gọi DAO rồi ds.add(new MucChon(maNCC, tenNCC)). */
	private List<MucChon> layDanhSachNhaCungCap() {
		List<MucChon> ds = new ArrayList<>();
		// TODO: ds.add(new MucChon(...));
		return ds;
	}

	/** Lấy danh sách phụ tùng để chọn khi thêm lô. TODO: gọi DAO rồi ds.add(new PhuTungChon(ma, ten, donViTinh)). */
	private List<PhuTungChon> layDanhSachPhuTung() {
		List<PhuTungChon> ds = new ArrayList<>();
		// TODO: ds.add(new PhuTungChon(...));
		return ds;
	}

	/**
	 * Đếm số phiếu nhập đã có trong ngày để sinh mã dạng PNK + ddMMyyyy + số thứ tự (VD: PNK17102026001).
	 * TODO: đếm phiếu nhập theo ngày (hoặc lấy số thứ tự lớn nhất trong mã để tránh trùng).
	 */
	private int demSoPhieuNhapTrongNgay(LocalDate ngay) {
		// TODO: return phieuNhapKhoDAO.demTheoNgay(ngay);
		return 0;
	}

	/**
	 * Số lô đã có trong kho, dùng để đánh mã lô mới (LO001, LO002...) nối tiếp.
	 * TODO: lấy số thứ tự mã lô lớn nhất hiện có.
	 */
	private int demSoLoDaCo() {
		// TODO: return loPhuTungDAO.laySoThuTuLonNhat();
		return 0;
	}

	/**
	 * Lưu phiếu nhập kho. NÊN chạy trong MỘT transaction: thêm phiếu, thêm từng lô,
	 * rồi cộng tồn kho cho các phụ tùng tương ứng.
	 * @return true nếu lưu thành công
	 */
	private boolean luuPhieuNhapKho(String maPhieu, LocalDate ngayNhap, String maNVLap, String maNCC, String ghiChu,
			double tongTien, List<LoNhapRow> cacLo) {
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
		dtNgayNhap = new NgayChonField();
		dtNgayNhap.setKhiDoiNgay(this::capNhatMaPhieu); // đổi ngày thì mã phiếu đổi theo
		txtNguoiLap = new Input(null, true);
		cboNhaCungCap = new JComboBox<>();
		cboNhaCungCap.setFont(FONT_BODY);
		cboNhaCungCap.setBackground(Color.WHITE);

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
		themO(luoi, labeled("Ngày nhập *", dtNgayNhap), 1, 2, 2, 16, 12);
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
		MucChon dangChon = (MucChon) cboNhaCungCap.getSelectedItem();
		String maCu = (dangChon == null) ? null : dangChon.ma;
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
	}

	/** Sinh lại mã phiếu theo ngày nhập đang chọn. */
	private void capNhatMaPhieu() {
		LocalDate ngay = dtNgayNhap.getNgay();
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

		model = new DefaultTableModel(new String[] { "Mã lô", "Tên phụ tùng", "Ngày sản xuất", "Hạn sử dụng", "ĐVT",
				"Số lượng nhập", "Đơn giá nhập (đ)", "Thành tiền (đ)", "Thao tác" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tblLo = new BangCoThongBao(model);
		tblLo.setThongBaoTrong("Chưa có lô phụ tùng nào. Bấm \"Thêm lô phụ tùng\" để bắt đầu.");
		tuyChinhBang(tblLo, new int[] { 100, 230, 115, 115, 70, 115, 130, 130, 110 },
				new TableCellRenderer[] { new CellRenderer(true), new CellRenderer(false), new CellRenderer(false),
						new CellRenderer(false), new CellRenderer(false), new CellRenderer(false),
						new CellRenderer(false), new CellRenderer(false),
						new ThaoTacRenderer(new SlotIcon(ICON_SUA, 16, 16, DASH), ThaoTacRenderer.NEN_VIEN,
								new SlotIcon(ICON_XOA, 16, 16, DASH), ThaoTacRenderer.NEN_XANH_NHAT) });

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

		ActionButton btnThemLo = new ActionButton("Thêm lô phụ tùng", ActionButton.KIEU_VIEN_XANH, ICON_THEM_XANH);
		btnThemLo.addActionListener(e -> themLo());
		JPanel chan = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		chan.setOpaque(false);
		chan.setBorder(new EmptyBorder(16, 16, 16, 16));
		chan.add(btnThemLo);
		cardChiTiet.getBody().add(chan, BorderLayout.SOUTH);
		return cardChiTiet;
	}

	/** Đánh lại mã lô tuần tự (LO001, LO002...) nối tiếp số lô đã có trong kho. */
	private void danhSoLaiLo() {
		for (int i = 0; i < dsLo.size(); i++) {
			dsLo.get(i).maLo = maLoTheoViTri(i);
		}
	}

	private String maLoTheoViTri(int viTri) {
		return "LO" + String.format("%03d", soLoDaCo + viTri + 1);
	}

	/** Đổ danh sách lô vào bảng và cập nhật tổng tiền. */
	private void hienThiLo() {
		model.setRowCount(0);
		double tong = 0;
		for (LoNhapRow lo : dsLo) {
			PhuTungChon p = lo.phuTung;
			model.addRow(new Object[] { lo.maLo, p.ten, lo.ngaySanXuat.format(FMT_NGAY), lo.hanSuDung.format(FMT_NGAY),
					p.donViTinh == null ? "" : p.donViTinh, lo.soLuong, FMT_TIEN.format(lo.donGiaNhap),
					FMT_TIEN.format(lo.thanhTien()), "" });
			tong += lo.thanhTien();
		}
		lblTongTien.setText(FMT_TIEN.format(tong) + " đ");

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
		JLabel lblGhiChuTong = new JLabel("Giá vốn nhập kho không bao gồm thuế GTGT.");
		lblGhiChuTong.setFont(FONT_SMALL);
		lblGhiChuTong.setForeground(TEXT_MUTED);
		lblGhiChuTong.setBorder(new EmptyBorder(16, 0, 0, 0));
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
	private static final String[] NHAN_LO = { "Mã lô", "Phụ tùng *", "Ngày sản xuất * (dd/MM/yyyy)",
			"Hạn sử dụng * (dd/MM/yyyy)", "Số lượng nhập *", "Đơn giá nhập (đ) *" };

	private void themLo() {
		List<PhuTungChon> ds = layDanhSachPhuTung();
		if (ds == null || ds.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Chưa có phụ tùng nào để chọn. Vui lòng kiểm tra danh mục phụ tùng!",
					"Thông báo", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		String[] hienThi = ds.stream().map(PhuTungChon::toString).toArray(String[]::new);

		String[] kq = hienFormDialog("Thêm lô phụ tùng", NHAN_LO,
				new String[] { maLoTheoViTri(dsLo.size()), hienThi[0], "", "", "", "" },
				new boolean[] { true, false, false, false, false, false },
				new String[][] { null, hienThi, null, null, null, null }, NhapKhoGUI::kiemTraLo);
		if (kq == null) {
			return;
		}
		for (PhuTungChon p : ds) {
			if (p.toString().equals(kq[1])) {
				dsLo.add(new LoNhapRow(kq[0], p, parseNgay(kq[2]), parseNgay(kq[3]), Integer.parseInt(kq[4]),
						docTien(kq[5])));
				break;
			}
		}
		hienThiLo();
	}

	private void suaLo(LoNhapRow lo) {
		String[] kq = hienFormDialog("Sửa lô phụ tùng", NHAN_LO,
				new String[] { lo.maLo, lo.phuTung.toString(), lo.ngaySanXuat.format(FMT_NGAY),
						lo.hanSuDung.format(FMT_NGAY), String.valueOf(lo.soLuong),
						String.valueOf((long) lo.donGiaNhap) },
				new boolean[] { true, true, false, false, false, false }, null, NhapKhoGUI::kiemTraLo);
		if (kq == null) {
			return;
		}
		lo.ngaySanXuat = parseNgay(kq[2]);
		lo.hanSuDung = parseNgay(kq[3]);
		lo.soLuong = Integer.parseInt(kq[4]);
		lo.donGiaNhap = docTien(kq[5]);
		hienThiLo();
	}

	private void xoaLo(LoNhapRow lo) {
		int c = JOptionPane.showConfirmDialog(this,
				"Xóa lô " + lo.maLo + " - " + lo.phuTung.ten + " khỏi phiếu nhập?", "Xác nhận xóa",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (c == JOptionPane.YES_OPTION) {
			dsLo.remove(lo);
			danhSoLaiLo();
			hienThiLo();
		}
	}

	/** Kiểm tra dữ liệu của một lô. v = {mã lô, phụ tùng, ngày SX, hạn SD, số lượng, đơn giá}. */
	private static String kiemTraLo(String[] v) {
		LocalDate nsx = parseNgay(v[2]);
		if (nsx == null) {
			return "Ngày sản xuất không hợp lệ (định dạng dd/MM/yyyy)!";
		}
		LocalDate hsd = parseNgay(v[3]);
		if (hsd == null) {
			return "Hạn sử dụng không hợp lệ (định dạng dd/MM/yyyy)!";
		}
		if (nsx.isAfter(LocalDate.now())) {
			return "Ngày sản xuất không được sau ngày hiện tại!";
		}
		if (!hsd.isAfter(nsx)) {
			return "Hạn sử dụng phải sau ngày sản xuất!";
		}
		if (!v[4].matches("\\d{1,9}") || Integer.parseInt(v[4]) <= 0) {
			return "Số lượng nhập phải là số nguyên lớn hơn 0!";
		}
		if (!v[5].replaceAll("[.,\\s]", "").matches("\\d{1,12}")) {
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
		LocalDate ngay = dtNgayNhap.getNgay();
		if (ngay == null) {
			baoLoi("Vui lòng chọn ngày nhập!", dtNgayNhap);
			return;
		}
		if (ngay.isAfter(LocalDate.now())) {
			baoLoi("Ngày nhập không được sau ngày hiện tại!", dtNgayNhap);
			return;
		}
		MucChon ncc = (MucChon) cboNhaCungCap.getSelectedItem();
		if (ncc == null || ncc.ma == null) {
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

		if (luuPhieuNhapKho(ma, ngay, maNVLap, ncc.ma, txtGhiChu.getText().trim(), tong, new ArrayList<>(dsLo))) {
			JOptionPane.showMessageDialog(this, "Đã lập phiếu nhập kho " + ma + ".", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
			lamMoi();
			refresh(); // nạp lại số lô / số thứ tự phiếu sau khi lưu
		} else {
			JOptionPane.showMessageDialog(this, "Lập phiếu nhập kho thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	/** Đưa form về trạng thái ban đầu: xóa lô, ghi chú, bỏ chọn nhà cung cấp, ngày nhập = hôm nay. */
	private void lamMoi() {
		dsLo.clear();
		txtGhiChu.setText("");
		dtNgayNhap.setNgay(LocalDate.now());
		dangNapNcc = true;
		if (cboNhaCungCap.getItemCount() > 0) {
			cboNhaCungCap.setSelectedIndex(0);
		}
		dangNapNcc = false;
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
	// Ô CHỌN NGÀY DẠNG DROPDOWN (giống ảnh mẫu: ô có mũi tên, bấm vào hiện lịch)
	// =====================================================================
	private static class NgayChonField extends Input {
		private static final long serialVersionUID = 1L;
		private LocalDate ngay;
		private Runnable khiDoiNgay;

		NgayChonField() {
			super(null, false);
			setEditable(false);
			setFocusable(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			setBorder(new EmptyBorder(0, 12, 0, 36));
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					hienLich();
				}
			});
		}

		void setKhiDoiNgay(Runnable r) {
			this.khiDoiNgay = r;
		}

		LocalDate getNgay() {
			return ngay;
		}

		void setNgay(LocalDate d) {
			this.ngay = d;
			setText(d == null ? "" : d.format(FMT_NGAY));
		}

		private void hienLich() {
			JPopupMenu popup = new JPopupMenu();
			popup.setBorder(new LineBorder(BORDER));
			popup.add(new LichPanel(ngay == null ? LocalDate.now() : ngay, LocalDate.now(), d -> {
				setNgay(d);
				popup.setVisible(false);
				if (khiDoiNgay != null) {
					khiDoiNgay.run();
				}
			}));
			popup.show(this, 0, getHeight() + 2);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(TEXT_DARK);
			g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			int cx = getWidth() - 22;
			int cy = getHeight() / 2;
			g2.drawLine(cx - 4, cy - 2, cx, cy + 2);
			g2.drawLine(cx, cy + 2, cx + 4, cy - 2);
			g2.dispose();
		}
	}

	/** Bảng lịch tháng: chọn ngày, chuyển tháng trước / sau. Không cho chọn ngày sau {@code toiDa}. */
	private static class LichPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private YearMonth thang;
		private final LocalDate dangChon, toiDa;
		private final Consumer<LocalDate> khiChon;
		private final JLabel lblThang = new JLabel("", SwingConstants.CENTER);
		private final JPanel luoi = new JPanel(new GridLayout(0, 7, 2, 2));

		LichPanel(LocalDate dangChon, LocalDate toiDa, Consumer<LocalDate> khiChon) {
			super(new BorderLayout(0, 8));
			this.dangChon = dangChon;
			this.toiDa = toiDa;
			this.khiChon = khiChon;
			this.thang = YearMonth.from(dangChon);
			setBackground(Color.WHITE);
			setBorder(new EmptyBorder(10, 10, 10, 10));
			setPreferredSize(new Dimension(290, 270));

			JPanel top = new JPanel(new BorderLayout());
			top.setOpaque(false);
			lblThang.setFont(FONT_BOLD);
			lblThang.setForeground(TITLE);
			top.add(nutThang("‹", -1), BorderLayout.WEST);
			top.add(lblThang, BorderLayout.CENTER);
			top.add(nutThang("›", 1), BorderLayout.EAST);

			luoi.setOpaque(false);
			add(top, BorderLayout.NORTH);
			add(luoi, BorderLayout.CENTER);
			capNhat();
		}

		private JLabel nutThang(String text, int delta) {
			JLabel l = new JLabel(text, SwingConstants.CENTER);
			l.setFont(FONT_SECTION);
			l.setForeground(PRIMARY);
			l.setPreferredSize(new Dimension(34, 28));
			l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			l.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					thang = thang.plusMonths(delta);
					capNhat();
				}
			});
			return l;
		}

		private void capNhat() {
			lblThang.setText("Tháng " + thang.getMonthValue() + " / " + thang.getYear());
			luoi.removeAll();
			for (String t : new String[] { "T2", "T3", "T4", "T5", "T6", "T7", "CN" }) {
				JLabel l = new JLabel(t, SwingConstants.CENTER);
				l.setFont(FONT_SMALL_BOLD);
				l.setForeground(TEXT_MUTED);
				luoi.add(l);
			}
			int lech = thang.atDay(1).getDayOfWeek().getValue() - 1; // thứ Hai = 1
			for (int i = 0; i < lech; i++) {
				luoi.add(new JLabel(""));
			}
			for (int d = 1; d <= thang.lengthOfMonth(); d++) {
				LocalDate ngay = thang.atDay(d);
				boolean tatDi = toiDa != null && ngay.isAfter(toiDa);
				NgayCell cell = new NgayCell(d, ngay.equals(dangChon), tatDi);
				if (!tatDi) {
					cell.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							khiChon.accept(ngay);
						}
					});
				}
				luoi.add(cell);
			}
			luoi.revalidate();
			luoi.repaint();
		}
	}

	/** Một ô ngày trong lịch. */
	private static class NgayCell extends JLabel {
		private static final long serialVersionUID = 1L;
		private final boolean chon;
		private boolean hover = false;

		NgayCell(int ngay, boolean chon, boolean tatDi) {
			super(String.valueOf(ngay), SwingConstants.CENTER);
			this.chon = chon;
			setFont(FONT_BODY);
			setForeground(chon ? Color.WHITE : (tatDi ? new Color(190, 195, 205) : TEXT_DARK));
			setOpaque(false);
			setPreferredSize(new Dimension(34, 30));
			if (!tatDi) {
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				addMouseListener(new MouseAdapter() {
					@Override
					public void mouseEntered(MouseEvent e) {
						hover = true;
						repaint();
					}

					@Override
					public void mouseExited(MouseEvent e) {
						hover = false;
						repaint();
					}
				});
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			if (chon) {
				g2.setColor(PRIMARY);
				g2.fillRoundRect(2, 1, getWidth() - 4, getHeight() - 2, 10, 10);
			} else if (hover) {
				g2.setColor(PRIMARY_LIGHT);
				g2.fillRoundRect(2, 1, getWidth() - 4, getHeight() - 2, 10, 10);
			}
			g2.dispose();
			super.paintComponent(g);
		}
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
		static final int NEN_XANH_NHAT = 0; // nút Xóa (nền xanh nhạt, icon đỏ)
		static final int NEN_VIEN = 1; // nút Sửa: nền trắng, viền xám

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
