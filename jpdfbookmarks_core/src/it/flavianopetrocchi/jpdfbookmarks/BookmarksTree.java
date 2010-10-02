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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

public class BookmarksTree extends MouseDraggableTree implements CellEditorListener {

    private Bookmark lastFollowedBookmark;
    private CellTextField editor = new CellTextField();

    public BookmarksTree() {
        BookmarkRenderer bookmarkRenderer = new BookmarkRenderer();
        setCellRenderer(bookmarkRenderer);
        //DefaultCellEditor customEditor = new DefaultCellEditor(editor);
        CustomCellEditor customEditor = new CustomCellEditor(bookmarkRenderer, editor);
        setCellEditor(customEditor);
    }

    public void setLastFollowedBookmark(Bookmark b) {
        lastFollowedBookmark = b;
    }

    @Override
    public void editingStopped(ChangeEvent e) {
        Bookmark b = getSelectedBookmark();
        if (b != null) {
            b.setTitle(editor.getText());
        }
    }

    @Override
    public void editingCanceled(ChangeEvent e) {
//        getSelectedBookmark().setTitle(editor.getText());
    }

    private Bookmark getSelectedBookmark() {
        TreePath path = getSelectionPath();
        if (path == null) {
            return null;
        }

        Bookmark treeNode = null;
        try {
            treeNode = (Bookmark) path.getLastPathComponent();
        } catch (ClassCastException e) {
        }
        return treeNode;
    }

    private class CellTextField extends JTextField {

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            if (d.width < 100) {
                d.width = 100;
            }
            return d;
        }

    }

    private class CustomCellEditor extends DefaultCellEditor {
        private BookmarkRenderer renderer;

        public CustomCellEditor(BookmarkRenderer renderer, JTextField editor) {
            super(editor);
            this.renderer = renderer;
        }

        @Override
        public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {

            DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            TreePath path = new TreePath(node.getPath());

            JTextField c = (JTextField) super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
            FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 0, 0);
            JPanel panel = new JPanel(layout);
            JLabel lbl = new JLabel();
            if (node.isLeaf()) {
                lbl.setIcon(defaultRenderer.getLeafIcon());
            } else if (tree.isCollapsed(path)) {
                lbl.setIcon(defaultRenderer.getClosedIcon());
            } else if (tree.isExpanded(path)) {
                lbl.setIcon(defaultRenderer.getOpenIcon());
            }
//            int iconWidth = renderer.getIcon().getIconWidth();
//            int offset = renderer.getIconTextGap() + iconWidth;
//            panel.setBorder(BorderFactory.createEmptyBorder(0, offset, 0, 0));
            panel.setOpaque(false);
//            panel.setBorder(null);
            Border b = UIManager.getBorder("Tree.editorBorder");
            c.setBorder(new CompoundBorder(b, BorderFactory.createEmptyBorder(0, renderer.getIconTextGap(), 0, 0)));
//            c.setBorder(BorderFactory.createEmptyBorder(0, renderer.getIconTextGap(), 0, 0));
//            panel.setLayout(null);
//            c.setBounds(0, 0, 100, 25);
            panel.add(lbl);
            panel.add(c);
            return panel;
        }

    }

    private class BookmarkRenderer extends MouseDraggableTree.CustomRenderer {

        public BookmarkRenderer() {
        }

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

                if (node.equals(lastFollowedBookmark)) {
                    setEnabled(false);
                } else {
                    setEnabled(true);
                }

                setForeground(node.getColor());
                setText(node.getTitle() + " ");
            }

            Component res = super.getTreeCellRendererComponent(tree, value,
                    sel, expanded, leaf, row, hasFocus);

            return res;
        }
    }
}
