/**
 * * $Id: Space.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/


package gcspy.interpreter;

import gcspy.comm.BufferedInput;
import gcspy.comm.BufferedOutput;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents a space
 * @author Tony Printezis
 */
abstract public class Space {

    // Constants
    /** Control value for a used tile */
    static public final byte CONTROL_USED = 1;
    /** Control value for a background tile */
    static public final byte CONTROL_BACKGROUND = 2;
    /** Control value for an unused tile */
    static public final byte CONTROL_UNUSED = 4;
    /** Control value for a separator
     *  the separator goes <emph>before</emph> the tile */
    static public final byte CONTROL_SEPARATOR = 8;
    /** Control value for a control link
     *  the link goes <emph>after</emph> the tile */
    static public final byte CONTROL_LINK = 16;

    /** Default string for unused tiles */
    static protected final String DEFAULT_UNUSED_STRING = "NOT USED";

    // Configuration
    /** The space's ID */
    protected int id;
    /** The space's name */
    protected String name;
    /** The name of the space's driver */
    protected String driverName;
    /** The space's title */
    protected String title;
    /** The block information for a space */
    protected String blockInfo;
    /** The number of tiles */
    protected int tileNum;
    /** The names of the tiles */
    protected String tileNames[];
    /** The number of streams */
    protected int streamNum;
    /** The streams */
    protected Stream streams[];
    /** The space information */
    protected String spaceInfo;
    /** The string to use for unused tiles */
    protected String unusedString;
    /** Is this the main space? */
    protected boolean mainSpace;
    /** The controls for this space */
    protected byte control[];

    /******************** Data Setting ********************/

    /**
     * Set this space's ID
     * @param id the ID
     */
    public void setID(int id) {
        this.id = id;
    }

    /**
     * Set the controls
     * @param control the controls
     */
    public void setControl(byte control[]) {
        this.control = control;
    }

    /**
     * Set the space information
     * @param spaceInfo The space information
     */
    public void setSpaceInfo(String spaceInfo) {
        this.spaceInfo = spaceInfo;
    }

    /******************** Accessor Methods ********************/

    /**
     * Get the space's ID
     * @return the space ID
     */
    public int getID() {
        return id;
    }

    /**
     * Get the space's name
     * @return the space's name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the space's full name
     * @return the name of the space and its driver
     */
    public String getFullName() {
        return name + "  [" + driverName + "]";
    }

    /**
     * Get the space's title
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the space's block information
     * @return the block information for this space
     */
    public String getBlockInfo() {
        return blockInfo;
    }

    /**
     * Get teh number of streams in this space
     * @return the number of streams
     */
    public int getStreamNum() {
        return streamNum;
    }

    /**
     * Get the number of tiles in this space
     *@return the number of tiles
     */
    public int getTileNum() {
        return tileNum;
    }

    /**
     * Return the name of a tile
     * @param i the index of the tile
     * @return its name
     */
    public String getTileName(int i) {
        return tileNames[i];
    }

    /**
     * Return a stream
     * @param streamID the ID of the stream
     * @return the stream
     */
    public Stream getStream(int streamID) {
        return streams[streamID];
    }

    /**
     * Get the controls for this space
     * @return the controls
     */
    public byte[] getControl() {
        return control;
    }

    /**
     * Get the space information for this space
     * @return the space information
     */
    public String getSpaceInfo() {
        return spaceInfo;
    }

    /**
     * Get the string used for unused tiles
     * @return the string for unused tiles
     */
    public String getUnusedString() {
        return unusedString;
    }

    /**
     * Is this the main space in the visualiser?
     * @return true if this is the main space
     */
    public boolean isMainSpace() {
        return mainSpace;
    }

    /******************** Utilities ********************/

    /** Setup ne controls an initialise them to unused */
    protected void setupControl() {
        control = new byte[tileNum];
        initControl();
    }

    /** Initialise all the controls to unused */
    protected void initControl() {
        for (int i = 0; i < control.length; ++i)
            control[i] = CONTROL_USED;
    }

    /******************** Control ********************/

    /**
     * Is this value a used control?
     * @param val A control
     * @return true if the tile is used
     */
    static public boolean isControlUsed(byte val) {
        return (val & CONTROL_USED) != 0;
    }

    /**
     * Is this value a background control?
     * @param val A control
     * @return true if the tile is a background tile
     */
    static public boolean isControlBackground(byte val) {
        return (val & CONTROL_BACKGROUND) != 0;
    }

    /**
     * Is this value an unused control?
     * @param val A control
     * @return true if the tile is unused
     */
    static public boolean isControlUnused(byte val) {
        return (val & CONTROL_UNUSED) != 0;
    }

    /**
     * Is this value a separator control?
     * @param val A control
     * @return true if the tile is a separator
     */
    static public boolean isControlSeparator(byte val) {
        return (val & CONTROL_SEPARATOR) != 0;
    }

    /**
     * Is this value a link control?
     * @param val A control
     * @return true if the tile is a control link
     */
    static public boolean isControlLink(byte val) {
        return (val & CONTROL_LINK) != 0;
    }

    /******************** Serialisation / Deserialisation ********************/

    /**
     * Serialise the space
     * @param output The BufferedOutput to use
     */
    public void serialise(BufferedOutput output) {
        output.writeShort((short) id);
        output.writeString(name);
        output.writeString(driverName);
        output.writeString(title);
        output.writeString(blockInfo);
        output.writeInt(tileNum);
        output.writeString(unusedString);
        output.writeBoolean(mainSpace);
        output.writeShort((short) streamNum);
        for (int i = 0; i < streamNum; ++i)
            streams[i].serialise(output);
        serialiseTileNames(output);
    }

    /**
     * Deserialise the space
     * @param input The BufferedInput to use
     */
    public void deserialise(BufferedInput input) {
        id = (int) input.readShort();
        name = input.readString();
        driverName = input.readString();
        title = input.readString();
        blockInfo = input.readString();
        tileNum = input.readInt();
        unusedString = input.readString();
        mainSpace = input.readBoolean();
        streamNum = (int) input.readShort();
        streams = new Stream[streamNum];
        for (int i = 0; i < streamNum; ++i) {
            streams[i] = new Stream();
            streams[i].deserialise(input);
            streams[i].setSpace(this);
        }
        deserialiseTileNames(input);
        setupControl();
    }

    private void serialiseTileNames(BufferedOutput output) {
        for (int i = 0; i < tileNum; ++i)
            output.writeString(tileNames[i]);
    }

    private void deserialiseTileNames(BufferedInput input) {
        tileNames = new String[tileNum];
        for (int i = 0; i < tileNum; ++i)
            tileNames[i] = input.readString();
    }

    /******************** Debugging ********************/

    public void dump() {
        System.out.println("== Space[" + id + "]: " + getFullName());
        System.out.println("--   " + tileNum + " tiles");
        System.out.println("--   " + streamNum + " streams");
        System.out.println("--   tile names");
        for (int i = 0; i < tileNum; ++i)
            System.out.println("--     " + tileNames[i]);
        for (int i = 0; i < streamNum; ++i)
            streams[i].dump();
    }

    public void dump(OutputStream os) throws IOException {
        os.write(String.format("Space[%d]: %s\n", id, getFullName()).getBytes());
        os.write(String.format("-- %d tiles\n-- %d streams\n", tileNum, streamNum).getBytes());
        os.write("--   tile names\n".getBytes());
        for (int i = 0; i < tileNum; ++i)
            os.write(String.format("--     %s\n", tileNames[i]).getBytes());
        for (int i = 0; i < streamNum; ++i)
            streams[i].dump(os);
    }
    /******************** Constructors ********************/

    public Space() {
    }

}
