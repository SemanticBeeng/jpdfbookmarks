/*
 * PdfRendererViewPanel.java
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

import java.nio.ByteBuffer;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import it.flavianopetrocchi.utilities.Ut;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;

public class PdfRendererViewPanel extends JScrollPane implements IPdfView {

    // <editor-fold defaultstate="collapsed" desc="Members">
    private ArrayList<PageChangedListener> pageChangedListeners =
            new ArrayList<PageChangedListener>();
    private ArrayList<ViewChangedListener> viewChangedListeners =
            new ArrayList<ViewChangedListener>();
    private ArrayList<RenderingStartListener> renderingStartListeners =
            new ArrayList<RenderingStartListener>();
    private int top = -1;
    private int left = -1;
    private int bottom = -1;
    private int right = -1;
    private float scale = 1.0f;
    private int currentPage;
    private int oldPage = -2;
    private PdfRenderPanel rendererPanel;
    private FitType fitType = FitType.FitPage;
    private int numberOfPages;
    private PDFPage currentPageObject;
    private boolean drawingComplete = true;
    private Rectangle rect = null;
    private BufferedImage img;
    private float oldScale;
    volatile boolean painting = false;
    private PDFFile document;// </editor-fold>

    @Override
    public void open(File file) throws Exception {
        //this locks the file
        //RandomAccessFile raf = new RandomAccessFile(file, "r");
        //FileChannel channel = raf.getChannel();
        //ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());

        //this instead should not lock the file
        byte[] fileBytes = Ut.getBytesFromFile(file);
        ByteBuffer fileBuffer = ByteBuffer.allocate(fileBytes.length);
        fileBuffer.put(fileBytes);
        document = new PDFFile(fileBuffer);
        numberOfPages = document.getNumPages();
    }

    @Override
    public void reopen(File file) throws Exception {
        close();
        open(file);
    }

    @Override
    public void close() {
        if (document != null) {
            document = null;
        }
        img = null;
        currentPage = 0;
        rendererPanel.repaint();
    }

    public PdfRendererViewPanel() {
//		setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
//		setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_ALWAYS);

        rendererPanel = new PdfRenderPanel();
        viewport.setBackground(Color.gray);
//		viewport.setBorder(BorderFactory.createLoweredBevelBorder());
        setViewportView(rendererPanel);
        addComponentListener(new ResizeListener());
    }

    public PdfRendererViewPanel(FitType fitType) {
        this();
        this.fitType = fitType;
    }

    public void goToFirstPage() {
        goToPage(1);
    }

    public void goToPreviousPage() {
        goToPage(currentPage);
    }

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

        try {
            currentPageObject = document.getPage(currentPage + 1, true);
        } catch (Exception e) {
            JPdfBookmarks.printErrorForDebug(e);
        }

        rendererPanel.repaint();
        firePageChangedEvent(new PageChangedEvent(this, currentPage + 1, hasNext,
                hasPrevious));
    }

    public void goToNextPage() {
        goToPage(currentPage + 2);
    }

    public void goToLastPage() {
        goToPage(numberOfPages);
    }

    public void goToBookmark(Bookmark bookmark) {
        int pageNum = bookmark.getPageNumber();
        goToPage(pageNum);
    }

    public void setFitWidth(int top) {
        this.top = top;
        setFit(FitType.FitWidth);
    }

    public void setFitHeight(int left) {
        this.left = left;
        setFit(FitType.FitHeight);
    }

    public void setFitPage() {
        setFit(FitType.FitPage);
    }

    public void setFitRect(Rectangle rect) {
        this.rect = rect;
        drawingComplete = true;
        setFit(FitType.FitRect);
    }

    public void setFitRect(int top, int left, int bottom, int right) {
        drawingComplete = true;
//        PDimension mediaBox = currentPageObject.getSize(Page.BOUNDARY_MEDIABOX,
//                0f, 1f);
        this.rect = new Rectangle(left, Math.round(currentPageObject.getHeight() - top),
                right - left, top - bottom);
        setFit(FitType.FitRect);
    }

    public void setTopLeftZoom(int top, int left, float zoom) {
        this.left = left;
        this.top = top;
        if (zoom > 0.001f) {
            this.scale = zoom;
        }
        setFit(FitType.TopLeftZoom);
    }

    public int getNumPages() {
        return numberOfPages;
    }

    public int getCurrentPage() {
        return currentPage + 1;
    }

    public FitType getFitType() {
        return fitType;
    }

    public void addPageChangedListener(PageChangedListener listener) {
        pageChangedListeners.add(listener);
    }

    public void removePageChangedListener(PageChangedListener listener) {
        pageChangedListeners.remove(listener);
    }

    public void setFitNative() {
        setFit(FitType.FitNative);
    }

    public void setFit(FitType fitType) {
        this.fitType = fitType;
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {

                if (PdfRendererViewPanel.this.fitType == FitType.FitRect) {
                    setCursor(Cursor.getPredefinedCursor(
                            Cursor.CROSSHAIR_CURSOR));
                } else {
                    rect = null;
                    setCursor(Cursor.getDefaultCursor());
                }
                calcScaleFactor();
                adjustPreferredSize();
                adjustViewportPosition();
            }
        });
        rendererPanel.repaint();
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
//        PDimension mediaBox = currentPageObject.getSize(Page.BOUNDARY_MEDIABOX,
//                0f, 1f);

        switch (fitType) {
            case FitWidth:
                scale = (float) viewport.getWidth() /
                        currentPageObject.getWidth();
                break;
            case FitHeight:
                scale = (float) viewport.getHeight() /
                        currentPageObject.getHeight();
                break;
            case FitNative:
                scale = 1.0f;
                break;
            case FitRect:
                if (rect != null && drawingComplete) {
                    float scaleWidth = (float) viewport.getWidth() /
                            rect.width;
                    float scaleHeight = (float) viewport.getHeight() /
                            rect.height;
                    scale = Math.min(scaleWidth, scaleHeight);
                } else {
//					float scaleWidth = (float) viewport.getWidth() /
//							(right - left);
//					float scaleHeight = (float) viewport.getHeight() /
//							(top - bottom);
//					scale = Math.min(scaleWidth, scaleHeight);
                }
                break;
            case FitPage:
                float scaleWidth = (float) viewport.getWidth() /
                        currentPageObject.getWidth();
                float scaleHeight = (float) viewport.getHeight() /
                        currentPageObject.getHeight();
                scale = Math.min(scaleWidth, scaleHeight);
                break;
        }
    }

    private void adjustPreferredSize() {
        rendererPanel.setSize(calcViewSize());
    }

    public Bookmark getBookmarkFromView() {
        Bookmark bookmark = new Bookmark();
        bookmark.setPageNumber(getCurrentPage());
        bookmark.setType(getFitType().convertToBookmarkType());
        PageDimension mediaBox = new PageDimension(
                currentPageObject.getWidth(),
                currentPageObject.getHeight());
        Point pt = viewport.getViewPosition();
        switch (bookmark.getType()) {
            case FitWidth:
                bookmark.setTop(Math.round(mediaBox.getHeight() -
                        (pt.y / scale)));
                bookmark.setThousandthsTop(
                        Bookmark.thousandthsVertical(bookmark.getTop(),
                        mediaBox.getHeight()));
                break;
            case FitHeight:
                bookmark.setLeft(Math.round(pt.x / scale));
                bookmark.setThousandthsLeft(
                        Bookmark.thousandthsHorizontal(bookmark.getLeft(),
                        mediaBox.getWidth()));
                break;
            case TopLeftZoom:
                bookmark.setTop(Math.round(mediaBox.getHeight() -
                        (pt.y / scale)));
                bookmark.setThousandthsTop(
                        Bookmark.thousandthsVertical(bookmark.getTop(),
                        mediaBox.getHeight()));
                bookmark.setLeft(Math.round(pt.x / scale));
                bookmark.setThousandthsLeft(
                        Bookmark.thousandthsHorizontal(bookmark.getLeft(),
                        mediaBox.getWidth()));
                bookmark.setZoom(scale);
                break;
            case FitRect:
                if (rect != null) {
                    float f = drawingComplete ? 1.0f : scale;
                    Point p = rect.getLocation();
                    Dimension d = rect.getSize();
                    bookmark.setLeft(Math.round(p.x / f));
                    bookmark.setThousandthsLeft(
                            Bookmark.thousandthsHorizontal(bookmark.getLeft(),
                            mediaBox.getWidth()));
                    bookmark.setTop(Math.round(mediaBox.getHeight() - Math.round(p.y / f)));
                    bookmark.setThousandthsTop(
                            Bookmark.thousandthsVertical(bookmark.getTop(),
                            mediaBox.getHeight()));
                    bookmark.setRight(Math.round(p.x / f) + Math.round(d.width / f));
                    bookmark.setThousandthsRight(
                            Bookmark.thousandthsHorizontal(bookmark.getRight(),
                            mediaBox.getWidth()));
                    bookmark.setBottom(Math.round(mediaBox.getHeight() -
                            (Math.round(p.y / f) + Math.round(d.height / f))));
                    bookmark.setThousandthsBottom(
                            Bookmark.thousandthsVertical(bookmark.getBottom(),
                            mediaBox.getWidth()));
                }
                break;
        }
        return bookmark;
    }

    private Dimension calcViewSize() {
        PageDimension scaledMediaBox = new PageDimension(
                currentPageObject.getWidth() * scale,
                currentPageObject.getHeight() * scale);

        int viewWidth = Math.max(Math.round(scaledMediaBox.getWidth()),
                viewport.getWidth());
        int viewHeight = Math.max(Math.round(scaledMediaBox.getHeight()),
                viewport.getHeight());

        switch (fitType) {
            case FitWidth:
                viewHeight = Math.round(scaledMediaBox.getHeight() +
                        viewport.getHeight());
                break;
            case FitHeight:
                viewWidth = Math.round(scaledMediaBox.getWidth()) +
                        viewport.getWidth();
                break;
            case FitPage:
                break;
            case FitNative:
            case FitRect:
            case TopLeftZoom:
                viewWidth = viewport.getWidth() +
                        Math.round(scaledMediaBox.getWidth());
                viewHeight = viewport.getHeight() +
                        Math.round(scaledMediaBox.getHeight());
                break;
        }

        return new Dimension(viewWidth, viewHeight);
    }

    private void adjustViewportPosition() {
        PageDimension mediaBox = new PageDimension(
                currentPageObject.getWidth() * scale,
                currentPageObject.getHeight() * scale);

        switch (fitType) {
            case FitPage:
                movePanel(0, 0);
                break;
            case FitWidth:
                if (top != -1) {
                    float gap = mediaBox.getHeight() - top * scale;
                    movePanel(0, Math.round(gap));
                }
                break;
            case FitHeight:
                if (left != -1) {
                    float gap = left * scale;
                    movePanel(Math.round(gap), 0);
                }
                break;
            case FitRect:
                if (rect != null && drawingComplete) {
                    movePanel(Math.round(rect.x * scale),
                            Math.round(rect.y * scale));
                } else {
//					movePanel(Math.round(left * scale),
//							Math.round((mediaBox.getHeight() - top) * scale));
                }
                break;
            case TopLeftZoom:
                int gapHeight;
                int gapWidth;
                Point pt = viewport.getViewPosition();
                if (top != -1) {
                    gapHeight = Math.round(mediaBox.getHeight() - top * scale);
                } else {
                    gapHeight = pt.y;
                }
                if (left != -1) {
                    gapWidth = Math.round(left * scale);
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

    private void fireRenderingStartEvent(RenderingStartEvent e) {
        for (RenderingStartListener listener : renderingStartListeners) {
            listener.renderingStart(e);
        }
    }

    public void addViewChangedListener(ViewChangedListener listener) {
        viewChangedListeners.add(listener);
    }

    public void removeViewChangedListener(ViewChangedListener listener) {
        viewChangedListeners.remove(listener);
    }

    public void addRenderingStartListener(RenderingStartListener listener) {
        renderingStartListeners.add(listener);
    }

    public void removeRenderingStartListener(RenderingStartListener listener) {
        renderingStartListeners.remove(listener);
    }

    private class ResizeListener extends ComponentAdapter {

        @Override
        public void componentResized(ComponentEvent e) {
            super.componentResized(e);
        }
    }

    private class PdfRenderPanel extends JPanel implements Scrollable {

//		Cursor handOpenCursor;
//		Cursor handClosedCursor;
        public PdfRenderPanel() {
//			ImageIcon img = Res.getIcon(getClass(), "gfx32/hand-opened.png");
//			handOpenCursor = Toolkit.getDefaultToolkit().createCustomCursor(
//					img.getImage(), new Point(16, 16), "hand-opened");
//			img = Res.getIcon(getClass(), "gfx32/hand-closed.png");
//			handClosedCursor = Toolkit.getDefaultToolkit().createCustomCursor(
//					img.getImage(), new Point(16, 16), "hand-closed");
//			setCursor(handOpenCursor);
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

            if (document == null || currentPageObject == null) {
                setPreferredSize(viewport.getSize());
                revalidate();
                return;
            }

            Graphics2D g2 = (Graphics2D) g;
            int rotation = 0;

            calcScaleFactor();


            if (oldScale != scale || currentPage != oldPage || img == null) {
                CursorToolkit.startWaitCursor(PdfRendererViewPanel.this);
                try {
//                    img = (BufferedImage) document.getPageImage(
//                            currentPage, GraphicsRenderingHints.SCREEN,
//                            Page.BOUNDARY_MEDIABOX, rotation, scale);
                    img = (BufferedImage) currentPageObject.getImage(
                            Math.round(currentPageObject.getWidth() * scale),
                            Math.round(currentPageObject.getHeight() * scale),
                            currentPageObject.getBBox(),
                            this, true, true);
                    oldScale = scale;
                    oldPage = currentPage;
                } catch (Exception e) {
                    JPdfBookmarks.printErrorForDebug(e);
                } finally {
                    CursorToolkit.stopWaitCursor(PdfRendererViewPanel.this);
                }
            }

            setPreferredSize(calcViewSize());
            revalidate();

            if (fitType == FitType.FitRect && drawingComplete == false && rect != null && img != null) {
                BufferedImage clone = new BufferedImage(img.getWidth(),
                        img.getHeight(), img.getType());
                Graphics gcopy = clone.getGraphics();
                gcopy.drawImage(img, 0, 0, null);
                Graphics2D g2img = (Graphics2D) clone.getGraphics();
                g2img.setStroke(new BasicStroke(2.0f));
                g2img.setColor(Color.red);
                g2img.drawRect(rect.x, rect.y, rect.width, rect.height);
                g2.drawImage(clone, 0, 0, this);
            } else if (img != null) {
                g2.drawImage(img, 0, 0, this);
            }

            Bookmark bookmark = getBookmarkFromView();
            fireViewChangedEvent(new ViewChangedEvent(this, fitType, scale, bookmark));
        }

        private class PdfPanelMouseListener extends MouseAdapter {

            private int xDiff, yDiff;

            @Override
            public void mouseDragged(MouseEvent e) {
                if (fitType != FitType.FitRect) {
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
                } else {
                    if (e.getX() < xDiff) {
                        rect.setLocation(e.getX(), rect.y);
                    }
                    if (e.getY() < yDiff) {
                        rect.setLocation(rect.x, e.getY());
                    }
                    int rectWidth = Math.abs(e.getX() - xDiff);
                    int rectHeight = Math.abs(e.getY() - yDiff);
                    rect.setSize(rectWidth, rectHeight);
                    repaint();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                requestFocusInWindow();
            }

            @Override
            public void mousePressed(MouseEvent e) {
//				setCursor(handClosedCursor);
                xDiff = e.getX();
                yDiff = e.getY();
                if (fitType != FitType.FitRect) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                } else {
                    rect = new Rectangle();
                    rect.setLocation(xDiff, yDiff);
                    drawingComplete = false;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
//				setCursor(handOpenCursor);
                if (fitType == FitType.FitRect) {
                    drawingComplete = true;
                    rect.setSize(Math.round(rect.width / scale),
                            Math.round(rect.height / scale));
                    rect.setLocation(Math.round(rect.x / scale),
                            Math.round(rect.y / scale));
                    setFitRect(rect);
                    repaint();
                } else {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }

        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        public int getScrollableUnitIncrement(Rectangle visibleRect,
                int orientation, int direction) {
            return 20;
        }

        public int getScrollableBlockIncrement(Rectangle visibleRect,
                int orientation, int direction) {
            return 100;
        }

        public boolean getScrollableTracksViewportWidth() {
            return false;
        }

        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
