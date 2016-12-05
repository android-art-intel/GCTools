/**
 * * $Id: OKCancelDialog.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/


package gcspy.vis.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog window that provides some facilities for OK/Cancel buttons
 * @author Tony Printezis
 */
abstract public class OKCancelDialog extends AbstractDialog {

    private JButton okB;

    private JButton cancelB;

    /** The result */
    protected boolean oked;

    private class OKBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            oked = true;
            okClicked();
            dispose();
        }
    }

    private class CancelBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            oked = false;
            cancelClicked();
            dispose();
        }
    }

    /** OH button has been clicked */
    abstract protected void okClicked();

    /** Cancel button has been clicked */
    abstract protected void cancelClicked();

    /**
     * Add buttons to a panel
     * @param panel The panel
     */
    abstract protected void addButtons(JPanel panel);

    /**
     * Make the dialog visible
     * @return the result
     */
    public boolean result() {
        setVisible(true);
        return oked;
    }

    /** Set up the dialog */
    protected void setup() {
        JPanel panel;
        Container cont = getContentPane();
        cont.setLayout(new BorderLayout());

        panel = Factory.createFlowPanel(FlowLayout.CENTER);
        okB = Factory.createButton("  OK  ", true, new OKBListener());
        panel.add(okB);
        addButtons(panel);
        cancelB = Factory.createButton("Cancel", true, new CancelBListener());
        panel.add(cancelB);

        cont.add(panel, BorderLayout.SOUTH);
    }

    /**
     * A dialog with OK and cancel buttons
     * @param owner The parent frame
     */
    public OKCancelDialog(Frame owner) {
        super(owner);
    }

}
