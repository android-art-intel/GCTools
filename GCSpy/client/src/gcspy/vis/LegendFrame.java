/**
 * * $Id: LegendFrame.java 24 2005-06-17 09:37:56Z rej $
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
import gcspy.vis.utils.AbstractFrame;
import gcspy.vis.utils.AdjustedColor;
import gcspy.vis.utils.Factory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Frame that displays the legend of the current space view
 * @author Tony Printezis
 */
class LegendFrame extends AbstractFrame
        implements EventListener, SpaceListener, DisconnectListener {

    private ClientInterpreter interpreter;
    private Space space;
    // private Stream stream;
    private int streamID;
    private int rows;
    private int values[];
    private TileManager tileManagers[] = null;
    private JLabel labels[];
    private TileManagerColors colorConfig;

    private class CloseBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            setVisible(false);
        }
    }

    public void event(int eventID, int elapsedTime, int compensationTime) {
        // Space space = interpreter.getSpace(spaceID);
        Stream stream = space.getStream(streamID);
        if (stream.getPresentation() == Stream.PRESENTATION_MAX_VAR) {
            setupValues();
            setupLabels();
            paintTileManagers();
        }
    }

    public void space(ClientSpace space) {
        if (this.space.getID() == space.getID()) {
            this.space = space;
        }
    }

    public void disconnect(boolean reconnecting) {
        if (reconnecting) {
            setVisible(false);
            destroy();
        }
    }

    private void paintTileManagers() {
        // Space space = interpreter.getSpace(spaceID);
        Stream stream = space.getStream(streamID);
        DataAccessor accessor = stream.getAccessor();
        int max = accessor.getAdjustedMax();
        Color theColor = colorConfig.getStreamColor(streamID);
        AdjustedColor ac = new AdjustedColor(theColor, max);
        for (int i = 0; i <= rows; ++i) {
            TileManager tm = tileManagers[i];
            tm.clear();
            if (i != rows) {
                int val = values[i];
                int adj;
                int presentation = stream.getPresentation();
                if (presentation != Stream.PRESENTATION_PERCENT_VAR)
                    adj = stream.adjustValue(val);
                else
                    adj = val;
                if (stream.isZero(val)) {
                    tm.paintTileZero(0);
                } else if (stream.isOverflow(val)) {
                    Color c = ac.generate(adj);
                    tm.paintTileOverflow(0, c);
                } else {
                    Color c = ac.generate(adj);
                    tm.paintTile(0, c);
                }
            } else {
                tm.setTileUnused(0);
            }
            tm.flush();
        }
    }

    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) {
            paintTileManagers();
        }
    }

    private void setupValues() {
        // Space space = interpreter.getSpace(spaceID);
        Stream stream = space.getStream(streamID);
        int presentation = stream.getPresentation();
        int paintStyle = stream.getPaintStyle();
        int min = stream.getMinValue();
        int zero = stream.getZeroValue();
        int max = stream.getMaxValue();
        int curr = 0;
        int half;
        boolean doZero = (paintStyle == Stream.PAINT_STYLE_ZERO);

        switch (presentation) {
            case Stream.PRESENTATION_PLUS:
                values[curr++] = max + 1;
            case Stream.PRESENTATION_PLAIN:
            case Stream.PRESENTATION_MAX_VAR:
            case Stream.PRESENTATION_PERCENT:
                values[curr++] = max;
                if (doZero && (zero == max)) {
                    values[curr++] = max - 1;
                }
                half = min + ((max - min) / 2);
                if (doZero && (max > zero) && (zero > half))
                    values[curr++] = zero;
                if (doZero && (zero == half))
                    values[curr++] = half + 1;
                values[curr++] = half;
                if (doZero && (half > zero) && (zero > min))
                    values[curr++] = zero;
                if (doZero && (zero == min)) {
                    values[curr++] = min + 1;
                }
                values[curr++] = min;
                break;
            case Stream.PRESENTATION_PERCENT_VAR:
                values[curr++] = 100;
                values[curr++] = 50;
                if (doZero)
                    values[curr++] = 1;
                values[curr++] = 0;
                break;
            case Stream.PRESENTATION_ENUM:
                for (int i = 0; i <= max; ++i)
                    values[i] = max - i;
                break;
        }
    }

    private void setupLabels() {
        // Space space = interpreter.getSpace(spaceID);
        Stream stream = space.getStream(streamID);
        for (int i = 0; i <= rows; ++i) {
            String str;
            if (i != rows) {
                str = "  " + stream.presentDataSmall(values[i]);
            } else {
                str = "  " + space.getUnusedString();
            }
            labels[i].setText(str);
        }
    }

    private void addComponents(Container cont) {
        // Space space = interpreter.getSpace(spaceID);
        Stream stream = space.getStream(streamID);
        int presentation = stream.getPresentation();
        int paintStyle = stream.getPaintStyle();
        int max = stream.getMaxValue();

    /*
     * System.out.println(stream.getName() + "@" + space + ": " + "max = " +
     * max);
     */

        Container vertical = new Box(BoxLayout.Y_AXIS);

        switch (presentation) {
            case Stream.PRESENTATION_PLAIN:
            case Stream.PRESENTATION_MAX_VAR:
            case Stream.PRESENTATION_PERCENT:
            case Stream.PRESENTATION_PERCENT_VAR:
                rows = 3;
                break;
            case Stream.PRESENTATION_PLUS:
                rows = 4;
                break;
            case Stream.PRESENTATION_ENUM:
                rows = max + 1;
                break;
        }

        switch (paintStyle) {
            case Stream.PAINT_STYLE_ZERO:
                ++rows;
                break;
        }

        values = new int[rows];
        tileManagers = new TileManager[rows + 1];
        labels = new JLabel[rows + 1];

        setupValues();

        for (int i = 0; i <= rows; ++i) {
            JPanel panel = Factory.createEmptyPanel(2);
            panel.setLayout(new BorderLayout());

            tileManagers[i] = new TileManager(null, 1, true, false);
            tileManagers[i].setDrawFrame(true);
            tileManagers[i].setColumns(1);
            panel.add(tileManagers[i], BorderLayout.WEST);
            labels[i] = Factory.createLabel("",
                    (presentation == Stream.PRESENTATION_ENUM) ? JLabel.LEFT
                            : JLabel.RIGHT);
            panel.add(labels[i], BorderLayout.CENTER);

            vertical.add(panel);
        }
        setupLabels();

        cont.add(Factory.createTitlePanel(stream.getName(), vertical),
                BorderLayout.CENTER);
    }

    /**
     * Select the stream for this legend
     * @param streamID The stream's ID
     */
    public void selectStream(int streamID) {
        if (streamID == -1)
            return;

        this.streamID = streamID;
        if (tileManagers == null)
            return;

        populateContentPane();
        paintTileManagers();
    }

    private void populateContentPane() {
        Container cont = getContentPane();
        cont.removeAll();

        JLabel label = Factory.createLabel(space.getName(), JLabel.LEFT, true);
        cont.add(label, BorderLayout.NORTH);

        addComponents(cont);

        JPanel panel = Factory.createFlowPanel(FlowLayout.CENTER);
        JButton closeB = Factory.createButton("Close", true, new CloseBListener());
        panel.add(closeB);
        cont.add(panel, BorderLayout.SOUTH);

        pack();
    }

    private void setup(ClientInterpreter interpreter, ClientSpace space,
                       int streamID, TileManagerColors colorConfig) {
        setTitle("Legend");
        setResizable(false);

        this.interpreter = interpreter;
        interpreter.addEventListener(this);
        interpreter.addSpaceListener(this);
        // this.spaceID = spaceID;
        this.space = space;
        // this.space = interpreter.getSpace(spaceID);
        this.streamID = streamID;
        this.colorConfig = colorConfig;

        Container cont = getContentPane();
        cont.setLayout(new BorderLayout());

        populateContentPane();
    }

    public void destroy() {
        interpreter = null;
        space = null;
        tileManagers = null;
        colorConfig = null;
        super.destroy();
    }

    /**
     * Construct a new legend frame
     * @param owner The parent frame
     * @param interpreter The client interpreter
     * @param space The space
     * @param streamID The stream's ID
     * @param colorConfig The colour configuration to use
     */
    LegendFrame(AbstractFrame owner, ClientInterpreter interpreter,
                ClientSpace space, int streamID, TileManagerColors colorConfig) {
        super(owner, Position.POS_LEFT_COMPONENT, true);
        setup(interpreter, space, streamID, colorConfig);
    }

}
