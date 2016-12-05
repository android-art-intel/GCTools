/**
 * * $Id: HistoryConfigDialog.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.vis.plugins.history;

import gcspy.interpreter.Interpreter;
import gcspy.interpreter.Space;
import gcspy.utils.ColorDB;
import gcspy.vis.TileManagerColors;
import gcspy.vis.utils.ColorChooser;
import gcspy.vis.utils.Factory;
import gcspy.vis.utils.OKCancelDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog for the history graph settings
 *
 * @author Tony Printezis
 */
class HistoryConfigDialog extends OKCancelDialog {

    private HistoryConfig config;

    private JTextField colsTF;
    private JTextField rowsTF;
    private JTextField tileSizeTF;
    private JTextField imageSizeTF;
    private JComboBox streamCB;
    private JCheckBox horSepCB;
    private JComboBox horSepEventCB;
    private JCheckBox verSepCB;
    private JTextField verSepPeriodTF;
    private JCheckBox verSepLegendCB;
    private JTextField verSepLegendPeriodTF;
    private JTextField verSepLegendStringPostTF;
    private JCheckBox spaceSepCB;
    private ColorChooser spaceSepCC;
    private ColorChooser unusedCC;
    private ColorChooser zeroCC;
    private ColorChooser lowCC;
    private ColorChooser hiCC;
    private ColorChooser horSepCC;
    private ColorChooser verSepCC;
    private ColorChooser bgCC;
    private ColorChooser borderCC;
    private ColorChooser titleCC;
    private JTextField fontSizeTF;
    private JTextField borderSizeTF;
    private JTextField titlePreTF;
    private JCheckBox titleCB;

    private TileManagerColors tmColors;

    private JButton validateB;
    private JButton defaultsB;

    private int noStreamIndex;

    private class DefaultsBListener implements ActionListener {
        private Component component;

        public void actionPerformed(ActionEvent event) {
            config.revertToDefaults(component, tmColors);
            updateFromConfig();
        }

        DefaultsBListener(Component component) {
            this.component = component;
        }
    }

    private class ValidateBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            updateConfig();
            updateFromConfig();
        }
    }

    private class VerSepPanelListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            updateVerSepPanel();
        }
    }

    private class HorSepPanelListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            updateHorSepPanel();
        }
    }

    private class SpaceSepPanelListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            updateSpaceSepPanel();
        }
    }

    private class TitlePanelListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            updateTitlePanel();
        }
    }

    private class StreamCBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (hiCC != null) {
                // int stream = streamCB.getSelectedIndex();
                // Color color = tmColors.getStreamColor(stream);
                Color color = getCurrentStreamColor();
                hiCC.setColor(color);
            }
        }
    }

    private Color getCurrentStreamColor() {
        int stream = streamCB.getSelectedIndex();
        Color color;
        if (stream == noStreamIndex) {
            ColorDB colorDB = ColorDB.getColorDB();
            color = colorDB.getColor("Black");
        } else
            color = tmColors.getStreamColor(stream);
        return color;
    }

    /** The OK button has been clicked */
    protected void okClicked() {
        updateConfig();
    }

    /** The cancel button has been clicked */
    protected void cancelClicked() {
    }

    private void updateVerSepPanel() {
        boolean selected = verSepCB.isSelected();
        if (selected) {
            Factory.enableTextField(verSepPeriodTF);
            verSepCC.setEnable(selected);
            verSepLegendCB.setEnabled(selected);
            if (verSepLegendCB.isSelected()) {
                Factory.enableTextField(verSepLegendPeriodTF);
                Factory.enableTextField(verSepLegendStringPostTF);
            } else {
                Factory.disableTextField(verSepLegendPeriodTF);
                Factory.disableTextField(verSepLegendStringPostTF);
            }
        } else {
            Factory.disableTextField(verSepPeriodTF);
            verSepCC.setEnable(selected);
            verSepLegendCB.setEnabled(selected);
            Factory.disableTextField(verSepLegendPeriodTF);
            Factory.disableTextField(verSepLegendStringPostTF);
        }
    }

    private void updateHorSepPanel() {
        boolean selected = horSepCB.isSelected();
        if (selected) {
            Factory.enableComboBox(horSepEventCB);
            horSepCC.setEnable(selected);
        } else {
            Factory.disableComboBox(horSepEventCB);
            horSepCC.setEnable(selected);
        }
    }

    private void updateSpaceSepPanel() {
        boolean selected = spaceSepCB.isSelected();
        if (selected) {
            spaceSepCC.setEnable(selected);
        } else {
            spaceSepCC.setEnable(selected);
        }
    }

    private void updateTitlePanel() {
        boolean selected = titleCB.isSelected();
        if (selected) {
            titleCC.setEnable(selected);
            Factory.enableTextField(titlePreTF);
        } else {
            titleCC.setEnable(selected);
            Factory.disableTextField(titlePreTF);
        }
    }

    private void updateFromConfig() {
        int width = config.calcWidth();
        int height = config.calcHeight();
        String imageSize = width + "x" + height;

        colsTF.setText("" + config.getCols());
        rowsTF.setText("" + config.getRows());
        tileSizeTF.setText("" + config.getTileSize());
        imageSizeTF.setText(imageSize);
        int stream = config.getStream();
        if (stream == -1)
            streamCB.setSelectedIndex(noStreamIndex);
        else
            streamCB.setSelectedIndex(stream);

        horSepEventCB.setSelectedIndex(config.getHorSepEvent());
        if (config.horSep())
            Factory.selectCheckBox(horSepCB);
        else
            Factory.deselectCheckBox(horSepCB);
        updateHorSepPanel();

        if (config.verSep())
            Factory.selectCheckBox(verSepCB);
        else
            Factory.deselectCheckBox(verSepCB);
        verSepPeriodTF.setText("" + config.getVerSepPeriod());
        if (config.getVerSepLegend())
            Factory.selectCheckBox(verSepLegendCB);
        else
            Factory.deselectCheckBox(verSepLegendCB);
        verSepLegendPeriodTF.setText("" + config.getVerSepLegendPeriod());
        verSepLegendStringPostTF.setText("" + config.getVerSepLegendStringPost());
        updateVerSepPanel();

        if (config.getSpaceSep())
            Factory.selectCheckBox(spaceSepCB);
        else
            Factory.deselectCheckBox(spaceSepCB);
        spaceSepCC.setColor(config.getSpaceSepColor());
        updateSpaceSepPanel();

        unusedCC.setColor(config.getUnusedColor());
        zeroCC.setColor(config.getZeroColor());
        lowCC.setColor(config.getLowColor());
        hiCC.setColor(config.getHiColor());
        horSepCC.setColor(config.getHorSepColor());
        verSepCC.setColor(config.getVerSepColor());
        bgCC.setColor(config.getBGColor());

        if (config.getTitle())
            Factory.selectCheckBox(titleCB);
        else
            Factory.deselectCheckBox(titleCB);
        titleCC.setColor(config.getTitleColor());
        titlePreTF.setText(config.getTitlePre());
        updateTitlePanel();

        borderSizeTF.setText("" + config.getBorderSize());
        borderCC.setColor(config.getBorderColor());

        fontSizeTF.setText("" + config.getFontSize());
    }

    private void updateConfig() {
        int stream = streamCB.getSelectedIndex();
        if (stream == noStreamIndex)
            stream = -1;
        config = new HistoryConfig(this, config.getCols(),
                HistoryConfig.parseRows(rowsTF.getText()),
                HistoryConfig.parseTileSize(tileSizeTF.getText()), stream,
                horSepCB.isSelected(), horSepEventCB.getSelectedIndex(),
                verSepCB.isSelected(),
                HistoryConfig.parseVerSepPeriod(verSepPeriodTF.getText()),
                verSepLegendCB.isSelected(),
                HistoryConfig.parseVerSepLegendPeriod(verSepLegendPeriodTF.getText()),
                verSepLegendStringPostTF.getText(), spaceSepCB.isSelected(),
                spaceSepCC.getColor(), unusedCC.getColor(), zeroCC.getColor(),
                lowCC.getColor(), hiCC.getColor(), horSepCC.getColor(),
                verSepCC.getColor(), bgCC.getColor(), titleCB.isSelected(),
                titlePreTF.getText(), titleCC.getColor(),
                HistoryConfig.parseBorderSize(borderSizeTF.getText()), borderCC.getColor(),
                HistoryConfig.parseFontSize(fontSizeTF.getText()));
    }

    /**
     * Add Default and Validate buttons to a panel
     * @param panel the panel to add the buttons to
     */
    protected void addButtons(JPanel panel) {
        defaultsB = Factory.createButton("Defaults", true, new DefaultsBListener(
                this));
        panel.add(defaultsB);
        validateB = Factory.createButton("Validate", true, new ValidateBListener());
        panel.add(validateB);
    }

    private void setup(HistoryConfig config, Interpreter interpreter,
                       Space space, TileManagerColors tmColors) {
        setup();

        this.config = config;
        this.tmColors = tmColors;
        this.noStreamIndex = space.getStreamNum();

        JPanel panel;
        Container cont = getContentPane();

        Container generalPanel = new Box(BoxLayout.Y_AXIS);
        Container horSepPanel = new Box(BoxLayout.Y_AXIS);
        Container colorPanel = new Box(BoxLayout.Y_AXIS);
        Container verSepPanel = new Box(BoxLayout.Y_AXIS);
        Container spaceSepPanel = new Box(BoxLayout.Y_AXIS);
        Container titlePanel = new Box(BoxLayout.Y_AXIS);
        Container borderPanel = new Box(BoxLayout.Y_AXIS);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        panel.add(Factory.createLabel("Columns:"));
        colsTF = Factory.createTextField("", 6, false);
        panel.add(colsTF);
        generalPanel.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        panel.add(Factory.createLabel("Max Rows:"));
        rowsTF = Factory.createTextField("", 6, true);
        panel.add(rowsTF);
        generalPanel.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        panel.add(Factory.createLabel("Tile Size:"));
        tileSizeTF = Factory.createTextField("", 3, true);
        panel.add(tileSizeTF);
        generalPanel.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        panel.add(Factory.createLabel("Image Size:"));
        imageSizeTF = Factory.createTextField("", 10, false);
        panel.add(imageSizeTF);
        generalPanel.add(panel);

        hiCC = null;
        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        panel.add(Factory.createLabel("Stream:"));
        streamCB = Factory.createComboBox(new StreamCBListener());
        for (int i = 0; i < space.getStreamNum(); ++i)
            streamCB.addItem(space.getStream(i).getName());
        streamCB.addItem("<none>");
        panel.add(streamCB);
        generalPanel.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        panel.add(Factory.createLabel("Enabled:"));
        horSepCB = Factory.createCheckBox(null, false, new HorSepPanelListener());
        panel.add(horSepCB);
        horSepPanel.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        panel.add(Factory.createLabel("Triger Event:"));
        horSepEventCB = Factory.createComboBox();
        for (int i = 0; i < interpreter.getEvents().getNum(); ++i)
            horSepEventCB.addItem(interpreter.getEvents().getName(i));
        panel.add(horSepEventCB);
        horSepPanel.add(panel);

        VerSepPanelListener listener = new VerSepPanelListener();
        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        panel.add(Factory.createLabel("Enabled:"));
        verSepCB = Factory.createCheckBox(null, false, listener);
        panel.add(verSepCB);
        verSepPanel.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        panel.add(Factory.createLabel("Period:"));
        verSepPeriodTF = Factory.createTextField("", 4, true);
        panel.add(verSepPeriodTF);
        verSepPanel.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        verSepCC = new ColorChooser("Color");
        panel.add(verSepCC);
        verSepPanel.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        panel.add(Factory.createLabel("Legend:"));
        verSepLegendCB = Factory.createCheckBox(null, false, listener);
        panel.add(verSepLegendCB);
        verSepPanel.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        panel.add(Factory.createLabel("Legend Period:"));
        verSepLegendPeriodTF = Factory.createTextField("", 6, true);
        panel.add(verSepLegendPeriodTF);
        verSepPanel.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        panel.add(Factory.createLabel("Legend Postfix:"));
        verSepLegendStringPostTF = Factory.createTextField("", 6, true);
        panel.add(verSepLegendStringPostTF);
        verSepPanel.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        panel.add(Factory.createLabel("Enabled:"));
        spaceSepCB = Factory.createCheckBox(null, false,
                new SpaceSepPanelListener());
        panel.add(spaceSepCB);
        spaceSepPanel.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        spaceSepCC = new ColorChooser("Color");
        panel.add(spaceSepCC);
        spaceSepPanel.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        zeroCC = new ColorChooser("Zero Color");
        panel.add(zeroCC);
        colorPanel.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        lowCC = new ColorChooser("Low Color");
        panel.add(lowCC);
        colorPanel.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        hiCC = new ColorChooser("High Color");
        panel.add(hiCC);
        colorPanel.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        unusedCC = new ColorChooser("Unused Color");
        panel.add(unusedCC);
        colorPanel.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        bgCC = new ColorChooser("Background Color");
        panel.add(bgCC);
        colorPanel.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        horSepCC = new ColorChooser("Color");
        panel.add(horSepCC);
        horSepPanel.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        panel.add(Factory.createLabel("Font Size:"));
        fontSizeTF = Factory.createTextField("", 3, true);
        panel.add(fontSizeTF);
        generalPanel.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        panel.add(Factory.createLabel("Enabled:"));
        titleCB = Factory.createCheckBox(null, false, new TitlePanelListener());
        panel.add(titleCB);
        titlePanel.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        titleCC = new ColorChooser("Color");
        panel.add(titleCC);
        titlePanel.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        panel.add(Factory.createLabel("Title Prefix:"));
        titlePreTF = Factory.createTextField("", 16, true);
        panel.add(titlePreTF);
        titlePanel.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        panel.add(Factory.createLabel("Border Size:"));
        borderSizeTF = Factory.createTextField("", 3, true);
        panel.add(borderSizeTF);
        borderPanel.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        borderCC = new ColorChooser("Color");
        panel.add(borderCC);
        borderPanel.add(panel);

        Container left = new Box(BoxLayout.Y_AXIS);
        Container right = new Box(BoxLayout.Y_AXIS);

        left.add(Factory.createTitlePanel("General", generalPanel));
        left.add(Factory.createTitlePanel("Vertical Separators", verSepPanel));
        left.add(Factory.createTitlePanel("Title", titlePanel));
        JPanel outerLeft = Factory.createBorderPanel();
        outerLeft.add(left, BorderLayout.NORTH);

        right.add(Factory.createTitlePanel("Colors", colorPanel));
        right.add(Factory.createTitlePanel("Horizontal Separators", horSepPanel));
        right.add(Factory.createTitlePanel("Space Separators", spaceSepPanel));
        right.add(Factory.createTitlePanel("Border", borderPanel));
        JPanel outerRight = Factory.createBorderPanel();
        outerRight.add(right, BorderLayout.NORTH);

        JPanel grid = Factory.createGridPanel(1, 2);
        grid.add(outerLeft);
        grid.add(outerRight);

        panel = Factory.createTitlePanel("History Graph Configuration", grid);
        cont.add(panel, BorderLayout.CENTER);

        pack();

        updateFromConfig();
    }

    /**
     * Get the configuration for the history view
     * @return the configuration
     */
    HistoryConfig getConfig() {
        return config;
    }

    /**
     * A new configuration dialog for the history view
     * @param owner The parent frame
     * @param config The history configuration
     * @param interpreter The space's interpreter
     * @param space The space
     * @param tmColors The tile colour configuration
     */
    HistoryConfigDialog(JFrame owner, HistoryConfig config,
                        Interpreter interpreter, Space space, TileManagerColors tmColors) {
        super(owner);
        setup(config, interpreter, space, tmColors);
        placeIt();
    }

}
