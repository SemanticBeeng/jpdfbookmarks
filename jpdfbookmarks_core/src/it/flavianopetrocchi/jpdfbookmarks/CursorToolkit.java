/*
 * CursorToolkit.java
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** Basic CursorToolkit that swallows mouseclicks */
public class CursorToolkit {

    private final static MouseAdapter mouseAdapter = new MouseAdapter() {
    };
    private final static KeyAdapter keyAdapter = new KeyAdapter() {
    };

    private CursorToolkit() {
    }

    /** Sets cursor for specified component to Wait cursor */
    public static void startWaitCursor(JComponent component) {
        RootPaneContainer root =
                ((RootPaneContainer) component.getTopLevelAncestor());
        root.getGlassPane().setCursor(
                Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        root.getGlassPane().addMouseListener(mouseAdapter);
        root.getGlassPane().addMouseMotionListener(mouseAdapter);
        root.getGlassPane().addKeyListener(keyAdapter);
        root.getGlassPane().setVisible(true);
    }

    /** Sets cursor for specified component to normal cursor */
    public static void stopWaitCursor(JComponent component) {
        RootPaneContainer root =
                ((RootPaneContainer) component.getTopLevelAncestor());
        root.getGlassPane().setCursor(Cursor.getDefaultCursor());
        root.getGlassPane().removeMouseListener(mouseAdapter);
        root.getGlassPane().removeMouseMotionListener(mouseAdapter);
        root.getGlassPane().removeKeyListener(keyAdapter);
        root.getGlassPane().setVisible(false);
    }
}

