/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package csplugins.ui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;

/**
 * Demonstration Program to Illustrate Usage of JFloatablePanel.
 *
 * @author Ethan Cerami
 */
public class Demo1 {
    private JFrame frame;

    /**
     * Create and Show GUI.
     */
    private void createAndShowGUI() {
        //  Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //  Create and set up the window.
        frame = new JFrame("Prototype");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //  Create Left Panel
        JFloatableTabbedPane leftTabbedPane = createLeftPanel();

        //  Create Center Panel
        JPanel centerPanel = createCenterPanel();

        //  Create Right Panel
        JFloatableTabbedPane rightTabbedPane = createRightPanel();

        //  Create Bottom Panel
        JFloatableTabbedPane bottomTabbedPaneJ = createBottomPanel();

        //  Add Panels to correct border locations
        frame.getContentPane().add(leftTabbedPane, BorderLayout.WEST);
        frame.getContentPane().add(centerPanel, BorderLayout.CENTER);
        frame.getContentPane().add(rightTabbedPane, BorderLayout.EAST);
        frame.getContentPane().add(bottomTabbedPaneJ, BorderLayout.SOUTH);

        //  Size it and Display it.
        frame.setSize(new Dimension(700, 700));
        frame.setVisible(true);
    }

    /**
     * Creates a Sample Bottom Panel.
     *
     * @return JFloatableTabbedPane Object.
     */
    private JFloatableTabbedPane createBottomPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(0, 100));
        ArrayList buttonList = new ArrayList();
        URL iconURL = Demo1.class.getResource("resources/info.gif");
        ImageIcon icon = new ImageIcon(iconURL);
        JButton b = new JButton(icon);
        URL iconURL2 = Demo1.class.getResource("resources/help.gif");
        ImageIcon icon2 = new ImageIcon(iconURL2);
        JButton b2 = new JButton(icon2);
        buttonList.add(b);
        buttonList.add(b2);

        JFloatableTabbedPane bottomTabbedPaneJ = new JFloatableTabbedPane
                (frame, JTabbedPane.RIGHT, BorderLayout.SOUTH);
        bottomTabbedPaneJ.addTab("Console", null, buttonList, panel);
        bottomTabbedPaneJ.setTabBackgroundColor(0, Color.LIGHT_GRAY);
        bottomTabbedPaneJ.setTabForeGroundColor(0, Color.BLACK);
        return bottomTabbedPaneJ;
    }

    /**
     * Creates a Sample Left Panel
     *
     * @return JFloatableTabbedPane Object.
     */
    private JFloatableTabbedPane createLeftPanel() {
        JFloatableTabbedPane leftTabbedPane = new JFloatableTabbedPane
                (frame, JTabbedPane.LEFT, BorderLayout.WEST);
        JPanel panel = new JPanel();
        leftTabbedPane.addTab("Network List", null, panel);
        return leftTabbedPane;
    }

    /**
     * Creates a Sample Right Panel.
     *
     * @return JFloatableTabbedPane Object.
     */
    private JFloatableTabbedPane createRightPanel() {
        JFloatableTabbedPane tabbedPaneJ = new JFloatableTabbedPane
                (frame, JTabbedPane.RIGHT, BorderLayout.EAST);
        URL iconURL = Demo1.class.getResource("resources/db.gif");
        ImageIcon icon = new ImageIcon(iconURL);
        JPanel panel1 = new JPanel();
        tabbedPaneJ.addTab("Search cPath", icon, panel1);

        iconURL = Demo1.class.getResource("resources/glasses.gif");
        icon = new ImageIcon(iconURL);
        JPanel panel2 = new JPanel();
        tabbedPaneJ.addTab("Node/Edge Details", icon, panel2);
        tabbedPaneJ.setTabBackgroundColor(1, new Color(204, 51, 51));

        return tabbedPaneJ;
    }

    /**
     * Creates a Sample Center Panel.
     *
     * @return JPanel Object.
     */
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(Color.WHITE);
        JLabel centerLabel = new JLabel("Network View goes here...");
        centerPanel.add(centerLabel);
        return centerPanel;
    }

    /**
     * Main Method.
     *
     * @param args Program Arguments.
     */
    public static void main(String[] args) {
        Demo1 p = new Demo1();
        p.createAndShowGUI();
    }
}
