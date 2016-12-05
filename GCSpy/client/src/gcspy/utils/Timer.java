/**
 * * $Id: Timer.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.utils;

/**
 * Timer utilities
 * @author Tony Printezis
 */
public class Timer {

    private long startTime;
    private long stopTime;
    private long elapsedTime;

    /** Reset the timer */
    public void reset() {
        elapsedTime = 0;
    }

    /** Start the timer */
    public void start() {
        startTime = System.currentTimeMillis();
    }

    /** Stop the timer */
    public void stop() {
        stopTime = System.currentTimeMillis();
        elapsedTime += (stopTime - startTime);
    }

    /**
     * Get the time
     * @return the elapsed time
     */
    public long getTime() {
        return elapsedTime;
    }

    /** A timer */
    public Timer() {
        reset();
    }

}
