/*
 * UndoableSetDestinations.java
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


public class UndoableSetDestination extends AbstractUndoableEdit {
	private DefaultTreeModel treeModel;
	private Bookmark old, dest;
	private Bookmark backup = new Bookmark();

	public UndoableSetDestination(DefaultTreeModel model, Bookmark old,
			Bookmark dest) {
		this.treeModel = model;
		this.backup.cloneDestination(old);
		this.old = old;
		this.dest = dest;
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		old.cloneDestination(backup);
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		doEdit();
	}

	public void doEdit() {
		old.cloneDestination(dest);
	}

}
