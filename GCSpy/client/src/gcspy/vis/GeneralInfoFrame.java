/**
 * * $Id: GeneralInfoFrame.java 24 2005-06-17 09:37:56Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/


package gcspy.vis;


import gcspy.interpreter.client.ClientInterpreter;
import gcspy.utils.Utils;
import gcspy.vis.utils.AbstractFrame;
import gcspy.vis.utils.TextAreaFrame;

/**
 * Frame that holds the interpreter general info
 * @author Tony Printezis
 */
class GeneralInfoFrame extends TextAreaFrame
        implements DisconnectListener {

    static private final int MIN_ROWS = 1;
    static private final int MAX_ROWS = 20;

    public void disconnect(boolean reconnecting) {
        if (reconnecting) {
            setVisible(false);
            destroy();
        }
    }

    private void setup(ClientInterpreter interpreter) {
        String generalInfo = interpreter.getGeneralInfo();

        String frameTitle = "General Info";
        String textAreaTitle = "General Info";
        int rows = Utils.countChars(generalInfo, '\n') + 1;
        if (rows < MIN_ROWS)
            rows = MIN_ROWS;
        if (rows > MAX_ROWS)
            rows = MAX_ROWS;
        int columns = 50;

        super.setup(frameTitle, textAreaTitle, rows, columns);
        textArea.setText(generalInfo);
    }

    public void destroy() {
        super.destroy();
    }

    /**
     * Create a new information frame
     * @param owner The parent frame
     * @param interpreter The client interpreter
     */
    GeneralInfoFrame(AbstractFrame owner,
                     ClientInterpreter interpreter) {
        super(owner, Position.POS_TOP_LEFT, true);
        setup(interpreter);
    }

}
