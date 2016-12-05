/*
 * $Id: MultiLineHeaderRenderer.java 22 2005-06-14 05:17:45Z rej $
 * Copyright Richard Jones, University of Kent, 2005
 *
 * See the file "Kent_license.terms" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package gcspy.vis.utils;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A renderer for multiline headers
 * Based on http://www.java2s.com/ExampleCode/Swing-Components/MultiLineHeaderTable.htm
 *
 * @author <a href="http://www/cs/kent.ac.uk">Richard Jones</a>
 */
public class MultiLineHeaderRenderer extends JPanel
        implements TableCellRenderer {

    private int horizontalAlignment;
    private int verticalAlignment;
    private float alignmentX;
    private Map<Integer, Integer> columnHeights;

    /**
     * A new table column header rendered
     *
     * @param horizontalAlignment horizontal alignment of the header
     * @param verticalAlignment   vertical alignment of the header
     */
    public MultiLineHeaderRenderer(int horizontalAlignment, int verticalAlignment) {
        columnHeights = new HashMap<Integer, Integer>();
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
        switch (horizontalAlignment) {
            case SwingConstants.LEFT:
                alignmentX = (float) 0.0;
                break;
            case SwingConstants.CENTER:
                alignmentX = (float) 0.5;
                break;
            case SwingConstants.RIGHT:
                alignmentX = (float) 1.0;
                break;
            default:
                throw new IllegalArgumentException("Illegal horizontal alignment value");
        }

        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(true);
    }


    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        removeAll();
        invalidate();

        if (value == null) {
            return this; // Do nothing if no value
        }

        // Set the foreground and background colors
        // from the table header if they are not set
        if (table != null) {
            JTableHeader header = table.getTableHeader();
            super.setForeground(header.getForeground());
            super.setBackground(header.getBackground());
        }

        if (verticalAlignment != SwingConstants.TOP) {
            add(Box.createVerticalGlue());
        }

        // Must align the text at the top of the label
        // otherwise it gets truncated
        JLabel label = Factory.createLabel((String) value);
        label.setVerticalAlignment(SwingConstants.TOP);

        // calculate sizes
        TableColumnModel tcm = table.getColumnModel();
        TableColumn tableColumn = tcm.getColumn(column);
        int colWidth = tableColumn.getWidth();

        // Calculate the size needed to display this label in
        // this column.
        // We seem to have to reduce the calculated height by one line. Why??
        int oneLineHeight = new JLabel(" ").getPreferredSize().height;
        Dimension prefer = label.getPreferredSize();
        double factor = Math.ceil((double) prefer.width / colWidth);
        Dimension actual = new Dimension(colWidth,
                Math.max(prefer.height, (int) (prefer.height * factor) - oneLineHeight));
        label.setPreferredSize(actual); // needed

        // Calculate the header size
        int colHeight = Math.max(prefer.height, (int) (prefer.height * factor));
        JTableHeader hdr = table.getTableHeader();
        Dimension hdrSize = hdr.getPreferredSize();
        hdrSize.height = calcHeaderHeight(column, colHeight);
        hdr.setPreferredSize(hdrSize);
        add(label);
        hdr.revalidate();   // needed !

        if (verticalAlignment != SwingConstants.BOTTOM) {
            add(Box.createVerticalGlue());
        }
        return this;
    }

    /**
     * Add the height for this column  to the list of column header heights
     * and find the max height for any column header
     *
     * @param col    The column index
     * @param height The header height needed for this column
     * @return The greatest column height
     */
    private int calcHeaderHeight(int col, int height) {
        columnHeights.put(col, height);
        int hdrHeight = height;
        for (Integer h : columnHeights.values()) {
            hdrHeight = Math.max(hdrHeight, h);
        }
        return hdrHeight;
    }

}
