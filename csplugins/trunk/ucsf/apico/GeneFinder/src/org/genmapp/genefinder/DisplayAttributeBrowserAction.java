package org.genmapp.genefinder;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.SwingConstants;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.cytopanels.CytoPanelState;

/**
 * 
 *  This class is enabled only when browser plugin is loaded.
 *  User can on/off browser panel (CytoPanel3) by using f5 key.
 * 
 * @author kono
 *
 */
public class DisplayAttributeBrowserAction extends CytoscapeAction {

	public DisplayAttributeBrowserAction() {
		super("Show/Hide attribute browser");
//		setPreferredMenu("Data");
//		setAcceleratorCombo(KeyEvent.VK_F5, 0);

	}

	public void actionPerformed(ActionEvent ev) {
		
		// Check the state of the browser Panel
		CytoPanelState curState = Cytoscape.getDesktop().getCytoPanel(
				SwingConstants.SOUTH).getState();
		int panelCount = Cytoscape.getDesktop().getCytoPanel(
				SwingConstants.SOUTH).getCytoPanelComponentCount();

		int targetIndex = 0;
		String curName = null;
		
		for(int i = 0; i < panelCount; i++ ) {
			Cytoscape.getDesktop().getCytoPanel(
					SwingConstants.SOUTH).setSelectedIndex(i);
			curName = Cytoscape.getDesktop().getCytoPanel(
					SwingConstants.SOUTH).getSelectedComponent().getName();
			//System.out.println("CurName = " + curName);
			if( curName.equals("NodeAttributeBrowser")) {
				targetIndex = i;
				Cytoscape.getDesktop().getCytoPanel(
						SwingConstants.SOUTH).setSelectedIndex(targetIndex);
				break;
			}
		}
		
		// Check panel state and switch panel state.
		if (curState == CytoPanelState.HIDE) {
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).setState(
					CytoPanelState.DOCK);
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH)
					.setSelectedIndex(targetIndex);
		} else {
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).setState(
					CytoPanelState.HIDE);
		}

	}// action performed
}
