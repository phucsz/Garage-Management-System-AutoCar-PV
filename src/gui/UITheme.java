package gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Chứa bảng màu, font, đường dẫn ảnh và các component dùng chung cho toàn bộ giao diện.
 *
 * ẢNH: mọi vị trí chèn ảnh đều được vẽ bằng khung viền nét đứt. Chỉ cần đặt đúng file
 * vào thư mục {@link #IMG_DIR} (tính từ thư mục gốc của project), chương trình tự nạp ảnh.
 */
public final class UITheme {

	private UITheme() {
	}

	// =====================================================================
	// ĐƯỜNG DẪN ẢNH (sửa tên file cho khớp với ảnh của bạn)
	// =====================================================================
	public static final String IMG_DIR = "/images/";

	public static final String IMG_LOGO = IMG_DIR + "logo-3-primary.png"; // 200 x 36
	public static final String IMG_AVATAR = IMG_DIR + "user.png"; // 38 x 38

	public static final String IMG_TRANG_CHU = IMG_DIR + "icon_trangChu.png"; // 18 x 18
	public static final String IMG_TIEP_NHAN = IMG_DIR + "icon_tiepNhan.png";
	public static final String IMG_SUA_CHUA = IMG_DIR + "icon_suaChua.png";
	public static final String IMG_HOA_DON = IMG_DIR + "icon_hoaDon.png";
	public static final String IMG_PHU_TUNG = IMG_DIR + "icon_phuTung.png";
	public static final String IMG_DICH_VU = IMG_DIR + "icon_dichVu.png";
	public static final String IMG_KHACH_HANG = IMG_DIR + "icon_khachHang.png";
	public static final String IMG_NHAN_VIEN = IMG_DIR + "icon_nhanVien.png";
	public static final String IMG_BAO_CAO = IMG_DIR + "icon_baoCao.png";
	public static final String IMG_CA_NHAN = IMG_DIR + "icon_caNhan.png";
	public static final String IMG_DANG_XUAT = IMG_DIR + "icon_dangXuat.png";

	public static final String IMG_TIM_KIEM = IMG_DIR + "icon_timKiem.png"; // 16 x 16
	public static final String IMG_THEM = IMG_DIR + "icon_them.png";
	public static final String IMG_QUAY_LAI = IMG_DIR + "icon_quayLai.png";
	public static final String IMG_LUU = IMG_DIR + "icon_luu.png";

	// =====================================================================
	// KÍCH THƯỚC
	// =====================================================================
	public static final int SIDEBAR_WIDTH = 240;
	public static final int SIDEBAR_BUTTON_WIDTH = 220;
	public static final int SIDEBAR_BUTTON_HEIGHT = 42;

	// =====================================================================
	// MÀU SẮC
	// =====================================================================
	public static final Color PRIMARY = new Color(21, 101, 192);
	public static final Color PRIMARY_DARK = new Color(13, 71, 161);
	public static final Color PRIMARY_ACTIVE = new Color(30, 136, 229);
	public static final Color PRIMARY_LIGHT = new Color(225, 238, 252);

	public static final Color BG = new Color(240, 244, 250);
	public static final Color CARD_BORDER = new Color(226, 231, 238);
	public static final Color BORDER = new Color(206, 212, 220);
	public static final Color READONLY_BG = new Color(236, 240, 244);
	public static final Color ROW_ALT = new Color(247, 249, 252);
	public static final Color ROW_SELECTED = new Color(232, 241, 252);

	public static final Color TEXT_WHITE = Color.WHITE;
	public static final Color TEXT_LIGHT = new Color(200, 220, 245);
	public static final Color TEXT_DARK = new Color(33, 37, 41);
	public static final Color TEXT_MUTED = new Color(110, 120, 135);
	public static final Color TITLE = new Color(26, 35, 126);
	public static final Color DANGER_LIGHT = new Color(255, 138, 128);

	public static final Color OK_BG = new Color(230, 246, 233);
	public static final Color OK_FG = new Color(27, 127, 59);
	public static final Color ERR_BG = new Color(253, 232, 232);
	public static final Color ERR_FG = new Color(198, 40, 40);

	private static final Color DASH_LIGHT = new Color(255, 255, 255, 170);
	private static final Color DASH_GRAY = new Color(150, 155, 165);

	// =====================================================================
	// FONT
	// =====================================================================
	public static final String FONT_NAME = "Segoe UI";
	public static final Font FONT_TITLE = new Font(FONT_NAME, Font.BOLD, 24);
	public static final Font FONT_SECTION = new Font(FONT_NAME, Font.BOLD, 14);
	public static final Font FONT_BODY = new Font(FONT_NAME, Font.PLAIN, 13);
	public static final Font FONT_BOLD = new Font(FONT_NAME, Font.BOLD, 12);
	public static final Font FONT_LABEL = new Font(FONT_NAME, Font.PLAIN, 12);
	public static final Font FONT_SMALL = new Font(FONT_NAME, Font.PLAIN, 11);
	public static final Font FONT_SMALL_BOLD = new Font(FONT_NAME, Font.BOLD, 11);
	public static final Font FONT_SIDEBAR_BRAND = new Font(FONT_NAME, Font.BOLD, 16);
	public static final Font FONT_SIDEBAR_ITEM = new Font(FONT_NAME, Font.PLAIN, 13);

	// =====================================================================
	// HÀM TIỆN ÍCH
	// =====================================================================

	public static SidebarButton createSidebarButton(String text, String iconPath) {
		return new SidebarButton(text, iconPath, TEXT_WHITE);
	}

	public static SidebarButton createSidebarDangerButton(String text, String iconPath) {
		return new SidebarButton(text, iconPath, DANGER_LIGHT);
	}

	public static JButton createLinkButton(String text) {
		JButton b = new JButton("<html><u>" + text + "</u></html>");
		b.setFont(FONT_BOLD);
		b.setForeground(PRIMARY);
		b.setFocusPainted(false);
		b.setBorderPainted(false);
		b.setContentAreaFilled(false);
		b.setBorder(new EmptyBorder(0, 6, 0, 6));
		b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		return b;
	}

	public static JComboBox<String> createComboBox(String... items) {
		JComboBox<String> cbo = new JComboBox<>(items);
		cbo.setFont(FONT_BODY);
		cbo.setBackground(Color.WHITE);
		cbo.setForeground(TEXT_DARK);
		cbo.setFocusable(true);
		return cbo;
	}

	/** Ghép nhãn nhỏ phía trên với một component, ép chiều cao = h. */
	public static JPanel labeled(String text, JComponent comp, int h) {
		JPanel p = labeledFixed(text, comp);
		comp.setPreferredSize(new Dimension(100, h));
		comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, h));
		return p;
	}

	public static JPanel labeled(String text, JComponent comp) {
		return labeled(text, comp, 36);
	}

	/** Ghép nhãn nhỏ phía trên với một component, giữ nguyên kích thước (dùng cho nút). */
	public static JPanel labeledFixed(String text, JComponent comp) {
		JPanel p = new JPanel();
		p.setOpaque(false);
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		JLabel lbl = new JLabel(text);
		lbl.setFont(FONT_LABEL);
		lbl.setForeground(TEXT_MUTED);
		lbl.setBorder(new EmptyBorder(0, 2, 5, 0));
		lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
		comp.setAlignmentX(Component.LEFT_ALIGNMENT);
		p.add(lbl);
		p.add(comp);
		return p;
	}

	// =====================================================================
	// ẢNH: icon / vùng chèn ảnh (khung nét đứt nếu chưa có file)
	// =====================================================================

	/** Icon: vẽ ảnh nếu file tồn tại, ngược lại vẽ khung viền nét đứt. */
	public static class SlotIcon implements Icon {
		private static final Stroke NET_DUT = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f,
				new float[] { 3f, 3f }, 0f);
		private final int w, h;
		private final Color mauVien;
		private final Image anh;

		public SlotIcon(String duongDan, int w, int h, Color mauVien) {
		    this.w = w;
		    this.h = h;
		    this.mauVien = mauVien;
		    java.net.URL url = UITheme.class.getResource(duongDan);
		    this.anh = (url != null) ? new ImageIcon(url).getImage() : null;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2 = (Graphics2D) g.create();
			if (anh != null) {
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g2.drawImage(anh, x, y, w, h, null);
			} else {
				g2.setColor(mauVien);
				g2.setStroke(NET_DUT);
				g2.drawRect(x, y, w - 1, h - 1);
			}
			g2.dispose();
		}

		@Override
		public int getIconWidth() {
			return w;
		}

		@Override
		public int getIconHeight() {
			return h;
		}
	}

	/** Component độc lập dùng cho logo, avatar... */
	public static class ImageSlot extends JComponent {
		private static final long serialVersionUID = 1L;
		private final SlotIcon icon;

		public ImageSlot(String duongDan, int w, int h, boolean nenToi) {
			icon = new SlotIcon(duongDan, w, h, nenToi ? DASH_LIGHT : DASH_GRAY);
			Dimension d = new Dimension(w, h);
			setPreferredSize(d);
			setMinimumSize(d);
			setMaximumSize(d);
			setOpaque(false);
			setToolTipText(duongDan);
		}

		@Override
		protected void paintComponent(Graphics g) {
			icon.paintIcon(this, g, 0, 0);
		}
	}

	// =====================================================================
	// NÚT BẤM
	// =====================================================================

	/** Nút trên sidebar: icon bên trái + chữ, có trạng thái hover / đang chọn. */
	public static class SidebarButton extends JButton {
		private static final long serialVersionUID = 1L;
		private boolean active = false;
		private boolean hover = false;

		public SidebarButton(String text, String iconPath, Color fg) {
			super(text);
			setIcon(new SlotIcon(iconPath, 18, 18, DASH_LIGHT));
			setIconTextGap(12);
			setHorizontalAlignment(SwingConstants.LEFT);
			setFont(FONT_SIDEBAR_ITEM);
			setForeground(fg);
			setFocusPainted(false);
			setBorderPainted(false);
			setContentAreaFilled(false);
			setOpaque(false);
			setBorder(new EmptyBorder(0, 16, 0, 8));
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			Dimension d = new Dimension(SIDEBAR_BUTTON_WIDTH, SIDEBAR_BUTTON_HEIGHT);
			setPreferredSize(d);
			setMaximumSize(d);
			setMinimumSize(d);
			setAlignmentX(Component.CENTER_ALIGNMENT);
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

		public void setActive(boolean active) {
			this.active = active;
			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			if (active) {
				g2.setColor(PRIMARY_ACTIVE);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
			} else if (hover) {
				g2.setColor(new Color(255, 255, 255, 30));
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
			}
			g2.dispose();
			super.paintComponent(g);
		}
	}

	/** Nút hành động bo góc, 3 kiểu hiển thị, có thể kèm icon. */
	public static class ActionButton extends JButton {
		private static final long serialVersionUID = 1L;
		public static final int KIEU_CHINH = 0;
		public static final int KIEU_VIEN_XAM = 1;
		public static final int KIEU_VIEN_XANH = 2;

		private final int kieu;
		private boolean hover = false;

		public ActionButton(String text, int kieu) {
			this(text, kieu, null);
		}

		public ActionButton(String text, int kieu, String iconPath) {
			super(text);
			this.kieu = kieu;
			setFont(FONT_BOLD);
			setFocusPainted(false);
			setBorderPainted(false);
			setContentAreaFilled(false);
			setOpaque(false);
			setBorder(new EmptyBorder(0, 16, 0, 16));
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			setForeground(kieu == KIEU_CHINH ? Color.WHITE : (kieu == KIEU_VIEN_XANH ? PRIMARY : TEXT_DARK));
			if (iconPath != null) {
				setIcon(new SlotIcon(iconPath, 16, 16, kieu == KIEU_CHINH ? DASH_LIGHT : DASH_GRAY));
				setIconTextGap(8);
			}
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

		@Override
		public Dimension getPreferredSize() {
			Dimension d = super.getPreferredSize();
			return new Dimension(d.width, Math.max(d.height, 36));
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			int w = getWidth() - 1;
			int h = getHeight() - 1;
			if (kieu == KIEU_CHINH) {
				g2.setColor(hover ? PRIMARY_DARK : PRIMARY);
				g2.fillRoundRect(0, 0, w, h, 10, 10);
			} else {
				g2.setColor(hover ? new Color(238, 244, 252) : Color.WHITE);
				g2.fillRoundRect(0, 0, w, h, 10, 10);
				g2.setColor(kieu == KIEU_VIEN_XANH ? PRIMARY : BORDER);
				g2.drawRoundRect(0, 0, w, h, 10, 10);
			}
			g2.dispose();
			super.paintComponent(g);
		}
	}

	// =====================================================================
	// Ô NHẬP LIỆU
	// =====================================================================

	/** Ô nhập liệu bo góc, có placeholder, chế độ chỉ đọc (nền xám) và viền xanh khi focus. */
	public static class Input extends JTextField {
		private static final long serialVersionUID = 1L;
		private final String hint;
		private final boolean readOnly;

		public Input(String hint, boolean readOnly) {
			this.hint = hint;
			this.readOnly = readOnly;
			setOpaque(false);
			setFont(FONT_BODY);
			setForeground(TEXT_DARK);
			setEditable(!readOnly);
			setBackground(readOnly ? READONLY_BG : Color.WHITE);
			setBorder(new EmptyBorder(0, 12, 0, 12));
			addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					repaint();
				}

				@Override
				public void focusLost(FocusEvent e) {
					repaint();
				}
			});
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(getBackground());
			g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
			g2.setColor(isFocusOwner() && !readOnly ? PRIMARY : BORDER);
			g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
			g2.dispose();

			super.paintComponent(g);

			if (hint != null && getText().isEmpty()) {
				Graphics2D g3 = (Graphics2D) g.create();
				g3.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g3.setColor(new Color(150, 156, 165));
				g3.setFont(getFont());
				FontMetrics fm = g3.getFontMetrics();
				int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
				g3.drawString(hint, getInsets().left, y);
				g3.dispose();
			}
		}
	}

	// =====================================================================
	// PANEL
	// =====================================================================

	/** Panel nền bo góc. */
	public static class RoundedPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private final int radius;

		public RoundedPanel(int radius, Color bg) {
			this.radius = radius;
			setBackground(bg);
			setOpaque(false);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(getBackground());
			g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius * 2, radius * 2);
			g2.dispose();
			super.paintComponent(g);
		}
	}

	/** Thẻ (card) trắng bo góc, header xanh nhạt gồm tiêu đề bên trái và ghi chú bên phải. */
	public static class CardPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private static final int HEADER_H = 46;
		private final JLabel lblTitle = new JLabel();
		private final JLabel lblNote = new JLabel();
		private final JPanel body = new JPanel(new BorderLayout());

		public CardPanel(String title, String note) {
			super(new BorderLayout());
			setOpaque(false);
			setAlignmentX(Component.LEFT_ALIGNMENT);
			setBorder(new EmptyBorder(1, 1, 1, 1));

			JPanel header = new JPanel(new BorderLayout());
			header.setOpaque(false);
			header.setPreferredSize(new Dimension(0, HEADER_H));
			header.setBorder(new EmptyBorder(0, 16, 0, 16));
			lblTitle.setFont(FONT_SECTION);
			lblTitle.setForeground(PRIMARY);
			lblNote.setFont(FONT_SMALL);
			lblNote.setForeground(TEXT_MUTED);
			header.add(lblTitle, BorderLayout.WEST);
			header.add(lblNote, BorderLayout.EAST);

			body.setOpaque(false);
			body.setBorder(new EmptyBorder(16, 16, 16, 16));

			add(header, BorderLayout.NORTH);
			add(body, BorderLayout.CENTER);
			setTitle(title);
			setNote(note);
		}

		public JPanel getBody() {
			return body;
		}

		public void setTitle(String title) {
			lblTitle.setText(title);
		}

		public void setNote(String note) {
			lblNote.setText(note);
		}

		@Override
		public Dimension getMaximumSize() {
			return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			Shape the = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
			g2.setColor(Color.WHITE);
			g2.fill(the);
			g2.setClip(the);
			g2.setColor(PRIMARY_LIGHT);
			g2.fillRect(0, 0, getWidth(), HEADER_H + 1);
			g2.setClip(null);
			g2.setColor(CARD_BORDER);
			g2.draw(the);
			g2.dispose();
			super.paintComponent(g);
		}
	}

	/** Panel cuộn dọc nhưng luôn co giãn vừa chiều rộng viewport. */
	public static class ScrollablePanel extends JPanel implements Scrollable {
		private static final long serialVersionUID = 1L;

		public ScrollablePanel(LayoutManager lm) {
			super(lm);
		}

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			return getPreferredSize();
		}

		@Override
		public int getScrollableUnitIncrement(Rectangle r, int o, int d) {
			return 16;
		}

		@Override
		public int getScrollableBlockIncrement(Rectangle r, int o, int d) {
			return 100;
		}

		@Override
		public boolean getScrollableTracksViewportWidth() {
			return true;
		}

		@Override
		public boolean getScrollableTracksViewportHeight() {
			return false;
		}
	}
}