/* BEGIN_HEADER                                              Java TreeView
*
* $Author: alokito $
* $RCSfile: BitmapWriter.java,v $
* $Revision: 1.6 $
* $Date: 2005/12/05 05:27:53 $
* $Name:  $
*
* This file is part of Java TreeView
* Copyright (C) 2001-2003 Alok Saldanha, All Rights Reserved. Modified by Alex Segal 2004/08/13. Modifications Copyright (C) Lawrence Berkeley Lab.
*
* This software is provided under the GNU GPL Version 2. In particular, 
*
* 1) If you modify a source file, make a comment in it containing your name and the date.
* 2) If you distribute a modified version, you must do it under the GPL 2.
* 3) Developers are encouraged but not required to notify the Java TreeView maintainers at alok@genome.stanford.edu when they make a useful addition. It would be nice if significant contributions could be merged into the main distribution.
*
* A full copy of the license can be found in gpl.txt or online at
* http://www.gnu.org/licenses/gpl.txt
*
* END_HEADER 
*/
package clusterMaker.treeview;

import cytoscape.logger.CyLogger;

import java.awt.image.BufferedImage;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
* The purpose of this class is to collect all the messy special-case code for 
* exporting to bitmap images from java.
*
* It will consist entirely of static methods
*/
public class BitmapWriter {
	public static final String [] formats = new String [] {"png", "jpg"};

	/**
	* write image in the specified format to the output stream, popping up dialogs with specified parent in the event of a problem.
	* 
	*/
	public static boolean writeBitmap(BufferedImage i, String format, OutputStream output, JComponent parent){
			if (formats[0].equals(format)) { // png
				try {
					return writePng(i,output, parent);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(parent, 
					new JTextArea("PNG export had problem " +  e ));
					CyLogger.getLogger(BitmapWriter.class).error("Exception " + e);
					return false;
				}
			} else if (formats[1].equals(format)) { // jpg
				try {
					return writeJpg(i,output, parent);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(parent, 
					new JTextArea("JPEG export had problem " +  e ));
					CyLogger.getLogger(BitmapWriter.class).error("Exception " + e);
					e.printStackTrace();
					return false;
				}
			} else {
					JOptionPane.showMessageDialog(parent, 
					new JTextArea("Format " +  format + " not supported." ));
					return false;
			}
	}

	/**
	* return true on success, false on failure.
	*
	* may throw up warning screens or messages using parent.
	*/
	public static boolean writeJpg(BufferedImage i, OutputStream output, JComponent parent) throws java.io.IOException {
		String version  = System.getProperty("java.version");
		if (version.startsWith("1.4.0") || version.startsWith("1.4.1")) {
			JOptionPane.showMessageDialog(parent, new JTextArea("You are using Java Version " + version + "\n which has known issues with JPEG export. \nPlease try PNG format or upgrade to 1.4.2 or later if this export fails."));
		}
		
		try {
			ImageIO.write(i,"jpg",output);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(parent, new JTextArea(
			"Problem Saving JPEG " + e + "\n" + 
			"Jpeg export requires Java Version 1.4.1 or better.\n" + 
			"You are running " + version + "\n"+
			"If problem persists, try PPM format, which should always work"));
			return false;
		}
		return true;
	}
	
	/**
	* return true on success, false on failure.
	*
	* may throw up warning screens or messages using parent.
	*/
	public static boolean writePng(BufferedImage i, OutputStream output, JComponent parent) throws java.io.IOException {
		String version  = System.getProperty("java.version");
		
		try {
			ImageIO.write(i,"png",output);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(parent, new JTextArea(
						"Problem Saving PNG " + e +" \n"  +
						"Png export requires Java Version 1.4.1 or better.\n" + 
						"You are running " + version + "\n" +
						"If problem persists, try PPM format, which should always work"));
			return false;
		}
		return true;
	}
}
