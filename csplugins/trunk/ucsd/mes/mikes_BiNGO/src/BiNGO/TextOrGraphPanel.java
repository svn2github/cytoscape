package BiNGO;

/* * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
* *
* * Authors : Steven Maere, Karel Heymans
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
* * that allows user to switch between graph and text input
**/


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;


/**
 * ***************************************************************
 * TextOrGraphPanel.java:       Steven Maere (c) March 2005
 * -----------------------
 * <p/>
 * Class that extends JPanel and implements ActionListener ; makes panel
 * that allows user to switch between graph and text input
 * <p/>
 * ******************************************************************
 */


public class TextOrGraphPanel extends JPanel implements ActionListener {

    /*--------------------------------------------------------------
    Fields.
    --------------------------------------------------------------*/

    /**
     * radiobutton graph input.
     */
    private JRadioButton graphButton;
    /**
     * radiobutton text input.
     */
    private JRadioButton textButton;
    /**
     * panel with radio buttons.
     */
    private JPanel radioPanel;
    /**
     * pane with text area.
     */
    private JScrollPane scrollPane;
    // icons for the checkboxes.
    /**
     * Icon for unchecked box.
     */
    private Icon unchecked = new ToggleIcon(false);
    /**
     * Icon for checked box.
     */
    private Icon checked = new ToggleIcon(true);
    /**
     * the text area for gene input.
     */
    private JTextArea textInputArea;

    private static String GRAPHSTRING = "Get Cluster from Network";
    private static String TEXTSTRING = "Paste Genes from Text";

    /*private final int DIM_WIDTH = 500 ;
private final int DIM_HEIGHT = 200 ;*/

    /*-----------------------------------------------------------------
    CONSTRUCTOR.
    -----------------------------------------------------------------*/

    public TextOrGraphPanel() {

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

        c.fill = GridBagConstraints.BOTH;

        c.gridheight = 5;
        c.weighty = 5;
        gridbag.setConstraints(scrollPane, c);
        add(scrollPane);
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

        graphButton = new JRadioButton(GRAPHSTRING);
        graphButton.setIcon(unchecked);
        graphButton.setSelectedIcon(checked);
        graphButton.setMnemonic(KeyEvent.VK_N);
        graphButton.setActionCommand(GRAPHSTRING);
        graphButton.setSelected(true);

        textButton = new JRadioButton(TEXTSTRING);
        textButton.setIcon(unchecked);
        textButton.setSelectedIcon(checked);
        textButton.setMnemonic(KeyEvent.VK_T);
        textButton.setActionCommand(TEXTSTRING);

        //Group the radio buttons.
        ButtonGroup group = new ButtonGroup();
        group.add(graphButton);
        group.add(textButton);

        //Register a listener for the radio buttons.
        graphButton.addActionListener(this);
        textButton.addActionListener(this);

        //JTextArea
        textInputArea = new JTextArea(1000, 500);
        scrollPane = new JScrollPane(textInputArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        textInputArea.setEditable(false);
        textInputArea.setEnabled(false);

        //Put the radio buttons in a row in a panel.

        radioPanel = new JPanel(new GridLayout(1, 0));
        radioPanel.add(graphButton);
        radioPanel.add(textButton);

    }


    /**
     * Getter for the input text.
     *
     * @return input text.
     */
    public String getInputText() {
        return textInputArea.getText();
    }


    /**
     * Boolean method for checking whether box is checked or not.
     *
     * @return boolean checked or not checked.
     */
    public boolean graphButtonChecked() {
        return graphButton.isSelected();
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
        if (e.getActionCommand().equals(TEXTSTRING)) {
            textInputArea.setEnabled(true);
            textInputArea.setEditable(true);
        } else {
            textInputArea.setEnabled(false);
            textInputArea.setEditable(false);
        }
    }


}
