/**
 * * $Id: EventFilterTableModel.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/


package gcspy.vis;

import gcspy.interpreter.EventFilters;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Model for the table about event filtering
 * @author Tony Printezis
 * @author <a href="http://www/cs/kent.ac.uk">Richard Jones</a>
 */
class EventFilterTableModel extends AbstractTableModel {

    static private final int DELAY_MIN = EventFilters.DELAY_MIN;
    static private final int DELAY_MAX = EventFilters.DELAY_MAX;

    static private final int PERIOD_MIN = EventFilters.PERIOD_MIN;
    static private final int PERIOD_MAX = EventFilters.PERIOD_MAX;

    static private final int EVENT_INDEX = 0;
    static private final int ENABLED_INDEX = 1;
    static private final int DELAYS_INDEX = 2;
    static private final int PAUSED_INDEX = 3;
    static private final int PERIOD_INDEX = 4;

    static private final int COLUMNS = 5;

    private String columnNames[] = {"Event", "Enabled", "Delay (ms)", "Pause",
            "Period"};

    private String eventNames[];
    private int rows, columns;
    private EventFilters eventFilters;

    private List<EventFilterListener> listeners;

    public int getColumnCount() {
        return columns;
    }

    public int getRowCount() {
        return rows;
    }

    /**
     * Get the name of a column
     * @param col the column index
     * @return "Event", "Enabled", "Delay (ms)", "Pause" or "Period"
     */
    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        switch (col) {
            case EVENT_INDEX:
                return eventNames[row];
            case ENABLED_INDEX:
                return eventFilters.getEnabled()[row];
            case DELAYS_INDEX:
                return eventFilters.getDelays()[row];
            case PAUSED_INDEX:
                return eventFilters.getPauses()[row];
            case PERIOD_INDEX:
                return eventFilters.getPeriods()[row];
        }
        return null; // the compiler is being stupid
    }

    /**
     * Get the class of a column
     * @param c the column index
     * @return the column's class
     */
    public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /**
     * Cells in the event column are not editable.
     * All others are.
     * @param row the row index
     * @param col the column index
     */
    public boolean isCellEditable(int row, int col) {
        switch (col) {
            case EVENT_INDEX:
                return false;
            case ENABLED_INDEX: /* enabled */
            case DELAYS_INDEX: /* delay */
            case PAUSED_INDEX: /* pause */
            case PERIOD_INDEX: /* period */
                return true;
            default:
                return false;
        }
    }

    private Integer parseInteger(Object val, int min, int max) {
        int i;
        if (val == null)
            return min;
        try {
            i = Integer.parseInt(val.toString());
        } catch (NumberFormatException e) {
            try {
                double d = Double.parseDouble(val.toString());
                i = (int) d;
            } catch (NumberFormatException e2) {
                return null;
            }
        }
        if (i < min)
            i = min;
        if (i > max)
            i = max;
        return i;
    }

    public void setValueAt(Object val, int row, int col) {
        switch (col) {
            case ENABLED_INDEX:
                eventFilters.getEnabled()[row] = ((Boolean) val).booleanValue();
                break;
            case DELAYS_INDEX:
                val = parseInteger(val, DELAY_MIN, DELAY_MAX);
                if (val == null)
                    return;
                eventFilters.getDelays()[row] = ((Integer) val).intValue();
                break;
            case PAUSED_INDEX:
                eventFilters.getPauses()[row] = ((Boolean) val).booleanValue();
                break;
            case PERIOD_INDEX:
                val = parseInteger(val, PERIOD_MIN, PERIOD_MAX);
                if (val == null)
                    return;
                eventFilters.getPeriods()[row] = ((Integer) val).intValue();
                break;
        }

        fireTableCellUpdated(row, col);
    }

    /** Revert event filters to their defaults */
    public void revertToDefaults() {
        eventFilters.revertToDefaults();
        fireTableDataChanged();
    }

    /** Set all event filters to be enabled */
    public void enableAll() {
        eventFilters.enableAll();
        fireTableDataChanged();
    }

    /** Set all event filters to be disabled */
    public void disableAll() {
        eventFilters.disableAll();
        fireTableDataChanged();
    }

    /** Clear all delays */
    public void clearDelays() {
        eventFilters.clearDelays();
        fireTableDataChanged();
    }

    /** Clear all pauses */
    public void clearPauses() {
        eventFilters.clearPauses();
        fireTableDataChanged();
    }

    /** Reset all event periods */
    public void resetPeriods() {
        eventFilters.resetPeriods();
        fireTableDataChanged();
    }

    public void fireTableCellUpdated(int row, int column) {
        super.fireTableCellUpdated(row, column);
        callListeners();
    }

    public void fireTableDataChanged() {
        super.fireTableDataChanged();
        callListeners();
    }

    /** Add an event filter listener */
    public void addListener(EventFilterListener listener) {
        listeners.add(listener);
    }

    private void callListeners() {
        for (EventFilterListener listener : listeners)
            listener.eventFilterUpdated();
    }

    /**
     * Create a new model for teh event filter table
     * @param eventNames The event names
     * @param eventFilters Filters for the events
     */
    EventFilterTableModel(String eventNames[], EventFilters eventFilters) {
        this.eventNames = eventNames;
        this.eventFilters = eventFilters;

        rows = eventFilters.getNum();
        columns = COLUMNS;
        listeners = new ArrayList<EventFilterListener>();
    }

}
