package gui;

import static gui.UITheme.*;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/** Khung CRUD dùng chung cho các danh mục đơn giản của trang quản lý. */
abstract class QuanLyCRUDGUI<T> extends JPanel {
	private static final long serialVersionUID = 1L;
	private final JTextField[] inputs;
	private final JTable table;
	private final DefaultTableModel model;
	private T selected;

	protected QuanLyCRUDGUI(String title, String[] fields, String[] columns) {
		setLayout(new BorderLayout(0, 12));
		setBackground(BG);
		setBorder(BorderFactory.createEmptyBorder(22, 28, 28, 28));
		JLabel heading = new JLabel(title);
		heading.setFont(FONT_TITLE);
		heading.setForeground(TITLE);
		add(heading, BorderLayout.NORTH);

		JPanel body = new JPanel(new BorderLayout(0, 12));
		body.setOpaque(false);
		JPanel form = new JPanel(new GridLayout(0, 4, 10, 8));
		form.setOpaque(false);
		inputs = new JTextField[fields.length];
		for (int i = 0; i < fields.length; i++) {
			inputs[i] = new JTextField();
			form.add(labeled(fields[i], inputs[i]));
		}
		JPanel actions = new JPanel();
		actions.setOpaque(false);
		JButton add = new JButton("Thêm");
		JButton edit = new JButton("Cập nhật");
		JButton remove = new JButton("Xóa");
		JButton clear = new JButton("Làm mới");
		actions.add(add); actions.add(edit); actions.add(remove); actions.add(clear);
		JPanel top = new JPanel(new BorderLayout(0, 8));
		top.setOpaque(false);
		top.add(form, BorderLayout.CENTER);
		top.add(actions, BorderLayout.SOUTH);
		body.add(top, BorderLayout.NORTH);

		model = new DefaultTableModel(columns, 0) {
			private static final long serialVersionUID = 1L;
			@Override public boolean isCellEditable(int row, int column) { return false; }
		};
		table = new JTable(model);
		table.setRowHeight(30);
		table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		body.add(new JScrollPane(table), BorderLayout.CENTER);
		add(body, BorderLayout.CENTER);

		add.addActionListener(e -> save(false));
		edit.addActionListener(e -> save(true));
		remove.addActionListener(e -> delete());
		clear.addActionListener(e -> clearForm());
		table.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
				selected = itemAt(table.getSelectedRow());
				String[] values = valuesOf(selected);
				for (int i = 0; i < inputs.length; i++) inputs[i].setText(values[i]);
				inputs[0].setEditable(false);
			}
		});
	}

	protected abstract List<T> load() throws SQLException;
	protected abstract T from(String[] values) throws Exception;
	protected abstract boolean insert(T value) throws SQLException;
	protected abstract boolean update(T value) throws SQLException;
	protected abstract boolean remove(T value) throws SQLException;
	protected abstract String[] valuesOf(T value);
	protected abstract String idOf(T value);

	public final void refresh() {
		try {
			model.setRowCount(0);
			for (T value : load()) {
				model.addRow(valuesOf(value));
			}
		} catch (SQLException e) {
			showError("Không thể tải dữ liệu: " + e.getMessage());
		}
	}

	private void save(boolean editing) {
		try {
			String[] values = new String[inputs.length];
			for (int i = 0; i < inputs.length; i++) values[i] = inputs[i].getText().trim();
			T value = from(values);
			boolean ok = editing ? update(value) : insert(value);
			if (!ok) throw new SQLException("Không có bản ghi nào được thay đổi.");
			refresh();
			clearForm();
			JOptionPane.showMessageDialog(this, editing ? "Đã cập nhật." : "Đã thêm mới.", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			showError("Không thể lưu dữ liệu: " + e.getMessage());
		}
	}

	private void delete() {
		if (selected == null) {
			showError("Hãy chọn một dòng để xóa.");
			return;
		}
		if (JOptionPane.showConfirmDialog(this, "Xóa " + idOf(selected) + "?", "Xác nhận",
				JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
		try {
			if (!remove(selected)) throw new SQLException("Không có bản ghi nào được xóa.");
			refresh();
			clearForm();
		} catch (SQLException e) {
			showError("Không thể xóa. Bản ghi có thể đang được sử dụng ở nghiệp vụ khác.\n" + e.getMessage());
		}
	}

	private void clearForm() {
		selected = null;
		for (JTextField input : inputs) { input.setText(""); input.setEditable(true); }
		table.clearSelection();
	}

	private T itemAt(int row) {
		String id = String.valueOf(model.getValueAt(row, 0));
		try {
			for (T value : load()) if (id.equals(idOf(value))) return value;
		} catch (SQLException e) {
			showError("Không thể đọc bản ghi: " + e.getMessage());
		}
		return null;
	}

	private void showError(String message) {
		JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
	}
}
