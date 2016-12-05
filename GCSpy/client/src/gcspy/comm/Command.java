/**
 * * $Id: Command.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/


package gcspy.comm;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A command object
 * @author Tony Printezis
 *
 */
public interface Command {

    /**
     * Read a command from an input and execute it
     * @param in The buffered input
     */
    void execute(BufferedInput in, OutputStream os) throws IOException;
}
