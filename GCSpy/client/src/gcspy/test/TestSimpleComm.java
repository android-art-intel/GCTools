/**
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 * <p>
 * *  gcspy.test.TestSimpleComm
 * *
 * *  Tests the buffered I/O facilities and the comms framework
 **/

/**
 **  gcspy.test.TestSimpleComm
 **
 **  Tests the buffered I/O facilities and the comms framework
 **/

package gcspy.test;

import gcspy.comm.BufferedInput;
import gcspy.comm.BufferedOutput;
import gcspy.comm.Client;
import gcspy.comm.Server;

public class TestSimpleComm {

    static private int MAX_LEN = (32 * 1024);

    static private void send(Client client,
                             byte b,
                             short ub,
                             short s,
                             int us,
                             int i,
                             String string,
                             int mult)
            throws Exception {
        BufferedOutput out = client.createBufferedOutput();
        out.writeByte(b);
        out.writeUByte(ub);
        out.writeShort(s);
        out.writeUShort(us);
        out.writeInt(i);
        out.writeString(string);
        for (int n = 0; n < 15; ++n) {
            out.writeInt(n * mult);
        }
        int len = out.getLen();

        System.out.println("== Sending " + len + " bytes");
        client.send(len);
        System.out.println();
    }

    static private void receive(Client client)
            throws Exception {
        System.out.println("== Receiving data");
        client.definitelyReceive();

        int len = client.getLen();
        System.out.println("==   got " + len + " bytes");

        byte buffer[] = client.getBufferIn();
        BufferedInput in = client.createBufferedInput();
        byte b = in.readByte();
        short ub = in.readUByte();
        short s = in.readShort();
        int us = in.readUShort();
        int i = in.readInt();
        String string = in.readString();

        System.out.println("==   b = " + b + ", ub = " + ub +
                ", s = " + s + ", us = " + us +
                ", i = " + i +
                ", string = " + string);
        System.out.print("==   Rest:");
        while (!in.finished()) {
            System.out.print(" " + in.readInt());
        }
        System.out.println();
        System.out.println();
    }

    static public void main(String args[]) {
        Server server = null;
        Client client;
        boolean mode = false;

        if (args.length != 2) {
            System.out.println("## Wrong number of arguments");
            System.exit(-1);
        }

        String name = null;
        if (args[0].equals("server"))
            mode = true;
        else
            name = args[0];
        int port = Integer.parseInt(args[1]);

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

            if (mode) {
                receive(client);
                receive(client);
                receive(client);
                send(client, (byte) 1, (short) 130, (short) 511, (int) 1076,
                        2000666, "a J string!", 3);
                send(client, (byte) -3, (short) 5, (short) -600, (int) 100,
                        -1000000, "another J string!", -1);
                send(client, (byte) 100, (short) 255, (short) 20, (int) 50000,
                        100, "a final J string!", 2);
            } else {
                send(client, (byte) 1, (short) 130, (short) 511, (int) 1076,
                        2000666, "a J string!", 3);
                send(client, (byte) -3, (short) 5, (short) -600, (int) 100,
                        -1000000, "another J string!", -1);
                send(client, (byte) 100, (short) 255, (short) 20, (int) 50000,
                        100, "a final J string!", 2);
                receive(client);
                receive(client);
                receive(client);
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
