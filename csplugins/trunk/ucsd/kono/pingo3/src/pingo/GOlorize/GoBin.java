/* * Modified Date: Jul.27.2010
 * * by : Steven Maere
 * */

/*
 * GoBin.java
 *
 * Created on July 28, 2006, 2:32 PM
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * The software and documentation provided hereunder is on an "as is" basis,
 * and the Pasteur Institut
 * has no obligations to provide maintenance, support,
 * updates, enhancements or modifications.  In no event shall the
 * Pasteur Institut
 * be liable to any party for direct, indirect, special,
 * incidental or consequential damages, including lost profits, arising
 * out of the use of this software and its documentation, even if
 * the Pasteur Institut
 * has been advised of the possibility of such damage. See the
 * GNU General Public License for more details: 
 *                http://www.gnu.org/licenses/gpl.txt.
 *
 * Authors: Olivier Garcia
 * Adapted by Steven Maere July 2010
 */

package pingo.GOlorize;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.plugin.CyPluginAdapter;
import org.cytoscape.view.model.CyNetworkView;

import pingo.ModuleNetwork;
import pingo.SettingsPanel;

/**
 * @author ogarcia
 * @author stmae
 */
public class GoBin extends javax.swing.JFrame {

	private final JTabbedPane jTabbedPane;
	private boolean windowClosed = false;
	private int resultPanelCount = 0;

	private SettingsPanel settingsPanel;
	private CyNetworkView networkView;
	
	private final CyPluginAdapter adapter;

	public GoBin(pingo.SettingsPanel settingsPanel, CyNetworkView networkView, final CyPluginAdapter adapter) {
		this.adapter = adapter;
		this.setTitle("PiNGO output");
		this.settingsPanel = settingsPanel;
		this.networkView = networkView;
		this.jTabbedPane = new javax.swing.JTabbedPane();
		initComponents();
		this.windowClosed = false;
	}

	private void initComponents() {

		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();

		getJTabbedPane().setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().add(getJTabbedPane(), java.awt.BorderLayout.CENTER);

		pack();

		this.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				windowClosed = true;
			}
		});

		this.setLocation(25, screenSize.height - 450);
		this.setSize(screenSize.width - 50, 400);

		setVisible(true);
		setResizable(true);
		this.validate();
		this.repaint();
	}

	public void createResultTab(ModuleNetwork M, Map pvals, Map mapSmallX, Map mapSmallN, Map mapBigX,
			Map mapBigN, Map neighbors, String fileName, String annotationFile, String ontologyFile,
			String testString, String correctionString, CyNetwork currentNetwork, CyNetworkView currentNetworkview) {

		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		ResultPanel result = new ResultPanel(M, pvals, mapSmallX, mapSmallN, mapBigX, mapBigN, neighbors, fileName,
				annotationFile, ontologyFile, testString, correctionString, currentNetwork, currentNetworkview, this, adapter);
		if (getResultPanelCount() != 0)
			result.setTabName(trouveBonNom(result.getTabName()));

		getJTabbedPane().addTab(result.getTabName(), result);
		getJTabbedPane().setSelectedIndex(getJTabbedPane().getTabCount() - 1);
		result.validate();
		this.validate();
		resultPanelCount++;

	}

	private String trouveBonNom(String nom) {
		String retour = nom;
		for (int i = 1; i < getResultPanelCount() + 1; i++) {
			if (nom.equals(((ResultPanel) getResultPanelAt(i)).getTabName())) {
				String aChanger = nom;

				if (aChanger.matches(".*__[0123456789]$")) {
					String nombre2 = "";
					String temp = aChanger;
					while (temp.matches(".*__[0123456789]$")) {
						nombre2 = temp.substring(temp.length() - 1) + nombre2;
						temp = temp.substring(0, temp.length() - 1);
					}

					int nombre = Integer.parseInt(nombre2) + 1;
					retour = temp + nombre;
					retour = trouveBonNom(retour);
				} else {
					retour = aChanger + "__1";
					retour = trouveBonNom(retour);
				}

			}
		}
		return retour;
	}

	void synchroSelections(ResultPanel result) {

		JTable jtable;
		String gene;

		try {
			jtable = result.jTable1;
			for (int j = 0; j < jtable.getRowCount(); j++) {
				gene = (String) jtable.getValueAt(j, result.GENE_COLUMN);
				jtable.setValueAt(new Boolean(false), j, result.SELECT_COLUMN);
			}
		} catch (Exception e) {
			// on s'en fout c'etait juste le premier tab a etre cree
		}

	}

	void removeTab(ResultPanel result) {
		for (int i = 1; i < getResultPanelCount() + 1; i++) {
			if ((ResultPanel) getResultPanelAt(i) == result) {
				getJTabbedPane().removeTabAt(i - 1);
				break;
			}

		}
		resultPanelCount--;
	}

	JTable getResultTableAt(int i) {
		return ((ResultPanel) getJTabbedPane().getComponentAt(i - 1)).jTable1;
	}

	ResultPanel getResultPanelAt(int i) {
		return (ResultPanel) getJTabbedPane().getComponentAt(i - 1);
	}

	ResultPanel getResultTabAt(int i) {
		return (ResultPanel) getJTabbedPane().getComponentAt(i);
	}

	public JTabbedPane getJTabbedPane() {
		return jTabbedPane;
	}

	public int getResultPanelCount() {
		return resultPanelCount;
	}

	public boolean isWindowClosed() {
		return this.windowClosed;
	}

}
