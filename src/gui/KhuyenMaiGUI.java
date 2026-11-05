package gui;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import business.KhuyenMaiBUS;
import entity.ChuongTrinhKhuyenMai;

public class KhuyenMaiGUI extends QuanLyCRUDGUI<ChuongTrinhKhuyenMai> {
	private static final long serialVersionUID = 1L;
	private final KhuyenMaiBUS bus = new KhuyenMaiBUS();

	public KhuyenMaiGUI() {
		super("Quản lý khuyến mãi",
				new String[] { "Mã chương trình", "Tên chương trình", "Ngày bắt đầu (yyyy-MM-dd)",
						"Ngày kết thúc (yyyy-MM-dd)", "Loại khuyến mãi", "Giá trị khuyến mãi" },
				new String[] { "Mã chương trình", "Tên chương trình", "Bắt đầu", "Kết thúc", "Loại", "Giá trị" });
		refresh();
	}

	@Override protected List<ChuongTrinhKhuyenMai> load() throws SQLException { return bus.getAll(); }
	@Override protected ChuongTrinhKhuyenMai from(String[] v) {
		if (v[0].isEmpty() || v[1].isEmpty() || v[4].isEmpty())
			throw new IllegalArgumentException("Mã, tên và loại khuyến mãi không được trống.");
		LocalDate start = LocalDate.parse(v[2]);
		LocalDate end = LocalDate.parse(v[3]);
		if (end.isBefore(start)) throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu.");
		double amount = Double.parseDouble(v[5]);
		if (amount < 0) throw new IllegalArgumentException("Giá trị khuyến mãi phải lớn hơn hoặc bằng 0.");
		return new ChuongTrinhKhuyenMai(v[0], v[1], start, end, v[4], amount);
	}
	@Override protected boolean insert(ChuongTrinhKhuyenMai value) throws SQLException { return bus.them(value); }
	@Override protected boolean update(ChuongTrinhKhuyenMai value) throws SQLException { return bus.capNhat(value); }
	@Override protected boolean remove(ChuongTrinhKhuyenMai value) throws SQLException { return bus.xoa(value.getMaChuongTrinh()); }
	@Override protected String[] valuesOf(ChuongTrinhKhuyenMai value) {
		return new String[] { value.getMaChuongTrinh(), value.getTenChuongTrinh(), value.getNgayBatDau().toString(),
				value.getNgayKetThuc().toString(), value.getLoaiKhuyenMai(), String.valueOf(value.getGiaTriKhuyenMai()) };
	}
	@Override protected String idOf(ChuongTrinhKhuyenMai value) { return value.getMaChuongTrinh(); }
}
