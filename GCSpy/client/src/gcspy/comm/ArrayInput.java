/**
 * * $Id: ArrayInput.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.comm;

/**
 * Low-level input for ints and shorts from a byte array
 * @author Tony Printezis
 */
public class ArrayInput {

    static private short baseRead(byte buffer[], int i) {
        short res = (short) buffer[i];
        if (res < 0)
            res += 256;
        return res;
    }

    /**
     * Read an int from a byte array
     * @param buffer the byte array
     * @param i the starting index
     * @return the int from this position in the input buffer
     */
    static public int readInt(byte buffer[], int i) {
        int res = (int) baseRead(buffer, i);
        res <<= 8;
        res |= (int) baseRead(buffer, i + 1);
        res <<= 8;
        res |= (int) baseRead(buffer, i + 2);
        res <<= 8;
        res |= (int) baseRead(buffer, i + 3);

        return res;
    }

}
