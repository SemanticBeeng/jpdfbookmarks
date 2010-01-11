/*
 * TreeNodeMovedEvent.java
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

import java.util.EventObject;
import javax.swing.tree.TreeNode;

public class TreeNodeMovedEvent extends EventObject {
	private TreeNode movedNode;
	private TreeNode oldParent;
	private int oldParentIndex;

	public TreeNodeMovedEvent(Object source, TreeNode moved, TreeNode oldParent,
			int oldParentIndex) {
		super(source);
		this.movedNode = moved;
		this.oldParent = oldParent;
		this.oldParentIndex = oldParentIndex;
	}

	public TreeNode getMovedNode() {
		return movedNode;
	}

	public TreeNode getOldParent() {
		return oldParent;
	}

	public int getOldParentIndex() {
		return oldParentIndex;
	}
}
