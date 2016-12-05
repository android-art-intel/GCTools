/**
 * * $Id: ConnectionDialog.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.vis;

import gcspy.vis.utils.Factory;
import gcspy.vis.utils.OKCancelDialog;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog window for warnings
 * @author Tony Printezis
 */
class ConnectionDialog extends OKCancelDialog {

    private JTextField hostTF;
    private JTextField portTF;
    private JCheckBox pauseAtStartCB;
    private JCheckBox smallCB;

    private int port;
    private String host;
    private boolean pauseAtStart;
    private boolean small;

    protected void okClicked() {
        host = hostTF.getText();
        if (host.equals(""))
            oked = false;
        try {
            port = Integer.parseInt(portTF.getText());
        } catch (NumberFormatException e) {
            oked = false;
        }
        pauseAtStart = pauseAtStartCB.isSelected();
        small = smallCB.isSelected();
    }

    protected void cancelClicked() {
    }

    /**
     * Get the host's name
     * @return the host's name
     */
    String getHost() {
        return host;
    }

    /**
     * get the port number
     * @return the port number
     */
    int getPort() {
        return port;
    }

    /**
     * Should the server pause at the start?
     * @return true if server should paue at the start
     */
    boolean getPauseAtStart() {
        return pauseAtStart;
    }

    /**
     * Should small tiles be used?
     * @return true is small tiles should be used
     */
    boolean getSmall() {
        return small;
    }

    protected void addButtons(JPanel panel) {
    }

    private void setup(String host, int port, boolean pauseAtStart, boolean small) {
        setup();

        JPanel panel;
        Container cont = getContentPane();

        Container vertical = new Box(BoxLayout.Y_AXIS);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        panel.add(Factory.createLabel("Host:"));
        hostTF = Factory.createTextField(host, 15, true);
        panel.add(hostTF);
        vertical.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        panel.add(Factory.createLabel("Port:"));
        portTF = Factory.createTextField((port != -1) ? "" + port : null, 6, true);
        panel.add(portTF);
        vertical.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        panel.add(Factory.createLabel("Pause At Start:"));
        pauseAtStartCB = Factory.createCheckBox(pauseAtStart);
        panel.add(pauseAtStartCB);
        vertical.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.LEFT);
        panel.add(Factory.createLabel("Small Tiles:"));
        smallCB = Factory.createCheckBox(small);
        panel.add(smallCB);
        vertical.add(panel);

        panel = Factory.createTitlePanel("Connection Info", vertical);
        cont.add(panel, BorderLayout.CENTER);

        pack();
    }

    /**
     * Create a new dialog
     * @param owner The parent frame
     */
    ConnectionDialog(Frame owner) {
        this(owner, null, -1, false, false);
    }

    /**
     * Create a new dialog
     * @param owner The parent frame
     * @param host The host to use
     * @param port The port number to use
     * @param pauseAtStart True if the server should pause at the start
     * @param small True if small tiles should be used
     */
    ConnectionDialog(Frame owner, String host, int port, boolean pauseAtStart,
                     boolean small) {
        super(owner);
        setup(host, port, pauseAtStart, small);
        placeIt();
    }

    /** *** Testing **** */

    static public void main(String args[]) {
        ConnectionDialog cd = new ConnectionDialog(null, "greenwich", 30000, false,
                false);
        if (cd.result()) {
            System.out.println("OK");
        } else {
            System.out.println("Cancel");
        }
    }

}
