/**
 * * $Id: DisconnectListener.java 30 2005-06-30 12:04:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/


package gcspy.vis;

/**
 * Interface to listen to the main frame disconnecting
 * @author Tony Printezis
 */
public interface DisconnectListener {

    /**
     * Respond to the client disconnecting from the server
     * @param reconnecting Is the client reconnnecting?
     */
    void disconnect(boolean reconnecting);

}
