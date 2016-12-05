/**
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 * <p>
 * *  gcspy.test.TestBufferedIO
 * *
 * *  Tests the buffered I/O classes
 **/

/**
 **  gcspy.test.TestBufferedIO
 **
 **  Tests the buffered I/O classes
 **/

package gcspy.test;

import gcspy.comm.BufferedInput;
import gcspy.comm.BufferedOutput;

public class TestBufferedIO {

    static public void main(String args[]) {
        try {
            byte array[] = new byte[1024];
            BufferedOutput out = new BufferedOutput(array);
            out.writeByte((byte) 1);
            out.writeUByte((short) 255);
            out.writeShort((short) 511);
            out.writeUShort(1034);
            out.writeInt(2000666);
            out.writeString("This is a string!");
            for (int n = 0; n < 15; ++n) {
                out.writeInt(n);
            }
            int len = out.getLen();

            BufferedInput in = new BufferedInput(array, len);
            byte b = in.readByte();
            short ub = in.readUByte();
            short s = in.readShort();
            int us = in.readUShort();
            int i = in.readInt();
            String string = in.readString();

            System.out.println("b = " + b + ", ub = " + ub + ", s = " + s +
                    ", us = " + us + ", i = " + i +
                    ", string = " + string);
            System.out.print("Rest:");
            while (!in.finished()) {
                System.out.print(" " + in.readInt());
            }
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
