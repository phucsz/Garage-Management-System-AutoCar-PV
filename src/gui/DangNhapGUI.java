package gui;

import javax.swing.*;
import javax.swing.border.AbstractBorder;

import business.DangNhapBUS;
import connectDB.ConnectDB;
import dao.TaiKhoanDAO;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.SQLException;

/**
 * Màn hình đăng nhập - Hệ thống quản lý gara ô tô AutoCar PV
 */
public class DangNhapGUI extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// ====== Bảng màu ======
    private static final Color MAU_NEN       = new Color(2, 22, 70);
    private static final Color MAU_TIEU_DE   = new Color(0, 191, 230);
    private static final Color MAU_NUT       = new Color(26, 79, 240);
    private static final Color MAU_NUT_HOVER = new Color(18, 62, 200);
    private static final Color MAU_NHAN      = new Color(90, 90, 90);
    private static final Color MAU_VIEN       = new Color(218, 218, 218);

    private static final String FONT_NAME = "Segoe UI";

    // ====== Thành phần giao diện ======
    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JButton btnDangNhap;
    private JLabel lblQuenMatKhau;

    private DangNhapBUS dangNhap_bus;
    public DangNhapGUI() {
    	try {
			ConnectDB.getInstance().connect();
			System.out.println("connected!");
			dangNhap_bus = new DangNhapBUS();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	setTitle("AutoCar PV - Đăng nhập");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {

        // Panel nền (bố cục tuyệt đối để bám sát thiết kế)
        JPanel pnlNen = new JPanel(null);
        pnlNen.setBackground(MAU_NEN);
        pnlNen.setPreferredSize(new Dimension(950, 551));
        setContentPane(pnlNen);

        // ---------- Bên trái: logo + mô tả ----------
        URL imgUrl = getClass().getResource("/images/logo-light-transparent.png");
        ImageIcon originalIcon = new ImageIcon(imgUrl);

        Image img = originalIcon.getImage();
        Image scaledImg = img.getScaledInstance(432, 75, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImg);

        // Gắn vào JLabel
        JLabel lblLogoImage = new JLabel(scaledIcon);
        lblLogoImage.setBounds(27, 197, 432, 75);
        pnlNen.add(lblLogoImage);

        JLabel lblMoTa = new JLabel("Hệ thống quản lý gara ô tô", SwingConstants.CENTER);
        lblMoTa.setFont(new Font(FONT_NAME, Font.PLAIN, 24));
        lblMoTa.setForeground(Color.WHITE);
        lblMoTa.setBounds(27, 280, 432, 40);
        pnlNen.add(lblMoTa);

        // ---------- Bên phải: thẻ đăng nhập ----------
        RoundedPanel pnlThe = new RoundedPanel(24, Color.WHITE);
        pnlThe.setLayout(null);
        pnlThe.setBounds(505, 63, 400, 418);
        pnlNen.add(pnlThe);

        UserIcon icon = new UserIcon();
        icon.setBounds(80, 54, 70, 82);
        pnlThe.add(icon);

        JLabel lblTieuDe = new JLabel("Đăng nhập");
        lblTieuDe.setFont(new Font(FONT_NAME, Font.BOLD, 28));
        lblTieuDe.setForeground(MAU_TIEU_DE);
        lblTieuDe.setBounds(172, 74, 200, 44);
        pnlThe.add(lblTieuDe);

        JLabel lblTenDN = new JLabel("Tên đăng nhập");
        lblTenDN.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        lblTenDN.setForeground(MAU_NHAN);
        lblTenDN.setBounds(33, 170, 200, 24);
        pnlThe.add(lblTenDN);

        txtTenDangNhap = new JTextField();
        styleInput(txtTenDangNhap);
        txtTenDangNhap.setBounds(33, 202, 333, 41);
        pnlThe.add(txtTenDangNhap);

        JLabel lblMatKhau = new JLabel("Mật khẩu");
        lblMatKhau.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        lblMatKhau.setForeground(MAU_NHAN);
        lblMatKhau.setBounds(33, 253, 200, 24);
        pnlThe.add(lblMatKhau);

        txtMatKhau = new JPasswordField();
        styleInput(txtMatKhau);
        txtMatKhau.setBounds(33, 285, 333, 41);
        pnlThe.add(txtMatKhau);

        lblQuenMatKhau = new JLabel("Quên mật khẩu?", SwingConstants.RIGHT);
        lblQuenMatKhau.setFont(new Font(FONT_NAME, Font.PLAIN, 12));
        lblQuenMatKhau.setForeground(Color.RED);
        lblQuenMatKhau.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblQuenMatKhau.setBounds(216, 331, 150, 20);
        pnlThe.add(lblQuenMatKhau);

        btnDangNhap = new RoundedButton("Đăng nhập");
        btnDangNhap.setBounds(127, 369, 138, 37);
        pnlThe.add(btnDangNhap);

        // ---------- Chân trang ----------
        JLabel lblBanQuyen = new JLabel("© 2026 - Nhóm 5 - DHKTPM20_KSTN", SwingConstants.CENTER);
        lblBanQuyen.setFont(new Font(FONT_NAME, Font.PLAIN, 11));
        lblBanQuyen.setForeground(new Color(200, 205, 225));
        lblBanQuyen.setBounds(0, 515, 950, 20);
        pnlNen.add(lblBanQuyen);

        // ---------- Sự kiện ----------
        btnDangNhap.addActionListener(e -> {
			try {
				xuLyDangNhap();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
        txtMatKhau.addActionListener(e -> {
			try {
				xuLyDangNhap();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}); // Enter ở ô mật khẩu
        txtTenDangNhap.addActionListener(e -> txtMatKhau.requestFocusInWindow());

        lblQuenMatKhau.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	hienThiCuaSoQuenMatKhau();
            }
        });

        pack();
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(btnDangNhap);
    }
    private void hienThiCuaSoQuenMatKhau() {
        // Tạo một JDialog (this: cửa sổ cha, "Khôi phục mật khẩu": Tiêu đề, true: Modal khóa màn hình cha)
        JDialog dialog = new JDialog(DangNhapGUI.this, "Khôi phục mật khẩu", true);
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(DangNhapGUI.this); // Canh giữa so với màn hình đăng nhập
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(Color.WHITE);

        // --- Các thành phần giao diện ---
        JLabel lblTaiKhoan = new JLabel("Tên đăng nhập:");
        lblTaiKhoan.setBounds(40, 30, 120, 30);
        dialog.add(lblTaiKhoan);

        JTextField txtTaiKhoan = new JTextField();
        txtTaiKhoan.setBounds(160, 30, 200, 30);
        dialog.add(txtTaiKhoan);

        JLabel lblMatKhauMoi = new JLabel("Mật khẩu mới:");
        lblMatKhauMoi.setBounds(40, 90, 120, 30);
        dialog.add(lblMatKhauMoi);

        JPasswordField txtMatKhauMoi = new JPasswordField();
        txtMatKhauMoi.setBounds(160, 90, 200, 30);
        dialog.add(txtMatKhauMoi);

        JLabel lblXacNhan = new JLabel("Xác nhận MK:");
        lblXacNhan.setBounds(40, 150, 120, 30);
        dialog.add(lblXacNhan);

        JPasswordField txtXacNhan = new JPasswordField();
        txtXacNhan.setBounds(160, 150, 200, 30);
        dialog.add(txtXacNhan);

        // --- Nút bấm ---
        JButton btnXacNhan = new JButton("Xác nhận");
        btnXacNhan.setBounds(90, 220, 110, 35);
        btnXacNhan.setBackground(new Color(0, 102, 204));
        btnXacNhan.setForeground(Color.WHITE);
        dialog.add(btnXacNhan);

        JButton btnHuy = new JButton("Hủy");
        btnHuy.setBounds(220, 220, 110, 35);
        dialog.add(btnHuy);

        // --- Sự kiện cho nút Hủy ---
        btnHuy.addActionListener(e -> dialog.dispose()); // Đóng cửa sổ

        // --- Sự kiện cho nút Xác nhận ---
        btnXacNhan.addActionListener(e -> {
            String tk = txtTaiKhoan.getText().trim();
            String mkMoi = new String(txtMatKhauMoi.getPassword());
            String xacNhan = new String(txtXacNhan.getPassword());

            // Kiểm tra rỗng
            if (tk.isEmpty() || mkMoi.isEmpty() || xacNhan.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Kiểm tra mật khẩu khớp nhau
            if (!mkMoi.equals(xacNhan)) {
                JOptionPane.showMessageDialog(dialog, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            /* 
             * TODO: Gọi hàm từ DAO kết nối SQL Server tại đây
             * Ví dụ:
             * boolean thanhCong = taiKhoanDAO.doiMatKhau(tk, mkMoi);
             * if (thanhCong) { ... } else { ... }
             */

            JOptionPane.showMessageDialog(dialog, "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose(); // Đóng JDialog sau khi thành công
        });

        // Hiển thị dialog lên màn hình (phải đặt ở cuối cùng)
        dialog.setVisible(true);
    }
    /** Xử lý đăng nhập (hiện chỉ kiểm tra rỗng, bạn nối với DAO/Service sau). 
     * @throws SQLException */
    private void xuLyDangNhap() throws SQLException {
        String tenDN = txtTenDangNhap.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());

        if (tenDN.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            txtTenDangNhap.requestFocusInWindow();
            return;
        }
        if (matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            txtMatKhau.requestFocusInWindow();
            return;
        }

        
        if (dangNhap_bus.DangNhap(tenDN, matKhau)==true) {
        	JOptionPane.showMessageDialog(this, "Đăng nhập thành công!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
        	this.dispose();
            new TrangChuGUI(tenDN, matKhau).setVisible(true);
        } else JOptionPane.showMessageDialog(this, "Sai tên đăng nhập hoặc mật khẩu!",
                "Thông báo", JOptionPane.WARNING_MESSAGE);
        txtTenDangNhap.requestFocus();
        txtTenDangNhap.selectAll();
        
    }

    private void styleInput(JTextField field) {
        field.setFont(new Font(FONT_NAME, Font.PLAIN, 15));
        field.setBackground(Color.WHITE);
        field.setBorder(new RoundedBorder(8, MAU_VIEN, 10));
    }

    // =====================================================================
    //                      CÁC LỚP VẼ GIAO DIỆN TÙY CHỈNH
    // =====================================================================

    /** Panel nền bo góc. */
    private static class RoundedPanel extends JPanel {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final int radius;

        RoundedPanel(int radius, Color bg) {
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

    /** Viền bo góc cho ô nhập liệu. */
    private static class RoundedBorder extends AbstractBorder {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final int radius;
        private final Color color;
        private final int padding;

        RoundedBorder(int radius, Color color, int padding) {
            this.radius = radius;
            this.color = color;
            this.padding = padding;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, radius * 2, radius * 2);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, padding, 4, padding);
        }
    }

    /** Nút bo góc có hiệu ứng hover. */
    private static class RoundedButton extends JButton {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private boolean hover = false;

        RoundedButton(String text) {
            super(text);
            setFont(new Font(FONT_NAME, Font.PLAIN, 15));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override
                public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(hover ? MAU_NUT_HOVER : MAU_NUT);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Biểu tượng người dùng (đầu + thân). */
    private static class UserIcon extends JComponent {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            // Thân
            int bodyH = (int) (h * 0.48);
            Shape clip = new java.awt.Rectangle(0, h - bodyH, w, bodyH);
            g2.setColor(new Color(0, 119, 205));
            g2.setClip(clip);
            g2.fillRoundRect(0, h - bodyH, w, bodyH * 2, w / 2, w / 2);
            g2.setClip(null);

            // Đầu
            int d = (int) (w * 0.64);
            g2.setColor(new Color(66, 165, 245));
            g2.fillOval((w - d) / 2, 0, d, d);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DangNhapGUI().setVisible(true));
    }
}
