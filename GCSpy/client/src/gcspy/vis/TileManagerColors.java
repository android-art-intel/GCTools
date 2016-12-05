/**
 * * $Id: TileManagerColors.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/


package gcspy.vis;

import gcspy.interpreter.Space;
import gcspy.interpreter.Stream;
import gcspy.utils.ColorDB;

import java.awt.*;

/**
 * Color configuration for the tile manager
 * @author Tony Printezis
 */
public class TileManagerColors {

    static private final ColorDB colorDB = ColorDB.getColorDB();
    static private final Color SELECTED_DEFAULT = colorDB.getColor("White");
    static private final Color MARKER_DEFAULT = colorDB.getColor("Yellow");
    static private final Color ACTIVE_MARKER_DEFAULT = colorDB.getColor("Cyan");
    static private final Color SEPARATOR_DEFAULT = colorDB.getColor("White");
    static private final Color LINK_DEFAULT = colorDB.getColor("Dark Gray");
    static private final Color STREAM_DEFAULTS[] = {colorDB.getColor("Red"),
            colorDB.getColor("Off White"), colorDB.getColor("Magenta"),
            colorDB.getColor("Green"), colorDB.getColor("Orange")};

    private Color streamColors[];
    private Color defaultStreamColors[];
    private Color selectedColor;
    private Color markerColor;
    private Color activeMarkerColor;
    private Color separatorColor;
    private Color linkColor;

    /** ****************** Data Setting ******************* */

    /**
     * Set the selected colour
     * @param selectedColor the colour
     */
    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
    }

    /**
     * Set the marker colour
     * @param markerColor the colour
     */
    public void setMarkerColor(Color markerColor) {
        this.markerColor = markerColor;
    }

    /**
     * Set the active marker colour
     * @param activeMarkerColor the colour
     */
    public void setActiveMarkerColor(Color activeMarkerColor) {
        this.activeMarkerColor = activeMarkerColor;
    }

    /**
     * Set the separator colour
     * @param separatorColor the colour
     */
    public void setSeparatorColor(Color separatorColor) {
        this.separatorColor = separatorColor;
    }

    /**
     * Set the link colour
     * @param linkColor the colour
     */
    public void setLinkColor(Color linkColor) {
        this.linkColor = linkColor;
    }

    /**
     * Set the colour for a stream
     * @param index the stream ID
     * @param streamColor the colour
     */

    public void setStreamColor(int index, Color streamColor) {
        this.streamColors[index] = streamColor;
    }

    /** ****************** Accessor Methods ******************* */

    /**
     * get teh selected colour
     * @return the selected colour
     */
    public Color getSelectedColor() {
        return selectedColor;
    }

    /**
     * Get the colour used for markers
     * @return the marker colour
     */
    public Color getMarkerColor() {
        return markerColor;
    }

    /**
     * Get the colour used for active markers
     * @return the active marker colour
     */
    public Color getActiveMarkerColor() {
        return activeMarkerColor;
    }

    /**
     * Get the colour used for separators
     * @return the separator colour
     */
    public Color getSeparatorColor() {
        return separatorColor;
    }

    /**
     * Get the colour used for links
     * @return the link colour
     */
    public Color getLinkColor() {
        return linkColor;
    }

    /**
     * Return a stream's colour
     * @param index the stream ID
     * @return the selected colour
     */
    public Color getStreamColor(int index) {
        return streamColors[index];
    }

    /** ****************** Utilities ******************* */

    private void setupDefaults(Space space) {
        if (space == null)
            defaultStreamColors = STREAM_DEFAULTS;
        else {
            int len = space.getStreamNum();
            defaultStreamColors = new Color[len];
            for (int i = 0; i < len; ++i) {
                Stream stream = space.getStream(i);
                defaultStreamColors[i] = stream.getColor();
            }
        }
    }

    /**
     * Revert the colours for tiles, selections, markers, active markers,
     * separators, links
     * to their defaults
     */
    public void revertToDefaults() {
        selectedColor = SELECTED_DEFAULT;
        markerColor = MARKER_DEFAULT;
        activeMarkerColor = ACTIVE_MARKER_DEFAULT;
        separatorColor = SEPARATOR_DEFAULT;
        linkColor = LINK_DEFAULT;
        streamColors = new Color[defaultStreamColors.length];
        System.arraycopy(defaultStreamColors, 0, streamColors, 0,
                defaultStreamColors.length);
    }

    /** ****************** Constructors ******************* */

    /**
     * Create new set of (default) colours
     */
    public TileManagerColors() {
        this(null);
        revertToDefaults();
    }

    /**
     * Create new set of (default) colours for a space
     * @param space The space
     */
    public TileManagerColors(Space space) {
        setupDefaults(space);
        revertToDefaults();
    }

}
