
package it.flavianopetrocchi.veticalbutton;

import javax.swing.JButton;


public class VerticalButton extends JButton {

    public VerticalButton(int angle) {
        super();
        setUI(new VerticalButtonUI(angle));
    }

    public VerticalButton(String text, int angle) {
        super(text);
        setUI(new VerticalButtonUI(angle));
    }
}
