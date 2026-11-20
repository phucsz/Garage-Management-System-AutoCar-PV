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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
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

import business.PhuTungBUS;
import entity.DanhMucPhuTung;
import entity.PhuTung;

/**
 * Màn hình QUẢN LÝ PHỤ TÙNG (JPanel) - được nhúng vào CardLayout của TrangChuQuanLyGUI.
 *
 * Từ màn hình này có thể chuyển sang:
 *  - Nhập kho                 (NhapKhoGUI)            - nút "Nhập kho" ở góc trên phải
 *  - Tra cứu phiếu nhập kho   (TraCuuPhieuNhapKhoGUI) - nút "Tra cứu phiếu nhập kho"
 *
 * Gồm 2 thẻ (giống ảnh mẫu):
 *  1. Tra cứu phụ tùng: Mã / Tên phụ tùng, Loại phụ tùng, Tình trạng tồn, nút Tìm kiếm / Làm mới
 *  2. Danh sách phụ tùng: Mã, Tên phụ tùng, Loại phụ tùng, ĐVT, Tồn kho, Tồn tối thiểu, Số lô,
 *     Giá bán hiện tại (đ), Trạng thái (tự tính từ tồn kho và tồn tối thiểu), Thao tác (Xem, Sửa)
 *
 * KHÔNG có dữ liệu mẫu và chưa gắn với CSDL cụ thể. Mọi chỗ cần lấy / ghi dữ liệu là các hàm "TODO":
 *  layDanhSachPhuTung, layDanhSachDanhMuc, capNhatPhuTung.
 */
public class QuanLyPhuTungGUI extends JPanel {
	private static final long serialVersionUID = 1L;

	// =====================================================================
	// ICON CỦA MÀN HÌNH NÀY - TỰ NHẬP ĐƯỜNG DẪN VÀO GIỮA CẶP DẤU "".
	// Ví dụ: "/images/icon_sua.png" hoặc chỉ "icon_sua.png" (tự hiểu là nằm trong /images/).
	// Để rỗng thì hiện khung viền nét đứt. (Các icon dùng chung như IMG_XEM, IMG_TIM_KIEM... nằm trong UITheme.)
	// =====================================================================
	private static final String ICON_SUA = ""; // TODO: nhập đường dẫn icon "Sửa" (cây bút)

	// ====== Trạng thái tồn kho (tự tính) ======
	private static final String TT_CON_HANG = "Còn hàng";
	private static final String TT_SAP_HET = "Sắp hết";
	private static final String TT_HET_HANG = "Hết hàng";

	private static final String TAT_CA_NHOM = "Tất cả nhóm";
	private static final String TAT_CA_TINH_TRANG = "Tất cả";

	private static final int SO_DONG_TRANG = 8;
	private static final int CAO_DONG = 58;
	private static final int CAO_HEADER = 40;
	private static final int PAD_TRAI = 16;
	private static final int NUT = 32;
	private static final int KHOANG = 8;
	private static final int COL_THAO_TAC = 9;
	private static final Color DASH = new Color(150, 155, 165);

	private static final NumberFormat FMT_SO = NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN"));
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

	/** Một dòng phụ tùng. TODO: có thể thay bằng entity.PhuTung của project (sau khi có CSDL mới). */
	private static class PhuTungRow {
		final String ma, ten, maDanhMuc, tenDanhMuc, donViTinh;
		final int soLuongTon, tonToiThieu, soLo;
		final double donGia;

		PhuTungRow(String ma, String ten, String maDanhMuc, String tenDanhMuc, String donViTinh, int soLuongTon,
				int tonToiThieu, int soLo, double donGia) {
			this.ma = ma;
			this.ten = ten;
			this.maDanhMuc = maDanhMuc;
			this.tenDanhMuc = tenDanhMuc;
			this.donViTinh = donViTinh;
			this.soLuongTon = soLuongTon;
			this.tonToiThieu = tonToiThieu;
			this.soLo = soLo;
			this.donGia = donGia;
		}
	}

	private final Runnable quayVeTrangChu, moNhapKho, moTraCuuPhieuNhap;
	private final PhuTungBUS phuTungBUS = new PhuTungBUS();
	private final List<PhuTungRow> dsPhuTung = new ArrayList<>(); // kết quả sau khi lọc (chia trang ở UI)
	private final List<MucChon> dsDanhMuc = new ArrayList<>();
	private int trang = 1;
	private boolean dangNapCombo = false;

	// ---- Thẻ tra cứu ----
	private Input txtTuKhoa;
	private JComboBox<MucChon> cboLoai;
	private JComboBox<String> cboTinhTrang;

	// ---- Thẻ danh sách ----
	private CardPanel cardDanhSach;
	private BangCoThongBao tblPhuTung;
	private DefaultTableModel model;
	private JScrollPane spBang;
	private JLabel lblHienThi, lblSoTrang;
	private JButton btnTruoc, btnSau;

	/**
	 * @param quayVeTrangChu    nút "Trang chủ quản lý"
	 * @param moNhapKho         nút "Nhập kho" - chuyển sang NhapKhoGUI
	 * @param moTraCuuPhieuNhap nút "Tra cứu phiếu nhập kho" - chuyển sang TraCuuPhieuNhapKhoGUI
	 */
	public QuanLyPhuTungGUI(Runnable quayVeTrangChu, Runnable moNhapKho, Runnable moTraCuuPhieuNhap) {
		this.quayVeTrangChu = quayVeTrangChu;
		this.moNhapKho = moNhapKho;
		this.moTraCuuPhieuNhap = moTraCuuPhieuNhap;
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

	/** Gọi mỗi khi chuyển sang màn hình này: xóa bộ lọc, tải lại nhóm phụ tùng và danh sách phụ tùng. */
	public void refresh() {
		txtTuKhoa.setText("");
		cboTinhTrang.setSelectedIndex(0);
		napComboDanhMuc();
		taiLaiDanhSach();
	}

	// =====================================================================
	// TODO: KẾT NỐI CƠ SỞ DỮ LIỆU (DAO) - BẠN CHỈ CẦN SỬA CÁC HÀM TRONG KHỐI NÀY
	// =====================================================================

	/**
	 * Lấy danh sách phụ tùng theo bộ lọc.
	 * @param tuKhoa    mã hoặc tên cần tìm ("" = không lọc), nên tìm gần đúng: LIKE %...%
	 * @param maDanhMuc mã nhóm phụ tùng cần lọc (null = tất cả nhóm)
	 * TODO: gọi DAO rồi đổi mỗi entity thành new PhuTungRow(ma, ten, maDanhMuc, tenDanhMuc, donViTinh,
	 * soLuongTon, tonToiThieu, soLo, donGia).
	 * Việc lọc theo "Tình trạng tồn" được xử lý ở giao diện (trạng thái tự tính từ tồn kho và tồn tối thiểu).
	 */
	private List<PhuTungRow> layDanhSachPhuTung(String tuKhoa, String maDanhMuc) {
		List<PhuTungRow> ds = new ArrayList<>();
		try {
			for (PhuTung p : phuTungBUS.timKiem(tuKhoa, maDanhMuc)) {
				String maNhom = p.getDanhMucPhuTung() == null ? null : p.getDanhMucPhuTung().getMaDanhMuc();
				String tenNhom = p.getDanhMucPhuTung() == null ? "" : p.getDanhMucPhuTung().getTenDanhMuc();
				int soLo = p.getLoPhuTung() == null ? 0 : p.getLoPhuTung().size();
				ds.add(new PhuTungRow(p.getMaPhuTung(), p.getTenPhuTung(), maNhom, tenNhom,
						p.getDonViTinh(), p.tinhSoLuongTon(), 0, soLo, p.getDonGiaHienTai()));
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Không thể tải danh sách phụ tùng: " + e.getMessage(),
					"Lỗi", JOptionPane.ERROR_MESSAGE);
		}
		return ds;
	}

	/**
	 * Lấy các nhóm phụ tùng để đổ vào ô lọc "Loại phụ tùng".
	 * TODO: gọi DAO rồi ds.add(new MucChon(maNhom, tenNhom)).
	 */
	private List<MucChon> layDanhSachDanhMuc() {
		List<MucChon> ds = new ArrayList<>();
		try {
			for (DanhMucPhuTung dm : phuTungBUS.layDanhSachDanhMuc()) {
				ds.add(new MucChon(dm.getMaDanhMuc(), dm.getTenDanhMuc()));
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Không thể tải danh sách loại phụ tùng: " + e.getMessage(),
					"Lỗi", JOptionPane.ERROR_MESSAGE);
		}
		return ds;
	}

	/**
	 * Cập nhật thông tin phụ tùng (tên, nhóm, đơn vị tính, giá bán, tồn tối thiểu).
	 * Tồn kho và số lô KHÔNG sửa tay ở đây (thay đổi qua nhập kho / xuất kho).
	 * @return true nếu cập nhật thành công
	 */
	private boolean capNhatPhuTung(PhuTungRow pt) {
		DanhMucPhuTung danhMuc = pt.maDanhMuc == null ? null
				: new DanhMucPhuTung(pt.maDanhMuc, pt.tenDanhMuc);
		PhuTung entity = new PhuTung(pt.ma, pt.ten, pt.donViTinh, pt.donGia, 0, danhMuc, null);
		try {
			return phuTungBUS.capNhat(entity);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Không thể cập nhật phụ tùng: " + e.getMessage(),
					"Lỗi", JOptionPane.ERROR_MESSAGE);
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
		JLabel lblTitle = new JLabel("Quản lý phụ tùng");
		lblTitle.setFont(FONT_TITLE);
		lblTitle.setForeground(TITLE);
		JLabel lblSub = new JLabel("Theo dõi tồn kho phụ tùng, cảnh báo thiếu hàng và xử lý yêu cầu xuất vật tư sửa chữa.");
		lblSub.setFont(FONT_LABEL);
		lblSub.setForeground(TEXT_MUTED);
		tieuDe.add(lblTitle);
		tieuDe.add(lblSub);
		header.add(tieuDe, BorderLayout.WEST);

		ActionButton btnTrangChu = new ActionButton("Trang chủ quản lý", ActionButton.KIEU_VIEN_XANH, IMG_QUAY_LAI);
		ActionButton btnNhapKho = new ActionButton("Nhập kho", ActionButton.KIEU_CHINH, IMG_THEM);
		ActionButton btnTraCuu = new ActionButton("Tra cứu phiếu nhập kho", ActionButton.KIEU_CHINH, IMG_TIM_KIEM);
		btnTrangChu.addActionListener(e -> chay(quayVeTrangChu));
		btnNhapKho.addActionListener(e -> chay(moNhapKho));
		btnTraCuu.addActionListener(e -> chay(moTraCuuPhieuNhap));

		JPanel nut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
		nut.setOpaque(false);
		nut.add(btnTrangChu);
		nut.add(btnNhapKho);
		nut.add(btnTraCuu);
		header.add(nut, BorderLayout.EAST);
		return header;
	}

	private static void chay(Runnable r) {
		if (r != null) {
			r.run();
		}
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
	// THẺ 1: TRA CỨU PHỤ TÙNG
	// =====================================================================
	private CardPanel taoTheTraCuu() {
		CardPanel the = new CardPanel("Tra cứu phụ tùng", "");
		the.getBody().setBorder(new EmptyBorder(16, 20, 20, 20));

		txtTuKhoa = new Input("Nhập mã hoặc tên phụ tùng", false);
		cboLoai = new JComboBox<>();
		cboLoai.setFont(FONT_BODY);
		cboLoai.setBackground(Color.WHITE);
		cboTinhTrang = createComboBox(TAT_CA_TINH_TRANG, TT_CON_HANG, TT_SAP_HET, TT_HET_HANG);

		ActionButton btnTim = new ActionButton("Tìm kiếm", ActionButton.KIEU_CHINH, IMG_TIM_KIEM);
		ActionButton btnLamMoi = new ActionButton("Làm mới", ActionButton.KIEU_VIEN_XANH);
		btnTim.addActionListener(e -> taiLaiDanhSach());
		btnLamMoi.addActionListener(e -> refresh());
		txtTuKhoa.addActionListener(e -> taiLaiDanhSach());
		cboLoai.addActionListener(e -> {
			if (!dangNapCombo) {
				taiLaiDanhSach();
			}
		});
		cboTinhTrang.addActionListener(e -> taiLaiDanhSach());

		JPanel hang = new JPanel(new GridBagLayout());
		hang.setOpaque(false);
		themCot(hang, labeled("Mã / Tên phụ tùng", txtTuKhoa), 0, 1.0, 16);
		themCot(hang, labeled("Loại phụ tùng", cboLoai), 1, 1.0, 16);
		themCot(hang, labeled("Tình trạng tồn", cboTinhTrang), 2, 1.0, 16);
		themCot(hang, labeledFixed(" ", btnTim), 3, 0, 12);
		themCot(hang, labeledFixed(" ", btnLamMoi), 4, 0, 0);

		the.getBody().add(hang, BorderLayout.CENTER);
		return the;
	}

	private static void themCot(JPanel p, Component c, int x, double wx, int phai) {
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = x;
		g.gridy = 0;
		g.weightx = wx;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.anchor = GridBagConstraints.NORTH;
		g.insets = new Insets(0, 0, 0, phai);
		p.add(c, g);
	}

	/** Đổ danh sách nhóm phụ tùng từ CSDL vào ô lọc và chọn "Tất cả nhóm". */
	private void napComboDanhMuc() {
		List<MucChon> ds = layDanhSachDanhMuc();
		dsDanhMuc.clear();
		if (ds != null) {
			dsDanhMuc.addAll(ds);
		}
		DefaultComboBoxModel<MucChon> m = new DefaultComboBoxModel<>();
		m.addElement(new MucChon(null, TAT_CA_NHOM));
		for (MucChon c : dsDanhMuc) {
			m.addElement(c);
		}
		dangNapCombo = true;
		cboLoai.setModel(m);
		cboLoai.setSelectedIndex(0);
		dangNapCombo = false;
	}

	// =====================================================================
	// THẺ 2: DANH SÁCH PHỤ TÙNG
	// =====================================================================
	private CardPanel taoTheDanhSach() {
		cardDanhSach = new CardPanel("Danh sách phụ tùng", "Đơn giá chưa tính thuế");
		cardDanhSach.getBody().setBorder(new EmptyBorder(0, 0, 0, 0));

		model = new DefaultTableModel(new String[] { "Mã", "Tên Phụ tùng", "Loại phụ tùng", "ĐVT", "Tồn kho",
				"Tồn tối thiểu", "Số lô", "Giá bán hiện tại (đ)", "Trạng thái", "Thao tác" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tblPhuTung = new BangCoThongBao(model);
		tblPhuTung.setThongBaoTrong("Không có phụ tùng nào để hiển thị.");
		tuyChinhBang(tblPhuTung, new int[] { 80, 200, 150, 70, 80, 100, 70, 150, 110, 100 },
				new TableCellRenderer[] { new CellRenderer(true), new NhieuDongRenderer(), new NhieuDongRenderer(),
						new CellRenderer(false), new CellRenderer(false), new CellRenderer(false),
						new CellRenderer(false), new CellRenderer(false), new BadgeRenderer(),
						new ThaoTacRenderer(new SlotIcon(IMG_XEM, 16, 16, DASH), ThaoTacRenderer.NEN_XANH_NHAT,
								new SlotIcon(ICON_SUA, 16, 16, DASH), ThaoTacRenderer.NEN_VIEN) });

		tblPhuTung.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				PhuTungRow pt = phuTungTrenHang(tblPhuTung.rowAtPoint(e.getPoint()));
				if (pt == null) {
					return;
				}
				int v = vungIcon(tblPhuTung, e, COL_THAO_TAC);
				if (v == 1) {
					xemPhuTung(pt);
				} else if (v == 2) {
					suaPhuTung(pt);
				}
			}
		});
		tblPhuTung.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				tblPhuTung.setCursor(Cursor.getPredefinedCursor(
						vungIcon(tblPhuTung, e, COL_THAO_TAC) != 0 ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
			}
		});

		spBang = new JScrollPane(tblPhuTung);
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
	// ĐỔ DỮ LIỆU LÊN BẢNG
	// =====================================================================

	/** Lấy dữ liệu theo bộ lọc hiện tại (qua DAO), lọc thêm theo "Tình trạng tồn", rồi hiển thị trang 1. */
	private void taiLaiDanhSach() {
		String tuKhoa = txtTuKhoa.getText().trim();
		MucChon nhom = (MucChon) cboLoai.getSelectedItem();
		String maDanhMuc = (nhom == null) ? null : nhom.ma;
		String tinhTrang = cboTinhTrang.getSelectedIndex() > 0 ? (String) cboTinhTrang.getSelectedItem() : null;

		List<PhuTungRow> ds = layDanhSachPhuTung(tuKhoa, maDanhMuc);
		dsPhuTung.clear();
		if (ds != null) {
			for (PhuTungRow p : ds) {
				if (tinhTrang == null || tinhTrang.equals(tinhTrangTon(p))) {
					dsPhuTung.add(p);
				}
			}
		}
		trang = 1;
		hienThiTrang();
	}

	private int tongSoTrang() {
		return Math.max(1, (dsPhuTung.size() + SO_DONG_TRANG - 1) / SO_DONG_TRANG);
	}

	private void chuyenTrang(int trangMoi) {
		if (trangMoi < 1 || trangMoi > tongSoTrang()) {
			return;
		}
		trang = trangMoi;
		hienThiTrang();
	}

	private void hienThiTrang() {
		int tong = dsPhuTung.size();
		trang = Math.min(Math.max(1, trang), tongSoTrang());
		int tu = (trang - 1) * SO_DONG_TRANG;
		int den = Math.min(tong, tu + SO_DONG_TRANG);

		model.setRowCount(0);
		for (int i = tu; i < den; i++) {
			PhuTungRow p = dsPhuTung.get(i);
			model.addRow(new Object[] { p.ma, p.ten, p.tenDanhMuc, p.donViTinh == null ? "" : p.donViTinh,
					p.soLuongTon, p.tonToiThieu, p.soLo, FMT_SO.format(p.donGia), tinhTrangTon(p), "" });
		}

		lblHienThi.setText("Hiển thị " + (den - tu) + " / " + tong + " phụ tùng");
		lblSoTrang.setText(String.valueOf(trang));
		btnTruoc.setEnabled(trang > 1);
		btnSau.setEnabled(trang < tongSoTrang());

		int n = Math.max(2, den - tu); // tối thiểu 2 dòng để chừa chỗ cho dòng thông báo
		spBang.setPreferredSize(new Dimension(100, CAO_HEADER + n * CAO_DONG));
		cardDanhSach.revalidate();
		cardDanhSach.repaint();
	}

	private PhuTungRow phuTungTrenHang(int r) {
		int idx = (trang - 1) * SO_DONG_TRANG + r;
		return (r >= 0 && idx < dsPhuTung.size()) ? dsPhuTung.get(idx) : null;
	}

	/** Hết hàng: tồn = 0; Sắp hết: tồn <= tồn tối thiểu; còn lại: Còn hàng. */
	private static String tinhTrangTon(PhuTungRow p) {
		if (p.soLuongTon <= 0) {
			return TT_HET_HANG;
		}
		return p.soLuongTon <= p.tonToiThieu ? TT_SAP_HET : TT_CON_HANG;
	}

	// =====================================================================
	// XEM / SỬA
	// =====================================================================
	private void xemPhuTung(PhuTungRow p) {
		JOptionPane.showMessageDialog(this,
				"Mã phụ tùng: " + p.ma + "\nTên phụ tùng: " + p.ten + "\nLoại phụ tùng: " + p.tenDanhMuc
						+ "\nĐơn vị tính: " + (p.donViTinh == null ? "" : p.donViTinh) + "\nTồn kho: " + p.soLuongTon
						+ " (" + tinhTrangTon(p) + ")\nTồn tối thiểu: " + p.tonToiThieu + "\nSố lô: " + p.soLo
						+ "\nGiá bán hiện tại: " + FMT_SO.format(p.donGia) + " đ",
				"Thông tin phụ tùng", JOptionPane.INFORMATION_MESSAGE);
	}

	private void suaPhuTung(PhuTungRow p) {
		// Danh sách nhóm để chọn (nếu chưa nạp được từ CSDL thì ít nhất có nhóm hiện tại)
		List<String> tenNhom = new ArrayList<>();
		for (MucChon m : dsDanhMuc) {
			tenNhom.add(m.ten);
		}
		if (!tenNhom.contains(p.tenDanhMuc)) {
			tenNhom.add(p.tenDanhMuc);
		}

		String[] kq = hienFormDialog("Sửa thông tin phụ tùng",
				new String[] { "Mã phụ tùng", "Tên phụ tùng *", "Loại phụ tùng *", "Đơn vị tính",
						"Giá bán hiện tại (đ) *", "Tồn tối thiểu *", "Tồn kho", "Số lô" },
				new String[] { p.ma, p.ten, p.tenDanhMuc, p.donViTinh == null ? "" : p.donViTinh,
						String.valueOf((long) p.donGia), String.valueOf(p.tonToiThieu), String.valueOf(p.soLuongTon),
						String.valueOf(p.soLo) },
				new boolean[] { true, false, false, false, false, false, true, true },
				new String[][] { null, null, tenNhom.toArray(new String[0]), null, null, null, null, null }, v -> {
					if (v[1].isEmpty()) {
						return "Vui lòng nhập tên phụ tùng!";
					}
					if (!v[4].replaceAll("[.,\\s]", "").matches("\\d{1,12}")) {
						return "Giá bán phải là số không âm (VD: 180000 hoặc 180.000)!";
					}
					if (!v[5].matches("\\d{1,9}")) {
						return "Tồn tối thiểu phải là số nguyên không âm!";
					}
					return null;
				});
		if (kq == null) {
			return;
		}

		String maDanhMuc = p.maDanhMuc;
		for (MucChon m : dsDanhMuc) {
			if (m.ten.equals(kq[2])) {
				maDanhMuc = m.ma;
			}
		}
		PhuTungRow moi = new PhuTungRow(p.ma, kq[1], maDanhMuc, kq[2], kq[3].isEmpty() ? null : kq[3], p.soLuongTon,
				Integer.parseInt(kq[5]), p.soLo, Double.parseDouble(kq[4].replaceAll("[.,\\s]", "")));
		if (capNhatPhuTung(moi)) {
			JOptionPane.showMessageDialog(this, "Đã cập nhật phụ tùng " + p.ma + ".", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
			taiLaiDanhSach();
		} else {
			JOptionPane.showMessageDialog(this, "Cập nhật phụ tùng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
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

	/** Vị trí click trong ô "Thao tác": 0 = ngoài, 1 = nút thứ nhất (Xem), 2 = nút thứ hai (Sửa). */
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

	/** Ô cho phép xuống dòng khi chữ dài (VD: "Gầm và hệ thống treo"). */
	private static class NhieuDongRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
			int rong = Math.max(40, t.getColumnModel().getColumn(c).getWidth() - PAD_TRAI - 8);
			String s = v == null ? "" : v.toString().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
			super.getTableCellRendererComponent(t, "<html><body style='width:" + rong + "px'>" + s + "</body></html>",
					false, false, r, c);
			setOpaque(true);
			setBackground(mauNen(sel, r));
			setForeground(TEXT_DARK);
			setFont(FONT_BODY);
			setBorder(O_BANG);
			return this;
		}
	}

	/** Huy hiệu trạng thái tồn kho bo tròn, màu theo trạng thái. */
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
			if (TT_CON_HANG.equals(t)) {
				nen = OK_BG;
				setForeground(OK_FG);
			} else if (TT_SAP_HET.equals(t)) {
				nen = new Color(255, 238, 238);
				setForeground(new Color(205, 50, 50));
			} else {
				nen = ERR_BG;
				setForeground(ERR_FG);
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

	/** Ô "Thao tác": hai nút vuông bo góc có icon (VÙNG CHÈN ẢNH). */
	private static class ThaoTacRenderer extends JPanel implements TableCellRenderer {
		private static final long serialVersionUID = 1L;
		static final int NEN_XANH_NHAT = 0; // nút Xem
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
