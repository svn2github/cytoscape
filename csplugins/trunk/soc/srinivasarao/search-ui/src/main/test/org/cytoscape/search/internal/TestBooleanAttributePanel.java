package org.cytoscape.search.internal;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

import org.cytoscape.search.ui.BooleanAttributePanel;

public class TestBooleanAttributePanel {

	private JFrame jf = new JFrame();
	private BooleanAttributePanel bp = null;

	public TestBooleanAttributePanel() {
		bp = new BooleanAttributePanel("Attribute 1");

		Timer timer = new Timer();
		timer.schedule(new RunTimerTask(), 5000);
	}

	public void createAndShowGUI() {
		jf.add(bp);
		jf.setTitle("Boolean Attribute Panel");
		jf.setLocation(650, 130);
		jf.setSize(300, 600);
		jf.setVisible(true);
	}

	private class RunTimerTask extends TimerTask {
		public final void run() {
			bp.getCheckedValues();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestBooleanAttributePanel sp = new TestBooleanAttributePanel();
		sp.createAndShowGUI();

	}
}
