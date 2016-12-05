/**
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 * <p>
 * *  gcspy.vis.TestTileManager
 * *
 * *  Test for the canvas that manages tiles
 **/

/**
 **  gcspy.vis.TestTileManager
 **
 **  Test for the canvas that manages tiles
 **/

package gcspy.test;

import gcspy.utils.Utils;
import gcspy.vis.TileManager;
import gcspy.vis.utils.AdjustedColor;

import javax.swing.*;
import java.awt.*;

public class TestTileManager extends JFrame {
    static final private int MAX_TILES = 130;

    private TileManager tileManager;

    public void setWidth(int width) {
        tileManager.setWidth(width);
        pack();
    }

    public TestTileManager() {
        tileManager = new TileManager("Young Generation", MAX_TILES, true);
        getContentPane().add(tileManager);
    }

    public void paint1(int max) {
        tileManager.enableFooter(true);

        AdjustedColor ac = new AdjustedColor(Color.red, max - 1);
        int i = 0;

        tileManager.clear();
        while (i < max) {
            tileManager.paintTile(i, ac.generate(i));
            if ((i % 7) == 0)
                tileManager.paintTile(i, Color.black, TileManager.Tile.FOOTER);
            else if ((i % 4) == 0)
                tileManager.paintTile(i, Color.darkGray, TileManager.Tile.FOOTER);
            else
                tileManager.paintTile(i, Color.white, TileManager.Tile.FOOTER);
            ++i;
        }
        tileManager.flush();
    }

    public void paint2(int max) {
        tileManager.enableFooter(false);
        tileManager.clear();

        AdjustedColor ac = new AdjustedColor(Color.blue, Color.red, max - 1);
        for (int i = 0; i < max; ++i) {
            tileManager.paintTile(i, ac.generate(i));
            if ((i % 10) == 0)
                tileManager.paintTile(i, Color.yellow, TileManager.Tile.TOP_LEFT);
        }
        Color c = Color.black;
        tileManager.selectRange(2, 5, c);
        tileManager.selectRange(30, 30, c);
        tileManager.selectRange(48, 51, c);
        tileManager.selectRange(15, 19, c);
        tileManager.selectRange(38, 42, c);
        tileManager.flush();
    }

    public void paint3(int max) {
        tileManager.enableFooter(false);
        tileManager.clear();

        AdjustedColor ac =
                new AdjustedColor(Color.blue, Color.yellow, Color.red, max - 1);
        for (int i = 0; i < max; ++i) {
            tileManager.paintTile(i, ac.generate(i));
        }
        Color c = Color.black;
        tileManager.selectRange(2, 5, c);
        tileManager.selectRange(30, 30, c);
        tileManager.selectRange(48, 51, c);
        tileManager.selectRange(15, 19, c);
        tileManager.selectRange(38, 42, c);
        if (max > 105)
            tileManager.selectRange(94, 105, c);
        tileManager.flush();
    }

    private int count = 0;
    static final private int SMALL_MAX = 6;

    public void paint4(int max) {
        tileManager.enableFooter(false);
        // tileManager.setWindow(count, MAX_TILES - 2*count);

        tileManager.selectTile(count);

        tileManager.clear();

        AdjustedColor ac =
                new AdjustedColor(Color.yellow, Color.red,
                        MAX_TILES - 2 * SMALL_MAX);
        int i = 0;
        while (i < SMALL_MAX) {
            tileManager.paintTile(i, Color.black);
            ++i;
        }
        while (i < MAX_TILES - SMALL_MAX) {
            tileManager.paintTile(i, ac.generate(i - SMALL_MAX));
            ++i;
        }

        while (i < MAX_TILES) {
            tileManager.paintTile(i, Color.black);
            ++i;
        }

        tileManager.flush();

        if (count < (MAX_TILES - 1))
            count += 1;
    }

    static public void main(String args[]) {
        int count = 0;
        TestTileManager frame = new TestTileManager();
        frame.setWidth(370);
        frame.setVisible(true);

        while (true) {
            Utils.sleep(1 * 1000);
            frame.paint4(115);

            // ++count;
            count = 1;
            if ((count % 3) == 0) {
                Utils.sleep(200);
                frame.setWidth(370 + (count / 3) * 30);
            }

            Utils.sleep(1 * 800);
            frame.paint4(65);
        }
    }
}
