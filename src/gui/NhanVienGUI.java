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
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.SQLException;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
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

import business.NhanVienBUS;
import connectDB.ConnectDB;
import dao.TaiKhoanDAO;
import dao.VaiTroDAO;
import entity.NhanVien;
import entity.TaiKhoan;
import entity.VaiTro;

public class NhanVienGUI extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final DateTimeFormatter FMT_NGAY = DateTimeFormatter.ofPattern("dd/MM/uuuu")
			.withResolverStyle(ResolverStyle.STRICT);

	private static final String[] VAI_TRO = { "Quản lý", "Tiếp nhận viên", "Kỹ thuật viên", "Thu ngân" };
	private static final String[] TRANG_THAI = { "Đang làm việc", "Tạm nghỉ", "Đã nghỉ việc" };
	private static final String[] GIOI_TINH = { "Nam", "Nữ", "Khác" };
	private static final String TXT_THAO_TAC = "[i] Xem · [i] Sửa · [i] Đổi mật khẩu";
	private static final String TXT_THAO_TAC_XEM = "[i] Xem";
	private static final String TXT_THAO_TAC_SUA = "[i] Sửa";
	private static final String TXT_THAO_MAT_KHAU = "[i] Đổi mật khẩu";
	private static final int COL_THAO_TAC = 5;
	private static final int PAD_TRAI = 16;
	private static final Map<String, String> MA_VAI_TRO = new HashMap<>();
	static {
		MA_VAI_TRO.put("Quản lý", "QUANLY");
		MA_VAI_TRO.put("Tiếp nhận viên", "TIEPNHANVIEN");
		MA_VAI_TRO.put("Kỹ thuật viên", "KYTHUATVIEN");
		MA_VAI_TRO.put("Thu ngân", "THUNGAN");
	}

	private static final Border O_BANG = new CompoundBorder(new MatteBorder(0, 0, 1, 0, new Color(236, 240, 245)),
			new EmptyBorder(0, PAD_TRAI, 0, 8));

	private static class NhanVienPopupForm {
		final JPanel panel;
		final Input txtMaNV;
		final Input txtHoTenNV;
		final Input txtNoiSinh;
		final Input txtNgaySinh;
		final JComboBox<String> cboGioiTinh;
		final Input txtCCCD;
		final Input txtSdt;
		final Input txtDiaChi;
		final Input txtEmail;
		final JComboBox<String> cboTrangThai;
		final JComboBox<String> cboVaiTro;

		NhanVienPopupForm(JPanel panel, Input txtMaNV, Input txtHoTenNV, Input txtNoiSinh, Input txtNgaySinh,
				JComboBox<String> cboGioiTinh, Input txtCCCD, Input txtSdt, Input txtDiaChi, Input txtEmail,
				JComboBox<String> cboTrangThai, JComboBox<String> cboVaiTro) {
			this.panel = panel;
			this.txtMaNV = txtMaNV;
			this.txtHoTenNV = txtHoTenNV;
			this.txtNoiSinh = txtNoiSinh;
			this.txtNgaySinh = txtNgaySinh;
			this.cboGioiTinh = cboGioiTinh;
			this.txtCCCD = txtCCCD;
			this.txtSdt = txtSdt;
			this.txtDiaChi = txtDiaChi;
			this.txtEmail = txtEmail;
			this.cboTrangThai = cboTrangThai;
			this.cboVaiTro = cboVaiTro;
		}
	}

	private final Runnable quayVeTrangChu;
	private final ArrayList<NhanVien> dsNhanVien = new ArrayList<>();
	private final ArrayList<NhanVien> dsHienThi = new ArrayList<>();
	private final Map<String, String> vaiTroTheoMaNV = new HashMap<>();

	private CardPanel cardTraCuu;
	private Input txtTimKiem;
	private JComboBox<String> cboLocVaiTro;
	private JComboBox<String> cboLocTrangThai;

	private CardPanel cardDanhSach;
	private JTable tblNhanVien;
	private DefaultTableModel model;
	private JScrollPane spBang;
	private JLabel lblHienThi;

	private NhanVien dangChon;
	private boolean dangDongBo = false;

	private NhanVienBUS nvBus;
	private TaiKhoanDAO tkDao;
	private VaiTroDAO vtDao;

	public NhanVienGUI(Runnable quayVeTrangChu) {
		this.quayVeTrangChu = quayVeTrangChu;
		khoiTaoDuLieu();

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
		trang.add(cacThe, BorderLayout.CENTER);

		JScrollPane sp = new JScrollPane(trang, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setBorder(null);
		sp.getVerticalScrollBar().setUnitIncrement(16);
		add(sp, BorderLayout.CENTER);

		locDuLieu();
	}

	private void khoiTaoDuLieu() {
		try {
			ConnectDB.getInstance().connect();
			nvBus = new NhanVienBUS();
			tkDao = new TaiKhoanDAO();
			vtDao = new VaiTroDAO();
			napDuLieuTuCSDL();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Không thể kết nối CSDL để tải danh sách nhân viên.", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void napDuLieuTuCSDL() {
		if (nvBus == null || tkDao == null || vtDao == null) {
			return;
		}
		try {
			dsNhanVien.clear();
			dsNhanVien.addAll(nvBus.getDSNV());
			napVaiTroTheoNhanVien();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Không thể tải dữ liệu nhân viên từ CSDL.", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void napVaiTroTheoNhanVien() throws SQLException {
		vaiTroTheoMaNV.clear();
		for (TaiKhoan tk : tkDao.getdsTK()) {
			if (tk.getTenDangNhap() == null || tk.getVaiTro() == null) {
				continue;
			}
			String maNV = tk.getTenDangNhap().getMaNV();
			String maVaiTro = tk.getVaiTro().getMaVaiTro();
			if (maNV == null || maVaiTro == null) {
				continue;
			}
			VaiTro vt = vtDao.getVaiTroTheoMa(maVaiTro);
			if (vt != null && vt.getTenVaiTro() != null && !vt.getTenVaiTro().trim().isEmpty()) {
				vaiTroTheoMaNV.put(maNV, vt.getTenVaiTro());
			}
		}
	}

	private static Component khoangCach(int h) {
		return Box.createVerticalStrut(h);
	}

	public void refresh() {
		napDuLieuTuCSDL();
		locDuLieu();
	}

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
		btnThem.addActionListener(e -> {
			try {
				moPopupNhanVien(null);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

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

	private CardPanel taoTheDanhSach() {
		cardDanhSach = new CardPanel("Danh sách nhân viên", "Chọn Sửa để cập nhật hồ sơ trong popup");
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

		int[] rong = { 110, 200, 170, 150, 150, 210 };
		for (int i = 0; i < rong.length; i++) {
			tblNhanVien.getColumnModel().getColumn(i).setPreferredWidth(rong[i]);
		}
		tblNhanVien.getColumnModel().getColumn(0).setCellRenderer(new CellRenderer(true));
		tblNhanVien.getColumnModel().getColumn(1).setCellRenderer(new CellRenderer(false));
		tblNhanVien.getColumnModel().getColumn(2).setCellRenderer(new CellRenderer(false));
		tblNhanVien.getColumnModel().getColumn(3).setCellRenderer(new CellRenderer(false));
		tblNhanVien.getColumnModel().getColumn(4).setCellRenderer(new BadgeRenderer());
		tblNhanVien.getColumnModel().getColumn(COL_THAO_TAC).setCellRenderer(new ThaoTacRenderer());

		tblNhanVien.getSelectionModel().addListSelectionListener(e -> {
			if (e.getValueIsAdjusting() || dangDongBo) {
				return;
			}
			int r = tblNhanVien.getSelectedRow();
			if (r >= 0 && r < dsHienThi.size()) {
				dangChon = dsHienThi.get(r);
			}
		});

		tblNhanVien.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = tblNhanVien.rowAtPoint(e.getPoint());
				if (row < 0 || row >= dsHienThi.size()) {
					return;
				}
				NhanVien nv = dsHienThi.get(row);
				dangChon = nv;
				switch (vungThaoTac(e)) {
				case 1:
					xem(nv);
					break;
				case 2:
					try {
						moPopupNhanVien(nv);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
				case 3:
					doiMatKhau(nv);
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

	private int vungThaoTac(MouseEvent e) {
		int row = tblNhanVien.rowAtPoint(e.getPoint());
		int col = tblNhanVien.columnAtPoint(e.getPoint());
		if (row < 0 || col != COL_THAO_TAC) {
			return 0;
		}
		int x = e.getX() - tblNhanVien.getCellRect(row, col, false).x - PAD_TRAI;
		FontMetrics fm = tblNhanVien.getFontMetrics(FONT_BOLD);
		int wXem = fm.stringWidth(TXT_THAO_TAC_XEM);
		int wSua = fm.stringWidth(TXT_THAO_TAC_SUA);
		int wDoiMatKhau = fm.stringWidth(TXT_THAO_MAT_KHAU);
		int wSep = fm.stringWidth(" · ");

		if (x >= 0 && x <= wXem) {
			return 1;
		}
		int s = wXem + wSep;
		if (x >= s && x <= s + wSua) {
			return 2;
		}
		s += wSua + wSep;
		if (x >= s && x <= s + wDoiMatKhau) {
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

	private void moPopupNhanVien(NhanVien nv) throws SQLException {
		boolean dangThem = nv == null;
		NhanVienPopupForm form = taoFormNhanVienPopup();
		if (dangThem) {
			form.txtMaNV.setText(taoMaMoi());
			form.cboTrangThai.setSelectedIndex(0);
			form.cboGioiTinh.setSelectedIndex(0);
			form.cboVaiTro.setSelectedIndex(0);
		} else {
			doFormFromEntity(form, nv);
		}

		String title = dangThem ? "Thêm nhân viên" : "Cập nhật nhân viên";
		while (true) {
			int c = JOptionPane.showConfirmDialog(this, form.panel, title, JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE);
			if (c != JOptionPane.OK_OPTION) {
				return;
			}
			if (!kiemTraFormPopup(form, nv)) {
				continue;
			}
			
			NhanVien save = taoEntityFromForm(form);
			String maVaiTro = MA_VAI_TRO.get(form.cboVaiTro.getSelectedItem());
			try {
				boolean thanhCong = dangThem ? nvBus.themNhanVien(save, maVaiTro)
						: nvBus.capNhatNhanVien(save, maVaiTro);
				if (!thanhCong) {
					baoLoi("Không thể lưu thông tin nhân viên.", form.txtHoTenNV);
					continue;
				}
				napDuLieuTuCSDL();
				dangChon = timNhanVienTheoMa(save.getMaNV());
				locDuLieu();
				chonDong(dangChon);
				JOptionPane.showMessageDialog(this, "Đã lưu thông tin nhân viên " + save.getMaNV() + ".", "Thông báo",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			} catch (SQLException ex) {
				JOptionPane.showMessageDialog(this, "Không thể lưu nhân viên và tài khoản: " + ex.getMessage(),
						"Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private NhanVienPopupForm taoFormNhanVienPopup() {
		Input txtMaNV = new Input(null, true);
		Input txtHoTenNV = new Input(null, false);
		Input txtNoiSinh = new Input(null, false);
		Input txtNgaySinh = new Input("dd/MM/yyyy", false);
		JComboBox<String> cboGioiTinh = createComboBox(GIOI_TINH);
		Input txtCCCD = new Input(null, false);
		Input txtSdt = new Input(null, false);
		Input txtDiaChi = new Input(null, false);
		Input txtEmail = new Input(null, false);
		JComboBox<String> cboTrangThai = createComboBox(TRANG_THAI);
		JComboBox<String> cboVaiTro = createComboBox(VAI_TRO);

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setOpaque(false);
		panel.setPreferredSize(new Dimension(760, 380));
		themO(panel, labeled("Mã nhân viên", txtMaNV), 0, 0);
		themO(panel, labeled("Họ tên nhân viên *", txtHoTenNV), 0, 1);
		themO(panel, labeled("Nơi sinh", txtNoiSinh), 0, 2);
		themO(panel, labeled("Ngày sinh *", txtNgaySinh), 1, 0);
		themO(panel, labeled("Giới tính", cboGioiTinh), 1, 1);
		themO(panel, labeled("CCCD *", txtCCCD), 1, 2);
		themO(panel, labeled("Số điện thoại *", txtSdt), 2, 0);
		themO(panel, labeled("Địa chỉ", txtDiaChi), 2, 1);
		themO(panel, labeled("Email", txtEmail), 2, 2);
		themO(panel, labeled("Trạng thái *", cboTrangThai), 3, 0);
		themO(panel, labeled("Vai trò", cboVaiTro), 3, 1);

		return new NhanVienPopupForm(panel, txtMaNV, txtHoTenNV, txtNoiSinh, txtNgaySinh, cboGioiTinh, txtCCCD,
				txtSdt, txtDiaChi, txtEmail, cboTrangThai, cboVaiTro);
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

	private void doFormFromEntity(NhanVienPopupForm form, NhanVien nv) {
		form.txtMaNV.setText(nv.getMaNV() == null ? "" : nv.getMaNV());
		form.txtHoTenNV.setText(nv.getHoTenNV() == null ? "" : nv.getHoTenNV());
		form.txtNoiSinh.setText(nv.getNoiSinh() == null ? "" : nv.getNoiSinh());
		form.txtNgaySinh.setText(nv.getNgaySinh() == null ? "" : nv.getNgaySinh().format(FMT_NGAY));
		form.cboGioiTinh.setSelectedItem(nv.getGioiTinh() == null ? GIOI_TINH[0] : nv.getGioiTinh());
		form.txtCCCD.setText(nv.getCccd() == null ? "" : nv.getCccd());
		form.txtSdt.setText(nv.getSdt() == null ? "" : nv.getSdt());
		form.txtDiaChi.setText(nv.getDiaChi() == null ? "" : nv.getDiaChi());
		form.txtEmail.setText(nv.getEmail() == null ? "" : nv.getEmail());
		form.cboTrangThai.setSelectedItem(nv.getTrangThai() == null ? TRANG_THAI[0] : nv.getTrangThai());
		String vaiTro = layVaiTroHienThi(nv);
		form.cboVaiTro.setSelectedItem("-".equals(vaiTro) ? VAI_TRO[0] : vaiTro);
	}

	private NhanVien taoEntityFromForm(NhanVienPopupForm form) {
		return new NhanVien(form.txtMaNV.getText().trim(), form.txtHoTenNV.getText().trim(),
				form.txtNoiSinh.getText().trim(), parseNgay(form.txtNgaySinh.getText().trim()),
				(String) form.cboGioiTinh.getSelectedItem(), form.txtCCCD.getText().trim(),
				form.txtSdt.getText().trim(), form.txtDiaChi.getText().trim(), form.txtEmail.getText().trim(),
				(String) form.cboTrangThai.getSelectedItem());
	}

	private boolean kiemTraFormPopup(NhanVienPopupForm form, NhanVien nvDangSua) {
		if (form.txtMaNV.getText().trim().isEmpty()) {
			baoLoi("Mã nhân viên không được để trống.", form.txtMaNV);
			return false;
		}
		if (form.txtHoTenNV.getText().trim().isEmpty()) {
			baoLoi("Vui lòng nhập họ tên nhân viên.", form.txtHoTenNV);
			return false;
		}
		
		LocalDate ngaySinh = parseNgay(form.txtNgaySinh.getText().trim());
		if (ngaySinh == null) {
			baoLoi("Ngày sinh không hợp lệ (định dạng dd/MM/yyyy).", form.txtNgaySinh);
			return false;
		}
		if (!ngaySinh.isBefore(LocalDate.now())) {
			baoLoi("Ngày sinh phải trước ngày hiện tại.", form.txtNgaySinh);
			return false;
		}

		String cccd = form.txtCCCD.getText().trim();
		if (!cccd.matches("\\d{12}")) {
			baoLoi("CCCD phải gồm đúng 12 chữ số.", form.txtCCCD);
			return false;
		}

		String sdt = form.txtSdt.getText().trim();
		if (!sdt.matches("0\\d{9}")) {
			baoLoi("Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0.", form.txtSdt);
			return false;
		}
		
		String email = form.txtEmail.getText().trim();
		if (!email.isEmpty())
		if (!email.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
			baoLoi("Email không hợp lệ.", form.txtEmail);
			return false;
		}
		
		for (NhanVien nv : dsNhanVien) {
			if (nv != nvDangSua && sdt.equals(nv.getSdt())) {
				baoLoi("Số điện thoại đã tồn tại (" + nv.getMaNV() + " - " + nv.getHoTenNV() + ").", form.txtSdt);
				return false;
			}
		}

		return true;
	}

	private void locDuLieu() {
		String tuKhoa = boDau(txtTimKiem.getText().trim());
		boolean locVaiTro = cboLocVaiTro.getSelectedIndex() > 0;
		String vaiTro = (String) cboLocVaiTro.getSelectedItem();
		boolean locTrangThai = cboLocTrangThai.getSelectedIndex() > 0;
		String trangThai = (String) cboLocTrangThai.getSelectedItem();

		dsHienThi.clear();
		for (NhanVien nv : dsNhanVien) {
			String ma = boDau(nv.getMaNV());
			String ten = boDau(nv.getHoTenNV());
			String sdt = boDau(nv.getSdt());
			if (!tuKhoa.isEmpty() && !(ma.contains(tuKhoa) || ten.contains(tuKhoa) || sdt.contains(tuKhoa))) {
				continue;
			}
			if (locVaiTro && !vaiTro.equals(layVaiTroHienThi(nv))) {
				continue;
			}
			if (locTrangThai && !trangThai.equals(nv.getTrangThai())) {
				continue;
			}
			dsHienThi.add(nv);
		}

		dangDongBo = true;
		model.setRowCount(0);
		for (NhanVien nv : dsHienThi) {
			model.addRow(new Object[] { nv.getMaNV(), nv.getHoTenNV(), nv.getSdt(), layVaiTroHienThi(nv),
					nv.getTrangThai(), TXT_THAO_TAC });
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
		long dangLam = dsNhanVien.stream().filter(nv -> "Đang làm việc".equals(nv.getTrangThai())).count();
		cardTraCuu.setNote(dsNhanVien.size() + " nhân viên • " + dangLam + " đang làm việc");
	}

	private void capNhatChieuCaoBang() {
		int soDong = Math.max(1, Math.min(dsHienThi.size(), 8));
		spBang.setPreferredSize(new Dimension(100, 40 + soDong * tblNhanVien.getRowHeight()));
		cardDanhSach.revalidate();
		cardDanhSach.repaint();
	}

	private void chonDong(NhanVien nv) {
		if (nv == null) {
			tblNhanVien.clearSelection();
			return;
		}
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

	private static String boDau(String s) {
		if (s == null) {
			return "";
		}
		String t = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
		return t.replace('đ', 'd').replace('Đ', 'D').toLowerCase(Locale.ROOT);
	}

	private String taoMaMoi() {
		try {
			return nvBus.taoMaNVMoi();
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, "Không thể tạo mã nhân viên mới: " + ex.getMessage(), "Lỗi CSDL",
					JOptionPane.ERROR_MESSAGE);
			return "";
		}
	}

	private NhanVien timNhanVienTheoMa(String maNV) {
		for (NhanVien nhanVien : dsNhanVien) {
			if (maNV.equals(nhanVien.getMaNV())) {
				return nhanVien;
			}
		}
		return null;
	}

	private String layVaiTroHienThi(NhanVien nv) {
		if (nv == null || nv.getMaNV() == null) {
			return "-";
		}
		String vaiTro = vaiTroTheoMaNV.get(nv.getMaNV());
		return (vaiTro == null || vaiTro.trim().isEmpty()) ? "-" : vaiTro;
	}

	private void xem(NhanVien nv) {
		String ns = nv.getNgaySinh() == null ? "" : nv.getNgaySinh().format(FMT_NGAY);
		JOptionPane.showMessageDialog(this,
				"Mã nhân viên: " + giaTri(nv.getMaNV()) + "\nHọ tên: " + giaTri(nv.getHoTenNV()) + "\nNơi sinh: "
						+ giaTri(nv.getNoiSinh()) + "\nNgày sinh: " + ns + "\nGiới tính: " + giaTri(nv.getGioiTinh())
						+ "\nCCCD: " + giaTri(nv.getCccd()) + "\nSố điện thoại: " + giaTri(nv.getSdt())
						+ "\nĐịa chỉ: " + giaTri(nv.getDiaChi()) + "\nEmail: " + giaTri(nv.getEmail())
						+ "\nTrạng thái: " + giaTri(nv.getTrangThai()) + "\nVai trò: " + layVaiTroHienThi(nv),
				"Thông tin nhân viên", JOptionPane.INFORMATION_MESSAGE);
	}

	private static String giaTri(String s) {
		return s == null || s.trim().isEmpty() ? "(trống)" : s;
	}

	private void doiMatKhau(NhanVien nv) {
		JPasswordField txtMatKhauMoi = new JPasswordField();
		JPasswordField txtXacNhan = new JPasswordField();
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setPreferredSize(new Dimension(360, 110));
		themO(panel, labeled("Mật khẩu mới", txtMatKhauMoi), 0, 0);
		themO(panel, labeled("Xác nhận mật khẩu", txtXacNhan), 1, 0);

		int luaChon = JOptionPane.showConfirmDialog(this, panel,
				"Cập nhật mật khẩu - " + nv.getMaNV(), JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		if (luaChon != JOptionPane.OK_OPTION) {
			return;
		}

		String matKhauMoi = new String(txtMatKhauMoi.getPassword());
		String xacNhan = new String(txtXacNhan.getPassword());
		if (matKhauMoi.trim().isEmpty()) {
			baoLoi("Mật khẩu mới không được để trống.", txtMatKhauMoi);
			return;
		}
		if (!matKhauMoi.equals(xacNhan)) {
			baoLoi("Mật khẩu xác nhận không khớp.", txtXacNhan);
			return;
		}
		try {
			if (!nvBus.capNhatMatKhau(nv.getMaNV(), matKhauMoi)) {
				JOptionPane.showMessageDialog(this, "Không tìm thấy tài khoản của nhân viên.", "Thông báo",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			JOptionPane.showMessageDialog(this, "Đã cập nhật mật khẩu mới cho nhân viên " + nv.getHoTenNV()+ ".",
					"Thông báo", JOptionPane.INFORMATION_MESSAGE);
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, "Không thể cập nhật mật khẩu: " + ex.getMessage(), "Lỗi CSDL",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void baoLoi(String msg, Component focus) {
		JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.WARNING_MESSAGE);
		if (focus != null) {
			focus.requestFocusInWindow();
		}
	}

	private static LocalDate parseNgay(String s) {
		try {
			return LocalDate.parse(s, FMT_NGAY);
		} catch (DateTimeParseException ex) {
			return null;
		}
	}

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
