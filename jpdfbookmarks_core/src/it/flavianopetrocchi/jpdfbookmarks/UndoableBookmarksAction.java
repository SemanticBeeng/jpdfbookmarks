
package it.flavianopetrocchi.jpdfbookmarks;

import it.flavianopetrocchi.jpdfbookmarks.bookmark.Bookmark;
import javax.swing.tree.TreeModel;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;

public abstract class UndoableBookmarksAction extends AbstractUndoableEdit {

    protected TreeModel treeModel;
    protected Bookmark selectedBookmark;

    public UndoableBookmarksAction(TreeModel model) {
        treeModel = model;
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        doEdit();
    }

    public void doEdit() {
        
    }
}
