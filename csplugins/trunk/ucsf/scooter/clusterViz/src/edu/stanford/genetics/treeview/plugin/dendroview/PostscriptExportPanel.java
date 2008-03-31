/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: PostscriptExportPanel.java,v $
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
import java.awt.Font;
import java.io.*;
import java.util.Stack;

import edu.stanford.genetics.treeview.*;
/**
*  Subclass of ExportPanel which outputs a postscript version of a DendroView.
*
*/
public class PostscriptExportPanel extends ExportPanel implements SettingsPanel {

  // I wish I could just inherit this...
    public PostscriptExportPanel(HeaderInfo arrayHeaderInfo, HeaderInfo geneHeaderInfo, 
		TreeSelectionI geneSelection, 
		TreeSelectionI arraySelection, 
		InvertedTreeDrawer arrayTreeDrawer, LeftTreeDrawer geneTreeDrawer, ArrayDrawer arrayDrawer, MapContainer arrayMap,MapContainer geneMap) {
	  super(arrayHeaderInfo, geneHeaderInfo, 
		geneSelection, 
		arraySelection, 
	  arrayTreeDrawer, geneTreeDrawer, 
	  arrayDrawer, arrayMap,
	  geneMap, false);
	}

  protected Font getGeneFont() {
	return new Font("Courier", 0, 8);
  }
  protected Font getArrayFont() {
	return new Font("Courier", 0, 8);
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
	  
	  DendroPSWriter psw = new DendroPSWriter();
	  psw.write(output);
	  
	  
	  output.close();
	} catch (Exception e) {
	  LogBuffer.println("PostscriptExportPanel.save() caught exception " + e);
	  e.printStackTrace();
	}
  }
  
  
  
  /**
  *  Inner class which outputs a postscript version of Dendroview like things
  *
  *	It is loosely coupled in that it only calls protected methods in the ExporPanel superclass.
  */
  class DendroPSWriter {
	
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
	  ps.println("%%Creator: DendroPSWriter (a Java TreeView Component)");
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
	* draws boxes using maps with the lower left corner at the current origin.
	*
	*/
	private void writeBoxes(PrintStream ps) {
	  int height = (int) getYmapHeight();
	  int width  = (int) getXmapWidth();
	  
	  if (includeGtr()) { // make room for Gtr...
		ps.println("% make room for gtrview");
		ps.println(getGtrWidth() + " 0 translate");		
	  }
	  
	  int yoff = getYmapPixel(minGene() - 0.5);
	  int xoff = -getXmapPixel(minArray() - 0.5);
	  ps.println("% account for offset into data matrix");
	  ps.println(xoff + " " + yoff + " translate");
	  // HACK doesn't account for discontinuous selection...
	  // for each row...
	  int maxGene = maxGene(); // for efficiency...
	  for (int i = minGene(); i <= maxGene(); i++) {
		int maxArray = maxArray(); // for efficiency...
		for (int j = minArray(); j <= maxArray; j++) {
		  Color color =  getArrayDrawer().getColor(j, i);
		  // setcolor
		  ps.println(convertColor(color)+ " sr");
		  // move to lower left corner...
		  int lx = getXmapPixel(j - 0.5);
		  int ly = getYmapPixel(i - 0.5);
		  int ux = getXmapPixel(j+0.5);
		  int uy = getYmapPixel(i+0.5);
		  
		  ps.println((lx) + " " + (height - uy) + " moveto");
		  // draw filled box
		  int w = ux - lx;
		  int h = uy - ly;
		  ps.println(w + " " + h + " fb");
		}
	  }
	  ps.println((-xoff) + " " + (-yoff) + " translate");
	  
	  if (includeGtr()) {
		ps.println((-getGtrWidth()) + " 0 translate");		
	  }
	}
		
	private Color getColor(HeaderInfo headerInfo, int index) {
		int colorIndex       = headerInfo.getIndex("FGCOLOR");
		if (colorIndex > 0) {
			String[] headers  = headerInfo.getHeader(index);
			return TreeColorer.getColor(headers[colorIndex]);
		}
		return Color.black;
	}

	private void writeGeneNames(PrintStream ps) {
		// translate over
		if (getGeneAnnoLength() <= 0) return;
		if (includeArrayMap()) ps.println(getXmapWidth() + " 0 translate");
		if (includeGtr()) ps.println(getGtrWidth() + " 0 translate");		
		//	  if (includeAtr()) ps.println("0 " + getAtrHeight() + " translate");
		
		ps.println(" /Courier findfont");
		ps.println("8 scalefont");
		ps.println("setfont");
		
		int yoff = getYmapPixel(minGene() - 0.5);
		int xoff = 0;
		ps.println("% account for offset into data matrix");
		ps.println(xoff + " " + yoff + " translate");
		int height = (int)getYmapHeight();
		int maxGene = maxGene();

		for (int j = minGene(); j <= maxGene; j++) {
			Color bgColor = getGeneBgColor(j);
			if (bgColor != null) {
				int lx = 0;
				int ly = getYmapPixel(j - 0.5);
				int ux = getGeneAnnoLength();
				int uy = getYmapPixel(j+0.5);
				ps.println(convertColor(bgColor) + " sr");
//				ps.println("0 " + (height - uy) + " moveto");
				
				ps.println((lx) + " " + (height - uy) + " moveto");
				// draw filled box
				int w = ux - lx;
				int h = uy - ly;
				ps.println(w + " " + h + " fb");
			}
		}
		
		for (int j = minGene(); j <= maxGene; j++) {
			int uy = getYmapPixel(j+0.25);
			String out = getGeneAnno(j);
			Color fgColor = getGeneFgColor(j);
			if (out != null) {
				if (fgColor != null) {
					ps.println(convertColor(fgColor) + " sr");
				}
				ps.println("0 " + (height - uy) + " moveto");
				ps.println("( " + psEscape(out) + " ) show");
			}
		}
		ps.println((-xoff) + " " + (-yoff) + " translate");
		
		// translate back
		//	  if (includeAtr()) ps.println("0 " + - getAtrHeight() + " translate");
		if (includeGtr())      ps.println( - getGtrWidth() + " 0 translate");		
		if (includeArrayMap()) ps.println(- getXmapWidth() + " 0 translate");
	}
	
	private void writeArrayNames(PrintStream ps) {
	  if (getArrayAnnoLength() <= 0) return;
	  int tHeight = 0;
	  int tWidth  = 0;
	  if (includeGeneMap()) tHeight += getYmapHeight();
	  if (includeAtr() && (arrayAnnoInside() == false)) {
		tHeight += getAtrHeight();
	  }
	  if (includeGtr()) tWidth += getGtrWidth();		

	  ps.println(tWidth + " " + tHeight + " translate");

	  int xoff = -getXmapPixel(minArray() - 0.5);
	  int yoff = 0;
	  ps.println("% account for offset into data matrix");
	  ps.println(xoff + " " + yoff + " translate");

	  ps.println("0 0 0 sr");
	  ps.println(" /Courier findfont");
	  ps.println("8 scalefont");
	  ps.println("setfont");
	  ps.println("90 rotate");
	  int max = maxArray();

		for (int j = minArray(); j <= max; j++) {
			Color bgColor = getArrayBgColor(j);
			if (bgColor != null) {
				int lx = 0;
				int ly = getXmapPixel(j - 0.5);
				int ux = getArrayAnnoLength();
				int uy = getXmapPixel(j+0.5);
				ps.println(convertColor(bgColor) + " sr");
//				ps.println("0 " + (-uy) + " moveto");
				
				ps.println((lx) + " " + (-uy) + " moveto");
				// draw filled box
				int w = ux - lx;
				int h = uy - ly;
				ps.println(w + " " + h + " fb");
			}
		}


	  for (int i = minArray(); i <= max; i++) {
		int ux = getXmapPixel(i+0.25);
		String out = getArrayAnno(i);
		Color color = getArrayFgColor(i);
		if (out != null) {
		  if (color != null) {
			ps.println(convertColor(color) + " sr");
		  }
		  ps.println("0 " + (-ux) + " moveto");
		  ps.println("( " + psEscape(out) + " ) show");
		}
		
	  }

	  ps.println("-90 rotate");

	  ps.println((-xoff) + " " + (-yoff) + " translate");
	  ps.println(-tWidth + " " + (-tHeight) + " translate");

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
	
	private double scaleGTR, corrGTR;
	private int offsetGTR = 5;
	private void writeGTR(PrintStream ps) {
	  if (includeGtr() == false) return;
	  corrGTR = getMinGeneCorr();
	  scaleGTR = (getGtrWidth() - offsetGTR) / (1.0 - corrGTR);

	  ps.println((offsetGTR/2) + " 0 translate");

	  int yoff = getYmapPixel(minGene() - 0.5);
	  int xoff = 0;
	  ps.println("% account for offset into data matrix");
	  ps.println(xoff + " " + yoff + " translate");
	  interateGTR(ps, getGeneNode());
		ps.println(convertColor(Color.black)+ " sr");
	  ps.println((-xoff) + " " + (-yoff) + " translate");

	  ps.println((-offsetGTR/2) + " 0 translate");

	}

	private double scaleATR, corrATR;
	private int offsetATR = 5;
	private void writeATR(PrintStream ps) {
	  if (includeAtr() == false) return;
	  corrATR = getMinArrayCorr();
	  scaleATR = (getAtrHeight() - offsetATR) / (1.0 - corrATR);
	  int widthOffset = 0;
	  int heightOffset = 0;
	  if (includeGtr()) widthOffset += getGtrWidth();
	  if (includeGeneMap()) heightOffset += getYmapHeight();
	  if (arrayAnnoInside()) heightOffset += getArrayAnnoLength();

	  ps.println(widthOffset + " " + (heightOffset - offsetATR /2 ) + " translate");

	  int xoff = -getXmapPixel(minArray() - 0.5);
	  int yoff = 0;
	  ps.println("% account for offset into data matrix");
	  ps.println(xoff + " " + yoff + " translate");
	  recurseATR(ps, getArrayNode());
		ps.println(convertColor(Color.black)+ " sr");

	  ps.println((-xoff) + " " + (-yoff) + " translate");

	  ps.println((-widthOffset) + " " + (-heightOffset + offsetATR/2) + " translate");

	}

	private void interateGTR(PrintStream ps, TreeDrawerNode startNode) {
	  int height = (int)getYmapHeight();
	  Stack remaining = new Stack();
	  remaining.push(startNode);
	  while (remaining.empty() == false) {
	  	TreeDrawerNode node = (TreeDrawerNode) remaining.pop();
	  	TreeDrawerNode left = node.getLeft();
	  	TreeDrawerNode right = node.getRight();
	  	
	  	int rx = (int) (scaleGTR * (right.getCorr() - corrGTR));
	  	int lx = (int) (scaleGTR * (left.getCorr() - corrGTR));
	  	int tx = (int) (scaleGTR * (node.getCorr() - corrGTR));
	  	
	  	int ry = getYmapPixel(right.getIndex());
	  	int ly = getYmapPixel(left.getIndex());
	  	Color color =  node.getColor();
	  	// setcolor
	  	ps.println(convertColor(color)+ " sr");
	  	
	  	ps.println(rx + " " + (height - ry) + " " + tx + " " + (height - ly) + " " + lx + " snGTR");
	  	
	  	if (left.isLeaf() == false) remaining.push(left);
	  	if (right.isLeaf() == false) remaining.push(right);
	  }
	}
	
	private void recurseATR(PrintStream ps, TreeDrawerNode node) {
	  int height = (int)getAtrHeight();
	  
	  TreeDrawerNode left = node.getLeft();
	  TreeDrawerNode right = node.getRight();
	  
	  
	  int ry = (int) (scaleATR * (right.getCorr() - corrATR));
	  int ly = (int) (scaleATR * (left.getCorr() - corrATR));
	  int ty = (int) (scaleATR * (node.getCorr() - corrATR));
	  
	  int rx = getXmapPixel(right.getIndex());
	  int lx = getXmapPixel(left.getIndex());
		Color color =  node.getColor();
		// setcolor
		ps.println(convertColor(color)+ " sr");
	  ps.println((height - ry) + " " + rx + " " + (height - ty) + " " + lx + " " + (height - ly) + " snATR");
	  
	  if (left.isLeaf() == false) recurseATR(ps, left);
	  if (right.isLeaf() == false) recurseATR(ps, right);
	}
	
	public void write(PrintStream ps) {
	  
	  // calculateDimensions();
	  writeHeader(ps);
	  
	  //write gtr?
	  if (includeGtr()) {
		writeGTR(ps);
	  }
	  if (includeAtr()) {
		writeATR(ps);
	  }
	  
	  writeArrayNames(ps);
	  
	  writeGeneNames(ps);

	  if (includeData()) {
		writeBoxes(ps);
	  }
	  writeFooter(ps);
	  
	  if (ps.checkError()) {
		LogBuffer.println("Some error occured during PostScript export");
	  }
	}
  }
}
