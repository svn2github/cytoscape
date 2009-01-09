package cytoscape.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;

import cytoscape.view.CyWindow;
import cytoscape.CytoscapeVersion;
import cytoscape.util.CreditScreen;

public class HelpAboutAction extends AbstractAction   {

    public HelpAboutAction() {
        super("About");
    }
    public void actionPerformed(ActionEvent e) {

          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
		String version = "Cytoscape Version: "
				+ CytoscapeVersion.version;
                StringBuffer lines = new StringBuffer();
		  lines.append(version);
		  lines.append("\n");
                  lines.append("\n Cytoscape is a collaboration \n" );
                  lines.append("between the Institute for Systems\n" );
                  lines.append("Biology, University of California,\n" );
                  lines.append("San Diaego, Memorial Sloan Kettering\n" );
                  lines.append("Cancer Center and the\n" );
                  lines.append("Institute Pasteur\n" );
                  lines.append(" \n" );
                  lines.append("Cytosape 2.0 Primary Developers\n" );
                  lines.append("Iliana Avila-Campillo,  Ethan Cerami,\n" );
                  lines.append("Rowan Christmas, Ryan Kelley, Andrew\n" );
                  lines.append("Markiel, and Chris Workman\n" );
                  lines.append(" \n" );
                  lines.append(" \n" );
                  lines.append("ISB: Hamid Bolouri (PI) \n" );
                  lines.append("Paul Shannon, David Reiss, James\n" );
                  lines.append("Taylor, Larissa KamenkoVich and \n" );
                  lines.append("Paul Edlefsen ( GINY Library )\n" );
                  lines.append(" \n" );
                  lines.append("UCSD: Trey Ideker (PI) \n" );
                  lines.append("Jonathan Wang,  Nada Amin, and \n" );
                  lines.append("Owen Ozier\n" );
                  lines.append(" \n" );
                  lines.append("MSKCC: Chris Sander (PI) \n" );
                  lines.append("Gary Bader,  Robert Sheridan\n" );
                  lines.append(" \n" );
                  lines.append("IP: Benno Shwikowski (PI) \n" );
                  lines.append(" \n" );
                  lines.append("Addional Collaborators\n" );

               CreditScreen.showCredits( getClass().getResource(
			"/cytoscape/images/CytoscapeCredits.png"),
			lines.toString() );
              }
          }
      );
    } 
}

