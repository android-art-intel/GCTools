/**
 * * $Id: HistoryFrame.java 32 2005-07-12 10:50:05Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.vis.plugins.history;

import gcspy.interpreter.DataAccessor;
import gcspy.interpreter.Space;
import gcspy.interpreter.Stream;
import gcspy.interpreter.client.ClientInterpreter;
import gcspy.interpreter.client.ClientSpace;
import gcspy.vis.TileManagerColors;
import gcspy.vis.plugins.PluginFrame;
import gcspy.vis.utils.AbstractFrame;
import gcspy.vis.utils.AdjustedColor;
import gcspy.vis.utils.Factory;
import gcspy.vis.utils.IconFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;

/**
 * Frame that does all the history graph rendering
 * @author Tony Printezis
 */
public class HistoryFrame extends PluginFrame {

    private JScrollPane scrollPane;

    private HistoryCanvas canvas;

    private JButton startB;

    private JButton stopB;

    private JButton configB;

    private JButton saveB;

    private JButton maxB;

    private JButton closeB;

    private JCheckBox scrollCB;

    private JLabel titleL, titleR;

    private HistoryConfig config;

    private int maxTileNum;

    private boolean started;

    private boolean scroll = false;

    static private final int DEFAULT_WIDTH = 640;

    static private final int DEFAULT_HEIGHT = 400;

    private class StartBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            start();
        }
    }

    private class StopBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            stop();
        }
    }

    private class ConfigBListener implements ActionListener {
        private AbstractFrame owner;

        public void actionPerformed(ActionEvent event) {
            HistoryConfigDialog dialog = new HistoryConfigDialog(owner, config,
                    interpreter, space, tmColors);
            if (dialog.result()) {
                config = dialog.getConfig();
            }
        }

        ConfigBListener(AbstractFrame owner) {
            this.owner = owner;
        }
    }

    private class SaveBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            save();
        }
    }

    private class MaxBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            maximise();
        }
    }

    private class CloseBListener implements ActionListener {
        private AbstractFrame owner;

        public void actionPerformed(ActionEvent event) {
            // setVisible(false);
            if (started)
                stop();
            pluginFrames.remove(owner);
            shutdown();
        }

        CloseBListener(AbstractFrame owner) {
            this.owner = owner;
        }
    }

    private class ScrollCBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            scroll = scrollCB.isSelected();
        }
    }

    private class DragListener extends MouseAdapter implements
            MouseMotionListener {
        private int yStart, xStart;

        private Cursor cursor;

        private boolean canDrag(MouseEvent e) {
            return (canvas != null)
                    && (e.getModifiers() & InputEvent.BUTTON1_MASK) != 0;
        }

        public void dragScrollbar(JScrollBar scrollbar, int diff) {
            int val = scrollbar.getValue();
            int max = scrollbar.getMaximum();
            int vis = scrollbar.getVisibleAmount();

            int newVal = val + diff;
            if (newVal < 0) {
                if (val == 0)
                    return;
                else
                    newVal = 0;
            } else if (newVal > (max - vis)) {
                if (val == (max - vis))
                    return;
                else
                    newVal = max - vis;
            }
            scrollbar.setValue(newVal);
        }

        public void mouseDragged(MouseEvent e) {
            if (canDrag(e)) {
                int xDiff = e.getX() - xStart;
                int yDiff = e.getY() - yStart;
                xStart = e.getX();
                yStart = e.getY();

                dragScrollbar(scrollPane.getHorizontalScrollBar(), -xDiff);
                dragScrollbar(scrollPane.getVerticalScrollBar(), -yDiff);
            }
        }

        public void mousePressed(MouseEvent e) {
            if (canDrag(e)) {
                xStart = e.getX();
                yStart = e.getY();
                cursor = getCursor();
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        }

        public void mouseMoved(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
            if (canDrag(e)) {
                setCursor(cursor);
            }
        }
    }

    private void updateImageSize() {
        String imageSize = canvas.getWidth() + "x" + canvas.getHeight();
        titleR.setText("Size: " + imageSize);
    }

    private String generateTitle() {
        String title = generateSmallTitle();
        String pre = config.getTitlePre();

        if (!pre.equals("")) {
            title = pre + ", " + title;
        }

        return title;
    }

    private String generateSmallTitle() {
        String streamName;
        int index = config.getStream();
        if (index == -1)
            streamName = "<none>";
        else
            streamName = space.getStream(index).getName();
        return space.getFullName() + ", " + streamName;
    }

    private void start() {
        Factory.disableButton(startB);
        Factory.enableButton(stopB);
        Factory.disableButton(saveB);
        Factory.disableButton(configB);
        Factory.enableCheckBox(scrollCB);
        started = true;
        createNewCanvas();

        // int index = config.getStream();
        // String streamName;
        // if (index == -1)
        // streamName = "<none>";
        // else
        // streamName = space.getStream(index).getName();
        // titleL.setText(space.getFullName() + ", " + streamName);
        titleL.setText(generateSmallTitle());

        updateImageSize();
    }

    private void stop() {
        Factory.enableButton(startB);
        Factory.disableButton(stopB);
        Factory.enableButton(saveB);
        Factory.enableButton(configB);
        Factory.disableCheckBox(scrollCB);
        scroll = false;
        Factory.deselectCheckBox(scrollCB);
        started = false;
    }

    public void disconnect(boolean reconnecting) {
        if (reconnecting) {
            if (space != null) {
                shutdown();
            }
        } else {
            if (started) {
                stop();
            }
            Factory.disableButton(startB);
        }
    }

    private void save() {
        JFileChooser fc = new JFileChooser(".");

        int index = config.getStream();
        String streamName;
        if (index == -1)
            streamName = "none";
        else
            streamName = space.getStream(index).getName();
        String fileName = streamName.toLowerCase() + "_history.tif";
        String pre = config.getTitlePre();
        if (!pre.equals("")) {
            fileName = pre + " " + fileName;
        }
        fileName = fileName.toLowerCase();
        fileName = fileName.replace(' ', '_');
        fc.setSelectedFile(new File(fileName));

        int res = fc.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            Cursor cursor = getCursor();
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            canvas.save(file);
            setCursor(cursor);
        }
    }

    private void maximise() {
        int sWidth = scrollPane.getVerticalScrollBar().getWidth();
        int maxWidth = bgWidth - 2 * BORDER_PADDING - sWidth;
        int newWidth = canvas.getWidth();
        if (newWidth > maxWidth)
            newWidth = maxWidth;

        JViewport viewport = scrollPane.getViewport();
        int newHeight = viewport.getHeight();
        if (viewport.getWidth() < newWidth) {
            viewport.setPreferredSize(new Dimension(newWidth, newHeight));
            pack();
            if (sufficientlyOffRight())
                placeIt(Position.POS_ROOT_HOR_CENTER);
        }
    }

    public void event(int id, int elapsedTime, int compensationTime) {
        int count[] = interpreter.getEventCount();
        if (started) {
            if (config.horSep() && (id == config.getHorSepEvent())) {
                drawHorSep(id, count[id]);
            } else {
                redraw();
            }
        }
    }

    public void space(ClientSpace space) {
        if (this.space.getID() == space.getID()) {
            this.space = space;
            if (space.getTileNum() > maxTileNum) {
                config.setCols(space.getTileNum());
                maxTileNum = space.getTileNum();
                if (started) {
                    canvas.resize(generateTitle());
                    updateImageSize();
                    scrollPane.revalidate();
                }
            }
        }
    }

    private void drawHorSep(int id, int count) {
        canvas.paintHorSep(config.getHorSepColor(), count);
    }

    private void centerCanvas() {
        JScrollBar scrollbar = scrollPane.getVerticalScrollBar();
        int val = scrollbar.getValue();
        int min = scrollbar.getMinimum();
        int max = scrollbar.getMaximum();
        int vis = scrollbar.getVisibleAmount();
        int pos = canvas.getCurrentPos();

        if (max > vis) {
            if ((pos < val) || (pos > (val + (4 * vis / 5)))) {
                int newVal = pos - vis / 5;
                if ((newVal + vis) > max)
                    newVal = max - vis;
                scrollbar.setValue(newVal);
            }
        }
    }

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
        }

        AdjustedColor ac = new AdjustedColor(config.getLowColor(), config
                .getHiColor(), max);
        canvas.start(ac);

        byte control[] = space.getControl();
        int len = space.getTileNum();
        // previous version seemed to paint some used tiles as unused
        // this may of course be an error on the server side.
        if (accessor.isDataAvailable()) {
            for (int i = 0; i < len; ++i) {
                if (stream == null) {
                    // if no stream selected, use zero color
                    canvas.paintTile(i, config.getZeroColor());
                } else {
                    if (Space.isControlUsed(control[i])) {
                        //int val = accessor.get(i);
                        if (accessor.isZero(i)) {
                            canvas.paintTile(i, config.getZeroColor());
                        } else {
                            int adj = accessor.getAdjusted(i);
                            canvas.paintTile(i, adj);
                        }
                    } else if (Space.isControlUnused(control[i])) {
                        canvas.paintTile(i, config.getUnusedColor());
                    } else if (config.getSpaceSep()) {
                        if (Space.isControlSeparator(control[i])) {
                            canvas.paintSep(i);
                        }
                    }
                }
            }
        }

    /*
    for (int i = 0; i < len; ++i) {
      if (stream == null) {
        // if no stream selected, use zero color
        canvas.paintTile(i, config.getZeroColor());
      } else {
        if (Space.isControlUsed(control[i])) {
          int val = accessor.get(i);
          if (accessor.isZero(i)) {
            canvas.paintTile(i, config.getZeroColor());
          } else {
            int adj = accessor.getAdjusted(i);
            canvas.paintTile(i, adj);
          }
        }
      }
      if (Space.isControlUnused(control[i])) {
        canvas.paintTile(i, config.getUnusedColor());
      }

      if (config.getSpaceSep()) {
        if (Space.isControlSeparator(control[i])) {
          canvas.paintSep(i);
        }
      }
    }
    */

        canvas.flush();
        if (scroll)
            centerCanvas();

        if (canvas.hasStopped()) {
            stop();
        }
    }

    private void createNewCanvas() {
        Factory.enableButton(maxB);
        canvas = new HistoryCanvas(config, generateTitle());

        JViewport viewport = scrollPane.getViewport();

        int cWidth = canvas.getWidth();
        int cHeight = canvas.getHeight();
        int vWidth = viewport.getWidth();
        int vHeight = viewport.getHeight();

        if (cWidth < vWidth)
            vWidth = cWidth;
        if (cHeight < vHeight)
            vHeight = cHeight;

        viewport.setPreferredSize(new Dimension(vWidth, vHeight));
        scrollPane.setViewportView(canvas);
        pack();
    }

    private void setup(ClientInterpreter interpreter,
                       List<PluginFrame> pluginFrames, ClientSpace space, int selectedStream,
                       TileManagerColors tmColors) {
        super.setup(interpreter, pluginFrames, space, tmColors, "GCspy: History Graphs");

        config = new HistoryConfig(this, space.getTileNum(), selectedStream,
                tmColors);
        maxTileNum = space.getTileNum();
        started = false;

        Container cont = getContentPane();
        cont.setLayout(new BorderLayout());
        JPanel panel;

        // Title panel
        panel = Factory.createBorderPanel();
        titleL = Factory.createLabel(space.getFullName(), JLabel.LEFT, true);
        panel.add(titleL, BorderLayout.WEST);
        titleR = Factory.createLabel("", JLabel.RIGHT, true);
        panel.add(titleR, BorderLayout.EAST);
        cont.add(panel, BorderLayout.NORTH);

        scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        DragListener dragListener = new DragListener();
        scrollPane.addMouseMotionListener(dragListener);
        scrollPane.addMouseListener(dragListener);
        JViewport viewport = scrollPane.getViewport();
        viewport.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        cont.add(scrollPane, BorderLayout.CENTER);

        // Button controls panel
        panel = Factory.createFlowPanel(FlowLayout.CENTER);

        startB = Factory.createIconButton(IconFactory.createRecIcon(), true,
                "Start", new StartBListener());
        panel.add(startB);
        stopB = Factory.createIconButton(IconFactory.createStopIcon(), false,
                "Stop", new StopBListener());
        panel.add(stopB);
        configB = Factory.createButton("Config", new ConfigBListener(this));
        panel.add(configB);
        saveB = Factory.createButton("Save", false, new SaveBListener());
        panel.add(saveB);
        maxB = Factory.createButton(" Max ", false, new MaxBListener());
        panel.add(maxB);
        scrollCB = Factory.createCheckBox("Scroll", false, scroll,
                new ScrollCBListener());
        panel.add(scrollCB);
        closeB = Factory.createButton("Close", new CloseBListener(this));
        panel.add(closeB);

        cont.add(panel, BorderLayout.SOUTH);
        pack();
    }

    /** Destroy this view  and dispose of the frame */
    public void destroy() {
        scrollPane = null;
        canvas = null;
        config = null;
        tmColors = null;
        interpreter = null;
        space = null;
        pluginFrames = null;
        super.destroy();
    }

    /**
     * Create a new history frame
     * @param owner The owner, i.e. a MainFrame
     * @param pluginFrames The list of PluginFrames to which this plugin belongs
     * @param interpreter The ClientInterpreter
     * @param space The ClientSpace
     * @param selectedStream The stream to graph
     * @param tmColors The TileManagerColors
     */
    public HistoryFrame(AbstractFrame owner, List<PluginFrame> pluginFrames,
                        ClientInterpreter interpreter, ClientSpace space, int selectedStream,
                        TileManagerColors tmColors) {
        super(owner, Position.POS_CENTER, false);
        setup(interpreter, pluginFrames, space, selectedStream, tmColors);
    }

}
