/**
 * * $Id: Stream.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/


package gcspy.interpreter;

import gcspy.comm.BufferedInput;
import gcspy.comm.BufferedOutput;
import gcspy.utils.Utils;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents a stream
 * @author Tony Printezis
 */
public class Stream {

    // Constants
    /** Present a tiles as is */
    static public final int PRESENTATION_PLAIN = 0;
    /** Present a tile as <code>maximum+</code> if its value exceeds the maximum for the stream */
    static public final int PRESENTATION_PLUS = 1;
    /** Maximum value calclated by iterating over the stream */
    static public final int PRESENTATION_MAX_VAR = 2;
    /** Present as a percentage of a fixed maximum */
    static public final int PRESENTATION_PERCENT = 3;
    /** Present as a percentage but use the value of the
     * corresponding tile in stream <code>maxStreamIndex</code> for its maximum */
    static public final int PRESENTATION_PERCENT_VAR = 4;
    /** Select presentation from <code>enumNames</code> */
    static public final int PRESENTATION_ENUM = 5;
    static private final int BAD_PRESENTATION = -666;
    static private final int BAD_PRESENTATION_PERCENT = -777;

    /** Plain paint style */
    static public final int PAINT_STYLE_PLAIN = 0;
    /** Present zero values specially (by showing them as light frames) */
    static public final int PAINT_STYLE_ZERO = 1;

    /** Data type is byte */
    static public final int BYTE_TYPE = 0;
    /** Data type is short */
    static public final int SHORT_TYPE = 1;
    /** Data type is int */
    static public final int INT_TYPE = 2;

    static private final String DATA_TYPES[] = {"byte", "short", "int"};

    // Configuration
    private int id;
    private String name;
    private int minValue;
    private int maxValue;
    private int zeroValue;
    private int defaultValue;
    private String stringPre;
    private String stringPost;
    private int presentation;
    private int paintStyle;
    private int maxStreamIndex;
    private Color color;
    private String enumNames[];
    private Space space;

    private int dataType;
    private byte bData[];
    private short sData[];
    private int iData[];

    private DataAccessor accessor;

    /**
     * the len of the summary array depends on the presentation style
     *
     * PLAIN:
     * PLUS:
     * MAX_VAR:
     *   length = 1
     *   summary[0] = the aggregate value
     *
     * PERCENT:
     * PERCENT_VAR:
     *   length = 2
     *   summary[0] = the aggregate value
     *   summary[1] = the total
     *
     * ENUM:
     *   length = <enum num>
     *   summary[0..<enum num>-1] = count per enum
     */
    private int summary[];


    /** ****************** Inner Classes ******************* */

    private abstract class AbsAccessor implements DataAccessor {
        public abstract int get(int i);

        public int getAdjusted(int i) {
            return getAdjustedValue(i);
        }

        public int getAdjustedMax() {
            return getAdjustedMaxValue();
        }

        public boolean isZero(int i) {
            return internalIsZero(get(i));
        }

        public boolean isOverflow(int i) {
            return internalIsOverflow(get(i));
        }
    }

    private class ByteAccessor extends AbsAccessor {
        public int get(int i) {
            return (int) bData[i];
        }

        public boolean isDataAvailable() {
            return bData != null;
        }

        public int getLength() {
            return bData.length;
        }
    }

    private class ShortAccessor extends AbsAccessor {
        public int get(int i) {
            return (int) sData[i];
        }

        public boolean isDataAvailable() {
            return sData != null;
        }

        public int getLength() {
            return sData.length;
        }
    }

    private class IntAccessor extends AbsAccessor {
        public int get(int i) {
            return iData[i];
        }

        public boolean isDataAvailable() {
            return iData != null;
        }

        public int getLength() {
            return iData.length;
        }
    }

    /** ****************** Setting Data ******************* */

    /**
     * Set the space for this stream
     * @param space the space
     */
    public void setSpace(Space space) {
        this.space = space;
    }

    /**
     * Set the ID for this stream
     * @param id the ID
     */
    public void setID(int id) {
        this.id = id;
    }

    /**
     * Set the data for this stream as a byte array
     * @param bData the data
     */
    public void setByteData(byte bData[]) {
        this.bData = bData;
    }

    /**
     * Set the data for this stream as a short array
     * @param sData the data
     */
    public void setShortData(short sData[]) {
        this.sData = sData;
    }

    /**
     * Set the data for this stream as an int array
     * @param iData the data
     */
    public void setIntData(int iData[]) {
        this.iData = iData;
    }

    /**
     * Set the data for this stream according to the stream's <code>dataType>/code>
     * @param data the data
     */
    public void setData(Object data) {
        switch (dataType) {
            case BYTE_TYPE:
                bData = (byte[]) data;
                break;
            case SHORT_TYPE:
                sData = (short[]) data;
                break;
            case INT_TYPE:
                iData = (int[]) data;
                break;
        }
    }

    /**
     * Create the array for this stream's the data according to the stream's <code>dataType>/code>
     * @param len The lenght of the data
     */
    public void setData(int len) {
        switch (dataType) {
            case BYTE_TYPE:
                if ((bData == null) || (len != bData.length))
                    bData = new byte[len];
                break;
            case SHORT_TYPE:
                if ((sData == null) || (len != sData.length))
                    sData = new short[len];
                break;
            case INT_TYPE:
                if ((iData == null) || (len != iData.length))
                    iData = new int[len];
                break;
        }
    }

    /**
     * Set the summary values for this stream
     * @param summary The summary values
     */
    public void setSummary(int summary[]) {
        this.summary = summary;
    }

    /** ****************** Accessor Methods ******************* */

    /**
     * Get the name of this stream
     * @return the name of this stream
     */
    public String getName() {
        return name;
    }

    /**
     * Get the type of this stream
     * @return this stream's data type (byte, short or int)
     */
    public int getDataType() {
        return dataType;
    }

    /**
     * Get the minimum value for this stream
     * @return the minimum value for this stream
     */
    public int getMinValue() {
        return minValue;
    }

    /**
     * Get the maximum value for this stream
     * @return the maximum value for this stream
     */
    public int getMaxValue() {
        return maxValue;
    }

    /**
     * Get the zero value for this stream
     * @return the zero value for this stream
     */
    public int getZeroValue() {
        return zeroValue;
    }

    /**
     * Get the default value for this stream
     * @return the default value for this stream
     */
    public int getDefaultValue() {
        return defaultValue;
    }

    /**
     * Get the presentation style for this stream
     * @return the presentation style for this stream
     */
    public int getPresentation() {
        return presentation;
    }

    /**
     * Get the paint style for this stream
     * @return the paint style for this stream
     */
    public int getPaintStyle() {
        return paintStyle;
    }

    /**
     * Get the colour used for this stream
     * @return the tile colour for this stream
     */
    public Color getColor() {
        return color;
    }

    /**
     * Get the data for this stream (assumed to be bytes)
     * @return the data as a byte array for this stream
     */
    public byte[] getByteData() {
        return bData;
    }

    /**
     * Get the data for this stream (assumed to be shorts)
     * @return the data as a short array for this stream */
    public short[] getShortData() {
        return sData;
    }

    /**
     * Get the data for this stream (assumed to be ints)
     * @return the data as an int array for this stream */
    public int[] getIntData() {
        return iData;
    }

    /**
     * Get the data for this stream
     * @return the data as an array of appropriate type for this stream
     */
    public Object getData() {
        switch (dataType) {
            case BYTE_TYPE:
                return bData;
            case SHORT_TYPE:
                return sData;
            case INT_TYPE:
                return iData;
        }
        return null;
    }

    /**
     * Get the summary value(s) for this stream
     * @return the summary values for this stream
     * */
    public int[] getSummary() {
        return summary;
    }

    /**
     * Get the data accessor for this stream
     * @return the data accessor for this stream
     */
    public DataAccessor getAccessor() {
        return accessor;
    }

    /**
     * Get the the prefix string for tiles in this stream
     * (e.g. used in the block info view)
     * @return the prefix
     */
    public String getPrefix() {
        return stringPre;
    }

    /**
     * Get the suffix string for tiles in this stream
     * (e.g. used in the block info view)
     * @return the suffix
     */
    public String getSuffix() {
        return stringPost;
    }

    /** ****************** Utilities ******************* */

    /**
     * Calculate the maximum value found in this stream
     * if its presentation style is <code>PRESENTATION_MAX_VAR</code>
     */
    public void calcMaxIfNecessary() {
        if (presentation == PRESENTATION_MAX_VAR) {
            byte control[] = space.getControl();
            int max = minValue - 1;
            for (int i = 0; i < accessor.getLength(); ++i) {
                int val = accessor.get(i);
                if (Space.isControlUsed(control[i])) {
                    if (val > max)
                        max = val;
                }
            }

            int limit;
            if (paintStyle == PAINT_STYLE_ZERO)
                limit = minValue + 4;
            else
                limit = minValue + 2;
            if (max < limit)
                max = limit;

            maxValue = max;
      /*
       * System.out.println(name + "@" + space + ": " + "calc= " + max + ",
       * maxVal=" + maxValue);
       */
        }
    }

    /**
     * Get the maximum value for a tile
     * (neede for the <code>PRESENTATION_PERCENT_VAR</code> style.
     * @param i The tile's index
     * @return Its maximum value
     */
    public int getMaxValue(int i) {
        switch (presentation) {
            case PRESENTATION_PERCENT_VAR:
                return space.getStream(maxStreamIndex).getAccessor().get(i);
            default:
                return maxValue;
        }
    }

    /** Reset the data values to their defaults */
    public void reset() {
        switch (dataType) {
            case BYTE_TYPE:
                for (int i = 0; i < bData.length; ++i)
                    bData[i] = (byte) defaultValue;
                break;
            case SHORT_TYPE:
                for (int i = 0; i < sData.length; ++i)
                    sData[i] = (short) defaultValue;
                break;
            case INT_TYPE:
                for (int i = 0; i < iData.length; ++i)
                    iData[i] = defaultValue;
                break;
        }

        for (int i = 0; i < summary.length; ++i)
            summary[i] = 0;
    }

    private void initAccessor() {
        switch (dataType) {
            case BYTE_TYPE:
                accessor = new ByteAccessor();
                break;
            case SHORT_TYPE:
                accessor = new ShortAccessor();
                break;
            case INT_TYPE:
                accessor = new IntAccessor();
                break;
        }
    }

    private void initSummary() {
        int summaryLen = 0;
        switch (presentation) {
            case PRESENTATION_PLAIN:
            case PRESENTATION_PLUS:
            case PRESENTATION_MAX_VAR:
                summaryLen = 1;
                break;
            case PRESENTATION_PERCENT:
            case PRESENTATION_PERCENT_VAR:
                summaryLen = 2;
                break;
            case PRESENTATION_ENUM:
                summaryLen = enumNames.length;
                break;
        }
        summary = new int[summaryLen];
    }

    /** ****************** Translations ******************* */

    private int getAdjustedValue(int i) {
        int val = accessor.get(i);
        switch (presentation) {
            case PRESENTATION_PLAIN:
            case PRESENTATION_MAX_VAR:
            case PRESENTATION_PERCENT:
            case PRESENTATION_ENUM:
                return val - minValue;
            case PRESENTATION_PLUS:
                if (val > maxValue)
                    val = maxValue;
                return val - minValue;
            case PRESENTATION_PERCENT_VAR:
                return (int) calcPercentage((long) val, (long) getMaxValue(i));
        }
        return BAD_PRESENTATION;
    }

    /**
     * Adjust a tile value according to the stream's presentation style.
     * Note: should not really call this for <code>PERCENT_VAR</code>
     * @param val The tile value
     * @return The adjusted value
     * or <code>BAD_PRESENTATION_PERCENT</code> if the style is
     * <code>PRESENTATION_PERCENT_VAR</code>
     * or <code>BAD_PRESENTATION</code> if the style is bad.
     */
    public int adjustValue(int val) {
        switch (presentation) {
            case PRESENTATION_PLAIN:
            case PRESENTATION_MAX_VAR:
            case PRESENTATION_PERCENT:
            case PRESENTATION_ENUM:
                return val - minValue;
            case PRESENTATION_PLUS:
                if (val > maxValue)
                    val = maxValue;
                return val - minValue;
            case PRESENTATION_PERCENT_VAR:
                return BAD_PRESENTATION_PERCENT;
        }
        return BAD_PRESENTATION;
    }

    /**
     * Return the stream's maximum value, according to the stream's presentation style.
     * @return the maximum
     * or <code>BAD_PRESENTATION</code> if the style is bad.
     */
    private int getAdjustedMaxValue() {
        switch (presentation) {
            case PRESENTATION_PLAIN:
            case PRESENTATION_PLUS:
            case PRESENTATION_MAX_VAR:
            case PRESENTATION_PERCENT:
            case PRESENTATION_ENUM:
                return maxValue - minValue;
            case PRESENTATION_PERCENT_VAR:
                return 100;
        }
        return BAD_PRESENTATION;
    }

    /**
     * Is a tile value zero
     * @param val The tile value
     * @return true if it is zero
     */
    public boolean isZero(int val) {
        return internalIsZero(val);
    }

    /**
     * Does a tile value zero exceed the maximum
     * @param val The tile value
     * @return true if it exceeds the maximum
     */
    public boolean isOverflow(int val) {
        return internalIsOverflow(val);
    }

    // we might want to extend this later
    private boolean internalIsZero(int val) {
        if (paintStyle == PAINT_STYLE_ZERO)
            return (val == zeroValue);
        else
            return false;
    }

    private boolean internalIsOverflow(int val) {
        if (presentation == PRESENTATION_PLUS)
            return (val > maxValue);
        else
            return false;
    }

    /** ****************** Presentation ******************* */

    private double calcPercentage(long val) {
        return calcPercentage(val - minValue, maxValue - minValue);
    }

    private double calcPercentage(long val, long max) {
        return (double) val * 100.0 / (double) max;
    }

    /**
     * Present a tile value as a short string
     * @param val The value
     * @return It's string representation
     */
    public String presentDataSmall(int val) {
        String str = null;
        double perc;
        switch (presentation) {
            case PRESENTATION_PLAIN:
            case PRESENTATION_MAX_VAR:
                str = "" + Utils.formatSize((long) val);
                break;
            case PRESENTATION_PLUS:
                if (val > maxValue)
                    str = Utils.formatSize((long) maxValue) + "+";
                else
                    str = "" + Utils.formatSize((long) val);
                break;
            case PRESENTATION_PERCENT:
                perc = calcPercentage((long) val);
                str = Utils.formatSize((long) val) + " ("
                        + Utils.formatPercentage(perc) + "%)";
                break;
            case PRESENTATION_PERCENT_VAR:
                str = val + "%";
                break;
            case PRESENTATION_ENUM:
                str = enumNames[val];
                break;
        }
        return str;
    }

    /**
     * Present a tile value as a string
     * @param i The tiel index
     * @return It's string representation
     */
    public String presentData(int i) {
        String str = null;
        double perc;
        int val = accessor.get(i);
        switch (presentation) {
            case PRESENTATION_PLAIN:
            case PRESENTATION_MAX_VAR:
                str = stringPre + Utils.formatSize((long) val) + stringPost;
                break;
            case PRESENTATION_PLUS:
                str = stringPre + Utils.formatSize((long) val);
                if (val > maxValue)
                    str += " (" + Utils.formatSize((long) maxValue) + "+)";
                str += stringPost;
                break;
            case PRESENTATION_PERCENT:
                perc = calcPercentage((long) val);
                str = stringPre + Utils.formatSize((long) val) + stringPost + "  ("
                        + Utils.formatPercentage(perc) + "%)";
                break;
            case PRESENTATION_PERCENT_VAR:
                int max = getMaxValue(i);
                if (max == 0) {
                    str = stringPre + Utils.formatSize((long) val) + stringPost
                            + "  (N/A%)";
                } else {
                    perc = calcPercentage((long) val, (long) max);
                    str = stringPre + Utils.formatSize((long) val) + stringPost + "  ("
                            + Utils.formatPercentage(perc) + "%)";
                }
                break;
            case PRESENTATION_ENUM:
                str = stringPre + enumNames[val];
                break;
        }
        return str;
    }

    private String presentSummary(String prefix) {
        String str = null;
        String values[];
        int maxName = 0;
    /* int maxValue = 0; */
        int len;
        double perc;

        if ((summary == null) || (summary.length == 0)) {
            switch (presentation) {
                case PRESENTATION_PLAIN:
                case PRESENTATION_PLUS:
                case PRESENTATION_MAX_VAR:
                case PRESENTATION_PERCENT:
                case PRESENTATION_PERCENT_VAR:
                    // str = prefix + "NOT AVAILABLE\n";
                    str = null;
                    break;
                case PRESENTATION_ENUM:
                    // str = "";
                    // for (int i = 0; i < enumNames.length; ++i) {
                    // str += prefix + enumNames[i] + " " + "NOT AVAILABLE\n";
                    // }
                    str = null;
                    break;
            }
        } else {
            switch (presentation) {
                case PRESENTATION_PLAIN:
                case PRESENTATION_PLUS:
                case PRESENTATION_MAX_VAR:
                    str = prefix + Utils.formatSize((long) summary[0]) + stringPost
                            + "\n";
                    break;
                case PRESENTATION_PERCENT:
                case PRESENTATION_PERCENT_VAR:
                    if (summary[1] == 0) {
                        str = prefix + "N/A%  (" + Utils.formatSize((long) summary[0])
                                + stringPost + ")\n";
                    } else {
                        perc = calcPercentage((long) summary[0], (long) summary[1]);
                        str = prefix + Utils.formatPercentage(perc) + "%  ("
                                + Utils.formatSize((long) summary[0]) + stringPost + ")\n";
                    }
                    break;
                case PRESENTATION_ENUM:
                    len = enumNames.length;
                    values = new String[len];
                    for (int i = 0; i < len; ++i) {
                        if (enumNames[i].length() > maxName)
                            maxName = enumNames[i].length();
                        values[i] = Utils.formatSize((long) summary[i]);
            /*
             * if (values[i].length() > maxValue) maxValue = values[i].length();
             */
                    }

                    str = "";
                    for (int i = 0; i < enumNames.length; ++i) {
                        str += prefix + Utils.padRight(enumNames[i], maxName) + " "
                                + Utils.padLeft(values[i], maxValue) + "\n";
                    }
                    break;
            }
        }
        return str;
    }

    /**
     * Present a full summary
     * @param prefix1 A string to prefix the summary
     * @param prefix2 A string to prefix the summary if the style is <code>PRESENTATION_ENUM</code>
     * @return the full summary as a string
     */
    public String presentFullSummary(String prefix1, String prefix2) {
        String str = null;
        String tmp = null;
        switch (presentation) {
            case PRESENTATION_PLAIN:
            case PRESENTATION_PLUS:
            case PRESENTATION_MAX_VAR:
            case PRESENTATION_PERCENT:
            case PRESENTATION_PERCENT_VAR:
                tmp = presentSummary("");
                if (tmp == null)
                    str = "";
                else
                    str = prefix1 + stringPre + presentSummary("");
                break;
            case PRESENTATION_ENUM:
                tmp = presentSummary(prefix2);
                if (tmp == null)
                    str = "";
                else
                    str = prefix1 + stringPre + "\n" + presentSummary(prefix2);
                break;
        }
        return str;
    }

    /** ****************** Serialisation / Deserialisation ******************* */

    /**
     * Serialise the stream
     * @param output The BufferedOutput to use
     */
    public void serialise(BufferedOutput output) {
        output.writeByte((byte) id);
        output.writeByte((byte) dataType);
        output.writeString(name);
        output.writeInt(minValue);
        output.writeInt(maxValue);
        output.writeInt(zeroValue);
        output.writeInt(defaultValue);
        output.writeString(stringPre);
        output.writeString(stringPost);
        output.writeByte((byte) presentation);
        output.writeByte((byte) paintStyle);
        output.writeByte((byte) maxStreamIndex);
        output.writeColor(color);
        output.writeByte((byte) enumNames.length);
        for (String enumName : enumNames) {
            output.writeString(enumName);
        }
    }

    /**
     * Deserialise the stream
     * @param input The BufferedInput to use
     */
    public void deserialise(BufferedInput input) {
        int enumNum;
        id = (int) input.readByte();
        dataType = (int) input.readByte();
        name = input.readString();
        minValue = input.readInt();
        maxValue = input.readInt();
        zeroValue = input.readInt();
        defaultValue = input.readInt();
        stringPre = input.readString();
        stringPost = input.readString();
        presentation = (int) input.readByte();
        paintStyle = (int) input.readByte();
        maxStreamIndex = (int) input.readByte();
        color = input.readColor();
        enumNum = (int) input.readByte();
        enumNames = new String[enumNum];
        for (int i = 0; i < enumNum; ++i) {
            enumNames[i] = input.readString();
        }

        if (presentation == PRESENTATION_MAX_VAR)
            maxValue = minValue + 4;
        initAccessor();
    }

  /* ****************** Debugging ******************* */

    public void dump() {
        System.out.println("--   Stream[" + id + "]: " + name);
        System.out.println("--     data type    : " + DATA_TYPES[dataType]);
        System.out.println("--     minValue     : " + minValue);
        System.out.println("--     maxValue     : " + maxValue);
        System.out.println("--     zeroValue    : " + zeroValue);
        System.out.println("--     defaultValue : " + defaultValue);
        System.out.println("--     string pre   : " + stringPre);
        System.out.println("--     string post  : " + stringPost);
        System.out.println("--     presentation : " + presentation);
        System.out.println("--     paint style  : " + paintStyle);
        System.out.println("--     color        : " + color);
        System.out.println("--     enum names   :");
        if (enumNames.length == 0)
            System.out.println("--         NONE");
        else {
            for (String enumName : enumNames) System.out.println("--         " + enumName);
        }
    }

    public void dump(OutputStream os) throws IOException {
        os.write(String.format("--   Stream[%d]: %s\n", id, name).getBytes());
        os.write(String.format("--     data type    : %s\n", DATA_TYPES[dataType]).getBytes());
        os.write(String.format("--     minValue     : %d\n", minValue).getBytes());
        os.write(String.format("--     maxValue     : %d\n", maxValue).getBytes());
        os.write(String.format("--     zeroValue    : %d\n", zeroValue).getBytes());
        os.write(String.format("--     defaultValue : %d\n", defaultValue).getBytes());
        os.write(String.format("--     string pre   : %s\n", stringPre).getBytes());
        os.write(String.format("--     string post  : %s\n", stringPost).getBytes());
        os.write(String.format("--     presentation : %d\n", presentation).getBytes());
        os.write(String.format("--     paint style  : %d\n", paintStyle).getBytes());
        os.write(String.format("--     color        : %s\n", color).getBytes());
        os.write("--     enum names   :\n".getBytes());
        if (enumNames.length == 0)
            os.write("--         NONE\n".getBytes());
        else {
            for (String enumName : enumNames) os.write(String.format("--         %s\n", enumName).getBytes());
        }
    }

  /* ****************** Constructors ******************* */

    public Stream() {
    }

    /**
     * Create a new stream
     * @param name Its name
     * @param dataType Its data type
     * (one of <code>BYTE_TYPE</code>, <code>SHORT_TYPE</code>, <code>INT_TYPE</code>)
     * @param minValue The minimum value for the stream
     * @param maxValue The maximum value for the stream
     * @param zeroValue The zero value for the stream
     * @param defaultValue The default value for the stream
     * @param stringPre Text to prefix tile values in string representations
     * @param stringPost Text to prefix tile values in string representations
     * @param presentation The presentation style (see <code>PRESENTATION_*</code>)
     * @param paintStyle The presentation style (
     * (one of <code>PAINT_STYLE_PLAIN</code> or <code>PAINT_STYLE_ZERO</code>)
     * @param maxStreamIndex The ID of the stream to use as a maximum if the
     * presentation style is <code>PRESENTATION_PERCENT_VAR</code>.
     * @param color The tile colour
     * @param enumNames A list of name to use if the presentation style is <code>PRESENTATION_ENUM</code>.
     * @param tileNum The number of tiles in the stream
     */
    public Stream(String name, int dataType, int minValue, int maxValue,
                  int zeroValue, int defaultValue, String stringPre, String stringPost,
                  int presentation, int paintStyle, int maxStreamIndex, Color color,
                  String enumNames[], int tileNum) {
        this(name, dataType, minValue, maxValue, zeroValue, defaultValue,
                stringPre, stringPost, presentation, paintStyle, maxStreamIndex, color,
                enumNames);

        setData(tileNum);
    }

    /**
     * Create a new stream
     * @param name Its name
     * @param dataType Its data type
     * (one of <code>BYTE_TYPE</code>, <code>SHORT_TYPE</code>, <code>INT_TYPE</code>)
     * @param minValue The minimum value for the stream
     * @param maxValue The maximum value for the stream
     * @param zeroValue The zero value for the stream
     * @param defaultValue The default value for the stream
     * @param stringPre Text to prefix tile values in string representations
     * @param stringPost Text to prefix tile values in string representations
     * @param presentation The presentation style (see <code>PRESENTATION_*</code>)
     * @param paintStyle The presentation style (
     * (one of <code>PAINT_STYLE_PLAIN</code> or <code>PAINT_STYLE_ZERO</code>)
     * @param maxStreamIndex The ID of the stream to use as a maximum if the
     * presentation style is <code>PRESENTATION_PERCENT_VAR</code>.
     * @param color The tile colour
     * @param enumNames A list of name to use if the presentation style is <code>PRESENTATION_ENUM</code>.
     */
    public Stream(String name, int dataType, int minValue, int maxValue,
                  int zeroValue, int defaultValue, String stringPre, String stringPost,
                  int presentation, int paintStyle, int maxStreamIndex, Color color,
                  String enumNames[]) {
        this.name = name;
        this.dataType = dataType;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.zeroValue = zeroValue;
        this.defaultValue = defaultValue;
        this.stringPre = stringPre;
        this.stringPost = stringPost;
        this.presentation = presentation;
        this.paintStyle = paintStyle;
        this.maxStreamIndex = maxStreamIndex;
        this.color = color;
        if (enumNames == null)
            this.enumNames = new String[0];
        else
            this.enumNames = enumNames;
        initAccessor();
        initSummary();
    }

}
