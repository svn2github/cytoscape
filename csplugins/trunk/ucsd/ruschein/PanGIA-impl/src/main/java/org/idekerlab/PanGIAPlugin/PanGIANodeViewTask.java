package org.idekerlab.PanGIAPlugin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.View;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.view.model.CyNetworkView;

public class PanGIANodeViewTask extends AbstractTask {
	
	private View<CyNode> nodeView;
	private CyNetwork network;

	public PanGIANodeViewTask(CyNetworkView netView, View<CyNode> nodeView, String acton) {
		this.nodeView = nodeView;
		this.network = network;
	}

	@Override
	public void run(TaskMonitor tm) {
//		CyNode node = nodeView.getModel();
//		String identifier = node.getCyRow().get("name", String.class);
//
//
//		boolean selectedHasNested = false;
//
//		if (node.getNetwork()!=null)
//		{
//			selectedHasNested = true;
//			break;
//		}
//
//	     boolean isOverviewNetwork = PanGIAPlugin.output.containsKey(aview.getNetwork().getIdentifier());
//	    	 
//	     //ITEM1
//	     if (selectedHasNested && isOverviewNetwork)
//	     {
//	         JMenuItem item = new JMenuItem();
//	         item.addActionListener(new ActionListener()
//	         {
//	             public void actionPerformed(ActionEvent e) {
//	            	 DetailedNetworkCreator.createDetailedView(aview);
//	             }
//	         });
//		     item.setText("Create Detailed View");
//		
//		     pangiaMenu.add(item);
//	     }
//	     
//	     //ITEM2
//	     if (isOverviewNetwork)
//	     {
//	         JMenuItem item2 = new JMenuItem();
//	         item2.setText("Export Modules to Tab-Delimited File");
//	         item2.addActionListener(new ActionListener()
//	         {
//	        	 public void actionPerformed(ActionEvent e) {
//	            	 saveModules(aview, PanGIAPlugin.output.get(aview.getNetwork().getIdentifier()).getNodeAttrName());
//	             }
//	         });
//	         pangiaMenu.add(item2);
//	     }
//	     
//	     //ITEM3
//	     if (isOverviewNetwork)
//	     {
//	         JMenuItem item3 = new JMenuItem();
//	         item3.setText("Export Module Map to Tab-Delimited File");
//	         item3.addActionListener(new ActionListener()
//	         {
//	        	 public void actionPerformed(ActionEvent e) {
//	            	 saveOverviewNetwork(aview);
//	             }
//	         });
//	         pangiaMenu.add(item3);
//	     }
//	     
//	     //ITEM4
//	     if (isOverviewNetwork)
//	     {
//	         JMenu item1 = new JMenu();
//	         item1.setText("Save Selected Nodes to Matrix File");
//	         
//	         final PanGIAOutput output = PanGIAPlugin.output.get(aview.getNetwork().getIdentifier());
//	         
//	         //String[] ean = edgeAttr.getAttributeNames();
//	         
//	         String[] ean = new String[]{output.getPhysEdgeAttrName(),output.getGenEdgeAttrName()};
//	         
//	         List<String> eaNames = new ArrayList<String>(ean.length);
//	         for (String s : ean) eaNames.add(s);
//	         
//	         eaNames.removeAll(NestedNetworkCreator.getEdgeAttributeNames());
//	         eaNames.remove(NestedNetworkCreator.REFERENCE_NETWORK_NAME_ATTRIB);
//	         
//	         for (final String ea : eaNames)
//	         {
//	        	 JMenuItem eaItem = new JMenuItem();
//	        	 eaItem.setText(ea);
//	        	 
//	        	 eaItem.addActionListener(new ActionListener()
//		         {
//		             public void actionPerformed(ActionEvent e) {
//		            	 JFileChooser jfc = new JFileChooser();
//		            	 jfc.setCurrentDirectory(new File("."));
//		            	 int returnVal = jfc.showSaveDialog(aview.getComponent());
//		            	 
//		            	 if (returnVal==JFileChooser.APPROVE_OPTION)
//		            		 saveNodesToMatrix(aview, jfc.getSelectedFile(),output.getNodeAttrName(),ea);
//		             }
//		         });
//	        	 item1.add(eaItem);
//	         }
//	         pangiaMenu.add(item1);
//	     }
//	     
//	     /*
//	     //Copy network with new node IDs
//	     JMenuItem item1 = new JMenuItem();
//         item1.setText("Copy Network");
//         
//         
//         for (final String aname : Cytoscape.getNodeAttributes().getAttributeNames())
//         {
//         	if (!edgeAttr.getType(aname).equals("String")) continue;
//         
//        	 JMenuItem eaItem = new JMenuItem();
//        	 eaItem.setText(aname);
//        	 eaItem.addActionListener(new ActionListener()
//	         {
//	             public void actionPerformed(ActionEvent e) {
//	            	 copyNetworkWithNewIDs(aview.getNetwork(), aname);
//	             }
//	         });
//        	 item1.add(eaItem);
//         }
//         
//         pangiaMenu.add(item1);
//	     */
//	     
//	     //MENU
//	     if (pangiaMenu.getItemCount()>0) menu.add(pangiaMenu);
//     }

	}

	@Override
	public void cancel() {
	}
}
