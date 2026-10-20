package gui;

import static gui.UITheme.*;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
 * Khung chính của hệ thống AutoCar PV: sidebar (menu) + vùng nội dung dùng CardLayout.
 * Bấm nút menu nào thì hiển thị "card" tương ứng.
 */
public class TrangChuGUI extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	// ====== Khóa của từng card trong CardLayout ======
	public static final String KEY_TRANG_CHU = "trangchu";
	public static final String KEY_TIEP_NHAN = "tiepnhan";
	public static final String KEY_SUA_CHUA = "suachua";
	public static final String KEY_HOA_DON = "hoadon";
	public static final String KEY_PHU_TUNG = "phutung";
	public static final String KEY_DICH_VU = "dichvu";
	public static final String KEY_KHACH_HANG = "khachhang";
	public static final String KEY_NHAN_VIEN = "nhanvien";
	public static final String KEY_BAO_CAO = "baocao";
	public static final String KEY_CA_NHAN = "canhan";

	private final CardLayout cardLayout = new CardLayout();
	private final JPanel content = new JPanel(cardLayout);

	/** Nút menu theo khóa card (giữ thứ tự thêm vào). */
	private final Map<String, SidebarButton> dsNut = new LinkedHashMap<>();
	/** Các khóa của 9 mục menu chính (dùng cho phím tắt Ctrl+1..9). */
	private final List<String> dsKhoaChinh = new ArrayList<>();

	private SidebarButton btnDangXuat;
	private NhanVienGUI nhanVienGUI;

	/**
	 * @param hoTen  họ tên người đăng nhập (hiện ở thẻ người dùng trên sidebar)
	 * @param vaiTro vai trò người đăng nhập
	 *               (có thể đổi thành tham số NhanVien của project nếu bạn đã có entity)
	 */
	public TrangChuGUI(String hoTen, String vaiTro) {
		setTitle("AutoCar PV - Hệ thống quản lý gara ô tô");
		setMinimumSize(new Dimension(1100, 700));
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		JPanel p = new JPanel(new BorderLayout());
		add(p);

		p.add(taoSidebar(hoTen, vaiTro), BorderLayout.WEST);
		p.add(taoNoiDung(), BorderLayout.CENTER);

		installShortcuts();
		hienThi(KEY_TRANG_CHU);
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
		lblTen.setFont(FONT_BOLD);
		lblTen.setForeground(TEXT_WHITE);
		JLabel lblVaiTro = new JLabel(vaiTro);
		lblVaiTro.setFont(FONT_SMALL);
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

		// --- Các nút menu chính ---
		themNutMenu(sidebar, KEY_TRANG_CHU, "Trang Chủ", IMG_TRANG_CHU);
		themNutMenu(sidebar, KEY_TIEP_NHAN, "Tiếp Nhận Xe", IMG_TIEP_NHAN);
		themNutMenu(sidebar, KEY_SUA_CHUA, "Lập Phiếu Sửa Chữa", IMG_SUA_CHUA);
		themNutMenu(sidebar, KEY_HOA_DON, "Hóa Đơn & Thanh Toán", IMG_HOA_DON);
		themNutMenu(sidebar, KEY_PHU_TUNG, "Quản Lý Phụ Tùng", IMG_PHU_TUNG);
		themNutMenu(sidebar, KEY_DICH_VU, "Quản Lý Dịch Vụ", IMG_DICH_VU);
		themNutMenu(sidebar, KEY_KHACH_HANG, "Khách Hàng & Xe", IMG_KHACH_HANG);
		themNutMenu(sidebar, KEY_NHAN_VIEN, "Nhân Viên", IMG_NHAN_VIEN);
		themNutMenu(sidebar, KEY_BAO_CAO, "Báo Cáo Thống Kê", IMG_BAO_CAO);

		sidebar.add(Box.createVerticalGlue());

		// --- Nhóm cuối sidebar ---
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

	private void themNutMenu(JPanel sidebar, String key, String text, String iconPath) {
		SidebarButton btn = createSidebarButton(text, iconPath);
		btn.addActionListener(e -> hienThi(key));
		dsNut.put(key, btn);
		dsKhoaChinh.add(key);
		sidebar.add(btn);
		sidebar.add(Box.createVerticalStrut(4));
	}

	// =====================================================================
	// NỘI DUNG (CARD LAYOUT)
	// =====================================================================
	private JPanel taoNoiDung() {
		content.setBackground(BG);

		nhanVienGUI = new NhanVienGUI(() -> hienThi(KEY_TRANG_CHU));

		content.add(taoPanelTam("Trang chủ", "Thống kê tổng quan sẽ hiển thị tại đây."), KEY_TRANG_CHU);
		content.add(taoPanelTam("Tiếp nhận xe", "Gắn màn hình Lập phiếu tiếp nhận (dạng JPanel) vào đây."), KEY_TIEP_NHAN);
		content.add(taoPanelTam("Lập phiếu sửa chữa", "Màn hình đang được xây dựng."), KEY_SUA_CHUA);
		content.add(taoPanelTam("Hóa đơn & thanh toán", "Màn hình đang được xây dựng."), KEY_HOA_DON);
		content.add(taoPanelTam("Quản lý phụ tùng", "Màn hình đang được xây dựng."), KEY_PHU_TUNG);
		content.add(taoPanelTam("Quản lý dịch vụ", "Màn hình đang được xây dựng."), KEY_DICH_VU);
		content.add(taoPanelTam("Khách hàng & xe", "Màn hình đang được xây dựng."), KEY_KHACH_HANG);
		content.add(nhanVienGUI, KEY_NHAN_VIEN);
		content.add(taoPanelTam("Báo cáo thống kê", "Màn hình đang được xây dựng."), KEY_BAO_CAO);
		content.add(taoPanelTam("Thông tin cá nhân", "Màn hình đang được xây dựng."), KEY_CA_NHAN);

		return content;
	}

	/** Panel tạm cho những màn hình chưa làm: thay bằng panel thật khi có. */
	private JPanel taoPanelTam(String tieuDe, String moTa) {
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
		if (KEY_NHAN_VIEN.equals(key)) {
			nhanVienGUI.refresh();
		}
		// TODO: gọi refresh() cho các panel khác khi chúng có dữ liệu cần tải lại
		cardLayout.show(content, key);
		for (Map.Entry<String, SidebarButton> en : dsNut.entrySet()) {
			en.getValue().setActive(en.getKey().equals(key));
		}
	}

	// =====================================================================
	// PHÍM TẮT
	// =====================================================================
	private void installShortcuts() {
		InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = getRootPane().getActionMap();

		// Ctrl+1 ... Ctrl+9 cho 9 mục menu chính
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
	// CHẠY THỬ
	// =====================================================================
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(() -> new TrangChuGUI("Nguyễn Văn A", "Quản lý").setVisible(true));
	}
}