package org.cytoscape.webservice.psicquic.util;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

public class SplashScreen extends JPanel {

	private static final long serialVersionUID = 8169834934696574351L;

	public SplashScreen() {
		super();
		try {
			// dummy task
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		setPreferredSize(new Dimension(300, 200));
	}

	public static void showSplash() {
		try {
			UIManager.getInstalledLookAndFeels();
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		final JWindow splashScreen = new JWindow();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				System.out.println("splashScreen show start / EDT: "
						+ EventQueue.isDispatchThread());
				ImageIcon img = new ImageIcon(SplashScreen.class
						.getResource("splash.png"));
				splashScreen.getContentPane().add(new JLabel(img));
				splashScreen.pack();
				splashScreen.setLocationRelativeTo(null);
				splashScreen.setVisible(true);
				System.out.println("splashScreen show end");
			}
		});

		System.out.println("createGUI start / EDT: "
				+ EventQueue.isDispatchThread());
		final JFrame frame = new JFrame("SplashScreen");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().add(new SplashScreen()); // new MainPanel() take
														// long time
		frame.pack();
		frame.setLocationRelativeTo(null);
		System.out.println("createGUI end");

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				System.out.println("    splashScreen dispose start / EDT: "
						+ EventQueue.isDispatchThread());
				// splashScreen.setVisible(false);
				splashScreen.dispose();
				System.out.println("    splashScreen dispose end");

				System.out.println("  frame show start / EDT: "
						+ EventQueue.isDispatchThread());
				frame.setVisible(true);
				System.out.println("  frame show end");
			}
		});
	}
}