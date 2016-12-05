/**
 * * $Id: OutputGenerator.java 21 2005-06-11 00:25:23Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.interpreter;

import gcspy.comm.BufferedOutput;

import java.io.IOException;

/**
 * Interface that generates output for interpreter deserialisation
 * @author Tony Printezis
 */
public interface OutputGenerator {

    /**
     * Create a new BufferedOutput
     * @return a new BufferedOutput
     * @throws IOException
     */
    BufferedOutput createBufferedOutput() throws IOException;

    /**
     * Complete a transmission
     * @param len
     * @throws IOException
     */
    void done(int len) throws IOException;

}
