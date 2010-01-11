/*
 * UndoableDeleteBookmark.java
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;


public class UndoableDeleteBookmark extends AbstractUndoableEdit {
	private DefaultTreeModel treeModel;
	private HashMap<Bookmark, ArrayList<Bookmark>> parentsMap = new HashMap();

	public UndoableDeleteBookmark(DefaultTreeModel model, 
			ArrayList<Bookmark> bookmarks) {
		this.treeModel = model;
		for (Bookmark bookmark : bookmarks) {
			Bookmark parent = (Bookmark) bookmark.getParent();
			int i = parent.getIndex(bookmark);
			ArrayList<Bookmark> list = null;
			if (parentsMap.containsKey(parent)) {
				list = parentsMap.get(parent);
			} else {
				list = new ArrayList<Bookmark>();
				parentsMap.put(parent, list);
			}
			if (i >= list.size()) {
				for (int j = list.size(); j < i; j++) {
					list.add(j, null);
				}
				list.add(i, bookmark);
			} else {
				list.set(i, bookmark);
			}
		}
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		for (Entry<Bookmark, ArrayList<Bookmark>> e : parentsMap.entrySet()) {
			Bookmark parent = e.getKey();
			ArrayList<Bookmark> list = e.getValue();
			for (int i = 0; i < list.size(); i++) {
				Bookmark bookmark = list.get(i);
				if (bookmark != null) {
					parent.insert(bookmark, i);
				}
			}
		}
		treeModel.nodeStructureChanged((TreeNode) treeModel.getRoot());
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		doEdit();
	}

	public void doEdit() {
		for (Entry<Bookmark, ArrayList<Bookmark>> e : parentsMap.entrySet()) {
			Bookmark parent = e.getKey();
			ArrayList<Bookmark> list = e.getValue();
			for (Bookmark bookmark : list) {
				if (bookmark != null) {
					bookmark.removeFromParent();
				}
			}
		}
		treeModel.nodeStructureChanged((TreeNode) treeModel.getRoot());
	}

}
