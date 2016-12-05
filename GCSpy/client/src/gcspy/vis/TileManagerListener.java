/**
 * * $Id: TileManagerListener.java 22 2005-06-14 05:17:45Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/


package gcspy.vis;

/**
 * Interface to listen to events on a tile manager
 * @author Tony Printezis
 */
public interface TileManagerListener {

    /**
     * A tile is selected
     * @param index The index of the tile
     */
    void tileSelected(int index);

    /** Deselect a (previously selected) tile */
    void tileDeselected();

    /** Redraw the tiles */
    void redraw();

}
