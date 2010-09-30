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

import it.flavianopetrocchi.jpdfbookmarks.bookmark.Bookmark;
import it.flavianopetrocchi.mousedraggabletree.MouseDraggableTree;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTree;

public class BookmarksTree extends MouseDraggableTree {

    public BookmarksTree() {
        BookmarkRenderer bookmarkRenderer = new BookmarkRenderer();
        setCellRenderer(bookmarkRenderer);

    }

    private class BookmarkRenderer extends MouseDraggableTree.CustomRenderer {

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

            Component res = super.getTreeCellRendererComponent(tree, value,
                    sel, expanded, leaf, row, hasFocus);

            return res;
        }
    }
}
