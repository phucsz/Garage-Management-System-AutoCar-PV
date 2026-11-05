package gui;

import java.sql.SQLException;
import java.util.List;

import business.DichVuBUS;
import entity.DichVu;

public class DichVuGUI extends QuanLyCRUDGUI<DichVu> {
	private static final long serialVersionUID = 1L;
	private final DichVuBUS bus = new DichVuBUS();

	public DichVuGUI() {
		super("Quản lý dịch vụ", new String[] { "Mã dịch vụ", "Tên dịch vụ", "Loại dịch vụ", "Đơn giá hiện tại" },
				new String[] { "Mã dịch vụ", "Tên dịch vụ", "Loại dịch vụ", "Đơn giá" });
		refresh();
	}

	@Override protected List<DichVu> load() throws SQLException { return bus.getAll(); }
	@Override protected DichVu from(String[] v) {
		if (v[0].isEmpty() || v[1].isEmpty()) throw new IllegalArgumentException("Mã và tên dịch vụ không được trống.");
		double price = Double.parseDouble(v[3]);
		if (price < 0) throw new IllegalArgumentException("Đơn giá phải lớn hơn hoặc bằng 0.");
		return new DichVu(v[0], v[1], v[2], price);
	}
	@Override protected boolean insert(DichVu value) throws SQLException { return bus.them(value); }
	@Override protected boolean update(DichVu value) throws SQLException { return bus.capNhat(value); }
	@Override protected boolean remove(DichVu value) throws SQLException { return bus.xoa(value.getMaDichVu()); }
	@Override protected String[] valuesOf(DichVu value) {
		return new String[] { value.getMaDichVu(), value.getTenDichVu(), value.getLoaiDichVu(),
				String.valueOf(value.getDonGiaHienTai()) };
	}
	@Override protected String idOf(DichVu value) { return value.getMaDichVu(); }
}
