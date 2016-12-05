/**
 * * $Id: ColorChooserListener.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.vis.utils;

import java.awt.*;

/**
 * Listener called when the state of ColorChooser changes
 * @author Tony Printezis
 */
public interface ColorChooserListener {

    /**
     * Set the colour chosen
     * @param color the colour
     */
    void colorChosen(Color color);

}
