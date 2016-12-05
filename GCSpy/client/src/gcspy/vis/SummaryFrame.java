/**
 * * $Id: SummaryFrame.java 24 2005-06-17 09:37:56Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/


package gcspy.vis;

import gcspy.interpreter.Events;
import gcspy.interpreter.client.ClientInterpreter;
import gcspy.interpreter.client.ClientSpace;
import gcspy.interpreter.client.EventListener;
import gcspy.interpreter.client.SpaceListener;
import gcspy.utils.Utils;
import gcspy.vis.utils.AbstractFrame;
import gcspy.vis.utils.TextAreaFrame;

/**
 * Frame that holds space summaries
 * @author Tony Printezis
 */
class SummaryFrame extends TextAreaFrame
        implements EventListener, SpaceListener, DisconnectListener {

    // static private int EXTRA_ROWS = 2;
    static private int DEFAULT_ROWS = 10;

    private ClientInterpreter interpreter;
    private Events events;
    private ClientSpace space;

    public void disconnect(boolean reconnecting) {
        if (reconnecting) {
            setVisible(false);
            destroy();
        }
    }

    public void event(int eventID, int elapsedTime, int compensationTime) {
        int count[] = interpreter.getEventCount();
        String summaryStr = "Event: " + events.getName(eventID) +
                "  (" + Utils.formatSize(count[eventID]) + ")\n";
        summaryStr += "\n";
        summaryStr += space.presentSummary("", "   ");

        int lines = Utils.countLines(summaryStr);
        if (lines != textArea.getRows()) {
            textArea.setRows(lines);
            pack();
        }
        textArea.setText(summaryStr);
    }

    public void space(ClientSpace space) {
        if (this.space.getID() == space.getID())
            this.space = space;
    }

    private void setup(ClientInterpreter interpreter,
                       ClientSpace space) {
        this.interpreter = interpreter;
        events = interpreter.getEvents();
        this.space = space;

        interpreter.addEventListener(this);
        interpreter.addSpaceListener(this);

        String frameTitle = "Summary";
        String textAreaTitle = "Summary for " + space.getFullName();
        // int rows = space.getSummaryRows() + EXTRA_ROWS;
        int rows = DEFAULT_ROWS;
        int columns = 50;

        super.setup(frameTitle, textAreaTitle, rows, columns);
    }

    public void destroy() {
        interpreter = null;
        events = null;
        space = null;
        super.destroy();
    }

    /**
     * Create a new summary frame
     * @param owner The parent frame
     * @param interpreter The client interpreter
     * @param space The space
     */
    SummaryFrame(AbstractFrame owner,
                 ClientInterpreter interpreter,
                 ClientSpace space) {
        super(owner, Position.POS_RIGHT_COMPONENT, true);
        setup(interpreter, space);
    }

}
