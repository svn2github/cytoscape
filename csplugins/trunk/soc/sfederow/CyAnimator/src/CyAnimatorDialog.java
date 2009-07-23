package CyAnimator;

import giny.model.Node;
import giny.view.*;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextArea;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.table.*;
import javax.swing.Timer;

import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.File;

import java.sql.Time;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.view.*;
import cytoscape.layout.Tunable;

import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CyAnimatorDialog extends JDialog implements ActionListener, java.beans.PropertyChangeListener {

	

	private JButton captureButton;
	private JButton playButton;
	private JButton stopButton;
	private JButton pauseButton;
	private JButton forwardButton;
	private JButton backwardButton;
	private JButton recordButton;
	
	private JMenuItem menuItem;
	private JPanel mainPanel;
	private JPopupMenu thumbnailMenu;
	private JSlider speedSlider;
	private ArrayList<JPopupMenu> menuList;
	private JScrollPane framePane = new JScrollPane();
	private JPanel framePanel = new JPanel();
	
	private int fps = 10;
	CyFrame[] frames = null;
	
	//private NodeView[] currentFrame;
	private HashMap<String, double[]> posFrame;
	private HashMap<String, Paint> colFrame;
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
		
		captureButton = new JButton("Add Frame");
		captureButton.addActionListener(this);
		captureButton.setActionCommand("capture");
		
		ImageIcon playIcon = createImageIcon("play.png", "Play Button");
		playButton = new JButton(playIcon);
		playButton.addActionListener(this);
		playButton.setActionCommand("play");    

		ImageIcon stopIcon = createImageIcon("stop.png", "Stop Button");
		stopButton = new JButton(stopIcon);
		stopButton.addActionListener(this);
		stopButton.setActionCommand("stop");

		ImageIcon pauseIcon = createImageIcon("pause.png", "Pause Button");
		pauseButton = new JButton(pauseIcon);
		pauseButton.addActionListener(this);
		pauseButton.setActionCommand("pause");
		
		ImageIcon forwardIcon = createImageIcon("fastForward.png", "Step Forward Button");
		forwardButton = new JButton(forwardIcon);
		forwardButton.addActionListener(this);
		forwardButton.setActionCommand("step forward");
		forwardButton.setToolTipText("Step Forward One Frame");
		
		
		ImageIcon backwardIcon = createImageIcon("reverse.png", "Step Backward Button");
		backwardButton = new JButton(backwardIcon);
		backwardButton.addActionListener(this);
		backwardButton.setActionCommand("step backward");
		backwardButton.setToolTipText("Step Backward One Frame");
		
		ImageIcon recordIcon = createImageIcon("record.png", "Record Animation");
		recordButton = new JButton(recordIcon);
		recordButton.addActionListener(this);
		recordButton.setActionCommand("record");
		recordButton.setToolTipText("Record Animation");
		
		
		speedSlider = new JSlider(1,60);
		
		speedSlider.addChangeListener(new SliderListener());
		
		JPanel controlPanel = new JPanel();
		BoxLayout box = new BoxLayout(controlPanel, BoxLayout.X_AXIS);
		controlPanel.setLayout(box);
		
		controlPanel.add(captureButton);
		controlPanel.add(playButton);
		controlPanel.add(pauseButton);
		controlPanel.add(stopButton);
		controlPanel.add(backwardButton);
		controlPanel.add(forwardButton);
		controlPanel.add(recordButton);
		controlPanel.add(speedSlider);
		
		mainPanel.add(controlPanel);
		mainPanel.add(framePane);
		
		this.setSize(new Dimension(500,220));
		this.setLocation(900, 100);
		
		setContentPane(mainPanel);
		
		
	}
	
	public int getFrameRate(){
		return 30;
	}
	
	public void actionPerformed(ActionEvent e){
		
		String command = e.getActionCommand();
		
		System.out.println(command);
		
		if(command.equals("capture"))
		{
			frameList.add(captureCurrentFrame());
			if(frameList.size() > 1){ makeTimer(); }
			//viewList.add(Cytoscape.getCurrentNetworkView());
			updateThumbnails();
			
		}
		
		if(command.equals("play")){
			int fps = getFrameRate();
			
			if(timer == null){ return; }
			//1000ms in a second, so divided by frames per second gives ms interval 
			timer.setDelay(1000/fps);
			timer.start();
		} 
		
		if(command.equals("stop")){
			if(timer == null){ return; }
			timer.stop();
			makeTimer();
		}
		
		if(command.equals("pause")){
			if(timer == null){ return; }
			timer.stop();
		}
		
		if(command.equals("step forward")){
			if(timer == null){ return; }
			timer.stop();
			frameIndex++;
			frames[frameIndex].display();
		}
		
		if(command.equals("step backward")){
			if(timer == null){ return; }
			timer.stop();
			frameIndex--;
			frames[frameIndex].display();
		}
		
		if(command.equals("record")){
			try{
				recordAnimation();
			}catch (Exception excp) {
				System.out.println(excp.getMessage()); 
			}
		}
		
		Pattern frameID = Pattern.compile(".*([0-9]+)$");
		Pattern interpolateCount = Pattern.compile("(.*)interpolate([0-9]+)_$");
		Pattern deleteFrame = Pattern.compile("(.*)delete_$");
		Matcher fMatch = frameID.matcher(command);
		Matcher iMatch = interpolateCount.matcher(command);
		Matcher dMatch = deleteFrame.matcher(command);
		
		if(fMatch.matches()){
		 	//System.out.println(Integer.parseInt(m.group(1)));
			for(CyFrame frame: frameList){ 
				if(frame.getID().equals(command)){//frameList.get(Integer.parseInt(m.group(1)));
					frame.display();		
				}
			}	
		}
		if(iMatch.matches()){
			int inter = Integer.parseInt(iMatch.group(2));
			if(inter == 0){
				String input = JOptionPane.showInputDialog("Enter the number of frames to interpolate over: ");
				try {
					inter = Integer.parseInt(input);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(this, "Frame interpolation count must be an integer", 
					                              "Integer parse error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			for(CyFrame frame: frameList){
				if(frame.getID().equals(iMatch.group(1))){
					frame.setInterCount(inter);
					if(timer.isRunning()){ 
						timer.stop();
						makeTimer();
						timer.start();
					}else{
						makeTimer();
					}
				}
			}
			// System.out.println(iMatch.group(2));
		}
		if(dMatch.matches()){
			List<CyFrame>remove = new ArrayList();
			for(CyFrame frame: frameList){
				if(frame.getID().equals(dMatch.group(1))){
					remove.add(frame);
				}
			}
			for (CyFrame frame: remove)
				frameList.remove(frame);
			updateThumbnails();
		}
		
		setVisible(true);
		
	}
	
	int frameIndex = 0;
	
	public void makeTimer(){
	
		frameIndex = 0;
		
		Interpolator lint = new Interpolator();
		frames = lint.makeFrames(frameList);
		
		int delay = 1000/30; //milliseconds
		
		
		ActionListener taskPerformer = new ActionListener() {

			
			//int frameIndex = 0;
			
			//CyFrame[] frames = lint.makeFrames(frameList); //lint.makeColorFrames(frameOne, frameTwo, 10);


			public void actionPerformed(ActionEvent evt) {
				if(frameIndex == frames.length){ frameIndex = 0;}
				// System.out.println("Frame: "+i);
				frames[frameIndex].display();
				// System.out.println(timer.getDelay());
				frameIndex++;
			}
		};


		timer = new Timer(delay, taskPerformer);
		//timer.setDelay(delay);
		//timer.start();
	}
	
	public void updateThumbnails(){

		mainPanel.remove(framePane);
		framePanel = new JPanel();

		BoxLayout box = new BoxLayout(framePanel, BoxLayout.X_AXIS);
		framePanel.setLayout(box);

		MouseListener popupListener = new PopupListener();
		MouseListener mouseOver = new MouseOver();
		//Add listener to components that can bring up popup menus.
		
		//output.addMouseListener(popupListener);
		//menuBar.addMouseListener(popupListener);

		/*
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
		*/ 
		
		menuList = new ArrayList<JPopupMenu>();
		//mainPanel.remove(framePanel);
		int i = 0;
		for(CyFrame frame: frameList){
			//for(CyNetworkView view: viewList){


			ImageIcon ic = new ImageIcon(frame.getFrameImage());

			JButton lab = new JButton(ic);
			lab.addActionListener(this);
			lab.setActionCommand(frame.getID());
			//lab.setIcon(ic);
			
			thumbnailMenu = new JPopupMenu();
			JMenu interpolateMenu = new JMenu("Interpolate");
			menuItem = new JMenuItem("10 Frames");
			menuItem.addActionListener(this);
			menuItem.setActionCommand(frame.getID()+"interpolate10_");
			interpolateMenu.add(menuItem);

			menuItem = new JMenuItem("20 Frames");
			menuItem.addActionListener(this);
			menuItem.setActionCommand(frame.getID()+"interpolate20_");
			interpolateMenu.add(menuItem);

			menuItem = new JMenuItem("50 Frames");
			menuItem.addActionListener(this);
			menuItem.setActionCommand(frame.getID()+"interpolate50_");
			interpolateMenu.add(menuItem);

			menuItem = new JMenuItem("100 Frames");
			menuItem.addActionListener(this);
			menuItem.setActionCommand(frame.getID()+"interpolate100_");
			interpolateMenu.add(menuItem);

			menuItem = new JMenuItem("Custom...");
			menuItem.addActionListener(this);
			menuItem.setActionCommand(frame.getID()+"interpolate0_");
			interpolateMenu.add(menuItem);
			
			thumbnailMenu.add(interpolateMenu);
			
			menuItem = new JMenuItem("Delete");
			menuItem.addActionListener(this);
			menuItem.setActionCommand(frame.getID()+"delete_");
			menuItem.addMouseListener(popupListener);
			//menuItem.set("delete_"+frame.getID());
			thumbnailMenu.add(menuItem);
			menuList.add(thumbnailMenu);
			
			//MouseListener 
			lab.addMouseListener(mouseOver);
			lab.setName(i+"");
			i++;
			lab.addMouseListener(popupListener);
			framePanel.add(lab);

		}

		//framePanel.add(rightArrow);
		//initialize();
		framePane = new JScrollPane(framePanel);
		mainPanel.add(framePane);
		//setContentPane(mainPanel);
	}
	
	
	int frameid = 0;
	
	public CyFrame captureCurrentFrame(){
		
		
		CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();
		CyFrame frame = new CyFrame(currentNetwork);
		
		CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
		System.out.println("Current Network: "+currentNetwork.getIdentifier());
		System.out.println("Current NetworkView: "+networkView.getIdentifier());
		
		
		frame.populate(); // Initialize the frame
		frame.setInterCount(30);
		frame.setID(networkView.getIdentifier()+"_"+frameid);
		frame.captureImage();
		frameid++;
		return frame;
		
	}
	
	public void recordAnimation() throws IOException {
	
		String curDir = System.getProperty("user.dir");
		System.out.println(curDir);
		
		File file = new File("outputImages");
		file.mkdir();
		OutputStream imageStream = new FileOutputStream(file);
		
		for(int i=0; i<frames.length; i++){
			String name = "Frame_"+i;
			ImageIO.write(frames[i].getFrameImage(), name, imageStream);
		}
	}
	
	public void propertyChange ( PropertyChangeEvent e ) {
		if(e.getPropertyName().equals("ATTRIBUTES_CHANGED")){
			initialize();
			setVisible(true);
		}
	}
	
	class SliderListener implements ChangeListener {
	    public void stateChanged(ChangeEvent e) {
	        JSlider source = (JSlider)e.getSource();
	        if (!source.getValueIsAdjusting()) {
	            fps = (int)source.getValue();
	            if(timer == null){ return; }
	            System.out.println("FPS: "+fps);
	            timer.setDelay(1000/fps);
	            
	        }    
	    }
	}

	class MouseOver extends MouseAdapter implements MouseListener{
		int upperBound, lowerBound, leftBound, rightBound;
		
		
		public void mousePressed(MouseEvent e) {
			
		}

		public void mouseReleased(MouseEvent e) {
			
		}

		public void mouseEntered(MouseEvent e) {
			//e.getComponent().
			System.out.println(e.paramString());
		}

		public void mouseExited(MouseEvent e) {
			//saySomething("Mouse exited", e);
		}

		public void mouseClicked(MouseEvent e) {
			//saySomething("Mouse clicked (# of clicks: "
					//+ e.getClickCount() + ")", e);
		}

	}
	
	class PopupListener extends MouseAdapter {
	    public void mousePressed(MouseEvent e) {
	        maybeShowPopup(e);
	    }

	    public void mouseReleased(MouseEvent e) {
	        maybeShowPopup(e);
	    }

	    private void maybeShowPopup(MouseEvent e) {
	        if (e.isPopupTrigger()) {
	        	
	            //menuList.get(menuIndex)
	        	
	        	//System.out.println(e.getComponent().getName());
	        	int currentIndex = Integer.parseInt(e.getComponent().getName());
	            menuList.get(currentIndex).show(e.getComponent(),
	                       e.getX(), e.getY());
	        }
	    }
	}
	
	protected ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	
}
