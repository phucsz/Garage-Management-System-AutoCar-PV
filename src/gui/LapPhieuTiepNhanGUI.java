package gui;

import static gui.UITheme.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

/**
 * Màn hình LẬP PHIẾU TIẾP NHẬN (JPanel) - được nhúng vào CardLayout của TrangChuTiepNhanVienGUI.
 * Nút "Tra cứu phiếu tiếp nhận" ở góc trên phải sẽ gọi {@code moTraCuu} để chuyển sang màn hình tra cứu.
 */
public class LapPhieuTiepNhanGUI extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final DateTimeFormatter FMT_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final DateTimeFormatter FMT_GIO = DateTimeFormatter.ofPattern("H:mm");

	private static final Color VIEN_O = new Color(226, 230, 235);

	/** Nhóm hạng mục -> danh sách hạng mục kiểm tra (STT tự đánh liên tục). */
	private static final Map<String, String[]> HANG_MUC = new LinkedHashMap<>();
	static {
		HANG_MUC.put("NỘI THẤT", new String[] { "Tay mở cửa", "Nút nâng, hạ kính", "Phần taplo", "Ghế trước",
				"Ghế sau", "Che nắng hai bên" });
		HANG_MUC.put("NGOẠI THẤT", new String[] { "Gương chiếu hậu", "Kính trước hai bên", "Kính sau hai bên",
				"Kính lái trước", "Kính sau", "Đèn trước", "Đèn sau", "Đồng sơn toàn thân xe" });
	}

	private final Runnable moTraCuu;

	private Input txtMaPhieu, txtNgayTiepNhan, txtGioTiepNhan;
	private Input txtSoDienThoai, txtKhachHang, txtDiaChi;
	private Input txtBienSo, txtHangXe, txtSoKm;
	private JTextArea txtYeuCau;
	private Input txtKyThuatVien;

	private final List<String> dsHangMuc = new ArrayList<>();
	private final List<Input> dsTinhTrang = new ArrayList<>();

	/**
	 * @param nguoiTiepNhan tên người tiếp nhận hiện trên ô "Kỹ thuật viên tiếp nhận" (chỉ đọc)
	 * @param moTraCuu      hành động chuyển sang màn hình Tra cứu phiếu tiếp nhận
	 */
	public LapPhieuTiepNhanGUI(String nguoiTiepNhan, Runnable moTraCuu) {
		this.moTraCuu = moTraCuu;
		setLayout(new BorderLayout());
		setBackground(BG);

		ScrollablePanel trang = new ScrollablePanel(new BorderLayout(0, 16));
		trang.setBackground(BG);
		trang.setBorder(new EmptyBorder(22, 28, 28, 28));
		trang.add(taoTieuDeTrang(), BorderLayout.NORTH);
		trang.add(taoTheThongTin(), BorderLayout.CENTER);

		JScrollPane sp = new JScrollPane(trang, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setBorder(null);
		sp.getVerticalScrollBar().setUnitIncrement(16);
		add(sp, BorderLayout.CENTER);

		txtKyThuatVien.setText(nguoiTiepNhan);
		refresh();
	}

	/** Gọi mỗi khi màn hình được hiển thị: cập nhật mã phiếu, ngày, giờ (không xóa dữ liệu đang nhập). */
	public void refresh() {
		LocalDate homNay = LocalDate.now();
		//txtMaPhieu.setText(DuLieuMauTiepNhan.taoMaPhieu(homNay));
		txtNgayTiepNhan.setText(homNay.format(FMT_NGAY));
		txtGioTiepNhan.setText(LocalTime.now().format(FMT_GIO));
	}

	// =====================================================================
	// TIÊU ĐỀ TRANG
	// =====================================================================
	private JPanel taoTieuDeTrang() {
		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);

		JPanel tieuDe = new JPanel();
		tieuDe.setOpaque(false);
		tieuDe.setLayout(new javax.swing.BoxLayout(tieuDe, javax.swing.BoxLayout.Y_AXIS));
		JLabel lblTitle = new JLabel("Lập Phiếu Tiếp Nhận");
		lblTitle.setFont(FONT_TITLE);
		lblTitle.setForeground(TITLE);
		JLabel lblSub = new JLabel("Nhập thông tin khách hàng, thông tin xe và lưu phiếu tiếp nhận mới.");
		lblSub.setFont(FONT_LABEL);
		lblSub.setForeground(TEXT_MUTED);
		tieuDe.add(lblTitle);
		tieuDe.add(lblSub);
		header.add(tieuDe, BorderLayout.WEST);

		ActionButton btnTraCuu = new ActionButton("Tra cứu phiếu tiếp nhận", ActionButton.KIEU_VIEN_XANH);
		btnTraCuu.addActionListener(e -> {
			if (moTraCuu != null) {
				moTraCuu.run();
			}
		});
		JPanel wrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		wrap.setOpaque(false);
		wrap.add(btnTraCuu);
		header.add(wrap, BorderLayout.EAST);
		return header;
	}

	// =====================================================================
	// THẺ THÔNG TIN TIẾP NHẬN
	// =====================================================================
	private CardPanel taoTheThongTin() {
		CardPanel the = new CardPanel("Thông Tin Tiếp Nhận", "Phiếu tiếp nhận mới");
		the.getBody().setBorder(new EmptyBorder(20, 20, 20, 20));

		JPanel ngan = new JPanel(new GridBagLayout());
		ngan.setOpaque(false);
		themDong(ngan, taoFormThongTin(), 0, 6);
		themDong(ngan, taoBangKiemTra(), 1, 16);
		themDong(ngan, taoPhanYeuCau(), 2, 12);
		themDong(ngan, taoPhanKyThuatVien(), 3, 14);
		themDong(ngan, taoPhanNut(), 4, 0);

		the.getBody().add(ngan, BorderLayout.CENTER);
		return the;
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

	// ---------------------------------------------------------------------
	// Form thông tin phiếu / khách hàng / xe (lưới 6 cột)
	// ---------------------------------------------------------------------
	private JPanel taoFormThongTin() {
		JPanel form = new JPanel(new GridBagLayout());
		form.setOpaque(false);

		// Hàng "thước" giữ cho 6 cột luôn chia đều
		for (int c = 0; c < 6; c++) {
			GridBagConstraints g = new GridBagConstraints();
			g.gridx = c;
			g.gridy = 0;
			g.weightx = 1;
			g.fill = GridBagConstraints.HORIZONTAL;
			JPanel thuoc = new JPanel();
			thuoc.setOpaque(false);
			thuoc.setPreferredSize(new Dimension(1, 0));
			form.add(thuoc, g);
		}

		txtMaPhieu = new Input(null, true);
		txtNgayTiepNhan = new Input(null, true);
		txtGioTiepNhan = new Input(null, true);

		txtSoDienThoai = new Input("Nhập số điện thoại khách hàng", false);
		txtSoDienThoai.themIconBenPhai(IMG_TIM_KIEM, this::timKhachHang);
		txtKhachHang = new Input(null, true);
		txtDiaChi = new Input(null, true);

		txtBienSo = new Input("Nhập biển số xe", false);
		txtBienSo.themIconBenPhai(IMG_TIM_KIEM, this::timXe);
		txtHangXe = new Input("Nhập hãng / dòng xe", false);
		txtSoKm = new Input("Nhập số km", false);

		txtSoDienThoai.addActionListener(e -> timKhachHang());
		txtBienSo.addActionListener(e -> timXe());

		themO(form, labeled("Mã phiếu tiếp nhận", txtMaPhieu), 1, 0, 2);
		themO(form, labeled("Ngày tiếp nhận", txtNgayTiepNhan), 1, 2, 2);
		themO(form, labeled("Giờ tiếp nhận", txtGioTiepNhan), 1, 4, 2);

		themO(form, labeled("Số điện thoại", txtSoDienThoai), 2, 0, 3);
		themO(form, labeled("Khách hàng", txtKhachHang), 2, 3, 3);

		themO(form, labeled("Địa chỉ khách hàng", txtDiaChi), 3, 0, 6);

		themO(form, labeled("Biển số xe", txtBienSo), 4, 0, 2);
		themO(form, labeled("Hãng / dòng xe", txtHangXe), 4, 2, 2);
		themO(form, labeled("Số km đã đi", txtSoKm), 4, 4, 2);
		return form;
	}

	private static void themO(JPanel form, Component comp, int row, int col, int span) {
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = col;
		g.gridy = row;
		g.gridwidth = span;
		g.weightx = span;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.anchor = GridBagConstraints.NORTHWEST;
		g.insets = new Insets(0, 0, 10, (col + span == 6) ? 0 : 16);
		form.add(comp, g);
	}

	// ---------------------------------------------------------------------
	// Bảng kiểm tra tình trạng xe
	// ---------------------------------------------------------------------
	private JPanel taoBangKiemTra() {
		JPanel khung = new JPanel(new BorderLayout());
		khung.setOpaque(false);

		JPanel bang = new JPanel(new GridBagLayout());
		bang.setBorder(new LineBorder(VIEN_O));
		bang.setBackground(Color.WHITE);

		int row = 0;
		// Dòng tiêu đề
		bang.add(oBang(nhanBang("STT", javax.swing.SwingConstants.CENTER, Color.WHITE, true), PRIMARY),
				gbc(0, row, 1, 0, 40));
		bang.add(oBang(nhanBang("HẠNG MỤC KIỂM TRA", javax.swing.SwingConstants.LEFT, Color.WHITE, true), PRIMARY),
				gbc(1, row, 1, 0.62, 100));
		bang.add(oBang(nhanBang("TÌNH TRẠNG LÚC KIỂM TRA", javax.swing.SwingConstants.LEFT, Color.WHITE, true),
				PRIMARY), gbc(2, row, 1, 0.38, 100));
		row++;

		int stt = 1;
		for (Map.Entry<String, String[]> nhom : HANG_MUC.entrySet()) {
			// Dòng tiêu đề nhóm
			bang.add(oBang(nhanBang(nhom.getKey(), javax.swing.SwingConstants.CENTER, PRIMARY, true), PRIMARY_LIGHT),
					gbc(0, row, 2, 0, 40));
			bang.add(oBang(new JLabel(" "), PRIMARY_LIGHT), gbc(2, row, 1, 0.38, 100));
			row++;

			for (String ten : nhom.getValue()) {
				Color nen = (stt % 2 == 0) ? new Color(250, 251, 253) : Color.WHITE;
				bang.add(oBang(nhanBang(String.valueOf(stt), javax.swing.SwingConstants.CENTER, TEXT_DARK, false), nen),
						gbc(0, row, 1, 0, 40));
				bang.add(oBang(nhanBang(ten, javax.swing.SwingConstants.LEFT, TEXT_DARK, false), nen),
						gbc(1, row, 1, 0.62, 100));

				Input o = new Input("Nhập tình trạng...", false);
				o.setFont(FONT_SMALL);
				o.setPreferredSize(new Dimension(100, 26));
				bang.add(oBang(o, nen), gbc(2, row, 1, 0.38, 100));

				dsHangMuc.add(ten);
				dsTinhTrang.add(o);
				stt++;
				row++;
			}
		}
		khung.add(bang, BorderLayout.CENTER);

		// Ghi chú
		JPanel ghiChu = new JPanel(new BorderLayout());
		ghiChu.setBackground(new Color(255, 249, 219));
		ghiChu.setBorder(new CompoundBorder(new LineBorder(new Color(240, 225, 160)), new EmptyBorder(5, 10, 5, 10)));
		JLabel lblGhiChu = new JLabel("<html><b>Ghi chú:</b> Các hạng mục nêu trên có lỗi ghi rõ vào mục "
				+ "\"Tình trạng lúc kiểm tra\", nếu không có lỗi thì bỏ trống.</html>");
		lblGhiChu.setFont(FONT_SMALL);
		lblGhiChu.setForeground(new Color(90, 80, 40));
		ghiChu.add(lblGhiChu, BorderLayout.CENTER);
		khung.add(ghiChu, BorderLayout.SOUTH);
		return khung;
	}

	private static GridBagConstraints gbc(int x, int y, int w, double wx, int prefW) {
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = x;
		g.gridy = y;
		g.gridwidth = w;
		g.weightx = wx;
		g.fill = GridBagConstraints.BOTH;
		g.ipadx = prefW;
		return g;
	}

	private static JLabel nhanBang(String text, int align, Color fg, boolean dam) {
		JLabel l = new JLabel(text, align);
		l.setFont(dam ? FONT_SMALL_BOLD : FONT_LABEL);
		l.setForeground(fg);
		return l;
	}

	private static JPanel oBang(javax.swing.JComponent inner, Color nen) {
		JPanel p = new JPanel(new BorderLayout());
		p.setBackground(nen);
		p.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 1, VIEN_O), new EmptyBorder(4, 8, 4, 8)));
		p.add(inner, BorderLayout.CENTER);
		return p;
	}

	// ---------------------------------------------------------------------
	// Yêu cầu khách hàng, kỹ thuật viên, nút
	// ---------------------------------------------------------------------
	private JPanel taoPhanYeuCau() {
		txtYeuCau = new JTextArea(4, 20);
		txtYeuCau.setFont(FONT_BODY);
		txtYeuCau.setLineWrap(true);
		txtYeuCau.setWrapStyleWord(true);
		txtYeuCau.setBackground(new Color(250, 251, 253));
		txtYeuCau.setBorder(new EmptyBorder(8, 12, 8, 12));

		JScrollPane sp = new JScrollPane(txtYeuCau);
		sp.setBorder(new LineBorder(BORDER, 1, true));
		return labeled("Yêu cầu khách hàng / Lưu ý", sp, 96);
	}

	private JPanel taoPhanKyThuatVien() {
		txtKyThuatVien = new Input(null, true);
		return labeled("Kỹ thuật viên tiếp nhận", txtKyThuatVien);
	}

	private JPanel taoPhanNut() {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		p.setOpaque(false);

		ActionButton btnLap = new ActionButton("Lập phiếu tiếp nhận", ActionButton.KIEU_CHINH);
		ActionButton btnNhapLai = new ActionButton("Nhập lại", ActionButton.KIEU_VIEN_XANH);
		btnLap.addActionListener(e -> lapPhieu());
		btnNhapLai.addActionListener(e -> nhapLai());
		p.add(btnLap);
		p.add(btnNhapLai);
		return p;
	}

	// =====================================================================
	// XỬ LÝ NGHIỆP VỤ
	// =====================================================================

	/** Tìm khách hàng theo số điện thoại và điền Khách hàng + Địa chỉ. */
	private void timKhachHang() {
//		String so = DuLieuMauTiepNhan.chiSo(txtSoDienThoai.getText());
//		if (so.isEmpty()) {
//			baoLoi("Vui lòng nhập số điện thoại cần tìm!", txtSoDienThoai);
//			return;
//		}
//		// TODO: thay bằng khachHangDAO.timTheoSdt(so)
//		DuLieuMauTiepNhan.KhachHang kh = DuLieuMauTiepNhan.timKhachHang(so);
//		if (kh == null) {
//			txtKhachHang.setText("");
//			txtDiaChi.setText("");
//			JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng có số điện thoại " + so + ".",
//					"Thông báo", JOptionPane.INFORMATION_MESSAGE);
//			return;
//		}
//		txtKhachHang.setText(kh.ten);
//		txtDiaChi.setText(kh.diaChi);
//		txtBienSo.requestFocusInWindow();
	}

	/** Tìm xe theo biển số và điền Hãng / dòng xe + Số km. */
	private void timXe() {
//		String bs = txtBienSo.getText().trim();
//		if (bs.isEmpty()) {
//			baoLoi("Vui lòng nhập biển số xe cần tìm!", txtBienSo);
//			return;
//		}
//		// TODO: thay bằng xeDAO.timTheoBienSo(bs)
//		DuLieuMauTiepNhan.Xe xe = DuLieuMauTiepNhan.timXe(bs);
//		if (xe == null) {
//			JOptionPane.showMessageDialog(this,
//					"Biển số này chưa có trong hệ thống. Vui lòng nhập hãng / dòng xe và số km.", "Thông báo",
//					JOptionPane.INFORMATION_MESSAGE);
//			txtHangXe.requestFocusInWindow();
//			return;
//		}
//		txtBienSo.setText(xe.bienSo);
//		txtHangXe.setText(xe.hangXe);
//		txtSoKm.setText(String.valueOf(xe.soKm));
	}

	private void lapPhieu() {
//		String sdt = DuLieuMauTiepNhan.chiSo(txtSoDienThoai.getText());
//		String bienSo = txtBienSo.getText().trim();
//		String hangXe = txtHangXe.getText().trim();
//		String soKm = txtSoKm.getText().trim();
//
//		if (sdt.isEmpty()) {
//			baoLoi("Vui lòng nhập số điện thoại khách hàng!", txtSoDienThoai);
//			return;
//		}
//		if (txtKhachHang.getText().trim().isEmpty()) {
//			baoLoi("Chưa chọn khách hàng. Hãy nhập số điện thoại rồi bấm biểu tượng tìm kiếm (hoặc Enter)!",
//					txtSoDienThoai);
//			return;
//		}
//		if (bienSo.isEmpty()) {
//			baoLoi("Vui lòng nhập biển số xe!", txtBienSo);
//			return;
//		}
//		if (hangXe.isEmpty()) {
//			baoLoi("Vui lòng nhập hãng / dòng xe!", txtHangXe);
//			return;
//		}
//		if (!soKm.matches("\\d+")) {
//			baoLoi("Số km đã đi phải là số nguyên không âm!", txtSoKm);
//			return;
//		}

//		DuLieuMauTiepNhan.Phieu p = new DuLieuMauTiepNhan.Phieu();
//		p.ngay = LocalDate.now();
//		p.gio = LocalTime.now().withSecond(0).withNano(0);
//		p.ma = DuLieuMauTiepNhan.taoMaPhieu(p.ngay);
//		p.sdt = sdt;
//		p.tenKhach = txtKhachHang.getText().trim();
//		p.diaChi = txtDiaChi.getText().trim();
//		p.bienSo = bienSo;
//		p.hangXe = hangXe;
//		p.soKm = soKm;
//		p.yeuCau = txtYeuCau.getText().trim();
//		p.nguoiTiepNhan = txtKyThuatVien.getText().trim();
//		p.trangThai = DuLieuMauTiepNhan.TRANG_THAI[0];
//		for (int i = 0; i < dsHangMuc.size(); i++) {
//			String s = dsTinhTrang.get(i).getText().trim();
//			if (!s.isEmpty()) {
//				p.tinhTrang.put(dsHangMuc.get(i), s); // hạng mục để trống = không có lỗi
//			}
//		}
//
//		// TODO: lưu phiếu (và xe mới nếu có) xuống CSDL
//		if (DuLieuMauTiepNhan.timXe(bienSo) == null) {
//			DuLieuMauTiepNhan.XE.add(new DuLieuMauTiepNhan.Xe(bienSo, hangXe, Integer.parseInt(soKm)));
//		}
//		DuLieuMauTiepNhan.PHIEU.add(p);
//
//		JOptionPane.showMessageDialog(this, "Đã lập phiếu tiếp nhận " + p.ma + ".", "Thông báo",
//				JOptionPane.INFORMATION_MESSAGE);
//		nhapLai();
	}

	/** Xóa các ô nhập và sinh mã phiếu mới. */
	private void nhapLai() {
		txtSoDienThoai.setText("");
		txtKhachHang.setText("");
		txtDiaChi.setText("");
		txtBienSo.setText("");
		txtHangXe.setText("");
		txtSoKm.setText("");
		txtYeuCau.setText("");
		for (Input o : dsTinhTrang) {
			o.setText("");
		}
		refresh();
		txtSoDienThoai.requestFocusInWindow();
	}

	private void baoLoi(String msg, Component focus) {
		JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.WARNING_MESSAGE);
		focus.requestFocusInWindow();
	}
}
