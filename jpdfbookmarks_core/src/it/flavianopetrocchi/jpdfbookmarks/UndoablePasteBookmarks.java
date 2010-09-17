/*
 * UndoablePasteBookmarks.java
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
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

public class UndoablePasteBookmarks extends AbstractUndoableEdit {

    private DefaultTreeModel treeModel;
    private ArrayList<Bookmark> bookmarksCopied;
    private UndoableDeleteBookmark undoableDelete;

    public UndoablePasteBookmarks(DefaultTreeModel model, ArrayList<Bookmark> bookmarksCopied) {
        this.treeModel = model;
        this.bookmarksCopied = bookmarksCopied;
    }

    @Override
    public void redo() throws CannotRedoException {

        super.redo();
        undoableDelete.undo();
    }

    @Override
    public void undo() throws CannotUndoException {

        super.undo();
        if (undoableDelete.canRedo()) {
            undoableDelete.redo();
        } else {
            undoableDelete.doEdit();
        }
    }


    public void doEdit() {
        undoableDelete = new UndoableDeleteBookmark(treeModel, bookmarksCopied);
    }

}
