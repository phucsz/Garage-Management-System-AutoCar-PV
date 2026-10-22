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
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
 * Màn hình quản lý nhân viên (JPanel) - được nhúng vào CardLayout của TrangChuGUI.
 * Gồm 3 thẻ: Tra cứu, Danh sách (bảng) và Cập nhật / Thêm nhân viên.
 *
 * Dữ liệu hiện là dữ liệu mẫu trong bộ nhớ; các chỗ cần nối CSDL được đánh dấu TODO.
 */
public class NhanVienGUI extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final DateTimeFormatter FMT_NGAY = DateTimeFormatter.ofPattern("dd/MM/uuuu")
			.withResolverStyle(ResolverStyle.STRICT);

	private static final String[] VAI_TRO = { "Quản lý", "Tiếp nhận viên", "Kỹ thuật viên", "Thu ngân" };
	private static final String[] TRANG_THAI = { "Đang làm việc", "Tạm ngưng" };
	private static final String[] GIOI_TINH = { "Nam", "Nữ", "Khác" };
	private static final String TXT_THAO_TAC = "Xem · Sửa · Xóa";
	private static final int COL_THAO_TAC = 5;
	private static final int PAD_TRAI = 16;

	private static final Border O_BANG = new CompoundBorder(new MatteBorder(0, 0, 1, 0, new Color(236, 240, 245)),
			new EmptyBorder(0, PAD_TRAI, 0, 8));

	/** Một dòng dữ liệu nhân viên (tạm thời; thay bằng entity.NhanVien của project). */
	private static class NhanVienRow {
		String ma, hoTen, sdt, vaiTro, trangThai, email, gioiTinh;
		LocalDate ngaySinh, ngayVaoLam;
	}

	private final Runnable quayVeTrangChu;
	private final List<NhanVienRow> dsNhanVien = new ArrayList<>(); // dữ liệu gốc
	private final List<NhanVienRow> dsHienThi = new ArrayList<>(); // sau khi lọc

	// ---- Thẻ tra cứu ----
	private CardPanel cardTraCuu;
	private Input txtTimKiem;
	private JComboBox<String> cboLocVaiTro, cboLocTrangThai;

	// ---- Thẻ danh sách ----
	private CardPanel cardDanhSach;
	private JTable tblNhanVien;
	private DefaultTableModel model;
	private JScrollPane spBang;
	private JLabel lblHienThi;

	// ---- Thẻ cập nhật ----
	private CardPanel cardCapNhat;
	private Input txtMa, txtHoTen, txtSdt, txtEmail, txtNgaySinh, txtNgayVaoLam;
	private JComboBox<String> cboVaiTro, cboTrangThai, cboGioiTinh;
	private JButton btnDatLaiMk;

	// ---- Trạng thái ----
	private NhanVienRow dangChon;
	private boolean dangThem = false;
	private boolean dangDongBo = false; // chặn sự kiện chọn dòng khi code tự chọn

	public NhanVienGUI(Runnable quayVeTrangChu) {
		this.quayVeTrangChu = quayVeTrangChu;
		setLayout(new BorderLayout());
		setBackground(BG);

		ScrollablePanel trang = new ScrollablePanel(new BorderLayout(0, 16));
		trang.setBackground(BG);
		trang.setBorder(new EmptyBorder(22, 28, 28, 28));
		trang.add(taoTieuDeTrang(), BorderLayout.NORTH);

		JPanel cacThe = new JPanel();
		cacThe.setOpaque(false);
		cacThe.setLayout(new BoxLayout(cacThe, BoxLayout.Y_AXIS));
		cacThe.add(taoTheTraCuu());
		cacThe.add(khoangCach(16));
		cacThe.add(taoTheDanhSach());
		cacThe.add(khoangCach(16));
		cacThe.add(taoTheCapNhat());
		trang.add(cacThe, BorderLayout.CENTER);

		JScrollPane sp = new JScrollPane(trang, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setBorder(null);
		sp.getVerticalScrollBar().setUnitIncrement(16);
		add(sp, BorderLayout.CENTER);

		taiDuLieuMau();
		locDuLieu();
		if (!dsHienThi.isEmpty()) {
			chonDong(dsHienThi.get(0));
			datCheDoSua(dsHienThi.get(0));
		}
	}

	private static Component khoangCach(int h) {
		return javax.swing.Box.createVerticalStrut(h);
	}

	/** Gọi mỗi khi chuyển sang màn hình này (giống refresh() của các GUI khác). */
	public void refresh() {
		// TODO: nạp lại dsNhanVien từ CSDL (NhanVienDAO) rồi lọc lại
		locDuLieu();
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
		JLabel lblTitle = new JLabel("Quản lý nhân viên");
		lblTitle.setFont(FONT_TITLE);
		lblTitle.setForeground(TITLE);
		JLabel lblSub = new JLabel("Quản lý hồ sơ nhân viên, vai trò truy cập và trạng thái làm việc tại gara.");
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

	// =====================================================================
	// THẺ 1: TRA CỨU
	// =====================================================================
	private CardPanel taoTheTraCuu() {
		cardTraCuu = new CardPanel("Tra cứu nhân viên", "");

		txtTimKiem = new Input("Nhập thông tin nhân viên", false);
		cboLocVaiTro = createComboBox(themDau("Tất cả vai trò", VAI_TRO));
		cboLocTrangThai = createComboBox(themDau("Tất cả trạng thái", TRANG_THAI));

		ActionButton btnTim = new ActionButton("Tìm kiếm", ActionButton.KIEU_CHINH, IMG_TIM_KIEM);
		ActionButton btnThem = new ActionButton("Thêm nhân viên", ActionButton.KIEU_CHINH, IMG_THEM);

		btnTim.addActionListener(e -> locDuLieu());
		txtTimKiem.addActionListener(e -> locDuLieu());
		cboLocVaiTro.addActionListener(e -> locDuLieu());
		cboLocTrangThai.addActionListener(e -> locDuLieu());
		btnThem.addActionListener(e -> datCheDoThem());

		JPanel hang = new JPanel(new GridBagLayout());
		hang.setOpaque(false);
		themCot(hang, labeled("Mã / Họ tên / Số điện thoại", txtTimKiem), 0, 1.4, 14);
		themCot(hang, labeled("Vai trò", cboLocVaiTro), 1, 1.0, 14);
		themCot(hang, labeled("Trạng thái", cboLocTrangThai), 2, 1.0, 14);
		themCot(hang, labeledFixed(" ", btnTim), 3, 0, 10);
		themCot(hang, labeledFixed(" ", btnThem), 4, 0, 0);

		cardTraCuu.getBody().add(hang, BorderLayout.CENTER);
		return cardTraCuu;
	}

	private static String[] themDau(String dau, String[] ds) {
		String[] kq = new String[ds.length + 1];
		kq[0] = dau;
		System.arraycopy(ds, 0, kq, 1, ds.length);
		return kq;
	}

	private static void themCot(JPanel p, Component c, int x, double weightX, int phai) {
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = x;
		g.gridy = 0;
		g.weightx = weightX;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.anchor = GridBagConstraints.NORTH;
		g.insets = new Insets(0, 0, 0, phai);
		p.add(c, g);
	}

	// =====================================================================
	// THẺ 2: DANH SÁCH (BẢNG)
	// =====================================================================
	private CardPanel taoTheDanhSach() {
		cardDanhSach = new CardPanel("Danh sách nhân viên", "Chọn Sửa để cập nhật hồ sơ bên dưới");
		cardDanhSach.getBody().setBorder(new EmptyBorder(0, 0, 0, 0));

		model = new DefaultTableModel(
				new String[] { "Mã nhân viên", "Họ tên", "Số điện thoại", "Vai trò", "Trạng thái", "Thao tác" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tblNhanVien = new JTable(model);
		tuyChinhBang();

		spBang = new JScrollPane(tblNhanVien);
		spBang.setBorder(null);
		spBang.getViewport().setBackground(Color.WHITE);

		cardDanhSach.getBody().add(spBang, BorderLayout.CENTER);
		cardDanhSach.getBody().add(taoChanBang(), BorderLayout.SOUTH);
		return cardDanhSach;
	}

	private void tuyChinhBang() {
		tblNhanVien.setRowHeight(46);
		tblNhanVien.setShowGrid(false);
		tblNhanVien.setIntercellSpacing(new Dimension(0, 0));
		tblNhanVien.setFont(FONT_BODY);
		tblNhanVien.setForeground(TEXT_DARK);
		tblNhanVien.setSelectionBackground(ROW_SELECTED);
		tblNhanVien.setSelectionForeground(TEXT_DARK);
		tblNhanVien.setFillsViewportHeight(true);
		tblNhanVien.setFocusable(false);

		JTableHeader th = tblNhanVien.getTableHeader();
		th.setReorderingAllowed(false);
		th.setResizingAllowed(false);
		th.setPreferredSize(new Dimension(100, 40));
		th.setDefaultRenderer(new HeaderRenderer());

		int[] rong = { 110, 200, 170, 150, 150, 170 };
		for (int i = 0; i < rong.length; i++) {
			tblNhanVien.getColumnModel().getColumn(i).setPreferredWidth(rong[i]);
		}
		tblNhanVien.getColumnModel().getColumn(0).setCellRenderer(new CellRenderer(true));
		tblNhanVien.getColumnModel().getColumn(1).setCellRenderer(new CellRenderer(false));
		tblNhanVien.getColumnModel().getColumn(2).setCellRenderer(new CellRenderer(false));
		tblNhanVien.getColumnModel().getColumn(3).setCellRenderer(new CellRenderer(false));
		tblNhanVien.getColumnModel().getColumn(4).setCellRenderer(new BadgeRenderer());
		tblNhanVien.getColumnModel().getColumn(COL_THAO_TAC).setCellRenderer(new ThaoTacRenderer());

		// Chọn dòng -> nạp vào form cập nhật
		tblNhanVien.getSelectionModel().addListSelectionListener(e -> {
			if (e.getValueIsAdjusting() || dangDongBo) {
				return;
			}
			int r = tblNhanVien.getSelectedRow();
			if (r >= 0 && r < dsHienThi.size()) {
				datCheDoSua(dsHienThi.get(r));
			}
		});

		// Bấm vào cột "Thao tác": Xem / Sửa / Xóa
		tblNhanVien.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = tblNhanVien.rowAtPoint(e.getPoint());
				if (row < 0 || row >= dsHienThi.size()) {
					return;
				}
				NhanVienRow nv = dsHienThi.get(row);
				switch (vungThaoTac(e)) {
				case 1:
					xem(nv);
					break;
				case 2:
					datCheDoSua(nv);
					cuonDenForm();
					txtHoTen.requestFocusInWindow();
					break;
				case 3:
					xoa(nv);
					break;
				default:
					break;
				}
			}
		});
		tblNhanVien.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				tblNhanVien.setCursor(Cursor.getPredefinedCursor(
						vungThaoTac(e) != 0 ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
			}
		});
	}

	/** Xác định vị trí click trong ô "Thao tác": 0 = ngoài, 1 = Xem, 2 = Sửa, 3 = Xóa. */
	private int vungThaoTac(MouseEvent e) {
		int row = tblNhanVien.rowAtPoint(e.getPoint());
		int col = tblNhanVien.columnAtPoint(e.getPoint());
		if (row < 0 || col != COL_THAO_TAC) {
			return 0;
		}
		Rectangle rc = tblNhanVien.getCellRect(row, col, false);
		int x = e.getX() - rc.x - PAD_TRAI;
		FontMetrics fm = tblNhanVien.getFontMetrics(FONT_BOLD);
		int wXem = fm.stringWidth("Xem");
		int wSua = fm.stringWidth("Sửa");
		int wXoa = fm.stringWidth("Xóa");
		int wSep = fm.stringWidth(" · ");

		if (x >= 0 && x <= wXem) {
			return 1;
		}
		int s = wXem + wSep;
		if (x >= s && x <= s + wSua) {
			return 2;
		}
		s += wSua + wSep;
		if (x >= s && x <= s + wXoa) {
			return 3;
		}
		return 0;
	}

	private JPanel taoChanBang() {
		JPanel p = new JPanel(new BorderLayout());
		p.setOpaque(false);
		p.setBorder(new EmptyBorder(14, 16, 14, 16));

		lblHienThi = new JLabel();
		lblHienThi.setFont(FONT_SMALL);
		lblHienThi.setForeground(TEXT_MUTED);
		p.add(lblHienThi, BorderLayout.WEST);

		// Phân trang tĩnh (1 trang). TODO: nối với phân trang thực nếu cần.
		JPanel pt = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		pt.setOpaque(false);
		pt.add(nhanTrang("‹"));
		RoundedPanel chip = new RoundedPanel(8, PRIMARY);
		chip.setLayout(new GridBagLayout());
		chip.setPreferredSize(new Dimension(28, 28));
		JLabel lblSo = new JLabel("1");
		lblSo.setFont(FONT_BOLD);
		lblSo.setForeground(Color.WHITE);
		chip.add(lblSo);
		pt.add(chip);
		pt.add(nhanTrang("›"));
		p.add(pt, BorderLayout.EAST);
		return p;
	}

	private JLabel nhanTrang(String t) {
		JLabel l = new JLabel(t);
		l.setFont(FONT_SECTION);
		l.setForeground(TEXT_MUTED);
		return l;
	}

	// =====================================================================
	// THẺ 3: CẬP NHẬT / THÊM NHÂN VIÊN
	// =====================================================================
	private CardPanel taoTheCapNhat() {
		cardCapNhat = new CardPanel("Cập nhật nhân viên", "Chưa chọn nhân viên");

		txtMa = new Input(null, true);
		txtHoTen = new Input(null, false);
		txtSdt = new Input(null, false);
		txtEmail = new Input(null, false);
		txtNgaySinh = new Input("dd/MM/yyyy", false);
		txtNgayVaoLam = new Input("dd/MM/yyyy", false);
		cboVaiTro = createComboBox(VAI_TRO);
		cboTrangThai = createComboBox(TRANG_THAI);
		cboGioiTinh = createComboBox(GIOI_TINH);

		JPanel luoi = new JPanel(new GridBagLayout());
		luoi.setOpaque(false);
		themO(luoi, labeled("Mã nhân viên", txtMa), 0, 0);
		themO(luoi, labeled("Họ tên *", txtHoTen), 0, 1);
		themO(luoi, labeled("Số điện thoại *", txtSdt), 0, 2);
		themO(luoi, labeled("Vai trò *", cboVaiTro), 1, 0);
		themO(luoi, labeled("Trạng thái làm việc", cboTrangThai), 1, 1);
		themO(luoi, labeled("Email", txtEmail), 1, 2);
		themO(luoi, labeled("Ngày sinh", txtNgaySinh), 2, 0);
		themO(luoi, labeled("Giới tính", cboGioiTinh), 2, 1);
		themO(luoi, labeled("Ngày vào làm", txtNgayVaoLam), 2, 2);

		ActionButton btnLuu = new ActionButton("Lưu thay đổi", ActionButton.KIEU_CHINH, IMG_LUU);
		ActionButton btnHuy = new ActionButton("Hủy thay đổi", ActionButton.KIEU_VIEN_XAM);
		btnDatLaiMk = createLinkButton("Đặt lại mật khẩu");

		btnLuu.addActionListener(e -> luu());
		btnHuy.addActionListener(e -> huyThayDoi());
		btnDatLaiMk.addActionListener(e -> datLaiMatKhau());

		JPanel hangNut = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		hangNut.setOpaque(false);
		hangNut.setBorder(new EmptyBorder(6, 0, 0, 0));
		hangNut.add(btnLuu);
		hangNut.add(btnHuy);
		hangNut.add(btnDatLaiMk);

		cardCapNhat.getBody().add(luoi, BorderLayout.CENTER);
		cardCapNhat.getBody().add(hangNut, BorderLayout.SOUTH);
		return cardCapNhat;
	}

	private static void themO(JPanel p, Component c, int row, int col) {
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = col;
		g.gridy = row;
		g.weightx = 1;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.anchor = GridBagConstraints.NORTHWEST;
		g.insets = new Insets(0, 0, 12, col == 2 ? 0 : 16);
		p.add(c, g);
	}

	// =====================================================================
	// DỮ LIỆU & LỌC
	// =====================================================================
	private void taiDuLieuMau() {
		// TODO: thay bằng nhanVienDAO.getAll()
		themMau("NV-001", "Nguyễn Văn A", "0901111222", "Quản lý", "Đang làm việc", "nguyenvana@autocarpv.vn",
				LocalDate.of(1990, 5, 20), "Nam", LocalDate.of(2022, 1, 10));
		themMau("NV-002", "Lê Thu Hà", "0902222333", "Tiếp nhận viên", "Đang làm việc", "lethuha@autocarpv.vn",
				LocalDate.of(1996, 8, 12), "Nữ", LocalDate.of(2023, 3, 15));
		themMau("NV-003", "Trần Minh", "0903333444", "Kỹ thuật viên", "Đang làm việc", "tranminh@autocarpv.vn",
				LocalDate.of(1995, 4, 15), "Nam", LocalDate.of(2024, 3, 1));
		themMau("NV-004", "Phạm Ngọc Anh", "0904444555", "Thu ngân", "Đang làm việc", "phamngocanh@autocarpv.vn",
				LocalDate.of(1998, 11, 2), "Nữ", LocalDate.of(2023, 9, 1));
		themMau("NV-005", "Nguyễn Đức Long", "0905555666", "Kỹ thuật viên", "Tạm ngưng", "nguyenduclong@autocarpv.vn",
				LocalDate.of(1993, 2, 27), "Nam", LocalDate.of(2022, 6, 20));
		themMau("NV-006", "Lê Văn Khoa", "0906666777", "Kỹ thuật viên", "Đang làm việc", "levankhoa@autocarpv.vn",
				LocalDate.of(1997, 7, 9), "Nam", LocalDate.of(2024, 7, 1));
	}

	private void themMau(String ma, String hoTen, String sdt, String vaiTro, String trangThai, String email,
			LocalDate ngaySinh, String gioiTinh, LocalDate ngayVaoLam) {
		NhanVienRow nv = new NhanVienRow();
		nv.ma = ma;
		nv.hoTen = hoTen;
		nv.sdt = sdt;
		nv.vaiTro = vaiTro;
		nv.trangThai = trangThai;
		nv.email = email;
		nv.ngaySinh = ngaySinh;
		nv.gioiTinh = gioiTinh;
		nv.ngayVaoLam = ngayVaoLam;
		dsNhanVien.add(nv);
	}

	/** Lọc dsNhanVien theo ô tìm kiếm + 2 combo rồi đổ vào bảng. */
	private void locDuLieu() {
		String tuKhoa = boDau(txtTimKiem.getText().trim());
		boolean locVaiTro = cboLocVaiTro.getSelectedIndex() > 0;
		boolean locTrangThai = cboLocTrangThai.getSelectedIndex() > 0;
		String vaiTro = (String) cboLocVaiTro.getSelectedItem();
		String trangThai = (String) cboLocTrangThai.getSelectedItem();

		dsHienThi.clear();
		for (NhanVienRow nv : dsNhanVien) {
			if (!tuKhoa.isEmpty() && !(boDau(nv.ma).contains(tuKhoa) || boDau(nv.hoTen).contains(tuKhoa)
					|| nv.sdt.contains(tuKhoa))) {
				continue;
			}
			if (locVaiTro && !nv.vaiTro.equals(vaiTro)) {
				continue;
			}
			if (locTrangThai && !nv.trangThai.equals(trangThai)) {
				continue;
			}
			dsHienThi.add(nv);
		}

		dangDongBo = true;
		model.setRowCount(0);
		for (NhanVienRow nv : dsHienThi) {
			model.addRow(new Object[] { nv.ma, nv.hoTen, nv.sdt, nv.vaiTro, nv.trangThai, TXT_THAO_TAC });
		}
		dangDongBo = false;

		lblHienThi.setText("Hiển thị " + dsHienThi.size() + " / " + dsNhanVien.size() + " bản ghi");
		capNhatThongKe();
		capNhatChieuCaoBang();
		if (dangChon != null) {
			chonDong(dangChon);
		}
	}

	private void capNhatThongKe() {
		long dangLam = dsNhanVien.stream().filter(nv -> "Đang làm việc".equals(nv.trangThai)).count();
		cardTraCuu.setNote(dsNhanVien.size() + " nhân viên • " + dangLam + " đang làm việc");
	}

	private void capNhatChieuCaoBang() {
		int soDong = Math.max(1, Math.min(dsHienThi.size(), 8));
		spBang.setPreferredSize(new Dimension(100, 40 + soDong * tblNhanVien.getRowHeight()));
		cardDanhSach.revalidate();
		cardDanhSach.repaint();
	}

	private void chonDong(NhanVienRow nv) {
		int i = dsHienThi.indexOf(nv);
		dangDongBo = true;
		if (i >= 0) {
			tblNhanVien.setRowSelectionInterval(i, i);
			tblNhanVien.scrollRectToVisible(tblNhanVien.getCellRect(i, 0, true));
		} else {
			tblNhanVien.clearSelection();
		}
		dangDongBo = false;
	}

	/** Bỏ dấu tiếng Việt + chữ thường để tìm kiếm không phân biệt dấu. */
	private static String boDau(String s) {
		String t = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
		return t.replace('đ', 'd').replace('Đ', 'D').toLowerCase(Locale.ROOT);
	}

	// =====================================================================
	// XỬ LÝ FORM
	// =====================================================================
	private void datCheDoSua(NhanVienRow nv) {
		dangThem = false;
		dangChon = nv;
		if (nv == null) {
			xoaTrangForm();
			return;
		}
		txtMa.setText(nv.ma);
		txtHoTen.setText(nv.hoTen);
		txtSdt.setText(nv.sdt);
		cboVaiTro.setSelectedItem(nv.vaiTro);
		cboTrangThai.setSelectedItem(nv.trangThai);
		txtEmail.setText(nv.email == null ? "" : nv.email);
		txtNgaySinh.setText(nv.ngaySinh == null ? "" : nv.ngaySinh.format(FMT_NGAY));
		cboGioiTinh.setSelectedItem(nv.gioiTinh);
		txtNgayVaoLam.setText(nv.ngayVaoLam == null ? "" : nv.ngayVaoLam.format(FMT_NGAY));

		cardCapNhat.setTitle("Cập nhật nhân viên");
		cardCapNhat.setNote("Đang chọn: " + nv.ma + " • " + nv.hoTen);
		btnDatLaiMk.setEnabled(true);
	}

	private void datCheDoThem() {
		dangDongBo = true;
		tblNhanVien.clearSelection();
		dangDongBo = false;

		dangThem = true;
		dangChon = null;
		xoaTrangForm();
		txtMa.setText(taoMaMoi());
		cboTrangThai.setSelectedIndex(0);
		txtNgayVaoLam.setText(LocalDate.now().format(FMT_NGAY));

		cardCapNhat.setTitle("Thêm nhân viên mới");
		cardCapNhat.setNote("Đang thêm mới");
		btnDatLaiMk.setEnabled(false);
		cuonDenForm();
		txtHoTen.requestFocusInWindow();
	}

	private void xoaTrangForm() {
		txtMa.setText("");
		txtHoTen.setText("");
		txtSdt.setText("");
		txtEmail.setText("");
		txtNgaySinh.setText("");
		txtNgayVaoLam.setText("");
		cboVaiTro.setSelectedIndex(0);
		cboTrangThai.setSelectedIndex(0);
		cboGioiTinh.setSelectedIndex(0);
		cardCapNhat.setTitle("Cập nhật nhân viên");
		cardCapNhat.setNote("Chưa chọn nhân viên");
		btnDatLaiMk.setEnabled(false);
	}

	private void cuonDenForm() {
		SwingUtilities.invokeLater(() -> cardCapNhat
				.scrollRectToVisible(new Rectangle(0, 0, cardCapNhat.getWidth(), cardCapNhat.getHeight())));
	}

	private String taoMaMoi() {
		int max = 0;
		for (NhanVienRow nv : dsNhanVien) {
			try {
				max = Math.max(max, Integer.parseInt(nv.ma.substring(3)));
			} catch (RuntimeException ex) {
				// bỏ qua mã không đúng định dạng NV-xxx
			}
		}
		return String.format("NV-%03d", max + 1);
	}

	private void luu() {
		if (!dangThem && dangChon == null) {
			JOptionPane.showMessageDialog(this, "Hãy chọn nhân viên trong danh sách hoặc bấm \"Thêm nhân viên\".",
					"Thông báo", JOptionPane.WARNING_MESSAGE);
			return;
		}

		String hoTen = txtHoTen.getText().trim();
		String sdt = txtSdt.getText().trim();
		String email = txtEmail.getText().trim();

		if (hoTen.isEmpty()) {
			baoLoi("Vui lòng nhập họ tên!", txtHoTen);
			return;
		}
		if (!sdt.matches("0\\d{9}")) {
			baoLoi("Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0!", txtSdt);
			return;
		}
		for (NhanVienRow nv : dsNhanVien) {
			if (nv != dangChon && nv.sdt.equals(sdt)) {
				baoLoi("Số điện thoại đã tồn tại (" + nv.ma + " - " + nv.hoTen + ")!", txtSdt);
				return;
			}
		}
		if (!email.isEmpty() && !email.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
			baoLoi("Email không hợp lệ!", txtEmail);
			return;
		}
		LocalDate ngaySinh = parseNgay(txtNgaySinh.getText().trim());
		if (ngaySinh == null) {
			baoLoi("Ngày sinh không hợp lệ (định dạng dd/MM/yyyy)!", txtNgaySinh);
			return;
		}
		if (!ngaySinh.isBefore(LocalDate.now())) {
			baoLoi("Ngày sinh phải trước ngày hiện tại!", txtNgaySinh);
			return;
		}
		LocalDate ngayVao = parseNgay(txtNgayVaoLam.getText().trim());
		if (ngayVao == null) {
			baoLoi("Ngày vào làm không hợp lệ (định dạng dd/MM/yyyy)!", txtNgayVaoLam);
			return;
		}
		if (!ngayVao.isAfter(ngaySinh)) {
			baoLoi("Ngày vào làm phải sau ngày sinh!", txtNgayVaoLam);
			return;
		}

		NhanVienRow nv;
		if (dangThem) {
			nv = new NhanVienRow();
			nv.ma = txtMa.getText();
			dsNhanVien.add(nv);
		} else {
			nv = dangChon;
		}
		nv.hoTen = hoTen;
		nv.sdt = sdt;
		nv.vaiTro = (String) cboVaiTro.getSelectedItem();
		nv.trangThai = (String) cboTrangThai.getSelectedItem();
		nv.email = email;
		nv.ngaySinh = ngaySinh;
		nv.gioiTinh = (String) cboGioiTinh.getSelectedItem();
		nv.ngayVaoLam = ngayVao;
		// TODO: lưu / cập nhật nv xuống CSDL (NhanVienDAO)

		dangChon = nv;
		locDuLieu();
		chonDong(nv);
		datCheDoSua(nv);
		JOptionPane.showMessageDialog(this, "Đã lưu thông tin nhân viên " + nv.ma + ".", "Thông báo",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void huyThayDoi() {
		if (dangThem) {
			if (!dsHienThi.isEmpty()) {
				chonDong(dsHienThi.get(0));
				datCheDoSua(dsHienThi.get(0));
			} else {
				dangThem = false;
				xoaTrangForm();
			}
		} else if (dangChon != null) {
			datCheDoSua(dangChon); // nạp lại dữ liệu gốc
		}
	}

	private void datLaiMatKhau() {
		if (dangChon == null) {
			return;
		}
		int c = JOptionPane.showConfirmDialog(this,
				"Đặt lại mật khẩu của " + dangChon.hoTen + " (" + dangChon.ma + ") về mặc định?",
				"Đặt lại mật khẩu", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (c == JOptionPane.YES_OPTION) {
			// TODO: cập nhật mật khẩu mặc định cho tài khoản của nhân viên
			JOptionPane.showMessageDialog(this, "Đã đặt lại mật khẩu.", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void xem(NhanVienRow nv) {
		String ns = nv.ngaySinh == null ? "" : nv.ngaySinh.format(FMT_NGAY);
		String nvl = nv.ngayVaoLam == null ? "" : nv.ngayVaoLam.format(FMT_NGAY);
		JOptionPane.showMessageDialog(this,
				"Mã nhân viên: " + nv.ma + "\nHọ tên: " + nv.hoTen + "\nSố điện thoại: " + nv.sdt + "\nVai trò: "
						+ nv.vaiTro + "\nTrạng thái: " + nv.trangThai + "\nEmail: " + nv.email + "\nNgày sinh: " + ns
						+ "\nGiới tính: " + nv.gioiTinh + "\nNgày vào làm: " + nvl,
				"Thông tin nhân viên", JOptionPane.INFORMATION_MESSAGE);
	}

	private void xoa(NhanVienRow nv) {
		int c = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa nhân viên " + nv.hoTen + " (" + nv.ma + ")?",
				"Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (c != JOptionPane.YES_OPTION) {
			return;
		}
		// TODO: xóa (hoặc chuyển trạng thái) nhân viên trong CSDL
		dsNhanVien.remove(nv);
		if (dangChon == nv) {
			dangChon = null;
		}
		locDuLieu();
		if (!dsHienThi.isEmpty()) {
			chonDong(dsHienThi.get(0));
			datCheDoSua(dsHienThi.get(0));
		} else {
			xoaTrangForm();
		}
	}

	private void baoLoi(String msg, Component focus) {
		JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.WARNING_MESSAGE);
		focus.requestFocusInWindow();
	}

	private static LocalDate parseNgay(String s) {
		try {
			return LocalDate.parse(s, FMT_NGAY);
		} catch (DateTimeParseException ex) {
			return null;
		}
	}

	// =====================================================================
	// RENDERER CHO BẢNG
	// =====================================================================
	private static Color mauNen(boolean chon, int row) {
		return chon ? ROW_SELECTED : (row % 2 == 1 ? ROW_ALT : Color.WHITE);
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

	private static class ThaoTacRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
			super.getTableCellRendererComponent(t, TXT_THAO_TAC, false, false, r, c);
			setOpaque(true);
			setBackground(mauNen(sel, r));
			setForeground(PRIMARY);
			setFont(FONT_BOLD);
			setBorder(O_BANG);
			return this;
		}
	}

	/** Nhãn trạng thái dạng "huy hiệu" bo tròn. */
	private static class Badge extends JLabel {
		private static final long serialVersionUID = 1L;
		private Color nen = OK_BG;

		Badge() {
			setFont(FONT_SMALL_BOLD);
			setBorder(new EmptyBorder(4, 10, 4, 10));
			setOpaque(false);
		}

		void datTrangThai(String t) {
			boolean ok = "Đang làm việc".equals(t);
			setText(t);
			nen = ok ? OK_BG : ERR_BG;
			setForeground(ok ? OK_FG : ERR_FG);
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