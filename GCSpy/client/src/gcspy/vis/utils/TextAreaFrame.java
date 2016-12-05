/**
 * * $Id: TextAreaFrame.java 22 2005-06-14 05:17:45Z rej $
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
 * Generic frame that contains a text area
 * @author Tony Printezis
 */
public class TextAreaFrame extends AbstractFrame {

    /** The text area */
    protected JTextArea textArea;
    private JButton closeB;

    private class CloseBListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            setVisible(false);
        }
    }

    /**
     * Set up the frame
     * @param frameTitle A title for the frame
     * @param textAreaTitle A title for the text area
     * @param rows The number of rows in the text area
     * @param columns The number of columns in the text area
     */
    protected void setup(String frameTitle, String textAreaTitle, int rows,
                         int columns) {
        setTitle("GCspy: " + frameTitle);
        setResizable(false);

        Container cont = getContentPane();
        cont.setLayout(new BorderLayout());

        textArea = Factory.createTextArea(rows + 1, columns, false, true);
        JScrollPane scrollPane = new JScrollPane(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setViewportView(textArea);
        cont.add(Factory.createTitlePanel(textAreaTitle, scrollPane),
                BorderLayout.CENTER);

        JPanel panel = Factory.createFlowPanel(FlowLayout.CENTER);
        closeB = Factory.createButton("Close", true, new CloseBListener());
        panel.add(closeB);

        cont.add(panel, BorderLayout.SOUTH);
        pack();
    }

    /** Destroy this component */
    public void destroy() {
        textArea = null;
        super.destroy();
    }

    /**
     * A new frame with a text area
     * @param owner The parent frame
     * @param position The position of this frame relative to its parent
     * @param reposition
     */
    public TextAreaFrame(AbstractFrame owner, Position position,
                         boolean reposition) {
        super(owner, position, reposition);
    }

}
