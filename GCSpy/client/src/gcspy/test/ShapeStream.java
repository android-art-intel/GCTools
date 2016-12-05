/**
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 * <p>
 * *  gcspy.test.ShapeStream
 * *
 * *  Instantiation of the command stream
 **/

/**
 **  gcspy.test.ShapeStream
 **
 **  Instantiation of the command stream
 **/

package gcspy.test;

import gcspy.comm.BufferedInput;
import gcspy.comm.Command;
import gcspy.comm.CommandStream;

import java.io.IOException;
import java.io.OutputStream;

public class ShapeStream extends CommandStream {

    static private final byte POINT_CMD = CommandStream.FIRST_AVAILABLE_CMD;
    static private final byte CIRCLE_CMD = POINT_CMD + 1;
    static private final byte RECTANGLE_CMD = CIRCLE_CMD + 1;
    static private final byte LAST_CMD = RECTANGLE_CMD;

    private Point point = new Point();
    private Circle circle = new Circle();
    private Rectangle rectangle = new Rectangle();

    public ShapeStream() {
        super(LAST_CMD + 1);
        cmds[POINT_CMD] = point;
        cmds[CIRCLE_CMD] = circle;
        cmds[RECTANGLE_CMD] = rectangle;
    }

    private class Point implements Command {
        public void execute(BufferedInput in, OutputStream os) throws IOException {
            int x = in.readInt();
            int y = in.readInt();
            os.write(("Point (" + x + ", " + y + ")\n").getBytes());
        }

        void execute(int x, int y) {
            putCmd(POINT_CMD);
            output.writeInt(x);
            output.writeInt(y);
        }
    }

    private class Circle implements Command {
        public void execute(BufferedInput in, OutputStream os) throws IOException {
            int x = in.readInt();
            int y = in.readInt();
            int rad = in.readInt();
            os.write(("Circle (" + x + ", " + y + "), " + rad + "\n").getBytes());
        }

        void execute(int x, int y, int rad) {
            putCmd(CIRCLE_CMD);
            output.writeInt(x);
            output.writeInt(y);
            output.writeInt(rad);
        }
    }

    private class Rectangle implements Command {
        public void execute(BufferedInput in, OutputStream os) throws IOException {
            int x1 = in.readInt();
            int y1 = in.readInt();
            int x2 = in.readInt();
            int y2 = in.readInt();
            os.write(("Rectangle (" + x1 + ", " + y1 + "), (" +
                    x2 + ", " + y2 + ")\n").getBytes());
        }

        void execute(int x1, int y1, int x2, int y2) {
            putCmd(RECTANGLE_CMD);
            output.writeInt(x1);
            output.writeInt(y1);
            output.writeInt(x2);
            output.writeInt(y2);
        }
    }

    public void point(int x, int y) {
        point.execute(x, y);
    }

    public void circle(int x, int y, int rad) {
        circle.execute(x, y, rad);
    }

    public void rectangle(int x1, int y1, int x2, int y2) {
        rectangle.execute(x1, y1, x2, y2);
    }

}
