/*
 * Dumper.java
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

import it.flavianopetrocchi.jpdfbookmarks.bookmark.IBookmarksConverter;
import it.flavianopetrocchi.jpdfbookmarks.bookmark.Bookmark;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * This class implements the --dump functionality of JPdfBookmarks.
 */
public class Dumper extends OutlinePresentation {

    private static final int BUFF_CAPACITY = 2048;
    Prefs userPrefs = new Prefs();

    public Dumper() {
    }

    /**
     * Create a Dumper object to dump bookmarks from the file passed as
     * argument.
     *
     * @param pdf Bookmarks will be extracted from this file.
     */
    public Dumper(IBookmarksConverter pdf) {
        super(pdf);
    }

    /**
     * Create a Dumper object to dump bookmarks from the file passed as
     * argument.
     *
     * @param pdf Bookmarks will be extracted from this file.
     * @param indent Character used to indent children bookmarks.
     * @param pageSep Character to serparate title from page number.
     * @param attributesSep Character to separate attrubutes of the bookmark.
     */
    public Dumper(IBookmarksConverter pdf, String indent, String pageSep, String attributesSep) {
        super(pdf, indent, pageSep, attributesSep);
    }

    /**
     * Print the bookmarks hierarchy to standard output.
     */
//    public void printBookmarks() {
//        String bookmarks = getBookmarks();
//        PrintWriter out = new PrintWriter(System.out, true);
//        out.println(bookmarks.toString());
//    }
//    public String getBookmarks(Bookmark root) {
//        String bookmarks;
//        if (root != null) {
//            try {
//                Bookmark firstChild = (Bookmark) root.getFirstChild();
//                StringBuffer buffer = new StringBuffer(BUFF_CAPACITY);
//                getBookmarkRecursive(buffer, firstChild, "");
//                bookmarks = buffer.toString();
//            } catch (NoSuchElementException exc) {
//                bookmarks = Res.getString("EMPTY_OUTLINE_FOUND");
//            }
//        } else {
//            bookmarks = Res.getString("NO_BOOKMARK_FOUND");
//        }
//
//        return bookmarks;
//    }
    /**
     * Use to get a text presentation of the bookmarks hierarchy.
     *
     * @return String containing a text presentation ok the bookmarks hierarchy.
     */
//    public String getBookmarks() {
//        IBookmarksConverter converter = super.getPdf();
//        if (converter == null) {
//            return "";
//        }
//        Bookmark root = converter.getRootBookmark();
//        return getBookmarks(root);
//    }
    /**
     * Print the bookmarks hierarchy to the output stream starting from root
     * it needs that the Dumper has been created passing a valid IBookmarksConverter
     *
     * @param out The OutputStreamWriter where to print the bookmarks
     */
    public void printBookmarksIterative(OutputStreamWriter out) {
        IBookmarksConverter converter = super.getPdf();
        if (converter == null) {
            return;
        }
        Bookmark root = converter.getRootBookmark(userPrefs.getConvertNamedDestinations());
        if (root != null) {
            Enumeration preOrder = root.preorderEnumeration();
            printEnumeration(out, preOrder);
        }
    }

    /**
     * Print the bookmarks hierarchy to the output stream starting from the root passed 
     * as argument. It can be used with a Dumper created with a null IBookmarksConverter
     * for cases where a hierarchy of Bookmark types is already available. 
     * 
     * @param out The OutputStreamWriter where to print the bookmarks
     * @param root Bookmark from where to start printing the hierarchy
     */
    public void printBookmarksIterative(OutputStreamWriter out, Bookmark root) {

        if (root != null) {
            Enumeration preOrder = root.preorderEnumeration();
            printEnumeration(out, preOrder);
        }
    }

    /**
     * Create a text presentation ok the bookmarks hierarchy.
     *
     * @param buffer String buffer where we create the presentation.
     * @param bookmark Bookmark from where to start the hierarchy, generally the
     * first child of the root bookmark.
     * @param indentation Indentation increased from parent to child. An empty
     * string on the first call.
     */
//    private void getBookmarkRecursive(StringBuffer buffer, Bookmark bookmark,
//            String indentation) {
//
//        buffer.append(indentation);
//        buffer.append(bookmark.getExtendedDescription(super.getPageSep(),
//                super.getAttributesSep(), userPrefs.getUseThousandths()));
//        buffer.append(NEWLINE);
//
//        Bookmark firstChild = null;
//        try {
//            firstChild = (Bookmark) bookmark.getFirstChild();
//        } catch (NoSuchElementException exc) {
//        }
//
//        if (firstChild != null) {
//            getBookmarkRecursive(buffer, firstChild, indentation
//                    + super.getIndentationString());
//        }
//        Bookmark sibling = null;
//        sibling = (Bookmark) bookmark.getNextSibling();
//        if (sibling != null) {
//            getBookmarkRecursive(buffer, sibling, indentation);
//        }
//    }
    private void printEnumeration(OutputStreamWriter out, Enumeration e) {
        PrintWriter printer = new PrintWriter(out, true);
        boolean useThousandths = userPrefs.getUseThousandths();
        String psep = getPageSep();
        String asep = getAttributesSep();
        String indent = getIndentationString();

        //just skip the root element
        if (e.hasMoreElements()) {
            e.nextElement();
        }

        while (e.hasMoreElements()) {
            Bookmark b = (Bookmark) e.nextElement();
            StringBuilder indentLevel = new StringBuilder();
            for (int i = 0; i < (b.getLevel() - 1); i++) {
                indentLevel.append(indent);
            }
            printer.print(indentLevel);
            printer.println(b.getExtendedDescription(indentLevel.toString(), psep, asep, useThousandths));
        }
    }
}
