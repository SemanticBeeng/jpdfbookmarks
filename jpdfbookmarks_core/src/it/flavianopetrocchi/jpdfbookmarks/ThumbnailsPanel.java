
package it.flavianopetrocchi.jpdfbookmarks;

import java.awt.Font;
import javax.swing.JButton;
import org.jpedal.PdfDecoder;
import org.jpedal.examples.simpleviewer.Values;
import org.jpedal.examples.simpleviewer.gui.swing.SwingThumbnailPanel;
import org.jpedal.objects.PdfPageData;


public class ThumbnailsPanel extends SwingThumbnailPanel implements PageChangedListener {

    public ThumbnailsPanel(Values commonValues, PdfDecoder decoder) {
        super(commonValues, decoder);
        setThumbnailsEnabled(true);
    }

    @Override
    public void pageChanged(PageChangedEvent evt) {
        //resetHighlightedThumbnail(evt.getCurrentPage() - 1);
        generateOtherVisibleThumbnails(evt.getCurrentPage());
    }

    @Override
    public void setupThumbnails(int pages, Font textFont, String message, PdfPageData pageData) {
        super.setupThumbnails(pages, textFont, message, pageData);
//        super.panel.setLayout(new WrapFlowLayout(WrapFlowLayout.LEFT));
//        for (JButton btn : (JButton[])getButtons()) {
//            btn.setContentAreaFilled(false);
//        }
    }


}
