/*
 * Bookmark.java
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
package it.flavianopetrocchi.jpdfbookmarks.bookmark;

import it.flavianopetrocchi.colors.Colors;
//import it.flavianopetrocchi.jpdfbookmarks.Res;
import it.flavianopetrocchi.utilities.Ut;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.StringTokenizer;
import javax.swing.tree.DefaultMutableTreeNode;

public class Bookmark extends DefaultMutableTreeNode implements Serializable {

    protected static final String NEWLINE = System.getProperty("line.separator");
    private ArrayList<Bookmark> chainedBookmarks = new ArrayList<Bookmark>();
    //used as tokens array indexes in bookmarkFromString
    private static final int PAGE = 0, COLOR = 1, BOLD = 2, ITALIC = 3, OPEN = 4;
    //private String title = Res.getString("DEFAULT_TITLE");
    private static String defaultTitle = "Bookmark";
    private String title = null;
    private static String sPage = "Page";
    private static String parseError = "PARSE ERROR:";
    private int pageNumber = -1;
    private boolean bold = false, italic = false;
    private Color color = Color.black;
    private int top = -1, left = -1, right = -1, bottom = -1;
    float zoom = 0.0f;
    private int thousandthsTop = -1, thousandthsLeft = -1,
            thousandthsBottom = -1, thousandthsRight = -1;
    private BookmarkType type = BookmarkType.FitWidth;
    private String pageSep = "/";
    private String attributeSep = ",";
    private String namedDestination = "";
    private Bookmark namedTarget;
    private boolean opened = false;
    private float pageHeight = Float.NaN;
    private float pageWidth = Float.NaN;
    private String uri;
    private String remoteFilePath;
    private String fileToLaunch;
    private boolean newWindow = true;
    private boolean remoteDestination = false;
    private boolean namedAsName = false;
    private String fieldNameToHide = null;
    private boolean hide = true;

//	private long pageWidth;
//	private long pageHeight;
    public Bookmark() {
        title = defaultTitle;
    }

    public String getFieldNameToHide() {
        return fieldNameToHide;
    }

    public void setFieldNameToHide(String fieldNameToHide) {
        this.fieldNameToHide = fieldNameToHide;
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public static void localizeStrings(String defTitle, String page, String parseErr) {
        if (defTitle != null) {
            defaultTitle = defTitle;
        }

        if (page != null) {
            sPage = page;
        }

        if (parseError != null) {
            parseError = parseErr;
        }
    }

    public void addChainedBookmark(Bookmark bookmark) {
        chainedBookmarks.add(bookmark);
    }

    public boolean isNamedAsName() {
        return namedAsName;
    }

    public Bookmark getNamedTarget() {
        return namedTarget;
    }

    public void setNamedTarget(Bookmark namedTarget) {
        this.namedTarget = namedTarget;
    }

    public void setNamedAsName(boolean namedAsName) {
        this.namedAsName = namedAsName;
    }

    public boolean isRemoteDestination() {
        return remoteDestination;
    }

    public void setRemoteDestination(boolean remoteDestination) {
        this.remoteDestination = remoteDestination;
    }

    public boolean isNewWindow() {
        return newWindow;
    }

    public void setNewWindow(boolean newWindow) {
        this.newWindow = newWindow;
    }

    public String getFileToLaunchOsDep() {
        //On windows tranform path like /C/folder/document.txt to C:\folder\document.txt
        String sys = System.getProperty("os.name");
        StringBuilder s = new StringBuilder(fileToLaunch);
        if (sys.startsWith("Windows")) {
            if (fileToLaunch.startsWith("/")) {
                while (s.charAt(0) == '/') {
                    s.deleteCharAt(0);
                }
                if (s.charAt(1) == '/') {
                    s.insert(1, ":");
                }
            }
        }
        //File f = new File(s.toString());
        //return f.toString();
        return s.toString();
    }

    public String getFileToLaunch() {
        if (fileToLaunch == null) {
            return null;
        } else {
            return getFileToLaunchOsDep();
        }
        //return fileToLaunch; 
    }

    public void setFileToLaunch(String fileToLaunch) {
        this.fileToLaunch = fileToLaunch;
    }

    public String getRemoteFilePath() {
        return remoteFilePath;
    }

    public void setRemoteFilePath(String remoteFilePath) {
        this.remoteFilePath = remoteFilePath;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public float getPageHeight() {
        return pageHeight;
    }

    public void setPageHeight(float pageHeight) {
        this.pageHeight = pageHeight;
    }

    public float getPageWidth() {
        return pageWidth;
    }

    public void setPageWidth(float pageWidth) {
        this.pageWidth = pageWidth;
    }

// <editor-fold defaultstate="collapsed" desc="Getters And Setters">
    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public String getNamedDestination() {
        return namedDestination;
    }

    public void setNamedDestination(String namedDestination) {
        this.namedDestination = namedDestination;
    }

    public int getThousandthsLeft() {
        return thousandthsLeft;
    }

    public void setThousandthsLeft(int thousandthsLeft) {
        this.thousandthsLeft = thousandthsLeft;
    }

    public int getThousandthsTop() {
        return thousandthsTop;
    }

    public void setThousandthsTop(int thousandthsTop) {
        this.thousandthsTop = thousandthsTop;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public BookmarkType getType() {
        return type;
    }

    public void setType(BookmarkType fitType) {
        this.type = fitType;
    }

    public boolean isItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getTitle() {
        if (title == null) {
            title = defaultTitle;
        }
        return title;
    }

    public void setTitle(String title) {
        title = title.replaceAll("\n", " ");
        title = title.replaceAll("\r", " ");
        title = title.trim();
        this.title = title;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }// </editor-fold>

    public void clearChainedBookmarks() {
        chainedBookmarks.clear();
    }

    public static Bookmark cloneBookmark(Bookmark bookmarkToClone, boolean copyChildren) {
        Bookmark clonedBookmark;
        clonedBookmark = new Bookmark();
        clonedBookmark.cloneAppearance(bookmarkToClone);
        clonedBookmark.cloneDestination(bookmarkToClone);
        if (copyChildren) {
            cloneBookmarkWithChildren(clonedBookmark, bookmarkToClone);
        }
        return clonedBookmark;
    }

    private static void cloneBookmarkWithChildren(Bookmark father, Bookmark bookmarkToClone) {

        Enumeration e = bookmarkToClone.children();
        while (e.hasMoreElements()) {
            Bookmark child = (Bookmark) e.nextElement();
            Bookmark clonedChild = new Bookmark();
            clonedChild.cloneAppearance(child);
            clonedChild.cloneDestination(child);
            father.add(clonedChild);
            cloneBookmarkWithChildren(clonedChild, child);
        }
    }

    public void cloneAppearance(Bookmark bookmark) {
        setTitle(bookmark.getTitle());
        setColor(bookmark.getColor());
        setItalic(bookmark.isItalic());
        setBold(bookmark.isBold());
        setOpened(bookmark.isOpened());
    }

    public void cloneDestination(Bookmark bookmark) {
        setType(bookmark.getType());
        setPageNumber(bookmark.getPageNumber());
        setTop(bookmark.getTop());
        setLeft(bookmark.getLeft());
        setRight(bookmark.getRight());
        setBottom(bookmark.getBottom());
        setZoom(bookmark.getZoom());
        setFileToLaunch(bookmark.getFileToLaunch());
        setNamedAsName(bookmark.isNamedAsName());
        setNamedDestination(bookmark.getNamedDestination());
        Bookmark target = bookmark.getNamedTarget();
        if (target != null) {
            Bookmark copiedTarget = cloneBookmark(target, false);
            setNamedTarget(copiedTarget);
        }
        setNewWindow(bookmark.isNewWindow());
        setRemoteDestination(bookmark.isRemoteDestination());
        setRemoteFilePath(bookmark.getRemoteFilePath());
        setUri(bookmark.getUri());
        setThousandthsBottom(bookmark.getThousandthsBottom());
        setThousandthsRight(bookmark.getThousandthsRight());
        setThousandthsTop(bookmark.getThousandthsTop());
        setThousandthsLeft(bookmark.getThousandthsLeft());
        setFieldNameToHide(bookmark.getFieldNameToHide());
        setHide(bookmark.isHide());

        chainedBookmarks.clear();
        ArrayList<Bookmark> chainedBookmarksToCopy = bookmark.getChainedBookmarks();
        chainedBookmarks.ensureCapacity(chainedBookmarksToCopy.size());
        for (Bookmark b : chainedBookmarksToCopy) {
            Bookmark copy = new Bookmark();
            copy.cloneDestination(b);
            chainedBookmarks.add(copy);
        }
    }

//    public Bookmark(Object outlineNode) {
//        super(outlineNode);
//    }
    public String getDescription(boolean useThousandths) {

        String extendedDescr = getExtendedDescription(null, null, null, useThousandths);
        StringBuilder buffer = new StringBuilder(extendedDescr);

        //restrict to first line (that is to say first action)
        int firstNewLineIndex = buffer.indexOf(NEWLINE);
        if (firstNewLineIndex != -1) {
            buffer.delete(firstNewLineIndex, buffer.length());
        }

        if (isRemoteDestination()) {
            String filePath = getRemoteFilePath();
            int filePathIndex = buffer.lastIndexOf(filePath);
            buffer = new StringBuilder(buffer.substring(0, filePathIndex));
        }

        //int pageSepIndex = buffer.lastIndexOf(pageSep);
        int pageSepIndex = findPageSepIndex(buffer.toString(), attributeSep, pageSep);

        StringTokenizer tokenizer = new StringTokenizer(
                buffer.substring(pageSepIndex + pageSep.length()), attributeSep);
        String[] attributes = new String[tokenizer.countTokens()];
        for (int i = 0; tokenizer.hasMoreTokens(); i++) {
            attributes[i] = tokenizer.nextToken();
        }
        //String[] attributes = buffer.substring(pageSepIndex + pageSep.length()).split(attributeSep);
        //buffer = new StringBuilder("[ ").append(Res.getString("PAGE")).append(" ").append(attributes[0]).append("  ");
        buffer = new StringBuilder("[ ").append(sPage).append(" ").append(attributes[0]).append("  ");
        boolean goToFileReached = false;
        int i = OPEN + 1;
        for (; i < attributes.length; i++) {
            buffer.append(attributes[i]).append(" ");
            if (attributes[i].equalsIgnoreCase("GoToFile")) {
                goToFileReached = true;
                break;
            }
        }
        //necessary to handle file names containing attributesSep
        if (goToFileReached) {
            buffer.append('\"');
//            for ( i++; i < attributes.length; i++) {
//                buffer.append(attributes[i]);
//                if (i < (attributes.length - 1)) {
//                    buffer.append(attributeSep);
//                }
//            }
            buffer.append(getRemoteFilePath());
            buffer.append('\"');
        }

        buffer.append("]");

        return buffer.toString();
    }

    public String getExtendedDescriptionNoTitle(String prepend, String pageSeparator, String attributeSeparator,
            boolean useThousandths) {

        if (pageSeparator == null) {
            pageSeparator = pageSep;
        }

        if (attributeSeparator == null) {
            attributeSeparator = attributeSep;
        }

        StringBuilder buffer = new StringBuilder(pageSeparator);
        buffer.append(pageNumber);
        buffer.append(attributeSeparator);
        buffer.append(Colors.colorToString(color));
        buffer.append(attributeSeparator);
        if (bold) {
            buffer.append("bold");
        } else {
            buffer.append("notBold");
        }
        buffer.append(attributeSeparator);
        if (italic) {
            buffer.append("italic");
        } else {
            buffer.append("notItalic");
        }
        buffer.append(attributeSeparator);
        if (opened) {
            buffer.append("open");
        } else {
            buffer.append("closed");
        }
        buffer.append(attributeSeparator);
        buffer.append(type);

        if (type != BookmarkType.FitPage && type != BookmarkType.Unknown
                && type != BookmarkType.FitContent) {
            buffer.append(attributeSeparator);
        }

        if (type == BookmarkType.FitWidth) {
            if (useThousandths) {
                buffer.append(thousandthsTop);
            } else {
                buffer.append(top);
            }
        } else if (type == BookmarkType.FitContentWidth) {
            if (useThousandths) {
                buffer.append(thousandthsTop);
            } else {
                buffer.append(top);
            }
        } else if (type == BookmarkType.FitHeight) {
            if (useThousandths) {
                buffer.append(thousandthsLeft);
            } else {
                buffer.append(left);
            }
        } else if (type == BookmarkType.FitContentHeight) {
            if (useThousandths) {
                buffer.append(thousandthsLeft);
            } else {
                buffer.append(left);
            }
        } else if (type == BookmarkType.TopLeft) {
            if (useThousandths) {
                buffer.append(thousandthsTop);
                buffer.append(attributeSeparator);
                buffer.append(thousandthsLeft);
            } else {
                buffer.append(top);
                buffer.append(attributeSeparator);
                buffer.append(left);
            }
        } else if (type == BookmarkType.TopLeftZoom) {
            if (useThousandths) {
                buffer.append(thousandthsTop);
                buffer.append(attributeSeparator);
                buffer.append(thousandthsLeft);
            } else {
                buffer.append(top);
                buffer.append(attributeSeparator);
                buffer.append(left);
            }
            buffer.append(attributeSeparator);
            buffer.append(zoom);
        } else if (type == BookmarkType.FitRect) {
            if (useThousandths) {
                buffer.append(thousandthsTop);
                buffer.append(attributeSeparator);
                buffer.append(thousandthsLeft);
                buffer.append(attributeSeparator);
                buffer.append(thousandthsBottom);
                buffer.append(attributeSeparator);
                buffer.append(thousandthsRight);
            } else {
                buffer.append(top);
                buffer.append(left);
                buffer.append(bottom);
                buffer.append(right);
            }
        } else if (type == BookmarkType.Named) {
            buffer.append(namedDestination);
        } else if (type == BookmarkType.Uri) {
            buffer.append(uri);
        } else if (type == BookmarkType.Launch) {
            buffer.append(getFileToLaunch());
        } else if (type == BookmarkType.Hide) {
            buffer.append(getFieldNameToHide());
            buffer.append(attributeSeparator);
            buffer.append(isHide());
        } else if (type == BookmarkType.GoToFile) {
        }

        if (isRemoteDestination()) {
            buffer.append(attributeSeparator);
            buffer.append(BookmarkType.GoToFile.toString());
            buffer.append(attributeSeparator);
            buffer.append(remoteFilePath);
        }

        if (!chainedBookmarks.isEmpty()) {
            String newLine = System.getProperty("line.separator");
            for (Bookmark b : chainedBookmarks) {
                buffer.append(newLine);
                buffer.append(prepend);
                buffer.append(b.getExtendedDescriptionNoTitle(prepend, pageSeparator, attributeSeparator, useThousandths));
            }
        }

        return buffer.toString();

    }

    public ArrayList<Bookmark> getChainedBookmarks() {
        return chainedBookmarks;
    }

    public void setChainedBookmarks(ArrayList<Bookmark> chained) {
        chainedBookmarks = chained;
    }

    public String getExtendedDescription(String indentationForHierarchy, String pageSeparator,
            String attributeSeparator, boolean useThousandths) {

        //indent chained bookmarks to be aligned with the first
        StringBuilder indentToAlignChainedBookmark = new StringBuilder();
        if (indentationForHierarchy != null) {
            indentToAlignChainedBookmark.append(indentationForHierarchy);
        }
        for (int i = 0; i < title.length(); i++) {
            indentToAlignChainedBookmark.append(" ");
        }
        return title + getExtendedDescriptionNoTitle(indentToAlignChainedBookmark.toString(), pageSeparator, attributeSeparator, useThousandths);
    }

    private static boolean isBetweenAttributesSeparator(String line, String token, int tokenIndex, String attributesSeparator) {

        boolean betweenAttributesSeparator = false;

        //check if after the token an attributes separator is present
        for (int i = (tokenIndex + token.length()); i < line.length(); i++) {
            char c = line.charAt(i);
            if (!Character.isWhitespace(c)) {
                if (line.substring(i, i + attributesSeparator.length()).equals(attributesSeparator)) {
                    betweenAttributesSeparator = true;
                    break;
                }
            }
        }

        //check if before the token an attributes separator is present
        for (int i = tokenIndex; i >= 0; i--) {
            char c = line.charAt(i);
            if (!Character.isWhitespace(c)) {
                if (line.substring(i - attributesSeparator.length() + 1, i).equals(attributesSeparator)) {
                    betweenAttributesSeparator = true;
                    break;
                }
            }
        }

        return betweenAttributesSeparator;
    }

    private static int findProblematicBookmarksTypesIndex(String line, String attributesSeparator) {

        String[] types = new String[]{BookmarkType.GoToFile.toString(),
            BookmarkType.Launch.toString(), BookmarkType.Uri.toString()};

        int typeIndex = -1;
        for (String type : types) {
            typeIndex = line.lastIndexOf(type);
            if (typeIndex != -1
                    && isBetweenAttributesSeparator(line, type, typeIndex, attributesSeparator)) {
                break;
            }
        }
        return typeIndex;
    }

    private static int findPageSepIndex(String line, String attributesSeparator, String pageSeparator) {
        //if there is a GoToFile or Launch or Uri we must be sure the pageSeparator is
        //before the bookmark type attribute otherwise could be a path separator
        int lastCharToScan = findProblematicBookmarksTypesIndex(line, attributesSeparator);
        int pageSepIndex;
        if (lastCharToScan != -1) {
            //further trying to avoid that Launch Uri or GoToFIle are part of the tile or file name
            //for that check if they are followed and preceeded by attribute separator
            pageSepIndex = line.substring(0, lastCharToScan).lastIndexOf(pageSeparator);
        } else {
            pageSepIndex = line.lastIndexOf(pageSeparator);
        }
        return pageSepIndex;
    }

    public static Bookmark bookmarkFromString(Bookmark chainedBookmarkFather, IBookmarksConverter converter,
            String line, String indentation, String pageSeparator,
            String attributesSeparator) {

        boolean wellFormed = true;

        Bookmark bookmark = new Bookmark();

        while (line.startsWith(indentation)) {
            line = line.replaceFirst(indentation, "");
        }

        int pageSepIndex = findPageSepIndex(line, attributesSeparator, pageSeparator);

        String title = line;
        String attributes = null;

        if (pageSepIndex != -1) {
            title = line.substring(0, pageSepIndex);
            attributes = line.substring(pageSepIndex + pageSeparator.length());
        } else {
            wellFormed = false;
        }

        bookmark.setTitle(title.trim());
        if (attributes == null) {
            return bookmark;
        }

        //String[] tokens = attributes.split(attributesSeparator);
        StringTokenizer tokenizer = new StringTokenizer(attributes,
                attributesSeparator);
        String[] tokens = new String[tokenizer.countTokens()];
        for (int i = 0; tokenizer.hasMoreTokens(); i++) {
            tokens[i] = tokenizer.nextToken();
        }

        try {
            bookmark.setPageNumber(Integer.parseInt(tokens[PAGE]));
        } catch (Exception e) {
            wellFormed = false;
        }

        if (tokens.length < 2) {
            return bookmark;
        }

        boolean shortFormat = false;
        BookmarkType type = BookmarkType.FitWidth;
        try {
            type = BookmarkType.valueOf(tokens[1].trim());
            shortFormat = true;
        } catch (Exception ex) {
        }

        int typeIndex = 1;
        if (!shortFormat) {
            try {
                bookmark.setColor(Colors.stringToColor(tokens[COLOR].trim()));
                bookmark.setBold(tokens[BOLD].trim().equalsIgnoreCase("bold"));
                bookmark.setItalic(tokens[ITALIC].trim().equalsIgnoreCase("italic"));
                bookmark.setOpened(tokens[OPEN].trim().equalsIgnoreCase("open"));
                type = BookmarkType.valueOf(tokens[OPEN + 1].trim());
            } catch (Exception e) {
                wellFormed = false;
            }
            typeIndex = OPEN + 1;
        }

        bookmark.setType(type);
        switch (type) {
            case FitWidth:
            case FitContentWidth:
                try {
                    //bookmark.setThousandthsTop(Integer.parseInt(tokens[typeIndex + 1]));
                    bookmark.setThousandthsTop(Integer.parseInt(tokens[++typeIndex]));
                    bookmark.setTop(verticalFromThousandths(bookmark.getThousandthsTop(),
                            converter.getPageHeight(bookmark.getPageNumber())));
                } catch (NumberFormatException e) {
                    wellFormed = false;
                } catch (Exception e) {
                }
                break;
            case FitHeight:
            case FitContentHeight:
                try {
                    //bookmark.setThousandthsLeft(Integer.parseInt(tokens[typeIndex + 1]));
                    bookmark.setThousandthsLeft(Integer.parseInt(tokens[++typeIndex]));
                    bookmark.setLeft(horizontalFromThousandths(bookmark.getThousandthsLeft(),
                            converter.getPageWidth(bookmark.getPageNumber())));
                } catch (NumberFormatException e) {
                    wellFormed = false;
                } catch (Exception e) {
                }
                break;
            case TopLeft:
            case TopLeftZoom:
                try {
                    //bookmark.setThousandthsTop(Integer.parseInt(tokens[typeIndex + 1]));
                    bookmark.setThousandthsTop(Integer.parseInt(tokens[++typeIndex]));
                    bookmark.setTop(verticalFromThousandths(bookmark.getThousandthsTop(),
                            converter.getPageHeight(bookmark.getPageNumber())));
                    //bookmark.setThousandthsLeft(Integer.parseInt(tokens[typeIndex + 2]));
                    bookmark.setThousandthsLeft(Integer.parseInt(tokens[++typeIndex]));
                    bookmark.setLeft(horizontalFromThousandths(bookmark.getThousandthsLeft(),
                            converter.getPageWidth(bookmark.getPageNumber())));
                    //bookmark.setZoom(Float.parseFloat(tokens[typeIndex + 3]));
                    bookmark.setZoom(Float.parseFloat(tokens[++typeIndex]));
                } catch (NumberFormatException e) {
                    wellFormed = false;
                } catch (Exception e) {
                }
                break;
            case FitRect:
                try {
                    //bookmark.setThousandthsTop(Integer.parseInt(tokens[typeIndex + 1]));
                    bookmark.setThousandthsTop(Integer.parseInt(tokens[++typeIndex]));
                    bookmark.setTop(verticalFromThousandths(bookmark.getThousandthsTop(),
                            converter.getPageHeight(bookmark.getPageNumber())));
                    //bookmark.setThousandthsLeft(Integer.parseInt(tokens[typeIndex + 2]));
                    bookmark.setThousandthsLeft(Integer.parseInt(tokens[++typeIndex]));
                    bookmark.setLeft(horizontalFromThousandths(bookmark.getThousandthsLeft(),
                            converter.getPageWidth(bookmark.getPageNumber())));
                    //bookmark.setThousandthsBottom(Integer.parseInt(tokens[typeIndex + 3]));
                    bookmark.setThousandthsBottom(Integer.parseInt(tokens[++typeIndex]));
                    bookmark.setBottom(verticalFromThousandths(bookmark.getThousandthsBottom(),
                            converter.getPageHeight(bookmark.getPageNumber())));
                    //bookmark.setThousandthsRight(Integer.parseInt(tokens[typeIndex + 4]));
                    bookmark.setThousandthsRight(Integer.parseInt(tokens[++typeIndex]));
                    bookmark.setRight(horizontalFromThousandths(bookmark.getThousandthsRight(),
                            converter.getPageWidth(bookmark.getPageNumber())));
                } catch (NumberFormatException e) {
                    wellFormed = false;
                } catch (Exception e) {
                }
                break;
            case Named:
                try {
                    //bookmark.setNamedDestination(tokens[typeIndex + 1].trim());
                    bookmark.setNamedDestination(tokens[++typeIndex].trim());
                } catch (Exception e) {
                    wellFormed = false;
                }
                break;
            case Uri:
                try {
                    //bookmark.setUri(tokens[typeIndex + 1].trim());
                    bookmark.setUri(tokens[++typeIndex].trim());
                } catch (Exception e) {
                    wellFormed = false;
                }
                break;
            case Launch:
                try {
                    bookmark.setFileToLaunch(tokens[++typeIndex].trim());
                } catch (Exception e) {
                    wellFormed = false;
                }
                break;
            case Hide:
                try {
                    bookmark.setFieldNameToHide(tokens[++typeIndex].trim());
                    bookmark.setHide(!(tokens[++typeIndex].trim().equalsIgnoreCase("false")));
                } catch (Exception e) {
                    wellFormed = false;
                }
        }

        try {
            if (tokens[++typeIndex].trim().equalsIgnoreCase(BookmarkType.GoToFile.toString())) {
                bookmark.setRemoteDestination(true);
                StringBuilder remotePath = new StringBuilder();
                //if the path contains the attributes separator we must add all
                //remaining tokens to get the correct path
                remotePath.append(tokens[++typeIndex]);
                for (int i = ++typeIndex; i < tokens.length; i++) {
                    remotePath.append(attributesSeparator);
                    remotePath.append(tokens[i]);
                }
                bookmark.setRemoteFilePath(remotePath.toString().trim());
                IBookmarksConverter remoteFileConverter = getBookmarksConverter();
                if (remoteFileConverter != null) {
                    try {
                        File absoluteRemoteFile = Ut.createAbsolutePath(new File(converter.getOpenedFilePath()),
                                new File(bookmark.getRemoteFilePath()));
                        remoteFileConverter.open(absoluteRemoteFile.getAbsolutePath());
                        bookmark.setTop(verticalFromThousandths(bookmark.getThousandthsTop(),
                                remoteFileConverter.getPageHeight(bookmark.getPageNumber())));
                        bookmark.setLeft(horizontalFromThousandths(bookmark.getThousandthsLeft(),
                                remoteFileConverter.getPageWidth(bookmark.getPageNumber())));
                        remoteFileConverter.close();
                    } catch (IOException ex) {
                    }
                }
            }
        } catch (Exception e) {
        }

        if (!wellFormed) {
            bookmark.setColor(Color.red);
            //bookmark.setTitle(Res.getString("PARSE_ERROR") + " " + bookmark.getTitle());
            bookmark.setTitle(parseError + " " + bookmark.getTitle());
        }

        if (bookmark.getTitle().isEmpty() && chainedBookmarkFather != null) {
            chainedBookmarkFather.addChainedBookmark(bookmark);
            return null;
        }

        return bookmark;
    }

    public static Bookmark outlineFromFile(IBookmarksConverter converter,
            String bookmarksFile, String indentation, String pageSeparator,
            String attributesSeparator, String charset)
            throws FileNotFoundException, IOException {

        ArrayList<Bookmark> fathers = new ArrayList<Bookmark>(8);


        Bookmark newOutline = new Bookmark();
        fathers.add(newOutline);

        //BufferedReader br = new BufferedReader(new FileReader(bookmarksFile));
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(bookmarksFile), charset));

        String line;
        int fatherIndex = 0;
        int newFatherIndex = 1;
        Bookmark current = null;
        Bookmark father = null;
        Bookmark chainedBookmarkFather = null;
        int numLine = 0;
        while ((line = br.readLine()) != null) {
            numLine++;
            line = Ut.rtrim(line);
            if (line.isEmpty()) {
                continue;
            }
            current = bookmarkFromString(chainedBookmarkFather, converter, line, indentation, pageSeparator,
                    attributesSeparator);
            //null is returned in case of chained bookmark
            if (current != null) {
                if (line.lastIndexOf(indentation) <= 0) {
                    fatherIndex = line.lastIndexOf(indentation) + 1;
                } else {
                    fatherIndex = line.lastIndexOf(indentation) / indentation.length() + 1;
                }
                newFatherIndex = fatherIndex + 1;
                if (fatherIndex >= fathers.size()) {
                    father = fathers.get(fathers.size() - 1);
                } else {
                    father = fathers.get(fatherIndex);
                }
                father.add(current);
                chainedBookmarkFather = current;
                if (newFatherIndex < fathers.size()) {
                    fathers.remove(newFatherIndex);
                    fathers.add(newFatherIndex, current);
                } else {
                    fathers.add(current);
                }
            }
        }
        br.close();
        return newOutline;
    }

    public static IBookmarksConverter getBookmarksConverter() {
        ServiceLoader<IBookmarksConverter> s = ServiceLoader.load(IBookmarksConverter.class);
        Iterator<IBookmarksConverter> i = s.iterator();
        if (i.hasNext()) {
            return i.next();
        }
        //return new iTextBookmarksConverter();
        return null;
    }

    public static Bookmark outlineFromBufferedReader(IBookmarksConverter converter,
            BufferedReader br, String indentation, String pageSeparator,
            String attributesSeparator) throws IOException {

        ArrayList<Bookmark> fathers = new ArrayList<Bookmark>(8);
        Bookmark newOutline = new Bookmark();

        fathers.add(newOutline);

        String line;
        int fatherIndex = 0;
        int newFatherIndex = 1;
        Bookmark current = null;
        Bookmark father = null;
        int numLine = 0;
        while ((line = br.readLine()) != null) {
            numLine++;
            line = Ut.rtrim(line);
            if (line.isEmpty()) {
                continue;
            }
            if (line.lastIndexOf(indentation) <= 0) {
                fatherIndex = line.lastIndexOf(indentation) + 1;
            } else {
                fatherIndex = line.lastIndexOf(indentation) / indentation.length() + 1;
            }
            newFatherIndex = fatherIndex + 1;
            current = bookmarkFromString(current, converter, line, indentation, pageSeparator,
                    attributesSeparator);
            if (fatherIndex >= fathers.size()) {
                father = fathers.get(fathers.size() - 1);
            } else {
                father = fathers.get(fatherIndex);
            }
            father.add(current);

            if (newFatherIndex < fathers.size()) {
                fathers.remove(newFatherIndex);
                fathers.add(newFatherIndex, current);
            } else {
                fathers.add(current);
            }
        }
        br.close();
        return newOutline;

    }

    public static Bookmark outlineFromString(IBookmarksConverter converter,
            String bookmarks, String indentation, String pageSeparator,
            String attributesSeparator)
            throws IOException {


        BufferedReader br = new BufferedReader(new StringReader(bookmarks));

        return outlineFromBufferedReader(converter, br, indentation, pageSeparator, attributesSeparator);
    }

    @Override
    public String toString() {
        return title;
    }

    public void setThousandthsRight(int thousandthsHorizontal) {
        this.thousandthsRight = thousandthsHorizontal;
    }

    public void setThousandthsBottom(int thousandthsVertical) {
        this.thousandthsBottom = thousandthsVertical;
    }

    private int getThousandthsBottom() {
        return thousandthsBottom;
    }

    private int getThousandthsRight() {
        return thousandthsRight;
    }

    public static int thousandthsHorizontal(int x, float width) {
        int thousandths = -1;
        if (x != -1) {
            try {
                thousandths = (int) (x * 1000.0f / width);
            } catch (Exception exc) {
            }
        }
        return thousandths;
    }

    public static int thousandthsVertical(int y, float height) {
        int thousandths = -1;
        if (y != -1) {
            try {
                thousandths = 1000 - Math.round(y * 1000.0f / height);
            } catch (Exception exc) {
            }
        }
        return thousandths;
    }

    public static int verticalFromThousandths(int thousandths, float height) {
        return (thousandths == -1) ? -1 : Math.round((1000 - thousandths) * height / 1000.0f);
    }

    public static int horizontalFromThousandths(int thousandths, float width) {
        return (thousandths == -1) ? -1 : Math.round(thousandths * width / 1000.0f);
    }

    public void setRemoteFilePathWithChildren(File file) {
        String path = Ut.onWindowsReplaceBackslashWithSlash(file.getPath());
        Enumeration<Bookmark> e = this.preorderEnumeration();
        while (e.hasMoreElements()) {
            Bookmark b = e.nextElement();
            setRemoteTarget(b, path);
        }
    }

    private void setRemoteTarget(Bookmark b, String path) {
        if (b.type != BookmarkType.Launch && b.type != BookmarkType.Uri
                && !b.isRemoteDestination()) {
            b.setRemoteDestination(true);
            b.setRemoteFilePath(path);
        }

        for (Bookmark chained : b.getChainedBookmarks()) {
            setRemoteTarget(chained, path);
        }
    }
}
