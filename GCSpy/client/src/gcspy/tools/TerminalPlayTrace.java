/**
 * * $Id: TerminalPlayTrace.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.tools;

import gcspy.interpreter.Stream;
import gcspy.interpreter.server.ServerInterpreter;
import gcspy.interpreter.server.ServerSpace;
import gcspy.utils.Arguments;
import gcspy.utils.FileUtils;
import gcspy.utils.Timer;
import gcspy.utils.Utils;

import java.io.IOException;

/**
 * Plays a trace from a file
 * @author Tony Printezis
 */
public class TerminalPlayTrace extends FileTrace {

    private ServerInterpreter interpreter;

    private boolean ignoreDelay = true;
    /* if false, then should use delay */
    private int delay = 0;
    private boolean compensate = false;
    private int count;
    private int eventCount[];

  /* ****************** Connection ******************* */

    protected void stream(int spaceID, int streamID, Object data, int summary[])
            throws IOException {
        Stream stream = interpreter.getSpace(spaceID).getStream(streamID);
        stream.setData(data);
        stream.setSummary(summary);
    }

    protected void control(int spaceID, byte control[]) throws IOException {
        interpreter.getServerSpace(spaceID).setControl(control);
    }

    protected void eventCount(int eventCount[]) throws IOException {
        this.eventCount = eventCount;
        interpreter.setEventCount(eventCount);
    }

    protected void event(int eventID, int elapsedTime, int compensationTime)
            throws IOException {
        if (ignoreDelay) {
            if (elapsedTime != 0) {
                int diff = elapsedTime - compensationTime;
                if (diff > 0) {
                    Utils.sleep(diff);
                }
            }
        } else {
            Utils.sleep(delay);
        }
        ++count;
        if (interpreter.shouldTransmit(eventID)) {
            if (ignoreDelay && (!compensate))
                Utils.sleep(compensationTime);
            println(count + ". EVENT  '" + interpreter.getEvents().getName(eventID)
                    + "'  (" + eventID + ")\n" + "    count = " + eventCount[eventID]
                    + ",  elapsed = " + elapsedTime + "ms,  compensation = "
                    + compensationTime + "ms");
        }
        interpreter.eventBoundary(eventID, elapsedTime, compensationTime);
    }

    protected void spaceInfo(int spaceID, String spaceInfo) {
        interpreter.getSpace(spaceID).setSpaceInfo(spaceInfo);
    }

    protected void space(ServerSpace space) throws IOException {
        interpreter.setSpace(space);
        space.flagChanged();
    }

    private void go(int port, boolean wait) throws IOException {
        Timer timer = new Timer();

        System.out.println("Input file: " + fileName);
        setupInput();

        System.out.println("Compression type: " + getCompressionType());
        System.out.print("Timing: ");

        if (!ignoreDelay)
            System.out.println("DELAY " + delay + "ms");
        else if (compensate)
            System.out.println("COMPENSATED");
        else
            System.out.println("ORIGINAL");
        println();

        println("Reading bootstrap information");
        interpreter.deserialiseEverything(this);
        // println(" DONE.");

        println("Starting server at port " + port);
        interpreter.startServer(port, wait);
        // println(" DONE.");
        println();

        timer.start();
        parse();
        timer.stop();

        close();

        System.out.println();
        System.out.println("Summary:");
        System.out.println("    Read " + count + " events");
        System.out.println("    Playing time was " + (timer.getTime() / 1000)
                + " secs");
        System.out.println();
        System.exit(0);
    }

    /** ****************** Constructor ******************* */

    private TerminalPlayTrace(boolean verbose, boolean iVerbose, String fileName,
                              int delay) throws IOException {
        this(verbose, iVerbose, fileName);
        this.ignoreDelay = false;
        this.delay = delay;
        this.count = 0;
    }

    private TerminalPlayTrace(boolean verbose, boolean iVerbose, String fileName,
                              boolean compensate) throws IOException {
        this(verbose, iVerbose, fileName);
        this.compensate = compensate;
    }

    private TerminalPlayTrace(boolean verbose, boolean iVerbose, String fileName)
            throws IOException {
        super(fileName, verbose);

        if (!FileUtils.fileExists(fileName)) {
            reportError("    File '" + fileName + "' does not exists.");
            System.exit(-1);
        }

        System.out.println("-- gcspy.tools.TerminalPlayTrace starting");
        System.out.println();

        interpreter = new ServerInterpreter("java.tools.TerminalPlayTrace", false);
        interpreter.setVerbose(iVerbose);
    }

    /** ****************** Main ******************* */

    static private void usage(String mesg) {
        System.out.println();
        System.out.println("usage:");
        System.out.println("    gcspy.tools.TerminalPlayTrace <options>");
        System.out.println();
        System.out.println("where <options> are");
        System.out.println("    -port <port>");
        System.out.println("    -input <file name>");
        System.out.println("    -wait (optional)");
        System.out.println("    -delay <delay in ms> (optional)");
        System.out.println("    -compensate (optional)");
        System.out.println("    -verbose (optional)");
        System.out.println("    -iverbose (optional)");
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
        TerminalPlayTrace playTrace;
        Arguments arguments = new Arguments();
        boolean ignoreDelay = true;
        int delay = 0;
        boolean compensate = false;

        int portType[] = {Arguments.POS_INT_TYPE};
        arguments.add("-port", true, portType);

        int fileType[] = {Arguments.STRING_TYPE};
        arguments.add("-input", true, fileType);

        arguments.add("-wait");

        int delayType[] = {Arguments.POS_INT_TYPE};
        String delayExcluded[] = {"-compensate"};
        arguments.add("-delay", false, delayType, null, delayExcluded);

        String compensateExcluded[] = {"-delay"};
        arguments.add("-compensate", null, compensateExcluded);

        arguments.add("-verbose");

        arguments.add("-iverbose");

        if (!arguments.parse(args)) {
            usage(arguments.getError());
        }

        Object values[];

        values = arguments.getValues("-port");
        int port = ((Integer) values[0]).intValue();

        values = arguments.getValues("-input");
        String fileName = (String) values[0];

        boolean wait = arguments.isSet("-wait");

        if (arguments.isSet("-delay")) {
            ignoreDelay = false;
            values = arguments.getValues("-delay");
            delay = ((Integer) values[0]).intValue();
        }

        compensate = arguments.isSet("-compensate");

        boolean verbose = arguments.isSet("-verbose");

        boolean iVerbose = arguments.isSet("-iverbose");

        try {
            if (ignoreDelay) {
                playTrace = new TerminalPlayTrace(verbose, iVerbose, fileName,
                        compensate);
            } else {
                playTrace = new TerminalPlayTrace(verbose, iVerbose, fileName, delay);
            }
            playTrace.go(port, wait);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
