/**
 * * $Id: ConnectionInfoLabel.java 22 2005-06-14 05:17:45Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/


package gcspy.vis;

import gcspy.vis.utils.Factory;

import javax.swing.*;

/**
 * Sets / Unsets the connection info label
 * @author Tony Printezis
 */
class ConnectionInfoLabel extends JLabel {

    private String connected(String host, int port, String name) {
        return "  Connected to " + host + ":" + port + " [" + name + "]";
    }

    private void set(String text, boolean enabled) {
        if (enabled)
            Factory.enableLabel(this);
        else
            Factory.disableLabel(this);
        setText(text);
    }

    /**
     * Set the label to show which host is connected
     * @param host The hostname
     * @param port The port number
     * @param name The GCspy server name
     */
    public void setConnected(String host, int port, String name) {
        String str = connected(host, port, name);
        set(str, true);
    }

    /**
     * Set the label to show the host that we are connecting to
     * @param host The hostname
     * @param port The port number
     */
    public void setConnecting(String host, int port) {
        set("  Connecting to " + host + ":" + port, false);
    }

    /**
     * Set the label to show that we are pausing a server
     * @param host The hostname
     * @param port The port number
     * @param name the GCspy server name
     */
    public void setPausing(String host, int port, String name) {
        String str = connected(host, port, name) + "  (Pausing)";
        set(str, true);
    }

    /**
     * Set the label to show that a server has paused
     * @param host The hostname
     * @param port The port number
     * @param name the GCspy server name
     */
    public void setPaused(String host, int port, String name) {
        String str = connected(host, port, name) + "  (Paused)";
        set(str, true);
    }

    /**
     * Set the label to show that we are disconnecting
     */
    public void setDisconnecting() {
        set("  Disconnecting", false);
    }

    /**
     * Set the label to show that we are disconnected
     */
    public void setDisconnected() {
        set("  Not connected", false);
    }

    /** Create a new connection information label */
    public ConnectionInfoLabel() {
    }

}
