/**
 * * $Id: BufferedInput.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.comm;

import java.awt.*;
import java.io.ByteArrayOutputStream;

/**
 * Buffered input from a byte array
 * The communication stream has the following wire format (b is a byte)
 * <pre>
 * byte           b
 * ubyte          b
 * short          bb
 * ushort         bb
 * false          0
 * true           1
 * int            bbbb
 * string         byte...byte0
 * len            int
 * array          len item...item
 * color          ubyte ubyte ubyte
 * </pre>
 * @author Tony Printezis
 */
public class BufferedInput {

    private byte buffer[];
    /* The current position in the buffer */
    private int i;
    /* The length of the buffer */
    private int len;

    /**
     * Create a new buffer
     * @param buffer The byte array buffer
     * @param len The buffer's length
     */
    public BufferedInput(byte buffer[], int len) {
        this.buffer = buffer;
        this.len = len;
        i = 0;
    }

    /**
     * Have we reached the end of the input buffer?
     * @return true if we have reached the end of the buffer
     */
    public boolean finished() {
        return (i == len);
    }

    /* return A BufferedIOException indicating that the end of the buffer has been reached */
    private BufferedIOException raiseError() {
        return new BufferedIOException("End of buffer reached, buffer length = " +
                buffer.length);
    }

    /*
     * Check whether there is sufficient room left in the buffer
     * param extra The room required (bytes)
     */
    private void checkLength(int extra) {
        if ((i + extra) > len) throw raiseError();
    }

    /* return the next value as a short from the buffer */
    private short baseRead() {
        short res = (short) buffer[i++];
        if (res < 0)
            res += 256;
        return res;
    }

    /**
     * Read the next boolean from the input buffer
     * @return the next value as a boolean from the buffer
     */
    public boolean readBoolean() {
        byte b = readByte();
        return (b == 1);
    }

    /**
     * Return the next byte from the input buffer
     * @return the next value as a byte from the buffer
     */
    public byte readByte() {
        checkLength(1);
        return (byte) baseRead();
    }

    /**
     * Read the nexct unsigned byte from the input buffer.
     * @return the next value as an unsigned byte from the buffer
     */
    public short readUByte() {
        checkLength(1);
        return baseRead();
    }

    /**
     * Read the next short from the input buffer
     * @return the next value as a short from the buffer
     */
    public short readShort() {
        checkLength(2);
        short res = baseRead();
        res <<= 8;
        res |= baseRead();
        return res;
    }

    /**
     * Read the next short from the input buffer
     * @return the next value as an unsigned short from the buffer
     */
    public int readUShort() {
        checkLength(2);
        int res = baseRead();
        res <<= 8;
        res |= baseRead();
        return res;
    }

    /**
     * Read the next int from the input buffer.
     * @return the next value as an int from the buffer
     */
    public int readInt() {
        checkLength(4);
        int res = (int) baseRead();
        res <<= 8;
        res |= (int) baseRead();
        res <<= 8;
        res |= (int) baseRead();
        res <<= 8;
        res |= (int) baseRead();
        return res;
    }


    /**
     * Read the next 0-terminated string from the buffer
     * @return the string
     */
    public String readString() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte b = readByte();
        while (b != 0) {
            stream.write((int) b);
            b = readByte();
        }
        return stream.toString();
    }


    /**
     * Read the length of an array (which follows) from the buffer
     * @return the array's length
     */
    public int readArrayLen() {
        return readInt();
    }

    /**
     * Read an array from the buffer. The first item is the length of the array
     * @return the next array of bytes read from the buffer
     */
    public byte[] readByteArray() {
        int len = readArrayLen();
        byte array[] = new byte[len];
        for (int i = 0; i < len; ++i)
            array[i] = readByte();
        return array;
    }

    /**
     * Read an array from the buffer. The first item is the length of the array
     * @return the next array of unsigned bytes read from the buffer
     */
    public short[] readUByteArray() {
        int len = readArrayLen();
        short array[] = new short[len];
        for (int i = 0; i < len; ++i)
            array[i] = readUByte();
        return array;
    }

    /**
     * Read an array from the buffer. The first item is the length of the array
     * @return the next array of shorts read from the buffer
     */
    public short[] readShortArray() {
        int len = readArrayLen();
        short array[] = new short[len];
        for (int i = 0; i < len; ++i)
            array[i] = readShort();
        return array;
    }

    /**
     * Read an array from the buffer. The first item is the length of the array
     * @return the next array of unsigned shorts read from the buffer
     */
    public int[] readUShortArray() {
        int len = readArrayLen();
        int array[] = new int[len];
        for (int i = 0; i < len; ++i)
            array[i] = readUShort();
        return array;
    }

    /**
     * Read an array from the buffer. The first item is the length of the array
     * @return the next array of ints read from the buffer
     */
    public int[] readIntArray() {
        int len = readArrayLen();
        int array[] = new int[len];
        for (int i = 0; i < len; ++i)
            array[i] = readInt();
        return array;
    }

    /**
     * Read a colour from the input buffer
     * @return the next colour from the buffer
     */
    public Color readColor() {
        int red = (int) readUByte();
        int blue = (int) readUByte();
        int green = (int) readUByte();
        return new Color(red, blue, green);
    }


    /**
     * Close the buffer
     * @throws a BufferedIOException if we haven't reached the end of the buffer
     */
    public void close() {
        if (!finished())
            throw new BufferedIOException("Buffered input not finished");
    }
}
