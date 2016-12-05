/**
 * * Id$
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.tools;

/**
 * Exception for file traces
 *
 * @author Tony Printezis
 */
public class FileTraceException extends RuntimeException {

    /**
     * An exception for trace files
     * @param e The exception captured
     */
    public FileTraceException(Exception e) {
        super(e.getMessage());
        e.printStackTrace();
    }

}
