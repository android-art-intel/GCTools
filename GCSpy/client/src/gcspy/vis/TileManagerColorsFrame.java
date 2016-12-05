/**
 * * $Id: TileManagerColorsFrame.java 24 2005-06-17 09:37:56Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/


package gcspy.vis;

import gcspy.interpreter.Space;
import gcspy.vis.utils.AbstractFrame;
import gcspy.vis.utils.ColorChooser;
import gcspy.vis.utils.ColorChooserListener;
import gcspy.vis.utils.Factory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Frame to change the color configuration for the tile manager
 * @author Tony Printezis
 */
public class TileManagerColorsFrame extends AbstractFrame
        implements DisconnectListener {

    // private Space space;
    private LegendFrame legendFrame;

    private SpaceManager spaceManager;
    private TileManagerColors colorConfig;

    private ColorChooserListener minorRedrawListener =
            new MinorRedrawListener();
    private ColorChooserListener majorRedrawListener =
            new MajorRedrawListener();

    private ColorChooser streamCC;
    private ColorChooser selectedCC;
    private ColorChooser markerCC;
    private ColorChooser activeMarkerCC;
    private ColorChooser separatorCC;
    private ColorChooser linkCC;

    private JButton closeB;
    private JButton defaultsB;
    private boolean listenerEnabled = false;

    private int selectedStream = 0;

    private class DefaultsBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            colorConfig.revertToDefaults();
            updateFromConfig();
            spaceManager.redraw();
            legendFrame.selectStream(selectedStream);
        }
    }

    private class CloseBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            setVisible(false);
        }
    }

    private class MinorRedrawListener implements ColorChooserListener {
        public void colorChosen(Color c) {
            if (listenerEnabled) {
                updateConfig();
                spaceManager.mapImage();
            }
        }
    }

    private class MajorRedrawListener implements ColorChooserListener {
        public void colorChosen(Color c) {
            if (listenerEnabled) {
                updateConfig();
                spaceManager.redraw();
            }
        }
    }

    private class StreamColorListener implements ColorChooserListener {
        public void colorChosen(Color c) {
            if (listenerEnabled) {
                legendFrame.selectStream(selectedStream);
            }
        }
    }

    public void disconnect(boolean reconnecting) {
        if (reconnecting) {
            setVisible(false);
            destroy();
        }
    }

    /**
     * A stream is selected
     * @param id The stream's ID
     */
    public void selectStream(int id) {
        if (id != -1) {
            selectedStream = id;
            streamCC.setColor(colorConfig.getStreamColor(selectedStream));
        }
    }

    private void updateConfig() {
        colorConfig.setStreamColor(selectedStream, streamCC.getColor());
        colorConfig.setSelectedColor(selectedCC.getColor());
        colorConfig.setMarkerColor(markerCC.getColor());
        colorConfig.setActiveMarkerColor(activeMarkerCC.getColor());
        colorConfig.setSeparatorColor(separatorCC.getColor());
        colorConfig.setLinkColor(linkCC.getColor());
    }

    private void updateFromConfig() {
        listenerEnabled = false;
        streamCC.setColor(colorConfig.getStreamColor(selectedStream));
        selectedCC.setColor(colorConfig.getSelectedColor());
        markerCC.setColor(colorConfig.getMarkerColor());
        activeMarkerCC.setColor(colorConfig.getActiveMarkerColor());
        separatorCC.setColor(colorConfig.getSeparatorColor());
        linkCC.setColor(colorConfig.getLinkColor());
        listenerEnabled = true;
    }

    private void setup(SpaceManager spaceManager,
                       Space space,
                       TileManagerColors colorConfig,
                       LegendFrame legendFrame) {
        this.spaceManager = spaceManager;
        this.colorConfig = colorConfig;
        this.legendFrame = legendFrame;

        setTitle("GCspy: Colors");
        setResizable(false);

        JPanel panel;
        Container cont = getContentPane();
        cont.setLayout(new BorderLayout());

        JLabel label = Factory.createLabel(space.getFullName(), JLabel.LEFT, true);
        cont.add(label, BorderLayout.NORTH);

        Container vertical = new Box(BoxLayout.Y_AXIS);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        streamCC = new ColorChooser("Stream Color", majorRedrawListener);
        streamCC.addListener(new StreamColorListener());
        panel.add(streamCC);
        vertical.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        selectedCC = new ColorChooser("Selection Color", minorRedrawListener);
        panel.add(selectedCC);
        vertical.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        markerCC = new ColorChooser("Marker Color", minorRedrawListener);
        panel.add(markerCC);
        vertical.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        activeMarkerCC = new ColorChooser("Active Marker Color",
                minorRedrawListener);
        panel.add(activeMarkerCC);
        vertical.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        separatorCC = new ColorChooser("Separator Color", majorRedrawListener);
        panel.add(separatorCC);
        vertical.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        linkCC = new ColorChooser("Link Color", majorRedrawListener);
        panel.add(linkCC);
        vertical.add(panel);

        cont.add(Factory.createTitlePanel("Colors", vertical),
                BorderLayout.CENTER);

        panel = Factory.createFlowPanel(FlowLayout.CENTER);
        defaultsB = Factory.createButton("Defaults", true,
                new DefaultsBListener());
        panel.add(defaultsB);
        closeB = Factory.createButton(" Close ", true,
                new CloseBListener());
        panel.add(closeB);
        cont.add(panel, BorderLayout.SOUTH);

        pack();

        updateFromConfig();
        listenerEnabled = true;
    }

    public void destroy() {
        legendFrame = null;
        spaceManager = null;
        colorConfig = null;
        super.destroy();
    }

    /**
     * Create a new frame for choosing tile colours
     * @param owner The parent frame
     * @param space The space
     * @param spaceManager The space manager
     * @param colorConfig Its tile colour configuration
     * @param legendFrame The legend frame
     */
    TileManagerColorsFrame(AbstractFrame owner,
                           Space space,
                           SpaceManager spaceManager,
                           TileManagerColors colorConfig,
                           LegendFrame legendFrame) {
        super(owner, Position.POS_RIGHT_COMPONENT, true);
        setup(spaceManager, space, colorConfig, legendFrame);
    }

}
