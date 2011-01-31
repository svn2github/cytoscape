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
	     
	     boolean isOverviewNetwork = PanGIAPlugin.output.isAvailable() && aview.getNetwork().getIdentifier().equals(PanGIAPlugin.output.getOverviewNetwork().getIdentifier());
	     
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
	            	 saveModules(aview);
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
	     if (PanGIAPlugin.output.isAvailable())
	     {
	         JMenu item1 = new JMenu();
	         item1.setText("Save Selected Nodes to Matrix File");
	         
	         //String[] ean = edgeAttr.getAttributeNames();
	         
	         String[] ean = new String[]{PanGIAPlugin.output.getPhysAttrName(),PanGIAPlugin.output.getGenAttrName()};
	         
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
		            		 saveNodesToMatrix(aview, jfc.getSelectedFile(),ea);
		             }
		         });
	        	 item1.add(eaItem);
	         }
	         pangiaMenu.add(item1);
	     }
	     
	     //MENU
	     menu.add(pangiaMenu);
     }
     
     private static void saveNodesToMatrix(final CyNetworkView aview, File file, String attr)
     {
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
    		 ids[i] = aview.getRootGraph().getNode(selectedNodes[i]).getIdentifier();
    	 
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
    				 Double d = edgeAttr.getDoubleAttribute(aview.getRootGraph().getEdge(ei).getIdentifier(), attr);
    				 
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
     
     public static void saveModules(CyNetworkView view)
     {
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
    				 nodes.add(Cytoscape.getRootGraph().getNode(ni2).getIdentifier());
    			 
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
