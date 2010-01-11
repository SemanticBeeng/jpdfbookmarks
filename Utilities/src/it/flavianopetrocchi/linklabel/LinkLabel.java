/*
 * LinkLabel.java
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

package it.flavianopetrocchi.linklabel;

import java.awt.Desktop;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.BorderLayout;
import java.awt.GridLayout;

import java.awt.event.*;

import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

import javax.swing.border.MatteBorder;
import javax.swing.border.Border;

import java.net.URI;
import java.io.File;

/**
A Java 1.6+ LinkLabel that uses the Desktop class for opening
the document of interest.

The Desktop.browse(URI) method can be invoked from applications,
applets and apps. launched using Java Webstart.  In the latter
two cases, the usual fall-back methods are used for sandboxed apps
(see the JavaDocs for further details).

While called a 'label', this class actually extends JTextField,
to easily allow the component to become focusable using keyboard
navigation.

To successfully browse to a URI for a local File, the file name
must be constructed using a canonical path.

@author Andrew Thompson
@version 2008/08/23
 */
public class LinkLabel
        // we extend a JTextField, to get a focusable component
        extends JTextField
        implements MouseListener, FocusListener, ActionListener {

    /** The target or href of this link. */
    private URI target;
    public Color standardColor = new Color(0, 0, 255);
    public Color hoverColor = new Color(255, 0, 0);
    public Color activeColor = new Color(128, 0, 128);
    public Color transparent = new Color(0, 0, 0, 0);
    public boolean underlineVisible = true;
    private Border activeBorder;
    private Border hoverBorder;
    private Border standardBorder;

    /** Construct a LinkLabel that points to the given target.
    The URI will be used as the link text.*/
    public LinkLabel(URI target) {
        this(target, target.toString());
    }

    /** Construct a LinkLabel that points to the given target,
    and displays the text to the user. */
    public LinkLabel(URI target, String text) {
        super(text);
        this.target = target;
    }

    /* Set the active color for this link (default is purple). */
    public void setActiveColor(Color active) {
        activeColor = active;
    }

    /* Set the hover/focused color for this link (default is red). */
    public void setHoverColor(Color hover) {
        hoverColor = hover;
    }

    /* Set the standard (non-focused, non-active) color for this
    link (default is blue). */
    public void setStandardColor(Color standard) {
        standardColor = standard;
    }

    /** Determines whether the */
    public void setUnderlineVisible(boolean underlineVisible) {
        this.underlineVisible = underlineVisible;
    }

    /* Add the listeners, configure the field to look and act
    like a link. */
    public void init() {
        this.addMouseListener(this);
        this.addFocusListener(this);
        this.addActionListener(this);
        setToolTipText(target.toString());

        if (underlineVisible) {
            activeBorder = new MatteBorder(0, 0, 1, 0, activeColor);
            hoverBorder = new MatteBorder(0, 0, 1, 0, hoverColor);
            standardBorder = new MatteBorder(0, 0, 1, 0, transparent);
        } else {
            activeBorder = new MatteBorder(0, 0, 0, 0, activeColor);
            hoverBorder = new MatteBorder(0, 0, 0, 0, hoverColor);
            standardBorder = new MatteBorder(0, 0, 0, 0, transparent);
        }

        // make it appear like a label/link
        setEditable(false);
        setForeground(standardColor);
        setBorder(standardBorder);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /** Browse to the target URI using the Desktop.browse(URI)
    method.  For visual indication, change to the active color
    at method start, and return to the standard color once complete.
    This is usually so fast that the active color does not appear,
    but it will take longer if there is a problem finding/loading
    the browser or URI (e.g. for a File). */
    public void browse() {
        setForeground(activeColor);
        setBorder(activeBorder);
        try {
            Desktop.getDesktop().browse(target);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setForeground(standardColor);
        setBorder(standardBorder);
    }

    /** Browse to the target. */
    public void actionPerformed(ActionEvent ae) {
        browse();
    }

    /** Browse to the target. */
    public void mouseClicked(MouseEvent me) {
        browse();
    }

    /** Set the color to the hover color. */
    public void mouseEntered(MouseEvent me) {
        setForeground(hoverColor);
        setBorder(hoverBorder);
    }

    /** Set the color to the standard color. */
    public void mouseExited(MouseEvent me) {
        setForeground(standardColor);
        setBorder(standardBorder);
    }

    public void mouseReleased(MouseEvent me) {
    }

    public void mousePressed(MouseEvent me) {
    }

    /** Set the color to the standard color. */
    public void focusLost(FocusEvent fe) {
        setForeground(standardColor);
        setBorder(standardBorder);
    }

    /** Set the color to the hover color. */
    public void focusGained(FocusEvent fe) {
        setForeground(hoverColor);
        setBorder(hoverBorder);
    }

    public static void main(String[] args) throws Exception {
        JPanel p = new JPanel(new GridLayout(0, 1));
        File f = new File(".", "LinkLabel.java");

        /* Filename must be constructed with a canonical path in
        order to successfully use Desktop.browse(URI)! */
        f = new File(f.getCanonicalPath());

        URI uriFile = f.toURI();

        LinkLabel linkLabelFile = new LinkLabel(uriFile);
        linkLabelFile.init();
        p.add(linkLabelFile);

        LinkLabel linkLabelWeb = new LinkLabel(
                new URI("http://pscode.org/sscce.html"),
                "SSCCE");
        linkLabelWeb.setStandardColor(new Color(0, 128, 0));
        linkLabelWeb.setHoverColor(new Color(222, 128, 0));
        linkLabelWeb.init();

        /* This shows a quirk of the LinkLabel class, the
        size of the text field needs to be constrained to
        get the underline to appear properly. */
        p.add(linkLabelWeb);

        LinkLabel linkLabelConstrain = new LinkLabel(
                new URI("http://sdnshare.sun.com/"),
                "SDN Share");
        linkLabelConstrain.init();
        /* ..and this shows one way to constrain the size
        (appropriate for this layout).
        Similar tricks can be used to ensure the underline does
        not drop too far *below* the link (think BorderLayout
        NORTH/SOUTH).
        The same technique can also be nested further to produce
        a NORTH+EAST packing (for example). */
        JPanel labelConstrain = new JPanel(new BorderLayout());
        labelConstrain.add(linkLabelConstrain, BorderLayout.EAST);
        p.add(labelConstrain);

        LinkLabel linkLabelNoUnderline = new LinkLabel(
                new URI("http://java.net/"),
                "java.net");
        // another way to deal with the underline is to remove it
        linkLabelNoUnderline.setUnderlineVisible(false);
        // we can use the methods inherited from JTextField
        linkLabelNoUnderline.setHorizontalAlignment(JTextField.CENTER);
        linkLabelNoUnderline.init();
        p.add(linkLabelNoUnderline);

        JOptionPane.showMessageDialog(null, p);
    }
}
