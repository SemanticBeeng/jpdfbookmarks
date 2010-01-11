/*
 * Ut.java
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

package it.flavianopetrocchi.utilities;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * Class for utlity static methods usefull in different situations.
 */
public class Ut {

	/**
	 * Get the class name for the current look and feel. The returned string can
	 * be used by UIManager.setLookAndFeel(className)
	 * @return The class name for the current Look & Fell
	 */
	public static String getClassNameForCurrentLAF() {
		String className = null;
		String currentLAF = UIManager.getLookAndFeel().getName();
		LookAndFeelInfo[] infoArray = UIManager.getInstalledLookAndFeels();
		for (LookAndFeelInfo info : infoArray) {
			if (currentLAF.equals(info.getName())) {
				className = info.getClassName();
			}
		}
		return className;
	}

	/**
	 * Try to change the Look & Feel of a user interface.
	 * @param laf The class name of the Look & Feel to apply.
	 * @param c The Component at the root of the interface, generally the main 
	 * window.
	 * @return True for sucess false for fail.
	 */
	public static boolean changeLAF(String laf, Component c) {
		boolean success = true;
		try {
			UIManager.setLookAndFeel(laf);
			SwingUtilities.updateComponentTreeUI(c);
		} catch (Exception ex) {
			success = false;
		}
		return success;
	}

	/**
	 * Change the enabled state of the actions passed as parameters.
	 * @param enabled The state to change to.
	 * @param actions The actions to change.
	 */
	public static void enableActions(boolean enabled, Action... actions) {
		for (Action action : actions) {
			action.setEnabled(enabled);
		}
	}

	/**
	 * Change the enabled state of the components passed as parameters.
	 * @param enabled The state to change to.
	 * @param components The components to change.
	 */
	public static void enableComponents(boolean enabled, JComponent... components) {
		for (JComponent component : components) {
			component.setEnabled(enabled);
		}
	}

	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		long length = file.length();

		if (length > Integer.MAX_VALUE) {
			throw new IOException("Could not completely read file " + file.getName());
		}

		byte[] bytes = new byte[(int) length];

		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length &&
				(numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		if (offset < bytes.length) {
			throw new IOException("Could not completely read file " + file.getName());
		}

		is.close();
		return bytes;
	}
}
