package org.idekerlab.PanGIAPlugin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.idekerlab.PanGIAPlugin.utilities.files.FileUtil;

import giny.view.NodeView;
import cytoscape.Cytoscape;
import cytoscape.CyNode;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import ding.view.NodeContextMenuListener;

import java.util.*;

import java.io.*;

public class PanGIANodeContextMenuListener implements NodeContextMenuListener
{
	 private final CyNetworkView view;
	 private CyAttributes edgeAttr = Cytoscape.getEdgeAttributes();


     public PanGIANodeContextMenuListener(CyNetworkView view)
     {
             this.view = view;
     }

     public void addNodeContextMenuItems(NodeView nv, JPopupMenu menu)
     {
         if (menu == null)
                 return;

         final JMenu pangiaMenu = new JMenu("PanGIA");

         boolean selectedHasNested = false;
         
         for (Object n : nv.getGraphView().getSelectedNodes())
        	 if (((ding.view.DNodeView)n).getNode().getNestedNetwork()!=null)
        	 {
        		 selectedHasNested = true;
        		 break;
        	 }
         
         //ITEM1
         if (selectedHasNested && PanGIAPlugin.output.isAvailable())
         {
	         JMenuItem item = new JMenuItem();
	         item.addActionListener(new ActionListener()
	         {
	             public void actionPerformed(ActionEvent e) {
	            	 DetailedNetworkCreator.createDetailedView(view);
	             }
	         });
		     item.setText("Create detailed view");
		
		     pangiaMenu.add(item);
         }
         
         //ITEM2
         JMenu item1 = new JMenu();
         item1.setText("Save selected nodes to matrix file");
         
         String[] eaNames = edgeAttr.getAttributeNames();
         
         for (final String ea : eaNames)
         {
        	 JMenuItem eaItem = new JMenuItem();
        	 eaItem.setText(ea);
        	 eaItem.addActionListener(new ActionListener()
	         {
	             public void actionPerformed(ActionEvent e) {
	            	 JFileChooser jfc = new JFileChooser();
	            	 jfc.setCurrentDirectory(new File("."));
	            	 int returnVal = jfc.showSaveDialog(view.getComponent());
	            	 
	            	 if (returnVal==JFileChooser.APPROVE_OPTION)
	            		 saveNodesToMatrix(jfc.getSelectedFile(),ea);
	             }
	         });
        	 item1.add(eaItem);
         }
         pangiaMenu.add(item1);

	     
         //MENU
         menu.add(pangiaMenu);
     }
     
     private void saveNodesToMatrix(File file, String attr)
     {
    	 int[] selectedNodes = view.getSelectedNodeIndices();
    	 
    	 Set<Integer> choiceNodes = new HashSet<Integer>(1000);
    	 for (int i : selectedNodes)
    	 {
    		 if (view.getRootGraph().getNode(i).getNestedNetwork()==null) choiceNodes.add(i);
    		 else
    		 {
    			 for (int j : view.getRootGraph().getNode(i).getNestedNetwork().getNodeIndicesArray())
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
    		 ids[i] = view.getRootGraph().getNode(selectedNodes[i]).getIdentifier();
    	 
    	 double[][] m = new double[selectedNodes.length][];
    	 
    	 for (int i=0;i<selectedNodes.length;i++)
    	 {
    		 int jcount = i+1;
    		 m[i] = new double[jcount];
    		 
    		 for (int j=0;j<jcount;j++)
    		 {
    			 m[i][j] = Double.NaN;
    			 
    			 for (int ei : view.getRootGraph().getConnectingEdgeIndicesArray(new int[]{selectedNodes[i],selectedNodes[j]}))
    			 {
    				 Double d = edgeAttr.getDoubleAttribute(view.getRootGraph().getEdge(ei).getIdentifier(), attr);
    				 
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
    		 bw.write("Gene\t");
    		 
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
    	 }catch (Exception e){e.printStackTrace();}
     }

     
     
}
