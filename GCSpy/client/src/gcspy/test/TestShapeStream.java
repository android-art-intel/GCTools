/**
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 * <p>
 * *  gcspy.test.TestShapeStream
 * *
 * *  Tests the shape stream
 **/

/**
 **  gcspy.test.TestShapeStream
 **
 **  Tests the shape stream
 **/

package gcspy.test;

import gcspy.comm.BufferedInput;
import gcspy.comm.BufferedOutput;

public class TestShapeStream {

    static public void main(String args[]) {
        try {
            ShapeStream stream = new ShapeStream();
            byte buffer[] = new byte[32 * 1024];
            BufferedOutput output = new BufferedOutput(buffer);
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

            BufferedInput input = new BufferedInput(buffer, output.getLen());
            stream.execute(input);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
