/**
 * * $Id: HistoryCanvas.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.vis.plugins.history;

import gcspy.utils.FileUtils;
import gcspy.vis.utils.AdjustedColor;
import gcspy.vis.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Canvas for the history graphs
 *
 * @author Tony Printezis
 */
class HistoryCanvas extends JComponent {

    private BufferedImage image;
    private Graphics imageG;
    private int width;
    private int height;
    private boolean stopped;
    private boolean verSep;

    private HistoryConfig config;
    private int y, yMax;
    private int lastHorSepY;

    private AdjustedColor ac;

    private void mapImage(Graphics g) {
        if (g != null)
            g.drawImage(image, 0, 0, null);
    }

    private void paintBorder(Graphics g, int width, int height) {
        int borderSize = config.getBorderSize();
        g.setColor(config.getBorderColor());
        for (int i = 0; i < borderSize; ++i) {
            g.drawRect(i, i, width - 2 * i - 1, height - 2 * i - 1);
        }
    }

    private void createImage(String title) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        imageG = image.getGraphics();
        imageG.setColor(config.getBGColor());
        imageG.fillRect(0, 0, width, height);

        // paint border
        paintBorder(imageG, width, height);

        int borderSize = config.getBorderSize();
        imageG.setClip(borderSize, borderSize, width - 2 * borderSize, height - 2
                * borderSize);

        // paint vertical separators and possibly legend
        if (config.verSep())
            paintVerSep(imageG);

        // paint title
        if (config.getTitle()) {
            imageG.setColor(config.getTitleColor());
            imageG.setFont(config.getFont());
            imageG.drawString(title, config.getTextGap() + config.getBorderSize(),
                    config.getFontSize() + config.getBorderSize());
        }
    }

    public void paintComponent(Graphics g) {
        mapImage(g);
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
     * Get the position of the current line
     * @return the current y position
     */
    int getCurrentPos() {
        return y;
    }

    /**
     * Resize the canvas
     * @param title The title to use
     */
    void resize(String title) {
        BufferedImage oldImage = image;
        int borderSize = config.getBorderSize();
        width = config.calcWidth();
        createImage(title);
        imageG.drawImage(oldImage, borderSize, borderSize, oldImage.getWidth()
                - borderSize, y, borderSize, borderSize, oldImage.getWidth()
                - borderSize, y, null);
        setPreferredSize(new Dimension(width, height));
    }

    /**
     * Start a history graph
     * @param ac The adjusted color to use
     */
    public void start(AdjustedColor ac) {
        this.ac = ac;
    }

    /**
     * Has the graph reached its maximum extent?
     * @return true if stopped
     */
    public boolean hasStopped() {
        return stopped;
    }

    private void gotoNextLine(int step) {
        y += step;
        if ((y + config.getTileSize()) > yMax) {
            stopped = true;
        }
    }

    /**
     * Paint a horizontal separator
     * @param c The separator colout
     * @param count The separator's count (e.g. Nth event)
     */
    public void paintHorSep(Color c, int count) {
        imageG.setColor(c);

        if ((y - lastHorSepY) > config.getFontSize()) {
            String str = "" + count;
            int sWidth = config.getTextWidth(str) + config.getTextGap()
                    + config.getHorBorderSize();
            imageG.setFont(config.getFont());
            imageG.drawString(str, width - sWidth, y - config.getTextGap());
        }
        lastHorSepY = y;

        imageG.fillRect(0, y, width, config.getTileSize());
        gotoNextLine(config.getTileSize());
    }

    private void paintVerSep(Graphics g) {
        boolean legend = config.getVerSepLegend();
        Font font = config.getFont();
        int legendSep = 0;
        int legendPeriod = 0;
        int verSepStart = config.getVerSepStart();

        int period = config.getVerSepPeriod();
        int sep = config.getTileSize() * period;
        if (legend) {
            legendSep = config.getVerSepLegendPeriod();
            legendPeriod = legendSep;
            g.setFont(font);
        }

        g.setColor(config.getVerSepColor());
        int i;
        for (i = sep; i < width; i += sep) {
            g.drawLine(i, verSepStart, i, height - 1);

            if (legend) {
                String str = legendPeriod + config.getVerSepLegendStringPost();
                int sWidth = config.getTextWidth(str) + config.getTextGap();
                g.drawString(str, i - sWidth, config.getTopBorderSize()
                        - config.getTextGap());
                legendPeriod += legendSep;
            }
        }
        if (legend) {
            String str = legendPeriod + config.getVerSepLegendStringPost();
            int sWidth = config.getTextWidth(str) + config.getTextGap();
            g.drawString(str, i - sWidth, config.getTopBorderSize()
                    - config.getTextGap());
        }
    }

    /**
     * Paint a tile
     * @param index The tile's index
     * @param c The colour to use
     */
    public void paintTile(int index, Color c) {
        int tileSize = config.getTileSize();
        int x = config.getHorBorderSize() + index * tileSize;
        imageG.setColor(c);
        imageG.fillRect(x, y, config.getTileSize(), tileSize);
    }

    /**
     * Paint a tile
     * @param index The tile's index
     * @param val The tile's value
     */
    public void paintTile(int index, int val) {
        paintTile(index, ac.generate(val));
    }

    /**
     * Paint a separator
     * @param index The index of the tile wher the separator is to be painted
     */
    public void paintSep(int index) {
        int tileSize = config.getTileSize();
        int x = config.getHorBorderSize() + index * tileSize;
        imageG.setColor(config.getSpaceSepColor());
        imageG.drawLine(x, y, x, y + tileSize - 1);
    }

    /** Flush */
    public void flush() {
        gotoNextLine(config.getTileSize());
        if (config.verSep())
            paintVerSep(imageG);
        mapImage(getGraphics());
    }

    /**
     * Save the graph to a file
     * @param file The file
     */
    public void save(File file) {
        int border = config.getBottomBorderSize();

        BufferedImage cropped = ImageUtils
                .cropImage(image, 0, 0, width, y + border);
        paintBorder(cropped.getGraphics(), width, y + border);

        FileUtils.exportToTIFF(cropped, file, true);
    }

    /**
     * Create a new history canvas
     * @param config The history configuration to use
     * @param title The title to use
     */
    HistoryCanvas(HistoryConfig config, String title) {
        this.config = config;

        width = config.calcWidth();
        height = config.calcHeight();

        y = config.getTopBorderSize();
        yMax = height - config.getBottomBorderSize();
        lastHorSepY = config.getTopBorderSize();
        stopped = false;

        setPreferredSize(new Dimension(width, height));
        createImage(title);
    }

}
