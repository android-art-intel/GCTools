/**
 * * $Id: Utils.java 22 2005-06-14 05:17:45Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.vis.utils;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

/**
 * General GUI-related utilities
 *
 * @author Tony Printezis
 */
public class Utils {

    /**
     * Which mouse button was pressed?
     * @param e The mouse event
     * @return The button number or 0 if not button 1-3
     */
    static public int getMouseButton(MouseEvent e) {
        int button;
        int m = e.getModifiers();
        if ((m & InputEvent.BUTTON1_MASK) != 0)
            button = 1;
        else if ((m & InputEvent.BUTTON2_MASK) != 0)
            button = 2;
        else if ((m & InputEvent.BUTTON3_MASK) != 0)
            button = 3;
        else
            button = 0;
        return button;
    }

    /**
     * Is the shift key pressed
     * @param e The mouse event
     * @return true if pressed
     */
    static public boolean isShiftPressed(MouseEvent e) {
        return (e.getModifiers() & InputEvent.SHIFT_MASK) != 0;
    }

    /**
     * Is the control key pressed
     * @param e The mouse event
     * @return true if pressed
     */
    static public boolean isCtrlPressed(MouseEvent e) {
        return (e.getModifiers() & InputEvent.CTRL_MASK) != 0;
    }

    /**
     * Is the alt key pressed
     * @param e The mouse event
     * @return true if pressed
     */
    static public boolean isAltPressed(MouseEvent e) {
        return (e.getModifiers() & InputEvent.ALT_MASK) != 0;
    }

}
