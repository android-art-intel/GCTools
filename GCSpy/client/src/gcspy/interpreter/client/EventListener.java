/**
 * * $Id: EventListener.java 30 2005-06-30 12:04:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.interpreter.client;

/**
 * Gets called upon an EVENT command
 * @author Tony Printezis
 * @author <a href="http://www/cs/kent.ac.uk">Richard Jones</a>
 */
public interface EventListener {

    /**
     * Respond to an EVENT command from the server
     * @param eventID The event's ID
     * @param elapsedTime the elapsed time for the event
     * @param compensationTime The compensation time
     */
    void event(int eventID, int elapsedTime, int compensationTime);

}
