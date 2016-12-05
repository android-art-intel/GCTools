/**
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 * <p>
 * *  gcspy.interpreter.server.TestDriver
 * *
 * *  Test GC driver
 **/

/**
 **  gcspy.interpreter.server.TestDriver
 **
 **  Test GC driver
 **/

package gcspy.test;


import gcspy.interpreter.Space;
import gcspy.interpreter.Stream;
import gcspy.interpreter.server.ServerInterpreter;
import gcspy.interpreter.server.ServerSpace;
import gcspy.utils.ColorDB;

public class TestDriver {

    private ServerSpace space;
    private int spaceID;

    private int maxTileNum;
    private int tileNum;
    private int startAddr;
    private int endAddr;
    private int blockSize;

    private byte cards[];
    private short objects[];
    private int usedSpace[];

    private int cardsSummary[];
    private int objectsSummary[];
    private int usedSpaceSummary[];

    private Stream cardsStream;
    private Stream objectsStream;
    private Stream usedSpaceStream;

    private int getTileNum(int addr) {
        int diff = addr - startAddr;
        int index = diff / blockSize;
        if ((diff % blockSize) != 0)
            ++index;
        return index;
    }

    private int getIndex(int addr) {
        int diff = addr - startAddr;
        return (diff / blockSize);
    }

    public void zero(int maxAddr) {
    /* maxAddr is the current max address used in the heap */
        tileNum = getTileNum(maxAddr);
    /* (store it so we can use it later when setting the control stream) */

    /* this will appear in the Summary string */
        String spaceInfo = "Size: " + (maxAddr - startAddr) + " bytes\n";
        space.setSpaceInfo(spaceInfo);

    /* this ensures that all data arrays in the streams are of the
       correct size - if they are, it leaves them alone - if they are not,
       it re-allocates them */
        space.setData(tileNum);

    /* get the data from the corresponding streams, just in case it
       got re-allocated during the setData call above */
        objects = objectsStream.getShortData();
        cards = cardsStream.getByteData();
        usedSpace = usedSpaceStream.getIntData();

    /* this initialises the data of the streams to their default values,
       which is passed to the stream during initialisation - it also 
       zeros the summary array.

       if necessary, the driver can always initialise these itself */
        space.resetData();

    /* this is the total space which we have to set explicitly */
        usedSpaceSummary[1] = maxAddr - startAddr;
    }

    public void object(int addr) {
    /* just count how many objects headers there are per tile */
        int index = getIndex(addr);

    /* this is not realistic of how an object would affect these;
       I'm just trying to test the infrastructure */
        byte cardState = (byte) (addr % 3);

        if (cards[index] < cardState)
            cards[index] = cardState;
        ++cardsSummary[cardState];

        objects[index] += 1;
        ++objectsSummary[0];
        usedSpace[index] += 10;
        usedSpaceSummary[0] += 10;
    }

    private void calcSizes() {
        int diff = endAddr - startAddr;
        maxTileNum = diff / blockSize;
        if ((diff % blockSize) != 0)
            ++maxTileNum;
    }

    private void setupNames(int from, int to) {
        for (int i = from; i < to; ++i) {
            int start = startAddr + (i * blockSize);
            int end = start + blockSize;
            if (end > endAddr)
                end = endAddr;
            space.setTileName(i, "   [" + start + "-" + end + ")");
        }
    }

    public void resize(int newEndAddr) {
        int oldEndAddr = endAddr;
        int oldMaxTileNum = maxTileNum;
        endAddr = newEndAddr;
        calcSizes();

        space.resize(maxTileNum);
        if (maxTileNum > oldMaxTileNum) {
            // spece is being extended
            setupNames((oldMaxTileNum > 0) ? (oldMaxTileNum - 1) : 0, maxTileNum);
        } else {
            // space is being shrunk
            if (maxTileNum > 0)
                setupNames(maxTileNum - 1, maxTileNum);
        }
    }

    public void finish() {
    /* here, we'll setup the control stream */

    /* we always start control as we need to initialise it */
        space.startControl();
        if (tileNum < maxTileNum) {
      /* setup which tiles at the end are not used */
            space.setControlRange(Space.CONTROL_UNUSED,
                    tileNum, maxTileNum - tileNum);
        }
        space.finishControl();
    }

    public TestDriver(ServerInterpreter interpreter,
                      String name,
                      int startAddr, /* heap area managed by this GC */
                      int endAddr,
                      int blockSize) {
        Stream stream;
        ColorDB colorDB = ColorDB.getColorDB();

        this.startAddr = startAddr;
        this.endAddr = endAddr;
        this.blockSize = blockSize;
        calcSizes();

    /* a driver may have more than one spaces
       e.g. M&S driver might have heap and free list spaces */
        space = new ServerSpace(name, /* space name */
                "M&C", /* driver name */
                maxTileNum, /* tile num */
                "Block ", /* title */
                "Size: " + blockSize + "\n", /* block info */
                5 /* max stream number */,
                "UNUSED", /* unused string */
                true /* is this the main space? */);
        setupNames(0, maxTileNum);

        spaceID = interpreter.addServerSpace(space);

        objectsStream = new Stream("Objects",
                Stream.SHORT_TYPE, /* data type */
                0, /* min value */
                10, /* max value */
                0, /* zero value */
                0, /* default value */
                "Objects: ", /* string pre */
                "", /* string post */
                Stream.PRESENTATION_PLUS,
                Stream.PAINT_STYLE_ZERO,
                0,
                colorDB.getColor("Yellow"),
                null /* enum names */);
        space.addStream(objectsStream);
        objectsSummary = objectsStream.getSummary();

        String cardNames[] = {"CLEAN", "SUMMARISED", "DIRTY"};
        cardsStream = new Stream("Card Table",
                Stream.BYTE_TYPE, /* data type */
                0, /* min value */
                2, /* max value */
                0, /* zero value */
                0, /* default value */
                "Card Status: ", /* string pre */
                "", /* string post */
                Stream.PRESENTATION_ENUM,
                Stream.PAINT_STYLE_PLAIN,
                0,
                colorDB.getColor("Off White"),
                cardNames /* enum names */);
        space.addStream(cardsStream);
        cardsSummary = cardsStream.getSummary();

        usedSpaceStream = new Stream("Used Space",
                Stream.INT_TYPE, /* data type */
                0, /* min value */
                blockSize, /* max value */
                0, /* zero value */
                0, /* default value */
                "Used Space: ", /* string pre */
                " bytes", /* string post */
                Stream.PRESENTATION_PERCENT,
                Stream.PAINT_STYLE_ZERO,
                0,
                colorDB.getColor("Red"),
                null);
        space.addStream(usedSpaceStream);
        usedSpaceSummary = usedSpaceStream.getSummary();
    }

}
