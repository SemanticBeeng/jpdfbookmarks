/*
 * DummyPageProducer.java
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
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public class DummyPageProducer implements IPageProducer {

    private IBookmarksConverter converter;
    private int numberOfPages;

    @Override
    public IPage getPage(int pageNumber) {
        return new Page(pageNumber);
    }

    public void setIBookmarksConverter(IBookmarksConverter converter) {
        this.converter = converter;
        numberOfPages = converter.getCountOfPages();
    }

    private class Page implements IPage {

        private PageDimension size;
        private BufferedImage img;
        private float previousScale;
        private final float A4_WIDTH = 8.27f;
        private final float A4_HEIGHT = 11.7F;

        public Page(int pageNumber) {
            int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
            if (converter != null) {
                float width = converter.getPageWidth(pageNumber);
                float height = converter.getPageHeight(pageNumber);
                size = new PageDimension(width, height);
            } else {
                //An A4 page should be 210 x 297 mm (8.27 x 11.7 in)
                //but is not really important is just to simulate a renderer
                size = new PageDimension(dpi * A4_WIDTH, dpi * A4_HEIGHT);
            }
            img = createImage(1f);
        }

        @Override
        public BufferedImage getImage(float scale) {
            if (scale != previousScale) {
                img = createImage(scale);
                previousScale = scale;
            }
            return img;
        }

        private BufferedImage createImage(float scale) {
            img = new BufferedImage(Math.round(size.getWidth() * scale),
                    Math.round(size.getHeight() * scale), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) img.getGraphics();
            g.setColor(Color.white);
            g.fillRect(0, 0, img.getWidth(), img.getHeight());
            g.setColor(Color.black);
            FontMetrics fontMetrics = g.getFontMetrics();
            String line1 = Res.getString("NOT_A_PDF_PAGE");
            int height = fontMetrics.getHeight();
            int width = fontMetrics.stringWidth(line1);
            g.drawString(line1,
                    (img.getWidth() - width) / 2, img.getHeight() / 2 - height);
            line1 = Res.getString("THIS_IS_A_DUMMY_PAGE");
            width = fontMetrics.stringWidth(line1);
            g.drawString(line1,
                    (img.getWidth() - width) / 2, img.getHeight() / 2 + height);
            g.dispose();
            previousScale = scale;
            return img;
        }

        public PageDimension getSize(float scale) {
            return size;
        }
    }
}
