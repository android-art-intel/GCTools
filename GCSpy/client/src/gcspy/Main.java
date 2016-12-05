/**
 * * $id$
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy;

import gcspy.utils.Arguments;
import gcspy.vis.MainFrame;

/**
 *  Main class of GCspy
 * @author Tony Printezis
 */
public class Main {

    /**
     * Print usage messages
     * @param message An error message
     */
    static private void usage(String message) {
        System.out.println();
        System.out.println("usage:");
        System.out.println("    gcspy.Main <options>");
        System.out.println();
        System.out.println("where <options> are");
        System.out.println("    -server <host> <port>");
        System.out.println("        specifies where the server is");
        System.out.println("        e.g. -server host 3000");
        System.out.println("    -connect (requires -server)");
        System.out.println("        connects immediately");
        System.out.println("    -pause");
        System.out.println("        pauses immediately after it connects");
        System.out.println("    -small (optional)");
        System.out.println("        uses small tiles");
        System.out.println("    -tiny (optional)");
        System.out.println("        uses tiny tiles");
        System.out.println();
        if (message != null) {
            System.out.println("Error:");
            System.out.println("    " + message);
            System.out.println();
        }

        System.exit(-1);
    }

    /** GCspy main entry point */
    static public void main(String args[]) {
        Arguments arguments = new Arguments();

        int serverParams[] = {Arguments.STRING_TYPE, Arguments.POS_INT_TYPE};
        Object serverValues[] = {"localhost", 3000};
        arguments.add("-server", false, serverParams, serverValues);
        String connectRequires[] = {"-server"};
        arguments.add("-connect", connectRequires, null);
        String pauseRequires[] = {"-server"};
        arguments.add("-pause", pauseRequires, null);
        String smallExcluded[] = {"-tiny"};
        arguments.add("-small", null, smallExcluded);
        String tinyExcluded[] = {"-small"};
        arguments.add("-tiny", null, tinyExcluded);

        if (!arguments.parse(args))
            usage(arguments.getError());

        Object values[] = arguments.getValues("-server");
        String host = (String) values[0];
        int port = (Integer) values[1];
        boolean connect = arguments.isSet("-connect");
        boolean pause = arguments.isSet("-pause");
        boolean small = arguments.isSet("-small");
        boolean tiny = arguments.isSet("-tiny");

        MainFrame frame = new MainFrame(host, port, small, tiny, pause, connect);
        frame.setSize(1024, 720);
        frame.setVisible(true);
    }

}
