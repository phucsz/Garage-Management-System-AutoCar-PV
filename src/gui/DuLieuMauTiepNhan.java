package gui;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * DỮ LIỆU MẪU TẠM THỜI (lưu trong bộ nhớ) dùng chung cho LapPhieuTiepNhanGUI và TraCuuPhieuTiepNhanGUI,
 * để phiếu vừa lập có thể được tra cứu ngay.
 *
 * TODO: khi nối CSDL, thay các lớp lồng nhau bằng entity của project và thay các danh sách / hàm
 * tìm kiếm bên dưới bằng lời gọi DAO. Có thể xóa file này sau khi thay xong.
 */
public final class DuLieuMauTiepNhan {

	private DuLieuMauTiepNhan() {
	}

	public static final String[] TRANG_THAI = { "Đã tiếp nhận", "Đang sửa chữa", "Hoàn thành", "Đã hủy" };

	private static final DateTimeFormatter FMT_MA = DateTimeFormatter.ofPattern("ddMMyyyy");

	// =====================================================================
	// CÁC LỚP DỮ LIỆU
	// =====================================================================
	public static class KhachHang {
		public final String sdt, ten, diaChi;

		public KhachHang(String sdt, String ten, String diaChi) {
			this.sdt = sdt;
			this.ten = ten;
			this.diaChi = diaChi;
		}
	}

	public static class Xe {
		public final String bienSo, hangXe;
		public final int soKm;

		public Xe(String bienSo, String hangXe, int soKm) {
			this.bienSo = bienSo;
			this.hangXe = hangXe;
			this.soKm = soKm;
		}
	}

	public static class Phieu {
		public String ma;
		public LocalDate ngay;
		public LocalTime gio;
		public String sdt, tenKhach, diaChi;
		public String bienSo, hangXe, soKm;
		public String yeuCau, nguoiTiepNhan, trangThai;
		/** Hạng mục có lỗi -> mô tả tình trạng (hạng mục bỏ trống = không có lỗi). */
		public Map<String, String> tinhTrang = new LinkedHashMap<>();
	}

	public static final List<KhachHang> KHACH_HANG = new ArrayList<>();
	public static final List<Xe> XE = new ArrayList<>();
	public static final List<Phieu> PHIEU = new ArrayList<>();

	// =====================================================================
	// TÌM KIẾM
	// =====================================================================
	public static KhachHang timKhachHang(String sdt) {
		String so = chiSo(sdt);
		for (KhachHang k : KHACH_HANG) {
			if (k.sdt.equals(so)) {
				return k;
			}
		}
		return null;
	}

	public static Xe timXe(String bienSo) {
		String bs = chuanHoaBienSo(bienSo);
		for (Xe x : XE) {
			if (chuanHoaBienSo(x.bienSo).equals(bs)) {
				return x;
			}
		}
		return null;
	}

	/** Sinh mã phiếu dạng ddMMyyyyPTNxxx (xxx = số thứ tự trong ngày). */
	public static String taoMaPhieu(LocalDate ngay) {
		int dem = 0;
		for (Phieu p : PHIEU) {
			if (p.ngay.equals(ngay)) {
				dem++;
			}
		}
		return ngay.format(FMT_MA) + "PTN" + String.format("%03d", dem + 1);
	}

	// =====================================================================
	// TIỆN ÍCH CHUỖI
	// =====================================================================
	/** Giữ lại các chữ số: "0903 123 456" -> "0903123456". */
	public static String chiSo(String s) {
		return s == null ? "" : s.replaceAll("\\D", "");
	}

	/** "51g-123.45" -> "51G12345" để so khớp biển số không phụ thuộc dấu phân cách. */
	public static String chuanHoaBienSo(String s) {
		return s == null ? "" : s.replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ROOT);
	}

	/** Bỏ dấu tiếng Việt + chữ thường để tìm kiếm không phân biệt dấu. */
	public static String boDau(String s) {
		if (s == null) {
			return "";
		}
		String t = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
		return t.replace('đ', 'd').replace('Đ', 'D').toLowerCase(Locale.ROOT);
	}

	// =====================================================================
	// DỮ LIỆU KHỞI TẠO
	// =====================================================================
	static {
		KHACH_HANG.add(new KhachHang("0903123456", "Nguyễn Văn A", "12 Nguyễn Văn Bảo, phường Hạnh Thông, TP.HCM"));
		KHACH_HANG.add(new KhachHang("0912567890", "Bùi Thanh Sơn", "45 Lê Lợi, Quận 1, TP.HCM"));
		KHACH_HANG.add(new KhachHang("0987654321", "Trần Thị Mai", "88 Nguyễn Trãi, Quận 5, TP.HCM"));
		KHACH_HANG.add(new KhachHang("0933444555", "Lê Quốc Huy", "21 Phan Văn Trị, phường 7, Gò Vấp, TP.HCM"));

		XE.add(new Xe("51G-123.45", "Toyota Corolla Altis", 5000));
		XE.add(new Xe("92A-567.11", "Mazda 3", 32000));
		XE.add(new Xe("59C-888.88", "Honda CR-V", 18000));
		XE.add(new Xe("51H-456.78", "Hyundai Accent", 41000));

		// (số ngày trước, giờ, phút, sđt, biển số, yêu cầu, trạng thái)
		them(12, 9, 15, "0987654321", "59C-888.88", "Bảo dưỡng định kỳ 20.000 km", "Hoàn thành");
		them(8, 14, 0, "0933444555", "51H-456.78", "Điều hòa kém mát, kiểm tra gas", "Hoàn thành");
		them(5, 10, 30, "0903123456", "51G-123.45", "Thay dầu động cơ, kiểm tra phanh", "Đang sửa chữa");
		them(3, 8, 45, "0987654321", "59C-888.88", "Xe rung lắc khi chạy trên 80 km/h", "Đã hủy");
		them(2, 15, 20, "0933444555", "51H-456.78", "Thay lốp trước, cân chỉnh thước lái", "Đã tiếp nhận");
		them(1, 11, 5, "0903123456", "51G-123.45", "Báo lỗi đèn check engine", "Đang sửa chữa");
		them(0, 8, 30, "0912567890", "92A-567.11", "Thay ắc quy, bugi", "Đã tiếp nhận");
		them(0, 9, 10, "0987654321", "59C-888.88", "Kiểm tra tổng quát trước chuyến đi xa", "Đã tiếp nhận");
	}

	private static void them(int ngayTruoc, int gio, int phut, String sdt, String bienSo, String yeuCau,
			String trangThai) {
		KhachHang k = timKhachHang(sdt);
		Xe x = timXe(bienSo);
		Phieu p = new Phieu();
		p.ngay = LocalDate.now().minusDays(ngayTruoc);
		p.gio = LocalTime.of(gio, phut);
		p.ma = taoMaPhieu(p.ngay);
		p.sdt = k.sdt;
		p.tenKhach = k.ten;
		p.diaChi = k.diaChi;
		p.bienSo = x.bienSo;
		p.hangXe = x.hangXe;
		p.soKm = String.valueOf(x.soKm);
		p.yeuCau = yeuCau;
		p.nguoiTiepNhan = "Trần Minh";
		p.trangThai = trangThai;
		PHIEU.add(p);
	}
}
