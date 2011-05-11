
package it.flavianopetrocchi.jpdfbookmarks;

import it.flavianopetrocchi.components.collapsingpanel.CollapsingPanel;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;


public class LeftPanel extends CollapsingPanel {

    JPanel bookmarksPanel;
    JPanel thumbnailsPanel;
    JScrollPane voidScrollPane = new JScrollPane();

    public LeftPanel(JSplitPane splitter) {
        super(splitter);
    }

    public void addBookmarksPanel(JPanel bookmarksPanel) {
        this.bookmarksPanel = bookmarksPanel;
        addInnerPanel(bookmarksPanel, Res.getString("BOOKMARKS_TAB_TITLE"));
    }

    public void addThumbnailsPanel(JPanel thumbnailsPanel) {
        this.thumbnailsPanel = thumbnailsPanel;
        this.thumbnailsPanel.setLayout(new BorderLayout());
        addInnerPanel(this.thumbnailsPanel, Res.getString("THUMBNAILS_TAB_TITLE"));
    }

    public void updateThumbnails(JScrollPane thumbnails) {
        thumbnailsPanel.removeAll();
        if (thumbnails != null) {
            thumbnailsPanel.add(thumbnails, BorderLayout.CENTER);
        }
    }

    public void selectPanelToShow(String name) {
        getCardLayout().show(getCardsContainerPanel(), name);
        getComboBoxSelector().setSelectedItem(name);
    }
}
