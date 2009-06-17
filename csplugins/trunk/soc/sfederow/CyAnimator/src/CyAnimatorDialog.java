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
import javax.swing.Timer;

import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.sql.Time;

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
	private JButton captureFrameOne;
	private JButton captureFrameTwo;
	private JButton animateBetween;
	private JButton returnFrame;
	
	//private NodeView[] currentFrame;
	private HashMap<String, double[]> posFrame;
	private HashMap<String, Paint> colFrame;
	private CyFrame frameOne;
	private CyFrame frameTwo;
	
	//private CyFrame[]
	
	                
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
		
		captureFrameOne = new JButton("Capture Frame One");
		captureFrameOne.addActionListener(this);
		captureFrameOne.setActionCommand("captureOne");
		
		captureFrameTwo = new JButton("Capture Frame Two");
		captureFrameTwo.addActionListener(this);
		captureFrameTwo.setActionCommand("captureTwo");
		
		animateBetween = new JButton("Animate From One to Two");
		animateBetween.addActionListener(this);
		animateBetween.setActionCommand("animateOneTwo");
		
		returnFrame = new JButton("Return To Frame One");
		returnFrame.addActionListener(this);
		returnFrame.setActionCommand("returnOne");
		
		mainPanel.add(captureFrameOne);
		mainPanel.add(captureFrameTwo);
		mainPanel.add(animateBetween);
		mainPanel.add(returnFrame);
		this.setSize(new Dimension(200,180));
		this.setLocation(800, 100);
		setContentPane(mainPanel);
		System.out.println("hey");
		
	}
	
	
	public void actionPerformed(ActionEvent e)
	{	
		String command = e.getActionCommand();
		
		if(command.equals("captureOne"))
		{
			frameOne = captureSettings();
			
		}
		
		if(command.equals("captureTwo"))
		{
			frameTwo = captureSettings();
			
		}
		
		if(command.equals("animateOneTwo")){
			
			//LinearInterpolator lint = new LinearInterpolator();
			//CyFrame[] frames = lint.generateInterpolatedFrames(frameOne, frameTwo, 10);
		    
			//Time animationTime = new Time(5000);
			
			 int delay = 1000; //milliseconds
			  ActionListener taskPerformer = new ActionListener() {
				  LinearInterpolator lint = new LinearInterpolator();
					CyFrame[] frames = lint.generateInterpolatedFrames(frameOne, frameTwo, 10);
				    int i = 0;
				  
				  public void actionPerformed(ActionEvent evt) {
			          if(i == frames.length){ return;}
					  frames[i].display();
			          System.out.println(i);
					  i++;
			      }
			  };
			  
			  new Timer(delay, taskPerformer).start();
			
			  
			  
	       //for(int i=0; i<frames.length; i++){
	        	
	        	
	        //}
		}
		
		if(command.equals("returnOne"))
		{
			frameOne.display();
		}
		setVisible(true);
		
	}
	
	public CyFrame captureSettings()
	{
		
		
		CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();
		CyFrame frame = new CyFrame(currentNetwork);
		
		
		//List<Node> nodeList = currentNetwork.nodesList();
		
		CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
		
		frame.populate(networkView);
		
		return frame;
		
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
