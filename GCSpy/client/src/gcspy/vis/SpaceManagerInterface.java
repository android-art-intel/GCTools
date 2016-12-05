/**
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/


package gcspy.vis;

import gcspy.vis.plugins.PluginListener;

import java.util.List;

/**
 * Interface through which the space manager communicates with the main frame
 * @author Tony Printezis
 * @author <a href="http://www/cs/kent.ac.uk">Richard Jones</a>
 */
interface SpaceManagerInterface {

    /** Clear all the views */
    void clearViews();

    /**
     * Add a view 
     * @param obj The object to add to the views combo box
     */
    void addView(Object obj);

    /**
     * Set the active stream
     * @param streamID the ID of the stream
     */
    void setActiveView(int streamID);

    /**
     * Set the block information
     * @param text The text for the block information
     */
    void setBlockInfo(String text);

    /**
     * Add a magnification manager
     * @param small The tile manager for the row of small tiles
     * @param large The tile manager for the row of large tiles
     */
    void addMagManagers(TileManager small, TileManager large);

    /**
     * Activate a space
     * @param spaceManager The space's manager
     */
    void setActive(SpaceManager spaceManager);

    /** Validate the space manager's GUI */
    void validateContainer();

    /**
     * Get the plugin's listeners for this space
     * @return a list of plugin listeners known to the space manager 
     */
    List<PluginListener> getPluginListeners();
}
