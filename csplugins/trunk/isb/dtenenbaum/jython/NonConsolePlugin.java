package csplugins.isb.dtenenbaum.jython;



import java.io.*; 
import java.net.*;
import java.util.zip.*;

import org.python.core.*;

import java.awt.event.*;
import javax.swing.*;

import java.net.URL;

import java.awt.*;

import cytoscape.*;
import cytoscape.plugin.*;
import cytoscape.view.*; 

import org.python.util.PythonInterpreter;


public class NonConsolePlugin extends CytoscapePlugin {
	protected CyNetwork cyNetwork;
	protected CyNetworkView cyNetworkView;
	protected Thread consoleThread;
	protected PythonInterpreter interp;
	protected NonConsolePlugin nonConsolePlugin;
	protected Thread th;
	JFrame consoleFrame;


	public NonConsolePlugin () {
		this.cyNetwork = Cytoscape.getCurrentNetwork();
		this.cyNetworkView = Cytoscape.getCurrentNetworkView();
		this.nonConsolePlugin = this;
		System.out.println("Waiting for python...");


		Runnable sp = new StartPython();

		th = new Thread(sp);
		th.start();
		new WaitUp();
	}
	
	
	class WaitUp extends JFrame  implements ActionListener {
		
		public WaitUp() {
			
			setSize(300,100);
			setTitle("Please Wait...");
			// TODO - get rid of this--place it in center relative to cytoscape frame
			placeInCenter();
			getContentPane().setLayout(new BorderLayout());
			// keep the message vague enough for general usage
			JLabel lbl = new JLabel("Cytoscape is Loading...");
			getContentPane().add(lbl, BorderLayout.NORTH);
			JProgressBar pb = new JProgressBar();
			pb.setIndeterminate(true);
			getContentPane().add(pb,BorderLayout.CENTER);
			cyNetworkView.redrawGraph(false,true);
			show();
			int delay = 300; // milliseconds
			new javax.swing.Timer(delay, this).start();
			
		}
		
        public void actionPerformed(ActionEvent evt) {
        	if (!th.isAlive()) {
        		this.dispose();
        	}
        }
		
		public void placeInCenter() {
			GraphicsConfiguration gc = getGraphicsConfiguration();
			int screenHeight = (int) gc.getBounds().getHeight();
			int screenWidth = (int) gc.getBounds().getWidth();
			int windowWidth = getWidth();
			int windowHeight = getHeight();
			setLocation((screenWidth - windowWidth) / 2,
					(screenHeight - windowHeight) / 2);

		} // placeInCenter

		
	}
	
	class StartPython implements Runnable {
		public void run() {
			startPython();
		}
		
		public synchronized void startPython() {
			// Setup the basic python system state
			PySystemState.initialize();
			interp = new PythonInterpreter();
			interp.exec("import sys");
			String[] jarNames = ImportPyLibs.importLibs();

			for (int i = 0; i < jarNames.length; i++) {
				interp.exec("sys.path.append('" + jarNames[i] + "')");
			}

			String bootCode = ImportPyLibs.getResourceCode("__run__.py");
			interp.exec(bootCode);
		}
	}


}