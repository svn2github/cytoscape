package pingo;

/**
 * * Copyright (c) 2010 Flanders Interuniversitary Institute for Biotechnology (VIB)
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
 * * Date: Jul.27.2010
 * * Description: PiNGO is a Cytoscape plugin that leverages functional enrichment
 * * analysis to discover lead genes from biological networks.          
 **/

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 * ***************************************************************
 * VizPanel.java: Steven Maere (c) 2005-2010 -----------------------
 * <p/>
 * Class that extends JPanel and implements ActionListener ; makes panel that
 * allows user to choose the type of gene identifiers used in the analysis.
 * <p/>
 * ******************************************************************
 */

public class VizPanel extends JPanel implements ItemListener {

	private static final long serialVersionUID = -50442866656747125L;
	
	/**
	 * checkBox visualization
	 */
	private JCheckBox vizBox;
	
	/**
	 * radiobutton underrepresentation
	 */
	// private JCheckBox noButton;
	/**
	 * checkBox for tabbed view
	 */
	private JCheckBox tabBox;
	private JCheckBox starBox;

	/**
	 * panel with radio buttons.
	 */
	private JPanel checkBoxPanel;
	// icons for the checkboxes.
	/**
	 * Icon for unchecked box.
	 */
	// private Icon unchecked = new ToggleIcon(false);
	/**
	 * Icon for checked box.
	 */
	// private Icon checked = new ToggleIcon(true);

	public static final String VIZSTRING = "Visualization";
	public static final String NOVIZSTRING = "No Visualization";
	public static final String TABSTRING = "Multiple tabs";
	public static final String NOTABSTRING = "No tabs";
	public static final String NOSTARSTRING = "Complete network";
	public static final String STARSTRING = "Star network";

	private String def;
	private String tabDef;
	private String starDef;

	/*-----------------------------------------------------------------
	CONSTRUCTOR.
	-----------------------------------------------------------------*/

	public VizPanel(String def, String tabDef, String starDef) {
		super();
		
		this.def = def;
		this.tabDef = tabDef;
		this.starDef = starDef;
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

		gridbag.setConstraints(checkBoxPanel, c);
		add(checkBoxPanel);

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
	 * @param sort
	 *            string that denotes part of the name of the button.
	 */
	public void makeJComponents() {

		vizBox = new JCheckBox(VIZSTRING, true);
		tabBox = new JCheckBox(TABSTRING, true);
		starBox = new JCheckBox(STARSTRING, true);

		if (def != null && def.equals(VIZSTRING))
			vizBox.setSelected(true);
		else
			vizBox.setSelected(false);

		if (tabDef != null && tabDef.equals(TABSTRING))
			tabBox.setSelected(true);
		else
			tabBox.setSelected(false);

		if (starDef != null && starDef.equals(STARSTRING))
			starBox.setSelected(true);
		else
			starBox.setSelected(false);

		// Register a listener for the radio buttons.
		vizBox.addItemListener(this);
		tabBox.addItemListener(this);
		starBox.addItemListener(this);

		// Put the radio buttons in a row in a panel.

		checkBoxPanel = new JPanel(new GridLayout(1, 0));
		checkBoxPanel.add(vizBox);
		checkBoxPanel.add(tabBox);
		checkBoxPanel.add(starBox);

	}

	/**
	 * Boolean method for checking whether box is checked or not.
	 * 
	 * @return boolean checked or not checked.
	 */
	public String getVizMode() {
		String id = VIZSTRING;
		if (vizBox.isSelected()) {
			id = VIZSTRING;
		} else {
			id = NOVIZSTRING;
		}
		return id;
	}

	public String getTabMode() {
		String id = TABSTRING;
		if (tabBox.isSelected()) {
			id = TABSTRING;
		} else {
			id = NOTABSTRING;
		}
		return id;
	}

	public String getStarMode() {
		String id = STARSTRING;
		if (starBox.isSelected()) {
			id = STARSTRING;
		} else {
			id = NOSTARSTRING;
		}
		return id;
	}

	public void disableButtons() {
		vizBox.setEnabled(false);
		tabBox.setEnabled(false);
		starBox.setEnabled(false);
	}

	public void enableButtons() {
		vizBox.setEnabled(true);
		tabBox.setEnabled(true);
		starBox.setEnabled(true);
	}

	/*----------------------------------------------------------------
	ACTIONLISTENER-PART.
	----------------------------------------------------------------*/

	/**
	 * Method performed when checkBox clicked.
	 * 
	 * @param event
	 *            event that triggers action, here clicking of the button.
	 */

	public void itemStateChanged(ItemEvent e) {

	}

}
