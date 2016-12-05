/**
 * * $Id: PopupToggleMenu.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.vis.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Popup menu that has a series of toggles
 *
 * @author Tony Printezis
 */
public class PopupToggleMenu extends JButton {

    private JCheckBoxMenuItem menuItems[];
    private JPopupMenu menu;

    private class ButtonListener implements ActionListener {
        Component comp;

        public void actionPerformed(ActionEvent e) {
            menu.show(comp, 0, 0);
        }

        ButtonListener(Component comp) {
            this.comp = comp;
        }
    }

    /**
     * Get the state of a toggle
     * @return the state of the toggles
     */
    public int[] getState() {
        int num = 0;
        for (JCheckBoxMenuItem menuItem : menuItems)
            if (menuItem.getState())
                ++num;
        int state[] = new int[num];
        int curr = 0;
        for (int i = 0; i < menuItems.length; ++i) {
            if (menuItems[i].getState())
                state[curr++] = i;
        }
        return state;
    }

    /**
     * Create a pop-up menu with toggles
     * @param title The title of the menu
     * @param items Strings for each of the toggles
     * @param itemsOn Initial state of each of the toggles
     */
    public PopupToggleMenu(String title, String items[], int itemsOn[]) {
        super(title);

        menu = new JPopupMenu();
        menuItems = new JCheckBoxMenuItem[items.length];
        for (int i = 0; i < items.length; ++i) {
            menuItems[i] = new JCheckBoxMenuItem(items[i]);
            menu.add(menuItems[i]);
        }
        for (int anItemsOn : itemsOn) menuItems[anItemsOn].setState(true);

        addActionListener(new ButtonListener(this));
    }

}
