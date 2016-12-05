/*
 * $Id: HistoryListener.java 28 2005-06-20 13:13:35Z rej $
 * Copyright Richard Jones, University of Kent, 2005
 *
 * See the file "Kent_license.terms" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */

package gcspy.vis.plugins.history;


import gcspy.vis.plugins.PluginFrame;
import gcspy.vis.plugins.PluginListener;

import java.awt.event.ActionEvent;

/**
 * A listener for the history view
 *
 * @author Tony Printezis
 * @author <a href="http://www/cs/kent.ac.uk">Richard Jones</a>
 */
public class HistoryListener extends PluginListener {

    /**
     * A new listener for the history view
     */
    public HistoryListener() {
        super("History");
    }

    /**
     * Create a new History frame
     */
    public void actionPerformed(ActionEvent event) {
        PluginFrame frame = new HistoryFrame(owner, pluginFrames, interpreter,
                space, selectedStream, colorConfig);
        frame.setVisible(true);
    }

}
