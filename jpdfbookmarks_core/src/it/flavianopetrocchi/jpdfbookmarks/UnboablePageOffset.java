/*
 * UndoablePageOffset.java
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

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;


public class UnboablePageOffset extends AbstractUndoableEdit {
	private DefaultTreeModel treeModel;
	TreePath[] paths;
	private int offset;

	public UnboablePageOffset(DefaultTreeModel model, 
			TreePath[] selectedPaths, int offset) {
		this.treeModel = model;
		paths = selectedPaths;
		this.offset = offset;
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		for (TreePath path : paths) {
			Bookmark bookmark = (Bookmark) path.getLastPathComponent();
			bookmark.setPageNumber(bookmark.getPageNumber() - offset);
		}
		treeModel.nodeStructureChanged((TreeNode) treeModel.getRoot());
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		doEdit();
	}

	public void doEdit() {
		for (TreePath path : paths) {
			Bookmark bookmark = (Bookmark) path.getLastPathComponent();
			bookmark.setPageNumber(offset + bookmark.getPageNumber());
		}
		treeModel.nodeStructureChanged((TreeNode) treeModel.getRoot());
	}
}
