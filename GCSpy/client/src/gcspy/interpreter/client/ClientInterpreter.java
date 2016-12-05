/**
 * * $Id: ClientInterpreter.java 21 2005-06-11 00:25:23Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/


package gcspy.interpreter.client;

import gcspy.comm.BufferedInput;
import gcspy.comm.BufferedOutput;
import gcspy.comm.Client;
import gcspy.comm.Command;
import gcspy.interpreter.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The client interpreter
 * @author Tony Printezis
 * @author <a href="http://www/cs/kent.ac.uk">Richard Jones</a>
 */
public class ClientInterpreter extends Interpreter {

    private boolean terminated = false;

    // Listeners
    private List<PauseListener> pauseListeners = new ArrayList<>();

    private boolean enableEventListeners = false;

    private List<EventListener> eventListeners = new ArrayList<>();

    private List<SpaceListener> spaceListeners = new ArrayList<>();

    /** ****************** Accessor Methods ******************* */

    /**
     * Get a client space
     * @param i the space's ID
     * @return The client space
     */
    public ClientSpace getClientSpace(int i) {
        return (ClientSpace) getSpace(i);
    }

    /** ****************** Utilities ******************* */

    private void calcMaxima() {
        for (int i = 0; i < spaceNum; ++i) {
            ClientSpace space = getClientSpace(i);
            space.calcMaxima();
        }
    }

    /** ****************** Listeners ******************* */

    // Pause

    /**
     * Add a pause listener
     * @param pauseListener The listener
     */
    public void addPauseListener(PauseListener pauseListener) {
        pauseListeners.add(pauseListener);
    }

    /**
     * Tell all pause listeners to pause
     */
    public void callPauseListeners() {
        for (PauseListener listener : pauseListeners)
            listener.pause();
    }

    // Event

    /** Enable the event listeners */
    public void enableEventListeners() {
        enableEventListeners = true;
    }

  /*
   * we need synchronization in the next three methods because the add/remove
   * calls modify the number of elements in the list, and the iterator further
   * down might throw a concurrent modification exception
   */

    /**
     * Add an event listener.
     * We need synchronization because the add call modifies the number
     * of elements in the list, and iterators might throw a concurrent
     * modification exception.
     * @param eventListener the event listener
     */
    synchronized public void addEventListener(EventListener eventListener) {
        eventListeners.add(eventListener);
    }

    /**
     * Remove an event listener
     * We need synchronization because the remove call modifies the number
     * of elements in the list, and iterators might throw a concurrent
     * modification exception.
     * @param eventListener the event listener
     */
    synchronized public void removeEventListener(EventListener eventListener) {
        eventListeners.remove(eventListener);
    }

    /**
     * Call the event listeners for an event
     * We need synchronization because the add/remove calla modify the number
     * of elements in the list, and the iterator might throw a concurrent
     * modification exception.
     * @param eventID The event's ID
     * @param elapsedTime The elapsed time
     * @param compensationTime The compensation time
     */
    synchronized public void callEventListeners(int eventID, int elapsedTime,
                                                int compensationTime) {
        if (!enableEventListeners)
            return;
        for (EventListener listener : eventListeners)
            listener.event(eventID, elapsedTime, compensationTime);
    }

    // Space

    /**
     * Add a space listener
     * @param spaceListener the listener.
     */
    public void addSpaceListener(SpaceListener spaceListener) {
        spaceListeners.add(spaceListener);
    }

    /**
     * Remove a space listener
     * @param spaceListener the listener.
     */
    public void removeSpaceListener(EventListener spaceListener) {
        spaceListeners.remove(spaceListener);
    }

    /**
     * Call the space listeners for a space
     * @param space The space
     */
    public void callSpaceListeners(ClientSpace space) {
        for (SpaceListener listener : spaceListeners)
            listener.space(space);
    }

    /** ****************** Commands ******************* */

    /** *** PAUSE REQ **** */

    private class PauseReqCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) {
            throw new InterpreterException("PAUSE REQ Command not implemented");
        }
    }

    /**
     * Send a pause request command
     * @throws IOException
     */
    public void sendPauseReq() throws IOException {
        sendSingleCommand(PAUSE_REQ_CMD);
    }

    /** *** PAUSE **** */

    private class PauseCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) throws IOException {
            callPauseListeners();
            if (os != null) os.write("Pause\n".getBytes());
        }
    }

    /** *** RESTART **** */

    private class RestartCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) {
            throw new InterpreterException("RESTART Command not implemented");
        }
    }

    /**
     * Send a restart command
     * @throws IOException
     */
    public void sendRestart() throws IOException {
        sendSingleCommand(RESTART_CMD);
    }

    /** *** PLAY ONE **** */

    private class PlayOneCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) {
            throw new InterpreterException("PLAY ONE Command not implemented");
        }
    }

    /**
     * Send a play one command
     * @throws IOException
     */
    public void sendPlayOne() throws IOException {
        sendSingleCommand(PLAY_ONE_CMD);
    }

    /** *** SHUTDOWN REQ **** */

    private class ShutdownReqCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) {
            throw new InterpreterException("SHUTDOWN REQ Command not implemented");
        }
    }

    /**
     * Send a shut down request command
     * @throws IOException
     */
    public void sendShutdownReq() throws IOException {
        sendSingleCommand(SHUTDOWN_REQ_CMD);
    }

    /** *** SHUTDOWN **** */

    private class ShutdownCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) throws IOException {
            terminated = true;
            if (os != null) os.write("Shutdown\n".getBytes());
        }
    }

    /** *** STREAM **** */

    private class StreamCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) throws IOException {
            byte bData[];
            short sData[];
            int iData[];

            int spaceID = (int) input.readByte();
            int streamID = (int) input.readByte();

            ClientSpace space = getClientSpace(spaceID);
            Stream stream = space.getStream(streamID);

            if (os != null) {
                os.write(String.format("Space[%d]: %s\n", spaceID, space.getFullName()).getBytes());
                stream.dump(os);
            }
            switch (stream.getDataType()) {
                case Stream.BYTE_TYPE:
                    bData = input.readByteArray();
                    stream.setByteData(bData);
                    if (os != null) os.write(bData);
                    break;
                case Stream.SHORT_TYPE:
                    sData = input.readShortArray();
                    stream.setShortData(sData);
                    if (os != null) os.write(Arrays.toString(sData).getBytes());
                    break;
                case Stream.INT_TYPE:
                    iData = input.readIntArray();
                    stream.setIntData(iData);
                    if (os != null) os.write(Arrays.toString(iData).getBytes());
                    break;
            }
            if (os != null) os.write("\n--   End Stream\n".getBytes());
        }
    }

    /** *** EVENT **** */

    private class EventCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) throws IOException {
            int eventID = (int) input.readByte();
            int elapsedTime = input.readInt();
            int compensationTime = input.readInt();
            if (os != null) os.write(String.format("Event ID: %d\n", eventID).getBytes());
            calcMaxima();
            callEventListeners(eventID, elapsedTime, compensationTime);
        }
    }

    /** *** CONTROL **** */

    private class ControlCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) throws IOException {
            int spaceID = (int) input.readByte();

            byte control[] = input.readByteArray();

            ClientSpace space = getClientSpace(spaceID);
            space.setControl(control);
            if (os != null) {
                os.write(String.format("Space ID: %d, Controls: ", spaceID).getBytes());
                os.write(control);
                os.write('\n');
            }
        }
    }

    /** *** EVENT FILTERS **** */

    private class EventFiltersCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) {
            throw new InterpreterException("EVENT FILTERS Command not implemented");
        }
    }

    /**
     * Send the client's event filters
     * @throws IOException
     */
    public void sendEventFilters() throws IOException {
        sendEventFilters(eventFilters);
    }

    /**
     * Send the event filters
     * @param eventFilters The evnet filters to send
     * @throws IOException
     */
    public void sendEventFilters(EventFilters eventFilters) throws IOException {
        BufferedOutput output = client.createBufferedOutput();
        int len = eventFilters.getNum();

        start(output);
        putCmd(output, EVENT_FILTERS_CMD);
        output.writeShort((short) len);

        boolean enabled[] = eventFilters.getEnabled();
        int delays[] = eventFilters.getDelays();
        boolean pauses[] = eventFilters.getPauses();
        int periods[] = eventFilters.getPeriods();

        for (int i = 0; i < len; ++i) {
            output.writeBoolean(enabled[i]);
            output.writeInt(delays[i]);
            output.writeBoolean(pauses[i]);
            output.writeInt(periods[i]);
        }
        finish(output);

        client.send(output);
    }

    /** *** EVENT COUNT **** */

    private class EventCountCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) throws IOException {
            int count[] = input.readIntArray();
            setEventCount(count);
            if (os != null) {
                os.write(String.format("Event Count: %d\n", count.length).getBytes());
                os.write(Arrays.toString(count).getBytes());
                os.write('\n');
            }
        }
    }

    /** *** SUMMARY **** */

    private class SummaryCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) throws IOException {
            int spaceID = (int) input.readByte();
            int streamID = (int) input.readByte();
            int summary[] = input.readIntArray();

            if (os != null) {
                os.write(String.format("Begin Summary\nSpace ID: %d, Stream ID: %d\nSummary: ", spaceID, streamID).getBytes());
                os.write(Arrays.toString(summary).getBytes());
                os.write("End Summary\n".getBytes());
            }

            ClientSpace space = getClientSpace(spaceID);
            Stream stream = space.getStream(streamID);
            stream.setSummary(summary);
        }
    }

    /** *** SPACE INFO **** */

    private class SpaceInfoCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) throws IOException {
            int spaceID = (int) input.readByte();
            String spaceInfo = input.readString();

            if (os != null) {
                os.write(String.format("Space ID: %d, info: ", spaceID).getBytes());
                os.write(spaceInfo.getBytes());
            }

            ClientSpace space = getClientSpace(spaceID);
            space.setSpaceInfo(spaceInfo);
        }
    }

    /** *** SPACE **** */

    private class SpaceCmd implements Command {
        public void execute(BufferedInput input, OutputStream os) throws IOException {
            ClientSpace space = new ClientSpace();
            space.deserialise(input);
            if (os != null) space.dump(os);
            setSpace(space);
            callSpaceListeners(space);
        }
    }

    /** ****************** Confuguration Communication ******************* */

    private class ClientInputGenerator implements InputGenerator {

        private Client client;

        public BufferedInput createBufferedInput() throws IOException {
            client.definitelyReceive();
            return client.createBufferedInput();
        }

        public Space createSpace() {
            return new ClientSpace();
        }

        ClientInputGenerator(Client client) {
            this.client = client;
        }
    }

    private void deserialiseEverything() throws IOException {
        super.deserialiseEverything(new ClientInputGenerator(client));
    }

    /** ****************** Connection ******************* */

    private void checkServer(BootstrapParameters params) throws IOException {
    /* client sends first */
        sendBootInfo(false, params);
        receiveBootInfo(false, null);
    }

    private void setupClient(String server, int port, int maxLen)
            throws IOException {
    /*
     * make sure we clear up the client variable before we re-assign it,
     * otherwise we need double the memory requirements
     */
        client = null;
        client = new Client(server, port, maxLen);
    }

    /**
     * Connect to the server
     * @param server The server's host name
     * @param port The port on which to connect
     * @param pauseAtStart Whether the server should pause at the start
     * @throws IOException
     */
    public void connectToServer(String server, int port, boolean pauseAtStart)
            throws IOException {
        println(1, "connecting to server " + server + ", port " + port);
        connectToServer(server, port, pauseAtStart, DEFAULT_MAX_LEN);
    }

    /**
     * Connect to the server
     * @param server The server's host name
     * @param port The port on which to connect
     * @param pauseAtStart Whether the server should pause at the start
     * @param maxLen The maximum length of the  communication buffers
     * @throws IOException
     */
    public void connectToServer(String server, int port, boolean pauseAtStart,
                                int maxLen) throws IOException {
        BootstrapParameters params = new BootstrapParameters(pauseAtStart);
        setupClient(server, port, maxLen);
        println(1, "  connected to server");
        // println(1, " checking server");
        checkServer(params);
        // println(1, " receiving boot info");
        deserialiseEverything();
        setupEventFilters();
    }

    /** ****************** Main Loop ******************* */

    /**
     *  The main clinet interpretet loop.
     *  It repeatedly receives commands and exectutes them
     */
    public void mainLoop() throws IOException {

        println(1, "  starting client main loop");
        while (true) {
            client.receive();
            if (client.hasTerminated()) {
                println(1, "  main loop terminated (server died)");
                break;
            }
            terminated = false;
            execute(client.createBufferedInput());
            if (terminated) {
                println(1, "  main loop terminated (shutdown received)");
                break;
            }
        }
        client.close();
    }

    /** ****************** Constructors ******************* */

    /** Create a new ClientInterpreter */
    public ClientInterpreter() {
        super();

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
        cmds[SPACE_CMD] = new SpaceCmd();
    }

}
