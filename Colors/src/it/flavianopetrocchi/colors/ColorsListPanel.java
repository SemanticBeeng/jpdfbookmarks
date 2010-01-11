/*
 * ColorsListPanel.java
 *
 * Copyright (c) 2010 Flaviano Petrocchi <flavianopetrocchi at gmail.com>.
 * All rights reserved.
 *
 * This file is part of JPdfBookmarks.
 *
 * JPdfBookmarks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JPdfBookmarks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JPdfBookmarks.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.flavianopetrocchi.colors;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ColorsListPanel extends AbstractColorChooserPanel 
		implements ListSelectionListener {
	private JList list;

	@Override
	public void updateChooser() {
		Color color = getColorFromModel();
		Colors colorsItem = Colors.lookUp(color);
		if (colorsItem != null) {
			list.setSelectedValue(colorsItem, true);
		}
	}

	@Override
	protected void buildChooser() {
		setLayout(new BorderLayout());
		list = Colors.getColorsList();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setVisibleRowCount(20);
		JScrollPane scroller = new JScrollPane(list);
		add(scroller, BorderLayout.CENTER);
		list.addListSelectionListener(this);
	}

	@Override
	public String getDisplayName() {
		return getName();
	}

	@Override
	public Icon getSmallDisplayIcon() {
		return null;
	}

	@Override
	public Icon getLargeDisplayIcon() {
		return null;
	}

	public void valueChanged(ListSelectionEvent e) {
		Colors item = (Colors) list.getSelectedValue();
		getColorSelectionModel().setSelectedColor(item.getColor());
	}

}
