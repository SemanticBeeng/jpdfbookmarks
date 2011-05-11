package it.flavianopetrocchi.components.collapsingpanel;

import it.flavianopetrocchi.reshelper.ResHelper;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

public class CollapsingPanel extends JPanel {

    private static class SplitterContainerListener implements ContainerListener {

        JButton closePanelButton;
        JButton openPanelButton;
        Border emptyBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);

        public SplitterContainerListener(JButton closePanelButton, JButton openPanelButton) {
            this.closePanelButton = closePanelButton;
            this.openPanelButton = openPanelButton;
        }

        public void componentAdded(ContainerEvent e) {
            closePanelButton.setBorder(emptyBorder);
        }

        public void componentRemoved(ContainerEvent e) {
            openPanelButton.setBorder(emptyBorder);
        }
    }
    private final static String PROPERTIES_PATH = "it/flavianopetrocchi/components/collapsingpanel/CollapsingPanel";
    protected final JPanel innerPanelsContainer = new JPanel();
    protected JComboBox tabsCombo = new JComboBox();
    private final JPanel openLeftPanelContainer = new JPanel();
    private JSplitPane containerSplitter;
    private int dividerLocation;
    protected final CardLayout cardLayout = new CardLayout();
    private ResHelper resHelper;
    private int state = PANEL_OPENED;
    private boolean firstRestore = true;


    public final static int COLLAPSING_PANEL_LEFT = 0,
        COLLAPSING_PANEL_RIGHT = 1;

    public final static int PANEL_COLLAPSED = 0,
        PANEL_OPENED = 1;
    

    public CollapsingPanel(JSplitPane containerSplitter) {
        this(containerSplitter, COLLAPSING_PANEL_LEFT);
    }

    public CollapsingPanel(JSplitPane containerSplitter, int collapsingSide) {
        this.containerSplitter = containerSplitter;
        initComponents();
    }

    private void initComponents() {

        innerPanelsContainer.setLayout(cardLayout);
        JPanel cardsManagerPanel = new JPanel();
        cardsManagerPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 5, 3));

        cardsManagerPanel.setLayout(new BoxLayout(cardsManagerPanel, BoxLayout.X_AXIS));
        tabsCombo.setEditable(false);
        cardsManagerPanel.add(tabsCombo);

        tabsCombo.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                cardLayout.show(innerPanelsContainer, (String) e.getItem());
            }
        });

        resHelper = new ResHelper(getClass(), PROPERTIES_PATH);

        openLeftPanelContainer.setBorder(BorderFactory.createEmptyBorder(5, 1, 0, 1));
        openLeftPanelContainer.setLayout(new BoxLayout(openLeftPanelContainer, BoxLayout.Y_AXIS));
        JButton openPanelButton = new JButton(resHelper.getIcon("gfx16/open-panel.png"));
        openPanelButton.setToolTipText(resHelper.getString("OPEN_PANEL_BUTTON_DESCR"));
        openPanelButton.setContentAreaFilled(false);
        openPanelButton.setRolloverEnabled(true);
        openPanelButton.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        openPanelButton.addMouseListener(new ButtonRolloverListener(openPanelButton));
        openPanelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setPanelState(PANEL_OPENED);
            }
        });
        openLeftPanelContainer.add(openPanelButton);

        setLayout(new BorderLayout());

        JButton closePanelButton = new JButton(resHelper.getIcon("gfx16/close-panel.png"));
        closePanelButton.addMouseListener(new ButtonRolloverListener(closePanelButton));
        closePanelButton.setContentAreaFilled(false);
        closePanelButton.setToolTipText(resHelper.getString("CLOSE_PANEL_BUTTON_DESCR"));
        cardsManagerPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        cardsManagerPanel.add(closePanelButton);
        closePanelButton.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        closePanelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setPanelState(PANEL_COLLAPSED);
            }
        });

        add(cardsManagerPanel, BorderLayout.NORTH);
        add(innerPanelsContainer, BorderLayout.CENTER);


        containerSplitter.addContainerListener(
                new SplitterContainerListener(closePanelButton, openPanelButton));
    }

    public void addInnerPanel(JPanel innerPanel, String name) {
        innerPanelsContainer.add(innerPanel, name);
        tabsCombo.addItem(name);
    }

//    public void removeInnerPanel(String name) {
//        for (Component c : innerPanelsContainer.getComponents()) {
//            if (c.getName() != null && c.getName().equals(name)) {
//                innerPanelsContainer.remove(c);
//                tabsCombo.setEditable(true);
//                tabsCombo.removeItem(name);
//                tabsCombo.setEditable(false);
//                break;
//            }
//        }
//    }

    private class ButtonRolloverListener extends MouseAdapter {

        JButton btn;

        public ButtonRolloverListener(JButton btn) {
            this.btn = btn;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            super.mouseEntered(e);
            btn.setBorder(BorderFactory.createLineBorder(Color.black));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            super.mouseExited(e);
            btn.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        }
    }

    public int getPanelState() {
        return state;
    }

    public int setPanelState(int state) {
        int oldState = this.state;
        if (state == PANEL_COLLAPSED) {
            if (!firstRestore) {
                dividerLocation = containerSplitter.getDividerLocation();
            } else {
                firstRestore = false;
            }
            containerSplitter.setLeftComponent(openLeftPanelContainer);
            containerSplitter.setOneTouchExpandable(false);
            containerSplitter.setEnabled(false);
        } else {
            containerSplitter.setDividerLocation(dividerLocation);
            containerSplitter.setLeftComponent(CollapsingPanel.this);
            containerSplitter.setOneTouchExpandable(true);
            containerSplitter.setEnabled(true);
        }
        this.state = state;
        return oldState;
    }

    public int getDividerLocation() {
        if (state == PANEL_OPENED) {
            return containerSplitter.getDividerLocation();
        }
        return dividerLocation;
    }

    public void setDividerLocation(int location) {
        dividerLocation = location;
//        if (state == PANEL_OPENED) {
//            containerSplitter.setDividerLocation(dividerLocation);
//        }
    }

    public void updateComponentsUI() {
        if (state == PANEL_COLLAPSED) {
            SwingUtilities.updateComponentTreeUI(this);
        } else {
            SwingUtilities.updateComponentTreeUI(openLeftPanelContainer);
        }
    }

    public JComboBox getComboBoxSelector() {
        return tabsCombo;
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public JPanel getCardsContainerPanel() {
        return innerPanelsContainer;
    }
}

