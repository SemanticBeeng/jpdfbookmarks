/*
 * JPedalViewPanel.java
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

import it.flavianopetrocchi.utilities.Ut;
import java.awt.Adjustable;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;
import org.jpedal.grouping.PdfGroupingAlgorithms;
import org.jpedal.objects.PdfPageData;

public class JPedalViewPanel extends JScrollPane implements IPdfView {

    // <editor-fold defaultstate="collapsed" desc="Members">
    private static final int MIN_RECT_WIDTH = 100;
    private static final int MIN_RECT_HEIGHT = 100;
    private static final float MIN_SCALE = 0.001f;
    private static final float MAX_SCALE = 4.0f;
    private ArrayList<PageChangedListener> pageChangedListeners =
            new ArrayList<PageChangedListener>();
    private ArrayList<ViewChangedListener> viewChangedListeners =
            new ArrayList<ViewChangedListener>();
    private ArrayList<RenderingStartListener> renderingStartListeners =
            new ArrayList<RenderingStartListener>();
    private ArrayList<TextCopiedListener> textCopiedListeners =
            new ArrayList<TextCopiedListener>();
    private int top = -1;
    private int left = -1;
    private int bottom = -1;
    private int right = -1;
    private float scale = 1.0f;
    private int currentPage;
    private int oldPage = -2;
    private PdfRenderPanel rendererPanel;
    //private PdfRenderer rendererPanel;
    private FitType fitType = FitType.FitPage;
    private int numberOfPages;
    private PdfPageData pdfPageData;
    private boolean drawingComplete = true;
    //private Rectangle rect = null;
    private Rectangle drawingRect;
    private Rectangle rectInCropBox;
    //private Rectangle rectInMediaBox;
    private BufferedImage img, cloneImg;
    private float oldScale;
    volatile boolean painting = false;
    private PdfDecoder decoder;
    private int cropBoxX, cropBoxY;
    private int cropBoxWidth, cropBoxHeight;
    private int mediaBoxWidth, mediaBoxHeight;
    private Cursor rectRedCur, rectBlueCur;
    private Boolean textSelectionActive = false;
    private String copiedText;
    private Boolean connectToClipboard = false;// </editor-fold>
    JScrollBar vbar;

    @Override
    public void open(File file) throws Exception {
        if (decoder == null) {
            decoder = new PdfDecoder();
            decoder.setExtractionMode(PdfDecoder.TEXT);
            decoder.init(true);
        }
        byte[] fileBytes = Ut.getBytesFromFile(file);
        decoder.openPdfArray(fileBytes);
        pdfPageData = decoder.getPdfPageData();
        numberOfPages = decoder.getPageCount();
        updateCurrentPageBoxes();
    }

    @Override
    public void reopen(File file) throws Exception {
        int pageCurrentlyDisplayed = currentPage;
        close();
        currentPage = pageCurrentlyDisplayed;
        open(file);
    }

    @Override
    public void close() {
        if (decoder != null) {
            decoder.closePdfFile();
            decoder = null;
        }
        img = null;
        currentPage = 0;
        setCopiedText(null);
        rendererPanel.repaint();
    }

    public JPedalViewPanel() {
        vbar = getVerticalScrollBar();
        rendererPanel = new PdfRenderPanel();
        viewport.setBackground(Color.gray);
        setViewportView(rendererPanel);
        rendererPanel.addKeyListener(new PdfViewKeyListener());
        addMouseWheelListener(new PdfViewMouseWheelListener());
        addComponentListener(new ResizeListener());
        PdfDecoder.useTextExtraction();

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Point hotSpot = new Point(8, 7);
        Image image = Res.getIcon(getClass(), "gfx32/rect-red.png").getImage();
        rectRedCur = toolkit.createCustomCursor(image, hotSpot, "rect-red");
        image = Res.getIcon(getClass(), "gfx32/rect-blue.png").getImage();
        rectBlueCur = toolkit.createCustomCursor(image, hotSpot, "rect-blue");

    }

    class PdfViewMouseWheelListener implements MouseWheelListener {

        int oldValue = -1;

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            int newValue = vbar.getValue();
            if (newValue == oldValue) {
                scrollToAnotherPage();
                oldValue = -1;
            } else {
                oldValue = newValue;
            }
        }
    }

    private void scrollToAnotherPage() {
        Point location = rendererPanel.getLocation();
        int panelY = Math.abs(location.y);
        int panelHeight = rendererPanel.getSize().height;
        int viewportHeight = viewport.getSize().height;
        if (panelY == (panelHeight - viewportHeight)) {
            goToNextPage();
            vbar.setValue(vbar.getMinimum());
        } else if (panelY == 0) {
            goToPreviousPage();
            vbar.setValue(vbar.getMaximum());
        }
    }

    class PdfViewKeyListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            switch (key) {
                case KeyEvent.VK_PAGE_DOWN:
                case KeyEvent.VK_DOWN:
                    Point location = rendererPanel.getLocation();
                    int panelY = Math.abs(location.y);
                    int panelHeight = rendererPanel.getSize().height;
                    int viewportHeight = viewport.getSize().height;
                    if (panelY == (panelHeight - viewportHeight)) {
                        goToNextPage();
                        vbar.setValue(vbar.getMinimum());
                    }
                    break;
                case KeyEvent.VK_PAGE_UP:
                case KeyEvent.VK_UP:
                    location = rendererPanel.getLocation();
                    panelY = Math.abs(location.y);
                    if (panelY == 0) {
                        goToPreviousPage();
                        vbar.setValue(vbar.getMaximum());
                    }
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    public JPedalViewPanel(FitType fitType) {
        this();
        this.fitType = fitType;
    }

    @Override
    public void goToFirstPage() {
        goToPage(1);
    }

    @Override
    public void goToPreviousPage() {
        goToPage(currentPage);
    }

    @Override
    public void goToPage(int numPage) {
        boolean hasNext = true;
        boolean hasPrevious = true;

        if (numberOfPages == 1) {
            currentPage = 0;
            hasPrevious = false;
            hasNext = false;
        } else if (numPage <= 1) {
            currentPage = 0;
            hasPrevious = false;
        } else if (numPage >= numberOfPages) {
            currentPage = numberOfPages - 1;
            hasNext = false;
        } else {
            currentPage = numPage - 1;
        }

        updateCurrentPageBoxes();

        rendererPanel.repaint();

        firePageChangedEvent(new PageChangedEvent(this, currentPage + 1, hasNext,
                hasPrevious));
    }

    private void updateCurrentPageBoxes() {
        if (pdfPageData == null) {
            return;
        }
        int p = currentPage + 1; //JPedal is one based counting pages
        cropBoxX = pdfPageData.getCropBoxX(p);
        cropBoxY = pdfPageData.getCropBoxY(p);
        cropBoxWidth = pdfPageData.getCropBoxWidth(p);
        cropBoxHeight = pdfPageData.getCropBoxHeight(p);
        mediaBoxWidth = pdfPageData.getMediaBoxWidth(p);
        mediaBoxHeight = pdfPageData.getMediaBoxHeight(p);
    }

    @Override
    public void goToNextPage() {
        goToPage(currentPage + 2);
    }

    @Override
    public void goToLastPage() {
        goToPage(numberOfPages);
    }

//    public void goToBookmark(Bookmark bookmark) {
//        int pageNum = bookmark.getPageNumber();
//        goToPage(pageNum);
//    }
    @Override
    public void setFitWidth(int top) {
        this.top = top;
        setFit(FitType.FitWidth);
    }

    @Override
    public void setFitHeight(int left) {
        this.left = left;
        setFit(FitType.FitHeight);
    }

    @Override
    public void setFitPage() {
        setFit(FitType.FitPage);
    }

    @Override
    public void setFitRect(Rectangle rect) {
        if (rect == null) {
            rectInCropBox = new Rectangle(0, 0, cropBoxWidth, cropBoxHeight);
        } else {
            rectInCropBox = rect;
        }
        drawingComplete = true;
        setFit(FitType.FitRect);
    }

    @Override
    public void setFitRect(int top, int left, int bottom, int right) {
        drawingComplete = true;
//        rectInMediaBox = new Rectangle(left, top,
//                Math.abs(right - left), Math.abs(bottom - top));
        rectInCropBox = new Rectangle(left - cropBoxX, mediaBoxHeight - top - cropBoxY,
                Math.abs(right - left), Math.abs(bottom - top));
        setFit(FitType.FitRect);
    }

    @Override
    public void setTopLeftZoom(int top, int left, float zoom) {
        this.left = left;
        this.top = top;
        if (zoom > MIN_SCALE) {
            this.scale = zoom;
        }
//        setScale(zoom);
        setFit(FitType.TopLeftZoom);
    }

    public void setScale(float zoom) {
//        if (zoom < MIN_SCALE) {
//            this.scale = MIN_SCALE;
//        } else if (zoom > MAX_SCALE) {
//            this.scale = MAX_SCALE;
//        } else {
//            scale = zoom;
//        }

        scale = zoom;
    }

    @Override
    public int getNumPages() {
        return numberOfPages;
    }

    @Override
    public int getCurrentPage() {
        return currentPage + 1;
    }

    @Override
    public FitType getFitType() {
        return fitType;
    }

    @Override
    public void addPageChangedListener(PageChangedListener listener) {
        pageChangedListeners.add(listener);
    }

    @Override
    public void removePageChangedListener(PageChangedListener listener) {
        pageChangedListeners.remove(listener);
    }

    @Override
    public void setFitNative() {
        setFit(FitType.FitNative);
    }

    public void setFit(FitType fitType) {
        this.fitType = fitType;
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                if (JPedalViewPanel.this.fitType == FitType.FitRect) {
//                    viewport.setCursor(Cursor.getPredefinedCursor(
//                            Cursor.CROSSHAIR_CURSOR));
                    if (!textSelectionActive) {
                        viewport.setCursor(rectRedCur);
                    }
                } else {
                    viewport.setCursor(Cursor.getDefaultCursor());
                }
                calcScaleFactor();
                adjustPreferredSize();
                adjustViewportPosition();
            }
        });
        rendererPanel.repaint();
    }

    public void setTextSelectionMode(boolean set) {
        if (set) {
            viewport.setCursor(rectBlueCur);
        } else {
            copiedText = null;
            if (fitType == FitType.FitRect) {
                viewport.setCursor(rectRedCur);
            } else {
                viewport.setCursor(Cursor.getDefaultCursor());
            }
        }

        textSelectionActive = set;
    }

    @Override
    public void setConnectToClipboard(Boolean set) {
        connectToClipboard = set;
    }

    private void movePanel(int xmove, int ymove) {
        Point pt = viewport.getViewPosition();
        pt.x = xmove;
        pt.y = ymove;

        pt.x = Math.max(0, pt.x);
        pt.x = Math.min(getMaxXExtent(), pt.x);
        pt.y = Math.max(0, pt.y);
        pt.y = Math.min(getMaxYExtent(), pt.y);

        viewport.setViewPosition(pt);
    }

    private int getMaxXExtent() {
        return viewport.getView().getWidth() - viewport.getWidth();
    }

    private int getMaxYExtent() {
        return viewport.getView().getHeight() - viewport.getHeight();
    }

    private void calcScaleFactor() {
        //float zoom = 1f;
        switch (fitType) {
            case FitWidth:
                scale = (float) viewport.getWidth() /
                        cropBoxWidth;
                break;
            case FitHeight:
                scale = (float) viewport.getHeight() /
                        cropBoxHeight;
                break;
            case FitNative:
                scale = 1.0f;
                break;
            case FitRect:
                if (rectInCropBox != null && drawingComplete && !textSelectionActive) {
                    float scaleWidth = (float) viewport.getWidth() /
                            rectInCropBox.width;
                    float scaleHeight = (float) viewport.getHeight() /
                            rectInCropBox.height;
                    scale = Math.min(scaleWidth, scaleHeight);
                }
                break;
            case FitPage:
                float scaleWidth = (float) viewport.getWidth() /
                        cropBoxWidth;
                float scaleHeight = (float) viewport.getHeight() /
                        cropBoxHeight;
                scale = Math.min(scaleWidth, scaleHeight);
                break;
        }
        //setScale(zoom);
    }

    private void adjustPreferredSize() {
        rendererPanel.setSize(calcViewSize());
    }

    private static String getTextFromClipboard() {
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        try {
            if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                String text = (String) t.getTransferData(DataFlavor.stringFlavor);
                return text;
            }
        } catch (UnsupportedFlavorException e) {
        } catch (IOException e) {
        }
        return null;
    }

    @Override
    public Bookmark getBookmarkFromView() {
        Bookmark bookmark = new Bookmark();
        if (connectToClipboard) {
            String text = getTextFromClipboard();
            if (text != null) {
                bookmark.setTitle(text);
            }
        } else if (copiedText != null) {
            bookmark.setTitle(copiedText);
        }
        bookmark.setPageNumber(getCurrentPage());
        bookmark.setType(getFitType().convertToBookmarkType());
        Point pt = viewport.getViewPosition();
        switch (bookmark.getType()) {
            case FitWidth:
                bookmark.setTop(Math.round(
                        (cropBoxHeight - (pt.y / scale)) + cropBoxY));
                bookmark.setThousandthsTop(
                        Bookmark.thousandthsVertical(bookmark.getTop(),
                        mediaBoxHeight));
                break;
            case FitHeight:
                bookmark.setLeft(Math.round((pt.x / scale) + cropBoxX));
                bookmark.setThousandthsLeft(
                        Bookmark.thousandthsHorizontal(bookmark.getLeft(),
                        mediaBoxWidth));
                break;
            case TopLeftZoom:
                bookmark.setTop(Math.round(
                        (cropBoxHeight - (pt.y / scale)) + cropBoxY));
                bookmark.setThousandthsTop(
                        Bookmark.thousandthsVertical(bookmark.getTop(),
                        mediaBoxHeight));
                bookmark.setLeft(Math.round((pt.x / scale) + cropBoxX));
                bookmark.setThousandthsLeft(
                        Bookmark.thousandthsHorizontal(bookmark.getLeft(),
                        mediaBoxWidth));
                bookmark.setZoom(scale);
                break;
            case FitRect:
                if (rectInCropBox != null) {
                    //float f = drawingComplete ? 1.0f : scale;
                    Point p = rectInCropBox.getLocation();
                    Dimension d = rectInCropBox.getSize();
                    bookmark.setLeft(p.x + cropBoxX);
                    bookmark.setThousandthsLeft(
                            Bookmark.thousandthsHorizontal(bookmark.getLeft(),
                            mediaBoxWidth));
                    bookmark.setTop(mediaBoxHeight - (p.y + cropBoxY));
                    bookmark.setThousandthsTop(
                            Bookmark.thousandthsVertical(bookmark.getTop(),
                            mediaBoxHeight));
                    bookmark.setRight(p.x + d.width + cropBoxX);
                    bookmark.setThousandthsRight(
                            Bookmark.thousandthsHorizontal(bookmark.getRight(),
                            mediaBoxWidth));
                    bookmark.setBottom(mediaBoxHeight - (p.y + d.height + cropBoxY));
                    bookmark.setThousandthsBottom(
                            Bookmark.thousandthsVertical(bookmark.getBottom(),
                            mediaBoxHeight));
                }
                break;
        }
        return bookmark;
    }

    private Dimension calcViewSize() {
        float scaledCropBoxWidth = cropBoxWidth * scale;
        float scaledCropBoxHeight = cropBoxHeight * scale;

        int viewWidth = Math.max(Math.round(scaledCropBoxWidth),
                viewport.getWidth());
        int viewHeight = Math.max(Math.round(scaledCropBoxHeight),
                viewport.getHeight());

        switch (fitType) {
            case FitWidth:
                viewHeight = Math.round(scaledCropBoxHeight +
                        viewport.getHeight());
                break;
            case FitHeight:
                viewWidth = Math.round(scaledCropBoxWidth) +
                        viewport.getWidth();
                break;
            case FitPage:
                break;
            case FitNative:
            case FitRect:
            case TopLeftZoom:
                viewWidth = viewport.getWidth() +
                        Math.round(scaledCropBoxWidth);
                viewHeight = viewport.getHeight() +
                        Math.round(scaledCropBoxHeight);
                break;
        }

        return new Dimension(viewWidth, viewHeight);
    }

    private void adjustViewportPosition() {
        float scaledMediaBoxHeight = mediaBoxHeight * scale;
        float scaledCropBoxY = cropBoxY * scale;
        float scaledCropBoxX = cropBoxX * scale;

        switch (fitType) {
            case FitPage:
                movePanel(0, 0);
                break;
            case FitWidth:
                if (top != -1) {
                    float gap = (scaledMediaBoxHeight - top * scale) - scaledCropBoxY;
                    movePanel(0, Math.round(gap));
                }
                break;
            case FitHeight:
                if (left != -1) {
                    float gap = (left * scale) - scaledCropBoxX;
                    movePanel(Math.round(gap), 0);
                }
                break;
            case FitRect:
                if (rectInCropBox != null && drawingComplete) {
                    movePanel(Math.round(rectInCropBox.x * scale),
                            Math.round(rectInCropBox.y * scale));
                }
                break;
            case TopLeftZoom:
                int gapHeight;
                int gapWidth;
                Point pt = viewport.getViewPosition();
                if (top != -1) {
                    gapHeight = Math.round((scaledMediaBoxHeight - top * scale) - scaledCropBoxY);
                } else {
                    gapHeight = pt.y;
                }
                if (left != -1) {
                    gapWidth = Math.round((left * scale) - scaledCropBoxX);
                } else {
                    gapWidth = pt.x;
                }
                movePanel(gapWidth, gapHeight);
                break;
        }

    }

    private void firePageChangedEvent(PageChangedEvent e) {
        for (PageChangedListener listener : pageChangedListeners) {
            listener.pageChanged(e);
        }
    }

    private void fireViewChangedEvent(ViewChangedEvent e) {
        for (ViewChangedListener listener : viewChangedListeners) {
            listener.viewChanged(e);
        }
    }

    private void fireTextCopiedEvent(TextCopiedEvent e) {
        for (TextCopiedListener listener : textCopiedListeners) {
            listener.textCopied(e);
        }
    }

    private void fireRenderingStartEvent(RenderingStartEvent e) {
        for (RenderingStartListener listener : renderingStartListeners) {
            listener.renderingStart(e);
        }
    }

    @Override
    public void addViewChangedListener(ViewChangedListener listener) {
        viewChangedListeners.add(listener);
    }

    @Override
    public void removeViewChangedListener(ViewChangedListener listener) {
        viewChangedListeners.remove(listener);
    }

    @Override
    public void addRenderingStartListener(RenderingStartListener listener) {
        renderingStartListeners.add(listener);
    }

    @Override
    public void removeRenderingStartListener(RenderingStartListener listener) {
        renderingStartListeners.remove(listener);
    }

    private void setCopiedText(String text) {
        copiedText = text;

        if (text != null) {
            if (connectToClipboard) {
                StringSelection ss = new StringSelection(text);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
            }
        } else {
//            JOptionPane.showMessageDialog(this, Res.getString("ERROR_COPYING_TEXT"),
//                    JPdfBookmarks.APP_NAME,
//                    JOptionPane.ERROR_MESSAGE);
        }

        fireTextCopiedEvent(new TextCopiedEvent(this, text));
    }

    @Override
    public void addTextCopiedListener(TextCopiedListener listener) {
        textCopiedListeners.add(listener);
    }

    @Override
    public void removeTextCopiedListener(TextCopiedListener listener) {
        textCopiedListeners.remove(listener);
    }

    private class ResizeListener extends ComponentAdapter {

        @Override
        public void componentResized(ComponentEvent e) {
            super.componentResized(e);
        }
    }

    private class PdfRenderPanel extends JPanel implements Scrollable {

        public PdfRenderPanel() {
            setFocusable(true);
            PdfPanelMouseListener mouseListener = new PdfPanelMouseListener();
            addMouseListener(mouseListener);
            addMouseMotionListener(mouseListener);
            setBackground(Color.gray);
            setBorder(BorderFactory.createLoweredBevelBorder());
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (decoder == null || pdfPageData == null) {
                setPreferredSize(viewport.getSize());
                revalidate();
                return;
            }

            Graphics2D g2 = (Graphics2D) g;

            calcScaleFactor();

            if (oldScale != scale || currentPage != oldPage || img == null) {
                CursorToolkit.startWaitCursor(JPedalViewPanel.this);
                try {
                    decoder.setPageParameters(scale, currentPage + 1);
                    img = decoder.getPageAsImage(currentPage + 1);
                    oldScale = scale;
                    oldPage = currentPage;
                } catch (Exception e) {
                    JPdfBookmarks.printErrorForDebug(e);
                } finally {
                    CursorToolkit.stopWaitCursor(JPedalViewPanel.this);
                }
                if (fitType == FitType.FitRect || textSelectionActive) {
                    cloneImg = new BufferedImage(img.getWidth(),
                            img.getHeight(), img.getType());
                }
            }

            setPreferredSize(calcViewSize());
            revalidate();

            if (img != null) {
                if (fitType == FitType.FitRect || textSelectionActive) {
                    if (cloneImg == null) {
                        cloneImg = new BufferedImage(img.getWidth(),
                                img.getHeight(), img.getType());
                    }
                    Graphics2D g2CloneImg = (Graphics2D) cloneImg.getGraphics();
                    g2CloneImg.drawImage(img, 0, 0, null);
                    g2CloneImg.setStroke(new BasicStroke());
                    if (textSelectionActive) {
                        g2CloneImg.setColor(Color.blue);
                    } else {
                        g2CloneImg.setColor(Color.red);
                    }

                    if (drawingComplete == false) {
                        g2CloneImg.drawRect(drawingRect.x, drawingRect.y, drawingRect.width,
                                drawingRect.height);
                        if (textSelectionActive) {
                            g2CloneImg.setColor(new Color(0, 0, 255, 50));
                            g2CloneImg.fillRect(drawingRect.x, drawingRect.y, drawingRect.width,
                                    drawingRect.height);
                        }
                    } else {
                        if (!textSelectionActive) {
                            g2CloneImg.drawRect(Math.round(rectInCropBox.x * scale),
                                    Math.round(rectInCropBox.y * scale),
                                    Math.round(scale * rectInCropBox.width),
                                    Math.round(scale * rectInCropBox.height));
                        } else {
                            g2CloneImg.drawImage(img, 0, 0, this);
                        }
                    }

                    g2.drawImage(cloneImg, 0, 0, this);

                } else {
                    g2.drawImage(img, 0, 0, this);
                }
            }

            Bookmark bookmark = getBookmarkFromView();
            fireViewChangedEvent(new ViewChangedEvent(this, fitType, scale, bookmark));
        }

        private class PdfPanelMouseListener extends MouseAdapter {

            private int xDiff, yDiff;

            @Override
            public void mouseDragged(MouseEvent e) {
                if (fitType == FitType.FitRect || textSelectionActive) {
                    if (e.getX() < xDiff) {
                        drawingRect.setLocation(e.getX(), drawingRect.y);
                    }
                    if (e.getY() < yDiff) {
                        drawingRect.setLocation(drawingRect.x, e.getY());
                    }
                    int rectWidth = Math.abs(e.getX() - xDiff);
                    int rectHeight = Math.abs(e.getY() - yDiff);
                    drawingRect.setSize(rectWidth, rectHeight);
                    repaint();
                } else {
                    Point p = viewport.getViewPosition();
                    int newX = p.x - (e.getX() - xDiff);
                    int newY = p.y - (e.getY() - yDiff);

                    Dimension size = viewport.getPreferredSize();
                    int maxX = size.width - viewport.getWidth();
                    int maxY = size.height - viewport.getHeight();
                    if (newX < 0) {
                        newX = 0;
                    }
                    if (newX > maxX) {
                        newX = maxX;
                    }
                    if (newY < 0) {
                        newY = 0;
                    }
                    if (newY > maxY) {
                        newY = maxY;
                    }

                    viewport.setViewPosition(new Point(newX, newY));
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                requestFocusInWindow();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                xDiff = e.getX();
                yDiff = e.getY();
                if (fitType != FitType.FitRect && (!textSelectionActive)) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                } else {
                    drawingRect = new Rectangle();
                    drawingRect.setLocation(xDiff, yDiff);
                    drawingComplete = false;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (fitType == FitType.FitRect || textSelectionActive) {
                    drawingComplete = true;
                    rectInCropBox = new Rectangle(cropBoxX, cropBoxY,
                            cropBoxWidth, cropBoxHeight);

                    rectInCropBox.setLocation(Math.round(drawingRect.x / scale),
                            Math.round(drawingRect.y / scale));

                    if (textSelectionActive) {
                        rectInCropBox.setSize(Math.round(drawingRect.width / scale),
                                Math.round(drawingRect.height / scale));
                        setCopiedText(extractText(rectInCropBox));
                        //repaint(); //uncomment to remove the selection rect immediately
                    } else {
                        rectInCropBox.setSize(Math.round(drawingRect.width / scale),
                                Math.round(drawingRect.height / scale));
//                        int width = Math.max(MIN_RECT_WIDTH,
//                                Math.round(drawingRect.width / scale));
//                        int height = Math.max(MIN_RECT_HEIGHT,
//                                Math.round(drawingRect.height / scale));
//                        rectInCropBox.setSize(width, height);
                        setFitRect(rectInCropBox);
                    }
                } else {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }

        private String extractText(Rectangle rectInCrop) {
            String text = null;
            try {
                int page = currentPage + 1;
                decoder.decodePage(page);
                PdfGroupingAlgorithms currentGrouping = decoder.getGroupingObject();
                int x1 = rectInCrop.x + cropBoxX;
                int x2 = rectInCrop.x + rectInCrop.width + cropBoxX;
                int y1 = mediaBoxHeight - cropBoxY - rectInCrop.y;
                int y2 = mediaBoxHeight - cropBoxY - rectInCrop.y - rectInCrop.height;
                text = currentGrouping.extractTextInRectangle(x1, y1,
                        x2, y2, page, false, true);
            } catch (Exception ex) {
            }

            return text;
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect,
                int orientation, int direction) {
            return 20;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect,
                int orientation, int direction) {
            return 100;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return false;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
