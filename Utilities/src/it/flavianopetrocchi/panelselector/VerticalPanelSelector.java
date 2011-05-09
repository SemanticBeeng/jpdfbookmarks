package it.flavianopetrocchi.panelselector;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import sun.swing.SwingUtilities2;

public class VerticalPanelSelector extends JComponent {

    private String name;

    public VerticalPanelSelector(String name) {
        this.name = name;
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

//        FontMetrics fm = g.getFontMetrics();
//
        Graphics2D g2 = (Graphics2D) g;
//        AffineTransform tr = g2.getTransform();
//        g2.rotate(-Math.PI / 2);
//        g2.translate(10, 0);

        g2.drawString(name, 0, 0);

//        g2.setTransform(tr);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 200);
    }



    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        VerticalPanelSelector vp = new VerticalPanelSelector("OpenPanel");
        frame.add(vp);
        frame.pack();
        frame.setVisible(true);
    }
}
