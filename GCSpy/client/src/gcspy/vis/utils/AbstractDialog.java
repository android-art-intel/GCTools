/**
 * * $Id: AbstractDialog.java 22 2005-06-14 05:17:45Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.vis.utils;

import javax.swing.*;
import java.awt.*;

/**
 * Facilities for the dialog windows
 * @author Tony Printezis
 */
abstract class AbstractDialog extends JDialog {

    private Component owner;

    /** Place the dialog centred on its parent */
    protected void placeIt() {
        setResizable(false);
        if (owner != null) {
            Point p = owner.getLocation();
            Dimension d = owner.getSize();

            int x = p.x + (d.width - getWidth()) / 2;
            int y = p.y + (d.height - getHeight()) / 2;

            setLocation(x, y);
        }
    }

    /**
     * New dialog
     * @param owner Its parent frame
     */
    protected AbstractDialog(Frame owner) {
        super(owner, true);
        this.owner = owner;
    }

    /**
     * New dialog
     * @param owner Its parent dialog
     */
    protected AbstractDialog(Dialog owner) {
        super(owner, true);
        this.owner = owner;
    }

}
