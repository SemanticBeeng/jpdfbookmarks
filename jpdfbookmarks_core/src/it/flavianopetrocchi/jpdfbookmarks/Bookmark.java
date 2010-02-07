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
package it.flavianopetrocchi.jpdfbookmarks;

import it.flavianopetrocchi.colors.Colors;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.tree.DefaultMutableTreeNode;

public class Bookmark extends DefaultMutableTreeNode {

    private static final int PAGE = 0,
            COLOR = 1,
            BOLD = 2,
            ITALIC = 3,
            OPEN = 4;
    private String title = Res.getString("DEFAULT_TITLE");
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
    private boolean opened = false;
    private float pageHeight = Float.NaN;
    private float pageWidth = Float.NaN;
    private String uri;
    private String remoteFilePath;
    private String fileToLaunch;
    private boolean newWindow;
    private boolean remoteDestination = false;
    private boolean namedAsName = false;
    private Bookmark namedTarget;
    private static IBookmarksConverter converter;
//	private long pageWidth;
//	private long pageHeight;

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

    public String getFileToLaunch() {
        return fileToLaunch;
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
        setNewWindow(bookmark.isNewWindow());
        setRemoteDestination(bookmark.isRemoteDestination());
        setRemoteFilePath(bookmark.getRemoteFilePath());
        setUri(bookmark.getUri());
        setThousandthsBottom(bookmark.getThousandthsBottom());
        setThousandthsRight(bookmark.getThousandthsRight());
        setThousandthsTop(bookmark.getThousandthsTop());
        setThousandthsLeft(bookmark.getThousandthsLeft());
    }

    public Bookmark(Object outlineNode) {
        super(outlineNode);
    }

    public Bookmark() {
    }

    public String getDescription(boolean useThousandths) {
        StringBuilder buffer = new StringBuilder(getExtendedDescription(null,
                null, useThousandths));
        int pageSepIndex = buffer.lastIndexOf(pageSep);
        StringTokenizer tokenizer = new StringTokenizer(
                buffer.substring(pageSepIndex + pageSep.length()), attributeSep);
        String[] attributes = new String[tokenizer.countTokens()];
        for (int i = 0; tokenizer.hasMoreTokens(); i++) {
            attributes[i] = tokenizer.nextToken();
        }
        //String[] attributes = buffer.substring(pageSepIndex + pageSep.length()).split(attributeSep);
        buffer = new StringBuilder("[ ").append(Res.getString("PAGE")).append(" ").append(attributes[0]).append("  ");
        for (int i = OPEN + 1; i < attributes.length; i++) {
            buffer.append(attributes[i]).append(" ");
        }
        buffer.append("]");

        return buffer.toString();
    }

    public String getExtendedDescription(String pageSeparator,
            String attributeSeparator, boolean useThousandths) {

        if (pageSeparator == null) {
            pageSeparator = pageSep;
        }

        if (attributeSeparator == null) {
            attributeSeparator = attributeSep;
        }

        StringBuilder buffer = new StringBuilder(title);
        buffer.append(pageSeparator);
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

        if (type != BookmarkType.FitPage && type != BookmarkType.Unknown &&
                type != BookmarkType.FitContent) {
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
            buffer.append(fileToLaunch);
        } else if (type == BookmarkType.GoToFile) {
        }
        return buffer.toString();
    }

    public static Bookmark bookmarkFromString(IBookmarksConverter converter,
            String line, String indentation, String pageSeparator,
            String attributesSeparator) {

        boolean wellFormed = true;

        Bookmark bookmark = new Bookmark();

        while (line.startsWith(indentation)) {
            line = line.replaceFirst(indentation, "");
        }

        int pageSepIndex = line.lastIndexOf(pageSeparator);
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
                    bookmark.setThousandthsTop(Integer.parseInt(tokens[typeIndex + 1]));
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
                    bookmark.setThousandthsLeft(Integer.parseInt(tokens[typeIndex + 1]));
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
                    bookmark.setThousandthsTop(Integer.parseInt(tokens[typeIndex + 1]));
                    bookmark.setTop(verticalFromThousandths(bookmark.getThousandthsTop(),
                            converter.getPageHeight(bookmark.getPageNumber())));
                    bookmark.setThousandthsLeft(Integer.parseInt(tokens[typeIndex + 2]));
                    bookmark.setLeft(horizontalFromThousandths(bookmark.getThousandthsLeft(),
                            converter.getPageWidth(bookmark.getPageNumber())));
                    bookmark.setZoom(Float.parseFloat(tokens[typeIndex + 3]));
                } catch (NumberFormatException e) {
                    wellFormed = false;
                } catch (Exception e) {
                }
                break;
            case FitRect:
                try {
                    bookmark.setThousandthsTop(Integer.parseInt(tokens[typeIndex + 1]));
                    bookmark.setTop(verticalFromThousandths(bookmark.getThousandthsTop(),
                            converter.getPageHeight(bookmark.getPageNumber())));
                    bookmark.setThousandthsLeft(Integer.parseInt(tokens[typeIndex + 2]));
                    bookmark.setLeft(horizontalFromThousandths(bookmark.getThousandthsLeft(),
                            converter.getPageWidth(bookmark.getPageNumber())));
                    bookmark.setThousandthsBottom(Integer.parseInt(tokens[typeIndex + 3]));
                    bookmark.setBottom(verticalFromThousandths(bookmark.getThousandthsBottom(),
                            converter.getPageHeight(bookmark.getPageNumber())));
                    bookmark.setThousandthsRight(Integer.parseInt(tokens[typeIndex + 4]));
                    bookmark.setRight(horizontalFromThousandths(bookmark.getThousandthsRight(),
                            converter.getPageWidth(bookmark.getPageNumber())));
                } catch (NumberFormatException e) {
                    wellFormed = false;
                } catch (Exception e) {
                }
                break;
            case Named:
                try {
                    bookmark.setNamedDestination(tokens[typeIndex + 1].trim());
                } catch (Exception e) {
                    wellFormed = false;
                }
                break;
            case Uri:
                try {
                    bookmark.setUri(tokens[typeIndex + 1].trim());
                } catch (Exception e) {
                    wellFormed = false;
                }
                break;
        }

        if (!wellFormed) {
            bookmark.setColor(Color.red);
            bookmark.setTitle(Res.getString("PARSE_ERROR") + " " + bookmark.getTitle());
        }

        return bookmark;
    }

    public static Bookmark outlineFromFile(IBookmarksConverter converter, String bookmarksFile)
            throws FileNotFoundException, IOException {
        return outlineFromFile(converter, bookmarksFile, "\t", "/", ",");
    }

    public static Bookmark outlineFromFile(IBookmarksConverter converter,
            String bookmarksFile, String indentation, String pageSeparator,
            String attributesSeparator)
            throws FileNotFoundException, IOException {

        ArrayList<Bookmark> fathers = new ArrayList<Bookmark>(8);


        Bookmark newOutline = new Bookmark();
        fathers.add(newOutline);

        BufferedReader br = new BufferedReader(new FileReader(bookmarksFile));

        String line;
        int fatherIndex = 0;
        int newFatherIndex = 1;
        Bookmark current = null;
        Bookmark father = null;
        int numLine = 0;
        while ((line = br.readLine()) != null) {
            numLine++;
            if (line.trim().isEmpty()) {
                continue;
            }
            if (line.lastIndexOf(indentation) <= 0) {
                fatherIndex = line.lastIndexOf(indentation) + 1;
            } else {
                fatherIndex = line.lastIndexOf(indentation) / indentation.length() + 1;
            }
            newFatherIndex = fatherIndex + 1;
            current = bookmarkFromString(converter, line, indentation, pageSeparator,
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
}
