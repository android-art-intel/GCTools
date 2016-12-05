/*
 * $Id: TextListener.java 21 2005-06-11 00:25:23Z rej $
 * Copyright Richard Jones, University of Kent, 2005
 *
 * See the file "Kent_license.terms" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */


package gcspy.vis.plugins.text;

import gcspy.vis.plugins.PluginFrame;
import gcspy.vis.plugins.PluginListener;

import java.awt.event.ActionEvent;

/**
 * A listener for the a simple text view of a space
 *
 * @author <a href="http://www/cs/kent.ac.uk">Richard Jones</a>
 */
public class TextListener extends PluginListener {

    /**
     * A new listener
     */
    public TextListener() {
        super("Text");
    }

    /**
     * Create a new TextFrame
     */
    public void actionPerformed(ActionEvent event) {
        PluginFrame frame = new TextFrame(owner, pluginFrames, interpreter,
                space, selectedStream, colorConfig);
        frame.setVisible(true);
    }

}
