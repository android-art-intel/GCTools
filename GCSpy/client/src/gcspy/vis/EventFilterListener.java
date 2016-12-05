/**
 * * $Id: EventFilterListener.java 22 2005-06-14 05:17:45Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/


package gcspy.vis;

/**
 * Interface to listen to changes of the event filter table
 * @author Tony Printezis
 */
public interface EventFilterListener {

    /** The event filter was changed */
    void eventFilterUpdated();

}
