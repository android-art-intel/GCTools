/*
 * $Id: TextRow.java 32 2005-07-12 10:50:05Z rej $
 * Copyright Richard Jones, University of Kent, 2005
 *
 * See the file "Kent_license.terms" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */

package gcspy.vis.plugins.text;

/**
 * This represents a row for a single tile
 *
 * @author <a href="http://www/cs/kent.ac.uk">Richard Jones</a>
 */
class TextRow {
    private String blockName;
    private Integer[] values;

    /**
     * Create a row in the table
     *
     * @param name   The tile's name
     * @param values The stram values
     */
    TextRow(String name, Integer[] values) {
        this.blockName = name;
        this.values = values;
    }

    /**
     * @return the name of the tile in this row of the table
     */
    String getName() {
        return blockName;
    }

    /**
     * @return the stream value for this columns of the table
     */
    Integer getValue(int stream) {
        return values[stream];
    }

    /**
     * @return the number of streams in this row of the table
     */
    int getStreamNum() {
        return values.length;
    }

    public void dump() {
        System.err.print(blockName);
        for (Integer value : values) System.err.print(" " + value);
        System.err.println();
    }

}
