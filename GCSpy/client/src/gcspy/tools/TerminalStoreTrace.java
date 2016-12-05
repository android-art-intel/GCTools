/**
 * * $Id: TerminalStoreTrace.java 21 2005-06-11 00:25:23Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.tools;

import gcspy.interpreter.Stream;
import gcspy.interpreter.client.ClientInterpreter;
import gcspy.interpreter.client.ClientSpace;
import gcspy.interpreter.client.EventListener;
import gcspy.interpreter.client.SpaceListener;
import gcspy.utils.Arguments;
import gcspy.utils.FileUtils;
import gcspy.utils.Timer;
import gcspy.utils.Utils;

import java.io.IOException;
import java.util.Date;

/**
 * Dumps the transmission from the server to the terminal in ASCII format
 * @author Tony Printezis
 */
public class TerminalStoreTrace extends FileTrace
        implements EventListener, SpaceListener {

    private ClientInterpreter interpreter;
    private int count, maxCount;

    /******************** For Listeners ********************/

    private boolean shouldReturn() {
        return ((maxCount > 0) && (count >= maxCount));
    }

    public void event(int eventID, int elapsedTime, int compensationTime) {
        if (shouldReturn())
            return;
        ++count;

        int counts[] = interpreter.getEventCount();

        println(count + ". EVENT  '" +
                interpreter.getEvents().getName(eventID) +
                "'  (" + eventID + ")\n" +
                "    count = " + counts[eventID] +
                ",  elapsed = " + elapsedTime + "ms,  compensation = " +
                compensationTime + "ms");

        try {
            dumpStreams(eventID, elapsedTime, compensationTime);
            if (count == maxCount) {
                println();
                println("Requesting shutdown");
                interpreter.sendShutdownReq();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void space(ClientSpace space) {
        writeSpace(space);
    }

    private void dumpStreams(int eventID, int elapsedTime, int compensationTime)
            throws IOException {
        int counts[] = interpreter.getEventCount();
        writeEventCount(counts);

        int spaceNum = interpreter.getSpaceNum();
        for (int spaceID = 0; spaceID < spaceNum; ++spaceID) {
            ClientSpace space = interpreter.getClientSpace(spaceID);
            writeSpaceInfo(spaceID, space.getSpaceInfo());
            int streamNum = space.getStreamNum();
            for (int streamID = 0; streamID < streamNum; ++streamID) {
                Stream stream = space.getStream(streamID);
                Object data = stream.getData();
                int summary[] = stream.getSummary();

                writeStream(spaceID, streamID, data, summary);
            }

            byte control[] = space.getControl();
            writeControl(spaceID, control);
        }

        writeEvent(eventID, elapsedTime, compensationTime);
    }

    /******************** Connection ********************/

    private void ammendGeneralInfo(String host, int port) {
        String str = interpreter.getGeneralInfo();
        Date date = new Date();

        String ammended =
                "Trace Stored By:\n" +
                        "  gcspy.tools.TerminalStoreTrace\n" +
                        "\n" +
                        "Trace File:\n  " +
                        fileName + "\n" +
                        "Recorded On:\n  " +
                        Utils.formatDateTime(date) + "\n" +
                        "Host:\n  " +
                        host + ":" + port + "\n" +
                        "\n" +
                        "----------\n" +
                        "\n" +
                        str;

        interpreter.setGeneralInfo(ammended);
    }

    private void go(int maxCount)
            throws IOException {
        Timer timer = new Timer();

        this.maxCount = maxCount;
        this.count = 0;

        System.out.println("Output file: " + fileName);
        setupOutput();

        System.out.println("Compression type: " + getCompressionType());
        if (maxCount > 0)
            System.out.println("Max events: " + maxCount);
        println();

        println("Writing bootstrap information");
        interpreter.serialiseEverything(this);
        // println("    DONE.");
        println();

        interpreter.addEventListener(this);
        interpreter.addSpaceListener(this);
        interpreter.enableEventListeners();

        timer.start();
        interpreter.mainLoop();
        timer.stop();

        close();

        long length = FileUtils.fileLength(fileName);
        double kbs = (double) length / 1024.0;
        double mbs = (double) kbs / 1024.0;
        System.out.println();
        System.out.println("Summary:");
        System.out.println("    Wrote " + Utils.formatSize(mbs) + " MBs (" +
                Utils.formatSize(kbs) + " KBs, " +
                Utils.formatSize(length) + " bytes)");
        System.out.println("    Wrote " + count + " events");
        System.out.println("    Storing time was " +
                (timer.getTime() / 1000) + " secs");
        System.out.println();
    }

    /******************** Constructor ********************/

    private TerminalStoreTrace(String host,
                               int port,
                               boolean overwrite,
                               boolean verbose,
                               boolean iVerbose,
                               String fileName)
            throws IOException {
        super(fileName, verbose);

        if (!overwrite) {
            if (FileUtils.fileExists(fileName)) {
                reportError("    File '" + fileName + "' already exists.");
                System.exit(-1);
            }
        }

        System.out.println("-- gcspy.tools.TerminalStoreTrace starting");
        System.out.println();

        interpreter = new ClientInterpreter();
        interpreter.setVerbose(iVerbose);

        println("Connecting to " + host + ":" + port);
        interpreter.connectToServer(host, port, false);
        // println("    DONE.");
        println();

        ammendGeneralInfo(host, port);
    }

    /******************** Main ********************/

    static private void usage(String mesg) {
        System.out.println();
        System.out.println("usage:");
        System.out.println("    gcspy.tools.TerminalStoreTrace <options>");
        System.out.println();
        System.out.println("where <options> are");
        System.out.println("    -server <host> <port>");
        System.out.println("    -output <file name>");
        System.out.println("    -count <count> (optional)");
        System.out.println("    -overwrite (optional)");
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
        TerminalStoreTrace storeTrace;
        Arguments arguments = new Arguments();

        int serverParams[] = {Arguments.STRING_TYPE, Arguments.POS_INT_TYPE};
        arguments.add("-server", true, serverParams);

        int fileParam[] = {Arguments.STRING_TYPE};
        arguments.add("-output", true, fileParam);

        arguments.add("-overwrite");

        int countParam[] = {Arguments.POS_INT_TYPE};
        Object countDefault[] = {-1};
        arguments.add("-count", false, countParam, countDefault);

        arguments.add("-verbose");

        arguments.add("-iverbose");


        if (!arguments.parse(args)) {
            usage(arguments.getError());
        }


        Object values[];

        values = arguments.getValues("-server");
        String host = (String) values[0];
        int port = ((Integer) values[1]).intValue();

        values = arguments.getValues("-output");
        String fileName = (String) values[0];

        boolean overwrite = arguments.isSet("-overwrite");

        values = arguments.getValues("-count");
        int count = ((Integer) values[0]).intValue();

        boolean verbose = arguments.isSet("-verbose");

        boolean iVerbose = arguments.isSet("-iverbose");

        try {
            storeTrace = new TerminalStoreTrace(host, port,
                    overwrite,
                    verbose, iVerbose,
                    fileName);
            storeTrace.go(count);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
