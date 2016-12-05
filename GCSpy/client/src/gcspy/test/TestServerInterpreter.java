/**
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 * <p>
 * *  gcspy.test.TestServerInterpreter
 * *
 * *  Test harness for the server interpreter
 * <p>
 * *  gcspy.test.TestServerInterpreter
 * *
 * *  Test harness for the server interpreter
 **/

/**
 **  gcspy.test.TestServerInterpreter
 **
 **  Test harness for the server interpreter
 **/

package gcspy.test;

import gcspy.interpreter.Events;
import gcspy.interpreter.server.ServerInterpreter;
import gcspy.utils.Utils;

import java.io.IOException;

public class TestServerInterpreter {

    static public void main(String args[]) {
        /* initialise the server */
        String eventNames[] = {"Young GC", "Old GC"};
        Events events = new Events(eventNames);
        int minEnd = 12500;
        int maxEnd = 36500;
        int end = minEnd;
        int blockSize = 1000;
        int step = 4 * blockSize;
        int count = 0;

        String generalInfo =
                "Test Server Interpreter\n" +
                        "\n" +
                        "General Info";
        ServerInterpreter interpreter =
                new ServerInterpreter("TestServerInterpreter",
                        true,
                        events,
                        2 /* max space number */);
        interpreter.setVerbose(true);
        interpreter.setGeneralInfo(generalInfo);

    /* initialise each driver */
        TestDriver driver = new TestDriver(interpreter,
                "Test Space",
                0,
                end,
                blockSize);

    /* usage */
        int port = Integer.parseInt(args[0]);
        try {
            interpreter.startServer(port,
                    true /* wait for client */);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int event = 0;
        while (true) {
                Utils.sleep(300);

                if (interpreter.shouldTransmit(event)) {
                    // System.out.println("transmitting...");

                    interpreter.startCompensationTimer();
                    if (count == 6) {
                        end += step;
                        if ((end == maxEnd) || (end == minEnd))
                            step = -step;
                        System.out.println("resizing - end = " + end);
                        driver.resize(end);
                        count = -1;
                    }
                    ++count;
                    driver.zero(9500);

	  /* the GC is supposed to transmit this */
                    driver.object(5);
                    driver.object(102);
                    driver.object(2045);
                    driver.object(1235);
                    driver.object(7000);

                    Utils.sleep(150);

	  /* this will transmit the control stream */
                    driver.finish();
                    interpreter.stopCompensationTimer();
                }
                interpreter.countingEventBoundary(event);
	/* alternate between 0 and 1 */
            event = 1 - event;
        }
    }

}
