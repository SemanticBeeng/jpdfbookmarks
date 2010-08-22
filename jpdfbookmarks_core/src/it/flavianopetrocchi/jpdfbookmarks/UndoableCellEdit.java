/*
 * UndoableCellEdit.java
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
import javax.swing.tree.DefaultTreeModel;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

public class UndoableCellEdit extends AbstractUndoableEdit {

    private String newText;
    private String oldText;
    private DefaultTreeModel treeModel;
    private Bookmark bookmark;

    public UndoableCellEdit(DefaultTreeModel model, Bookmark bookmark,
            String newText) {
        this.treeModel = model;
        this.bookmark = bookmark;
        this.newText = newText;
        this.oldText = bookmark.getTitle();
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        bookmark.setTitle(oldText);
        treeModel.nodeStructureChanged(bookmark);
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        doEdit();
    }

    public void doEdit() {
        bookmark.setTitle(newText);
        treeModel.nodeStructureChanged(bookmark);
    }
}
