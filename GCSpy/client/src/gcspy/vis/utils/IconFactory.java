/**
 * * $Id: IconFactory.java 47 2006-11-22 10:31:24Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/


package gcspy.vis.utils;

import javax.swing.*;

/**
 * Icon Factory for GUI components
 * @author Tony Printezis
 * @author <a href="http://www/cs/kent.ac.uk">Richard Jones</a>
 */
public class IconFactory {

    static private ImageIcon playIcon = null;
    static private ImageIcon playOneIcon = null;
    static private ImageIcon recIcon = null;
    static private ImageIcon pauseIcon = null;
    static private ImageIcon stopIcon = null;

    static private final String ICON_PATH = "/";

    /**
     * Create a Play icon
     * @return a play icon
     */
    static public Icon createPlayIcon() {
        if (playIcon == null) {
            playIcon = createImageIcon("play_trans.gif");
        }
        return playIcon;
    }

    /**
     * Create a Play One icon
     * @return a play one icon
     */
    static public Icon createPlayOneIcon() {
        if (playOneIcon == null) {
            playOneIcon = createImageIcon("play_one_trans.gif");
        }
        return playOneIcon;
    }

    /**
     * Create a Record icon
     * @return a record icon
     */
    static public Icon createRecIcon() {
        if (recIcon == null) {
            recIcon = createImageIcon("rec_trans.gif");
        }
        return recIcon;
    }

    /**
     * Create a Pause icon
     * @return a pause icon
     */
    static public Icon createPauseIcon() {
        if (pauseIcon == null) {
            pauseIcon = createImageIcon("pause_trans.gif");
        }
        return pauseIcon;
    }

    /**
     * Create a Stop icon
     * @return a stop icon
     */
    static public Icon createStopIcon() {
        if (stopIcon == null) {
            stopIcon = createImageIcon("stop_trans.gif");
        }
        return stopIcon;
    }

    static private ImageIcon createImageIcon(String filename) {
        //return new ImageIcon(ICON_PATH + filename);
        try {
            return new ImageIcon(Object.class.getResource(ICON_PATH + filename));
        } catch (NullPointerException e) {
            System.err.println("Could not load icon: maybe you forgot to make install?");
            throw e;
        }
    }

}
