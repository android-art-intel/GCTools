/*
 * $Id: PluginFrame.java 28 2005-06-20 13:13:35Z rej $
 * Copyright Richard Jones, University of Kent, 2005
 *
 * See the file "Kent_license.terms" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */

package gcspy.vis.plugins;

import gcspy.interpreter.client.ClientInterpreter;
import gcspy.interpreter.client.ClientSpace;
import gcspy.interpreter.client.EventListener;
import gcspy.interpreter.client.SpaceListener;
import gcspy.vis.DisconnectListener;
import gcspy.vis.TileManagerColors;
import gcspy.vis.utils.AbstractFrame;

import java.util.List;

/**
 * Abstract base class for plugins' frames.
 * All plugin frames must extend this class
 *
 * @author <a href="http://www/cs/kent.ac.uk">Richard Jones</a>
 */
public abstract class PluginFrame extends AbstractFrame
        implements EventListener, SpaceListener, DisconnectListener {

    /**
     * The Client Interpreter
     */
    protected ClientInterpreter interpreter;
    /**
     * A The list of plugin frames known to the space manager
     */
    protected List<PluginFrame> pluginFrames;
    /**
     * The client space being visualised
     */
    protected ClientSpace space;
    /**
     * The tile manager colours for this space
     */
    protected TileManagerColors tmColors;

    /**
     * Create a new frame for a plugin
     *
     * @param owner      The owner, i.e. a MainFrame
     * @param position   Where to place the frame relative to its parent
     * @param reposition
     */
    public PluginFrame(AbstractFrame owner, Position position, boolean reposition) {
        super(owner, position, reposition);
    }

    /**
     * Initialise a Plugin Frame
     *
     * @param interpreter  The ClientInterpreter
     * @param pluginFrames The list of PluginFrames to which this plugin belongs
     * @param space        The ClientSpace
     * @param tmColors     The TileManagerColors
     * @param title        A title for the frame
     */
    protected void setup(ClientInterpreter interpreter,
                         List<PluginFrame> pluginFrames, ClientSpace space,
                         TileManagerColors tmColors, String title) {

        setTitle(title);
        this.interpreter = interpreter;
        this.pluginFrames = pluginFrames;
        this.space = space;
        this.tmColors = tmColors;
        pluginFrames.add(this);
        interpreter.addEventListener(this);
        interpreter.addSpaceListener(this);
    }

    public abstract void event(int id, int elapsedTime, int compensationTime);

    public abstract void space(ClientSpace space);

    public abstract void disconnect(boolean reconnecting);

    /**
     * Shut down  this view
     */
    protected void shutdown() {
        interpreter.removeEventListener(this);
        interpreter.removeSpaceListener(this);
        setVisible(false);
        destroy();
    }

}
