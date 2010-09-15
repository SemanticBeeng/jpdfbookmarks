
package it.flavianopetrocchi.jpdfbookmarks;

import it.flavianopetrocchi.jpdfbookmarks.bookmark.Bookmark;
import javax.swing.JOptionPane;
import javax.swing.JTree;

public class UndoableMultipleSetDestination extends UndoableBookmarksAction {

    public static final int REPLACE = 0;
    public static final int ADD = 1;
    
    private Bookmark dest;
    private int addOrReplace;
    private boolean excludePageNumber;

    public UndoableMultipleSetDestination(JTree tree, Bookmark dest, int addOrReplace, boolean excludePageNumber) {
        super(tree);
        this.dest = dest;
        this.addOrReplace = addOrReplace;
        this.excludePageNumber = excludePageNumber;
    }

    @Override
    public void doEdit() {
        for (Bookmark b : selectedBookmarks) {
            int oldPageNumber = b.getPageNumber();
            if (addOrReplace == REPLACE) {
                b.clearChainedBookmarks();
                b.cloneDestination(dest);
                if (excludePageNumber) {
                    b.setPageNumber(oldPageNumber);
                }
            } else {
                Bookmark copy = Bookmark.cloneBookmark(dest, false);
                if (excludePageNumber) {
                    copy.setPageNumber(oldPageNumber);
                }
                b.addChainedBookmark(copy);
            }
        }
    }

}
