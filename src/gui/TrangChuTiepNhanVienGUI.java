package gui;

import static gui.UITheme.*;

import java.sql.SQLException;

import javax.swing.SwingUtilities;

/**
 * Trang chủ dành cho vai trò TIẾP NHẬN VIÊN.
 * Menu chính: Trang chủ, Khách hàng & Xe, Tiếp nhận xe, Lập phiếu sửa chữa, Báo cáo thống kê.
 *
 * Nút "Tiếp Nhận Xe" mở 2 màn hình con trong CardLayout:
 *  - KEY_TIEP_NHAN          : LapPhieuTiepNhanGUI   (mặc định khi bấm nút menu)
 *  - KEY_TRA_CUU_TIEP_NHAN  : TraCuuPhieuTiepNhanGUI (mở từ nút "Tra cứu phiếu tiếp nhận" ở góc trên phải)
 * Cả hai đều tô sáng nút "Tiếp Nhận Xe" trên sidebar.
 */
public class TrangChuTiepNhanVienGUI extends TrangChuBaseGUI {
	private static final long serialVersionUID = 1L;

	// =====================================================================
	// ĐƯỜNG DẪN ICON CỦA TỪNG NÚT MENU - TỰ NHẬP VÀO GIỮA CẶP DẤU "".
	// Ví dụ: "/images/icon_trangChu.png" hoặc chỉ cần "icon_trangChu.png" (tự hiểu là nằm trong /images/).
	// Để rỗng thì sẽ hiện khung viền nét đứt.
	// =====================================================================
	private static final String ICON_TRANG_CHU = IMG_TRANG_CHU; 
	private static final String ICON_KHACH_HANG = IMG_KHACH_HANG;
	private static final String ICON_TIEP_NHAN = IMG_TIEP_NHAN; 
	private static final String ICON_SUA_CHUA = IMG_SUA_CHUA; 
	private static final String ICON_BAO_CAO = IMG_BAO_CAO;
	
	private static final MucMenu[] MENU = {
			new MucMenu(KEY_TRANG_CHU, "Trang Chủ", ICON_TRANG_CHU),
			new MucMenu(KEY_KHACH_HANG, "Khách Hàng & Xe", ICON_KHACH_HANG),
			new MucMenu(KEY_TIEP_NHAN, "Tiếp Nhận Xe", ICON_TIEP_NHAN),
			new MucMenu(KEY_SUA_CHUA, "Lập Phiếu Sửa Chữa", ICON_SUA_CHUA),
			new MucMenu(KEY_BAO_CAO, "Báo Cáo Thống Kê", ICON_BAO_CAO) };

	private final LapPhieuTiepNhanGUI lapPhieuGUI;
	private final TraCuuPhieuTiepNhanGUI traCuuGUI;
	private final KhachHangVaXeGUI khachHangVaXeGUI;

	public TrangChuTiepNhanVienGUI(String hoTen) throws SQLException {
		super(hoTen, "Tiếp nhận viên", MENU);

		// Lập phiếu -> bấm "Tra cứu phiếu tiếp nhận" -> sang màn hình tra cứu (vẫn tô sáng nút "Tiếp Nhận Xe")
		lapPhieuGUI = new LapPhieuTiepNhanGUI(hoTen, () -> hienThi(KEY_TRA_CUU_TIEP_NHAN, KEY_TIEP_NHAN));
		traCuuGUI = new TraCuuPhieuTiepNhanGUI(() -> hienThi(KEY_TIEP_NHAN));
		khachHangVaXeGUI = new KhachHangVaXeGUI(() -> hienThi(KEY_TRANG_CHU));

		themCard(KEY_TIEP_NHAN, lapPhieuGUI);
		themCard(KEY_TRA_CUU_TIEP_NHAN, traCuuGUI);
		themCard(KEY_KHACH_HANG, khachHangVaXeGUI);
		// themCard(KEY_KHACH_HANG, new KhachHangGUI());
		// themCard(KEY_SUA_CHUA, new LapPhieuSuaChuaGUI());

		hoanTat();
	}

	@Override
	protected void onHienThi(String key) {
		if (KEY_TIEP_NHAN.equals(key)) {
			try {
				lapPhieuGUI.refresh();
				khachHangVaXeGUI.refresh();
			} catch (SQLException e) {
				e.printStackTrace();
			} // cập nhật mã phiếu / ngày / giờ
		} else if (KEY_TRA_CUU_TIEP_NHAN.equals(key)) {
			traCuuGUI.datLai(); // mặc định tra cứu các phiếu trong ngày hôm nay
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				new TrangChuTiepNhanVienGUI("Lê Thu Hà").setVisible(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
}
