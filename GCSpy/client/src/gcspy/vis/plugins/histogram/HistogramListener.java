/*
 * $Id: HistogramListener.java 34 2005-09-22 16:17:34Z rej $
 * Copyright Hanspeter Johner, University of Kent, 2005
 *
 * See the file "Kent_license.terms" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.vis.plugins.histogram;


import gcspy.vis.plugins.PluginFrame;
import gcspy.vis.plugins.PluginListener;

import java.awt.event.ActionEvent;

/**
 * A listener for the historam view plugin
 *
 * @author Hanspeter Johner
 * @author <a href="http://www/cs/kent.ac.uk">Richard Jones</a>
 */
public class HistogramListener extends PluginListener {

    /**
     * A new listener for the histogram view
     */
    public HistogramListener() {
        super("Histogram");
    }

    /**
     * Create a new Histogram frame
     */
    public void actionPerformed(ActionEvent event) {
        PluginFrame frame = new HistogramFrame(owner, pluginFrames, interpreter,
                space, selectedStream, colorConfig);
        frame.setVisible(true);
    }

}
