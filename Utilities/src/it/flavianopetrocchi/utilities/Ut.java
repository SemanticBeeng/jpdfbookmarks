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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
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

    public static void copyFile(String srcPath, String dstPath)
            throws FileNotFoundException, IOException {
        File f1 = new File(srcPath);
        File f2 = new File(dstPath);
        InputStream in = new FileInputStream(f1);
        OutputStream out = new FileOutputStream(f2);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public static String rtrim(String s) {

        StringBuffer sb = new StringBuffer(s);

        for (int i = sb.length() - 1; i >= 0; i--) {
            char c = sb.charAt(i);
            if (Character.isWhitespace(c)) {
                sb.deleteCharAt(i);
            } else {
                break;
            }
        }

        return sb.toString();
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

    public static void setSelectedButtons(boolean selected, AbstractButton... buttons) {
        for (AbstractButton button : buttons) {
            button.setSelected(selected);
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
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }

    /**
     * Create an absolute path file from a target path relative to a base path
     *
     * @param base The path relative to which target is indicated
     * @param target The path relative to base
     * @return The absolute path to target
     */
    public static File createAbsolutePath(File base, File target) {
        File absoluteTarget = target;
        if (!target.isAbsolute()) {
            String containingFolder = base.getParent();
            String remotePath = containingFolder + File.separator + target.getPath();
            absoluteTarget = new File(remotePath);
        }
        return absoluteTarget.getAbsoluteFile();
    }

    public static File createRelativePath(File base, File target) {
        File relativeFile = target;
        ArrayList<File> baseDirectories = new ArrayList<File>();
        ArrayList<File> targetDirectories = new ArrayList<File>();
        try {
            File baseCanonical = base.getCanonicalFile();
            File targetCanonical = target.getCanonicalFile();
            File parent = baseCanonical.getParentFile();
            while (parent != null) {
                baseDirectories.add(parent);
                parent = parent.getParentFile();
            }
            parent = targetCanonical.getParentFile();
            while (parent != null) {
                targetDirectories.add(parent);
                parent = parent.getParentFile();
            }

            File commonBaseDir = null;

            int baseIndex = baseDirectories.size() - 1;
            int targetIndex = targetDirectories.size() - 1;
            for (; baseIndex >= 0 && targetIndex >= 0; baseIndex--, targetIndex--) {
                if (baseDirectories.get(baseIndex).equals(targetDirectories.get(targetIndex))) {
                    commonBaseDir = baseDirectories.get(baseIndex);
                } else {
                    break;
                }
            }

            int upDirectories = baseIndex + 1;

            StringBuilder path = new StringBuilder();
            for (int j = 0; j < upDirectories; j++) {
                path.append("..");
                path.append(File.separator);
            }
            for (; targetIndex >= 0; targetIndex--) {
                path.append(targetDirectories.get(targetIndex).getName());
                path.append(File.separator);
            }
            path.append(target.getName());
            relativeFile = new File(path.toString());

        } catch (IOException ex) {
        }

        return relativeFile;
    }
}
