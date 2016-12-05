/**
 * * $Id: AbstractFrame.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 * <p>
 * *  gcspy.vis.utils.AbstractFrame
 * *
 * *  Facilities for the frames
 **/

/**
 **  gcspy.vis.utils.AbstractFrame
 **
 **  Facilities for the frames
 **/

package gcspy.vis.utils;

import javax.swing.*;
import java.awt.*;

/**
 * Facilities for all frames
 * @author Tony Printezis
 * @author <a href="http://www/cs/kent.ac.uk">Richard Jones</a>
 *
 */
abstract public class AbstractFrame extends JFrame {

    /** Positions of a frame relative to its parent */
    protected enum Position {
        POS_NONE, POS_CENTER, POS_TOP_LEFT, POS_TOP_RIGHT, POS_LEFT_COMPONENT,
        POS_RIGHT_COMPONENT, POS_BOTTOM_LEFT, POS_BOTTOM_RIGHT, POS_ROOT_CENTER,
        POS_ROOT_HOR_CENTER
    }

    /** Padding for the frame border */
    static protected final int BORDER_PADDING = 10;
    /** Padding for the frame title */
    static protected final int TITLE_PADDING = 30;

    private AbstractFrame owner;

    private Position position = Position.POS_NONE;

    private boolean reposition = false;

    private boolean first = true;

    private JComponent comp = null;

    static private final int screenMinX, screenMaxX, screenMinY, screenMaxY;

    static protected final int bgWidth, bgHeight;

    static {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension sd = toolkit.getScreenSize();

        bgWidth = sd.width;
        bgHeight = sd.height;

        screenMinX = BORDER_PADDING;
        screenMinY = TITLE_PADDING;
        screenMaxX = bgWidth - BORDER_PADDING;
        screenMaxY = bgHeight - BORDER_PADDING;
    }

    static private int checkMinX(int x) {
        if (x < screenMinX)
            return screenMinX;
        return x;
    }

    static private int checkMaxX(int x, int width) {
        if ((x + width) > screenMaxX)
            return screenMaxX - width;
        return x;
    }

    static private int checkMinY(int y) {
        if (y < screenMinY)
            return screenMinY;
        return y;
    }

    static private int checkMaxY(int y, int height) {
        if ((y + height) > screenMaxY)
            return screenMaxY - height;
        return y;
    }

    /**
     * Does the frame extend off the right of the screen
     * @return true if frame extends off the right of the screen
     */
    protected boolean sufficientlyOffRight() {
        int width = getWidth();
        Point pos = getLocation();
        int limit = 100;
        return pos.x + width + limit > bgWidth;
    }

    private void placeIt() {
        placeIt(owner, position);
    }

    /**
     * Place the frame
     * @param position The position to place it
     */
    protected void placeIt(Position position) {
        placeIt(null, position);
    }

    /**
     * Place the frame relative to its owner
     * @param owner the owner
     * @param position The position to place it
     */
    protected void placeIt(AbstractFrame owner, Position position) {
        if (position == Position.POS_NONE)
            return;

        int x = 0, y = 0;
        int width = getWidth();
        int height = getHeight();
        Point pos = getLocation();
        Point loc;

        if (owner != null) {
            Point p = owner.getLocation();
            Dimension d = owner.getSize();

            switch (position) {
                case POS_CENTER:
                    x = p.x + (d.width - width) / 2;
                    y = p.y + (d.height - height) / 2;
                    break;
                case POS_TOP_LEFT:
                    x = p.x;
                    y = p.y - height - TITLE_PADDING - BORDER_PADDING;
                    y = checkMinY(y);
                    break;
                case POS_TOP_RIGHT:
                    x = p.x + d.width + BORDER_PADDING - width;
                    y = p.y - height - TITLE_PADDING - BORDER_PADDING;
                    y = checkMinY(y);
                    break;
                case POS_LEFT_COMPONENT:
                    x = p.x - width - BORDER_PADDING;
                    x = checkMinX(x);
                    loc = comp.getLocationOnScreen();
                    y = loc.y - height / 2;

                    y = checkMaxY(y, height);
                    y = checkMinY(y);
                    break;
                case POS_RIGHT_COMPONENT:
                    x = p.x + d.width + BORDER_PADDING;
                    x = checkMaxX(x, width);
                    loc = comp.getLocationOnScreen();
                    y = loc.y - height / 2;

                    y = checkMaxY(y, height);
                    y = checkMinY(y);
                    break;
                case POS_BOTTOM_LEFT:
                    x = p.x;
                    y = p.y + d.height + TITLE_PADDING;
                    y = checkMaxY(y, height);
                    break;
                case POS_BOTTOM_RIGHT:
                    x = p.x + d.width + BORDER_PADDING - width;
                    y = p.y + d.height + TITLE_PADDING;
                    y = checkMaxY(y, height);
                    break;
            }
        } else {
            switch (position) {
                case POS_ROOT_CENTER:
                    x = (bgWidth - width) / 2;
                    y = (bgHeight - height) / 2;
                    break;
                case POS_ROOT_HOR_CENTER:
                    x = (bgWidth - width) / 2;
                    y = pos.y;
                    break;
            }
        }

        setLocation(x, y);
    }

    /**
     * Set a component relative to which the frame can be placed
     * @param comp The component
     */
    public void setPositionalComponent(JComponent comp) {
        this.comp = comp;
    }


    public void setVisible(boolean b) {
        if (b && !isVisible() && (first || reposition)) {
            first = false;
            placeIt();
        }
        super.setVisible(b);
        if (b) {
      /* for 1.4 */
      /* setExtendedState(Frame.NORMAL); */
            setState(AbstractFrame.NORMAL);
        }
    }

    /**
     * Set whether the frame is resizable
     * @param r True if this frame is resizable
     */
    public void setResizable(boolean r) {
        // super.setResizable(r);
    }

    /**
     * Show a warning dialog
     * @param message A warning message
     */
    protected void showWarning(String message) {
        new WarningDialog(this, message).setVisible(true);
    }

    /** Tear down this frame */
    public void destroy() {
        owner = null;
        comp = null;
        dispose();
    }


    /**
     * A new AbstractFrame
     * @param owner The owning frame
     * @param position Where to place the frame relative to its parent
     * @param reposition
     */
    protected AbstractFrame(AbstractFrame owner, Position position, boolean reposition) {
        super();
        this.owner = owner;
        this.position = position;
        this.reposition = reposition;
    }

    /**
     * A new AbstractFrame (with no owner)
     * @param position Where to place the frame relative to its parent
     */
    protected AbstractFrame(Position position) {
        this(null, position, false);
    }

}
