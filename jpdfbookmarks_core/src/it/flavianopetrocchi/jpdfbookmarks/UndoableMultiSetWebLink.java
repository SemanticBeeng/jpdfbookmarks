/*
 * UndoableMultiSetWebLink.java
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

import it.flavianopetrocchi.jpdfbookmarks.bookmark.Bookmark;
import it.flavianopetrocchi.jpdfbookmarks.bookmark.BookmarkType;
import javax.swing.JTree;

public class UndoableMultiSetWebLink extends UndoableMultiSetDest {

    private String address;

    public UndoableMultiSetWebLink(JTree tree, int addOrReplace, String address) {
        super(tree, addOrReplace);
        this.address = address;
    }

    @Override
    public void doEdit() {
        for (Bookmark b : selectedBookmarks) {
            if (addOrReplace == REPLACE) {
                b.clearChainedBookmarks();
                b.setType(BookmarkType.Uri);
                b.setUri(address);
                b.setPageNumber(-1);
            } else {
                Bookmark webBookmark = new Bookmark();
                webBookmark.setType(BookmarkType.Uri);
                webBookmark.setUri(address);
                b.addChainedBookmark(webBookmark);
            }
        }
    }
}
