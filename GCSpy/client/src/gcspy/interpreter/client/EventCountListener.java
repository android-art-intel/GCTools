/**
 * * $Id: EventCountListener.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/
package gcspy.interpreter.client;

/**
 * Gets called upon an EVENT COUNT command
 * @author Tony Printezis
 */
public interface EventCountListener {

    /**
     * Handle an event count command
     * @param count counts for the events
     */
    void eventCount(int count[]);

}
