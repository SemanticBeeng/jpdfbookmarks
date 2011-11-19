/*
 * JPdfBookmarksGui.java
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

// <editor-fold defaultstate="collapsed" desc="import">
import it.flavianopetrocchi.jpdfbookmarks.bookmark.Bookmark;
import it.flavianopetrocchi.jpdfbookmarks.bookmark.IBookmarksConverter;
import it.flavianopetrocchi.jpdfbookmarks.bookmark.BookmarkType;
import it.flavianopetrocchi.colors.ColorsListPanel;
import it.flavianopetrocchi.components.collapsingpanel.CollapsingPanel;
import it.flavianopetrocchi.jpdfbookmarks.bookmark.BookmarkSelection;
import it.flavianopetrocchi.labelvertical.VerticalLabel;
import it.flavianopetrocchi.labelvertical.VerticalLabelUI;
import it.flavianopetrocchi.linklabel.LinkLabel;
import it.flavianopetrocchi.mousedraggabletree.MouseDraggableTree;
import it.flavianopetrocchi.mousedraggabletree.TreeNodeMovedEvent;
import it.flavianopetrocchi.mousedraggabletree.TreeNodeMovedListener;
import it.flavianopetrocchi.utilities.FileOperationEvent;
import it.flavianopetrocchi.utilities.FileOperationListener;
import it.flavianopetrocchi.utilities.IntegerTextField;
import it.flavianopetrocchi.utilities.SimpleFileFilter;
import it.flavianopetrocchi.utilities.Ut;
import it.flavianopetrocchi.mousedraggabletree.UndoableNodeMoved;
import it.flavianopetrocchi.mousedraggabletree.Visitor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.CellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;// </editor-fold>

class JPdfBookmarksGui extends JFrame implements FileOperationListener,
        PageChangedListener, ViewChangedListener, TreeExpansionListener,
        UndoableEditListener, TreeSelectionListener, CellEditorListener,
        RenderingStartListener, TextCopiedListener, TreeNodeMovedListener {

    // <editor-fold defaultstate="collapsed" desc="Members">
    private static Clipboard localClipboard;
//    private Clipboard clipboard;
    private DropTarget dropTarget;
    private final static DataFlavor bookmarksFlavor;
    private final int ZOOM_STEP = 10;
    private int windowState;
    private JSplitPane centralSplit;
    private String title = "JPdfBookmarks";
    private Prefs userPrefs = new Prefs();
    private int numPages = 0;
    private JScrollPane bookmarksScroller;
    private BookmarksTree bookmarksTree;
    private DefaultTreeModel bookmarksTreeModel;
    private JTabbedPane leftTabbedPane;
    private UnifiedFileOperator fileOperator;
    private IPdfView viewPanel;
    private JToolBar navigationToolbar;
    private ButtonGroup zoomMenuItemsGroup;
//    private JMenuItem cutMenuItem;
//    private JMenuItem copyMenuItem;
//    private JMenuItem pasteMenuItem;
    private JRadioButtonMenuItem rbFitWidth;
    private JRadioButtonMenuItem rbFitHeight;
    private JRadioButtonMenuItem rbFitPage;
    private JRadioButtonMenuItem rbFitNative;
    private JRadioButtonMenuItem rbTopLeftZoom;
    private JRadioButtonMenuItem rbFitRect;
    private JRadioButtonMenuItem bookmarksButton;
    private JRadioButtonMenuItem thumbnailsButton;
    private JCheckBoxMenuItem cbBold;
    private JCheckBoxMenuItem cbItalic;
    private JCheckBoxMenuItem cbEditMenuBold;
    private JCheckBoxMenuItem cbEditMenuItalic;
    private JCheckBoxMenuItem cbShowOnOpen;
    private JCheckBoxMenuItem cbSelectText;
    private JCheckBoxMenuItem cbConnectToClipboard;
    private ButtonGroup zoomButtonsGroup;
    private JToggleButton tbShowOnOpen;
    private JToggleButton tbFitWidth;
    private JToggleButton tbFitHeight;
    private JToggleButton tbFitPage;
    private JToggleButton tbFitNative;
    private JToggleButton tbTopLeftZoom;
    private JToggleButton tbFitRect;
    private JToggleButton tbBold;
    private JToggleButton tbItalic;
    private JLabel lblPageOfPages;
    private JLabel lblMouseOverNode;
    private JLabel lblSelectedNode;
    private JLabel lblCurrentView;
    private JLabel lblPercent;
    private JLabel lblStatus;
    private IntegerTextField txtGoToPage;
    private IntegerTextField txtZoom;
    private ExtendedUndoManager undoManager;
    private UndoableEditSupport undoSupport;
    private JPopupMenu treeMenu;
    private JPopupMenu toolbarsPanelsMenu;
    private JColorChooser colorChooser;
    private JProgressBar progressBar;
    private Box busyPanel;
    private JCheckBox checkInheritTop;
    private JCheckBox checkInheritLeft;
    private JCheckBox checkInheritZoom;
    private VerticalLabel lblInheritLeft;
    private JMenu openRecent;
    private JToggleButton tbSelectText;
    private JToggleButton tbConnectToClipboard;
    private JPanel bookmarksPanel;
    private JPanel bookmarksToolbarsPanel = new JPanel(/*new WrapFlowLayout(WrapFlowLayout.LEFT)*/);
    private HashMap<String, JToolBar> mainToolbars = new HashMap<String, JToolBar>();
    private HashMap<String, JToolBar> bookmarksToolbars = new HashMap<String, JToolBar>();
    private JPanel mainToolbarsPanel = new JPanel(new WrapFlowLayout(WrapFlowLayout.LEFT));
    private MouseAdapter mouseAdapter;
    private ToolbarsPopupListener toolbarsPopupListener = new ToolbarsPopupListener();
    private LeftPanel leftPanel;
    private ButtonGroup leftPanelMenuGroup;// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Actions">
    private Action quitAction;
    //File actions
    private Action openAction;
    private Action saveAction;
    private Action saveAsAction;
    private Action closeAction;
    private Action dumpAction;
    private Action loadAction;
    //Navigation actions
    private Action goNextPageAction;
    private Action goLastPageAction;
    private Action goPreviousPageAction;
    private Action goFirstPageAction;
    private Action goToPageAction;
    //Zoom actions
    private Action fitWidthAction;
    private Action fitContentWidthAction;
    private Action fitHeightAction;
    private Action fitContentHeightAction;
    private Action fitNativeAction;
    private Action fitPageAction;
    private Action fitContentAction;
    private Action topLeftZoomAction;
    private Action fitRectAction;
    private Action zoomInAction;
    private Action zoomOutAction;
    //Bookmarks actions
    private Action undoAction;
    private Action redoAction;
    private Action expandAllAction;
    private Action collapseAllAction;
    private Action addSiblingAction;
    private Action addChildAction;
    private Action setBoldAction;
    private Action setItalicAction;
    private Action deleteAction;
    private Action renameAction;
    private Action changeColorAction;
    private Action setDestFromViewAction;
    private Action showOnOpenAction;
    private Action addWebLinkAction;
    private Action addLaunchLinkAction;
    private Action showActionsDialog;
    private Action applyPageOffset;
    private Action optionsDialogAction;
    private Action checkUpdatesAction;
    private Action readOnlineManualAction;
    private Action donateToProject;
    private Action goToAuthorBlog;
    private Action selectText;
    private Action connectToClipboard;
    private Action cutAction;
    private Action copyAction;
    private Action pasteAction;
    private Action openLinkedPdf;
    private Action extractLinks;
    private Action copyBookmarkFromViewAction;// </editor-fold>

    private void saveWindowState() {
        userPrefs.setWindowState(windowState);
        if (windowState == JFrame.MAXIMIZED_BOTH) {
            userPrefs.setLocation(null);
            userPrefs.setSize(null);
        } else {
            userPrefs.setLocation(getLocation());
            userPrefs.setSize(getSize());
        }
        //userPrefs.setSplitterLocation(centralSplit.getDividerLocation());
        userPrefs.setCollapsingPanelState(leftPanel.getPanelState());
        userPrefs.setSplitterLocation(leftPanel.getDividerLocation());
        userPrefs.setPanelToShow((String) leftPanel.getComboBoxSelector().getSelectedItem());
    }

    private void loadWindowState() {
        Ut.changeLAF(userPrefs.getLAF(), this);
        setSize(userPrefs.getSize());
        setLocation(userPrefs.getLocation());
        setExtendedState(userPrefs.getWindowState());
    }

    static {
        localClipboard = new Clipboard(JPdfBookmarks.APP_NAME);
        bookmarksFlavor = new DataFlavor(BookmarkSelection.class, "BookmarkSelection");
    }

    public JPdfBookmarksGui() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        Authenticator.setDefault(new ProxyAuthenticator(this, true));
        localClipboard.addFlavorListener(new FlavorListener() {

            @Override
            public void flavorsChanged(FlavorEvent e) {
                JPdfBookmarksGui.this.flavorsChanged();
            }
        });

        undoManager = new ExtendedUndoManager();
        undoSupport = new UndoableEditSupport(this);

        setTitle(title);
        setIconImage(Res.getIcon(getClass(), "gfx/jpdfbookmarks.png").getImage());
        loadWindowState();

        fileOperator = new UnifiedFileOperator();
        viewPanel = fileOperator.getViewPanel();
        viewPanel.addTextCopiedListener(this);

        initComponents();

        fileOperator.addFileOperationListener(this);
        viewPanel.addPageChangedListener(this);
        viewPanel.addViewChangedListener(this);
        undoSupport.addUndoableEditListener(undoManager);
        undoSupport.addUndoableEditListener(this);

        //set window close button event to ask for save option
        WindowAdapter wndCloser = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                saveWindowState();
                exitApplication();
            }

            @Override
            public void windowStateChanged(WindowEvent e) {
                windowState = getExtendedState();
            }

            @Override
            public void windowOpened(WindowEvent e) {
                if (userPrefs.getCheckUpdatesOnStart()) {
                    checkUpdates(true);
                }
            }
        };

        addWindowListener(wndCloser);
        addWindowStateListener(wndCloser);
    }

    public final void flavorsChanged() {

        DataFlavor[] flavorsInClipboard = localClipboard.getAvailableDataFlavors();
        for (DataFlavor flavor : flavorsInClipboard) {
            if (flavor.equals(bookmarksFlavor) && fileOperator != null && (fileOperator.getFilePath() != null)) {
                pasteAction.setEnabled(true);
            }
        }
    }

    public boolean askCloseWithoutSave() {
        if (!fileOperator.getFileChanged() || fileOperator.isReadonly()) {
            return true;
        }

        //this is to centre the dialog to the screen when iconified
        Component parent = (getState() != Frame.ICONIFIED) ? this : null;

        int response = JOptionPane.showConfirmDialog(
                parent,
                Res.getString("ASK_SAVE_CHANGES"), title,
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        switch (response) {
            case JOptionPane.YES_OPTION:
                return fileOperator.save((Bookmark) bookmarksTreeModel.getRoot());
            case JOptionPane.NO_OPTION:
                return true;
            case JOptionPane.CANCEL_OPTION:
                return false;
        }

        return true;
    }

    private void exitApplication() {
        if (!askCloseWithoutSave()) {
            return;
        }

        fileOperator.close();

        Frame[] frames = JFrame.getFrames();
        if (frames.length == 1) {
            saveWindowState();
            System.exit(0);
        } else {
            dispose();
        }
    }

    @Override
    public void fileOperation(FileOperationEvent evt) {
        if (evt.getOperation() == FileOperationEvent.Operation.FILE_OPENED
                || evt.getOperation() == FileOperationEvent.Operation.FILE_READONLY) {
            setTitle(title + ": " + evt.getPathToFile());
            userPrefs.setLastDirectory(evt.getPathToFile());
            userPrefs.addRecentFile(evt.getPathToFile());
            createRecentFilesItems();
            lblPageOfPages.setText(String.format(" / %d ",
                    viewPanel.getNumPages()));
            Ut.enableComponents(true, lblPageOfPages, txtGoToPage, txtZoom,
                    lblPercent);
            Ut.enableActions(true, closeAction, fitWidthAction,
                    fitHeightAction, fitPageAction, fitNativeAction,
                    zoomInAction, zoomOutAction, goToPageAction, fitRectAction,
                    expandAllAction, collapseAllAction, topLeftZoomAction,
                    addSiblingAction, showOnOpenAction, dumpAction, loadAction,
                    selectText, connectToClipboard, openLinkedPdf, copyBookmarkFromViewAction);
            if (evt.getOperation() == FileOperationEvent.Operation.FILE_OPENED) {
                Ut.enableActions(true, saveAsAction, extractLinks);
            }
            tbShowOnOpen.setSelected(fileOperator.getShowBookmarksOnOpen());
            cbShowOnOpen.setSelected(fileOperator.getShowBookmarksOnOpen());
            switch (viewPanel.getFitType()) {
                case FitWidth:
                    tbFitWidth.setSelected(true);
                    rbFitWidth.setSelected(true);
                    break;
                case FitHeight:
                    tbFitHeight.setSelected(true);
                    rbFitHeight.setSelected(true);
                    break;
                case FitPage:
                    tbFitPage.setSelected(true);
                    rbFitPage.setSelected(true);
                    break;
                case FitNative:
                    tbFitNative.setSelected(true);
                    rbFitNative.setSelected(true);
                    break;
                case FitRect:
                    tbFitRect.setSelected(true);
                    rbFitRect.setSelected(true);
                    break;
            }
            flavorsChanged();
            if (evt.getOperation() == FileOperationEvent.Operation.FILE_READONLY) {
                JOptionPane.showMessageDialog(this, Res.getString("MSG_READONLY"), JPdfBookmarks.APP_NAME,
                        JOptionPane.WARNING_MESSAGE);
                setTitle(getTitle() + " - " + Res.getString("READONLY"));
            }
        } else if (evt.getOperation() == FileOperationEvent.Operation.FILE_CLOSED) {
            setTitle(title);
            txtGoToPage.setText("0");
            lblPageOfPages.setText(" / 0 ");
            txtZoom.setText("0");
            Ut.enableComponents(false, lblPageOfPages, txtGoToPage, txtZoom,
                    lblPercent);
            Ut.enableActions(false, saveAsAction, closeAction, fitWidthAction,
                    fitHeightAction, fitPageAction, fitNativeAction,
                    zoomInAction, zoomOutAction, goFirstPageAction,
                    goPreviousPageAction, goNextPageAction, goLastPageAction,
                    goToPageAction, expandAllAction, collapseAllAction,
                    topLeftZoomAction, fitRectAction, addSiblingAction,
                    addChildAction, deleteAction, undoAction, redoAction,
                    showOnOpenAction, setBoldAction, setItalicAction,
                    renameAction, setDestFromViewAction, changeColorAction,
                    dumpAction, loadAction, addWebLinkAction, addLaunchLinkAction, saveAction,
                    applyPageOffset, selectText, connectToClipboard, showActionsDialog, openLinkedPdf,
                    copyBookmarkFromViewAction, extractLinks);
            lblMouseOverNode.setText(" ");
            lblSelectedNode.setText(" ");
            lblCurrentView.setText(" ");
            setEmptyBookmarksTree();
//            leftPanel.addThumbnailsPanel(new JPanel());
            updateThumbnailsPanel(null);
            leftPanel.setPanelState(leftPanel.getPanelState());
            undoManager.die();
        } else if (evt.getOperation() == FileOperationEvent.Operation.FILE_CHANGED) {
            if (fileOperator.getFileChanged() && !fileOperator.isReadonly()) {
                setTitle(title + ": " + evt.getPathToFile() + " *");
                Ut.enableActions(true, saveAction);
            }
        } else if (evt.getOperation() == FileOperationEvent.Operation.FILE_SAVED) {
            setTitle(title + ": " + evt.getPathToFile());
            userPrefs.setLastDirectory(evt.getPathToFile());
            userPrefs.addRecentFile(evt.getPathToFile());
            createRecentFilesItems();
            Ut.enableActions(false, saveAction);
        }
    }

    @Override
    public void pageChanged(PageChangedEvent evt) {
        int currentPage = evt.getCurrentPage();
        txtGoToPage.setInteger(currentPage);
        if (evt.hasPrevious()) {
            Ut.enableActions(true, goPreviousPageAction, goFirstPageAction);
        } else {
            Ut.enableActions(false, goPreviousPageAction, goFirstPageAction);
        }

        if (evt.hasNext()) {
            Ut.enableActions(true, goNextPageAction, goLastPageAction);
        } else {
            Ut.enableActions(false, goNextPageAction, goLastPageAction);
        }
    }

    private void enableInheritChecks(boolean top, boolean left, boolean zoom) {
        checkInheritTop.setEnabled(top);
        checkInheritLeft.setEnabled(left);
        lblInheritLeft.setEnabled(left);
        checkInheritZoom.setEnabled(zoom);
    }

    @Override
    public void viewChanged(ViewChangedEvent evt) {
        FitType fitType = evt.getFitType();
        float scale = evt.getScale();
        int zoom = Math.round(scale * 100);
        Bookmark bookmark = evt.getBookmark();
//		lblCurrentView.setText(Res.getString("CURRENT_VIEW") + ": [" +
//				fitType + "  zoom: " + zoom + " %]");
        lblCurrentView.setText(Res.getString("CURRENT_VIEW") + ": " + bookmark.getDescription(userPrefs.getUseThousandths()));
        txtZoom.setInteger(zoom);
        switch (fitType) {
            case FitWidth:
                enableInheritChecks(true, false, false);
                tbFitWidth.setSelected(true);
                rbFitWidth.setSelected(true);
                break;
            case FitHeight:
                enableInheritChecks(false, true, false);
                tbFitHeight.setSelected(true);
                rbFitHeight.setSelected(true);
                break;
            case FitPage:
                enableInheritChecks(false, false, false);
                tbFitPage.setSelected(true);
                rbFitPage.setSelected(true);
                break;
            case FitNative:
                enableInheritChecks(true, true, true);
                tbFitNative.setSelected(true);
                rbFitNative.setSelected(true);
                break;
            case TopLeftZoom:
                enableInheritChecks(true, true, true);
                tbTopLeftZoom.setSelected(true);
                rbTopLeftZoom.setSelected(true);
                break;
            case FitRect:
                enableInheritChecks(false, false, false);
                tbFitRect.setSelected(true);
                rbFitRect.setSelected(true);
                break;
        }
    }

    @Override
    public void treeExpanded(TreeExpansionEvent event) {
        TreePath path = event.getPath();
        Bookmark bookmark = (Bookmark) path.getLastPathComponent();
        if (!bookmark.isOpened()) {
            bookmark.setOpened(true);
            fileOperator.setFileChanged(true);
        }
        recreateNodesOpenedState();
    }

    @Override
    public void treeCollapsed(TreeExpansionEvent event) {
        TreePath path = event.getPath();
        Bookmark bookmark = (Bookmark) path.getLastPathComponent();
        if (bookmark.isOpened()) {
            bookmark.setOpened(false);
            fileOperator.setFileChanged(true);
        }
    }

    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
        UndoableEdit[] undoableEdits = undoManager.getUndoableEdits();
        if (undoableEdits.length > 0) {
            UndoableEdit undo = undoableEdits[0];
            if (undo instanceof UndoableNodeMoved) {
                recreateNodesOpenedState();
            }
        }
        updateUndoRedoPresentation();
        fileOperator.setFileChanged(true);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        Bookmark bookmark = getSelectedBookmark();

        //enable or disable actions applicable to multiple bookmarks
        Ut.enableActions((bookmark != null), setBoldAction, setItalicAction,
                changeColorAction, applyPageOffset, deleteAction, renameAction,
                setDestFromViewAction, cutAction, copyAction, addWebLinkAction,
                addLaunchLinkAction);

        //enable or disable actions applicable to only a single bookmark
        TreePath[] paths = bookmarksTree.getSelectionPaths();
        Ut.enableActions((bookmark != null) && (paths.length == 1),
                addChildAction, showActionsDialog);

        //Ut.enableComponents((bookmark != null), cutMenuItem, copyMenuItem);
        if (bookmark != null) {
            updateStyleButtons(bookmark);
        }
    }

    private Bookmark getSelectedBookmark() {
        TreePath path = bookmarksTree.getSelectionPath();

        if (path == null) {
            return null;
        }

        Bookmark treeNode = null;
        try {
            treeNode = (Bookmark) path.getLastPathComponent();
        } catch (ClassCastException e) {
        }
        return treeNode;
    }

    @Override
    public void editingStopped(ChangeEvent e) {
        Bookmark treeNode = getSelectedBookmark();
        if (treeNode == null) {
            return;
        }
        String oldValue = treeNode.getTitle().trim();
        CellEditor treeEditor = (CellEditor) e.getSource();
        String value = treeEditor.getCellEditorValue().toString().trim();

        UndoableCellEdit undoableCellEdit = new UndoableCellEdit(
                bookmarksTreeModel, treeNode, value);
        undoableCellEdit.doEdit();
        if (oldValue.equals(value)
                || oldValue.equals(Res.getString("DEFAULT_TITLE").trim())) {
        } else {
            undoSupport.postEdit(undoableCellEdit);
        }
    }

    @Override
    public void editingCanceled(ChangeEvent e) {
    }

    @Override
    public void renderingStart(RenderingStartEvent evt) {
        lblStatus.setText("Rendering page " + evt.getPageNumber() + " wait ...");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    @Override
    public void textCopied(TextCopiedEvent evt) {
        String text = evt.getText();
        if (text == null) {
            text = "";
        }

        lblStatus.setText(Res.getString("EXTRACTED") + ": " + text);
    }

    @Override
    public void treeNodeMoved(TreeNodeMovedEvent e) {
        recreateNodesOpenedState();
    }

    private JPanel createThumbnailsPanel() {
        return new JPanel();
    }

    abstract class ActionBuilder extends AbstractAction {

        public ActionBuilder(String resName, String resDescription,
                String accelerator, String resIcon, boolean enabled) {
            super(Res.getString(resName));
            String description = null;
            if (resDescription != null) {
                description = Res.getString(resDescription);
            }
            if (accelerator != null) {
                putValue(Action.ACCELERATOR_KEY,
                        KeyStroke.getKeyStroke(accelerator));
                description += " [" + accelerator.toUpperCase() + "]";
            }
            if (description != null) {
                putValue(Action.SHORT_DESCRIPTION, description);
            }

            if (resIcon != null) {
                putValue(Action.SMALL_ICON, Res.getIcon(getClass(),
                        "gfx16/" + resIcon));
                putValue(Action.LARGE_ICON_KEY, Res.getIcon(getClass(),
                        "gfx22/" + resIcon));
            }
            setEnabled(enabled);
        }
    }

    private void openLinkedPdf() {
        File file = pdfFileChooser();
        if (file != null && file.isFile()) {
            try {
                JPdfBookmarksGui gui = alreadyOpenedIn(file.getCanonicalFile());
                if (gui != null) {
                    gui.requestFocus();
                } else {
                    new JPdfBookmarks().launchNewGuiInstance(file.getCanonicalPath(), null);
                }
            } catch (IOException ex) {
            }
        }
    }

    private File pdfFileChooser() {
        File file = null;
        JFileChooser chooser = new JFileChooser(userPrefs.getLastDirectory());
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Pdf File",
                "pdf");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(filter);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
        }
        return file;
    }

    private void openDialog() {
        if (!askCloseWithoutSave()) {
            return;
        }
        fileOperator.close();

        File file = pdfFileChooser();
        if (file != null && file.isFile()) {
            openFileAsync(file, null);
        }

    }

    private void setProgressBar(String message) {
        lblStatus.setText(message);
        busyPanel.add(progressBar);
        busyPanel.repaint();
    }

    private void removeProgressBar() {
        lblStatus.setText(" ");
        busyPanel.remove(progressBar);
        busyPanel.repaint();
    }

//    public void openFileAsync(final File file, final Bookmark target) {
//        setProgressBar(Res.getString("WAIT_LOADING_FILE"));
//        CursorToolkit.startWaitCursor(tbBold);
//
//        try {
//            fileOperator.open(file);
//            Bookmark root = fileOperator.getRootBookmark();
//            bookmarksTree.setRootVisible(false);
//            bookmarksTree.setEditable(true);
//            if (root != null) {
//                bookmarksTreeModel.setRoot(root);
//                recreateNodesOpenedState();
//            } else {
//                bookmarksTreeModel.setRoot(new Bookmark());
//            }
//            bookmarksTree.treeDidChange();
//            SwingUtilities.invokeLater(new Runnable() {
//
//                @Override
//                public void run() {
//                    if (target != null) {
//                        followBookmarkInView(target);
//                    } else {
//                        viewPanel.goToFirstPage();
//                    }
//                }
//            });
//            updateThumbnailsPanel(fileOperator.getViewPanel().getThumbnails());
//        } catch (Exception ex) {
//            showErrorMessage(Res.getString("ERROR_OPENING_FILE") + " "
//                    + file.getName());
//        } finally {
//            CursorToolkit.stopWaitCursor(tbBold);
//            removeProgressBar();
//        }
//
//    }
    public void openFileAsync(final File file, final Bookmark target) {
        setProgressBar(Res.getString("WAIT_LOADING_FILE"));
        CursorToolkit.startWaitCursor(tbBold);

        SwingWorker opener = new SwingWorker<Bookmark, Void>() {

            @Override
            protected Bookmark doInBackground() throws Exception {
                fileOperator.open(file);
                Bookmark root = fileOperator.getRootBookmark();
                return root;
            }

            @Override
            protected void done() {
                Bookmark root = null;
                try {
                    root = get();
                    bookmarksTree.setRootVisible(false);
                    bookmarksTree.setEditable(true);
                    if (root != null) {
                        bookmarksTreeModel.setRoot(root);
                        recreateNodesOpenedState();
                    } else {
                        bookmarksTreeModel.setRoot(new Bookmark());
                    }
                    bookmarksTree.treeDidChange();
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            if (target != null) {
                                followBookmarkInView(target);
                            } else {
                                viewPanel.goToFirstPage();
                            }
                        }
                    });
                    updateThumbnailsPanel(fileOperator.getViewPanel().getThumbnails());
                } catch (Exception ex) {
                    showErrorMessage(Res.getString("ERROR_OPENING_FILE") + " "
                            + file.getName());
                } finally {
                    CursorToolkit.stopWaitCursor(tbBold);
                    removeProgressBar();
                }
            }
        };
        opener.execute();
    }

    private void updateThumbnailsPanel(JScrollPane thumbnails) {
        leftPanel.updateThumbnails(thumbnails);
    }

    private void recreateNodesOpenedState() {

        bookmarksTree.visitAllNodes(new Visitor<Bookmark>() {

            @Override
            public void process(Bookmark bookmark) {
                TreePath path = new TreePath(bookmark.getPath());
                if (bookmark.isOpened() && bookmarksTree.isVisible(path)) {
                    bookmarksTree.expandPath(path);
                }
            }
        });
    }

    private class ColorChooserListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            TreePath[] paths = bookmarksTree.getSelectionPaths();
            for (TreePath path : paths) {
                Bookmark bookmark = (Bookmark) path.getLastPathComponent();
                bookmark.setColor(colorChooser.getColor());
            }
        }
    }

    private void undo() {
        try {
            undoManager.undo();
            fileOperator.setFileChanged(true);
        } catch (CannotUndoException ex) {
        } finally {
            updateUndoRedoPresentation();
            bookmarksTreeModel.nodeStructureChanged((TreeNode) bookmarksTreeModel.getRoot());
            recreateNodesOpenedState();
        }
    }

    private void redo() {
        try {
            undoManager.redo();
            fileOperator.setFileChanged(true);
        } catch (CannotUndoException ex) {
        } finally {
            updateUndoRedoPresentation();
            bookmarksTreeModel.nodeStructureChanged((TreeNode) bookmarksTreeModel.getRoot());
            recreateNodesOpenedState();
        }
    }

    private void adjustInheritValues(Bookmark bookmark) {
        if (checkInheritTop.isSelected()) {
            bookmark.setTop(-1);
        }
        if (checkInheritLeft.isSelected()) {
            bookmark.setLeft(-1);
        }
        if (checkInheritZoom.isSelected()) {
            bookmark.setZoom(0.0f);
        }
    }

    private void addSibling() {
        Bookmark bookmark = viewPanel.getBookmarkFromView();
        adjustInheritValues(bookmark);
        Bookmark selected = getSelectedBookmark();
        Bookmark parent;
        if (selected == null) {
            parent = (Bookmark) bookmarksTreeModel.getRoot();
            parent.add(bookmark);
        } else {
            parent = (Bookmark) selected.getParent();
            int selectedPosition = parent.getIndex(selected);
            parent.insert(bookmark, selectedPosition + 1);
        }
        bookmarksTreeModel.nodeStructureChanged(parent);
        recreateNodesOpenedState();
        bookmarksTree.startEditingAtPath(
                new TreePath(bookmark.getPath()));
        fileOperator.setFileChanged(true);

    }

    private void addChild() {
        Bookmark bookmark = viewPanel.getBookmarkFromView();
        adjustInheritValues(bookmark);
        Bookmark selected = getSelectedBookmark();
        if (selected != null) {
            selected.add(bookmark);
            bookmarksTreeModel.nodeStructureChanged(selected);
            recreateNodesOpenedState();
            bookmarksTree.startEditingAtPath(
                    new TreePath(bookmark.getPath()));
        }
        fileOperator.setFileChanged(true);
    }

    private int askAddOrReplace() {

        Object[] choices = new Object[]{
            Res.getString("REPLACE_CURRENT"),
            Res.getString("ADD_TO_CURRENT"),
            Res.getString("CANCEL")};
        int response = JOptionPane.showOptionDialog(this, Res.getString("ADD_TO_CURRENT_OR_REPLACE"), JPdfBookmarks.APP_NAME,
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);

        int addOrReplace = response;
        switch (response) {
            case 0: //replace
                addOrReplace = UndoableMultiSetDest.REPLACE;
                break;
            case 1: //add
                addOrReplace = UndoableMultiSetDest.ADD;
                break;
            case 2: //abort
                addOrReplace = UndoableMultiSetDest.ABORT;
                break;
        }
        return addOrReplace;
    }

    private void setDestFromView() {

        Bookmark fromView = viewPanel.getBookmarkFromView();
        adjustInheritValues(fromView);

        int addOrReplace = askAddOrReplace();
        if (addOrReplace == UndoableMultiSetDest.ABORT) {
            return;
        }

        boolean keepPageNumbers = false;
        if (bookmarksTree.getSelectionPaths().length > 1) {
            int answer = JOptionPane.showConfirmDialog(this, Res.getString("KEEP_PAGE_NUMBERS"), JPdfBookmarks.APP_NAME,
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                keepPageNumbers = true;
            }
        }

        UndoableMultiSetDestFromView undoable = new UndoableMultiSetDestFromView(bookmarksTree,
                addOrReplace, fromView, keepPageNumbers);
        undoable.doEdit();
        undoSupport.postEdit(undoable);

        fileOperator.setFileChanged(true);
    }

    private void setLaunchLink() {

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setCurrentDirectory(fileOperator.getFile().getParentFile());
        chooser.setDialogTitle(Res.getString("LAUNCH_LINK_DIALOG_TITLE"));

        if (chooser.showSaveDialog(JPdfBookmarksGui.this)
                != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File f = chooser.getSelectedFile();
        if (f == null) {
            return;
        }

        File relativeFile = Ut.createRelativePath(fileOperator.getFile(), f);
        String relativePath = Ut.onWindowsReplaceBackslashWithSlash(relativeFile.toString());

        int addOrReplace = askAddOrReplace();
        if (addOrReplace == UndoableMultiSetDest.ABORT) {
            return;
        }

        UndoableMultiSetLaunchLink undoable =
                new UndoableMultiSetLaunchLink(bookmarksTree, addOrReplace, relativePath);
        undoable.doEdit();
        undoSupport.postEdit(undoable);

        fileOperator.setFileChanged(true);

    }

    private void setWebLink() {
        String address = JOptionPane.showInputDialog(this,
                Res.getString("INPUT_WEB_ADDRESS") + ": ");

        if (address == null) {
            return;
        }

        int addOrReplace = askAddOrReplace();
        if (addOrReplace == UndoableMultiSetDest.ABORT) {
            return;
        }

        UndoableMultiSetWebLink undoable =
                new UndoableMultiSetWebLink(bookmarksTree, addOrReplace, address);
        undoable.doEdit();
        undoSupport.postEdit(undoable);

        fileOperator.setFileChanged(true);
        goToWebLinkAsking(address);
    }

    private void launchFile(String file) {

        int answer = JOptionPane.showConfirmDialog(this,
                String.format(Res.getString("MSG_LAUNCH_FILE"), file), title,
                JOptionPane.OK_CANCEL_OPTION);
        if (answer != JOptionPane.OK_OPTION) {
            return;
        }

        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(new File(file).getCanonicalFile().getAbsoluteFile());
        } catch (Exception ex) {
            showErrorMessage(Res.getString("ERR_LAUNCHING_FILE") + " " + file + ".");
        }
    }

    private void goToWebLinkAsking(String uri) {

        if (userPrefs.getNeverAskWebAccess() == false) {
            int answer = JOptionPane.showConfirmDialog(this,
                    Res.getString("MSG_LAUNCH_BROWSER"), title,
                    JOptionPane.OK_CANCEL_OPTION);

            if (answer != JOptionPane.OK_OPTION) {
                return;
            }
        }

        goToWebLink(uri);
    }

    private void goToWebLink(String uri) {

        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI(uri));
        } catch (URISyntaxException ex) {
            showErrorMessage(Res.getString("ERROR_WRONG_URI"));
        } catch (IOException ex) {
            showErrorMessage(Res.getString("ERROR_LAUNCHING_BROWSER"));
        }
    }

    private void cut() {
        copy(true);
        delete();
    }

    private void copyBookmarkFromView() {
        Bookmark b = viewPanel.getBookmarkFromView();
        if (b != null) {
            //Bookmark bookmarkCopied = Bookmark.cloneBookmark(b, !b.isOpened());
            ArrayList<Bookmark> bookmarks = new ArrayList<Bookmark>();
            bookmarks.add(b);
            BookmarkSelection bs = new BookmarkSelection(bookmarks, bookmarksFlavor, false, fileOperator.getFile());
            localClipboard.setContents(bs, bs);
            flavorsChanged();
        }
    }

    private void copy(boolean cut) {
        ArrayList<Bookmark> bookmarksSelected = getSelectedBookmarks();
        if (bookmarksSelected != null && !bookmarksSelected.isEmpty()) {
            BookmarkSelection bs = new BookmarkSelection(bookmarksSelected, bookmarksFlavor, cut, fileOperator.getFile());
            localClipboard.setContents(bs, bs);
            flavorsChanged();
        }
    }

    private void paste() {
        Transferable content = localClipboard.getContents(this);

        if (content != null && content.isDataFlavorSupported(bookmarksFlavor)) {
            try {
                BookmarkSelection bs = (BookmarkSelection) content.getTransferData(bookmarksFlavor);
                ArrayList<Bookmark> bookmarksCopied = bs.getBookmarks();
                if (!fileOperator.getFile().equals(bs.getFile())) {
                    File relativeRemoteFile = Ut.createRelativePath(fileOperator.getFile(), bs.getFile());
                    for (Bookmark b : bookmarksCopied) {
                        b.setRemoteFilePathWithChildren(relativeRemoteFile);
                    }
                }
                TreePath path = bookmarksTree.getSelectionPath();
                Bookmark father;
                if (path != null) {
                    Bookmark selected = (Bookmark) path.getLastPathComponent();
                    father = (Bookmark) selected.getParent();
                    int i = father.getIndex(selected);
                    for (Bookmark b : bookmarksCopied) {
                        father.insert(b, i + 1);
                        i++;
                    }
                } else {
                    father = (Bookmark) bookmarksTreeModel.getRoot();
                    for (Bookmark b : bookmarksCopied) {
                        father.add(b);
                    }
                }
                UndoablePasteBookmarks undoablePaste = new UndoablePasteBookmarks(bookmarksTreeModel, bookmarksCopied);
                undoablePaste.doEdit();
                undoSupport.postEdit(undoablePaste);
                fileOperator.setFileChanged(true);
                bookmarksTreeModel.nodeStructureChanged(father);
                recreateNodesOpenedState();
            } catch (UnsupportedFlavorException ex) {
            } catch (IOException ex) {
            }
        }
    }

    private ArrayList<Bookmark> getSelectedBookmarks() {

        ArrayList<Bookmark> bookmarksList = new ArrayList<Bookmark>();
        TreePath[] paths = bookmarksTree.getSelectionPaths();
        for (TreePath path : paths) {
            bookmarksList.add((Bookmark) path.getLastPathComponent());
        }
        return bookmarksList;
    }

    private void delete() {

        UndoableDeleteBookmark undoableDelete =
                new UndoableDeleteBookmark(
                bookmarksTreeModel, getSelectedBookmarks());

        undoableDelete.doEdit();
        recreateNodesOpenedState();
        undoSupport.postEdit(undoableDelete);
    }

    private void setBold(boolean bold) {
        TreePath[] paths = bookmarksTree.getSelectionPaths();
        for (TreePath path : paths) {
            Bookmark bookmark = (Bookmark) path.getLastPathComponent();
            bookmark.setBold(bold);
        }

        bookmarksTree.updateTree(mouseAdapter);
        valueChanged(null);
        fileOperator.setFileChanged(true);
    }

    private void setItalic(boolean italic) {
        TreePath[] paths = bookmarksTree.getSelectionPaths();
        for (TreePath path : paths) {
            Bookmark bookmark = (Bookmark) path.getLastPathComponent();
            bookmark.setItalic(italic);
        }

        bookmarksTree.updateTree(mouseAdapter);
        valueChanged(null);
        fileOperator.setFileChanged(true);
    }

    private void changeColor() {
        colorChooser = new JColorChooser();
        ColorsListPanel panel = new ColorsListPanel();
        panel.setName(Res.getString("BROWSERS_KNOWN_COLORS"));
        colorChooser.addChooserPanel(panel);
        colorChooser.setColor(getSelectedBookmark().getColor());
        JColorChooser.createDialog(JPdfBookmarksGui.this,
                Res.getString("ACTION_CHANGE_COLOR"), true, colorChooser,
                new ColorChooserListener(), null).setVisible(true);

        fileOperator.setFileChanged(true);
    }

    private void rename() {
        if (bookmarksTree.getSelectionCount() > 1) {
            String s = (String) JOptionPane.showInputDialog(
                    bookmarksTree,
                    Res.getString("INSERT_NEW_TITLE"),
                    JPdfBookmarks.APP_NAME,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    getSelectedBookmark().getTitle());

            if ((s != null) && (s.length() > 0)) {
                UndoableRenameAction undoableRename =
                        new UndoableRenameAction(bookmarksTree, s);

                undoableRename.doEdit();
                bookmarksTreeModel.nodeStructureChanged((TreeNode) bookmarksTreeModel.getRoot());
                recreateNodesOpenedState();
                undoSupport.postEdit(undoableRename);
            }
        } else {
            bookmarksTree.startEditingAtPath(
                    bookmarksTree.getSelectionPath());
        }
    }

    public void showErrorMessage(String resMessage) {
        JOptionPane.showMessageDialog(JPdfBookmarksGui.this,
                resMessage, title,
                JOptionPane.ERROR_MESSAGE);
    }

    private void saveAsync() {
        setProgressBar(Res.getString("WAIT_SAVING_FILE"));
        CursorToolkit.startWaitCursor(tbBold);
        new AsyncSaveAs(fileOperator.getFile(), viewPanel.getBookmarkFromView()).execute();
//        setProgressBar(Res.getString("WAIT_SAVING_FILE"));
//        CursorToolkit.startWaitCursor(tbBold);
//
//        SwingWorker saver = new SwingWorker<Void, Void>() {
//
//            @Override
//            protected Void doInBackground() throws Exception {
//                if (!fileOperator.save((Bookmark) bookmarksTreeModel.getRoot())) {
//                    //fileOperator.save already print an error message, no need to do it again here
//                    //showErrorMessage(Res.getString("ERROR_SAVING_FILE"));
//                }
//                return null;
//            }
//
//            @Override
//            protected void done() {
//                CursorToolkit.stopWaitCursor(tbBold);
//                removeProgressBar();
//            }
//        };
//        saver.execute();

    }

//    private void save() {
//        if (!fileOperator.save((Bookmark) bookmarksTreeModel.getRoot())) {
//            showErrorMessage(Res.getString("ERROR_SAVING_FILE"));
//        }
//    }
    private class AsyncSaveAs extends SwingWorker {

        File f;
        Bookmark currentView;

        public AsyncSaveAs(File f, Bookmark currentView) {
            this.f = f;
            this.currentView = currentView;
        }

        @Override
        protected Object doInBackground() throws Exception {
            fileOperator.saveAs((Bookmark) bookmarksTreeModel.getRoot(),
                    f.getAbsolutePath());
            return null;
        }

        @Override
        protected void done() {
            followBookmarkInView(currentView);
            CursorToolkit.stopWaitCursor(tbBold);
            removeProgressBar();
        }
    }

    private void saveAsAsync() {

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new SimpleFileFilter("pdf", "PDF File"));
        chooser.setCurrentDirectory(fileOperator.getFile().getParentFile());


        if (chooser.showSaveDialog(JPdfBookmarksGui.this)
                != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File f = chooser.getSelectedFile();
        if (f == null) {
            return;
        }

        String filename = f.getAbsolutePath();
        if (filename.endsWith(".pdf") == false) {
            filename = filename + ".pdf";
            f = new File(filename);
        }

        if (f.exists()) {
            int response = JOptionPane.showConfirmDialog(
                    JPdfBookmarksGui.this,
                    Res.getString("WARNING_OVERWRITE"),
                    title,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (response != JOptionPane.YES_OPTION) {
                return;
            }
        }

        setProgressBar(Res.getString("WAIT_SAVING_FILE"));
        CursorToolkit.startWaitCursor(tbBold);
        new AsyncSaveAs(f, viewPanel.getBookmarkFromView()).execute();
//        fileOperator.saveAsAsync((Bookmark) bookmarksTreeModel.getRoot(),
//                f.getAbsolutePath());
    }

    private void close() {
        if (!askCloseWithoutSave()) {
            return;
        }

        fileOperator.close();
    }

    private void load() {

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new SimpleFileFilter("txt", "Text Files"));
        chooser.setCurrentDirectory(fileOperator.getFile().getParentFile());

        chooser.setDialogTitle(Res.getString("LOAD_DIALOG_TITLE"));

        if (chooser.showOpenDialog(this)
                != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        if (file == null || !file.isFile()) {
            return;
        }

        try {
//            IBookmarksConverter converter =
//                    new iTextBookmarksConverter(fileOperator.getFilePath());
            IBookmarksConverter converter = Bookmark.getBookmarksConverter();
            if (converter == null) {
                showErrorMessage(Res.getString("ERROR_BOOKMARKS_CONVERTER_NOT_FOUND"));
                throw new Exception();
            }
            converter.open(fileOperator.getFilePath(), fileOperator.getPassword());
            Bookmark root = Bookmark.outlineFromFile(converter,
                    file.getAbsolutePath(), userPrefs.getIndentationString(),
                    userPrefs.getPageSeparator(),
                    userPrefs.getAttributesSeparator(), userPrefs.getCharsetEncoding());
            converter.close();
            UndoableLoadBookmarks undoableLoad = new UndoableLoadBookmarks(
                    bookmarksTreeModel, bookmarksTree, root);
            undoableLoad.doEdit();
            undoSupport.postEdit(undoableLoad);
            fileOperator.setFileChanged(true);
            recreateNodesOpenedState();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            showErrorMessage(Res.getString("ERROR_LOADING_TEXT_FILE"));
        }
    }

    private JPanel newVersionAvailable(boolean available) {
        JPanel panel = new JPanel(new FlowLayout());
        if (available) {
            panel.add(new JLabel(Res.getString("NEW_VERSION_AVAILABLE")));
            LinkLabel address;
            try {
                address = new LinkLabel(new URI(JPdfBookmarks.DOWNLOAD_URL),
                        " " + Res.getString("DOWNLOAD_PAGE"));
                address.setUnderlineVisible(false);
                address.setBorder(null);
                address.init();
                panel.add(address);
            } catch (URISyntaxException ex) {
                showErrorMessage(Res.getString("ERROR_CHECKING_UPDATES"));
            }
        } else {
            panel.add(new JLabel(Res.getString("NO_NEW_VERSION_AVAILABLE")));
        }
        return panel;
    }

    private class LastVersionWebChecker extends SwingWorker<Boolean, Void> {

        boolean quietMode = false;

        public LastVersionWebChecker(boolean quietMode) {
            this.quietMode = quietMode;
        }

        @Override
        protected Boolean doInBackground() throws Exception {
            Proxy proxy = Proxy.NO_PROXY;
            if (userPrefs.getUseProxy()) {
                SocketAddress addr = new InetSocketAddress(
                        userPrefs.getProxyAddress(), userPrefs.getProxyPort());
                String proxyType = userPrefs.getProxyType();
                proxy = new Proxy(Proxy.Type.valueOf(proxyType), addr);
            }

            URL altervista = null;
            BufferedReader in;
            boolean newVersionAvailable = false;
            altervista = new URL(JPdfBookmarks.LAST_VERSION_PROPERTIES_URL);
            HttpURLConnection connection = (HttpURLConnection) altervista.openConnection(proxy);
            Properties prop = new Properties();
            Reader reader = new InputStreamReader(connection.getInputStream());
            prop.load(reader);
            String inputLine = prop.getProperty("VERSION");
            String[] newVersionNumbers = inputLine.split("\\.");
            String[] thisVersionNumbers = JPdfBookmarks.getVersion().split("\\.");
            for (int i = 0; i < newVersionNumbers.length; i++) {
                int newVerN = Integer.parseInt(newVersionNumbers[i]);
                int thisVerN = Integer.parseInt(thisVersionNumbers[i]);
                if (newVerN > thisVerN) {
                    newVersionAvailable = true;
                    break;
                } else if (thisVerN > newVerN) {
                    break;
                }
            }
            reader.close();
            connection.disconnect();
            return newVersionAvailable;
        }

        @Override
        protected void done() {
            boolean newVersion = false;
            try {
                newVersion = get();
                if (newVersion || !quietMode) {
                    JOptionPane.showMessageDialog(JPdfBookmarksGui.this, newVersionAvailable(newVersion),
                            JPdfBookmarks.APP_NAME, JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                if (!quietMode) {
                    showErrorMessage(Res.getString("ERROR_CHECKING_UPDATES"));
                }
            }

        }
    }

//    private class UpdatesChecker extends SwingWorker<Boolean, Void> {
//
//        boolean quietMode = false;
//
//        public UpdatesChecker(boolean quietMode) {
//            this.quietMode = quietMode;
//        }
//
//        @Override
//        protected Boolean doInBackground() throws Exception {
//            Proxy proxy = Proxy.NO_PROXY;
//            if (userPrefs.getUseProxy()) {
//                SocketAddress addr = new InetSocketAddress(
//                        userPrefs.getProxyAddress(), userPrefs.getProxyPort());
//                String proxyType = userPrefs.getProxyType();
//                proxy = new Proxy(Proxy.Type.valueOf(proxyType), addr);
//            }
//
//            URL altervista = null;
//            BufferedReader in;
//            boolean newVersionAvailable = false;
//            altervista = new URL(JPdfBookmarks.LAST_VERSION_URL);
//            HttpURLConnection connection = (HttpURLConnection) altervista.openConnection(proxy);
//            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            String inputLine;
//            while ((inputLine = in.readLine()) != null) {
//                String[] newVersionNumbers = inputLine.split("\\.");
//                String[] thisVersionNumbers = JPdfBookmarks.VERSION.split("\\.");
//                for (int i = 0; i < newVersionNumbers.length; i++) {
//                    if (Integer.parseInt(newVersionNumbers[i])
//                            > Integer.parseInt(thisVersionNumbers[i])) {
//                        newVersionAvailable = true;
//                    }
//                }
//            }
//            in.close();
//            return newVersionAvailable;
//        }
//
//        @Override
//        protected void done() {
//            boolean newVersion = false;
//            try {
//                newVersion = get();
//                if (newVersion || !quietMode) {
//                    JOptionPane.showMessageDialog(JPdfBookmarksGui.this, newVersionAvailable(newVersion),
//                            JPdfBookmarks.APP_NAME, JOptionPane.INFORMATION_MESSAGE);
//                }
//            } catch (Exception ex) {
//                if (!quietMode) {
//                    showErrorMessage(Res.getString("ERROR_CHECKING_UPDATES"));
//                }
//            }
//
//        }
//    }
    private void checkUpdates(boolean quietMode) {
//		UpdatesChecker updatesChecker = new UpdatesChecker(quietMode);
//		updatesChecker.execute();
        LastVersionWebChecker updatesChecker = new LastVersionWebChecker(quietMode);
        updatesChecker.execute();
    }

    private void dump() {

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new SimpleFileFilter("txt", "Text Files"));
        chooser.setCurrentDirectory(fileOperator.getFile().getParentFile());

        chooser.setDialogTitle(Res.getString("DUMP_DIALOG_TITLE"));
        if (chooser.showSaveDialog(this)
                != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File f = chooser.getSelectedFile();
        if (f == null) {
            return;
        }

        String filename = f.getName();

        if (filename.endsWith(".txt") == false) {
            filename = f.getParent() + File.separatorChar + filename + ".txt";
            f = new File(filename);
        }

        if (f.exists()) {
            int response = JOptionPane.showConfirmDialog(
                    this,
                    Res.getString("WARNING_OVERWRITE"),
                    title,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (response != JOptionPane.YES_OPTION) {
                return;
            }
        }

        Dumper dumper = new Dumper(null, userPrefs.getIndentationString(),
                userPrefs.getPageSeparator(), userPrefs.getAttributesSeparator());

        try {
            FileOutputStream fos = new FileOutputStream(f);
            OutputStreamWriter outStream = new OutputStreamWriter(fos, userPrefs.getCharsetEncoding());
            dumper.printBookmarksIterative(outStream, (Bookmark) bookmarksTreeModel.getRoot());
            outStream.close();
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(f);
            } catch (Exception ex) {
                showErrorMessage(Res.getString("ERR_LAUNCHING_FILE") + " " + f + ".");
            }
        } catch (Exception exc) {
            JOptionPane.showMessageDialog(this,
                    Res.getString("ERROR_SAVING_FILE"),
                    title, JOptionPane.WARNING_MESSAGE);
            return;
        }
    }

    private void applyPageOffsetDialog() {
        TreePath[] paths = bookmarksTree.getSelectionPaths();
        int maxPageNumber = -1, minPageNumber = viewPanel.getNumPages();
        for (TreePath path : paths) {
            Bookmark bookmark = (Bookmark) path.getLastPathComponent();
            int targetPage = bookmark.getPageNumber();
            if (targetPage > maxPageNumber) {
                maxPageNumber = targetPage;
            }
            if (targetPage < minPageNumber) {
                minPageNumber = targetPage;
            }
        }
        Bookmark selected = getSelectedBookmark();
        PageOffsetDialog pageOffsetDialog = new PageOffsetDialog(
                this, viewPanel.getCurrentPage() - selected.getPageNumber(),
                viewPanel.getNumPages() - maxPageNumber,
                -minPageNumber + 1);
        pageOffsetDialog.setVisible(true);
        if (pageOffsetDialog.operationNotAborted()) {
            UnboablePageOffset undoablePageOffset = new UnboablePageOffset(
                    bookmarksTreeModel, paths, pageOffsetDialog.getOffsetValue());

            undoablePageOffset.doEdit();
            recreateNodesOpenedState();
            undoSupport.postEdit(undoablePageOffset);
        }
    }

    private void goToPageDialog() {
        GoToPageDialog goToPageDialog = new GoToPageDialog(
                JPdfBookmarksGui.this,
                viewPanel.getCurrentPage(), viewPanel.getNumPages());
        goToPageDialog.setVisible(true);
        if (goToPageDialog.operationNotAborted()) {
            viewPanel.goToPage(goToPageDialog.getPage());
        }
    }

    private void createActions() {

//        cutAction = TransferHandler.getCutAction();
//        copyAction = TransferHandler.getCopyAction();
//        pasteAction = TransferHandler.getPasteAction();

        extractLinks = new ActionBuilder("ACTION_EXTRACT_LINKS", "ACTION_EXTRACT_LINKS_DESCR",
                null, "extract-links.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                extractLinksFromPage();
            }
        };

        cutAction = new ActionBuilder("ACTION_CUT", "ACTION_CUT_DESCR", "ctrl X",
                "edit-cut.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                cut();
            }
        };

        copyAction = new ActionBuilder("ACTION_COPY", "ACTION_COPY_DESCR", "ctrl C",
                "edit-copy.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                copy(false);
            }
        };

        copyBookmarkFromViewAction = new ActionBuilder("ACTION_COPY_BOOKMARK_FROM_VIEW",
                "ACTION_COPY_BOOKMARK_FROM_VIEW_DESCR", "ctrl shift c", "copy-linked.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                copyBookmarkFromView();
            }
        };

        pasteAction = new ActionBuilder("ACTION_PASTE", "ACTION_PASTE_DESCR", "ctrl V",
                "edit-paste.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                paste();
            }
        };

        quitAction = new ActionBuilder("ACTION_QUIT", "ACTION_QUIT_DESCR",
                "alt F4", "system-log-out.png", true) {

            @Override
            public void actionPerformed(ActionEvent e) {
                exitApplication();
            }
        };

        // <editor-fold defaultstate="collapsed" desc="File Actions">
        openAction = new ActionBuilder("ACTION_OPEN", "ACTION_OPEN_DESCR",
                "ctrl O", "document-open.png", true) {

            @Override
            public void actionPerformed(ActionEvent e) {
                openDialog();
            }
        };

        openLinkedPdf = new ActionBuilder("ACTION_OPEN_LINKED_PDF", "ACTION_OPEN_LINKED_PDF_DESCR",
                "ctrl alt O", "open-linked-pdf.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                openLinkedPdf();
            }
        };

        saveAction = new ActionBuilder("ACTION_SAVE", "ACTION_SAVE_DESCR",
                "ctrl S", "document-save.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                //save();
                saveAsync();
            }
        };

        saveAsAction = new ActionBuilder("ACTION_SAVE_AS", "ACTION_SAVE_AS_DESCR",
                "ctrl A", "document-save-as.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                saveAsAsync();
            }
        };

        closeAction = new ActionBuilder("ACTION_CLOSE", "ACTION_CLOSE_DESCR",
                "ctrl F4", "process-stop.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        };

        showOnOpenAction = new ActionBuilder("ACTION_SHOW_ON_OPEN",
                "ACTION_SHOW_ON_OPEN_DESCR", null, "show-on-open.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource().equals(tbShowOnOpen)) {
                    cbShowOnOpen.setSelected(tbShowOnOpen.isSelected());
                    fileOperator.setShowBookmarksOnOpen(tbShowOnOpen.isSelected());
                } else {
                    tbShowOnOpen.setSelected(cbShowOnOpen.isSelected());
                    fileOperator.setShowBookmarksOnOpen(cbShowOnOpen.isSelected());
                }
            }
        };

        dumpAction = new ActionBuilder("ACTION_DUMP", "ACTION_DUMP_DESCR",
                "ctrl alt D", "dump.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                dump();
            }
        };

        loadAction = new ActionBuilder("ACTION_LOAD", "ACTION_LOAD_DESCR",
                "ctrl alt L", "load.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                load();
            }
        };
        // </editor-fold>

        undoAction = new ActionBuilder("ACTION_UNDO", "ACTION_UNDO_DESCR",
                "ctrl Z", "edit-undo.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                undo();
            }
        };

        redoAction = new ActionBuilder("ACTION_REDO", "ACTION_REDO_DESCR",
                "ctrl shift Z", "edit-redo.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                redo();
            }
        };

        addSiblingAction = new ActionBuilder("ACTION_ADD_SIBLING",
                "ACTION_ADD_SIBLING_DESCR",
                "ctrl alt S", "add-sibling.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                addSibling();
            }
        };

        addChildAction = new ActionBuilder("ACTION_ADD_CHILD",
                "ACTION_ADD_CHILD_DESCR", "ctrl alt F", "add-child.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                addChild();
            }
        };

        addWebLinkAction = new ActionBuilder("ACTION_ADD_WEB_LINK",
                "ACTION_ADD_WEB_LINK_DESCR", "ctrl alt W", "bookmark-web.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                setWebLink();
            }
        };

        addLaunchLinkAction = new ActionBuilder("ACTION_ADD_LAUNCH_LINK",
                "ACTION_ADD_LAUNCH_LINK_DESCR", "ctrl alt H", "bookmark-launch.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                setLaunchLink();
            }
        };

        deleteAction = new ActionBuilder("ACTION_DELETE", "ACTION_DELETE_DESCR",
                "ctrl DELETE", "user-trash.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                delete();
            }
        };

        setBoldAction = new ActionBuilder("ACTION_SET_BOLD", "ACTION_SET_BOLD_DESCR",
                "ctrl G", "format-text-bold.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                AbstractButton btn = (AbstractButton) e.getSource();
                setBold(btn.isSelected());
            }
        };

        setItalicAction = new ActionBuilder("ACTION_SET_ITALIC", "ACTION_SET_ITALIC_DESCR",
                "ctrl I", "format-text-italic.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                AbstractButton btn = (AbstractButton) e.getSource();
                setItalic(btn.isSelected());
            }
        };

        changeColorAction = new ActionBuilder("ACTION_CHANGE_COLOR",
                "ACTION_CHANGE_COLOR_DESCR", null, "applications-graphics.png",
                false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                changeColor();
            }
        };

        renameAction = new ActionBuilder("ACTION_RENAME", "ACTION_RENAME_DESCR",
                "ctrl F2", "edit-select-all.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                rename();
            }
        };

        setDestFromViewAction = new ActionBuilder("ACTION_DEST_FROM_VIEW",
                "ACTION_DEST_FROM_VIEW_DESCR", "ctrl alt A",
                "dest-from-view.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                setDestFromView();
            }
        };

        applyPageOffset = new ActionBuilder("ACTION_PAGE_OFFSET",
                "ACTION_PAGE_OFFSET_DESCR", null, "page-offset.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                applyPageOffsetDialog();
            }
        };

        selectText = new ActionBuilder("ACTION_SELECT_TEXT", "ACTION_SELECT_TEXT_DESCR", "ctrl alt T",
                "select-text.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource().equals(tbSelectText)) {
                    cbSelectText.setSelected(tbSelectText.isSelected());
                } else {
                    tbSelectText.setSelected(cbSelectText.isSelected());
                }

                viewPanel.setTextSelectionMode(tbSelectText.isSelected());
            }
        };

        connectToClipboard = new ActionBuilder("ACTION_CONNECT_CLIPBOARD",
                "ACTION_CONNECT_CLIPBOARD_DESCR", "ctrl alt C", "system-clip.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource().equals(tbConnectToClipboard)) {
                    cbConnectToClipboard.setSelected(tbConnectToClipboard.isSelected());
                } else {
                    tbConnectToClipboard.setSelected(cbConnectToClipboard.isSelected());
                }

                viewPanel.setConnectToClipboard(tbConnectToClipboard.isSelected());
            }
        };

        // <editor-fold defaultstate="collapsed" desc="Navigation Actions">
        goNextPageAction = new ActionBuilder("ACTION_GO_NEXT",
                "ACTION_GO_NEXT_DESCR", "ctrl alt RIGHT", "go-next.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                viewPanel.goToNextPage();
            }
        };

        goFirstPageAction = new ActionBuilder("ACTION_GO_FIRST",
                "ACTION_GO_FIRST_DESCR", "ctrl alt HOME", "go-first.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                viewPanel.goToFirstPage();
            }
        };

        goLastPageAction = new ActionBuilder("ACTION_GO_LAST",
                "ACTION_GO_LAST_DESCR", "ctrl alt END", "go-last.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                viewPanel.goToLastPage();
            }
        };

        goPreviousPageAction = new ActionBuilder("ACTION_GO_PREV",
                "ACTION_GO_PREV_DESCR", "ctrl alt LEFT", "go-previous.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                viewPanel.goToPreviousPage();
            }
        };

        goToPageAction = new ActionBuilder("ACTION_GO_PAGE",
                "ACTION_GO_PAGE_DESCR", "ctrl alt INSERT", null, false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                goToPageDialog();
            }
        };// </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="Zoom Actions">
        fitRectAction = new ActionBuilder("ACTION_FIT_RECT",
                "ACTION_FIT_RECT_DESCR", "ctrl R", "fit-rect.png",
                false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof JToggleButton) {
                    rbFitRect.setSelected(true);
                } else {
                    tbFitRect.setSelected(true);
                }
                viewPanel.setFitRect(null);
            }
        };

        fitWidthAction = new ActionBuilder("ACTION_FIT_WIDTH",
                "ACTION_FIT_WIDTH_DESCR", "ctrl W", "fit-width.png",
                false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof JToggleButton) {
                    rbFitWidth.setSelected(true);
                } else {
                    tbFitWidth.setSelected(true);
                }

                viewPanel.setFitWidth(-1);
            }
        };

        fitHeightAction = new ActionBuilder("ACTION_FIT_HEIGHT",
                "ACTION_FIT_HEIGHT_DESCR", "ctrl H", "fit-height.png",
                false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof JToggleButton) {
                    rbFitHeight.setSelected(true);
                } else {
                    tbFitHeight.setSelected(true);
                }

                viewPanel.setFitHeight(-1);
            }
        };

        fitNativeAction = new ActionBuilder("ACTION_FIT_NATIVE",
                "ACTION_FIT_NATIVE_DESCR", "ctrl N", "fit-native.png",
                false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof JToggleButton) {
                    rbFitNative.setSelected(true);
                } else {
                    tbFitNative.setSelected(true);
                }

                viewPanel.setFitNative();
            }
        };

        fitPageAction = new ActionBuilder("ACTION_FIT_PAGE",
                "ACTION_FIT_PAGE_DESCR", "ctrl G", "fit-page.png",
                false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof JToggleButton) {
                    rbFitPage.setSelected(true);
                } else {
                    tbFitPage.setSelected(true);
                }

                viewPanel.setFitPage();
            }
        };

        topLeftZoomAction = new ActionBuilder("ACTION_TOP_LEFT_ZOOM",
                "ACTION_TOP_LEFT_ZOOM_DESCR", null, "top-left-zoom.png",
                false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof JToggleButton) {
                    rbTopLeftZoom.setSelected(true);
                } else {
                    tbTopLeftZoom.setSelected(true);
                }

                viewPanel.setTopLeftZoom(-1, -1, 0f);
            }
        };

        zoomInAction = new ActionBuilder("ACTION_ZOOM_IN",
                "ACTION_ZOOM_IN_DESCR", "alt +", "zoom-in.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                float scale = (txtZoom.getInteger() + ZOOM_STEP) / 100f;
                viewPanel.setTopLeftZoom(-1, -1, scale);
            }
        };

        zoomOutAction = new ActionBuilder("ACTION_ZOOM_OUT",
                "ACTION_ZOOM_OUT_DESCR", "alt -", "zoom-out.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                float scale = (txtZoom.getInteger() - ZOOM_STEP) / 100f;
                viewPanel.setTopLeftZoom(-1, -1, scale);
            }
        };// </editor-fold>

        expandAllAction = new ActionBuilder("ACTION_EXPAND_ALL",
                "ACTION_EXPAND_ALL_DESCR", "ctrl E", null, false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                expandAllNodes();
            }
        };

        collapseAllAction = new ActionBuilder("ACTION_COLLAPSE_ALL",
                "ACTION_COLLAPSE_ALL_DESCR", "ctrl P", null, false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                collapseAllNodes();
            }
        };

        optionsDialogAction = new ActionBuilder("ACTION_OPTIONS_DIALOG",
                "ACTION_OPTIONS_DIALOG_DESCR", "ctrl alt O",
                "preferences-system.png", true) {

            @Override
            public void actionPerformed(ActionEvent e) {
//				OptionsDialog optionsDlg =
//						new OptionsDialog(JPdfBookmarksGui.this, true);
                OptionsDlg optionsDlg = new OptionsDlg(JPdfBookmarksGui.this,
                        true);
                optionsDlg.setLocationRelativeTo(JPdfBookmarksGui.this);
                //optionsDlg.setVisibleTab(OptionsDlg.TOOLBARS_PANEL);
                optionsDlg.setVisible(true);
            }
        };

        showActionsDialog = new ActionBuilder("ACTION_ACTIONS_DIALOG",
                "ACTION_ACTIONS_DIALOG_DESCR", "ctrl alt N",
                "actions-dialog.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                ActionsDialog d = new ActionsDialog(JPdfBookmarksGui.this, true, getSelectedBookmark());
                d.setLocationRelativeTo(JPdfBookmarksGui.this);
                d.setVisible(true);
                fileOperator.setFileChanged(d.isBookmarkModified());
            }
        };

        checkUpdatesAction = new ActionBuilder("ACTION_CHECK_UPDATES",
                "ACTION_CHECK_UPDATES_DESCR", null,
                "system-software-update.png", true) {

            @Override
            public void actionPerformed(ActionEvent e) {
                checkUpdates(false);
            }
        };

        readOnlineManualAction = new ActionBuilder("ACTION_READ_MANUAL",
                "ACTION_READ_MANUAL_DESCR", null, "help-browser.png", true) {

            @Override
            public void actionPerformed(ActionEvent e) {
                goToWebLink(JPdfBookmarks.MANUAL_URL);
            }
        };

        donateToProject = new ActionBuilder("ACTION_DONATE",
                "ACTION_DONATE_DESCR", null, "donate-icon.png", true) {

            @Override
            public void actionPerformed(ActionEvent e) {
                goToWebLink("http://sourceforge.net/donate/index.php?group_id=297580");
            }
        };

        goToAuthorBlog = new ActionBuilder("ACTION_GO_TO_BLOG",
                "ACTION_GO_TO_BLOG_DESCR", null, "internet-web-browser.png", true) {

            @Override
            public void actionPerformed(ActionEvent e) {
                goToWebLink(JPdfBookmarks.BLOG_URL);
            }
        };
    }

    private void extractLinksFromPage() {

        ArrayList<Bookmark> links = fileOperator.getLinksOnPage(viewPanel.getCurrentPage());
        if (links.isEmpty()) {
            JOptionPane.showMessageDialog(this, Res.getString("NO_LINKS_FOUND"), JPdfBookmarks.APP_NAME,
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Bookmark parent = null;
        for (Bookmark b : links) {
            Bookmark selected = getSelectedBookmark();
            if (selected == null) {
                parent = (Bookmark) bookmarksTreeModel.getRoot();
                parent.add(b);
            } else {
                parent = (Bookmark) selected.getParent();
                int selectedPosition = parent.getIndex(selected);
                parent.insert(b, selectedPosition + 1);
            }
        }
        UndoablePasteBookmarks undoableExtractLinks = new UndoablePasteBookmarks(bookmarksTreeModel, links);
        undoableExtractLinks.doEdit();
        undoSupport.postEdit(undoableExtractLinks);
        fileOperator.setFileChanged(true);
        bookmarksTreeModel.nodeStructureChanged((TreeNode) bookmarksTreeModel.getRoot());
        recreateNodesOpenedState();
        fileOperator.setFileChanged(true);
    }

    private void expandAllNodes() {

        for (int i = 0; i < bookmarksTree.getRowCount(); i++) {
            bookmarksTree.expandRow(i);
        }
    }

    private void collapseAllNodes() {

        expandAllNodes();
//                for (int i = 0; i < bookmarksTree.getRowCount(); i++) {
//                    bookmarksTree.collapseRow(i);
//                }
        Bookmark root = (Bookmark) bookmarksTreeModel.getRoot();
        if (root != null) {
            Enumeration<Bookmark> postOrder = root.postorderEnumeration();
            //just skip the root element
            if (postOrder.hasMoreElements()) {
                postOrder.nextElement();
            }
            while (postOrder.hasMoreElements()) {
                Bookmark b = postOrder.nextElement();
                TreePath path = new TreePath(b.getPath());
                //bookmarksTree.collapsePath(path);
                bookmarksTree.collapseRow(bookmarksTree.getRowForPath(path));
            }
        }

    }

    private void updateStyleButtons(Bookmark bookmark) {
        cbBold.setSelected(bookmark.isBold());
        cbEditMenuBold.setSelected(bookmark.isBold());
        tbBold.setSelected(bookmark.isBold());
        cbItalic.setSelected(bookmark.isItalic());
        cbEditMenuItalic.setSelected(bookmark.isItalic());
        tbItalic.setSelected(bookmark.isItalic());
    }

    private void updateUndoRedoPresentation() {
        undoAction.setEnabled(undoManager.canUndo());
        redoAction.setEnabled(undoManager.canRedo());

        String redoPresentation = "";
        String undoPresentation = "";
        UndoableEdit[] undoableEdits = undoManager.getUndoableEdits();
        if (undoableEdits.length > 0) {
            UndoableEdit undo = undoableEdits[0];
            undoPresentation = getUndoablePresentation(undo);
        }
        undoAction.putValue(Action.NAME,
                Res.getString("ACTION_UNDO") + " " + undoPresentation);

        UndoableEdit[] redoableEdits = undoManager.getRedoableEdits();
        if (redoableEdits.length > 0) {
            UndoableEdit redo = redoableEdits[0];
            redoPresentation = getUndoablePresentation(redo);
        }
        redoAction.putValue(Action.NAME,
                Res.getString("ACTION_REDO") + " " + redoPresentation);
    }

    private String getUndoablePresentation(UndoableEdit undoable) {
        String presentation = "";
        if (undoable instanceof UndoableNodeMoved) {
            presentation = Res.getString("MOVE_EDIT");
        } else if (undoable instanceof UndoableCellEdit) {
            presentation = Res.getString("CELL_EDIT");
        } else if (undoable instanceof UndoableDeleteBookmark) {
            presentation = Res.getString("ACTION_DELETE");
        } else if (undoable instanceof UndoableSetDestination) {
            presentation = Res.getString("ACTION_DEST_FROM_VIEW");
        } else if (undoable instanceof UnboablePageOffset) {
            presentation = Res.getString("UNDOABLE_OFFSET");
        } else if (undoable instanceof UndoableLoadBookmarks) {
            presentation = Res.getString("UNDOABLE_LOAD_BOOKMARKS");
        }
        return presentation;
    }

    private class RecentFileListener implements ActionListener {

        private File f;

        public RecentFileListener(File f) {
            this.f = f;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!askCloseWithoutSave()) {
                return;
            }
            fileOperator.close();

            if (f != null && f.isFile()) {
                //close();
                openFileAsync(f, null);
            } else {
                showErrorMessage(Res.getString("ERROR_OPENING_FILE") + " " + f.getName());
            }
        }
    }

    private void createRecentFilesItems() {
        openRecent.removeAll();
        String[] paths = userPrefs.getRecentFiles();
        JMenuItem item;
        for (String path : paths) {
            if (!path.equals("")) {
                File f = new File(path);
                if (f.exists()) {
                    item = new JMenuItem(f.getName());
                    item.setToolTipText(f.getAbsolutePath());
                    item.addActionListener(new RecentFileListener(f));
                    openRecent.add(item);
                }
            }
        }

    }

    private JMenuBar createMenus() {
        JMenuBar menuBar = new JMenuBar();

        JMenuItem item;

        JMenu menuFile = new JMenu(Res.getString("MENU_FILE"));
        menuFile.setMnemonic(Res.mnemonicFromRes("MENU_FILE_MNEMONIC"));
        item = menuFile.add(openAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_OPEN_MNEMONIC"));

        item = menuFile.add(openLinkedPdf);
        item.setMnemonic(Res.mnemonicFromRes("MENU_OPEN_LINKED_PDF_MENMONIC"));

        openRecent = new JMenu(Res.getString("MENU_OPEN_RECENT"));
        openRecent.setMnemonic(Res.mnemonicFromRes("MENU_OPEN_RECENT_MNEMONIC"));
        createRecentFilesItems();
        menuFile.add(openRecent);

        item = menuFile.add(saveAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_SAVE_MNEMONIC"));
        item = menuFile.add(saveAsAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_SAVE_AS_MNEMONIC"));
        item = menuFile.add(closeAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_CLOSE_MNEMONIC"));

        menuFile.addSeparator();

        item = menuFile.add(quitAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_QUIT_MNEMONIC"));
        menuBar.add(menuFile);

        JMenu menuEdit = new JMenu(Res.getString("MENU_EDIT"));
        menuEdit.setMnemonic(Res.mnemonicFromRes("MENU_EDIT_MNEMONIC"));
        item = menuEdit.add(undoAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_UNDO_MNEMONIC"));
        item = menuEdit.add(redoAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_REDO_MNEMONIC"));
        menuEdit.addSeparator();
        menuEdit.add(cutAction);
        menuEdit.add(copyAction);
        menuEdit.add(pasteAction);
        menuEdit.add(copyBookmarkFromViewAction);
        menuEdit.addSeparator();
        item = menuEdit.add(addSiblingAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_ADD_SIBLING_MNEMONIC"));
        item = menuEdit.add(addChildAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_ADD_CHILD_MNEMONIC"));
        menuEdit.addSeparator();
        item = menuEdit.add(renameAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_RENAME_MNEMONIC"));
        item = menuEdit.add(deleteAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_DELETE_MNEMONIC"));
        menuEdit.addSeparator();
        cbEditMenuBold = new JCheckBoxMenuItem(setBoldAction);
        cbEditMenuBold.setMnemonic(Res.mnemonicFromRes("MENU_SET_BOLD_MNEMONIC"));
        menuEdit.add(cbEditMenuBold);
        cbEditMenuItalic = new JCheckBoxMenuItem(setItalicAction);
        cbEditMenuItalic.setMnemonic(Res.mnemonicFromRes("MENU_SET_ITALIC_MNEMONIC"));
        menuEdit.add(cbEditMenuItalic);
        item = menuEdit.add(changeColorAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_CHANGE_COLOR_MNEMONIC"));
        menuEdit.addSeparator();
        item = menuEdit.add(addWebLinkAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_ADD_WEB_LINK_MNEMONIC"));
        item = menuEdit.add(addLaunchLinkAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_ADD_LAUNCH_LINK_MNEMONIC"));
        item = menuEdit.add(setDestFromViewAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_SET_DESTINATION_MNEMONIC"));
        item = menuEdit.add(showActionsDialog);
        item.setMnemonic(Res.mnemonicFromRes("MENU_ACTIONS_DIALOG_MNEMONIC"));
        menuBar.add(menuEdit);

        JMenu menuView = new JMenu(Res.getString("MENU_VIEW"));
        menuView.setMnemonic(Res.mnemonicFromRes("MENU_VIEW_MNEMONIC"));
        item = menuView.add(goFirstPageAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_GO_FIRST_MNEMONIC"));
        item = menuView.add(goPreviousPageAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_GO_PREV_MNEMONIC"));
        item = menuView.add(goNextPageAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_GO_NEXT_MNEMONIC"));
        item = menuView.add(goLastPageAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_GO_LAST_MNEMONIC"));
        item = menuView.add(goToPageAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_GO_TO_PAGE_MNEMONIC"));
        menuView.addSeparator();

        zoomMenuItemsGroup = new ButtonGroup();
        rbFitWidth = new JRadioButtonMenuItem(fitWidthAction);
        rbFitWidth.setMnemonic(Res.mnemonicFromRes("MENU_FIT_WIDTH_MNEMONIC"));
        menuView.add(rbFitWidth);
        zoomMenuItemsGroup.add(rbFitWidth);

        rbFitHeight = new JRadioButtonMenuItem(fitHeightAction);
        rbFitHeight.setMnemonic(Res.mnemonicFromRes("MENU_FIT_HEIGHT_MNEMONIC"));
        menuView.add(rbFitHeight);
        zoomMenuItemsGroup.add(rbFitHeight);

        rbFitPage = new JRadioButtonMenuItem(fitPageAction);
        rbFitPage.setMnemonic(Res.mnemonicFromRes("MENU_FIT_PAGE_MNEMONIC"));
        menuView.add(rbFitPage);
        zoomMenuItemsGroup.add(rbFitPage);

        rbFitNative = new JRadioButtonMenuItem(fitNativeAction);
        rbFitNative.setMnemonic(Res.mnemonicFromRes("MENU_FIT_NATIVE_MNEMONIC"));
        menuView.add(rbFitNative);
        zoomMenuItemsGroup.add(rbFitNative);

        rbTopLeftZoom = new JRadioButtonMenuItem(topLeftZoomAction);
        rbTopLeftZoom.setMnemonic(Res.mnemonicFromRes("MENU_TOP_LEFT_ZOOM_MNEMONIC"));
        menuView.add(rbTopLeftZoom);
        zoomMenuItemsGroup.add(rbTopLeftZoom);

        rbFitRect = new JRadioButtonMenuItem(fitRectAction);
        rbFitRect.setMnemonic(Res.mnemonicFromRes("MENU_FIT_RECT_MNEMONIC"));
        menuView.add(rbFitRect);
        zoomMenuItemsGroup.add(rbFitRect);

        menuView.addSeparator();

        menuView.add(expandAllAction).setMnemonic(
                Res.mnemonicFromRes("MENU_EXPAND_ALL_MNEMONIC"));
        menuView.add(collapseAllAction).setMnemonic(
                Res.mnemonicFromRes("MENU_COLLAPSE_ALL_MNEMONIC"));

        menuView.addSeparator();
        JCheckBoxMenuItem viewLeftPanel =
                new JCheckBoxMenuItem(Res.getString("MENU_SHOW_NAVIGATION_PANEL"));
        viewLeftPanel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0));
        if (userPrefs.getCollapsingPanelState() == CollapsingPanel.PANEL_OPENED) {
            viewLeftPanel.setState(true);
        } else {
            viewLeftPanel.setState(false);
        }
        viewLeftPanel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int currentState = leftPanel.getPanelState();
                if (currentState == CollapsingPanel.PANEL_COLLAPSED) {
                    leftPanel.setPanelState(CollapsingPanel.PANEL_OPENED);
                } else {
                    leftPanel.setPanelState(CollapsingPanel.PANEL_COLLAPSED);
                }
            }
        });
        menuView.add(viewLeftPanel);
        leftPanelMenuGroup = new ButtonGroup();
        bookmarksButton = new JRadioButtonMenuItem(Res.getString("SHOW_BOOKMARKS"));
        bookmarksButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0));
        thumbnailsButton = new JRadioButtonMenuItem(Res.getString("SHOW_THUMBNAILS"));
        thumbnailsButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, InputEvent.SHIFT_DOWN_MASK));
        if (userPrefs.getPanelToShow().equals(Res.getString("THUMBNAILS_TAB_TITLE"))) {
            thumbnailsButton.setSelected(true);
        } else {
            bookmarksButton.setSelected(true);
        }
        bookmarksButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (leftPanel != null) {
                    leftPanel.selectPanelToShow(Res.getString("BOOKMARKS_TAB_TITLE"));
                }
            }
        });
        thumbnailsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (leftPanel != null) {
                    leftPanel.selectPanelToShow(Res.getString("THUMBNAILS_TAB_TITLE"));
                }
            }
        });
        leftPanelMenuGroup.add(bookmarksButton);
        leftPanelMenuGroup.add(thumbnailsButton);
        menuView.add(bookmarksButton);
        menuView.add(thumbnailsButton);

        menuBar.add(menuView);

        JMenu menuTools = new JMenu(Res.getString("MENU_TOOLS"));
        menuTools.setMnemonic(Res.mnemonicFromRes("MENU_TOOLS_MNEMONIC"));

        JMenu menuSetLAF = new JMenu(Res.getString("MENU_LAF"));
        menuSetLAF.setMnemonic(Res.mnemonicFromRes("MENU_LAF_MNEMONIC"));
        ButtonGroup group = new ButtonGroup();
        String currentLAF = UIManager.getLookAndFeel().getName();
        LookAndFeelInfo[] infoArray = UIManager.getInstalledLookAndFeels();
        for (LookAndFeelInfo info : infoArray) {
            JRadioButtonMenuItem rb = new JRadioButtonMenuItem(info.getName());
            group.add(rb);
            menuSetLAF.add(rb);
            if (currentLAF.equals(info.getName())) {
                rb.setSelected(true);
            }
            rb.addActionListener(new ActionListenerSetLAF(info.getClassName()));
        }
        //menuTools.add(menuSetLAF);

        cbSelectText = new JCheckBoxMenuItem(selectText);
        cbSelectText.setSelected(false);
        menuTools.add(cbSelectText);
        cbConnectToClipboard = new JCheckBoxMenuItem(connectToClipboard);
        cbConnectToClipboard.setSelected(false);
        menuTools.add(cbConnectToClipboard);

        menuTools.addSeparator();

        cbShowOnOpen = new JCheckBoxMenuItem(showOnOpenAction);
        cbShowOnOpen.setMnemonic(Res.mnemonicFromRes("MENU_SHOW_ON_OPEN_MNEMONIC"));
        menuTools.add(cbShowOnOpen);

        menuTools.addSeparator();

        item = menuTools.add(dumpAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_DUMP_MNEMONIC"));
        item = menuTools.add(loadAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_LOAD_MNEMONIC"));

        menuTools.addSeparator();

        item = menuTools.add(applyPageOffset);
        item.setMnemonic(Res.mnemonicFromRes("MENU_PAGE_OFFSET_MNEMONIC"));

        JCheckBoxMenuItem checkItem = new JCheckBoxMenuItem(Res.getString("MENU_CONVERT_NAMED_DEST"));
        checkItem.setToolTipText(Res.getString("MENU_CONVERT_NAMED_DEST_DESCR"));
        checkItem.setMnemonic(Res.mnemonicFromRes("MENU_CONVERT_NAMED_DEST_MNEMONIC"));
        checkItem.setSelected(userPrefs.getConvertNamedDestinations());
        checkItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
                userPrefs.setConvertNamedDestinations(item.isSelected());
                JOptionPane.showMessageDialog(JPdfBookmarksGui.this,
                        Res.getString("CONVERT_NAMED_DEST_MSG"), title,
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        //menuTools.add(checkItem);

        menuTools.addSeparator();

        item = menuTools.add(optionsDialogAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_OPTIONS_MNEMONIC"));

        menuBar.add(menuTools);

        JMenu menuWindow = new JMenu(Res.getString("MENU_WINDOW"));
        menuWindow.setMnemonic(Res.mnemonicFromRes("MENU_WINDOW_MNEMONIC"));
        menuWindow.add(menuSetLAF);
//        menuWindow.addSeparator();
//        JMenu menuShowMainToolbars = new JMenu(Res.getString("MENU_SHOW_MAIN_TOOLBARS"));
//        menuShowMainToolbars.setMnemonic(Res.mnemonicFromRes("MENU_SHOW_MAIN_TOOLBARS_MNEMONIC"));
//        menuWindow.add(menuShowMainToolbars);
//
//        JMenu menuShowBookmarksToolbars = new JMenu(Res.getString("MENU_SHOW_BOOKMARKS_TOOLBARS"));
//        menuShowBookmarksToolbars.setMnemonic(Res.mnemonicFromRes("MENU_SHOW_BOOKMARKS_TOOLBARS_MNEMONIC"));
//        menuWindow.add(menuShowBookmarksToolbars);

        menuBar.add(menuWindow);

        JMenu menuHelp = new JMenu(Res.getString("MENU_HELP"));
        menuHelp.setMnemonic(Res.mnemonicFromRes("MENU_HELP_MNEMONIC"));
        item = menuHelp.add(checkUpdatesAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_CHECK_UPDATES_MNEMONIC"));
        item = menuHelp.add(goToAuthorBlog);
        item.setMnemonic(Res.mnemonicFromRes("MENU_GO_TO_BLOG_MNEMONIC"));
        item = menuHelp.add(readOnlineManualAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_READ_MANUAL_MNEMONIC"));
        item = menuHelp.add(donateToProject);
        item.setMnemonic(Res.mnemonicFromRes("MENU_DONATE_MNEMONIC"));

        menuHelp.addSeparator();

        item = new JMenuItem(Res.getString("MENU_ABOUT_BOX") + " ...");
        item.setMnemonic(Res.mnemonicFromRes("MENU_ABOUT_BOX_MNEMONIC"));
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                AboutBox aboutBox = new AboutBox(JPdfBookmarksGui.this, true);
                aboutBox.setLocationRelativeTo(JPdfBookmarksGui.this);
                aboutBox.setVisible(true);
            }
        });
        menuHelp.add(item);

        menuBar.add(menuHelp);

        toolbarsPanelsMenu = new JPopupMenu();
        JMenuItem toolbarsManagerItem = new JMenuItem(Res.getString("TAB_TOOLBARS_MANAGER") + "...");
        toolbarsManagerItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                OptionsDlg optionsDlg = new OptionsDlg(JPdfBookmarksGui.this,
                        true);
                optionsDlg.setLocationRelativeTo(JPdfBookmarksGui.this);
                optionsDlg.setVisibleTab(OptionsDlg.TOOLBARS_PANEL);
                optionsDlg.setVisible(true);
            }
        });
        toolbarsPanelsMenu.add(toolbarsManagerItem);

        return menuBar;
    }

    private class ToolbarsPopupListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                toolbarsPanelsMenu.show(e.getComponent(),
                        e.getX(), e.getY());
            }
        }
    }

    private void createTreeMenu() {
        treeMenu = new JPopupMenu();

        JMenuItem item = treeMenu.add(addSiblingAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_ADD_SIBLING_MNEMONIC"));
        item = treeMenu.add(addChildAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_ADD_CHILD_MNEMONIC"));
        treeMenu.addSeparator();

//        ActionListener actionListener = new TransferActionListener();
//        cutMenuItem = new JMenuItem(Res.getString("ACTION_CUT"),
//                Res.getIcon(getClass(), "gfx16/edit-cut.png"));
//        cutMenuItem.setActionCommand((String) cutAction.getValue(Action.NAME));
//        cutMenuItem.addActionListener(actionListener);
//        cutMenuItem.setAccelerator(
//                KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
//        cutMenuItem.setEnabled(false);
//        treeMenu.add(cutMenuItem);
        treeMenu.add(cutAction);

//        copyMenuItem = new JMenuItem(Res.getString("ACTION_COPY"),
//                Res.getIcon(getClass(), "gfx16/edit-copy.png"));
//        copyMenuItem.setActionCommand((String) copyAction.getValue(Action.NAME));
//        copyMenuItem.addActionListener(actionListener);
//        copyMenuItem.setAccelerator(
//                KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
//        copyMenuItem.setEnabled(false);
//        treeMenu.add(copyMenuItem);
        treeMenu.add(copyAction);

//        pasteMenuItem = new JMenuItem(Res.getString("ACTION_PASTE"),
//                Res.getIcon(getClass(), "gfx16/edit-paste.png"));
//        pasteMenuItem.setActionCommand((String) pasteAction.getValue(Action.NAME));
//        pasteMenuItem.addActionListener(actionListener);
//        pasteMenuItem.setAccelerator(
//                KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
//        pasteMenuItem.setEnabled(false);
//        treeMenu.add(pasteMenuItem);
        treeMenu.add(pasteAction);
        //       flavorsChanged();

        item = treeMenu.add(renameAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_RENAME_MNEMONIC"));
        item = treeMenu.add(deleteAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_DELETE_MNEMONIC"));
        treeMenu.addSeparator();
        cbBold = new JCheckBoxMenuItem(setBoldAction);
        cbBold.setMnemonic(Res.mnemonicFromRes("MENU_SET_BOLD_MNEMONIC"));
        treeMenu.add(cbBold);
        cbItalic = new JCheckBoxMenuItem(setItalicAction);
        cbItalic.setMnemonic(Res.mnemonicFromRes("MENU_SET_ITALIC_MNEMONIC"));
        treeMenu.add(cbItalic);
        item = treeMenu.add(changeColorAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_CHANGE_COLOR_MNEMONIC"));
        treeMenu.addSeparator();
        item = treeMenu.add(addWebLinkAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_ADD_WEB_LINK_MNEMONIC"));
        item = treeMenu.add(addLaunchLinkAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_ADD_LAUNCH_LINK_MNEMONIC"));
        item = treeMenu.add(setDestFromViewAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_SET_DESTINATION_MNEMONIC"));
        item = treeMenu.add(showActionsDialog);
        item.setMnemonic(Res.mnemonicFromRes("MENU_ACTIONS_DIALOG_MNEMONIC"));
    }

    private class ActionListenerSetLAF implements ActionListener {

        private String laf;

        public ActionListenerSetLAF(String lookAndFeelClass) {
            super();
            laf = lookAndFeelClass;
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            Ut.changeLAF(laf, JPdfBookmarksGui.this);
            leftPanel.updateComponentsUI();
//            Ut.changeLAF(laf, cardsContainer);
//            Ut.changeLAF(laf, openLeftPanelContainer);
            lblInheritLeft.setUI(new VerticalLabelUI(false));
//            JOptionPane.showMessageDialog(JPdfBookmarksGui.this,
//                    Res.getString("LAF_CHANGED_RESTART_MANUALLY"));
            userPrefs.setLAF(laf);
            bookmarksTree.updateTree(JPdfBookmarksGui.this.mouseAdapter);
        }
    }

    private JPanel createToolbarsPanel() {


        JToolBar fileToolbar = new JToolBar();
        fileToolbar.add(openAction);
        fileToolbar.add(openLinkedPdf);
        fileToolbar.add(saveAction);
        fileToolbar.add(saveAsAction);
        fileToolbar.add(closeAction);
        mainToolbars.put(Prefs.SHOW_FILE_TB, fileToolbar);

        JToolBar undoToolbar = new JToolBar();
        undoToolbar.add(undoAction);
        undoToolbar.add(redoAction);
        undoToolbar.addSeparator();
        undoToolbar.add(cutAction);
        undoToolbar.add(copyAction);
        undoToolbar.add(pasteAction);
        undoToolbar.add(copyBookmarkFromViewAction);
        mainToolbars.put(Prefs.SHOW_UNDO_TB, undoToolbar);

        navigationToolbar = new JToolBar();
        JButton btn = navigationToolbar.add(goFirstPageAction);
        btn = navigationToolbar.add(goPreviousPageAction);
        mainToolbars.put(Prefs.SHOW_NAVIGATION_TB, navigationToolbar);

        txtGoToPage = new IntegerTextField(4);
        txtGoToPage.setText("0");
        txtGoToPage.setEnabled(false);
        txtGoToPage.setHorizontalAlignment(JTextField.CENTER);
        txtGoToPage.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                viewPanel.goToPage(txtGoToPage.getInteger());
            }
        });
        navigationToolbar.add(txtGoToPage);
        lblPageOfPages = new JLabel(String.format(" / %5d", numPages));
        lblPageOfPages.setEnabled(false);
        navigationToolbar.add(lblPageOfPages);

        btn = navigationToolbar.add(goNextPageAction);
        btn = navigationToolbar.add(goLastPageAction);

        JToolBar fitTypeToolbar = new JToolBar();
        mainToolbars.put(Prefs.SHOW_FITTYPE_TB, fitTypeToolbar);
        zoomButtonsGroup = new ButtonGroup();

        tbFitWidth = new JToggleButton(fitWidthAction);
        tbFitWidth.setText("");
        fitTypeToolbar.add(tbFitWidth);
        zoomButtonsGroup.add(tbFitWidth);

        tbFitHeight = new JToggleButton(fitHeightAction);
        tbFitHeight.setText("");
        fitTypeToolbar.add(tbFitHeight);
        zoomButtonsGroup.add(tbFitHeight);

        tbFitPage = new JToggleButton(fitPageAction);
        tbFitPage.setText("");
        fitTypeToolbar.add(tbFitPage);
        zoomButtonsGroup.add(tbFitPage);

        tbFitNative = new JToggleButton(fitNativeAction);
        tbFitNative.setText("");
        fitTypeToolbar.add(tbFitNative);
        zoomButtonsGroup.add(tbFitNative);

        tbTopLeftZoom = new JToggleButton(topLeftZoomAction);
        tbTopLeftZoom.setText("");
        fitTypeToolbar.add(tbTopLeftZoom);
        zoomButtonsGroup.add(tbTopLeftZoom);

        tbFitRect = new JToggleButton(fitRectAction);
        tbFitRect.setText("");
        fitTypeToolbar.add(tbFitRect);
        zoomButtonsGroup.add(tbFitRect);

        JToolBar zoomToolbar = new JToolBar();
        mainToolbars.put(Prefs.SHOW_ZOOM_TB, zoomToolbar);
        btn = zoomToolbar.add(zoomInAction);
        txtZoom = new IntegerTextField(4);
        txtZoom.setText("0");
        txtZoom.setEnabled(false);
        txtZoom.setHorizontalAlignment(JTextField.CENTER);
        txtZoom.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                float scale = txtZoom.getInteger() / 100f;
                viewPanel.setTopLeftZoom(-1, -1, scale);
            }
        });
        zoomToolbar.add(txtZoom);
        lblPercent = new JLabel(" % ");
        lblPercent.setEnabled(false);
        zoomToolbar.add(lblPercent);
        btn = zoomToolbar.add(zoomOutAction);

        JToolBar othersToolbar = new JToolBar();
        mainToolbars.put(Prefs.SHOW_OTHERS_TB, othersToolbar);
        tbSelectText = new JToggleButton(selectText);
        tbSelectText.setText("");
        othersToolbar.add(tbSelectText);
        tbConnectToClipboard = new JToggleButton(connectToClipboard);
        tbConnectToClipboard.setText("");
        othersToolbar.add(tbConnectToClipboard);
        othersToolbar.addSeparator();
        tbShowOnOpen = new JToggleButton(showOnOpenAction);
        tbShowOnOpen.setText("");
        othersToolbar.add(tbShowOnOpen);
        othersToolbar.add(dumpAction);
        othersToolbar.add(loadAction);
        othersToolbar.add(applyPageOffset);
        othersToolbar.add(extractLinks);

        JToolBar webToolbar = new JToolBar();
        mainToolbars.put(Prefs.SHOW_WEB_TB, webToolbar);
        webToolbar.add(checkUpdatesAction);
        webToolbar.add(goToAuthorBlog);
        webToolbar.add(readOnlineManualAction);
        btn = webToolbar.add(donateToProject);
        btn.setVerticalTextPosition(SwingConstants.CENTER);
        btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        btn.setText(btn.getToolTipText());

        mainToolbarsPanel.add(fileToolbar);
        mainToolbarsPanel.add(undoToolbar);
        mainToolbarsPanel.add(fitTypeToolbar);
        mainToolbarsPanel.add(zoomToolbar);
        mainToolbarsPanel.add(navigationToolbar);
        mainToolbarsPanel.add(othersToolbar);
        mainToolbarsPanel.add(webToolbar);

        mainToolbarsPanel.addMouseListener(toolbarsPopupListener);

        return mainToolbarsPanel;
    }

    public void updateToolbars() {
        for (Map.Entry<String, JToolBar> e : mainToolbars.entrySet()) {
            JToolBar toolbar = e.getValue();
            String prefsKey = e.getKey();
            toolbar.setVisible(userPrefs.getShowToolbar(prefsKey));
        }

        boolean foundVisibleToolbar = false;
        for (JToolBar toolbar : mainToolbars.values()) {
            if (toolbar.isVisible()) {
                foundVisibleToolbar = true;
                break;
            }
        }
        mainToolbarsPanel.setVisible(foundVisibleToolbar);

        for (Map.Entry<String, JToolBar> e : bookmarksToolbars.entrySet()) {
            JToolBar toolbar = e.getValue();
            String prefsKey = e.getKey();
            toolbar.setVisible(userPrefs.getShowToolbar(prefsKey));
        }

        foundVisibleToolbar = false;
        for (JToolBar toolbar : bookmarksToolbars.values()) {
            if (toolbar.isVisible()) {
                foundVisibleToolbar = true;
                break;
            }
        }
        bookmarksToolbarsPanel.setVisible(foundVisibleToolbar);

    }

    private JPanel createStatusBar() {
        JPanel statusPanel = new JPanel(new GridLayout(1, 4));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        busyPanel = Box.createHorizontalBox();
        busyPanel.setBorder(BorderFactory.createEtchedBorder());
        lblStatus = new JLabel(" ");
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        busyPanel.add(lblStatus);
        busyPanel.add(Box.createHorizontalGlue());
        statusPanel.add(busyPanel, 0);

        lblMouseOverNode = new JLabel(" ");
        lblMouseOverNode.setBorder(BorderFactory.createEtchedBorder());
        statusPanel.add(lblMouseOverNode, 1);

        lblSelectedNode = new JLabel(" ");
        lblSelectedNode.setBorder(BorderFactory.createEtchedBorder());
        statusPanel.add(lblSelectedNode, 2);

        lblCurrentView = new JLabel(" ");
        lblCurrentView.setBorder(BorderFactory.createEtchedBorder());
        statusPanel.add(lblCurrentView, 3);
        return statusPanel;
    }

    private void setEmptyBookmarksTree() {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(
                Res.getString("NO_PDF_LOADED"));
        bookmarksTreeModel = new DefaultTreeModel(top);
        bookmarksTree = new BookmarksTree();
        bookmarksTree.setToggleClickCount(0);

//        bookmarksTree.setTransferHandler(new BookmarksTransferHandler());

        ActionMap map = bookmarksTree.getActionMap();
        map.put(TransferHandler.getCutAction().getValue(Action.NAME),
                TransferHandler.getCutAction());
        map.put(TransferHandler.getCopyAction().getValue(Action.NAME),
                TransferHandler.getCopyAction());
        map.put(TransferHandler.getPasteAction().getValue(Action.NAME),
                TransferHandler.getPasteAction());

//        InputMap imap = bookmarksTree.getInputMap();
//        imap.put(KeyStroke.getKeyStroke("ctrl X"),
//                TransferHandler.getCutAction().getValue(Action.NAME));
//        imap.put(KeyStroke.getKeyStroke("ctrl C"),
//                TransferHandler.getCopyAction().getValue(Action.NAME));
//        imap.put(KeyStroke.getKeyStroke("ctrl V"),
//                TransferHandler.getPasteAction().getValue(Action.NAME));


//        bookmarksTree.setDragEnabled(true);

        bookmarksTree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        bookmarksTree.setShowsRootHandles(true);
        bookmarksTree.setRootVisible(true);
        JPanel bookmarksScrollerPanel = new JPanel(new BorderLayout());
        bookmarksScrollerPanel.add(bookmarksTree, BorderLayout.CENTER);
        //bookmarksScrollerPanel.add(bookmarksToolbarsPanel, BorderLayout.WEST);
        bookmarksScroller.setViewportView(bookmarksScrollerPanel);

        mouseAdapter = new MouseOverTree();
        bookmarksTree.addMouseMotionListener(mouseAdapter);
        bookmarksTree.addMouseListener(mouseAdapter);
        bookmarksTree.addKeyListener(new KeysOverTree());
        bookmarksTree.addTreeSelectionListener(this);
        bookmarksTree.addTreeExpansionListener(this);
        bookmarksTree.addTreeNodeMovedListener(this);
        bookmarksTree.getCellEditor().addCellEditorListener(this);
        //the extended undo manager recevies events from the tree and relaunch
        //to the gui with additional information
        bookmarksTree.addUndoableEditListener(undoManager);
        undoManager.addUndoableEditListener(this);
        bookmarksTree.setModel(bookmarksTreeModel);
        bookmarksTree.treeDidChange();
    }

    private JPanel createBookmarksPanel() {
        bookmarksPanel = new JPanel(new BorderLayout());
//        bookmarksPanel.setTransferHandler(new BookmarksTransferHandler());

        bookmarksScroller = new JScrollPane();
        setEmptyBookmarksTree();

        bookmarksPanel.add(bookmarksScroller, BorderLayout.CENTER);
        bookmarksToolbarsPanel.setLayout(new BoxLayout(bookmarksToolbarsPanel, BoxLayout.Y_AXIS));
        bookmarksPanel.add(bookmarksToolbarsPanel, BorderLayout.WEST);

        JToolBar addToolbar = new JToolBar(JToolBar.VERTICAL);
        bookmarksToolbars.put(Prefs.SHOW_ADD_TB, addToolbar);
        addToolbar.add(addSiblingAction);
        addToolbar.add(addChildAction);
        addToolbar.setMaximumSize(addToolbar.getPreferredSize());
//		addToolbar.add(setDestFromViewAction);
//		addToolbar.add(addWebLinkAction);

        JToolBar changeToolbar = new JToolBar(JToolBar.VERTICAL);
        bookmarksToolbars.put(Prefs.SHOW_CHANGE_TB, changeToolbar);
        changeToolbar.add(renameAction);
        changeToolbar.add(deleteAction);
        changeToolbar.setMaximumSize(changeToolbar.getPreferredSize());

//        JToolBar undoToolbar = new JToolBar(JToolBar.VERTICAL);
//        bookmarksToolbars.put(Prefs.SHOW_UNDO_TB, undoToolbar);
//        undoToolbar.add(undoAction);
//        undoToolbar.add(redoAction);

        JToolBar styleToolbar = new JToolBar(JToolBar.VERTICAL);
        bookmarksToolbars.put(Prefs.SHOW_STYLE_TB, styleToolbar);
        tbBold = new JToggleButton(setBoldAction);
        tbBold.setText("");
        styleToolbar.add(tbBold);
        tbItalic = new JToggleButton(setItalicAction);
        tbItalic.setText("");
        styleToolbar.add(tbItalic);
        styleToolbar.add(changeColorAction);
        styleToolbar.setMaximumSize(styleToolbar.getPreferredSize());

        JToolBar setDestToolbar = new JToolBar(JToolBar.VERTICAL);
        bookmarksToolbars.put(Prefs.SHOW_SETDEST_TB, setDestToolbar);

        setDestToolbar.add(addWebLinkAction);
        setDestToolbar.add(addLaunchLinkAction);
        setDestToolbar.add(setDestFromViewAction);
        setDestToolbar.add(showActionsDialog);
        setDestToolbar.setMaximumSize(setDestToolbar.getPreferredSize());

        bookmarksToolbarsPanel.add(addToolbar);
        bookmarksToolbarsPanel.add(changeToolbar);
//        bookmarksToolbarsPanel.add(undoToolbar);
        bookmarksToolbarsPanel.add(styleToolbar);
        bookmarksToolbarsPanel.add(setDestToolbar);

        bookmarksToolbarsPanel.addMouseListener(toolbarsPopupListener);

        return bookmarksPanel;
    }
    private int dividerLocation;

    private void initComponents() {
        UIManager.put("Tree.leafIcon", Res.getIcon(getClass(), "gfx16/bookmark.png"));
        UIManager.put("Tree.openIcon", Res.getIcon(getClass(), "gfx16/bookmark.png"));
        UIManager.put("Tree.closedIcon", Res.getIcon(getClass(), "gfx16/bookmarks.png"));

        createActions();

        JMenuBar menuBar = createMenus();
        setJMenuBar(menuBar);

        createTreeMenu();

        JPanel toolbarsPanel = createToolbarsPanel();
        add(toolbarsPanel, BorderLayout.NORTH);

        centralSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);
        leftPanel = new LeftPanel(centralSplit);
        leftPanel.addBookmarksPanel(createBookmarksPanel());
        leftPanel.addThumbnailsPanel(createThumbnailsPanel());
        leftPanel.getComboBoxSelector().addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                String item = (String) e.getItem();
                if (item.equals(Res.getString("BOOKMARKS_TAB_TITLE"))) {
                    bookmarksButton.setSelected(true);
                } else {
                    thumbnailsButton.setSelected(true);
                }
            }
        });
        leftPanel.getComboBoxSelector().setSelectedItem(userPrefs.getPanelToShow());
//        leftPanel.addInnerPanel(createBookmarksPanel(), Res.getString("BOOKMARKS_TAB_TITLE"));
//        leftPanel.addInnerPanel(createThumbnailsPanel(), Res.getString("THUMBNAILS_TAB_TITLE"));

        JPanel centralPanel = new JPanel(new BorderLayout());
        centralPanel.add((Component) viewPanel, BorderLayout.CENTER);

        Color headersColor = new Color(230, 163, 4);
        JPanel verticalScrollbarHeader = new JPanel(
                new BorderLayout());
        verticalScrollbarHeader.setBackground(headersColor);
        String inheriTop = Res.getString("INHERIT_TOP");
//		JLabel lblInheritTop = new JLabel(inheriTop);
        checkInheritTop = new JCheckBox();
        checkInheritTop.setBorder(BorderFactory.createEmptyBorder(0, 2, 1, 2));
        checkInheritTop.setBackground(headersColor);
        checkInheritTop.setText(inheriTop);
        checkInheritTop.setMnemonic(Res.mnemonicFromRes("INHERIT_TOP_MNEMONIC"));
        checkInheritTop.setHorizontalTextPosition(SwingConstants.LEFT);
        checkInheritTop.setEnabled(false);
        verticalScrollbarHeader.add(checkInheritTop, BorderLayout.EAST);

        centralPanel.add(verticalScrollbarHeader, BorderLayout.NORTH);
//		lblInheritTop.setDisplayedMnemonic(Res.mnemonicFromRes("INHERIT_TOP_MNEMONIC"));
//		lblInheritTop.setLabelFor(checkInheritTop);

        //JPanel horizontalScrollbarHeader = new JPanel(new BoxLayout(this, WIDTH));
        Box horizontalScrollbarHeader = Box.createVerticalBox();
        horizontalScrollbarHeader.setOpaque(true);
        horizontalScrollbarHeader.setBackground(headersColor);
        horizontalScrollbarHeader.add(Box.createVerticalGlue());
        String inheriLeft = Res.getString("INHERIT_LEFT");
        lblInheritLeft = new VerticalLabel(inheriLeft, false);
        lblInheritLeft.setBackground(headersColor);
        horizontalScrollbarHeader.add(lblInheritLeft);
        horizontalScrollbarHeader.add(Box.createVerticalStrut(4));
        checkInheritLeft = new JCheckBox();
        checkInheritLeft.setBorder(BorderFactory.createEmptyBorder(0, 1, 3, 2));
        checkInheritLeft.setBackground(headersColor);
        checkInheritLeft.setEnabled(false);
        horizontalScrollbarHeader.add(checkInheritLeft);
        lblInheritLeft.setDisplayedMnemonic(Res.mnemonicFromRes("INHERIT_LEFT_MNEMONIC"));
        lblInheritLeft.setLabelFor(checkInheritLeft);
        lblInheritLeft.setEnabled(false);
        centralPanel.add(horizontalScrollbarHeader, BorderLayout.WEST);

//		JLabel lblInheritZoom = new JLabel(Res.getString("INHERIT_ZOOM"));
//		lblInheritZoom.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
//		verticalScrollbarHeader.add(lblInheritZoom, BorderLayout.WEST);
        checkInheritZoom = new JCheckBox();
        checkInheritZoom.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 2));
        checkInheritZoom.setBackground(headersColor);
        checkInheritZoom.setText(Res.getString("INHERIT_ZOOM"));
        checkInheritZoom.setHorizontalTextPosition(SwingConstants.RIGHT);
        checkInheritZoom.setMnemonic(
                Res.mnemonicFromRes("INHERIT_ZOOM_MNEMONIC"));
        checkInheritZoom.setEnabled(false);
        verticalScrollbarHeader.add(checkInheritZoom, BorderLayout.WEST);

//		lblInheritZoom.setDisplayedMnemonic(
//				Res.mnemonicFromRes("INHERIT_ZOOM_MNEMONIC"));
//		lblInheritZoom.setLabelFor(checkInheritZoom);

//        centralSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
//                false, leftTabbedPane, centralPanel);
//        centralSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
//                false, leftPanel, centralPanel);
        centralSplit.setLeftComponent(leftPanel);
        centralSplit.setRightComponent(centralPanel);
        centralSplit.setOneTouchExpandable(true);

        leftPanel.setDividerLocation(userPrefs.getSplitterLocation());
        leftPanel.setPanelState(userPrefs.getCollapsingPanelState());

        dropTarget = new DropTarget(centralSplit, new SplitDropListener());
        centralSplit.setDropTarget(dropTarget);
        //centralSplit.setTransferHandler(new FilesTransferHandler());
        //centralSplit.setDividerLocation(userPrefs.getSplitterLocation());
        add(centralSplit, BorderLayout.CENTER);

        add(createStatusBar(), BorderLayout.SOUTH);

        updateToolbars();
    }

    private void followBookmarkInView(Bookmark bookmarkToFollow) {
        if (bookmarkToFollow == null) {
            return;
        }

        bookmarksTree.setLastFollowedBookmark(bookmarkToFollow);

        lblSelectedNode.setText(Res.getString("SELECTED_BOOKMARK") + ": "
                + bookmarkToFollow.getDescription(userPrefs.getUseThousandths()));

        if (bookmarkToFollow.isRemoteDestination()) {
            Bookmark b = new Bookmark();
            b.cloneDestination(bookmarkToFollow);
            b.setRemoteDestination(false);
            File actualFile = fileOperator.getFile();
            try {
                File absoluteActualFile = actualFile.getAbsoluteFile();
//                File absoluteRemoteFile = new File(bookmarkToFollow.getRemoteFilePath()).getAbsoluteFile();
                File absoluteRemoteFile = Ut.createAbsolutePath(absoluteActualFile,
                        new File(bookmarkToFollow.getRemoteFilePath()));
                JPdfBookmarksGui gui = alreadyOpenedIn(absoluteRemoteFile.getCanonicalFile());
                if (gui != null) {
                    gui.requestFocus();
                    gui.followBookmarkInView(b);
                } else {
                    JPanel message = new JPanel();
                    message.setLayout(new BoxLayout(message, BoxLayout.Y_AXIS));
                    message.add(new JLabel(Res.getString("BOOKMARK_TO_REMOTE_FILE")));
                    message.add(Box.createRigidArea(new Dimension(0, 5)));
                    message.add(new JLabel(bookmarkToFollow.getDescription(true)));
                    message.add(Box.createRigidArea(new Dimension(0, 5)));
                    message.add(new JLabel(Res.getString("ASK_OPEN_IN_NEW_WINDOW")));
                    int response = JOptionPane.showConfirmDialog(this, message, title,
                            JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        String target = absoluteActualFile.getParent() + File.separator + bookmarkToFollow.getRemoteFilePath();
                        actualFile = new File(target);
                        String targetCanonical = actualFile.getCanonicalPath();
                        new JPdfBookmarks().launchNewGuiInstance(targetCanonical, b);
                    }
                }
            } catch (IOException ex) {
            }
        } else if (bookmarkToFollow.getType() == BookmarkType.Named) {
            followBookmarkInView(bookmarkToFollow.getNamedTarget());
        } else if (bookmarkToFollow.getType() == BookmarkType.Uri) {
            goToWebLinkAsking(bookmarkToFollow.getUri());
        } else if (bookmarkToFollow.getType() == BookmarkType.Launch) {
            launchFile(bookmarkToFollow.getFileToLaunch());
        } else if (bookmarkToFollow.getType() != BookmarkType.Unknown) {

            int destPage = bookmarkToFollow.getPageNumber();
            viewPanel.goToPage(destPage);

            switch (bookmarkToFollow.getType()) {
                case FitWidth:
                    checkInheritTop.setSelected(bookmarkToFollow.getTop() < 0);
                    viewPanel.setFitWidth(bookmarkToFollow.getTop());
                    break;
                case FitHeight:
                    checkInheritLeft.setSelected(bookmarkToFollow.getLeft() < 0);
                    viewPanel.setFitHeight(bookmarkToFollow.getLeft());
                    break;
                case FitPage:
                    viewPanel.setFitPage();
                    break;
                case FitRect:
                    viewPanel.setFitRect(bookmarkToFollow.getTop(), bookmarkToFollow.getLeft(),
                            bookmarkToFollow.getBottom(), bookmarkToFollow.getRight());
                    break;
                case TopLeft:
                case TopLeftZoom:
                    checkInheritTop.setSelected(bookmarkToFollow.getTop() < 0);
                    checkInheritLeft.setSelected(bookmarkToFollow.getLeft() < 0);
                    checkInheritZoom.setSelected(bookmarkToFollow.getZoom() <= 0);
                    viewPanel.setTopLeftZoom(bookmarkToFollow.getTop(),
                            bookmarkToFollow.getLeft(), bookmarkToFollow.getZoom());
                    break;
            }
        }

        if (!bookmarkToFollow.isRemoteDestination()) {
            for (Bookmark b : bookmarkToFollow.getChainedBookmarks()) {
                followBookmarkInView(b);
            }
        }

        bookmarksTree.repaint();
    }

    private JPdfBookmarksGui alreadyOpenedIn(File file) {
        JPdfBookmarksGui g = null;

        Window[] windows = Window.getOwnerlessWindows();
        for (Window w : windows) {
            if (w instanceof JPdfBookmarksGui) {
                JPdfBookmarksGui gui = (JPdfBookmarksGui) w;
                if (gui.fileOperator != null) {
                    File openedFile = gui.fileOperator.getFile();
                    if (openedFile != null && openedFile.equals(file)) {
                        g = gui;
                    }
                }
            }
        }
        return g;
    }

    private class MouseOverTree extends MouseAdapter {

        private TreePath mousePressedPath;

        public MouseOverTree() {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);

            mousePressedPath = bookmarksTree.getPathForLocation(e.getX(), e.getY());

            //On Linux is necessary to do this on mousePresed on Windows on mouseReleased
            if (e.isPopupTrigger()) {
                //if there are multiple bookmarks selected change selection only if over
                //a not selected bookmark
                TreePath[] paths = bookmarksTree.getSelectionPaths();
                if (mousePressedPath != null) {
                    boolean changeSelection = true;
                    if (paths != null) {
                        for (TreePath p : paths) {
                            if (p.equals(mousePressedPath)) {
                                changeSelection = false;
                            }
                        }
                    }
                    if (changeSelection) {
                        bookmarksTree.setSelectionPath(mousePressedPath);
                    }
                }
                treeMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            super.mouseExited(e);
            lblMouseOverNode.setText(" ");
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            super.mouseMoved(e);
            if ((e.getSource() instanceof MouseDraggableTree) == false) {
                return;
            }

            MouseDraggableTree tree = (MouseDraggableTree) e.getSource();
            TreePath overPath = tree.getPathForLocation(e.getX(), e.getY());
            if (overPath == null) {
                lblMouseOverNode.setText(" ");
                return;
            }
            Object obj = overPath.getLastPathComponent();
            if (obj != null && obj instanceof Bookmark) {
                Bookmark bookmark = (Bookmark) obj;
                //in case of multiple actions keep only first line of description for statusbar
                //which is the first action
                String bookmarksDescription = bookmark.getDescription(userPrefs.getUseThousandths());

                lblMouseOverNode.setText(Res.getString("MOUSE_OVER_BOOKMARK")
                        + ": " + bookmarksDescription);
            }
        }

        /* I use this instead of TreeSelectionListener to avoid changing view
         * while dragging */
        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);

            TreePath path = bookmarksTree.getPathForLocation(e.getX(), e.getY());
            TreePath[] paths = bookmarksTree.getSelectionPaths();

            if (e.isPopupTrigger()) {
                treeMenu.show(e.getComponent(), e.getX(), e.getY());
                //if there are multiple bookmarks selected change selection only if over
                //a not selected bookmark
                if (path != null) {
                    boolean changeSelection = true;
                    if (paths != null) {
                        for (TreePath p : paths) {
                            if (p.equals(path)) {
                                changeSelection = false;
                            }
                        }
                    }
                    if (changeSelection) {
                        bookmarksTree.setSelectionPath(path);
                    }
                }
            } else if (bookmarksTree.isDragging()) {
            } else if (path != null && !e.isControlDown() && !e.isAltDown()
                    && !e.isShiftDown() && !(e.getClickCount() < userPrefs.getNumClicks())) {
                Bookmark bookmark = null;
                try {
                    bookmark = (Bookmark) path.getLastPathComponent();
                } catch (ClassCastException exc) {
                }
                if (bookmark != null) {
                    followBookmarkInView(bookmark);
                }
            }
//            else {
//                if (e.isControlDown()) {
//                    if (bookmarksTree.isPathSelected(path)) {
//                        bookmarksTree.removeSelectionPath(path);
//                    } else {
//                        bookmarksTree.addSelectionPath(path);
//                    }
//                } else {
//                    bookmarksTree.setSelectionPath(path);
//                }
//            }
        }
    }

    private class KeysOverTree extends KeyAdapter {
        /* I use this instead of TreeSelectionListener to avoid changing view
         * while dragging */

        @Override
        public void keyReleased(KeyEvent e) {
            super.keyReleased(e);
            int code = e.getKeyCode();
            int[] triggers;
            if (userPrefs.getNumClicks() > 1) {
                triggers = new int[]{KeyEvent.VK_ENTER, KeyEvent.VK_SPACE};

            } else {
                triggers = new int[]{KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
                            KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_HOME,
                            KeyEvent.VK_END, KeyEvent.VK_PAGE_DOWN,
                            KeyEvent.VK_PAGE_UP};
            }

            for (int trigger : triggers) {
                if (trigger == code) {
                    TreePath path = bookmarksTree.getLeadSelectionPath();
                    Bookmark b = (Bookmark) path.getLastPathComponent();
//                    Bookmark b = (Bookmark) bookmarksTree.getLastSelectedPathComponent();
                    if (b != null) {
                        followBookmarkInView(b);
                    }
                    break;
                }
            }

            if (deleteAction.isEnabled() && code == KeyEvent.VK_DELETE) {
                delete();
            }

            if (e.isControlDown() && code == KeyEvent.VK_C) {
                if (copyAction.isEnabled()) {
                    copy(false);
                }
            }

            if (e.isControlDown() && code == KeyEvent.VK_V) {
                if (pasteAction.isEnabled()) {
                    paste();
                }
            }

            if (e.isControlDown() && code == KeyEvent.VK_X) {
                if (cutAction.isEnabled()) {
                    cut();
                }
            }
        }
    }

    private class SplitDropListener implements DropTargetListener {

        public SplitDropListener() {
        }

        @Override
        public void dragEnter(DropTargetDragEvent dtde) {
        }

        @Override
        public void dragOver(DropTargetDragEvent dtde) {
        }

        @Override
        public void dropActionChanged(DropTargetDragEvent dtde) {
        }

        @Override
        public void dragExit(DropTargetEvent dte) {
        }

        private java.util.List textURIListToFileList(String data) {
            java.util.List list = new java.util.ArrayList(1);
            for (java.util.StringTokenizer st = new java.util.StringTokenizer(data, "\r\n");
                    st.hasMoreTokens();) {
                String s = st.nextToken();
                if (s.startsWith("#")) {
                    continue;
                }
                try {
                    java.net.URI uri = new java.net.URI(s);
                    java.io.File file = new java.io.File(uri);
                    list.add(file);
                } catch (java.net.URISyntaxException e) {
                } catch (IllegalArgumentException e) {
                }
            }
            return list;
        }

        @Override
        public void drop(DropTargetDropEvent evt) {
            int action = evt.getDropAction();
            evt.acceptDrop(action);
            try {
                Transferable data = evt.getTransferable();
                DataFlavor uriListFlavor = null;
                try {
                    uriListFlavor = new DataFlavor("text/uri-list;class=java.lang.String");
                } catch (ClassNotFoundException e1) {
                }


                List filesList = null;
                if (data.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    filesList = (List) data.getTransferData(DataFlavor.javaFileListFlavor);

                } else if (data.isDataFlavorSupported(uriListFlavor)) {
                    String data1 = (String) data.getTransferData(uriListFlavor);
                    filesList = (List<File>) textURIListToFileList(data1);
                }

                if (filesList != null) {
                    Iterator i = filesList.iterator();
                    if (i.hasNext()) {
                        File f = (File) i.next();
                        if (!askCloseWithoutSave()) {
                            return;
                        }
                        fileOperator.close();

                        if (f != null && f.isFile()) {
                            //close();
                            openFileAsync(f, null);
                            return;
                        } else {
                            showErrorMessage(Res.getString("ERROR_OPENING_FILE") + " " + f.getName());
                            return;
                        }
                    }
                }
            } catch (UnsupportedFlavorException e) {
            } catch (IOException e) {
            } finally {
                evt.dropComplete(true);
            }
        }
    }
}
