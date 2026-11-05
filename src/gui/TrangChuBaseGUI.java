package gui;

import static gui.UITheme.*;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Lớp cha của 4 màn hình trang chủ theo vai trò (Quản lý, Tiếp nhận viên, Kỹ thuật viên, Thu ngân).
 *
 * Lớp này lo phần dùng chung: sidebar (logo, thẻ người dùng, menu chính, Xem thông tin cá nhân,
 * Đăng xuất), vùng nội dung dùng CardLayout, phím tắt Ctrl+1..9 và đăng xuất.
 * Mỗi lớp con chỉ cần khai báo danh sách nút "Menu chính" của vai trò mình và đăng ký các card.
 *
 * Cách viết một lớp con:
 * <pre>
 * public MyGUI(String hoTen) {
 *     super(hoTen, "Tên vai trò", MENU);   // MENU là mảng MucMenu
 *     themCard(KEY_XXX, panelThat);         // chỉ cần đăng ký những màn hình đã làm xong
 *     hoanTat();                            // tự tạo panel tạm cho mục còn lại + hiện trang đầu
 * }
 * </pre>
 */
public abstract class TrangChuBaseGUI extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	// ====== Khóa của từng card trong CardLayout ======
	public static final String KEY_TRANG_CHU = "trangchu";
	public static final String KEY_BAO_CAO = "baocao";
	public static final String KEY_CA_NHAN = "canhan";

	// Quản lý
	public static final String KEY_PHU_TUNG = "phutung";
	public static final String KEY_DICH_VU = "dichvu";
	public static final String KEY_NHAN_VIEN = "nhanvien";
	public static final String KEY_KHUYEN_MAI = "khuyenmai";

	// Tiếp nhận viên
	public static final String KEY_KHACH_HANG = "khachhang";
	public static final String KEY_TIEP_NHAN = "tiepnhan";
	public static final String KEY_SUA_CHUA = "suachua";
	public static final String KEY_TRA_CUU_TIEP_NHAN = "tracuutiepnhan"; // màn hình con của "Tiếp nhận xe"

	// Kỹ thuật viên
	public static final String KEY_DS_CONG_VIEC = "dscongviec";
	public static final String KEY_CONG_VIEC_PHU_TRACH = "congviecphutrach";
	public static final String KEY_YEU_CAU_XUAT_KHO = "yeucauxuatkho";

	// Thu ngân
	public static final String KEY_THANH_TOAN = "thanhtoan";

	/** Mô tả một nút trong "Menu chính": khóa card, chữ hiển thị và đường dẫn icon. */
	public static class MucMenu {
		final String key;
		final String text;
		final String iconPath;

		/**
		 * @param key      khóa card sẽ hiển thị khi bấm nút
		 * @param text     chữ trên nút
		 * @param iconPath đường dẫn icon, ví dụ "/images/icon_trangChu.png" (để "" nếu chưa có)
		 */
		public MucMenu(String key, String text, String iconPath) {
			this.key = key;
			this.text = text;
			this.iconPath = iconPath;
		}
	}

	private final CardLayout cardLayout = new CardLayout();
	private final JPanel content = new JPanel(cardLayout);

	private final MucMenu[] menu;
	/** Nút menu theo khóa card (giữ thứ tự thêm vào). */
	private final Map<String, SidebarButton> dsNut = new LinkedHashMap<>();
	/** Các khóa của mục menu chính (dùng cho phím tắt Ctrl+1..9). */
	private final List<String> dsKhoaChinh = new ArrayList<>();
	/** Các card đã được đăng ký. */
	private final Set<String> dsCard = new HashSet<>();

	private SidebarButton btnDangXuat;

	/**
	 * @param hoTen  họ tên người đăng nhập (hiện ở thẻ người dùng trên sidebar)
	 * @param vaiTro vai trò (hiện ở thẻ người dùng và tiêu đề cửa sổ)
	 * @param menu   danh sách nút "Menu chính" của vai trò này
	 */
	protected TrangChuBaseGUI(String hoTen, String vaiTro, MucMenu[] menu) {
		this.menu = menu;

		setTitle("AutoCar PV - Hệ thống quản lý gara ô tô (" + vaiTro + ")");
		setMinimumSize(new Dimension(1100, 700));
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		JPanel p = new JPanel(new BorderLayout());
		add(p);

		content.setBackground(BG);
		p.add(taoSidebar(hoTen, vaiTro), BorderLayout.WEST);
		p.add(content, BorderLayout.CENTER);
	}

	// =====================================================================
	// API DÀNH CHO LỚP CON
	// =====================================================================

	/** Đăng ký một màn hình (JPanel) vào CardLayout với khóa tương ứng. */
	protected void themCard(String key, JPanel panel) {
		content.add(panel, key);
		dsCard.add(key);
	}

	/**
	 * Gọi ở cuối constructor của lớp con: tự tạo panel tạm cho những mục menu chưa có màn hình,
	 * cài phím tắt và hiển thị mục đầu tiên.
	 */
	protected void hoanTat() {
		for (MucMenu m : menu) {
			if (!dsCard.contains(m.key)) {
				String moTa = KEY_TRANG_CHU.equals(m.key) ? "Thống kê tổng quan sẽ hiển thị tại đây."
						: "Màn hình đang được xây dựng.";
				themCard(m.key, taoPanelTam(m.text, moTa));
			}
		}
		if (!dsCard.contains(KEY_CA_NHAN)) {
			themCard(KEY_CA_NHAN, taoPanelTam("Thông tin cá nhân", "Màn hình đang được xây dựng."));
		}
		installShortcuts();
		hienThi(menu[0].key);
	}

	/** Hook: được gọi ngay trước khi chuyển sang một card (dùng để refresh dữ liệu). */
	protected void onHienThi(String key) {
		// lớp con ghi đè nếu cần
	}

	/** Panel tạm cho những màn hình chưa làm: thay bằng panel thật khi có. */
	protected JPanel taoPanelTam(String tieuDe, String moTa) {
		JPanel p = new JPanel(new GridBagLayout());
		p.setBackground(BG);
		JPanel box = new JPanel();
		box.setOpaque(false);
		box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
		JLabel l1 = new JLabel(tieuDe);
		l1.setFont(FONT_TITLE);
		l1.setForeground(TITLE);
		l1.setAlignmentX(Component.CENTER_ALIGNMENT);
		JLabel l2 = new JLabel(moTa);
		l2.setFont(FONT_BODY);
		l2.setForeground(TEXT_MUTED);
		l2.setAlignmentX(Component.CENTER_ALIGNMENT);
		box.add(l1);
		box.add(Box.createVerticalStrut(6));
		box.add(l2);
		p.add(box);
		return p;
	}

	/** Chuyển sang card theo khóa và tô sáng nút menu tương ứng. */
	public void hienThi(String key) {
		hienThi(key, key);
	}

	/**
	 * Chuyển sang card {@code cardKey} nhưng tô sáng nút menu {@code menuKey}.
	 * Dùng cho các màn hình "con" không có nút riêng trên sidebar
	 * (ví dụ: màn hình Tra cứu phiếu tiếp nhận vẫn tô sáng nút "Tiếp Nhận Xe").
	 */
	public void hienThi(String cardKey, String menuKey) {
		onHienThi(cardKey);
		cardLayout.show(content, cardKey);
		for (Map.Entry<String, SidebarButton> en : dsNut.entrySet()) {
			en.getValue().setActive(en.getKey().equals(menuKey));
		}
	}

	// =====================================================================
	// SIDEBAR
	// =====================================================================
	private JPanel taoSidebar(String hoTen, String vaiTro) {
		JPanel sidebar = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				GradientPaint gp = new GradientPaint(0, 0, PRIMARY_DARK, 0, getHeight(), PRIMARY);
				g2.setPaint(gp);
				g2.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
		sidebar.setOpaque(false);
		sidebar.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 0));
		sidebar.setBorder(new EmptyBorder(0, 0, 20, 0));

		// --- Logo (VÙNG CHÈN ẢNH) ---
		JPanel pnlLogo = new JPanel(new GridBagLayout());
		pnlLogo.setBackground(PRIMARY_DARK);
		pnlLogo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
		pnlLogo.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 60));
		pnlLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
		pnlLogo.add(new ImageSlot(IMG_LOGO, 200, 36, true));
		sidebar.add(pnlLogo);
		sidebar.add(Box.createVerticalStrut(12));

		// --- Thẻ người dùng (avatar = VÙNG CHÈN ẢNH) ---
		RoundedPanel pnlUser = new RoundedPanel(12, new Color(255, 255, 255, 30));
		pnlUser.setLayout(new BorderLayout(10, 0));
		pnlUser.setBorder(new EmptyBorder(10, 12, 10, 12));
		Dimension dUser = new Dimension(SIDEBAR_BUTTON_WIDTH, 62);
		pnlUser.setPreferredSize(dUser);
		pnlUser.setMaximumSize(dUser);
		pnlUser.setAlignmentX(Component.CENTER_ALIGNMENT);

		JPanel avatarWrap = new JPanel(new GridBagLayout());
		avatarWrap.setOpaque(false);
		avatarWrap.add(new ImageSlot(IMG_AVATAR, 38, 38, true));
		pnlUser.add(avatarWrap, BorderLayout.WEST);

		JPanel thongTin = new JPanel();
		thongTin.setOpaque(false);
		thongTin.setLayout(new BoxLayout(thongTin, BoxLayout.Y_AXIS));
		JLabel lblTen = new JLabel(hoTen);
		lblTen.setFont(new Font("Arial", Font.BOLD, 10));
		lblTen.setForeground(TEXT_WHITE);
		JLabel lblVaiTro = new JLabel(vaiTro);
		lblVaiTro.setFont(new Font("Arial", Font.BOLD, 10));
		lblVaiTro.setForeground(TEXT_LIGHT);
		thongTin.add(Box.createVerticalGlue());
		thongTin.add(lblTen);
		thongTin.add(lblVaiTro);
		thongTin.add(Box.createVerticalGlue());
		pnlUser.add(thongTin, BorderLayout.CENTER);

		sidebar.add(pnlUser);
		sidebar.add(Box.createVerticalStrut(16));

		// --- Tiêu đề nhóm menu ---
		JPanel pnlNhan = new JPanel(new BorderLayout());
		pnlNhan.setOpaque(false);
		pnlNhan.setMaximumSize(new Dimension(SIDEBAR_BUTTON_WIDTH, 24));
		pnlNhan.setAlignmentX(Component.CENTER_ALIGNMENT);
		JLabel lblMenu = new JLabel("MENU CHÍNH");
		lblMenu.setFont(FONT_SMALL_BOLD);
		lblMenu.setForeground(TEXT_LIGHT);
		lblMenu.setBorder(new EmptyBorder(0, 16, 0, 0));
		pnlNhan.add(lblMenu, BorderLayout.CENTER);
		sidebar.add(pnlNhan);
		sidebar.add(Box.createVerticalStrut(6));

		// --- Các nút "Menu chính" tùy theo vai trò ---
		for (MucMenu m : menu) {
			themNutMenu(sidebar, m);
		}

		sidebar.add(Box.createVerticalGlue());

		// --- Nhóm cuối sidebar (dùng chung cho mọi vai trò) ---
		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		sep.setForeground(new Color(255, 255, 255, 40));
		sep.setMaximumSize(new Dimension(SIDEBAR_BUTTON_WIDTH, 1));
		sep.setAlignmentX(Component.CENTER_ALIGNMENT);
		sidebar.add(sep);
		sidebar.add(Box.createVerticalStrut(10));

		SidebarButton btnCaNhan = createSidebarButton("Xem thông tin cá nhân", IMG_CA_NHAN);
		dsNut.put(KEY_CA_NHAN, btnCaNhan);
		btnCaNhan.addActionListener(e -> hienThi(KEY_CA_NHAN));
		sidebar.add(btnCaNhan);
		sidebar.add(Box.createVerticalStrut(4));

		btnDangXuat = createSidebarDangerButton("Đăng xuất", IMG_DANG_XUAT);
		btnDangXuat.addActionListener(this);
		sidebar.add(btnDangXuat);

		return sidebar;
	}

	private void themNutMenu(JPanel sidebar, MucMenu m) {
		SidebarButton btn = createSidebarButton(m.text, m.iconPath);
		btn.addActionListener(e -> hienThi(m.key));
		dsNut.put(m.key, btn);
		dsKhoaChinh.add(m.key);
		sidebar.add(btn);
		sidebar.add(Box.createVerticalStrut(4));
	}

	// =====================================================================
	// PHÍM TẮT
	// =====================================================================
	private void installShortcuts() {
		InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = getRootPane().getActionMap();

		// Ctrl+1 ... Ctrl+9 cho các mục menu chính
		for (int i = 0; i < dsKhoaChinh.size() && i < 9; i++) {
			final SidebarButton btn = dsNut.get(dsKhoaChinh.get(i));
			final String tenAction = "menu" + (i + 1);
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_1 + i, InputEvent.CTRL_DOWN_MASK), tenAction);
			actionMap.put(tenAction, new AbstractAction() {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					btn.doClick();
				}
			});
			btn.setToolTipText(btn.getText() + " (Ctrl+" + (i + 1) + ")");
		}

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK),
				"logout");
		actionMap.put("logout", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				btnDangXuat.doClick();
			}
		});
		btnDangXuat.setToolTipText("Đăng xuất (Ctrl+Shift+Q)");
	}

	// =====================================================================
	// ĐĂNG XUẤT
	// =====================================================================
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnDangXuat) {
			int choice = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất không?",
					"Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

			if (choice == JOptionPane.YES_OPTION) {
				new DangNhapGUI().setVisible(true);
				this.dispose();
			}
		}
	}

	// =====================================================================
	// MỞ TRANG CHỦ THEO VAI TRÒ (gọi từ DangNhapGUI sau khi đăng nhập thành công)
	// =====================================================================
	/**
	 * Tạo đúng màn hình trang chủ theo vai trò. Chuỗi vai trò phải khớp với giá trị lưu
	 * trong CSDL: "Quản lý", "Tiếp nhận viên", "Kỹ thuật viên", "Thu ngân".
	 * @throws SQLException 
	 */
	public static TrangChuBaseGUI taoTheoVaiTro(String vaiTro, String hoTen) throws SQLException {
		switch (vaiTro) {
		case "Quản lý":
			return new TrangChuQuanLyGUI(hoTen);
		case "Tiếp nhận viên":
			return new TrangChuTiepNhanVienGUI(hoTen);
		case "Kỹ thuật viên":
			return new TrangChuKyThuatVienGUI(hoTen);
		case "Thu ngân":
			return new TrangChuThuNganGUI(hoTen);
		default:
			throw new IllegalArgumentException("Vai trò không hợp lệ: " + vaiTro);
		}
	}
}
