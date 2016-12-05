/*
 * $Id: PluginException.java 28 2005-06-20 13:13:35Z rej $
 * Copyright Richard Jones 2005
 *
 * See the file "Kent_license.terms" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */

package gcspy.vis.plugins;

/**
 * An exception for the plugin mechanism
 *
 * @author <a href="http://www/cs/kent.ac.uk">Richard Jones</a>
 */
public class PluginException extends RuntimeException {

    /**
     * An exception for plugins
     *
     * @param mesg An error message
     */
    PluginException(String mesg) {
        super(mesg);
    }
}
