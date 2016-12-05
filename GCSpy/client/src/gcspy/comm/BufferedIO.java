/**
 * * $Id: BufferedIO.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.comm;

/**
 * Stuff shared by the array/buffered I/O classes
 *
 * @author Tony Printezis
 */
public class BufferedIO {

    /** A mask for the bottom 8 bits */
    static final int LOW_8_MASK = (1 << 8) - 1;

}
