package ucsd.rmkelley.GraphMerge;
import java.util.*;
import edu.umd.cs.piccolo.activities.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import giny.view.NodeView;
import giny.view.EdgeView;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import phoebe.PNodeView;
import phoebe.PGraphView;
import giny.model.Node;
import giny.model.Edge;
import giny.view.Label;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.BoxLayout;
import javax.swing.ListSelectionModel;
import javax.swing.DefaultListModel;

/**
 * This is a sample Cytoscape plugin using Giny graph structures. For each
 * currently selected node in the graph view, the action method of this plugin
 * additionally selects the neighbors of that node if their canonical name ends
 * with the same letter. (For yeast genes, whose names are of the form 'YOR167C',
 * this selects genes that are on the same DNA strand). This operation was
 * chosen to be illustrative, not necessarily useful.
 *
 * Note that selection is a property of the view of the graph, while neighbors
 * are a property of the graph itself. Thus this plugin must access both the
 * graph and its view.
 */
public class GraphMerge extends CytoscapePlugin{
    /**
     * This constructor saves the cyWindow argument (the window to which this
     * plugin is attached) and adds an item to the operations menu.
     */
    public GraphMerge(){
	Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add( new TestAction() );
    }
    
   

    public class TestAction extends AbstractAction{
    
	public TestAction() {super("Merge all networks");}
    
	/**
	 * This method is called when the user selects the menu item.
	 */
	public void actionPerformed(ActionEvent ae) {
	  MergeDialog dialog = new MergeDialog();
	  dialog.show();
	  if(!dialog.isCancelled()){
	    Thread t = new GunThread(dialog.getNetworkList());
	    t.run();
	  }
	}

    }
}


class MergeDialog extends JDialog{
  JButton upButton,downButton,removeButton;
  JList networkList;
  DefaultListModel networkData;
  boolean cancelled = true;
  public MergeDialog(){
    setModal(true);
    
    getContentPane().setLayout(new BorderLayout());
    JPanel centerPanel = new JPanel();
    networkData = new DefaultListModel();
    for(Iterator networkIt = Cytoscape.getNetworkSet().iterator();networkIt.hasNext();){
      networkData.addElement(new NetworkContainer((CyNetwork)networkIt.next()));
    }
    networkList = new JList(networkData);
    networkList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    networkList.addListSelectionListener(new ListSelectionListener(){
	public void valueChanged(ListSelectionEvent e){
	  int index = networkList.getMinSelectionIndex();
	  if(index == -1){
	    upButton.setEnabled(false);
	    downButton.setEnabled(false);
	    removeButton.setEnabled(false);
	  }
	  else if(networkData.size() < 2){
	    upButton.setEnabled(false);
	    downButton.setEnabled(false);
	    removeButton.setEnabled(true);
	  }
	  else if(index == 0){
	    upButton.setEnabled(false);
	    downButton.setEnabled(true);
	    removeButton.setEnabled(true);
	  }
	  else if(index == networkData.size()-1){
	    upButton.setEnabled(true);
	    downButton.setEnabled(false);
	    removeButton.setEnabled(true);
	  }
	  else{
	    upButton.setEnabled(true);
	    downButton.setEnabled(true);
	    removeButton.setEnabled(true);
	  }
	}});
    centerPanel.add(networkList);
    getContentPane().add(centerPanel,BorderLayout.CENTER);
    
    JPanel eastPanel = new JPanel();
    eastPanel.setLayout(new BoxLayout(eastPanel,BoxLayout.Y_AXIS));
    upButton = new JButton("+");
    downButton = new JButton("-");
    removeButton = new JButton("x");

    upButton.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent ae){
	  int currentIndex = networkList.getMinSelectionIndex();
	  Object removed = networkData.remove(currentIndex);
	  networkData.add(currentIndex-1,removed);
	  networkList.repaint();
	}});
    downButton.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent ae){
	  int currentIndex = networkList.getMinSelectionIndex();
	  Object removed = networkData.remove(currentIndex);
	  networkData.add(currentIndex+1,removed);
	  networkList.repaint();
	}});
    removeButton.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent ae){
	  int currentIndex = networkList.getMinSelectionIndex();
	  networkData.remove(currentIndex);
	  networkList.repaint();
	  MergeDialog.this.pack();
	}});
    upButton.setEnabled(false);
    downButton.setEnabled(false);
    removeButton.setEnabled(false);

    eastPanel.add(upButton);
    eastPanel.add(downButton);
    eastPanel.add(removeButton);

    getContentPane().add(eastPanel,BorderLayout.EAST);

    JPanel southPanel = new JPanel();
    JButton ok,cancel;
    ok = new JButton("OK");
    cancel = new JButton("Cancel");
    ok.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent ae){
	  cancelled = false;
	  MergeDialog.this.hide();
	}});
    cancel.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent ae){
	  MergeDialog.this.hide();
	}});
    southPanel.add(cancel);
    southPanel.add(ok);
    getContentPane().add(southPanel,BorderLayout.SOUTH);
    this.pack();
  }

  public List getNetworkList(){
    Vector result = new Vector();
    for(int idx=0;idx<networkData.size();idx++){
      result.add(((NetworkContainer)networkData.elementAt(idx)).getNetwork());
    }
    return result;
  }

  public boolean isCancelled(){
    return cancelled;
  }

}

class GunThread extends Thread {

  /**
   * A list of all the networks we are trying to merge
   */
  Collection networkList;
  public GunThread(Collection networkList){
    this.networkList = networkList;
  }

  public void run(){
    
    /*
     * A list of node views that we will copy over to the new network
     */
    List nodeViews = new Vector();
    
    /*
     * A list of edge views that we will copy over to the new network
     */
    List edgeViews = new Vector();
    
    /*
     * Create an empty network 
     */
    CyNetwork newNetwork = Cytoscape.createNetwork("Merged Network");
    
    /*
     * For each network in the merge list, do the following
     * First, check to see if a view exists for this network.
     * If it does, then check which nodes we are planning to
     * add are already present in the network. For each node that
     * is not present, keep track of the associated NodeView.
     */
    for(Iterator networkIt = networkList.iterator();networkIt.hasNext();){
      CyNetwork nextNetwork = (CyNetwork)networkIt.next();
      /*
       * Only do all of this if we are acutally trying to import visual information
       * from this network
       */
      if(Cytoscape.viewExists(nextNetwork.getIdentifier())){
	CyNetworkView nextNetworkView = Cytoscape.getNetworkView(nextNetwork.getIdentifier());
	for(Iterator nodeIt = nextNetwork.nodesIterator();nodeIt.hasNext();){
	  Node newNode = (Node)nodeIt.next();
	  if(!newNetwork.containsNode(newNode)){
	    nodeViews.add(nextNetworkView.getNodeView(newNode));
	  }
	}
	for(Iterator edgeIt = nextNetwork.edgesIterator();edgeIt.hasNext();){
	  Edge newEdge = (Edge)edgeIt.next();
	  if(!newNetwork.containsEdge(newEdge)){
	    edgeViews.add(nextNetworkView.getEdgeView(newEdge));
	  }
	}
      }
      /*
       * Regardless of whether a view exists, we now want ot merge the old network into the one
       */
      newNetwork.restoreNodes(nextNetwork.getNodeIndicesArray());
      newNetwork.restoreEdges(nextNetwork.getEdgeIndicesArray());
    }
    

    CyNetworkView newNetworkView = Cytoscape.getNetworkView(newNetwork.getIdentifier());
    /*
     * Apply the nodeview information from the merged graph views
     */
    for(Iterator nodeViewIt = nodeViews.iterator();nodeViewIt.hasNext();){
      NodeView oldView = (NodeView)nodeViewIt.next();
      Node node = oldView.getNode();
      NodeView newView = newNetworkView.getNodeView(node);
      newView.setBorder(oldView.getBorder());
      newView.setBorderPaint(oldView.getBorderPaint());
      newView.setBorderWidth(oldView.getBorderWidth());
      newView.setHeight(oldView.getHeight());
      newView.setSelectedPaint(oldView.getSelectedPaint());
      newView.setShape(oldView.getShape());
      newView.setTransparency(oldView.getTransparency());
      newView.setUnselectedPaint(oldView.getUnselectedPaint());
      newView.setWidth(oldView.getWidth());
      newView.setXPosition(oldView.getXPosition());
      newView.setYPosition(oldView.getYPosition());
      Label oldLabel = oldView.getLabel();
      Label newLabel = newView.getLabel();
      newLabel.setFont(oldLabel.getFont());
      newLabel.setGreekThreshold(oldLabel.getGreekThreshold());
      newLabel.setText(oldLabel.getText());
      newLabel.setTextPaint(oldLabel.getTextPaint());
    }
    for(Iterator edgeViewIt = edgeViews.iterator();edgeViewIt.hasNext();){
      EdgeView oldView = (EdgeView)edgeViewIt.next();
      Edge edge = oldView.getEdge();
      EdgeView newView = newNetworkView.getEdgeView(edge);
      newView.setLineType(oldView.getLineType());
      newView.setSelectedPaint(oldView.getSelectedPaint());
      newView.setSourceEdgeEndPaint(oldView.getSourceEdgeEndPaint());
      newView.setSourceEdgeEndSelectedPaint(oldView.getSourceEdgeEndSelectedPaint());
      newView.setStroke(oldView.getStroke());
      newView.setStrokeWidth(oldView.getStrokeWidth());
      newView.setTargetEdgeEndPaint(oldView.getTargetEdgeEndPaint());
      newView.setTargetEdgeEndSelectedPaint(oldView.getTargetEdgeEndSelectedPaint());
    }
  }
}

class NetworkContainer{
  CyNetwork network;
  public NetworkContainer(CyNetwork network){
    this.network = network;
  }

  public String toString(){
    return network.getTitle();
  }

  public CyNetwork getNetwork(){
    return network;
  }
}

