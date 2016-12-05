/**
 * * $Id: Verbose.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.utils;

/**
 * Provides verbose output
 *
 * @author Tony Printezis
 */
public class Verbose {

    /** Is output verbose? */
    protected boolean verbose;

    /** Print a new line if verbose */
    public void println() {
        if (verbose)
            System.out.println();
    }

    /**
     * Print a string if f=verbose
     * @param str The string
     */
    public void print(String str) {
        if (verbose)
            System.out.print(str);
    }

    /**
     * Print a string followed by a new line if verbose
     * @param str The string
     */
    public void println(String str) {
        if (verbose)
            System.out.println(str);
    }

    /**
     * Is verbose loggin on?
     * @return true if verbose
     */
    public boolean verbose() {
        return verbose;
    }

    /**
     * Set verbosity
     * @param verbose True if verbose
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /** ****************** Constructor ******************* */

    /** Create new silent Verbose object */
    public Verbose() {
        this(false);
    }

    /**
     * Create a bew Verbose object
     * @param verbose True if verbose
     */
    public Verbose(boolean verbose) {
        setVerbose(verbose);
    }

}
