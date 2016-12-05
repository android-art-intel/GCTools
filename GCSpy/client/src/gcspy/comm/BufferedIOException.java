/**
 * * $Id: BufferedIOException.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.comm;

/**
 * Buffered IO exception
 *
 * @author Tony Printezis
 */
public class BufferedIOException extends RuntimeException {

    /**
     * An exception for buffered IO errors
     * @param mesg An error message
     */
    BufferedIOException(String mesg) {
        super(mesg);
    }

}
