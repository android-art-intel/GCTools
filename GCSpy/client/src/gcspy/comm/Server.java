/**
 * * $Id: Server.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.comm;

import gcspy.utils.Utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * More socket stuff wrapped out
 * @author Tony Printezis
 */
public class Server {
    private ServerSocket server;

    /**
     * Create a new Server
     * @param port The port to communicate on
     * @param backlog The server socket backlog
     * @throws IOException
     */
    public Server(int port,
                  int backlog)
            throws IOException {
        server = new ServerSocket(port, backlog);
    }

    /**
     * Create a new Server
     * @param port The port to communicate on
     * @throws IOException
     */
    public Server(int port)
            throws IOException {
        server = new ServerSocket(port);
    }

    /**
     * Wait for a new client
     * @param maxLen The maximum length of the stream
     * @return a new client
     * @throws IOException
     */
    public Client waitForNewClient(int maxLen)
            throws IOException {
        Socket socket = server.accept();
        return new Client(socket, maxLen);
    }

    /**
     * Close the server
     * @throws IOException
     */
    public void close()
            throws IOException {
        Utils.sleep(50);
        server.close();
    }

}
