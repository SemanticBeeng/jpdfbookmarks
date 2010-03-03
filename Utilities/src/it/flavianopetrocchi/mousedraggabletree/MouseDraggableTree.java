/*
 * MouseDraggableTree.java
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.UndoableEditListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.undo.UndoableEditSupport;

public class MouseDraggableTree extends JTree {

    boolean isDragging = false;
    private DefaultMutableTreeNode draggingNode;
    protected DefaultMutableTreeNode draggingOverNode;
    private JPopupMenu treePopupMenu;
    private DefaultTreeModel defaultTreeModel;
    protected MoveType moveType = MoveType.MOVE_AS_SIBLING_AFTER;
    private Timer expanderTimer;
    private TreePath draggingOverPath;
    private Cursor dragCursor;
    private Cursor nodropCursor;
    private MutableTreeNode targetParent, sourceParent;
    private int targetPosition, sourcePosition;
    private UndoableEditSupport undoSupport;
    private List<TreeDoubleClickListener> treeDoubleClickListeners =
            new ArrayList<TreeDoubleClickListener>();
    private List<TreeNodeMovedListener> treeNodeMovedListeners =
            new ArrayList<TreeNodeMovedListener>();

    protected enum MoveType {

        MOVE_AS_CHILD,
        MOVE_AS_SIBLING_AFTER,
        MOVE_AS_SIBLING_BEFORE,
    }

//	public MouseDraggableTree(DefaultTreeModel model) {
//		this();
//		setModel(model);
//		defaultTreeModel = model;
//	}
    public MouseDraggableTree() {
        undoSupport = new UndoableEditSupport(this);

        dragCursor = DragSource.DefaultMoveDrop;
        nodropCursor = DragSource.DefaultMoveNoDrop;

        expanderTimer = new Timer(600, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (isExpanded(draggingOverPath)) {
                    //uncomment if you wish to be able to collapse while dragging
                    //collapsePath(draggingOverPath);
                } else {
                    expandPath(draggingOverPath);
                }
            }
        });
        expanderTimer.setRepeats(false);

        TreeMouseListener mouseListener = new TreeMouseListener();
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
        CustomRenderer treeRenderer = new CustomRenderer();
        setCellRenderer(treeRenderer);
        DefaultTreeCellEditor treeEditor = new DefaultTreeCellEditor(this,
                treeRenderer);
        setCellEditor(treeEditor);
        setEditable(false);
        setInvokesStopCellEditing(true);

    }

    public boolean isDragging() {
        return isDragging;
    }

    @Override
    public void setModel(TreeModel newModel) {
        super.setModel(newModel);
        if (newModel instanceof DefaultTreeModel) {
            defaultTreeModel = (DefaultTreeModel) newModel;
        }
    }

    public JPopupMenu getTreePopupMenu() {
        return treePopupMenu;
    }

    public void setTreePopupMenu(JPopupMenu treePopupMenu) {
        this.treePopupMenu = treePopupMenu;
    }

    public void addUndoableEditListener(UndoableEditListener l) {
        undoSupport.addUndoableEditListener(l);
    }

    public void removeUndoableEditListener(UndoableEditListener l) {
        undoSupport.removeUndoableEditListener(l);
    }

    public void addTreeDoubleClickListener(TreeDoubleClickListener listener) {
        treeDoubleClickListeners.add(listener);
    }

    public void removeTreeDoubleClickListener(TreeDoubleClickListener listener) {
        treeDoubleClickListeners.remove(listener);
    }

    public void addTreeNodeMovedListener(TreeNodeMovedListener listener) {
        treeNodeMovedListeners.add(listener);
    }

    public void removeTreeNodeMovedListener(TreeNodeMovedListener listener) {
        treeNodeMovedListeners.remove(listener);
    }

    public void visitAllNodes(Visitor visitor) {
        visitAllNodes((TreeNode) getModel().getRoot(), visitor);
    }

    public void visitAllNodes(TreeNode node, Visitor visitor) {
        // node is visited exactly once
        visitor.process(node);

        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                visitAllNodes(n, visitor);
            }
        }
    }

    private void moveNode() {
        if (draggingNode == null || draggingOverNode == null) {
            return;
        }

        if (draggingNode.equals(draggingOverNode)) {
            return;
        }

        if (isChildNode(draggingNode, draggingOverNode)) {
            return;
        }

        sourceParent = (MutableTreeNode) draggingNode.getParent();
        sourcePosition = sourceParent.getIndex(draggingNode);

        //the next two calls are necessary to avoid confusion while dragging
        draggingNode.removeFromParent();
        defaultTreeModel.nodeStructureChanged(sourceParent);

        targetParent = (MutableTreeNode) draggingOverNode.getParent();
        targetPosition = targetParent.getIndex(draggingOverNode);

        UndoableNodeMoved undoableNodeMoved = null;
        switch (moveType) {
            case MOVE_AS_CHILD:
                undoableNodeMoved = new UndoableNodeMoved(defaultTreeModel, draggingNode,
                        sourceParent, sourcePosition, draggingOverNode, 0);
                break;
            case MOVE_AS_SIBLING_AFTER:
                undoableNodeMoved = new UndoableNodeMoved(defaultTreeModel, draggingNode,
                        sourceParent, sourcePosition,
                        targetParent, targetPosition + 1);
                break;
            case MOVE_AS_SIBLING_BEFORE:
                undoableNodeMoved = new UndoableNodeMoved(defaultTreeModel, draggingNode,
                        sourceParent, sourcePosition,
                        targetParent, targetPosition);
                break;
        }
        if (undoableNodeMoved != null) {
            undoableNodeMoved.doEdit();
            undoSupport.postEdit(undoableNodeMoved);
        }
        TreePath path = new TreePath(draggingNode.getPath());
        setSelectionPath(path);
        scrollPathToVisible(path);

        if (SwingUtilities.isEventDispatchThread()) {
            fireTreeNodeMovedEvent(new TreeNodeMovedEvent(this, draggingNode,
                    sourceParent, sourcePosition));
        } else {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    fireTreeNodeMovedEvent(new TreeNodeMovedEvent(this,
                            draggingNode, sourceParent, sourcePosition));
                }
            });
        }


    }

    public void updateTree() {
        if (SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.updateComponentTreeUI(this);
        } else {
            Runnable callUpdateTreeAndTable = new Runnable() {

                public void run() {
                    updateTree();
                }
            };
            SwingUtilities.invokeLater(callUpdateTreeAndTable);
        }
    }

    private static boolean isChildNode(TreeNode parent, TreeNode node) {
        if (parent == null || node == null) {
            return false;
        }
        if (parent.equals(node)) {
            return true;
        }
        for (int k = 0; k < parent.getChildCount(); k++) {
            TreeNode child = parent.getChildAt(k);
            if (isChildNode(child, node)) {
                return true;
            }
        }
        return false;
    }

    private void fireTreeDoubleClickEvent(TreeDoubleClickEvent e) {
        for (int i = treeDoubleClickListeners.size() - 1; i >= 0; i--) {
            treeDoubleClickListeners.get(i).treeDoubleClick(e);
        }
    }

    private void fireTreeNodeMovedEvent(TreeNodeMovedEvent e) {
        for (int i = treeNodeMovedListeners.size() - 1; i >= 0; i--) {
            treeNodeMovedListeners.get(i).treeNodeMoved(e);
        }
    }

    private class TreeMouseListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            if (checkAndShowPopup(e)) {
                return;
            }

            TreePath path = getPathForLocation(e.getX(), e.getY());
            if (path == null) {
                draggingNode = null;
            } else {
                draggingNode =
                        (DefaultMutableTreeNode) path.getLastPathComponent();
            }
            draggingOverNode = null;
            repaint();

            if (e.getClickCount() == 2) {
                fireTreeDoubleClickEvent(new TreeDoubleClickEvent(
                        MouseDraggableTree.this, e, draggingNode));
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {

            if (checkAndShowPopup(e)) {
                return;
            }

            expanderTimer.stop();

            if (draggingNode == null) {
                return;
            }
            setCursor(Cursor.getDefaultCursor());

            moveNode();

            isDragging = false;
            draggingNode = null;
            draggingOverNode = null;
            repaint();
        }

        private boolean checkAndShowPopup(MouseEvent e) {
            if (e.isPopupTrigger() && treePopupMenu != null) {
                treePopupMenu.show(e.getComponent(), e.getX(), e.getY());
                return true;
            }
            return false;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
            scrollRectToVisible(r);

            if (draggingNode == null) {
                return;
            }
            if (!isDragging) {	// Update cursor only on move, not on click
                isDragging = true;
                setCursor(dragCursor);
            }

            if (dragNodeOverTree(e.getX(), e.getY())) {
                setCursor(dragCursor);

            } else {
                setCursor(nodropCursor);
            }
        }
    }

    private boolean dragNodeOverTree(int x, int y) {
        draggingOverPath = getPathForLocation(x, y);
        if (draggingOverPath == null) {
            draggingOverNode = null;
            repaint();
            return false;
        } else {
            Rectangle nodeRect = getPathBounds(draggingOverPath);
            draggingOverNode = (DefaultMutableTreeNode) draggingOverPath.getLastPathComponent();
            if (!draggingOverNode.isLeaf()) {
                expanderTimer.restart();
            }
            JLabel lbl = (JLabel) getCellRenderer().getTreeCellRendererComponent(
                    this, // tree
                    draggingOverNode, // value
                    false, // isSelected	(dont want a colored background)
                    isExpanded(draggingOverPath), // isExpanded
                    getModel().isLeaf(draggingOverNode), // isLeaf
                    0, // row			(not important for rendering)
                    false // hasFocus		(dont want a focus rectangle)
                    );
            javax.swing.Icon icon = lbl.getIcon();

            if (x < (nodeRect.x + icon.getIconWidth())) {
                if (y < (nodeRect.y + nodeRect.height / 2)) {
                    moveType = MoveType.MOVE_AS_SIBLING_BEFORE;
                } else {
                    moveType = MoveType.MOVE_AS_SIBLING_AFTER;
                }
            } else {
                moveType = MoveType.MOVE_AS_CHILD;
            }
            repaint();
            return true;
        }
    }

    private class CustomRenderer extends DefaultTreeCellRenderer {

        Color draggingBackground = Color.lightGray;
        Color draggingForeground = Color.white;
        Color stdBackground = getBackgroundNonSelectionColor();
        Color stdNonSelectionForeground = getTextNonSelectionColor();
        Color stdSelectionForeground = getTextSelectionColor();
        DefaultMutableTreeNode previous;
        ImageIcon borderIcon;

        @Override
        public Component getTreeCellRendererComponent(JTree tree,
                Object value, boolean sel, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {

            setBorder(null);
            Color borderColor = stdNonSelectionForeground.brighter();
            for (int i = 0; i < 2; i++) {
                borderColor = borderColor.brighter();
            }

            if (value.equals(draggingOverNode)) {
                switch (moveType) {
                    case MOVE_AS_CHILD:
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                        if (previous == null || (node != previous)) {
                            TreePath path = new TreePath(node.getPath());
                            Rectangle rect = getPathBounds(path);
                            Icon lblIcon = getIcon();
                            int iconWidth = lblIcon.getIconWidth();
                            BufferedImage borderImage = new BufferedImage(
                                    rect.width, 3,
                                    BufferedImage.TYPE_INT_ARGB);
                            Graphics2D g2 = (Graphics2D) borderImage.getGraphics();
                            g2.setColor(borderColor);
                            int childLineStart = iconWidth + getIconTextGap();
                            g2.fillRect(childLineStart, 0,
                                    rect.width - childLineStart, 3);
                            g2.dispose();
                            borderIcon = new ImageIcon(borderImage);
                            previous = node;
                        }

                        setBorder(BorderFactory.createMatteBorder(
                                0, 0, 3, 0, borderIcon));
                        break;
                    case MOVE_AS_SIBLING_BEFORE:
                        setBorder(BorderFactory.createMatteBorder(
                                3, 0, 0, 0, borderColor));
                        break;
                    case MOVE_AS_SIBLING_AFTER:
                        setBorder(BorderFactory.createMatteBorder(
                                0, 0, 3, 0, borderColor));
                        break;
                }

                sel = false;
            } else {

                setBackgroundNonSelectionColor(stdBackground);
                setTextNonSelectionColor(stdNonSelectionForeground);
                setTextSelectionColor(stdSelectionForeground);
            }

            Component res = super.getTreeCellRendererComponent(tree, value,
                    sel, expanded, leaf, row, hasFocus);

            return res;
        }
    }
}
