/**
 * * $Id: BufferedOutput.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.comm;

import java.awt.*;

/**
 * Buffered output to a byte array
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
public class BufferedOutput {

    /* The buffer */
    private byte buffer[];
    /* The current position in the buffer */
    private int i;

    /**
     * Create a new output buffer
     * @param buffer the buffer
     */
    public BufferedOutput(byte buffer[]) {
        this.buffer = buffer;
        i = 0;
    }

    /**
     * Get the current length of the buffer
     * @return the current length (position) of the buffer
     */
    public int getLen() {
        return i;
    }

    /* return a new BufferedIOException if the buffer is full */
    private BufferedIOException raiseError() {
        return new BufferedIOException("Buffer is full, buffer length = " +
                buffer.length);
    }

    /*
     * Check that there is sufficient room in the buffer
     * param extra The room required (bytes)
     * throw a BufferedIOException if there is insufficient room left
     */
    private void checkLength(int extra) {
        if ((i + extra) > buffer.length) throw raiseError();
    }

    /*
     * Write a value into the buffer as a byte
     * param v The value to write
     */
    private void baseWrite(short v) {
        if (v > 127)
            v -= 256;
        buffer[i++] = (byte) v;
    }


    /**
     * Write a boolean into the buffer
     * @param b The value to write
     */
    public void writeBoolean(boolean b) {
        if (b)
            writeByte((byte) 1);
        else
            writeByte((byte) 0);
    }

    /**
     * Write a byte into the buffer
     * @param v The value to write
     */
    public void writeByte(byte v) {
        checkLength(1);
        baseWrite((short) v);
    }

    /**
     * Write an unsigned into the buffer
     * @param v The value to write
     */
    public void writeUByte(short v) {
        checkLength(1);
        baseWrite(v);
    }

    /**
     * Write a short into the buffer
     * @param v The value to write
     */
    public void writeShort(int v) {
        checkLength(2);
        baseWrite((short) (v >> 8));
        baseWrite((short) (v & BufferedIO.LOW_8_MASK));
    }

    /**
     * Write an unsigned short into the buffer
     * @param v The value to write
     */
    public void writeUShort(int v) {
        checkLength(2);
        baseWrite((short) (v >> 8));
        baseWrite((short) (v & BufferedIO.LOW_8_MASK));
    }

    /**
     * Write an int into the buffer
     * @param v The value to write
     */
    public void writeInt(int v) {
        checkLength(4);
        baseWrite((short) (v >> 24));
        baseWrite((short) ((v >> 16) & BufferedIO.LOW_8_MASK));
        baseWrite((short) ((v >> 8) & BufferedIO.LOW_8_MASK));
        baseWrite((short) (v & BufferedIO.LOW_8_MASK));
    }


    /**
     * Write a string into the buffer, terminated by 0.
     * @param v The string to write
     */
    public void writeString(String v) {
        byte bytes[] = v.getBytes();
        for (byte aByte : bytes) {
            writeByte(aByte);
        }
        writeByte((byte) 0);
    }

    /**
     * Write an array's length into the buffer
     * @param len the length to write
     */
    public void writeArrayLen(int len) {
        writeInt(len);
    }


    /**
     * Write a array of bytes into the buffer, preceded by its length
     * @param v the array to write
     */
    public void writeByteArray(byte v[]) {
        writeByteArray(v, v.length);
    }

    /**
     * Write a prefix of array of bytes into the buffer, preceded by its length
     * @param v the array
     * @param len the number of bytes to write
     */
    public void writeByteArray(byte v[], int len) {
        writeArrayLen(len);
        for (int i = 0; i < len; ++i)
            writeByte(v[i]);
    }


    /**
     * Write a array of unsigned bytes into the buffer, preceded by its length
     * @param v the array to write
     */
    public void writeUByteArray(short v[]) {
        writeUByteArray(v, v.length);
    }

    /**
     * Write a prefix of array of unsigned bytes into the buffer, preceded by its length
     * @param v the array
     * @param len the number of bytes to write
     */
    public void writeUByteArray(short v[], int len) {
        writeArrayLen(len);
        for (int i = 0; i < len; ++i)
            writeUByte(v[i]);
    }


    /**
     * Write a array of shorts into the buffer, preceded by its length
     * @param v the array to write
     */
    public void writeShortArray(short v[]) {
        writeShortArray(v, v.length);
    }

    /**
     * Write a prefix of array of shorts into the buffer, preceded by its length
     * @param v the array
     * @param len the number of bytes to write
     */
    public void writeShortArray(short v[], int len) {
        writeArrayLen(len);
        for (int i = 0; i < len; ++i)
            writeShort(v[i]);
    }


    /**
     * Write a array of unsigned shorts into the buffer, preceded by its length
     * @param v the array to write
     */
    public void writeUShortArray(int v[]) {
        writeUShortArray(v, v.length);
    }

    /**
     * Write a prefix of array of unsigned shorts into the buffer, preceded by its length
     * @param v the array
     * @param len the number of bytes to write
     */
    public void writeUShortArray(int v[], int len) {
        writeArrayLen(len);
        for (int i = 0; i < len; ++i)
            writeUShort(v[i]);
    }


    /**
     * Write a array of ints into the buffer, preceded by its length
     * @param v the array to write
     */
    public void writeIntArray(int v[]) {
        writeIntArray(v, v.length);
    }

    /**
     * Write a prefix of array of ins into the buffer, preceded by its length
     * @param v the array
     * @param len the number of bytes to write
     */
    public void writeIntArray(int v[], int len) {
        writeArrayLen(len);
        for (int i = 0; i < len; ++i)
            writeInt(v[i]);
    }

    /** Write a zero-length array into the buffer */
    public void writeEmptyArray() {
        writeArrayLen(0);
    }

    /**
     * Write a colour into the buffer
     * @param c The colour to write
     */
    public void writeColor(Color c) {
        writeUByte((short) c.getRed());
        writeUByte((short) c.getGreen());
        writeUByte((short) c.getBlue());
    }

    /** Close the buffer */
    public void close() {
        // currently a NOP - do I need this?
    }

}
