/*
 * MyHyperlinkListener.java
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

import java.awt.Component;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class MyHyperlinkListener implements HyperlinkListener {
    private Component parent;

    public MyHyperlinkListener(Component dialogsParent) {
        parent = dialogsParent;
    }

    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            goToWebLink(e.getURL().toString());
        }
    }

    public void goToWebLink(String uri) {
        int answer = JOptionPane.showConfirmDialog(parent,
                Res.getString("MSG_LAUNCH_BROWSER"), JPdfBookmarks.APP_NAME,
                JOptionPane.OK_CANCEL_OPTION);

        if (answer != JOptionPane.OK_OPTION) {
            return;
        }

        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI(uri));
        } catch (URISyntaxException ex) {
            JOptionPane.showMessageDialog(parent,
                    Res.getString("ERROR_WRONG_URI"), JPdfBookmarks.APP_NAME,
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parent,
                    Res.getString("ERROR_LAUNCHING_BROWSER"), JPdfBookmarks.APP_NAME,
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
