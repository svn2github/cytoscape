package org.idekerlab.PanGIAPlugin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.idekerlab.PanGIAPlugin.utilities.collections.HashMapUtil;
import org.idekerlab.PanGIAPlugin.utilities.files.FileUtil;

import giny.model.Edge;
import giny.model.Node;
import giny.view.EdgeView;
import giny.view.GraphView;
import giny.view.NodeView;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CyNode;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import ding.view.NodeContextMenuListener;

import java.util.*;

import org.idekerlab.PanGIAPlugin.data.StringMatrix;

import java.io.*;

public class PanGIANodeContextMenuListener implements NodeContextMenuListener
{
	 private final CyNetworkView view;
	 
	 public PanGIANodeContextMenuListener(CyNetworkView view)
     {
             this.view = view;
     }

     public void addNodeContextMenuItems(NodeView nv, JPopupMenu menu)
     {
    	 addContextMenuItems(view, nv.getGraphView(), menu);
     }
     
     public static void addContextMenuItems(final CyNetworkView aview, GraphView gv, JPopupMenu menu)
     {
         if (menu == null)
             return;

	     final JMenu pangiaMenu = new JMenu("PanGIA");
	
	     boolean selectedHasNested = false;
	     
	     for (Object n : gv.getSelectedNodes())
	    	 if (((ding.view.DNodeView)n).getNode().getNestedNetwork()!=null)
	    	 {
	    		 selectedHasNested = true;
	    		 break;
	    	 }
	     
	     boolean isOverviewNetwork = PanGIAPlugin.output.containsKey(aview.getNetwork().getIdentifier());
	    	 
	     //ITEM1
	     if (selectedHasNested && isOverviewNetwork)
	     {
	         JMenuItem item = new JMenuItem();
	         item.addActionListener(new ActionListener()
	         {
	             public void actionPerformed(ActionEvent e) {
	            	 DetailedNetworkCreator.createDetailedView(aview);
	             }
	         });
		     item.setText("Create Detailed View");
		
		     pangiaMenu.add(item);
	     }
	     
	     //ITEM2
	     if (isOverviewNetwork)
	     {
	         JMenuItem item2 = new JMenuItem();
	         item2.setText("Export Modules to Tab-Delimited File");
	         item2.addActionListener(new ActionListener()
	         {
	        	 public void actionPerformed(ActionEvent e) {
	            	 saveModules(aview, PanGIAPlugin.output.get(aview.getNetwork().getIdentifier()).getNodeAttrName());
	             }
	         });
	         pangiaMenu.add(item2);
	     }
	     
	     //ITEM3
	     if (isOverviewNetwork)
	     {
	         JMenuItem item3 = new JMenuItem();
	         item3.setText("Export Module Map to Tab-Delimited File");
	         item3.addActionListener(new ActionListener()
	         {
	        	 public void actionPerformed(ActionEvent e) {
	            	 saveOverviewNetwork(aview);
	             }
	         });
	         pangiaMenu.add(item3);
	     }
	     
	     //ITEM4
	     if (isOverviewNetwork)
	     {
	         JMenu item1 = new JMenu();
	         item1.setText("Save Selected Nodes to Matrix File");
	         
	         final PanGIAOutput output = PanGIAPlugin.output.get(aview.getNetwork().getIdentifier());
	         
	         //String[] ean = edgeAttr.getAttributeNames();
	         
	         String[] ean = new String[]{output.getPhysEdgeAttrName(),output.getGenEdgeAttrName()};
	         
	         List<String> eaNames = new ArrayList<String>(ean.length);
	         for (String s : ean) eaNames.add(s);
	         
	         eaNames.removeAll(NestedNetworkCreator.getEdgeAttributeNames());
	         eaNames.remove(NestedNetworkCreator.REFERENCE_NETWORK_NAME_ATTRIB);
	         
	         for (final String ea : eaNames)
	         {
	        	 JMenuItem eaItem = new JMenuItem();
	        	 eaItem.setText(ea);
	        	 
	        	 eaItem.addActionListener(new ActionListener()
		         {
		             public void actionPerformed(ActionEvent e) {
		            	 JFileChooser jfc = new JFileChooser();
		            	 jfc.setCurrentDirectory(new File("."));
		            	 int returnVal = jfc.showSaveDialog(aview.getComponent());
		            	 
		            	 if (returnVal==JFileChooser.APPROVE_OPTION)
		            		 saveNodesToMatrix(aview, jfc.getSelectedFile(),output.getNodeAttrName(),ea);
		             }
		         });
	        	 item1.add(eaItem);
	         }
	         pangiaMenu.add(item1);
	     }
	     
	     /*
	     //Copy network with new node IDs
	     JMenuItem item1 = new JMenuItem();
         item1.setText("Copy Network");
         
         
         for (final String aname : Cytoscape.getNodeAttributes().getAttributeNames())
         {
         	if (!edgeAttr.getType(aname).equals("String")) continue;
         
        	 JMenuItem eaItem = new JMenuItem();
        	 eaItem.setText(aname);
        	 eaItem.addActionListener(new ActionListener()
	         {
	             public void actionPerformed(ActionEvent e) {
	            	 copyNetworkWithNewIDs(aview.getNetwork(), aname);
	             }
	         });
        	 item1.add(eaItem);
         }
         
         pangiaMenu.add(item1);
	     */
	     
	     //MENU
	     if (pangiaMenu.getItemCount()>0) menu.add(pangiaMenu);
     }
     
     private static void copyNetworkWithNewIDs(CyNetwork net, String aname)
     {
    	 //Get the new name
    	 String newTitle = net.getTitle()+"_"+aname;
    	 
    	 boolean hasDup = true;
    	 int index = 2;
    	 while(hasDup)
    	 {
    		 hasDup = false;
    		 for (CyNetwork cnet : Cytoscape.getNetworkSet())
    			 if (cnet.getTitle().equals(newTitle))
    			 {
    				 newTitle =  net.getTitle()+"_"+aname+" ("+index+")";
    				 index++;
    				 hasDup = true;
    				 break;
    			 }
    	 }
    	 
    	 
    	 //Create nodes
    	 List<CyNode> nodes = new ArrayList<CyNode>(net.getNodeCount());
    	 CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
    	 
    	 
    	 for (int ni : net.getNodeIndicesArray())
    	 {
    		 String newID = String.valueOf(nodeAttr.getAttribute(net.getNode(ni).getIdentifier(),aname));
    		 if (newID.equals("")) continue;
    		 nodes.add(Cytoscape.getCyNode(newID,true));
    	 }
    	 
    	 CyAttributes edgeAttr = Cytoscape.getEdgeAttributes();
    	 
    	 List<CyEdge> edges = new ArrayList<CyEdge>(net.getEdgeCount());
    	 for (int ei : net.getEdgeIndicesArray())
    	 {
    		 String sourceID = String.valueOf(nodeAttr.getAttribute(net.getNode(net.getEdgeSourceIndex(ei)).getIdentifier(),aname));
    		 String targetID = String.valueOf(nodeAttr.getAttribute(net.getNode(net.getEdgeTargetIndex(ei)).getIdentifier(),aname));
    		 
    		 if (sourceID.equals("") || targetID.equals("")) continue;
    		 
    		 edges.add(Cytoscape.getCyEdge(sourceID, sourceID+" - "+targetID, targetID, String.valueOf(edgeAttr.getAttribute(net.getEdge(ei).getIdentifier(),"interaction"))));
    	 }
    	 
    	 
    	 CyNetwork newNet = Cytoscape.createNetwork(nodes, edges, newTitle);
    	 Cytoscape.createNetworkView(newNet);
    	 
    	 //Need to copy edge attributes as well!
     }
     
     private static void saveNodesToMatrix(final CyNetworkView aview, File file, String nAttr, String eattr)
     {
    	 CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
    	 CyAttributes edgeAttr = Cytoscape.getEdgeAttributes();
    	 
    	 int[] selectedNodes = aview.getSelectedNodeIndices();
    	 
    	 Set<Integer> choiceNodes = new HashSet<Integer>(1000);
    	 for (int i : selectedNodes)
    	 {
    		 if (aview.getRootGraph().getNode(i).getNestedNetwork()==null) choiceNodes.add(i);
    		 else
    		 {
    			 for (int j : aview.getRootGraph().getNode(i).getNestedNetwork().getNodeIndicesArray())
    				 choiceNodes.add(j);
    		 }
    	 }
    	 
    	 selectedNodes = new int[choiceNodes.size()];
    	 int ind=0;
    	 for (int i : choiceNodes)
    	 {
    		 selectedNodes[ind] = i;
    		 ind++;
    	 }
    	 
    	 String[] ids = new String[selectedNodes.length];
    	 
    	 for (int i=0;i<selectedNodes.length;i++)
    		 ids[i] = String.valueOf(nodeAttr.getAttribute(aview.getRootGraph().getNode(selectedNodes[i]).getIdentifier(),nAttr));
    	 
    	 double[][] m = new double[selectedNodes.length][];
    	 
    	 for (int i=0;i<selectedNodes.length;i++)
    	 {
    		 int jcount = i+1;
    		 m[i] = new double[jcount];
    		 
    		 for (int j=0;j<jcount;j++)
    		 {
    			 m[i][j] = Double.NaN;
    			 
    			 for (int ei : aview.getRootGraph().getConnectingEdgeIndicesArray(new int[]{selectedNodes[i],selectedNodes[j]}))
    			 {
    				 Double d = edgeAttr.getDoubleAttribute(aview.getRootGraph().getEdge(ei).getIdentifier(), eattr);
    				 
    				 if (d!=null)
    				 {
    					 m[i][j] = d;
    					 break;
    				 }
    			 }
    		 }
    	 }
    	 
    	 BufferedWriter bw = FileUtil.getBufferedWriter(file.getAbsolutePath(), false);
    	 
    	 try
    	 {
    		 bw.write("Gene");
    		 
    		 for (String id : ids)
    			 bw.write("\t"+id);
    		 
    		 bw.write("\n");
    		 
    		 for (int i=0;i<m.length;i++)
    		 {
    			 bw.write(ids[i]);
    			 for (int j=0;j<=i;j++)
    				 bw.write("\t"+m[i][j]);
    			 
    			 for (int i2=i+1;i2<m.length;i2++)
    				 bw.write("\t"+m[i2][i]);
    			 
    			 bw.write("\n");
    		 }
    		 
    		 bw.close();
    		 
    		 JOptionPane.showMessageDialog(null, "Matrix saved successfully.");
    		 
    	 }catch (Exception e)
    	 {
    		 e.printStackTrace();
    		 JOptionPane.showMessageDialog(null, "There was a problem saving the matrix: "+e.getMessage());
    	 }
     }
     
     public static void saveModules(CyNetworkView view, String nodeAttrName)
     {
    	 CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
    	 
    	 JFileChooser jfc = new JFileChooser();
    	 jfc.setCurrentDirectory(new File("."));
    	 int returnVal = jfc.showSaveDialog(view.getComponent());
    	 
    	 if (returnVal==JFileChooser.APPROVE_OPTION)
    	 {
    		 String fout = jfc.getSelectedFile().getAbsolutePath();
    		 
    		 Map<String,Set<String>> mod_nodes = new HashMap<String,Set<String>>(1000);
    		 
    		 for (int ni : view.getNetwork().getNodeIndicesArray())
    		 {
    			 Node n = Cytoscape.getRootGraph().getNode(ni);
    			 
    			 Set<String> nodes = new HashSet<String>(1000);
    			 
    			 for (int ni2 : n.getNestedNetwork().getNodeIndicesArray())
    				 nodes.add(String.valueOf(nodeAttr.getAttribute(Cytoscape.getRootGraph().getNode(ni2).getIdentifier(),nodeAttrName)));
    			 
    			 mod_nodes.put(n.getIdentifier(), nodes);
    		 }
    		 
    		 try
    		 {
    				BufferedWriter bw = new BufferedWriter(new FileWriter(fout));
    				
    				for (String key : mod_nodes.keySet()) {
    					bw.write(key + "\t");

    					Set<String> vals = mod_nodes.get(key);

    					boolean first = true;
    					for (String val : vals)
    						if (!first)
    							bw.write("|" + val);
    						else {
    							first = false;
    							bw.write(val);
    						}

    					bw.write("\n");
    				}

    				bw.close();
    				
    				JOptionPane.showMessageDialog(null, "Modules saved successfully.");
			} catch (Exception e)
			{
				 e.printStackTrace();
	    		 JOptionPane.showMessageDialog(null, "There was a problem saving the modules: "+e.getMessage());
			}
    		 
    	 }
    		 
     }
     
     public static void saveOverviewNetwork(CyNetworkView view)
     {
    	 JFileChooser jfc = new JFileChooser();
    	 jfc.setCurrentDirectory(new File("."));
    	 int returnVal = jfc.showSaveDialog(view.getComponent());
    	 
    	 if (returnVal==JFileChooser.APPROVE_OPTION)
    	 {
    		 String fout = jfc.getSelectedFile().getAbsolutePath();
    		 
    		 List<EdgeView> edges = (List<EdgeView>) view.getEdgeViewsList();
    		 
    		 StringMatrix out = new StringMatrix(edges.size(),9);
    		 
    		 List<String> edgeAttributes = NestedNetworkCreator.getEdgeAttributeNames();
    		 
    		 List<String> colNames = new ArrayList<String>(9);
    		 colNames.add("NodeA");
    		 colNames.add("NodeB");
    		 colNames.addAll(edgeAttributes);
    		 out.setColNames(colNames);
    		 
    		 CyAttributes edgeAttr = Cytoscape.getEdgeAttributes();
    		 
    		 int row = 0;
    		 for (EdgeView ev : edges)
    		 {
    			 Edge e = ev.getEdge();
    			 
    			 out.set(row, 0, e.getSource().getIdentifier());
    			 out.set(row, 1, e.getTarget().getIdentifier());
    			 
    			 for (int j=0;j<edgeAttributes.size();j++)
    				 out.set(row, j+2, edgeAttr.getAttribute(e.getIdentifier(), edgeAttributes.get(j)).toString());
    			 
    			 row++;
    		 }
    		 
    		 try
    		 {
    		 	//Open/Create file for writing. If no file exists append->false
    			BufferedWriter bw = new BufferedWriter(new FileWriter(fout));
    			
    			if (out.hasColNames())
    			{
					if (out.hasRowNames()) bw.write("\t");
					
					bw.write(out.getColName(0));
					for (int i=1;i<out.numCols();i++)
						bw.write("\t" + out.getColName(i));
					
					bw.write("\n");
    			}
    			
    			for (int i=0;i<out.numRows();i++)
    			{
    				if (out.hasRowNames()) bw.write(out.getRowName(i)+"\t");
					
					bw.write(out.get(i,0));
					for (int j=1;j<out.numCols();j++)
						bw.write("\t" + out.get(i,j));
								
					bw.write("\n");
    			}
    			
    			bw.close();
    			JOptionPane.showMessageDialog(null, "Overview network saved successfully.");
    			
    		 }catch (Exception e)
    		 {
    			e.printStackTrace();
    			JOptionPane.showMessageDialog(null, "There was a problem saving the overview network: "+e.getMessage());
    		 }
 	    	

    		 
    	 }
     }
}
