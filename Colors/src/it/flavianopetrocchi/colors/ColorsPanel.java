/*
 * ColorsPanel.java
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

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.colorchooser.AbstractColorChooserPanel;

public class ColorsPanel extends AbstractColorChooserPanel 
		implements ItemListener{
	private JComboBox combo;

	@Override
	public void updateChooser() {
		Color color = getColorFromModel();
		Colors colorsItem = Colors.lookUp(color);
		if (colorsItem != null) {
			combo.setSelectedItem(colorsItem);
		}
	}

	@Override
	protected void buildChooser() {
		combo = Colors.getColorsChooser();
		add(combo);
		combo.addItemListener(this);
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

	public void itemStateChanged(ItemEvent e) {
		Colors item = (Colors) e.getItem();
		getColorSelectionModel().setSelectedColor(item.getColor());
	}

}
