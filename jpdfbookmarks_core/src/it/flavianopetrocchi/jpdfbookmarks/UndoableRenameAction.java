/*
 * UndoableRenameAction.java
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
import java.util.ArrayList;
import javax.swing.JTree;
import javax.swing.undo.CannotUndoException;

public class UndoableRenameAction extends UndoableBookmarksAction {

    ArrayList<Bookmark> list;
    ArrayList<String> oldtTtles = new ArrayList<String>();
    String title;

    public UndoableRenameAction(JTree tree, String title) {
        super(tree);
        list = getSelectedBookmarks();
        this.title = title;
    }

    @Override
    public void doEdit() {
        super.doEdit();

        if ((title != null) && (title.length() > 0)) {
            for (Bookmark b : list) {
                oldtTtles.add(b.getTitle());
                b.setTitle(title);
            }
        }
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();

        for (int i = 0; i < list.size(); i++) {
            list.get(i).setTitle(oldtTtles.get(i));
        }
    }
}
