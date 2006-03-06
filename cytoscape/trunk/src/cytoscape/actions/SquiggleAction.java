
/*
  File: SquiggleAction.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Pasteur Institute
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

package cytoscape.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;
import phoebe.PGraphView;
import cytoscape.view.CyNetworkView;
import cytoscape.Cytoscape;

public class SquiggleAction extends JMenu {
  
  private JMenuItem squiggleMode;
  private JMenuItem clearSquiggle;
  private boolean enabled;

  public SquiggleAction  () {
    super("Squiggle");

    squiggleMode = new JMenuItem( new AbstractAction( "Enable" ) {
	  public void actionPerformed ( ActionEvent e ) {
	    // Do this in the GUI Event Dispatch thread...
	    SwingUtilities.invokeLater( new Runnable() {
	      public void run() {
		    PGraphView view = (PGraphView)Cytoscape.getCurrentNetworkView();
		    if (enabled) {
              Cytoscape.enableSquiggle();
              squiggleMode.setText("Disable");
            } else {
              Cytoscape.disableSquiggle();
              squiggleMode.setText("Enable");
            }
            clearSquiggle.setEnabled(enabled);
            enabled = !enabled;
	  } } ); } } ) ;
    add(squiggleMode);
    squiggleMode.setAccelerator( javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_F12, 0 ) );

    clearSquiggle =  new JMenuItem( new AbstractAction( "Clear" ) {
      public void actionPerformed ( ActionEvent e ) {
        // Do this in the GUI Event Dispatch thread...
        SwingUtilities.invokeLater( new Runnable() {
          public void run() {
            PGraphView view = (PGraphView)Cytoscape.getCurrentNetworkView();
              view.getSquiggleHandler().clearSquiggles();
      } } ); } } );
    clearSquiggle.setEnabled(false);
    add(clearSquiggle);
  }
}
