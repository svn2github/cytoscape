package cytoscape.editor.impl;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JLabel;

import cytoscape.editor.CytoscapeEditorManager;

public class TextAnnotation extends JComponent {
	private BufferedImage img; // image for figure we are drawing

	private int x1, y1, h1, w1;

	private BufferedImage image; // enclosing image for rendering on the
									// canvas

	private String text;

	public TextAnnotation(String text, int x1, int y1, int w1, int h1) {
//		super();
//		this.img = new BufferedImage(44, 33, BufferedImage.TYPE_INT_ARGB);
//		Graphics2D grafx = img.createGraphics();

		this.text = text;
		this.img = img;
		this.x1 = x1;
		this.y1 = y1;
		this.w1 = w1;
		this.h1 = y1;
		
		JLabel label = new JLabel(text);
		this.add(label);
		label.setVisible(true);
		label.setOpaque(true);

		setBounds(x1, y1, w1, h1);
		label.repaint();
		this.repaint();
	}

//	public void setBounds(int x, int y, int width, int height) {
//		super.setBounds(x, y, width, height);

		// set member vars
//		this.x1 = x;
//		this.y1 = y;
//		this.w1 = width;
//		this.h1 = height;
//
//		// our bounds have changed, create a new image with new size
//		if ((width > 0) && (height > 0)) {
//			image = new BufferedImage(width, height,
//					BufferedImage.TYPE_INT_ARGB);
//		}
		public void setBounds(int x, int y, int width, int height) {
			super.setBounds(x, y, width, height);

			// set member vars
			this.x1 = x;
			this.y1 = y;
			this.w1 = width;
			this.h1 = height;

			// our bounds have changed, create a new image with new size
			if ((width > 0) && (height > 0)) {
				image = new BufferedImage(width, height,
						BufferedImage.TYPE_INT_ARGB);
			}
		}
//	}

	public void paint(Graphics g) {

		Graphics2D image2D = image.createGraphics();
		

		// draw into the image
		Composite origComposite = image2D.getComposite();
		image2D
				.setComposite(AlphaComposite
						.getInstance(AlphaComposite.SRC));
////		image2D.drawString(this.text, this.x1, this.y1);
		CytoscapeEditorManager.log ("drawing " + text + " to " + image);
		
		image2D.setColor(Color.red);
//		image2D.fillRect(0, 0, image.getWidth(null), image.getHeight(null));
		
		image2D.setColor(Color.black);
		image2D.drawString(text, 10, 10);
		image2D.setComposite(origComposite);


		((Graphics2D) g).drawImage(image, null, 0, 0);
		
	}

	public int getH1() {
		return h1;
	}

	public void setH1(int h1) {
		this.h1 = h1;
	}

	public int getW1() {
		return w1;
	}

	public void setW1(int w1) {
		this.w1 = w1;
	}

	public int getX1() {
		return x1;
	}

	public void setX1(int x1) {
		this.x1 = x1;
	}

	public int getY1() {
		return y1;
	}

	public void setY1(int y1) {
		this.y1 = y1;
	}

	public BufferedImage getImg() {
		return img;
	}

	public void setImg(BufferedImage img) {
		this.img = img;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
