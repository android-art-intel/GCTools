/**
 * * $Id: Client.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.comm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * All the socket stuff wrapped up
 *
 * @author Tony Printezis
 */
public class Client {

    private Socket client;

    private int maxLen;

    private OutputStream out;

    private InputStream in;

    private byte inBuffer[];

    private int len;

    private byte tmpBuffer[];

    private int tmpLen;

    private byte outBuffer[];

    /** INIT * */

    /**
     * Create a new client
     * @param socket The socket on which to communicate
     * @param maxLen Maximum buffer size
     * @throws IOException
     */
    Client(Socket socket, int maxLen) throws IOException {
        client = socket;

        out = client.getOutputStream();
        in = client.getInputStream();

        this.maxLen = maxLen;
        client.setReceiveBufferSize(maxLen);
        client.setSendBufferSize(maxLen);

        inBuffer = new byte[maxLen];

        tmpLen = 0;
        tmpBuffer = new byte[2 * maxLen];

        outBuffer = new byte[maxLen];
    }

    /**
     * Create a new client
     * @param server The name of the server
     * @param port The port number
     * @param maxLen Maximum length of the stream
     * @throws IOException
     */
    public Client(String server, int port, int maxLen) throws IOException {
        this(new Socket(server, port), maxLen);
    }

    /**
     * Close the client
     *
     * @throws IOException
     */
    public void close() throws IOException {
        client.close();
    }

    /** DELIMITERS * */

    static final private int DEL_SIZE = 4;

    /*
     * Read the length of an array from tmpBuffer @return the length
     */
    private int readStreamLen() {
        return ArrayInput.readInt(tmpBuffer, 0);
    }

    /*
     * Write a length (in the GCspy wire format) to the output stream
     * @param len
     * @throws IOException
     */
    private void writeStreamLen(int len) throws IOException {
        byte buffer[] = new byte[DEL_SIZE];
        ArrayOutput.writeInt(buffer, 0, len);
        out.write(buffer);
    }

    /** SENDING * */

  /*
   * Send a prefix of a buffer 
   * @param buffer the buffer to send 
   * @param its length
   */
    private void baseSend(byte buffer[], int len) throws IOException {
        writeStreamLen(len);
        out.write(buffer, 0, len);
        out.flush();
    }

    /**
     * Send a stream using a buffer
     * @param buffer The buffer.
     * @throws IOException
     */
    public void send(byte buffer[]) throws IOException {
        baseSend(buffer, buffer.length);
    }

    /**
     * Send a stream using the prefix of a buffer
     * @param buffer the buffer to send
     * @param len its length
     */
    public void send(byte buffer[], int len) throws IOException {
        baseSend(buffer, len);
    }

    /**
     * Send the outBuffer as a stream
     * @param len the length of the buffer to send
     * @throws IOException
     */
    public void send(int len) throws IOException {
        baseSend(outBuffer, len);
    }

    /**
     * Send a stream
     * @param output the BufferedOutput to send
     * @throws IOException
     */
    public void send(BufferedOutput output) throws IOException {
        send(output.getLen());
    }

    /**
     * Return the output buffer
     * @return the out buffer
     */
    public byte[] getBufferOut() {
        return outBuffer;
    }

    /** RECEIVING * */

    /**
     * Receive a stream into the inBuffer
     */
    public void definitelyReceive() throws IOException {
        receive();
        if (hasTerminated())
            throw new IOException("communication was unexpectedly terminated");
    }

    /**
     * Receive a stream into the inBuffer
     * @throws IOException
     */
    public void receive() throws IOException {
        int cur;
        int totalLen;

        while (tmpLen < DEL_SIZE) {
            cur = in.read(tmpBuffer, tmpLen, maxLen);
            if (cur <= 0) {
                len = 0;
                tmpLen = 0;
                return;
            }
            tmpLen += cur;
        }
        len = readStreamLen();

        totalLen = len + DEL_SIZE;
        while (tmpLen < totalLen) {
            cur = in.read(tmpBuffer, tmpLen, maxLen);
            if (cur <= 0) {
                len = 0;
                tmpLen = 0;
                return;
            }
            tmpLen += cur;
        }

        System.arraycopy(tmpBuffer, DEL_SIZE, inBuffer, 0, len);

        if (tmpLen > totalLen) {
            System.arraycopy(tmpBuffer, totalLen, tmpBuffer, 0, tmpLen - totalLen);
            tmpLen -= totalLen;
        } else {
            tmpLen = 0;
        }
    }

    /**
     * Return the input buffer
     * @return the in buffer
     */
    public byte[] getBufferIn() {
        return inBuffer;
    }

    /**
     * Has the input terminated?
     * @return true if len <= 0
     */
    public boolean hasTerminated() {
        return len <= 0;
    }

    /**
     *Return the length of the in/out buffer
     *@return the lenght
     */
    public int getLen() {
        return len;
    }

    /**
     * Create a new input buffered
     * @return a new BufferedInput using the in buffer
     */
    public BufferedInput createBufferedInput() {
        return new BufferedInput(getBufferIn(), getLen());
    }

    /**
     * Creatre a new output buffer
     * @return a new BufferedOutput using the out buffer
     */
    public BufferedOutput createBufferedOutput() {
        return new BufferedOutput(getBufferOut());
    }

}
