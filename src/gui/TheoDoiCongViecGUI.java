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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.swing.BoxLayout;
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

/**
 * Màn hình THEO DÕI CÔNG VIỆC PHỤ TRÁCH (JPanel) - nhúng vào CardLayout của TrangChuKyThuatVienGUI.
 * Mở từ nút "Theo dõi công việc" trên thẻ công việc màu xanh (đang thực hiện) của DanhSachCongViecGUI.
 *
 * Gồm (giống ảnh mẫu):
 *  1. Thông tin công việc: Phiếu sửa chữa, Khách hàng / Điện thoại, Biển số xe phụ trách (chỉ đọc)
 *  2. Checklist công việc thực hiện: Đánh dấu (tích để hoàn thành), Dịch vụ, Ghi chú, Cập nhật, Trạng thái
 *  3. Phụ tùng sử dụng: Mã, Tên, ĐVT, Dự kiến, Đã dùng, Cấp phát, Trạng thái; nút "Thêm phụ tùng phát sinh"
 *     và "Lập yêu cầu xuất kho"
 *  4. Thanh tiến độ + nút "Hoàn thành công việc" / "Lưu tiến độ"
 *
 * Cách dùng: từ DanhSachCongViecGUI gọi {@link #napCongViec(String)} rồi hiển thị card này; màn hình tự tải
 * dữ liệu trong {@link #refresh()} (gọi trong onHienThi). Mở trực tiếp từ menu thì refresh() sẽ tải công việc
 * đang phụ trách (qua layMaPhieuDangPhuTrach).
 *
 * KHÔNG có dữ liệu mẫu và chưa gắn với CSDL cụ thể (sẽ cập nhật theo CSDL mới). Các hàm "TODO": xem khối
 * "KẾT NỐI CƠ SỞ DỮ LIỆU" bên dưới.
 */
public class TheoDoiCongViecGUI extends JPanel {
	private static final long serialVersionUID = 1L;

	// =====================================================================
	// ICON CỦA MÀN HÌNH NÀY - TỰ NHẬP ĐƯỜNG DẪN VÀO GIỮA CẶP DẤU "".
	// Ví dụ: "/images/icon_xuatKho.png" hoặc chỉ "icon_xuatKho.png" (tự hiểu là nằm trong /images/).
	// Để rỗng thì hiện khung viền nét đứt. (Các icon dùng chung như IMG_THEM, IMG_LUU... nằm trong UITheme.)
	// =====================================================================
	private static final String ICON_XUAT_KHO = ""; // TODO: icon hộp ở nút "Lập yêu cầu xuất kho"
	private static final String ICON_HOAN_THANH = ""; // TODO: icon dấu tích ở nút "Hoàn thành công việc"

	// ====== Nhãn trạng thái ======
	private static final String TT_DV_XONG = "Hoàn thành";
	private static final String TT_DV_CHUA = "Chưa thực hiện";
	private static final String TT_PT_DA_CAP = "Đã cấp";
	private static final String TT_PT_CHO = "Chờ xuất kho";

	private static final int CAO_DONG = 58;
	private static final int CAO_HEADER = 40;
	private static final int PAD_TRAI = 16;
	private static final int COL_CHECK = 0;
	private static final int COL_GHI_CHU = 2;
	private static final int COL_DA_DUNG = 4;

	private static final DateTimeFormatter FMT_CAP_NHAT = DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy");
	private static final Border O_BANG = new CompoundBorder(new MatteBorder(0, 0, 1, 0, new Color(236, 240, 245)),
			new EmptyBorder(0, PAD_TRAI, 0, 8));
	private static final Color XANH_LA = new Color(34, 160, 56);
	private static final Color XANH_LA_DAM = new Color(26, 130, 44);
	private static final Color CAM = new Color(255, 152, 0);

	/** Thông tin chung của công việc (chỉ đọc). TODO: có thể thay bằng entity (sau khi có CSDL mới). */
	private static class ThongTinRow {
		final String maPhieu, tenKhach, sdtKhach, bienSo;

		ThongTinRow(String maPhieu, String tenKhach, String sdtKhach, String bienSo) {
			this.maPhieu = maPhieu;
			this.tenKhach = tenKhach;
			this.sdtKhach = sdtKhach;
			this.bienSo = bienSo;
		}
	}

	/** Một dịch vụ trong checklist. */
	private static class DichVuRow {
		final String ma, ten;
		String ghiChu;
		boolean hoanThanh;
		LocalDateTime capNhat; // null = chưa cập nhật

		DichVuRow(String ma, String ten, String ghiChu, boolean hoanThanh, LocalDateTime capNhat) {
			this.ma = ma;
			this.ten = ten;
			this.ghiChu = ghiChu;
			this.hoanThanh = hoanThanh;
			this.capNhat = capNhat;
		}
	}

	/** Một phụ tùng sử dụng cho công việc. */
	private static class PhuTungRow {
		final String ma, ten, donViTinh;
		final int duKien, capPhat;
		int daDung;

		PhuTungRow(String ma, String ten, String donViTinh, int duKien, int daDung, int capPhat) {
			this.ma = ma;
			this.ten = ten;
			this.donViTinh = donViTinh;
			this.duKien = duKien;
			this.daDung = daDung;
			this.capPhat = capPhat;
		}
	}

	/** Một phụ tùng có thể chọn khi thêm phụ tùng phát sinh. */
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

	private final Runnable quayVeDanhSach;
	private String maNV = "";
	private String hoTenKTV = "";
	private String maPhieuHienTai = null;

	private final List<DichVuRow> dsDichVu = new ArrayList<>();
	private final List<PhuTungRow> dsPhuTung = new ArrayList<>();

	// ---- Thông tin ----
	private CardPanel cardThongTin;
	private Input txtPhieu, txtKhachHang, txtBienSo;

	// ---- Checklist ----
	private CardPanel cardChecklist;
	private BangCoThongBao tblChecklist;
	private DefaultTableModel modelChecklist;
	private JScrollPane spChecklist;

	// ---- Phụ tùng ----
	private CardPanel cardPhuTung;
	private BangCoThongBao tblPhuTung;
	private DefaultTableModel modelPhuTung;
	private JScrollPane spPhuTung;

	// ---- Tiến độ ----
	private JLabel lblTienDo, lblPhanTram;
	private ThanhTienDo thanhTienDo;

	/**
	 * @param quayVeDanhSach hành động của nút "Danh sách công việc" (quay về DanhSachCongViecGUI)
	 */
	public TheoDoiCongViecGUI(Runnable quayVeDanhSach) {
		this.quayVeDanhSach = quayVeDanhSach;
		setLayout(new BorderLayout());
		setBackground(BG);

		ScrollablePanel trangND = new ScrollablePanel(new BorderLayout(0, 16));
		trangND.setBackground(BG);
		trangND.setBorder(new EmptyBorder(22, 28, 28, 28));
		trangND.add(taoTieuDeTrang(), BorderLayout.NORTH);

		JPanel cacThe = new JPanel(new GridBagLayout());
		cacThe.setOpaque(false);
		themDong(cacThe, taoTheThongTin(), 0, 16);
		themDong(cacThe, taoTheChecklist(), 1, 16);
		themDong(cacThe, taoThePhuTung(), 2, 16);
		themDong(cacThe, taoKhuTienDo(), 3, 14);
		themDong(cacThe, taoHangNut(), 4, 0);
		trangND.add(cacThe, BorderLayout.CENTER);

		JScrollPane sp = new JScrollPane(trangND, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setBorder(null);
		sp.getVerticalScrollBar().setUnitIncrement(16);
		add(sp, BorderLayout.CENTER);

		hienThiTatCa();
	}

	/**
	 * Gán kỹ thuật viên đang đăng nhập (hiện ở góc thẻ "Thông tin công việc"). Gọi sau khi tạo màn hình.
	 * @param maNV  mã nhân viên
	 * @param hoTen họ tên
	 */
	public void setKyThuatVien(String maNV, String hoTen) {
		this.maNV = maNV == null ? "" : maNV;
		this.hoTenKTV = hoTen == null ? "" : hoTen;
		cardThongTin.setNote("Phụ trách: " + this.hoTenKTV + (this.maNV.isEmpty() ? "" : " • " + this.maNV));
	}

	/**
	 * Chọn công việc cần theo dõi (chỉ ghi nhớ mã phiếu). Dữ liệu được tải khi màn hình hiển thị (refresh()).
	 * @param maPhieu mã phiếu của công việc (lấy từ thẻ công việc ở DanhSachCongViecGUI)
	 */
	public void napCongViec(String maPhieu) {
		this.maPhieuHienTai = maPhieu;
	}

	/**
	 * Gọi mỗi khi chuyển sang màn hình này: tải lại thông tin, checklist và phụ tùng của công việc đang chọn.
	 * Nếu chưa chọn công việc nào (mở trực tiếp từ menu) thì lấy công việc đang phụ trách.
	 */
	public void refresh() {
		if (maPhieuHienTai == null || maPhieuHienTai.isEmpty()) {
			maPhieuHienTai = layMaPhieuDangPhuTrach(maNV);
		}
		taiDuLieu();
	}

	// =====================================================================
	// TODO: KẾT NỐI CƠ SỞ DỮ LIỆU (DAO) - BẠN CHỈ CẦN SỬA CÁC HÀM TRONG KHỐI NÀY
	// =====================================================================

	/**
	 * Mã phiếu của công việc đang thực hiện mà kỹ thuật viên phụ trách (dùng khi mở màn hình trực tiếp từ menu).
	 * @return mã phiếu, hoặc null nếu không có công việc nào đang thực hiện
	 */
	private String layMaPhieuDangPhuTrach(String maNV) {
		// TODO: return congViecDAO.layMaPhieuDangThucHien(maNV);
		return null;
	}

	/** Thông tin chung của công việc; null nếu không tìm thấy. TODO: gọi DAO rồi new ThongTinRow(...). */
	private ThongTinRow layThongTinCongViec(String maPhieu) {
		// TODO: return new ThongTinRow(maPhieu, tenKhach, sdtKhach, bienSo);
		return null;
	}

	/** Danh sách dịch vụ (checklist) của công việc. TODO: gọi DAO rồi ds.add(new DichVuRow(...)). */
	private List<DichVuRow> layChecklistCongViec(String maPhieu) {
		List<DichVuRow> ds = new ArrayList<>();
		// TODO: ds.add(new DichVuRow(ma, tenDichVu, ghiChu, hoanThanh, thoiGianCapNhat));
		return ds;
	}

	/** Danh sách phụ tùng sử dụng của công việc. TODO: gọi DAO rồi ds.add(new PhuTungRow(...)). */
	private List<PhuTungRow> layPhuTungSuDung(String maPhieu) {
		List<PhuTungRow> ds = new ArrayList<>();
		// TODO: ds.add(new PhuTungRow(ma, ten, donViTinh, duKien, daDung, capPhat));
		return ds;
	}

	/** Danh sách phụ tùng để chọn khi thêm phụ tùng phát sinh. TODO: gọi DAO rồi ds.add(new PhuTungChon(...)). */
	private List<PhuTungChon> layDanhSachPhuTung() {
		List<PhuTungChon> ds = new ArrayList<>();
		// TODO: ds.add(new PhuTungChon(ma, ten, donViTinh));
		return ds;
	}

	/**
	 * Lưu tiến độ: trạng thái / ghi chú / thời gian cập nhật của từng dịch vụ, số lượng đã dùng và các phụ tùng
	 * phát sinh mới thêm (phụ tùng chưa có trong công việc). NÊN chạy trong một transaction.
	 * @return true nếu lưu thành công
	 */
	private boolean luuTienDo(String maPhieu, List<DichVuRow> checklist, List<PhuTungRow> phuTung) {
		// TODO: return congViecDAO.luuTienDo(...);
		return false;
	}

	/** Đánh dấu công việc hoàn thành. @return true nếu thành công */
	private boolean hoanThanhCongViec(String maPhieu) {
		// TODO: return congViecDAO.hoanThanh(maPhieu);
		return false;
	}

	/**
	 * Lập yêu cầu xuất kho cho các phụ tùng còn chờ cấp (cấp phát < dự kiến).
	 * @return true nếu lập thành công
	 */
	private boolean lapYeuCauXuatKho(String maPhieu, List<PhuTungRow> cacPhuTungCho) {
		// TODO: return yeuCauXuatKhoDAO.lap(...);
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
		JLabel lblTitle = new JLabel("Theo dõi công việc phụ trách");
		lblTitle.setFont(FONT_TITLE);
		lblTitle.setForeground(TITLE);
		JLabel lblSub = new JLabel("Cập nhật tiến độ và ghi nhận vật tư thực tế cho công việc đang sửa chữa.");
		lblSub.setFont(FONT_LABEL);
		lblSub.setForeground(TEXT_MUTED);
		tieuDe.add(lblTitle);
		tieuDe.add(lblSub);
		header.add(tieuDe, BorderLayout.WEST);

		ActionButton btnQuayLai = new ActionButton("Danh sách công việc", ActionButton.KIEU_VIEN_XANH, IMG_QUAY_LAI);
		btnQuayLai.addActionListener(e -> {
			if (quayVeDanhSach != null) {
				quayVeDanhSach.run();
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
	// THẺ 1: THÔNG TIN CÔNG VIỆC
	// =====================================================================
	private CardPanel taoTheThongTin() {
		cardThongTin = new CardPanel("Thông tin công việc", "");
		cardThongTin.getBody().setBorder(new EmptyBorder(16, 20, 20, 20));

		txtPhieu = new Input(null, true);
		txtKhachHang = new Input(null, true);
		txtBienSo = new Input(null, true);

		JPanel hang = new JPanel(new GridLayout(1, 3, 16, 0));
		hang.setOpaque(false);
		hang.add(labeled("Phiếu sửa chữa", txtPhieu));
		hang.add(labeled("Khách hàng / Điện thoại", txtKhachHang));
		hang.add(labeled("Biển số xe phụ trách", txtBienSo));
		cardThongTin.getBody().add(hang, BorderLayout.CENTER);
		return cardThongTin;
	}

	// =====================================================================
	// THẺ 2: CHECKLIST CÔNG VIỆC THỰC HIỆN
	// =====================================================================
	private CardPanel taoTheChecklist() {
		cardChecklist = new CardPanel("Checklist công việc thực hiện", "");
		cardChecklist.getBody().setBorder(new EmptyBorder(0, 0, 0, 0));

		modelChecklist = new DefaultTableModel(
				new String[] { "Đánh dấu", "Dịch vụ", "Ghi chú", "Cập nhật", "Trạng thái" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tblChecklist = new BangCoThongBao(modelChecklist);
		tuyChinhBang(tblChecklist, new int[] { 90, 300, 420, 190, 150 },
				new TableCellRenderer[] { new CheckRenderer(), new CellRenderer(false), new CellRenderer(false),
						new CellRenderer(false), new BadgeRenderer() });

		tblChecklist.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = tblChecklist.rowAtPoint(e.getPoint());
				int col = tblChecklist.columnAtPoint(e.getPoint());
				if (row < 0 || row >= dsDichVu.size()) {
					return;
				}
				if (col == COL_CHECK) {
					danhDauDichVu(dsDichVu.get(row));
				} else if (col == COL_GHI_CHU) {
					suaGhiChu(dsDichVu.get(row));
				}
			}
		});
		tblChecklist.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int col = tblChecklist.columnAtPoint(e.getPoint());
				int row = tblChecklist.rowAtPoint(e.getPoint());
				boolean tay = row >= 0 && (col == COL_CHECK || col == COL_GHI_CHU);
				tblChecklist.setCursor(Cursor.getPredefinedCursor(tay ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
			}
		});

		spChecklist = new JScrollPane(tblChecklist);
		spChecklist.setBorder(null);
		spChecklist.getViewport().setBackground(Color.WHITE);
		cardChecklist.getBody().add(spChecklist, BorderLayout.CENTER);
		return cardChecklist;
	}

	// =====================================================================
	// THẺ 3: PHỤ TÙNG SỬ DỤNG
	// =====================================================================
	private CardPanel taoThePhuTung() {
		cardPhuTung = new CardPanel("Phụ tùng sử dụng", "");
		cardPhuTung.getBody().setBorder(new EmptyBorder(0, 0, 0, 0));

		modelPhuTung = new DefaultTableModel(
				new String[] { "Mã phụ tùng", "Tên phụ tùng", "ĐVT", "Dự kiến", "Đã dùng", "Cấp phát", "Trạng thái" },
				0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tblPhuTung = new BangCoThongBao(modelPhuTung);
		tuyChinhBang(tblPhuTung, new int[] { 130, 330, 90, 110, 110, 110, 170 },
				new TableCellRenderer[] { new CellRenderer(true), new CellRenderer(false), new CellRenderer(false),
						new CellRenderer(false), new CellRenderer(false), new CellRenderer(false),
						new BadgeRenderer() });

		tblPhuTung.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = tblPhuTung.rowAtPoint(e.getPoint());
				int col = tblPhuTung.columnAtPoint(e.getPoint());
				if (row >= 0 && row < dsPhuTung.size() && col == COL_DA_DUNG) {
					suaDaDung(dsPhuTung.get(row));
				}
			}
		});
		tblPhuTung.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				boolean tay = tblPhuTung.rowAtPoint(e.getPoint()) >= 0
						&& tblPhuTung.columnAtPoint(e.getPoint()) == COL_DA_DUNG;
				tblPhuTung.setCursor(Cursor.getPredefinedCursor(tay ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
			}
		});

		spPhuTung = new JScrollPane(tblPhuTung);
		spPhuTung.setBorder(null);
		spPhuTung.getViewport().setBackground(Color.WHITE);
		cardPhuTung.getBody().add(spPhuTung, BorderLayout.CENTER);

		// Chân thẻ: "Thêm phụ tùng phát sinh" (trái) và "Lập yêu cầu xuất kho" (phải)
		ActionButton btnThem = new ActionButton("Thêm phụ tùng phát sinh", ActionButton.KIEU_VIEN_XANH, IMG_THEM);
		ActionButton btnXuatKho = new ActionButton("Lập yêu cầu xuất kho", ActionButton.KIEU_CHINH, ICON_XUAT_KHO);
		btnThem.addActionListener(e -> themPhuTungPhatSinh());
		btnXuatKho.addActionListener(e -> lapYeuCauXuat());
		JPanel chan = new JPanel(new BorderLayout());
		chan.setOpaque(false);
		chan.setBorder(new EmptyBorder(16, 16, 16, 16));
		JPanel trai = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		trai.setOpaque(false);
		trai.add(btnThem);
		JPanel phai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		phai.setOpaque(false);
		phai.add(btnXuatKho);
		chan.add(trai, BorderLayout.WEST);
		chan.add(phai, BorderLayout.EAST);
		cardPhuTung.getBody().add(chan, BorderLayout.SOUTH);
		return cardPhuTung;
	}

	// =====================================================================
	// KHU TIẾN ĐỘ + NÚT
	// =====================================================================
	private JPanel taoKhuTienDo() {
		JPanel p = new JPanel();
		p.setOpaque(false);
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

		JPanel dong = new JPanel(new BorderLayout());
		dong.setOpaque(false);
		dong.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblTienDo = new JLabel("Tiến độ công việc: 0/0 dịch vụ đã hoàn thành");
		lblTienDo.setFont(FONT_SECTION);
		lblTienDo.setForeground(TITLE);
		lblPhanTram = new JLabel("0%");
		lblPhanTram.setFont(FONT_SECTION);
		lblPhanTram.setForeground(TITLE);
		dong.add(lblTienDo, BorderLayout.WEST);
		dong.add(lblPhanTram, BorderLayout.EAST);

		thanhTienDo = new ThanhTienDo();
		thanhTienDo.setAlignmentX(Component.LEFT_ALIGNMENT);

		p.add(dong);
		p.add(javax.swing.Box.createVerticalStrut(8));
		p.add(thanhTienDo);
		return p;
	}

	private JPanel taoHangNut() {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
		p.setOpaque(false);

		NutMau btnHoanThanh = new NutMau("Hoàn thành công việc", XANH_LA, XANH_LA_DAM, ICON_HOAN_THANH);
		ActionButton btnLuu = new ActionButton("Lưu tiến độ", ActionButton.KIEU_CHINH, IMG_LUU);
		btnHoanThanh.addActionListener(e -> hoanThanh());
		btnLuu.addActionListener(e -> luu());
		p.add(btnHoanThanh);
		p.add(btnLuu);
		return p;
	}

	// =====================================================================
	// TẢI & HIỂN THỊ DỮ LIỆU
	// =====================================================================
	private void taiDuLieu() {
		dsDichVu.clear();
		dsPhuTung.clear();

		ThongTinRow tt = (maPhieuHienTai == null) ? null : layThongTinCongViec(maPhieuHienTai);
		if (tt != null) {
			txtPhieu.setText(tt.maPhieu);
			txtKhachHang.setText(tt.tenKhach + " / " + tt.sdtKhach);
			txtBienSo.setText(tt.bienSo);

			List<DichVuRow> cl = layChecklistCongViec(maPhieuHienTai);
			if (cl != null) {
				dsDichVu.addAll(cl);
			}
			List<PhuTungRow> pt = layPhuTungSuDung(maPhieuHienTai);
			if (pt != null) {
				dsPhuTung.addAll(pt);
			}
		} else {
			txtPhieu.setText("");
			txtKhachHang.setText("");
			txtBienSo.setText("");
		}

		String khongCoViec = "Chưa chọn công việc. Hãy chọn \"Theo dõi công việc\" ở màn hình Danh sách công việc.";
		tblChecklist.setThongBaoTrong(tt == null ? khongCoViec : "Công việc này chưa có dịch vụ nào trong checklist.");
		tblPhuTung.setThongBaoTrong(tt == null ? khongCoViec : "Công việc này chưa có phụ tùng nào.");
		hienThiTatCa();
	}

	private void hienThiTatCa() {
		hienThiChecklist();
		hienThiPhuTung();
		capNhatTienDo();
	}

	private void hienThiChecklist() {
		modelChecklist.setRowCount(0);
		for (DichVuRow dv : dsDichVu) {
			modelChecklist.addRow(new Object[] { dv.hoanThanh, dv.ten, dv.ghiChu == null ? "" : dv.ghiChu,
					dv.capNhat == null ? "—" : dv.capNhat.format(FMT_CAP_NHAT), dv.hoanThanh ? TT_DV_XONG : TT_DV_CHUA });
		}
		int n = Math.max(2, dsDichVu.size());
		spChecklist.setPreferredSize(new Dimension(100, CAO_HEADER + n * CAO_DONG));
		cardChecklist.revalidate();
		cardChecklist.repaint();
	}

	private void hienThiPhuTung() {
		modelPhuTung.setRowCount(0);
		for (PhuTungRow p : dsPhuTung) {
			modelPhuTung.addRow(new Object[] { p.ma, p.ten, p.donViTinh == null ? "" : p.donViTinh, p.duKien, p.daDung,
					p.capPhat, trangThaiPhuTung(p) });
		}
		int n = Math.max(2, dsPhuTung.size());
		spPhuTung.setPreferredSize(new Dimension(100, CAO_HEADER + n * CAO_DONG));
		cardPhuTung.revalidate();
		cardPhuTung.repaint();
	}

	/** Đã cấp: cấp phát >= dự kiến; ngược lại: Chờ xuất kho. */
	private static String trangThaiPhuTung(PhuTungRow p) {
		return p.capPhat >= p.duKien ? TT_PT_DA_CAP : TT_PT_CHO;
	}

	private void capNhatTienDo() {
		int tong = dsDichVu.size();
		int xong = 0;
		for (DichVuRow dv : dsDichVu) {
			if (dv.hoanThanh) {
				xong++;
			}
		}
		int phanTram = tong == 0 ? 0 : (int) Math.round(100.0 * xong / tong);
		lblTienDo.setText("Tiến độ công việc: " + xong + "/" + tong + " dịch vụ đã hoàn thành");
		lblPhanTram.setText(phanTram + "%");
		thanhTienDo.setTyLe(tong == 0 ? 0 : (double) xong / tong);
	}

	// =====================================================================
	// THAO TÁC TRÊN CHECKLIST / PHỤ TÙNG
	// =====================================================================
	private void danhDauDichVu(DichVuRow dv) {
		dv.hoanThanh = !dv.hoanThanh;
		dv.capNhat = dv.hoanThanh ? LocalDateTime.now() : null;
		hienThiChecklist();
		capNhatTienDo();
	}

	private void suaGhiChu(DichVuRow dv) {
		Object kq = JOptionPane.showInputDialog(this, "Ghi chú cho dịch vụ \"" + dv.ten + "\":", "Cập nhật ghi chú",
				JOptionPane.PLAIN_MESSAGE, null, null, dv.ghiChu == null ? "" : dv.ghiChu);
		if (kq != null) {
			dv.ghiChu = kq.toString().trim();
			hienThiChecklist();
		}
	}

	private void suaDaDung(PhuTungRow p) {
		Object kq = JOptionPane.showInputDialog(this,
				"Số lượng đã dùng của " + p.ten + " (tối đa bằng số lượng đã cấp phát: " + p.capPhat + "):",
				"Cập nhật số lượng đã dùng", JOptionPane.PLAIN_MESSAGE, null, null, String.valueOf(p.daDung));
		if (kq == null) {
			return;
		}
		String s = kq.toString().trim();
		if (!s.matches("\\d{1,9}")) {
			baoLoi("Số lượng đã dùng phải là số nguyên không âm!");
			return;
		}
		if (Integer.parseInt(s) > p.capPhat) {
			baoLoi("Số lượng đã dùng không được vượt quá số lượng đã cấp phát (" + p.capPhat + ")!");
			return;
		}
		p.daDung = Integer.parseInt(s);
		hienThiPhuTung();
	}

	/** Thêm một phụ tùng phát sinh vào danh sách (dự kiến = số lượng nhập, đã dùng = 0, cấp phát = 0). */
	private void themPhuTungPhatSinh() {
		if (!coCongViec()) {
			return;
		}
		List<PhuTungChon> ds = layDanhSachPhuTung();
		List<PhuTungChon> conLai = new ArrayList<>();
		if (ds != null) {
			for (PhuTungChon c : ds) {
				boolean daCo = false;
				for (PhuTungRow p : dsPhuTung) {
					if (p.ma.equals(c.ma)) {
						daCo = true;
						break;
					}
				}
				if (!daCo) {
					conLai.add(c);
				}
			}
		}
		if (conLai.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Không còn phụ tùng nào để thêm vào công việc.", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		String[] hienThi = conLai.stream().map(PhuTungChon::toString).toArray(String[]::new);
		String[] kq = hienFormDialog("Thêm phụ tùng phát sinh",
				new String[] { "Phụ tùng *", "Số lượng dự kiến *" }, new String[] { hienThi[0], "" },
				new boolean[] { false, false }, new String[][] { hienThi, null }, v -> {
					if (!v[1].matches("\\d{1,9}") || Integer.parseInt(v[1]) <= 0) {
						return "Số lượng dự kiến phải là số nguyên lớn hơn 0!";
					}
					return null;
				});
		if (kq == null) {
			return;
		}
		for (PhuTungChon c : conLai) {
			if (c.toString().equals(kq[0])) {
				dsPhuTung.add(new PhuTungRow(c.ma, c.ten, c.donViTinh, Integer.parseInt(kq[1]), 0, 0));
				break;
			}
		}
		hienThiPhuTung();
	}

	/** Lập yêu cầu xuất kho cho các phụ tùng còn chờ cấp. */
	private void lapYeuCauXuat() {
		if (!coCongViec()) {
			return;
		}
		List<PhuTungRow> cho = new ArrayList<>();
		for (PhuTungRow p : dsPhuTung) {
			if (p.capPhat < p.duKien) {
				cho.add(p);
			}
		}
		if (cho.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Không có phụ tùng nào đang chờ xuất kho.", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		StringBuilder sb = new StringBuilder("Lập yêu cầu xuất kho cho các phụ tùng sau?\n");
		for (PhuTungRow p : cho) {
			sb.append("\n • ").append(p.ma).append(" - ").append(p.ten).append(": cần thêm ")
					.append(p.duKien - p.capPhat).append(" ").append(p.donViTinh == null ? "" : p.donViTinh);
		}
		int c = JOptionPane.showConfirmDialog(this, sb.toString(), "Lập yêu cầu xuất kho", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		if (c != JOptionPane.YES_OPTION) {
			return;
		}
		if (lapYeuCauXuatKho(maPhieuHienTai, cho)) {
			JOptionPane.showMessageDialog(this, "Đã lập yêu cầu xuất kho.", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
			taiDuLieu();
		} else {
			JOptionPane.showMessageDialog(this, "Lập yêu cầu xuất kho thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	// =====================================================================
	// LƯU TIẾN ĐỘ / HOÀN THÀNH CÔNG VIỆC
	// =====================================================================
	private void luu() {
		if (!coCongViec()) {
			return;
		}
		if (luuTienDo(maPhieuHienTai, new ArrayList<>(dsDichVu), new ArrayList<>(dsPhuTung))) {
			JOptionPane.showMessageDialog(this, "Đã lưu tiến độ công việc.", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
			taiDuLieu(); // nạp lại từ CSDL để khớp dữ liệu đã lưu
		} else {
			JOptionPane.showMessageDialog(this, "Lưu tiến độ thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void hoanThanh() {
		if (!coCongViec()) {
			return;
		}
		int chuaXong = 0;
		for (DichVuRow dv : dsDichVu) {
			if (!dv.hoanThanh) {
				chuaXong++;
			}
		}
		if (dsDichVu.isEmpty() || chuaXong > 0) {
			baoLoi(dsDichVu.isEmpty() ? "Công việc chưa có dịch vụ nào trong checklist!"
					: "Còn " + chuaXong + " dịch vụ chưa hoàn thành. Hãy đánh dấu hết các dịch vụ trước khi hoàn thành công việc!");
			return;
		}
		int c = JOptionPane.showConfirmDialog(this, "Xác nhận hoàn thành công việc " + maPhieuHienTai + "?",
				"Hoàn thành công việc", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (c != JOptionPane.YES_OPTION) {
			return;
		}
		// Lưu tiến độ trước rồi mới đánh dấu hoàn thành
		if (luuTienDo(maPhieuHienTai, new ArrayList<>(dsDichVu), new ArrayList<>(dsPhuTung))
				&& hoanThanhCongViec(maPhieuHienTai)) {
			JOptionPane.showMessageDialog(this, "Đã hoàn thành công việc " + maPhieuHienTai + ".", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
			maPhieuHienTai = null; // lần mở sau sẽ lấy công việc đang phụ trách khác (nếu có)
			if (quayVeDanhSach != null) {
				quayVeDanhSach.run();
			}
		} else {
			JOptionPane.showMessageDialog(this, "Hoàn thành công việc thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private boolean coCongViec() {
		if (maPhieuHienTai == null || maPhieuHienTai.isEmpty()) {
			baoLoi("Chưa chọn công việc. Hãy chọn \"Theo dõi công việc\" ở màn hình Danh sách công việc.");
			return false;
		}
		return true;
	}

	private void baoLoi(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.WARNING_MESSAGE);
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
			repaint();
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

	/** Ô "Đánh dấu": hộp kiểm tự vẽ (đã tích: nền xám đậm có dấu tích trắng; chưa tích: viền xanh). */
	private static class CheckRenderer extends JPanel implements TableCellRenderer {
		private static final long serialVersionUID = 1L;
		private boolean tick = false;

		CheckRenderer() {
			setOpaque(true);
		}

		@Override
		public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
			tick = Boolean.TRUE.equals(v);
			setBackground(mauNen(sel, r));
			return this;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			int x = PAD_TRAI;
			int y = (getHeight() - 16) / 2;
			if (tick) {
				g2.setColor(new Color(100, 104, 112));
				g2.fillRoundRect(x, y, 16, 16, 4, 4);
				g2.setColor(Color.WHITE);
				g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				g2.drawLine(x + 4, y + 8, x + 7, y + 11);
				g2.drawLine(x + 7, y + 11, x + 12, y + 5);
			} else {
				g2.setColor(Color.WHITE);
				g2.fillRoundRect(x, y, 16, 16, 4, 4);
				g2.setColor(PRIMARY);
				g2.setStroke(new BasicStroke(1.6f));
				g2.drawRoundRect(x, y, 15, 15, 4, 4);
			}
			g2.setColor(new Color(236, 240, 245));
			g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
			g2.dispose();
		}
	}

	/** Huy hiệu trạng thái bo tròn, màu theo nội dung. */
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
			if (TT_DV_XONG.equals(t) || TT_PT_DA_CAP.equals(t)) {
				nen = OK_BG;
				setForeground(OK_FG);
			} else if (TT_PT_CHO.equals(t)) {
				nen = new Color(255, 240, 220);
				setForeground(new Color(224, 112, 0));
			} else {
				nen = PRIMARY_LIGHT;
				setForeground(PRIMARY);
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

	// =====================================================================
	// THANH TIẾN ĐỘ & NÚT MÀU
	// =====================================================================

	/** Thanh tiến độ mảnh: nền xanh nhạt, phần đã hoàn thành màu cam. */
	private static class ThanhTienDo extends JComponent {
		private static final long serialVersionUID = 1L;
		private double tyLe = 0;

		ThanhTienDo() {
			setPreferredSize(new Dimension(100, 8));
			setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
		}

		void setTyLe(double tyLe) {
			this.tyLe = Math.max(0, Math.min(1, tyLe));
			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(new Color(226, 232, 244));
			g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
			int w = (int) Math.round(getWidth() * tyLe);
			if (w > 0) {
				g2.setColor(CAM);
				g2.fillRoundRect(0, 0, Math.max(w, 8), getHeight(), 8, 8);
			}
			g2.dispose();
		}
	}

	/** Nút bo góc có màu nền tùy chọn (dùng cho nút "Hoàn thành công việc" màu xanh lá). */
	private static class NutMau extends JButton {
		private static final long serialVersionUID = 1L;
		private final Color nen, nenHover;
		private boolean hover = false;

		NutMau(String text, Color nen, Color nenHover, String iconPath) {
			super(text);
			this.nen = nen;
			this.nenHover = nenHover;
			setFont(FONT_BOLD);
			setForeground(Color.WHITE);
			setFocusPainted(false);
			setBorderPainted(false);
			setContentAreaFilled(false);
			setOpaque(false);
			setBorder(new EmptyBorder(0, 16, 0, 16));
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			setIcon(new SlotIcon(iconPath, 16, 16, new Color(255, 255, 255, 190)));
			setIconTextGap(8);
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
			g2.setColor(hover ? nenHover : nen);
			g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
			g2.dispose();
			super.paintComponent(g);
		}
	}
}
