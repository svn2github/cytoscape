package ucsd.rmkelley.BetweenPathway;
import java.io.*;
import java.util.*;
import edu.umd.cs.piccolo.activities.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import giny.view.NodeView;
import giny.model.*;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import phoebe.PNodeView;
import phoebe.PGraphView;
import cytoscape.data.Semantics;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*; 
import java.awt.BorderLayout;
import java.awt.event.*;
import cytoscape.layout.*;
import java.awt.Dimension;

class NetworkSelectionPanel extends JPanel{
  File scoreFile;
  JTextField fileText;
  JList list;
  public NetworkSelectionPanel(){
    setLayout(new BorderLayout());
    Vector model = new Vector();
    for(Iterator networkIt = Cytoscape.getNetworkSet().iterator();networkIt.hasNext();){
      model.add(new NetworkContainer((CyNetwork)networkIt.next()));
    }
    list = new JList(model);
    list.setSelectedIndex(0);
    add(list,BorderLayout.CENTER);
    JPanel southPanel = new JPanel();
    southPanel.add(new JLabel("Score file"));
    fileText = new JTextField("none");
    southPanel.add(fileText);
    JButton change = new JButton("Select score file");
    change.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent ae){
	  JFileChooser chooser = new JFileChooser();
	  int returnVal = chooser.showOpenDialog(Cytoscape.getDesktop());
	  if(returnVal == JFileChooser.APPROVE_OPTION) {
	    scoreFile = chooser.getSelectedFile();
	    fileText.setText(scoreFile.getName());
	  }
	}});
    southPanel.add(change);
    add(southPanel,BorderLayout.NORTH);
  }

  public CyNetwork getSelectedNetwork(){
    return ((NetworkContainer)list.getSelectedValue()).getNetwork();
  }

  public File getScoreFile(){
    return scoreFile;
  }


}
