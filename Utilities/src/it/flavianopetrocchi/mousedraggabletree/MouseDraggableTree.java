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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import javax.swing.UIManager;
import javax.swing.event.UndoableEditListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
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
    private TreeMouseListener mouseListener;
    private MouseListener[] listeners;

    public MouseDraggableTree() {
        //take full control of mouse interaction with the tree
        removeUIMouseListeners();

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

        mouseListener = new TreeMouseListener();
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
        CustomRenderer treeRenderer = new CustomRenderer();
        setCellRenderer(treeRenderer);
//        DefaultCellEditor treeEditor = new DefaultCellEditor();
        setEditable(false);
        setInvokesStopCellEditing(true);
//        JTree tree = new JTree();
//        setDragEnabled(true);
    }

    private void removeUIMouseListeners() {
        listeners = getMouseListeners();

        for (MouseListener listener : listeners) {
            removeMouseListener(listener);
        }
    }

    public Icon getExpanedIcon() {
        return (Icon) UIManager.get("Tree.expandedIcon");
    }

    public Icon getCollapsedIcon() {
        return (Icon) UIManager.get("Tree.collapsedIcon");
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

    private boolean isValidDropLocation() {

        boolean valid = draggingNode != null
                && draggingOverNode != null
                && !draggingNode.equals(draggingOverNode)
                && !isChildNode(draggingNode, draggingOverNode);

        TreePath[] paths = getSelectionPaths();
        if (paths != null) {
            for (TreePath path : paths) {
                valid = valid && !isChildNode((TreeNode) path.getLastPathComponent(), draggingOverNode);
            }
        }

        return valid;
    }

    private boolean moveNode() {
        if (!isValidDropLocation()) {
            return false;
        }

//        sourceParent = (MutableTreeNode) draggingNode.getParent();
//        sourcePosition = sourceParent.getIndex(draggingNode);

        //the next two calls are necessary to avoid confusion while dragging
//        draggingNode.removeFromParent();
//        defaultTreeModel.nodeStructureChanged(sourceParent);

        if (moveType == MoveType.MOVE_AS_CHILD) {
            targetParent = draggingOverNode;
        } else {
            targetParent = (MutableTreeNode) draggingOverNode.getParent();
        }
//        targetPosition = targetParent.getIndex(draggingOverNode);

        UndoableMoveNodes undoableMoveNodes = new UndoableMoveNodes(this,
                targetParent, draggingOverNode, moveType);
        undoableMoveNodes.doEdit();
        undoSupport.postEdit(undoableMoveNodes);

//        UndoableNodeMoved undoableNodeMoved = null;
//        switch (moveType) {
//            case MOVE_AS_CHILD:
//                undoableNodeMoved = new UndoableNodeMoved(defaultTreeModel, draggingNode,
//                        sourceParent, sourcePosition, draggingOverNode, 0);
//                break;
//            case MOVE_AS_SIBLING_AFTER:
//                undoableNodeMoved = new UndoableNodeMoved(defaultTreeModel, draggingNode,
//                        sourceParent, sourcePosition,
//                        targetParent, targetPosition + 1);
//                break;
//            case MOVE_AS_SIBLING_BEFORE:
//                undoableNodeMoved = new UndoableNodeMoved(defaultTreeModel, draggingNode,
//                        sourceParent, sourcePosition,
//                        targetParent, targetPosition);
//                break;
//        }
//        if (undoableNodeMoved != null) {
//            undoableNodeMoved.doEdit();
//            undoSupport.postEdit(undoableNodeMoved);
//        }
//        TreePath path = new TreePath(draggingNode.getPath());
//        setSelectionPath(path);
//        scrollPathToVisible(path);

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

        return true;
    }

    public void updateTree(final MouseListener... mouseListenersToRestore) {
        if (SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.updateComponentTreeUI(this);
            removeUIMouseListeners();
            addMouseListener(mouseListener);
            if (mouseListenersToRestore != null) {
                for (MouseListener l : mouseListenersToRestore) {
                    addMouseListener(l);
                }
            }
        } else {
            Runnable callUpdateTreeAndTable = new Runnable() {

                public void run() {
                    updateTree(mouseListenersToRestore);
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

        private Timer startEditingTimer;
        private TreePath mousePressedPath;

        public TreeMouseListener() {

            startEditingTimer = new Timer(600, new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (mousePressedPath != null && !isDragging) {
                        startEditingAtPath(mousePressedPath);
                    }
                }
            });
            startEditingTimer.setRepeats(false);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            requestFocus();

            if (isEditing()) {
                stopEditing();
            }

            if (checkAndShowPopup(e)) {
                return;
            }

            int x = e.getX();
            int y = e.getY();
            mousePressedPath = getPathForLocation(x, y);
            if (mousePressedPath != null) {
                startEditingTimer.restart();
            }
            if (mousePressedPath == null) {
                draggingNode = null;
            } else {
                draggingNode =
                        (DefaultMutableTreeNode) mousePressedPath.getLastPathComponent();
            }
            draggingOverNode = null;

            repaint();

            if (e.getClickCount() == 2) {
                fireTreeDoubleClickEvent(new TreeDoubleClickEvent(
                        MouseDraggableTree.this, e, draggingNode));
            }

            if (mousePressedPath == null) {
                checkExpandIconClick(x, y);
            }
        }

        private void checkExpandIconClick(int x, int y) {
            TreePath path = findNodeOnTheRight(x, y);
            if (path != null && !getModel().isLeaf(path.getLastPathComponent())) {
                if (isExpanded(path)) {
                    collapsePath(path);
                } else {
                    expandPath(path);
                }
            }
        }

        private TreePath findNodeOnTheRight(int x, int y) {
            int step = 4;
            int max_x = getWidth();

            TreePath path = null;
            for (; x < max_x; x += step) {
                path = getPathForLocation(x, y);
                if (path != null) {
                    break;
                }
            }
            return path;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            startEditingTimer.stop();

            if (checkAndShowPopup(e)) {
                return;
            }

            expanderTimer.stop();

            boolean moveDone = false;
            if (draggingNode != null) {
                setCursor(Cursor.getDefaultCursor());

                moveDone = moveNode();

                isDragging = false;
                draggingNode = null;
                draggingOverNode = null;
                repaint();
            }


            if (!moveDone) {
                if (isEditing()) {
//                    stopEditing();
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    int x = e.getX();
                    int y = e.getY();
                    TreePath path = getPathForLocation(x, y);
                    if (path != null && !isEditing()) {
                        if (e.isControlDown()) {
                            if (isPathSelected(path)) {
                                removeSelectionPath(path);
                            } else {
                                addSelectionPath(path);
                            }
                        } else {
                            setSelectionPath(path);
                        }
                    }
                }
            }
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

            if (draggingNode == null || isEditing()) {
                return;
            }

            //this permits to start dragging even if the bookmark is not already selected
            TreePath draggingPath = new TreePath(draggingNode.getPath());
            if (!isPathSelected(draggingPath)) {
                setSelectionPath(draggingPath);
            }

            if (!isDragging) {	// Update cursor only on move, not on click
                isDragging = true;
                setCursor(dragCursor);
            }

            if (isDragNodeOverTree(e.getX(), e.getY()) && isValidDropLocation()) {
                setCursor(dragCursor);
            } else {
                setCursor(nodropCursor);
            }
        }
    }

    private boolean isDragNodeOverTree(int x, int y) {
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

    private TreeNode getSelectedNode() {
        TreePath path = getSelectionPath();
        if (path == null) {
            return null;
        }

        TreeNode treeNode = null;
        try {
            treeNode = (TreeNode) path.getLastPathComponent();
        } catch (ClassCastException e) {
        }
        return treeNode;
    }

    public class CustomRenderer extends JLabel implements TreeCellRenderer {

        //Color stdNonSelectionForeground = getTextNonSelectionColor();
        private DefaultMutableTreeNode previous;
        private ImageIcon borderIcon;
        private boolean selected;
        private boolean focused;
        private Color backgroundSelectionColor;
        private Color textSelectionColor;
        private Color borderSelectionColor;

        @Override
        public Component getTreeCellRendererComponent(JTree tree,
                Object value, boolean sel, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {

            DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();
            backgroundSelectionColor = defaultRenderer.getBackgroundSelectionColor();
            textSelectionColor = defaultRenderer.getTextSelectionColor();
            borderSelectionColor = defaultRenderer.getBorderSelectionColor();

            setForeground(defaultRenderer.getTextNonSelectionColor());
            // setText(value.toString());
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            setText(node.toString());

            TreePath path = new TreePath(node.getPath());
            if (node.isLeaf()) {
                setIcon(defaultRenderer.getLeafIcon());
            } else if (tree.isCollapsed(path)) {
                setIcon(defaultRenderer.getClosedIcon());
            } else if (tree.isExpanded(path)) {
                setIcon(defaultRenderer.getOpenIcon());
            }

            if (sel) {
//                setOpaque(true);
//                setBackground(defaultRenderer.getBackgroundSelectionColor());
                setForeground(textSelectionColor);
            }
//            else {
//                setOpaque(false);
//                setBackground(defaultRenderer.getBackgroundNonSelectionColor());
//                setForeground(defaultRenderer.getTextNonSelectionColor());
//            }

            setBorder(null);
            Color borderColor = defaultRenderer.getTextNonSelectionColor().brighter();
            for (int i = 0; i < 2; i++) {
                borderColor = borderColor.brighter();
            }

            if (value.equals(draggingOverNode)) {
                switch (moveType) {
                    case MOVE_AS_CHILD:
                        if (previous == null || (node != previous)) {
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

            }

            selected = sel;
            focused = hasFocus;

            return this;

        }

//        @Override
//        public void paint(Graphics g) {
//            if (selected) {
//                g.setColor(backgroundSelectionColor);
//                Icon icon = getIcon();
//                int iconWidth = icon.getIconWidth();
//                int gap = getIconTextGap();
//                g.fillRect(iconWidth + gap - gap / 2, 0, getWidth(), getHeight());
//            }
//
//            super.paint(g);
//        }
        @Override
        protected void paintComponent(Graphics g) {

            String currentLAF = UIManager.getLookAndFeel().getName();
            if (currentLAF.equals("Nimbus")) {
                super.paintComponent(g);
                return;
            }

            Icon icon = getIcon();
            int iconWidth = icon.getIconWidth();
            int gap = getIconTextGap();
            int labelGap = iconWidth + gap - gap / 2;
            Rectangle rect = new Rectangle(labelGap, 0, getWidth() - labelGap, getHeight());
            Graphics2D g2 = (Graphics2D) g;
            if (selected) {
                g2.setColor(backgroundSelectionColor);
                g2.fill(rect);
            }

            if (focused) {
                g2.setColor(borderSelectionColor);
                rect.width--;
                rect.height--;

                boolean drawDashed = UIManager.getBoolean("Tree.drawDashedFocusIndicator");
                if (drawDashed) {
                    float dash1[] = {1.0f};
                    BasicStroke dashed = new BasicStroke(1.0f,
                                          BasicStroke.CAP_BUTT,
                                          BasicStroke.JOIN_MITER,
                                          1.0f, dash1, 0.0f);
                    g2.setStroke(dashed);
                }

                g2.draw(rect);
            }

            super.paintComponent(g);
        }
    }
//    public class CustomRenderer extends DefaultTreeCellRenderer {
//
//        Color stdNonSelectionForeground = getTextNonSelectionColor();
//        DefaultMutableTreeNode previous;
//        ImageIcon borderIcon;
//
//        @Override
//        public Component getTreeCellRendererComponent(JTree tree,
//                Object value, boolean sel, boolean expanded,
//                boolean leaf, int row, boolean hasFocus) {
//
//            setBorder(null);
//            Color borderColor = stdNonSelectionForeground.brighter();
//            for (int i = 0; i < 2; i++) {
//                borderColor = borderColor.brighter();
//            }
//
//            if (value.equals(draggingOverNode)) {
//                switch (moveType) {
//                    case MOVE_AS_CHILD:
//                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
//                        if (previous == null || (node != previous)) {
//                            TreePath path = new TreePath(node.getPath());
//                            Rectangle rect = getPathBounds(path);
//                            Icon lblIcon = getIcon();
//                            int iconWidth = lblIcon.getIconWidth();
//                            BufferedImage borderImage = new BufferedImage(
//                                    rect.width, 3,
//                                    BufferedImage.TYPE_INT_ARGB);
//                            Graphics2D g2 = (Graphics2D) borderImage.getGraphics();
//                            g2.setColor(borderColor);
//                            int childLineStart = iconWidth + getIconTextGap();
//                            g2.fillRect(childLineStart, 0,
//                                    rect.width - childLineStart, 3);
//                            g2.dispose();
//                            borderIcon = new ImageIcon(borderImage);
//                            previous = node;
//                        }
//
//                        setBorder(BorderFactory.createMatteBorder(
//                                0, 0, 3, 0, borderIcon));
//                        break;
//                    case MOVE_AS_SIBLING_BEFORE:
//                        setBorder(BorderFactory.createMatteBorder(
//                                3, 0, 0, 0, borderColor));
//                        break;
//                    case MOVE_AS_SIBLING_AFTER:
//                        setBorder(BorderFactory.createMatteBorder(
//                                0, 0, 3, 0, borderColor));
//                        break;
//                }
//
//                //sel = false;
//            }
//
//            Component res = super.getTreeCellRendererComponent(tree, value,
//                    sel, expanded, leaf, row, hasFocus);
//
//            return res;
////            return this;
//
//        }
//    }
}
