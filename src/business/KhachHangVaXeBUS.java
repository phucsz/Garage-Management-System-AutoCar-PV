package business;

import java.sql.SQLException;
import java.util.ArrayList;

import dao.KhachHangDAO;
import dao.XeDAO;
import entity.KhachHang;
import entity.Xe;

public class KhachHangVaXeBUS {
	private final KhachHangDAO khachHangDAO = new KhachHangDAO();
	private final XeDAO xeDAO = new XeDAO();

	public ArrayList<KhachHang> timKiemKhachHang(String hoTen, String sdt) throws SQLException {
		return khachHangDAO.timKiem(hoTen, sdt);
	}

	public boolean themKhachHang(KhachHang kh) throws SQLException {
		kh.setMaKH(khachHangDAO.taoMaKHMoi());
		return khachHangDAO.themKH(kh);
	}

	public boolean capNhatKhachHang(KhachHang kh) throws SQLException {
		return khachHangDAO.capNhatKH(kh);
	}

	public boolean xoaKhachHang(String maKH) throws SQLException {
		return khachHangDAO.xoaKH(maKH);
	}

	public ArrayList<Xe> getXeTheoKhachHang(String maKH) throws SQLException {
		return xeDAO.getXeTheoKhachHang(maKH);
	}

	public boolean capNhatXe(Xe xe) throws SQLException {
		return xeDAO.capNhatXe(xe);
	}
}
