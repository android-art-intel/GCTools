/**
 * * Id$
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/


package gcspy.vis;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Keeps track of marker ranges
 * @author Tony Printezis
 * @author <a href="http://www/cs/kent.ac.uk">Richard Jones</a>
 */
class Markers {

    private List<MarkerRange> list;


    private class MarkerIterator implements Iterator<MarkerRange> {
        private Iterator<MarkerRange> iterator;

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public MarkerRange next() {
            return iterator.next();
        }

        public void remove() {
        }

        public MarkerIterator() {
            iterator = list.iterator();
        }
    }

    /**
     * Get an iterator for the markers
     * @return an iterator for thes markers
     */
    public Iterator<MarkerRange> iterator() {
        return new MarkerIterator();
    }

    /**
     * Create and add a new marker range
     * @param m1 The index of one end of the range
     * @param m2 The indes of the other end of the range
     * @return @ return the new range
     */
    public MarkerRange add(int m1, int m2) {
        MarkerRange range = new MarkerRange(m1, m2);
        list.add(range);
        return range;
    }

    /**
     * Remove a marker range
     * @param range The range ot remove
     */
    public void remove(MarkerRange range) {
        list.remove(range);
    }

    /**
     * * ----------- * ##### existingRange | | newRange * ----------- * * 1.
     * Disjoint to the right * ----------- * ##### | | * ----------- * * 2.
     * Adjacent to the right * ----------- * #####| | * ----------- * * 3.
     * Disjoint to the left * ----------- * | | ##### * ----------- * * 4.
     * Adjacent to the left * ----------- * | |##### * ----------- * * 5.
     * Enclosing * ----------- ----------- * | ##### | or ########### *
     * ----------- ----------- * * 6. Enclosed * ----------- * ############### *
     * ----------- * * 7. Partially joint to the right * ----------- ----------- *
     * ##### | or ############# * ----------- ----------- * * 8. Partially joint
     * to the left * ----------- ----------- * | ##### or ############# *
     * ----------- -----------
     */

    private enum Intersection {
        BAD,
        EXISTING,
        DISJOINT_RIGHT,
        ADJACENT_RIGHT,
        DISJOINT_LEFT,
        ADJACENT_LEFT,
        ENCLOSED,
        PARTIAL_RICHT,
        PARTIAL_LEFT
    }

    private Intersection calculateIntersection(MarkerRange newRange,
                                               MarkerRange existingRange) {
        int existingFrom = existingRange.getFrom();
        int existingTo = existingRange.getTo();
        int newFrom = newRange.getFrom();
        int newTo = newRange.getTo();

        if (existingFrom > existingTo) {
            System.out.println("### BARF 1!");
            return Intersection.BAD;
        }
        if (newFrom > newTo) {
            System.out.println("### BARF 2!");
            return Intersection.BAD;
        }

        if (newFrom > existingTo) {
            if (newFrom == (existingTo + 1))
                return Intersection.DISJOINT_RIGHT;
            else
                return Intersection.EXISTING;
        }
        if (newTo < existingFrom) {
            if (newTo == (existingFrom - 1))
                return Intersection.DISJOINT_LEFT;
            else
                return Intersection.ADJACENT_RIGHT;
        }
        if ((newFrom <= existingFrom) && (newTo >= existingTo))
            return Intersection.ADJACENT_LEFT;
        if ((newFrom > existingFrom) && (newTo < existingTo))
            return Intersection.ENCLOSED;
        if (newFrom > existingFrom)
      /*
       * I think that after eliminating the previous cases we do not need any
       * further checking for this one
       */
            return Intersection.PARTIAL_RICHT;
        if (newTo < existingTo)
      /* ditto */
            return Intersection.PARTIAL_LEFT;

        System.out.println("### BARF 3!");
        return Intersection.BAD;
    }

    /**
     * Extend a range of markers
     * @param newRange The new range
     */
    public void addRange(MarkerRange newRange) {
        Object elements[] = list.toArray();
        for (Object element : elements) {
            MarkerRange existingRange = (MarkerRange) element;
            Intersection pos = calculateIntersection(newRange, existingRange);

            switch (pos) {
                case EXISTING:
                case ADJACENT_RIGHT:
                    // Nothing to do - we'll added it later
                    break;
                case DISJOINT_RIGHT:
                case PARTIAL_RICHT:
                    newRange.setFrom(existingRange.getFrom());
                    list.remove(existingRange);
                    break;
                case DISJOINT_LEFT:
                case PARTIAL_LEFT:
                    newRange.setTo(existingRange.getTo());
                    list.remove(existingRange);
                    break;
                case ADJACENT_LEFT:
                    list.remove(existingRange);
                    break;
                case ENCLOSED:
                    // Nothing to do - we're done
                    return;
            }
        }
        list.add(newRange);
    }

    /**
     * Remove a range of markers
     * @param newRange The new range
     */
    public void removeRange(MarkerRange newRange) {
        Object elements[] = list.toArray();
        for (Object element : elements) {
            MarkerRange existingRange = (MarkerRange) element;
            Intersection pos = calculateIntersection(newRange, existingRange);

            switch (pos) {
                case EXISTING:
                case DISJOINT_RIGHT:
                case ADJACENT_RIGHT:
                case DISJOINT_LEFT:
                    // Nothing to do
                    break;
                case ADJACENT_LEFT:
                    list.remove(existingRange);
                    break;
                case ENCLOSED:
                    int from1 = existingRange.getFrom();
                    int to1 = newRange.getFrom() - 1;
                    int from2 = newRange.getTo() + 1;
                    int to2 = existingRange.getTo();
                    existingRange.set(from1, to1);
                    newRange.set(from2, to2);
                    list.add(newRange);
                    break;
                case PARTIAL_RICHT:
                    existingRange.setTo(newRange.getFrom() - 1);
                    break;
                case PARTIAL_LEFT:
                    existingRange.setFrom(newRange.getTo() + 1);
                    break;
            }
        }
    }

    /** Clear the list of markers */
    public void clear() {
        list = new LinkedList<MarkerRange>();
    }

    /** New empty markers */
    public Markers() {
        clear();
    }

}
