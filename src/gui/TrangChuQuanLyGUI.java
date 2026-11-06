package gui;

import static gui.UITheme.*;
import javax.swing.SwingUtilities;

/**
 * Trang chủ dành cho vai trò QUẢN LÝ.
 * Menu chính: Trang chủ, Quản lý phụ tùng, Quản lý dịch vụ, Nhân viên, Khuyến mãi, Báo cáo thống kê.
 */
public class TrangChuQuanLyGUI extends TrangChuBaseGUI {
	private static final long serialVersionUID = 1L;

	// =====================================================================
	// ĐƯỜNG DẪN ICON CỦA TỪNG NÚT MENU - TỰ NHẬP VÀO GIỮA CẶP DẤU "".
	// Ví dụ: "/images/icon_trangChu.png" hoặc chỉ cần "icon_trangChu.png" (tự hiểu là nằm trong /images/).
	// Để rỗng thì sẽ hiện khung viền nét đứt.
	// =====================================================================
	private static final String ICON_TRANG_CHU = IMG_TRANG_CHU;  
	private static final String ICON_PHU_TUNG = IMG_PHU_TUNG; 
	private static final String ICON_DICH_VU = IMG_DICH_VU; 
	private static final String ICON_NHAN_VIEN = IMG_NHAN_VIEN; 
	private static final String ICON_KHUYEN_MAI = IMG_KHUYEN_MAI; 
	private static final String ICON_BAO_CAO = IMG_BAO_CAO; 

	private static final MucMenu[] MENU = {
			new MucMenu(KEY_TRANG_CHU, "Trang Chủ", ICON_TRANG_CHU),
			new MucMenu(KEY_PHU_TUNG, "Quản Lý Phụ Tùng", ICON_PHU_TUNG),
			new MucMenu(KEY_DICH_VU, "Quản Lý Dịch Vụ", ICON_DICH_VU),
			new MucMenu(KEY_NHAN_VIEN, "Nhân Viên", ICON_NHAN_VIEN),
			new MucMenu(KEY_KHUYEN_MAI, "Khuyến Mãi", ICON_KHUYEN_MAI),
			new MucMenu(KEY_BAO_CAO, "Báo Cáo Thống Kê", ICON_BAO_CAO) };

	private final NhanVienGUI nhanVienGUI;
	private final PhuTungGUI phuTungGUI;
	private final QuanLyDichVuGUI dichVuGUI;
	private final KhuyenMaiGUI khuyenMaiGUI;

	public TrangChuQuanLyGUI(String hoTen) {
		super(hoTen, "Quản lý", MENU);

		// Màn hình đã làm xong thì đăng ký vào CardLayout; các mục còn lại tự có panel tạm.
		nhanVienGUI = new NhanVienGUI(() -> hienThi(KEY_TRANG_CHU));
		themCard(KEY_NHAN_VIEN, nhanVienGUI);
		phuTungGUI = new PhuTungGUI();
		dichVuGUI = new QuanLyDichVuGUI(null);
		khuyenMaiGUI = new KhuyenMaiGUI(null);
		themCard(KEY_PHU_TUNG, phuTungGUI);
		themCard(KEY_DICH_VU, dichVuGUI);
		themCard(KEY_KHUYEN_MAI, khuyenMaiGUI);

		hoanTat();
	}

	@Override
	protected void onHienThi(String key) {
		if (KEY_NHAN_VIEN.equals(key)) {
			nhanVienGUI.refresh();
		}
		if (KEY_PHU_TUNG.equals(key)) {
			phuTungGUI.refresh();
		}
		if (KEY_DICH_VU.equals(key)) {
			dichVuGUI.refresh();
		}
		if (KEY_KHUYEN_MAI.equals(key)) {
			khuyenMaiGUI.refresh();
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new TrangChuQuanLyGUI("Nguyễn Văn A").setVisible(true));
	}
}
