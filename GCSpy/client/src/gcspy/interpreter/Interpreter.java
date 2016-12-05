/**
 * * $Id: Interpreter.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.interpreter;

import gcspy.comm.BufferedInput;
import gcspy.comm.BufferedOutput;
import gcspy.comm.Client;
import gcspy.comm.CommandStream;
import gcspy.utils.Verbose;

import java.io.IOException;

/**
 * Facilities shared by the client and server interpreters
 * @author Tony Printezis
 */
abstract public class Interpreter extends CommandStream {

    // Constants
    static private final String MAGIC_STRING = "GCspy666";
    static private final int ENDIANESS = 1;
    static protected final int DEFAULT_MAX_LEN = (4 * 1024 * 1024);

    // Shared data
    /** The name of the interpreter */
    protected String name;
    /** The client */
    protected Client client;
    /** The spaces */
    protected Space spaces[];
    /** The number of spaces */
    protected int spaceNum;

    /** The events */
    protected Events events;
    /** The event filters */
    protected EventFilters eventFilters;
    /** Counts of each event */
    protected int eventCount[];
    /** General information presented to the client */
    protected String generalInfo;

    private Verbose verbose = new Verbose(true);

    // Commands
    /** Client has requested pause command */
    static protected final byte PAUSE_REQ_CMD = FIRST_AVAILABLE_CMD;
    /** Server has paused command */
    static protected final byte PAUSE_CMD = FIRST_AVAILABLE_CMD + 1;
    /** Client has requested restart command */
    static protected final byte RESTART_CMD = FIRST_AVAILABLE_CMD + 2;
    /** Client has requested play one event command */
    static protected final byte PLAY_ONE_CMD = FIRST_AVAILABLE_CMD + 3;
    /** Client has requested shutdown command */
    static protected final byte SHUTDOWN_REQ_CMD = FIRST_AVAILABLE_CMD + 4;
    /** Server has shut down command*/
    static protected final byte SHUTDOWN_CMD = FIRST_AVAILABLE_CMD + 5;
    /** Start new stream command */
    static protected final byte STREAM_CMD = FIRST_AVAILABLE_CMD + 6;
    /** Event occurred command */
    static protected final byte EVENT_CMD = FIRST_AVAILABLE_CMD + 7;
    /** Start new control stream command */
    static protected final byte CONTROL_CMD = FIRST_AVAILABLE_CMD + 8;
    /** Event filters command */
    static protected final byte EVENT_FILTERS_CMD = FIRST_AVAILABLE_CMD + 9;
    /** Event count command */
    static protected final byte EVENT_COUNT_CMD = FIRST_AVAILABLE_CMD + 10;
    /** Summary information command */
    static protected final byte SUMMARY_CMD = FIRST_AVAILABLE_CMD + 11;
    /** Space information command */
    static protected final byte SPACE_INFO_CMD = FIRST_AVAILABLE_CMD + 12;
    /** Space command */
    static protected final byte SPACE_CMD = FIRST_AVAILABLE_CMD + 13;

    static private final byte CMD_LEN = SPACE_CMD + 1;

    /******************** Inner Classes ********************/

    protected class BootstrapParameters {
        public boolean pauseAtStart;

        public BootstrapParameters() {
        }

        public BootstrapParameters(boolean pauseAtStart) {
            this.pauseAtStart = pauseAtStart;
        }
    }

    /******************** Data Setting ********************/

    /**
     * Set verbosity
     * @param v verbosity on/off
     */
    public void setVerbose(boolean v) {
        verbose.setVerbose(v);
    }

    /**
     * Set the event counts
     * @param eventCount The event counts
     */
    public void setEventCount(int eventCount[]) {
        this.eventCount = eventCount;
    }

    /**
     * Set the general information
     * @param generalInfo the general information
     */
    public void setGeneralInfo(String generalInfo) {
        this.generalInfo = generalInfo;
    }

    /**
     * Set one space
     * @param space the space
     */
    public void setSpace(Space space) {
        spaces[space.getID()] = space;
    }

    /******************** Accessor Methods ********************/

    /**
     * Get the interpreter's name
     * @return the name of the interpreter
     */
    public String getName() {
        return name;
    }

    /**
     * Get the number of spaces
     * @return the number of spaces
     */
    public int getSpaceNum() {
        return spaceNum;
    }

    /**
     * Return a space
     * @param i The space's ID
     * @return the space with this ID
     */
    public Space getSpace(int i) {
        return spaces[i];
    }

    /**
     * Get the events
     * @return the events
     */
    public Events getEvents() {
        return events;
    }

    /**
     * Get the event filters
     * @return the event filterss
     */
    public EventFilters getEventFilters() {
        return eventFilters;
    }

    /**
     * Get the counts for each event
     * @return the numbers of events
     */
    public int[] getEventCount() {
        return eventCount;
    }

    /**
     * Get the general information for this interpreter
     * @return the general information
     */
    public String getGeneralInfo() {
        return generalInfo;
    }

    /******************** Utilities ********************/

    /**
     * Send a single command
     * @param cmd the command to send
     * @throws IOException
     */
    protected void sendSingleCommand(byte cmd)
            throws IOException {
        BufferedOutput output = client.createBufferedOutput();

        start(output);
        putCmd(output, cmd);
        finish(output);

        client.send(output);
    }

    /**
     * Print a message
     * @param type 0 for server, 1 for client
     * @param text the message
     */
    protected void println(int type, String text) {
        verbose.println("GCspy " +
                ((type == 0) ? "Server" : "Client") +
                ":  " + text);
    }

    /******************** Boot Info ********************/

    /**
     * Send boot information
     * @param server True if this is the server
     * @param params the BootStrapParameters
     * @throws IOException
     */
    protected void sendBootInfo(boolean server,
                                BootstrapParameters params)
            throws IOException {
        BufferedOutput output = client.createBufferedOutput();
        output.writeString(MAGIC_STRING);
        output.writeInt(ENDIANESS);

        if (server) {
            output.writeString(name);
        } else {
            output.writeBoolean(params.pauseAtStart);
        }
        client.send(output);
    }

    /**
     * Receive boot information
     * @param server True if this is the server
     * @param params the BootStrapParameters
     * @throws IOException
     * @throws InterpreterException if magic string or endianness do not match
     */
    protected void receiveBootInfo(boolean server,
                                   BootstrapParameters params)
            throws IOException {
        client.definitelyReceive();
        BufferedInput input = client.createBufferedInput();
        if (!input.readString().equals(MAGIC_STRING))
            throw new InterpreterException("Magic String does not match");
        if (input.readInt() != ENDIANESS)
            throw new InterpreterException("Endianess field does not match");

        if (!server) {
            name = input.readString();
        } else {
            params.pauseAtStart = input.readBoolean();
        }
        input.close();
    }

    /******************** Serialisation / Deserialisation ********************/

    /**
     * Serialise the space number, the general information and the events
     * @param output the BufferedOutput on which to serialise
     */
    public void serialise(BufferedOutput output) {
        output.writeShort((short) spaceNum);
        output.writeString(generalInfo);
        events.serialise(output);
    }

    /**
     * S
     * Deserialise the space number, the general information and the events
     * @param input the BufferedInput from which to deserialise
     */
    public void deserialise(BufferedInput input) {
        spaceNum = (int) input.readShort();
        generalInfo = input.readString();
        events = null;
        events = new Events();
        events.deserialise(input);
    }

    /**
     * Serialise everything (space number, general information, events, spaces)
     * @param generator A generator that will create a new BufferedOutput
     * @throws IOException
     */
    public void serialiseEverything(OutputGenerator generator)
            throws IOException {
        BufferedOutput output;

        output = generator.createBufferedOutput();
        serialise(output);
        generator.done(output.getLen());

        for (int i = 0; i < spaceNum; ++i) {
            Space space = spaces[i];

            output = generator.createBufferedOutput();
            space.serialise(output);
            generator.done(output.getLen());
        }
    }

    /**
     * Deserialise everything (space number, general information, events, spaces)
     * @param generator A generator that will create a new BufferedInput
     * @throws IOException
     */
    public void deserialiseEverything(InputGenerator generator)
            throws IOException {
        BufferedInput input;

        input = generator.createBufferedInput();
        deserialise(input);
        input.close();

        // if (verbose.verbose())
        //   dump();

        spaces = null;
        spaces = new Space[spaceNum];
        for (int i = 0; i < spaceNum; ++i) {
            Space space = generator.createSpace();

            input = generator.createBufferedInput();
            space.deserialise(input);
            input.close();

            // if (verbose.verbose())
            //   space.dump();

            spaces[i] = space;
        }
    }

    /******************** Debugging ********************/

    public void dump() {
        System.out.println("== Interpreter");
        System.out.println("--   Name: " + getName());
        System.out.println("--   " + spaceNum + " space(s)");
        events.dump();
    }

    public void dumpSpaces() {
        for (int i = 0; i < spaceNum; ++i)
            spaces[i].dump();
    }

    /******************** Constructors ********************/

    /**
     * Setup the event filters for all events
     */
    protected void setupEventFilters() {
        eventFilters = null;
        eventFilters = new EventFilters(events.getNum());
    }

    /**
     * Setup counts of events for all events
     */
    protected void setupEventCount() {
        eventCount = new int[events.getNum()];
        for (int i = 0; i < eventCount.length; ++i)
            eventCount[i] = 0;
    }

    /** Create an interpreter */
    public Interpreter() {
        super(CMD_LEN);
    }

    /**
     * Create an interpreter
     * @param name The nane of the interpreter
     */
    public Interpreter(String name) {
        this();
        this.name = name;
    }

}
