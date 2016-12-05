/**
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 * <p>
 * *  gcspy.test.TestShapeComm
 * *
 * *  Tests the comm and shape stream facilities
 **/

/**
 **  gcspy.test.TestShapeComm
 **
 **  Tests the comm and shape stream facilities
 **/

package gcspy.test;

import gcspy.comm.BufferedInput;
import gcspy.comm.BufferedOutput;
import gcspy.comm.Client;
import gcspy.comm.Server;

public class TestShapeComm {

    static private int MAX_LEN = (32 * 1024);

    static private void send(Client client,
                             ShapeStream stream)
            throws Exception {
        BufferedOutput output = client.createBufferedOutput();
        stream.setBufferedOutput(output);

        stream.start();
        stream.point(2, 3);
        stream.circle(3, 5, 2);
        stream.rectangle(2, 3, 10, 12);
        stream.rectangle(-2, -3, 3, 4);
        stream.point(4, 3);
        stream.point(1, 2);
        stream.circle(1, 2, 3);
        stream.finish();

        int len = output.getLen();
        System.out.println("== Sending " + len + " bytes");
        client.send(len);
        System.out.println();
    }

    static private void receive(Client client,
                                ShapeStream stream)
            throws Exception {
        System.out.println("== Receiving data");
        client.definitelyReceive();

        int len = client.getLen();
        System.out.println("==   got " + len + " bytes");

        byte buffer[] = client.getBufferIn();
        BufferedInput input = client.createBufferedInput();
        stream.execute(input);

        System.out.println();
    }

    static public void main(String args[]) {
        Server server = null;
        Client client;
        boolean mode = false;
        ShapeStream stream = new ShapeStream();

        if (args.length != 3) {
            System.out.println("## Wrong number of arguments");
            System.exit(-1);
        }

        String name = null;
        if (args[0].equals("server"))
            mode = true;
        else
            name = args[0];
        int port = Integer.parseInt(args[1]);
        int reps = Integer.parseInt(args[2]);

        try {
            if (mode) {
                System.out.println("== Starting server on port " + port);
                server = new Server(port);
                System.out.println("== Waiting for a new client");
                client = server.waitForNewClient(MAX_LEN);
            } else {
                System.out.println("== Looking for server " + name + ":" + port);
                client = new Client(name, port, MAX_LEN);
            }
            System.out.println("== Connection established");
            System.out.println();

            for (int i = 0; i < reps; ++i) {
                System.out.println("#### Repetition " + (i + 1) + " out of " + reps);
                System.out.println();
                if (mode) {
                    receive(client, stream);
                    receive(client, stream);
                    send(client, stream);
                    send(client, stream);
                } else {
                    send(client, stream);
                    send(client, stream);
                    receive(client, stream);
                    receive(client, stream);
                }
            }

            System.out.println("== Closing client");
            client.close();
            if (mode) {
                System.out.println("== Closing server");
                server.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
