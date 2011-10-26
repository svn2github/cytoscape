package BiNGO;

/* * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
* *
* * Authors : Steven Maere
* *
* * This program is free software; you can redistribute it and/or modify
* * it under the terms of the GNU General Public License as published by
* * the Free Software Foundation; either version 2 of the License, or
* * (at your option) any later version.
* *
* * This program is distributed in the hope that it will be useful,
* * but WITHOUT ANY WARRANTY; without even the implied warranty of
* * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* * The software and documentation provided hereunder is on an "as is" basis,
* * and the Flanders Interuniversitary Institute for Biotechnology
* * has no obligations to provide maintenance, support,
* * updates, enhancements or modifications.  In no event shall the
* * Flanders Interuniversitary Institute for Biotechnology
* * be liable to any party for direct, indirect, special,
* * incidental or consequential damages, including lost profits, arising
* * out of the use of this software and its documentation, even if
* * the Flanders Interuniversitary Institute for Biotechnology
* * has been advised of the possibility of such damage. See the
* * GNU General Public License for more details.
* *
* * You should have received a copy of the GNU General Public License
* * along with this program; if not, write to the Free Software
* * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
* *
* * Authors: Steven Maere
* * Date: Mar.25.2005
* * Description: Class that extends JPanel and implements ActionListener ; makes panel
* * that allows user to choose the type of gene identifiers used in the analysis.
**/


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;


/**
 * ***************************************************************
 * TypeOfIdentifierPanel.java:       Steven Maere (c) March 2005
 * -----------------------
 * <p/>
 * Class that extends JPanel and implements ActionListener ; makes panel
 * that allows user to choose the type of gene identifiers used in the analysis.
 * <p/>
 * ******************************************************************
 */


public class OverUnderPanel extends JPanel implements ActionListener {

    /*--------------------------------------------------------------
    Fields.
    --------------------------------------------------------------*/

    /**
     * radiobutton overrepresentation
     */
    private JRadioButton overButton;
    /**
     * radiobutton underrepresentation
     */
    private JRadioButton underButton;
    /**
     * button group
     */
    private ButtonGroup group;
    /**
     * panel with radio buttons.
     */
    private JPanel radioPanel;
    // icons for the checkboxes.
    /**
     * Icon for unchecked box.
     */
    private Icon unchecked = new ToggleIcon(false);
    /**
     * Icon for checked box.
     */
    private Icon checked = new ToggleIcon(true);

    private static String OVERSTRING = "Overrepresentation";
    private static String UNDERSTRING = "Underrepresentation";

    /*private final int DIM_WIDTH = 500 ;
private final int DIM_HEIGHT = 200 ;*/

    /*-----------------------------------------------------------------
    CONSTRUCTOR.
    -----------------------------------------------------------------*/

    public OverUnderPanel() {

        super();
        //setPreferredSize(new Dimension(DIM_WIDTH, DIM_HEIGHT));
        setOpaque(false);

        makeJComponents();

        // Layout with GridBagLayout.

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        setLayout(gridbag);
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.HORIZONTAL;

        gridbag.setConstraints(radioPanel, c);
        add(radioPanel);

    }

    /*----------------------------------------------------------------
    PAINTCOMPONENT.
    ----------------------------------------------------------------*/

    /**
     * Paintcomponent, draws panel.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    /*----------------------------------------------------------------
    METHODS.
    ----------------------------------------------------------------*/

    /**
     * Method that creates the JComponents.
     *
     * @param sort string that denotes part of the name of the button.
     */
    public void makeJComponents() {

        overButton = new JRadioButton(OVERSTRING, true);
        overButton.setIcon(unchecked);
        overButton.setSelectedIcon(checked);
        overButton.setMnemonic(KeyEvent.VK_G);
        overButton.setActionCommand(OVERSTRING);

        underButton = new JRadioButton(UNDERSTRING, false);
        underButton.setIcon(unchecked);
        underButton.setSelectedIcon(checked);
        underButton.setMnemonic(KeyEvent.VK_S);
        underButton.setActionCommand(UNDERSTRING);

        //Group the radio buttons.
        group = new ButtonGroup();
        group.add(overButton);
        group.add(underButton);

        //Register a listener for the radio buttons.
        overButton.addActionListener(this);
        underButton.addActionListener(this);

        //Put the radio buttons in a row in a panel.

        radioPanel = new JPanel(new GridLayout(1, 0));
        radioPanel.add(overButton);
        radioPanel.add(underButton);

    }

    /**
     * Boolean method for checking whether box is checked or not.
     *
     * @return boolean checked or not checked.
     */
    public String getCheckedButton() {
        String id = OVERSTRING;
        if (overButton.isSelected()) {
            id = OVERSTRING;
        } else if (underButton.isSelected()) {
            id = UNDERSTRING;
        }
        return id;
    }

    public void disableButtons() {
        overButton.setEnabled(false);
        underButton.setEnabled(false);
    }

    public void enableButtons() {
        overButton.setEnabled(true);
        underButton.setEnabled(true);
    }

    /*----------------------------------------------------------------
    ACTIONLISTENER-PART.
    ----------------------------------------------------------------*/

    /**
     * Method performed when radiobutton clicked.
     *
     * @param event event that triggers action, here clicking of the button.
     */


    public void actionPerformed(ActionEvent e) {
        //empty for now
     }

}
