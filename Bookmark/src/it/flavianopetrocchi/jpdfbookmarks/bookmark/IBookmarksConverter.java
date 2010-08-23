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

import java.io.IOException;

public interface IBookmarksConverter {

	void close() throws IOException;

	/**
	 * Constructs the bookmarks hierarchy and returns the root bookmark of the pdf
         * or null if there are no bookmarks.
	 *
         * @param convertNamedDestinations Whether to convert named destinations to explicit destinations.
	 * @return The root bookmark of the pdf, or null if the pdf has no bookmarks.
	 */
	Bookmark getRootBookmark(boolean convertNamedDestinations);

	boolean isEncryped();

	void open(String pdfPath) throws IOException;

	/**
	 * Create an outline in the pdf from a Bookmark objects hierarchy starting
	 * at the root given as parameter.
	 *
	 * @param root The root Bookmark of the outline.
	 */
	void rebuildBookmarksFromTreeNodes(Bookmark root);

	void save(String filePath) throws IOException;

	boolean showBookmarksOnOpen();
	void setShowBookmarksOnOpen(boolean show);

	float getPageWidth(int pageNumber);
	float getPageHeight(int pageNumber);

        public int getCountOfPages();

        public String getOpenedFilePath();

}
