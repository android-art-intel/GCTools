/**
 * * $Id: TileManager.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/


package gcspy.vis;

import gcspy.utils.ColorDB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Canvas that manages tiles
 * @author Tony Printezis
 * @author <a href="http://www/cs/kent.ac.uk">Richard Jones</a>
 */
public class TileManager extends JComponent {

    /** Tile types */
    public enum Tile {
        FULL, SOLID, FOOTER, TOP, BOTTOM, TOP_LEFT, BOTTOM_RIGHT, ZERO, OVERFLOW, MORE_TILES
    }

    static final private ColorDB colorDB = ColorDB.getColorDB();

    static final private Color bgColor = colorDB.getColor("Gray");

    static final private Color zeroColor = colorDB.getColor("Dark Gray");

    static final private Color frameColor = colorDB.getColor("Black");

    static final private Color offColor = colorDB.getColor("Light Gray");

    static final private Color overflowColor = colorDB.getColor("Dark Gray");

    static final private Color textColor = colorDB.getColor("Black");

    static final private int fontSize = 12;

    static final private Font TEXT_FONT = new Font("SansSerif", Font.BOLD,
            fontSize);

    private TileManagerColors colorConfig;

    /* for double buffering */
    private BufferedImage image = null;

    private Graphics imageG = null;

    private BufferedImage dbImage = null;

    private Graphics dbImageG = null;

    private boolean drawIt = false;

    /* number of max tiles */
    private int maxTiles;

    /* index of first tile in the visibility window */
    private int low;

    /* number of tiles in the visibility window */
    private int maxTilesCalc;

    /* number of visible tiles according to current canvas size */
    private int currentVisibleTiles;

    /* set to true of not all tiles fit */
    private boolean overflow = false;

    /* gap geometry */
    private int borderGap;

    private int gap;

    /* selection geometry */
    private int selectionStart;

    private int selectionEnd;

    /* tile geometry */
    private int tileW;

    private int tileH;

    /* solid (i.e. minus footer) height */
    private int tileSH;

    /* footer */
    private boolean footer = false;

    private int footerH;

    /* tile half dimensions */
    private int tileHW;

    private int tileHH;

    private int columns;

    private int rows;

    /* current width and height */
    private int width;

    private int height;

    /* left and top padding */
    private int leftBorder = 0;

    private int topBorder = 0;

    /* extra left padding for single row mode */
    private int extraLeftPadding = 0;

    private String title;

    /* single row stuff */
    private boolean singleRow;

    /* selected tile */
    private boolean selected;

    private boolean shiftSelected;

    private int selectedTile;

    private int selectedTileTmp;

    /* markers */
    private Markers markers = new Markers();

    /* listeners */
    private java.util.List<TileManagerListener> listeners = new ArrayList<>();

    private MouseL mouseListener;

    private MarkerRangeListener markerRangeListener;

    /* frame drawing */
    private boolean drawFrame = false;

    /**
     * * Mappings
     */

    private int adjust(int index) {
        return index - low;
    }

    private int unadjust(int indexA) {
        return indexA + low;
    }

    /* map adjusted index to x coordinate */
    private int getX(int indexA) {
        int a = indexA % columns;
        int res = leftBorder + extraLeftPadding + borderGap + a * tileW;
        if (a > 0)
            res += a * gap;
        return res;
    }

    /* map adjusted index to y coordinate */
    private int getY(int indexA) {
        int a = indexA / columns;
        int res = topBorder + borderGap + a * tileH;
        if (a > 0)
            res += a * gap;
        return res;
    }

    /**
     * Get the index of a tile
     * @param x The x coord
     * @param y The y coord
     * @return The unadjusted index
     */
    public int getIndex(int x, int y) {
        if (x < (borderGap + leftBorder + extraLeftPadding))
            return -1;
        int rightMostBorder = extraLeftPadding + leftBorder + borderGap + columns
                * tileW + (columns - 1) * gap;
        if (x > rightMostBorder)
            return -1;
    /*
     * if (x > (width - borderGap - extraLeftPadding)) return -1;
     */

        if (y < (borderGap + topBorder))
            return -1;
        if (y > (height - borderGap))
            return -1;

        int xx = x - (borderGap + leftBorder + extraLeftPadding);
        xx += gap / 2;
        int yy = y - (borderGap + topBorder);
        yy += gap / 2;
        int index = (xx / (tileW + gap)) + (yy / (tileH + gap)) * columns;
        index = unadjust(index);
        if (index >= unadjust(currentVisibleTiles))
            index = -1;

        return index;
    }

  /*
   *  General painting stuff
   */

    private void drawTitle(Graphics g) {
        if (title != null) {
            g.setFont(TEXT_FONT);
            g.setColor(textColor);
            FontMetrics fm = g.getFontMetrics();
            int asc = fm.getAscent();
            g.drawString(title, borderGap, borderGap + asc);
        }
    }

    private void drawFrame(Graphics g, Color c) {
        g.setColor(c);
        g.drawRect(0, 0, width - 1, height - 1);
        g.drawRect(1, 1, width - 3, height - 3);
    }

    private void drawFrame(Graphics g) {
        drawFrame(g, frameColor);
    }

    private void clearBackground(Graphics g) {
        g.setColor(bgColor);
        g.fillRect(0, 0, width, height);

        if (singleRow) {
            g.setColor(frameColor);
            g.drawRect(0, 0, width - 1, height - 1);
        }

        drawTitle(g);
    }

    /** Clear the tile area */
    public void clear() {
        drawIt = true;
        synchronized (this) {
            if ((image == null) || (getWidth() != width) || (getHeight() != height)) {
                do {
          /*
           * this do-while is a gross way to ensure that the component is
           * properly initialised before we start drawing anything on it
           */
                    width = getWidth();
                    height = getHeight();
                    gcspy.utils.Utils.sleep(5);
                } while ((width == 0) || (height == 0));
                calcSizes();
                image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
                imageG = image.getGraphics();
                dbImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
                dbImageG = dbImage.getGraphics();
            }
        }
        calcSelectedTile();
        clearBackground(imageG);

        if (overflow)
            paintTile(currentVisibleTiles - 1, overflowColor, Tile.MORE_TILES, true);
    }

    private void mapImage(Graphics g) {
        if ((g != null) && (image != null)) {
            dbImageG.drawImage(image, 0, 0, null);

            Iterator<MarkerRange> iter = markers.iterator();
            while (iter.hasNext()) {
                MarkerRange range = iter.next();
                Color c;
                if (range.isNewlyAdded()) {
                    c = colorConfig.getActiveMarkerColor();
                } else {
                    c = colorConfig.getMarkerColor();
                }
                selectRange(range.getFrom(), range.getTo(), c, dbImageG);
            }

            if (selected)
                selectTile(selectedTile, colorConfig.getSelectedColor(), dbImageG);
            if (drawFrame)
                drawFrame(dbImageG);

            g.drawImage(dbImage, 0, 0, null);
        }
    }


    /** Draw the image including markers, selected tile */
    void mapImage() {
        mapImage(getGraphics());
    }

    /** Flush the image */
    synchronized public void flush() {
        if (drawIt) {
            mapImage();
            drawIt = false;
        }
    }

    synchronized public void paintComponent(Graphics g) {
        if ((getWidth() != width) || (getHeight() != height)) {
            callRedrawListeners();
        }
        mapImage(g);
    }

  /*
   * Selecting Tiles
   */

    /**
     * Select a range on a single row, the indexes passed are adjusted, it
     * operates over the imageG graphics object
     * @param fromA adjusted from index
     * @param toA adjusted to index
     * @param c The rectangle color
     */
    private void selectSimpleRange(int fromA, int toA, Color c) {
        selectSimpleRange(fromA, toA, c, imageG);
    }

    /**
     * Selects a range on a single row, the indexes passed are adjusted, it
     * operates over the graphics object passed to it
     * @param fromA adjusted from index
     * @param toA adjusted to index
     * @param c The rectangle color
     * @param g The graphics context
     */
    private void selectSimpleRange(int fromA, int toA, Color c, Graphics g) {
        int x = getX(fromA);
        int y = getY(fromA);

        int w = (toA - fromA + 1) * tileW + (toA - fromA) * gap;

        g.setColor(c);
        for (int i = selectionStart; i <= selectionEnd; ++i) {
            g.drawRect(x - i, y - i, w + 2 * i - 1, tileH + 2 * i - 1);
        }
    }

    /**
     * Returns the last (adjusted) index on the row where the adjusted index
     * parameter lies
     * @param indexA The adjusted index
     * @return the last (adjusted) index
     */
    private int getLast(int indexA) {
        if ((indexA + 1) % columns == 0)
            return indexA;
        else
            return (indexA / columns + 1) * columns - 1;
    }

    /**
     *  Paints a separator *before* the tile
     *  @param index the unadjusted index
     */
    public void paintSeparator(int index) {
        paintSeparator(index, colorConfig.getSeparatorColor(), imageG);
    }

    /**
     *  Paints a separator *before* the tile
     *  @param index the unadjusted index
     *  @param c The separator color
     *  @param g The graphics context
     */
    private void paintSeparator(int index, Color c, Graphics g) {
        int indexA = adjust(index);

        if (indexA < 0)
            return;
        if (indexA > currentVisibleTiles)
            return;

        if (indexA < currentVisibleTiles) {
            int x = getX(indexA);
            int y = getY(indexA);

            // this is the left one
            paintSeparator(x, y, c, g);
        }
        if (indexA % columns == 0) {
            int indexA2 = indexA - 1;
            if (indexA2 >= 0) {
                int x2 = getX(indexA2);
                int y2 = getY(indexA2);

                // this is the right one
                paintSeparator(x2 + tileW + gap, y2, c, g);
            }
        }
    }

    private void paintSeparator(int x, int y, Color c, Graphics g) {
        g.setColor(c);
        if (gap <= 2) {
            int tileWPortion = tileW / 3;
            g.fillRect(x - gap, y - gap, gap, tileH + 2 * gap);
            if (x == getX(0)) {
                // on the left edge
                g.fillRect(x - gap, y - gap, gap + tileWPortion, gap);
                g.fillRect(x - gap, y + tileH, gap + tileWPortion, gap);
            } else if (x > getX(columns - 1)) {
                // on the right edge
                g.fillRect(x - gap - tileWPortion, y - gap, gap + tileWPortion, gap);
                g.fillRect(x - gap - tileWPortion, y + tileH, gap + tileWPortion, gap);
            } else {
                g
                        .fillRect(x - gap - tileWPortion, y - gap, gap + 2 * tileWPortion,
                                gap);
                g.fillRect(x - gap - tileWPortion, y + tileH, gap + 2 * tileWPortion,
                        gap);
            }
        } else {
            int tileWPortion = tileW / 3;
            g.fillRect(x - gap + 1, y - gap + 1, gap - 2, tileH + 2 * gap - 2);
            if (x == getX(0)) {
                // on the left edge
                g.fillRect(x - gap + 1, y - gap + 1, tileWPortion + gap - 1, gap - 2);
                g.fillRect(x - gap + 1, y + tileH + 1, tileWPortion + gap - 1, gap - 2);
            } else if (x > getX(columns - 1)) {
                // on the right edge
                g.fillRect(x - gap - tileWPortion - 1, y - gap + 1, tileWPortion + gap,
                        gap - 2);
                g.fillRect(x - gap - tileWPortion - 1, y + tileH + 1, tileWPortion
                        + gap, gap - 2);
            } else {
                g.fillRect(x - gap - tileWPortion, y - gap + 1, (tileWPortion) * 2
                        + gap, gap - 2);
                g.fillRect(x - gap - tileWPortion, y + tileH + 1, (tileWPortion) * 2
                        + gap, gap - 2);
            }
        }
    }

    /**
     *  Paints the link *after* the tile
     *  @param index the unadjusted index
     */
    public void paintLink(int index) {
        paintLink(index, colorConfig.getLinkColor(), imageG);
    }

    /* paints the link *after* the tile -- index is unadjusted */
    private void paintLink(int index, Color c, Graphics g) {
        int indexA = adjust(index);

        if (indexA < -1)
            return;
        if (indexA >= currentVisibleTiles)
            return;

        if (indexA > -1) {
            int x = getX(indexA);
            int y = getY(indexA);

            // this is the right one
            paintLink(x + tileW, y, c, g);

        }
        if ((indexA + 1) % columns == 0) {
      /* tile is at the right edge */
            int indexA2 = indexA + 1;
            if (indexA2 < currentVisibleTiles) {
                int x2 = getX(indexA2);
                int y2 = getY(indexA2);

                // this is the left one
                paintLink(x2 - gap, y2, c, g);
            }
        }
    }

    private void paintLink(int x, int y, Color c, Graphics g) {
        int linkGap = tileH / 5;
        g.setColor(c);
        g.fillRect(x, y + linkGap, gap, tileH - 2 * linkGap);
    }

    /* selects a single tile -- index is unadjusted */
    private void selectTile(int index, Color c, Graphics g) {
        selectRange(index, index, c, g);
    }

    /**
     *  Selects a tile range
     *  @param from unadjusted from index
     *  @param to unadjusted to index
     *  @param c the selection colour
     */
    public void selectRange(int from, int to, Color c) {
        selectRange(from, to, c, imageG);
    }

    /* selects a tile range -- from and to are unadjusted */
    private void selectRange(int from, int to, Color c, Graphics g) {
        int fromA = adjust(from);
        int toA = adjust(to);

        if (toA < 0)
            return;
        if (fromA >= currentVisibleTiles)
            return;
        if (fromA < 0)
            fromA = 0;
        if (toA >= currentVisibleTiles)
            toA = currentVisibleTiles - 1;

        int curr = fromA;
        int currTo;
        int last;
        while (true) {
            last = getLast(curr);
            if (toA < last)
                currTo = toA;
            else
                currTo = last;
            selectSimpleRange(curr, currTo, c, g);
            if (toA <= last)
                break;
            else
                curr = last + 1;
        }
    }

  /*
   * Tile Painting
   */

    /**
     *  Paints a tile with the 'off' color
     *  @param index the unadjusted index
     */
    public void setTileUnused(int index) {
        paintTile(index, offColor, Tile.FULL);
        paintTile(index, zeroColor, Tile.ZERO);
    }

    /**
     * Paints a tile
     * @param index the unadjusted index
     * @param c the colour of the tile
     */
    public void paintTile(int index, Color c) {
        paintTile(index, c, Tile.FULL);
    }

    /**
     *  Paints a tile with value zero
     * @param index the unadjusted index
     */
    public void paintTileZero(int index) {
        paintTile(index, zeroColor, Tile.ZERO);
    }

    /**
     * Paints a tile with overflow
     * @param index the unadjusted index
     * @param c The tile colour
     */
    public void paintTileOverflow(int index, Color c) {
        paintTile(index, c, Tile.OVERFLOW);
    }

    /**
     * Paints a tile with overflow
     * @param index the unadjusted index
     * @param c The tile colour
     * @param tile the tile kind
     */
    public void paintTile(int index, Color c, Tile tile) {
        paintTile(index, c, tile, false);
    }

    /**
     * Paints a tile with overflow
     * @param index the unadjusted index
     * @param c The colour of the tile
     * @param tile the tile kind
     * @param force force the painting regardless of overflow or visibility
     */
    public void paintTile(int index, Color c, Tile tile, boolean force) {
        int indexA = adjust(index);
        if (indexA < 0)
            return;
        if (indexA >= currentVisibleTiles)
            return;

        if (!force && overflow && (index == (currentVisibleTiles - 1)))
            return;

        int x = getX(indexA);
        int y = getY(indexA);
        imageG.setColor(c);

        switch (tile) {
            case FULL:
                imageG.fillRect(x, y, tileW, tileH);
                break;
            case SOLID:
                imageG.fillRect(x, y, tileW, tileSH);
                break;
            case FOOTER:
                imageG.fillRect(x, y + tileSH, tileW, footerH);
                break;
            case TOP:
                imageG.fillRect(x, y, tileW, tileHH);
                break;
            case BOTTOM:
                imageG.fillRect(x, y + tileHH, tileW, tileH - tileHH);
                break;
            case TOP_LEFT:
                imageG.fillRect(x, y, tileHW, tileHH);
                break;
            case BOTTOM_RIGHT:
                imageG.fillRect(x + tileHW, y + tileHH, tileW - tileHW, tileH - tileHH);
                break;
            case ZERO:
                imageG.drawRect(x, y, tileW - 1, tileH - 1);
                break;
            case OVERFLOW:
                // int w = tileW / 3;
                // int h = tileH / 3;
                // imageG.fillRect(x, y, tileW, tileH);
                // imageG.setColor(bgColor);
                // imageG.fillRect(x+w, y+h, tileW-2*w, tileH-2*h);

                int w = tileW / 5;
                int h = tileH / 5;
                imageG.fillRect(x + w, y + h, tileW - 2 * w, tileH - 2 * h);
                imageG.drawRect(x, y, tileW - 1, tileH - 1);

                // imageG.fillRect(x, y, tileW, tileH);
                // imageG.setColor(bgColor);
                // imageG.drawRect(x+1, y+1, tileW-3, tileH-3);
                break;
            case MORE_TILES:
                int xs[] = {x, x + tileW - 1, x};
                int ys[] = {y, y + tileH / 2, y + tileH - 1};
                imageG.fillPolygon(xs, ys, 3);
                break;
        }
    }

  /*
   *  Calculating sizes
   */

    /**
     * Set the number of columns in the tiles area
     * @param cols the number of columns
     */
    public void setColumns(int cols) {
        setWidth(cols * tileW + (cols - 1) * gap + leftBorder + 2 * borderGap);
    }

    /**
     * Set the width of the tiles area
     * @param widthNew the new width
     */
    public void setWidth(int widthNew) {
    /* 'remove' the border */
        int widthNB = widthNew - leftBorder - 2 * borderGap;
        int heightNew;

        if (widthNB < tileW) {
            columns = 0;
            rows = 0;
        } else {
            columns = (widthNB + gap) / (tileW + gap);
            if (singleRow) {
                rows = 1;
            } else {
                if ((maxTilesCalc % columns) == 0)
                    rows = maxTilesCalc / columns;
                else
                    rows = maxTilesCalc / columns + 1;
            }
            heightNew = topBorder + 2 * borderGap + rows * tileH + (rows - 1) * gap;
            setPreferredSize(new Dimension(widthNew, heightNew));
        }
    }

    private void calcSelectedTile() {
        if (singleRow) {
            selectedTile = selectedTileTmp;
            if (shiftSelected) {
                int visible = (currentVisibleTiles < columns) ? currentVisibleTiles
                        : columns;
                int first = selectedTile - visible / 2;
                if (first < 0)
                    first = 0;
                if (first + visible > maxTiles)
                    first = maxTiles - visible;
                low = first;
                shiftSelected = false;
            }
        }
    }

    /**
     * Resize the tiles area
     * @param tileNum The new number of tiles in the area
     */
    public void resize(int tileNum) {
        maxTiles = tileNum;
        maxTilesCalc = tileNum;
        calcSizes();

        if ((singleRow) && ((low + currentVisibleTiles) > tileNum)) {
            if (tileNum > columns) {
                low = tileNum - columns;
                currentVisibleTiles = columns;
            } else {
                low = 0;
                currentVisibleTiles = tileNum;
            }
        }

    /*
     * if (singleRow && selected) { if (selectedTile >= tileNum) { if (tileNum >
     * columns) { low = tileNum - columns; currentVisibleTiles = columns; } else {
     * low = 0; currentVisibleTiles = tileNum; } } }
     */
    }

    private void calcSizes() {
    /* 'remove' the border */
        int widthNB = width - leftBorder - 2 * borderGap;
        int heightNB = height - topBorder - 2 * borderGap;

        if ((widthNB < tileW) || (heightNB < tileH)) {
            columns = 0;
            rows = 0;
            currentVisibleTiles = 0;
        } else {
            columns = (widthNB + gap) / (tileW + gap);
            rows = (heightNB + gap) / (tileH + gap);
            if (maxTilesCalc > (columns * rows))
                currentVisibleTiles = columns * rows;
            else {
                currentVisibleTiles = maxTilesCalc;
            }

            overflow = false;
            if (!singleRow) {
                if (currentVisibleTiles < maxTilesCalc) {
                    overflow = true;
                }
            }

            if (singleRow) {
                if ((currentVisibleTiles > 0) && (currentVisibleTiles % 2 == 0)) {
                    --currentVisibleTiles;
                    --columns;
                }
            }

            if (singleRow) {
                int visible = (currentVisibleTiles < columns) ? currentVisibleTiles
                        : columns;
                int usedWidth = visible * tileW + (visible - 1) * gap + 2 * borderGap
                        + leftBorder;
                extraLeftPadding = (width - usedWidth) / 2;
            }
        }
    }

  /*
   *  Settings Adjustements
   */

    /**
     * Enable/disable the footer
     * @param enable True if the footer is enabled
     */
    public void enableFooter(boolean enable) {
        footer = enable;
        calcFooterSize();
    }

    /**
     *
     * @param low
     * @param num
     */
    public void setWindow(int low, int num) {
        this.low = low;
        maxTilesCalc = num;

    /* force clear to re-create the image */
        width = 0;
        height = 0;
    }

    private void setTileSizeDefault() {
        tileW = 10;
        tileH = 16;
        gap = 2;
        borderGap = 5;

        selectionStart = 1;
        selectionEnd = 2;

        calcTileSize();
    }

    /** Set tiles to be large  */
    public void setTileSizeLarge() {
        tileW = 20;
        tileH = 32;
        gap = 3;
        borderGap = 6;

        selectionStart = 1;
        selectionEnd = 3;

        calcTileSize();
    }

    /** Set tiles to be small  */
    public void setTileSizeSmall() {
        tileW = 6;
        tileH = 10;
        gap = 1;
        borderGap = 5;

        selectionStart = 1;
        selectionEnd = 1;

        calcTileSize();
    }

    /** Set tiles to be tiny  */
    public void setTileSizeTiny() {
        tileW = 2;
        tileH = 4;
        gap = 1;
        borderGap = 4;

        selectionStart = 1;
        selectionEnd = 1;

        calcTileSize();
    }

    private void calcTileSize() {
        tileHW = tileW / 2;
        calcFooterSize();
    }

    private void calcFooterSize() {
        if (footer) {
            footerH = tileH / 4;
        } else {
            footerH = 0;
        }
        tileSH = tileH - footerH;
        tileHH = tileSH / 2;
    }

    /**
     * Select a tile
     * @param selectedTile the tile
     */
    public void selectTile(int selectedTile) {
        selected = true;
        if (singleRow) {
            shiftSelected = true;
            selectedTileTmp = selectedTile;
        } else {
            this.selectedTile = selectedTile;
            mapImage();
        }
    }

    /** Deselect a (previously selected, if any) tile */
    public void deselectTile() {
        if (selected) {
            selected = false;
            mapImage();
        }
    }

    /**
     * Draw the frame if not a single row and drawFrame has changed
     * @param drawFrame
     */
    public void setDrawFrame(boolean drawFrame) {
        if (!singleRow) {
            boolean change = (drawFrame != this.drawFrame);
            this.drawFrame = drawFrame;
            if (change && (imageG != null)) {
                mapImage();
            }
        }
    }

    /*
     * Tile manager listeners
     */

    /**
     * Add a listener
     * @param listener the listener
     */
    public void addListener(TileManagerListener listener) {
        listeners.add(listener);
    }

    private void callListenersSelected(int index) {
        for (TileManagerListener listener : listeners)
            listener.tileSelected(index);
    }

    private void callListenersDeselected() {
        listeners.forEach(TileManagerListener::tileDeselected);
    }

    private void callRedrawListeners() {
        listeners.forEach(TileManagerListener::redraw);
    }

  /*
   * Mouse Listener
   */

    private class MouseL extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            int button = gcspy.vis.utils.Utils.getMouseButton(e);
            int index = getIndex(x, y);

            switch (button) {
                case 1:
                    if (index != -1) {
                        if (singleRow) {
                            shiftSelected = false;
                            selectedTileTmp = index;
                        }
                        selected = true;
                        selectedTile = index;
                        mapImage();
                        callListenersSelected(index);
                    }
                    break;

                case 2:
                    // NOP
                    break;

                case 3:
                    if (selected) {
                        selected = false;
                        mapImage();
                        callListenersDeselected();
                    }
                    break;
            }
        }
    }

    /** Clear all the markers */
    public void clearMarkers() {
        markers.clear();
        mapImage();
    }

    /** Enable tile selection */
    public void enableSelection() {
        addMouseListener(mouseListener);

        if (!singleRow) {
            addMouseListener(markerRangeListener);
            addMouseMotionListener(markerRangeListener);
        }
    }

    /** Disable tile selection */
    public void disableSelection() {
        deselectTile();
        removeMouseListener(mouseListener);

        if (!singleRow) {
            removeMouseListener(markerRangeListener);
            removeMouseMotionListener(markerRangeListener);
        }
    }

  /*
   * Constructors
   */

    /**
     * Create a new tile manager
     * @param title A title for this area
     * @param maxTiles The maximum number of tiles in this area
     */
    public TileManager(String title, int maxTiles) {
        this(title, maxTiles, false, true);
    }

    /**
     * Create a new tile manager
     * @param title A title for this area
     * @param maxTiles The maximum number of tiles in this area
     * @param singleRow True if the area is a single row
     */
    public TileManager(String title, int maxTiles, boolean singleRow) {
        this(title, maxTiles, singleRow, true);
    }

    /**
     * Create a new tile manager
     * @param title A title for this area
     * @param maxTiles The maximum number of tiles in this area
     * @param singleRow True if the area is a single row
     * @param enableSelect True if tiles can be selected
     */
    public TileManager(String title, int maxTiles, boolean singleRow,
                       boolean enableSelect) {
        this(title, maxTiles, null, singleRow, enableSelect);
    }

    /**
     * Create a new tile manager
     * @param title A title for this area
     * @param maxTiles The maximum number of tiles in this area
     * @param colorConfig A colour configuration for the tiles
     * @param singleRow True if the area is a single row
     * @param enableSelect True if tiles can be selected
     */
    public TileManager(String title, int maxTiles, TileManagerColors colorConfig,
                       boolean singleRow, boolean enableSelect) {

        this.title = title;
        this.maxTiles = maxTiles;
        this.singleRow = singleRow;

        if (colorConfig == null)
            this.colorConfig = new TileManagerColors();
        else
            this.colorConfig = colorConfig;

        mouseListener = new MouseL();
        markerRangeListener = new MarkerRangeListener(this, markers);
        if (enableSelect)
            enableSelection();

        selected = false;
        shiftSelected = false;
        selectedTile = 0;
        selectedTileTmp = 0;

        low = 0;
        maxTilesCalc = maxTiles;

        footer = false;
        setTileSizeDefault();

        if (title != null)
            topBorder = fontSize + borderGap;

    /* make sure that clear() will create the image */
        width = 0;
        height = 0;
    }

}
