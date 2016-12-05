/**
 * * $Id: DataAccessor.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.interpreter;

/**
 * This interface represents a stream
 * and provides methods to interrogate the stream's contents.
 * @author Tony Printezis 
 */
public interface DataAccessor {

    /**
     * Get the value of a tile
     * @param i The index of the tile
     * @return Its value
     */
    int get(int i);

    /**
     * Get the value of a tile, adjusted according to the stream's
     * presentation style.
     * @param i The index of the tile
     * @return Its value
     */
    int getAdjusted(int i);

    /**
     * Get the maximum value of a tile in this stream,
     * adjusted according to the stream's presentation style.
     * @return The maximum value
     */
    int getAdjustedMax();

    /**
     * Is the value of a tile zero?
     * @param i The index of the tile
     * @return true if the tile is zero.
     */
    boolean isZero(int i);

    /**
     * Is the value of a tile greater than the maximum for this stream?
     * @param i The index of the tile
     * @return true uf the tile's value exceeds the maximum.
     */
    boolean isOverflow(int i);

    /**
     * Is data available?
     * @return true if data is available for this stream
     */
    boolean isDataAvailable();

    /**
     * Get the length of the stream
     * @return the length of the stream
     */
    int getLength();

}
