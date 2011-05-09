package it.flavianopetrocchi.veticalbutton;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;

public class VerticalButtonUI extends BasicButtonUI {

    protected int angle;

    public VerticalButtonUI(int angle) {
        super();
        this.angle = angle;
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        Dimension dim = super.getPreferredSize(c);
        return new Dimension(dim.height, dim.width);
    }
    private static Rectangle paintIconR = new Rectangle();
    private static Rectangle paintTextR = new Rectangle();
    private static Rectangle paintViewR = new Rectangle();
    private static Insets paintViewInsets = new Insets(0, 0, 0, 0);

    @Override
    public void paint(Graphics g, JComponent c) {
        JButton button = (JButton) c;
        String text = button.getText();
        Icon icon = (button.isEnabled()) ? button.getIcon() : button.getDisabledIcon();

        if ((icon == null) && (text == null)) {
            return;
        }

        FontMetrics fm = g.getFontMetrics();
        paintViewInsets = c.getInsets(paintViewInsets);

        paintViewR.x = paintViewInsets.left;
        paintViewR.y = paintViewInsets.top;

        // Use inverted height &amp; width
        paintViewR.height = c.getWidth() - (paintViewInsets.left + paintViewInsets.right);
        paintViewR.width = c.getHeight() - (paintViewInsets.top + paintViewInsets.bottom);

        paintIconR.x = paintIconR.y = paintIconR.width = paintIconR.height = 0;
        paintTextR.x = paintTextR.y = paintTextR.width = paintTextR.height = 0;

        Graphics2D g2 = (Graphics2D) g;
        AffineTransform tr = g2.getTransform();

        if (angle == 90) {
            g2.rotate(Math.PI / 2);
            g2.translate(0, -c.getWidth());
            paintViewR.x = c.getHeight() / 2 - (int) fm.getStringBounds(text, g).getWidth() / 2;
            paintViewR.y = c.getWidth() / 2 - (int) fm.getStringBounds(text, g).getHeight() / 2;
        } else if (angle == 270) {
            g2.rotate(-Math.PI / 2);
            g2.translate(-c.getHeight(), 0);
            paintViewR.x = c.getHeight() / 2 - (int) fm.getStringBounds(text, g).getWidth() / 2;
            paintViewR.y = c.getWidth() / 2 - (int) fm.getStringBounds(text, g).getHeight() / 2;
        }

        if (icon != null) {
            icon.paintIcon(c, g, paintIconR.x, paintIconR.y);
        }

        if (text != null) {
            int textX = paintTextR.x;
            int textY = paintTextR.y + fm.getAscent();

            if (button.isEnabled()) {
                paintText(g, c, new Rectangle(paintViewR.x, paintViewR.y, textX, textY), text);
            } else {
                paintText(g, c, new Rectangle(paintViewR.x, paintViewR.y, textX, textY), text);
            }
        }

        g2.setTransform(tr);
    }
}
