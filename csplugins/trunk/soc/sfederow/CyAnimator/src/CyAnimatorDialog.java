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
import javax.swing.JTextPane;

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

import java.text.DecimalFormat;

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
import java.awt.event.FocusListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
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

import javax.swing.JButton;
import javax.swing.Icon;
import javax.swing.Action;
import java.awt.Font;



public class CyAnimatorDialog extends JDialog implements ActionListener, java.beans.PropertyChangeListener, FocusListener {

	

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
	
	private ArrayList<JPopupMenu> menuList = new ArrayList<JPopupMenu>();
	private ArrayList<JButton> thumbnailList = new ArrayList<JButton>();
	
	private JScrollPane framePane = new JScrollPane();
	private JPanel framePanel = new JPanel();
	private DragAndDropManager dragnDrop = new DragAndDropManager();
	
	private int fps = 10;
	CyFrame[] frames = null;
	
	//private NodeView[] currentFrame;
	private HashMap<String, double[]> posFrame;
	private HashMap<String, Paint> colFrame;
	ArrayList<CyFrame> frameList = new ArrayList<CyFrame>();
	ArrayList<CyNetworkView> viewList = new ArrayList<CyNetworkView>();
	//private CyFrame[]
	private Timer timer;
	       
	boolean upFlag = true;
	
	public CyAnimatorDialog(){
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(this);
		//add as listener to CytoscapeDesktop
		Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(this);
		
		initialize();
	}
	
	/*
	 * Create the control buttons, panels, and initialize the main JDialog.
	 */
	public void initialize(){
		
		mainPanel = new JPanel();
		mainPanel.addPropertyChangeListener(this);
		
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
		pauseButton.addPropertyChangeListener("Text", this);
		
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
		
		updateThumbnails(); 
		mainPanel.add(framePane);
		
		this.setSize(new Dimension(500,220));
		this.setLocation(900, 100);
		
		setContentPane(mainPanel);
		
		
	}

	
	public void actionPerformed(ActionEvent e){
		
		String command = e.getActionCommand();
		
		//System.out.println(command);
		
		if(command.equals("capture"))
		{
			frameList.add(captureCurrentFrame());			
			if(frameList.size() > 1){ makeTimer(); }
			
			updateThumbnails();
		}
		
		if(command.equals("play")){
			int fps = 30;
			
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
			
			//check to see if we have reached the last frame
			if(frameIndex == frames.length-1){ frameIndex = 0; }
			else{ frameIndex++; }
			
			frames[frameIndex].display();
		}
		
		if(command.equals("step backward")){
			if(timer == null){ return; }
			timer.stop();
			
			//check to see if we are back to the first frame
			if(frameIndex == 0){ frameIndex = frames.length-1; }
			else{ frameIndex--; }
			
			frames[frameIndex].display();
		}
		
		if(command.equals("record")){
			try{
				recordAnimation();
			}catch (Exception excp) {
				System.out.println(excp.getMessage()); 
			}
			updateThumbnails();
			makeTimer();
		}
		
		Pattern interpolateCount = Pattern.compile("(.*)interpolate([0-9]+)_$");
		Matcher iMatch = interpolateCount.matcher(command);
				
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

			int index = thumbnailPopupIndex;

			frameList.get(index).setInterCount(inter);
			updateTimer();
		}
		
		
		if(command.equals("delete")){
			List<CyFrame>remove = new ArrayList();

			remove.add(frameList.get(thumbnailPopupIndex));
				
			
			for (CyFrame frame: remove)
				frameList.remove(frame);
				
			
			updateThumbnails();
			
			updateTimer();
		}
		
		
		
		if(command.equals("move right")){

			int i = thumbnailPopupIndex;

			if(i != frameList.size()-1){
				CyFrame tmp = frameList.get(i+1);
				frameList.set(i+1, frameList.get(i));
				frameList.set(i, tmp);

			}

			updateThumbnails();
			
			updateTimer();
		}
		
		if(command.equals("move left")){
			int i = thumbnailPopupIndex;

			if(i != 0){
				CyFrame tmp = frameList.get(i-1);
				frameList.set(i-1, frameList.get(i));
				frameList.set(i, tmp);	
			}	

			updateThumbnails();
			
			updateTimer();
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

			public void actionPerformed(ActionEvent evt) {
				if(frameIndex == frames.length){ frameIndex = 0;}
				// System.out.println("Frame: "+i);
				frames[frameIndex].display();
				// System.out.println(timer.getDelay());
				frameIndex++;
			}
		};


		timer = new Timer(delay, taskPerformer);
		//updateThumbnails();
		
	}

	public void updateTimer(){
		if(timer.isRunning()){ 
			timer.stop();
			makeTimer();
			timer.start();
		}else{
			makeTimer();
		}
	}
	/*
	 * Takes the current frameList and cycles through it to create a JButton
	 * for each frame with the corresponding thumbnail image.  It also creates
	 * a JPopupMenu for each frame in the frameList so that the right click controls
	 * for interpolate and delete can be tied to each JButton.
	 * 
	 */
	public void updateThumbnails(){

		
		mainPanel.remove(framePane);
		framePanel = new JPanel();
		
		BoxLayout box = new BoxLayout(framePanel, BoxLayout.X_AXIS);
		framePanel.setLayout(box);

		MouseListener popupListener = new PopupListener();
		
 		
		//menuList = new ArrayList<JPopupMenu>();
		//mainPanel.remove(framePanel);
		int i = 0;
		int totalFrameWidth = 0;
		//dragnDrop = new DragAndDropManager();
		dragnDrop.setFrameCount(frameList.size());
	
		for(CyFrame frame: frameList){

			ImageIcon ic = new ImageIcon(frame.getFrameImage());

			JButton thumbnailButton = new JButton(ic);
			thumbnailButton.addMouseListener(dragnDrop);
			thumbnailButton.addActionListener(this);
			thumbnailButton.setActionCommand(frame.getID());
		
			
			
			//System.out.println(ic.getIconWidth());
			
		    //for some reason thumbnailButton.getWidth() returns 0 so I had
			//to improvise and use the icon width plus 13 pixels for the border. 
			totalFrameWidth = totalFrameWidth + ic.getIconWidth() + 13;
			dragnDrop.setFrameHeight(ic.getIconHeight());
			
			
			thumbnailMenu = new JPopupMenu();
			JMenu interpolateMenu = new JMenu("Interpolate");
			menuItem = new JMenuItem("10 Frames");
			menuItem.addActionListener(this);
			menuItem.setActionCommand(i+"interpolate10_");
			//menuItem.setActionCommand(frame.getID()+"interpolate10_");
			interpolateMenu.add(menuItem);

			menuItem = new JMenuItem("20 Frames");
			menuItem.addActionListener(this);
			menuItem.setActionCommand(i+"interpolate20_");
			//menuItem.setActionCommand(frame.getID()+"interpolate20_");
			interpolateMenu.add(menuItem);

			menuItem = new JMenuItem("50 Frames");
			menuItem.addActionListener(this);
			menuItem.setActionCommand(i+"interpolate50_");
			//menuItem.setActionCommand(frame.getID()+"interpolate50_");
			interpolateMenu.add(menuItem);

			menuItem = new JMenuItem("100 Frames");
			menuItem.addActionListener(this);
			menuItem.setActionCommand(i+"interpolate100_");
			//menuItem.setActionCommand(frame.getID()+"interpolate100_");
			interpolateMenu.add(menuItem);

			menuItem = new JMenuItem("Custom...");
			menuItem.addActionListener(this);
			menuItem.setActionCommand(i+"interpolate0_");
			//menuItem.setActionCommand(frame.getID()+"interpolate0_");
			interpolateMenu.add(menuItem);
			
			thumbnailMenu.add(interpolateMenu);
			
			menuItem = new JMenuItem("Delete");
			menuItem.addActionListener(this);
			menuItem.setActionCommand("delete");
			//menuItem.setActionCommand(frame.getID()+"delete_");
			menuItem.addMouseListener(popupListener);
			thumbnailMenu.add(menuItem);
			
			System.out.println("Update: "+frame.getID());
			
			menuItem = new JMenuItem("Move Right");
			menuItem.addActionListener(this);
			menuItem.setActionCommand("move right");
			//menuItem.setActionCommand(frame.getID()+"mright_");
			menuItem.addMouseListener(popupListener);
			thumbnailMenu.add(menuItem);
			
			
			
			menuItem = new JMenuItem("Move Left");
			menuItem.addActionListener(this);
			menuItem.setActionCommand("move left");
			//menuItem.setActionCommand(frame.getID()+"mleft_");
			menuItem.addMouseListener(popupListener);
			thumbnailMenu.add(menuItem);
			
			//menuItem.set("delete_"+frame.getID());
			
			menuList.add(thumbnailMenu);
			
		
			thumbnailButton.setName(i+"");
			i++;
			thumbnailButton.addMouseListener(popupListener);
			framePanel.add(thumbnailButton);
			
		}
		
		
		dragnDrop.setFrameWidth(totalFrameWidth);
		
		framePane = new JScrollPane(framePanel);
		
		mainPanel.add(framePane);
		
	}
	
	
	int frameid = 0;
	
	public CyFrame captureCurrentFrame(){
		
		CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();
		CyFrame frame = new CyFrame(currentNetwork);
		
		CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
		
		frame.populate(); // Initialize the frame
		frame.setInterCount(30);
		frame.setID(networkView.getIdentifier()+"_"+frameid);
		System.out.println("Frame ID: "+frameid);
		frame.captureImage();
		frameid++;
		return frame;
		
	}
	
	public void recordAnimation() throws IOException {
	
		String curDir = System.getProperty("user.dir");
		System.out.println(curDir);
		
		File file = new File(curDir+"/outputImgs");
		file.mkdir();
		
		for(int i=0; i<frames.length; i++){
			DecimalFormat frame = new DecimalFormat("#000");
			//String name = curDir+"/outputImgs/Frame_"+frame.format(i)+".png";
			String name = curDir+"/outputImgs/Frame_"+frame.format(i)+".jpeg";
			
			
			frames[i].writeImage(name);
			}
	}
	
	public void propertyChange ( PropertyChangeEvent e ) {
		if(e.getPropertyName().equals("ATTRIBUTES_CHANGED")){
			//initialize();
			setVisible(true);
		}
		
	
	}
	
	public void focusGained(FocusEvent e){
		updateThumbnails();
	}
	
	public void focusLost(FocusEvent e){
		
	}
	
	/*
	 * Listens for changes to the slider which then adjusts the speed of animation
	 */
	class SliderListener implements ChangeListener {
	    public void stateChanged(ChangeEvent e) {
	        JSlider source = (JSlider)e.getSource();
	        if (!source.getValueIsAdjusting()) {
	        	
	        	//fps is frames per second
	            fps = (int)source.getValue();
	            if(timer == null){ return; }
	            System.out.println("FPS: "+fps);
	            //timer delay is set in milliseconds, so 1000/fps gives delay per frame
	            timer.setDelay(1000/fps);
	            
	        }    
	    }
	}

	/*
	 * Contains all of the code for managing mouse selections in relation to the drag and drop features.
	 * 
	 * 
	 */
	public class DragAndDropManager extends MouseMotionAdapter implements MouseListener, MouseMotionListener{
		private int frameCount = 0;
		private int totalFrameWidth = 0;
		private int frameWidth = 0;
		private int frameHeight = 0;
		private int currFrameIndex = 0;
		private Component clickedComponent;
		private int startX = 0;
		private int startY = 0;
		private int endX = 0;
		private int endY = 0;
		
		public DragAndDropManager(){}
		
		public void mousePressed(MouseEvent e) {
			
			//get starting point of the drag and drop
			startX = e.getX();
			startY = e.getY();
				
			//this.currFrameIndex = Integer.parseInt(e.getComponent().getName());	
		}
		
		
		public void mouseReleased(MouseEvent e) {
			
			//get ending point of the drag and drop
			endX = e.getX();
			endY = e.getY();
			
			//check to make sure the drag and drop is within 1.5 times the height 
			//of the frame in either direction of the y coordinate
			if(endY >= 0 && endY < frameHeight*1.5){
				
			}
			else{ 
				if(endY < 0 && endY > frameHeight*-1.5){}
				else{ return; }
			}
			int curr = currFrameIndex; //Integer.parseInt(clickedComponent.getName());
			
			//align the start position to the right or leftmost point of the clicked frame
			//based upon whether it is a forward or backwards drag respectively.
			int xgap = endX - startX;
			if(xgap >= 0){ xgap = xgap - (frameWidth - startX); }
			else{ xgap = xgap + (frameWidth - startX); }
			
			//divide xgap by framewidth to determine the number of frame positions which should be shifted
			double shiftCount = xgap/frameWidth;
			//round this same number
			int shiftInt = Math.round(xgap/frameWidth);
			
			//compare the raw number to the rounded number to see if it was rounded up or down
			//if it is rounded up then subtract 1 from the shift count
			if(shiftInt > shiftCount){
				shuffleList(curr, curr+shiftInt-1);
			}else{
				shuffleList(curr, curr+shiftInt);
			}

			//System.out.println("release   X: "+e.getX()+"  Y: "+e.getY());
			}
		
		 public void mouseDragged(MouseEvent e) {
			 	System.out.println("hey");
			
		}
		
		 
		/*
		 * Shuffles the list by inserting the frame which was clicked (curr) into
		 * the position where the user released the mouse which is the insert
		 * position.
		 * 
		 * @param curr is the index of the frame that was clicked by the user
		 * @param insertPos is the index in the frame list where the frame will be inserted
		 */ 
		public void shuffleList(int curr, int insertPos){
			
			ArrayList<CyFrame> temp = new ArrayList<CyFrame>();
			
			for(int i=0; i<frameList.size(); i++){
				
				
				if(i == insertPos){ 
					temp.add(frameList.get(curr));
					continue;
				}
	
				
				if(i >= curr && curr < insertPos && i < insertPos && i < frameList.size()-1){ 
					temp.add(frameList.get(i+1)); 
					continue;
				}else{
					if(i <= curr && curr > insertPos && i > insertPos){
						temp.add(frameList.get(i-1));
						continue;
					}
				}

				temp.add(frameList.get(i));
			
			}
			
			
			frameList = temp;
			
			//This is where I feel like updateThumbnails() should go, however when this is done it freezes
			//until another button is pressed (i.e. Add Frame, pause, etc..) at which point the thumbnails update.
			
			//updateThumbnails();
		}

		public void mouseEntered(MouseEvent e) { 
			//e.getComponent().requestFocus();
			
		}

		public void mouseExited(MouseEvent e) {	}

		public void mouseClicked(MouseEvent e) {
			
			//Pattern frameID = Pattern.compile(".*([0-9]+)$");
			//Matcher fMatch = frameID.matcher(e.getComponent().getName());
			
			this.currFrameIndex = Integer.parseInt(e.getComponent().getName());
			//if(fMatch.matches()){
				frameList.get(currFrameIndex).display();
			//}
		}
		
		/*
		 * Sets the number of frames which is the number of thumbnail buttons
		 * 
		 * @param number of frames before interpolation
		 */
		public void setFrameCount(int frameCount){
			this.frameCount = frameCount;
		}
		/*
		 * set the total width(in pixels) of all of the frames combined
		 * 
		 * @param the total frame width
		 */
		public void setFrameWidth(int totalFrameWidth){
			this.totalFrameWidth = totalFrameWidth;
			if(frameCount == 0){ frameWidth = 0; return; }
			frameWidth = totalFrameWidth/frameCount;
		}
		
		/*
		 * set the frame height
		 * 
		 * @param frame height
		 */
		public void setFrameHeight(int frameHeight){
			this.frameHeight = frameHeight;
		}
		
		
	}
	
	int thumbnailPopupIndex = 0;
	
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
	        	thumbnailPopupIndex = currentIndex;
	        	System.out.println("CI: "+currentIndex);
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
