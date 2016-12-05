/**
 * * $Id: EventCountTableModel.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/


package gcspy.vis;

import gcspy.interpreter.Interpreter;

import javax.swing.table.AbstractTableModel;

/**
 * Model for the table of event counting
 * @author Tony Printezis
 */
class EventCountTableModel extends AbstractTableModel {

    static private final int COLUMNS = 2;

    private String columnNames[] = {"Event", "Counter"};

    private Object data[][] = null;
    private int rows, columns;
    private Interpreter interpreter;

    /** @inheritDoc */
    public int getColumnCount() {
        return columns;
    }

    /** @inheritDoc */
    public int getRowCount() {
        return rows;
    }

    /**
     * Get the name of a column
     * @param col The column index
     * @return "Event" or "Counter"
     */
    public String getColumnName(int col) {
        return columnNames[col];
    }

    /** @inheritDoc */
    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    /**
     * Get the class of this column
     * @return the class of this column
     */
    public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /**
     * No cells are editable
     * @param row the row index
     * @param col the column index
     * @return false
     */
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    /**
     * Set the table data
     * @param count The new event counts
     */
    public void setData(int count[]) {
        for (int i = 0; i < rows; ++i) {
            data[i][1] = count[i];
        }
        fireTableDataChanged();
    }

    /** Set up and initialise the data values for the table */
    private void setupValues() {
        for (int i = 0; i < rows; ++i) {
            data[i] = new Object[columns];

            data[i][0] = interpreter.getEvents().getName(i);
            data[i][1] = 0;
        }
    }

    /**
     * Create a new table model for the event count table
     * @param interpreter The interpreter
     */
    EventCountTableModel(Interpreter interpreter) {
        this.interpreter = interpreter;

        rows = interpreter.getEvents().getNum();
        columns = COLUMNS;
        data = new Object[rows][];

        setupValues();
    }

}
