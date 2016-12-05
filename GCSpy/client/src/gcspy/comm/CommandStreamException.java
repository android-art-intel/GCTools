/**
 * * $Id: CommandStreamException.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.comm;

/**
 * Command Stream Exception
 * @author Tony Printezis
 */
public class CommandStreamException extends RuntimeException {

    /**
     * An exception for command errors
     * @param mesg An error message
     */
    CommandStreamException(String mesg) {
        super(mesg);
    }

}
