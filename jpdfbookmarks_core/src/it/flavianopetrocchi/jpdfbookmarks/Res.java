/*
 * Res.java
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

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;

public final class Res {

	private static final String LOCALES_PATH =
			"it/flavianopetrocchi/jpdfbookmarks/locales/";

	/**
	 * ResourceBundle to obtain localized strings related to command line
	 * interface.
	 */
	private static final ResourceBundle textResource = ResourceBundle.getBundle(
			LOCALES_PATH + "localizedText");

	public static String getString(String key) {
		return textResource.getString(key);
	}

	/**
	 * Return the first char of the string in the resource.
	 * @param key The key to retrieve the resource.
	 * @return The first char of the string in the resource.
	 */
	public static char mnemonicFromRes(String key) {
		return Res.getString(key).trim().charAt(0);
	}

	/**
	* utility method to get an icon from the resources of this class
	* @param name the name of the icon
	* @return the icon, or null if the icon wasn't found.
	*/
    public static ImageIcon getIcon(Class cl, String name) {
        ImageIcon icon = null;
        URL url = null;
        try {
            url = cl.getResource(name);

            icon = new ImageIcon(url);
            if (icon == null) {
                System.out.println(Res.getString("RESOURCE_NOT_FOUND") + url);
            }
        } catch (Exception e) {
            System.out.println(Res.getString("RESOURCE_NOT_FOUND") +
					cl.getName() + File.pathSeparator + name);
        }
        return icon;
    }

	private Res() {
	}
}
