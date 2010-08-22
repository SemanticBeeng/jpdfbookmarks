
package it.flavianopetrocchi.jpdfbookmarks.bookmark;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class BookmarkSelection implements Transferable, ClipboardOwner, Serializable {
    private DataFlavor bookmarkFlavor;
    private DataFlavor[] dataFlavors = new DataFlavor[1];
    private Bookmark bookmark;
    private boolean cut;
    private File file;
    
    public BookmarkSelection(Bookmark bookmark, DataFlavor flavor, boolean cut) {
           bookmarkFlavor = flavor;
           this.bookmark = bookmark;
           dataFlavors[0] = bookmarkFlavor;
           this.cut = cut;
    }

    public BookmarkSelection(Bookmark bookmark, DataFlavor flavor, boolean cut, File pdfFile) {
        this(bookmark, flavor, cut);
        this.file = pdfFile;
    }

    public File getFile() {
        return file;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return dataFlavors;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(bookmarkFlavor);
    }
    
    public Bookmark getBookmark() {
        return bookmark;
    }
    
    public boolean isCutOperation() {
        return cut;
    }

    public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(bookmarkFlavor)) {
            return this;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }

}
