/*
 * $Id: TextTableModel.java 32 2005-07-12 10:50:05Z rej $
 * Copyright Richard Jones, University of Kent, 2005
 *
 * See the file "Kent_license.terms" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */

package gcspy.vis.plugins.text;

import gcspy.interpreter.DataAccessor;
import gcspy.interpreter.Space;
import gcspy.interpreter.Stream;
import gcspy.interpreter.client.ClientSpace;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A table model for the text view
 *
 * @author <a href="http://www/cs/kent.ac.uk">Richard Jones</a>
 */
public class TextTableModel extends AbstractTableModel {

    private ClientSpace space;
    private List<TextRow> data;
    private int len;  // The number of non-background tiles
    private static final int MIN_TABLE_ROWS = 10;

    private int sortColumn = 0;
    private boolean ascending = true;

    /**
     * Create a model for a table view of a space
     *
     * @param space The space
     */
    TextTableModel(ClientSpace space) {
        this.space = space;
        data = new ArrayList<TextRow>();
        len = 0;
    }

    /**
     * Get the value of a tile
     *
     * @param tile     The tile index
     * @param streamID The stream ID
     * @return the tile's value
     */
    private Integer getEntry(int tile, int streamID) {
        Stream stream = space.getStream(streamID);
        DataAccessor accessor = stream.getAccessor();
        byte control[] = space.getControl();
        if (accessor.isDataAvailable())
            if (Space.isControlUsed(control[tile]))
                return accessor.get(tile);
            else if (Space.isControlUnused(control[tile]))
                return null;
        return null;
    /*
    if (ClientSpace.isControlBackground(space.getControl()[tile]) ||
        ClientSpace.isControlUnused(space.getControl()[tile]))
      return null;
    return stream.getAccessor().get(tile);  
    */
    }

    private boolean isUsedTile(int tile) {
        byte[] control = space.getControl();
        return (Space.isControlUsed(control[tile]) &&
                !Space.isControlUnused(control[tile]) &&
                !Space.isControlBackground(control[tile]) &&
                !Space.isControlSeparator(control[tile]) &&
                !Space.isControlLink(control[tile]));
    }

    /**
     * Set the space to view
     *
     * @param space the ClientSpace
     */
    void setSpace(ClientSpace space) {
        this.space = space;
    }

    /**
     * Refresh the data for the space, and reset the sorting parameters
     * Looks better if there is always a minimum number of rows.
     */
    void setData() {
        int tiles = space.getTileNum();
        int cols = space.getStreamNum();
        int rows = (tiles > MIN_TABLE_ROWS) ? tiles : MIN_TABLE_ROWS;
        data = new ArrayList<TextRow>();
        for (int t = 0; t < tiles; t++) {
            Integer[] tmp = new Integer[cols];
            for (int s = 0; s < cols; s++) {
                tmp[s] = getEntry(t, s);
            }
            TextRow tr = new TextRow(space.getTileName(t), tmp);
            data.add(tr);
        }
        len = data.size();
        initSorting();
        fireTableStructureChanged();
    }

    public int getRowCount() {
        return data.size();
    }

    public int getColumnCount() {
        return space.getStreamNum() + 1;
    }

    /**
     * Get a block name or a stream value
     *
     * @param row the tile
     * @param col 0 for block name, else stream + 1
     */
    public Object getValueAt(int row, int col) {
        if (col == 0)
            return data.get(row).getName();
        else {
            Integer val = data.get(row).getValue(col - 1);
            return (val == null) ? "" : val;
        }
    }

    /**
     * Get the class of a column
     *
     * @param col the index of the column
     * @return the class of the objects in this column
     */
    public Class<?> getColumnClass(int col) {
        if (col == 0)
            return String.class;
        else
            return Integer.class;
    }

    /**
     * Return either "Block" (for column 0) or a title
     * combining the prefix/suffix string for this stream
     *
     * @param col The column index (i.e. stream ID + 1)
     * @return a column heading string
     */
    public String getColumnName(int col) {
        if (col == 0) {
            return format(" Block");
        }

        try {
            Stream stream = space.getStream(col - 1);
            String pre = stream.getPrefix().trim();
            String post = stream.getSuffix().trim();
            return format(new String[]{pre, post});
        } catch (ArrayIndexOutOfBoundsException e) {
            int streamNum = space.getStreamNum();
            System.err.println("Column index (" + col +
                    ") is higher than number of streams (" + streamNum + ")");
            return null;
        }
    }

    /**
     * Format a column header
     *
     * @param str a string for the header
     * @return a centred representation of a string
     */
    public String format(String str) {
        return "<html><body align='center'>" + str + "</body></html>";
    }

    /**
     * Format a column header
     *
     * @param strings an array of strings to be appended into a single header
     * @return a centred representation of a string
     */
    public String format(String[] strings) {
        String tmp = "<html><body align='center'>";
        if (strings != null)
            tmp += strings[0].trim();
        for (int i = 1; i < strings.length; i++) {
            tmp += "<br>" + strings[i];
        }
        return tmp + "</body></html>";
    }

    /**
     * Get the strings for the column header
     * @param col The column
     * @return an array of strings for the column header
    public String[] getColumnMultiName(int col){
    if (col == 0) {
    return new String[] { format("Block") };
    }
    Stream stream;
    try {
    stream = space.getStream(col - 1);
    } catch (ArrayIndexOutOfBoundsException e) {
    int streamNum = space.getStreamNum();
    System.err.println("Column index (" + col +
    ") is higher than number of streams (" + streamNum + ")");
    return null;
    }  
    return new String[] { format(stream.getPrefix()), "("+stream.getSuffix()+")" };
    }
     */

    /**
     * No cells are editable
     *
     * @return false
     */
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    //---------------Sorting--------------------------------------------


    /**
     * Set up the sorting so that we remember when a column has been clicked
     */
    private void initSorting() {
        sortColumn = 0;
        ascending = true;
    }

    /**
     * Sort the column by a particular column.
     *
     * @param column The column
     */
    public void sortAllRowsByColumn(int column) {
        if (column != sortColumn) {
            sortColumn = column;
            ascending = true;
        } else {
            ascending = !ascending;
        }
        sortAllRowsByColumn(column, ascending);
    }


    /**
     * Sort the column by a particular column.
     *
     * @param column    The column
     * @param ascending Whether to sort in ascending or descending order
     */
    public void sortAllRowsByColumn(int column, boolean ascending) {
        Comparator<TextRow> comparator;
        if (column == 0)
            comparator = new BlockComparator(ascending);
        else
            comparator = new ValueComparator(column, ascending);

        Collections.sort(data, comparator);
        fireTableStructureChanged();
    }

    /**
     * Compare two objects
     * Sort nulls so they appear last, regardless of sort order
     *
     * @param o1        An object to compare
     * @param 02        An object to compare
     * @param ascending true to sort in ascending order
     */
    private static <T extends Comparable<T>> int
    compareInOrder(T o1, T o2, boolean ascending) {
        if (o1 == null && o2 == null)
            return 0;
        else if (o1 == null)
            return 1;
        else if (o2 == null)
            return -1;
        else if (ascending)
            return o1.compareTo(o2);
        else
            return o2.compareTo(o1);
    }

    /**
     * A comparator for block names
     */
    private class BlockComparator implements Comparator<TextRow> {
        private boolean ascending;

        BlockComparator(boolean ascending) {
            this.ascending = ascending;
        }

        public int compare(TextRow tile1, TextRow tile2) {
            String block1 = tile1.getName();
            String block2 = tile2.getName();
            if (block1 != null && block1.length() == 0) block1 = null;
            if (block2 != null && block2.length() == 0) block2 = null;
            return compareInOrder(block1, block2, ascending);
        }
    }

    /**
     * A comparator for tile values
     */
    private class ValueComparator implements Comparator<TextRow> {
        private int stream;
        private boolean ascending;

        ValueComparator(int column, boolean ascending) {
            this.stream = column - 1;
            this.ascending = ascending;
        }

        //  do we want to distinguish presentation styles?
        public int compare(TextRow tile1, TextRow tile2) {
            Integer value1 = tile1.getValue(stream);
            Integer value2 = tile2.getValue(stream);
            return compareInOrder(value1, value2, ascending);
        }
    }

}
