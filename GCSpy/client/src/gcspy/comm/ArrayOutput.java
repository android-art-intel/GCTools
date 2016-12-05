/**
 * * $Id: ArrayOutput.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.comm;

/**
 * Output to a byte array
 * @author Tony Printezis
 */
public class ArrayOutput {

    static private void baseWrite(byte buffer[], int i, short v) {
        if (v > 127)
            v -= 256;
        buffer[i] = (byte) v;
    }

    /**
     * Write an int into a byte array
     * @param buffer The byte array
     * @param i The position in the array
     * @param v The value to be written
     */
    static public void writeInt(byte buffer[], int i, int v) {
        baseWrite(buffer, i, (short) (v >> 24));
        baseWrite(buffer, i + 1, (short) ((v >> 16) & BufferedIO.LOW_8_MASK));
        baseWrite(buffer, i + 2, (short) ((v >> 8) & BufferedIO.LOW_8_MASK));
        baseWrite(buffer, i + 3, (short) (v & BufferedIO.LOW_8_MASK));
    }

}
