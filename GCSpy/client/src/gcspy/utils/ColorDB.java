/**
 * * $Id: ColorDB.java 28 2005-06-20 13:13:35Z rej $
 * *Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.utils;

import java.awt.*;

/**
 * Maps colors to/from names
 *
 * @author Tony Printezis
 */
public class ColorDB {

    static private final Color COLORS[] = {Color.black, Color.blue, Color.cyan,
            Color.darkGray, Color.gray, Color.green, Color.lightGray, Color.magenta,
            new Color(160, 160, 160), new Color(0, 0, 150), new Color(230, 230, 230),
            Color.orange, Color.pink, Color.red, Color.white, Color.yellow};

    static private final String COLOR_NAMES[] = {"Black", "Blue", "Cyan",
            "Dark Gray", "Gray", "Green", "Light Gray", "Magenta", "Mid Gray",
            "Navy Blue", "Off White", "Orange", "Pink", "Red", "White", "Yellow"};

    static private final int DEFAULT_COLOR_INDEX = 0;
    static private final Color DEFAULT_COLOR = COLORS[DEFAULT_COLOR_INDEX];

    static private final ColorDB colorDB = new ColorDB();

    /**
     * Return the colour corresponding to an index
     * @param index The index
     * @return The corresponding colour
     */
    public Color getColor(int index) {
        return COLORS[index];
    }

    /**
     * Return the colour corresponding to a name
     * @param name The name
     * @return The corresponding colour
     */
    public Color getColor(String name) {
        if (name == null)
            return DEFAULT_COLOR;
        for (int i = 0; i < COLOR_NAMES.length; ++i) {
            if (name.equalsIgnoreCase(COLOR_NAMES[i]))
                return COLORS[i];
        }
        return DEFAULT_COLOR;
    }

    /**
     * Return the name corresponding to a colour index
     * @param index The index
     * @return The corresponding name
     */
    public String getName(int index) {
        return COLOR_NAMES[index];
    }

    /**
     * Return the name corresponding to a colour
     * @param c The colour
     * @return The corresponding name
     */
    public String getName(Color c) {
        return COLOR_NAMES[getIndex(c)];
    }

    /**
     * Return the index corresponding to a colour
     * @param c The colour
     * @return The corresponding index
     */
    public int getIndex(Color c) {
        if (c == null)
            return DEFAULT_COLOR_INDEX;
        for (int i = 0; i < COLORS.length; ++i) {
            if (c.equals(COLORS[i]))
                return i;
        }
        return -1;
    }

    /**
     * Get the number of colours in the database
     * @return the length of the colours list
     */
    public int getLength() {
        return COLORS.length;
    }

    /**
     * Print out all the indices, names and values for all colours
     * in the database
     */
    public void dump() {
        for (int i = 0; i < getLength(); ++i) {
            Color c = getColor(i);
            System.out.println(i + "." + getName(i) + ": " + c.getRed() + ","
                    + c.getGreen() + "," + c.getBlue());
        }
    }

    private ColorDB() {
    }

    /**
     * Get the colours database
     * @return the colours database
     */
    static public ColorDB getColorDB() {
        return colorDB;
    }

    static public void main(String args[]) {
        ColorDB colorDB = getColorDB();
        colorDB.dump();
    }

}
