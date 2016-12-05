/**
 * * $Id: ServerInterpreter.java 29 2005-06-20 14:03:41Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.interpreter.server;

import gcspy.comm.*;
import gcspy.interpreter.*;
import gcspy.utils.Timer;
import gcspy.utils.Utils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * The server interpreter
 *
 * @author Tony Printezis
 */
public class ServerInterpreter extends Interpreter implements Runnable {

    private Server server;
    private int maxLen;

    private boolean connected;

    private boolean paused;
    private boolean pauseNow;

    private boolean playOne;

    private boolean shutdownReq;
    private boolean shutdownSent;

    // of true, server counts events and times
    private boolean collectStats;

    // counters
    private Timer elapsedTimer;
    private Timer compensationTimer;

    /** ****************** Accessor Methods ******************* */

    /**
     * Get a server space
     *
     * @param i The space's ID
     * @return The space
     */
    public ServerSpace getServerSpace(int i) {
        return (ServerSpace) getSpace(i);
    }

    /**
     * ***************** Utilities *******************
     */

    private int getArrayLength(int dataType, Object array) {
        switch (dataType) {
            case Stream.BYTE_TYPE:
                return ((byte[]) array).length;
            case Stream.SHORT_TYPE:
                return ((short[]) array).length;
            case Stream.INT_TYPE:
                return ((int[]) array).length;
        }
        return 0;
    }

    /**
     * Add a server space to the interpreter
     *
     * @param space The server space
     * @return an ID for the space
     */
    public int addServerSpace(ServerSpace space) {
        int spaceID = spaceNum;
        space.setID(spaceID);
        spaces[spaceID] = space;

        ++spaceNum;

        return spaceID;
    }

    /**
     * Transmit all the streams for all the spaces known
     * to tis interpreter.
     *
     * @throws IOException
     */
    public void transmitStreams() throws IOException {
        for (int i = 0; i < spaceNum; ++i) {
            ServerSpace space = getServerSpace(i);
            if (space.hasChanged()) {
                sendSpace(space);
                space.unflagChanged();
            }
            for (int j = 0; j < space.getStreamNum(); ++j) {
                Stream stream = space.getStream(j);
                Object data = stream.getData();
                if (data == null)
                    continue; // no data for this stream

                if (space.getTilesToSend() == 0)
                    sendStream(i, j, stream.getDataType(), data);
                else
                    sendStream(i, j, stream.getDataType(), data, space.getTilesToSend());
                sendSummary(i, j, stream.getSummary());
            }
            sendSpaceInfo(i, space.getSpaceInfo());
            sendControl(i, space.getControl());
        }
    }

    /** ****************** Commands ******************* */

    /**
     * ** PAUSE REQ ****
     */

    private class PauseReqCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) {
            pauseNow = true;
        }
    }

    /**
     * ** PAUSE ****
     */

    private class PauseCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) {
            throw new InterpreterException("PAUSE Command not implemented");
        }
    }

    /**
     * Send a PAUSE comand
     *
     * @throws IOException
     */
    public void sendPause() throws IOException {
        sendSingleCommand(PAUSE_CMD);
    }

    /**
     * ** RESTART ****
     */

    private class RestartCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) {
            paused = false;
        }
    }

    /**
     * ** PLAY ONE ****
     */

    private class PlayOneCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) {
            playOne = true;
        }
    }

    /**
     * ** SHUTDOWN REQ ****
     */

    private class ShutdownReqCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) {
            shutdownReq = true;
        }
    }

    /**
     * ** SHUTDOWN ****
     */

    private class ShutdownCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) {
            throw new InterpreterException("SHUTDOWN Command not implemented");
        }
    }

    /**
     * Senf a SHUT_DOWN command
     *
     * @throws IOException
     */
    public void sendShutdown() throws IOException {
        sendSingleCommand(SHUTDOWN_CMD);
    }

    /**
     * ** STREAM ****
     */

    private class StreamCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) {
            throw new InterpreterException("STREAM Command not implemented");
        }
    }

    /**
     * Send a stream for a space
     *
     * @param spaceID  The space's ID
     * @param streamID The stream's ID
     * @param dataType The stream's data type
     * @param data     The stream's data
     * @throws IOException
     */
    public void sendStream(int spaceID, int streamID, int dataType, Object data)
            throws IOException {
        sendStream(spaceID, streamID, dataType, data, getArrayLength(dataType, data));
    }

    /**
     * Send a stream for a space
     *
     * @param spaceID  The space's ID
     * @param streamID The stream's ID
     * @param dataType The stream's data type
     * @param data     The stream's data
     * @param len      The length of the stream data
     * @throws IOException
     */
    public void sendStream(int spaceID, int streamID, int dataType, Object data,
                           int len) throws IOException {
        BufferedOutput output = client.createBufferedOutput();

        start(output);
        putCmd(output, STREAM_CMD);
        output.writeByte((byte) spaceID);
        output.writeByte((byte) streamID);
        switch (dataType) {
            case Stream.BYTE_TYPE:
                output.writeByteArray((byte[]) data, len);
                break;
            case Stream.SHORT_TYPE:
                output.writeShortArray((short[]) data, len);
                break;
            case Stream.INT_TYPE:
                output.writeIntArray((int[]) data, len);
                break;
        }
        finish(output);

        client.send(output);
    }

    /**
     * ** EVENT ****
     */

    private class EventCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) {
            throw new InterpreterException("EVENT Command not implemented");
        }
    }

    /**
     * Send an event
     *
     * @param eventID          The event's ID
     * @param elapsedTime      The elapsed time fo rthe event
     * @param compensationTime The compensation time fo rthe event
     * @throws IOException
     */
    public void sendEvent(int eventID, int elapsedTime, int compensationTime)
            throws IOException {
        BufferedOutput output = client.createBufferedOutput();

        start(output);
        putCmd(output, EVENT_CMD);
        output.writeByte((byte) eventID);
        output.writeInt(elapsedTime);
        output.writeInt(compensationTime);
        finish(output);

        client.send(output);
    }

    /**
     * ** CONTROL ****
     */

    private class ControlCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) {
            throw new InterpreterException("CONTROL Command not implemented");
        }
    }

    /**
     * Send a control stream for a space
     *
     * @param spaceID The space's ID
     * @param control The controls
     * @throws IOException
     */
    public void sendControl(int spaceID, byte control[]) throws IOException {
        BufferedOutput output = client.createBufferedOutput();

        start(output);
        putCmd(output, CONTROL_CMD);
        output.writeByte((byte) spaceID);
        output.writeByteArray(control);
        finish(output);

        client.send(output);
    }

    /**
     * ** EVENT FILTERS ****
     */

    private class EventFiltersCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) {
            // compiler is being dense
            EventFilters eventFilters = getEventFilters();

            int len = (int) input.readShort();
            boolean enabled[] = new boolean[len];
            int delays[] = new int[len];
            boolean pauses[] = new boolean[len];
            int periods[] = new int[len];

            for (int i = 0; i < len; ++i) {
                enabled[i] = input.readBoolean();
                delays[i] = input.readInt();
                pauses[i] = input.readBoolean();
                periods[i] = input.readInt();
            }

            eventFilters.setEnabled(enabled);
            eventFilters.setDelays(delays);
            eventFilters.setPauses(pauses);
            eventFilters.setPeriods(periods);
        }
    }

    /**
     * ** EVENT COUNT ****
     */

    private class EventCountCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) {
            throw new InterpreterException("EVENT COUNT Command not implemented");
        }
    }

    /**
     * Send the event count
     *
     * @throws IOException
     */
    public void sendEventCount() throws IOException {
        BufferedOutput output = client.createBufferedOutput();

        start(output);
        putCmd(output, EVENT_COUNT_CMD);
        output.writeIntArray(eventCount);
        finish(output);

        client.send(output);
    }

    /**
     * ** SUMMARY ****
     */

    private class SummaryCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) {
            throw new InterpreterException("SUMMARY Command not implemented");
        }
    }

    /**
     * Send the summary
     *
     * @param spaceID  The space's ID
     * @param streamID The stream's ID
     * @param summary  The summary values
     * @throws IOException
     */
    public void sendSummary(int spaceID, int streamID, int summary[])
            throws IOException {
        BufferedOutput output = client.createBufferedOutput();

        start(output);
        putCmd(output, SUMMARY_CMD);
        output.writeByte((byte) spaceID);
        output.writeByte((byte) streamID);
        if (summary == null)
            output.writeEmptyArray();
        else
            output.writeIntArray(summary);
        finish(output);

        client.send(output);
    }

    /**
     * ** SPACE INFO ****
     */

    private class SpaceInfoCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) {
            throw new InterpreterException("SPACE INFO Command not implemented");
        }
    }

    /**
     * Send the space information
     *
     * @param spaceID   The space's ID
     * @param spaceInfo The space information
     * @throws IOException
     */
    public void sendSpaceInfo(int spaceID, String spaceInfo) throws IOException {
        BufferedOutput output = client.createBufferedOutput();

        start(output);
        putCmd(output, SPACE_INFO_CMD);
        output.writeByte((byte) spaceID);
        output.writeString(spaceInfo);
        finish(output);

        client.send(output);
    }

    /**
     * ** SPACE ****
     */

    private class SpaceCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) {
            throw new InterpreterException("SPACE Command not implemented");
        }
    }

    /**
     * Send a space
     *
     * @param space The space
     * @throws IOException
     */
    public void sendSpace(Space space) throws IOException {
        BufferedOutput output = client.createBufferedOutput();

        start(output);
        putCmd(output, SPACE_CMD);
        space.serialise(output);
        finish(output);
        client.send(output);
    }

    /**
     * ***************** Confuguration Communication *******************
     */

    private class ClientOutputGenerator implements OutputGenerator {

        private Client client;

        public BufferedOutput createBufferedOutput() throws IOException {
            return client.createBufferedOutput();
        }

        public void done(int len) throws IOException {
            client.send(len);
        }

        ClientOutputGenerator(Client client) {
            this.client = client;
        }
    }

    private void serialiseEverything() throws IOException {
        super.serialiseEverything(new ClientOutputGenerator(client));
    }

    /**
     * ***************** Connection *******************
     */

    private boolean isConnected() {
        return connected;
    }

    private void checkClient() throws IOException {
        BootstrapParameters params = new BootstrapParameters();

    /* client sends first */
        receiveBootInfo(true, params);
        sendBootInfo(true, null);

        if (params.pauseAtStart) {
            pauseNow = true;
        }
    }

    private void waitForNewClient(int maxLen) throws IOException {
        println(0, "waiting for client to connect");
    /*
     * make sure we clear up the client variable before we re-assign it,
     * otherwise we need double the memory requirements
     */
        client = null;
        client = server.waitForNewClient(maxLen);
        println(0, "  client connected");
    }

    private void waitUntilClientConnected() {
        while (!isConnected()) {
            Utils.sleep(10);
        }

        Utils.sleep(50);
        safepoint();
    }

    /**
     * Start the server
     *
     * @param port The port on which to communicate
     * @throws IOException
     */
    public void startServer(int port) throws IOException {
        startServer(port, false);
    }

    /**
     * Start the server
     *
     * @param port The port on which to communicate
     * @param wait Whether to wait until client connects
     * @throws IOException
     */
    public void startServer(int port, boolean wait) throws IOException {
        startServer(port, wait, DEFAULT_MAX_LEN);
    }

    /**
     * Start the server
     *
     * @param port   The port on which to communicate
     * @param wait   Whether to wait until client connects
     * @param maxLen The buffer length
     * @throws IOException
     */
    public void startServer(int port, boolean wait, int maxLen)
            throws IOException {
        println(0, "starting server, port " + port);
        server = new Server(port);

        if (wait)
            println(0, "blocked until client connects");

        this.maxLen = maxLen;
        new Thread(this).start();

        if (wait)
            waitUntilClientConnected();
    }

    /** ****************** Timers ******************* */

    /**
     * Start the compensation timer
     */
    public void startCompensationTimer() {
        compensationTimer.start();
    }

    /**
     * Stop the compensation timer
     */
    public void stopCompensationTimer() {
        compensationTimer.stop();
    }

    /**
     * Reset the compensation and elapsed timers
     */
    private void resetTimers() {
        compensationTimer.reset();
        elapsedTimer.reset();
        elapsedTimer.start();
    }

    /** ****************** Main Loop ******************* */

    /**
     * Start the main interpreter thread loop
     */
    public void run() {
        mainThreadLoop();
    }

    private void connectToClient() throws IOException {
        waitForNewClient(maxLen);
        internalSetup();
        // println(0, " checking client");
        checkClient();
        // println(0, " sending boot info");
        serialiseEverything();

        if (collectStats)
            resetTimers();

        connected = true;
    }

    private void mainLoop() throws IOException {
        println(0, "  starting server main loop");
        while (true) {
            client.receive();
            if (client.hasTerminated()) {
                println(0, "  main loop terminated (client died)");
                connected = false;
                break;
            }

            BufferedInput input = client.createBufferedInput();
            execute(input);
            if (shutdownReq) {
                println(0, "  main loop terminated (shutdown request received)");
                break;
            }
        }
    }

    private void mainThreadLoop() {
        while (true) {
            try {
                connectToClient();
                mainLoop();
                if (shutdownReq) {
                    while (!shutdownSent) {
                        Utils.sleep(10);
                    }
                    connected = false;
                }

                client.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    /**
     * ***************** Safepoints *******************
     */

    private void internalSafepoint() {
        try {
            if (shutdownReq) {
                sendShutdown();
                shutdownSent = true;
            }

            if (pauseNow) {
                println(0, "  paused");
                sendPause();
                pauseNow = false;
                paused = true;
            }

            if (paused) {
                while (paused && isConnected()) {
                    Utils.sleep(10);
                    if (shutdownReq) {
                        sendShutdown();
                        shutdownSent = true;
                        paused = false;
                    }
                    if (playOne) {
                        println(0, "  play one");
                        playOne = false;
                        break;
                    }
                }
                if ((!paused) && (!shutdownReq))
                    println(0, "  restarted");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Indicate a safe point
     */
    public void safepoint() {
        if (isConnected()) {
            internalSafepoint();
        }
    }

    /**
     * Update the event counter for an event
     *
     * @param eventID The event's ID
     */
    public void updateEventCounter(int eventID) {
        ++eventCount[eventID];
    }

    private void internalEventBoundary(int eventID, int elapsedTime,
                                       int compensationTime) {
        try {
            transmitStreams();
            sendEventCount();
            sendEvent(eventID, elapsedTime, compensationTime);

            int delay = eventFilters.getDelays()[eventID];
            if (delay > 0) {
                Utils.sleep(delay);
            }

            if (eventFilters.getPauses()[eventID]) {
                pauseNow = true;
            }

            internalSafepoint();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Indicate an event boundary
     *
     * @param eventID          The event's ID
     * @param elapsedTime      The elapsed time at this event
     * @param compensationTime The compensation time
     */
    public void eventBoundary(int eventID, int elapsedTime, int compensationTime) {
        if (shouldTransmit(eventID)) {
            internalEventBoundary(eventID, elapsedTime, compensationTime);
        }
    }

    /**
     * Indicate an event boundary and update counters if necessary
     *
     * @param eventID The event's ID
     */
    public void countingEventBoundary(int eventID) {
        if (collectStats)
            updateEventCounter(eventID);

        if (shouldTransmit(eventID)) {
            if (collectStats)
                elapsedTimer.stop();

            internalEventBoundary(eventID, (int) elapsedTimer.getTime(),
                    (int) compensationTimer.getTime());

            if (collectStats) {
                resetTimers();
            }
        }
    }

    /**
     * Should this event be transmitted?
     *
     * @param eventID The event ID
     * @return true if the server is connected and
     * this event is enabled?
     */
    public boolean shouldTransmit(int eventID) {
        return isConnected() && eventFilters.getEnabled()[eventID]
                && (eventCount[eventID] % (eventFilters.getPeriods()[eventID]) == 0);
    }

    /** ****************** Constructors ******************* */

    /**
     * Set up a connection.
     * This is called per connection
     */
    protected void internalSetup() {
        setupEventFilters();

        paused = false;
        pauseNow = false;
        playOne = false;
        shutdownReq = false;
        shutdownSent = false;
    }

    /**
     * Create a new server interpreter
     *
     * @param name         The server's name
     * @param collectStats WHether to collect statistics
     */
    public ServerInterpreter(String name, boolean collectStats) {
        super(name);

        cmds[PAUSE_REQ_CMD] = new PauseReqCmd();
        cmds[PAUSE_CMD] = new PauseCmd();
        cmds[RESTART_CMD] = new RestartCmd();
        cmds[PLAY_ONE_CMD] = new PlayOneCmd();
        cmds[SHUTDOWN_REQ_CMD] = new ShutdownReqCmd();
        cmds[SHUTDOWN_CMD] = new ShutdownCmd();
        cmds[STREAM_CMD] = new StreamCmd();
        cmds[EVENT_CMD] = new EventCmd();
        cmds[CONTROL_CMD] = new ControlCmd();
        cmds[EVENT_FILTERS_CMD] = new EventFiltersCmd();
        cmds[EVENT_COUNT_CMD] = new EventCountCmd();
        cmds[SUMMARY_CMD] = new SummaryCmd();
        cmds[SPACE_INFO_CMD] = new SpaceInfoCmd();

        this.collectStats = collectStats;
        if (collectStats) {
            elapsedTimer = new Timer();
            compensationTimer = new Timer();
        }
    }

    /**
     * Create a new server interpreter
     *
     * @param name         The server's name
     * @param collectStats Whether to collect statistics
     * @param events       The events
     * @param spaceNum     The number of spaces
     */
    public ServerInterpreter(String name, boolean collectStats, Events events,
                             int spaceNum) {
        this(name, collectStats);
        this.events = events;
        spaces = new Space[spaceNum];
        spaceNum = 0;

        setupEventCount();
    }

}
