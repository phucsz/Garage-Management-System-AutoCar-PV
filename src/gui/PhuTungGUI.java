package gui;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import business.PhuTungBUS;
import entity.DanhMucPhuTung;
import entity.NhaCungCap;
import entity.PhuTung;

public class PhuTungGUI extends QuanLyCRUDGUI<PhuTung> {
	private static final long serialVersionUID = 1L;
	private final PhuTungBUS bus = new PhuTungBUS();

	public PhuTungGUI() {
		super("Quản lý phụ tùng",
				new String[] { "Mã phụ tùng", "Tên phụ tùng", "Mã danh mục", "Đơn vị tính", "Đơn giá",
						"Số lượng tồn", "Ngày sản xuất (yyyy-MM-dd)", "Mã nhà cung cấp" },
				new String[] { "Mã phụ tùng", "Tên phụ tùng", "Danh mục", "Đơn vị", "Đơn giá", "Tồn kho",
						"Ngày sản xuất", "Nhà cung cấp" });
		refresh();
	}

	@Override protected List<PhuTung> load() throws SQLException { return bus.getAll(); }
	@Override protected PhuTung from(String[] v) {
		if (v[0].isEmpty() || v[1].isEmpty() || v[7].isEmpty())
			throw new IllegalArgumentException("Mã, tên và nhà cung cấp không được trống.");
		double price = Double.parseDouble(v[4]);
		int stock = Integer.parseInt(v[5]);
		if (price < 0 || stock < 0) throw new IllegalArgumentException("Đơn giá và tồn kho không được âm.");
		LocalDate manufactured = v[6].isEmpty() ? null : LocalDate.parse(v[6]);
		return new PhuTung(v[0], v[1], v[2].isEmpty() ? null : new DanhMucPhuTung(v[2]), v[3], price, stock, manufactured,
				new NhaCungCap(v[7]));
	}
	@Override protected boolean insert(PhuTung value) throws SQLException { return bus.them(value); }
	@Override protected boolean update(PhuTung value) throws SQLException { return bus.capNhat(value); }
	@Override protected boolean remove(PhuTung value) throws SQLException { return bus.xoa(value.getMaPhuTung()); }
	@Override protected String[] valuesOf(PhuTung value) {
		return new String[] { value.getMaPhuTung(), value.getTenPhuTung(),
				value.getDanhMucPhuTung() == null ? "" : value.getDanhMucPhuTung().getMaDanhMuc(),
				value.getDonViTinh(), String.valueOf(value.getDonGiaHienTai()), String.valueOf(value.getSoLuongTon()),
				value.getNgaySanXuat() == null ? "" : value.getNgaySanXuat().toString(),
				value.getNhaCungCap() == null ? "" : value.getNhaCungCap().getMaNCC() };
	}
	@Override protected String idOf(PhuTung value) { return value.getMaPhuTung(); }
}
