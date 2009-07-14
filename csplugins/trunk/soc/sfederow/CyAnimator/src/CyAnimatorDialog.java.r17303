package src;

import giny.model.Node;
import giny.view.*;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.*;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.ImageIcon;


import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Image;

import java.sql.Time;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.view.*;


import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CyAnimatorDialog extends JDialog implements ActionListener, java.beans.PropertyChangeListener {

	
	
	
	
	private JButton captureFrameOne;
	private JButton captureFrameTwo;
	private JButton animateBetween;
	private JButton returnFrame;
	private JButton captureButton;
	private JButton playButton;
	private JButton stopButton;
	private JPanel mainPanel;
	private JPanel framePanel = new JPanel();
	
	
	//private NodeView[] currentFrame;
	private HashMap<String, double[]> posFrame;
	private HashMap<String, Paint> colFrame;
	private CyFrame frameOne;
	private CyFrame frameTwo;
	ArrayList<CyFrame> frameList = new ArrayList<CyFrame>();
	ArrayList<CyNetworkView> viewList = new ArrayList<CyNetworkView>();
	//private CyFrame[]
	private Timer timer;
	                
	public CyAnimatorDialog(){
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(this);
   	    //add as listener to CytoscapeDesktop
	    Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(this);
		
	    
	    
	    
	    
		initialize();
	}
	
	public void initialize(){
		
		mainPanel = new JPanel();
	    BoxLayout mainbox = new BoxLayout(mainPanel, BoxLayout.Y_AXIS);
		mainPanel.setLayout(mainbox);
		
        JPanel oldPanel = new JPanel();
        //BoxLayout oldbox = new BoxLayout(oldPanel, BoxLayout.Y_AXIS);
		//oldPanel.setLayout(oldbox); 
		
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
		
		oldPanel.add(captureFrameOne);
		oldPanel.add(captureFrameTwo);
		oldPanel.add(animateBetween);
		oldPanel.add(returnFrame);
		
		
		captureButton = new JButton("Capture");
		captureButton.addActionListener(this);
		captureButton.setActionCommand("capture");
		
		playButton = new JButton("Play");
		playButton.addActionListener(this);
		playButton.setActionCommand("play");
		
		stopButton = new JButton("Stop");
		stopButton.addActionListener(this);
		stopButton.setActionCommand("stop");
		
		
		JPanel controlPanel = new JPanel();
		BoxLayout box = new BoxLayout(controlPanel, BoxLayout.X_AXIS);
		controlPanel.setLayout(box);
		
		controlPanel.add(captureButton);
		controlPanel.add(playButton);
		controlPanel.add(stopButton);
		
		// mainPanel.add(oldPanel);
		mainPanel.add(controlPanel);
		mainPanel.add(framePanel);
		
		this.setSize(new Dimension(500,180));
		this.setLocation(900, 100);
		
		setContentPane(mainPanel);
		
		
	}
	
	public int getFrameRate(){
		return 30;
	}
	
	public void actionPerformed(ActionEvent e){
		
		String command = e.getActionCommand();
		
		if(command.equals("capture"))
		{
			frameList.add(captureCurrentFrame());
			//viewList.add(Cytoscape.getCurrentNetworkView());
			updateThumbnails();
			
		}
		
		if(command.equals("play")){
			int delay = getFrameRate();
			play();
		} 
		
		if(command.equals("stop")){
			timer.stop();
		}
		
		if(command.equals("rewind")){
			
		}
		
		if(command.equals("captureOne"))
		{
			frameOne = captureCurrentFrame();
		}
		
		if(command.equals("captureTwo"))
		{
			frameTwo = captureCurrentFrame();
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
				  CyFrame[] frames = lint.makeFrames(frameOne, frameTwo, 10);
				  //}catch (Exception e){
					//  System.out.println(e.getMessage());
				  //}
			      
				  
				  public void actionPerformed(ActionEvent evt) {
			          if(i == frames.length){ return;}
					  frames[i].display();
					  i++;
			      }
			  };
			  
			  new Timer(delay, taskPerformer).start();
			
			  
			  
		}
		
		if(command.equals("returnOne"))
		{
			frameList.get(0).display();
			//frameOne.display();
		}
		
		
		Pattern p = Pattern.compile(".*([0-9]+)$");
		Matcher m = p.matcher(command);
		if(m.matches()){
		 
		
			System.out.println(Integer.parseInt(m.group(1)));
			for(CyFrame frame: frameList){ 
				if(frame.getID().equals(command)){//frameList.get(Integer.parseInt(m.group(1)));
					frame.display();
					
				}
			}
			//Color temp = new Color(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)),Integer.parseInt(m.group(3)));
			//return temp;
		}
		
		
		setVisible(true);
		
	}
	/*
	  ActionListener frameProjector = new ActionListener() {
		  
		  Interpolator lint = new Interpolator();
		  int i = 0;
		  
		  int framenum = frameList.size()*10;
		  CyFrame[] frames = lint.makeFrames(frameOne, frameTwo, framenum);
		  
		  
		  public void actionPerformed(ActionEvent evt) {
	          
			  if(i == frames.length){ return;}
			  frames[i].display();
	          System.out.println(i);
			  i++;
	      }
	  };
	*/
	public void play(){
		
		 int delay = 100; //milliseconds
		  ActionListener taskPerformer = new ActionListener() {
			  
			  Interpolator lint = new Interpolator();
			  int i = 0;
			  
			  CyFrame[] frames = lint.makeFrames(frameList, 10); //lint.makeColorFrames(frameOne, frameTwo, 10);
			
			  
			  public void actionPerformed(ActionEvent evt) {
		          if(i == frames.length){ i = 0;}
				  frames[i].display();
				  i++;
		      }
		  };
		  
		  timer = new Timer(delay, taskPerformer);
	      timer.start();
	}
	
	public void updateThumbnails(){
		
		mainPanel.remove(framePanel);
		framePanel = new JPanel();
		BoxLayout box = new BoxLayout(framePanel, BoxLayout.X_AXIS);
		framePanel.setLayout(box);

		
		java.net.URL leftIconURL = CyAnimatorDialog.class.getResource("leftArrow.png");
		java.net.URL rightIconURL = CyAnimatorDialog.class.getResource("rightArrow.png");
		
        ImageIcon leftIcon = new ImageIcon();
        if (leftIconURL != null) {
            leftIcon = new ImageIcon(leftIconURL);
        }

        ImageIcon rightIcon = new ImageIcon();
        if (rightIconURL != null) {
            rightIcon = new ImageIcon(rightIconURL);
        }
		
		JButton leftArrow = new JButton(leftIcon);
		//leftArrow.setIcon(leftIcon);
		//leftArrow.setSize(new Dimension(200,200));
        framePanel.add(leftArrow);
		JButton rightArrow = new JButton(rightIcon);
		//rightArrow.setIcon(rightIcon);
		//rightArrow.setSize(new Dimension(200,200));
				
		//mainPanel.remove(framePanel);
		for(CyFrame frame: frameList){
		//for(CyNetworkView view: viewList){
			

			ImageIcon ic = new ImageIcon(frame.networkImage);

			JButton lab = new JButton(ic);
			lab.addActionListener(this);
			lab.setActionCommand(frame.getID());
			//lab.setIcon(ic);

			framePanel.add(lab);

		}
		
		framePanel.add(rightArrow);
		//initialize();
		mainPanel.add(framePanel);
		//setContentPane(mainPanel);
	}
	
	
	int frameid = 0;
	
	public CyFrame captureCurrentFrame(){
		
		
		CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();
		CyFrame frame = new CyFrame(currentNetwork);
		
		
		//List<Node> nodeList = currentNetwork.nodesList();
		
		CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
		
		
		
		frame.populate(networkView); 
		
		frame.setID(networkView.getIdentifier()+"_"+frameid);
		frame.captureImage();
		frameid++;
		return frame;
		
	}
	
	
	
	public void propertyChange ( PropertyChangeEvent e ) {
		if(e.getPropertyName().equals("ATTRIBUTES_CHANGED")){
			initialize();
			setVisible(true);
		}
	}
	
}
