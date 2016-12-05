/**
 * * $Id: InterpreterException.java 21 2005-06-11 00:25:23Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 * <p>
 * *  gcspy.client.InterpreterException
 * *
 * *  Interpreter Exception
 **/

/**
 **  gcspy.client.InterpreterException
 **
 **  Interpreter Exception
 **/

package gcspy.interpreter;

/**
 * Interpreter Exception
 * @author Tony Printezis
 */
public class InterpreterException extends RuntimeException {

    /**
     * Create a new interpreter exception
     * @param mesg An error message
     */
    public InterpreterException(String mesg) {
        super(mesg);
    }

}
