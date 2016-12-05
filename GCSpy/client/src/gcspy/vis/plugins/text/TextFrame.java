/*
 * $Id: TextFrame.java 22 2005-06-14 05:17:45Z rej $
 * Copyright Richard Jones, University of Kent, 2005
 *
 * See the file "Kent_license.terms" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */

package gcspy.vis.plugins.text;

import gcspy.interpreter.client.ClientInterpreter;
import gcspy.interpreter.client.ClientSpace;
import gcspy.vis.TileManagerColors;
import gcspy.vis.plugins.PluginFrame;
import gcspy.vis.utils.AbstractFrame;
import gcspy.vis.utils.Factory;
import gcspy.vis.utils.MultiLineHeaderRenderer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * A simple, table-based textual view of the streams of a space
 *
 * @author <a href="http://www/cs/kent.ac.uk">Richard Jones</a>
 */
public class TextFrame extends PluginFrame {

    private int maxTileNum;
    private TextTableModel tableModel;

    /**
     * A listener for the close box
     *
     * @author Tony Printezis
     */
    private class CloseBListener implements ActionListener {
        private AbstractFrame owner;

        public void actionPerformed(ActionEvent event) {
            pluginFrames.remove(owner);
            shutdown();
        }

        CloseBListener(AbstractFrame owner) {
            this.owner = owner;
        }
    }

    /**
     * A listener for window closing events
     */
    private class CloseWindowListener extends WindowAdapter {
        private AbstractFrame owner;

        public void windowClosing(WindowEvent event) {
            pluginFrames.remove(owner);
            shutdown();
        }

        CloseWindowListener(AbstractFrame owner) {
            this.owner = owner;
        }
    }

    /**
     * A listener for table headers. Clicking on a column header sorts
     * the table by that column.
     */
    private class TextTableHeaderListener extends MouseAdapter {
        private JTable table;

        public void mouseClicked(MouseEvent e) {
            Point pt = e.getPoint();
            int column = table.getTableHeader().columnAtPoint(pt);
            TextTableModel model = (TextTableModel) table.getModel();
            model.sortAllRowsByColumn(column);
        }

        TextTableHeaderListener(JTable table) {
            this.table = table;
        }
    }


    /**
     * Create a new TextFrame
     *
     * @param owner          The owning MainFrame
     * @param pluginFrames   A list of plugin frames belonging to the owner
     * @param interpreter    The ClientInterpreter for this space
     * @param space          The space
     * @param selectedStream -not used-
     * @param tmColors       -not used-
     */
    public TextFrame(AbstractFrame owner, List<PluginFrame> pluginFrames,
                     ClientInterpreter interpreter, ClientSpace space, int selectedStream,
                     TileManagerColors tmColors) {
        super(owner, Position.POS_CENTER, false);
        super.setup(interpreter, pluginFrames, space, tmColors, "GCspy: Table view");
        setup();
        pack();
    }

    /**
     * Setup specific to this view. Add visula components etc
     */
    private void setup() {
        maxTileNum = space.getTileNum();
        Container cont = getContentPane();
        cont.setLayout(new BorderLayout());
        JPanel panel;

        // add the space's name at the top
        panel = Factory.createBorderPanel();
        JLabel titleL = Factory.createLabel(space.getFullName(), JLabel.LEFT, true);
        titleL.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.add(titleL, BorderLayout.WEST);
        cont.add(panel, BorderLayout.NORTH);

        // add a jtable in the middle
        tableModel = new TextTableModel(space);
        JTable table = Factory.createTable(tableModel, true, false);

        // add a header renderer for multiline headers
        MultiLineHeaderRenderer headerRenderer
                = new MultiLineHeaderRenderer(SwingConstants.CENTER, SwingConstants.CENTER);
        TableColumnModel tcm = table.getColumnModel();
        int columns = tableModel.getColumnCount();
        for (int i = 0; i < columns; i++) {
            String hdr = tableModel.getColumnName(i);
            TableColumn column = tcm.getColumn(i);
            column.setHeaderRenderer(headerRenderer);
            column.setHeaderValue(hdr);
        }


        // Column headers to sort rows
        JTableHeader hdr = table.getTableHeader();
        hdr.addMouseListener(new TextTableHeaderListener(table));
        Dimension hdrSize = calcHeaderSize(table.getColumnModel());
        hdr.setPreferredSize(hdrSize);

        table.setPreferredScrollableViewportSize(new Dimension(400, 400));
        JScrollPane tablePane = new JScrollPane(table);
        cont.add(Factory.createEmptyPanel(20, 100), BorderLayout.WEST);
        cont.add(tablePane, BorderLayout.CENTER);
        cont.add(Factory.createEmptyPanel(20, 100), BorderLayout.EAST);


        // add a close button at bottom right
        panel = Factory.createFlowPanel(FlowLayout.CENTER);
        JButton closeB = Factory.createButton("Close", new CloseBListener(this));
        panel.add(closeB);
        cont.add(panel, BorderLayout.SOUTH);

        addWindowListener(new CloseWindowListener(this));
    }

    /**
     * Calculate the size of the table's JTableHeader
     *
     * @param tcm The table's model
     * @return the dimensions needed for the header
     */
    private Dimension calcHeaderSize(TableColumnModel tcm) {
        Dimension hdrDim = new Dimension(0, 0);
        for (int i = 0; i < tcm.getColumnCount(); i++) {
            TableColumn col = tcm.getColumn(i);
            //System.out.println(col.getHeaderValue().getClass());
            String value = (String) col.getHeaderValue();
            //System.out.println("Heading ="+value);
            JLabel label = new JLabel(value);
            Dimension d = label.getPreferredSize();
            if (d.height > hdrDim.height)
                hdrDim.height = d.height;
            hdrDim.width += d.width;
        }
        return hdrDim;
    }

    public void disconnect(boolean reconnecting) {
        if (reconnecting) {
            if (space != null) {
                shutdown();
            }
        }
    }

    public void event(int eventID, int elapsedTime, int compensationTime) {
        // int count[] = interpreter.getEventCount();
        tableModel.setData();
    }

    public void space(ClientSpace space) {
        if (this.space.getID() == space.getID()) {
            this.space = space;
            tableModel.setSpace(this.space);
            if (space.getTileNum() > maxTileNum) {
                maxTileNum = space.getTileNum();
            }
        }
    }

    public void destroy() {
        tmColors = null;
        interpreter = null;
        space = null;
        pluginFrames = null;
        super.destroy();
    }
}
