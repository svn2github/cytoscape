
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package example;

import cytoscape.util.CytoscapeAction;
import cytoscape.Cytoscape; 
import cytoscape.CytoscapeInit; 
import cytoscape.actions.LoadNetworkTask;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;

public class AbstractOneHundredKAction extends CytoscapeAction {

	protected List<TestScenario> testScenarios;

	protected AbstractOneHundredKAction(String title) {
		super(title);
		setPreferredMenu("Plugins.100K Object Performance Test");
		testScenarios = new ArrayList<TestScenario>();	
	}

	public void actionPerformed(ActionEvent e) {

		// make sure we should proceed
		int answer = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(), "This test could take a very long time (hours), are you prepared?", "This could take a while...", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (answer == 1)
			return;

		// make sure we create views
		CytoscapeInit.getProperties().setProperty("viewThreshold","200000");

		StringBuffer buf = new StringBuffer();

		for ( TestScenario test : testScenarios ) {
			resetSession(test.getDescription());
			long begin = System.currentTimeMillis();
			LoadNetworkTask.loadURL(getClass().getResource(test.getFileName()), true);
			long duration = System.currentTimeMillis() - begin;

			buf.append( test.getDescription() );
			buf.append( Long.toString( duration )); 
			buf.append(" milliseconds.\n");

			System.out.println("100K TEST: " + test.getDescription() + duration + " milliseconds.");
		}


		System.out.println(buf.toString());
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(), buf.toString(), 
		                              "Performance Statistics", JOptionPane.INFORMATION_MESSAGE);

	}

	private void resetSession(String title) {
		Cytoscape.setSessionState(Cytoscape.SESSION_OPENED);
		Cytoscape.createNewSession();
		Cytoscape.getDesktop().setTitle(title);
		Cytoscape.getDesktop().getNetworkPanel().repaint();
		Cytoscape.getDesktop().repaint();
		Cytoscape.setSessionState(Cytoscape.SESSION_NEW);
		Cytoscape.getPropertyChangeSupport().firePropertyChange(Cytoscape.CYTOSCAPE_INITIALIZED, null, null);

	}
}
