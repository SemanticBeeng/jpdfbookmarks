/*
 * BookmarksTree.java
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

package it.flavianopetrocchi.jpdfbookmarks;

import it.flavianopetrocchi.mousedraggabletree.MouseDraggableTree;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

public class BookmarksTree extends MouseDraggableTree {

	public BookmarksTree() {
		BookmarkRenderer bookmarkRenderer = new BookmarkRenderer();
		setCellRenderer(bookmarkRenderer);
	}

	private class BookmarkRenderer extends DefaultTreeCellRenderer {

		Color draggingBackground = Color.lightGray;
		Color draggingForeground = Color.white;
		Color stdBackground = getBackgroundNonSelectionColor();
		Color stdSelectedBackground = getBackgroundSelectionColor();
		Color stdNonSelectionForeground = getTextNonSelectionColor();
		Color stdSelectionForeground = getTextSelectionColor();
		DefaultMutableTreeNode previous;
		ImageIcon borderIcon;

		@Override
		public Component getTreeCellRendererComponent(JTree tree,
				Object value, boolean sel, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {

			Bookmark node = null;
			if (value instanceof Bookmark) {
				node = (Bookmark) value;
				int styleMask = Font.PLAIN;
				if (node.isItalic()) {
					styleMask |= Font.ITALIC;
				}
				if (node.isBold()) {
					styleMask |= Font.BOLD;
				}
				Font font = new Font(getFont().getFamily(), styleMask,
						getFont().getSize());
				setFont(font);
				setBorder(null);

				setTextNonSelectionColor(node.getColor());
			}

			setBorder(null);
			Color borderColor = stdNonSelectionForeground.brighter();
			for (int i = 0; i < 4; i++) {
				borderColor = borderColor.brighter();
			}

			if (value.equals(draggingOverNode)) {
				switch (moveType) {
					case MOVE_AS_CHILD:
						if (node != null) {
							if (previous == null || (node != previous)) {
								TreePath path = new TreePath(node.getPath());
								Rectangle rect = getPathBounds(path);
								Icon lblIcon = getIcon();
								int iconWidth = lblIcon.getIconWidth();
								BufferedImage borderImage = new BufferedImage(
										rect.width, 3,
										BufferedImage.TYPE_INT_ARGB);
								Graphics2D g2 = (Graphics2D) borderImage.getGraphics();
								g2.setColor(borderColor);
								int childLineStart = iconWidth + getIconTextGap();
								g2.fillRect(childLineStart, 0,
										rect.width - childLineStart, 3);
								g2.dispose();
								borderIcon = new ImageIcon(borderImage);
								previous = node;
							}

							setBorder(BorderFactory.createMatteBorder(
									0, 0, 3, 0, borderIcon));
						}
						break;
					case MOVE_AS_SIBLING_BEFORE:
						setBorder(BorderFactory.createMatteBorder(
								3, 0, 0, 0, borderColor));
						break;
					case MOVE_AS_SIBLING_AFTER:
						setBorder(BorderFactory.createMatteBorder(
								0, 0, 3, 0, borderColor));
						break;
				}

				sel = false;
			}

			Component res = super.getTreeCellRendererComponent(tree, value,
					sel, expanded, leaf, row, hasFocus);

			return res;
		}
	}
}
