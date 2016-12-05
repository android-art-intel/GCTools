/**
 * * $Id: FileTrace.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 * <p>
 * *  gcspy.tools.FileTrace
 * *
 * *  Contains facilities to store / retrieve traces to / from files
 * <p>
 * *  gcspy.tools.FileTrace
 * *
 * *  Contains facilities to store / retrieve traces to / from files
 **/

/**
 **  gcspy.tools.FileTrace
 **
 **  Contains facilities to store / retrieve traces to / from files
 **/

package gcspy.tools;

import gcspy.comm.BufferedInput;
import gcspy.comm.BufferedOutput;
import gcspy.interpreter.InputGenerator;
import gcspy.interpreter.OutputGenerator;
import gcspy.interpreter.Space;
import gcspy.interpreter.server.ServerSpace;
import gcspy.utils.FileUtils;
import gcspy.utils.Verbose;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.*;

/**
 * Contains facilities to store / retrieve traces to / from files
 *
 * @author Tony Printezis
 */
public class FileTrace extends Verbose implements OutputGenerator,
        InputGenerator {

    static private final int MAX_LEN = (256 * 1024);

    static private final byte STREAM_CMD = 0;
    static private final byte CONTROL_CMD = 1;
    static private final byte EVENT_COUNT_CMD = 2;
    static private final byte EVENT_CMD = 3;
    static private final byte SPACE_INFO_CMD = 4;
    static private final byte SPACE_CMD = 5;

    static private final String CMD_NAMES[] = {"STREAM", "CONTROL",
            "EVENT COUNT", "EVENT", "SPACE INFO", "SPACE"};

    static private final int COMPRESSION_UNSET = 0;
    /** No compression */
    static public final int COMPRESSION_NONE = 1;
    /** Use gzip format */
    static public final int COMPRESSION_GZIP = 2;
    /** Use zip format */
    static public final int COMPRESSION_ZIP = 3;

    static private final String COMPRESSION_NAMES[] = {"", "NONE", "GZIP", "ZIP"};

    static private final int DEFAULT_BUFFER_LEN = (256 * 1024);

    private OutputStream foutput = null;
    private FileTraceOutputStream output = null;

    private InputStream finput = null;
    private FileTraceInputStream input = null;
    private ZipFile zFile = null;

    /** The name of the file containng the trace */
    String fileName;
    private int bufferLen;
    private int compression;

    private byte buffer[];

    /** ****************** Accessor Methods ******************* */

    /**
     * Get the files compression  type (e.g. non, gzip, zip, etc)
     * @return the type
     */
    protected String getCompressionType() {
        return COMPRESSION_NAMES[compression];
    }

  /* ****************** Object Input/Output ******************* */

    private class FileTraceOutputStream extends ObjectOutputStream {
        public void customWriteObject(Object obj) throws IOException {
            ByteArrayOutputStream aoutput = new ByteArrayOutputStream();
            ObjectOutputStream ooutput = new ObjectOutputStream(aoutput);
            ooutput.writeObject(obj);
            byte arr[] = aoutput.toByteArray();
            ooutput.close();
            aoutput.close();

            writeInt(arr.length);
            write(arr, 0, arr.length);
        }

        public void writeTag(int tag) throws IOException {
            writeByte((byte) tag);
        }

        public FileTraceOutputStream(OutputStream out) throws IOException {
            super(out);
        }
    }

    private class FileTraceInputStream extends ObjectInputStream {
        public Object customReadObject() throws IOException, ClassNotFoundException {
            int length = readInt();
            byte arr[] = new byte[length];
            FileUtils.read(this, arr, 0, length);

            ByteArrayInputStream ainput = new ByteArrayInputStream(arr);
            ObjectInputStream oinput = new FileTraceInputStream(ainput);
            Object obj = oinput.readObject();
            oinput.close();
            ainput.close();

            return obj;
        }

        public int readTag() throws IOException {
            return (int) readByte();
        }

        public FileTraceInputStream(InputStream in) throws IOException {
            super(in);
        }
    }

    /** ****************** Output ******************* */

    /** *** OutputGenerator **** */

    /**
     * Create a buffered output
     *
     * @return the bufferd outpu object
     */
    public BufferedOutput createBufferedOutput() throws IOException {
        return new BufferedOutput(buffer);
    }

    /**
     * Complete writing a stream
     *
     * @param len
     *          the lenght of the stream
     */
    public void done(int len) throws IOException {
        output.writeInt(len);
        output.write(buffer, 0, len);
    }

    /** *** STREAM **** */

    /**
     * Write a stream
     *
     * @param spaceID
     *          the space's ID
     * @param streamID
     *          The stream's ID
     * @param data
     *          The stream data
     * @param summary
     *          The stream's summary values
     */
    public void writeStream(int spaceID, int streamID, Object data, int summary[]) {
        try {
            output.writeTag(STREAM_CMD);
            output.writeByte((byte) spaceID);
            output.writeByte((byte) streamID);
            output.customWriteObject(data);
            output.customWriteObject(summary);
            // writeObject(output, data);
            // writeObject(output, summary);
        } catch (IOException e) {
            throw new FileTraceException(e);
        }
    }

    /** *** CONTROL **** */

    /**
     * Write the controls for a space
     *
     * @param spaceID
     *          The space's ID
     * @param control
     *          The controls
     */
    public void writeControl(int spaceID, byte control[]) {
        try {
            output.writeTag(CONTROL_CMD);
            output.writeByte((byte) spaceID);
            output.customWriteObject(control);
            // writeObject(output, control);
        } catch (IOException e) {
            throw new FileTraceException(e);
        }
    }

    /** *** EVENT_COUNT **** */

    /**
     * Write event counts
     *
     * @param counts
     *          The counts
     */
    public void writeEventCount(int counts[]) {
        try {
            output.writeTag(EVENT_COUNT_CMD);
            output.customWriteObject(counts);
            // writeObject(output, counts);
        } catch (IOException e) {
            throw new FileTraceException(e);
        }
    }

    /** *** EVENT **** */

    /**
     * Write an event
     *
     * @param eventID
     *          The event's iD
     * @param elapsedTime
     *          The eleapsed time for the event
     * @param compensationTime
     *          The compensation time
     */
    public void writeEvent(int eventID, int elapsedTime, int compensationTime) {
        try {
            output.writeTag(EVENT_CMD);
            output.writeByte((byte) eventID);
            output.writeInt(elapsedTime);
            output.writeInt(compensationTime);
        } catch (IOException e) {
            throw new FileTraceException(e);
        }
    }

    /** *** SPACE INFO **** */

    /**
     * Write the space inforrmation for a space
     *
     * @param spaceID
     *          the space's ID
     * @param spaceInfo
     *          the space information
     */
    public void writeSpaceInfo(int spaceID, String spaceInfo) {
        try {
            output.writeTag(SPACE_INFO_CMD);
            output.writeByte((byte) spaceID);
            if (spaceInfo != null) {
                output.customWriteObject(spaceInfo);
                // writeObject(output, spaceInfo);
            } else {
                output.customWriteObject("");
                // writeObject(output, "");
            }
        } catch (IOException e) {
            throw new FileTraceException(e);
        }
    }

    /** *** SPACE **** */

    /**
     * Write a space
     *
     * @param space
     *          The space
     */
    public void writeSpace(Space space) {
        try {
            output.writeTag(SPACE_CMD);
            BufferedOutput output = createBufferedOutput();
            space.serialise(output);
            done(output.getLen());
        } catch (IOException e) {
            throw new FileTraceException(e);
        }
    }

    /** ****************** Input ******************* */

    /** *** InputGenerator **** */

    /**
     * Create a new buffered input
     *
     * @return The new buffered input
     * @throws IOException
     */
    public BufferedInput createBufferedInput() throws IOException {
        int len = input.readInt();
        FileUtils.read(input, buffer, 0, len);
        return new BufferedInput(buffer, len);
    }

    // is this safe?

    /**
     * Create a new space
     * @return a new space
     */
    public Space createSpace() {
        // is this right?
        return new ServerSpace();
    }

    /** *** Parsing **** */

    /**
     * Handle a stream's data and dummary values for a stream
     * @param spaceID The space's ID
     * @param streamID The stream's ID
     * @param data The stream's data
     * @param summary The summary values
     * @throws IOException
     */
    protected void stream(int spaceID, int streamID, Object data, int summary[])
            throws IOException {
    }

    /**
     * Handle a space's controls
     * @param spaceID The space's ID
     * @param control The controls
     * @throws IOException
     */
    protected void control(int spaceID, byte control[]) throws IOException {
    }

    /**
     * Handle event counts
     * @param counts The event counts
     * @throws IOException
     */
    protected void eventCount(int counts[]) throws IOException {
    }

    /**
     * Handle an event
     * @param eventID The event's ID
     * @param elapsedTime The elapsed time for the event
     * @param compensationTime Its compensation time
     * @throws IOException
     */
    protected void event(int eventID, int elapsedTime, int compensationTime)
            throws IOException {
    }

    /**
     * Handle space information
     * @param spaceID The space's ID
     * @param spaceInfo Its space information
     * @throws IOException
     */
    protected void spaceInfo(int spaceID, String spaceInfo) throws IOException {
    }

    /**
     * Handle a space
     * @param space The space
     * @throws IOException
     */
    protected void space(ServerSpace space) throws IOException {
    }

    /**
     * Parse input
     * @throws IOException
     */
    public void parse() throws IOException {
        int spaceID, streamID;
        int eventID;
        int elapsedTime, compensationTime;
        Object data;
        int summary[];
        byte control[];
        int counts[];
        String spaceInfo;

        while (true) {
            try {
                int tag = input.readTag();
                switch (tag) {
                    case STREAM_CMD:
                        spaceID = (int) input.readByte();
                        streamID = (int) input.readByte();
                        data = input.customReadObject();
                        summary = (int[]) input.customReadObject();
                        // Data = readObject(input);
                        // summary = (int[]) readObject(input);
                        stream(spaceID, streamID, data, summary);
                        break;
                    case CONTROL_CMD:
                        spaceID = (int) input.readByte();
                        control = (byte[]) input.customReadObject();
                        // control = (byte[]) readObject(input);
                        control(spaceID, control);
                        break;
                    case EVENT_COUNT_CMD:
                        counts = (int[]) input.customReadObject();
                        // counts = (int[]) readObject(input);
                        eventCount(counts);
                        break;
                    case EVENT_CMD:
                        eventID = (int) input.readByte();
                        elapsedTime = input.readInt();
                        compensationTime = input.readInt();
                        event(eventID, elapsedTime, compensationTime);
                        break;
                    case SPACE_INFO_CMD:
                        spaceID = (int) input.readByte();
                        spaceInfo = (String) input.customReadObject();
                        // spaceInfo = (String) readObject(input);
                        spaceInfo(spaceID, spaceInfo);
                        break;
                    case SPACE_CMD:
                        BufferedInput input = createBufferedInput();
                        ServerSpace space = (ServerSpace) createSpace();
                        space.deserialise(input);
                        space(space);
                        break;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (EOFException e) {
            }
        }
    }

    /** ****************** Constructors ******************* */

    private String getEntryName() {
        // remove the .zip from the file name
        int len = fileName.length();
        String name = fileName.substring(0, len - 4);
        if (name.length() == 0)
            name = "trace";
        return name;
    }

    private void setCompression() {
        if (compression == COMPRESSION_UNSET) {
            if (fileName.endsWith(".gz"))
                compression = COMPRESSION_GZIP;
            else if (fileName.endsWith(".zip"))
                compression = COMPRESSION_ZIP;
            else
                compression = COMPRESSION_NONE;
        }
    }

    /** Set up the input */
    public void setupInput() {
        InputStream cinput;
        ZipEntry entry;

        try {
            setCompression();

            switch (compression) {
                case COMPRESSION_NONE:
                    finput = new FileInputStream(fileName);
                    input = new FileTraceInputStream(finput);
                    break;
                case COMPRESSION_GZIP:
                    finput = new FileInputStream(fileName);
                    cinput = new GZIPInputStream(finput);
                    input = new FileTraceInputStream(cinput);
                    break;
                case COMPRESSION_ZIP:
                    zFile = new ZipFile(fileName);
                    Enumeration<? extends ZipEntry> enumm = zFile.entries();
                    // only get the first entry
                    entry = enumm.nextElement();
                    cinput = zFile.getInputStream(entry);
                    input = new FileTraceInputStream(cinput);
                    break;
            }
        } catch (IOException e) {
            throw new FileTraceException(e);
        }
    }

    /** Set up the output */
    public void setupOutput() {
        ZipOutputStream zoutput;
        OutputStream coutput;
        ZipEntry entry;
        String entryName;

        try {
            setCompression();

            foutput = new FileOutputStream(fileName);
            switch (compression) {
                case COMPRESSION_NONE:
                    output = new FileTraceOutputStream(foutput);
                    break;
                case COMPRESSION_GZIP:
                    coutput = new GZIPOutputStream(foutput);
                    output = new FileTraceOutputStream(coutput);
                    break;
                case COMPRESSION_ZIP:
                    zoutput = new ZipOutputStream(foutput);
                    entryName = getEntryName();
                    entry = new ZipEntry(entryName);
                    zoutput.putNextEntry(entry);
                    output = new FileTraceOutputStream(zoutput);
                    break;
            }
        } catch (IOException e) {
            throw new FileTraceException(e);
        }
    }

    /** Close the input / output */
    public void close() {
        try {
            if (input != null) {
                input.close();
                if (finput != null)
                    finput.close();
                if (zFile != null)
                    zFile.close();
            }
            if (output != null) {
                output.close();
                foutput.close();
            }
        } catch (IOException e) {
            throw new FileTraceException(e);
        }
    }

    /**
     * Create a new FileTrace with default buffer length
     * @param fileName The name of the file
     * @param verbose SHould we be verbose?
     */
    public FileTrace(String fileName, boolean verbose) {
        this(fileName, DEFAULT_BUFFER_LEN, verbose);
    }

    /**
     * Create a new FileTrace with default buffer length and
     * compression not set
     * @param fileName The name of the file
     * @param bufferLen The length of the buffer
     * @param verbose SHould we be verbose?
     */
    public FileTrace(String fileName, int bufferLen, boolean verbose) {
        this(fileName, bufferLen, verbose, COMPRESSION_UNSET);
    }

    /**
     * Create a new FileTrace with default buffer length with
     * compression not set
     * @param fileName The name of the file
     * @param bufferLen The length of the buffer
     * @param verbose SHould we be verbose?
     * @param compression The compression type
     */
    public FileTrace(String fileName, int bufferLen, boolean verbose,
                     int compression) {
        super(verbose);
        this.fileName = fileName;
        this.bufferLen = bufferLen;
        this.compression = compression;
        buffer = new byte[bufferLen];
    }

}
