package gui;

import static gui.UITheme.*;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

/**
 * Màn hình DANH SÁCH CÔNG VIỆC của kỹ thuật viên (JPanel) - nhúng vào CardLayout của TrangChuKyThuatVienGUI.
 *
 * Gồm (giống ảnh mẫu):
 *  1. Thẻ "Tra cứu công việc": Mã phiếu / Biển số, Ngày phân công, Trạng thái; nút Tra cứu, Lọc phiếu, Đặt lại
 *  2. Bốn thẻ thống kê: Tất cả công việc, Đang thực hiện, Chờ bắt đầu, Đã hoàn thành
 *  3. Thẻ "Công việc được phân công": lịch theo tuần (Thứ hai -> Chủ nhật), mỗi công việc là một thẻ màu theo trạng thái
 *
 * Nút trên mỗi thẻ công việc:
 *  - Đang thực hiện -> "Theo dõi công việc"   : gọi {@code moTheoDoi} (chuyển sang TheoDoiCongViecGUI)
 *  - Chờ bắt đầu    -> "Lập phiếu sửa chữa"   : gọi {@code moLapPhieuSuaChua} (có thể null)
 *  - Đã hoàn thành  -> "Xem chi tiết"
 *
 * KHÔNG có dữ liệu mẫu và chưa gắn với CSDL cụ thể (sẽ cập nhật theo CSDL mới).
 * Hàm có "TODO": layCongViecTrongTuan.
 */
public class DanhSachCongViecGUI extends JPanel {
	private static final long serialVersionUID = 1L;

	// =====================================================================
	// ICON CỦA MÀN HÌNH NÀY - TỰ NHẬP ĐƯỜNG DẪN VÀO GIỮA CẶP DẤU "".
	// Ví dụ: "/images/icon_thongKe.png" hoặc chỉ "icon_thongKe.png" (tự hiểu là nằm trong /images/).
	// Để rỗng thì hiện khung viền nét đứt. (Các icon dùng chung như IMG_TIM_KIEM... nằm trong UITheme.)
	// =====================================================================
	private static final String ICON_TAT_CA = ""; // TODO: icon thẻ "Tất cả công việc"
	private static final String ICON_DANG_THUC_HIEN = ""; // TODO: icon thẻ "Đang thực hiện"
	private static final String ICON_CHO_BAT_DAU = ""; // TODO: icon thẻ "Chờ bắt đầu"
	private static final String ICON_DA_HOAN_THANH = ""; // TODO: icon thẻ "Đã hoàn thành"
	private static final String ICON_LOC = ""; // TODO: icon phễu ở nút "Lọc phiếu"

	// ====== Các trạng thái công việc ======
	public static final String TT_CHO_BAT_DAU = "Chờ bắt đầu";
	public static final String TT_DANG_THUC_HIEN = "Đang thực hiện";
	public static final String TT_DA_HOAN_THANH = "Đã hoàn thành";
	private static final String TAT_CA_TRANG_THAI = "Tất cả trạng thái";

	private static final String[] TEN_THU = { "Thứ hai", "Thứ ba", "Thứ tư", "Thứ năm", "Thứ sáu", "Thứ bảy",
			"Chủ nhật" };
	private static final int MIN_CAO_COT = 560;

	private static final Color NEN_HEADER = new Color(244, 246, 250);
	private static final Color NEN_COT = new Color(248, 250, 253);
	private static final Color VIEN_COT = new Color(226, 230, 236);

	private static final DateTimeFormatter FMT_NGAY = DateTimeFormatter.ofPattern("dd/MM/uuuu")
			.withResolverStyle(ResolverStyle.STRICT);
	private static final DateTimeFormatter FMT_NGAY_THANG = DateTimeFormatter.ofPattern("dd/MM");

	/** Một công việc được phân công. TODO: có thể thay bằng entity của project (sau khi có CSDL mới). */
	private static class CongViecRow {
		final String maPhieu, bienSo, hangXe, loaiXe, yeuCau, trangThai;
		final LocalDate ngayPhanCong;

		CongViecRow(String maPhieu, String bienSo, String hangXe, String loaiXe, String yeuCau, String trangThai,
				LocalDate ngayPhanCong) {
			this.maPhieu = maPhieu;
			this.bienSo = bienSo;
			this.hangXe = hangXe;
			this.loaiXe = loaiXe;
			this.yeuCau = yeuCau;
			this.trangThai = trangThai;
			this.ngayPhanCong = ngayPhanCong;
		}
	}

	private final Runnable quayVeTrangChu;
	private final Consumer<String> moTheoDoi;
	private final Consumer<String> moLapPhieuSuaChua;

	private String maNV = "";
	private String hoTenKTV = "";
	private LocalDate ngayThamChieu = LocalDate.now();
	private int rongDaDung = 0;

	private final List<CongViecRow> dsTuan = new ArrayList<>(); // toàn bộ công việc trong tuần (từ DAO)
	private final List<CongViecRow> dsHienThi = new ArrayList<>(); // sau khi lọc theo trạng thái

	// ---- Tra cứu ----
	private Input txtTuKhoa;
	private NgayChonField dtNgay;
	private JComboBox<String> cboTrangThai;
	private JLabel lblSub;

	// ---- Thống kê ----
	private final JLabel[] lblSoLieu = new JLabel[4];

	// ---- Lịch tuần ----
	private JLabel lblKhoangTuan, lblSoCongViec;
	private JPanel luoiTuan;

	/**
	 * @param quayVeTrangChu    nút "Trang chủ kỹ thuật viên"
	 * @param moTheoDoi         nút "Theo dõi công việc" - nhận mã phiếu của công việc, chuyển sang TheoDoiCongViecGUI
	 * @param moLapPhieuSuaChua nút "Lập phiếu sửa chữa" (có thể null) - nhận mã phiếu của công việc
	 */
	public DanhSachCongViecGUI(Runnable quayVeTrangChu, Consumer<String> moTheoDoi,
			Consumer<String> moLapPhieuSuaChua) {
		this.quayVeTrangChu = quayVeTrangChu;
		this.moTheoDoi = moTheoDoi;
		this.moLapPhieuSuaChua = moLapPhieuSuaChua;
		setLayout(new BorderLayout());
		setBackground(BG);

		ScrollablePanel trangND = new ScrollablePanel(new BorderLayout(0, 16));
		trangND.setBackground(BG);
		trangND.setBorder(new EmptyBorder(22, 28, 28, 28));
		trangND.add(taoTieuDeTrang(), BorderLayout.NORTH);

		JPanel cacThe = new JPanel(new GridBagLayout());
		cacThe.setOpaque(false);
		themDong(cacThe, taoTheTraCuu(), 0, 16);
		themDong(cacThe, taoHangThongKe(), 1, 16);
		themDong(cacThe, taoTheLich(), 2, 0);
		trangND.add(cacThe, BorderLayout.CENTER);

		JScrollPane sp = new JScrollPane(trangND, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setBorder(null);
		sp.getVerticalScrollBar().setUnitIncrement(16);
		add(sp, BorderLayout.CENTER);

		datLai();
	}

	/**
	 * Gán kỹ thuật viên đang đăng nhập (dùng để tải đúng công việc được phân công). Gọi sau khi tạo màn hình.
	 * @param maNV  mã nhân viên
	 * @param hoTen họ tên hiển thị ở dòng mô tả
	 */
	public void setKyThuatVien(String maNV, String hoTen) {
		this.maNV = maNV == null ? "" : maNV;
		this.hoTenKTV = hoTen == null ? "" : hoTen;
		capNhatTieuDe();
	}

	/** Gọi mỗi khi chuyển sang màn hình này: tải lại công việc của tuần đang xem (giữ nguyên bộ lọc). */
	public void refresh() {
		taiLaiTuan();
	}

	// =====================================================================
	// TODO: KẾT NỐI CƠ SỞ DỮ LIỆU (DAO) - BẠN CHỈ CẦN SỬA HÀM TRONG KHỐI NÀY
	// =====================================================================

	/**
	 * Lấy các công việc được phân công cho kỹ thuật viên trong khoảng ngày (một tuần: thứ hai -> chủ nhật).
	 * @param maNV    mã kỹ thuật viên đang đăng nhập
	 * @param tuKhoa  mã phiếu hoặc biển số cần tìm ("" = không lọc), nên tìm gần đúng
	 * @param tuNgay  ngày đầu tuần (thứ hai)
	 * @param denNgay ngày cuối tuần (chủ nhật)
	 * TODO: gọi DAO rồi đổi mỗi dòng thành new CongViecRow(maPhieu, bienSo, hangXe, loaiXe, yeuCau, trangThai,
	 * ngayPhanCong). trangThai phải là một trong TT_CHO_BAT_DAU, TT_DANG_THUC_HIEN, TT_DA_HOAN_THANH.
	 * Việc lọc theo trạng thái được xử lý ở giao diện.
	 */
	private List<CongViecRow> layCongViecTrongTuan(String maNV, String tuKhoa, LocalDate tuNgay, LocalDate denNgay) {
		List<CongViecRow> ds = new ArrayList<>();
		// TODO: ds.add(new CongViecRow(...));
		return ds;
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
		JLabel lblTitle = new JLabel("Danh sách công việc");
		lblTitle.setFont(FONT_TITLE);
		lblTitle.setForeground(TITLE);
		lblSub = new JLabel(" ");
		lblSub.setFont(FONT_LABEL);
		lblSub.setForeground(TEXT_MUTED);
		tieuDe.add(lblTitle);
		tieuDe.add(lblSub);
		header.add(tieuDe, BorderLayout.WEST);

		ActionButton btnTrangChu = new ActionButton("Trang chủ kỹ thuật viên", ActionButton.KIEU_VIEN_XANH,
				IMG_QUAY_LAI);
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

	private void capNhatTieuDe() {
		StringBuilder sb = new StringBuilder("Các công việc được phân công cho ");
		sb.append(hoTenKTV.isEmpty() ? "kỹ thuật viên" : hoTenKTV);
		if (!maNV.isEmpty()) {
			sb.append(" • ").append(maNV);
		}
		sb.append(" • ").append(ngayThamChieu.format(FMT_NGAY)).append(".");
		lblSub.setText(sb.toString());
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
	// THẺ 1: TRA CỨU CÔNG VIỆC
	// =====================================================================
	private CardPanel taoTheTraCuu() {
		CardPanel the = new CardPanel("Tra cứu công việc", "");
		the.getBody().setBorder(new EmptyBorder(16, 20, 20, 20));

		txtTuKhoa = new Input("Nhập mã phiếu hoặc biển số", false);
		dtNgay = new NgayChonField();
		dtNgay.setNgay(LocalDate.now());
		cboTrangThai = createComboBox(TAT_CA_TRANG_THAI, TT_CHO_BAT_DAU, TT_DANG_THUC_HIEN, TT_DA_HOAN_THANH);

		ActionButton btnTraCuu = new ActionButton("Tra cứu", ActionButton.KIEU_CHINH, IMG_TIM_KIEM);
		ActionButton btnLoc = new ActionButton("Lọc phiếu", ActionButton.KIEU_CHINH, ICON_LOC);
		ActionButton btnDatLai = new ActionButton("Đặt lại", ActionButton.KIEU_VIEN_XANH, IMG_DAT_LAI);
		btnTraCuu.addActionListener(e -> traCuu());
		btnLoc.addActionListener(e -> locPhieu());
		btnDatLai.addActionListener(e -> datLai());
		txtTuKhoa.addActionListener(e -> traCuu());

		JPanel hang = new JPanel(new GridBagLayout());
		hang.setOpaque(false);
		themCot(hang, labeled("Mã phiếu / Biển số", txtTuKhoa), 0, 1.0, 16);
		themCot(hang, labeled("Ngày phân công", dtNgay), 1, 0.85, 16);
		themCot(hang, labeledFixed(" ", btnTraCuu), 2, 0, 16);
		themCot(hang, labeled("Trạng thái", cboTrangThai), 3, 1.0, 16);
		themCot(hang, labeledFixed(" ", btnLoc), 4, 0, 16);
		themCot(hang, labeledFixed(" ", btnDatLai), 5, 0, 0);

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
	// THẺ 2: THỐNG KÊ
	// =====================================================================
	private JPanel taoHangThongKe() {
		JPanel hang = new JPanel(new GridLayout(1, 4, 16, 0));
		hang.setOpaque(false);
		hang.add(taoTheThongKe(0, "Tất cả công việc", ICON_TAT_CA));
		hang.add(taoTheThongKe(1, "Đang thực hiện", ICON_DANG_THUC_HIEN));
		hang.add(taoTheThongKe(2, "Chờ bắt đầu", ICON_CHO_BAT_DAU));
		hang.add(taoTheThongKe(3, "Đã hoàn thành", ICON_DA_HOAN_THANH));
		return hang;
	}

	private JPanel taoTheThongKe(int viTri, String nhan, String iconPath) {
		TheTrang the = new TheTrang();
		the.setLayout(new BorderLayout(0, 6));
		the.setBorder(new EmptyBorder(16, 16, 14, 16));

		JPanel dau = new JPanel(new BorderLayout(10, 0));
		dau.setOpaque(false);
		JPanel tron = new JPanel(new GridBagLayout()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(PRIMARY_LIGHT);
				g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		tron.setOpaque(false);
		tron.setPreferredSize(new Dimension(36, 36));
		tron.add(new ImageSlot(iconPath, 18, 18, false));
		JLabel lblNhan = new JLabel(nhan);
		lblNhan.setFont(FONT_SECTION);
		lblNhan.setForeground(TEXT_DARK);
		dau.add(tron, BorderLayout.WEST);
		dau.add(lblNhan, BorderLayout.CENTER);

		lblSoLieu[viTri] = new JLabel("0");
		lblSoLieu[viTri].setFont(FONT_TITLE.deriveFont(30f));
		lblSoLieu[viTri].setForeground(TITLE);

		the.add(dau, BorderLayout.NORTH);
		the.add(lblSoLieu[viTri], BorderLayout.CENTER);
		return the;
	}

	/** Thẻ trắng bo góc có viền mảnh. */
	private static class TheTrang extends JPanel {
		private static final long serialVersionUID = 1L;

		TheTrang() {
			setOpaque(false);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(Color.WHITE);
			g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
			g2.setColor(CARD_BORDER);
			g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
			g2.dispose();
			super.paintComponent(g);
		}
	}

	// =====================================================================
	// THẺ 3: LỊCH TUẦN
	// =====================================================================
	private CardPanel taoTheLich() {
		CardPanel the = new CardPanel("Công việc được phân công", "");
		the.getBody().setBorder(new EmptyBorder(20, 20, 28, 20));

		// --- Thanh công cụ: nhãn chế độ xem + khoảng tuần + nút chuyển tuần ---
		JPanel thanhCu = new JPanel(new BorderLayout());
		thanhCu.setOpaque(false);

		JPanel trai = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		trai.setOpaque(false);
		JLabel lblCheDo = new JLabel("Lịch theo tuần", SwingConstants.CENTER) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(PRIMARY_LIGHT);
				g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
				g2.setColor(new Color(190, 215, 245));
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		lblCheDo.setFont(FONT_SMALL_BOLD);
		lblCheDo.setForeground(PRIMARY);
		lblCheDo.setPreferredSize(new Dimension(130, 32));
		lblKhoangTuan = new JLabel(" ");
		lblKhoangTuan.setFont(FONT_SECTION.deriveFont(17f));
		lblKhoangTuan.setForeground(TEXT_DARK);
		lblKhoangTuan.setBorder(new EmptyBorder(0, 60, 0, 0));
		trai.add(lblCheDo);
		trai.add(lblKhoangTuan);

		JPanel phai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		phai.setOpaque(false);
		NutVien btnHienTai = new NutVien("Tuần hiện tại", PRIMARY, new Color(190, 215, 245));
		NutVien btnTruoc = new NutVien("‹  Tuần trước", TEXT_DARK, new Color(205, 211, 220));
		NutVien btnSau = new NutVien("Tuần sau  ›", TEXT_DARK, new Color(205, 211, 220));
		btnHienTai.addActionListener(e -> chuyenTuan(0));
		btnTruoc.addActionListener(e -> chuyenTuan(-1));
		btnSau.addActionListener(e -> chuyenTuan(1));
		phai.add(btnHienTai);
		phai.add(btnTruoc);
		phai.add(btnSau);

		thanhCu.add(trai, BorderLayout.WEST);
		thanhCu.add(phai, BorderLayout.EAST);

		// --- Thanh chú thích ---
		RoundedPanel chuThich = new RoundedPanel(8, new Color(245, 247, 250));
		chuThich.setLayout(new FlowLayout(FlowLayout.LEFT, 18, 11));
		lblSoCongViec = new JLabel("0 công việc trong tuần");
		lblSoCongViec.setFont(FONT_BOLD);
		lblSoCongViec.setForeground(TEXT_DARK);
		chuThich.add(lblSoCongViec);
		chuThich.add(muc("●  " + TT_CHO_BAT_DAU, mauChu(TT_CHO_BAT_DAU)));
		chuThich.add(muc("●  " + TT_DANG_THUC_HIEN, mauChu(TT_DANG_THUC_HIEN)));
		chuThich.add(muc("●  " + TT_DA_HOAN_THANH, mauChu(TT_DA_HOAN_THANH)));

		JPanel dau = new JPanel(new GridBagLayout());
		dau.setOpaque(false);
		themDong(dau, thanhCu, 0, 16);
		themDong(dau, chuThich, 1, 18);

		// --- Lưới 7 ngày ---
		luoiTuan = new JPanel(new GridLayout(1, 7));
		luoiTuan.setBackground(NEN_COT);
		luoiTuan.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				// Chiều rộng đổi -> dựng lại thẻ để chữ "Yêu cầu" xuống dòng đúng bề rộng cột
				if (Math.abs(luoiTuan.getWidth() - rongDaDung) > 8) {
					dungLich();
				}
			}
		});
		JPanel khungLuoi = new JPanel(new BorderLayout());
		khungLuoi.setBorder(new LineBorder(VIEN_COT));
		khungLuoi.add(luoiTuan, BorderLayout.CENTER);

		the.getBody().add(dau, BorderLayout.NORTH);
		the.getBody().add(khungLuoi, BorderLayout.CENTER);
		return the;
	}

	private static JLabel muc(String text, Color mau) {
		JLabel l = new JLabel(text);
		l.setFont(FONT_SMALL);
		l.setForeground(mau);
		return l;
	}

	// =====================================================================
	// NGHIỆP VỤ: TRA CỨU / LỌC / CHUYỂN TUẦN
	// =====================================================================

	/** Tra cứu theo mã phiếu / biển số và ngày phân công (tuần chứa ngày đó), rồi lọc theo trạng thái. */
	private void traCuu() {
		LocalDate ngay = dtNgay.getNgay();
		ngayThamChieu = (ngay == null) ? LocalDate.now() : ngay;
		taiLaiTuan();
	}

	/** Chỉ lọc theo trạng thái trên dữ liệu đã tải (không gọi lại DAO). */
	private void locPhieu() {
		apDungLocTrangThai();
		dungLich();
	}

	/** Đưa bộ lọc về mặc định (ngày hôm nay, tất cả trạng thái) và tải lại. */
	private void datLai() {
		txtTuKhoa.setText("");
		cboTrangThai.setSelectedIndex(0);
		ngayThamChieu = LocalDate.now();
		dtNgay.setNgay(ngayThamChieu);
		taiLaiTuan();
	}

	/** delta = 0: tuần hiện tại; -1: tuần trước; +1: tuần sau. */
	private void chuyenTuan(int delta) {
		ngayThamChieu = (delta == 0) ? LocalDate.now() : ngayThamChieu.plusWeeks(delta);
		dtNgay.setNgay(ngayThamChieu);
		taiLaiTuan();
	}

	private LocalDate dauTuan() {
		return ngayThamChieu.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
	}

	/** Lấy công việc của tuần đang xem (qua DAO), cập nhật thống kê và vẽ lại lịch. */
	private void taiLaiTuan() {
		LocalDate dau = dauTuan();
		List<CongViecRow> ds = layCongViecTrongTuan(maNV, txtTuKhoa.getText().trim(), dau, dau.plusDays(6));
		dsTuan.clear();
		if (ds != null) {
			dsTuan.addAll(ds);
		}
		capNhatSoLieu();
		apDungLocTrangThai();
		capNhatTieuDe();
		dungLich();
	}

	private void apDungLocTrangThai() {
		String tt = cboTrangThai.getSelectedIndex() > 0 ? (String) cboTrangThai.getSelectedItem() : null;
		dsHienThi.clear();
		for (CongViecRow cv : dsTuan) {
			if (tt == null || tt.equals(cv.trangThai)) {
				dsHienThi.add(cv);
			}
		}
	}

	private void capNhatSoLieu() {
		int cho = 0, dang = 0, xong = 0;
		for (CongViecRow cv : dsTuan) {
			if (TT_CHO_BAT_DAU.equals(cv.trangThai)) {
				cho++;
			} else if (TT_DANG_THUC_HIEN.equals(cv.trangThai)) {
				dang++;
			} else if (TT_DA_HOAN_THANH.equals(cv.trangThai)) {
				xong++;
			}
		}
		lblSoLieu[0].setText(String.valueOf(dsTuan.size()));
		lblSoLieu[1].setText(String.valueOf(dang));
		lblSoLieu[2].setText(String.valueOf(cho));
		lblSoLieu[3].setText(String.valueOf(xong));
	}

	// =====================================================================
	// VẼ LỊCH TUẦN
	// =====================================================================
	private void dungLich() {
		rongDaDung = luoiTuan.getWidth();
		LocalDate dau = dauTuan();
		int rongChu = Math.max(100, (rongDaDung > 0 ? rongDaDung / 7 : 160) - 40);

		luoiTuan.removeAll();
		for (int i = 0; i < 7; i++) {
			LocalDate ngay = dau.plusDays(i);
			List<CongViecRow> viec = new ArrayList<>();
			for (CongViecRow cv : dsHienThi) {
				if (ngay.equals(cv.ngayPhanCong)) {
					viec.add(cv);
				}
			}
			luoiTuan.add(taoCotNgay(i, ngay, viec, rongChu));
		}
		lblKhoangTuan.setText(dinhDangKhoangTuan(dau, dau.plusDays(6)));
		lblSoCongViec.setText(dsHienThi.size() + " công việc trong tuần");
		luoiTuan.revalidate();
		luoiTuan.repaint();
	}

	private JPanel taoCotNgay(int viTri, LocalDate ngay, List<CongViecRow> viec, int rongChu) {
		JPanel cot = new JPanel(new BorderLayout()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				return new Dimension(d.width, Math.max(d.height, MIN_CAO_COT));
			}
		};
		cot.setBackground(NEN_COT);
		if (viTri > 0) {
			cot.setBorder(new MatteBorder(0, 1, 0, 0, VIEN_COT));
		}

		// --- Tiêu đề cột: Thứ + ngày/tháng (+ huy hiệu "0" nếu không có công việc) ---
		JPanel header = new JPanel(new BorderLayout());
		header.setBackground(NEN_HEADER);
		header.setBorder(new EmptyBorder(14, 12, 12, 10));
		JPanel chu = new JPanel();
		chu.setOpaque(false);
		chu.setLayout(new BoxLayout(chu, BoxLayout.Y_AXIS));
		JLabel lblThu = new JLabel(TEN_THU[viTri]);
		lblThu.setFont(FONT_BOLD);
		lblThu.setForeground(TEXT_DARK);
		JLabel lblNgay = new JLabel(ngay.format(FMT_NGAY_THANG));
		lblNgay.setFont(FONT_LABEL);
		lblNgay.setForeground(TEXT_MUTED);
		lblNgay.setBorder(new EmptyBorder(4, 0, 0, 0));
		chu.add(lblThu);
		chu.add(lblNgay);
		header.add(chu, BorderLayout.WEST);
		if (viec.isEmpty()) {
			header.add(taoHuyHieuSo("0"), BorderLayout.EAST);
		}
		cot.add(header, BorderLayout.NORTH);

		// --- Thân cột: các thẻ công việc ---
		JPanel xep = new JPanel();
		xep.setOpaque(false);
		xep.setLayout(new BoxLayout(xep, BoxLayout.Y_AXIS));
		xep.setBorder(new EmptyBorder(2, 4, 8, 4));
		for (CongViecRow cv : viec) {
			xep.add(new TheCongViec(cv, rongChu, e -> xuLyHanhDong(cv)));
			xep.add(Box.createVerticalStrut(8));
		}
		JPanel than = new JPanel(new BorderLayout());
		than.setOpaque(false);
		than.add(xep, BorderLayout.NORTH);
		cot.add(than, BorderLayout.CENTER);
		return cot;
	}

	private static JComponentTron taoHuyHieuSo(String so) {
		return new JComponentTron(so);
	}

	/** Huy hiệu tròn xám nhỏ chứa một số. */
	private static class JComponentTron extends JLabel {
		private static final long serialVersionUID = 1L;

		JComponentTron(String so) {
			super(so, SwingConstants.CENTER);
			setFont(FONT_SMALL_BOLD);
			setForeground(TEXT_MUTED);
			setPreferredSize(new Dimension(22, 22));
			setOpaque(false);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(new Color(228, 231, 236));
			g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
			g2.dispose();
			super.paintComponent(g);
		}
	}

	/** Khoảng tuần: "12 – 18 Tháng 10, 2026" (xử lý cả trường hợp tuần nằm giữa hai tháng / hai năm). */
	private static String dinhDangKhoangTuan(LocalDate dau, LocalDate cuoi) {
		if (dau.getYear() == cuoi.getYear() && dau.getMonth() == cuoi.getMonth()) {
			return dau.getDayOfMonth() + " – " + cuoi.getDayOfMonth() + " Tháng " + dau.getMonthValue() + ", "
					+ dau.getYear();
		}
		if (dau.getYear() == cuoi.getYear()) {
			return dau.getDayOfMonth() + " Tháng " + dau.getMonthValue() + " – " + cuoi.getDayOfMonth() + " Tháng "
					+ cuoi.getMonthValue() + ", " + cuoi.getYear();
		}
		return dau.getDayOfMonth() + " Tháng " + dau.getMonthValue() + ", " + dau.getYear() + " – "
				+ cuoi.getDayOfMonth() + " Tháng " + cuoi.getMonthValue() + ", " + cuoi.getYear();
	}

	// =====================================================================
	// HÀNH ĐỘNG TRÊN THẺ CÔNG VIỆC
	// =====================================================================
	private void xuLyHanhDong(CongViecRow cv) {
		if (TT_DANG_THUC_HIEN.equals(cv.trangThai)) {
			if (moTheoDoi != null) {
				moTheoDoi.accept(cv.maPhieu);
			}
		} else if (TT_CHO_BAT_DAU.equals(cv.trangThai)) {
			if (moLapPhieuSuaChua != null) {
				moLapPhieuSuaChua.accept(cv.maPhieu);
			} else {
				JOptionPane.showMessageDialog(this, "Chức năng lập phiếu sửa chữa cho " + cv.maPhieu
						+ " đang được xây dựng.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
			}
		} else {
			xemChiTiet(cv);
		}
	}

	private void xemChiTiet(CongViecRow cv) {
		// TODO: có thể tải thêm chi tiết (checklist, phụ tùng đã dùng...) từ DAO để hiển thị
		JOptionPane.showMessageDialog(this,
				"Mã phiếu: " + cv.maPhieu + "\nBiển số: " + cv.bienSo + "\nHãng xe: " + cv.hangXe + "\nLoại xe: "
						+ cv.loaiXe + "\nNgày phân công: " + cv.ngayPhanCong.format(FMT_NGAY) + "\nTrạng thái: "
						+ cv.trangThai + "\nYêu cầu: " + cv.yeuCau,
				"Chi tiết công việc", JOptionPane.INFORMATION_MESSAGE);
	}

	// =====================================================================
	// MÀU THEO TRẠNG THÁI
	// =====================================================================
	private static Color mauChu(String tt) {
		if (TT_CHO_BAT_DAU.equals(tt)) {
			return new Color(176, 112, 0);
		}
		if (TT_DANG_THUC_HIEN.equals(tt)) {
			return PRIMARY;
		}
		return OK_FG;
	}

	private static Color mauNenThe(String tt) {
		if (TT_CHO_BAT_DAU.equals(tt)) {
			return new Color(255, 251, 235);
		}
		if (TT_DANG_THUC_HIEN.equals(tt)) {
			return new Color(238, 245, 255);
		}
		return new Color(240, 250, 243);
	}

	private static Color mauVienThe(String tt) {
		if (TT_CHO_BAT_DAU.equals(tt)) {
			return new Color(240, 200, 90);
		}
		if (TT_DANG_THUC_HIEN.equals(tt)) {
			return new Color(150, 190, 240);
		}
		return new Color(150, 205, 170);
	}

	private static String tenNut(String tt) {
		if (TT_CHO_BAT_DAU.equals(tt)) {
			return "Lập phiếu sửa chữa";
		}
		if (TT_DANG_THUC_HIEN.equals(tt)) {
			return "Theo dõi công việc";
		}
		return "Xem chi tiết";
	}

	// =====================================================================
	// THẺ CÔNG VIỆC TRONG LỊCH
	// =====================================================================
	private static class TheCongViec extends JPanel {
		private static final long serialVersionUID = 1L;
		private final Color nen, vien;

		TheCongViec(CongViecRow cv, int rongChu, java.awt.event.ActionListener hanhDong) {
			this.nen = mauNenThe(cv.trangThai);
			this.vien = mauVienThe(cv.trangThai);
			setOpaque(false);
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			setBorder(new EmptyBorder(10, 12, 12, 12));
			setAlignmentX(Component.LEFT_ALIGNMENT);

			add(dong("●  " + cv.trangThai, FONT_SMALL_BOLD, mauChu(cv.trangThai), 0));
			add(dong(cv.bienSo, FONT_SECTION.deriveFont(15f), TEXT_DARK, 6));
			add(dong(cv.maPhieu, FONT_SMALL, TEXT_MUTED, 4));
			add(dong("Hãng xe: " + cv.hangXe, FONT_SMALL, TEXT_DARK, 10));
			add(dong("Loại xe: " + cv.loaiXe, FONT_SMALL, TEXT_DARK, 2));
			add(dong("Yêu cầu", FONT_SMALL, TEXT_MUTED, 10));
			add(dongNhieu(cv.yeuCau, rongChu));

			add(Box.createVerticalStrut(8));
			NutVien nut = new NutVien(tenNut(cv.trangThai), mauChu(cv.trangThai), vien);
			nut.setAlignmentX(Component.LEFT_ALIGNMENT);
			nut.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
			nut.addActionListener(hanhDong);
			add(nut);
		}

		private static JLabel dong(String text, java.awt.Font font, Color mau, int tren) {
			JLabel l = new JLabel(text);
			l.setFont(font);
			l.setForeground(mau);
			l.setBorder(new EmptyBorder(tren, 0, 0, 0));
			l.setAlignmentX(Component.LEFT_ALIGNMENT);
			return l;
		}

		/** Đoạn chữ tự xuống dòng theo bề rộng cho trước. */
		private static JLabel dongNhieu(String text, int rong) {
			String s = text == null ? "" : text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
			JLabel l = new JLabel("<html><body style='width:" + rong + "px'>" + s + "</body></html>");
			l.setFont(FONT_SMALL);
			l.setForeground(TEXT_DARK);
			l.setBorder(new EmptyBorder(2, 0, 0, 0));
			l.setAlignmentX(Component.LEFT_ALIGNMENT);
			return l;
		}

		@Override
		public Dimension getMaximumSize() {
			return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(nen);
			g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
			g2.setColor(vien);
			g2.setStroke(new BasicStroke(1.4f));
			g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
			g2.dispose();
			super.paintComponent(g);
		}
	}

	/** Nút nhỏ nền trắng, viền và chữ cùng tông màu. */
	private static class NutVien extends JButton {
		private static final long serialVersionUID = 1L;
		private final Color mauVien;
		private boolean hover = false;

		NutVien(String text, Color mauChu, Color mauVien) {
			super(text);
			this.mauVien = mauVien;
			setFont(FONT_SMALL_BOLD);
			setForeground(mauChu);
			setFocusPainted(false);
			setBorderPainted(false);
			setContentAreaFilled(false);
			setOpaque(false);
			setBorder(new EmptyBorder(0, 14, 0, 14));
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
			return new Dimension(d.width, Math.max(d.height, 30));
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(hover ? new Color(246, 249, 253) : Color.WHITE);
			g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
			g2.setColor(mauVien);
			g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
			g2.dispose();
			super.paintComponent(g);
		}
	}

	// =====================================================================
	// Ô CHỌN NGÀY DẠNG DROPDOWN (bấm vào hiện lịch tháng)
	// =====================================================================
	private static class NgayChonField extends Input {
		private static final long serialVersionUID = 1L;
		private LocalDate ngay;

		NgayChonField() {
			super(null, false);
			setEditable(false);
			setFocusable(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			setBorder(new EmptyBorder(0, 12, 0, 36));
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					hienLich();
				}
			});
		}

		LocalDate getNgay() {
			return ngay;
		}

		void setNgay(LocalDate d) {
			this.ngay = d;
			setText(d == null ? "" : d.format(FMT_NGAY));
		}

		private void hienLich() {
			JPopupMenu popup = new JPopupMenu();
			popup.setBorder(new LineBorder(BORDER));
			popup.add(new LichPanel(ngay == null ? LocalDate.now() : ngay, d -> {
				setNgay(d);
				popup.setVisible(false);
			}));
			popup.show(this, 0, getHeight() + 2);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(TEXT_DARK);
			g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			int cx = getWidth() - 22;
			int cy = getHeight() / 2;
			g2.drawLine(cx - 4, cy - 2, cx, cy + 2);
			g2.drawLine(cx, cy + 2, cx + 4, cy - 2);
			g2.dispose();
		}
	}

	/** Bảng lịch tháng: chọn ngày, chuyển tháng trước / sau. */
	private static class LichPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private YearMonth thang;
		private final LocalDate dangChon;
		private final Consumer<LocalDate> khiChon;
		private final JLabel lblThang = new JLabel("", SwingConstants.CENTER);
		private final JPanel luoi = new JPanel(new GridLayout(0, 7, 2, 2));

		LichPanel(LocalDate dangChon, Consumer<LocalDate> khiChon) {
			super(new BorderLayout(0, 8));
			this.dangChon = dangChon;
			this.khiChon = khiChon;
			this.thang = YearMonth.from(dangChon);
			setBackground(Color.WHITE);
			setBorder(new EmptyBorder(10, 10, 10, 10));
			setPreferredSize(new Dimension(290, 270));

			JPanel top = new JPanel(new BorderLayout());
			top.setOpaque(false);
			lblThang.setFont(FONT_BOLD);
			lblThang.setForeground(TITLE);
			top.add(nutThang("‹", -1), BorderLayout.WEST);
			top.add(lblThang, BorderLayout.CENTER);
			top.add(nutThang("›", 1), BorderLayout.EAST);

			luoi.setOpaque(false);
			add(top, BorderLayout.NORTH);
			add(luoi, BorderLayout.CENTER);
			capNhat();
		}

		private JLabel nutThang(String text, int delta) {
			JLabel l = new JLabel(text, SwingConstants.CENTER);
			l.setFont(FONT_SECTION);
			l.setForeground(PRIMARY);
			l.setPreferredSize(new Dimension(34, 28));
			l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			l.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					thang = thang.plusMonths(delta);
					capNhat();
				}
			});
			return l;
		}

		private void capNhat() {
			lblThang.setText("Tháng " + thang.getMonthValue() + " / " + thang.getYear());
			luoi.removeAll();
			for (String t : new String[] { "T2", "T3", "T4", "T5", "T6", "T7", "CN" }) {
				JLabel l = new JLabel(t, SwingConstants.CENTER);
				l.setFont(FONT_SMALL_BOLD);
				l.setForeground(TEXT_MUTED);
				luoi.add(l);
			}
			int lech = thang.atDay(1).getDayOfWeek().getValue() - 1; // thứ Hai = 1
			for (int i = 0; i < lech; i++) {
				luoi.add(new JLabel(""));
			}
			for (int d = 1; d <= thang.lengthOfMonth(); d++) {
				LocalDate ngay = thang.atDay(d);
				NgayCell cell = new NgayCell(d, ngay.equals(dangChon));
				cell.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						khiChon.accept(ngay);
					}
				});
				luoi.add(cell);
			}
			luoi.revalidate();
			luoi.repaint();
		}
	}

	/** Một ô ngày trong lịch. */
	private static class NgayCell extends JLabel {
		private static final long serialVersionUID = 1L;
		private final boolean chon;
		private boolean hover = false;

		NgayCell(int ngay, boolean chon) {
			super(String.valueOf(ngay), SwingConstants.CENTER);
			this.chon = chon;
			setFont(FONT_BODY);
			setForeground(chon ? Color.WHITE : TEXT_DARK);
			setOpaque(false);
			setPreferredSize(new Dimension(34, 30));
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			if (chon) {
				g2.setColor(PRIMARY);
				g2.fillRoundRect(2, 1, getWidth() - 4, getHeight() - 2, 10, 10);
			} else if (hover) {
				g2.setColor(PRIMARY_LIGHT);
				g2.fillRoundRect(2, 1, getWidth() - 4, getHeight() - 2, 10, 10);
			}
			g2.dispose();
			super.paintComponent(g);
		}
	}
}
