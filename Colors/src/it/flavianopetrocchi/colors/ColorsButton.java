/*
 * ColorsButton.java
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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;


public class ColorsButton extends JButton {

	private	Color color = Color.black;
	private int iconSize = 22;

	public ColorsButton(int iconSize) {
		this.iconSize = iconSize;
		setIcon(createColorIcon(iconSize));
	}

	public ColorsButton(Icon icon) {
		super(icon);
	}

	private ImageIcon createColorIcon(int iconSize) {
		FontMetrics metrics = getFontMetrics(getFont());
		int lenght = metrics.getHeight() - metrics.getDescent();
		BufferedImage image = new BufferedImage(iconSize, iconSize,
				BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		g.setColor(color);
		g.fillRect(0, 0, lenght, lenght);
		g.setColor(getForeground());
		g.drawRect(0, 0, lenght - 1, lenght - 1);
		g.dispose();
		return new ImageIcon(image);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		//setIcon(createColorIcon(iconSize));
	}
}
