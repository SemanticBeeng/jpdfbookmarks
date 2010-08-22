/*
 * UndoableBookmarksAction.java
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
import java.util.ArrayList;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;

public abstract class UndoableBookmarksAction extends AbstractUndoableEdit {

    protected DefaultTreeModel treeModel;
    protected Bookmark selectedBookmark;
    protected JTree tree;

    protected UndoableBookmarksAction(JTree tree) {
        this.tree = tree;
        treeModel = (DefaultTreeModel) tree.getModel();
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        doEdit();
    }

    public void doEdit() {
        
    }

    protected Bookmark getSelectedBookmark() {
        TreePath path = tree.getSelectionPath();
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

    protected ArrayList<Bookmark> getSelectedBookmarks() {

        ArrayList<Bookmark> bookmarksList = new ArrayList<Bookmark>();
        TreePath[] paths = tree.getSelectionPaths();
        for (TreePath path : paths) {
            bookmarksList.add((Bookmark) path.getLastPathComponent());
        }
        return bookmarksList;
    }
}
