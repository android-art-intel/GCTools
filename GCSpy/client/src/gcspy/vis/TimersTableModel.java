/**
 * * $Id: TimersTableModel.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/


package gcspy.vis;

import javax.swing.table.AbstractTableModel;

/**
 * Model for the table of timers
 * @author Tony Printezis
 */
class TimersTableModel extends AbstractTableModel {

    static private final int ROWS = 3;
    static private final int COLUMNS = 2;

    private Object data[][] = null;
    private int rows, columns;

    public int getColumnCount() {
        return columns;
    }

    public int getRowCount() {
        return rows;
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    /**
     * Get the class of objects in a column
     * @param c the column index
     * @return the column's class
     */
    public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /**
     * No cells are editable
     * @return false
     */
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    /**
     * Set the data for the timers table
     * @param elapsedTime The elapsed time
     * @param compensationTime The compensation time
     */
    public void setData(int elapsedTime,
                        int compensationTime) {
        data[0][1] = new String(elapsedTime + "ms");
        data[1][1] = new String(compensationTime + "ms");
        data[2][1] = new String((elapsedTime - compensationTime) + "ms");
        fireTableDataChanged();
    }

    private void setupValues() {
        data[0][0] = "Elapsed";
        data[1][0] = "Compensation";
        data[2][0] = "Difference";
        for (int i = 0; i < rows; ++i) {
            data[i][1] = "0ms";
        }
    }

    /** Create a new model for the timers table */
    TimersTableModel() {
        rows = ROWS;
        columns = COLUMNS;
        data = new Object[rows][columns];

        setupValues();
    }

}
