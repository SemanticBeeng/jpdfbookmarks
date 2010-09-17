/*
 * BookmarkSelection.java
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

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class BookmarkSelection implements Transferable, ClipboardOwner, Serializable {
    private DataFlavor bookmarkFlavor;
    private DataFlavor[] dataFlavors = new DataFlavor[1];
    private Bookmark bookmark;
    private ArrayList<Bookmark> bookmarks;
    private boolean cut;
    private File file;


    public BookmarkSelection(ArrayList<Bookmark> bookmarks, DataFlavor flavor, boolean cut) {
           bookmarkFlavor = flavor;
           this.bookmarks = bookmarks;
           dataFlavors[0] = bookmarkFlavor;
           this.cut = cut;
    }


    public BookmarkSelection(ArrayList<Bookmark> bookmarks, DataFlavor flavor, boolean cut, File pdfFile) {
        this(bookmarks, flavor, cut);
        this.file = pdfFile;
    }

    public File getFile() {
        return file;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return dataFlavors;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(bookmarkFlavor);
    }

    public ArrayList<Bookmark> getBookmarks() {
        ArrayList<Bookmark> copies = new ArrayList<Bookmark>(bookmarks.size());
        for (Bookmark b : bookmarks) {
            copies.add(Bookmark.cloneBookmark(b, !b.isOpened()));
        }
        return copies;
    }
    
    public boolean isCutOperation() {
        return cut;
    }

    public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(bookmarkFlavor)) {
            return this;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }

}
