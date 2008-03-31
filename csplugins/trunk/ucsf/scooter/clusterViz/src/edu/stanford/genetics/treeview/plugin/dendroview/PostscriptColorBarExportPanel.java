/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: PostscriptColorBarExportPanel.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/08/16 19:13:45 $
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
package edu.stanford.genetics.treeview.plugin.dendroview;

import java.awt.Color;
import java.io.*;

import edu.stanford.genetics.treeview.LogBuffer;
import edu.stanford.genetics.treeview.SettingsPanel;

/**
*  Subclass of ColorBarExportPanel which outputs a postscript version of color bar scale
*
*/
public class PostscriptColorBarExportPanel extends ColorBarExportPanel implements SettingsPanel {
  
  // I wish I could just inherit this...
  public PostscriptColorBarExportPanel(ColorExtractor colorExtractor) {
	super(colorExtractor);
  }
  
  public void synchronizeTo() {
	save();
  }
  
  public void synchronizeFrom() {
	// do nothing...
  }
  public void save() {
	  try {
	  PrintStream output = new PrintStream(new BufferedOutputStream
	  (new FileOutputStream(getFile())));
		  
		  ColorBarPostscriptWriter gw = new ColorBarPostscriptWriter();
		  gw.write(output);
		  
		  output.close();
	  } catch (Exception e) {
		  LogBuffer.println("Postscript ColorBar Export Panel caught exception " + e);
	  }
  }
  /**
  * indicate to superclass that this type does not have bbox
  */
  protected boolean hasBbox() { return true;}
  
  protected String getInitialExtension() {
	return("_colorbar.ps");
  }

  /**
  *  Inner class which outputs a postscript version of Dendroview like things
  *
  *	It is "loosely coupled" in that it only calls protected methods in the ExportPanel superclass.
  */
  
  class ColorBarPostscriptWriter {
	
	/** 
	* Writes out postscript header, much of it stolen directly from eisen.
	*/
	private void writeHeader(PrintStream ps) {
	  int totalWidth = estimateWidth();;
	  int totalHeight = estimateHeight(); 
	  
	  ps.println("%!PS-Adobe-3.0");
	  if (includeBbox()) {
		ps.println("%%BoundingBox: 0 0 " + totalWidth + " " + totalHeight);
	  }
	  ps.println("%%Creator: ColorBarPostscriptWriter (a Java TreeView Component)");
	  ps.println("%%CreationDate: " + (new java.util.Date()).toString());
	  ps.println("%%Pages: (atend)");
	  ps.println("%%EndComments");
	  ps.println("%%BeginSetup");
	  
	  ps.println("/ln { newpath moveto lineto stroke closepath } bind def");
	  ps.println("/tx { newpath moveto show closepath } bind def");
	  ps.println("/sl { setlinewidth } def");
	  ps.println("/sc { setlinecap } def");
	  ps.println("/sr { setrgbcolor } def");
	  ps.println("/sf { exch findfont exch scalefont setfont } def");
	  ps.println("/tr { translate } def");
	  ps.println("/sp { 1 sc 1 sl 0.0 0.0 0.0 sr 18.00000 13.00000 tr 0.96000 0.98205 scale tr } def");
	  ps.println("/fb {exch dup 0 rlineto exch 0 exch rlineto neg 0 rlineto closepath fill } bind def");
	  // consLineTo duplicates the point on the stack, lineto and strokes, and then moves to it.
	  ps.println("/consLineTo {1 index 1 index lineto stroke moveto} bind def");
	  // the following expects rx, ry, tx, ly, lx as arguments, and draws a line connecting, for GTR
	  ps.println("/snGTR {1 index moveto 1 index exch consLineTo 1 index consLineTo lineto stroke} bind def");
	  ps.println("/snATR {1 index exch moveto 1 index consLineTo 1 index exch consLineTo exch lineto stroke } bind def");
	  
	  // old eisen fillbox: '/fillbox {newpath moveto 8 0 rlineto 0 8 rlineto -8 0 rlineto closepath fill} def
	  ps.println("%%EndSetup");
	  ps.println("%%Page: tree 1");
	  ps.println("%%PageResources: (atend)");
	  ps.println("%%BeginPageSetup");
	  ps.println("/pgsave save def");
	  ps.println("%%EndPageSetup");
	  
	  
	}
	
	
	/**
	* draws boxes. Just leave origin at lower left corner of image, I'll figure it out.
	*
	*/
	private void writeBoxes(PrintStream ps) {
		int height=1;
		int width=1;
		double contrast = getColorExtractor().getContrast();
		int boxes = getNumBoxes();
		if (drawVertical()) {
			height = (int)(getYscale()*boxes );
			width = (int)(getXscale() );
		} else {
			width = (int)(getXscale()*boxes );
			height = (int)(getYscale() );
		}
		for (int i = 0; i < boxes; i++) {
			double val  = ((double)i*contrast*2.0)/((double)boxes-1) - contrast;
			Color color =  getColorExtractor().getColor(val);
			// setcolor
			ps.println(convertColor(color)+ " sr");
			double lx, ly, ux, uy;
			if (drawVertical()) {
				// draw from bottom up
				lx = 0;
				ly = i*getYscale();
				ux = getXscale();
				uy = ly + getYscale();
			} else {
				// draw from left
				lx = i*getXscale();
				ly = 0;
				ux = lx+getXscale();
				uy = getYscale();
			}
			
			ps.println((int)(lx) + " " + (int)(ly) + " moveto");
			// draw filled box
			int w = (int)(ux - lx);
			int h = (int)(uy - ly);
			ps.println(w + " " + h + " fb");
		}
	}
	private void writeNumbers(PrintStream ps) {
		double contrast = getColorExtractor().getContrast();
		int boxes = getNumBoxes();

		ps.println("0 0 0 sr");
	  ps.println(" /Courier findfont");
	  ps.println("12 scalefont");
	  ps.println("setfont");

		for (int i = 0; i < boxes; i++) {
			double val  = ((double)i*contrast*2.0)/((double)boxes-1) - contrast;
			String out = formatValue(val);
			if (drawVertical()) {
				int ly = (int)(i*getYscale());
				ps.println(getXscale() + " " + (ly) + " moveto");
				ps.println("( " + psEscape(out) + " ) show");
			} else {
				int ly = (int)((i+1)*getXscale());
				ps.println("90 rotate");
				ps.println(getYscale() + " " + (-ly) + " moveto");
				ps.println("( " + psEscape(out) + " ) show");
				ps.println("-90 rotate");
			}
		}
	}

	private String psEscape(String inString) {
	  String convicts = "()"; // escape the convicts!!!
	  StringBuffer outString = new StringBuffer(inString.length());
	  for (int i =0 ; i < inString.length(); i++) {
		char thisChar = inString.charAt(i);
		if (convicts.indexOf(thisChar) >= 0) {
		  outString.append('\\');
		}
		outString.append(thisChar);
	  }
	  return outString.toString();
	}
	
	private String convertColor(Color c) {
	  // God Damn java 1.0!!!
	  //	float comp[] = new float [3];
	  //	c.getRGBColorComponents(comp);
	  return convertRGB(c.getRed()) + " " + 
	  convertRGB(c.getGreen()) + " " + 
	  convertRGB(c.getBlue());
	}
	private float convertRGB(int r) {
	  return ((float) r) / 255;
	}
	
	private void writeFooter(PrintStream ps) {
	  ps.println("showpage");
	}
	
	/**
	* write a postscript image corresponding to the colorbar export panel preview
	* to the OutputStream output.
	*/
	public void write(PrintStream ps) {
	  writeHeader(ps);
	  writeBoxes(ps);
	  writeNumbers(ps);
	  writeFooter(ps);
	  
	  if (ps.checkError()) {
		LogBuffer.println("Some error occured during PostScript export");
	  }
	}
  }
}


