/**
 * * $Id: ColorChooser.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.vis.utils;

import gcspy.utils.ColorDB;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Combo box to choose colors
 * @author Tony Printezis
 * @author <a href="http://www/cs/kent.ac.uk">Richard Jones</a>
 */
public class ColorChooser extends JPanel {

    private JComboBox comboBox;

    private JLabel label;

    private List<ColorChooserListener> listeners = new ArrayList<ColorChooserListener>();

    static private ColorDB colorDB = ColorDB.getColorDB();

    private class ComboBoxListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            int index = comboBox.getSelectedIndex();
            updateIndicator(index);
            callListeners(colorDB.getColor(index));
        }
    }

    /**
     * Add a listener for the color chooser
     * @param listener the listener
     */
    public void addListener(ColorChooserListener listener) {
        listeners.add(listener);
    }

    private void callListeners(Color color) {
        for (ColorChooserListener listener : listeners)
            listener.colorChosen(color);
    }

    /**
     * Set the colour in the combo box
     * @param c The colour
     */
    public void setColor(Color c) {
        // int index = findIndex(c);
        int index = colorDB.getIndex(c);
        comboBox.setSelectedIndex(index);
        updateIndicator(index);
    }

    private void updateIndicator(int index) {
        label.setBackground(colorDB.getColor(index));
    }

    /**
     * Get the colour from the combo box
     * @return the colour
     */
    public Color getColor() {
        int index = comboBox.getSelectedIndex();
        return colorDB.getColor(index);
    }

    /**
     * Enable/disable the combo box
     * @param enable True to enable
     */
    public void setEnable(boolean enable) {
        if (enable)
            Factory.enableComboBox(comboBox);
        else
            Factory.disableComboBox(comboBox);
    }

    /** Create a color chooser */
    public ColorChooser() {
        this(null, null, null);
    }

    /**
     * Create a color chooser
     * @param text The label to use on the chooser
     */
    public ColorChooser(String text) {
        this(text, null, null);
    }

    /**
     * Create a color chooser
     * @param c The colour initially selected
     */
    public ColorChooser(Color c) {
        this(null, c, null);
    }

    /**
     * Create a color chooser
     * @param listener A ColorChooserListener
     */
    public ColorChooser(ColorChooserListener listener) {
        this(null, null, listener);
    }

    /**
     * Create a color chooser with no label
     * @param c The colour initially selected
     * @param listener A ColorChooserListener
     */
    public ColorChooser(Color c, ColorChooserListener listener) {
        this(null, c, listener);
    }

    /**
     * Create a color chooser
     * @param text The label to use on the chooser
     * @param listener A ColorChooserListener
     */
    public ColorChooser(String text, ColorChooserListener listener) {
        this(text, null, listener);
    }

    /**
     * Create a color chooser
     * @param text The label to use on the chooser
     * @param c The colour initially selected
     * @param listener A ColorChooserListener
     */
    public ColorChooser(String text, Color c, ColorChooserListener listener) {
        setLayout(new FlowLayout());

        if (text != null) {
            add(Factory.createLabel(text + ": "));
        }

        label = Factory.createLabel("            ");
        label.setBorder(new LineBorder(colorDB.getColor("Black"), 1));
        label.setOpaque(true);
        add(label);
        add(Factory.createLabel("("));
        comboBox = Factory.createComboBox();
        comboBox.addActionListener(new ComboBoxListener());
        add(comboBox);
        add(Factory.createLabel(")"));

        for (int i = 0; i < colorDB.getLength(); ++i)
            comboBox.addItem(colorDB.getName(i));
        updateIndicator(colorDB.getIndex(c));

        if (listener != null)
            addListener(listener);
    }

}
