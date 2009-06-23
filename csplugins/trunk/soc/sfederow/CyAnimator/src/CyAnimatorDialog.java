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
				  
				  Interpolator lint = new Interpolator();
				  int i = 0;
				  
				  //try{
				  CyFrame[] frames = lint.makeColorFrames(frameOne, frameTwo, 10);
				  //}catch (Exception e){
					//  System.out.println(e.getMessage());
				  //}
			      
				  
				  public void actionPerformed(ActionEvent evt) {
			          if(i == frames.length){ return;}
					  frames[i].display();
			          System.out.println(i);
					  i++;
			      }
			  };
			  
			  new Timer(delay, taskPerformer).start();
			
			  
			  
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
		
	}
	

	
	public void propertyChange ( PropertyChangeEvent e ) {
		if(e.getPropertyName().equals("ATTRIBUTES_CHANGED")){
			initialize();
			setVisible(true);
		}
	}
	
	
}
