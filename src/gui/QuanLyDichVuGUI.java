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
import java.util.Comparator;
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

import business.DichVuBUS;
import entity.DichVu;

/**
 * Màn hình QUẢN LÝ DỊCH VỤ (JPanel) - được nhúng vào CardLayout của TrangChuQuanLyGUI.
 *
 * Gồm 2 thẻ:
 *  1. Tra cứu: Mã dịch vụ, Loại dịch vụ, Sắp xếp theo đơn giá hiện tại, nút Tìm kiếm / Thêm dịch vụ
 *  2. Danh sách dịch vụ: Mã dịch vụ, Tên dịch vụ, Loại dịch vụ, Đơn giá hiện tại, Thao tác (Cập nhật)
 *
 * Bảng CSDL: DichVu(maDichVu, tenDichVu, loaiDichVu NULL, donGiaHienTai).
 *
 * Dữ liệu danh sách, bộ lọc và thao tác lưu được đọc/ghi trực tiếp qua DichVuBUS.
 */
public class QuanLyDichVuGUI extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final int SO_DONG_TRANG = 8;
	private static final int CAO_DONG = 52;
	private static final int CAO_HEADER = 40;
	private static final int PAD_TRAI = 16;

	private static final String TAT_CA_LOAI = "Tất cả loại dịch vụ";
	private static final String[] LOAI_DICH_VU = { "Bảo dưỡng", "Kiểm tra", "Vệ sinh" };
	private static final String[] SAP_XEP = { "Mặc định", "Đơn giá tăng dần", "Đơn giá giảm dần" };

	private static final String[] NHAN_THAO_TAC = { "Cập nhật" };
	private static final String SEP = " · ";
	private static final String TXT_THAO_TAC = String.join(SEP, NHAN_THAO_TAC);
	private static final int COL_THAO_TAC = 4;

	private static final Border O_BANG = new CompoundBorder(new MatteBorder(0, 0, 1, 0, new Color(236, 240, 245)),
			new EmptyBorder(0, PAD_TRAI, 0, 8));
	private static final NumberFormat FMT_TIEN = NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN"));

	/** Một dòng dịch vụ đã chuẩn hóa cho phần bảng và phân trang. */
	
	private final Runnable quayVeTrangChu;
	private final DichVuBUS dichVuBUS = new DichVuBUS();
	private final List<DichVu> dsDichVu = new ArrayList<>(); // toàn bộ kết quả (chia trang ở UI)
	private int trang = 1;
	private boolean dangNapCombo = false;

	// ---- Thẻ tra cứu ----
	private Input txtMa;
	private JComboBox<String> cboLoai, cboSapXep;

	// ---- Thẻ danh sách ----
	private CardPanel cardDanhSach;
	private BangCoThongBao tblDichVu;
	private DefaultTableModel model;
	private JScrollPane spBang;
	private JLabel lblHienThi, lblSoTrang;
	private JButton btnTruoc, btnSau;

	private DichVuBUS dv_bus;
	
	/**
	 * @param quayVeTrangChu hành động của nút "Trang chủ quản lý" (thường là hienThi(KEY_TRANG_CHU))
	 */
	public QuanLyDichVuGUI(Runnable quayVeTrangChu) {
		dv_bus = new DichVuBUS();
		
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

	/** Gọi mỗi khi chuyển sang màn hình này: xóa bộ lọc, tải lại danh sách loại và danh sách dịch vụ. */
	public void refresh() {
		txtMa.setText("");
		cboSapXep.setSelectedIndex(0);
		napComboLoai(); // đặt lại về "Tất cả loại dịch vụ"
		taiLaiDanhSach();
	}

	// =====================================================================
	// KẾT NỐI CƠ SỞ DỮ LIỆU (DAO/BUS)
	// =====================================================================

	/**
	 * Lấy danh sách dịch vụ theo bộ lọc.
	 * @param maDichVu   chuỗi mã cần tìm ("" = không lọc), nên tìm gần đúng: maDichVu LIKE %...%
	 * @param loaiDichVu loại cần lọc (null = tất cả loại)
	 * Việc sắp xếp theo đơn giá được xử lý ở giao diện sau khi nhận kết quả.
	 */
	private List<DichVu> layDanhSachDichVu(String maDichVu, String loaiDichVu) {
		List<DichVu> ds = new ArrayList<>();
		try {
			for (DichVu dv : dichVuBUS.timKiem(maDichVu, loaiDichVu)) {
				ds.add(new DichVu(dv.getMaDichVu(), dv.getTenDichVu(), dv.getLoaiDichVu(),
						dv.getDonGiaHienTai()));
			}
		} catch (SQLException e) {
			hienLoiCSDL("tải danh sách dịch vụ", e);
		}
		return ds;
	}

	/**
	 * Lấy các loại dịch vụ hiện có để đổ vào ô lọc.
	 */
	private List<String> layDanhSachLoaiDichVu() {
		List<String> ds = new ArrayList<>();
		try {
			ds.addAll(dichVuBUS.layDanhSachLoai());
		} catch (SQLException e) {
			hienLoiCSDL("tải danh sách loại dịch vụ", e);
		}
		return ds;
	}

	/**
	 * Thêm mới (themMoi = true, mã tự sinh, ví dụ DV004) hoặc cập nhật (themMoi = false) một dịch vụ.
	 * @return true nếu lưu thành công
	 */
	private boolean luuDichVu(DichVu dv, boolean themMoi) throws SQLException {
		String ma = themMoi ? dichVuBUS.phatSinhMaDV() : dv.getMaDichVu();
		DichVu entity = new DichVu(ma, dv.getTenDichVu(), dv.getLoaiDichVu(), dv.getDonGiaHienTai());
		return themMoi ? dichVuBUS.them(entity) : dichVuBUS.capNhat(entity);
	}

	private void hienLoiCSDL(String thaoTac, SQLException e) {
		JOptionPane.showMessageDialog(this, "Không thể " + thaoTac + ": " + e.getMessage(), "Lỗi",
				JOptionPane.ERROR_MESSAGE);
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
		JLabel lblTitle = new JLabel("Quản lý dịch vụ");
		lblTitle.setFont(FONT_TITLE);
		lblTitle.setForeground(TITLE);
		JLabel lblSub = new JLabel("Quản lý danh mục dịch vụ và đơn giá hiện tại của gara.");
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
		CardPanel the = new CardPanel("Tra cứu dịch vụ", "");
		the.setTitleIcon(IMG_TIM_KIEM);
		the.getBody().setBorder(new EmptyBorder(16, 20, 20, 20));

		txtMa = new Input("Nhập mã dịch vụ", false);
		cboLoai = createComboBox(TAT_CA_LOAI);
		cboSapXep = createComboBox(SAP_XEP);

		ActionButton btnTim = new ActionButton("Tìm kiếm", ActionButton.KIEU_CHINH, IMG_TIM_KIEM);
		ActionButton btnThem = new ActionButton("Thêm dịch vụ", ActionButton.KIEU_CHINH, IMG_THEM);
		btnTim.addActionListener(e -> taiLaiDanhSach());
		txtMa.addActionListener(e -> taiLaiDanhSach());
		cboLoai.addActionListener(e -> {
			if (!dangNapCombo) {
				taiLaiDanhSach();
			}
		});
		cboSapXep.addActionListener(e -> taiLaiDanhSach());
		btnThem.addActionListener(e -> themDichVu());

		JPanel hang = new JPanel(new GridBagLayout());
		hang.setOpaque(false);
		themCot(hang, labeled("Mã dịch vụ", txtMa), 0, 1.2, 16);
		themCot(hang, labeled("Loại dịch vụ", cboLoai), 1, 1.0, 16);
		themCot(hang, labeled("Sắp xếp theo đơn giá hiện tại", cboSapXep), 2, 1.0, 16);
		themCot(hang, labeledFixed(" ", btnTim), 3, 0, 12);
		themCot(hang, labeledFixed(" ", btnThem), 4, 0, 0);

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

	/** Đổ danh sách loại dịch vụ từ CSDL vào ô lọc, cố gắng giữ lại lựa chọn hiện tại. */
	private void napComboLoai() {
		Object dangChon = cboLoai.getSelectedItem();
		List<String> loai = layDanhSachLoaiDichVu();

		List<String> items = new ArrayList<>();
		items.add(TAT_CA_LOAI);
		if (loai != null) {
			items.addAll(loai);
		}
		dangNapCombo = true;
		cboLoai.setModel(new DefaultComboBoxModel<>(items.toArray(new String[0])));
		cboLoai.setSelectedIndex(0);
		if (dangChon != null && items.contains(dangChon.toString())) {
			cboLoai.setSelectedItem(dangChon.toString());
		}
		dangNapCombo = false;
	}

	// =====================================================================
	// THẺ 2: DANH SÁCH DỊCH VỤ
	// =====================================================================
	private CardPanel taoTheDanhSach() {
		cardDanhSach = new CardPanel("Danh sách dịch vụ", "");
		cardDanhSach.getBody().setBorder(new EmptyBorder(0, 0, 0, 0));

		model = new DefaultTableModel(
				new String[] { "Mã dịch vụ", "Tên dịch vụ", "Loại dịch vụ", "Đơn giá hiện tại", "Thao tác" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tblDichVu = new BangCoThongBao(model);
		tblDichVu.setThongBaoTrong("Không có dịch vụ nào để hiển thị.");
		tuyChinhBang(tblDichVu, new int[] { 130, 330, 200, 190, 130 },
				new DefaultTableCellRenderer[] { new CellRenderer(true), new CellRenderer(false),
						new CellRenderer(false), new CellRenderer(false), new ThaoTacRenderer(TXT_THAO_TAC) });

		tblDichVu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = tblDichVu.rowAtPoint(e.getPoint());
				DichVu dv = dichVuTrenHang(row);
				if (dv != null && vungThaoTac(tblDichVu, e, COL_THAO_TAC, NHAN_THAO_TAC, SEP) == 1) {
					capNhatDichVu(dv);
				}
			}
		});
		tblDichVu.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				tblDichVu.setCursor(Cursor.getPredefinedCursor(
						vungThaoTac(tblDichVu, e, COL_THAO_TAC, NHAN_THAO_TAC, SEP) != 0 ? Cursor.HAND_CURSOR
								: Cursor.DEFAULT_CURSOR));
			}
		});

		spBang = new JScrollPane(tblDichVu);
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

	/** Lấy dữ liệu theo bộ lọc hiện tại (qua DAO), sắp xếp theo đơn giá nếu được chọn, rồi hiển thị trang 1. */
	private void taiLaiDanhSach() {
		String ma = txtMa.getText().trim();
		String loai = cboLoai.getSelectedIndex() > 0 ? (String) cboLoai.getSelectedItem() : null;

		List<DichVu> ds = layDanhSachDichVu(ma, loai);
		dsDichVu.clear();
		if (ds != null) {
			dsDichVu.addAll(ds);
		}

		int sx = cboSapXep.getSelectedIndex();
		if (sx == 1) {
			dsDichVu.sort(Comparator.comparingDouble((DichVu d) -> d.getDonGiaHienTai()));
		} else if (sx == 2) {
			dsDichVu.sort(Comparator.comparingDouble((DichVu d) -> d.getDonGiaHienTai()).reversed());
		}

		trang = 1;
		hienThiTrang();
	}

	private int tongSoTrang() {
		return Math.max(1, (dsDichVu.size() + SO_DONG_TRANG - 1) / SO_DONG_TRANG);
	}

	private void chuyenTrang(int trangMoi) {
		if (trangMoi < 1 || trangMoi > tongSoTrang()) {
			return;
		}
		trang = trangMoi;
		hienThiTrang();
	}

	private void hienThiTrang() {
		int tong = dsDichVu.size();
		trang = Math.min(Math.max(1, trang), tongSoTrang());
		int tu = (trang - 1) * SO_DONG_TRANG;
		int den = Math.min(tong, tu + SO_DONG_TRANG);

		model.setRowCount(0);
		for (int i = tu; i < den; i++) {
			DichVu dv = dsDichVu.get(i);
			model.addRow(new Object[] { dv.getMaDichVu(), dv.getTenDichVu(), dv.getLoaiDichVu() == null ? "" : dv.getLoaiDichVu(), hienTien(dv.getDonGiaHienTai()),
					TXT_THAO_TAC });
		}

		cardDanhSach.setNote(tong + " dịch vụ");
		lblHienThi.setText("Hiển thị " + (den - tu) + " / " + tong + " bản ghi");
		lblSoTrang.setText(String.valueOf(trang));
		btnTruoc.setEnabled(trang > 1);
		btnSau.setEnabled(trang < tongSoTrang());

		int n = Math.max(2, den - tu); // tối thiểu 2 dòng để chừa chỗ cho dòng thông báo
		spBang.setPreferredSize(new Dimension(100, CAO_HEADER + n * CAO_DONG));
		cardDanhSach.revalidate();
		cardDanhSach.repaint();
	}

	private DichVu dichVuTrenHang(int r) {
		int idx = (trang - 1) * SO_DONG_TRANG + r;
		return (r >= 0 && idx < dsDichVu.size()) ? dsDichVu.get(idx) : null;
	}

	private static String hienTien(double v) {
		return FMT_TIEN.format(v) + " đ";
	}

	// =====================================================================
	// THÊM / CẬP NHẬT
	// =====================================================================
	private void themDichVu() {
		String maDV = null;
		String[] kq = null;
		try {
			maDV = dv_bus.phatSinhMaDV();
			
			kq = hienFormDialog("Thêm dịch vụ",
					new String[] { "Mã dịch vụ", "Tên dịch vụ *", "Loại dịch vụ *",
							"Đơn giá hiện tại (đ) *" },
					new String[] { maDV, "", LOAI_DICH_VU[0], "" }, new boolean[] { true, false, false, false },
					LOAI_DICH_VU,
					QuanLyDichVuGUI::kiemTraDichVu);
		} catch (SQLException e) {
			hienLoiCSDL("phát sinh mã dịch vụ", e);
			return;
		}
		if (kq == null) {
			return;
		}
		DichVu dv = new DichVu(maDV, kq[1], kq[2].isEmpty() ? null : kq[2], docTien(kq[3]));
		try {
			if (luuDichVu(dv, true)) {
				JOptionPane.showMessageDialog(this, "Đã thêm dịch vụ " + dv.getTenDichVu() + ".", "Thông báo",
						JOptionPane.INFORMATION_MESSAGE);
				napComboLoai();
				taiLaiDanhSach();
			} else {
				JOptionPane.showMessageDialog(this, "Thêm dịch vụ thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		} catch (SQLException e) {
			hienLoiCSDL("thêm dịch vụ", e);
		}
	}

	private void capNhatDichVu(DichVu dv) {
		String[] kq = hienFormDialog("Cập nhật dịch vụ",
				new String[] { "Mã dịch vụ", "Tên dịch vụ *", "Loại dịch vụ *",
						"Đơn giá hiện tại (đ) *" },
				new String[] { dv.getMaDichVu(), dv.getTenDichVu(), dv.getLoaiDichVu() == null ? "" : dv.getLoaiDichVu(), String.valueOf((long) dv.getDonGiaHienTai()) },
				new boolean[] { true, false, false, false }, LOAI_DICH_VU, QuanLyDichVuGUI::kiemTraDichVu);
		if (kq == null) {
			return;
		}
		DichVu moi = new DichVu(dv.getMaDichVu(), kq[1], kq[2].isEmpty() ? null : kq[2], docTien(kq[3]));
		try {
			if (luuDichVu(moi, false)) {
				JOptionPane.showMessageDialog(this, "Đã cập nhật dịch vụ " + dv.getMaDichVu() + ".", "Thông báo",
						JOptionPane.INFORMATION_MESSAGE);
				napComboLoai();
				taiLaiDanhSach();
			} else {
				JOptionPane.showMessageDialog(this, "Cập nhật dịch vụ thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		} catch (SQLException e) {
			hienLoiCSDL("cập nhật dịch vụ", e);
		}
	}

	/** Trả về thông báo lỗi, hoặc null nếu hợp lệ. v = {mã, tên, loại, đơn giá}. */
	private static String kiemTraDichVu(String[] v) {
		if (v[1].isEmpty()) {
			return "Vui lòng nhập tên dịch vụ!";
		}
		if (v[1].length() > 150) {
			return "Tên dịch vụ tối đa 150 ký tự!";
		}
		if (v[2].length() > 100) {
			return "Loại dịch vụ tối đa 100 ký tự!";
		}
		if (!v[3].replaceAll("[.,\\s]", "").matches("\\d+")) {
			return "Đơn giá phải là số không âm (VD: 250000 hoặc 250.000)!";
		}
		return null;
	}

	private static double docTien(String s) {
		return Double.parseDouble(s.replaceAll("[.,\\s]", ""));
	}

	// =====================================================================
	// HỘP THOẠI NHẬP LIỆU DÙNG CHUNG
	// =====================================================================
	/**
	 * Hiện hộp thoại nhập liệu (modal) gồm các ô "nhãn - giá trị".
	 * @param kiemTra hàm kiểm tra dữ liệu: trả về thông báo lỗi hoặc null nếu hợp lệ (có thể null)
	 * @return mảng giá trị đã nhập (đã trim), hoặc null nếu người dùng bấm Hủy
	 */
	private String[] hienFormDialog(String tieuDe, String[] nhan, String[] giaTri, boolean[] chiDoc,
			Function<String[], String> kiemTra) {
		return hienFormDialog(tieuDe, nhan, giaTri, chiDoc, null, kiemTra);
	}

	private String[] hienFormDialog(String tieuDe, String[] nhan, String[] giaTri, boolean[] chiDoc,
			String[] luaChonLoai, Function<String[], String> kiemTra) {
		Window cha = SwingUtilities.getWindowAncestor(this);
		JDialog dlg = new JDialog(cha, tieuDe, Dialog.ModalityType.APPLICATION_MODAL);

		JPanel noiDung = new JPanel(new BorderLayout(0, 8));
		noiDung.setBackground(Color.WHITE);
		noiDung.setBorder(new EmptyBorder(20, 24, 20, 24));

		JPanel luoi = new JPanel(new GridBagLayout());
		luoi.setOpaque(false);
		JComponent[] o = new JComponent[nhan.length];
		for (int i = 0; i < nhan.length; i++) {
			if (luaChonLoai != null && i == 2) {
				JComboBox<String> cbo = createComboBox(luaChonLoai);
				cbo.setSelectedItem(giaTri[i]);
				o[i] = cbo;
			} else {
				JTextField txt = new Input(null, chiDoc[i]);
				txt.setText(giaTri[i]);
				o[i] = txt;
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
				v[i] = o[i] instanceof JComboBox
						? String.valueOf(((JComboBox<?>) o[i]).getSelectedItem()).trim()
						: ((JTextField) o[i]).getText().trim();
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
	private static void tuyChinhBang(JTable t, int[] rong, DefaultTableCellRenderer[] renderers) {
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
}
