/**
 * * $Id: EventFilters.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.interpreter;

/**
 * Class containing the event filter settings
 * @author Tony Printezis
 */
public class EventFilters {

    /** By default a filter is enabled */
    public static final boolean ENABLED_DEFAULT = true;

    /** Default delay*/
    public static final int DELAY_DEFAULT = 0;
    /** Minimum delay */
    public static final int DELAY_MIN = 0;
    /** Maximum delay */
    public static final int DELAY_MAX = 10000;

    /** Default is not to pause */
    public static final boolean PAUSE_DEFAULT = false;

    /** Default period */
    public static final int PERIOD_DEFAULT = 1;
    /** Minimum period */
    public static final int PERIOD_MIN = 1;
    /** Maximum period */
    public static final int PERIOD_MAX = 1000;


    private boolean enabled[];
    private int delays[]; // in ms
    private boolean pauses[];
    private int periods[];

    private int eventNum;


    /**
     * Create a new event filter
     * @param eventNum The number of events
     * @param enabled Which events are enabled
     * @param delays The delays for each event
     * @param pauses The pauses for each event
     * @param periods The periods for each event
     */
    public EventFilters(int eventNum,
                        boolean enabled[],
                        int delays[],
                        boolean pauses[],
                        int periods[]) {
        this.eventNum = eventNum;
        setData(enabled, delays, pauses, periods);
    }

    /**
     * Create a new event filter
     * @param eventNum The number of events
     */
    public EventFilters(int eventNum) {
        this.eventNum = eventNum;
        revertToDefaults();
    }

    /**
     * Set the filter to default values
     */
    public void revertToDefaults() {
        setData(defaultEnabled(eventNum),
                defaultDelays(eventNum),
                defaultPauses(eventNum),
                defaultPeriods(eventNum));
    }

    /** Enable all events */
    public void enableAll() {
        enabled = defaultEnabledAll(eventNum);
    }

    /** Disable all events */
    public void disableAll() {
        enabled = defaultDisabledAll(eventNum);
    }

    /** Set all delays to default value */
    public void clearDelays() {
        delays = defaultDelays(eventNum);
    }

    /** Set all pauses to default value */
    public void clearPauses() {
        pauses = defaultPauses(eventNum);
    }

    /** Set all periods to default value */
    public void resetPeriods() {
        periods = defaultPeriods(eventNum);
    }


    /**
     * Set the filter
     * @param enabled Which events are enabled
     * @param delays The delays for each event
     * @param pauses The pauses for each event
     * @param periods The periods for each event
     */
    private void setData(boolean enabled[],
                         int delays[],
                         boolean pauses[],
                         int periods[]) {
        this.enabled = enabled;
        this.delays = delays;
        this.pauses = pauses;
        this.periods = periods;
    }

    /**
     * Return a new array for for enabled settings, with each
     * event enabled
     * @param eventNum The number of events
     * @return The enabled settings
     */
    static private boolean[] defaultEnabled(int eventNum) {
        return defaultEnabledAll(eventNum);
    }

    /**
     * Return a new array for for enabled settings, with each
     * event enabled
     * @param eventNum The number of events
     * @return The enabled settings
     */
    static private boolean[] defaultEnabledAll(int eventNum) {
        boolean enabled[] = new boolean[eventNum];
        for (int i = 0; i < eventNum; ++i)
            enabled[i] = true;
        return enabled;
    }

    /**
     * Return a new array for for enabled settings, with each
     * event disabled
     * @param eventNum The number of events
     * @return The enabled settings
     */
    static private boolean[] defaultDisabledAll(int eventNum) {
        boolean enabled[] = new boolean[eventNum];
        for (int i = 0; i < eventNum; ++i)
            enabled[i] = false;
        return enabled;
    }

    /**
     * Return a new array for for delay settings, with each
     * delay set to the default
     * @param eventNum The number of events
     * @return The delay settings
     */
    static private int[] defaultDelays(int eventNum) {
        int delays[] = new int[eventNum];
        for (int i = 0; i < eventNum; ++i)
            delays[i] = DELAY_DEFAULT;
        return delays;
    }

    /**
     * Return a new array for for pause settings, with each
     * pause set to the default
     * @param eventNum The number of events
     * @return The pause settings
     */
    static private boolean[] defaultPauses(int eventNum) {
        boolean pauses[] = new boolean[eventNum];
        for (int i = 0; i < eventNum; ++i)
            pauses[i] = PAUSE_DEFAULT;
        return pauses;
    }

    /**
     * Return a new array for for periods, with each
     * period set to the default
     * @param eventNum The number of events
     * @return The periods
     */
    static private int[] defaultPeriods(int eventNum) {
        int periods[] = new int[eventNum];
        for (int i = 0; i < eventNum; ++i)
            periods[i] = PERIOD_DEFAULT;
        return periods;
    }


    /**
     * Get the number of events
     * @return the number of events
     */
    public int getNum() {
        return eventNum;
    }

    /**
     * Get an array of which events are enabled
     * @return the enabled settings
     */
    public boolean[] getEnabled() {
        return enabled;
    }

    /**
     * Get an array of event delays
     * @return the delay settings
     */
    public int[] getDelays() {
        return delays;
    }

    /**
     * Get an array of event pauses
     * @return the pause settings
     */
    public boolean[] getPauses() {
        return pauses;
    }

    /**
     * Get an array of event periods
     * @return the period settings
     */
    public int[] getPeriods() {
        return periods;
    }

    /**
     * Set the enabled settings
     * @param enabled The new settings
     */
    public void setEnabled(boolean enabled[]) {
        this.enabled = enabled;
    }

    /**
     * Set the delays
     * @param delays The new delays
     */
    public void setDelays(int delays[]) {
        this.delays = delays;
    }

    /**
     * Set the pauses
     * @param pauses The new pauses
     */
    public void setPauses(boolean pauses[]) {
        this.pauses = pauses;
    }

    /**
     * Set the periods
     * @param periods The new periods
     */
    public void setPeriods(int periods[]) {
        this.periods = periods;
    }

}
