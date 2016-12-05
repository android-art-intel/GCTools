/**
 * $Id: HistogramCanvas.java 34 2005-09-22 16:17:34Z rej $
 * Copyright Hanspeter Johner, University of Kent, 2005
 * <p>
 * See the file "Kent_license.terms" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.vis.plugins.histogram;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Canvas for the histogram graph
 *
 * @author Hanspeter Johner
 */
class HistogramCanvas extends JComponent {

    // instance variables
    private Graphics imageG;
    private int width;
    private int height;
    private int markedColumnIndex = -1;
    private String maxYString, middleYString, zeroYString;
    private HistogramConfig config;
    private BufferedImage image;

    /**
     * Initialise the chart: Draw x and y lines
     */
    private void initChart() {
        // draw x line
        int x1 = config.getLeftFreeBorder();
        int y1 = config.getYZero();
        int x2 = config.getCanvasWidth() - config.getRightFreeBorder();
        int y2 = y1;
        imageG.setColor(config.getChartAxisColor());
        imageG.drawLine(x1, y1, x2, y2);
        // draw y line
        x1 = config.getXZero();
        y1 = config.getTopFreeBorder();
        x2 = x1;
        y2 = config.getCanvasHeight() - config.getBottomFreeBorder();
        imageG.drawLine(x1, y1, x2, y2);

        // draw horizontal 50 % line
        imageG.setColor(config.getChartLineColor());
        x1 = config.getLeftFreeBorder();
        y1 = config.getYZero() - config.getColumnMaxHeight() / 2;
        x2 = config.getCanvasWidth() - config.getRightFreeBorder();
        y2 = y1;
        imageG.drawLine(x1, y1, x2, y2);
        // draw horizontal 100% line
        y1 = config.getYZero() - config.getColumnMaxHeight();
        y2 = y1;
        imageG.drawLine(x1, y1, x2, y2);

        // draw x chart Strings
        imageG.setFont(config.getChartFont());
        FontMetrics fm = imageG.getFontMetrics();
        imageG.setColor(config.getChartFontColor());
        int x = config.getXZero();
        int y = config.getYZero() + 20;
        imageG.drawString("0", x, y);
        x = config.getCanvasWidth() / 2;
        imageG.drawString("Tiles", x, y);
        // draw y chart Strings
        x = config.getLeftFreeBorder() - fm.stringWidth(zeroYString) - 3;
        y = config.getYZero();
        imageG.drawString(zeroYString, x, y);
        x = config.getLeftFreeBorder() - fm.stringWidth(middleYString) - 3;
        y = config.getYZero() - config.getColumnMaxHeight() / 2;
        imageG.drawString(middleYString, x, y);
        x = config.getLeftFreeBorder() - fm.stringWidth(maxYString) - 3;
        y = config.getYZero() - config.getColumnMaxHeight();
        imageG.drawString(maxYString, x, y);

    }

    private void mapImage(Graphics g) {
        if (g != null)
            g.drawImage(image, 0, 0, null);
    }

    public synchronized void paintComponent(Graphics g) {
        mapImage(g);
    }

    /**
     * Set the string to mark the y axis with
     * @param max The string for the topmost, maximum line.
     * @param middle The string for the middle line
     * @param zero The string for zero, x axis height.
     *
     */
    public void setYStrings(String max, String middle, String zero) {
        this.maxYString = max;
        this.middleYString = middle;
        this.zeroYString = zero;
    }

    /**
     * Wipe image. The image is overpainted with the background color.
     */
    public void wipeImage() {
        if (imageG != null) {
            imageG.setColor(config.getBGColor());
            imageG.fillRect(0, 0, width, height);
        }
    }

    /**
     * Finish image. Draws the chart.
     */
    public void finishImage() {
        initChart();
    }

    /**
     * Flush the image
     */
    public void flush() {
        mapImage(getGraphics());
    }

    private void createImage() {
        image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        imageG = image.getGraphics();
        wipeImage();
    }

    /**
     * Get the width of the canvas
     * @return the canvas' width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the height of the canvas
     * @return the canvas' height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Resize the canvas
     * @param tileNum The current number of tiles
     */
    void resize(int tileNum) {
        // calculate width needed
        int newWidth = config.calcWidth(tileNum);
        if (newWidth > width) {
            // resize canvas
            width = newWidth;
            createImage();
        }
        setPreferredSize(new Dimension(width, height));
    }

    /**
     * Paint a column
     * @param index The columns's index
     * @param c The colour to use
     */
    private void paintColumn(int index, int height, Color c) {
        int x1 = config.getXZero() + 1 + index * config.getTileWidth();
        int y1 = config.getYZero() - height;
        imageG.setColor(c);
        imageG.fillRect(x1, y1, config.getTileWidth(), height);
    }

    /**
     * Paint an overflow hat for columns where the value exceeds the
     * maximum set by the tile style.
     *
     * @param index The columns's index
     * @param columnTop Start height of the overflow hat
     * @param c The colour to use
     * @return The total height of the hat
     */
    private int paintOverflowHat(int index, int columnTop, Color c) {
        int x = config.getXZero() + 1 + index * config.getTileWidth();
        // first rect
        int y = columnTop - config.getOverflowTopGap() - config.getOverflowTopRectHeight();
        imageG.setColor(c);
        imageG.fillRect(x, y, config.getTileWidth(), config.getOverflowTopRectHeight());
        // second rect
        y -= (config.getOverflowTopRectHeight() + config.getOverflowTopGap());
        imageG.fillRect(x, y, config.getTileWidth(), config.getOverflowTopRectHeight());
        // third rect
        y -= (config.getOverflowTopRectHeight() + config.getOverflowTopGap());
        imageG.fillRect(x, y, config.getTileWidth(), config.getOverflowTopRectHeight());

        // return total height of overflow hat
        return columnTop - y;
    }

    /**
     * Paint a column. Up to 120 percent of the max value are drawn,
     * more than 120 percent are displayed as an overflow hat (bar with gaps).
     *
     * @param index The tiles's index
     * @param percent The value of the tile in percent of the stream's maximum value.
     */
    public void paintColumn(int index, int percent) {
        int columnHeight = 0;
        if (percent < 0) {
            return;
        } else if (percent <= config.getColumnDrawablePercent()) {
            columnHeight = (percent * config.getColumnMaxHeight()) / 100;
            paintColumn(index, columnHeight, config.getColorForTile(index));
        } else {
            // overflow
            columnHeight = (config.getColumnDrawablePercent() * config.getColumnMaxHeight()) / 100;
            paintColumn(index, columnHeight, config.getColorForTile(index));
            int topHeight = paintOverflowHat(index, config.getYZero() - columnHeight, config.getColorForTile(index));
            columnHeight += (topHeight + config.getOverflowTopGap());
        }

        // is column marked?
        if (index == markedColumnIndex) {
            int x = config.getXZero() + 1 + index * config.getTileWidth();
            int y = config.getYZero() - columnHeight;
            imageG.setColor(config.getMarkColor());
            imageG.drawRect(x, y, config.getTileWidth() - 1, columnHeight - 1);
        }
    }

    /**
     * Mark or unmark a column.
     * @param index The tiles's index
     */
    public void markColumn(int index) {
        if (index == markedColumnIndex) {
            // unmark
            markedColumnIndex = -1;
        } else {
            // mark
            markedColumnIndex = index;
        }
    }

    /**
     * Create a new histogram canvas
     *
     * @param config The histogram configuration to use
     */
    HistogramCanvas(HistogramConfig config) {
        this.config = config;
        // set initial size
        width = config.getCanvasWidth();
        height = config.getCanvasHeight();
        setPreferredSize(new Dimension(width, height));
        // create image
        createImage();
    }

}
