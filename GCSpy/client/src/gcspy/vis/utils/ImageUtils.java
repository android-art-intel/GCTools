/**
 * * $Id: ImageUtils.java 22 2005-06-14 05:17:45Z rej $
 * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/


package gcspy.vis.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Utilities for image manipulation
 * @author Tony Printezis
 */
public class ImageUtils {

    /**
     * Crop image
     * @param image The image
     * @param x x-cooordinate of the image origin
     * @param y y-cooordinate of the image origin
     * @param width The width to crop to
     * @param height The height to crop to
     * @return the cropped image
     */
    static public BufferedImage cropImage(BufferedImage image,
                                          int x, int y,
                                          int width, int height) {
        BufferedImage cropped = new BufferedImage(width, height,
                BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = cropped.getGraphics();
        g.drawImage(image, x, y, null);

        return cropped;
    }

}
