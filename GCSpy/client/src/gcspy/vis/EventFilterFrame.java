/**
 * * $Id: EventFilterFrame.java 22 2005-06-14 05:17:45Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/


package gcspy.vis;

import gcspy.interpreter.EventFilters;
import gcspy.vis.utils.AbstractFrame;
import gcspy.vis.utils.Factory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Window about event filtering
 * @author Tony Printezis
 */
class EventFilterFrame extends AbstractFrame {

    private EventFilterTableModel model;

    private class EnableAllBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            model.enableAll();
        }
    }

    private class DisableAllBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            model.disableAll();
        }
    }

    private class ClearDelaysBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            model.clearDelays();
        }
    }

    private class ClearPausesBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            model.clearPauses();
        }
    }

    private class ResetPeriodsBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            model.resetPeriods();
        }
    }

    private class DefaultsBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            model.revertToDefaults();
        }
    }

    private class CloseBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            setVisible(false);
        }
    }

    private void setup(String eventNames[],
                       EventFilters eventFilters,
                       EventFilterListener listener) {
        setTitle("GCspy: Event Filters");
        setResizable(false);

        Container cont = getContentPane();
        JPanel panel;
        cont.setLayout(new BorderLayout());

        model = new EventFilterTableModel(eventNames, eventFilters);
        model.addListener(listener);
        JTable table = Factory.createTable(model);
        Factory.setColumnWidth(table, 0, 200);

        cont.add(table, BorderLayout.CENTER);
        cont.add(table.getTableHeader(), BorderLayout.NORTH);

        Container bottom = new Box(BoxLayout.Y_AXIS);

        panel = Factory.createFlowPanel(FlowLayout.CENTER);
        JButton clearDelaysB = Factory.createButton(" Clear Delays ", true,
                new ClearDelaysBListener());
        panel.add(clearDelaysB);
        JButton clearPausesB = Factory.createButton(" Clear Pauses ", true,
                new ClearPausesBListener());
        panel.add(clearPausesB);
        JButton resetPeriodsB = Factory.createButton("Reset Periods", true,
                new ResetPeriodsBListener());
        panel.add(resetPeriodsB);
        bottom.add(panel);

        panel = Factory.createFlowPanel(FlowLayout.CENTER);
        JButton enableAllB = Factory.createButton("Enable All", true,
                new EnableAllBListener());
        panel.add(enableAllB);
        JButton disableAllB = Factory.createButton("Disable All", true,
                new DisableAllBListener());
        panel.add(disableAllB);
        JButton defaultsB = Factory.createButton("Defaults", true,
                new DefaultsBListener());
        panel.add(defaultsB);
        JButton closeB = Factory.createButton("  Close  ", true,
                new CloseBListener());
        panel.add(closeB);
        bottom.add(panel);

        cont.add(bottom, BorderLayout.SOUTH);
        pack();
    }

    public void destroy() {
        super.destroy();
    }

    /**
     * Create a new event filter frame
     * @param owner The parent frame
     * @param eventNames The event names
     * @param eventFilters The event filters
     * @param listener A listener
     */
    EventFilterFrame(AbstractFrame owner,
                     String eventNames[],
                     EventFilters eventFilters,
                     EventFilterListener listener) {
        super(owner, Position.POS_BOTTOM_LEFT, true);
        setup(eventNames, eventFilters, listener);
    }

}
