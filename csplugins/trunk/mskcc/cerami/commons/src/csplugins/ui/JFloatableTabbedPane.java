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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * A Floatable/Dockable set of Tabs.
 *
 * @author Ethan Cerami
 */
public class JFloatableTabbedPane extends JTabbedPane {

    /**
     * List of All Tabs.
     */
    private ArrayList tabList;

    /**
     * JFrame Owner.
     * The ownerFrame initally contains the JFloatableTabbedPane.
     */
    private JFrame ownerFrame;

    /**
     * External Frame used to hold the floating tabs.
     */
    private JFrame externalFrame;

    /**
     * Location of Floating Tabs, relative to owerFrame.
     */
    private String borderLayoutLocation;

    /**
     * Current Status of Floating Tabs.
     */
    private boolean isFloating = false;

    /**
     * Inset between owner frame and floating window.
     */
    private static final int INSET = 5;

    /**
     * Constructor.
     *
     * @param ownerFrame           JFrame that owns this component.
     * @param tabPlacement         the placement for the tabs relative to the
     *                             content, e.g. JTabbedPane.RIGHT.
     * @param borderLayoutLocation location of tabs, relative to the owner.
     *                             Must be one of:
     *                             BorderLayout.EAST, BorderLayout.WEST,
     *                             BorderLayout.NORTH or BorderLayout.SOUTH.
     */
    public JFloatableTabbedPane(JFrame ownerFrame, int tabPlacement,
            String borderLayoutLocation) {
        super(tabPlacement);

        //  Validate that Owner Frame uses a BorderLayout
        LayoutManager layoutMgr = ownerFrame.getContentPane().getLayout();
        if (!(layoutMgr instanceof BorderLayout)) {
            throw new IllegalArgumentException("Owner Frame must use the "
                    + " BorderLayout.");
        }

        //  Validate borderLayoutLocation
        if (!(borderLayoutLocation.equals(BorderLayout.EAST)
                || borderLayoutLocation.equals(BorderLayout.WEST)
                || borderLayoutLocation.equals(BorderLayout.NORTH)
                || borderLayoutLocation.equals(BorderLayout.SOUTH))) {
            throw new IllegalArgumentException("Invalid parameter:  "
                    + "borderLayoutLocation.  Must be one of:  "
                    + "BorderLayout.EAST, BorderLayout.WEST, "
                    + "BorderLayout.NORTH, or BorderLayout.SOUTH");
        }

        //  Store Incoming Parameters
        this.borderLayoutLocation = borderLayoutLocation;
        this.ownerFrame = ownerFrame;
        this.tabList = new ArrayList();
    }

    /**
     * Sets the Background Color of the ToolBar in Specified Tab.
     *
     * @param tabIndex Tab Index.
     * @param color    Background  Color.
     */
    public void setTabBackgroundColor(int tabIndex, Color color) {
        JFloatablePanel jFloatablePanel =
                (JFloatablePanel) tabList.get(tabIndex);
        jFloatablePanel.setToolBarBackroundColor(color);
    }

    /**
     * Sets the Foreground Color of the ToolBar in Specified Tab.
     *
     * @param tabIndex Tab Index.
     * @param color    Background  Color.
     */
    public void setTabForeGroundColor(int tabIndex, Color color) {
        JFloatablePanel jFloatablePanel =
                (JFloatablePanel) tabList.get(tabIndex);
        jFloatablePanel.setToolBarForeGroundColor(color);
    }

    /**
     * Adds a New Tab.
     *
     * @param title Title of Tab.
     * @param icon  Icon of Tab.
     * @param panel Internal JPanel Object.
     */
    public void addTab(String title, Icon icon, JPanel panel) {
        JFloatablePanel jFloatablePanel =
                new JFloatablePanel(this, title, panel);
        tabList.add(jFloatablePanel);
        super.addTab(title, icon, jFloatablePanel);
    }

    /**
     * Adds a New Tab.
     *
     * @param title      Title of Tab.
     * @param icon       Icon of Tab.
     * @param buttonList ArrayList of JButton Objects.
     * @param panel      Internal JPanel Object.
     */
    public void addTab(String title, Icon icon, ArrayList buttonList,
            JPanel panel) {
        JFloatablePanel jFloatablePanel = new JFloatablePanel(this, title,
                buttonList, panel);
        tabList.add(jFloatablePanel);
        super.addTab(title, icon, jFloatablePanel);
    }

    /**
     * Float/Dock the Tabs, depending on current status.
     */
    public void flipFloatingStatus() {
        //  If we are currently Floating, we want to dock the window
        if (isFloating) {

            //  Remove Tabs from External Frame
            externalFrame.remove(this);

            //  Add Tabs Back to Fixed Parent Frame
            ownerFrame.getContentPane().add(this, borderLayoutLocation);

            //  Dispose of the External Frame
            externalFrame.dispose();

            //  If we are currently Docked, we want to float the window.
        } else {

            //  Create New External Frame and attach Window Listener
            externalFrame = new JFrame();
            addWindowListener();

            //  Add Tabs to the New External Frame
            Container contentPane = externalFrame.getContentPane();
            contentPane.add(this, BorderLayout.CENTER);
            setLocationOfExternalFrame(externalFrame);
            externalFrame.setSize(this.getSize());
            externalFrame.validate();

            //  Remove Tabs from Parent Frame
            ownerFrame.remove(this);

            //  Show New External Frame
            externalFrame.show();
        }
        isFloating = !isFloating;

        //  Adjust Icons for all embedded Panels.
        for (int i = 0; i < tabList.size(); i++) {
            JFloatablePanel panelJ = (JFloatablePanel) tabList.get(i);
            panelJ.setFloating(isFloating);
        }

        //  Layout everything
        this.validate();
        ownerFrame.validate();
    }

    /**
     * Adds the Correct Window Listener.
     */
    private void addWindowListener() {
        externalFrame.addWindowListener(new WindowAdapter() {

            /**
             * Window is Closing.
             *
             * @param e Window Event.
             */
            public void windowClosing(WindowEvent e) {
                flipFloatingStatus();
            }
        });
    }

    /**
     * Sets the Location of the External Frame.  Location is based on the
     * borderLayoutLocation attribute.
     *
     * @param externalFrame ExternalFrame Object.
     */
    private void setLocationOfExternalFrame(JFrame externalFrame) {
        Point point = ownerFrame.getLocation();
        double width = ownerFrame.getSize().getWidth();
        double height = ownerFrame.getSize().getHeight();

        int x = 0;
        int y = 0;

        if (borderLayoutLocation.equals(BorderLayout.SOUTH)) {
            x = point.x;
            y = point.y + (int) height + INSET;
        } else if (borderLayoutLocation.equals(BorderLayout.EAST)) {
            x = point.x + (int) width + INSET;
            y = point.y;
        } else if (borderLayoutLocation.equals(BorderLayout.WEST)) {
            x = point.x - INSET - this.getPreferredSize().width;
            y = point.y;
        }

        //  Adjust for Left-Most Case, as needed.
        x = Math.max(0, x);
        y = Math.max(0, y);

        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dm = tk.getScreenSize();
        int screenWidth = (int) dm.getWidth();
        int screenHeight = (int) dm.getHeight();

        //  Adjust for Right-Most Case, as needed.
        if (x + this.getPreferredSize().width > screenWidth) {
            x = screenWidth - getPreferredSize().width;
        }

        //  Adjust for Bottom-Most Case, as needed.
        if (y + this.getPreferredSize().height > screenHeight) {
            y = screenHeight - getPreferredSize().height;
        }

        externalFrame.setLocation(x, y);
    }
}

