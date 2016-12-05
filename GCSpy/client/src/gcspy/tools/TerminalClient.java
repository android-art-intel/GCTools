/**
 * * $Id: TerminalClient.java 21 2005-06-11 00:25:23Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.tools;

import gcspy.interpreter.DataAccessor;
import gcspy.interpreter.Space;
import gcspy.interpreter.Stream;
import gcspy.interpreter.client.ClientInterpreter;
import gcspy.interpreter.client.ClientSpace;
import gcspy.interpreter.client.EventListener;
import gcspy.interpreter.client.SpaceListener;
import gcspy.utils.Arguments;

/**
 * Dumps the transmission from the server to the terminal in ASCII format
 * @author Tony Printezis
 * @author <a href="http://www/cs/kent.ac.uk">Richard Jones</a>
 */
public class TerminalClient
        implements EventListener, SpaceListener {

    private ClientInterpreter interpreter;

    /******************** Utilities ********************/

    private String getEventName(int eventID) {
        return interpreter.getEvents().getName(eventID) + " (" + eventID + ")";
    }

    private String getSpaceName(int spaceID) {
        return interpreter.getSpace(spaceID).getFullName();
    }

    private String getStreamName(int spaceID, int streamID) {
        return interpreter.getSpace(spaceID).getStream(streamID).getName();
    }

    /******************** For Listeners ********************/

    public void event(int eventID, int elapsedTime, int compensationTime) {
        System.out.println("## EVENT Command received");
        System.out.println("     Event = " + getEventName(eventID));
        System.out.println("     Elapsed Time = " + elapsedTime + "ms");
        System.out.println("     Compensation Time = " + compensationTime + "ms");
        System.out.println();
        dumpAllData();
    }

    public void space(ClientSpace space) {
        System.out.println("## SPACE Command received");
        System.out.println("     Space = " + space.getID());
        interpreter.dumpSpaces();
        System.out.println();
    }

    private void dumpAllData() {
        int eventCount[] = interpreter.getEventCount();

        System.out.println("  Event Counts");
        for (int i = 0; i < eventCount.length; ++i) {
            System.out.println("     " + getEventName(i) + " = " +
                    eventCount[i]);
        }
        System.out.println();

        for (int i = 0; i < interpreter.getSpaceNum(); ++i) {
            ClientSpace space = interpreter.getClientSpace(i);
            int tileNum = space.getTileNum();
            byte control[] = space.getControl();
            System.out.println("  Space[" + i + "]: " + space.getFullName());

            System.out.println("     Control:");
            System.out.print("        Data:");
            for (int k = 0; k < tileNum; ++k) {
                if (Space.isControlUsed(control[k]))
                    System.out.print(" x");
                else if (Space.isControlBackground(control[k]))
                    System.out.print(" #");
                else if (Space.isControlUnused(control[k]))
                    System.out.print(" -");
            }
            System.out.println();

            for (int j = 0; j < space.getStreamNum(); ++j) {
                Stream stream = space.getStream(j);
                System.out.println("     Stream[" + j + "]: " + stream.getName());
                DataAccessor accessor = stream.getAccessor();
                System.out.print("        Data:");
                for (int k = 0; k < tileNum; ++k) {
                    if (Space.isControlUsed(control[k]))
                        System.out.print(" " + accessor.get(k));
                    else if (Space.isControlBackground(control[k]))
                        System.out.print(" #");
                    else if (Space.isControlUnused(control[k]))
                        System.out.print(" -");
                }
                System.out.println();
            }

            System.out.println("     Summary:");
            System.out.print(space.presentSummary("        ", "           "));
        }
        System.out.println();
    }

    /******************** Connection ********************/

    private void connect(String host, int port) {
        try {
            interpreter.connectToServer(host, port, false);

            interpreter.dump();
            interpreter.dumpSpaces();
            System.out.println();

            interpreter.addEventListener(this);
            interpreter.addSpaceListener(this);
            interpreter.enableEventListeners();

            interpreter.mainLoop();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /******************** Constructor ********************/

    private TerminalClient() {
        interpreter = new ClientInterpreter();
    }

    /******************** Main ********************/

    static private void usage(String mesg) {
        System.out.println();
        System.out.println("usage:");
        System.out.println("    gcspy.tools.TerminalStoreTrace <options>");
        System.out.println();
        System.out.println("where <options> are");
        System.out.println("    -server <host> <port>");
        if (mesg != null) {
            reportError(mesg);
        }
        System.exit(-1);
    }

    static private void reportError(String mesg) {
        System.out.println();
        System.out.println("Error:");
        System.out.println("    " + mesg);
        System.out.println();
    }

    static public void main(String args[]) {
        Arguments arguments = new Arguments();

        int serverParams[] = {Arguments.STRING_TYPE, Arguments.POS_INT_TYPE};
        arguments.add("-server", true, serverParams);

        if (!arguments.parse(args)) {
            usage(arguments.getError());
        }

        Object values[];

        values = arguments.getValues("-server");
        String host = (String) values[0];
        int port = ((Integer) values[1]).intValue();

        System.out.println("-- gcspy.tools.TerminalClient starting");
        System.out.println();

        TerminalClient client = new TerminalClient();
        client.connect(host, port);
    }

}
