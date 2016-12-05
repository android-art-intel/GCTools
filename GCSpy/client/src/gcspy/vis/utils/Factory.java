/**
 * * $Id: Factory.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/


package gcspy.vis.utils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Factory for GUI components
 * @author Tony Printezis
 * @author <a href="http://www/cs/kent.ac.uk">Richard Jones</a>
 */
public class Factory {

    /** *** Fonts **** */

    static private Font BORDER_TITLE_FONT = new Font("SansSerif", Font.PLAIN, 12);

    static private Font TEXT_AREA_FONT = new Font("SansSerif", Font.BOLD, 10);

    static private Font TEXT_AREA_MONO_FONT = new Font("DialogInput", Font.PLAIN,
            11);

    static private Font TEXT_FIELD_FONT = new Font("SansSerif", Font.BOLD, 10);

    static private Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 12);

    static private Font DISABLED_LABEL_FONT = new Font("SansSerif", Font.ITALIC,
            12);

    static private Font BOLD_LABEL_FONT = new Font("SansSerif", Font.BOLD, 12);

    static private Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 10);

    static private Font BUTTON_SMALL_FONT = new Font("SansSerif", Font.BOLD, 9);

    static private Font COMBO_BOX_FONT = new Font("SansSerif", Font.BOLD, 10);

    static private Font MENU_FONT = new Font("SansSerif", Font.BOLD, 12);

    static private Font MENU_ITEM_FONT = new Font("SansSerif", Font.PLAIN, 12);

    static private Font TABLE_FONT = new Font("SansSerif", Font.PLAIN, 12);

    static private Color TEXT_AREA_BG = new Color(220, 220, 220);

    /** *** Buttons **** */

    /**
     * Create a JButton
     * @param text The label to use
     * @return a new JButton
     */
    static public JButton createButton(String text) {
        return createButton(text, true);
    }

    /**
     * Create a JButton
     * @param text The label to use
     * @param al An ActionListener for the button
     * @return a new JButton
     */
    static public JButton createButton(String text, ActionListener al) {
        return createButton(text, true, al);
    }

    /**
     * Create a JButton
     * @param text The label to use
     * @param enabled Is the button enabled?
     * @return a new JButton
     */
    static public JButton createButton(String text, boolean enabled) {
        return createButton(text, enabled, null);
    }

    /**
     * Create a JButton
     * @param text The label to use
     * @param enabled Is the button enabled?
     * @param al An ActionListener for the button
     * @return a new JButton
     */
    static public JButton createButton(String text, boolean enabled,
                                       ActionListener al) {
        return createButton(text, enabled, false, al);
    }

    /**
     * Create a JButton
     * @param text The label to use
     * @param enabled Is the button enabled?
     * @param small Is the button's font size small?
     * @param al An ActionListener for the button
     * @return a new JButton
     */
    static public JButton createButton(String text, boolean enabled,
                                       boolean small, ActionListener al) {
        return createButton(text, enabled, small, null, al);
    }

    /**
     * Create a JButton
     * @param text The label to use
     * @param enabled Is the button enabled?
     * @param toolTipText tool-tip text for the button
     * @param al An ActionListener for the button
     * @return a new JButton
     */
    static public JButton createButton(String text, boolean enabled,
                                       String toolTipText, ActionListener al) {
        return createButton(text, enabled, false, toolTipText, al);
    }

    /**
     * Create a JButton
     * @param text The label to use
     * @param enabled Is the button enabled?
     * @param small Is the button's font size small?
     * @param toolTipText tool-tip text for the button
     * @param al An ActionListener for the button
     * @return a new JButton
     */
    static public JButton createButton(String text, boolean enabled,
                                       boolean small, String toolTipText, ActionListener al) {
        JButton button = new JButton(text);
        if (enabled)
            enableButton(button);
        else
            disableButton(button);
        if (small) {
            Insets margin = (Insets) button.getMargin().clone();
            margin.left = margin.top;
            margin.right = margin.top;
            margin.top = margin.top / 2;
            margin.bottom = margin.top;
            button.setMargin(margin);
            button.setFont(BUTTON_SMALL_FONT);
        } else
            button.setFont(BUTTON_FONT);
        if (toolTipText != null)
            button.setToolTipText(toolTipText);
        if (al != null)
            button.addActionListener(al);
        return button;
    }

    /**
     * Enable a JButton
     * @param button the button
     */
    static public void enableButton(JButton button) {
        button.setEnabled(true);
    }

    /**
     * Disable a JButton
     * @param button the button
     */
    static public void disableButton(JButton button) {
        button.setEnabled(false);
    }

    /**
     * Create a JButton with an icon
     * @param icon The icon
     * @return a new JButton
     */
    static public JButton createIconButton(Icon icon) {
        return createIconButton(icon, true, null);
    }

    /**
     * Create a JButton with an icon
     * @param icon The icon
     * @param enable Is the button enabled?
     * @param al ActionListener for the button
     * @return a new JButton
     */
    static public JButton createIconButton(Icon icon, boolean enable,
                                           ActionListener al) {
        return createIconButton(icon, enable, null, al);
    }

    /**
     * Create a JButton with an icon
     * @param icon The icon
     * @param enable Is the button enabled?
     * @param toolTipText Tool-tip text for the button
     * @param al ActionListener for the button
     * @return a new JButton
     */
    static public JButton createIconButton(Icon icon, boolean enable,
                                           String toolTipText, ActionListener al) {
        JButton button = new JButton(icon);

        Insets margin = (Insets) button.getMargin().clone();
        margin.left = margin.top;
        margin.right = margin.top;
        margin.bottom = margin.top;
        button.setMargin(margin);
        if (enable)
            enableButton(button);
        else
            disableButton(button);
        if (toolTipText != null)
            button.setToolTipText(toolTipText);
        if (al != null)
            button.addActionListener(al);

        return button;
    }

    /** *** CheckBoxes **** */

    /**
     * Create a new JCheckBoxMenuItem
     * @param text The text for the menu item
     * @return a new JCheckBoxMenuItem
     */
    static public JCheckBoxMenuItem createCheckBoxMenuItem(String text) {
        return createCheckBoxMenuItem(text, true);
    }

    /**
     * Create a new JCheckBoxMenuItem
     * @param text The text for the menu item
     * @param state The state of the check-box
     * @return a new JCheckBoxMenuItem
     */
    static public JCheckBoxMenuItem createCheckBoxMenuItem(String text,
                                                           boolean state) {
        return createCheckBoxMenuItem(text, state, null);
    }

    /**
     * Create a new JCheckBoxMenuItem
     * @param text The text for the menu item
     * @param state The state of the check-box
     * @param al An ActionListener for the item
     * @return a new JCheckBoxMenuItem
     */
    static public JCheckBoxMenuItem createCheckBoxMenuItem(String text,
                                                           boolean state, ActionListener al) {
        JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(text);
        menuItem.setFont(MENU_ITEM_FONT);
        menuItem.setState(state);
        if (al != null)
            menuItem.addActionListener(al);
        return menuItem;
    }

    /**
     * Create a new labelled JCheckBox
     * @return return a new JCheckBox
     */
    static public JCheckBox createCheckBox() {
        return createCheckBox(null, true, null);
    }

    /**
     * Create a new labelled JCheckBox
     * @param selected Is the box selected?
     * @return return a new JCheckBox
     */
    static public JCheckBox createCheckBox(boolean selected) {
        return createCheckBox(null, selected, null);
    }

    /**
     * Create a new labelled JCheckBox
     * @param text The text to label the check-box
     * @return return a new JCheckBox
     */
    static public JCheckBox createCheckBox(String text) {
        return createCheckBox(text, true, null);
    }

    /**
     * Create a new labelled JCheckBox
     * @param text The text to label the check-box
     * @param selected Is the box selected?
     * @return return a new JCheckBox
     */
    static public JCheckBox createCheckBox(String text, boolean selected) {
        return createCheckBox(text, selected, null);
    }

    /**
     * Create a new labelled JCheckBox
     * @param text The text to label the check-box
     * @param selected Is the box selected?
     * @param al An ActionListener for the check-box
     * @return return a new JCheckBox
     */
    static public JCheckBox createCheckBox(String text, boolean selected,
                                           ActionListener al) {
        return createCheckBox(text, true, selected, al);
    }

    /**
     * Create a new labelled JCheckBox
     * @param text The text to label the check-box
     * @param enabled Is the check-box enabled?
     * @param selected Is the box selected?
     * @param al An ActionListener for the check-box
     * @return return a new JCheckBox
     */
    static public JCheckBox createCheckBox(String text, boolean enabled,
                                           boolean selected, ActionListener al) {
        JCheckBox checkBox;
        if (text != null)
            checkBox = new JCheckBox(text);
        else
            checkBox = new JCheckBox();
        if (enabled)
            enableCheckBox(checkBox);
        else
            disableCheckBox(checkBox);
        checkBox.setSelected(selected);
        if (al != null)
            checkBox.addActionListener(al);
        return checkBox;
    }

    /**
     * Make a check box selected
     * @param checkBox The check box
     */
    static public void selectCheckBox(JCheckBox checkBox) {
        checkBox.setSelected(true);
    }

    /**
     * Make a check box unselected
     * @param checkBox The check box
     */
    static public void deselectCheckBox(JCheckBox checkBox) {
        checkBox.setSelected(false);
    }

    /**
     * Enable a check box
     * @param checkBox The check box
     */
    static public void enableCheckBox(JCheckBox checkBox) {
        checkBox.setEnabled(true);
    }

    /**
     * Disable a check box
     * @param checkBox The check box
     */
    static public void disableCheckBox(JCheckBox checkBox) {
        checkBox.setEnabled(false);
    }

    /** *** ComboBoxes **** */

    /**
     * Create a new combo box
     * @return the combo box
     */
    static public JComboBox createComboBox() {
        return createComboBox(null);
    }

    /**
     * Create a new combo box
     * @param al An ActionListener
     * @return the combo box
     */
    static public JComboBox createComboBox(ActionListener al) {
        JComboBox comboBox = new JComboBox();
        comboBox.setFont(COMBO_BOX_FONT);
        if (al != null)
            comboBox.addActionListener(al);
        return comboBox;
    }

    /**
     * Enable a combo box
     * @param comboBox the combo box
     */
    static public void enableComboBox(JComboBox comboBox) {
        comboBox.setEnabled(true);
    }

    /**
     * Disable a combo box
     * @param comboBox the combo box
     */
    static public void disableComboBox(JComboBox comboBox) {
        comboBox.setEnabled(false);
    }

    /** *** Labels **** */

    /**
     * Create a JLabel
     * @param text Text for the label
     * @param alignment Horizontal alignment
     * @return a new JLabel
     */
    static public JLabel createLabel(String text, int alignment) {
        return createLabel(text, alignment, false);
    }

    /**
     * Create a JLabel
     * @param text Text for the label
     * @param alignment Horizontal alignment
     * @param bold Should the font be bold?
     * @return a new JLabel
     */
    static public JLabel createLabel(String text, int alignment, boolean bold) {
        JLabel label = new JLabel(text, alignment);
        if (bold)
            label.setFont(BOLD_LABEL_FONT);
        else
            label.setFont(LABEL_FONT);
        return label;
    }

    /**
     * Create a JLabel
     * @param text Text for the label
     * @return a new JLabel
     */
    static public JLabel createLabel(String text) {
        return createLabel(text, true);
    }

    /**
     * Create a JLabel
     * @param text Text for the label
     * @param enabled True if the label is enabled
     * @return a new JLabel
     */
    static public JLabel createLabel(String text, boolean enabled) {
        JLabel label = new JLabel(text);
        if (enabled)
            enableLabel(label);
        else
            disableLabel(label);
        return label;
    }


    /**
     * Enable a JLabel
     * @param label the JLabel
     */
    static public void enableLabel(JLabel label) {
        label.setFont(LABEL_FONT);
    }

    /**
     * Disable a JLabel
     * @param label the JLabel
     */
    static public void disableLabel(JLabel label) {
        label.setFont(DISABLED_LABEL_FONT);
    }

    /** *** Menus **** */

    /**
     * Create a new JMenu
     * @param title The text for the menu
     * @return The new JMenu
     */
    static public JMenu createMenu(String title) {
        JMenu menu = new JMenu(title);
        menu.setFont(MENU_FONT);
        return menu;
    }

    /**
     * Create a new JMenuItem
     * @param text The text for the menu item
     * @return The new JMenuItem
     */
    static public JMenuItem createMenuItem(String text) {
        return createMenuItem(text, null);
    }

    /**
     * Create a new JMenuItem
     * @param text The text for the menu item
     * @param al An ActionListener for the item
     * @return The new JMenuItem
     */
    static public JMenuItem createMenuItem(String text, ActionListener al) {
        return createMenuItem(text, true, al);
    }

    /**
     * Create a new JMenuItem
     * @param text The text for the menu item
     * @param enable Is the item enabled?
     * @param al An ActionListener for the item
     * @return The new JMenuItem
     */
    static public JMenuItem createMenuItem(String text, boolean enable,
                                           ActionListener al) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(MENU_ITEM_FONT);
        if (al != null)
            menuItem.addActionListener(al);
        if (enable)
            enableMenuItem(menuItem);
        else
            disableMenuItem(menuItem);
        return menuItem;
    }


    /**
     * Enable a JMenuItem
     * @param menuItem the JMenuItem
     */
    static public void enableMenuItem(JMenuItem menuItem) {
        menuItem.setEnabled(true);
    }

    /**
     * Disable a JMenuItem
     * @param menuItem the JMenuItem
     */
    static public void disableMenuItem(JMenuItem menuItem) {
        menuItem.setEnabled(false);
    }

    /** *** Panels **** */

    /**
     * Create an empty panel
     * @param width The width of the panel
     * @param height The height of the panel
     * [Why not use a box Box? ]
     */
    static public JPanel createEmptyPanel(int width, int height) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(height, width, 0, 0));
        return panel;
    }

  /* Panel with Empty Border */

    /**
     * Create an empty panel with a border
     * @param border the width of the border
     * @return the panel
     */
    static public JPanel createEmptyPanel(int border) {
        return createEmptyPanel(border, null);
    }

    /**
     * Place a component in a new panel with a border
     * @param border the width of the border
     * @param comp the component
     * @return the panel
     */
    static public JPanel createEmptyPanel(int border, Component comp) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(border, border, border,
                border));
        if (comp != null)
            panel.add(comp);
        return panel;
    }

  /* Panel with Title Border */

    /**
     * Create a new panel with a title
     * @param title the title
     * @return the new panel
     */
    static public JPanel createTitlePanel(String title) {
        return createTitlePanel(title, null);
    }

    /**
     * Place a component in a panel with a title
     * @param title The title
     * @param comp The component
     * @return the new panel
     */
    static public JPanel createTitlePanel(String title, Component comp) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(createTitledBorder(title));
        if (comp != null)
            panel.add(comp, BorderLayout.CENTER);
        return panel;
    }

  /* Panel with Border Layout */

    /**
     * Create a new panel with a border layout
     * @return a new panel with a border layout
     */
    static public JPanel createBorderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        return panel;
    }

  /* Panel with Flow Layout */

    /**
     * Create a new panel with a flow layout
     * @param align The alignment
     * @return the new panel
     */
    static public JPanel createFlowPanel(int align) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(align));
        return panel;
    }

  /* Panel with Grid Layout */

    /**
     * Create a new panel with a title, using grid layout
     * @param rows The number of rows
     * @param columns The number of columns
     * @return the new panel
     */
    static public JPanel createGridPanel(int rows, int columns) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(rows, columns));
        return panel;
    }

    /**
     * Create a new panel with a title, using grid layout
     * @param rows The number of rows
     * @param columns The number of columns
     * @param title the title of the panel
     * @return the new panel
     */
    static public JPanel createGridPanel(int rows, int columns, String title) {
        JPanel panel = createGridPanel(rows, columns);
        if (title != null)
            panel.setBorder(createTitledBorder(title));
        else
            panel.setBorder(createPlainBorder());
        return panel;
    }

    /** *** Tables **** */

    /**
     * Create a new table
     * @param model The table's model
     * @return the new table
     */
    static public JTable createTable(TableModel model) {
        return createTable(model, false, true);
    }

    /**
     * Create a new table
     * @param model The table's model
     * @param resizable Treu if the user can resize/reorder columns?
     * @param auto True if columns are automatically created from the model
     * @return the new table
     */
    static public JTable createTable(TableModel model, boolean resizable, boolean auto) {
        JTable table = new JTable(model);
        table.setAutoCreateColumnsFromModel(auto);

        table.setFont(TABLE_FONT);
        table.setRowSelectionAllowed(false);
        table.setColumnSelectionAllowed(false);
        table.setBackground(TEXT_AREA_BG);

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(resizable);
        header.setResizingAllowed(resizable);

        return table;
    }

    /**
     * Set the width of a column of a table
     * @param table The table
     * @param col The column number
     * @param width The new width
     */
    static public void setColumnWidth(JTable table, int col, int width) {
        TableColumnModel columnModel = table.getColumnModel();
        TableColumn column = columnModel.getColumn(col);
        column.setPreferredWidth(width);
    }


    /** *** Text Areas **** */

    static public JTextArea createTextArea(int rows, int columns, boolean editable) {
        return createTextArea(rows, columns, editable, false);
    }

    /**
     * Create a new text area
     * @param rows The number of rows
     * @param columns The number of columns
     * @param editable True if the text area is editable
     * @param monospaced True if the font is monospaced
     * @return a new text area
     */
    static public JTextArea createTextArea(int rows, int columns,
                                           boolean editable, boolean monospaced) {
        JTextArea textArea = new JTextArea(rows, columns);
        textArea.setEditable(editable);
        if (monospaced)
            textArea.setFont(TEXT_AREA_MONO_FONT);
        else
            textArea.setFont(TEXT_AREA_FONT);
        textArea.setBackground(TEXT_AREA_BG);

        return textArea;
    }

    /**
     * Enable a text area
     * @param area The test area
     */
    static public void enableTextArea(JTextArea area) {
        area.setEnabled(true);
    }

    /**
     * Disable a text area
     * @param area The test area
     */
    static public void disableTextArea(JTextArea area) {
        area.setEnabled(false);
    }

    /** *** Text Fields **** */

    /**
     * Create a new text field
     * @param columns The columns in the field
     * @param editable True if the field is editable
     * @return a new text field
     */
    static public JTextField createTextField(int columns, boolean editable) {
        return createTextField(null, columns, editable);
    }

    /**
     * Create a new text field
     * @param text The text to place in the field
     * @param columns The columns in the field
     * @param editable True if the field is editable
     * @return a new text field
     */
    static public JTextField createTextField(String text, int columns,
                                             boolean editable) {
        JTextField textField = new JTextField(columns);
        textField.setEditable(editable);
        textField.setFont(TEXT_FIELD_FONT);
        if (text != null)
            textField.setText(text);
        textField.setBackground(TEXT_AREA_BG);
        return textField;
    }

    /**
     * Enable a text field
     * @param textField The test field
     */
    static public void enableTextField(JTextField textField) {
        textField.setEnabled(true);
    }

    /**
     * Disable a text field
     * @param textField The test field
     */
    static public void disableTextField(JTextField textField) {
        textField.setEnabled(false);
    }

    /** *** Titled Borders **** */

    /**
     * Create a new plain border
     * @return a new plain area
     */
    private static TitledBorder createPlainBorder() {
        return new TitledBorder((String) null);
    }

    /**
     * Create a new titled border
     * @param title The title to use
     * @return a new titled area
     */
    static public TitledBorder createTitledBorder(String title) {
        TitledBorder border = new TitledBorder(title);
        border.setTitleFont(BORDER_TITLE_FONT);
        return border;
    }

}
