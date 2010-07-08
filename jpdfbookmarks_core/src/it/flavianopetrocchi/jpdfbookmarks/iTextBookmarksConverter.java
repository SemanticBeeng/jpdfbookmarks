/*
 * ITextBookmarksConverter.java
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

/*
 * Portions of this code are derived from iText-2.1.7 source code in
 * com/lowagie/text/pdf/SimpleBookmark.java which has the following copyright
 * and license.
 *
 * Copyright 2003 by Paulo Soares.
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is 'iText, a free JAVA-PDF library'.
 *
 * The Initial Developer of the Original Code is Bruno Lowagie. Portions created by
 * the Initial Developer are Copyright (C) 1999, 2000, 2001, 2002 by Bruno Lowagie.
 * All Rights Reserved.
 * Co-Developer of the code is Paulo Soares. Portions created by the Co-Developer
 * are Copyright (C) 2000, 2001, 2002 by Paulo Soares. All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 *
 * Alternatively, the contents of this file may be used under the terms of the
 * LGPL license (the "GNU LIBRARY GENERAL PUBLIC LICENSE"), in which case the
 * provisions of LGPL are applicable instead of those above.  If you wish to
 * allow use of your version of this file only under the terms of the LGPL
 * License and not to allow others to use your version of this file under
 * the MPL, indicate your decision by deleting the provisions above and
 * replace them with the notice and other provisions required by the LGPL.
 * If you do not delete the provisions above, a recipient may use your version
 * of this file under either the MPL or the GNU LIBRARY GENERAL PUBLIC LICENSE.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the MPL as stated above or under the terms of the GNU
 * Library General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library general Public License for more
 * details.
 *
 * If you didn't download this code from the following link, you should check if
 * you aren't using an obsolete version:
 * http://www.lowagie.com/iText/
 */
package it.flavianopetrocchi.jpdfbookmarks;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfStamper;
import it.flavianopetrocchi.utilities.Ut;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class iTextBookmarksConverter implements IBookmarksConverter {

    private PdfReader reader;
    private PdfStamper stamper;
    private Bookmark rootBookmark;
    private String filePath;
    private List outline;
    private boolean showOnOpen = false;
    private HashMap namesAsString;
    private HashMap namesAsName;
    IntHashtable pages;

    public iTextBookmarksConverter(String string) throws IOException {
        open(string);
    }

    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
        reader = null;
        stamper = null;
        outline = null;
        rootBookmark = null;
        filePath = null;
        namesAsString = null;
    }

    public Bookmark getRootBookmark() {
        if (rootBookmark == null) {
            if (new Prefs().getConvertNamedDestinations()) {
                reader.consolidateNamedDestinations();
            }
            rootBookmark = getBookmark();
        }
        return rootBookmark;
    }

    public boolean isEncryped() {
        return reader.isEncrypted();
    }

    public void open(String pdfPath) throws IOException {
        if (reader != null) {
            close();
        }
        this.filePath = pdfPath;
        //byte[] fileBytes = Ut.getBytesFromFile(new File(pdfPath));
        //random access seems to slow down loading a lot
        //reader = new PdfReader(new RandomAccessFileOrArray(fileBytes), null);
        //reader = new PdfReader(fileBytes);
        reader = new PdfReader(pdfPath);
        //reader = new PdfReader(new RandomAccessFileOrArray(this.filePath), null);
        int preferences = reader.getSimpleViewerPreferences();
        if ((preferences & PdfWriter.PageModeUseOutlines) == 0) {
            showOnOpen = false;
        } else {
            showOnOpen = true;
        }
    }

    public void rebuildBookmarksFromTreeNodes(Bookmark root) {
        Bookmark current;
        try {
            current = (Bookmark) root.getFirstChild();
        } catch (Exception exc) {
            current = null;
        }

        outline = new ArrayList();
        while (current != null) {
            outline.add(rebuildBookmarksRecursive(current));
            current = (Bookmark) current.getNextSibling();
        }

    }

    private HashMap rebuildBookmarksRecursive(Bookmark node) {
        HashMap bookmarkMap = bookmarkToHashMap(node);

        Bookmark current;
        try {
            current = (Bookmark) node.getFirstChild();
        } catch (Exception exc) {
            current = null;
        }

        ArrayList kids = new ArrayList();
        while (current != null) {
            HashMap childMap = rebuildBookmarksRecursive(current);
            if (childMap != null) {
                kids.add(childMap);
            }
            current = (Bookmark) current.getNextSibling();
        }
        if (!kids.isEmpty()) {
            bookmarkMap.put("Kids", kids);
        }

        return bookmarkMap;
    }

    private HashMap bookmarkToHashMap(Bookmark bookmark) {
        HashMap map = new HashMap();

        map.put("Title", bookmark.getTitle().trim());

        if (bookmark.isOpened()) {
            map.put("Open", "true");
        } else {
            map.put("Open", "false");
        }

        if (bookmark.getColor().equals(Color.black) == false) {
            float[] rgb = bookmark.getColor().getRGBColorComponents(null);
            map.put("Color", String.valueOf(rgb[0]) + " " +
                    String.valueOf(rgb[1]) + " " + String.valueOf(rgb[2]));
        }
        String style = "";
        if (bookmark.isBold()) {
            style += "bold ";
        }
        if (bookmark.isItalic()) {
            style += "italic ";
        }
        if (!style.isEmpty()) {
            map.put("Style", style.trim());
        }

        BookmarkType type = bookmark.getType();

        if (type == BookmarkType.Launch) {
            map.put("Action", "Launch");
            map.put("File", bookmark.getFileToLaunch());
        } else if (type == BookmarkType.Uri) {
            map.put("Action", "URI");
            map.put("URI", bookmark.getUri());
        } else if (type != BookmarkType.Unknown) {
            if (bookmark.isRemoteDestination()) {
                map.put("Action", "GoToR");
                map.put("File", bookmark.getRemoteFilePath());
                //map.put("NewWindow", bookmark.isNewWindow());
            } else {
                map.put("Action", "GoTo");
//                if (type == BookmarkType.Named) {
//                    map.put("Named", bookmark.getNamedDestination());
//                }
            }

            if (type == BookmarkType.Named) {
                if (bookmark.isNamedAsName()) {
                    map.put("NamedN", bookmark.getNamedDestination());
                } else {
                    map.put("Named", bookmark.getNamedDestination());
                }
            } else {
                StringBuffer pageDest = new StringBuffer();
                if (bookmark.isRemoteDestination()) {
                    pageDest.append(
                            String.valueOf(bookmark.getPageNumber() -  1));
                } else {
                    pageDest.append(
                            String.valueOf(bookmark.getPageNumber()));
                }
                int left = bookmark.getLeft();
                int right = bookmark.getRight();
                int top = bookmark.getTop();
                int bottom = bookmark.getBottom();
                float zoom = bookmark.getZoom();
                if (type == BookmarkType.TopLeftZoom) {
                    pageDest.append(" XYZ ");
                    pageDest.append(left == -1 ? "null" : left).append(" ");
                    pageDest.append(top == -1 ? "null" : top).append(" ").append(zoom);
                } else if (type == BookmarkType.FitPage) {
                    pageDest.append(" Fit");
                } else if (type == BookmarkType.FitWidth) {
                    pageDest.append(" FitH ").append(top == -1 ? "null" : top);
                } else if (type == BookmarkType.FitHeight) {
                    pageDest.append(" FitV ").append(left == -1 ? "null" : left);
                } else if (type == BookmarkType.FitRect) {
                    pageDest.append(" FitR ").append(left == -1 ? "null" : left).append(" ").append(bottom == -1 ? "null" : bottom).append(" ").append(right == -1 ? "null" : right).append(" ").append(top == -1 ? "null" : top);
                } else if (type == BookmarkType.FitContent) {
                    pageDest.append(" FitB");
                } else if (type == BookmarkType.FitContentWidth) {
                    pageDest.append(" FitBH ").append(top == -1 ? "null" : top);
                } else if (type == BookmarkType.FitContentHeight) {
                    pageDest.append(" FitBV ").append(left == -1 ? "null" : left);
                }
                map.put("Page", pageDest.toString());
            }
        }

        return map;
    }

    public void save(String filePath) throws IOException {
        try {
            File tmp = File.createTempFile("jpdf", ".pdf");
            tmp.deleteOnExit();
            Ut.copyFile(this.filePath, tmp.getPath());
            PdfReader tmpReader = new PdfReader(tmp.getPath());
            stamper = new PdfStamper(tmpReader, new FileOutputStream(filePath));
            if (outline != null) {
                stamper.setOutlines(outline);
            }
            int preferences = reader.getSimpleViewerPreferences();
            if (showOnOpen) {
                preferences |= PdfWriter.PageModeUseOutlines;
            } else {
                preferences = preferences & ~PdfWriter.PageModeUseOutlines;
            }
            stamper.setViewerPreferences(preferences);
            stamper.close();
            tmp.delete();
        } catch (DocumentException ex) {
        }
    }

    private Bookmark getBookmark() {
        PdfDictionary catalog = reader.getCatalog();
        PdfObject obj = PdfReader.getPdfObjectRelease(catalog.get(PdfName.OUTLINES));
        if (obj == null || !obj.isDictionary()) {
            return null;
        }
        PdfDictionary outlines = (PdfDictionary) obj;
        pages = new IntHashtable();
        int numPages = reader.getNumberOfPages();
        for (int k = 1; k <= numPages; ++k) {
            pages.put(reader.getPageOrigRef(k).getNumber(), k);
            reader.releasePage(k);
        }

        Bookmark root = new Bookmark();
        root.setTitle("Root Bookmark");
        bookmarkDepth(reader, root,
                (PdfDictionary) PdfReader.getPdfObjectRelease(
                outlines.get(PdfName.FIRST)));
        return root;
    }

    private void bookmarkDepth(PdfReader reader, Bookmark father,
            PdfDictionary outline) {
        Bookmark bookmark = null;
        while (outline != null) {
            bookmark = new Bookmark();
            PdfString title = (PdfString) PdfReader.getPdfObjectRelease(
                    outline.get(PdfName.TITLE));
            bookmark.setTitle(title.toUnicodeString());
            PdfArray color = (PdfArray) PdfReader.getPdfObjectRelease(
                    outline.get(PdfName.C));
            if (color != null && color.size() == 3) {
                ByteBuffer out = new ByteBuffer();
                out.append(color.getAsNumber(0).floatValue()).append(' ');
                out.append(color.getAsNumber(1).floatValue()).append(' ');
                out.append(color.getAsNumber(2).floatValue());
                bookmark.setColor(new Color(color.getAsNumber(0).floatValue(),
                        color.getAsNumber(1).floatValue(),
                        color.getAsNumber(2).floatValue()));
            }
            PdfNumber style = (PdfNumber) PdfReader.getPdfObjectRelease(
                    outline.get(PdfName.F));
            if (style != null) {
                int f = style.intValue();
                if ((f & 1) != 0) {
                    bookmark.setItalic(true);
                }
                if ((f & 2) != 0) {
                    bookmark.setBold(true);
                }
            }
            PdfNumber count = (PdfNumber) PdfReader.getPdfObjectRelease(
                    outline.get(PdfName.COUNT));
            if (count != null && count.intValue() < 0) {
                bookmark.setOpened(false);
            } else {
                bookmark.setOpened(true);
            }
            try {
                PdfObject dest = PdfReader.getPdfObjectRelease(
                        outline.get(PdfName.DEST));
                if (dest != null) {
                    mapGotoBookmark(bookmark, dest);
                } else {
                    PdfDictionary action = (PdfDictionary) PdfReader.getPdfObjectRelease(
                            outline.get(PdfName.A));
                    if (action != null) {
                        if (PdfName.GOTO.equals(
                                PdfReader.getPdfObjectRelease(
                                action.get(PdfName.S)))) {
                            dest = PdfReader.getPdfObjectRelease(
                                    action.get(PdfName.D));
                            if (dest != null) {
                                mapGotoBookmark(bookmark, dest);
                            }
                        } else if (PdfName.URI.equals(
                                PdfReader.getPdfObjectRelease(
                                action.get(PdfName.S)))) {
                            bookmark.setType(BookmarkType.Uri);
                            bookmark.setUri(((PdfString) PdfReader.getPdfObjectRelease(
                                    action.get(PdfName.URI))).toUnicodeString());
                        } else if (PdfName.GOTOR.equals(
                                PdfReader.getPdfObjectRelease(
                                action.get(PdfName.S)))) {
                            dest = PdfReader.getPdfObjectRelease(
                                    action.get(PdfName.D));
                            if (dest != null) {
                                if (dest.isString()) {
                                    bookmark.setNamedDestination(dest.toString());
                                } else if (dest.isName()) {
                                    bookmark.setNamedDestination(
                                            PdfName.decodeName(dest.toString()));
                                    bookmark.setNamedAsName(true);
                                } else if (dest.isArray()) {
                                    PdfArray arr = (PdfArray) dest;
                                    makeBookmarkParam(bookmark, arr, null);
                                }
                            }
                            bookmark.setRemoteDestination(true);
                            PdfObject file = PdfReader.getPdfObjectRelease(
                                    action.get(PdfName.F));
                            if (file != null) {
                                if (file.isString()) {
                                    bookmark.setRemoteFilePath(
                                            ((PdfString) file).toUnicodeString());
                                } else if (file.isDictionary()) {
                                    file = PdfReader.getPdfObject(
                                            ((PdfDictionary) file).get(PdfName.F));
                                    if (file.isString()) {
                                        bookmark.setRemoteFilePath(
                                                ((PdfString) file).toUnicodeString());
                                    }
                                }
                            }
                            PdfObject newWindow =
                                    PdfReader.getPdfObjectRelease(
                                    action.get(PdfName.NEWWINDOW));
                            if (newWindow != null) {
                                bookmark.setNewWindow(
                                        ((PdfBoolean) newWindow).booleanValue());
                            }
                        } else if (PdfName.LAUNCH.equals(
                                PdfReader.getPdfObjectRelease(action.get(PdfName.S)))) {
                            bookmark.setType(BookmarkType.Launch);
                            PdfObject file = PdfReader.getPdfObjectRelease(
                                    action.get(PdfName.F));
                            if (file == null) {
                                file = PdfReader.getPdfObjectRelease(
                                        action.get(PdfName.WIN));
                            }
                            if (file != null) {
                                if (file.isString()) {
                                    bookmark.setFileToLaunch(
                                            ((PdfString) file).toUnicodeString());
                                } else if (file.isDictionary()) {
                                    file = PdfReader.getPdfObjectRelease(
                                            ((PdfDictionary) file).get(PdfName.F));
                                    if (file.isString()) {
                                        bookmark.setFileToLaunch(
                                                ((PdfString) file).toUnicodeString());
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                //empty on purpose
            }
            PdfDictionary first = (PdfDictionary) PdfReader.getPdfObjectRelease(
                    outline.get(PdfName.FIRST));
            if (first != null) {
                bookmarkDepth(reader, bookmark, first);
            }
            father.add(bookmark);
            outline = (PdfDictionary) PdfReader.getPdfObjectRelease(
                    outline.get(PdfName.NEXT));
        }

    }

    private void mapGotoBookmark(Bookmark bookmark, PdfObject dest) {
        if (dest.isString()) {
            if (namesAsString == null) {
                namesAsString = reader.getNamedDestinationFromStrings();
            }
            bookmark.setType(BookmarkType.Named);
            bookmark.setNamedDestination(dest.toString());
            PdfArray namedDest = getNamedDestination(reader, dest.toString(), false);
            Bookmark namedTarget = new Bookmark();
            namedTarget.setTitle(dest.toString());
            makeBookmarkParam(namedTarget, (PdfArray) namedDest, pages);
            bookmark.setNamedTarget(namedTarget);
        } else if (dest.isName()) {
            if (namesAsName == null) {
                namesAsName = reader.getNamedDestinationFromNames();
            }
            bookmark.setType(BookmarkType.Named);
            String name = PdfName.decodeName(dest.toString());
            bookmark.setNamedDestination(name);
            bookmark.setNamedAsName(true);
            PdfArray namedDest = getNamedDestination(reader, name, true);
            Bookmark namedTarget = new Bookmark();
            namedTarget.setTitle(name);
            makeBookmarkParam(namedTarget, (PdfArray) namedDest, pages);
            bookmark.setNamedTarget(namedTarget);
        } else if (dest.isArray()) {
            makeBookmarkParam(bookmark, (PdfArray) dest, pages);
        }
    }

    private PdfArray getNamedDestination(PdfReader reader, String dest, boolean fromNames) {
        PdfArray arr = null;
        for (Iterator it = namesAsString.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            if (fromNames) {
                if (dest.equals(key)) {
                    arr = (PdfArray) entry.getValue();
                    break;
                }
            } else if (dest.equals(key)) {
                arr = (PdfArray) entry.getValue();
                break;
            }
        }
        return arr;
    }

    private String makeBookmarkParam(Bookmark bookmark, PdfArray dest, IntHashtable pages) {
        StringBuffer s = new StringBuffer();
        PdfObject obj = dest.getPdfObject(0);
        if (obj.isNumber()) {
            s.append(((PdfNumber) obj).intValue() + 1);
            bookmark.setPageNumber(((PdfNumber) obj).intValue() + 1);
        } else {
            if (pages != null) {
                s.append(pages.get(getNumber((PdfIndirectReference) obj))); //changed by ujihara 2004-06-13
                bookmark.setPageNumber(pages.get(getNumber((PdfIndirectReference) obj)));
            }
        }

        Rectangle pageSize = null;
        if (bookmark.getType().equals(BookmarkType.GoToFile) == false) {
            pageSize = reader.getPageSize(bookmark.getPageNumber());
        }

        if (pageSize != null) {
            bookmark.setPageWidth(pageSize.getWidth());
            bookmark.setPageHeight(pageSize.getHeight());
        }

        String destType = dest.getPdfObject(1).toString();
        PdfObject[] params = new PdfObject[dest.size()];
        for (int k = 2; k < dest.size(); ++k) {
            params[k - 2] = dest.getPdfObject(k);
        }
        if (destType.equals("/XYZ")) {
            bookmark.setType(BookmarkType.TopLeftZoom);
            if (!params[0].isNull()) {
                bookmark.setLeft(((PdfNumber) params[0]).intValue());
            }
            if (!params[1].isNull()) {
                bookmark.setTop(((PdfNumber) params[1]).intValue());
            }
            if (pageSize != null) {
                bookmark.setThousandthsLeft(thousandthsHorizontal(
                        bookmark.getLeft(), pageSize));
                bookmark.setThousandthsTop(thousandthsVertical(
                        bookmark.getTop(), pageSize));
            }
            if (!params[2].isNull()) {
                bookmark.setZoom(((PdfNumber) params[2]).floatValue());
            }
        } else if (destType.equals("/FitH")) {
            bookmark.setType(BookmarkType.FitWidth);
            if (!params[0].isNull()) {
                bookmark.setTop(((PdfNumber) params[0]).intValue());
            }
            if (pageSize != null) {
                bookmark.setThousandthsTop(thousandthsVertical(
                        bookmark.getTop(), pageSize));
            }
        } else if (destType.equals("/FitV")) {
            bookmark.setType(BookmarkType.FitHeight);
            if (!params[0].isNull()) {
                bookmark.setLeft(((PdfNumber) params[0]).intValue());
            }
            if (pageSize != null) {
                bookmark.setThousandthsLeft(thousandthsHorizontal(
                        bookmark.getLeft(), pageSize));
            }
        } else if (destType.equals("/FitBH")) {
            bookmark.setType(BookmarkType.FitContentWidth);
            if (!params[0].isNull()) {
                bookmark.setTop(((PdfNumber) params[0]).intValue());
            }
            if (pageSize != null) {
                bookmark.setThousandthsTop(thousandthsVertical(
                        bookmark.getTop(), pageSize));
            }
        } else if (destType.equals("/FitBV")) {
            bookmark.setType(BookmarkType.FitContentHeight);
            if (!params[0].isNull()) {
                bookmark.setLeft(((PdfNumber) params[0]).intValue());
            }
            if (pageSize != null) {
                bookmark.setThousandthsLeft(thousandthsHorizontal(
                        bookmark.getLeft(), pageSize));
            }
        } else if (destType.equals("/Fit")) {
            bookmark.setType(BookmarkType.FitPage);
        } else if (destType.equals("/FitB")) {
            bookmark.setType(BookmarkType.FitContent);
        } else if (destType.equals("/FitR")) {
            bookmark.setType(BookmarkType.FitRect);
            if (!params[0].isNull()) {
                bookmark.setLeft(((PdfNumber) params[0]).intValue());
            }
            if (!params[1].isNull()) {
                bookmark.setBottom(((PdfNumber) params[1]).intValue());
            }
            if (!params[2].isNull()) {
                bookmark.setRight(((PdfNumber) params[2]).intValue());
            }
            if (!params[3].isNull()) {
                bookmark.setTop(((PdfNumber) params[3]).intValue());
            }
            if (pageSize != null) {
                bookmark.setThousandthsLeft(thousandthsHorizontal(
                        bookmark.getLeft(), pageSize));
                bookmark.setThousandthsTop(thousandthsVertical(
                        bookmark.getTop(), pageSize));
                bookmark.setThousandthsRight(thousandthsHorizontal(
                        bookmark.getRight(), pageSize));
                bookmark.setThousandthsBottom(thousandthsVertical(
                        bookmark.getBottom(), pageSize));
            }
        }

        s.append(' ').append(dest.getPdfObject(1).toString().substring(1));
        for (int k = 2; k < dest.size(); ++k) {
            s.append(' ').append(dest.getPdfObject(k).toString());
        }
        return s.toString();
    }

    private static int getNumber(PdfIndirectReference indirect) {
        PdfDictionary pdfObj = (PdfDictionary) PdfReader.getPdfObjectRelease(indirect);
        if (pdfObj.contains(PdfName.TYPE) && pdfObj.get(PdfName.TYPE).equals(PdfName.PAGES) && pdfObj.contains(PdfName.KIDS)) {
            PdfArray kids = (PdfArray) pdfObj.get(PdfName.KIDS);
            indirect = (PdfIndirectReference) kids.getPdfObject(0);
        }
        return indirect.getNumber();
    }

    public static int thousandthsHorizontal(int x, Rectangle pageRect) {
        int thousandths = -1;
        if (x != -1) {
            try {
                thousandths = (int) (x * 1000.0f /
                        pageRect.getWidth());
            } catch (Exception exc) {
            }
        }
        return thousandths;
    }

    public static int thousandthsVertical(int y, Rectangle pageRect) {
        int thousandths = -1;
        if (y != -1) {
            try {
                thousandths = 1000 - Math.round(y * 1000.0f / pageRect.getHeight());
            } catch (Exception exc) {
            }
        }
        return thousandths;
    }

    public boolean showBookmarksOnOpen() {
        return showOnOpen;
    }

    public void setShowBookmarksOnOpen(boolean show) {
        showOnOpen = show;
    }

    public float getPageWidth(int pageNumber) {
        Rectangle pageSize = reader.getPageSize(pageNumber);
        return pageSize.getWidth();
    }

    public float getPageHeight(int pageNumber) {
        Rectangle pageSize = reader.getPageSize(pageNumber);
        return pageSize.getHeight();
    }

    public int getCountOfPages() {
        return reader.getNumberOfPages();
    }
}
