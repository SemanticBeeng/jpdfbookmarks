/*
 * Colors.java
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
package it.flavianopetrocchi.colors;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

/**
 * The enum contains a complete list of the 140 color names supported by all
 * major browsers.
 */
public enum Colors {

    // <editor-fold defaultstate="collapsed" desc="Color Names">
    AliceBlue(0xF0F8FF),
    AntiqueWhite(0xFAEBD7),
    Aqua(0x00FFFF),
    Aquamarine(0x7FFFD4),
    Azure(0xF0FFFF),
    Beige(0xF5F5DC),
    Bisque(0xFFE4C4),
    Black(0x000000),
    BlanchedAlmond(0xFFEBCD),
    Blue(0x0000FF),
    BlueViolet(0x8A2BE2),
    Brown(0xA52A2A),
    BurlyWood(0xDEB887),
    CadetBlue(0x5F9EA0),
    Chartreuse(0x7FFF00),
    Chocolate(0xD2691E),
    Coral(0xFF7F50),
    CornflowerBlue(0x6495ED),
    Cornsilk(0xFFF8DC),
    Crimson(0xDC143C),
    Cyan(0x00FFFF),
    DarkBlue(0x00008B),
    DarkCyan(0x008B8B),
    DarkGoldenRod(0xB8860B),
    DarkGray(0xA9A9A9),
    DarkGreen(0x006400),
    DarkKhaki(0xBDB76B),
    DarkMagenta(0x8B008B),
    DarkOliveGreen(0x556B2F),
    Darkorange(0xFF8C00),
    DarkOrchid(0x9932CC),
    DarkRed(0x8B0000),
    DarkSalmon(0xE9967A),
    DarkSeaGreen(0x8FBC8F),
    DarkSlateBlue(0x483D8B),
    DarkSlateGray(0x2F4F4F),
    DarkTurquoise(0x00CED1),
    DarkViolet(0x9400D3),
    DeepPink(0xFF1493),
    DeepSkyBlue(0x00BFFF),
    DimGray(0x696969),
    DodgerBlue(0x1E90FF),
    FireBrick(0xB22222),
    FloralWhite(0xFFFAF0),
    ForestGreen(0x228B22),
    Fuchsia(0xFF00FF),
    Gainsboro(0xDCDCDC),
    GhostWhite(0xF8F8FF),
    Gold(0xFFD700),
    GoldenRod(0xDAA520),
    Gray(0x808080),
    Green(0x008000),
    GreenYellow(0xADFF2F),
    HoneyDew(0xF0FFF0),
    HotPink(0xFF69B4),
    IndianRed(0xCD5C5C),
    Indigo(0x4B0082),
    Ivory(0xFFFFF0),
    Khaki(0xF0E68C),
    Lavender(0xE6E6FA),
    LavenderBlush(0xFFF0F5),
    LawnGreen(0x7CFC00),
    LemonChiffon(0xFFFACD),
    LightBlue(0xADD8E6),
    LightCoral(0xF08080),
    LightCyan(0xE0FFFF),
    LightGoldenRodYellow(0xFAFAD2),
    LightGrey(0xD3D3D3),
    LightGreen(0x90EE90),
    LightPink(0xFFB6C1),
    LightSalmon(0xFFA07A),
    LightSeaGreen(0x20B2AA),
    LightSkyBlue(0x87CEFA),
    LightSlateGray(0x778899),
    LightSteelBlue(0xB0C4DE),
    LightYellow(0xFFFFE0),
    Lime(0x00FF00),
    LimeGreen(0x32CD32),
    Linen(0xFAF0E6),
    Magenta(0xFF00FF),
    Maroon(0x800000),
    MediumAquaMarine(0x66CDAA),
    MediumBlue(0x0000CD),
    MediumOrchid(0xBA55D3),
    MediumPurple(0x9370D8),
    MediumSeaGreen(0x3CB371),
    MediumSlateBlue(0x7B68EE),
    MediumSpringGreen(0x00FA9A),
    MediumTurquoise(0x48D1CC),
    MediumVioletRed(0xC71585),
    MidnightBlue(0x191970),
    MintCream(0xF5FFFA),
    MistyRose(0xFFE4E1),
    Moccasin(0xFFE4B5),
    NavajoWhite(0xFFDEAD),
    Navy(0x000080),
    OldLace(0xFDF5E6),
    Olive(0x808000),
    OliveDrab(0x6B8E23),
    Orange(0xFFA500),
    OrangeRed(0xFF4500),
    Orchid(0xDA70D6),
    PaleGoldenRod(0xEEE8AA),
    PaleGreen(0x98FB98),
    PaleTurquoise(0xAFEEEE),
    PaleVioletRed(0xD87093),
    PapayaWhip(0xFFEFD5),
    PeachPuff(0xFFDAB9),
    Peru(0xCD853F),
    Pink(0xFFC0CB),
    Plum(0xDDA0DD),
    PowderBlue(0xB0E0E6),
    Purple(0x800080),
    Red(0xFF0000),
    RosyBrown(0xBC8F8F),
    RoyalBlue(0x4169E1),
    SaddleBrown(0x8B4513),
    Salmon(0xFA8072),
    SandyBrown(0xF4A460),
    SeaGreen(0x2E8B57),
    SeaShell(0xFFF5EE),
    Sienna(0xA0522D),
    Silver(0xC0C0C0),
    SkyBlue(0x87CEEB),
    SlateBlue(0x6A5ACD),
    SlateGray(0x708090),
    Snow(0xFFFAFA),
    SpringGreen(0x00FF7F),
    SteelBlue(0x4682B4),
    Tan(0xD2B48C),
    Teal(0x008080),
    Thistle(0xD8BFD8),
    Tomato(0xFF6347),
    Turquoise(0x40E0D0),
    Violet(0xEE82EE),
    Wheat(0xF5DEB3),
    White(0xFFFFFF),
    WhiteSmoke(0xF5F5F5),
    Yellow(0xFFFF00),
    YellowGreen(0x9ACD32);// </editor-fold>

    Colors(int rgb) {
        this.rgb = rgb;
        this.color = new Color(rgb);
    }

    Colors(Color color) {
        this.color = color;
        this.rgb = color.getRGB();
    }

    /**
     * Return a string presentation of the Color passed as parameter as one of
     * the names defined by the Colors enum or as an exadecimal value in the
     * format "#RRGGBB".
     *
     * @param color The color to transform to a string.
     * @return String presentation of the color.
     */
    public static String colorToString(Color color) {
        Colors item = lookUp(color);
        String presentation = null;
        if (item != null) {
            presentation = item.toString();
        } else {
            presentation = "#"
                    + Integer.toHexString(color.getRGB()).substring(2);
        }
        return presentation;
    }

    public static Color stringToColor(String color)
            throws NumberFormatException {
        Colors item = null;
        try {
            item = Colors.valueOf(color);
        } catch (Exception e) {
        }

        Color c = null;
        if (item != null) {
            c = item.getColor();
        } else {
            c = Color.decode(color);
        }

        return c;
    }

    public int getRgb() {
        return rgb;
    }

    public Color getColor() {
        return color;
    }

    /**
     * Search for a member of the Colors enum that corresponds to the rgb color
     * value.
     *
     * @param rgb Color expressed in RGB format.
     * @return Member of this enum that has the RGB value asked or null if it is
     * not found.
     */
    public static Colors lookUp(int rgb) {
        Colors searched = null;
        for (Colors c : Colors.values()) {
            if (c.getRgb() == rgb) {
                searched = c;
            }
        }

        return searched;
    }

    public static Color fromString(String name) {
        Color color = null;
        for (Colors c : Colors.values()) {
            if (c.toString().equalsIgnoreCase(name)) {
                color = c.getColor();
                break;
            }
        }
        return color;
    }

    /**
     * Try to search for corresponding enum value to the color passed as string
     * in a format that Color.decode() method can evaluate.
     *
     * @param rgb Value passed to Color.decode(String nm).
     * @return Member of this enum corresponding to the rgb value if found or
     * null.
     */
    public static Colors lookUp(String rgb) {
        Colors searched = null;
        try {
            Color colorFromString = Color.decode(rgb);
            for (Colors c : Colors.values()) {
                if (c.getColor().equals(colorFromString)) {
                    searched = c;
                }
            }
        } catch (NumberFormatException numberFormatException) {
        }
        return searched;
    }

    public static Colors lookUp(Color col) {
        Colors searched = null;
        for (Colors c : Colors.values()) {
            if (c.getColor().equals(col)) {
                searched = c;
            }
        }
        return searched;
    }

    public static JComboBox getColorsChooser() {
        JComboBox combo = new JComboBox(Colors.values());
        combo.setRenderer(new ColorsCellRenderer());
        return combo;
    }

    public static JList getColorsList() {
        JList list = new JList(Colors.values());
        list.setCellRenderer(new ColorsCellRenderer());
        return list;
    }

    private static class ColorsCellRenderer extends JLabel
            implements ListCellRenderer {

        public ColorsCellRenderer() {
            setOpaque(true);
            setHorizontalAlignment(LEFT);
            setVerticalAlignment(CENTER);
            setBorder(new EmptyBorder(0, getIconTextGap(), 0, 0));
        }

        /*
         * This method finds the image and text corresponding
         * to the selected value and returns the label, set up
         * to display the text and image.
         */
        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            Colors selectedColor = (Colors) value;

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            FontMetrics metrics = getFontMetrics(getFont());
            int lenght = metrics.getHeight() - metrics.getDescent();
            BufferedImage image = new BufferedImage(lenght, lenght,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = image.getGraphics();
            g.setColor(selectedColor.getColor());
            g.fillRect(0, 0, lenght, lenght);
            g.setColor(list.getForeground());
            g.drawRect(0, 0, lenght - 1, lenght - 1);
            g.dispose();
            setIcon(new ImageIcon(image));
            setText(value.toString());

            return this;
        }
    }
    private int rgb;
    private Color color;
}
