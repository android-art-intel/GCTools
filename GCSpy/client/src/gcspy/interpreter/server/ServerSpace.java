/**
 * * $Id: ServerSpace.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.interpreter.server;

import gcspy.interpreter.Space;
import gcspy.interpreter.Stream;

/**
 * Represents a server-side space
 *
 * @author Tony Printezis
 */
public class ServerSpace extends Space {

    private int tilesToSend;
    private boolean changed;

  /* ****************** Setting Data ******************* */

    /**
     * Set a tile's name
     *
     * @param tile the tile index
     * @param name the name of the tile
     */
    public void setTileName(int tile, String name) {
        tileNames[tile] = name;
    }

    /**
     * Set the data for each stream in this space
     *
     * @param tileNum The number of tiles in the space
     */
    public void setData(int tileNum) {
        for (int i = 0; i < streamNum; ++i) {
            Stream stream = streams[i];
            stream.setData(tileNum);
        }
    }

    /** This space has changed */
    public void flagChanged() {
        changed = true;
    }

    /** This space has not changed */
    public void unflagChanged() {
        changed = false;
    }

  /* ****************** Accessor Methods ******************* */

    /**
     * Get the number of tiles to send
     *
     * @return the number of tiles to send
     */
    public int getTilesToSend() {
        return tilesToSend;
    }

    /**
     * Has this space changed?
     *
     * @return true if this space has changed
     */
    public boolean hasChanged() {
        return changed;
    }

  /* ****************** Utilities ******************* */

    private void setupTileNames() {
        String tmp[] = null;
        if (tileNames != null) {
            tmp = tileNames;
        }
        tileNames = new String[tileNum];
        if (tmp != null) {
            int len = Math.min(tmp.length, tileNames.length);
            System.arraycopy(tmp, 0, tileNames, 0, len);
        }
    }

    /**
     * Add a stream to this space
     *
     * @param stream The stream to add
     * @return An ID for this stream
     */
    public int addStream(Stream stream) {
        int streamID = streamNum;
        stream.setID(streamID);
        streams[streamID] = stream;

        ++streamNum;

        return streamID;
    }

    /** Reset the data in every stream */
    public void resetData() {
        for (int i = 0; i < streamNum; ++i) {
            Stream stream = streams[i];
            stream.reset();
        }
    }

    /**
     * Resize this space
     *
     * @param tileNum The number of tiles in the resized space
     */
    public void resize(int tileNum) {
        this.tileNum = tileNum;
        for (int i = 0; i < streamNum; ++i)
            streams[i].setData(tileNum);
        setupControl();
        setupTileNames();
        flagChanged();
    }

    /** ****************** Control ******************* */

    /** Initialise the control values for this space */
    public void startControl() {
        initControl();
    }

    /**
     * Set the control values for a range of tiles
     *
     * @param tag The control value
     * @param start The starting tile
     * @param len The number of tiles
     */
    public void setControlRange(byte tag, int start, int len) {
        for (int i = start; i < (start + len); ++i) {
            setControl(tag, i);
        }
    }

    /**
     * Set the control for a tile
     *
     * @param tag The control value
     * @param index The tile index
     */
    public void setControl(byte tag, int index) {
        if (isControlBackground(tag) || isControlUnused(tag)) {
            if (isControlUsed(control[index]))
                control[index] &= (~CONTROL_USED);
        }

        control[index] |= (byte) tag;
    }


    public void finishControl() {
    }

  /* ****************** Constructors ******************* */

    /**
     * Create an empty server space. Initially it has no tiles to send
     */
    public ServerSpace() {
        this.tilesToSend = 0;
    }

    /**
     * Create a new server space
     *
     * @param name The space's name
     * @param driverName Its driver's name
     * @param tileNum The number of tiles in the space
     * @param title The title to use
     * @param blockInfo The block information tile to use
     * @param streamNum The number of streams in the space
     * @param unusedString The string to use for unused tiles
     * @param mainSpace Is this the main space?
     */
    public ServerSpace(String name, String driverName, int tileNum, String title,
                       String blockInfo, int streamNum, String unusedString, boolean mainSpace) {
        this.name = name;
        this.driverName = driverName;
        this.tileNum = tileNum;
        this.title = title;
        this.blockInfo = blockInfo;
        this.tileNames = null;
        this.streams = new Stream[streamNum];
        this.streamNum = 0;
        if (unusedString != null)
            this.unusedString = unusedString;
        else
            this.unusedString = DEFAULT_UNUSED_STRING;
        this.mainSpace = mainSpace;
        this.changed = false;

        setupControl();
        setupTileNames();
        this.tilesToSend = 0;
    }

}
