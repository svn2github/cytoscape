


import java.util.*;
import java.awt.event.*;
import javax.swing.*;


import cytoscape.data.CyAttributes;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.*;

/*
* Class to set up the selection of the attribute that will be used as a distance metric in finding the shortest path
*/


public class SelectSetup {

	private CyAttributes edgeAttributes;
	private CyNetworkView view;
	private Vector attributeVect;
	private JList attributeList;
	private CytoscapeDesktop desktop;
	private CytoPanel cytoPanel;
	
	
	
	public void attributeUpdate()
	{
		desktop = Cytoscape.getDesktop();
		cytoPanel = desktop.getCytoPanel(SwingConstants.WEST);
		view = Cytoscape.getCurrentNetworkView();
		attributeVect = new Vector();
		attributeVect.addElement(new String("Hop Distance"));
		edgeAttributes = Cytoscape.getEdgeAttributes();
		String[] attributeNames = edgeAttributes.getAttributeNames();
		
	
	
		//Finds all attributes that are integers or doubles, and adds them to list
		for(int i = 0; i < attributeNames.length; i++)
		{
			String name = attributeNames[i];
			byte type = edgeAttributes.getType(name);

			if((type == edgeAttributes.TYPE_INTEGER) || (type == edgeAttributes.TYPE_FLOATING))
				attributeVect.addElement(name); 
		}
		
		attributeList = new JList(attributeVect);
		attributeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		attributeList.setLayoutOrientation(JList.VERTICAL);
		JScrollPane listPane = new JScrollPane(attributeList);
		
		//creates new tab in cytopanel. Adds JList of attributes to that tab.
		if(cytoPanel.indexOfComponent("Shortest Path Attributes") == -1)
		 cytoPanel.add("Shortest Path Attributes",listPane);
		
		cytoPanel.setSelectedIndex(cytoPanel.indexOfComponent("Shortest Path Attributes"));
		
		//Double click on attributes in list to run shortest path using selected attribute as distance metric
		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					String selectedAttribute = (String)attributeList.getModel().getElementAt(attributeList.locationToIndex(e.getPoint()));
					Object[] runSPoptions = {"Directed", "Undirected","Cancel"};
					int s = JOptionPane.showOptionDialog(view.getComponent(), "Find Shortest Path using " +  selectedAttribute + " as distance metric",
							"",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE,null,runSPoptions,runSPoptions[0]);
					
					if(s == 0)
					{
						ShortestPath sp = new ShortestPath();
						sp.calculate(true,selectedAttribute); 
					}
					
					else if (s == 1)
					{
						ShortestPath sp = new ShortestPath();
						sp.calculate(false,selectedAttribute); 
					}
				}
			}
			};
			attributeList.addMouseListener(mouseListener);
		}
		
		
		}
			
