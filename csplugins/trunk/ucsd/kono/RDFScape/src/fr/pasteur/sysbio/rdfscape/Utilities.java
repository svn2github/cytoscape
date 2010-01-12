/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Jan 26, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.pasteur.sysbio.rdfscape;

import java.awt.Color;

import javax.swing.ImageIcon;

import cytoscape.Cytoscape;

/**
 * @author andrea
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Utilities {

	public static String getNameSpaceColorHexString(String ns) {
		Color tempColor=DefaultSettings.translateString2Color(ns);
		String red=Integer.toHexString(tempColor.getRed());
		String green=Integer.toHexString(tempColor.getGreen());
		String blue=Integer.toHexString(tempColor.getBlue());
		if(red.length()==1) red="0"+red;
		if(green.length()==1) green="0"+green;
		if(blue.length()==1) blue="0"+blue;
		return red+green+blue;
		
	}
	public static ImageIcon getGreenlightIcon() {
		java.net.URL imageURL=null;
		ImageIcon icon=new ImageIcon();
		imageURL= Utilities.class.getResource("../../../../images/green.png");
		if(imageURL!=null) icon = new ImageIcon(imageURL);
		return icon;
	}
	public static ImageIcon getYellowlightIcon() {
		java.net.URL imageURL=null;
		ImageIcon icon=new ImageIcon();
		imageURL= Utilities.class.getResource("../../../../images/yellow.png");
		if(imageURL!=null) icon = new ImageIcon(imageURL);
		return icon;
	}
	public static ImageIcon getRedlightIcon() {
		java.net.URL imageURL=null;
		ImageIcon icon=new ImageIcon();
		imageURL= Utilities.class.getResource("../../../../images/red.png");
		if(imageURL!=null) icon = new ImageIcon(imageURL);
		return icon;
	}
	/**
	 * TODO In theory we should change icons depending on the current platform
	 */
	public static ImageIcon getHelpIcon(String iconName) {
		java.net.URL imageURL=null;
		ImageIcon icon=new ImageIcon();
		imageURL= Utilities.class.getResource("../../../../images/help/unix/"+iconName);
		if(imageURL!=null) icon = new ImageIcon(imageURL);
		return icon;
	}
}
