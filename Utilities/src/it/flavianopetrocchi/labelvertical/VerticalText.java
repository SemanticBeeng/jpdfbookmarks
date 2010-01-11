/*
 * VerticalText.java
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

package it.flavianopetrocchi.labelvertical;

import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.JLabel;

public class VerticalText extends JPanel {
	private JLabel lblToCopy;
	private Dimension lblSize;
	private String text;

	public VerticalText(String text) {
		this.text = text;
		lblToCopy = new JLabel(" " + text);
		lblSize = lblToCopy.getPreferredSize();
		lblToCopy.setVisible(false);
		add(lblToCopy);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(lblSize.height, lblSize.width);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		lblToCopy = new JLabel(" " + text);
		lblSize = lblToCopy.getPreferredSize();
		Graphics2D g2 = (Graphics2D) g;
		BufferedImage img = new BufferedImage(lblSize.width, lblSize.height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2img = (Graphics2D) img.getGraphics();
		g2img.setColor(lblToCopy.getForeground());
		g2img.setFont(lblToCopy.getFont());
		int base = lblToCopy.getBaseline(lblSize.width, lblSize.height);
		
		g2img.drawString(lblToCopy.getText(), 0, base);
		AffineTransform t = new AffineTransform();
		t.translate(lblSize.height, lblSize.width);
		t.rotate(Math.toRadians(270.0));
		t.translate(0, -lblSize.height);
		g2.drawImage(img, t, this);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				createAndShowGUI();
			}
		});
	}

	private static void createAndShowGUI() {
		System.out.println("Created GUI on EDT? " +
				SwingUtilities.isEventDispatchThread());
		JFrame f = new JFrame("Swing Paint Demo");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(new VerticalText("Ciao"));
		f.pack();
		f.setVisible(true);
	}
}

