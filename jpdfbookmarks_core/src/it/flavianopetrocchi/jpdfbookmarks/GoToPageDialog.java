/*
 * GoToPageDialog.java
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

import it.flavianopetrocchi.utilities.IntegerTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

class GoToPageDialog extends JDialog {

    private IntegerTextField txtGoToPage;
    private JLabel lblPageOfPages;
    private boolean operationNotAborted = false;

    /** Creates the reusable dialog. */
    public GoToPageDialog(Frame aFrame, int currentPage, int numberOfPages) {
        super(aFrame, true);

        setTitle(Res.getString("GO_TO_PAGE_DIALOG_TITLE"));
        ((JPanel) getContentPane()).setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel superior = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        getContentPane().add(superior);

        JLabel page = new JLabel(Res.getString("PAGE"));
        superior.add(page);
        txtGoToPage = new IntegerTextField(4);
        txtGoToPage.setInteger(currentPage);
        txtGoToPage.setHorizontalAlignment(JTextField.CENTER);
        txtGoToPage.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                operationNotAborted = true;
                setVisible(false);
            }
        });
        superior.add(txtGoToPage);

        lblPageOfPages = new JLabel();
        lblPageOfPages.setText(String.format("/ %d ", numberOfPages));
        superior.add(lblPageOfPages);

        //JPanel inferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JPanel inferior = new JPanel(new GridLayout(1, 2, 10, 10));
        getContentPane().add(inferior, BorderLayout.SOUTH);
        JButton btn = new JButton(Res.getString("OK"));
        btn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                operationNotAborted = true;
                setVisible(false);
            }
        });
        inferior.add(btn);
        btn = new JButton(Res.getString("CANCEL"));
        btn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                operationNotAborted = false;
                setVisible(false);
            }
        });
        inferior.add(btn);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentShown(ComponentEvent ce) {
                txtGoToPage.requestFocusInWindow();
            }
        });

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                operationNotAborted = false;
                setVisible(false);
            }
        });

        pack();
        setLocationRelativeTo(aFrame);
    }

    public boolean operationNotAborted() {
        return operationNotAborted;
    }

    public int getPage() {
        return txtGoToPage.getInteger();
    }
}
