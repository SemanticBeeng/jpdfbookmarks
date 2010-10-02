/*
 * UndoableMoveNodes.java
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

public class UndoableMoveNodes extends AbstractUndoableEdit {

    private JTree tree;
    private DefaultTreeModel treeModel;
    private MutableTreeNode newParent;
    private MutableTreeNode sibling;
    private MoveType type;
    private ArrayList<DefaultMutableTreeNode> nodesMoved;
    private ArrayList<NodePositionInfo> nodesOldPositions;

    public UndoableMoveNodes(JTree tree,
            MutableTreeNode newParent, MutableTreeNode sibling, MoveType type) {
        this.newParent = newParent;
        this.sibling = sibling;
        this.tree = tree;
        treeModel = (DefaultTreeModel) tree.getModel();
        this.type = type;
        nodesMoved = getSelectedNodes();
        nodesOldPositions = new ArrayList<NodePositionInfo>(nodesMoved.size());
        for (DefaultMutableTreeNode node : nodesMoved) {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
            nodesOldPositions.add(new NodePositionInfo(parent, parent.getIndex(node)));
        }
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
//		oldParent.insert(movedNode, oldParentIndex);
//		defaultModel.nodeStructureChanged(oldParent);
//		defaultModel.nodeStructureChanged(newParent);

        removeFromParents();

        for (int i = 0; i < nodesMoved.size(); i++) {
            DefaultMutableTreeNode node = nodesMoved.get(i);
            NodePositionInfo position = nodesOldPositions.get(i);
            position.parent.insert(node, position.index);
        }

        treeModel.nodeStructureChanged((TreeNode) treeModel.getRoot());

        setSelected();
    }

    private void removeFromParents() {

        for (DefaultMutableTreeNode node : nodesMoved) {
            node.removeFromParent();
        }

        treeModel.nodeStructureChanged((TreeNode) treeModel.getRoot());
    }

    private void setSelected() {

        TreePath[] paths = new TreePath[nodesMoved.size()];
        int i = 0;
        for (DefaultMutableTreeNode node : nodesMoved) {
            TreePath path = new TreePath(node.getPath());
            paths[i] = path;
            i++;
            //tree.addSelectionPath(path);
            //tree.scrollPathToVisible(path);
        }
        tree.setSelectionPaths(paths);
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        doEdit();
    }

    public void doEdit() {
//		newParent.insert(movedNode, newParentIndex);
//		defaultModel.nodeStructureChanged(newParent);
//		defaultModel.nodeStructureChanged(oldParent);
        removeFromParents();

        int index = newParent.getIndex(sibling);
        switch (type) {
            case MOVE_AS_CHILD:
                index = 0;
                break;
            case MOVE_AS_SIBLING_AFTER:
                index++;
                break;
            case MOVE_AS_SIBLING_BEFORE:
                break;
        }

        for (DefaultMutableTreeNode node : nodesMoved) {
            newParent.insert(node, index);
            index++;
        }
        treeModel.nodeStructureChanged(newParent);

        setSelected();
    }

    //we must sort selected nodes by row and move them in that order
    private ArrayList<DefaultMutableTreeNode> getSelectedNodes() {

        ArrayList<DefaultMutableTreeNode> nodesList = new ArrayList<DefaultMutableTreeNode>();
        TreePath[] paths = tree.getSelectionPaths();
        for (TreePath path : paths) {
            nodesList.add((DefaultMutableTreeNode) path.getLastPathComponent());
        }

        Collections.sort(nodesList, new Comparator<DefaultMutableTreeNode>() {

            public int compare(DefaultMutableTreeNode o1, DefaultMutableTreeNode o2) {
                int i_o1 = tree.getRowForPath(new TreePath(o1.getPath()));
                int i_o2 = tree.getRowForPath(new TreePath(o2.getPath()));
                if (i_o1 < i_o2) {
                    return -1;
                } else if (i_o1 > i_o2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return nodesList;
    }

    private class NodePositionInfo {

        private DefaultMutableTreeNode parent;
        private int index;

        public NodePositionInfo(DefaultMutableTreeNode parent, int index) {
            this.parent = parent;
            this.index = index;
        }
    }
}
