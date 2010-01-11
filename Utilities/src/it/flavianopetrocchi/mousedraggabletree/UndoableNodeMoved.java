/*
 * UndoableNodeMoved.java
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

package it.flavianopetrocchi.mousedraggabletree;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

public class UndoableNodeMoved extends AbstractUndoableEdit {
	private DefaultTreeModel defaultModel;
	private MutableTreeNode movedNode;
	private MutableTreeNode oldParent;
	private int oldParentIndex;
	private MutableTreeNode newParent;
	private int newParentIndex;
	private String presentation = "";

	public UndoableNodeMoved(DefaultTreeModel treeModel, MutableTreeNode moved,
			MutableTreeNode oldParent, int oldParentIndex,
			MutableTreeNode newParent, int newParentIndex) {
		this.movedNode = moved;
		this.oldParent = oldParent;
		this.oldParentIndex = oldParentIndex;
		this.newParent = newParent;
		this.newParentIndex = newParentIndex;
		this.defaultModel = treeModel;
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		oldParent.insert(movedNode, oldParentIndex);
		defaultModel.nodeStructureChanged(oldParent);
		defaultModel.nodeStructureChanged(newParent);
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		doEdit();
	}

	public void doEdit() {
		newParent.insert(movedNode, newParentIndex);
		defaultModel.nodeStructureChanged(newParent);
		defaultModel.nodeStructureChanged(oldParent);
	}

	@Override
	public String getPresentationName() {
		return presentation;
	}

	public void setPresentationName(String presentation) {
		this.presentation = presentation;
	}
}
