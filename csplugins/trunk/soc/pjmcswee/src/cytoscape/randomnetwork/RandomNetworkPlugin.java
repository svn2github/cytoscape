/*
File: RandomNetworkPlugin


Author: Patrick J. McSweeney
Creation Date: 5/07/08

*/

package cytoscape.randomnetwork;


import javax.swing.JOptionPane;
import java.util.*;
import giny.model.*;
import giny.view.*;
import cytoscape.plugin.*;
import cytoscape.*;
import cytoscape.view.*;
import cytoscape.data.*;
import cytoscape.util.*;
import java.awt.event.ActionEvent;


public class RandomNetworkPlugin extends CytoscapePlugin 
{

    public RandomNetworkPlugin() 
	{
		GenerateRandomActionER action = new GenerateRandomActionER();
        action.setPreferredMenu("Plugins");
        Cytoscape.getDesktop().getCyMenus().addAction(action);
    
	
		GenerateRandomActionWA action1 = new GenerateRandomActionWA();
        action1.setPreferredMenu("Plugins");
        Cytoscape.getDesktop().getCyMenus().addAction(action1);
	
	
		GenerateRandomActionBA action2 = new GenerateRandomActionBA();
        action2.setPreferredMenu("Plugins");
        Cytoscape.getDesktop().getCyMenus().addAction(action2);
	
	}
	
	


	public class GenerateRandomActionER extends CytoscapeAction
	{
	
		public GenerateRandomActionER()
		{super("Erdos-Renyi random network");}


		public void actionPerformed(ActionEvent ae)
		{
			ErdosRenyiDialog erd = new ErdosRenyiDialog(Cytoscape.getDesktop());
			erd.pack();
			erd.setLocationRelativeTo(Cytoscape.getDesktop());
			erd.show();
			
		}
	}



	public class GenerateRandomActionBA extends CytoscapeAction
	{

	
		public GenerateRandomActionBA()
		{super("Bararbasi-Albert random network");}


		public void actionPerformed(ActionEvent ae)
		{
			BarabasiAlbertDialog bad = new BarabasiAlbertDialog(Cytoscape.getDesktop());
			bad.pack();
			bad.setLocationRelativeTo(Cytoscape.getDesktop());
			bad.show();
		}
	}



	public class GenerateRandomActionWA extends CytoscapeAction
	{

	
		public GenerateRandomActionWA()
		{super("Watts-Strogatz random network");}


		public void actionPerformed(ActionEvent ae)
		{
			WattsStrogatzModel wam = new WattsStrogatzModel(20,true,true,.5,5);
			wam.Generate();
		}
	}



}

