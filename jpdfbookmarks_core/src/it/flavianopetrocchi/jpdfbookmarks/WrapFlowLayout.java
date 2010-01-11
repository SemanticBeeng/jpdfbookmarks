/*
 * WrapFlowLayout.java
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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;

/**
 * This Layout is to permit the toolbars panel to increase height and position
 * toolbars in more rows when the frame width diminished.
 */
public class WrapFlowLayout extends FlowLayout {

    WrapFlowLayout(int LEADING) {
        super(LEADING);
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            Dimension dim = new Dimension(0, 0);
            int maxWidth = 0;
            int componentCount = parent.getComponentCount();

            int hgap = super.getHgap();
            int vgap = super.getVgap();
            for (int i = 0; i < componentCount; i++) {
                Component c = parent.getComponent(i);
                if (c.isVisible()) {
                    Dimension d = c.getPreferredSize();
                    if ((dim.width + d.width + hgap) <= parent.getWidth()) {
                        dim.height = Math.max(dim.height, d.height);
                    } else {
                        dim.height += vgap + d.height;
                        dim.width = 0;
                    }
                    if (dim.width > 0) {
                        dim.width += hgap;
                    }
                    dim.width += d.width;
                    if (dim.width > maxWidth) {
                        maxWidth = dim.width;
                    }
                }
            }
            Insets insets = parent.getInsets();
            dim.width = Math.max(dim.width, maxWidth);
            dim.width += insets.left + insets.right + 2 * hgap;
            dim.height += insets.top + insets.bottom + 2 * vgap;
            return dim;
        }
    }
}
