/**
 * * Id$
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/


package gcspy.vis;

/**
 * A marker range
 * @author Tony Printezis
 */
class MarkerRange {

    private int from;
    private int to;
    private boolean newlyAdded;

    /**
     * Get the start of the range
     * @return the index of the start of the range
     */
    public int getFrom() {
        return from;
    }

    /**
     * Get the end of the range
     * @return the index of the end of the range
     */
    public int getTo() {
        return to;
    }

    /** The range has not been newly added */
    public void unsetNewlyAdded() {
        newlyAdded = false;
    }

    /** The range has been newly added */
    public boolean isNewlyAdded() {
        return newlyAdded;
    }

    /**
     * Set a new range
     * @param m1 The index of one end of the range
     * @param m2 The index of the other end of the range
     */
    public void set(int m1, int m2) {
        if (m1 < m2) {
            from = m1;
            to = m2;
        } else {
            from = m2;
            to = m1;
        }
    }

    /** Set the end of the range */
    public void setTo(int to) {
        this.to = to;
    }

    /** Set the start of the range */
    public void setFrom(int from) {
        this.from = from;
    }

    /**
     * Copy a marker range
     * @param range The source range
     */
    public void copyFrom(MarkerRange range) {
        from = range.from;
        to = range.to;
    }

    /**
     * Create a new range
     * @param m1 The index of one end of the range
     * @param m2 The index of the other end of the range
     */
    public MarkerRange(int m1, int m2) {
        set(m1, m2);
        newlyAdded = true;
    }

}
