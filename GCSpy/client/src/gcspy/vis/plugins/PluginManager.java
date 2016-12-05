/*
 * $Id
$
 * Copyright Richard Jones, University of Kent, 2005
 *
 * See the file "Kent_license.terms" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */

package gcspy.vis.plugins;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * This class manages pluggable views. A pluggable view must provide
 * <ol>
 * <li> A short-form name
 * <li> A PluginListener class, <code><i>name</i>Listener</code>
 * <li> A PluginFrame class
 * </ol>
 *
 * @author <a href="http://www/cs/kent.ac.uk">Richard Jones</a>
 */
public class PluginManager {

    /**
     * The plugins directory is relative to the classpath
     */
    private static final String PLUGINS_DIR = "plugins/";

    /**
     * Is a plugin names selected for viewing?
     **/
    private Map<String, Boolean> plugins;

    /**
     * A class loader for the plugins
     */
    URLClassLoader pluginClassLoader;

    /**
     * Create the Plugin Manager and load all plugins
     * We expect the plugin to be in a jar file
     * <code>plugins/<i>plugin</i>.jar</code>.
     * We expect the plugin listener to be
     * <code><i>Plugin</i>Listener</code>.
     */
    public PluginManager() {
        plugins = new HashMap<String, Boolean>();
        List<URL> urls = new LinkedList<URL>();
        File dir = new File(PLUGINS_DIR);
        if (dir == null)
            return;
        File[] files = dir.listFiles();
        if (files == null)
            return;
        //System.out.println(dir.getName() + ", " + dir.getAbsolutePath());
        for (File f : files) {
            String jarName = f.getName();               // foo.jar
            //System.out.println(jarName);
            String p = basename(jarName, ".jar");       // foo
            if (p == null)
                continue;
            addToPlugins(p);                            // Foo
            try {
                urls.add(new URL("file", "localhost", PLUGINS_DIR + jarName));
            } catch (MalformedURLException e) {
                System.err.println("Could not add plugin jar " + PLUGINS_DIR + jarName);
                e.printStackTrace();
            }

        }

        URL[] urlArray = urls.toArray(new URL[urls.size()]);
        pluginClassLoader = new URLClassLoader(urlArray);
    }

    /**
     * Drop a file name extension
     *
     * @param name      the name of the file
     * @param extension the extension
     * @return the name without the extension or null if the extension doesn't match
     */
    private static String basename(String name, String extension) {
        if (!name.endsWith(extension))
            return null;
        int end = name.length() - extension.length();
        return name.substring(0, end);
    }

    /**
     * Add the name of a plugin to the plugins list.
     * Note that we treat the Hostory plugin specially
     *
     * @param p Name of the plugin
     */
    private void addToPlugins(String p) {
        StringBuffer sb = new StringBuffer(p.toLowerCase());
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        String pluginName = new String(sb);
        boolean selected = pluginName.equals("History");
        plugins.put(pluginName, selected);
    }

    /**
     * Get the names of all the plugins found
     *
     * @return short-form names of all plugins
     */
    public Set<String> getAllPlugins() {
        return plugins.keySet();
    }

    /**
     * Get the listeners for all the plugins selected
     *
     * @return the PluginListeners for all selected plugins
     */
    public List<PluginListener> getListeners() {
        List<PluginListener> listeners = new ArrayList<PluginListener>();
        for (String name : plugins.keySet()) {
            if (isActivePlugin(name)) {
                try {
                    String className = "gcspy.vis.plugins." + name.toLowerCase() + "." + name + "Listener";
                    //Class<?> clazz = Class.forName(className);
                    Class<?> clazz = pluginClassLoader.loadClass(className);
                    PluginListener pluginListener = (PluginListener) clazz.newInstance();
                    listeners.add(pluginListener);
                } catch (ClassNotFoundException e) {
                    System.err.println("Cannot find " + name + "Listener class");
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    System.err.println("Cannot instantiate " + name + "Listener object");
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    System.err.println("Cannot access " + name + "Listener object");
                    e.printStackTrace();
                }
            }
        }
        return listeners;
    }

    /**
     * Is a plugin selected
     *
     * @param p the plugin's name
     * @return true if the plugin is selected
     */
    public boolean isActivePlugin(String p) {
        return plugins.get(p).booleanValue();
    }

    /**
     * Set a plugin's active state
     *
     * @param plugin   The plugin's name
     * @param selected true if it is to be selected
     */
    public void setActivePlugin(String plugin, boolean selected) {
        if (!plugins.containsKey(plugin))
            throw new PluginException("Cannot activate plugin " + plugin);
        plugins.put(plugin, selected);
    }
}
