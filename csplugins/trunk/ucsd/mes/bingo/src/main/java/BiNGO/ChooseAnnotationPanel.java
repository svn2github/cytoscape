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
 * * Date: Apr.20.2005
 * * Class that extends JPanel and implements ActionListener. Makes
 * * a panel with a drop-down box of organism/annotation choices. Custom... opens FileChooser   
 **/


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
//import java.util.TreeMap;


/**
 * ***************************************************************
 * ChooseAnnotationPanel.java:   Steven Maere (c) April 2005
 * -----------------------
 * <p/>
 * Class that extends JPanel and implements ActionListener. Makes
 * a panel with a drop-down box of organism/annotation choices. Custom... opens FileChooser
 * ******************************************************************
 */

public class ChooseAnnotationPanel extends JPanel implements ActionListener {

    /*--------------------------------------------------------------
     Fields.
    --------------------------------------------------------------*/
    private final String CUSTOM = BingoAlgorithm.CUSTOM;
    private final String NONE = BingoAlgorithm.NONE;


    /**
     * JComboBox with the possible choices.
     */
    public JComboBox choiceBox;
    /**
     * Type Of Identifier choice panel for precompiled annotations
     */
   // private TypeOfIdentifierPanel typeOfIdentifierPanel;
    /**
     * the selected file.
     */
    private String specified_species = null;
    private File openFile = null;
    /**
     * parent window
     */
    private Component settingsPanel;
    /**
     * boolean to assess default or custom input
     */
    private boolean def = true;
    /**
     * BiNGO directory path
     */
    private String bingoDir;

    private String [] choiceArray;
   // private TreeMap identifier_labels;
//	private String identifier_def;

    /*-----------------------------------------------------------------
     CONSTRUCTOR.
    -----------------------------------------------------------------*/

    /**
     * Constructor with a string argument that becomes part of the label of
     * the button.
     *
     * @param settingsPanel : parent window
     */
    public ChooseAnnotationPanel(Component settingsPanel, String bingoDir, String [] choiceArray, String choice_def) {
        super();
        this.bingoDir = bingoDir;
        this.settingsPanel = settingsPanel;
        setOpaque(false);

        this.choiceArray = choiceArray;
    //   this.identifier_labels = identifier_labels;
    //	 this.identifier_def = identifier_def;
        makeJComponents();

        // Layout with GridBagLayout.

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        setLayout(gridbag);
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.HORIZONTAL;

        gridbag.setConstraints(choiceBox, c);
        add(choiceBox);

        c.gridheight = 2;
        c.weighty = 2;
  //      gridbag.setConstraints(typeOfIdentifierPanel, c);
  //      add(typeOfIdentifierPanel);
  //      typeOfIdentifierPanel.enableButtons();

        //defaults

        choiceBox.setSelectedItem(choice_def);
        specified_species = choice_def;
        //File tmp = new File(bingoDir, "BiNGO");
        //openFile = new File(tmp, choice_def + "_default");
        def = true;


    }

    /*----------------------------------------------------------------
    PAINTCOMPONENT.
    ----------------------------------------------------------------*/

    /**
     * Paintcomponent, part where the drawing on the panel takes place.
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

        // choiceBox.
        choiceBox = new JComboBox(choiceArray);
        choiceBox.setEditable(false);
        choiceBox.addActionListener(this);

   //     typeOfIdentifierPanel = new TypeOfIdentifierPanel(identifier_labels,identifier_def);

    }

    /**
     * Method that returns the TypeOfIdentifierPanel.
     *
     * @return File selected file.
     */
 /*   public TypeOfIdentifierPanel getTypeOfIdentifierPanel() {
        return typeOfIdentifierPanel;
    }*/


    /**
     * Method that returns the specified species.
     *
     * @return File selected file.
     */
    public String getSpecifiedSpecies() {
        return specified_species;
    }

    /**
     * Method that returns the selected item.
     *
     * @return String selection.
     */
    public String getSelection() {
        return choiceBox.getSelectedItem().toString();
    }

    /**
     * Method that returns 1 if one of default choices was chosen, 0 if custom
     */
    public boolean getDefault() {
        return def;
    }

    /*----------------------------------------------------------------
    LISTENER-PART.
    ----------------------------------------------------------------*/

    /**
     * Method performed when button clicked.
     *
     * @param event event that triggers action, here clicking of the button.
     */
    public void actionPerformed(ActionEvent event) {
     //   typeOfIdentifierPanel.enableButtons();
        File tmp = new File(bingoDir, "BiNGO");
        if (choiceBox.getSelectedItem().equals(NONE)) {
            specified_species = NONE;
            def = true;
        } else if (choiceBox.getSelectedItem().equals(CUSTOM)) {
	    specified_species = CUSTOM;
            JFileChooser chooser = new JFileChooser(bingoDir);
            int returnVal = chooser.showOpenDialog(settingsPanel);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                openFile = chooser.getSelectedFile();
                choiceBox.setEditable(true);
                choiceBox.setSelectedItem(openFile.toString());
                choiceBox.setEditable(false);
     //           typeOfIdentifierPanel.disableButtons();
                def = false;
            }
            if (returnVal == JFileChooser.CANCEL_OPTION) {
                choiceBox.setSelectedItem(NONE);
		specified_species = NONE;
                def = true;
            }
        } else {
            specified_species = (String)choiceBox.getSelectedItem();
            def = true;
        }
    }
}
