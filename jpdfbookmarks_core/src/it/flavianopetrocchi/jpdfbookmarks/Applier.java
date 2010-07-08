/*
 * Applier.java
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

import java.io.FileNotFoundException;
import java.io.IOException;

public class Applier extends OutlinePresentation {
	Bookmark root;

	public Applier(IBookmarksConverter pdf) {
		super(pdf);
	}

	public Applier(IBookmarksConverter pdf, String indent, String pageSep,
			String attributesSep) {
		super(pdf, indent, pageSep, attributesSep);
	}

	void loadBookmarksFile(String bookmarksFilePath, String charset)
			throws FileNotFoundException, IOException {
		root = Bookmark.outlineFromFile(pdf, bookmarksFilePath,
				getIndentationString(), getPageSep(), getAttributesSep(), charset);
	}

	void save(String outputFilePath) throws IOException {
		pdf.rebuildBookmarksFromTreeNodes(root);
		pdf.save(outputFilePath);
	}
}
