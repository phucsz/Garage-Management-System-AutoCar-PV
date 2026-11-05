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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import dao.HangMucKiemTraDAO;
import entity.HangMucKiemTra;

/**
 * Màn hình LẬP PHIẾU TIẾP NHẬN (JPanel) - được nhúng vào CardLayout của TrangChuTiepNhanVienGUI.
 * Nút "Tra cứu phiếu tiếp nhận" ở góc trên phải sẽ gọi {@code moTraCuu} để chuyển sang màn hình tra cứu.
 */
public class LapPhieuTiepNhanGUI extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final DateTimeFormatter FMT_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final DateTimeFormatter FMT_GIO = DateTimeFormatter.ofPattern("H:mm");

	private final Runnable moTraCuu;

	private Input txtMaPhieu, txtNgayTiepNhan, txtGioTiepNhan;
	private Input txtSoDienThoai, txtKhachHang, txtDiaChi;
	private Input txtBienSo, txtHangXe, txtSoKm;
	private JTextArea txtYeuCau;
	private Input txtKyThuatVien;

	private final HangMucKiemTraDAO hangMucDAO = new HangMucKiemTraDAO();
	private final Set<String> maHangMucDaChon = new HashSet<>();
	private final List<HangMucKiemTra> dsNoiThat = new ArrayList<>();
	private final List<HangMucKiemTra> dsNgoaiThat = new ArrayList<>();
	private final java.util.Map<String, String> tinhTrangTheoHangMuc = new java.util.HashMap<>();
	private JTable tblNoiThat;
	private JTable tblNgoaiThat;
	private DefaultTableModel modelNoiThat;
	private DefaultTableModel modelNgoaiThat;
	private JScrollPane scrollNoiThat;
	private JScrollPane scrollNgoaiThat;
	private boolean dangNapGoiY;

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
		txtSoDienThoai.themIconBenPhai("/images/icon_timKiemBlack.png", this::timKhachHang);
		txtKhachHang = new Input(null, false);
		txtDiaChi = new Input(null, true);

		txtBienSo = new Input("Nhập biển số xe", false);
		txtBienSo.themIconBenPhai("/images/icon_timKiemBlack.png", this::timXe);
		txtHangXe = new Input("Nhập hãng / dòng xe", false);
		txtSoKm = new Input("Nhập số km", false);

		txtSoDienThoai.addActionListener(e -> timKhachHang());
		txtBienSo.addActionListener(e -> timXe());

		themO(form, labeled("Mã phiếu tiếp nhận", txtMaPhieu), 1, 0, 3);
		themO(form, labeled("Ngày tiếp nhận", txtNgayTiepNhan), 1, 3, 3);

		themO(form, labeled("Số điện thoại", txtSoDienThoai), 2, 0, 3);
		themO(form, labeled("Họ tên khách hàng", txtKhachHang), 2, 3, 3);

		themO(form, labeled("Biển số xe", txtBienSo), 4, 0, 2);
		themO(form, labeled("Hãng xe", txtHangXe), 4, 2, 2);
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
		JPanel cacNhom = new JPanel(new GridBagLayout());
		cacNhom.setOpaque(false);
		themDong(cacNhom, taoNhomHangMuc("NỘI THẤT", "Nội thất", true), 0, 10);
		themDong(cacNhom, taoNhomHangMuc("NGOẠI THẤT", "Ngoại thất", false), 1, 0);
		khung.add(cacNhom, BorderLayout.CENTER);
		return khung;
	}

	private JPanel taoNhomHangMuc(String tieuDe, String loai, boolean noiThat) {
		JPanel nhom = new JPanel(new BorderLayout(0, 6));
		nhom.setOpaque(false);

		JPanel dau = new JPanel(new BorderLayout());
		dau.setOpaque(false);
		JLabel lbl = new JLabel(tieuDe);
		lbl.setFont(FONT_SECTION);
		lbl.setForeground(TITLE);
		JButton btnThem = new JButton("+");
		btnThem.setFont(FONT_SECTION);
		btnThem.setForeground(PRIMARY);
		btnThem.setFocusPainted(false);
		btnThem.setToolTipText("Thêm hạng mục " + loai.toLowerCase());
		btnThem.addActionListener(e -> moBoChonHangMuc(nhom, loai, noiThat));
		dau.add(lbl, BorderLayout.WEST);
		dau.add(btnThem, BorderLayout.EAST);
		nhom.add(dau, BorderLayout.NORTH);

		DefaultTableModel model = taoModelHangMuc();
		JTable bang = new JTable(model);
		if (noiThat) {
			modelNoiThat = model;
			tblNoiThat = bang;
		} else {
			modelNgoaiThat = model;
			tblNgoaiThat = bang;
		}
		tuyChinhBangHangMuc(bang, model);
		JScrollPane scroll = new JScrollPane(bang);
		scroll.getViewport().setBackground(Color.WHITE);
		if (noiThat) {
			scrollNoiThat = scroll;
		} else {
			scrollNgoaiThat = scroll;
		}
		capNhatChieuCaoBang(scroll, 0);
		nhom.add(scroll, BorderLayout.CENTER);
		return nhom;
	}

	private DefaultTableModel taoModelHangMuc() {
		return new DefaultTableModel(
				new Object[] { "STT", "HẠNG MỤC KIỂM TRA", "TÌNH TRẠNG LÚC KIỂM TRA", "", "Mã hạng mục" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 2;
			}
		};
	}

	private void tuyChinhBangHangMuc(JTable bang, DefaultTableModel model) {
		bang.setRowHeight(38);
		bang.setShowGrid(false);
		bang.setIntercellSpacing(new Dimension(0, 0));
		bang.setFont(FONT_BODY);
		bang.setForeground(TEXT_DARK);
		bang.setSelectionBackground(ROW_SELECTED);
		bang.setFillsViewportHeight(true);
		bang.setFocusable(false);
		bang.getColumnModel().getColumn(0).setPreferredWidth(55);
		bang.getColumnModel().getColumn(1).setPreferredWidth(300);
		bang.getColumnModel().getColumn(2).setPreferredWidth(500);
		bang.getColumnModel().getColumn(3).setPreferredWidth(55);
		bang.getColumnModel().getColumn(4).setMinWidth(0);
		bang.getColumnModel().getColumn(4).setMaxWidth(0);
		bang.getColumnModel().getColumn(4).setPreferredWidth(0);
		bang.getColumnModel().getColumn(0).setCellRenderer(new BangRenderer(javax.swing.SwingConstants.CENTER));
		bang.getColumnModel().getColumn(1).setCellRenderer(new BangRenderer(javax.swing.SwingConstants.LEFT));
		bang.getColumnModel().getColumn(2).setCellRenderer(new BangRenderer(javax.swing.SwingConstants.LEFT));
		bang.getColumnModel().getColumn(3).setCellRenderer(new XoaRenderer());
		JTextField editor = new JTextField();
		editor.setFont(FONT_BODY);
		editor.setBorder(new CompoundBorder(new LineBorder(new Color(190, 205, 220)), new EmptyBorder(5, 8, 5, 8)));
		bang.getColumnModel().getColumn(2).setCellEditor(new javax.swing.DefaultCellEditor(editor));
		bang.getTableHeader().setReorderingAllowed(false);
		bang.getTableHeader().setResizingAllowed(false);
		bang.getTableHeader().setDefaultRenderer(new HeaderRenderer());
		bang.getTableHeader().setPreferredSize(new Dimension(100, 38));
		bang.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = bang.rowAtPoint(e.getPoint());
				int col = bang.columnAtPoint(e.getPoint());
				if (row >= 0 && col == 3) {
					xoaHangMuc(bang, model, row);
				}
			}
		});
	}

	private void moBoChonHangMuc(JPanel nhom, String loai, boolean noiThat) {
		JPanel boChon = new JPanel(new BorderLayout(8, 0));
		boChon.setBackground(Color.WHITE);
		boChon.setBorder(new CompoundBorder(new LineBorder(PRIMARY), new EmptyBorder(6, 8, 6, 8)));
		JComboBox<HangMucKiemTra> combo = new JComboBox<>();
		combo.setEditable(true);
		combo.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index,
					boolean selected, boolean focus) {
				super.getListCellRendererComponent(list, value, index, selected, focus);
				setText(value instanceof HangMucKiemTra ? ((HangMucKiemTra) value).getTenHangMuc() : "");
				return this;
			}
		});
		JButton btnHuy = new JButton("Hủy");
		JButton btnThem = new JButton("Thêm");
		boChon.add(combo, BorderLayout.CENTER);
		JPanel nut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
		nut.setOpaque(false);
		nut.add(btnHuy);
		nut.add(btnThem);
		boChon.add(nut, BorderLayout.EAST);
		nhom.add(boChon, BorderLayout.SOUTH);
		nhom.revalidate();
		nhom.repaint();

		Timer timerNapGoiY = new Timer(250, e -> napGoiY(combo, loai));
		timerNapGoiY.setRepeats(false);
		JTextField editor = (JTextField) combo.getEditor().getEditorComponent();
		editor.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { timerNapGoiY.restart(); }
			public void removeUpdate(DocumentEvent e) { timerNapGoiY.restart(); }
			public void changedUpdate(DocumentEvent e) { timerNapGoiY.restart(); }
		});
		btnHuy.addActionListener(e -> dongBoChon(nhom, boChon));
		btnThem.addActionListener(e -> {
			Object selected = combo.getSelectedItem();
			if (!(selected instanceof HangMucKiemTra)) {
				JOptionPane.showMessageDialog(this, "Vui lòng chọn một hạng mục trong danh sách gợi ý.", "Thông báo",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			themHangMuc((HangMucKiemTra) selected, noiThat, nhom, boChon);
		});
		timerNapGoiY.setInitialDelay(0);
		timerNapGoiY.start();
		SwingUtilities.invokeLater(editor::requestFocusInWindow);
	}

	private void napGoiY(JComboBox<HangMucKiemTra> combo, String loai) {
		if (dangNapGoiY) {
			return;
		}
		dangNapGoiY = true;
		JTextField editor = (JTextField) combo.getEditor().getEditorComponent();
		String tuKhoa = editor.getText();
		try {
			List<HangMucKiemTra> goiY = hangMucDAO.timTheoLoai(loai, tuKhoa);
			DefaultComboBoxModel<HangMucKiemTra> model = new DefaultComboBoxModel<>();
			for (HangMucKiemTra item : goiY) {
				if (!maHangMucDaChon.contains(item.getMaHangMuc())) {
					model.addElement(item);
				}
			}
			combo.setModel(model);
			editor = (JTextField) combo.getEditor().getEditorComponent();
			editor.setText(tuKhoa);
			editor.setCaretPosition(editor.getDocument().getLength());
			if (!goiY.isEmpty() && editor.hasFocus()) {
				combo.showPopup();
			}
		} catch (java.sql.SQLException e) {
			JOptionPane.showMessageDialog(this, "Không thể tải danh sách hạng mục kiểm tra.", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} finally {
			dangNapGoiY = false;
		}
	}

	private void themHangMuc(HangMucKiemTra item, boolean noiThat, JPanel nhom, JPanel boChon) {
		if (!maHangMucDaChon.add(item.getMaHangMuc())) {
			JOptionPane.showMessageDialog(this, "Hạng mục này đã được chọn trong phiếu tiếp nhận.", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		List<HangMucKiemTra> danhSach = noiThat ? dsNoiThat : dsNgoaiThat;
		danhSach.add(item);
		capNhatBangHangMuc(noiThat ? modelNoiThat : modelNgoaiThat, danhSach);
		dongBoChon(nhom, boChon);
	}

	private void xoaHangMuc(JTable bang, DefaultTableModel model, int row) {
		List<HangMucKiemTra> danhSach = bang == tblNoiThat ? dsNoiThat : dsNgoaiThat;
		if (row < 0 || row >= danhSach.size()) {
			return;
		}
		HangMucKiemTra item = danhSach.remove(row);
		maHangMucDaChon.remove(item.getMaHangMuc());
		tinhTrangTheoHangMuc.remove(item.getMaHangMuc());
		capNhatBangHangMuc(model, danhSach);
	}

	private void capNhatBangHangMuc(DefaultTableModel model, List<HangMucKiemTra> danhSach) {
		JTable bang = model == modelNoiThat ? tblNoiThat : tblNgoaiThat;
		if (bang.isEditing()) {
			bang.getCellEditor().stopCellEditing();
		}
		for (int i = 0; i < model.getRowCount(); i++) {
			String ma = (String) model.getValueAt(i, 4);
			if (ma != null && maHangMucDaChon.contains(ma)) {
				tinhTrangTheoHangMuc.put(ma, String.valueOf(model.getValueAt(i, 2)));
			}
		}
		model.setRowCount(0);
		for (int i = 0; i < danhSach.size(); i++) {
			HangMucKiemTra item = danhSach.get(i);
			model.addRow(new Object[] { i + 1, item.getTenHangMuc(),
					tinhTrangTheoHangMuc.getOrDefault(item.getMaHangMuc(), ""), "X", item.getMaHangMuc() });
		}
		JScrollPane scroll = model == modelNoiThat ? scrollNoiThat : scrollNgoaiThat;
		capNhatChieuCaoBang(scroll, danhSach.size());
	}

	private void capNhatChieuCaoBang(JScrollPane scroll, int soDong) {
		if (scroll == null) {
			return;
		}
		int soDongHienThi = Math.max(5, soDong);
		scroll.setPreferredSize(new Dimension(100, 38 + soDongHienThi * 42 + 2));
		scroll.revalidate();
	}

	private void dongBoChon(JPanel nhom, JPanel boChon) {
		nhom.remove(boChon);
		nhom.revalidate();
		nhom.repaint();
	}

	private static class BangRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		BangRenderer(int align) {
			setHorizontalAlignment(align);
			setBorder(new EmptyBorder(0, 8, 0, 8));
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused,
				int row, int column) {
			Component c = super.getTableCellRendererComponent(table, value, selected, focused, row, column);
			c.setBackground(selected ? ROW_SELECTED : (row % 2 == 0 ? Color.WHITE : new Color(250, 251, 253)));
			c.setForeground(TEXT_DARK);
			return c;
		}
	}

	private static class XoaRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		XoaRenderer() {
			setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			setFont(FONT_SMALL_BOLD);
			setForeground(new Color(190, 60, 60));
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused,
				int row, int column) {
			Component c = super.getTableCellRendererComponent(table, value, selected, focused, row, column);
			c.setBackground(selected ? ROW_SELECTED : (row % 2 == 0 ? Color.WHITE : new Color(250, 251, 253)));
			return c;
		}
	}

	private static class HeaderRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		HeaderRenderer() {
			setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			setFont(FONT_SMALL_BOLD);
			setForeground(Color.WHITE);
			setBackground(PRIMARY);
			setBorder(new EmptyBorder(0, 8, 0, 8));
		}
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
		dsNoiThat.clear();
		dsNgoaiThat.clear();
		maHangMucDaChon.clear();
		tinhTrangTheoHangMuc.clear();
		modelNoiThat.setRowCount(0);
		modelNgoaiThat.setRowCount(0);
		refresh();
		txtSoDienThoai.requestFocusInWindow();
	}

	private void baoLoi(String msg, Component focus) {
		JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.WARNING_MESSAGE);
		focus.requestFocusInWindow();
	}
}
