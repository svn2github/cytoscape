package de.layclust.test;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class TestLayout extends JFrame{
//	BufferedImage image = new BufferedImage(500,500,BufferedImage.TYPE_INT_RGB);
	BufferedImage image;
	
	public TestLayout(BufferedImage image){
		
		super("test layout");
		this.image = image;
		this.setSize(800, 600);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		JPanel panel = new JPanel();
//		panel.add(image);		
		this.getContentPane().add(panel);
	}
	
	public void paint (Graphics g, BufferedImage image) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(image, 0, 0, null);
	}
}