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
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;


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


public class TypeOfIdentifierPanel extends JPanel implements ActionListener {

    /*--------------------------------------------------------------
    Fields.
    --------------------------------------------------------------*/

    /**
     * radiobutton Entrez Gene GeneID (formerly LocusLink) input.
     */
    private JRadioButton [] button_list;
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
    private TreeMap identifier_labels;
	private String identifier_def;

    /*private final int DIM_WIDTH = 500 ;
    private final int DIM_HEIGHT = 200 ;*/

    /*-----------------------------------------------------------------
    CONSTRUCTOR.
    -----------------------------------------------------------------*/

    public TypeOfIdentifierPanel(TreeMap identifier_labels, String identifier_def) {

        super();
        //setPreferredSize(new Dimension(DIM_WIDTH, DIM_HEIGHT));
        setOpaque(false);
        this.identifier_labels = identifier_labels;
		this.identifier_def = identifier_def;
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
     */
    public void makeJComponents() {
        String key;
        String value;
        JRadioButton id_button;

        group = new ButtonGroup();
        radioPanel = new JPanel(new GridLayout(1, 0));
        button_list = new JRadioButton [identifier_labels.size()];
        int num = 0;

        for (Iterator iter = identifier_labels.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            key = (String) entry.getKey();
            value = (String) entry.getValue();

            id_button = new JRadioButton(key, false);
            id_button.setIcon(unchecked);
            id_button.setSelectedIcon(checked);
            id_button.setMnemonic(KeyEvent.VK_G);
            id_button.setActionCommand(key);
            id_button.addActionListener(this);
			
			if(identifier_def.equals(key)){id_button.setSelected(true);}

            group.add(id_button);
            radioPanel.add(id_button);
            button_list[num] = id_button;
            num++;

        }

    }

    /**
     * Boolean method for checking whether box is checked or not.
     *
     * @return boolean checked or not checked.
     */
    public String getCheckedButton() {
        String id = "0";
        for (int i = 0; i < button_list.length; i++) {
            if (button_list[i].isSelected()) {
                id = button_list[i].getActionCommand();
            }
        }
        return id;
    }

    public void disableButtons() {
        for (int i = 0; i < button_list.length; i++) {
            button_list[i].setEnabled(false);
        }
    }

    public void enableButtons() {
        for (int i = 0; i < button_list.length; i++) {
            button_list[i].setEnabled(true);
        }
    }

    /*----------------------------------------------------------------
    ACTIONLISTENER-PART.
    ----------------------------------------------------------------*/

    /**
     * Method performed when radiobutton clicked.
     */


    public void actionPerformed(ActionEvent e) {
        //empty for now
    }

}
