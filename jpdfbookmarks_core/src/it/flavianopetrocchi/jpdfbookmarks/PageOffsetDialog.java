/*
 * PageOffsetDialog.java
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
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

class PageOffsetDialog extends JDialog {
	private JSpinner pageOffset;
	private JLabel lblMaxOffset;
	private boolean operationNotAborted = false;


    /** Creates the reusable dialog. */
    public PageOffsetDialog(Frame aFrame, int initialValue, int maxValue,
			int minValue) {
        super(aFrame, true);
		
        setTitle(Res.getString("PAGE_OFFSET"));
		((JPanel)getContentPane()).setBorder(
				BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		JPanel superior = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		getContentPane().add(superior);
		
		JLabel page = new JLabel(Res.getString("PAGE_OFFSET") + ": ");
		superior.add(page);

		SpinnerModel model = new SpinnerNumberModel(initialValue, minValue,
				maxValue, 1);
		pageOffset = new JSpinner(model);
		superior.add(pageOffset);

		lblMaxOffset = new JLabel();
		lblMaxOffset.setText(String.format(" [%d,%d]", minValue, maxValue));
		superior.add(lblMaxOffset);

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
                pageOffset.requestFocusInWindow();
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

	public int getOffsetValue() {
		return (Integer) pageOffset.getValue();
	}

}
