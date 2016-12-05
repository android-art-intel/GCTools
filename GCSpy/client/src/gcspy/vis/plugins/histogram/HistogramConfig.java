/**
 * $Id: HistogramConfig.java 34 2005-09-22 16:17:34Z rej $
 * Copyright Hanspeter Johner, University of Kent, 2005
 * <p>
 * See the file "Kent_license.terms" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.vis.plugins.histogram;

import gcspy.vis.TileManagerColors;
import gcspy.vis.utils.AdjustedColor;

import java.awt.*;
import java.util.HashMap;

/**
 * Wrapped up settings for the histogram graph
 *
 * @author Hanspeter Johner
 */
class HistogramConfig {

    // constants
    private final int CANVAS_MIN_WIDTH = 330;
    private final int CANVAS_HEIGHT = 280;

    private final int TOP_FREE_BORDER = 20;
    private final int RIGHT_FREE_BORDER = 20;
    private final int BOTTOM_FREE_BORDER = 30;
    private final int LEFT_FREE_BORDER = 50;
    private final int RIGHT_FREE_CHART_SPACE = 30;

    private final int X_CHART_ZERO = LEFT_FREE_BORDER + 10;
    private final int Y_CHART_ZERO = CANVAS_HEIGHT - BOTTOM_FREE_BORDER - 10;

    private final int TILE_WIDTH = 3;
    private final int COLUMN_MAX_HEIGHT = 160;
    private final int COLUMN_DRAWABLE_PERCENT = 120;
    private final int OVERFLOW_TOP_RECT_HEIGHT = 3;
    private final int OVERFLOW_TOP_GAP = 3;

    private final int COLOR_PERCENT = 50;
    private final Color CHART_AXIS_COLOR = Color.BLACK;
    private final Color CHART_LINE_COLOR = Color.LIGHT_GRAY;
    private final Color BG_COLOR = Color.WHITE;
    private final Color ZERO_COLOR = BG_COLOR;
    private final Color CHART_FONT_COLOR = Color.BLUE;
    private final Color MARK_COLOR = Color.CYAN;

    private final Font CHART_FONT = new Font("Arial", Font.PLAIN, 10);


    // instance variables
    private int stream;
    private int canvasWidth;

    private Color lowColor = Color.BLACK;
    private Color streamBaseColor = Color.WHITE;
    private Color columnColor = AdjustedColor.generate(streamBaseColor, lowColor, COLOR_PERCENT, 100);

    private HashMap<Integer, Color> tileColors;


    /**
     * Get/calculate the width of the canvas
     * @param tileNum Current number of tiles
     * @return the width
     */
    int calcWidth(int tileNum) {
        int newWidth = LEFT_FREE_BORDER + (tileNum * TILE_WIDTH) + RIGHT_FREE_CHART_SPACE + RIGHT_FREE_BORDER;
        if (newWidth < CANVAS_MIN_WIDTH) {
            canvasWidth = CANVAS_MIN_WIDTH;
        } else {
            canvasWidth = newWidth;
        }
        return canvasWidth;
    }

    /**
     * Get the width of the canvas
     * @return the width
     */
    int getCanvasWidth() {
        return canvasWidth;
    }

    /**
     * Get the height of the canvas
     * @return the height
     */
    int getCanvasHeight() {
        return CANVAS_HEIGHT;
    }

    /**
     * Get the x-coordinate of the chart's origin
     * @return the coordinate
     */
    int getXZero() {
        return X_CHART_ZERO;
    }

    /**
     * Get the yx-coordinate of the chart's origin
     * @return the coordinate
     */
    int getYZero() {
        return Y_CHART_ZERO;
    }

    /**
     * Get the size of the chart's top border in pixels
     * @return the size
     */
    int getTopFreeBorder() {
        return TOP_FREE_BORDER;
    }

    /**
     * Get the size of the chart's right border in pixels
     * @return the size
     */
    int getRightFreeBorder() {
        return RIGHT_FREE_BORDER;
    }

    /**
     * Get the size of the chart's bottom border in pixels
     * @return the size
     */
    int getBottomFreeBorder() {
        return BOTTOM_FREE_BORDER;
    }

    /**
     * Get the size of the chart's left border in pixels
     * @return the size
     */
    int getLeftFreeBorder() {
        return LEFT_FREE_BORDER;
    }

    /**
     * Get the width of a tile (column) in pixels
     * @return the width
     */
    int getTileWidth() {
        return TILE_WIDTH;
    }

    /**
     * Get the maximum height of a column in pixels
     * @return the height
     */
    int getColumnMaxHeight() {
        return COLUMN_MAX_HEIGHT;
    }

    /**
     * Get the maximum fraction of the column height that we are prepared to draw
     * @return the fraction as a percentage
     */
    int getColumnDrawablePercent() {
        return COLUMN_DRAWABLE_PERCENT;
    }

    /**
     * Get the overflow height
     * @return the height
     */
    int getOverflowTopRectHeight() {
        return OVERFLOW_TOP_RECT_HEIGHT;
    }

    /**
     * Get the overflow gap
     * @return the gap
     */
    int getOverflowTopGap() {
        return OVERFLOW_TOP_GAP;
    }

    /**
     * Calculate the tile corresponding to a position on the chart
     * @param imageX The x-coordinate
     * @param imageY The y-coordinate
     * @return The tile index
     */
    int calcTileForPoint(int imageX, int imageY) {
        // X correction (6 pixels seem to be used for the left border!)
        imageX = imageX - 6;
        // Y corresction (20 pixels seem to be used for the top border!)
        imageY = imageY - 20;
        // Y valid?
        if (imageY > Y_CHART_ZERO) {
            return -1;
        }
        // X valid?
        if (imageX < X_CHART_ZERO || imageX > (canvasWidth - RIGHT_FREE_BORDER)) {
            return -1;
        }
        // Calculate tile index
        return (imageX - X_CHART_ZERO) / TILE_WIDTH;

    }

    /**
     * Get the colour of the axis
     * @return the colour
     */
    Color getChartAxisColor() {
        return CHART_AXIS_COLOR;
    }

    /**
     * Get the colour of the line
     * @return the colour
     */
    Color getChartLineColor() {
        return CHART_LINE_COLOR;
    }

    /**
     * Get the background colour
     * @return the colour
     */
    Color getBGColor() {
        return BG_COLOR;
    }

    /**
     * Get the zero colour
     * @return the colour
     */
    Color getZeroColor() {
        return ZERO_COLOR;
    }

    /**
     * Get the colour of a tile
     * @param index the tile's index
     * @return the colour
     */
    Color getColorForTile(int index) {
        Color value = tileColors.get(index);
        if (value == null) {
            return columnColor;
        } else {
            return value;
        }
    }

    /**
     * Get the font colour
     * @return the colour
     */
    Color getChartFontColor() {
        return CHART_FONT_COLOR;
    }

    /**
     * Get the zero colour
     * @return the colour
     */
    Color getMarkColor() {
        return MARK_COLOR;
    }

    /**
     * Get the chart's font
     * @return the font
     */
    Font getChartFont() {
        return CHART_FONT;
    }

    /**
     * Set the colour of a tile
     * @param index the tile's index
     * @param color the tile's colour
     */
    void setColorForTile(int index, Color color) {
        tileColors.put(index, color);
    }

    /**
     * Set selected stream
     * @param stream Stream's index
     */
    public void setStream(int stream) {
        this.stream = stream;
    }

    /**
     * Get selected stream
     * @return Stream's index
     */
    public int getStream() {
        return stream;
    }

    /**
     * Set the stream color for the histogram image
     * @param color The Stream's base color
     */
    public void setStreamColor(Color color) {
        this.streamBaseColor = color;
        this.columnColor = AdjustedColor.generate(streamBaseColor, lowColor, COLOR_PERCENT, 100);
    }

    /**
     * A new histogram configuration
     * @param streamID The stream to graph
     * @param tmColors the tile color configuration
     */
    HistogramConfig(int streamID, TileManagerColors tmColors) {
        // tmColors used for what?
        this.stream = streamID;
        this.canvasWidth = CANVAS_MIN_WIDTH;
        tileColors = new HashMap<Integer, Color>();
    }

}
