/**
 * * $Id: Events.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.interpreter;

import gcspy.comm.BufferedInput;
import gcspy.comm.BufferedOutput;

/**
 * List of the events for a given interpreter
 * @author Tony Printezis
 */
public class Events {

    private int eventNum;
    private String names[];

    /******************** Serialisation / Deserialisation ********************/

    /**
     * Serialise these events
     * @param output The BufferedOutput into which the events are serialised
     */
    public void serialise(BufferedOutput output) {
        output.writeShort((short) eventNum);
        for (int i = 0; i < eventNum; ++i)
            output.writeString(names[i]);
    }

    /**
     * Deserialise events
     * @param input The BufferedInput from which the events are serialised
     */
    public void deserialise(BufferedInput input) {
        eventNum = (int) input.readShort();
        names = new String[eventNum];
        for (int i = 0; i < eventNum; ++i)
            names[i] = input.readString();
    }

    /******************** Accessor Methods ********************/

    /**
     * The name of an event
     * @param id The event number
     * @return the event's name
     */
    public String getName(int id) {
        return names[id];
    }

    /**
     * Get the event names
     * @return the names of all the events
     */
    public String[] getNames() {
        return names;
    }

    /**
     * Get the number of events
     * @return the number of events
     */
    public int getNum() {
        return eventNum;
    }

    /******************** Debugging ********************/

    public void dump() {
        System.out.println("== Events");
        System.out.println("--   " + eventNum + " event(s)");
        for (int i = 0; i < eventNum; ++i)
            System.out.println("--     Event[" + i + "]: " + names[i]);
    }

    /******************** Constructors ********************/

    public Events() {
    }

    /**
     * Create new events
     * @param names The names of the events
     */
    public Events(String names[]) {
        this();
        this.names = names;
        eventNum = names.length;
    }

}
