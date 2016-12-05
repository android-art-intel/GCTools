/**
 * * $Id: EventCountFrame.java 22 2005-06-14 05:17:45Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/


package gcspy.vis;

import gcspy.interpreter.client.ClientInterpreter;
import gcspy.interpreter.client.EventListener;
import gcspy.vis.utils.AbstractFrame;
import gcspy.vis.utils.Factory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Window about event counting
 * @author Tony Printezis
 */
class EventCountFrame extends AbstractFrame
        implements EventListener {

    private ClientInterpreter interpreter;

    private EventCountTableModel model;

    private class CloseBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            setVisible(false);
        }
    }

    public void event(int eventID, int elapsedTime, int compensationTime) {
        int count[] = interpreter.getEventCount();
        model.setData(count);
    }

    private void setup(ClientInterpreter interpreter) {
        this.interpreter = interpreter;

        setTitle("GCspy: Event Counters");
        setResizable(false);

        Container cont = getContentPane();
        cont.setLayout(new BorderLayout());

        model = new EventCountTableModel(interpreter);
        JTable table = Factory.createTable(model);
        Factory.setColumnWidth(table, 0, 200);

        cont.add(table, BorderLayout.CENTER);
        cont.add(table.getTableHeader(), BorderLayout.NORTH);

        JPanel panel = Factory.createFlowPanel(FlowLayout.CENTER);
        JButton closeB = Factory.createButton("Close", true,
                new CloseBListener());
        panel.add(closeB);

        cont.add(panel, BorderLayout.SOUTH);
        pack();
    }

    /** Destroy this frame */
    public void destroy() {
        interpreter = null;
        super.destroy();
    }

    /**
     * Create a new event count frame
     * @param owner The parent frame
     * @param interpreter The client interpreter
     */
    EventCountFrame(AbstractFrame owner,
                    ClientInterpreter interpreter) {
        super(owner, Position.POS_BOTTOM_RIGHT, true);
        setup(interpreter);
    }

}
