/**
 * $Id: HistogramFrame.java 34 2005-09-22 16:17:34Z rej $
 * Copyright Hanspeter Johner, University of Kent, 2005
 * <p>
 * See the file "Kent_license.terms" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.vis.plugins.histogram;

import gcspy.interpreter.DataAccessor;
import gcspy.interpreter.Space;
import gcspy.interpreter.Stream;
import gcspy.interpreter.client.ClientInterpreter;
import gcspy.interpreter.client.ClientSpace;
import gcspy.vis.TileManagerColors;
import gcspy.vis.plugins.PluginFrame;
import gcspy.vis.utils.AbstractFrame;
import gcspy.vis.utils.Factory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Frame that does all the histogram rendering
 * @author Hanspeter Johner
 */
public class HistogramFrame extends PluginFrame {

    // constants
    private final String NO_TILE_STRING = "no tiles";
    private final Font LEGEND_LIST_FONT = new Font("Arial", Font.PLAIN, 10);

    // instance variables
    private JScrollPane canvasScrollPane;
    private HistogramCanvas canvas;
    private JButton closeB;
    private JComboBox streamCB;
    private JList tileLegendList;
    private DefaultListModel tileLegendModel;
    private HistogramConfig config;
    private int currentTileNum;

    /** Private inner classes */

    // The listener for the close Button
    //
    private class CloseBListener implements ActionListener {
        private AbstractFrame owner;

        public void actionPerformed(ActionEvent event) {
            // setVisible(false);
            pluginFrames.remove(owner);
            shutdown();
        }

        CloseBListener(AbstractFrame owner) {
            this.owner = owner;
        }
    }

    // The listener for the stream selection ComboBox
    //
    private class StreamCBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            int streamID = streamCB.getSelectedIndex();
            config.setStream(streamID);
            config.setStreamColor(tmColors.getStreamColor(streamID));
            prepareCanvas(streamID);
            fillLegendList();
            redraw();
        }
    }

    // The items in the tile Legend
    //
    private class TileLegendItem extends JLabel {
        // Instance variables
        private int tileNumber;
        private String labelText;

        // Constructor
        TileLegendItem(String labelT, int tileNum) {
            super();
            labelText = labelT;
            tileNumber = tileNum;
        }

        public String toString() {
            return "Tile " + tileNumber;
        }

        public String getLabelText() {
            return labelText;
        }

        public int getTileNumber() {
            return tileNumber;
        }
    }

    // A renderer for the tile legend items
    //
    private class TileLegendCellRenderer extends JPanel implements ListCellRenderer {
        // Constants
        private final int colorLabelBorder = 2;
        private final Color BG_COLOR = Color.WHITE;
        private final Color SELECT_COLOR = Color.LIGHT_GRAY;
        // Instance variables
        private JLabel colorLabel;
        private JLabel textLabel;

        // Constructor
        TileLegendCellRenderer() {
            // Layout panel
            setPreferredSize(new Dimension(180, 20));
            setBackground(BG_COLOR);
            setLayout(new FlowLayout(FlowLayout.LEFT));
            // Color label
            colorLabel = new JLabel(" ");
            colorLabel.setOpaque(true);
            colorLabel.setPreferredSize(new Dimension(15, 15));
            colorLabel.setBorder(BorderFactory.createLineBorder(BG_COLOR, colorLabelBorder));
            // Text label
            textLabel = new JLabel(NO_TILE_STRING);
            textLabel.setFont(LEGEND_LIST_FONT);
            add(colorLabel);
            add(textLabel);
        }

        // Get stamp renderer
        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {

            if (value instanceof TileLegendItem) {
                // tile item
                TileLegendItem item = (TileLegendItem) value;
                textLabel.setText(item.getLabelText());
                colorLabel.setBackground(config.getColorForTile(item.getTileNumber()));
            } else {
                // String
                textLabel.setText(NO_TILE_STRING);
                colorLabel.setBackground(this.getBackground());
            }
            // Selection
            Color bgColor = isSelected ? SELECT_COLOR : BG_COLOR;
            setBackground(bgColor);
            colorLabel.setBorder(BorderFactory.createLineBorder(bgColor, colorLabelBorder));
            return this;
        }
    }

    // The listener for the tile legend list
    //
    private class LegendClickListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            if (currentTileNum == 0) {
                // Do nothing if legend empty
                return;
            }
            // Single click marks tile in canvas
            if (e.getClickCount() == 1) {
                int tileIndex = tileLegendList.locationToIndex(e.getPoint());
                // mark column
                canvas.markColumn(tileIndex);
                // repaint this tile
                redraw();
            }
            // Double click opens a color chooser for this tile
            else if (e.getClickCount() == 2) {
                int tileIndex = tileLegendList.locationToIndex(e.getPoint());
                Color newTileColor = JColorChooser.showDialog(HistogramFrame.this,
                        "Choose Color for tile " + tileIndex,
                        config.getColorForTile(tileIndex));
                // set new selected color
                config.setColorForTile(tileIndex, newTileColor);
                // mark column
                canvas.markColumn(tileIndex);
                // repaint this tile
                redraw();
            }
        }
    }

    // The mouse listener for the canvas
    //
    private class CanvasClickListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            if (currentTileNum == 0) {
                // Do nothing if image empty
                return;
            }
            // One click marks the tile
            if (e.getClickCount() == 1) {
                int tileIndex = calcTileIndex(e);
                canvas.markColumn(tileIndex);
                tileLegendList.setSelectedIndex(tileIndex);
                // repaint this tile
                redraw();
            }
            // two clicks open the color chooser
            else if (e.getClickCount() == 2) {
                int tileIndex = calcTileIndex(e);
                // mark column
                canvas.markColumn(tileIndex);
                tileLegendList.setSelectedIndex(tileIndex);
                // open color chooser
                Color newTileColor = JColorChooser.showDialog(HistogramFrame.this,
                        "Choose Color for tile " + tileIndex,
                        config.getColorForTile(tileIndex));
                // set new selected color
                config.setColorForTile(tileIndex, newTileColor);
                // repaint this tile
                redraw();
            }

        }

        // helper method to calculate clicked column
        private int calcTileIndex(MouseEvent e) {
            JViewport viewport = canvasScrollPane.getViewport();
            Point upperLeft = viewport.getViewPosition();
            int imageX = (int) e.getX() + (int) upperLeft.getX();
            int imageY = (int) e.getY() + (int) upperLeft.getY();
            return config.calcTileForPoint(imageX, imageY);
        }

        // Cursor is a crosshair inside the canvas
        public void mouseEntered(MouseEvent e) {
            setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        }

        public void mouseExited(MouseEvent e) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    // Helper method to mark the chart
    //
    private void prepareCanvas(int streamID) {
        Stream stream = space.getStream(streamID);
        if (stream.getPresentation() == Stream.PRESENTATION_PERCENT) {
            // Percent marks
            canvas.setYStrings("100%", "50%", "0");
        } else {
            // Take real values
            canvas.setYStrings(stream.getMaxValue() + "",
                    stream.getMaxValue() / 2 + "",
                    stream.getZeroValue() + "");
        }
    }

    public void disconnect(boolean reconnecting) {
        if (reconnecting) {
            if (space != null) {
                shutdown();
            }
        }
    }

    public void event(int id, int elapsedTime, int compensationTime) {
        // handle selection
        fillLegendList();
        // redraw image
        redraw();
    }

    public void space(ClientSpace space) {
        if (this.space.getID() == space.getID()) {
            this.space = space;
            if (space.getTileNum() > currentTileNum) {
                currentTileNum = space.getTileNum();
                canvas.resize(currentTileNum);
                canvasScrollPane.revalidate();
            }
        }
    }

    // Helper method to wipe and redraw the canvas with the stream data
    //
    private void redraw() {
        int streamID = config.getStream();
        Stream stream;
        DataAccessor accessor = null;
        int max = 0;

        if (streamID == -1)
            stream = null;
        else {
            stream = space.getStream(streamID);
            accessor = stream.getAccessor();
            max = accessor.getAdjustedMax();
            config.setStreamColor(tmColors.getStreamColor(streamID));
        }

        byte control[] = space.getControl();
        currentTileNum = space.getTileNum();

        // wipe old image
        canvas.wipeImage();

        // resize
        canvas.resize(currentTileNum);
        canvasScrollPane.revalidate();

        // Loop over all tiles
        for (int i = 0; i < currentTileNum; ++i) {
            if (stream != null) {
                if (Space.isControlUsed(control[i])) {
                    int val = accessor.get(i);
                    // paint nothing for zero values
                    if (!accessor.isZero(i)) {
                        int percent = (val * 100) / max;
                        canvas.paintColumn(i, percent);
                    }
                }
            }
        }
        // finish image
        canvas.finishImage();
        // flush image
        canvas.flush();
    }

    // Helper method to fill the legend list with all tiles
    //
    private void fillLegendList() {
        // remove all items
        tileLegendModel.removeAllElements();
        // check stream
        Stream stream = this.space.getStream(config.getStream());
        if (stream == null) {
            tileLegendModel.addElement("No Stream!");
            return;
        }
        currentTileNum = space.getTileNum();
        if (currentTileNum == 0) {
            // insert placeholder
            tileLegendModel.addElement(NO_TILE_STRING);
            return;
        }
        // add tile items
        for (int i = 0; i < currentTileNum; ++i) {
            String tileName = ("[" + i + "] " + space.getTileName(i)).trim();
            tileLegendModel.addElement(new TileLegendItem(tileName, i));
        }
    }

    // Setup frame
    //
    private void setup(ClientInterpreter interpreter,
                       List<PluginFrame> pluginFrames, ClientSpace space, int selectedStream,
                       TileManagerColors tmColors) {
        super.setup(interpreter, pluginFrames, space, tmColors, "GCspy: Histogram for " + space.getName());

        config = new HistogramConfig(selectedStream, tmColors);
        currentTileNum = space.getTileNum();

        Container cont = getContentPane();
        cont.setLayout(new BorderLayout());
        JPanel panel;

        // Space canvas
        canvas = new HistogramCanvas(config);
        canvasScrollPane = new JScrollPane(canvas, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        canvasScrollPane.setBorder(Factory.createTitledBorder(space.getName()));
        canvasScrollPane.addMouseListener(new CanvasClickListener());
        cont.add(canvasScrollPane, BorderLayout.CENTER);

        // Selection panel
        panel = Factory.createTitlePanel("Selection");
        panel.setLayout(new BorderLayout());
        tileLegendModel = new DefaultListModel();
        tileLegendList = new JList(tileLegendModel);
        tileLegendList.setLayoutOrientation(JList.VERTICAL);
        tileLegendList.setCellRenderer(new TileLegendCellRenderer());
        tileLegendList.addMouseListener(new LegendClickListener());
        tileLegendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // fill list
        fillLegendList();
        JScrollPane legendScroller = new JScrollPane(tileLegendList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(legendScroller, BorderLayout.CENTER);
        cont.add(panel, BorderLayout.EAST);

        // Stream combo box
        streamCB = Factory.createComboBox(new StreamCBListener());
        for (int i = 0; i < space.getStreamNum(); ++i) {
            streamCB.addItem(space.getStream(i).getName());
        }
        streamCB.setSelectedIndex(selectedStream);

        // Stream panel
        JPanel streamP = Factory.createFlowPanel(FlowLayout.CENTER);
        streamP.add(Factory.createLabel("Stream  ", JLabel.RIGHT, true));
        streamP.add(streamCB);

        // Close button
        closeB = Factory.createButton("Close", new CloseBListener(this));
        JPanel closeP = Factory.createFlowPanel(FlowLayout.RIGHT);
        closeP.add(closeB);

        // Controls panel
        panel = Factory.createTitlePanel("Controls");
        panel.setLayout(new BorderLayout());

        panel.add(streamP, BorderLayout.WEST);
        panel.add(closeP, BorderLayout.EAST);

        cont.add(panel, BorderLayout.SOUTH);
        pack();
    }

    public void destroy() {
        canvasScrollPane = null;
        canvas = null;
        config = null;
        tmColors = null;
        interpreter = null;
        space = null;
        pluginFrames = null;
        super.destroy();
    }

    /**
     * Create a new histogram frame
     * @param owner The owner, i.e. a MainFrame
     * @param pluginFrames The list of PluginFrames to which this plugin belongs
     * @param interpreter The ClientInterpreter
     * @param space The ClientSpace
     * @param selectedStream The stream to graph
     * @param tmColors The TileManagerColors
     */
    public HistogramFrame(AbstractFrame owner, List<PluginFrame> pluginFrames,
                          ClientInterpreter interpreter, ClientSpace space, int selectedStream,
                          TileManagerColors tmColors) {
        super(owner, Position.POS_CENTER, false);
        setup(interpreter, pluginFrames, space, selectedStream, tmColors);
        // init chart and flush first image
        prepareCanvas(selectedStream);
        canvas.finishImage();
        canvas.flush();
    }

}
