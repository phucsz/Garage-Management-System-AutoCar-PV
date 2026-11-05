package gui;

import static gui.UITheme.*;
import javax.swing.SwingUtilities;

/**
 * Trang chủ dành cho vai trò TIẾP NHẬN VIÊN.
 * Menu chính: Trang chủ, Khách hàng & Xe, Tiếp nhận xe, Lập phiếu sửa chữa, Báo cáo thống kê.
 */
public class TrangChuTiepNhanVienGUI extends TrangChuBaseGUI {
	private static final long serialVersionUID = 1L;

	// =====================================================================
	// ĐƯỜNG DẪN ICON CỦA TỪNG NÚT MENU - TỰ NHẬP VÀO GIỮA CẶP DẤU "".
	// Ví dụ: "/images/icon_trangChu.png" hoặc chỉ cần "icon_trangChu.png" (tự hiểu là nằm trong /images/).
	// Để rỗng thì sẽ hiện khung viền nét đứt.
	// =====================================================================
	private static final String ICON_TRANG_CHU = IMG_TRANG_CHU; // TODO: nhập đường dẫn
	private static final String ICON_KHACH_HANG = IMG_KHACH_HANG; // TODO: nhập đường dẫn
	private static final String ICON_TIEP_NHAN = IMG_TIEP_NHAN; // TODO: nhập đường dẫn
	private static final String ICON_SUA_CHUA = IMG_SUA_CHUA; // TODO: nhập đường dẫn
	private static final String ICON_BAO_CAO = IMG_BAO_CAO; // TODO: nhập đường dẫn

	private static final MucMenu[] MENU = {
			new MucMenu(KEY_TRANG_CHU, "Trang Chủ", ICON_TRANG_CHU),
			new MucMenu(KEY_KHACH_HANG, "Khách Hàng & Xe", ICON_KHACH_HANG),
			new MucMenu(KEY_TIEP_NHAN, "Tiếp Nhận Xe", ICON_TIEP_NHAN),
			new MucMenu(KEY_SUA_CHUA, "Lập Phiếu Sửa Chữa", ICON_SUA_CHUA),
			new MucMenu(KEY_BAO_CAO, "Báo Cáo Thống Kê", ICON_BAO_CAO) };

	public TrangChuTiepNhanVienGUI(String hoTen) {
		super(hoTen, "Tiếp nhận viên", MENU);

		// Màn hình đã làm xong thì đăng ký vào CardLayout; các mục còn lại tự có panel tạm.
		themCard(KEY_TIEP_NHAN,
				taoPanelTam("Tiếp nhận xe", "Gắn màn hình Lập phiếu tiếp nhận (dạng JPanel) vào đây."));
		// themCard(KEY_KHACH_HANG, new KhachHangGUI());
		// themCard(KEY_SUA_CHUA, new LapPhieuSuaChuaGUI());

		hoanTat();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new TrangChuTiepNhanVienGUI("Lê Thu Hà").setVisible(true));
	}
}
