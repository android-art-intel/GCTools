/**
 * * $Id: SpaceManager.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.vis;

import gcspy.interpreter.DataAccessor;
import gcspy.interpreter.Space;
import gcspy.interpreter.Stream;
import gcspy.interpreter.client.ClientInterpreter;
import gcspy.interpreter.client.ClientSpace;
import gcspy.interpreter.client.EventListener;
import gcspy.interpreter.client.SpaceListener;
import gcspy.vis.plugins.PluginFrame;
import gcspy.vis.plugins.PluginListener;
import gcspy.vis.utils.AbstractFrame;
import gcspy.vis.utils.AdjustedColor;
import gcspy.vis.utils.Factory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the interaction between a Space and the visualiser
 * @author Tony Printezis
 * @author <a href="http://www/cs/kent.ac.uk">Richard Jones</a>
 */
public class SpaceManager implements TileManagerListener, DisconnectListener,
        EventListener, SpaceListener {

    private JPanel mainPanel;

    private TileManager tileManager;
    private TileManager smallTileManager;
    private TileManager largeTileManager;
    private TileManagerColors colorConfig;

    private SpaceManagerInterface spaceManagerInterface;

    private ClientSpace space;
    private boolean active;

    private boolean firstRedraw;

    private int selectedStream = 0;

    private int selectedTile = -1;

    private boolean validate = false;

    private JLabel viewLabel;

    private JButton activateB;

    private JButton summaryB;

    private JButton legendB;

    private JButton colorsB;

    private ClientInterpreter interpreter;

    private AbstractFrame owner;

    private List<PluginFrame> pluginFrames;

    // private HistoryFrame historyFrame;
    private LegendFrame legendFrame;

    private SummaryFrame summaryFrame;

    private TileManagerColorsFrame colorsFrame;
  
  /*
   * Listeners
   */

    /**
     * Select a stream
     * @param id The stream's ID
     */
    public void selectedStream(int id) {
        selectedStream = id;
        setViewLabel();
        legendFrame.selectStream(id);
        colorsFrame.selectStream(id);
        redraw();
    }

    public void event(int eventID, int elapsedTime, int compensationTime) {
        if (firstRedraw) {
            if (active) {
                tileManager.enableSelection();
                smallTileManager.enableSelection();
                largeTileManager.enableSelection();
            }
            firstRedraw = false;
        }
        validateContainer();
        redraw();
    }

    public void space(ClientSpace space) {
        if (this.space.getID() == space.getID()) {
            this.space = space;

            int tileNum = space.getTileNum();
            if ((selectedTile != -1) && (selectedTile >= tileNum)) {
                selectedTile = -1;
            }

            tileManager.resize(tileNum);
            tileManager.setWidth(tileManager.getWidth());
            // tileManager.invalidate();
            invalidateTileManager(tileManager);
            // spaceManagerInterface.validateContainer();

            if (selectedTile == -1)
                tileManager.deselectTile();
            if (active) {
                smallTileManager.resize(tileNum);
                largeTileManager.resize(tileNum);
                if (selectedTile == -1) {
                    smallTileManager.deselectTile();
                    largeTileManager.deselectTile();
                }
            }
        }
    }

    /** Draw the tiles image */
    public void mapImage() {
        tileManager.mapImage();
        if (active) {
            smallTileManager.mapImage();
            largeTileManager.mapImage();
        }
    }

    private void setViewLabel() {
        viewLabel.setText("View: " + space.getStream(selectedStream).getName());
    }

    public void redraw() {
        redrawOne(tileManager);
        if (active) {
            redrawOne(smallTileManager);
            redrawOne(largeTileManager);
            updateBlockInfo();
        }
    }

    public void disconnect(boolean reconnecting) {
        for (PluginFrame frame : pluginFrames)
            frame.disconnect(reconnecting);
        if (reconnecting)
            pluginFrames.clear();

        legendFrame.disconnect(reconnecting);
        summaryFrame.disconnect(reconnecting);
        colorsFrame.disconnect(reconnecting);
    }

    /** Deactivate this space */
    public void deactivate() {
        active = false;
        selectedTile = -1;
        Factory.enableButton(activateB);
        tileManager.setDrawFrame(false);
        tileManager.disableSelection();
        smallTileManager = null;
        largeTileManager = null;
    }

    /** Activate this space */
    public void activate() {
        if (!active) {
            active = true;
            Factory.disableButton(activateB);
            spaceManagerInterface.setActive(this);
            tileManager.setDrawFrame(true);
            if (!firstRedraw)
                tileManager.enableSelection();
            takeOverMainFrame();
        }
    }

    private class ActivateListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            activate();
        }
    }

    private class SummaryListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            summaryFrame.setVisible(true);
        }
    }

    private class LegendListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            legendFrame.setVisible(true);
        }
    }

    private class ColorsListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            colorsFrame.setVisible(true);
        }
    }

    private class ClearMarkersListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            tileManager.clearMarkers();
        }
    }

    private class EnterListener extends MouseAdapter {
        public void mouseEntered(MouseEvent event) {
            activate();
        }
    }

    /**
     * Redraw the space
     * @param tm The tile manager for this space
     */
    public void redrawOne(TileManager tm) {
        if (tm == null)
            return;

        if (selectedStream != -1) {
            tm.enableFooter(false);
            tm.clear();

            Stream stream = space.getStream(selectedStream);
            DataAccessor accessor = stream.getAccessor();
            int max = accessor.getAdjustedMax();
            Color theColor = colorConfig.getStreamColor(selectedStream);
            AdjustedColor ac = new AdjustedColor(theColor, max);
            byte control[] = space.getControl();
            int len = space.getTileNum();
            if (accessor.isDataAvailable()) {
                for (int i = 0; i < len; ++i) {
                    if (Space.isControlUsed(control[i])) {
                        if (accessor.isZero(i)) {
                            tm.paintTileZero(i);
                        } else if (accessor.isOverflow(i)) {
                            int val = accessor.getAdjusted(i);
                            Color c = ac.generate(val);
                            tm.paintTileOverflow(i, c);
                        } else {
                            int val = accessor.getAdjusted(i);
                            Color c = ac.generate(val);
                            tm.paintTile(i, c);
                        }
                    } else if (Space.isControlUnused(control[i])) {
                        tm.setTileUnused(i);
                    }
                    if (Space.isControlSeparator(control[i])) {
                        tm.paintSeparator(i);
                    }
                    if (Space.isControlLink(control[i])) {
                        tm.paintLink(i);
                    }
                }
            }

            tm.flush();
        }
    }

    private class MagManagerListener implements TileManagerListener {
        private boolean small;

        public void tileSelected(int index) {
            if (small) {
                largeTileManager.selectTile(index);
                redrawOne(largeTileManager);
            } else {
                smallTileManager.selectTile(index);
                redrawOne(smallTileManager);
            }
            tileManager.selectTile(index);

            selectedTile = index;
            updateBlockInfo();
        }

        public void tileDeselected() {
            if (small)
                largeTileManager.deselectTile();
            else
                smallTileManager.deselectTile();
            tileManager.deselectTile();

            selectedTile = -1;
            updateBlockInfo();
        }

        public void redraw() {
            if (small)
                redrawOne(smallTileManager);
            else
                redrawOne(largeTileManager);
        }

        MagManagerListener(boolean small) {
            this.small = small;
        }
    }

    private class ResizeListener extends ComponentAdapter {
        public void componentResized(ComponentEvent e) {
            Dimension d = mainPanel.getSize();
            tileManager.setWidth(d.width);
            // tileManager.invalidate();
            invalidateTileManager(tileManager);
            // spaceManagerInterface.validateContainer();
        }
    }

  /*
   * Tile Manager listener
   */

    public void tileSelected(int index) {
        smallTileManager.selectTile(index);
        largeTileManager.selectTile(index);

        selectedTile = index;
        updateBlockInfo();

        redrawOne(smallTileManager);
        redrawOne(largeTileManager);
    }

    public void tileDeselected() {
        smallTileManager.deselectTile();
        largeTileManager.deselectTile();

        selectedTile = -1;
        updateBlockInfo();
    }

    /** Update the block info */
    public void updateBlockInfo() {
        if (selectedTile == -1)
            spaceManagerInterface.setBlockInfo("");
        else
            spaceManagerInterface.setBlockInfo(space.presentTile(selectedTile));
    }

  /*
   * GUI related stuff
   */

    private void setupMainPanel(boolean small, boolean tiny) {
        Container horizontal;
        mainPanel = Factory.createBorderPanel();
        mainPanel.addComponentListener(new ResizeListener());

        tileManager = new TileManager(null, space.getTileNum(), colorConfig, false, false);
        if (small)
            tileManager.setTileSizeSmall();
        if (tiny)
            tileManager.setTileSizeTiny();
        // tileManager.addMouseListener(new EnterListener());

        tileManager.addListener(this);
        mainPanel.add(tileManager, BorderLayout.CENTER);

        JPanel top = Factory.createBorderPanel();

        JLabel titleLabel = Factory.createLabel(space.getFullName(), SwingConstants.RIGHT, true);
        top.add(titleLabel, BorderLayout.WEST);

        viewLabel = Factory.createLabel("", SwingConstants.RIGHT, true);
        setViewLabel();
        top.add(viewLabel, BorderLayout.EAST);

        mainPanel.add(top, BorderLayout.NORTH);

        JPanel bottomPanel = Factory.createBorderPanel();

        horizontal = new Box(BoxLayout.X_AXIS);
        activateB = Factory.createButton("Activate", !active, true, new ActivateListener());
        horizontal.add(activateB);
        horizontal.add(Factory.createEmptyPanel(2, 2));
        summaryB = Factory.createButton("Summary", true, true, new SummaryListener());
        horizontal.add(summaryB);

        legendB = Factory.createButton("Legend", true, true, new LegendListener());
        horizontal.add(legendB);
        colorsB = Factory.createButton("Colors", true, true, new ColorsListener());
        horizontal.add(colorsB);

        // Add plugins
        horizontal.add(Factory.createEmptyPanel(2, 2));
        for (PluginListener pluginListener : spaceManagerInterface.getPluginListeners()) {
            pluginListener.init(owner, pluginFrames, interpreter, space, selectedStream, colorConfig);
            JButton traceB = Factory.createButton(pluginListener.getLabel(), true, true, pluginListener);
            horizontal.add(traceB);
        }

        bottomPanel.add(horizontal, BorderLayout.WEST);

        horizontal = new Box(BoxLayout.X_AXIS);
        JButton clearMarkersB = Factory.createButton("Clear Markers", true, true, new ClearMarkersListener());
        horizontal.add(clearMarkersB);
        bottomPanel.add(horizontal, BorderLayout.EAST);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void invalidateTileManager(TileManager tm) {
        tm.invalidate();
        validate = true;
    }

    private void validateContainer() {
        if (validate)
            spaceManagerInterface.validateContainer();
        validate = false;
    }

    /**
     * Set the width of the space
     * @param width The width to set
     */
    public void setWidth(int width) {
        tileManager.setWidth(width);
    }


    /**
     * Get the main panel
     * @return the main panel
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void takeOverMainFrame() {
        spaceManagerInterface.clearViews();
        for (int i = 0; i < space.getStreamNum(); ++i) {
            spaceManagerInterface.addView(space.getStream(i).getName());
        }
        spaceManagerInterface.setActiveView(selectedStream);

        smallTileManager = new TileManager(null, space.getTileNum(), colorConfig, true, !firstRedraw);
        smallTileManager.setTileSizeSmall();
        smallTileManager.setWidth(200);
        smallTileManager.addListener(new MagManagerListener(true));

        largeTileManager = new TileManager(null, space.getTileNum(), colorConfig, true, !firstRedraw);
        largeTileManager.setTileSizeLarge();
        largeTileManager.setWidth(200);
        largeTileManager.addListener(new MagManagerListener(false));

        spaceManagerInterface.addMagManagers(smallTileManager, largeTileManager);
    }

  /*
   * Constructor
   */

    /**
     * Create a new space manager
     * @param owner The parent fram
     * @param interpreter The client interpreter
     * @param space the space to manage
     * @param spaceManagerInterface Typically the main frame
     * @param active True if this space is active
     * @param small True if the tiles are small
     * @param tiny True if the tiles are tiny
     */
    public SpaceManager(AbstractFrame owner, ClientInterpreter interpreter,
                        ClientSpace space, SpaceManagerInterface spaceManagerInterface,
                        boolean active, boolean small, boolean tiny) {
        this.active = active;
        this.firstRedraw = true;
        this.spaceManagerInterface = spaceManagerInterface;
        this.colorConfig = new TileManagerColors(space);

        this.owner = owner;
        this.interpreter = interpreter;
        this.space = space;

        pluginFrames = new ArrayList<>();

        setupMainPanel(small, tiny);

        if (active) {
            takeOverMainFrame();
            tileManager.setDrawFrame(true);
        }

        // historyFrame = new HistoryFrame(owner, interpreter, space, colorConfig);
        legendFrame = new LegendFrame(owner, interpreter, space, selectedStream,
                colorConfig);
        legendFrame.setPositionalComponent(legendB);
        summaryFrame = new SummaryFrame(owner, interpreter, space);
        summaryFrame.setPositionalComponent(summaryB);
        colorsFrame = new TileManagerColorsFrame(owner, space, this, colorConfig,
                legendFrame);
        colorsFrame.setPositionalComponent(colorsB);
    }

}
