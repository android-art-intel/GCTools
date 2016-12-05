/**
 * * $Id: MainFrame.java 41 2006-08-15 14:26:59Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.vis;

import gcspy.interpreter.client.ClientInterpreter;
import gcspy.interpreter.client.ClientSpace;
import gcspy.interpreter.client.EventListener;
import gcspy.interpreter.client.PauseListener;
import gcspy.utils.Utils;
import gcspy.vis.plugins.PluginListener;
import gcspy.vis.plugins.PluginManager;
import gcspy.vis.utils.AbstractFrame;
import gcspy.vis.utils.Factory;
import gcspy.vis.utils.IconFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Main frame of the visualiser
 *
 * @author Tony Printezis
 * @author <a href="http://www/cs/kent.ac.uk">Richard Jones</a>
 */
public class MainFrame extends AbstractFrame implements SpaceManagerInterface,
        EventListener, PauseListener, EventFilterListener {
    static private int BLOCK_INFO_MIN_ROWS = 10;

    private JTextField activityTF;

    private JTextArea blockInfoTA;

    private JComboBox viewSelectorCB;

    private boolean viewSelectorEnabled = true;

    private JButton connectB;

    private JButton disconnectB;

    private ConnectionInfoLabel connectedL;

    private JButton pauseB;

    private JButton playB;

    private JButton playOneB;

    private JPanel spacesP;

    private JPanel toolsP;

    private JPanel bottomP;

    private JPanel magP;

    private JMenu windowsM = null;

    private JMenu pluginsM = null;

    private JMenuItem generalInfoMI;

    private JMenuItem timersMI;

    private JMenuItem eventCountMI;

    private JMenuItem eventFilterMI;

    private ClientInterpreter interpreter;

    private String host = null;

    private int port = -1;

    private boolean pauseAtStart = false;

    private boolean small;

    private boolean tiny;

    private boolean doConnect = false;

    private SpaceManager activeSpaceManager = null;

    private EventFilterFrame eventFilterFrame = null;

    private EventCountFrame eventCountFrame = null;

    private GeneralInfoFrame generalInfoFrame = null;

    private TimersFrame timersFrame = null;

    private List<DisconnectListener> disconnectListeners;

    private static PluginManager pluginManager;

    /** *** Listeners **** */

    /**
     * Add a disconnect listener
     *
     * @param listener
     *          the listener
     */
    public void addDisconnectListener(DisconnectListener listener) {
        disconnectListeners.add(listener);
    }

    private void callDisconnectListeners(boolean reconnecting) {
        for (DisconnectListener listener : disconnectListeners)
            listener.disconnect(reconnecting);
        if (reconnecting) {
            Iterator<DisconnectListener> it = disconnectListeners.iterator();
            while (it.hasNext()) {
                it.next();
                it.remove();
            }
            // for (DisconnectListener listener : disconnectListeners)
            // disconnectListeners.remove(listener);
        }
    }

    private class ConnectBListener implements ActionListener {
        private AbstractFrame owner;

        public void actionPerformed(ActionEvent event) {
            Factory.disableButton(connectB);
            ConnectionDialog cd = new ConnectionDialog(owner, host, port,
                    pauseAtStart, small);
            if (cd.result()) {
                host = cd.getHost();
                port = cd.getPort();
                pauseAtStart = cd.getPauseAtStart();
                small = cd.getSmall();
                connect(host, port, pauseAtStart);
            } else {
                Factory.enableButton(connectB);
            }
        }

        ConnectBListener(AbstractFrame owner) {
            this.owner = owner;
        }
    }

    private class DisconnectBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            requestShutdown();
        }
    }

    private class PauseBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            pauseReq();
        }
    }

    private class PlayBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            play();
        }
    }

    private class PlayOneBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            playOne();
        }
    }

    private class MainWindowListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            quit();
        }
    }

    private class QuitMIListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            quit();
        }
    }

    private class AboutMListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            // About Window!
        }
    }

    private class GeneralInfoMIListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            generalInfoFrame.setVisible(true);
        }
    }

    private class PluginMIListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            JCheckBoxMenuItem cbmi = (JCheckBoxMenuItem) e.getSource();
            String plugin = cbmi.getText();
            boolean enabled = cbmi.getState();
            pluginManager.setActivePlugin(plugin, enabled);
        }

    }

    private class TimersMIListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            timersFrame.setVisible(true);
        }
    }

    private class EventCountMIListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            eventCountFrame.setVisible(true);
        }
    }

    private class EventFilterMIListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            eventFilterFrame.setVisible(true);
        }
    }

    private class ViewSelectorListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (viewSelectorEnabled) {
                int selected = getSelectedView();
                if (activeSpaceManager != null)
                    activeSpaceManager.selectedStream(selected);
            }
        }
    }

    /** *** Main Loop Thread **** */

    private class MainLoopThread extends Thread {
        private MainLoopThread() {
        }

        public void run() {
            try {
                interpreter.mainLoop();
            } catch (Exception e) {
                System.out.println(e.getClass() + " thrown.");
                e.printStackTrace();
                System.exit(-1);
            }
            interpreter = null;
            disconnect();
        }
    }

  /* *** Some Methods For Manipulating The Main Frame State **** */

    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (doConnect)
            connect(host, port, pauseAtStart);
        doConnect = false;
    }

    public void setActive(SpaceManager spaceManager) {
        activeSpaceManager.deactivate();
        setBlockInfo("");
        activeSpaceManager = spaceManager;
    }

    private void setCurrentActivity(String text) {
        activityTF.setText(text);
    }

    public void setBlockInfo(String text) {
        if (!blockInfoTA.getText().equals(text)) {
            int lines = Utils.countLines(text);
            if (lines < BLOCK_INFO_MIN_ROWS)
                lines = BLOCK_INFO_MIN_ROWS;
            if (lines != blockInfoTA.getRows()) {
                blockInfoTA.setRows(lines);
                toolsP.validate();
            }
            blockInfoTA.setText(text);
        }
    }

    public void clearViews() {
        viewSelectorEnabled = false;
        viewSelectorCB.removeAllItems();
    }

    public void addView(Object obj) {
        viewSelectorCB.addItem(obj);
    }

    public void setActiveView(int streamID) {
        viewSelectorCB.setSelectedIndex(streamID);
        viewSelectorEnabled = true;
    }

    public void addMagManagers(TileManager small, TileManager large) {
        magP.removeAll();

        Container vertical = new Box(BoxLayout.Y_AXIS);
        vertical.add(small);
        vertical.add(large);
        magP.add(vertical);
    }

    private int getSelectedView() {
        return viewSelectorCB.getSelectedIndex();
    }

    private void setSelectedView(int index) {
        viewSelectorCB.setSelectedIndex(index);
    }

    /** *** Some Events **** */

    private void requestShutdown() {
        if (interpreter != null) {
            try {
                connectedL.setDisconnecting();
                interpreter.sendShutdownReq();

                while (interpreter != null) {
                    Utils.sleep(10);
                }
            } catch (IOException e) {
                System.out.println(e.getClass() + " thrown.");
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    private void quit() {
        requestShutdown();
        System.exit(-1);
    }

    /** An event filter has been updated */
    public void eventFilterUpdated() {
        try {
      /*
       * no need to get the event filters from the frame, it's already sharing
       * the object from the interpreter
       */
            interpreter.sendEventFilters();
        } catch (Exception e) {
            System.out.println(e.getClass() + " thrown.");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private void connect(String host, int port, boolean pauseAtStart) {
        connectedL.setConnecting(host, port);
        callDisconnectListeners(true);

        try {

            setupInterpreter();
            interpreter.connectToServer(host, port, pauseAtStart);
            setCurrentActivity("");
            setupSpaceManagers();
            enableWindowsMenu();

            eventFilterFrame = new EventFilterFrame(this, interpreter.getEvents()
                    .getNames(), interpreter.getEventFilters(), this);

            if (eventCountFrame != null) {
                eventCountFrame.setVisible(false);
                eventCountFrame.destroy();
            }
            eventCountFrame = new EventCountFrame(this, interpreter);
            interpreter.addEventListener(eventCountFrame);

            if (generalInfoFrame != null) {
                generalInfoFrame.setVisible(false);
                generalInfoFrame.destroy();
            }
            generalInfoFrame = new GeneralInfoFrame(this, interpreter);

            if (timersFrame != null) {
                timersFrame.setVisible(false);
                timersFrame.destroy();
            }
            timersFrame = new TimersFrame(this);
            interpreter.addEventListener(timersFrame);

            connectedL.setConnected(host, port, interpreter.getName());

            MainLoopThread mainLoopThread = new MainLoopThread();
            mainLoopThread.start();

            if (!pauseAtStart) {
                Factory.enableButton(pauseB);
            }
            Factory.enableButton(disconnectB);
            // Factory.disableButton(connectB);
        } catch (Exception e) {
            //e.printStackTrace();
            interpreter = null;
            showWarning(e.getClass().toString());
            Factory.enableButton(connectB);
            connectedL.setDisconnected();
        }

    }

    private void disconnect() {
        connectedL.setDisconnected();
        Factory.disableButton(disconnectB);
        if (!doConnect)
            Factory.enableButton(connectB);
        disableNavButtons();

        if (eventFilterFrame != null) {
            eventFilterFrame.setVisible(false);
            eventFilterFrame.destroy();
            eventFilterFrame = null;
        }
        disableWindowsMenu();
        callDisconnectListeners(false);
    }

    public void event(int event, int elapsedTime, int compensationTime) {
        String eventName = interpreter.getEvents().getName(event);
        setCurrentActivity(eventName);
    }

    private void disableNavButtons() {
        Factory.disableButton(pauseB);
        Factory.disableButton(playB);
        Factory.disableButton(playOneB);
    }

    private void pauseReq() {
        try {
            connectedL.setPausing(host, port, interpreter.getName());
            interpreter.sendPauseReq();
        } catch (Exception e) {
            System.out.println(e.getClass() + " thrown.");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private void play() {
        try {
            interpreter.sendRestart();
            Factory.enableButton(pauseB);
            Factory.disableButton(playB);
            Factory.disableButton(playOneB);
            connectedL.setConnected(host, port, interpreter.getName());
        } catch (Exception e) {
            System.out.println(e.getClass() + " thrown.");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private void playOne() {
        try {
            interpreter.sendPlayOne();
        } catch (Exception e) {
            System.out.println(e.getClass() + " thrown.");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void pause() {
        Factory.disableButton(pauseB);
        Factory.enableButton(playB);
        Factory.enableButton(playOneB);
        connectedL.setPaused(host, port, interpreter.getName());
    }

  /* *** Space Managers **** */

    public void validateContainer() {
        spacesP.validate();
    }

    private void setupSpaceManagers() {
        spacesP.removeAll();
        JPanel panel = Factory.createBorderPanel();
        spacesP.add(panel);
        Container vertical = null;
        boolean middle = false;
        int num = interpreter.getSpaceNum();

        for (int i = 0; i < num; ++i) {
            ClientSpace space = interpreter.getClientSpace(i);
            boolean active = space.isMainSpace() || (!middle && (i == (num - 1)));
            SpaceManager spaceManager = new SpaceManager(this, interpreter, space,
                    this, active, small, tiny);
            spaceManager.setWidth(spacesP.getWidth());
            addDisconnectListener(spaceManager);

            if (active) {
                activeSpaceManager = spaceManager;

                vertical = new Box(BoxLayout.Y_AXIS);
                panel.add(vertical, BorderLayout.CENTER);
                vertical.add(spaceManager.getMainPanel());
                vertical.add(Factory.createEmptyPanel(0, 1));

                middle = true;
                vertical = null;
            } else {
                if (vertical == null) {
                    vertical = new Box(BoxLayout.Y_AXIS);
                    if (!middle)
                        panel.add(vertical, BorderLayout.NORTH);
                    else
                        panel.add(vertical, BorderLayout.SOUTH);
                }

                vertical.add(spaceManager.getMainPanel());
                vertical.add(Factory.createEmptyPanel(0, 1));
            }

      /*
       * have to do this after the component has been added to the frame to
       * ensure that any redraw events only happen after the comonent is visible
       */
            interpreter.addEventListener(spaceManager);
            interpreter.addSpaceListener(spaceManager);
        }

        interpreter.enableEventListeners();
    }

    private void enableWindowsMenu() {
        if (windowsM != null) {
            Factory.enableMenuItem(timersMI);
            Factory.enableMenuItem(generalInfoMI);
            Factory.enableMenuItem(eventCountMI);
            Factory.enableMenuItem(eventFilterMI);
        }
        // Do not allow views to be changed when we are connected
        if (pluginsM != null)
            Factory.disableMenuItem(pluginsM);
    }

    private void disableWindowsMenu() {
        if (windowsM != null) {
            Factory.disableMenuItem(eventFilterMI);
        }
        // Do not allow views to be changed when we are connected
        if (pluginsM != null)
            Factory.enableMenuItem(pluginsM);
    }

    public List<PluginListener> getPluginListeners() {
        return pluginManager.getListeners();
    }

    /** *** Setup **** */

    private void setupToolsPanel() {
        JPanel panel;

        toolsP = Factory.createBorderPanel();

        Container vertical = new Box(BoxLayout.Y_AXIS);

        activityTF = Factory.createTextField(20, false);
        panel = Factory.createTitlePanel("Current Event", activityTF);
        vertical.add(panel);

        blockInfoTA = Factory.createTextArea(BLOCK_INFO_MIN_ROWS, 23, false);
        panel = Factory.createTitlePanel("Tile Info", blockInfoTA);
        vertical.add(panel);

        viewSelectorCB = Factory.createComboBox(new ViewSelectorListener());
        panel = Factory.createTitlePanel("View Chooser", viewSelectorCB);
        vertical.add(panel);

        toolsP.add(vertical, BorderLayout.NORTH);

        magP = Factory.createTitlePanel("Magnification");
        toolsP.add(magP, BorderLayout.SOUTH);
    }

    private void setupSpacesPanel() {
        spacesP = Factory.createBorderPanel();
    }

    private void setupBottomPanel() {
        Container horizontal;

        bottomP = Factory.createBorderPanel();

        connectedL = new ConnectionInfoLabel();
        bottomP.add(connectedL, BorderLayout.CENTER);

        horizontal = new Box(BoxLayout.X_AXIS);
        connectB = Factory.createButton(" Connect ", !doConnect,
                new ConnectBListener(this));
        horizontal.add(connectB);
        disconnectB = Factory.createButton("Disconnect", false,
                new DisconnectBListener());
        horizontal.add(disconnectB);
        bottomP.add(horizontal, BorderLayout.EAST);

        horizontal = new Box(BoxLayout.X_AXIS);
        pauseB = Factory.createIconButton(IconFactory.createPauseIcon(), false,
                "Pause", new PauseBListener());
        horizontal.add(pauseB);
        playB = Factory.createIconButton(IconFactory.createPlayIcon(), false,
                "Play", new PlayBListener());
        horizontal.add(playB);
        playOneB = Factory.createIconButton(IconFactory.createPlayOneIcon(), false,
                "Step One", new PlayOneBListener());
        horizontal.add(playOneB);

        disconnect();

        bottomP.add(horizontal, BorderLayout.WEST);
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = Factory.createMenu("GCspy");
        menu.add(Factory.createMenuItem("About", new AboutMListener()));
        menu.addSeparator();
        menu.add(Factory.createMenuItem("Exit", new QuitMIListener()));
        menuBar.add(menu);

        windowsM = Factory.createMenu("Windows");
        generalInfoMI = Factory.createMenuItem("General Info", false,
                new GeneralInfoMIListener());
        windowsM.add(generalInfoMI);
        timersMI = Factory.createMenuItem("Timers", false, new TimersMIListener());
        windowsM.add(timersMI);
        eventCountMI = Factory.createMenuItem("Event Counters", false,
                new EventCountMIListener());
        windowsM.add(eventCountMI);
        eventFilterMI = Factory.createMenuItem("Event Filters", false,
                new EventFilterMIListener());
        windowsM.add(eventFilterMI);
        menuBar.add(windowsM);

        pluginsM = Factory.createMenu("Plugins");
        setupPluginsMenu();
        menuBar.add(pluginsM);
        setJMenuBar(menuBar);
    }

    private void setupComponents() {
        Container cont = getContentPane();
        cont.setLayout(new BorderLayout());

        setupToolsPanel();
        cont.add(toolsP, BorderLayout.WEST);

        setupSpacesPanel();
        cont.add(spacesP, BorderLayout.CENTER);

        setupBottomPanel();
        cont.add(bottomP, BorderLayout.SOUTH);

        setupMenuBar();

    }

    private void setupInterpreter() {
        interpreter = new ClientInterpreter();
        interpreter.setVerbose(true);
        interpreter.addPauseListener(this);
        interpreter.addEventListener(this);
    }

    /**
     * Set up the Plugins menu
     */
    private void setupPluginsMenu() {
        // Allow menu contents to change on the fly
        pluginsM.removeAll();
        Set<String> allPlugins = pluginManager.getAllPlugins();
        for (String plugin : allPlugins) {
            boolean selected = pluginManager.isActivePlugin(plugin);
            pluginsM.add(Factory.createCheckBoxMenuItem(plugin, selected,
                    new PluginMIListener()));
        }
    }

    /** *** Constructors **** */

    /**
     * Construct a new MainFrame
     *
     * @param host
     *          the server's hostname
     * @param port
     *          The port on which to connect
     * @param small
     *          True if small tiles are to be used
     * @param tiny
     *          True if tiny tiles are to be used
     * @param pauseAtStart
     *          true if the server is to pause at the start
     * @param doConnect
     *          true if the visualiser is to connect
     */
    public MainFrame(String host, int port, boolean small, boolean tiny,
                     boolean pauseAtStart, boolean doConnect) {
        super(Position.POS_ROOT_CENTER);
        setTitle("GCspy");
        addWindowListener(new MainWindowListener());
        disconnectListeners = new ArrayList<>();

        this.host = host;
        this.port = port;
        this.small = small;
        this.tiny = tiny;
        this.pauseAtStart = pauseAtStart;
        this.doConnect = doConnect;

        pluginManager = new PluginManager();
        setupComponents();
        pack();
    }

}
