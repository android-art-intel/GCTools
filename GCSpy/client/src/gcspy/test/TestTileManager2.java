/**
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 * <p>
 * *  gcspy.vis.TestTileManager2
 * *
 * *  Second test for the canvas that manages tiles
 **/

/**
 **  gcspy.vis.TestTileManager2
 **
 **  Second test for the canvas that manages tiles
 **/

package gcspy.test;

import gcspy.utils.Utils;
import gcspy.vis.TileManager;
import gcspy.vis.TileManagerListener;
import gcspy.vis.utils.AdjustedColor;

import javax.swing.*;
import java.awt.*;

public class TestTileManager2 extends JFrame implements TileManagerListener {
    static final private int MAX_TILES = 130;

    private TileManager tileManager;
    private TileManager smallManager;
    private TileManager largeManager;

    public void tileSelected(int index) {
        smallManager.selectTile(index);
        largeManager.selectTile(index);
    }

    public void tileDeselected() {
        smallManager.deselectTile();
        largeManager.deselectTile();
    }

    public void redraw() {
    }

    private class TheListener implements TileManagerListener {
        private boolean small;

        public void tileSelected(int index) {
            if (small)
                largeManager.selectTile(index);
            else
                smallManager.selectTile(index);
            tileManager.selectTile(index);
        }

        public void tileDeselected() {
            if (small)
                largeManager.deselectTile();
            else
                smallManager.deselectTile();
            tileManager.deselectTile();
        }

        public void redraw() {
        }

        TheListener(boolean small) {
            this.small = small;
        }
    }

    public TestTileManager2() {
        Container cont = getContentPane();
        cont.setLayout(new BorderLayout());

        tileManager = new TileManager("Young Generation", MAX_TILES, false);
        tileManager.setWidth(400);
        tileManager.addListener(this);
        cont.add(tileManager, BorderLayout.CENTER);

        Container top = new Box(BoxLayout.Y_AXIS);

        smallManager = new TileManager(null, MAX_TILES, true);
        smallManager.setTileSizeSmall();
        smallManager.setWidth(180);
        smallManager.addListener(new TheListener(true));
        top.add(smallManager);

        largeManager = new TileManager(null, MAX_TILES, true);
        largeManager.setTileSizeLarge();
        largeManager.setWidth(180);
        largeManager.addListener(new TheListener(false));
        top.add(largeManager);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(top, BorderLayout.SOUTH);

        cont.add(panel, BorderLayout.WEST);

        pack();
    }

    private void clear() {
        tileManager.clear();
        smallManager.clear();
        largeManager.clear();
    }

    private void enableFooter(boolean enable) {
        tileManager.enableFooter(enable);
        smallManager.enableFooter(enable);
        largeManager.enableFooter(enable);
    }

    private void paintTile(int index, Color c) {
        tileManager.paintTile(index, c);
        smallManager.paintTile(index, c);
        largeManager.paintTile(index, c);
    }

    private void paintTile(int index, Color c, TileManager.Tile tile) {
        tileManager.paintTile(index, c, tile);
        smallManager.paintTile(index, c, tile);
        largeManager.paintTile(index, c, tile);
    }

    private void flush() {
        tileManager.flush();
        smallManager.flush();
        largeManager.flush();
    }

    public void paint1(int max) {
        enableFooter(true);

        AdjustedColor ac = new AdjustedColor(Color.red);
        int i = 0;

        clear();
        while (i < max) {
            paintTile(i, ac.generate(i, max - 1));
            if ((i % 7) == 0)
                paintTile(i, Color.black, TileManager.Tile.FOOTER);
            else if ((i % 4) == 0)
                paintTile(i, Color.darkGray, TileManager.Tile.FOOTER);
            else
                paintTile(i, Color.white, TileManager.Tile.FOOTER);
            ++i;
        }
        flush();
    }

    public void loop() {
        while (true) {
            Utils.sleep(1 * 1000);
            paint1(115);

            Utils.sleep(1 * 800);
            paint1(65);
        }
    }

    static public void main(String args[]) {
        TestTileManager2 frame = new TestTileManager2();
        frame.setSize(640, 300);
        frame.setVisible(true);
        frame.loop();
    }
}
