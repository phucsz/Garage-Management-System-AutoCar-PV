package gui;

import static gui.UITheme.*;
import javax.swing.SwingUtilities;

/**
 * Trang chủ dành cho vai trò THU NGÂN.
 * Menu chính: Trang chủ, Thanh toán hóa đơn, Báo cáo thống kê.
 */
public class TrangChuThuNganGUI extends TrangChuBaseGUI {
	private static final long serialVersionUID = 1L;

	// =====================================================================
	// ĐƯỜNG DẪN ICON CỦA TỪNG NÚT MENU - TỰ NHẬP VÀO GIỮA CẶP DẤU "".
	// Ví dụ: "/images/icon_trangChu.png" hoặc chỉ cần "icon_trangChu.png" (tự hiểu là nằm trong /images/).
	// Để rỗng thì sẽ hiện khung viền nét đứt.
	// =====================================================================
	private static final String ICON_TRANG_CHU = IMG_TRANG_CHU; 
	private static final String ICON_THANH_TOAN = "/images/icon_thanhToan.png";
	private static final String ICON_BAO_CAO = IMG_BAO_CAO; 

	private static final MucMenu[] MENU = {
			new MucMenu(KEY_TRANG_CHU, "Trang Chủ", ICON_TRANG_CHU),
			new MucMenu(KEY_THANH_TOAN, "Thanh Toán Hóa Đơn", ICON_THANH_TOAN),
			new MucMenu(KEY_BAO_CAO, "Báo Cáo Thống Kê", ICON_BAO_CAO) };

	public TrangChuThuNganGUI(String hoTen) {
		super(hoTen, "Thu ngân", MENU);

		// Màn hình đã làm xong thì đăng ký vào CardLayout; các mục còn lại tự có panel tạm.
		// themCard(KEY_THANH_TOAN, new ThanhToanHoaDonGUI());

		hoanTat();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new TrangChuThuNganGUI("Phạm Ngọc Anh").setVisible(true));
	}
}
