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
import javax.swing.JButton;
import javax.swing.JDialog;
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

import business.KhachHangVaXeBUS;
import connectDB.ConnectDB;
import entity.KhachHang;
import entity.Xe;

/**
 * Màn hình KHÁCH HÀNG & XE (JPanel) - được nhúng vào CardLayout của trang chủ.
 *
 * Gồm 3 thẻ:
 *  1. Tra cứu khách hàng (Họ tên, Số điện thoại, Tìm kiếm, Thêm khách hàng)
 *  2. Danh sách khách hàng (bảng có phân trang, cột Thao tác: Xem · Sửa · Xóa)
 *  3. Danh sách xe khách đã tiếp nhận - MẶC ĐỊNH ẨN, chỉ hiện khi đang chọn 1 dòng khách hàng
 *
 * Dữ liệu được tải và ghi trực tiếp qua KhachHangVaXeBUS.
 */
public class KhachHangVaXeGUI extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final int SO_DONG_TRANG = 8;
	private static final int SO_DONG_XE_TOI_DA = 6;
	private static final int CAO_DONG = 58;
	private static final int CAO_HEADER = 40;
	private static final int PAD_TRAI = 16;

	private static final String[] NHAN_KH = { "Xem", "Sửa", "Xóa" };
	private static final String SEP_KH = " · ";
	private static final String[] NHAN_XE = { "Xem", "Sửa" };
	private static final String SEP_XE = " - ";
	private static final String TXT_KH = String.join(SEP_KH, NHAN_KH);
	private static final String TXT_XE = String.join(SEP_XE, NHAN_XE);
	private static final int COL_THAO_TAC_KH = 4;
	private static final int COL_THAO_TAC_XE = 6;

	private static final Border O_BANG = new CompoundBorder(new MatteBorder(0, 0, 1, 0, new Color(236, 240, 245)),
			new EmptyBorder(0, PAD_TRAI, 0, 8));

	private static final NumberFormat FMT_SO = NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN"));

	// =====================================================================
	// DỮ LIỆU: dòng khách hàng / dòng xe.
	// Các row giữ dữ liệu cần hiển thị, còn thao tác CSDL dùng entity của project.
	// =====================================================================
	private static class KhachHangRow {
		final String ma, hoTen, sdt, diaChi;

		KhachHangRow(String ma, String hoTen, String sdt, String diaChi) {
			this.ma = ma;
			this.hoTen = hoTen;
			this.sdt = sdt;
			this.diaChi = diaChi;
		}
	}

	private static class XeRow {
		final String bienSo, loaiXe, mauXe, hang;
		final int soThangDaDi, soKm;

		XeRow(String bienSo, String loaiXe, String mauXe, String hang, int soThangDaDi, int soKm) {
			this.bienSo = bienSo;
			this.loaiXe = loaiXe;
			this.mauXe = mauXe;
			this.hang = hang;
			this.soThangDaDi = soThangDaDi;
			this.soKm = soKm;
		}
	}

	private final Runnable quayVeTrangChu;
	private final List<KhachHangRow> dsKhachHang = new ArrayList<>(); // toàn bộ kết quả (chia trang ở UI)
	private final List<XeRow> dsXe = new ArrayList<>(); // xe của khách đang chọn
	private int trang = 1;
	private KhachHangRow khachDangChon;
	private boolean dangDongBo = false; // chặn sự kiện chọn dòng khi code tự đổ dữ liệu
	private KhachHangVaXeBUS khachHangVaXeBUS;

	// ---- Thẻ tra cứu ----
	private Input txtHoTenTim, txtSdtTim;

	// ---- Thẻ danh sách khách hàng ----
	private CardPanel cardKhach;
	private BangCoThongBao tblKhach;
	private DefaultTableModel modelKhach;
	private JScrollPane spKhach;
	private JLabel lblHienThi, lblSoTrang;
	private JButton btnTruoc, btnSau;

	// ---- Thẻ danh sách xe ----
	private JPanel cacThe, khuVucXe;
	private CardPanel cardXe;
	private BangCoThongBao tblXe;
	private DefaultTableModel modelXe;
	private JScrollPane spXe;

	/**
	 * @param quayVeTrangChu hành động của nút "Trang chủ tiếp nhận" (thường là hienThi(KEY_TRANG_CHU))
	 */
	public KhachHangVaXeGUI(Runnable quayVeTrangChu) {
		this.quayVeTrangChu = quayVeTrangChu;
		khoiTaoDuLieu();
		setLayout(new BorderLayout());
		setBackground(BG);

		ScrollablePanel trangND = new ScrollablePanel(new BorderLayout(0, 16));
		trangND.setBackground(BG);
		trangND.setBorder(new EmptyBorder(22, 28, 28, 28));
		trangND.add(taoTieuDeTrang(), BorderLayout.NORTH);

		cacThe = new JPanel(new GridBagLayout());
		cacThe.setOpaque(false);
		themDong(cacThe, taoTheTraCuu(), 0, 16);
		themDong(cacThe, taoTheDanhSachKhach(), 1, 0);

		// Thẻ xe nằm trong khung riêng để ẩn / hiện cùng khoảng cách phía trên
		khuVucXe = new JPanel(new BorderLayout());
		khuVucXe.setOpaque(false);
		khuVucXe.setBorder(new EmptyBorder(16, 0, 0, 0));
		khuVucXe.add(taoTheDanhSachXe(), BorderLayout.CENTER);
		khuVucXe.setVisible(false); // MẶC ĐỊNH ẨN
		themDong(cacThe, khuVucXe, 2, 0);

		trangND.add(cacThe, BorderLayout.CENTER);

		JScrollPane sp = new JScrollPane(trangND, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setBorder(null);
		sp.getVerticalScrollBar().setUnitIncrement(16);
		add(sp, BorderLayout.CENTER);

		refresh();
	}

	private void khoiTaoDuLieu() {
		try {
			ConnectDB.getInstance().connect();
			khachHangVaXeBUS = new KhachHangVaXeBUS();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Không thể kết nối CSDL để tải dữ liệu khách hàng.", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	/** Gọi mỗi khi chuyển sang màn hình này: tải lại danh sách khách hàng và ẩn danh sách xe. */
	public void refresh() {
		khachDangChon = null;
		taiLaiDanhSachKhach(null);
	}

	// =====================================================================
	// KẾT NỐI CƠ SỞ DỮ LIỆU
	// =====================================================================

	/**
	 * Lấy danh sách khách hàng theo bộ lọc. Cả hai tham số có thể rỗng (= không lọc).
	 * Dữ liệu được lấy qua BUS và chuyển thành row hiển thị.
	 */
	private List<KhachHangRow> layDanhSachKhachHang(String hoTen, String sdt) {
		List<KhachHangRow> ds = new ArrayList<>();
		if (khachHangVaXeBUS == null) {
			return ds;
		}
		try {
			for (KhachHang kh : khachHangVaXeBUS.timKiemKhachHang(hoTen, sdt)) {
				ds.add(new KhachHangRow(kh.getMaKH(), kh.getHoTenKH(), kh.getSdt(), kh.getDiaChi()));
			}
		} catch (SQLException e) {
			baoLoiCSDL("Không thể tải danh sách khách hàng.", e);
		}
		return ds;
	}

	/**
	 * Lấy danh sách xe mà khách hàng đã được tiếp nhận.
	 * Dữ liệu được lấy qua BUS và chuyển thành row hiển thị.
	 */
	private List<XeRow> layDanhSachXeCuaKhach(String maKhachHang) {
		List<XeRow> ds = new ArrayList<>();
		if (khachHangVaXeBUS == null) {
			return ds;
		}
		try {
			for (Xe x : khachHangVaXeBUS.getXeTheoKhachHang(maKhachHang)) {
				ds.add(new XeRow(x.getBienSoXe(), x.getLoaiXe(), x.getMauXe(), x.getHangXe(),
						x.getSoThangDaDi(), (int) x.getSoKmHienTai()));
			}
		} catch (SQLException e) {
			baoLoiCSDL("Không thể tải danh sách xe của khách hàng.", e);
		}
		return ds;
	}

	/**
	 * Thêm mới (themMoi = true, mã tự sinh) hoặc cập nhật (themMoi = false) khách hàng.
	 * @return true nếu lưu thành công
	 */
	private boolean luuKhachHang(KhachHangRow kh, boolean themMoi) {
		if (khachHangVaXeBUS == null) {
			return false;
		}
		KhachHang entity = new KhachHang(kh.ma, kh.hoTen, null, null, null, kh.sdt, kh.diaChi, null);
		try {
			return themMoi ? khachHangVaXeBUS.themKhachHang(entity)
					: khachHangVaXeBUS.capNhatKhachHang(entity);
		} catch (SQLException e) {
			baoLoiCSDL(themMoi ? "Không thể thêm khách hàng." : "Không thể cập nhật khách hàng.", e);
			return false;
		}
	}

	/** @return true nếu xóa thành công */
	private boolean xoaKhachHang(String maKhachHang) {
		if (khachHangVaXeBUS == null) {
			return false;
		}
		try {
			return khachHangVaXeBUS.xoaKhachHang(maKhachHang);
		} catch (SQLException e) {
			baoLoiCSDL("Không thể xóa khách hàng. Có thể khách hàng đang có xe hoặc phiếu liên quan.", e);
			return false;
		}
	}

	/** Cập nhật thông tin một xe của khách hàng. @return true nếu thành công */
	private boolean capNhatXe(String maKhachHang, XeRow xe) {
		if (khachHangVaXeBUS == null) {
			return false;
		}
		Xe entity = new Xe(xe.bienSo, xe.loaiXe, 0, xe.hang, xe.mauXe, xe.soThangDaDi, xe.soKm,
				new KhachHang(maKhachHang));
		try {
			return khachHangVaXeBUS.capNhatXe(entity);
		} catch (SQLException e) {
			baoLoiCSDL("Không thể cập nhật thông tin xe.", e);
			return false;
		}
	}

	private void baoLoiCSDL(String thongBao, SQLException e) {
		JOptionPane.showMessageDialog(this, thongBao, "Lỗi", JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
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
		JLabel lblTitle = new JLabel("Khách hàng & Xe");
		lblTitle.setFont(FONT_TITLE);
		lblTitle.setForeground(TITLE);
		JLabel lblSub = new JLabel("Quản lý hồ sơ khách hàng và các xe liên kết để tiếp nhận nhanh, chính xác.");
		lblSub.setFont(FONT_LABEL);
		lblSub.setForeground(TEXT_MUTED);
		tieuDe.add(lblTitle);
		tieuDe.add(lblSub);
		header.add(tieuDe, BorderLayout.WEST);

		ActionButton btnTrangChu = new ActionButton("Trang chủ tiếp nhận", ActionButton.KIEU_VIEN_XANH, IMG_QUAY_LAI);
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
	// THẺ 1: TRA CỨU KHÁCH HÀNG
	// =====================================================================
	private CardPanel taoTheTraCuu() {
		CardPanel the = new CardPanel("Tra cứu khách hàng", "");
		the.getBody().setBorder(new EmptyBorder(16, 20, 20, 20));

		txtHoTenTim = new Input("Nhập họ tên khách hàng", false);
		txtSdtTim = new Input("Nhập số điện thoại", false);

		ActionButton btnTim = new ActionButton("Tìm kiếm", ActionButton.KIEU_CHINH, IMG_TIM_KIEM);
		ActionButton btnThem = new ActionButton("Thêm khách hàng", ActionButton.KIEU_CHINH, IMG_THEM);
		btnTim.addActionListener(e -> timKiem());
		txtHoTenTim.addActionListener(e -> timKiem());
		txtSdtTim.addActionListener(e -> timKiem());
		btnThem.addActionListener(e -> themKhachHang());

		JPanel hang = new JPanel(new GridBagLayout());
		hang.setOpaque(false);
		themCot(hang, labeled("Họ tên khách hàng", txtHoTenTim), 0, 1.0, 16);
		themCot(hang, labeled("Số điện thoại", txtSdtTim), 1, 1.0, 16);
		themCot(hang, labeledFixed(" ", btnTim), 2, 0, 16);
		themCot(hang, labeledFixed(" ", btnThem), 3, 0, 0);

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

	// =====================================================================
	// THẺ 2: DANH SÁCH KHÁCH HÀNG
	// =====================================================================
	private CardPanel taoTheDanhSachKhach() {
		cardKhach = new CardPanel("Danh sách khách hàng", "Chọn một khách hàng để xem các xe đã tiếp nhận");
		cardKhach.getBody().setBorder(new EmptyBorder(0, 0, 0, 0));

		modelKhach = new DefaultTableModel(new String[] { "Mã khách hàng", "Họ tên", "Số điện thoại", "Địa chỉ", "Thao tác" },
				0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tblKhach = new BangCoThongBao(modelKhach);
		tblKhach.setThongBaoTrong("Không có khách hàng nào để hiển thị.");
		tuyChinhBang(tblKhach, new int[] { 130, 210, 160, 380, 170 },
				new DefaultTableCellRenderer[] { new CellRenderer(true), new CellRenderer(false),
						new CellRenderer(false), new CellRenderer(false), new ThaoTacRenderer(TXT_KH) });

		// Chọn 1 dòng khách hàng -> hiện danh sách xe; bỏ chọn -> ẩn
		tblKhach.getSelectionModel().addListSelectionListener(e -> {
			if (e.getValueIsAdjusting() || dangDongBo) {
				return;
			}
			int r = tblKhach.getSelectedRow();
			KhachHangRow kh = khachTrenHang(r);
			if (kh == null) {
				khachDangChon = null;
				anDanhSachXe();
			} else {
				chonKhach(kh);
			}
		});

		tblKhach.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = tblKhach.rowAtPoint(e.getPoint());
				KhachHangRow kh = khachTrenHang(row);
				if (kh == null) {
					return;
				}
				switch (vungThaoTac(tblKhach, e, COL_THAO_TAC_KH, NHAN_KH, SEP_KH)) {
				case 1:
					xemKhachHang(kh);
					break;
				case 2:
					suaKhachHang(kh);
					break;
				case 3:
					xoaKhachHang(kh);
					break;
				default:
					break;
				}
			}
		});
		tblKhach.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				tblKhach.setCursor(Cursor.getPredefinedCursor(
						vungThaoTac(tblKhach, e, COL_THAO_TAC_KH, NHAN_KH, SEP_KH) != 0 ? Cursor.HAND_CURSOR
								: Cursor.DEFAULT_CURSOR));
			}
		});

		spKhach = new JScrollPane(tblKhach);
		spKhach.setBorder(null);
		spKhach.getViewport().setBackground(Color.WHITE);

		cardKhach.getBody().add(spKhach, BorderLayout.CENTER);
		cardKhach.getBody().add(taoChanKhach(), BorderLayout.SOUTH);
		return cardKhach;
	}

	private JPanel taoChanKhach() {
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
	// THẺ 3: DANH SÁCH XE KHÁCH ĐÃ TIẾP NHẬN (mặc định ẩn)
	// =====================================================================
	private CardPanel taoTheDanhSachXe() {
		cardXe = new CardPanel("Danh sách xe khách đã tiếp nhận", "");
		cardXe.getBody().setBorder(new EmptyBorder(0, 0, 0, 0));

		modelXe = new DefaultTableModel(
				new String[] { "Biển số", "Loại xe", "Màu xe", "Hãng", "Số tháng đã đi", "Số km", "Thao tác" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tblXe = new BangCoThongBao(modelXe);
		tblXe.setThongBaoTrong("Khách hàng này chưa có xe nào được tiếp nhận.");
		tuyChinhBang(tblXe, new int[] { 130, 160, 130, 150, 140, 110, 130 },
				new DefaultTableCellRenderer[] { new CellRenderer(true), new CellRenderer(false),
						new CellRenderer(false), new CellRenderer(false), new CellRenderer(false),
						new CellRenderer(false), new ThaoTacRenderer(TXT_XE) });

		tblXe.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = tblXe.rowAtPoint(e.getPoint());
				if (row < 0 || row >= dsXe.size()) {
					return;
				}
				XeRow xe = dsXe.get(row);
				int v = vungThaoTac(tblXe, e, COL_THAO_TAC_XE, NHAN_XE, SEP_XE);
				if (v == 1) {
					xemXe(xe);
				} else if (v == 2) {
					suaXe(xe);
				}
			}
		});
		tblXe.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				tblXe.setCursor(Cursor.getPredefinedCursor(
						vungThaoTac(tblXe, e, COL_THAO_TAC_XE, NHAN_XE, SEP_XE) != 0 ? Cursor.HAND_CURSOR
								: Cursor.DEFAULT_CURSOR));
			}
		});

		spXe = new JScrollPane(tblXe);
		spXe.setBorder(null);
		spXe.getViewport().setBackground(Color.WHITE);
		cardXe.getBody().add(spXe, BorderLayout.CENTER);
		return cardXe;
	}

	// =====================================================================
	// ĐỔ DỮ LIỆU LÊN BẢNG
	// =====================================================================

	/** Tải lại danh sách khách hàng theo bộ lọc hiện tại; maChon != null thì chọn lại khách đó. */
	private void taiLaiDanhSachKhach(String maChon) {
		List<KhachHangRow> ds = layDanhSachKhachHang(txtHoTenTim.getText().trim(), txtSdtTim.getText().trim());
		dsKhachHang.clear();
		if (ds != null) {
			dsKhachHang.addAll(ds);
		}

		trang = 1;
		khachDangChon = null;
		if (maChon != null) {
			for (int i = 0; i < dsKhachHang.size(); i++) {
				if (dsKhachHang.get(i).ma.equals(maChon)) {
					khachDangChon = dsKhachHang.get(i);
					trang = i / SO_DONG_TRANG + 1;
					break;
				}
			}
		}
		hienThiTrangKhach();
		if (khachDangChon != null) {
			chonKhach(khachDangChon);
		} else {
			anDanhSachXe();
		}
	}

	private int tongSoTrang() {
		return Math.max(1, (dsKhachHang.size() + SO_DONG_TRANG - 1) / SO_DONG_TRANG);
	}

	private void chuyenTrang(int trangMoi) {
		if (trangMoi < 1 || trangMoi > tongSoTrang()) {
			return;
		}
		trang = trangMoi;
		khachDangChon = null; // đổi trang thì bỏ chọn và ẩn danh sách xe
		hienThiTrangKhach();
		anDanhSachXe();
	}

	/** Đổ các dòng của trang hiện tại vào bảng khách hàng (và chọn lại dòng khachDangChon nếu có). */
	private void hienThiTrangKhach() {
		int tong = dsKhachHang.size();
		trang = Math.min(Math.max(1, trang), tongSoTrang());
		int tu = (trang - 1) * SO_DONG_TRANG;
		int den = Math.min(tong, tu + SO_DONG_TRANG);

		dangDongBo = true;
		modelKhach.setRowCount(0);
		for (int i = tu; i < den; i++) {
			KhachHangRow kh = dsKhachHang.get(i);
			modelKhach.addRow(new Object[] { kh.ma, kh.hoTen, dinhDangSdt(kh.sdt), kh.diaChi, TXT_KH });
		}
		if (khachDangChon != null) {
			int i = dsKhachHang.indexOf(khachDangChon) - tu;
			if (i >= 0 && i < den - tu) {
				tblKhach.setRowSelectionInterval(i, i);
			}
		}
		dangDongBo = false;

		lblHienThi.setText("Hiển thị " + (den - tu) + " / " + tong + " bản ghi");
		lblSoTrang.setText(String.valueOf(trang));
		btnTruoc.setEnabled(trang > 1);
		btnSau.setEnabled(trang < tongSoTrang());

		datChieuCao(spKhach, den - tu, SO_DONG_TRANG);
		cardKhach.revalidate();
		cardKhach.repaint();
	}

	/** Khách hàng đang hiển thị ở dòng r của bảng (null nếu r không hợp lệ). */
	private KhachHangRow khachTrenHang(int r) {
		int idx = (trang - 1) * SO_DONG_TRANG + r;
		return (r >= 0 && idx < dsKhachHang.size()) ? dsKhachHang.get(idx) : null;
	}

	/** Chọn một khách hàng: nạp danh sách xe của họ và HIỆN thẻ xe. */
	private void chonKhach(KhachHangRow kh) {
		khachDangChon = kh;

		List<XeRow> ds = layDanhSachXeCuaKhach(kh.ma);
		dsXe.clear();
		if (ds != null) {
			dsXe.addAll(ds);
		}
		hienThiXe();

		cardXe.setNote("Khách hàng: " + kh.ma + " • " + kh.hoTen);
		khuVucXe.setVisible(true);
		cacThe.revalidate();
		cacThe.repaint();
		SwingUtilities.invokeLater(() -> khuVucXe
				.scrollRectToVisible(new Rectangle(0, 0, khuVucXe.getWidth(), khuVucXe.getHeight())));
	}

	/** ẨN thẻ danh sách xe và xóa dữ liệu của nó. */
	private void anDanhSachXe() {
		dsXe.clear();
		modelXe.setRowCount(0);
		khuVucXe.setVisible(false);
		cacThe.revalidate();
		cacThe.repaint();
	}

	private void hienThiXe() {
		modelXe.setRowCount(0);
		for (XeRow x : dsXe) {
			modelXe.addRow(new Object[] { x.bienSo, x.loaiXe, x.mauXe, x.hang, x.soThangDaDi, FMT_SO.format(x.soKm),
					TXT_XE });
		}
		datChieuCao(spXe, dsXe.size(), SO_DONG_XE_TOI_DA);
		cardXe.revalidate();
		cardXe.repaint();
	}

	private static void datChieuCao(JScrollPane sp, int soDong, int toiDa) {
		int n = Math.max(2, Math.min(soDong, toiDa)); // tối thiểu 2 dòng để chừa chỗ cho dòng thông báo
		sp.setPreferredSize(new Dimension(100, CAO_HEADER + n * CAO_DONG));
	}

	private static String dinhDangSdt(String s) {
		return s != null && s.matches("\\d{10}") ? s.substring(0, 4) + " " + s.substring(4, 7) + " " + s.substring(7)
				: s;
	}

	// =====================================================================
	// TÌM KIẾM / THÊM / XEM / SỬA / XÓA KHÁCH HÀNG
	// =====================================================================
	private void timKiem() {
		taiLaiDanhSachKhach(null);
	}

	private void themKhachHang() {
		String[] kq = hienFormDialog("Thêm khách hàng", new String[] { "Mã khách hàng", "Họ tên *", "Số điện thoại *", "Địa chỉ *" },
				new String[] { "(tự sinh)", "", "", "" }, new boolean[] { true, false, false, false },
				KhachHangVaXeGUI::kiemTraKhachHang);
		if (kq == null) {
			return;
		}
		KhachHangRow kh = new KhachHangRow("", kq[1], kq[2].replaceAll("\\D", ""), kq[3]);
		if (luuKhachHang(kh, true)) {
			JOptionPane.showMessageDialog(this, "Đã thêm khách hàng " + kh.hoTen + ".", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
			taiLaiDanhSachKhach(null);
		} else {
			JOptionPane.showMessageDialog(this, "Thêm khách hàng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void suaKhachHang(KhachHangRow kh) {
		String[] kq = hienFormDialog("Sửa khách hàng", new String[] { "Mã khách hàng", "Họ tên *", "Số điện thoại *", "Địa chỉ *" },
				new String[] { kh.ma, kh.hoTen, kh.sdt, kh.diaChi }, new boolean[] { true, false, false, false },
				KhachHangVaXeGUI::kiemTraKhachHang);
		if (kq == null) {
			return;
		}
		KhachHangRow moi = new KhachHangRow(kh.ma, kq[1], kq[2].replaceAll("\\D", ""), kq[3]);
		if (luuKhachHang(moi, false)) {
			JOptionPane.showMessageDialog(this, "Đã cập nhật khách hàng " + kh.ma + ".", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
			taiLaiDanhSachKhach(kh.ma);
		} else {
			JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void xemKhachHang(KhachHangRow kh) {
		JOptionPane.showMessageDialog(this,
				"Mã khách hàng: " + kh.ma + "\nHọ tên: " + kh.hoTen + "\nSố điện thoại: " + dinhDangSdt(kh.sdt)
						+ "\nĐịa chỉ: " + kh.diaChi,
				"Thông tin khách hàng", JOptionPane.INFORMATION_MESSAGE);
	}

	private void xoaKhachHang(KhachHangRow kh) {
		int c = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa khách hàng " + kh.hoTen + " (" + kh.ma + ")?",
				"Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (c != JOptionPane.YES_OPTION) {
			return;
		}
		if (xoaKhachHang(kh.ma)) {
			taiLaiDanhSachKhach(null);
		} else {
			JOptionPane.showMessageDialog(this, "Xóa khách hàng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	/** Trả về thông báo lỗi, hoặc null nếu hợp lệ. v = {mã, họ tên, sđt, địa chỉ}. */
	private static String kiemTraKhachHang(String[] v) {
		if (v[1].isEmpty()) {
			return "Vui lòng nhập họ tên!";
		}
		if (!v[2].replaceAll("\\D", "").matches("0\\d{9}")) {
			return "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0!";
		}
		if (v[3].isEmpty()) {
			return "Vui lòng nhập địa chỉ!";
		}
		return null;
	}

	// =====================================================================
	// XEM / SỬA XE
	// =====================================================================
	private void xemXe(XeRow x) {
		JOptionPane.showMessageDialog(this,
				"Biển số: " + x.bienSo + "\nLoại xe: " + x.loaiXe + "\nMàu xe: " + x.mauXe + "\nHãng: " + x.hang
						+ "\nSố tháng đã đi: " + x.soThangDaDi + "\nSố km: " + FMT_SO.format(x.soKm),
				"Thông tin xe", JOptionPane.INFORMATION_MESSAGE);
	}

	private void suaXe(XeRow x) {
		if (khachDangChon == null) {
			return;
		}
		String[] kq = hienFormDialog("Sửa thông tin xe",
				new String[] { "Biển số", "Loại xe *", "Màu xe", "Hãng *", "Số tháng đã đi", "Số km" },
				new String[] { x.bienSo, x.loaiXe, x.mauXe, x.hang, String.valueOf(x.soThangDaDi),
						String.valueOf(x.soKm) },
				new boolean[] { true, false, false, false, false, false }, v -> {
					if (v[1].isEmpty()) {
						return "Vui lòng nhập loại xe!";
					}
					if (v[3].isEmpty()) {
						return "Vui lòng nhập hãng xe!";
					}
					if (!v[4].matches("\\d+")) {
						return "Số tháng đã đi phải là số nguyên không âm!";
					}
					if (!v[5].replaceAll("[.,\\s]", "").matches("\\d+")) {
						return "Số km phải là số nguyên không âm!";
					}
					return null;
				});
		if (kq == null) {
			return;
		}
		XeRow moi = new XeRow(x.bienSo, kq[1], kq[2], kq[3], Integer.parseInt(kq[4]),
				Integer.parseInt(kq[5].replaceAll("[.,\\s]", "")));
		if (capNhatXe(khachDangChon.ma, moi)) {
			JOptionPane.showMessageDialog(this, "Đã cập nhật xe " + x.bienSo + ".", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
			chonKhach(khachDangChon); // nạp lại danh sách xe từ CSDL
		} else {
			JOptionPane.showMessageDialog(this, "Cập nhật xe thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
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
		Window cha = SwingUtilities.getWindowAncestor(this);
		JDialog dlg = new JDialog(cha, tieuDe, Dialog.ModalityType.APPLICATION_MODAL);

		JPanel noiDung = new JPanel(new BorderLayout(0, 8));
		noiDung.setBackground(Color.WHITE);
		noiDung.setBorder(new EmptyBorder(20, 24, 20, 24));

		JPanel luoi = new JPanel(new GridBagLayout());
		luoi.setOpaque(false);
		Input[] o = new Input[nhan.length];
		for (int i = 0; i < nhan.length; i++) {
			o[i] = new Input(null, chiDoc[i]);
			o[i].setText(giaTri[i]);
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
				v[i] = o[i].getText().trim();
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
		dlg.setSize(Math.max(dlg.getWidth(), 460), dlg.getHeight());
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

	/** Xác định vị trí click trong ô "Thao tác": 0 = ngoài, 1..n = nhãn thứ n (Xem, Sửa, Xóa...). */
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

	/** Ô "Thao tác": chữ xanh đậm, ví dụ "Xem · Sửa · Xóa". */
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
