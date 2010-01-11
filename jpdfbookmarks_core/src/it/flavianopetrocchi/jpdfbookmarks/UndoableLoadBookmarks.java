/*
 * UndoableLoadBookmarks.java
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
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;


public class UndoableLoadBookmarks extends AbstractUndoableEdit {
	private DefaultTreeModel treeModel;
	private BookmarksTree tree;
	private Bookmark oldRoot, newRoot;

	public UndoableLoadBookmarks(DefaultTreeModel model, BookmarksTree tree, Bookmark newRoot) {
		this.treeModel = model;
		this.tree = tree;
		this.oldRoot = (Bookmark) treeModel.getRoot();
		this.newRoot = newRoot;
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		if (oldRoot != null) {
			treeModel.setRoot(oldRoot);
			tree.setRootVisible(false);
			tree.setEditable(true);
			tree.treeDidChange();
		}
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		doEdit();
	}

	public void doEdit() {
		if (newRoot != null) {
			treeModel.setRoot(newRoot);
			tree.setRootVisible(false);
			tree.setEditable(true);
			tree.treeDidChange();
		}
	}

}
