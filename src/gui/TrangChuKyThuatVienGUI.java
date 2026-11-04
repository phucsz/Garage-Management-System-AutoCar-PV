package gui;

import javax.swing.SwingUtilities;

/**
 * Trang chủ dành cho vai trò KỸ THUẬT VIÊN.
 * Menu chính: Trang chủ, Danh sách công việc, Công việc phụ trách, Yêu cầu xuất kho, Báo cáo thống kê.
 */
public class TrangChuKyThuatVienGUI extends TrangChuBaseGUI {
	private static final long serialVersionUID = 1L;

	// =====================================================================
	// ĐƯỜNG DẪN ICON CỦA TỪNG NÚT MENU - TỰ NHẬP VÀO GIỮA CẶP DẤU "".
	// Ví dụ: "/images/icon_trangChu.png" hoặc chỉ cần "icon_trangChu.png" (tự hiểu là nằm trong /images/).
	// Để rỗng thì sẽ hiện khung viền nét đứt.
	// =====================================================================
	private static final String ICON_TRANG_CHU = ""; // TODO: nhập đường dẫn
	private static final String ICON_DS_CONG_VIEC = ""; // TODO: nhập đường dẫn
	private static final String ICON_CONG_VIEC_PHU_TRACH = ""; // TODO: nhập đường dẫn
	private static final String ICON_YEU_CAU_XUAT_KHO = ""; // TODO: nhập đường dẫn
	private static final String ICON_BAO_CAO = ""; // TODO: nhập đường dẫn

	private static final MucMenu[] MENU = {
			new MucMenu(KEY_TRANG_CHU, "Trang Chủ", ICON_TRANG_CHU),
			new MucMenu(KEY_DS_CONG_VIEC, "Danh Sách Công Việc", ICON_DS_CONG_VIEC),
			new MucMenu(KEY_CONG_VIEC_PHU_TRACH, "Công Việc Phụ Trách", ICON_CONG_VIEC_PHU_TRACH),
			new MucMenu(KEY_YEU_CAU_XUAT_KHO, "Yêu Cầu Xuất Kho", ICON_YEU_CAU_XUAT_KHO),
			new MucMenu(KEY_BAO_CAO, "Báo Cáo Thống Kê", ICON_BAO_CAO) };

	public TrangChuKyThuatVienGUI(String hoTen) {
		super(hoTen, "Kỹ thuật viên", MENU);

		// Màn hình đã làm xong thì đăng ký vào CardLayout; các mục còn lại tự có panel tạm.
		// themCard(KEY_DS_CONG_VIEC, new DanhSachCongViecGUI());
		// themCard(KEY_CONG_VIEC_PHU_TRACH, new CongViecPhuTrachGUI());
		// themCard(KEY_YEU_CAU_XUAT_KHO, new YeuCauXuatKhoGUI());

		hoanTat();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new TrangChuKyThuatVienGUI("Trần Minh").setVisible(true));
	}
}
