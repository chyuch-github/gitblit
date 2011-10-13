/*
 * Copyright 2011 gitblit.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gitblit.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class JPalette<T> extends JPanel {

	private static final long serialVersionUID = 1L;
	private PaletteModel<T> availableModel;
	private PaletteModel<T> selectedModel;

	public JPalette() {
		super(new BorderLayout(5, 5));

		availableModel = new PaletteModel<T>();
		selectedModel = new PaletteModel<T>();

		final JTable available = new JTable(availableModel);
		final JTable selected = new JTable(selectedModel);

		JButton add = new JButton("->");
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				List<T> move = new ArrayList<T>();
				if (available.getSelectedRowCount() <= 0) {
					return;
				}
				for (int row : available.getSelectedRows()) {
					int modelIndex = available.convertRowIndexToModel(row);
					T item = (T) availableModel.list.get(modelIndex);
					move.add(item);
				}
				availableModel.list.removeAll(move);
				selectedModel.list.addAll(move);
				availableModel.fireTableDataChanged();
				selectedModel.fireTableDataChanged();
			}
		});
		JButton subtract = new JButton("<-");
		subtract.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				List<T> move = new ArrayList<T>();
				if (selected.getSelectedRowCount() <= 0) {
					return;
				}
				for (int row : selected.getSelectedRows()) {
					int modelIndex = selected.convertRowIndexToModel(row);
					T item = (T) selectedModel.list.get(modelIndex);
					move.add(item);
				}
				selectedModel.list.removeAll(move);
				availableModel.list.addAll(move);

				selectedModel.fireTableDataChanged();
				availableModel.fireTableDataChanged();
			}
		});

		JPanel controls = new JPanel(new GridLayout(0, 1, 0, 5));
		controls.add(add);
		controls.add(subtract);

		JPanel center = new JPanel(new GridBagLayout());
		center.add(controls);

		add(newListPanel("Available", available), BorderLayout.WEST);
		add(center, BorderLayout.CENTER);
		add(newListPanel("Selected", selected), BorderLayout.EAST);
	}

	private JPanel newListPanel(String label, JTable table) {
		NameRenderer nameRenderer = new NameRenderer();
		table.setCellSelectionEnabled(false);
		table.setRowSelectionAllowed(true);
		table.setRowHeight(nameRenderer.getFont().getSize() + 8);
		table.getTableHeader().setReorderingAllowed(false);
		table.setGridColor(new Color(0xd9d9d9));
		table.setBackground(Color.white);
		table.getColumn(table.getColumnName(0)).setCellRenderer(nameRenderer);

		JScrollPane jsp = new JScrollPane(table);
		jsp.setPreferredSize(new Dimension(225, 175));
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel(label), BorderLayout.NORTH);
		panel.add(jsp, BorderLayout.CENTER);
		return panel;
	}

	public void setObjects(List<T> all, List<T> selected) {
		List<T> available = new ArrayList<T>(all);
		if (selected != null) {
			available.removeAll(selected);
		}
		availableModel.list.clear();
		availableModel.list.addAll(available);
		availableModel.fireTableDataChanged();

		if (selected != null) {
			selectedModel.list.clear();
			selectedModel.list.addAll(selected);
			selectedModel.fireTableDataChanged();
		}
	}

	public List<T> getSelections() {
		return new ArrayList<T>(selectedModel.list);
	}

	public class PaletteModel<K> extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		List<K> list;

		public PaletteModel() {
			this(new ArrayList<K>());
		}

		public PaletteModel(List<K> list) {
			this.list = new ArrayList<K>(list);
		}

		@Override
		public int getRowCount() {
			return list.size();
		}

		@Override
		public int getColumnCount() {
			return 1;
		}

		@Override
		public String getColumnName(int column) {
			return "Name";
		}

		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			K o = list.get(rowIndex);
			return o.toString();
		}
	}
}
