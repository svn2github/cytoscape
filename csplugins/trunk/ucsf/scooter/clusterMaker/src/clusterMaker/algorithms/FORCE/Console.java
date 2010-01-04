package clusterMaker.algorithms.FORCE;


import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;



public class Console {
	
	private static JProgressBar progressBar;
	private static JPanel root = new JPanel();
	
	private static JFrame frame = new JFrame("FORCE Clustering");
	
	static {
		
		root.setLayout(new BoxLayout(root,BoxLayout.Y_AXIS));
				
		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setString("");
		
		root.add(progressBar);
		// root.setPreferredSize(new Dimension(400, 50));
		
	}
	
	public static void startNewConsoleWindow(int min, int max, String text) {
		frame = new JFrame("FORCE Clustering");
		restartBar(min, max);
		setBarText(text);
		frame.add(getConsolePanel());
		frame.setVisible(true);
		frame.setSize(400, 50);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width-frame.getSize().width)/2;
		int y = (screenSize.height-frame.getSize().height)/2;
		frame.setLocation(x, y);
	}
	
	public static void closeWindow() {
		frame.dispose();
	}
	
	public static JPanel getConsolePanel() {
		return root;
	}
	
	
	public static void setBarMin(int x) {
		progressBar.setMinimum(x);
	}
	
	public static void setBarMax(int x) {
		progressBar.setMaximum(x);
	}
	
	public static void setBarText(String x) {
		progressBar.setString(x);
	}
	
	static long stopTime = 0;
	static long timePerRun = 0;
	static long restTime = 0;
	static long startTime = 0;
	
	public static void restartBarTimer() {
		startTime = System.currentTimeMillis();
	}
	
	public static void restartBar(int min, int max) {
		progressBar.setMinimum(min);
		progressBar.setMaximum(max);
		restartBarTimer();
	}
	
	public static void setBarTextPlusRestTime(String x) {
		progressBar.setString(x + " - Remaining: " + getTimeString(restTime));
	}
	
	public static void setBarTextAndValuePlusRestTime(String x, int value) {
		setBarValue(value);
		progressBar.setString(x + " - Remaining: " + getTimeString(restTime));
	}
	
	public static void setBarValue(int x) {
		progressBar.setValue(x);
		
		stopTime = System.currentTimeMillis();
		timePerRun = (stopTime - startTime)/(x+1);
		restTime = timePerRun * (progressBar.getMaximum()-x);
	}
	
	
	
	public static String getTimeString(long diff) {
		
		int h = 0;
		int m = 0;
		int s = 0;
		
		s = (int) Math.rint(diff/1000);
		m = (int) Math.rint(s/60);
		h = (int) Math.rint(m/60);
		
		String str = new String();
		
		if ((h == 0) && (m == 0)) {
			str = s + " s";
		} else if (h == 0) {
			s = s - (m*60);
			str = m + " min " + s + " s";
		} else {
			m = m - (h*60);
			s = s - (m*60);
			str = h + " h " + m + " min " + s + " s";
		}
		
		return str;
	}

}













