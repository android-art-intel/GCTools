/**
 * * $Id: AdjustedColor.java 22 2005-06-14 05:17:45Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.vis.utils;

import java.awt.*;

/**
 * Facilities to adjust colors for tiles
 * @author Tony Printezis
 */
public class AdjustedColor {

    private Color from = null;
    private Color middle = null;
    private Color to = null;
    private int max;

    /**
     * Generate a new color as a fraction of an existing one
     * @param c The base colour
     * @param val The numerator
     * @param max The denominator
     * @return return the colour scaled by <code>val/code</code>
     */
    static public Color generate(Color c, int val, int max) {
        long r = c.getRed();
        long g = c.getGreen();
        long b = c.getBlue();

        r = (r * val / max);
        g = (g * val / max);
        b = (b * val / max);

    /*
     * if ((r > 255) || (r < 0)) System.out.println("BARF 1, r = " + r + ", val = " +
     * val + ", max = " + max); if ((g > 255) || (g < 0))
     * System.out.println("BARF 1, g = " + g + ", val = " + val + ", max = " +
     * max); if ((b > 255) || (b < 0)) System.out.println("BARF 1, b = " + b + ",
     * val = " + val + ", max = " + max);
     */

        return new Color((int) r, (int) g, (int) b);
    }

    /**
     * INterpolate between colours
     * @param from A colour
     * @param to A second
     * @param val The numerator
     * @param max The denominator
     * @return return a colour interpolated between <code>from</code> and
     * <code>to</code> by <code>val/code</code>
     */
    static public Color generate(Color from, Color to, int val, int max) {
        long rf = from.getRed();
        long gf = from.getGreen();
        long bf = from.getBlue();

        long rt = to.getRed();
        long gt = to.getGreen();
        long bt = to.getBlue();

        long rd = rt - rf;
        long gd = gt - gf;
        long bd = bt - bf;

        long r = rf + (rd * val / max);
        long g = gf + (gd * val / max);
        long b = bf + (bd * val / max);

    /*
     * if ((r > 255) || (r < 0)) System.out.println("BARF 2, r = " + r + ", val = " +
     * val + ", max = " + max); if ((g > 255) || (g < 0))
     * System.out.println("BARF 2, g = " + g + ", val = " + val + ", max = " +
     * max); if ((b > 255) || (b < 0)) System.out.println("BARF 2, b = " + b + ",
     * val = " + val + ", max = " + max);
     */

        return new Color((int) r, (int) g, (int) b);
    }

    /**
     * Interpolate between colours.
     * If <code>val < max/2</code> generate a colour between <code>from</code>
     * and <code>middle</code> else generate a colour between <code>middle</code>
     * and <code>to</code>.
     * @param from A starting colour
     * @param middle A middle colour
     * @param to An ending colour
     * @param val The numerator
     * @param max The denominator
     * @return The interpolated colour
     */
    static public Color generate(Color from, Color middle, Color to, int val,
                                 int max) {
        int half = max / 2;
        if (val == half)
            return middle;
        else if (val < half)
            return generate(from, middle, val, half);
        else
            return generate(middle, to, val - half, half);
    }

    /**
     * Generate a new colour
     * @param val The numerator
     * @param max The denominator
     * @return the interpolated colour
     */
    public Color generate(int val, int max) {
        if (from == null)
            return generate(to, val, max);
        else if (middle == null)
            return generate(from, to, val, max);
        else
            return generate(from, middle, to, val, max);
    }

    /**
     * Generate a new colour
     * @param val The numerator
     * @return an interpolated colour
     */
    public Color generate(int val) {
        if (from == null)
            return generate(to, val, max);
        else if (middle == null)
            return generate(from, to, val, max);
        else
            return generate(from, middle, to, val, max);
    }

    /**
     * A new colour
     * @param to The base colour to use. This is used as a maximum
     * for future generated colours.
     */
    public AdjustedColor(Color to) {
        this.to = to;
    }

    /**
     * A new colour
     * @param to The base colour to use. This is used as a maximum
     * for future generated colours.
     * @param max This value is used as a denominator in future
     * interpolations
     */
    public AdjustedColor(Color to, int max) {
        this.to = to;
        this.max = max;
    }

    /**
     * A new colour
     * @param from The lower end of the range of future colours
     * interpolated from this AdjustedColour
     * @param to The upper end of the range of future colours
     * interpolated from this AdjustedColour
     * @param max This value is used as a denominator in future
     * interpolations
     */
    public AdjustedColor(Color from, Color to, int max) {
        this.from = from;
        this.to = to;
        this.max = max;
    }

    /**
     * A new colour
     * @param from The lower end of the range of future colours
     * interpolated from this AdjustedColour
     * @param middle A middle value in the range of future colours
     * interpolated from this AdjustedColour
     * @param to The upper end of the range of future colours
     * interpolated from this AdjustedColour
     * @param max This value is used as a denominator in future
     * interpolations
     */
    public AdjustedColor(Color from, Color middle, Color to, int max) {
        this.from = from;
        this.middle = middle;
        this.to = to;
        this.max = max;
    }

}
