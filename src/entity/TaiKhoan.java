package entity;

public class TaiKhoan {
	private NhanVien tenDangNhap;
	private String matKhau;
	private VaiTro vaiTro;
	
	public TaiKhoan() {
		// TODO Auto-generated constructor stub
	}

	public TaiKhoan(NhanVien tenDangNhap, String matKhau, VaiTro vaiTro) {
		this.tenDangNhap = tenDangNhap;
		this.matKhau = matKhau;
		this.vaiTro = vaiTro;
	}

	public NhanVien getTenDangNhap() {
		return tenDangNhap;
	}

	public void setTenDangNhap(NhanVien tenDangNhap) {
		this.tenDangNhap = tenDangNhap;
	}

	public String getMatKhau() {
		return matKhau;
	}

	public void setMatKhau(String matKhau) {
		this.matKhau = matKhau;
	}

	public VaiTro getVaiTro() {
		return vaiTro;
	}

	public void setVaiTro(VaiTro vaiTro) {
		this.vaiTro = vaiTro;
	}
	@Override
	public String toString() {
		return String.format("%s %s %s", tenDangNhap.getMaNV(),matKhau,vaiTro.getMaVaiTro());
	}
}
