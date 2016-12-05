/*
 * $Id: PluginListener.java 30 2005-06-30 12:04:35Z rej $
 * Copyright Richard Jones, University of Kent, 2005
 *
 * See the file "Kent_license.terms" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package gcspy.vis.plugins;

import gcspy.interpreter.client.ClientInterpreter;
import gcspy.interpreter.client.ClientSpace;
import gcspy.vis.TileManagerColors;
import gcspy.vis.utils.AbstractFrame;

import java.awt.event.ActionListener;
import java.util.List;

/**
 * A plugin view called <i>MyView</i> must provide a
 * <code>PluginListener</code> called <code><i>MyView</i>Listener</code>.
 * This listener's <code>actionPerformed</code> will be called when the
 * user clicks its button attached to a space in the visualiser's main view.
 *
 * @author <a href="http://www/cs/kent.ac.uk">Richard Jones</a>
 */
public abstract class PluginListener implements ActionListener {

    private final String label;
    /**
     * The frame that owns this plugin
     */
    protected AbstractFrame owner;
    /**
     * A The list of plugin frames known to the space manager
     */
    protected List<PluginFrame> pluginFrames;
    /**
     * The Client Interpretet
     */
    protected ClientInterpreter interpreter;
    /**
     * The client space being visualised
     */
    protected ClientSpace space;
    /**
     * The stream (if any) to be visualised
     */
    protected int selectedStream;
    /**
     * The tile manager colours for this space
     */
    protected TileManagerColors colorConfig;

    /**
     * Create a new listener for this view.
     * Any subclass must provide a constructor with exactly this signature.
     *
     * @param label Text for a label to launch this view
     */
    protected PluginListener(String label) {
        this.label = label;
    }

    /**
     * Initialise this view.
     * This is necessary because instances of PluginListeners are created by the
     * PluginManager with only a label as an argument, but are initialised by the
     * a SpaceManager.
     *
     * @param owner          The owning frame
     * @param pluginFrames   The list of plugin frames
     * @param interpreter    The client interpreter
     * @param space          The client space
     * @param selectedStream The stream (if any) visualised
     * @param colorConfig    The tile manager colors to use
     */
    public void init(AbstractFrame owner, List<PluginFrame> pluginFrames,
                     ClientInterpreter interpreter, ClientSpace space, int selectedStream,
                     TileManagerColors colorConfig) {
        this.owner = owner;
        this.pluginFrames = pluginFrames;
        this.interpreter = interpreter;
        this.space = space;
        this.selectedStream = selectedStream;
        this.colorConfig = colorConfig;
    }

    /**
     * Get the name of this view
     *
     * @return the text used for this listener
     */
    public String getLabel() {
        return label;
    }
}
