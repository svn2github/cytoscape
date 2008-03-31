package de.layclust.layout.acc;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import de.layclust.layout.data.ConnectedComponent;
import de.layclust.layout.geometric_clustering.SingleLinkageClusterer;

public class AntPanel extends JPanel implements MouseListener {

	private ConnectedComponent cc;
	private Playground playground;
	private BufferedImage image;
	private int point_size = 8;
	
	public AntPanel(ConnectedComponent cc, Playground playground, boolean cluster){
		this.cc = cc;
		this.playground = playground;
		if(cluster) {
			buildImageWithClusters();
		} else {
			buildImage();
		}
		this.addMouseListener(this);
	}
	
	
	public void mouseClicked(MouseEvent e) {
		Point xy = this.getMousePosition();
		int x = (int) xy.getX() / point_size;
		int y = (int) xy.getY() / point_size;
		System.out.println("CLICKED! "+x +" / "+ y );
		int[] pos = {x,y};
		int item = playground.getLocation(pos);
		System.out.println("Item: "+item);
		if(item != 0) {
			cc = playground.getCC();
			double[][] positions = cc.getCCPositions();
			double[] posi = positions[item-1];
			double xx = posi[0];
			double yy = posi[1];
			System.out.println("Item: "+item);
			System.out.println("ID: "+cc.getObjectID(item-1));
			System.out.println("Position: "+xx+" / "+yy);
		}
	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	
	public void buildImage() {
		image = new BufferedImage(playground.getSize()*point_size, playground.getSize()*point_size,BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < playground.getSize(); i++) {
			for(int j = 0; j < playground.getSize(); j++) {
				int[] pos = {i,j};
				if(playground.getLocation(pos) == 0) {
					for(int k = 0; k < point_size; k++) {
						for (int l = 0; l < point_size; l++) {
							image.setRGB(i*point_size+k, j*point_size+l, (new Color(120,120,120)).getRGB());
						}
					}
				}else {
					for(int k = 0; k < point_size; k++) {
						for (int l = 0; l < point_size; l++) {
							image.setRGB(i*point_size+k, j*point_size+l, (new Color(0,0,0)).getRGB());
						}
					}
				}
			}
		}
	}
	
	
	public void buildImageWithClusters() {
		image = new BufferedImage(playground.getSize()*point_size, playground.getSize()*point_size,BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < playground.getSize(); i++) {
			for(int j = 0; j < playground.getSize(); j++) {
				int[] pos = {i,j};
				if(playground.getLocation(pos) == 0) {
					for(int k = 0; k < point_size; k++) {
						for (int l = 0; l < point_size; l++) {
							image.setRGB(i*point_size+k, j*point_size+l, (new Color(120,120,120)).getRGB());
						}
					}
				}else {
					int clusterNo = cc.getClusterNoForObject(playground.getLocation(pos)-1);
					int color = getColor(clusterNo);
					for(int k = 0; k < point_size; k++) {
						for (int l = 0; l < point_size; l++) {
							image.setRGB(i*point_size+k, j*point_size+l, color);
						}
					}
				}
			}
		}
	}
	
	public void paint (Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(image, 0, 0, null);
	}
	
	public int getColor(int number) {
		switch (number) {
		case 0:
			return new Color(255,0,0).getRGB();
		case 1:
			return new Color(0,0,255).getRGB();
		case 2:
			return new Color(0,255,0).getRGB();
		case 3:
			return new Color(255,255,0).getRGB();
		case 4:
			return new Color(0,255,255).getRGB();
		case 5:
			return new Color(255,0,255).getRGB();
		case 6:
			return new Color(120,0,0).getRGB();
		case 7:
			return new Color(0,120,0).getRGB();
		case 8:
			return new Color(0,0,120).getRGB();
		case 9:
			return new Color(120,120,0).getRGB();
		case 10:
			return new Color(120,0,120).getRGB();
		case 11:
			return new Color(0,120,120).getRGB();
		default:
			return new Color(255,255,255).getRGB();
		}
	}

}
