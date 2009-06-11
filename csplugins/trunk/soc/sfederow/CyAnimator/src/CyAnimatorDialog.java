package src;

import giny.model.Node;
import giny.view.*;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.view.*;


import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Paint;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.util.*;

public class CyAnimatorDialog extends JDialog implements ActionListener, java.beans.PropertyChangeListener {

	private JPanel mainPanel;
	private JButton captureFrame;
	private JButton returnFrame;
	
	//private NodeView[] currentFrame;
	private HashMap<String, double[]> posFrame;
	private HashMap<String, Paint> colFrame;
	private CyFrame currentFrame;
	
	public CyAnimatorDialog()
	{
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(this);
   	    //add as listener to CytoscapeDesktop
	    Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(this);
		
		initialize();
	}
	
	public void initialize()
	{
	
		mainPanel = new JPanel();
		
		captureFrame = new JButton("Capture Frame");
		captureFrame.addActionListener(this);
		captureFrame.setActionCommand("capture");
		
		returnFrame = new JButton("Return To Frame");
		returnFrame.addActionListener(this);
		returnFrame.setActionCommand("return");
		
		mainPanel.add(captureFrame);
		mainPanel.add(returnFrame);
		this.setSize(new Dimension(120,120));
		setContentPane(mainPanel);
		System.out.println("hey");
		
	}
	
	
	public void actionPerformed(ActionEvent e)
	{	
		String command = e.getActionCommand();
		
		if(command.equals("capture"))
		{
			captureSettings();
			
		}
		
		if(command.equals("return"))
		{
			currentFrame.display();
		}
		setVisible(true);
		
	}
	
	public void captureSettings()
	{
		
		
		CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();
		currentFrame = new CyFrame(currentNetwork);
		
		
		//List<Node> nodeList = currentNetwork.nodesList();
		
		CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
		
		currentFrame.populate(networkView);
		
		/*
		currentFrame = new NodeView[nodeList.size()];
		posFrame = new HashMap<String, double[]>();
		colFrame = new HashMap<String, Paint>();
		for(int i=0;i<nodeList.size();i++)
		{
		   
		   NodeView nodeView = networkView.getNodeView(nodeList.get(i));
		   currentFrame[i] = nodeView;
		   double[] xy = new double[2];
		   xy[0] = nodeView.getXPosition();
		   xy[1] = nodeView.getYPosition();
		   posFrame.put(nodeList.get(i).getIdentifier(), xy);
		   colFrame.put(nodeList.get(i).getIdentifier(), nodeView.getUnselectedPaint());
		   System.out.println(nodeView.getUnselectedPaint()+"    X: "+nodeView.getXPosition()+"    Y: "+nodeView.getYPosition());
		   
		}
		*/
	}
	
	public void displayCurrentFrame()
	{
		//currentFrame.display();
		
		/*
		System.out.println("WOOOO");
		CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();
		CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
		List<Node> nodeList = currentNetwork.nodesList();
		for(int i=0;i<nodeList.size();i++)
        {
			
			NodeView nodeView = networkView.getNodeView(nodeList.get(i));
			double[] xy = posFrame.get(nodeList.get(i).getIdentifier());
			Paint p = colFrame.get(nodeList.get(i).getIdentifier());
			
			nodeView.setXPosition(xy[0]);
			nodeView.setYPosition(xy[1]);
			
			nodeView.setUnselectedPaint(p);
			//nodeView.setXPosition(currentFrame[i].getXPosition());
			//nodeView.setYPosition(currentFrame[i].getYPosition());
			
			
			//nodeView.setXPosition(cframe.get(nodeList.get(i).getIdentifier()));
			//nodeView.setYPosition(cframe.get(nodeList.get(i).getIdentifier()));
    	   
        }
		networkView.updateView();
		*/
	}
	
	public void propertyChange ( PropertyChangeEvent e ) {
		if(e.getPropertyName().equals("ATTRIBUTES_CHANGED")){
			initialize();
			setVisible(true);
		}
	}
	
	
}
