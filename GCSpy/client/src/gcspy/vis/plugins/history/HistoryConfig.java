/**
 * * $Id: HistoryConfig.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.vis.plugins.history;

import gcspy.utils.ColorDB;
import gcspy.vis.TileManagerColors;

import java.awt.*;

/**
 * Wrapped up settings for the history graphs
 *
 * @author Tony Printezis
 */
class HistoryConfig {

    private int cols;
    private int rows;
    private int tileSize;
    private int stream;

    private boolean horSep;
    private int horSepEvent;

    private boolean verSep;
    private int verSepPeriod;

    private boolean verSepLegend;
    private int verSepLegendPeriod;
    private String verSepLegendStringPost;

    private boolean spaceSep;
    private Color spaceSepColor;

    private boolean title;
    private Color titleColor;
    private int titleHeight;
    private String titlePre;

    private Color bgColor;
    private Color unusedColor;
    private Color zeroColor;
    private Color lowColor;
    private Color hiColor;
    private Color horSepColor;
    private Color verSepColor;
    private Color borderColor;

    private int fontSize;
    private Font font;
    private FontMetrics fontMetrics;
    private int textGap;

    private int borderSize;
    private int extraVerBorderSize;

    static private final int COLS_MIN = 1;
    static private final int COLS_MAX = 10000;

    static private final int ROWS_MIN = 10;
    static private final int ROWS_DEFAULT = 500;
    static private final int ROWS_MAX = 10000;

    static private final int TILE_SIZE_MIN = 1;
    static private final int TILE_SIZE_DEFAULT = 2;
    static private final int TILE_SIZE_MAX = 10;

    static private final int STREAM_DEFAULT = 0;

    static private final boolean VER_SEP_DEFAULT = false;

    static private final int VER_SEP_PERIOD_MIN = 2;
    static private final int VER_SEP_PERIOD_DEFAULT = 80;
    static private final int VER_SEP_PERIOD_MAX = 1000;

    static private final boolean VER_SEP_LEGEND_DEFAULT = false;

    static private final int VER_SEP_LEGEND_PERIOD_MIN = 1;
    static private final int VER_SEP_LEGEND_PERIOD_DEFAULT = 1;
    static private final int VER_SEP_LEGEND_PERIOD_MAX = 999999;

    static private final String VER_SEP_LEGEND_STRING_POST_DEFAULT = "MB";

    static private final boolean HOR_SEP_DEFAULT = false;
    static private final int HOR_SEP_EVENT_DEFAULT = 0;

    static private final boolean SPACE_SEP_DEFAULT = false;

    static private final ColorDB colorDB = ColorDB.getColorDB();
    static private final Color BG_COLOR_DEFAULT = colorDB.getColor("Mid Gray");
    static private final Color ZERO_COLOR_DEFAULT = colorDB.getColor("Gray");
    static private final Color UNUSED_COLOR_DEFAULT = colorDB
            .getColor("Light Gray");
    static private final Color LOW_COLOR_DEFAULT = colorDB.getColor("Black");
    // don't really need this as we pick it up from TileManagerColors
    // static private final Color HI_COLOR_DEFAULT = colorDB.getColor("Red");
    static private final Color HOR_SEP_COLOR_DEFAULT = colorDB
            .getColor("Off White");
    static private final Color VER_SEP_COLOR_DEFAULT = colorDB
            .getColor("Off White");
    // we'll pick this up from TileManagerColors too
    // static private final Color SPACE_SEP_COLOR_DEFAULT =
    // colorDB.getColor("Black");

    static private final boolean TITLE_DEFAULT = true;
    static private final Color TITLE_COLOR_DEFAULT = colorDB
            .getColor("Off White");
    static private final String TITLE_PRE_DEFAULT = "";

    static private final int BORDER_SIZE_MIN = 0;
    static private final int BORDER_SIZE_DEFAULT = 2;
    static private final int BORDER_SIZE_MAX = 10;

    static private final Color BORDER_COLOR_DEFAULT = colorDB.getColor("Black");

    static private final int FONT_SIZE_MIN = 8;
    static private final int FONT_SIZE_DEFAULT = 12;
    static private final int FONT_SIZE_MAX = 50;

    /**
     * Get the width of the configuration frame
     * @return the width
     */
    int calcWidth() {
        return 2 * borderSize + cols * tileSize;
    }

    /**
     * Get the height of the configuration frame
     * @return the height
     */
    int calcHeight() {
        return 2 * borderSize + extraVerBorderSize + rows * tileSize;
    }

    static private int checkMinMax(int val, int min, int max) {
        if (val < min)
            return min;
        if (val > max)
            return max;
        return val;
    }

    static private int parseInt(String text, int def) {
        int val;
        try {
            val = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            val = def;
        }
        return val;
    }

  /* Cols */

    /**
     * Check the number of columns
     * @param cols the number of columns given
     * @return the appropriate number of columns
     */
    static int checkCols(int cols) {
        return cols;
    }

    /**
     * Get the number of columns
     * @return th enumber of colu,mns
     */
    int getCols() {
        return cols;
    }

    /**
     * Set the number of columns
     * @param cols The number of columns
     */
    void setCols(int cols) {
        this.cols = checkCols(cols);
    }

  /* Rows */

    /**
     * Check the number of rows
     * @param rows the given number of rows
     * @return an appropriate number of rows
     */
    static int checkRows(int rows) {
        return checkMinMax(rows, ROWS_MIN, ROWS_MAX);
    }

    /**
     * Parse a string to give the number of rows
     * @param text The input string
     * @return the number of rows
     */
    static int parseRows(String text) {
        return checkRows(parseInt(text, ROWS_DEFAULT));
    }

    /**
     * Get the number of rows
     * @return the number of rows
     */
    int getRows() {
        return rows;
    }

  /* Tile Size */

    /**
     * Get the tile size
     * @return the size of the tiles
     */
    int getTileSize() {
        return tileSize;
    }

    /**
     * Check the tile size
     * @param tileSize the given tile size
     * @return an appropriate tile size
     */
    static int checkTileSize(int tileSize) {
        return checkMinMax(tileSize, TILE_SIZE_MIN, TILE_SIZE_MAX);
    }

    /**
     * Parse a string to give a tile size
     * @param text the string to parse
     * @return the tile size
     */
    static int parseTileSize(String text) {
        return checkTileSize(parseInt(text, TILE_SIZE_DEFAULT));
    }

  /* Stream */

    /**
     * Get the ID od the stream being graphed
     * @return the stream ID
     */
    int getStream() {
        return stream;
    }

  /* Hor Sep */

    /**
     * Is a horizontal separator needed
     * @return true if a horizontal separator is needed
     */
    boolean horSep() {
        return horSep;
    }

    /**
     * Get the ID of the event marked by horizontal separators
     * @return the event ID
     */
    int getHorSepEvent() {
        return horSepEvent;
    }

  /* Ver Sep */

    /**
     * Is a vertical separator wanted?
     * @return true if a vertical separator is wanted
     */
    boolean verSep() {
        return verSep;
    }

  /* Ver Sep Period */

    /**
     * Get the period for vertical separators
     * @return the period
     */
    int getVerSepPeriod() {
        return verSepPeriod;
    }

    /**
     * Get the vertical separation before the start
     * @return the separation
     */
    int getVerSepStart() {
        return (title) ? (titleHeight + borderSize) : 0;
    }

    /**
     * Check the period for vertical separators
     * @param verSepPeriod The period given
     * @return an appropriate period
     */
    static int checkVerSepPeriod(int verSepPeriod) {
        return checkMinMax(verSepPeriod, VER_SEP_PERIOD_MIN, VER_SEP_PERIOD_MAX);
    }

    /**
     * Parse a string to give a vertical separator period
     * @param text The string to parse
     * @return the period
     */
    static int parseVerSepPeriod(String text) {
        return checkVerSepPeriod(parseInt(text, VER_SEP_PERIOD_DEFAULT));
    }

  /* Ver Sep Legend */

    /**
     * Is there a legend for vertical separators
     * @return true if there is a legend
     */
    boolean getVerSepLegend() {
        return verSepLegend;
    }

  /* Ver Sep Legend Period */

    /**
     * Get the period for vertical separators for the legend
     * @return the period
     */
    int getVerSepLegendPeriod() {
        return verSepLegendPeriod;
    }

    /**
     * Check the period for vertical separators for the legend
     * @param verSepLegendPeriod The period give
     * @return An appropriate legend
     */
    static int checkVerSepLegendPeriod(int verSepLegendPeriod) {
        return checkMinMax(verSepLegendPeriod, VER_SEP_LEGEND_PERIOD_MIN,
                VER_SEP_LEGEND_PERIOD_MAX);
    }

    /**
     * Parse a string to get the vertical separation for the legend
     * @param text the text to parse
     * @return the period
     */
    static int parseVerSepLegendPeriod(String text) {
        return checkVerSepLegendPeriod(parseInt(text, VER_SEP_LEGEND_PERIOD_DEFAULT));
    }

    /**
     * Return the suffix for the veritcal separation legend
     * @return the suffix
     */
    String getVerSepLegendStringPost() {
        return verSepLegendStringPost;
    }

  /* Space Sep */

    /**
     * Are there space separators
     * @return true if space separators are wanted
     */
    boolean getSpaceSep() {
        return spaceSep;
    }

    /**
     * Get the colour for space separators
     * @return the colour
     */
    Color getSpaceSepColor() {
        return spaceSepColor;
    }

  /* Colors */

    /**
     * Get the colour for background
     * @return the colour
     */
    Color getBGColor() {
        return bgColor;
    }

    /**
     * Get the colour wanted for unused tiles
     * @return the colour
     */
    Color getUnusedColor() {
        return unusedColor;
    }

    /**
     * Get the colour for zero tiles
     * @return the colour
     */
    Color getZeroColor() {
        return zeroColor;
    }


    /**
     * Get the colour for low valued tiles
     * @return the colour
     */
    Color getLowColor() {
        return lowColor;
    }


    /**
     * Get the colour for high valued tiles
     * @return the colour
     */
    Color getHiColor() {
        return hiColor;
    }


    /**
     * Get the colour for horizontal separators
     * @return the colour
     */
    Color getHorSepColor() {
        return horSepColor;
    }

    /**
     * Get the colour for vertical separators
     * @return the colour
     */
    Color getVerSepColor() {
        return verSepColor;
    }

    /**
     * Get the colour for the title
     * @return the colour
     */
    Color getTitleColor() {
        return titleColor;
    }

    /**
     * Get a prefix for the title
     * @return the prefix
     */
    String getTitlePre() {
        return titlePre;
    }

  /* Title */

    /**
     * Is a title wanted
     * @return true if a title is wanted
     */
    boolean getTitle() {
        return title;
    }

  /* Font Size */

    /**
     * Check the font size
     * @param fontSize a given size for fonts
     * @return an appropriate font size
     */
    static int checkFontSize(int fontSize) {
        return checkMinMax(fontSize, FONT_SIZE_MIN, FONT_SIZE_MAX);
    }

    /**
     * Parse a string to get a font size
     * @param text the string to parse
     * @return an appropriate font size
     */
    static int parseFontSize(String text) {
        return checkFontSize(parseInt(text, FONT_SIZE_DEFAULT));
    }

    /**
     * Get the font size
     * @return the font size
     */
    int getFontSize() {
        return fontSize;
    }

    /**
     * Check the border size
     * @param borderSize A size given for the border
     * @return an appropriate size
     */
    static int checkBorderSize(int borderSize) {
        return checkMinMax(borderSize, BORDER_SIZE_MIN, BORDER_SIZE_MAX);
    }

    /**
     * Parse a string to get sizse for the border
     * @param text the string to parse
     * @return an appropriate size
     */
    static int parseBorderSize(String text) {
        return checkBorderSize(parseInt(text, BORDER_SIZE_DEFAULT));
    }

    /**
     * Get the border size
     * @return the size
     */
    int getBorderSize() {
        return borderSize;
    }

    /**
     * Get the border colour
     * @return the colour
     */
    Color getBorderColor() {
        return borderColor;
    }

    /**
     * Get the horizontal border size
     * @return the size
     */
    int getHorBorderSize() {
        return borderSize;
    }

    /**
     * get the top border size
     * @return the size
     */
    int getTopBorderSize() {
        return borderSize + extraVerBorderSize;
    }

    /**
     * get the bottom border size
     * @return the size
     */
    int getBottomBorderSize() {
        return borderSize;
    }

    /**
     * get the text gap
     * @return the gap
     */
    int getTextGap() {
        return textGap;
    }

    private Font generateFont() {
        Font font;
        if (fontSize < 10)
            font = new Font("SansSerif", Font.PLAIN, fontSize);
        else
            font = new Font("SansSerif", Font.BOLD, fontSize);
        return font;
    }

    /**
     * Get the font to use
     * @return the font
     */
    Font getFont() {
        return font;
    }

    // FontMetrics getFontMetrics () {
    // return fontMetrics;
    // }

    /**
     * Measure the width of a string
     * @param text the string to measure
     * @return its width
     */
    int getTextWidth(String text) {
        return fontMetrics.stringWidth(text);
    }

    /**
     * Revert a component to its default colours
     * @param component The component
     * @param tmColors The colour configuration
     */
    void revertToDefaults(Component component, TileManagerColors tmColors) {
        init(component, cols, stream, tmColors);
    }

    private void init(Component component, int cols, int rows, int tileSize,
                      int stream, boolean horSep, int horSepEvent, boolean verSep,
                      int verSepPeriod, boolean verSepLegend, int verSepLegendPeriod,
                      String verSepLegendStringPost, boolean spaceSep, Color spaceSepColor,
                      Color unusedColor, Color zeroColor, Color lowColor, Color hiColor,
                      Color horSepColor, Color verSepColor, Color bgColor, boolean title,
                      String titlePre, Color titleColor, int borderSize, Color borderColor,
                      int fontSize) {
        this.cols = checkCols(cols);
        this.rows = checkRows(rows);
        this.tileSize = checkTileSize(tileSize);
        this.stream = stream;
        this.horSep = horSep;
        this.horSepEvent = horSepEvent;
        this.verSep = verSep;
        this.verSepPeriod = verSepPeriod;
        this.verSepLegend = verSepLegend;
        this.verSepLegendPeriod = verSepLegendPeriod;
        this.verSepLegendStringPost = verSepLegendStringPost;
        this.spaceSep = spaceSep;
        this.spaceSepColor = spaceSepColor;
        this.unusedColor = unusedColor;
        this.zeroColor = zeroColor;
        this.lowColor = lowColor;
        this.hiColor = hiColor;
        this.horSepColor = horSepColor;
        this.verSepColor = verSepColor;
        this.bgColor = bgColor;
        this.title = title;
        this.titlePre = titlePre;
        this.titleColor = titleColor;
        this.borderSize = borderSize;
        this.borderColor = borderColor;
        this.fontSize = fontSize;

        this.font = generateFont();
        this.fontMetrics = component.getFontMetrics(this.font);
        this.textGap = fontSize / 5;

        this.extraVerBorderSize = 0;
        this.titleHeight = 2 * fontSize;
        if (verSepLegend)
            this.extraVerBorderSize += fontSize;
        if (title)
            this.extraVerBorderSize += titleHeight;
    }

    private void init(Component component, int cols, int streamID,
                      TileManagerColors tmColors) {
        init(component, cols, ROWS_DEFAULT, TILE_SIZE_DEFAULT, streamID,
                HOR_SEP_DEFAULT, HOR_SEP_EVENT_DEFAULT, VER_SEP_DEFAULT,
                VER_SEP_PERIOD_DEFAULT, VER_SEP_LEGEND_DEFAULT,
                VER_SEP_LEGEND_PERIOD_DEFAULT, VER_SEP_LEGEND_STRING_POST_DEFAULT,
                SPACE_SEP_DEFAULT, tmColors.getSeparatorColor(), UNUSED_COLOR_DEFAULT,
                ZERO_COLOR_DEFAULT, LOW_COLOR_DEFAULT, tmColors
                        .getStreamColor(streamID), HOR_SEP_COLOR_DEFAULT,
                VER_SEP_COLOR_DEFAULT, BG_COLOR_DEFAULT, TITLE_DEFAULT,
                TITLE_PRE_DEFAULT, TITLE_COLOR_DEFAULT, BORDER_SIZE_DEFAULT,
                BORDER_COLOR_DEFAULT, FONT_SIZE_DEFAULT);
    }

    /**
     * A new history configuration
     * @param component The configuration dialog
     * @param cols The number of columns to use
     * @param rows The number of rows to use
     * @param tileSize The tilesize to use
     * @param stream The stream to graph
     * @param horSep Is a horizontal separator needed?
     * @param horSepEvent The ID of the event shown by horiozontal separators
     * @param verSep Are vertical separators wanted?
     * @param verSepPeriod The period for vertical separators
     * @param verSepLegend The legend for vertical separators
     * @param verSepLegendPeriod The period for vertical separators for the legend
     * @param verSepLegendStringPost The suffix for vertical separators in the legend
     * @param spaceSep Are space separators wanted?
     * @param spaceSepColor The space separator colour
     * @param unusedColor The colour for unused tiles
     * @param zeroColor The colour for zero-valued tiles
     * @param lowColor The colour for low-valued tiles
     * @param hiColor The colour for high-valued tiles
     * @param horSepColor The colour for horizontal separators
     * @param verSepColor The colour for vertical separators
     * @param bgColor The background colour
     * @param title The title
     * @param titlePre A prefix for the title
     * @param titleColor The colour of the title
     * @param borderSize The border size
     * @param borderColor The border colour
     * @param fontSize The font size
     */
    HistoryConfig(Component component, int cols, int rows, int tileSize,
                  int stream, boolean horSep, int horSepEvent, boolean verSep,
                  int verSepPeriod, boolean verSepLegend, int verSepLegendPeriod,
                  String verSepLegendStringPost, boolean spaceSep, Color spaceSepColor,
                  Color unusedColor, Color zeroColor, Color lowColor, Color hiColor,
                  Color horSepColor, Color verSepColor, Color bgColor, boolean title,
                  String titlePre, Color titleColor, int borderSize, Color borderColor,
                  int fontSize) {
        init(component, cols, rows, tileSize, stream, horSep, horSepEvent, verSep,
                verSepPeriod, verSepLegend, verSepLegendPeriod, verSepLegendStringPost,
                spaceSep, spaceSepColor, unusedColor, zeroColor, lowColor, hiColor,
                horSepColor, verSepColor, bgColor, title, titlePre, titleColor,
                borderSize, borderColor, fontSize);
    }

    /**
     * A new history configuration
     * @param component The configuration dialog
     * @param cols The number of columns to use
     * @param streamID The stream to graph
     * @param tmColors the tile colopur configuration
     */

    HistoryConfig(Component component, int cols, int streamID,
                  TileManagerColors tmColors) {
        init(component, cols, streamID, tmColors);
    }

}
