/*
 * ExtendedUndoManager.java
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

import javax.swing.event.*;
import javax.swing.undo.*;
import java.util.Vector;

public class ExtendedUndoManager extends UndoManager
        implements UndoableEditListener {

    private ExtendedUndoableEditSupport support =
            new ExtendedUndoableEditSupport();
    private Object source; // The source of the last edit

    // Return the complete list of edits in an array.
    public synchronized UndoableEdit[] getEdits() {
        UndoableEdit[] array = new UndoableEdit[edits.size()];
        edits.copyInto(array);
        return array;
    }

    // Return all currently significant undoable edits. The first edit is the
    // next one to be undone.
    public synchronized UndoableEdit[] getUndoableEdits() {
        int size = edits.size();
        Vector v = new Vector(size);
        for (int i = size - 1; i >= 0; i--) {
            UndoableEdit u = (UndoableEdit) edits.elementAt(i);
            if (u.canUndo() && u.isSignificant()) {
                v.addElement(u);
            }
        }
        UndoableEdit[] array = new UndoableEdit[v.size()];
        v.copyInto(array);
        return array;
    }

    // Return all currently significant redoable edits. The first edit is the
    // next one to be redone.
    public synchronized UndoableEdit[] getRedoableEdits() {
        int size = edits.size();
        Vector v = new Vector(size);
        for (int i = 0; i < size; i++) {
            UndoableEdit u = (UndoableEdit) edits.elementAt(i);
            if (u.canRedo() && u.isSignificant()) {
                v.addElement(u);
            }
        }
        UndoableEdit[] array = new UndoableEdit[v.size()];
        v.copyInto(array);
        return array;
    }
    // UndoableEditListener Method Support (ExtendedUndoManager.java, part 2)
    //

    // Add an edit and notify our listeners.
    public synchronized boolean addEdit(UndoableEdit anEdit) {
        boolean b = super.addEdit(anEdit);
        if (b) {
            support.postEdit(anEdit); // If the edit was added, notify listeners.
        }
        return b;
    }

    // When an edit is sent to us, call addEdit() to notify any of our listeners.
    public synchronized void undoableEditHappened(UndoableEditEvent ev) {
        UndoableEdit ue = ev.getEdit();
        source = ev.getSource();
        addEdit(ue);
    }

    // Add a listener to be notified each time an edit is added to this manager.
    // This makes it easy to update undo/redo menus as edits are added.
    public synchronized void addUndoableEditListener(UndoableEditListener l) {
        support.addUndoableEditListener(l);
    }

    // Remove a listener from this manager.
    public synchronized void removeUndoableEditListener(UndoableEditListener l) {
        support.removeUndoableEditListener(l);
    }

    // A simple extension of UndoableEditSupport that lets us specify the event
    // source each time we post an edit
    class ExtendedUndoableEditSupport extends UndoableEditSupport {

        // Post an edit to added listeners.
        @Override
        public synchronized void postEdit(UndoableEdit ue) {
            realSource = source; // From our enclosing manager object
            super.postEdit(ue);
        }
    }
}
