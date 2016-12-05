/**
 * * $Id: InputGenerator.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.interpreter;

import gcspy.comm.BufferedInput;

import java.io.IOException;

/**
 * Interface that generates input for interpreter deserialisation
 * @author Tony Printezis
 */
public interface InputGenerator {

    /**
     * Create a new BufferedInput
     * @return a new BufferedInput
     * @throws IOException
     */
    BufferedInput createBufferedInput() throws IOException;

    // is this safe?

    /**
     * Create a new space
     * @return a new space
     */
    Space createSpace();

}
