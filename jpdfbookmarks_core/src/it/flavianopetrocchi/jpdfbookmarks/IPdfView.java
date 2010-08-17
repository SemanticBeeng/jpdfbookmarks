/*
 * IPdfView.java
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
import java.awt.Rectangle;
import java.io.File;

public interface IPdfView {
	
	public void open(File file) throws Exception;
        public void reopen(File file) throws Exception;
	public void close();

	public void goToFirstPage();
	public void goToPreviousPage();
	public void goToPage(int numPage);
	public void goToNextPage();
	public void goToLastPage();

//	public void goToBookmark(Bookmark bookmark);

	public void setFitNative();
	public void setFitWidth(int top);
	public void setFitHeight(int left);
	public void setFitPage();
	public void setFitRect(int top, int left, int bottom, int right);
	public void setFitRect(Rectangle rect);
	public void setTopLeftZoom(int top, int left, float zoom);
	
	public Bookmark getBookmarkFromView();

	public int getNumPages();
	public FitType getFitType();
	public int getCurrentPage();

	public void addPageChangedListener(PageChangedListener listener);
	public void removePageChangedListener(PageChangedListener listener);

	public void addViewChangedListener(ViewChangedListener listener);
	public void removeViewChangedListener(ViewChangedListener listener);

	public void addTextCopiedListener(TextCopiedListener listener);
	public void removeTextCopiedListener(TextCopiedListener listener);

	public void addRenderingStartListener(RenderingStartListener listener);
	public void removeRenderingStartListener(RenderingStartListener listener);

        public void setTextSelectionMode(boolean set);
        public void setConnectToClipboard(Boolean set);

}
