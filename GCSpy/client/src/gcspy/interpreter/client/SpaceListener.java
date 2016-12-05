/**
 * * $Id: SpaceListener.java 30 2005-06-30 12:04:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/


package gcspy.interpreter.client;

/**
 * Gets called upon a SPACE command
 * @author Tony Printezis
 */
public interface SpaceListener {

    /**
     * Respond to a SPACE command from the server.
     * @param space The new ClientSpace
     */
    void space(ClientSpace space);

}
