/*
 * IBookmarksConverter.java
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

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public interface IBookmarksConverter {
    
    public void close() throws IOException;

    /**
     * Constructs the bookmarks hierarchy and returns the root bookmark of the pdf
     * or null if there are no bookmarks.
     *
     * @param convertNamedDestinations Whether to convert named destinations to explicit destinations.
     * @return The root bookmark of the pdf, or null if the pdf has no bookmarks.
     */
    public Bookmark getRootBookmark(boolean convertNamedDestinations);

    public boolean isEncryped();

    public boolean isBookmarksEditingPermitted();

    public void open(String pdfPath) throws IOException;
    public void open(String path, byte[] bytes) throws IOException;

    public void createUnencryptedCopy(File tmpFile) throws IOException;

    /**
     * Create an outline in the pdf from a Bookmark objects hierarchy starting
     * at the root given as parameter.
     *
     * @param root The root Bookmark of the outline.
     */
    public void rebuildBookmarksFromTreeNodes(Bookmark root);

    public void save(String filePath) throws IOException;
    public void save(String path, byte[] ownerPassword) throws IOException;
    public void save(String path, byte[] userPassword, byte[] ownerPassword) throws IOException;

    public boolean showBookmarksOnOpen();

    public void setShowBookmarksOnOpen(boolean show);

    public float getPageWidth(int pageNumber);

    public float getPageHeight(int pageNumber);

    public int getCountOfPages();

    public String getOpenedFilePath();

    public ArrayList<AnnotationRect> getLinks(int page, boolean convertNamedDestinations);

    public class AnnotationRect {
        public Bookmark bookmark;
        public int llx;
        public int lly;
        public int urx;
        public int ury;
    }

}
