/**
 * * $Id: MarkerRangeListener.java 22 2005-06-14 05:17:45Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.vis;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * A mouse listener that updates the Tile Manager marker ranges
 * @author Tony Printezis
 */
class MarkerRangeListener extends MouseAdapter implements MouseMotionListener {

    private int startIndex;
    private int endIndex;
    private MarkerRange range;

    private Markers markers;
    private TileManager tileManager;

    private boolean add = false;
    private boolean remove = false;
    private boolean replace = false;

    private void startMarker(int m1, int m2) {
        if (replace)
            markers.clear();
        range = markers.add(m1, m2);
        tileManager.mapImage();
    }

    private void updateMarker(int m1, int m2) {
        range.set(m1, m2);
        tileManager.mapImage();
    }

    private void endMarker() {
        range.unsetNewlyAdded();
        if (add) {
            markers.remove(range);
            markers.addRange(range);
        }
        if (remove) {
            markers.remove(range);
            markers.removeRange(range);
        }
        tileManager.mapImage();
        range = null;
    }

    public void mouseMoved(MouseEvent e) {
        // NOP
    }

    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int button = gcspy.vis.utils.Utils.getMouseButton(e);
        int index = tileManager.getIndex(x, y);

        switch (button) {
            case 2:
                if (index != -1) {
                    if (startIndex != -1) {
                        if (endIndex != index) {
                            endIndex = index;
                            updateMarker(startIndex, endIndex);
                        }
                    } else {
                        startIndex = index;
                        endIndex = index;
                        startMarker(startIndex, endIndex);
                    }
                }
                break;
        }
    }

    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int button = gcspy.vis.utils.Utils.getMouseButton(e);
        int index = tileManager.getIndex(x, y);

        switch (button) {
            case 2:
                if (index != -1) {
                    startIndex = index;
                    endIndex = index;

                    add = false;
                    remove = false;
                    replace = false;

                    if (gcspy.vis.utils.Utils.isShiftPressed(e))
                        add = true;
                    if (gcspy.vis.utils.Utils.isCtrlPressed(e))
                        remove = true;
                    if (add && remove) {
                        add = false;
                        remove = false;
                    }
                    if ((!add) && (!remove))
                        replace = true;

                    startMarker(startIndex, endIndex);
                } else
                    startIndex = -1;
                break;
        }
    }

    public void mouseReleased(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int button = gcspy.vis.utils.Utils.getMouseButton(e);
        int index = tileManager.getIndex(x, y);

        switch (button) {
            case 2:
                if (startIndex != -1) {
                    if ((index != -1) && (index != endIndex)) {
                        endIndex = index;
                        updateMarker(startIndex, endIndex);
                    }
                    endMarker();

                    startIndex = -1;
                    endIndex = -1;
                }
                break;
        }
    }

    /**
     * Create a new listener for marker ranges
     * @param tileManager The tile manager
     * @param markers The markers
     */
    MarkerRangeListener(TileManager tileManager, Markers markers) {
        this.tileManager = tileManager;
        this.markers = markers;
    }

}
