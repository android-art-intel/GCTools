/**
 * * $Id: CommandStream.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.comm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Parses a buffered I/O stream and invokes the commands in it
 *
 * @author Tony Printezis
 */
public class CommandStream {

    /** An array of GCspy commands */
    protected Command cmds[];

    /** The index of the largest numbered command */
    protected int max;

    /** The output buffer */
    protected BufferedOutput output;

    static private final int MAGIC_TAG_START = 666666;

    static private final int MAGIC_TAG_END = -666;

    static private final byte END_STREAM = (byte) 0;

    /** The index of the first command */
    protected static final int FIRST_AVAILABLE_CMD = END_STREAM + 1;

    /** Configure dump input stream */
    private final boolean DUMP_INPUT = false;

    /**
     * Create a new array of commands
     * @param cmds The new commands
     */
    public CommandStream(Command cmds[]) {
        this.cmds = cmds;
        max = cmds.length - 1;
    }

    /**
     * Create a new array of commands
     * @param len The number of commands
     */
    public CommandStream(int len) {
        this(new Command[len]);
    }

    /** Create an emty command stream */
    public CommandStream() {
        cmds = null;
    }

    /**
     * Read a command and execute it
     * @param input The BufferedInput from which to read the command
     */
    public void execute(BufferedInput input) throws IOException {
        int magic = input.readInt();
        FileOutputStream fos = null;
        if (DUMP_INPUT) {
            File logDir = new File("log");
            if (!logDir.exists()) {
                boolean suc = logDir.mkdir();
                if (!suc) {
                    throw new IOException("file to create directory for logs.");
                }
            }
            File file = new File("log/dump-" + System.currentTimeMillis() + ".log");
            if (!file.exists() && !file.createNewFile())
                throw new IOException("Failed to create dump file: " + file.getAbsolutePath());
            fos = new FileOutputStream(file, true);
            fos.write(String.format("Start magic: %d\n", magic).getBytes());
        }
        if (magic != MAGIC_TAG_START)
            throw new CommandStreamException("  Wrong first magic number: " + magic);

        byte cmd = getCmd(input);
        while (cmd != END_STREAM) {
            if ((cmd > max) || (cmd < 0))
                throw new CommandStreamException("Command out of bounds: " + cmd);
            cmds[cmd].execute(input, fos);
            cmd = getCmd(input);
        }

        magic = input.readInt();
        if (DUMP_INPUT) {
            fos.write(String.format("End magic: %d\n", magic).getBytes());
            fos.close();
        }
        if (magic != MAGIC_TAG_END)
            throw new CommandStreamException("Wrong second magic number: " + magic);
        input.close();
    }

    /**
     * Set the Buffered Output.
     * used when we are creating a command stream looks a bit nicer rather than
     * always passing the output
     */
    public void setBufferedOutput(BufferedOutput output) {
        this.output = output;
    }

    /** Start the command stream */
    public void start() {
        start(output);
    }

    /**
     * Start a command using a BufferedOutput
     * @param output The BufferedOutput
     */
    protected void start(BufferedOutput output) {
        output.writeInt(MAGIC_TAG_START);
    }

    /** Finish the command stream */
    public void finish() {
        finish(output);
    }

    /**
     * Finish the command stream for a BufferedOutput
     * @param output The BufferedOutput
     */
    public void finish(BufferedOutput output) {
        putCmd(output, END_STREAM);
        output.writeInt(MAGIC_TAG_END);
    }

    /**
     * Put a command into the output buffer
     * @param cmd The command
     */
    public void putCmd(byte cmd) {
        putCmd(output, cmd);
    }

    /**
     * Put a command into a BufferedOutput
     * @param output The BufferedOutput
     * @param cmd The command
     */
    public void putCmd(BufferedOutput output, byte cmd) {
        output.writeByte(cmd);
    }

    /**
     * Get a command from a BufferedInput
     * @param input The VufferedInput
     * @return The command
     */
    public byte getCmd(BufferedInput input) {
        return input.readByte();
    }

}
