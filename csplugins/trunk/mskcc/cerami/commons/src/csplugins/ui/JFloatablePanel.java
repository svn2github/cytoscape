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
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;

/**
 * FloatablePanel Object.
 *
 * @author Ethan Cerami.
 */
class JFloatablePanel extends JPanel {
    private ImageIcon floatIcon;
    private ImageIcon dockIcon;
    private JButton button;
    private JFloatableTabbedPane owner;
    private JToolBar toolBar;
    private ArrayList buttonList;
    private JLabel titleLabel;

    /**
     * Constructor.
     *
     * @param owner         JFloatableTabbedPane Owner.
     * @param title         Title of Tab.
     * @param internalPanel Internal JPanel Object.
     */
    public JFloatablePanel(JFloatableTabbedPane owner, String title,
            JPanel internalPanel) {
        init(owner, title, internalPanel);
    }

    /**
     * Constructor.
     *
     * @param owner         JFloatableTabbedPane Owner.
     * @param title         Title of Tab.
     * @param buttonList    JToolBar.
     * @param internalPanel Internal JPanel Object.
     */
    public JFloatablePanel(JFloatableTabbedPane owner, String title,
            ArrayList buttonList, JPanel internalPanel) {
        this.buttonList = buttonList;
        init(owner, title, internalPanel);
    }

    /**
     * Initializes Component.
     *
     * @param owner         JFloatableTabbedPane owner.
     * @param title         Tab Title.
     * @param internalPanel Internal Panel Object.
     */
    private void init(JFloatableTabbedPane owner, String title,
            JPanel internalPanel) {
        this.owner = owner;
        this.initIcons();
        this.setLayout(new BorderLayout());
        this.add(internalPanel, BorderLayout.CENTER);

        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        initButtonBar(toolBar, title);
        this.add(toolBar, BorderLayout.PAGE_START);
    }

    /**
     * Sets the Background Color of the Tool Bar Header.
     *
     * @param color Color.
     */
    public void setToolBarBackroundColor(Color color) {
        toolBar.setBackground(color);
    }

    /**
     * Sets the Foreground Color of the Tool Bar Header.
     *
     * @param color Color.
     */
    public void setToolBarForeGroundColor(Color color) {
        titleLabel.setForeground(color);
    }

    /**
     * Initialize all Icons.
     */
    private void initIcons() {
        URL floatIconURL = Demo1.class.getResource
                ("resources/float2.gif");
        URL pinIconURL = Demo1.class.getResource
                ("resources/pin.gif");
        floatIcon = new ImageIcon(floatIconURL);
        dockIcon = new ImageIcon(pinIconURL);
    }

    /**
     * Initialize Tool Bar.
     *
     * @param toolBar JToolBar Object.
     * @param title   Title of Tab.
     */
    void initButtonBar(JToolBar toolBar, String title) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.LIGHT_GRAY);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        //  Add Specific List of Buttons
        if (buttonList != null) {
            for (int i = 0; i < buttonList.size(); i++) {
                JButton b = (JButton) buttonList.get(i);
                b.setMargin(new Insets(2, 2, 2, 2));
                buttonPanel.add(b);
                buttonPanel.add(Box.createRigidArea(new Dimension(2, 0)));
            }
        }

        toolBar.setLayout(new BorderLayout());
        toolBar.setBackground(new Color(107, 135, 191));
        titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);

        Font font = (Font) UIManager.get("Label.font");
        titleLabel.setFont(font);
        titleLabel.setBorder(new EmptyBorder(1, 4, 1, 1));
        toolBar.add(titleLabel, BorderLayout.WEST);
        button = new JButton();

        //  Set 0 Margin All-Around and setBorderPainted to false
        //  so that button appears as small as possible
        button.setMargin(new Insets(2, 2, 2, 2));
        //button.setBorderPainted(false);

        attachActionListener();
        button.setHorizontalAlignment(SwingConstants.RIGHT);

        setFloating(false);
        buttonPanel.add(button);
        toolBar.add(buttonPanel, BorderLayout.EAST);
    }

    /**
     * Attach Button Listeners.
     */
    private void attachActionListener() {
        button.addActionListener(new ActionListener() {
            /**
             * User has chosen to float/dock the Tabs.
             *
             * @param e ActionEvent.
             */
            public void actionPerformed(ActionEvent e) {
                owner.flipFloatingStatus();
            }
        });
    }

    /**
     * Sets the Floating Status of this Panel.
     *
     * @param floating floating Status.
     */
    public void setFloating(boolean floating) {
        if (floating) {
            button.setIcon(dockIcon);
            button.setToolTipText("Dock Window");
        } else {
            button.setIcon(floatIcon);
            button.setToolTipText("Float Window");
        }
    }
}
