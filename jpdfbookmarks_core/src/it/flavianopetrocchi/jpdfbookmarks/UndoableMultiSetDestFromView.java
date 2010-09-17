/*
 * UndoableMultipleSetDestination.java
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
import javax.swing.JTree;

public class UndoableMultiSetDestFromView extends UndoableMultiSetDest {

    private boolean excludePageNumber;
    private Bookmark dest;

    public UndoableMultiSetDestFromView(JTree tree, int addOrReplace, Bookmark dest, boolean excludePageNumber) {
        super(tree, addOrReplace);
        this.excludePageNumber = excludePageNumber;
        this.dest = dest;
    }

    @Override
    public void doEdit() {
        for (Bookmark b : selectedBookmarks) {
            int oldPageNumber = b.getPageNumber();
            if (addOrReplace == REPLACE) {
                b.clearChainedBookmarks();
                b.cloneDestination(dest);
                if (excludePageNumber) {
                    b.setPageNumber(oldPageNumber);
                }
            } else {
                Bookmark copy = Bookmark.cloneBookmark(dest, false);
                if (excludePageNumber) {
                    copy.setPageNumber(oldPageNumber);
                }
                b.addChainedBookmark(copy);
            }
        }
    }

}
