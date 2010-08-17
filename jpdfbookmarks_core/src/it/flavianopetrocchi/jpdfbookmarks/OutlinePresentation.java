/*
 * OutlinePresentation.java
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

/**
 * This is parent class for dumper and applier contains parameters to read or
 * create a bookmarks.txt file.
 * 
 */
public abstract class OutlinePresentation {

    protected IBookmarksConverter pdf = null;
    protected String indentationString = "\t";
    protected String pageSep = "/";
    protected String attributesSep = ",";

    public String getAttributesSep() {
        return attributesSep;
    }

    public void setAttributesSep(String attributesSep) {
        this.attributesSep = attributesSep;
    }

    public String getIndentationString() {
        return indentationString;
    }

    public void setIndentationString(String indentationString) {
        this.indentationString = indentationString;
    }

    public String getPageSep() {
        return pageSep;
    }

    public void setPageSep(String pageSep) {
        this.pageSep = pageSep;
    }

    public IBookmarksConverter getPdf() {
        return pdf;
    }

    public void setPdf(IBookmarksConverter pdf) {
        this.pdf = pdf;
    }

    public OutlinePresentation() {
    }

    /**
     * Create an OutlinePresentation object from the file passed as
     * argument.
     *
     * @param pdf Bookmarks will be processed from this file.
     */
    public OutlinePresentation(IBookmarksConverter pdf) {
        this.pdf = pdf;
    }

    /**
     * Create a OutlinePresentation object from the file passed as argument.
     *
     * @param pdf Bookmarks will be extracted from this file.
     * @param indent Character used to indent children bookmarks.
     * @param pageSep Character to serparate title from page number.
     * @param attributesSep Character to separate attrubutes of the bookmark.
     */
    public OutlinePresentation(IBookmarksConverter pdf, String indent, String pageSep,
            String attributesSep) {
        this(pdf);
        this.indentationString = indent;
        this.pageSep = pageSep;
        this.attributesSep = attributesSep;

    }
}
