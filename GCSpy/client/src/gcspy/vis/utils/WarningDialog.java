/**
 * * $Id: WarningDialog.java 28 2005-06-20 13:13:35Z rej $
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
 * Dialog window for warnings
 * @author Tony Printezis
 */
class WarningDialog extends AbstractDialog {

    private JButton okB;

    private class OKBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            dispose();
        }
    }

    private void setup(String message) {
        JPanel panel;
        JLabel label;
        Container cont = getContentPane();
        cont.setLayout(new BorderLayout());

        panel = Factory.createGridPanel(2, 1, null);

        label = Factory.createLabel("Warning", JLabel.CENTER, true);
        panel.add(label);
        label = Factory.createLabel("     " + message + "     ", JLabel.CENTER,
                false);
        panel.add(label);

        cont.add(panel, BorderLayout.CENTER);

        panel = Factory.createFlowPanel(FlowLayout.CENTER);
        okB = Factory.createButton("  OK  ", true, new OKBListener());
        panel.add(okB);

        cont.add(panel, BorderLayout.SOUTH);
        pack();
    }

    /**
     * A new warning dialog
     * @param owner The parent frame
     * @param message The warning message
     */
    WarningDialog(Frame owner, String message) {
        super(owner);
        setup(message);
        placeIt();
    }

  /* *** Testing **** */

    static public void main(String args[]) {
        WarningDialog wd = new WarningDialog(null, "An error has occured");
        wd.setVisible(true);
    }

}
