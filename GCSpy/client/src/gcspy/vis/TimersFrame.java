/**
 * * $Id: TimersFrame.java 22 2005-06-14 05:17:45Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/


package gcspy.vis;

import gcspy.interpreter.client.EventListener;
import gcspy.vis.utils.AbstractFrame;
import gcspy.vis.utils.Factory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Window about timers
 * @author Tony Printezis
 */
class TimersFrame extends AbstractFrame
        implements EventListener {

    private JTable table;
    private TimersTableModel model;

    private JButton closeB;

    private class CloseBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            setVisible(false);
        }
    }

    public void event(int eventID, int elapsedTime, int compensationTime) {
        model.setData(elapsedTime, compensationTime);
    }

    private void setup() {
        setTitle("GCspy: Timers");
        setResizable(false);

        Container cont = getContentPane();
        cont.setLayout(new BorderLayout());

        model = new TimersTableModel();
        table = Factory.createTable(model);
        Factory.setColumnWidth(table, 0, 200);
        Factory.setColumnWidth(table, 1, 100);

        cont.add(table, BorderLayout.CENTER);
        // cont.add(table.getTableHeader(), BorderLayout.NORTH);

        JPanel panel = Factory.createFlowPanel(FlowLayout.CENTER);
        closeB = Factory.createButton("Close", true,
                new CloseBListener());
        panel.add(closeB);

        cont.add(panel, BorderLayout.SOUTH);
        pack();
    }

    public void destroy() {
        table = null;
        model = null;
        super.destroy();
    }

    /**
     * Create a new timers frame
     * @param owner the parent frame
     */
    TimersFrame(AbstractFrame owner) {
        super(owner, Position.POS_TOP_RIGHT, true);
        setup();
    }

}
